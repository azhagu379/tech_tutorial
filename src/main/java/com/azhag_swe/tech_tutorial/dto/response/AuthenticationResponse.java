package com.azhag_swe.tech_tutorial.dto.response;

import lombok.Builder;

// AuthenticationResponse.java
@Builder
public record AuthenticationResponse(
        String accessToken,
        String refreshToken) {

}
