// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   BoosterWordsList.java

package uk.ac.wlv.sentistrength.classification.resource.concrete;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.resource.Resource;
import uk.ac.wlv.utilities.Sort;

import java.util.Objects;
import java.util.stream.Stream;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 助推词列表类。助推词是可提高或降低后续词的情绪强度的词语，例如really.<br/>
 * 该类提供了助推词初始化方法，添加助推词方法，获取助推词改变的情绪强度方法。
 *
 * @see ClassificationOptions
 */
@Log4j2
public class BoosterWordsList extends Resource {

  private String[] sgBoosterWords;
  private int[] igBoosterWordStrength;
  private int igBoosterWordsCount;

  /**
   * 无参构造函数。
   */
  public BoosterWordsList() {
    igBoosterWordsCount = 0;
  }

  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    sgBoosterWords = new String[nrLines + 1 + extraBlankArrayEntriesToInclude];
    igBoosterWordStrength = new int[nrLines + 1 + extraBlankArrayEntriesToInclude];
    igBoosterWordsCount = 0;

    lines
        .filter(line -> !line.isBlank())
        .map(this::parseColumns)
        .filter(Objects::nonNull)
        .forEachOrdered(ws -> {
          igBoosterWordsCount++;
          sgBoosterWords[igBoosterWordsCount] = ws.word();
          igBoosterWordStrength[igBoosterWordsCount] = ws.strength();
        });

    sortBoosterWordList();

    return true;
  }

  @Override
  public boolean haveOptionsChanged(ClassificationOptions old, ClassificationOptions now) {
    return false;
  }

  /**
   * 添加一个新的助推词条目。
   *
   * @param sText                           助推词的文本
   * @param iWordStrength                   助推词改变的情绪强度
   * @param bSortBoosterListAfterAddingTerm 在添加后是否重新排序助推词列表
   * @return 是否添加成功
   */
  public boolean addExtraTerm(String sText, int iWordStrength, boolean bSortBoosterListAfterAddingTerm) {
    try {
      igBoosterWordsCount++;
      sgBoosterWords[igBoosterWordsCount] = sText;
      igBoosterWordStrength[igBoosterWordsCount] = iWordStrength;
      if (bSortBoosterListAfterAddingTerm) {
        Sort.quickSortStringsWithInt(sgBoosterWords, igBoosterWordStrength, 1, igBoosterWordsCount);
      }
    } catch (Exception e) {
      System.out.println("Could not add extra booster word: " + sText);
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 给助推词列表按字典序排序。
   */
  public void sortBoosterWordList() {
    Sort.quickSortStringsWithInt(sgBoosterWords, igBoosterWordStrength, 1, igBoosterWordsCount);
  }

  /**
   * 获取一个助推词改变的情绪强度。
   *
   * @param sWord 助推词的文本
   * @return 改变的情绪强度
   */
  public int getBoosterStrength(String sWord) {
    int iWordID = Sort.i_FindStringPositionInSortedArray(sWord.toLowerCase(), sgBoosterWords, 1, igBoosterWordsCount);
    if (iWordID >= 0) {
      return igBoosterWordStrength[iWordID];
    } else {
      return 0;
    }
  }
}
