package net.cavitos.workshop.security.service;

import net.cavitos.workshop.security.domain.UserProfile;

public interface UserService {

    UserProfile getUserProfile(String username);
}
