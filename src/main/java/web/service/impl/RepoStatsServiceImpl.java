package web.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import web.entity.vo.IssueVO;
import web.entity.vo.PageVO;
import web.entity.vo.ReleaseVO;
import web.entity.vo.RepoVO;
import web.enums.CalcApproachEnum;
import web.enums.GranularityEnum;
import web.enums.RepoStatusEnum;
import web.enums.SortByEnum;
import web.service.RepoStatsService;

import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class RepoStatsServiceImpl implements RepoStatsService {
  @Override
  public List<ReleaseVO> getAllReleasesOfARepo(String owner, String name) {
    return null;
  }

  @Override
  public List<RepoVO> getAllExistedRepos() {
    return null;
  }

  @Override
  public RepoStatusEnum initRepo(String owner, String name) {
    return null;
  }

  @Override
  public RepoVO getRepo(String owner, String name) {
    return null;
  }

  @Override
  public PageVO<IssueVO> getAPageOfIssuesFromRepo(String owner, String name, int page, SortByEnum sortBy) {
    return null;
  }

  @Override
  public PageVO<IssueVO> getAPageOfCommentsFromIssue(String owner, String name, int issueNumber, int page, SortByEnum sortBy) {
    return null;
  }

  @Override
  public int calcTotalScoreOfRepo(String owner, String name, List<String> releaseTags, CalcApproachEnum calcApproach) {
    return 0;
  }

  @Override
  public Map<String, Integer> getTendencyData(String owner, String name, GranularityEnum granularity, CalcApproachEnum calcApproach) {
    return null;
  }
}
