package web.entity.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author tanziyue
 * @date 2023/5/20
 * @description Issue comment PO
 */
@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentPO {
  @Id
  private Long id;
  /**
   * 对应 Repo 的全名
   */
  private String repoFullName;
  /**
   * Issue Number
   */
  private Long issueNumber;
  /**
   * Issue 对应的 HTML URL
   */
  private String htmlUrl;

  /**
   * 作者用户名
   */
  private String author;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;
  /**
   * 更新时间
   */
  private LocalDateTime updatedAt;

  /**
   * 内容
   */
  private String body;
}
