package web.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import web.data.vo.AnalysisOptionsVO;
import web.data.vo.FileAnalysisVO;
import web.data.vo.TextAnalysisVO;
import web.util.Result;

@Controller
@RequestMapping("/sentiment/analysis")
public class AnalysisController {

  @PostMapping("text")
  public Result<TextAnalysisVO> textAnalysis(
      @RequestBody String text,
      @RequestBody String mode,
      @RequestBody Boolean explain,
      @RequestBody @Valid AnalysisOptionsVO options
  ) {
    return Result.buildSuccess(null);
  }

  @PostMapping("file")
  public Result<FileAnalysisVO> fileAnalysis(
      @RequestBody String file,
      @RequestBody String mode,
      @RequestBody Boolean explain,
      @RequestBody @Valid AnalysisOptionsVO options
  ) {
    return Result.buildSuccess(null);
  }
}
