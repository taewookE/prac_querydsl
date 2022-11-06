package com.example.querydsl.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithJobData {
    private String name;
    private int age;
    private String jobName;
}
