package org.example.model.req;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private Set<RoleReq> roles;

    @Setter
    @Getter
    public static class RoleReq{
        private long id;
    }
}
