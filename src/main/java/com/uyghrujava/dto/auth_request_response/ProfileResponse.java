package com.uyghrujava.dto.auth_request_response;

import java.util.List;

public record ProfileResponse(String username, String email, String userNumber, List<String> role) {
}
