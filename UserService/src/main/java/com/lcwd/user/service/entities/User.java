package com.lcwd.user.service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "micro_users")
public class User {

    @Id
    @Column(name = "ID")
    private String userId;
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
    @Size(max = 255)
    private String about;

    @Transient
    @Builder.Default
    private List<Rating> ratings = new ArrayList<>();

    // other user properties that we want
}
