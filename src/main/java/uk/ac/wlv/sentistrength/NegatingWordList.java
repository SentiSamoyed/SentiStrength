// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   NegatingWordList.java

package uk.ac.wlv.sentistrength;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;


// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 否定词列表类，用于存储否定词词典。<br/>
 * 包含 sgNegatingWord、igNegatingWordCount 和 igNegatingWordMax 成员变量，并提供了初始化和判断是否为否定词的方法,
 * 初始化后的否定词在 sgNegatingWord 中按字典序排序。
 */
public class NegatingWordList {
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

  /**
   * 初始化 NegatingWordList 对象。
   *
   * @param sFilename 否定词表文件路径
   * @param options 分类的选项
   * @return 若初始化成功或者已经初始化过，则返回 true,否则返回 false
   */
  public boolean initialise(String sFilename, ClassificationOptions options) {
    if (igNegatingWordMax > 0) {
      return true;
    }
    File f = new File(sFilename);
    if (!f.exists()) {
      System.out.println("Could not find the negating words file: " + sFilename);
      return false;
    }
    igNegatingWordMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
    sgNegatingWord = new String[igNegatingWordMax];
    igNegatingWordCount = 0;
    try {
      BufferedReader rReader;
      if (options.bgForceUTF8) {
        rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), StandardCharsets.UTF_8));
      } else {
        rReader = new BufferedReader(new FileReader(sFilename));
      }
      String sLine;
      while ((sLine = rReader.readLine()) != null) {
        if (!sLine.equals("")) {
          igNegatingWordCount++;
          sgNegatingWord[igNegatingWordCount] = sLine;
        }
      }
      rReader.close();
      Sort.quickSortStrings(sgNegatingWord, 1, igNegatingWordCount);
    } catch (FileNotFoundException e) {
      System.out.println("Could not find negating words file: " + sFilename);
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      System.out.println("Found negating words file but could not read from it: " + sFilename);
      e.printStackTrace();
      return false;
    }
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
