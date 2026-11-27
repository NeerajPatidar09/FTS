
package com.lost.fts.customerdevice.application;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lost.fts.customer.infrastructure.persistence.CustomerEntity;
import com.lost.fts.customer.infrastructure.persistence.CustomerRepository;
import com.lost.fts.customer.infrastructure.persistence.CustomerStatus;
import com.lost.fts.customerdevice.api.request.CustomerDeviceRegisterRequest;
import com.lost.fts.customerdevice.api.request.CustomerDeviceStatusRequest;
import com.lost.fts.customerdevice.api.response.CustomerDeviceResponse;
import com.lost.fts.customerdevice.infrastructure.persistence.CustomerDeviceEntity;
import com.lost.fts.customerdevice.infrastructure.persistence.CustomerDeviceRepository;
import com.lost.fts.customerdevice.infrastructure.persistence.CustomerDeviceStatus;
import com.lost.fts.device.infrastructure.persistence.DeviceEntity;
import com.lost.fts.device.infrastructure.persistence.DeviceRepository;

@Service
@Transactional
public class CustomerDeviceServiceImpl implements CustomerDeviceService {

    private final CustomerDeviceRepository customerDeviceRepository;
    private final CustomerRepository customerRepository;
    private final DeviceRepository deviceRepository;
    private final CustomerDeviceMapper customerDeviceMapper;

    public CustomerDeviceServiceImpl(
            CustomerDeviceRepository customerDeviceRepository,
            CustomerRepository customerRepository,
            DeviceRepository deviceRepository,
            CustomerDeviceMapper customerDeviceMapper) {

        this.customerDeviceRepository = customerDeviceRepository;
        this.customerRepository = customerRepository;
        this.deviceRepository = deviceRepository;
        this.customerDeviceMapper = customerDeviceMapper;
    }

    @Override
    public CustomerDeviceResponse register(
            CustomerDeviceRegisterRequest request) {

        CustomerEntity customer = customerRepository
                .findByCustomerRef(request.customerRef())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only active customers can register devices");
        }

        DeviceEntity device = deviceRepository
                .findById(request.deviceId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Device not found"));

        if (customerDeviceRepository.existsByCustomerIdAndDeviceId(
                customer.getId(), device.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Device is already registered for this customer");
        }

        Instant now = Instant.now();

        CustomerDeviceEntity entity = new CustomerDeviceEntity();

        entity.setCustomer(customer);
        entity.setDevice(device);
        entity.setStatus(CustomerDeviceStatus.REGISTERED);
        entity.setFirstSeenAt(now);
        entity.setLastSeenAt(now);
        entity.setFailedAuthCount(0);

        device.recordObservation(now);

        CustomerDeviceEntity saved =
                customerDeviceRepository.save(entity);

        return customerDeviceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDeviceResponse getById(Long id) {

        return customerDeviceMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDeviceResponse> getAll(
            Long customerId,
            Pageable pageable) {

        Page<CustomerDeviceEntity> page;

        if (customerId == null) {
            page = customerDeviceRepository.findAll(pageable);
        } else {
            if (!customerRepository.existsById(customerId)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found");
            }

            page = customerDeviceRepository.findByCustomerId(
                    customerId, pageable);
        }

        return page.map(customerDeviceMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDeviceResponse> getByCustomerRef(
            String customerRef) {

        CustomerEntity customer = customerRepository
                .findByCustomerRef(customerRef)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer not found"));

        return customerDeviceRepository
                .findByCustomerId(customer.getId())
                .stream()
                .map(customerDeviceMapper::toResponse)
                .toList();
    }

    @Override
    public CustomerDeviceResponse updateStatus(
            Long id,
            CustomerDeviceStatusRequest request) {

        CustomerDeviceEntity entity = findById(id);

        CustomerDeviceStatus current = entity.getStatus();
        CustomerDeviceStatus target = request.status();

        if (current == target) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Device already has this status");
        }

        switch (target) {

            case TRUSTED -> {
                if (current != CustomerDeviceStatus.REGISTERED) {
                    throw invalidTransition(current, target);
                }

                entity.trust();
            }

            case BLOCKED -> {
                if (current == CustomerDeviceStatus.REVOKED) {
                    throw invalidTransition(current, target);
                }

                entity.block();
            }

            case REVOKED -> entity.revoke();

            case REGISTERED -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot manually restore a device to REGISTERED");
        }

        return customerDeviceMapper.toResponse(
                customerDeviceRepository.save(entity));
    }

    @Override
    public void delete(Long id) {

        CustomerDeviceEntity entity = findById(id);

        // Revoke the association instead of physically deleting
        // security-related device registration history.
        entity.revoke();

        customerDeviceRepository.save(entity);
    }

    private CustomerDeviceEntity findById(Long id) {

        return customerDeviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer device registration not found"));
    }

    private ResponseStatusException invalidTransition(
            CustomerDeviceStatus current,
            CustomerDeviceStatus target) {

        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid customer device status transition: "
                        + current + " -> " + target);
    }
}
