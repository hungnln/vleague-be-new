package com.hungnln.vleague.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "accounts")
public class Account implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "name")
    private  String name;
    @Column(name = "imageurl")
    private  String imageURL;
    @Column(name = "isbanned")
    private  boolean isBanned;
    @Column(name = "email",nullable = false,unique = true)
    private  String email;
}

