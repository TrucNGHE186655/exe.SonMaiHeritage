package exe.SonMaiHeritage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    
    @Column(name="username", unique = true, nullable = false)
    private String username;
    
    @Column(name="email", unique = true, nullable = false)
    private String email;
    
    @Column(name="password", nullable = false)
    private String password;
    
    @Column(name="first_name")
    private String firstName;
    
    @Column(name="last_name")
    private String lastName;
    
    @Column(name="phone")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private Role role = Role.USER;
    
    @Column(name="enabled")
    private Boolean enabled = true;
    
    @Column(name="account_non_expired")
    private Boolean accountNonExpired = true;
    
    @Column(name="account_non_locked")
    private Boolean accountNonLocked = true;
    
    @Column(name="credentials_non_expired")
    private Boolean credentialsNonExpired = true;

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses;

    public enum Role {
        USER, ADMIN
    }
}
