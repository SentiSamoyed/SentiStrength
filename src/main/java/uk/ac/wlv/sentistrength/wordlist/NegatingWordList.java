// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   NegatingWordList.java

package uk.ac.wlv.sentistrength.wordlist;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.utilities.Sort;

import java.util.stream.Stream;


// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 否定词列表类，用于存储否定词词典。<br/>
 * 包含 sgNegatingWord、igNegatingWordCount 和 igNegatingWordMax 成员变量，并提供了初始化和判断是否为否定词的方法,
 * 初始化后的否定词在 sgNegatingWord 中按字典序排序。
 */
public class NegatingWordList extends WordList {
  /**
   * 存储否定词的字符串数组。
   */
  private String[] sgNegatingWord;
  /**
   * 否定词数量计数器。
   */
  private int igNegatingWordCount;
  /**
   * 否定词数组最大容量。
   */
  private int igNegatingWordMax;

  /**
   * 构造函数，初始化计数器和最大容量，将字段 igNegatingWordCount 与 igNegatingWordMax 都置为 0 。
   */
  public NegatingWordList() {
    igNegatingWordCount = 0;
    igNegatingWordMax = 0;
  }

  @Override
  public boolean initialise(String filename, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    if (igNegatingWordMax > 0) {
      return true;
    }

    return super.initialise(filename, options, extraBlankArrayEntriesToInclude);
  }

  /**
   * 初始化 NegatingWordList 对象。
   *
   * @param options 分类的选项
   * @return 若初始化成功或者已经初始化过，则返回 true,否则返回 false
   */
  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    igNegatingWordMax = nrLines + 2;
    sgNegatingWord = new String[igNegatingWordMax];
    igNegatingWordCount = 0;

    lines
        .filter(line -> !line.isBlank())
        .forEachOrdered(line -> {
          igNegatingWordCount++;
          sgNegatingWord[igNegatingWordCount] = line;
        });

    Sort.quickSortStrings(sgNegatingWord, 1, igNegatingWordCount);

    return true;
  }

  /**
   * 判断给定词是否为否定词。
   *
   * @param sWord 需要判断的词
   * @return 如果 sWord 是否定词，返回 true；否则返回 false
   */
  public boolean negatingWord(String sWord) {
    return Sort.i_FindStringPositionInSortedArray(sWord, sgNegatingWord, 1, igNegatingWordCount) >= 0;
  }
}
