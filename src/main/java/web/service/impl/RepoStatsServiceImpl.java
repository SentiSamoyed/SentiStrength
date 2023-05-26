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
import web.dao.ReleaseRepository;
import web.dao.RepoRepository;
import web.entity.dto.TendencySummarizedDataDTO;
import web.entity.dto.TendencySummarizedDataDTOImpl;
import web.entity.po.IssuePO;
import web.entity.po.ReleasePO;
import web.entity.po.RepoPO;
import web.entity.vo.*;
import web.enums.*;
import web.factory.SentiStrengthFactory;
import web.service.RepoStatsService;
import web.util.Converters;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RepoStatsServiceImpl implements RepoStatsService {

  private final RepoRepository repoRepository;

  private final IssueRepository issueRepository;

  private final ReleaseRepository releaseRepository;

  private final SentiStrength sentiStrength;

  @Autowired
  public RepoStatsServiceImpl(RepoRepository repoRepository, IssueRepository issueRepository, ReleaseRepository releaseRepository, SentiStrengthFactory sentiStrengthFactory) {
    this.repoRepository = repoRepository;
    this.issueRepository = issueRepository;
    this.releaseRepository = releaseRepository;

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

    analyzeIssues(fullName, repoPO);

    analyzeReleases(fullName);

    return RepoStatusEnum.DONE;
  }

  private void analyzeReleases(String fullName) {
    List<ReleasePO> releases = releaseRepository.findByRepoFullNameOrderByCreatedAt(fullName);
    long delta = releases.stream()
        .filter(r -> Objects.isNull(r.getSumHitherto()))
        .peek(r -> {
          var dto = issueRepository.getDataAtAPoint(r.getCreatedAt());
          releaseRepository.updateAnalysisData(dto.getSum(), dto.getCount(), r.getId());
        })
        .count();
    log.info("[{}] Analyzed {} releases", fullName, delta);
  }

  private void analyzeIssues(String fullName, RepoPO repoPO) {
    if (Objects.nonNull(repoPO.getLastAnalysisTs())) {
      var lastTime = Instant.ofEpochMilli(repoPO.getLastAnalysisTs()).atZone(ZoneId.systemDefault());
      log.info("[{}] Analysis has been performed on {}", fullName, lastTime.toString());
      return;
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
  }

  @Override
  public RepoVO getRepo(String owner, String name) {
    var po = repoRepository.findByFullName(getFullName(owner, name));
    return Converters.convertRepo(po);
  }

  @Override
  public PageVO<IssueVO> getAPageOfIssuesFromRepo(String owner, String name, int page, DirectionEnum dir, SortByEnum
      sortBy) {
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
  public PageVO<IssueVO> getAPageOfCommentsFromIssue(String owner, String name, int issueNumber, int page, SortByEnum
      sortBy) {
    return null;
  }

  @Override
  public int calcTotalScoreOfRepo(String owner, String name, List<String> releaseTags, CalcApproachEnum
      calcApproach) {
    return 0;
  }

  @Override
  public List<TendencyDataVO> getTendencyData(String owner, String name, GranularityEnum granularity, CalcApproachEnum calcApproach) {
    final var fullName = getFullName(owner, name);
    if (!repoRepository.existsByFullName(fullName)) {
      return null;
    }

    List<TendencySummarizedDataDTO> data =
        switch (granularity) {
          case YEAR -> issueRepository.getTendencyDataByYear(fullName);
          case QUARTER -> issueRepository.getTendencyDataByYearQuarter(fullName);
          case MONTH -> issueRepository.getTendencyDataByYearMonth(fullName);
          case RELEASE -> {
            var releases = releaseRepository.findByRepoFullNameOrderByCreatedAt(fullName);
            List<TendencySummarizedDataDTO> out = new ArrayList<>(releases.size() + 1);
            var it = releases.iterator();
            var next = it.next();
            out.add(new TendencySummarizedDataDTOImpl("No release", next.getSumHitherto(), next.getCountHitherto()));
            while (it.hasNext()) {
              var cur = next;
              next = it.next();
              out.add(new TendencySummarizedDataDTOImpl(cur.getTagName(), next.getSumHitherto(), next.getCountHitherto()));
            }
            var current = issueRepository.getDataAtAPoint(LocalDateTime.now());
            out.add(new TendencySummarizedDataDTOImpl(next.getTagName(), current.getSum(), current.getCount()));
            yield out;
          }
        };

    return data.stream()
        .map(d -> TendencyDataVO.builder()
            .milestone(d.getMilestone())
            .value(switch (calcApproach) {
              case AVG -> (double) d.getSum() / d.getCount();
              case SUM -> (double) d.getSum();
            })
            .build()
        )
        .collect(Collectors.toList());
  }

  private String getFullName(String owner, String name) {
    return "%s/%s".formatted(owner, name);
  }
}
