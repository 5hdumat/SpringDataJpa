package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    /**
     * 도메인 클래스 컨버터 사용 전
     */
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 도메인 클래스 컨버터 사용 후
     * <p>
     * 단, 사용을 권장하진 않는다.
     * 굳이 사용한다면 정말 간단한 엔티티 조회용으로만 사용하자.
     * (트랜잭션 범위를 잡고 사용하는게 아니므로, 엔티티 값을 변경해도 DB에 반영되지 않는다.)
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> memberDtos = page.map(m -> new MemberDto(m));
        return memberDtos;
    }

//    @PostConstruct
    public void init() {
        memberRepository.save(new Member("member1"));
        memberRepository.save(new Member("member2"));
        memberRepository.save(new Member("member3"));
        memberRepository.save(new Member("member4"));
        memberRepository.save(new Member("member5"));
        memberRepository.save(new Member("member6"));
        memberRepository.save(new Member("member7"));
        memberRepository.save(new Member("member8"));
        memberRepository.save(new Member("member9"));
        memberRepository.save(new Member("member10"));
    }

}
