package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.Lob;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {
    List<Member> findByUsernameAndAgeGreaterThanEqual(String username, int age);

    List<Member> findTop3By();

    /**
     * Member 엔티티에 findByUsername이라는 이름을 가진 네임드쿼리가 존재하는지 먼저
     * 체크하므로 생략가능
     */
    // @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    List<Member> findByUsernameAndAgeGreaterThan(@Param("username") String username, @Param("int") int age);

    /**
     * (권장)
     */

    // Repository에 엔티티 조회 쿼리 작성하기
    @Query("select m from Member m where m.username = :username and m.age > :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // Repository에 엔티티 조회 쿼리 작성하기
    @Query("select m.username from Member m where m.username = :username and m.age > :age")
    List<String> findUserNameList(@Param("username") String username, @Param("age") int age);

    // 명시적 조인 권장
    // -> select 절: t.name
    // -> from 절: join m.team t
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t where m.username = :username and m.age > :age")
    List<MemberDto> findMemberDto(@Param("username") String username, @Param("age") int age);

    // in절 활용
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    /**
     * Spring Data Jpa는 다양한 반환타입을 지원한다.
     * 또한 컬렉션의 경우 존재하지 않는 데이터는 Null이 아닌 Empty 컬렉션이 반환됨을 보장한다.
     */
    List<Member> findListByUsername(String username);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalByUsername(String username);

    // Count 쿼리 분리
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    // 슬라이스 페이징 기능
    // Slice<Member> findByAge(int age, Pageable pageable);

    /**
     * Bulk 연산
     * <p>
     * [주의점]
     * 1. @Modifying 애노테이션이 있어야 excuteUpdate를 실행한다.
     * 2. 벌크 연산 이후엔 영속성 컨택스트를 꼭 clear() 해줘야 한다. (clearAutomatically = true 속성 추가)
     * -> clear해주지 않으면 연산 후 영속성 컨택스트와 DB의 싱크가 맞지 않을 수도 있다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * fetch는 join 뿐만 아니라 select절에 있는
     * EntityGraph에 프록시 객체가 아닌 실제 엔티티 객체들을 모두 조회해서 넣어준다.
     */
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    /**
     * 하지만 fetch를 사용하기 위해선 항상 JPQL을 사용해야한다.
     * Spring data JPA는 이를 더 편리하게 사용할 수 있도록 한다.
     */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @Query("select m from Member m")
    @EntityGraph(attributePaths = {"team"})
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m where m.username = :username")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // Member 엔티티의 namedEntityGraph 이용하는 법 (권장 X)
    //    @EntityGraph("Member.all")
    //    List<Member> findNamedEntityGraphByUsername(@Param("username") String username);

    /**
     * 힌트와 락
     */
    // 영속성 컨택스트의 스냅샷으로 저장하지 않는다.
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);

}
