package com.example.querydsl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "team_id")
    private Long id;
    private String name;

    /*TODO:Entity간의 연관관계 설정에 대해서 계속 학습해나갈것.
    *      OneToMany에서 해당 list가 필요없는데  Job(id=2, name=student, users=[]) 해당 형태로 출력되고 있어 변경 필요..
    * */
    @OneToMany(mappedBy = "job")
    private List<User> users = new ArrayList<>();

}
