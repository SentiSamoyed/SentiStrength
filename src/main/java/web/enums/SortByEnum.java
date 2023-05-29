package web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public enum SortByEnum {
  CREATED_AT("createdAt"),
  ISSUE_NUMBER("issueNumber"),
  SCALE_VALUE("scaleVal");

  @Getter
  @Setter
  @JsonValue
  private String value;

  private static final Map<String, SortByEnum> valueMap;

  static {
    SortByEnum[] values = SortByEnum.values();
    valueMap = new HashMap<>(values.length);
    for (SortByEnum value : values) {
      valueMap.put(value.value, value);
    }
  }

  @JsonCreator
  public static SortByEnum getByValue(String value) {
    return valueMap.get(value);
  }
}
