package com.uyghrujava.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

// TODO: make this AuthRequest to normal Pojo class and add more fields or create more files in User model

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String userNumber;
    private List<String> roleList = new ArrayList<>();
}
