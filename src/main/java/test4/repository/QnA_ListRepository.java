package test4.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import test4.entity.QnAList;

@Repository
public interface QnA_ListRepository extends JpaRepository<QnAList, Long> {
    public List<QnAList> findAllByUidOrderByIdDesc(Long uid);
    public List<QnAList> findAllByOrderByIdDesc();
}
