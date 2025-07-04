package com.trubochisty.truboserver.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder

//по-идее нужен отдельный entity class, но и так сойдет
public class User implements UserDetails {
    @Id
    @UuidGenerator
    String id;

    @ManyToMany(mappedBy = "users")
    @JsonBackReference
    private List<Culvert> culverts = new ArrayList<>();

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String password;

    @Column(name = "created_at", nullable = false)
    LocalTime createdAt;

    @Column(name = "updated_at",nullable = false)
    LocalTime updatedAt;

    @Column(name = "phone_number")
    String phoneNumber;

    String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
