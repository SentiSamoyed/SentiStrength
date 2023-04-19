package uk.ac.wlv.sentistrength.classification.resource.factory;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.resource.Resource;

/**
 * @author tanziyue
 * @date 2023/4/12
 * @description 单例资源工厂抽象类
 */
public interface ResourceFactory {
  /**
   * 构建资源
   *
   * @param clazz        资源类
   * @param filename     来源文件名
   * @param options      分类选项
   * @param nrExtraLines 需要保留的额外行数
   * @return 资源
   */
  <T extends Resource> T buildResource(
      Class<? extends Resource> clazz, String filename,
      ClassificationOptions options, int nrExtraLines) throws IllegalArgumentException;
}
