package study.datajpa.entity;

import lombok.Getter;
import org.apache.tomcat.jni.Local;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * 순수 JPA로 MapaedSuperClass 구현하기
 *
 * MapaedSuperClass?
 * 특정 엔티티의 속성만 도메인 엔티티에 내려서 쓸 수 있게 하는 객체를
 * @MappedSuperClass라고 한다.
 */
@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
