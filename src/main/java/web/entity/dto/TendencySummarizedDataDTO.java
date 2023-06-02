package web.entity.dto;

public interface TendencySummarizedDataDTO {
  /**
   * 里程碑，如：2020 / 2020.Q3 / 2020.03
   */
  String getMilestone();

  /**
   * 截至该里程碑的数值总和
   */
  Integer getSum();

  /**
   * 截至该里程碑的条目总数
   */
  Integer getCount();

  Integer getNegCnt();

  Integer getPosCnt();
}
