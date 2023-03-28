// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   CorrectSpellingsList.java

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
 * 单词正确拼写列表。该类用于检测语句中的单词是否正确。<br/>
 *
 * @see ClassificationOptions
 */
public class CorrectSpellingsList {

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
   * @param sFilename 源文件名
   * @param options 分类选项
   * @return 是否初始化成功
   */
  public boolean initialise(String sFilename, ClassificationOptions options) {
    if (igCorrectWordMax > 0) {
      return true;
    }
    if (!options.bgCorrectSpellingsUsingDictionary) {
      return true;
    }
    igCorrectWordMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
    sgCorrectWord = new String[igCorrectWordMax];
    igCorrectWordCount = 0;
    File f = new File(sFilename);
    if (!f.exists()) {
      System.out.println("Could not find the spellings file: " + sFilename);
      return false;
    }
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
          igCorrectWordCount++;
          sgCorrectWord[igCorrectWordCount] = sLine;
        }
      }
      rReader.close();
      Sort.quickSortStrings(sgCorrectWord, 1, igCorrectWordCount);
    }  catch (FileNotFoundException e) {
      System.out.println("Could not find the spellings file: " + sFilename);
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      System.out.println("Found spellings file but could not read from it: " + sFilename);
      e.printStackTrace();
      return false;
    }
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
