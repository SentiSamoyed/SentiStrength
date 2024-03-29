package web.service;

import web.entity.vo.AnalysisOptionsVO;
import web.entity.vo.TextAnalysisVO;
import web.enums.AnalysisModeEnum;

import java.util.List;

/**
 * @author tanziyue
 * @date 2023/4/3
 * @description 分析业务逻辑接口
 */
public interface AnalysisService {

  /**
   * 对某一个文本文件的内容进行情感分析。需要指定输出的模式和是否需要解释。分析的选项是可选的。
   *
   * @param text    文本文件内容
   * @param mode    模式
   * @param explain 是否需要解释
   * @param options 分析选项
   * @return 分析结果
   */
  List<TextAnalysisVO> textAnalysis(String text, AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options);
}
