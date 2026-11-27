package com.lost.fts.rulemanagement.api.request;

import com.lost.fts.rulemanagement.infrastructure.persistence.RuleDomain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RuleCreateRequest(

        @NotBlank
        @Size(max = 100)
        String ruleCode,

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        RuleDomain domain

) {
}