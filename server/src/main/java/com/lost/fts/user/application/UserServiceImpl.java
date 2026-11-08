package com.lost.fts.user.application;

import com.lost.fts.user.api.request.UserCreateRequest;
import com.lost.fts.user.api.response.UserResponse;
import com.lost.fts.user.infrastructure.persistence.User;
import com.lost.fts.user.infrastructure.persistence.UserRepository;
import com.lost.fts.user.infrastructure.persistence.UserStatus;
import com.lost.fts.employee.infrastructure.persistence.Employee;
import com.lost.fts.employee.infrastructure.persistence.EmployeeRepository;
import com.lost.fts.employee.infrastructure.persistence.EmployeeStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE USER
    @Override
    public UserResponse createUser(UserCreateRequest request) {

        if (userRepository.existsByUsernameIgnoreCase(
                request.username())) {
            throw new IllegalArgumentException(
                    "Username already exists");
        }

        if (userRepository.existsByEmailIgnoreCase(
                request.email())) {
            throw new IllegalArgumentException(
                    "Email already exists");
        }

        User user = new User();

        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim());

        // Encrypt password using BCrypt
        user.setPasswordHash(
                passwordEncoder.encode(request.initialPassword()));

        user.setStatus(UserStatus.ACTIVE);
        user.setAccountLocked(false);

        // Link employee if employeeId is provided
        if (request.employeeId() != null) {

            Employee employee = employeeRepository
                    .findById(request.employeeId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Employee not found"));

            if (employee.getStatus() != EmployeeStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Cannot create user for inactive employee");
            }

            if (userRepository.existsByEmployee_Id(
                    employee.getId())) {
                throw new IllegalArgumentException(
                        "Employee already has a login account");
            }

            user.setEmployee(employee);
        }

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    // GET ALL USERS
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET USER BY ID
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + id));

        return mapToResponse(user);
    }

    // DISABLE USER
    @Override
    public void disableUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + id));

        user.setStatus(UserStatus.DISABLED);

        userRepository.save(user);
    }

    // ENTITY TO RESPONSE DTO MAPPING
    private UserResponse mapToResponse(User user) {

        Long employeeId = null;
        String employeeName = null;

        if (user.getEmployee() != null) {

            employeeId = user.getEmployee().getId();

            employeeName = user.getEmployee().getFirstName()
                    + " "
                    + user.getEmployee().getLastName();
        }

        return new UserResponse(
                user.getId(),
                employeeId,
                employeeName,
                user.getUsername(),
                user.getEmail(),
                user.getStatus().name(),
                user.isAccountLocked());
    }
}
