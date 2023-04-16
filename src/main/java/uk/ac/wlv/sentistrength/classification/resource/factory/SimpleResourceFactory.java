package uk.ac.wlv.sentistrength.classification.resource.factory;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.resource.Resource;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @author tanziyue
 * @date 2023/4/14
 * @description 最简单、无缓存设计的工厂类
 */
@Log4j2
public class SimpleResourceFactory implements ResourceFactory {

  private static SimpleResourceFactory instance;

  public static SimpleResourceFactory getInstance() {
    if (Objects.isNull(instance)) {
      synchronized (CachedResourceFactory.class) {
        if (Objects.isNull(instance)) {
          instance = new SimpleResourceFactory();
        }
      }
    }

    return instance;
  }

  protected SimpleResourceFactory() {
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Resource> T buildResource(Class<? extends Resource> clazz, String filename, ClassificationOptions options, int nrExtraLines) throws IllegalArgumentException {
    try {
      T resource = (T) clazz.getConstructor().newInstance();
      boolean success = resource.initialise(filename, options, nrExtraLines);
      if (!success) {
        throw new IllegalArgumentException("Failed at initializing " + clazz);
      }

      return resource;

    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      log.error(e.getLocalizedMessage());
      return null;
    }
  }
}
