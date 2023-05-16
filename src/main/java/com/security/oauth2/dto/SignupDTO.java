package com.security.oauth2.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class SignupDTO {
    private String username;
    private String password;
}
