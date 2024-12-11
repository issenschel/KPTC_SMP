package com.example.kptc_smp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users_information")
public class UserInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long  id;

    @OneToOne
    @MapsId
    private User user;

    @Column(name = "email")
    private String email;

    @Column(name = "minecraftname")
    private String minecraftName;

    @Column(name = "photo")
    private String photo;
}
