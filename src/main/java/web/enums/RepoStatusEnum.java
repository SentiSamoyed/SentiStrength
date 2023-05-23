package web.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tanziyue
 * @date 2023/5/23
 * @description 当前仓库获取状态
 */
@NoArgsConstructor
public enum RepoStatusEnum {
  UNDONE(0),
  DONE(1),
  NOT_EXISTED(2),
  NO_ISSUE(3);

  RepoStatusEnum(int val) {
    this.val = val;
  }

  @JsonValue
  @Setter
  int val;
}
