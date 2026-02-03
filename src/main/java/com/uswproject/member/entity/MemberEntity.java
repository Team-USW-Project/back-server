package com.uswproject.member.entity;

import com.uswproject.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Entity : 엔티티임을 명시함.
 * @NoArgsConstructor : 기본생성자 어노테이션 Member객체를 조회할 시, 기본생성자가 필요함
 * AccessLevel.PROTECTED : Entity레벨 외에 서비스, 컨트롤러 레이어에서 객체 생성 접근을 막기 위함
 * @Getter : 객체 조회 시 Getter 메서드가 존재해야 가능하지만, 이를 가능케 해주는 Lombok 어노테이션
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
public class MemberEntity extends BaseEntity {

    /**
     * @Id(PK)
     * @GeneratedValue(strategy = GenerationType.IDENTITY) : Member가 DB에 저장되면, ID값을 알아서 채워줌
     * @Column(name = "member_id" ) : member_id라는 이름으로 Entity컬럼을 만들거고, 실제로는 id라는 값으로 활용할거임
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_password")
    private String password;

    /**
     * 생성자를 private로 닫아 외부에서 new Member()를 통한 생성 금지
     * (ID는 DB에서 만들어주기에 제외)
     */
    private MemberEntity(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    /**
     * 정적 팩토리 메서드
     * 외부에서는 오직 이 메서드를 통해서만 객체 생성이 가능하다.
     */
    public static MemberEntity create(String email, String name, String password) {
        return new MemberEntity(email, name, password);
    }

}
