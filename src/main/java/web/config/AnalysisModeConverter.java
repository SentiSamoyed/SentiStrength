package web.config;

import org.springframework.core.convert.converter.Converter;
import web.enums.AnalysisModeEnum;

public class AnalysisModeConverter implements Converter<String, AnalysisModeEnum> {
  @Override
  public AnalysisModeEnum convert(String source) {
    return switch (source) {
      case "default" -> AnalysisModeEnum.DEFAULT;
      case "binary" -> AnalysisModeEnum.BINARY;
      case "trinary" -> AnalysisModeEnum.TRINARY;
      case "scale" -> AnalysisModeEnum.SCALE;
      default -> throw new IllegalArgumentException();
    };
  }
}
