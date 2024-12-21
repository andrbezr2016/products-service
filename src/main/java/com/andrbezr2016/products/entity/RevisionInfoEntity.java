package com.andrbezr2016.products.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@NoArgsConstructor
@Setter
@Getter
@RevisionEntity
@Entity
@Table(name = "revinfo")
public class RevisionInfoEntity {

    @Id
    @GeneratedValue
    @Column(name = "rev", nullable = false)
    @RevisionNumber
    private Long id;
    @Column(name = "revtstmp", nullable = false)
    @RevisionTimestamp
    private Long timestamp;
}
