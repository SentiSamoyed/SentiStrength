package web.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import web.entity.po.ReleasePO;

import java.util.List;

public interface ReleaseRepository extends CrudRepository<ReleasePO, Long> {
  @Query(value = """
            SELECT id, repo_full_name, tag_name, created_at, sum_hitherto, count_hitherto, pos_cnt_hitherto, neg_cnt_hitherto
             FROM `release`
             WHERE repo_full_name = :fullName
             ORDER BY created_at;
      """, nativeQuery = true)
  List<ReleasePO> findByRepoFullNameOrderByCreatedAt(@Param("fullName") String fullName);

  @Modifying
  @Query(value = """
            UPDATE `release`
             SET sum_hitherto = :sum, count_hitherto = :count, pos_cnt_hitherto = :posCnt, neg_cnt_hitherto = :negCnt
             WHERE id = :id
      """, nativeQuery = true)
  void updateAnalysisData(long id, int sum, int count, int posCnt, int negCnt);
}
