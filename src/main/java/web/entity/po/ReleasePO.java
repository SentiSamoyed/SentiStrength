package web.entity.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "release")
@Data
@AllArgsConstructor
@Builder
public class ReleasePO {
  @Id
  private Long id;
  /**
   * 对应 Repo 的全名
   */
  private String repoFullName;
  /**
   * Tag 名称
   */
  private String tagName;
  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  /**
   * 到这个版本创建为止的分数总和
   */
  private Integer sumHitherto;
  /**
   * 到这个版本创建为止的条目总数
   */
  private Integer countHitherto;
  /**
   * 到这个版本创建为止的正向分值条目数
   */
  @Column(name = "pos_cnt_hitherto")
  private Integer posCntHitherto;
  /**
   * 到这个版本创建为止的负向分值条目数
   */
  @Column(name = "neg_cnt_hitherto")
  private Integer negCntHitherto;

  public ReleasePO() {
    sumHitherto = 0;
    countHitherto = 0;
    posCntHitherto = 0;
    negCntHitherto = 0;
  }
}
