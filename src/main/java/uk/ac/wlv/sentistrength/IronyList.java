// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   IronyList.java

package uk.ac.wlv.sentistrength;

import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 存放反语词的列表，数据来自文件 {@link ClassificationResources#sgIronyWordListFile}.
 *
 * @see ClassificationResources
 */
public class IronyList {

  private String[] sgIronyTerm;
  private int igIronyTermCount;
  private int igIronyTermMax;

  /**
   * 构造函数。
   */
  public IronyList() {
    igIronyTermCount = 0;
    igIronyTermMax = 0;
  }

  /**
   * 判断一个词是否是反语词。
   *
   * @param term 词
   * @return 是否是反语词
   */
  public boolean termIsIronic(String term) {
    int iIronyTermCount = Sort.i_FindStringPositionInSortedArray(term, sgIronyTerm, 1, igIronyTermCount);
    return iIronyTermCount >= 0;
  }

  /**
   * 初始化反语词列表，从文件中读取反语词。
   *
   * @param sSourceFile 源文件
   * @param options     分类选项
   * @return 是否初始化成功
   */
  public boolean initialise(String sSourceFile, ClassificationOptions options) {
    if (igIronyTermCount > 0) {
      return true;
    }
    File f = new File(sSourceFile);
    if (!f.exists()) {
      return true;
    }
    try {
      igIronyTermMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
      igIronyTermCount = 0;
      sgIronyTerm = new String[igIronyTermMax];
      BufferedReader rReader;
      if (options.bgForceUTF8)
        rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSourceFile), StandardCharsets.UTF_8));
      else
        rReader = new BufferedReader(new FileReader(sSourceFile));
      String sLine;
      while ((sLine = rReader.readLine()) != null) {
        if (!sLine.equals("")) {
          String[] sData = sLine.split("\t");
          if (sData.length > 0) {
            sgIronyTerm[++igIronyTermCount] = sData[0];
          }
        }
      }
      rReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find IronyTerm file: " + sSourceFile);
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      System.out.println("Found IronyTerm file but could not read from it: " + sSourceFile);
      e.printStackTrace();
      return false;
    }
    Sort.quickSortStrings(sgIronyTerm, 1, igIronyTermCount);
    return true;
  }
}
