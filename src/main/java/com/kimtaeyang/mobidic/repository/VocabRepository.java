package com.kimtaeyang.mobidic.repository;

import com.kimtaeyang.mobidic.entity.Member;
import com.kimtaeyang.mobidic.entity.Vocab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VocabRepository extends JpaRepository<Vocab, UUID> {
    List<Vocab> findByMember(Member member);

    int countByTitleAndMemberAndIdNot(String title, Member member, UUID id);

    int countByTitleAndMember(String title, Member member);
}