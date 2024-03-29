package web.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.entity.po.RepoPO;

@Repository
public interface RepoRepository extends CrudRepository<RepoPO, Long> {
  RepoPO findByFullName(String fullName);

  boolean existsByFullName(String fullName);

  @Modifying
  @Query("update RepoPO r set r.lastAnalysisTs = :lastAnalysis where r.id = :id")
  void updateLastAnalysisTime(@Param("id") long id, @Param("lastAnalysis") long lastAnalysis);
}
