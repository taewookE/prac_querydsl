package com.example.querydsl;

import com.example.querydsl.domain.Gender;
import com.example.querydsl.domain.Job;
import com.example.querydsl.domain.QUser;
import com.example.querydsl.domain.User;
import com.example.querydsl.repository.dto.UserData;
import com.example.querydsl.repository.dto.UserDefaultData;
import com.example.querydsl.repository.dto.UserWithJobData;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.querydsl.domain.QJob.job;
import static com.example.querydsl.domain.QUser.user;


@SpringBootTest
@Transactional
public class QuerydslTest {

    @Autowired
    EntityManager entityManager;
    JPAQueryFactory queryFactory;


    /*TODO: Test를 위한 셋팅에 대해서도 학습할것.*/
    @BeforeEach
    void insertUser(){

        Job job1 = new Job();
        job1.setName("teacher");
        Job job2 = new Job();
        job2.setName("student");

        entityManager.persist(job1);
        entityManager.persist(job2);

        User user = new User();
        user.setName("wook");
        user.setEmail("taewook@gmail.com");
        user.setAge(36);
        user.setGender(Gender.MALE);
        user.setJob(job1);
        entityManager.persist(user);

        User user2 = new User();
        user2.setName("wook2222");
        user2.setEmail("taewook222@gmail.com");
        user2.setAge(35);
        user2.setGender(Gender.MALE);
        user2.setJob(job1);
        entityManager.persist(user2);

        User user3 = new User();
        user3.setName("test333");
        user3.setEmail("taewook33332@gmail.com");
        user3.setAge(20);
        user3.setGender(Gender.FEMALE);
        user3.setJob(job2);
        entityManager.persist(user3);

        User user4 = new User();
        user4.setName("wook4");
        user4.setEmail("taewook44@gmail.com");
        user4.setAge(22);
        user4.setGender(Gender.FEMALE);
        user4.setJob(job2);
        entityManager.persist(user4);

        queryFactory = new JPAQueryFactory(entityManager);

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
    void User_list(){
        List<User> users = queryFactory.select(user)
                .from(user)
                .fetch();
        System.out.println(users);
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

    @Test
    void function_Test(){
        Tuple users = queryFactory.select(user.count(),user.age.sum(),user.age.avg(), user.age.max(),user.age.min())
                .from(user)
                .fetchOne();
        System.out.println(users);

    }



    /*TODO: user내 gender 추가하여 grouping. Entity의 변경사항이 있을때 마다 Q클래스에 바로 반영이 안되므로
    *       compile을 매번해주어야 하는것인지 ?? 자동반영방법은 없는것인지 ?
    *       연관관계생성을 위해서 job entity를 추가하고 OneToOne으로 mapping
    *
    * */
    /*TODO : join에서 에러가 발생 .join(user.job, job)
             Qclass를 import해서 join했는데 에러가 나는이유는 ? querydsl build를 안해줘서 ... 현행화가 되지 않았었음.
             OneToOne 관계설정에 대해서 다시 학습이 필요하다.
     \*/
    @Test
    void Group_Test(){
        List<Tuple> ages = queryFactory.select(user.job, user.age.avg())
                .from(user)
                .join(user.job, job)
                .groupBy(job.name)
                .having(user.age.avg().goe(10))
                .fetch();

        System.out.println(ages);

    }

    /*TODO: Job에 대한 연관관계 생성에 대해서 참조해볼것
    *
    *
    * */
    @Test
    void join_test(){
        List<User> users = queryFactory.select(user)
                .from(user)
                .join(user.job,job)
                .fetch();

        users.forEach(p -> System.out.printf("%s , %s%n",p, p.getJob()));
    }

    @Test
    void simple_theta_join(){
        List<User> users = queryFactory.select(user)
                .from(user,job)
                .where(user.job.name.eq(job.name))
                .fetch();

        users.forEach(u-> System.out.printf("%s %s%n", u ,u.getJob()));
//        System.out.println(users);
    }

    @Test
    void on_Test(){
        List<Tuple> tuples = queryFactory.select(user,job)
                .from(user)
//                .leftJoin(user.job,job)
                .join(user.job,job)
                .on(job.name.eq("teacher"))
                .fetch();
        tuples.forEach(System.out::println);
    }

    /*TODO: fetchJoin의 다른 활용방법에 대해서 확인해보기. 성능을 위한 부분이긴하지만 사용방법에 대해서 좀더 확실히 해두는게 좋을 듯.*/
    @Test
    void FetchJoin_Test(){
        User founded = queryFactory.selectFrom(user)
                .join(user.job, job)
                .fetchJoin()
                .where(user.name.eq("wook"))
                .fetchOne();
        System.out.println(founded + " " + founded.getJob());
    }

    /*TODO: 왠만하면 서브쿼리를 사용하지 않고, 쿼리를 분리하여서 사용하는것이 좋다.*/
    @Test
    void 서브쿼리테스트(){
        QUser subUser = new QUser("subUser");

        User found = queryFactory.selectFrom(user)
                .where(user.age.eq(
                        JPAExpressions
                                .select(subUser.age.max())
                                .from(subUser)
                )).fetchOne();

        System.out.println(found);
    }

    @Test
    void constant_test(){
        List<Tuple> age = queryFactory.select(user.name, Expressions.constant("NAME"))
                .from(user)
                .fetch();
        age.forEach(System.out::println);
    }

    @Test
    void concat_test(){
        List<String> nameWithAge = queryFactory
                .select(user.name.concat(": ").concat(user.age.stringValue()))
                .from(user)
                .orderBy(user.age.desc())
                .fetch();
        nameWithAge.forEach(System.out::println);
    }

    @Test
    void Projection(){
        List<String> userNames = queryFactory.select(user.name)
                .from(user)
                .fetch();
        userNames.forEach(System.out::println);
    }

    @Test
    void Projection2() {
        List<Tuple> tuples = queryFactory.select(user.name, user.age)
                .from(user)
                .fetch();
        tuples.forEach(System.out::println);
        tuples.forEach(t -> System.out.printf("%s, %s%n", t.get(user.name), t.get(user.age)));
    }

    @Test
    void classMappingTest(){
        List<UserDefaultData> users = queryFactory
//                .select(Projections.bean(UserDefaultData.class, user.name,user.age))
//                .select(Projections.fields(UserDefaultData.class, user.name,user.age))
                .select(Projections.constructor(UserDefaultData.class, user.name,user.age))
                .from(user)
                .fetch();

        users.forEach(System.out::println);
    }

    /*TODO: 필드가 다른경우 constructor 이외에는 null 값이 들어가게 된다.
    *       UserData(userName=null, userAge=0)
            UserData(userName=null, userAge=0)
            UserData(userName=null, userAge=0)
            UserData(userName=null, userAge=0)
    *       이를 해결하기 위해 as를 사용하여 name을 맵핑시켜준다.
    * */
    @Test
    void 필드명이다른경우_Test(){
        List<UserData> users = queryFactory
                .select(Projections.bean(UserData.class,user.name.as("userName"),user.age.as("userAge")))
                .from(user)
                .fetch();
        users.forEach(System.out::println);
    }

    @Test
    void 프로젝션_Join(){
//        List<UserWithJobData> users = queryFactory
//                .select(Projections.bean(UserWithJobData.class),user.name,user.age,user.job.name.as("jobName"))
//                .from(user)
//                .join(user.job,job)
//                .fetch();

//        users.forEach(System.out::println);

    }

}
