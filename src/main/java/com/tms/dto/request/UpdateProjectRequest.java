package com.tms.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateProjectRequest {
    @NotBlank @Size(max = 200)
    private String name;

    private String description;
}
