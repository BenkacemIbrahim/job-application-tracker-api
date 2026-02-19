package com.ibrahim.jobtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationStatsResponse {
    private long totalApplications;
    private long interviews;
    private long offers;
    private long rejected;
}
