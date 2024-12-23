package test4.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import test4.entity.FaQ;

@Repository
public interface FAQRepository extends JpaRepository<FaQ, Long> {
    public List<FaQ> findAllByDeletedYn(String deleted);
}
