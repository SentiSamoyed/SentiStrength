package web.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TendencySummarizedDataDTOImpl implements TendencySummarizedDataDTO {
  private String milestone;
  private Integer sum;
  private Integer count;
}
