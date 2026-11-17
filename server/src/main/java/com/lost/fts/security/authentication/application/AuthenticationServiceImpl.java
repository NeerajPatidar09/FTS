package com.lost.fts.security.authentication.application;

import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.api.response.*;
import com.lost.fts.security.token.application.RefreshTokenService;
import com.lost.fts.security.jwt.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import com.lost.fts.security.userdetails.CustomUserDetails;
import com.lost.fts.menu.application.MenuService;
import com.lost.fts.user.infrastructure.persistence.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import com.lost.fts.security.mfa.infrastructure.persistence.UserMfa;
import com.lost.fts.security.mfa.infrastructure.persistence.UserMfaRepository;
import com.lost.fts.security.mfa.application.MfaChallengeService;
import com.lost.fts.security.userdetails.CustomUserDetailsService;
import com.lost.fts.security.mfa.api.response.MfaVerificationResult;
import com.lost.fts.security.mfa.application.MfaLoginService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final MenuService menuService;
        private final UserRepository userRepository;
        private final RefreshTokenService refreshTokenService;
        private final UserMfaRepository userMfaRepository;
        private final MfaChallengeService mfaChallengeService;
        private final CustomUserDetailsService customUserDetailsService;
        private final MfaLoginService mfaLoginService;

        public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                        JwtService jwtService, MenuService menuService, UserRepository userRepository,
                        RefreshTokenService refreshTokenService, UserMfaRepository userMfaRepository,
                        CustomUserDetailsService customUserDetailsService, MfaLoginService mfaLoginService,
                        MfaChallengeService mfaChallengeService) {

                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
                this.menuService = menuService;
                this.userRepository = userRepository;
                this.refreshTokenService = refreshTokenService;
                this.userMfaRepository = userMfaRepository;
                this.customUserDetailsService = customUserDetailsService;
                this.mfaLoginService = mfaLoginService;
                this.mfaChallengeService = mfaChallengeService;
        }

        @Override
        @Transactional
        public LoginResult login(LoginRequest request) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername()).orElseThrow();
                UserDetails userDetails1 = customUserDetailsService.loadUserByUsername(user.getUsername());
                String accessToken = jwtService.generateToken(userDetails1);
                System.out.println("accessToken ::::::: " + accessToken);
                UserMfa mfa = userMfaRepository.findByUserId(user.getId()).orElse(null);
                if (mfa != null && mfa.isEnabled() && mfa.isVerified()) {
                        String challenge = mfaChallengeService.createChallenge(user.getId());
                        return LoginResult.challenge(challenge);
                }
                return issueTokens(user);
        }

        private LoginResult issueTokens(User user) {

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
                String accessToken = jwtService.generateToken(userDetails);
                String refreshToken = refreshTokenService.createRefreshToken(user.getId());
                Set<String> roles = userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> authority.startsWith("ROLES_"))
                                .map(authority -> authority.substring(6))
                                .collect(Collectors.toSet());

                Set<String> permissions = userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> !authority.startsWith("ROLES_"))
                                .collect(Collectors.toSet());

                Long employeeId = user.getEmployee() == null
                                ? null
                                : user.getEmployee().getId();

                String employeeName = user.getEmployee() == null
                                ? null
                                : user.getEmployee().getFirstName()
                                                + " "
                                                + user.getEmployee().getLastName();

                CurrentUserResponse currentUser = new CurrentUserResponse(
                                user.getId(),
                                employeeId,
                                user.getUsername(),
                                user.getEmail(),
                                employeeName,
                                roles,
                                permissions);

                LoginResponse response = new LoginResponse(
                                accessToken,
                                "Bearer",
                                jwtService.getExpiration(),
                                currentUser,
                                menuService.getAccessibleMenus(user));

                return LoginResult.authenticated(response, refreshToken);
        }

        @Override
        public LoginResult completeMfaLogin(String challengeToken, String code) {

                MfaVerificationResult result = mfaLoginService.verify(challengeToken, code);

                if (!result.verified()) {
                        throw new BadCredentialsException("Invalid or expired authentication challenge");
                }

                User user = userRepository.findById(result.userId())
                                .orElseThrow(() -> new BadCredentialsException("Invalid authentication challenge"));

                if (!user.isEnabled()) {
                        throw new DisabledException("Account is disabled");
                }

                return issueTokens(user);
        }

        @Override
        public LoginResult completeRecoveryLogin(String challengeToken, String recoveryCode) {

                MfaVerificationResult result = mfaLoginService.verifyRecoveryCode(challengeToken, recoveryCode);

                if (!result.verified()) {
                        throw new BadCredentialsException("Invalid or expired authentication challenge");
                }

                User user = userRepository.findById(result.userId())
                                .orElseThrow(() -> new BadCredentialsException("Invalid authentication challenge"));

                if (!user.isEnabled()) {
                        throw new DisabledException("Account is disabled");
                }

                // Issue a normal authenticated session.
                // Do not disable MFA automatically.
                // Notify the user that a recovery code was used.
                return issueTokens(user);
        }
}
