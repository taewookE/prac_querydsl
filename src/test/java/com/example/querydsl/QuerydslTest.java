package com.example.querydsl;

import com.example.querydsl.domain.QUser;
import com.example.querydsl.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
public class QuerydslTest {

    @Autowired
    EntityManager entityManager;

    /*TODO: Test를 위한 셋팅에 대해서도 학습할것.*/
    @BeforeEach
    void insertUser(){

        User user = new User();
        user.setName("wook");
        user.setEmail("taewook@gmail.com");
        user.setAge(36);
        entityManager.persist(user);

        User user2 = new User();
        user2.setName("wook2222");
        user2.setEmail("taewook222@gmail.com");
        user2.setAge(35);
        entityManager.persist(user2);

        User user3 = new User();
        user3.setName("test333");
        user3.setEmail("taewook33332@gmail.com");
        user3.setAge(20);
        entityManager.persist(user3);

        User user4 = new User();
        user4.setName("wook4");
        user4.setEmail("taewook44@gmail.com");
        user4.setAge(22);
        entityManager.persist(user4);


    }

    @Test
    void querydslTest(){



        /*TODO: queryDSL 문법학습 필요. (e.g. join관련)
        *       alias를 바꾸어도 왜 실제 jpa query에서는 제대로 동작을 하지 않는지.. ?
        *       where 절 내부로 .and를 붙여서 조건식을 하나씩 추가해 줄 수 있다.
        *       조건절 : equal(==,eq), not equal(!=, ne) , like("%xxx") , less than (<, lt)
        *
        *
        *
        * */
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager); //JPAQueryFactory 생성 , 생성자로 entityManager 주입
        QUser qUser = new QUser("test"); // QUser 객체 생성 , 생성자에는 Entity의 alias로 사용할 변수명을 기입.
        User found = queryFactory.selectFrom(qUser)
                .where(qUser.name.eq("wook2222")
                        .and(qUser.age.eq(30)))
                .fetchOne(); // JPQL과 같이 자바코드로 쿼리를 작성

        System.out.println(found);

    }

    @Test
    void queryDsl_comma_condition_test(){


        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QUser qUser = new QUser("user");
        User found = queryFactory.selectFrom(qUser)
                .where(qUser.name.like("wook%"), qUser.age.lt(35))
                .fetchFirst();

        System.out.println(found);
    }

    /*TODO : Quser를 선언해서 selectFrom에 사용하는식으로 쓰는데 현업에서는 어떻게 쓰는지 확인해보기 */
    @Test
    void order_Test(){
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QUser user = new QUser("u");
        List<User> users = queryFactory.selectFrom(user)
                .orderBy(user.age.desc(), user.name.asc().nullsLast())
                .fetch();

        System.out.println(users);

    }
}
