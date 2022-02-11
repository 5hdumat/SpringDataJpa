package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testEntity() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 20, teamA);
        Member memberC = new Member("memberC", 30, teamB);
        Member memberD = new Member("memberD", 40, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

        em.flush(); // 트랜잭션 커밋
        em.clear(); // 1차 캐시 제거

        // when
        List<Member> members = em.createQuery("select m from Member m join fetch m.team", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member -> " + member);
            System.out.println("member.getTeam() -> " + member.getTeam());
        }

        // then
    }


    @Test
    public void JpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("BemberA");
        memberRepository.save(member);

        Thread.sleep(100);
        member.changeUsername("MemberB");

        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        System.out.println(findMember.getCreatedDate());
        System.out.println(findMember.getLastModifiedDate());
        System.out.println(findMember.getCreatedBy());
        System.out.println(findMember.getUpdatedBy());
    }

}