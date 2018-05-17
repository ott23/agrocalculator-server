package net.tngroup.rest.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class User implements UserDetails {

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column
    @NotNull
    private String username;

    @Column
    @NotNull
    private String password;

    @Column
    private String token;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
