package test4.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import test4.entity.Apply;

@Repository
public interface ApplyRepository extends JpaRepository<Apply, Long> {
    @Query("SELECT a FROM Apply a WHERE a.id = :id")
    Apply selectApplyDetail(Long id); // ID로 특정 게시물 정보를 반환
    // 전체 신청서 목록 (관리자 전용)
    List<Apply> findAll(Sort sort);
    // 특정 사용자 신청서 목록
    List<Apply> findAllByUidOrderByCreatedAtDesc(Long uid);
    Apply findByUid(Long uid);
    public List<Apply> findAllByCompletedYnOrderByIdAsc(char completedYn);
}
