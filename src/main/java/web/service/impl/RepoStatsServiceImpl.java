package web.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.config.ConfigValue;
import web.dao.IssueRepository;
import web.dao.RepoRepository;
import web.entity.vo.AnalysisOptionsVO;
import web.entity.po.IssuePO;
import web.entity.po.RepoPO;
import web.entity.vo.IssueVO;
import web.entity.vo.PageVO;
import web.entity.vo.ReleaseVO;
import web.entity.vo.RepoVO;
import web.enums.*;
import web.factory.SentiStrengthFactory;
import web.service.RepoStatsService;
import web.util.Converters;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
public class RepoStatsServiceImpl implements RepoStatsService {

  private final RepoRepository repoRepository;

  private final IssueRepository issueRepository;

  private final SentiStrength sentiStrength;

  @Autowired
  public RepoStatsServiceImpl(RepoRepository repoRepository, IssueRepository issueRepository, SentiStrengthFactory sentiStrengthFactory) {
    this.repoRepository = repoRepository;
    this.issueRepository = issueRepository;

    this.sentiStrength = sentiStrengthFactory.build(AnalysisModeEnum.SCALE, true, new AnalysisOptionsVO());
  }

  @Override
  public List<ReleaseVO> getAllReleasesOfARepo(String owner, String name) {
    return null;
  }

  @Override
  public List<RepoVO> getAllExistedRepos() {
    return null;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RepoStatusEnum initRepo(String owner, String name) {
    String fullName = getFullName(owner, name);
    RepoPO repoPO = repoRepository.findByFullName(fullName);
    if (Objects.isNull(repoPO)) {
      log.error("Unimplemented yet!");
      throw new IllegalArgumentException();
    }

    if (Objects.nonNull(repoPO.getLastAnalysisTs())) {
      var lastTime = Instant.ofEpochMilli(repoPO.getLastAnalysisTs()).atZone(ZoneId.systemDefault());
      log.info("[%s] Analysis has been performed on %s".formatted(fullName, lastTime.toString()));
      return RepoStatusEnum.DONE;
    }

    int cur = 0, total = Integer.MAX_VALUE;
    for (; cur < total; ++cur) {
      Page<IssuePO> page = issueRepository.findAll(
          PageRequest.of(cur, ConfigValue.INNER_PAGE_SZ)
      );
      total = page.getTotalPages();
      long delta = page.stream()
          .filter(i -> Objects.isNull(i.getScaleVal()))
          .peek(i -> {
            var fullText = String.join("\n", i.getTitle(), i.getBody());
            var result = sentiStrength.computeSentimentScoresSeparate(fullText);
            issueRepository.updateSentimentScores(i.getId(), result.pos(), result.neg(), result.scale());
          })
          .count();
      log.info("[%s] Analyzed %d entries".formatted(fullName, delta));
    }

    long ts = Instant.now().toEpochMilli();
    repoRepository.updateLastAnalysisTime(repoPO.getId(), ts);

    log.info("[%s] Analysis updated".formatted(fullName));

    return RepoStatusEnum.DONE;
  }

  @Override
  public RepoVO getRepo(String owner, String name) {
    var po = repoRepository.findByFullName(getFullName(owner, name));
    return Converters.convertRepo(po);
  }

  @Override
  public PageVO<IssueVO> getAPageOfIssuesFromRepo(String owner, String name, int page, DirectionEnum dir, SortByEnum sortBy) {
    if (Objects.isNull(sortBy)) {
      sortBy = SortByEnum.ISSUE_NUMBER;
    }
    if (Objects.isNull(dir)) {
      dir = DirectionEnum.DESC_D;
    }
    var resultPage = issueRepository.findAll(PageRequest.of(page, ConfigValue.OUTER_PAGE_SZ, Sort.by(
        dir == DirectionEnum.ASC_D ?
            Sort.Order.asc(sortBy.getValue())
            : Sort.Order.desc(sortBy.getValue())
    )));

    return PageVO.<IssueVO>builder()
        .pageNo(page)
        .pageCount(resultPage.getTotalPages())
        .pageSize(ConfigValue.OUTER_PAGE_SZ)
        .contentList(resultPage.map(Converters::convertIssue).toList())
        .build();
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

  private String getFullName(String owner, String name) {
    return "%s/%s".formatted(owner, name);
  }
}
