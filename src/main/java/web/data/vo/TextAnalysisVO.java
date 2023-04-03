package web.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tanziyue
 * @date 2023/4/3
 * @description 文本分析结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextAnalysisVO {
  private int val1;
  private int val2;
  private int val3;
  private String explain;
}
