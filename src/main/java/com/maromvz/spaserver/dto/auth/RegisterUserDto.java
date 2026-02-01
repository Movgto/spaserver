package com.maromvz.spaserver.dto.auth;

import com.maromvz.spaserver.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RegisterUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String passwordConfirmation;

    private Set<UserRole.Privilege> privileges;

    public boolean checkPasswords() {
        return password.equals(passwordConfirmation);
    }
}
