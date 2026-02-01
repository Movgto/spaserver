package com.maromvz.spaserver.controllers;

import com.maromvz.spaserver.dto.auth.JwtResponse;
import com.maromvz.spaserver.dto.auth.LoginDto;
import com.maromvz.spaserver.dto.auth.RegisterUserDto;
import com.maromvz.spaserver.entities.RefreshToken;
import com.maromvz.spaserver.entities.User;
import com.maromvz.spaserver.exceptions.auth.PasswordsDontMatchException;
import com.maromvz.spaserver.repo.RefreshTokenRepo;
import com.maromvz.spaserver.security.annotations.CanCreateAdmins;
import com.maromvz.spaserver.services.AuthService;
import com.maromvz.spaserver.services.RefreshTokenService;
import com.maromvz.spaserver.utils.JwtUtils;
import com.maromvz.spaserver.utils.RefreshTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenUtils refreshTokenUtils;

    @PostMapping("/register")
    @CanCreateAdmins
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterUserDto userDto
            ) {
        try {
            User newUser = authService.registerUser(userDto);

            return ResponseEntity.ok(new Object() {
                public final String message = "The user was created successfully";
                public final User user = newUser;
            });

        } catch (PasswordsDontMatchException exception) {
            log.error("Passwords don't match");
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            log.error("An internal server error ocurred while trying to register a new User.");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginDto loginDto
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            var token = jwtUtils.generateJwtToken(authentication);

            User user = (User) authentication.getPrincipal();

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            ResponseCookie responseCookie = refreshTokenUtils.generateRefreshCookie(refreshToken.getToken());

            JwtResponse jwtResponse = new JwtResponse(token, user.getEmail(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(jwtResponse);
        } catch(Exception e) {
            log.info("An error ocurred while trying to login the user");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest req) {
        String token = refreshTokenUtils.getRefreshTokenFromRequest(req);
        log.info("Refresh Token: " + token);

        if (token != null && !token.isEmpty()) {
            return refreshTokenRepo.findByToken(token)
                    .map(refreshTokenService::verifyTokenExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        String freshToken = jwtUtils.generateTokenFromEmail(user.getEmail());

                        return ResponseEntity.ok(new JwtResponse(freshToken, user.getEmail(), user.getAuthorities().stream().map(a -> a.getAuthority()).toList()));
                    })
                    .orElseThrow(() -> new RuntimeException("Token was not found on the database."));
        }

        return ResponseEntity.badRequest().body("The refresh token is missing.");
    }
}
