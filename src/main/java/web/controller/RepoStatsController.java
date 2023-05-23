package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.enums.RepoStatusEnum;
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

}
