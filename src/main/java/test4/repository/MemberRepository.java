package test4.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import test4.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByLoginId(String loginId);

    @Query("select m.loginId from Member m where m.name = :name and m.email = :email")
    Optional<String> findLoginIdByNameAndEmail(String name, String email);
}
