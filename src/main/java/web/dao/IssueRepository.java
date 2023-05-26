package web.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import web.entity.dto.TendencySummarizedDataDTO;
import web.entity.po.IssuePO;

import java.time.LocalDateTime;
import java.util.List;

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


  @Query(value = """
          SELECT t.year                                                     AS milestone,
                 SUM(t.sum) OVER (ORDER BY year ROWS UNBOUNDED PRECEDING) AS sum,
                 SUM(count) OVER (ORDER BY year ROWS UNBOUNDED PRECEDING)   AS count
          FROM (SELECT YEAR(created_at) AS year, SUM(scale_val) AS sum, COUNT(scale_val) AS count
                FROM issue
                WHERE repo_full_name = :fullName
                GROUP BY year
                ORDER BY year) AS t;
      """, nativeQuery = true)
  List<TendencySummarizedDataDTO> getTendencyDataByYear(@Param("fullName") String fullName);

  @Query(value = """
          SELECT CONCAT(t.year, '.', LPAD(t.month, 2, '0'))             AS milestone,
                 SUM(t.sum) OVER (ROWS UNBOUNDED PRECEDING) AS sum,
                 SUM(count) OVER (ROWS UNBOUNDED PRECEDING)   AS count
          FROM (SELECT YEAR(created_at)  AS year,
                       MONTH(created_at) AS month,
                       SUM(scale_val)    AS sum,
                       COUNT(scale_val)  AS count
                FROM issue
                WHERE repo_full_name = :fullName
                GROUP BY year, month
                ORDER BY year, month) AS t;
      """, nativeQuery = true)
  List<TendencySummarizedDataDTO> getTendencyDataByYearMonth(@Param("fullName") String fullName);

  @Query(value = """
          SELECT CONCAT(t.year, 'Q', quarter)                             AS milestone,
                 SUM(t.sum) OVER (ROWS UNBOUNDED PRECEDING) AS sum,
                 SUM(count) OVER (ROWS UNBOUNDED PRECEDING)   AS count
          FROM (SELECT YEAR(created_at)    AS year,
                       QUARTER(created_at) AS quarter,
                       SUM(scale_val)      AS sum,
                       COUNT(scale_val)    AS count
                FROM issue
                WHERE repo_full_name = :fullName
                GROUP BY year, quarter
                ORDER BY year, quarter) AS t;
      """, nativeQuery = true)
  List<TendencySummarizedDataDTO> getTendencyDataByYearQuarter(@Param("fullName") String fullName);

  @Query(value = """
      SELECT :time AS milestone, SUM(scale_val) AS sum, COUNT(scale_val) AS count
      FROM issue
      WHERE created_at <= :time ;
      """, nativeQuery = true)
  TendencySummarizedDataDTO getDataAtAPoint(@Param("time") LocalDateTime time);
}
