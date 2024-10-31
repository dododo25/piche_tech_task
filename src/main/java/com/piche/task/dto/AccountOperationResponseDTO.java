package com.piche.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AccountOperationResponseDTO {

    private Long id;

    private String type;

    private String role;

    private double deposit;

    private LocalDateTime updatedAt;
}
