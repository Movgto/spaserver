package com.maromvz.spaserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.auth.super-admin")
@Data
public class AdminProperties {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
