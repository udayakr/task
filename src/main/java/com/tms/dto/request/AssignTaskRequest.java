package com.tms.dto.request;

import lombok.*;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AssignTaskRequest {
    private UUID assigneeId;
}
