package uk.ac.wlv.sentistrength.classification.resource;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author tanziyue
 * @date 2023/4/12
 * @description 资源的抽象类
 */
@Log4j2
public abstract class Resource {

  /**
   * 包含词和 Strength 数值的复合结构题
   */
  protected record WordAndStrength(String word, int strength) {

  }

  /**
   * 初始化该资源
   *
   * @param filename                        来源文件名
   * @param options                         分类选项
   * @param extraBlankArrayEntriesToInclude 额外空行
   * @return 是否初始化成功
   */
  public boolean initialise(String filename, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    int nrLines;
    try {
      if (Objects.isNull(filename) || filename.isBlank()) {
        throw new IllegalArgumentException("Invalid filename: " + filename);
      } else if (!(new File(filename)).exists()) {
        throw new IllegalArgumentException("Could not find file: " + filename);
      } else if ((nrLines = FileOps.i_CountLinesInTextFile(filename)) < 2) {
        throw new IllegalArgumentException("Less than 2 lines in file: " + filename);
      }
    } catch (IllegalArgumentException e) {
      log.fatal(e.getMessage());
      return false;
    }

    try (Stream<String> lines = FileOps.getFileStream(filename, options.bgForceUTF8)) {
      return initialise(lines, nrLines, options, extraBlankArrayEntriesToInclude);

    } catch (FileNotFoundException e) {
      log.fatal("Could not find file: " + filename);
      return false;
    } catch (IOException e) {
      log.fatal(e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * 简化版的初始化。
   *
   * @see Resource#initialise(String, ClassificationOptions, int)
   */
  public boolean initialise(String sSourceFile, ClassificationOptions options) {
    return this.initialise(sSourceFile, options, 0);
  }

  /**
   * 拆分获取改行的两列
   */
  protected String[] parseColumnsToStrings(String line) {
    int iFirstTabLocation = line.indexOf("\t");
    if (iFirstTabLocation < 0) {
      return null;
    }

    int iSecondTabLocation = line.indexOf("\t", iFirstTabLocation + 1);
    String second;
    if (iSecondTabLocation > 0) {
      second = line.substring(iFirstTabLocation + 1, iSecondTabLocation).trim();
    } else {
      second = line.substring(iFirstTabLocation + 1).trim();
    }

    line = line.substring(0, iFirstTabLocation);
    if (line.contains(" ")) {
      line = line.trim();
    }

    return new String[]{line, second};
  }

  /**
   * 由 subclass 检查它们用到的 Options 中的选项自上一次初始化后是否有变更.
   * 对于不使用选项的 subclass 而言应该返回 false.
   * 如果有修改初始化方法，应即时更新此类.
   *
   * @param old 上一次初始化时备份的选项
   * @param now 当前的新选项
   * @return 是否有变化
   */
  public abstract boolean haveOptionsChanged(ClassificationOptions old, ClassificationOptions now);

  /**
   * 将改行的两列解析成 词 + strength 数值
   */
  protected WordAndStrength parseColumns(String line) {
    String[] ss = parseColumnsToStrings(line);
    line = ss[0];
    if (line.isBlank()) {
      return null;
    }

    int strength;
    try {
      strength = Integer.parseInt(ss[1]);
    } catch (NumberFormatException e) {
      log.fatal("Failed to identify integer weight! Ignoring word\nLine: " + line);
      strength = 0;
    }

    return new WordAndStrength(line, strength);
  }

  /**
   * 留给 subclass 实现的初始化方法
   *
   * @param lines                           文件流
   * @param nrLines                         文件行数
   * @param options                         分类选项
   * @param extraBlankArrayEntriesToInclude 额外空行
   * @return 是否初始化成功
   */
  protected abstract boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude);
}
