package uk.ac.wlv.sentistrength.wordlist.factory;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.wordlist.WordList;

/**
 * @author tanziyue
 * @date 2023/4/12
 * @description 资源工厂类
 */
public interface WordListFactory {
  /**
   * 构建资源
   *
   * @param clazz        资源类
   * @param filename     来源文件名
   * @param options
   * @param nrExtraLines 需要保留的额外行数
   * @return 资源
   */
  <T extends WordList> T buildWordList(Class<? extends WordList> clazz, String filename, ClassificationOptions options, int nrExtraLines)
      throws IllegalArgumentException;
}
