package com.maskilometros.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "USERS")
@Setter @Getter
@NamedQueries({
        @NamedQuery(name = "MasKilometrosUser.fetchUserWithRoleByEmail", query =
            "SELECT DISTINCT m FROM MasKilometrosUser m JOIN FETCH m.role r WHERE m.email = :email")
})
public class MasKilometrosUser extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Size(max = 20)
    @Column(name = "MOBILE_NUMBER", nullable = false, length = 20)
    private String mobileNumber;

    @Size(max = 500)
    @NotNull
    @Column(name = "PASSWORD_HASH", nullable = false, length = 500)
    private String passwordHash;

    @NotNull
    @Column(name = "ENABLED", nullable = false)
    private boolean enabled=true;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Registration> registrations;
}
