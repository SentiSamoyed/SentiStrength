// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   IdiomList.java

package uk.ac.wlv.sentistrength.wordlist;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 存放习语的列表，其中数据来自文件 {@link ClassificationResources#idiomListFile}.
 *
 * @see ClassificationResources
 */
public class IdiomList extends WordList {

  /**
   * 习语列表。
   */
  public String[] sgIdioms;
  /**
   * 习语的情感强度。
   */
  public int[] igIdiomStrength;
  /**
   * 处理习语时的下标。
   */
  public int igIdiomCount;
  /**
   * 习语中的单词列表。
   */
  public String[][] sgIdiomWords;
  /**
   * 处理习语中的单词时的下标。
   */
  public int[] igIdiomWordCount;

  /**
   * 构造函数。
   */
  public IdiomList() {
    igIdiomCount = 0;
  }

  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    // 计算文件行数
    sgIdioms = new String[nrLines + 2 + extraBlankArrayEntriesToInclude];
    igIdiomStrength = new int[nrLines + 2 + extraBlankArrayEntriesToInclude];
    igIdiomCount = 0;

    lines
        .filter(line -> !line.isBlank())
        .map(this::parseColumns)
        .filter(Objects::nonNull)
        .forEachOrdered(ws -> {
          String line = ws.word();
          int strength = ws.strength();

          for (int i = 0; i < 2; i++) {
            if (line.indexOf("  ") > 0) {
              line = line.replace("  ", " ");
            }
          }

          igIdiomCount++;
          // 保存习语和情感值
          sgIdioms[igIdiomCount] = line;
          strength += strength > 0 ? -1 : 1;
          igIdiomStrength[igIdiomCount] = strength;
        });

    convertIdiomStringsToWordLists();
    return true;
  }

  @Override
  public boolean haveOptionsChanged(ClassificationOptions old, ClassificationOptions now) {
    return false;
  }

  /**
   * 添加额外的习语。<br>
   * 该方法在 {@link EvaluativeTerms} 中被调用。
   *
   * @param sIdiom                                          习语
   * @param iIdiomStrength                                  情感值
   * @param bConvertIdiomStringsToWordListsAfterAddingIdiom 添加习语之后是否拆分成单词并添加到单词列表中中
   * @return 是否添加成功
   */
  public boolean addExtraIdiom(String sIdiom, int iIdiomStrength, boolean bConvertIdiomStringsToWordListsAfterAddingIdiom) {
    try {
      igIdiomCount++;
      sgIdioms[igIdiomCount] = sIdiom;
      if (iIdiomStrength > 0) {
        iIdiomStrength--;
      } else if (iIdiomStrength < 0) {
        iIdiomStrength++;
      }
      igIdiomStrength[igIdiomCount] = iIdiomStrength;
      if (bConvertIdiomStringsToWordListsAfterAddingIdiom) {
        convertIdiomStringsToWordLists();
      }
    } catch (Exception e) {
      System.out.println("Could not add extra idiom: " + sIdiom);
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 将习语拆分成单词，存放在{@link #sgIdiomWords}中。<br>
   * 每个习语的单词数目存放在{@link #igIdiomWordCount}中。
   */
  public void convertIdiomStringsToWordLists() {
    // 初始化这两个变量, 允许每个习语最多有8个单词
    sgIdiomWords = new String[igIdiomCount + 1][10];
    igIdiomWordCount = new int[igIdiomCount + 1];
    for (int iIdiom = 1; iIdiom <= igIdiomCount; iIdiom++) {
      String[] sWordList = sgIdioms[iIdiom].split(" ");
      if (sWordList.length >= 9) {
        System.out.println("Ignoring idiom! Too many words in it! (>9): " + sgIdioms[iIdiom]);
      } else {
        igIdiomWordCount[iIdiom] = sWordList.length;
        System.arraycopy(sWordList, 0, sgIdiomWords[iIdiom], 0, sWordList.length);
      }
    }

  }

  /**
   * 获取习语的情感值。<br>
   * 该方法没有使用到。
   *
   * @param sPhrase 习语
   * @return 情感值
   */
  public int getIdiomStrength_oldNotUseful(String sPhrase) {
    sPhrase = sPhrase.toLowerCase();
    for (int i = 1; i <= igIdiomCount; i++) {
      if (sPhrase.contains(sgIdioms[i])) {
        return igIdiomStrength[i];
      }
    }
    return 999;
  }

  /**
   * 从 {@link #sgIdioms} 中获取习语。
   *
   * @param iIdiomId 习语的 Index
   * @return 习语
   */
  public String getIdiom(int iIdiomId) {
    if (iIdiomId > 0 && iIdiomId < igIdiomCount) {
      return sgIdioms[iIdiomId];
    } else {
      return "";
    }
  }
}
