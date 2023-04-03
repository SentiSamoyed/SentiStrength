package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.data.vo.FileRequestVO;
import web.data.vo.TextAnalysisVO;
import web.data.vo.TextRequestVO;
import web.service.AnalysisService;
import web.util.Result;

import java.util.List;

@RestController
@RequestMapping("/sentiment/analysis")
public class AnalysisController {

  private AnalysisService analysisService;

  @Autowired
  public AnalysisController(AnalysisService analysisService) {
    this.analysisService = analysisService;
  }

  @PostMapping("text")
  public Result<List<TextAnalysisVO>> fileAnalysis(@RequestBody TextRequestVO r) {
    List<TextAnalysisVO> data = analysisService.textAnalysis(r.getText(), r.getMode(), r.getExplain(), r.getOptions());
    return Result.buildSuccess(data);
  }
}
