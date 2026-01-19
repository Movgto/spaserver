package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.auth.RegisterUserDto;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.exceptions.auth.PasswordsDontMatchException;
import com.maromvz.spaserver.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    public User registerUser(RegisterUserDto userDto) {
        if (!userDto.checkPasswords()) throw new PasswordsDontMatchException();

        User newUser = new User();

        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(userDto.getPassword());
        newUser.setRole(User.Role.USER);

        userRepo.save(newUser);

        return newUser;
    }
}
