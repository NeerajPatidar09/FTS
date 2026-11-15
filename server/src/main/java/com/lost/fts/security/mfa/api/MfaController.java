package com.lost.fts.security.mfa.api;

import com.lost.fts.security.mfa.application.*;
import com.lost.fts.security.mfa.api.response.*;
import com.lost.fts.security.mfa.api.request.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/auth/mfa")
public class MfaController {

    private final MfaService mfaService;

    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @PostMapping("/setup")
    public MfaSetupResponse setup(Authentication authentication) {
        return mfaService.setup(authentication.getName());
    }

    @PostMapping("/enable")
    public void enable(@Valid @RequestBody MfaVerifyRequest request,Authentication authentication) {
        mfaService.enable(authentication.getName(),request.code());
    }

    @PostMapping("/disable")
    public void disable(
            @Valid @RequestBody MfaVerifyRequest request,
            Authentication authentication) {
        mfaService.disable(
                authentication.getName(),
                request.code());
    }
}