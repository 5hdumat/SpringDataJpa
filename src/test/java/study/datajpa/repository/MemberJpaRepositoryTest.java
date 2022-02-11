package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void testMember() throws Exception {
        //given
        Member member = new Member("userA");

        // when
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        // then
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() throws Exception {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // when
        Member findMember1 = memberJpaRepository.findById(member1.getId()).orElseGet(() -> createMember("memberA"));
        Member findMember2 = memberJpaRepository.findById(member2.getId()).orElseGet(() -> createMember("memberB"));

        // then

        // 단건 조회 검증
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        Long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        Long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    private Member createMember(String name) {
        Member member = new Member(name);
        memberJpaRepository.save(member);
        return member;
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() throws Exception {
        //given
        Member member = new Member("userA", 10);
        Member member2 = new Member("userB", 20);

        memberJpaRepository.save(member);
        memberJpaRepository.save(member2);

        // when
        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("userA", 10);

        // then
        assertThat(members.get(0).getUsername()).isEqualTo("userA");
        assertThat(members.get(0).getAge()).isEqualTo(10);
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() throws Exception {
        //given
        Member member = new Member("userA", 10);
        Member member2 = new Member("userB", 20);

        memberJpaRepository.save(member);
        memberJpaRepository.save(member2);

        // when

        List<Member> members = memberJpaRepository.findByUsername("userA");

        // then
        assertThat(members.get(0)).isEqualTo(member);
    }

    @Test
    public void paging() throws Exception {
        //given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 10);
        Member member3 = new Member("userC", 10);
        Member member4 = new Member("userD", 10);
        Member member5 = new Member("userE", 10);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);
        memberJpaRepository.save(member5);

        int age = 10;
        int offset = 1;
        int limit = 3;

        // when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        Long totalCount = memberJpaRepository.totalCount(age);

        // then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 19);
        Member member3 = new Member("userC", 20);
        Member member4 = new Member("userD", 21);
        Member member5 = new Member("userE", 40);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        memberJpaRepository.save(member3);
        memberJpaRepository.save(member4);
        memberJpaRepository.save(member5);

        // when
        int resultCount = memberJpaRepository.bulkAgePlus(10);

        // then
    }
}