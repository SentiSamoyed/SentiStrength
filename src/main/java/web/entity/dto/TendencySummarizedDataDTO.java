package web.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TendencySummarizedDataDTO {
  /**
   * 里程碑，如：2020 / 2020.Q3 / 2020.03
   */
  private String milestone;
  /**
   * 截至该里程碑的数值总和
   */
  private Integer sum;
  /**
   * 截至该里程碑的条目总数
   */
  private Integer count;
}
