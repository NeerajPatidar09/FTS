package com.lost.fts.customer.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerCreateRequest(

        @NotBlank(message = "Customer reference is required") @Size(max = 64, message = "Customer reference must not exceed 64 characters") @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Customer reference can contain letters, numbers, hyphens and underscores only") String customerRef,

        @NotBlank(message = "First name is required") @Size(max = 20, message = "First name must not exceed 20 characters") String firstName,

        @NotBlank(message = "Last name is required") @Size(max = 20, message = "Last name must not exceed 20 characters") String lastName,

        @NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") @Size(max = 250, message = "Email must not exceed 250 characters") String email

) {
}
