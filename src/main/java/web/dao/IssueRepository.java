package web.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.entity.po.IssuePO;

@Repository
public interface IssueRepository extends PagingAndSortingRepository<IssuePO, Long>, CrudRepository<IssuePO, Long> {
  @Modifying
  @Query("""
      UPDATE IssuePO i
       SET i.posVal = :posVal, i.negVal = :negVal, i.scaleVal = :scaleVal
       WHERE i.id = :id
       """)
  void updateSentimentScores(@Param("id") long id, @Param("posVal") int posVal,
                             @Param("negVal") int negVal, @Param("scaleVal") int scaleVal);
}
