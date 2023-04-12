package uk.ac.wlv.sentistrength.wordlist.factory;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.wordlist.WordList;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tanziyue
 * @date 2023/4/12
 * @description 单例工厂，负责生产 word list 相关资源.
 * @implNote 该类可能会被多 SentiStrength instance 并行调用，一定要考虑并发场景。
 * // TODO: SentiStrength 并发测试
 */
@Log4j2
public class SingletonWordListFactory implements WordListFactory {

  /**
   * 存放资源实例的条目
   *
   * @param timestamp    源文件的最终修改时间戳
   * @param filename     资源生成的源文件
   * @param options      资源生成基于的选项
   * @param nrExtraLines 资源生成预留的额外行
   * @param instance     资源实例
   */
  private record WordListEntry(long timestamp, String filename, ClassificationOptions options, int nrExtraLines,
                               WordList instance) {
    /**
     * 检查该资源是否已经过时并需要重创建
     */
    boolean isUpToDate(String filename, ClassificationOptions options, int nrExtraLines) {
      if (!Objects.equals(this.filename, filename)
          || !Objects.equals(nrExtraLines, this.nrExtraLines)
          || instance.haveOptionsChanged(this.options, options)
      ) {
        return false;
      }

      long ts = new File(filename).lastModified();
      return ts == this.timestamp;
    }
  }

  private static SingletonWordListFactory instance;

  private static final int STORAGE_CAPACITY = 10;

  private final ConcurrentHashMap<Class<? extends WordList>, WordListEntry> storage;

  private SingletonWordListFactory() {
    this.storage = new ConcurrentHashMap<>(STORAGE_CAPACITY);
  }

  public static SingletonWordListFactory getInstance() {
    if (Objects.isNull(instance)) {
      synchronized (SingletonWordListFactory.class) {
        if (Objects.isNull(instance)) {
          instance = new SingletonWordListFactory();
        }
      }
    }

    return instance;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends WordList> T buildWordList(Class<T> clazz, String filename, ClassificationOptions options, int nrExtraLines)
      throws IllegalArgumentException {
    WordListEntry entry = storage.get(clazz);
    if (Objects.nonNull(entry) && entry.isUpToDate(filename, options, nrExtraLines)) {
      return (T) entry.instance;
    }

    try {
      T nxt = clazz.getConstructor().newInstance();
      long ts = new File(filename).lastModified();
      boolean success = nxt.initialise(filename, options, nrExtraLines);
      if (!success) {
        throw new IllegalArgumentException("Failed at initializing " + clazz);
      }
      storage.put(clazz, new WordListEntry(ts, filename, options, nrExtraLines, nxt));

      return nxt;

    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      log.error(e.getLocalizedMessage());
      return null;
    }
  }
}
