package web.entity.po;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tanziyue
 * @date 2023/5/20
 * @description Repository PO
 */
@Entity
@Table(name = "repo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepoPO {
  @Id
  private Long id;
  /**
   * Repo 所有者，可以是用户名或组织名
   */
  private String owner;
  /**
   * Repo 名
   */
  private String name;
  /**
   * Repo 全名，一般为 {owner}/{name} 的形式
   */
  private String fullName;
  /**
   * Repo 对应的 HTML URL
   */
  private String htmlUrl;
}
