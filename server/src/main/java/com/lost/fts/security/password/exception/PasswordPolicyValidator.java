package com.lost.fts.security.password.exception;

import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

    public void validate(String password) {

        if (password == null || password.length() < 12) {
            throw new PasswordPolicyException(
                "Password must contain at least 12 characters"
            );
        }

        if (password.length() > 128) {
            throw new PasswordPolicyException(
                "Password cannot exceed 128 characters"
            );
        }

        boolean uppercase = password.matches("(?s).*\\p{Lu}.*");
        boolean lowercase = password.matches("(?s).*\\p{Ll}.*");
        boolean digit = password.matches("(?s).*\\d.*");
        boolean special = password.matches("(?s).*[^\\p{L}\\d\\s].*");

        if (!uppercase || !lowercase || !digit || !special) {
            throw new PasswordPolicyException(
                "Password must contain uppercase, lowercase, numeric and special characters"
            );
        }
    }
}