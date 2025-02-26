package com.azhag_swe.tech_tutorial.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String username;
    private Set<String> roles;
}
