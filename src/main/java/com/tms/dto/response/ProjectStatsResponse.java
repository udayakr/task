package com.tms.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectStatsResponse {
    private long total;
    private long todo;
    private long inProgress;
    private long review;
    private long done;
    private long low;
    private long medium;
    private long high;
    private long critical;
}
