package com.example.embroideryshop.model;

import com.sun.istack.NotNull;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 80)
    private String email;
    @Column(nullable = false, length = 50)
    private String username;
    @Column(nullable = false, length = 80)
    private String password;
}
