package com.lost.fts.department.application;

import com.lost.fts.department.api.request.DepartmentCreateRequest;
import com.lost.fts.department.api.response.DepartmentResponse;
import java.util.List;

public interface DepartmentService {

    DepartmentResponse create(DepartmentCreateRequest request);
    DepartmentResponse update(Long id, DepartmentCreateRequest request);
    DepartmentResponse getById(Long id);
    List<DepartmentResponse> getAll();
    void deactivate(Long id);
}