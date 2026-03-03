package com.tms.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardSummaryResponse {
    private long totalTasks;
    private long completedTasks;
    private long inProgressTasks;
    private long overdueTasks;
}
