package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import web.entity.vo.IssueVO;
import web.entity.vo.PageVO;
import web.entity.vo.RepoVO;
import web.enums.DirectionEnum;
import web.enums.RepoStatusEnum;
import web.enums.SortByEnum;
import web.service.RepoStatsService;
import web.util.Result;

/**
 * @author tanziyue
 * @date 2023/5/23
 * @description 仓库的信息获取
 */
@RestController
@RequestMapping("/repo")
public class RepoStatsController {

  public final RepoStatsService repoStatsService;

  @Autowired
  public RepoStatsController(RepoStatsService repoStatsService) {
    this.repoStatsService = repoStatsService;
  }

  @PostMapping("/{owner}/{name}/init")
  public Result<RepoStatusEnum> initRepo(@PathVariable String owner, @PathVariable String name) {
    return Result.buildSuccess(repoStatsService.initRepo(owner, name));
  }

  @GetMapping("/{owner}/{name}")
  public Result<RepoVO> getRepo(@PathVariable String owner, @PathVariable String name) {
    return Result.buildSuccess(repoStatsService.getRepo(owner, name));
  }

  @GetMapping("/{owner}/{name}/issues")
  public Result<PageVO<IssueVO>> getAPageOfIssuesFromRepo(@PathVariable String owner,
                                                          @PathVariable String name,
                                                          @RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "desc") DirectionEnum dir,
                                                          @RequestParam(defaultValue = "issueNumber") SortByEnum sortBy) {
    return Result.buildSuccess(repoStatsService.getAPageOfIssuesFromRepo(owner, name, page, dir, sortBy));
  }


}
