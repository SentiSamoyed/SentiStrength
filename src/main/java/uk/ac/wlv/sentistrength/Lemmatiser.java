// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   Lemmatiser.java

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

/**
 * 存放词根与衍生词对应关系的词形还原器，数据来自文件 {@link ClassificationResources#sgLemmaFile}.
 *
 * @see ClassificationResources
 */
public class Lemmatiser {

  private String[] sgWord;
  private String[] sgLemma;
  private int igWordLast;
  // TODO ClassificationResources中，LemmaFile = “”，同时CorrectSpellingFile有两个，
  //  第一个Dictionary文件是不存在的，代码逻辑中第一个不存在会指向第二个文件进行读取

  /**
   *  Lemmatiser 构造函数
   */
  public Lemmatiser() {
    igWordLast = -1;
  }

  /**
   * 词形还原器初始化，从文件中读取词根与衍生词。
   *
   * @param sFileName 源文件
   * @param bForceUtf8 是否强制使用UTF-8编码解析文件
   * @return 是否初始化成功
   */
  public boolean initialise(String sFileName, boolean bForceUtf8) {
    int iLinesInFile;
    if (sFileName.equals("")) {
      System.out.println("No lemma file specified!");
      return false;
    }
    File f = new File(sFileName);
    if (!f.exists()) {
      System.out.println("Could not find lemma file: " + sFileName);
      return false;
    }
    iLinesInFile = FileOps.i_CountLinesInTextFile(sFileName);
    if (iLinesInFile < 2) {
      System.out.println("Less than 2 lines in sentiment file: " + sFileName);
      return false;
    }
    // 文件存在且文件内容大于等于两行
    // 大于等于两行的原因详见EnglishWordList.txt，词根和衍生词至少各占一行
    sgWord = new String[iLinesInFile + 1];
    sgLemma = new String[iLinesInFile + 1];
    igWordLast = -1;
    try {
      BufferedReader rReader;
      if (bForceUtf8) {
        // 使用utf-8编码
        rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFileName), StandardCharsets.UTF_8));
      } else {
        rReader = new BufferedReader(new FileReader(sFileName));
      }
      String sLine;
      while ((sLine = rReader.readLine()) != null) {
        if (!sLine.equals("")) {
          int iFirstTabLocation = sLine.indexOf("\t");
          if (iFirstTabLocation >= 0) {
            // 读取第二个制表符
            int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
            // 第一个制表符前的为词根，第一个制表符到第二个制表符之间或者第一个制表符往后是该制表符的衍生词
            sgWord[++igWordLast] = sLine.substring(0, iFirstTabLocation);
            if (iSecondTabLocation > 0) {
              sgLemma[igWordLast] = sLine.substring(iFirstTabLocation + 1, iSecondTabLocation);
            } else {
              sgLemma[igWordLast] = sLine.substring(iFirstTabLocation + 1);
            }
            // 将word及lemma字符串去除首尾多余空格
            if (sgWord[igWordLast].contains(" ")) {
              sgWord[igWordLast] = sgWord[igWordLast].trim();
            }
            if (sgLemma[igWordLast].contains(" ")) {
              sgLemma[igWordLast] = sgLemma[igWordLast].trim();
            }
          }
        }
      }
      rReader.close();
      Sort.quickSortStringsWithStrings(sgWord, sgLemma, 0, igWordLast);
    } catch (FileNotFoundException e) {
      System.out.println("Couldn't find lemma file: " + sFileName);
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      System.out.println("Found lemma file but couldn't read from it: " + sFileName);
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 词形还原，根据单词在语境中的形式找出单词对应的词根。
   *
   * @param sWord 单词文本
   * @return 单词的词根
   */
  public String lemmatise(String sWord) {
    // 根据初始化过的词形还原表，搜索衍生词表中是否有sWord，若存在返回其对应下标在lemma表中的内容，否则其为词根本身
    int iLemmaId = Sort.i_FindStringPositionInSortedArray(sWord, sgWord, 0, igWordLast);
    if (iLemmaId >= 0) {
      return sgLemma[iLemmaId];
    } else {
      return sWord;
    }
  }
}
