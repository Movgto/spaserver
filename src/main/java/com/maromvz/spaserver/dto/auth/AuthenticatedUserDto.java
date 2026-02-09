package com.maromvz.spaserver.dto.auth;

public record AuthenticatedUserDto(
        String firstName,
        String lastName,
        String email
) {
}
