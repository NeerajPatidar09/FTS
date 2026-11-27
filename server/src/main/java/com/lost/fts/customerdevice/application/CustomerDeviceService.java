
package com.lost.fts.customerdevice.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lost.fts.customerdevice.api.request.CustomerDeviceRegisterRequest;
import com.lost.fts.customerdevice.api.request.CustomerDeviceStatusRequest;
import com.lost.fts.customerdevice.api.response.CustomerDeviceResponse;

public interface CustomerDeviceService {

    CustomerDeviceResponse register(
            CustomerDeviceRegisterRequest request);

    CustomerDeviceResponse getById(Long id);

    Page<CustomerDeviceResponse> getAll(
            Long customerId,
            Pageable pageable);

    List<CustomerDeviceResponse> getByCustomerRef(String customerRef);

    CustomerDeviceResponse updateStatus(
            Long id,
            CustomerDeviceStatusRequest request);

    void delete(Long id);
}
