package com.lost.fts.employee.api;

import com.lost.fts.employee.api.request.EmployeeCreateRequest;
import com.lost.fts.employee.api.response.EmployeeResponse;
import com.lost.fts.employee.application.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Create employee
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return employeeService.createEmployee(request);
    }

    // Get all employees
    @GetMapping
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // Get employee by ID
    @GetMapping("/{id}")
    public EmployeeResponse getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    // Update employee
    @PutMapping("/{id}")
    public EmployeeResponse updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeCreateRequest request) {
        return employeeService.updateEmployee(id, request);
    }

    // Deactivate employee
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);}

    @GetMapping("/search")
    public Page<EmployeeResponse> searchEmployees( @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        // Whitelist permitted sort fields.
        if (!java.util.Set.of("id", "employeeCode", "firstName", "lastName", "email", "joiningDate").contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field");
        }

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : null;

        if (sortDirection == null) {
            throw new IllegalArgumentException("Invalid sort direction");}

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return employeeService.searchEmployees(keyword, pageable);
    }
}