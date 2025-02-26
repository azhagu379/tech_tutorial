package com.azhag_swe.tech_tutorial.controller;

import com.azhag_swe.tech_tutorial.dto.request.RegisterRequest;
import com.azhag_swe.tech_tutorial.dto.request.UpdateUserRolesRequest;
import com.azhag_swe.tech_tutorial.dto.response.UserResponse;
import com.azhag_swe.tech_tutorial.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Validated @RequestBody RegisterRequest request) {
        UserResponse registeredUser = userService.registerUser(request);
        return ResponseEntity.ok(registeredUser);
    }

    @PutMapping("/update-roles")
    public ResponseEntity<UserResponse> updateUserRoles(@Validated @RequestBody UpdateUserRolesRequest request) {
        UserResponse updatedUser = userService.updateUserRoles(request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{username}/has-permission")
    public ResponseEntity<Boolean> hasPermission(@PathVariable String username, @RequestParam String permission) {
        boolean hasPermission = userService.hasPermission(username, permission);
        return ResponseEntity.ok(hasPermission);
    }
}
