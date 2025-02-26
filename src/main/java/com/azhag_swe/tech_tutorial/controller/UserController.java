package com.azhag_swe.tech_tutorial.controller;

import com.azhag_swe.tech_tutorial.model.entity.User;
import com.azhag_swe.tech_tutorial.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user, @RequestParam Set<String> roles) {
        User registeredUser = userService.registerUser(user, roles);
        return ResponseEntity.ok(registeredUser);
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<User> updateUserRoles(@PathVariable Long userId, @RequestParam Set<String> roles) {
        User updatedUser = userService.updateUserRoles(userId, roles);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{username}/has-permission")
    public ResponseEntity<Boolean> hasPermission(@PathVariable String username, @RequestParam String permission) {
        boolean hasPermission = userService.hasPermission(username, permission);
        return ResponseEntity.ok(hasPermission);
    }
}
