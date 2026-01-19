package com.maromvz.spaserver.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String passwordConfirmation;

    public boolean checkPasswords() {
        return password.equals(passwordConfirmation);
    }
}
