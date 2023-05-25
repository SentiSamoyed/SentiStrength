package web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tanziyue
 * @date 2023/4/3
 * @description 文件分析结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAnalysisVO {
  private String result;
}
