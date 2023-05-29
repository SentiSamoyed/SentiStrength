package web.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Log4j2
public class RepoStatsServiceImpl implements RepoStatsService {
  public static final String TRACKER_URL = "http://124.223.97.89:8192/repo/";
  public static final String NO_RELEASE = "No release";
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
    return releaseRepository.findByRepoFullNameOrderByCreatedAt(getFullName(owner, name))
        .stream()
        .map(Converters::convertRelease)
        .toList();
  }

  @Override
  public List<RepoVO> getAllExistedRepos() {
    List<RepoVO> repoVOS = new LinkedList<>();
    for (var po : repoRepository.findAll()) {
      repoVOS.add(Converters.convertRepo(po));
    }

    return repoVOS;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RepoStatusEnum initRepo(String owner, String name) {
    String fullName = getFullName(owner, name);
    RepoPO repoPO = repoRepository.findByFullName(fullName);
    if (Objects.isNull(repoPO)) {
      var status = requestForRepo(fullName);
      if (status != RepoStatusEnum.DONE) {
        return status;
      }
    }

    analyzeIssues(fullName, repoPO);
    analyzeReleases(fullName);

    return RepoStatusEnum.DONE;
  }

  private void analyzeReleases(String fullName) {
    List<ReleasePO> releases = releaseRepository.findByRepoFullNameOrderByCreatedAt(fullName);
    long delta = releases.stream()
        .filter(r -> Objects.isNull(r.getSumHitherto()) || Objects.isNull(r.getPosCntHitherto()))
        .peek(r -> {
          var dto = issueRepository.getDataAtAPoint(r.getCreatedAt(), fullName);
          releaseRepository.updateAnalysisData(r.getId(), dto.getSum(), dto.getCount(), dto.getPosCnt(), dto.getNegCnt());
        })
        .count();
    log.info("[{}] Analyzed {} releases", fullName, delta);
  }

  private RepoStatusEnum requestForRepo(String fullName) {
    var rest = new RestTemplate();
    try {
      var response = rest.postForEntity(TRACKER_URL + fullName, HttpEntity.EMPTY, String.class);
      if (response.getStatusCode() != HttpStatus.OK || Objects.isNull(response.getBody())) {
        throw new RuntimeException(response.toString());
      }

      var status = RepoStatusEnum.getByValue(Integer.parseInt(response.getBody()));
      if (Objects.isNull(status)) {
        throw new RuntimeException("Unexpected status: " + response.getBody());
      }

      return status;

    } catch (Exception e) {
      log.info("[{}] Request failed: {}", fullName, e.getLocalizedMessage());
      return RepoStatusEnum.UNDONE;
    }
  }

  private void analyzeIssues(String fullName, RepoPO repoPO) {
    if (Objects.nonNull(repoPO.getLastAnalysisTs())) {
      var lastTime = Instant.ofEpochMilli(repoPO.getLastAnalysisTs()).atZone(ZoneId.systemDefault());
      log.info("[{}] Analysis has been performed on {}", fullName, lastTime.toString());
      return;
    }

    int cur = 0, total = Integer.MAX_VALUE;
    for (; cur < total; ++cur) {
      Page<IssuePO> page = issueRepository.findAllByRepoFullName(
          fullName,
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
  public PageVO<IssueVO> getAPageOfIssuesFromRepo(String owner, String name, int page, DirectionEnum dir, SortByEnum sortBy, List<String> states) {
    if (Objects.isNull(sortBy)) {
      sortBy = SortByEnum.ISSUE_NUMBER;
    }
    if (Objects.isNull(dir)) {
      dir = DirectionEnum.DESC_D;
    }
    var resultPage = issueRepository.findAllByRepoFullNameAndStateIn(
        getFullName(owner, name),
        states,
        PageRequest.of(page, ConfigValue.OUTER_PAGE_SZ, Sort.by(
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
  public TendencyDataVO calcTotalScoreOfRepo(String owner, String name, List<String> releaseTags) {
    var fullName = getFullName(owner, name);

    var tendencyMap = getTendencyData(owner, name, GranularityEnum.RELEASE)
        .stream()
        .collect(Collectors.toMap(TendencyDataVO::getMilestone, Function.identity()));
    var releaseMap = releaseRepository.findByRepoFullNameOrderByCreatedAt(fullName)
        .stream()
        .collect(Collectors.toMap(ReleasePO::getTagName, Function.identity()));
    releaseMap.put(NO_RELEASE, new ReleasePO());

    int sum = 0, count = 0, posCnt = 0, negCnt = 0;
    for (String tag : releaseTags) {
      var data = tendencyMap.get(tag);
      var release = releaseMap.get(tag);
      if (Objects.isNull(data) || Objects.isNull(release)) {
        continue;
      }

      sum += data.getSum() - release.getSumHitherto();
      count += data.getCount() - release.getCountHitherto();
      posCnt += data.getPosCnt() - release.getPosCntHitherto();
      negCnt += data.getNegCnt() - release.getNegCntHitherto();
    }

    return TendencyDataVO.builder()
        .sum(sum)
        .count(count)
        .avg((double) sum / count)
        .posCnt(posCnt)
        .posRatio((double) posCnt / count)
        .negCnt(negCnt)
        .negRatio((double) negCnt / count)
        .build();
  }

  @Override
  public List<TendencyDataVO> getTendencyData(String owner, String name, GranularityEnum granularity) {
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
            /* 填充第一项 */
            out.add(
                TendencySummarizedDataDTOImpl.builder()
                    .milestone(NO_RELEASE)
                    .count(next.getCountHitherto())
                    .sum(next.getSumHitherto())
                    .posCnt(next.getPosCntHitherto())
                    .negCnt(next.getNegCntHitherto())
                    .build()
            );
            /* 填充每一次 Release */
            while (it.hasNext()) {
              var cur = next;
              next = it.next();
              out.add(
                  TendencySummarizedDataDTOImpl.builder()
                      .milestone(cur.getTagName())
                      .count(next.getCountHitherto())
                      .sum(next.getSumHitherto())
                      .posCnt(next.getPosCntHitherto())
                      .negCnt(next.getNegCntHitherto())
                      .build()
              );
            }
            /* 填充最后一项 */
            var current = issueRepository.getDataAtAPoint(LocalDateTime.now(), fullName);
            out.add(
                TendencySummarizedDataDTOImpl.builder()
                    .milestone(next.getTagName())
                    .count(current.getCount())
                    .sum(current.getSum())
                    .posCnt(current.getPosCnt())
                    .negCnt(current.getNegCnt())
                    .build()
            );
            yield out;
          }
        };

    return data.stream()
        .map(RepoStatsServiceImpl::convert)
        .collect(Collectors.toList());
  }

  @Override
  public Map<Integer, Integer> getPieChartData(String owner, String name, long from, long to) {
    final Integer[] valueRange = new Integer[]{-5, 5};
    var fullName = getFullName(owner, name);
    var fromDt = Instant.ofEpochMilli(from).atZone(ZoneId.systemDefault()).toLocalDateTime();
    var toDt = Instant.ofEpochMilli(to).atZone(ZoneId.systemDefault()).toLocalDateTime();
    return IntStream.rangeClosed(valueRange[0], valueRange[1])
        .boxed()
        .collect(Collectors.toMap(
            Function.identity(),
            i -> issueRepository.getCntOfScaleValue(fullName, i, fromDt, toDt)
        ));
  }

  private static TendencyDataVO convert(TendencySummarizedDataDTO d) {
    return TendencyDataVO.builder()
        .milestone(d.getMilestone())
        .sum(d.getSum())
        .count(d.getCount())
        .avg((double) d.getSum() / d.getCount())
        .posCnt(d.getPosCnt())
        .posRatio((double) d.getPosCnt() / d.getCount())
        .negCnt(d.getNegCnt())
        .negRatio((double) d.getNegCnt() / d.getCount())
        .build();
  }

  private String getFullName(String owner, String name) {
    return "%s/%s".formatted(owner, name);
  }
}
