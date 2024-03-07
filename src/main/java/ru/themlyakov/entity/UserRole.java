package ru.themlyakov.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import ru.themlyakov.util.Role;

@Data
@Entity(name="roles")
@NoArgsConstructor
public class UserRole implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public String getAuthority() {
        return role.name();
    }
}
