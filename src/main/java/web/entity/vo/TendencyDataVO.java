package web.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TendencyDataVO {
  private String milestone;

  private Integer sum;
  private Integer count;

  private Double avg;

  private Integer posCnt;
  private Double posRatio;

  private Integer negCnt;
  private Double negRatio;
}
