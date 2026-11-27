
package com.lost.fts.beneficiary.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.lost.fts.beneficiary.api.request.BeneficiaryCreateRequest;
import com.lost.fts.beneficiary.api.request.BeneficiaryStatusRequest;
import com.lost.fts.beneficiary.api.request.BeneficiaryUpdateRequest;
import com.lost.fts.beneficiary.api.response.BeneficiaryResponse;
import com.lost.fts.beneficiary.api.response.BeneficiarySummaryResponse;
import com.lost.fts.beneficiary.application.BeneficiaryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@Validated
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> create(
            @Valid @RequestBody BeneficiaryCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(beneficiaryService.create(request));
    }

    @GetMapping("/{beneficiaryRef}")
    public ResponseEntity<BeneficiaryResponse> getByReference(
            @PathVariable String beneficiaryRef) {

        return ResponseEntity.ok(
                beneficiaryService.getByReference(beneficiaryRef));
    }

    @GetMapping
    public ResponseEntity<Page<BeneficiarySummaryResponse>> getAll(
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String search,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return ResponseEntity.ok(
                beneficiaryService.getAll(accountId, search, pageable));
    }

    @PutMapping("/{beneficiaryRef}")
    public ResponseEntity<BeneficiaryResponse> update(
            @PathVariable String beneficiaryRef,
            @Valid @RequestBody BeneficiaryUpdateRequest request) {

        return ResponseEntity.ok(
                beneficiaryService.update(beneficiaryRef, request));
    }

    @PatchMapping("/{beneficiaryRef}/status")
    public ResponseEntity<BeneficiaryResponse> updateStatus(
            @PathVariable String beneficiaryRef,
            @Valid @RequestBody BeneficiaryStatusRequest request) {

        return ResponseEntity.ok(
                beneficiaryService.updateStatus(beneficiaryRef, request));
    }

    @DeleteMapping("/{beneficiaryRef}")
    public ResponseEntity<Void> delete(
            @PathVariable String beneficiaryRef) {

        beneficiaryService.delete(beneficiaryRef);

        return ResponseEntity.noContent().build();
    }
}
