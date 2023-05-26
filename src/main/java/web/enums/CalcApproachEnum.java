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
public enum CalcApproachEnum {
  SUM("sum"),
  AVG("avg"),
  POS_RATIO("pos"),
  NEG_RATIO("neg"),
  ;

  @JsonValue
  @Getter
  private String value;

  private static final Map<String, CalcApproachEnum> valueMap;

  static {
    CalcApproachEnum[] values = CalcApproachEnum.values();
    valueMap = new HashMap<>(values.length);
    for (CalcApproachEnum value : values) {
      valueMap.put(value.value, value);
    }
  }

  @JsonCreator
  public static CalcApproachEnum getByValue(String value) {
    return valueMap.get(value);
  }

}
