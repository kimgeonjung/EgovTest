package test4.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import test4.entity.Qna;
import test4.entity.QnaList;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    List<Qna> findAllByQnaIdOrderByIdAsc(QnaList qnaId);
}
