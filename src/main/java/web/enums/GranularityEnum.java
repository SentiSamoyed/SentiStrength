package web.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public enum GranularityEnum {
  YEAR("year"), QUARTER("quarter"), MONTH("month"), RELEASE("release");

  @JsonValue
  @Getter
  private String value;

  private static final Map<String, GranularityEnum> valueMap;

  static {
    GranularityEnum[] values = GranularityEnum.values();
    valueMap = new HashMap<>(values.length);
    for (GranularityEnum value : values) {
      valueMap.put(value.value, value);
    }
  }

  @JsonCreator
  public static GranularityEnum getByValue(String value) {
    return valueMap.get(value);
  }

}
