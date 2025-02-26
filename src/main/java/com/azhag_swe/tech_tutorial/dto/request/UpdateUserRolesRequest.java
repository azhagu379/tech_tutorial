package com.azhag_swe.tech_tutorial.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UpdateUserRolesRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Set<String> roles;
}

