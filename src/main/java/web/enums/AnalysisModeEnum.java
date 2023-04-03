package web.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tanziyue
 * @date 2023/4/3
 * @description 分析模式的枚举类
 */
@AllArgsConstructor
public enum AnalysisModeEnum {
  DEFAULT("default"), BINARY("binary"), TRINARY("trinary"), SCALE("scale");

  @Getter
  @JsonValue
  private final String val;
}
