package com.maromvz.spaserver.dto.auth;

import java.util.List;

public record JwtResponse(String token, String email, List<String> roles) {}
