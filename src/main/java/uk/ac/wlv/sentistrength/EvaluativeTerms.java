// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   EvaluativeTerms.java

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

/**
 * 存放额外的 Object, Evaluation 和 Strength.
 * 仅当开启参数 <code>additionalFile</code> 时，才会使用该类。
 *
 * @see ClassificationResources
 * @see IdiomList
 * @see SentimentWords
 */
public class EvaluativeTerms {

  /**
   * 最大的 Object 和 Evaluation 数量。
   */
  private int igObjectEvaluationMax;
  /**
   * 对象。
   */
  public String[] sgObject;
  /**
   * 评估。
   */
  public String[] sgObjectEvaluation;
  /**
   * 情感强度。
   */
  public int[] igObjectEvaluationStrength;
  /**
   * 记录处理 Object 和 Evaluation 时的 Index.
   */
  public int igObjectEvaluationCount;

  /**
   * 构造函数。
   */
  public EvaluativeTerms() {
    igObjectEvaluationMax = 0;
    igObjectEvaluationCount = 0;
  }

  /**
   * 初始化额外词的情感词典，向 {@link IdiomList} 和 {@link SentimentWords} 中添加词组和词。
   *
   * @param sSourceFile    文件名
   * @param options        分析的选项
   * @param idiomList      存放词组的情感词典
   * @param sentimentWords 存放词的情感词典
   * @return 是否初始化成功
   */
  public boolean initialise(String sSourceFile, ClassificationOptions options, IdiomList idiomList, SentimentWords sentimentWords) {
    if (igObjectEvaluationCount > 0) {
      return true;
    }
    File f = new File(sSourceFile);
    if (!f.exists()) {
      System.out.println("Could not find additional (object/evaluation) file: " + sSourceFile);
      return false;
    }
    int iStrength;
    boolean bIdiomsAdded = false;
    boolean bSentimentWordsAdded = false;
    try {
      igObjectEvaluationMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
      igObjectEvaluationCount = 0;
      sgObject = new String[igObjectEvaluationMax];
      sgObjectEvaluation = new String[igObjectEvaluationMax];
      igObjectEvaluationStrength = new int[igObjectEvaluationMax];
      BufferedReader rReader;
      if (options.bgForceUTF8) {
        rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSourceFile), StandardCharsets.UTF_8));
      } else {
        rReader = new BufferedReader(new FileReader(sSourceFile));
      }
      String sLine;
      while ((sLine = rReader.readLine()) != null) {
        if (sLine.indexOf("##") != 0 && sLine.indexOf("\t") > 0) {
          String[] sData = sLine.split("\t");
          if (sData.length > 2 && sData[2].indexOf("##") != 0) {
            sgObject[++igObjectEvaluationCount] = sData[0];
            sgObjectEvaluation[igObjectEvaluationCount] = sData[1];
            try {
              igObjectEvaluationStrength[igObjectEvaluationCount] = Integer.parseInt(sData[2].trim());
              if (igObjectEvaluationStrength[igObjectEvaluationCount] > 0) {
                igObjectEvaluationStrength[igObjectEvaluationCount]--;
              } else if (igObjectEvaluationStrength[igObjectEvaluationCount] < 0) {
                igObjectEvaluationStrength[igObjectEvaluationCount]++;
              }
            } catch (NumberFormatException e) {
              System.out.println("Failed to identify integer weight for object/evaluation! Ignoring object/evaluation");
              System.out.println("Line: " + sLine);
              igObjectEvaluationCount--;
            }
          } else if (sData[0].indexOf(" ") > 0) {
            try {
              iStrength = Integer.parseInt(sData[1].trim());
              // 向 idiomList 中添加额外的词组
              idiomList.addExtraIdiom(sData[0], iStrength, false);
              bIdiomsAdded = true;
            } catch (NumberFormatException e) {
              System.out.println("Failed to identify integer weight for idiom in additional file! Ignoring it");
              System.out.println("Line: " + sLine);
            }
          } else {
            try {
              iStrength = Integer.parseInt(sData[1].trim());
              sentimentWords.addOrModifySentimentTerm(sData[0], iStrength, false);
              bSentimentWordsAdded = true;
            } catch (NumberFormatException e) {
              System.out.println("Failed to identify integer weight for sentiment term in additional file! Ignoring it");
              System.out.println("Line: " + sLine);
              igObjectEvaluationCount--;
            }
          }
        }
      }
      rReader.close();
      if (igObjectEvaluationCount > 0)
        options.bgUseObjectEvaluationTable = true;
      if (bSentimentWordsAdded)
        sentimentWords.sortSentimentList();
      if (bIdiomsAdded)
        idiomList.convertIdiomStringsToWordLists();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find additional (object/evaluation) file: " + sSourceFile);
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      System.out.println("Found additional (object/evaluation) file but could not read from it: " + sSourceFile);
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
