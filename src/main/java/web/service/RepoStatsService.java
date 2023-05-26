package web.service;

import web.entity.vo.*;
import web.enums.*;

import java.util.List;
import java.util.Map;

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

  TendencyDataVO calcTotalScoreOfRepo(String owner, String name, List<String> releaseTags);

  List<TendencyDataVO> getTendencyData(String owner, String name, GranularityEnum granularity);

  Map<Integer, Integer> getPieChartData(String owner, String name, long from, long to);
}
