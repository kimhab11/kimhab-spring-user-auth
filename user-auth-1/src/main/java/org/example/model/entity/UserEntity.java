package org.example.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "user_tb")
@ToString
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @ToString.Exclude
    private String password;

    @ToString.Exclude
    private String accessToken;

    @ToString.Exclude
    private String refreshToken;

    @Column(nullable = false, length = 10)
    private Integer failLoginAttempt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

  //  @ManyToMany(fetch = FetchType.EAGER)
    @ManyToMany(fetch = FetchType.LAZY) // change to EAGER if no @Transactional
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();
}
