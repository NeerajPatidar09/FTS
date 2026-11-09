package com.lost.fts.permission.api;

import com.lost.fts.permission.api.request.PermissionCreateRequest;
import com.lost.fts.permission.api.response.PermissionResponse;
import com.lost.fts.permission.infrastructure.persistence.*;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionRepository repository;

    public PermissionController(PermissionRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermissionResponse create(
            @Valid @RequestBody PermissionCreateRequest request) {

        String code = request.permissionCode()
                .trim().toUpperCase();

        if (repository.existsByPermissionCode(code)) {
            throw new IllegalArgumentException(
                    "Permission already exists");
        }

        Permission permission = new Permission();

        permission.setPermissionCode(code);
        permission.setPermissionName(
                request.permissionName().trim());
        permission.setDescription(request.description());
        permission.setActive(true);

        return map(repository.save(permission));
    }

    @GetMapping
    public List<PermissionResponse> getAll() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    private PermissionResponse map(Permission permission) {

        return new PermissionResponse(
                permission.getId(),
                permission.getPermissionCode(),
                permission.getPermissionName(),
                permission.getDescription(),
                permission.isActive()
        );
    }
}