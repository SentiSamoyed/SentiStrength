package web.service;

import web.entity.vo.*;
import web.enums.*;

import java.util.List;

/**
 * @author tanziyue
 * @date 2023/5/23
 * @description 仓库信息获取业务逻辑
 */
public interface RepoStatsService {
  List<ReleaseVO> getAllReleasesOfARepo(String owner, String name);

  List<RepoVO> getAllExistedRepos();

  RepoStatusEnum initRepo(String owner, String name);

  RepoVO getRepo(String owner, String name);

  PageVO<IssueVO> getAPageOfIssuesFromRepo(String owner, String name, int page, DirectionEnum dir, SortByEnum sortBy);

  PageVO<IssueVO> getAPageOfCommentsFromIssue(String owner, String name, int issueNumber, int page, SortByEnum sortBy);

  int calcTotalScoreOfRepo(String owner, String name, List<String> releaseTags, CalcApproachEnum calcApproach);

  List<TendencyDataVO> getTendencyData(String owner, String name, GranularityEnum granularity, CalcApproachEnum calcApproach);
}