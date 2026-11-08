package com.lost.fts.employee.application;

import com.lost.fts.employee.api.request.EmployeeCreateRequest;
import com.lost.fts.employee.api.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeCreateRequest request);
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse getEmployeeById(Long id);
    EmployeeResponse updateEmployee(Long id,EmployeeCreateRequest request);
    void deleteEmployee(Long id);
    Page<EmployeeResponse> searchEmployees(String keyword,Pageable pageable);
}