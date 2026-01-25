package com.maromvz.spaserver.services;

import com.maromvz.spaserver.dto.auth.JwtResponse;
import com.maromvz.spaserver.dto.auth.LoginDto;
import com.maromvz.spaserver.dto.auth.RegisterUserDto;
import com.maromvz.spaserver.entities.RefreshToken;
import com.maromvz.spaserver.entities.Role;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.entities.UserRoleId;
import com.maromvz.spaserver.exceptions.auth.PasswordsDontMatchException;
import com.maromvz.spaserver.repo.RoleRepo;
import com.maromvz.spaserver.repo.UserRepo;
import com.maromvz.spaserver.utils.JwtUtils;
import com.maromvz.spaserver.utils.RefreshTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenUtils refreshTokenUtils;

    public User registerUser(RegisterUserDto userDto) {
        if (!userDto.checkPasswords()) throw new PasswordsDontMatchException();
        Role role = roleRepo.findByName(Role.ERole.ROLE_ADMIN).orElseThrow();

        User newUser = new User();

        newUser.setFirstName(userDto.getFirstName());
        newUser.setLastName(userDto.getLastName());
        newUser.setEmail(userDto.getEmail());

        var encodedPwd = passwordEncoder.encode(userDto.getPassword());

        newUser.setPassword(encodedPwd);
        newUser.addRole(role);

        userRepo.save(newUser);

        return newUser;
    }

    public JwtResponse loginUser(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var token = jwtUtils.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        ResponseCookie responseCookie = refreshTokenUtils.generateRefreshCookie(refreshToken.getToken());

        return new JwtResponse(token, user.getUsername(), user.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }
}
