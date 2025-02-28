package com.azhag_swe.tech_tutorial.dto.request;

import java.util.Set;

// RegisterRequest.java
public record RegisterRequest(
    String username,
    String password,
    Set<String> roles
) {}

