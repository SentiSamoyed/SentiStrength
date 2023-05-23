package web.entity.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "issue")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueVO {
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
   * 标题
   */
  private String title;
  /**
   * Issue 状态
   * TODO：转换为枚举值
   */
  private String state;
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
  /**
   * 评论数量
   */
  private String comments;
}
