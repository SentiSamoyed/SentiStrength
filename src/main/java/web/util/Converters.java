package web.util;

import web.entity.po.IssuePO;
import web.entity.po.RepoPO;
import web.entity.vo.IssueVO;
import web.entity.vo.RepoVO;

import java.time.ZoneId;
import java.util.Objects;

public class Converters {


  public static RepoVO convertRepo(RepoPO po) {
    if (Objects.isNull(po)) {
      return null;
    }

    return RepoVO.builder()
        .id(po.getId())
        .owner(po.getOwner())
        .name(po.getOwner())
        .fullName(po.getFullName())
        .htmlUrl(po.getHtmlUrl())
        .lastUpdate(po.getLastAnalysisTs())
        .build();
  }

  public static IssueVO convertIssue(IssuePO po) {
    if (Objects.isNull(po)) {
      return null;
    }

    return IssueVO.builder()
        .id(po.getId())
        .repoFullName(po.getRepoFullName())
        .issueNumber(po.getIssueNumber())
        .title(po.getTitle())
        .state(po.getState())
        .htmlUrl(po.getHtmlUrl())
        .author(po.getAuthor())
        .createdAt(po.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        .updatedAt(po.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        .body(po.getBody())
        .comments(po.getComments())
        .posVal(po.getPosVal())
        .negVal(po.getNegVal())
        .scaleVal(po.getScaleVal())
        .build();
  }
}
