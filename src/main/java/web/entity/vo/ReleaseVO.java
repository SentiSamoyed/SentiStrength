package web.entity.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseVO {
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
}
