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
public enum DirectionEnum {
  ASC_D("asc"), DESC_D("desc");

  @Setter
  @Getter
  @JsonValue
  private String value;

  private static final Map<String, DirectionEnum> valueMap;

  static {
    DirectionEnum[] values = DirectionEnum.values();
    valueMap = new HashMap<>(values.length);
    for (DirectionEnum value : values) {
      valueMap.put(value.value, value);
    }
  }

  @JsonCreator
  public static DirectionEnum getByValue(String value) {
    return valueMap.get(value);
  }
}
