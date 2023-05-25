package web.factory.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.entity.vo.AnalysisOptionsVO;
import web.enums.AnalysisModeEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * @author tanziyue
 * @date 2023/4/16
 * @description 带缓存的 SentiStrength 工厂类
 */
@Primary
@Qualifier("CachedSentiStrengthFactory")
@Component
@Log4j2
public class CachedSentiStrengthFactory extends SimpleSentiStrengthFactory {

  /**
   * arg 数组封装成的 key
   *
   * @param args
   */
  private record SentiKey(String[] args) {
    @Override
    public int hashCode() {
      return Arrays.hashCode(args);
    }

    @Override
    public boolean equals(Object obj) {
      if (Objects.isNull(obj)) {
        return false;
      } else if (obj == this) {
        return true;
      } else if (obj.getClass() != this.getClass()) {
        return false;
      }

      return Arrays.equals(this.args, ((SentiKey) obj).args);
    }
  }

  private static final int
      CAPACITY = 10,
      INITIAL = 10;

  private final Cache<SentiKey, SentiStrength> cache;

  public CachedSentiStrengthFactory() {
    super();
    cache = CacheBuilder.newBuilder()
        .maximumSize(CAPACITY)
        .initialCapacity(INITIAL)
        .build();
  }

  @Override
  public SentiStrength build(AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    String[] args = super.getArgs(mode, explain, options);
    SentiKey key = new SentiKey(args);
    try {
      return cache.get(key, () -> super.build(args));
    } catch (ExecutionException e) {
      log.fatal(e.getLocalizedMessage());
      return super.build(args);
    }
  }
}
