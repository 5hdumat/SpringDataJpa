package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    EntityManager em;

    @Test
    public void testMember() throws Exception {
        System.out.println(memberRepository.getClass());

        //given
        Member member = new Member("userA");

        // when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() throws Exception {
        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member findMember1 = memberRepository.findById(member1.getId()).orElseGet(() -> createMember("memberA"));
        Member findMember2 = memberRepository.findById(member2.getId()).orElseGet(() -> createMember("memberB"));

        // then

        // ?????? ?????? ??????
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // ????????? ?????? ??????
        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        Long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    private Member createMember(String name) {
        Member member = new Member(name);
        memberRepository.save(member);
        return member;
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        //given
        Member member = new Member("userA", 15);
        Member member2 = new Member("userB", 20);

        memberRepository.save(member);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThanEqual("userA", 10);

        // then
        assertThat(members.get(0).getUsername()).isEqualTo("userA");
        assertThat(members.get(0).getAge()).isEqualTo(15);
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void findMemberById() throws Exception {
        //given
        Member member = new Member("userA", 15);
        memberRepository.save(member);

        // when
        List<Member> members = memberRepository.findTop3By();

        // then
        assertThat(members.get(0).getUsername()).isEqualTo("userA");
    }

    @Test
    public void namedQuery() throws Exception {
        //given
        Member member = new Member("userA", 15);
        Member member2 = new Member("userB", 20);

        memberRepository.save(member);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("userA", 10);

        // then
        assertThat(members.get(0)).isEqualTo(member);
    }

    @Test
    public void Query() throws Exception {
        //given
        Member member = new Member("userA", 15);
        Member member2 = new Member("userB", 20);

        memberRepository.save(member);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findUser("userA", 10);

        // then
        assertThat(members.get(0)).isEqualTo(member);
    }

    @Test
    public void StringQuery() throws Exception {
        //given
        Member member = new Member("userA", 15);
        Member member2 = new Member("userB", 20);

        memberRepository.save(member);
        memberRepository.save(member2);

        // when
        List<String> memberNames = memberRepository.findUserNameList("userA", 10);

        // then
        assertThat(memberNames.get(0)).isEqualTo(member.getUsername());
    }

    @Test
    public void DtoQuery() throws Exception {
        //given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("userA", 20, team);
        memberRepository.save(member);

        // when
        List<MemberDto> memberDtos = memberRepository.findMemberDto("userA", 10);

        for (MemberDto memberDto : memberDtos) {
            System.out.println(memberDto);
        }
        // then
        assertThat(memberDtos.get(0).getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void findByName() throws Exception {
        //given
        Member member1 = new Member("userA", 15);
        Member member2 = new Member("userB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> members = memberRepository.findByNames(Arrays.asList("userA", "userB"));

        for (Member member : members) {
            System.out.println(member);
        }

        // then
        assertThat(members.get(0)).isEqualTo(member1);
    }

    @Test
    public void returnType() throws Exception {
        //given
        Member member1 = new Member("userA", 15);
        Member member2 = new Member("userB", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        List<Member> memberList = memberRepository.findListByUsername("uwserA");
        Member member = memberRepository.findMemberByUsername("userdA");
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("uswdwderA");

        // then
        assertThat(memberList.size()).isEqualTo(0);
        assertThat(member).isEqualTo(null);
        assertThat(optionalMember).isEqualTo(Optional.empty());
    }

    @Test
    public void paging() throws Exception {
        //given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 10);
        Member member3 = new Member("userC", 10);
        Member member4 = new Member("userD", 10);
        Member member5 = new Member("userE", 10);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> members = page.getContent();

        assertThat(members.size()).isEqualTo(3);
        assertThat(toMap.getTotalElements()).isEqualTo(5L);
        assertThat(toMap.getNumber()).isEqualTo(0);
        assertThat(toMap.getTotalPages()).isEqualTo(2);
        assertThat(toMap.isFirst()).isTrue();
        assertThat(toMap.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        Member member1 = new Member("userA", 10);
        Member member2 = new Member("userB", 19);
        Member member3 = new Member("userC", 20);
        Member member4 = new Member("userD", 21);
        Member member5 = new Member("userE", 40);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);

        // when
        memberRepository.bulkAgePlus(10);

        List<Member> result = memberRepository.findByUsername("userE");

        // then
        Member member = result.get(0);
        System.out.println(member);
    }

    @Test
    public void findMemberLazy() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("userA", 10, teamA);
        Member member2 = new Member("userB", 19, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findEntityGraphByUsername("userA");

        for (Member member : members) {
            System.out.println(member.getClass());
            System.out.println(member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() throws Exception {
        //given
        Member member = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member findMember = memberRepository.findReadOnlyByUsername(member.getUsername());
        findMember.changeUsername("member2");

        em.flush();
        // then
    }

    @Test
    public void lock() throws Exception {
        //given
        Member member = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();


        // when
        Member findMember = memberRepository.findLockByUsername(member.getUsername());
    }

    /**
     * SpringData JPA ????????? ?????? ??????????????? ??????
     * <p>
     * 1) ????????? ?????? ?????????????????? ????????????. (MemberRepositoryCustom)
     * 2) ??????????????? ???????????? ????????????. (findMemberCustom)
     * 3) ????????? ?????????(MemberRepositoryImpl)??? ????????? ??? ????????? ?????? ??????????????????(MemberRepositoryCustom)??? ????????????.
     * 4) JPA ??????????????? ?????????????????? ????????????. (MemberRepository -> MemberRepositoryCustom)
     * 5) ????????? ?????? ?????????????????? ???????????? 3????????? ????????? ????????? ???????????? ????????????. (Data JPA??? ??????)
     * <p>
     * ???, ????????? ????????? ???????????? MemberRepository??? ?????? ?????????????????? API ????????? ?????? ????????? ?????? ???????????? ??? ?????????
     * ????????? MemberQueryRepository ???????????? ???????????? ?????? ???????????? ?????? ??? ??????????????? ??????.
     */
    @Test
    public void callCustom() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("userA", 10, teamA);
        Member member2 = new Member("userB", 19, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findMemberCustom();

        // then
        for (Member member : members) {
            System.out.println(member.getTeam().getClass());
        }
    }

    /**
     * ????????????!
     */

    // JPA Specifications
    @Test
    public void specBasic() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("memberA", 0, teamA);
        Member member2 = new Member("memberB", 0, teamA);

        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpec.username("memberA").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // then
        assertThat(result.size()).isEqualTo(1);
    }
}