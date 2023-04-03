package web.data.vo;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tanziyue
 * @date 2023/4/3
 * @description 单行文本解析的参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextRequestVO {
  private String text;
  private String mode;
  private Boolean explain;
  @Valid
  private AnalysisOptionsVO options;
}
