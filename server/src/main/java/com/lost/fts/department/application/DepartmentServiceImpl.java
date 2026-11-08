package com.lost.fts.department.application;

import com.lost.fts.department.api.request.DepartmentCreateRequest;
import com.lost.fts.department.api.response.DepartmentResponse;
import com.lost.fts.department.infrastructure.persistence.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

        private final DepartmentRepository repository;

        public DepartmentServiceImpl(DepartmentRepository repository) {
                this.repository = repository;
        }

        @Override
        public DepartmentResponse create(DepartmentCreateRequest request) {

                if (repository.existsByDepartmentCodeIgnoreCase(
                                request.departmentCode())) {
                        throw new IllegalArgumentException(
                                        "Department code already exists");
                }

                if (repository.existsByDepartmentNameIgnoreCase(
                                request.departmentName())) {
                        throw new IllegalArgumentException(
                                        "Department name already exists");
                }

                Department department = new Department();

                department.setDepartmentCode(
                                request.departmentCode().trim());

                department.setDepartmentName(
                                request.departmentName().trim());

                department.setActive(true);

                return mapToResponse(repository.save(department));
        }

        @Override
        public DepartmentResponse update(
                        Long id,
                        DepartmentCreateRequest request) {

                Department department = repository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Department not found"));

                if (!department.getDepartmentCode().equalsIgnoreCase(
                                request.departmentCode())
                                && repository.existsByDepartmentCodeIgnoreCase(
                                                request.departmentCode())) {
                        throw new IllegalArgumentException(
                                        "Department code already exists");
                }

                if (!department.getDepartmentName().equalsIgnoreCase(
                                request.departmentName())
                                && repository.existsByDepartmentNameIgnoreCase(
                                                request.departmentName())) {
                        throw new IllegalArgumentException(
                                        "Department name already exists");
                }

                department.setDepartmentCode(
                                request.departmentCode().trim());

                department.setDepartmentName(
                                request.departmentName().trim());

                return mapToResponse(repository.save(department));
        }

        @Override
        @Transactional(readOnly = true)
        public DepartmentResponse getById(Long id) {

                return repository.findById(id)
                                .map(this::mapToResponse)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Department not found"));
        }

        @Override
        @Transactional(readOnly = true)
        public List<DepartmentResponse> getAll() {

                return repository.findAll()
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        public void deactivate(Long id) {

                Department department = repository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Department not found"));

                department.setActive(false);
                repository.save(department);
        }

        private DepartmentResponse mapToResponse(
                        Department department) {

                return new DepartmentResponse(
                                department.getId(),
                                department.getDepartmentCode(),
                                department.getDepartmentName(),
                                department.isActive());
        }
}