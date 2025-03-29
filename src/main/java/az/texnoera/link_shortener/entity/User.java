package az.texnoera.link_shortener.entity;

import az.texnoera.link_shortener.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fullName;
    @Column(unique = true)
    private String email;
    private String password;
    private Integer otp;
    private Boolean isVerified=false;
    private LocalDateTime expiryDate;
    private String profilePhoto;
    @ManyToMany
    private Set<Role> roles = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(mappedBy = "user")
    private Set<Url> links = new HashSet<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
