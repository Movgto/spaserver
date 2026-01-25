package com.maromvz.spaserver.dto.auth;

import lombok.Data;

@Data
public class LoginDto {
    private final String email;
    private final String password;
}
