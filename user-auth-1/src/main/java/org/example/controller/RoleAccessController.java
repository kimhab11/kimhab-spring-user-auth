package org.example.controller;

import org.example.enums.ERole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("role")
public class RoleAccessController {

    @GetMapping("admin")
    public String admin() {
        return ERole.ADMIN.name();
    }

    @GetMapping("manager")
    public String manager() {
        return ERole.MANAGER.name();
    }

    @GetMapping("all")
    public String all() {
        return "ALL ROLES";
    }


    @PatchMapping("role/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String preAuth() {
        return "@PreAuthorize(\"hasRole('ROLE_ADMIN')\")";
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER','MANAGER')")
    @PatchMapping("role/all")
    public String preAuthAny() {
        return "@PreAuthorize(\"hasAnyRole('ADMIN','USER','MANAGER')\")";
    }
}
