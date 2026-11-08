package com.lost.fts.employee.application;

import com.lost.fts.employee.api.request.EmployeeCreateRequest;
import com.lost.fts.employee.api.response.EmployeeResponse;
import com.lost.fts.employee.infrastructure.persistence.*;
import com.lost.fts.department.infrastructure.persistence.*;
import com.lost.fts.designation.infrastructure.persistence.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

        private final EmployeeRepository employeeRepository;
        private final DepartmentRepository departmentRepository;
        private final DesignationRepository designationRepository;

        public EmployeeServiceImpl(
                        EmployeeRepository employeeRepository,
                        DepartmentRepository departmentRepository,
                        DesignationRepository designationRepository) {

                this.employeeRepository = employeeRepository;
                this.departmentRepository = departmentRepository;
                this.designationRepository = designationRepository;
        }

        @Override
        public EmployeeResponse createEmployee(
                        EmployeeCreateRequest request) {

                if (employeeRepository.existsByEmployeeCode(
                                request.employeeCode())) {
                        throw new IllegalArgumentException(
                                        "Employee code already exists");
                }

                if (employeeRepository.existsByEmailIgnoreCase(
                                request.email())) {
                        throw new IllegalArgumentException(
                                        "Employee email already exists");
                }

                Department department = departmentRepository
                                .findById(request.departmentId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Department not found"));

                Designation designation = designationRepository
                                .findById(request.designationId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Designation not found"));

                if (!department.isActive() || !designation.isActive()) {
                        throw new IllegalArgumentException(
                                        "Department or designation is inactive");
                }

                Employee employee = new Employee();

                employee.setEmployeeCode(request.employeeCode());
                employee.setFirstName(request.firstName());
                employee.setLastName(request.lastName());
                employee.setEmail(request.email());
                employee.setPhoneNumber(request.phoneNumber());
                employee.setDepartment(department);
                employee.setDesignation(designation);
                employee.setJoiningDate(request.joiningDate());
                employee.setStatus(EmployeeStatus.ACTIVE);

                Employee savedEmployee = employeeRepository.save(employee);

                return mapToResponse(savedEmployee);
        }

        @Override
        @Transactional(readOnly = true)
        public List<EmployeeResponse> getAllEmployees() {

                return employeeRepository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public EmployeeResponse getEmployeeById(Long id) {

                Employee employee = employeeRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Employee not found"));

                return mapToResponse(employee);
        }

        @Override
        public EmployeeResponse updateEmployee(
                        Long id,
                        EmployeeCreateRequest request) {

                Employee employee = employeeRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Employee not found"));

                if (!employee.getEmployeeCode().equals(
                                request.employeeCode())
                                && employeeRepository.existsByEmployeeCode(
                                                request.employeeCode())) {
                        throw new IllegalArgumentException(
                                        "Employee code already exists");
                }

                if (!employee.getEmail().equalsIgnoreCase(
                                request.email())
                                && employeeRepository.existsByEmailIgnoreCase(
                                                request.email())) {
                        throw new IllegalArgumentException(
                                        "Employee email already exists");
                }

                Department department = departmentRepository
                                .findById(request.departmentId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Department not found"));

                Designation designation = designationRepository
                                .findById(request.designationId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Designation not found"));

                employee.setEmployeeCode(request.employeeCode());
                employee.setFirstName(request.firstName());
                employee.setLastName(request.lastName());
                employee.setEmail(request.email());
                employee.setPhoneNumber(request.phoneNumber());
                employee.setDepartment(department);
                employee.setDesignation(designation);
                employee.setJoiningDate(request.joiningDate());

                return mapToResponse(employeeRepository.save(employee));
        }

        @Override
        public void deleteEmployee(Long id) {

                Employee employee = employeeRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Employee not found"));

                // Prefer deactivation over physical deletion.
                employee.setStatus(EmployeeStatus.INACTIVE);

                employeeRepository.save(employee);
        }

        private EmployeeResponse mapToResponse(Employee employee) {

                return new EmployeeResponse(
                                employee.getId(),
                                employee.getEmployeeCode(),
                                employee.getFirstName(),
                                employee.getLastName(),
                                employee.getEmail(),
                                employee.getPhoneNumber(),
                                employee.getDepartment().getId(),
                                employee.getDepartment().getDepartmentName(),
                                employee.getDesignation().getId(),
                                employee.getDesignation().getDesignationName(),
                                employee.getJoiningDate(),
                                employee.getStatus().name());
        }

        @Override
        @Transactional(readOnly = true)
        public Page<EmployeeResponse> searchEmployees(
                        String keyword,
                        Pageable pageable) {

                Page<Employee> employees;

                if (keyword == null || keyword.isBlank()) {

                        employees = employeeRepository.findAll(pageable);

                } else {

                        String search = keyword.trim();

                        employees = employeeRepository
                                        .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmployeeCodeContainingIgnoreCase(
                                                        search,
                                                        search,
                                                        search,
                                                        pageable);
                }

                return employees.map(this::mapToResponse);
        }
}