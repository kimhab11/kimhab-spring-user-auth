package org.example.model.req;

import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private Set<RoleReq> roles;
}

