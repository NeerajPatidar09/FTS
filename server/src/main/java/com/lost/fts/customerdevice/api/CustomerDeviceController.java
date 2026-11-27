package com.lost.fts.customerdevice.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.lost.fts.customerdevice.api.request.CustomerDeviceRegisterRequest;
import com.lost.fts.customerdevice.api.request.CustomerDeviceStatusRequest;
import com.lost.fts.customerdevice.api.response.CustomerDeviceResponse;
import com.lost.fts.customerdevice.application.CustomerDeviceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customer-devices")
@Validated
public class CustomerDeviceController {

    private final CustomerDeviceService customerDeviceService;

    public CustomerDeviceController(
            CustomerDeviceService customerDeviceService) {
        this.customerDeviceService = customerDeviceService;
    }

    @PostMapping
    public ResponseEntity<CustomerDeviceResponse> register(
            @Valid @RequestBody CustomerDeviceRegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerDeviceService.register(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDeviceResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                customerDeviceService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerDeviceResponse>> getAll(
            @RequestParam(required = false) Long customerId,
            @PageableDefault(
                    size = 20,
                    sort = "lastSeenAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return ResponseEntity.ok(
                customerDeviceService.getAll(customerId, pageable));
    }

    @GetMapping("/customer/{customerRef}")
    public ResponseEntity<List<CustomerDeviceResponse>> getByCustomerRef(
            @PathVariable String customerRef) {

        return ResponseEntity.ok(
                customerDeviceService.getByCustomerRef(customerRef));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerDeviceResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDeviceStatusRequest request) {

        return ResponseEntity.ok(
                customerDeviceService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        customerDeviceService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
