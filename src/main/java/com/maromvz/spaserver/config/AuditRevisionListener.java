package com.maromvz.spaserver.config;

import com.maromvz.spaserver.entities.User;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity rev = (CustomRevisionEntity) revisionEntity;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            User user = (User) auth.getPrincipal();

            rev.setFingerprint(user.getFirstName() + " " + user.getLastName() + " " + user.getEmail());
        } else {
            rev.setFingerprint("SYSTEM");
        }
    }
}
