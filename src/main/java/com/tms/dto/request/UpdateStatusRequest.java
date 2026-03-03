package com.tms.dto.request;

import com.tms.model.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateStatusRequest {
    @NotNull
    private TaskStatus status;
}
