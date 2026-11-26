package com.lost.fts.security.authentication.application;

import com.lost.fts.security.authentication.api.request.LoginRequest;
import com.lost.fts.security.authentication.api.response.*;
import com.lost.fts.security.token.application.RefreshTokenService;
import com.lost.fts.security.jwt.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import com.lost.fts.security.userdetails.CustomUserDetails;
import com.lost.fts.menu.application.MenuService;
import com.lost.fts.riskassessment.infrastructure.persistence.RiskLevel;
import com.lost.fts.user.infrastructure.persistence.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import com.lost.fts.user.infrastructure.persistence.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import com.lost.fts.security.mfa.infrastructure.persistence.UserMfa;
import com.lost.fts.security.mfa.infrastructure.persistence.UserMfaRepository;
import com.lost.fts.security.session.application.DeviceService;
import com.lost.fts.security.session.application.SessionService;
import com.lost.fts.security.session.application.SuspiciousActivityService;
import com.lost.fts.security.session.infrastructure.persistence.LoginEventType;
import com.lost.fts.security.session.infrastructure.persistence.UserDevice;
import com.lost.fts.security.session.infrastructure.persistence.UserSession;
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
        private final SessionService sessionService;
        private final DeviceService deviceService;
        private final SuspiciousActivityService suspiciousActivityService;

        public AuthenticationServiceImpl(AuthenticationManager authenticationManager,
                        JwtService jwtService, MenuService menuService, UserRepository userRepository,
                        RefreshTokenService refreshTokenService, UserMfaRepository userMfaRepository,
                        CustomUserDetailsService customUserDetailsService, MfaLoginService mfaLoginService,
                        MfaChallengeService mfaChallengeService, SessionService sessionService,
                        DeviceService deviceService, SuspiciousActivityService suspiciousActivityService) {

                this.authenticationManager = authenticationManager;
                this.jwtService = jwtService;
                this.menuService = menuService;
                this.userRepository = userRepository;
                this.refreshTokenService = refreshTokenService;
                this.userMfaRepository = userMfaRepository;
                this.customUserDetailsService = customUserDetailsService;
                this.mfaLoginService = mfaLoginService;
                this.mfaChallengeService = mfaChallengeService;
                this.sessionService = sessionService;
                this.deviceService = deviceService;
                this.suspiciousActivityService = suspiciousActivityService;
        }

        @Override
        @Transactional
        public LoginResult login(LoginRequest request, HttpServletRequest httpRequest) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                User user = userRepository.findByUsernameIgnoreCase(userDetails.getUsername()).orElseThrow();
                UserMfa mfa = userMfaRepository.findByUserId(user.getId()).orElse(null);
                if (mfa != null && mfa.isEnabled() && mfa.isVerified()) {
                        String challenge = mfaChallengeService.createChallenge(user.getId());
                        return LoginResult.challenge(challenge);
                }
                return issueTokens(user, httpRequest);
        }

        private LoginResult issueTokens(User user, HttpServletRequest request) {

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
                UserDevice device = deviceService.getOrCreateDevice(user, request);
                UserSession session = sessionService.createSession(user, device, request);
                String accessToken = jwtService.generateToken(userDetails, session.getSessionReference());
                String refreshToken = refreshTokenService.createRefreshToken(user.getId(), session);
                Set<String> roles = userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> authority.startsWith("ROLE_"))
                                .map(authority -> authority.substring(5))
                                .collect(Collectors.toSet());

                Set<String> permissions = userDetails.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .filter(authority -> !authority.startsWith("ROLE_"))
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
        public LoginResult completeMfaLogin(String challengeToken, String code, HttpServletRequest request) {

                MfaVerificationResult result = mfaLoginService.verify(challengeToken, code);

                if (!result.verified()) {
                        throw new BadCredentialsException("Invalid or expired authentication challenge");
                }

                User user = userRepository.findById(result.userId())
                                .orElseThrow(() -> new BadCredentialsException("Invalid authentication challenge"));

                if (!user.isEnabled()) {
                        throw new DisabledException("Account is disabled");
                }
                suspiciousActivityService.recordEvent(
                                user,
                                LoginEventType.NEW_DEVICE_LOGIN,
                                request.getRemoteAddr(),
                                request.getHeader("User-Agent"),
                                RiskLevel.MEDIUM,
                                "Login detected from a newly registered device");
                return issueTokens(user, request);
        }

        @Override
        public LoginResult completeRecoveryLogin(String challengeToken, String recoveryCode,
                        HttpServletRequest request) {

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
                return issueTokens(user, request);
        }
}
