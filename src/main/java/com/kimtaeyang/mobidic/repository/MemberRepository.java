package com.kimtaeyang.mobidic.repository;

import com.kimtaeyang.mobidic.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    @Query("select m from Member m")
    List<Member> findAll();
    Optional<Member> findById(UUID id);
    Optional<Member> findByEmail(String email);
    int countByNickname(String nickname);
    int countByEmail(String email);
    int countByNicknameAndIdNot(String nickname, UUID id);
}