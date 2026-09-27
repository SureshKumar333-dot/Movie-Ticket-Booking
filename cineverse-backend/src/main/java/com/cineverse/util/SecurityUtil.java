package com.cineverse.util;

import com.cineverse.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static User currentUser() {
        User user = currentUserOptional();
        if (user == null) {
            throw new com.cineverse.exception.BadRequestException("Not authenticated");
        }
        return user;
    }

    public static User currentUserOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return null;
        }
        return user;
    }

    public static boolean isAdmin(User user) {
        return user.getRole().name().equals("ROLE_ADMIN");
    }

    public static boolean isManager(User user) {
        return user.getRole().name().equals("ROLE_MANAGER");
    }

    public static boolean isAdminOrManager(User user) {
        return isAdmin(user) || isManager(user);
    }
}
