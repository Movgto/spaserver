package com.maromvz.spaserver.filters;

import com.maromvz.spaserver.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = parseRequest(request);

        if (jwtUtils.verifyJwtToken(token)) {
            log.info("Token has been verified and it's valid.");
            String email = jwtUtils.getEmailFromJwtToken(token);

            log.info("Email of the authenticated user: " + email);

            var user = userDetailsService.loadUserByUsername(email);

            log.info("Authenticated user:");

            log.info(user.toString());

            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("The user has been successfully authenticated.");
        }

        filterChain.doFilter(request, response);
    }

    private String parseRequest(HttpServletRequest request) {
        try {
            var authHeader = request.getHeader("Authorization");

            return authHeader.split(" ")[1];
        } catch(Exception e) {
            return "";
        }
    }
}
