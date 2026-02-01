package com.maromvz.spaserver.config;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionMapping;

@Entity
@Table(name = "rev_info")
@RevisionEntity(AuditRevisionListener.class)
@Getter
@Setter
public class CustomRevisionEntity extends RevisionMapping {
    private String fingerprint;
}
