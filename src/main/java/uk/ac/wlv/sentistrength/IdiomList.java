// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   IdiomList.java

package uk.ac.wlv.sentistrength;

import uk.ac.wlv.utilities.FileOps;

import java.io.*;

/**
 * 存放习语的列表，其中数据来自文件 {@link ClassificationResources#sgIdiomLookupTableFile}.
 *
 * @see ClassificationResources
 */
public class IdiomList {

  /**
   * 习语列表。
   */
  public String sgIdioms[];
  /**
   * 习语的情感强度。
   */
  public int igIdiomStrength[];
  /**
   * 处理习语时的下标。
   */
  public int igIdiomCount;
  /**
   * 习语中的单词列表。
   */
  public String sgIdiomWords[][];
  /**
   * 处理习语中的单词时的下标。
   */
  int igIdiomWordCount[];

  /**
   * 构造函数。
   */
  public IdiomList() {
    igIdiomCount = 0;
  }

  /**
   * 初始化习语列表，从文件中读取习语的情感值。
   *
   * @param sFilename                        文件名
   * @param options                          分析的选项
   * @param iExtraBlankArrayEntriesToInclude 为{@link EvaluativeTerms}额外增加的词组预留空间
   * @return 是否初始化成功
   */
  public boolean initialise(String sFilename, ClassificationOptions options, int iExtraBlankArrayEntriesToInclude) {
    int iLinesInFile = 0;
    int iIdiomStrength = 0;
    // 文件名为空, 返回false
    if (sFilename == "")
      return false;

    File f = new File(sFilename);
    // 文件不存在, 返回false
    if (!f.exists()) {
      System.out.println((new StringBuilder("Could not find idiom list file: ")).append(sFilename).toString());
      return false;
    }

    // 计算文件行数
    iLinesInFile = FileOps.i_CountLinesInTextFile(sFilename);
    sgIdioms = new String[iLinesInFile + 2 + iExtraBlankArrayEntriesToInclude];
    igIdiomStrength = new int[iLinesInFile + 2 + iExtraBlankArrayEntriesToInclude];
    igIdiomCount = 0;
    try {
      BufferedReader rReader;
      if (options.bgForceUTF8)
        // 使用 UTF8 编码读取文件
        rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), "UTF8"));
      else
        rReader = new BufferedReader(new FileReader(sFilename));
      String sLine;
      while ((sLine = rReader.readLine()) != null)
        if (sLine != "") {
          int iFirstTabLocation = sLine.indexOf("\t");
          if (iFirstTabLocation >= 0) {
            int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
            try {
              // 读取习语的情感值
              if (iSecondTabLocation > 0)
                iIdiomStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation).trim());
              else
                iIdiomStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1).trim());
              // 修正情感值
              if (iIdiomStrength > 0)
                iIdiomStrength--;
              else if (iIdiomStrength < 0)
                iIdiomStrength++;
            } catch (NumberFormatException e) {
              System.out.println("Failed to identify integer weight for idiom! Ignoring idiom");
              System.out.println((new StringBuilder("Line: ")).append(sLine).toString());
              iIdiomStrength = 0;
            }
            sLine = sLine.substring(0, iFirstTabLocation);
            if (sLine.indexOf(" ") >= 0)
              sLine = sLine.trim();
            if (sLine.indexOf("  ") > 0)
              sLine = sLine.replace("  ", " ");
            if (sLine.indexOf("  ") > 0)
              sLine = sLine.replace("  ", " ");
            if (sLine != "") {
              igIdiomCount++;
              // 保存习语和情感值
              sgIdioms[igIdiomCount] = sLine;
              igIdiomStrength[igIdiomCount] = iIdiomStrength;
            }
          }
        }
      rReader.close();
    } catch (FileNotFoundException e) {
      System.out.println((new StringBuilder("Could not find idiom list file: ")).append(sFilename).toString());
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      System.out.println((new StringBuilder("Found idiom list file but could not read from it: ")).append(sFilename).toString());
      e.printStackTrace();
      return false;
    }
    convertIdiomStringsToWordLists();
    return true;
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
      if (iIdiomStrength > 0)
        iIdiomStrength--;
      else if (iIdiomStrength < 0)
        iIdiomStrength++;
      igIdiomStrength[igIdiomCount] = iIdiomStrength;
      if (bConvertIdiomStringsToWordListsAfterAddingIdiom)
        convertIdiomStringsToWordLists();
    } catch (Exception e) {
      System.out.println((new StringBuilder("Could not add extra idiom: ")).append(sIdiom).toString());
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
      String sWordList[] = sgIdioms[iIdiom].split(" ");
      if (sWordList.length >= 9) {
        System.out.println((new StringBuilder("Ignoring idiom! Too many words in it! (>9): ")).append(sgIdioms[iIdiom]).toString());
      } else {
        igIdiomWordCount[iIdiom] = sWordList.length;
        for (int iTerm = 0; iTerm < sWordList.length; iTerm++)
          sgIdiomWords[iIdiom][iTerm] = sWordList[iTerm];

      }
    }

  }

  /**
   * 获取习语的情感值。<br>
   * 该方法没有使用到。
   *
   * @param sPhrase 习语
   * @return 情感值
   * @deprecated
   */
  public int getIdiomStrength_oldNotUseful(String sPhrase) {
    sPhrase = sPhrase.toLowerCase();
    for (int i = 1; i <= igIdiomCount; i++)
      if (sPhrase.indexOf(sgIdioms[i]) >= 0)
        return igIdiomStrength[i];

    return 999;
  }

  /**
   * 从 {@link #sgIdioms} 中获取习语。
   *
   * @param iIdiomID 习语的 Index
   * @return 习语
   */
  public String getIdiom(int iIdiomID) {
    if (iIdiomID > 0 && iIdiomID < igIdiomCount)
      return sgIdioms[iIdiomID];
    else
      return "";
  }
}
