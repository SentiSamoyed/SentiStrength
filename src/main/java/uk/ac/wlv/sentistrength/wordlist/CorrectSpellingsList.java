// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   CorrectSpellingsList.java

package uk.ac.wlv.sentistrength.wordlist;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.utilities.Sort;

import java.util.stream.Stream;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 单词正确拼写列表。该类用于检测语句中的单词是否正确。<br/>
 *
 * @see ClassificationOptions
 */
public class CorrectSpellingsList extends WordList {

  private String[] sgCorrectWord;
  private int igCorrectWordCount;
  private int igCorrectWordMax;

  public CorrectSpellingsList() {
    igCorrectWordCount = 0;
    igCorrectWordMax = 0;
  }

  /**
   * 初始化单词正确拼写的列表，并按字典序排列。
   *
   * @param options 分类选项
   * @return 是否初始化成功
   */
  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    if (igCorrectWordMax > 0 || !options.bgCorrectSpellingsUsingDictionary) {
      return true;
    }
    
    igCorrectWordMax = nrLines + 2;
    sgCorrectWord = new String[igCorrectWordMax];
    igCorrectWordCount = 0;

    lines
        .filter(line -> !line.isBlank())
        .forEachOrdered(line -> {
          igCorrectWordCount++;
          sgCorrectWord[igCorrectWordCount] = line;
        });

    Sort.quickSortStrings(sgCorrectWord, 1, igCorrectWordCount);

    return true;
  }

  /**
   * 查询一个单词是否拼写正确。
   *
   * @param sWord 单词
   * @return 单词是否拼写正确
   */
  public boolean correctSpelling(String sWord) {
    return Sort.i_FindStringPositionInSortedArray(sWord, sgCorrectWord, 1, igCorrectWordCount) >= 0;
  }
}
