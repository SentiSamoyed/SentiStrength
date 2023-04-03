package web.service.impl;

import org.springframework.stereotype.Service;
import web.data.vo.AnalysisOptionsVO;
import web.data.vo.FileAnalysisVO;
import web.data.vo.TextAnalysisVO;
import web.service.AnalysisService;

@Service
public class AnalysisServiceImpl implements AnalysisService {
  @Override
  public TextAnalysisVO textAnalysis(String text, String mode, Boolean explain, AnalysisOptionsVO options) {
    return null;
  }

  @Override
  public FileAnalysisVO fileAnalysis(String file, String mode, Boolean explain, AnalysisOptionsVO options) {
    return null;
  }
}
