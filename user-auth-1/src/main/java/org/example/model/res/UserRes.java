package org.example.model.res;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserRes {
    private Long id;
    private String username;
    private String[] role;
    private String createdAt;
    private LocalDateTime updatedAt;
    private int failLoginCount;
}
