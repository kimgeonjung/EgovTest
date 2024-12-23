package test4.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import test4.entity.QnA;
import test4.entity.QnAList;

@Repository
public interface QnARepository extends JpaRepository<QnA, Long> {
    List<QnA> findAllByQnaIdOrderByIdAsc(QnAList qnaId);
}
