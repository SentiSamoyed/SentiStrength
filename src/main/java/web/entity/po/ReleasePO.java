package web.entity.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "release")
@Data
@NoArgsConstructor
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
   * 更新时间
   */
  private LocalDateTime updatedAt;
}
