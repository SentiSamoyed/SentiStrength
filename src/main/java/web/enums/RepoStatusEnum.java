package web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

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

  RepoStatusEnum(int value) {
    this.value = value;
  }

  @JsonValue
  @Setter
  int value;

  private static final Map<Integer, RepoStatusEnum> valueMap;

  static {
    RepoStatusEnum[] values = RepoStatusEnum.values();
    valueMap = new HashMap<>(values.length);
    for (RepoStatusEnum value : values) {
      valueMap.put(value.value, value);
    }
  }

  @JsonCreator
  public static RepoStatusEnum getByValue(Integer value) {
    return valueMap.get(value);
  }

}
