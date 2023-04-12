// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   QuestionWords.java

package uk.ac.wlv.sentistrength.wordlist;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.utilities.Sort;

import java.util.stream.Stream;


// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 疑问词类，用于处理疑问词的相关操作，包括初始化、判断是否为疑问词等功能，疑问词按字典序存储。
 */
public class QuestionWords extends WordList {
  /**
   * 存储疑问词的字符串数组。
   */
  private String[] sgQuestionWord;
  /**
   * 存储疑问词汇的数量。
   */
  private int igQuestionWordCount;
  /**
   * 存储疑问词汇的最大数量。
   */
  private int igQuestionWordMax;

  /**
   * 构造函数，初始化 igQuestionWordCount和igQuestionWordMax 为 0 。
   */
  public QuestionWords() {
    igQuestionWordCount = 0;
    igQuestionWordMax = 0;
  }

  @Override
  public boolean initialise(String filename, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    if (igQuestionWordMax > 0) {
      return true;
    }

    return super.initialise(filename, options, extraBlankArrayEntriesToInclude);
  }

  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    igQuestionWordMax = nrLines + 2;
    sgQuestionWord = new String[igQuestionWordMax];
    igQuestionWordCount = 0;

    lines
        .filter(line -> !line.isBlank())
        .forEachOrdered(line -> {
          igQuestionWordCount++;
          sgQuestionWord[igQuestionWordCount] = line;
        });

    Sort.quickSortStrings(sgQuestionWord, 1, igQuestionWordCount);

    return true;
  }

  @Override
  public boolean haveOptionsChanged(ClassificationOptions old, ClassificationOptions now) {
    return false;
  }

  /**
   * 判断一个单词是否为疑问词。
   *
   * @param sWord 待判断的单词
   * @return 如果该单词是疑问词则返回 true，否则返回 false
   */
  public boolean questionWord(String sWord) {
    return Sort.i_FindStringPositionInSortedArray(sWord, sgQuestionWord, 1, igQuestionWordCount) >= 0;
  }
}
