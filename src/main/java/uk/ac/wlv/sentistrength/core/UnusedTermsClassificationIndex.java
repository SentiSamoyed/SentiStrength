// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   UnusedTermsClassificationIndex.java

package uk.ac.wlv.sentistrength.core;

import uk.ac.wlv.utilities.Trie;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 未使用词分类索引。
 *
 * @see Corpus
 */
public class UnusedTermsClassificationIndex {
  private String[] sgTermList;
  private int igTermListCount;
  private int igTermListMax;
  private int[] igTermListLessPtr;
  private int[] igTermListMorePtr;
  private int[] igTermListFreq;
  private int[] igTermListFreqTemp;
  private int[] igTermListPosClassDiff;
  private int[] iTermsAddedIDTemp; // 已添加的Term的ID缓存
  private int[] igTermListNegClassDiff;
  private int[] igTermListScaleClassDiff;
  private int[] igTermListBinaryClassDiff;
  private int[] igTermListTrinaryClassDiff;
  private int iTermsAddedIDTempCount;
  private int[][] igTermListPosCorrectClass;
  private int[][] igTermListNegCorrectClass;
  private int[][] igTermListScaleCorrectClass;
  private int[][] igTermListBinaryCorrectClass;
  private int[][] igTermListTrinaryCorrectClass;

  /**
   * UnusedTermsClassificationIndex 构造函数。
   */
  public UnusedTermsClassificationIndex() {
    sgTermList = null;
    igTermListCount = 0;
    igTermListMax = 50000; // 最大术语数
  }

  /**
   * 主函数。
   *
   * @param args1 操作指令
   */
  public static void main(String[] args1) {
  }

  /**
   * 将未使用的词添加到词表中。
   *
   * @param sTerm 词字符串
   */
  public void addTermToNewTermIndex(String sTerm) {
    if (sgTermList == null) {
      initialise(true, true, true, true);
    }
    if (sTerm.equals("")) {
      return;
    }
    boolean bDontAddMoreElements = false;
    if (igTermListCount == igTermListMax) {
      // 术语表容量达到最大 50000
      bDontAddMoreElements = true;
    }
    // 获取术语在字典树中的索引
    int iTermID = Trie.i_GetTriePositionForString(sTerm, sgTermList, igTermListLessPtr, igTermListMorePtr, 1, igTermListCount, bDontAddMoreElements);
    if (iTermID > 0) {
      // 如果存在，添加到Temp中，其出现频率Freq加1
      iTermsAddedIDTemp[++iTermsAddedIDTempCount] = iTermID;
      igTermListFreqTemp[iTermID]++;
      // 如果TermID大于目前TermList的数目，则修改TermList的数目为最大的Term的ID
      // 即术语表大小是由所遇到的ID最大的术语决定的
      if (iTermID > igTermListCount) {
        igTermListCount = iTermID;
      }
    }
  }

  /**
   * 将新索引及其积极消极情绪强度添加到主索引中。
   *
   * @param iCorrectPosClass 积极情绪的准确值
   * @param iEstPosClass     积极情绪的预估值
   * @param iCorrectNegClass 消极情绪的准确值
   * @param iEstNegClass     消极情绪的预估值
   */
  public void addNewIndexToMainIndexWithPosNegValues(int iCorrectPosClass, int iEstPosClass, int iCorrectNegClass, int iEstNegClass) {
    // Binary，Trinary，Scale 函数逻辑相同，数据保存结构不同
    if (iCorrectNegClass > 0 && iCorrectPosClass > 0) { // 情绪强度值合法性判断
      for (int iTerm = 1; iTerm <= iTermsAddedIDTempCount; iTerm++) {
        // 遍历缓存ID列表，若该ID对应的Term出现频率不为零，将其强度值添加到TermList
        int iTermID = iTermsAddedIDTemp[iTerm];
        if (igTermListFreqTemp[iTermID] != 0) {
          try {
            igTermListNegCorrectClass[iTermID][iCorrectNegClass - 1]++;
            igTermListPosCorrectClass[iTermID][iCorrectPosClass - 1]++;
            igTermListPosClassDiff[iTermID] += iCorrectPosClass - iEstPosClass;
            igTermListNegClassDiff[iTermID] += iCorrectNegClass + iEstNegClass;
            igTermListFreq[iTermID]++;
            iTermsAddedIDTemp[iTerm] = 0; // 清除缓存
          } catch (Exception e) {
            System.out.println("[UnusedTermsClassificationIndex] Error trying to add Pos + Neg to index. " + e.getMessage());
          }
        }
      }

    }
    iTermsAddedIDTempCount = 0;
  }

  /**
   * 将新索引及其 scale 值添加到主索引中。
   *
   * @param iCorrectScaleClass scale value 准确值
   * @param iEstScaleClass     scale 的预估值
   */
  public void addNewIndexToMainIndexWithScaleValues(int iCorrectScaleClass, int iEstScaleClass) {
    for (int iTerm = 1; iTerm <= iTermsAddedIDTempCount; iTerm++) {
      int iTermID = iTermsAddedIDTemp[iTerm];
      if (igTermListFreqTemp[iTermID] != 0) {
        try {
          igTermListScaleCorrectClass[iTermID][iCorrectScaleClass + 4]++;
          igTermListScaleClassDiff[iTermID] += iCorrectScaleClass - iEstScaleClass;
          igTermListFreq[iTermID]++;
          iTermsAddedIDTemp[iTerm] = 0;
        } catch (Exception e) {
          System.out.println("Error trying to add scale values to index. " + e.getMessage());
        }
      }
    }

    iTermsAddedIDTempCount = 0;
  }

  /**
   * 将新索引及其 Trinary 值添加到主索引中。
   *
   * @param iCorrectTrinaryClass Trinary 类型正确值
   * @param iEstTrinaryClass     Trinary 类型预估值
   */
  public void addNewIndexToMainIndexWithTrinaryValues(int iCorrectTrinaryClass, int iEstTrinaryClass) {
    for (int iTerm = 1; iTerm <= iTermsAddedIDTempCount; iTerm++) {
      int iTermID = iTermsAddedIDTemp[iTerm];
      if (igTermListFreqTemp[iTermID] != 0) {
        try {
          igTermListTrinaryCorrectClass[iTermID][iCorrectTrinaryClass + 1]++;
          igTermListTrinaryClassDiff[iTermID] += iCorrectTrinaryClass - iEstTrinaryClass;
          igTermListFreq[iTermID]++;
          iTermsAddedIDTemp[iTerm] = 0;
        } catch (Exception e) {
          System.out.println("Error trying to add trinary values to index. " + e.getMessage());
        }
      }
    }

    iTermsAddedIDTempCount = 0;
  }

  /**
   * 将新索引及其 Binary 值添加到主索引中。
   *
   * @param iCorrectBinaryClass Binary 类型正确值
   * @param iEstBinaryClass     Binary 类型预估值
   */
  public void addNewIndexToMainIndexWithBinaryValues(int iCorrectBinaryClass, int iEstBinaryClass) {
    for (int iTerm = 1; iTerm <= iTermsAddedIDTempCount; iTerm++) {
      int iTermID = iTermsAddedIDTemp[iTerm];
      if (igTermListFreqTemp[iTermID] != 0) {
        try {
          igTermListBinaryClassDiff[iTermID] += iCorrectBinaryClass - iEstBinaryClass;
          if (iCorrectBinaryClass == -1) {
            iCorrectBinaryClass = 0;
          }
          igTermListBinaryCorrectClass[iTermID][iCorrectBinaryClass]++;
          igTermListFreq[iTermID]++;
          iTermsAddedIDTemp[iTerm] = 0;
        } catch (Exception e) {
          System.out.println("Error trying to add scale values to index. " + e.getMessage());
        }
      }
    }

    iTermsAddedIDTempCount = 0;
  }

  /**
   * 未使用词表的初始化。
   *
   * @param bInitialiseScale   是否以 Scale 值初始化
   * @param bInitialisePosNeg  是否以积极和消极强度初始化
   * @param bInitialiseBinary  是否以 Binary 初始化
   * @param bInitialiseTrinary 是否以 Trinary 初始化
   */
  public void initialise(boolean bInitialiseScale, boolean bInitialisePosNeg, boolean bInitialiseBinary, boolean bInitialiseTrinary) {
    igTermListCount = 0;
    igTermListMax = 50000;
    iTermsAddedIDTempCount = 0;
    sgTermList = new String[igTermListMax];
    igTermListLessPtr = new int[igTermListMax + 1];
    igTermListMorePtr = new int[igTermListMax + 1];
    igTermListFreq = new int[igTermListMax + 1];
    igTermListFreqTemp = new int[igTermListMax + 1];
    iTermsAddedIDTemp = new int[igTermListMax + 1];
    if (bInitialisePosNeg) {
      igTermListNegCorrectClass = new int[igTermListMax + 1][5];
      igTermListPosCorrectClass = new int[igTermListMax + 1][5];
      igTermListNegClassDiff = new int[igTermListMax + 1];
      igTermListPosClassDiff = new int[igTermListMax + 1];
    }
    if (bInitialiseScale) {
      igTermListScaleCorrectClass = new int[igTermListMax + 1][9];
      igTermListScaleClassDiff = new int[igTermListMax + 1];
    }
    if (bInitialiseBinary) {
      igTermListBinaryCorrectClass = new int[igTermListMax + 1][2];
      igTermListBinaryClassDiff = new int[igTermListMax + 1];
    }
    if (bInitialiseTrinary) {
      igTermListTrinaryCorrectClass = new int[igTermListMax + 1][3];
      igTermListTrinaryClassDiff = new int[igTermListMax + 1];
    }
  }

  /**
   * 打印每个索引及其积极和消极情绪强度。
   *
   * @param sOutputFile 导出文件路径
   * @param iMinFreq    最小出现次数
   */
  public void printIndexWithPosNegValues(String sOutputFile, int iMinFreq) {
    // Binary，Trinary，Scale函数逻辑相同，数据保存结构不同
    try {
      // 打印Headers
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
      wWriter.write("Term\tTermFreq >= " + iMinFreq + "\t" + "PosClassDiff (correct-estimate)\t" + "NegClassDiff\t" + "PosClassAvDiff\t" + "NegClassAvDiff\t");
      for (int i = 1; i <= 5; i++) {
        wWriter.write("CorrectClass" + i + "pos\t");
      }
      for (int i = 1; i <= 5; i++) {
        wWriter.write("CorrectClass" + i + "neg\t");
      }
      wWriter.write("\n");
      if (igTermListCount > 0) {   // 打印TermList的
        for (int iTerm = 1; iTerm <= igTermListCount; iTerm++) {
          if (igTermListFreq[iTerm] >= iMinFreq) {
            wWriter.write(sgTermList[iTerm] + "\t" + igTermListFreq[iTerm] + "\t" + igTermListPosClassDiff[iTerm] + "\t" + igTermListNegClassDiff[iTerm] + "\t" + (float) igTermListPosClassDiff[iTerm] / (float) igTermListFreq[iTerm] + "\t" + (float) igTermListNegClassDiff[iTerm] / (float) igTermListFreq[iTerm] + "\t");
            for (int i = 0; i < 5; i++) {
              wWriter.write(igTermListPosCorrectClass[iTerm][i] + "\t");
            }
            for (int i = 0; i < 5; i++) {
              wWriter.write(igTermListNegCorrectClass[iTerm][i] + "\t");
            }
            wWriter.write("\n");
          }
        }
      } else {
        wWriter.write("No terms found in corpus!\n");
      }
      wWriter.close();
    } catch (IOException e) {
      System.out.println("Error printing index to " + sOutputFile);
      e.printStackTrace();
    }
  }

  /**
   * 打印每个索引及其 Scale 值。
   *
   * @param sOutputFile 导出文件路径
   * @param iMinFreq    最小出现次数
   */
  public void printIndexWithScaleValues(String sOutputFile, int iMinFreq) {
    try {
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
      wWriter.write("Term\tTermFreq\tScaleClassDiff (correct-estimate)\tScaleClassAvDiff\t");
      for (int i = -4; i <= 4; i++) {
        wWriter.write("CorrectClass" + i + "\t");

      }
      wWriter.write("\n");
      for (int iTerm = 1; iTerm <= igTermListCount; iTerm++) {
        if (igTermListFreq[iTerm] > iMinFreq) {
          wWriter.write(sgTermList[iTerm] + "\t" + igTermListFreq[iTerm] + "\t" + igTermListScaleClassDiff[iTerm] + "\t" + (float) igTermListScaleClassDiff[iTerm] / (float) igTermListFreq[iTerm] + "\t");
          for (int i = 0; i < 9; i++) {
            wWriter.write(igTermListScaleCorrectClass[iTerm][i] + "\t");
          }
          wWriter.write("\n");
        }
      }
      wWriter.close();
    } catch (IOException e) {
      System.out.println("Error printing Scale index to " + sOutputFile);
      e.printStackTrace();
    }
  }

  /**
   * 打印每个索引及其 Trinary 值。
   *
   * @param sOutputFile 导出文件路径
   * @param iMinFreq    最小出现次数
   */
  public void printIndexWithTrinaryValues(String sOutputFile, int iMinFreq) {
    try {
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
      wWriter.write("Term\tTermFreq\tTrinaryClassDiff (correct-estimate)\tTrinaryClassAvDiff\t");
      for (int i = -1; i <= 1; i++) {
        wWriter.write("CorrectClass" + i + "\t");
      }
      wWriter.write("\n");
      for (int iTerm = 1; iTerm <= igTermListCount; iTerm++) {
        if (igTermListFreq[iTerm] > iMinFreq) {
          wWriter.write(sgTermList[iTerm] + "\t" + igTermListFreq[iTerm] + "\t" + igTermListTrinaryClassDiff[iTerm] + "\t" + (float) igTermListTrinaryClassDiff[iTerm] / (float) igTermListFreq[iTerm] + "\t");
          for (int i = 0; i < 3; i++) {
            wWriter.write(igTermListTrinaryCorrectClass[iTerm][i] + "\t");
          }
          wWriter.write("\n");
        }
      }
      wWriter.close();
    } catch (IOException e) {
      System.out.println("Error printing Trinary index to " + sOutputFile);
      e.printStackTrace();
    }
  }

  /**
   * 打印每个索引及其 Binary 值。
   *
   * @param sOutputFile 导出文件路径
   * @param iMinFreq    最小出现次数
   */
  public void printIndexWithBinaryValues(String sOutputFile, int iMinFreq) {
    try {
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
      wWriter.write("Term\tTermFreq\tBinaryClassDiff (correct-estimate)\tBinaryClassAvDiff\t");
      wWriter.write("CorrectClass-1\tCorrectClass1\t");
      wWriter.write("\n");
      for (int iTerm = 1; iTerm <= igTermListCount; iTerm++) {
        if (igTermListFreq[iTerm] > iMinFreq) {
          wWriter.write(sgTermList[iTerm] + "\t" + igTermListFreq[iTerm] + "\t" + igTermListBinaryClassDiff[iTerm] + "\t" + (float) igTermListBinaryClassDiff[iTerm] / (float) igTermListFreq[iTerm] + "\t");
          for (int i = 0; i < 2; i++) {
            wWriter.write(igTermListBinaryCorrectClass[iTerm][i] + "\t");
          }
          wWriter.write("\n");
        }
      }
      wWriter.close();
    } catch (IOException e) {
      System.out.println("Error printing Binary index to " + sOutputFile);
      e.printStackTrace();
    }
  }
}
