package com.tms.dto.request;

import com.tms.model.enums.TaskPriority;
import com.tms.model.enums.TaskStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateTaskRequest {
    @NotBlank @Size(max = 500)
    private String title;

    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private UUID assigneeId;

    @Size(max = 1000)
    private String tags;

    @DecimalMin("0.0") @DecimalMax("999.99")
    private BigDecimal estimatedHours;

    @DecimalMin("0.0") @DecimalMax("999.99")
    private BigDecimal actualHours;
}
