// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   EmoticonsList.java

package uk.ac.wlv.sentistrength.wordlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 表情符列表类。表情符带有一定的情感强度。<br/>
 * 数据来自EmoticonLookUpTable.txt。
 *
 * @see ClassificationOptions
 */
public class EmoticonsList {

  private String[] sgEmoticon;
  private int[] igEmoticonStrength;
  private int igEmoticonCount;
  private int igEmoticonMax;

  public EmoticonsList() {
    igEmoticonCount = 0;
    igEmoticonMax = 0;
  }

  /**
   * 获取一个emoticon的强度。
   *
   * @param emoticon emoticon文本
   * @return 输入的emoticon的强度
   */
  public int getEmoticon(String emoticon) {
    int iEmoticon = Sort.i_FindStringPositionInSortedArray(emoticon, sgEmoticon, 1, igEmoticonCount);
    if (iEmoticon >= 0) {
      return igEmoticonStrength[iEmoticon];
    } else {
      return 999;
    }
  }

  /**
   * 初始化Emoticon和其对应强度的列表，并按字典序排序。
   *
   * @param sSourceFile 源文件名
   * @param options 分类选项
   * @return 是否初始化成功
   */
  public boolean initialise(String sSourceFile, ClassificationOptions options) {
    if (igEmoticonCount > 0) {
      return true;
    }
    File f = new File(sSourceFile);
    if (!f.exists()) {
      System.out.println("Could not find file: " + sSourceFile);
      return false;
    }
    try {
      igEmoticonMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
      igEmoticonCount = 0;
      sgEmoticon = new String[igEmoticonMax];
      igEmoticonStrength = new int[igEmoticonMax];
      BufferedReader rReader;
      if (options.bgForceUTF8) {
        rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSourceFile), StandardCharsets.UTF_8));
      } else {
        rReader = new BufferedReader(new FileReader(sSourceFile));
      }
      String sLine;
      while ((sLine = rReader.readLine()) != null) {
        if (!sLine.equals("")) {
          String[] sData = sLine.split("\t");
          if (sData.length > 1) {
            igEmoticonCount++;
            sgEmoticon[igEmoticonCount] = sData[0];
            try {
              igEmoticonStrength[igEmoticonCount] = Integer.parseInt(sData[1].trim());
            } catch (NumberFormatException e) {
              System.out.println("Failed to identify integer weight for emoticon! Ignoring emoticon");
              System.out.println("Line: " + sLine);
              igEmoticonCount--;
            }
          }
        }
      }
      rReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find emoticon file: " + sSourceFile);
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      System.out.println("Found emoticon file but could not read from it: " + sSourceFile);
      e.printStackTrace();
      return false;
    }
    if (igEmoticonCount > 1) {
      Sort.quickSortStringsWithInt(sgEmoticon, igEmoticonStrength, 1, igEmoticonCount);
    }
    return true;
  }
}
