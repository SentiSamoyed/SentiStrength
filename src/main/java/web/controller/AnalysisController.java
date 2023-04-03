package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import web.data.vo.FileAnalysisVO;
import web.data.vo.FileRequestVO;
import web.data.vo.TextAnalysisVO;
import web.data.vo.TextRequestVO;
import web.service.AnalysisService;
import web.util.Result;

@Controller
@RequestMapping("/sentiment/analysis")
public class AnalysisController {

  private AnalysisService analysisService;

  @Autowired
  public AnalysisController(AnalysisService analysisService) {
    this.analysisService = analysisService;
  }

  @PostMapping("text")
  public Result<TextAnalysisVO> textAnalysis(@RequestBody TextRequestVO r) {
    TextAnalysisVO data = analysisService.textAnalysis(r.getText(), r.getMode(), r.getExplain(), r.getOptions());
    return Result.buildSuccess(data);
  }

  @PostMapping("file")
  public Result<FileAnalysisVO> fileAnalysis(@RequestBody FileRequestVO r) {
    FileAnalysisVO data = analysisService.fileAnalysis(r.getFile(), r.getMode(), r.getExplain(), r.getOptions());
    return Result.buildSuccess(data);
  }
}
