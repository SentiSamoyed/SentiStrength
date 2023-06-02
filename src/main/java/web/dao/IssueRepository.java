package web.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                 SUM(count) OVER (ROWS UNBOUNDED PRECEDING)   AS count,
                 SUM(posCnt) OVER (ROWS UNBOUNDED PRECEDING) AS posCnt,
                 SUM(negCnt) OVER (ROWS UNBOUNDED PRECEDING) AS negCnt
                 FROM (
                 SELECT
                  YEAR(created_at) AS year,
                  SUM(scale_val) AS sum,
                  COUNT(scale_val)    AS count,
                  SUM(IF(scale_val > 0, 1, 0)) AS posCnt,
                  SUM(IF(scale_val < 0, 1, 0)) AS negCnt
                FROM issue
                WHERE repo_full_name = :fullName
                GROUP BY year
                ORDER BY year) AS t;
      """, nativeQuery = true)
  List<TendencySummarizedDataDTO> getTendencyDataByYear(@Param("fullName") String fullName);

  @Query(value = """
          SELECT CONCAT(t.year, '.', LPAD(t.month, 2, '0'))             AS milestone,
                 SUM(t.sum) OVER (ROWS UNBOUNDED PRECEDING) AS sum,
                 SUM(count) OVER (ROWS UNBOUNDED PRECEDING)   AS count,
                 SUM(posCnt) OVER (ROWS UNBOUNDED PRECEDING) AS posCnt,
                 SUM(negCnt) OVER (ROWS UNBOUNDED PRECEDING) AS negCnt
                 FROM (SELECT YEAR(created_at)  AS year,
                       MONTH(created_at) AS month,
                       SUM(scale_val)    AS sum,
                       COUNT(scale_val)    AS count,
                       SUM(IF(scale_val > 0, 1, 0)) AS posCnt,
                       SUM(IF(scale_val < 0, 1, 0)) AS negCnt
                       FROM issue
                WHERE repo_full_name = :fullName
                GROUP BY year, month
                ORDER BY year, month) AS t;
      """, nativeQuery = true)
  List<TendencySummarizedDataDTO> getTendencyDataByYearMonth(@Param("fullName") String fullName);

  @Query(value = """
          SELECT CONCAT(t.year, 'Q', quarter)                             AS milestone,
                 SUM(t.sum) OVER (ROWS UNBOUNDED PRECEDING) AS sum,
                 SUM(count) OVER (ROWS UNBOUNDED PRECEDING)   AS count,
                 SUM(posCnt) OVER (ROWS UNBOUNDED PRECEDING) AS posCnt,
                 SUM(negCnt) OVER (ROWS UNBOUNDED PRECEDING) AS negCnt
          FROM (SELECT YEAR(created_at)    AS year,
                       QUARTER(created_at) AS quarter,
                       SUM(scale_val)      AS sum,
                       COUNT(scale_val)    AS count,
                       SUM(IF(scale_val > 0, 1, 0)) AS posCnt,
                       SUM(IF(scale_val < 0, 1, 0)) AS negCnt
                FROM issue
                WHERE repo_full_name = :fullName
                GROUP BY year, quarter
                ORDER BY year, quarter) AS t;
      """, nativeQuery = true)
  List<TendencySummarizedDataDTO> getTendencyDataByYearQuarter(@Param("fullName") String fullName);

  @Query(value = """
      SELECT
        :time AS milestone,
        IFNULL(SUM(scale_val)              , 0)  AS sum,
        IFNULL(COUNT(scale_val)            , 0)  AS count,
        IFNULL(SUM(IF(scale_val > 0, 1, 0)), 0) AS posCnt,
        IFNULL(SUM(IF(scale_val < 0, 1, 0)), 0) AS negCnt
      FROM issue
        WHERE created_at <= :time
        AND repo_full_name = :fullName
      ;
      """, nativeQuery = true)
  TendencySummarizedDataDTO getDataAtAPoint(@Param("time") LocalDateTime time, @Param("fullName") String fullName);

  @Query("""
      select count(*)
        from IssuePO po
        where po.repoFullName = :fullName
         and po.scaleVal = :value
         and po.createdAt between :from and :to
      """)
  int getCntOfScaleValue(@Param("fullName") String fullName, @Param("value") Integer value, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

  Page<IssuePO> findAllByRepoFullName(String repoFullName, Pageable pageable);

  Page<IssuePO> findAllByRepoFullNameAndStateIn(String repoFullName, List<String> states, Pageable pageable);
}
