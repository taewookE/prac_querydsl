package com.example.querydsl;

import com.example.querydsl.domain.QUser;
import com.example.querydsl.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
public class QuerydslTest {

    @Autowired
    EntityManager entityManager;

    @Test
    void querydslTest(){

        User user = new User();
        user.setName("wook");
        user.setEmail("taewook@gmail.com");
        user.setAge(36);
        entityManager.persist(user);

        User user2 = new User();
        user2.setName("wook2222");
        user2.setEmail("taewook222@gmail.com");
        user2.setAge(36);
        entityManager.persist(user2);

        /*TODO: queryDSL 문법학습 필요. (e.g. join관련)
        *       alias를 바꾸어도 왜 실제 jpa query에서는 제대로 동작을 하지 않는지.. ?
        *
        *
        * */
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager); //JPAQueryFactory 생성 , 생성자로 entityManager 주입
        QUser qUser = new QUser("test"); // QUser 객체 생성 , 생성자에는 Entity의 alias로 사용할 변수명을 기입.
        User found = queryFactory.selectFrom(qUser)
                .where(qUser.name.eq("wook2222"))
                .fetchOne(); // JPQL과 같이 자바코드로 쿼리를 작성

        System.out.println(found);
        System.out.println(user);
        assertEquals(found,user2);

    }
}
