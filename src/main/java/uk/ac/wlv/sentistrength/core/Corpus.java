// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   Corpus.java

package uk.ac.wlv.sentistrength.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import uk.ac.wlv.sentistrength.SentiStrength;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;
import uk.ac.wlv.sentistrength.classification.ClassificationStatistics;
import uk.ac.wlv.sentistrength.core.component.Paragraph;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions, ClassificationResources, UnusedTermsClassificationIndex, Paragraph, 
//            ClassificationStatistics, SentimentWords

/**
 * 存放所有语料的语料库，同时用作 SentiStrength 算法的效果衡量。
 *
 * @see SentiStrength
 */
public class Corpus {
  /**
   * 分类选项。
   */
  public ClassificationOptions options;
  /**
   * 分类依赖资源，包括所有的字典。
   */
  public ClassificationResources resources;
  /**
   * 段落。
   */
  private Paragraph[] paragraph;
  /**
   * 段落数目。
   */
  private int igParagraphCount;
  /**
   * 积极情绪强度正确值。
   */
  private int[] igPosCorrect;
  /**
   * 消极情绪强度正确值。
   */
  private int[] igNegCorrect;
  /**
   * Binary 正确值。
   */
  private int[] igTrinaryCorrect;
  /**
   * Scale 正确值。
   */
  private int[] igScaleCorrect;
  /**
   * 积极分类。
   */
  private int[] igPosClass;
  /**
   * 消极分类。
   */
  private int[] igNegClass;
  /**
   * Trinary 分类。
   */
  private int[] igTrinaryClass;
  /**
   * Scale 分类。
   */
  private int[] igScaleClass;
  /**
   * 当前语料库是否已经分类。
   */
  private boolean bgCorpusClassified;
  /**
   * 情绪强度 ID 列表。
   */
  private int[] igSentimentIDList;
  /**
   * 情绪强度 ID 列表的长度。
   */
  private int igSentimentIDListCount;
  /**
   * 情绪强度 ID 中段落的数目。
   */
  private int[] igSentimentIDParagraphCount;
  /**
   * 是否已经生成了情绪强度 ID 列表。
   */
  private boolean bSentimentIDListMade;
  /**
   * 未使用的词分类结果索引。
   */
  UnusedTermsClassificationIndex unusedTermsClassificationIndex;
  /**
   * 是否是子语料库成员。
   */
  private boolean[] bgSupcorpusMember;
  /**
   * 子语料库成员数目。
   */
  int igSupcorpusMemberCount;

  /**
   * Corpus 构造函数。
   */
  public Corpus() {
    options = new ClassificationOptions();
    resources = new ClassificationResources();
    igParagraphCount = 0;
    bgCorpusClassified = false;
    igSentimentIDListCount = 0;
    bSentimentIDListMade = false;
    unusedTermsClassificationIndex = null;
  }

  /**
   * 将语料库按照分类进行添加索引。
   */
  public void indexClassifiedCorpus() {
    unusedTermsClassificationIndex = new UnusedTermsClassificationIndex();
    if (options.bgScaleMode) {
      unusedTermsClassificationIndex.initialise(true, false, false, false);
      for (int i = 1; i <= igParagraphCount; i++) {
        paragraph[i].addParagraphToIndexWithScaleValues(unusedTermsClassificationIndex, igScaleCorrect[i], igScaleClass[i]);
      }
    } else if (options.bgTrinaryMode && options.bgBinaryVersionOfTrinaryMode) {
      unusedTermsClassificationIndex.initialise(false, false, true, false);
      for (int i = 1; i <= igParagraphCount; i++) {
        paragraph[i].addParagraphToIndexWithBinaryValues(unusedTermsClassificationIndex, igTrinaryCorrect[i], igTrinaryClass[i]);
      }
    } else if (options.bgTrinaryMode && !options.bgBinaryVersionOfTrinaryMode) {
      unusedTermsClassificationIndex.initialise(false, false, false, true);
      for (int i = 1; i <= igParagraphCount; i++) {
        paragraph[i].addParagraphToIndexWithTrinaryValues(unusedTermsClassificationIndex, igTrinaryCorrect[i], igTrinaryClass[i]);
      }
    } else {
      unusedTermsClassificationIndex.initialise(false, true, false, false);
      for (int i = 1; i <= igParagraphCount; i++) {
        paragraph[i].addParagraphToIndexWithPosNegValues(unusedTermsClassificationIndex, igPosCorrect[i], igPosClass[i], igNegCorrect[i], igNegClass[i]);
      }
    }
  }

  /**
   * 打印语料库中没有使用的词的分类的索引。
   *
   * @param saveFile 保存文件路径
   * @param iMinFreq 最小出现次数
   */
  public void printCorpusUnusedTermsClassificationIndex(String saveFile, int iMinFreq) {
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    if (unusedTermsClassificationIndex == null) {
      indexClassifiedCorpus();
    }
    if (options.bgScaleMode) {
      unusedTermsClassificationIndex.printIndexWithScaleValues(saveFile, iMinFreq);
    } else if (options.bgTrinaryMode && options.bgBinaryVersionOfTrinaryMode) {
      unusedTermsClassificationIndex.printIndexWithBinaryValues(saveFile, iMinFreq);
    } else if (options.bgTrinaryMode) {
      unusedTermsClassificationIndex.printIndexWithTrinaryValues(saveFile, iMinFreq);
    } else {
      unusedTermsClassificationIndex.printIndexWithPosNegValues(saveFile, iMinFreq);
    }
    System.out.println("Term weights saved to " + saveFile);
  }

  /**
   * 设置子语料库。
   *
   * @param bSubcorpusMember 是否为子语料库成员
   */
  public void setSubcorpus(boolean[] bSubcorpusMember) {
    // TODO 该函数未被使用，不确定哪个类掌握bSubcorpusMember参数
    igSupcorpusMemberCount = 0;
    for (int i = 0; i <= igParagraphCount; i++) {
      if (bSubcorpusMember[i]) {
        bgSupcorpusMember[i] = true;
        igSupcorpusMemberCount++;
      } else {
        bgSupcorpusMember[i] = false;
      }
    }
  }

  /**
   * 使用整个语料库而不是子语料库。
   */
  public void useWholeCorpusNotSubcorpus() {
    for (int i = 0; i <= igParagraphCount; i++) {
      bgSupcorpusMember[i] = true;
    }
    igSupcorpusMemberCount = igParagraphCount;
  }

  /**
   * 获取语料库大小。
   *
   * @return 语料库大小
   */
  public int getCorpusSize() {
    return igParagraphCount;
  }

  /**
   * 将单个文本作为一个语料库。
   *
   * @param sText       文本字符串
   * @param iPosCorrect 文本积极情绪强度
   * @param iNegCorrect 文本消极情绪强度
   * @return 是否添加成功
   */
  public boolean setSingleTextAsCorpus(String sText, int iPosCorrect, int iNegCorrect) {
    if (resources == null && !resources.initialise(options)) {
      // TODO bug 当 resource 为 null 时进行了函数调用
      return false;
    }
    igParagraphCount = 2;
    paragraph = new Paragraph[igParagraphCount];
    igPosCorrect = new int[igParagraphCount];
    igNegCorrect = new int[igParagraphCount];
    igTrinaryCorrect = new int[igParagraphCount];
    igScaleCorrect = new int[igParagraphCount];
    bgSupcorpusMember = new boolean[igParagraphCount];
    igParagraphCount = 1;
    paragraph[igParagraphCount] = new Paragraph();
    paragraph[igParagraphCount].setParagraph(sText, resources, options);
    igPosCorrect[igParagraphCount] = iPosCorrect;
    if (iNegCorrect < 0) {
      iNegCorrect *= -1;
    }
    igNegCorrect[igParagraphCount] = iNegCorrect;
    useWholeCorpusNotSubcorpus();
    return true;
  }

  /**
   * 设置语料库。
   *
   * @param sInFilenameAndPath 语料库文件名以及路径
   * @return 语料库设置是否成功
   */
  public boolean setCorpus(String sInFilenameAndPath) {
    if (resources == null && !resources.initialise(options)) {
      // TODO bug 当 resource 为 null 时进行了函数调用
      return false;
    }
    igParagraphCount = FileOps.i_CountLinesInTextFile(sInFilenameAndPath) + 1;
    if (igParagraphCount <= 2) {
      igParagraphCount = 0;
      return false;
    }
    paragraph = new Paragraph[igParagraphCount];
    igPosCorrect = new int[igParagraphCount];
    igNegCorrect = new int[igParagraphCount];
    igTrinaryCorrect = new int[igParagraphCount];
    igScaleCorrect = new int[igParagraphCount];
    bgSupcorpusMember = new boolean[igParagraphCount];
    igParagraphCount = 0;
    try {
      BufferedReader rReader = new BufferedReader(new FileReader(sInFilenameAndPath));
      String sLine;
      if (rReader.ready()) {
        sLine = rReader.readLine();
      }
      while ((sLine = rReader.readLine()) != null) {
        if (!sLine.equals("")) {
          paragraph[++igParagraphCount] = new Paragraph();
          int iLastTabPos = sLine.lastIndexOf("\t");
          int iFirstTabPos = sLine.indexOf("\t");
          if (iFirstTabPos < iLastTabPos || iFirstTabPos > 0 && (options.bgTrinaryMode || options.bgScaleMode)) {
            paragraph[igParagraphCount].setParagraph(sLine.substring(iLastTabPos + 1), resources, options);
            if (options.bgTrinaryMode) {
              try {
                igTrinaryCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(0, iFirstTabPos).trim());
              } catch (Exception e) {
                System.out.println("Trinary classification could not be read and will be ignored!: " + sLine);
                igTrinaryCorrect[igParagraphCount] = 999;
              }
              if (igTrinaryCorrect[igParagraphCount] > 1 || igTrinaryCorrect[igParagraphCount] < -1) {
                System.out.println("Trinary classification out of bounds and will be ignored!: " + sLine);
                igParagraphCount--;
              } else if (options.bgBinaryVersionOfTrinaryMode && igTrinaryCorrect[igParagraphCount] == 0) {
                System.out.println("Warning, unexpected 0 in binary classification!: " + sLine);
              }
            } else if (options.bgScaleMode) {
              try {
                igScaleCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(0, iFirstTabPos).trim());
              } catch (Exception e) {
                System.out.println("Scale classification could not be read and will be ignored!: " + sLine);
                igScaleCorrect[igParagraphCount] = 999;
              }
              if (igScaleCorrect[igParagraphCount] > 4 || igTrinaryCorrect[igParagraphCount] < -4) {
                System.out.println("Scale classification out of bounds (-4 to +4) and will be ignored!: " + sLine);
                igParagraphCount--;
              }
            } else {
              try {
                igPosCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(0, iFirstTabPos).trim());
                igNegCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(iFirstTabPos + 1, iLastTabPos).trim());
                if (igNegCorrect[igParagraphCount] < 0) {
                  igNegCorrect[igParagraphCount] = -igNegCorrect[igParagraphCount];
                }
              } catch (Exception e) {
                System.out.println("Positive or negative classification could not be read and will be ignored!: " + sLine);
                igPosCorrect[igParagraphCount] = 0;
              }
              if (igPosCorrect[igParagraphCount] > 5 || igPosCorrect[igParagraphCount] < 1) {
                System.out.println("Warning, positive classification out of bounds and line will be ignored!: " + sLine);
                igParagraphCount--;
              } else if (igNegCorrect[igParagraphCount] > 5 || igNegCorrect[igParagraphCount] < 1) {
                System.out.println("Warning, negative classification out of bounds (must be 1,2,3,4, or 5, with or without -) and line will be ignored!: " + sLine);
                igParagraphCount--;
              }
            }
          } else {
            if (iFirstTabPos >= 0) {
              if (options.bgTrinaryMode) {
                igTrinaryCorrect[igParagraphCount] = Integer.parseInt(sLine.substring(0, iFirstTabPos).trim());
              }
              sLine = sLine.substring(iFirstTabPos + 1);
            } else if (options.bgTrinaryMode) {
              igTrinaryCorrect[igParagraphCount] = 0;
            }
            paragraph[igParagraphCount].setParagraph(sLine, resources, options);
            igPosCorrect[igParagraphCount] = 0;
            igNegCorrect[igParagraphCount] = 0;
          }
        }
      }
      rReader.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    useWholeCorpusNotSubcorpus();
    System.out.println("Number of texts in corpus: " + igParagraphCount);
    return true;
  }

  /**
   * 语料库初始化。
   *
   * @return 语料库初始化是否成功
   */
  public boolean initialise() {
    return resources.initialise(options);
  }

  /**
   * 分段重新计算语料库的情感强度得分。
   */
  public void reCalculateCorpusSentimentScores() {
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i]) {
        paragraph[i].recalculateParagraphSentimentScores();
      }
    }
    calculateCorpusSentimentScores();
  }

  /**
   * 获取语料库某个段落的积极情感得分。
   *
   * @param i 段落的序号
   * @return 该段落的积极情感得分
   */
  public int getCorpusMemberPositiveSentimentScore(int i) {
    if (i < 1 || i > igParagraphCount) {
      return 0;
    } else {
      return paragraph[i].getParagraphPositiveSentiment();
    }
  }

  /**
   * 获取语料库某个段落的消极情感得分。
   *
   * @param i 段落的序号
   * @return 该段落的消极情感得分
   */
  public int getCorpusMemberNegativeSentimentScore(int i) {
    if (i < 1 || i > igParagraphCount) {
      return 0;
    } else {
      return paragraph[i].getParagraphNegativeSentiment();
    }
  }

  /**
   * 计算语料库的情绪强度得分。
   */
  public void calculateCorpusSentimentScores() {
    if (igParagraphCount == 0) {
      return;
    }
    if (igPosClass == null || igPosClass.length < igPosCorrect.length) {
      igPosClass = new int[igParagraphCount + 1];
      igNegClass = new int[igParagraphCount + 1];
      igTrinaryClass = new int[igParagraphCount + 1];
      igScaleClass = new int[igParagraphCount + 1];
    }
    // 按段落划分，计算每个段落的PosSentiment和NegSentiment以及Trinary，Scale
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i]) {
        igPosClass[i] = paragraph[i].getParagraphPositiveSentiment();
        igNegClass[i] = paragraph[i].getParagraphNegativeSentiment();
        if (options.bgTrinaryMode) {
          igTrinaryClass[i] = paragraph[i].getParagraphTrinarySentiment();
        }
        if (options.bgScaleMode) {
          igScaleClass[i] = paragraph[i].getParagraphScaleSentiment();
        }
      }
    }
    bgCorpusClassified = true;
  }

  /**
   * 将语料库基于某个情感词情感强度的变化重新分类。
   *
   * @param iSentimentWordID       情感值变化的词语的ID
   * @param iMinParasToContainWord 最小的包含该单词的段落数目
   */
  public void reClassifyClassifiedCorpusForSentimentChange(int iSentimentWordID, int iMinParasToContainWord) {
    if (igParagraphCount == 0) {
      return;
    }
    if (!bSentimentIDListMade) {
      makeSentimentIDListForCompleteCorpusIgnoringSubcorpus();
    }
    int iSentimentWordIDArrayPos = Sort.i_FindIntPositionInSortedArray(iSentimentWordID, igSentimentIDList, 1, igSentimentIDListCount);
    if (iSentimentWordIDArrayPos == -1 || igSentimentIDParagraphCount[iSentimentWordIDArrayPos] < iMinParasToContainWord) {
      return;
    }
    igPosClass = new int[igParagraphCount + 1];
    igNegClass = new int[igParagraphCount + 1];
    if (options.bgTrinaryMode) {
      igTrinaryClass = new int[igParagraphCount + 1];
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i]) {
        // 由单个词语的情绪值变化，到段落的情绪值变化
        paragraph[i].reClassifyClassifiedParagraphForSentimentChange(iSentimentWordID);
        igPosClass[i] = paragraph[i].getParagraphPositiveSentiment();
        igNegClass[i] = paragraph[i].getParagraphNegativeSentiment();
        if (options.bgTrinaryMode) {
          igTrinaryClass[i] = paragraph[i].getParagraphTrinarySentiment();
        }
        if (options.bgScaleMode) {
          igScaleClass[i] = paragraph[i].getParagraphScaleSentiment();
        }
      }
    }
    bgCorpusClassified = true;
  }

  /**
   * 打印语料库的情感得分至指定文件。
   *
   * @param sOutFilenameAndPath 文件路径
   * @return 是否打印成功
   */
  public boolean printCorpusSentimentScores(String sOutFilenameAndPath) {
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    try {
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutFilenameAndPath));
      wWriter.write("Correct+\tCorrect-\tPredict+\tPredict-\tText\n");
      for (int i = 1; i <= igParagraphCount; i++) {
        if (bgSupcorpusMember[i]) {
          wWriter.write(igPosCorrect[i] + "\t" + igNegCorrect[i] + "\t" + igPosClass[i] + "\t" + igNegClass[i] + "\t" + paragraph[i].getTaggedParagraph() + "\n");
        }
      }
      wWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 获取积极情绪的分类准确比例。
   *
   * @return 积极情绪的分类正确比例
   */
  public float getClassificationPositiveAccuracyProportion() {
    if (igSupcorpusMemberCount == 0) {
      return 0.0F;
    } else {
      return (float) getClassificationPositiveNumberCorrect() / (float) igSupcorpusMemberCount;
    }
  }

  /**
   * 获取消极情绪的分类准确比例。
   *
   * @return 消极情绪的分类正确比例
   */
  public float getClassificationNegativeAccuracyProportion() {
    if (igSupcorpusMemberCount == 0) {
      return 0.0F;
    } else {
      return (float) getClassificationNegativeNumberCorrect() / (float) igSupcorpusMemberCount;
    }
  }

  /**
   * 获取相对于基线的消极情绪预测准确比例。
   *
   * @return 消极情绪预测准确比例
   */
  public double getBaselineNegativeAccuracyProportion() {
    if (igParagraphCount == 0) {
      return 0.0D;
    } else {
      return ClassificationStatistics.baselineAccuracyMajorityClassProportion(igNegCorrect, igParagraphCount);
    }
  }

  /**
   * 获取相对于基线的积极情绪预测准确比例。
   *
   * @return 积极情绪预测准确比例
   */
  public double getBaselinePositiveAccuracyProportion() {
    if (igParagraphCount == 0) {
      return 0.0D;
    } else {
      return ClassificationStatistics.baselineAccuracyMajorityClassProportion(igPosCorrect, igParagraphCount);
    }
  }

  /**
   * 获取分类为消极情绪中的正确分类数量。
   *
   * @return 分类为消极情绪中的正确分类数量
   */
  public int getClassificationNegativeNumberCorrect() {
    if (igParagraphCount == 0) {
      return 0;
    }
    int iMatches = 0;
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i] && igNegCorrect[i] == -igNegClass[i]) {
        iMatches++;
      }
    }
    return iMatches;
  }

  /**
   * 获取分类为积极情绪中的正确分类数量。
   *
   * @return 分类为积极情绪中的正确分类数量
   */
  public int getClassificationPositiveNumberCorrect() {
    if (igParagraphCount == 0) {
      return 0;
    }
    int iMatches = 0;
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i] && igPosCorrect[i] == igPosClass[i]) {
        iMatches++;
      }
    }

    return iMatches;
  }

  /**
   * 获取预测为积极情绪的与其真实值的平均差值。
   *
   * @return 预测为积极情绪的与其真实值的平均差值
   */
  public double getClassificationPositiveMeanDifference() {
    if (igParagraphCount == 0) {
      return 0.0D;
    }
    double fTotalDiff = 0.0D;
    int iTotal = 0;
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i]) {
        fTotalDiff += Math.abs(igPosCorrect[i] - igPosClass[i]);
        iTotal++;
      }
    }

    if (iTotal > 0) {
      return fTotalDiff / (double) iTotal;
    } else {
      return 0.0D;
    }
  }

  /**
   * 获取预测为消极情绪的与其真实值的平均差值。
   *
   * @return 预测为消极情绪的与其真实值的平均差值
   */
  public int getClassificationPositiveTotalDifference() {
    if (igParagraphCount == 0) {
      return 0;
    }
    int iTotalDiff = 0;
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i]) {
        iTotalDiff += Math.abs(igPosCorrect[i] - igPosClass[i]);
      }
    }

    return iTotalDiff;
  }

  /**
   * 获取按照 Trinary 分类模式下分类预测正确的数目。
   *
   * @return Trinary 分类模式下准确预测的数目
   */
  public int getClassificationTrinaryNumberCorrect() {
    if (igParagraphCount == 0) {
      return 0;
    }
    int iTrinaryCorrect = 0;
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i] && igTrinaryCorrect[i] == igTrinaryClass[i]) {
        iTrinaryCorrect++;
      }
    }

    return iTrinaryCorrect;
  }

  /**
   * 获取与整个语料库的分类结果的 Scale 相关系数。
   *
   * @return 与整个语料库的分类结果的相关系数
   */
  public float getClassificationScaleCorrelationWholeCorpus() {
    if (igParagraphCount == 0) {
      return 0.0F;
    } else {
      return (float) ClassificationStatistics.correlation(igScaleCorrect, igScaleClass, igParagraphCount);
    }
  }

  /**
   * 获取 Scale 分类正确的准确率。
   *
   * @return 分类正确的数目 / 子语料库数目
   */
  public float getClassificationScaleAccuracyProportion() {
    if (igSupcorpusMemberCount == 0) {
      return 0.0F;
    } else {
      return (float) getClassificationScaleNumberCorrect() / (float) igSupcorpusMemberCount;
    }
  }

  /**
   * 获取积极情绪预测与整个语料库的相关系数。
   *
   * @return 积极情绪预测与整个语料库的相关系数
   */
  public float getClassificationPosCorrelationWholeCorpus() {
    if (igParagraphCount == 0) {
      return 0.0F;
    } else {
      return (float) ClassificationStatistics.correlationAbs(igPosCorrect, igPosClass, igParagraphCount);
    }
  }

  /**
   * 获取消极情绪预测与整个语料库的相关系数。
   *
   * @return 消极情绪预测与整个语料库的相关系数
   */
  public float getClassificationNegCorrelationWholeCorpus() {
    if (igParagraphCount == 0) {
      return 0.0F;
    } else {
      return (float) ClassificationStatistics.correlationAbs(igNegCorrect, igNegClass, igParagraphCount);
    }
  }

  /**
   * 获取语料库的 Scale 分类正确数目。
   *
   * @return 语料库分类的正确数目
   */
  public int getClassificationScaleNumberCorrect() {
    if (igParagraphCount == 0) {
      return 0;
    }
    int iScaleCorrect = 0;
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i] && igScaleCorrect[i] == igScaleClass[i]) {
        iScaleCorrect++;
      }
    }
    return iScaleCorrect;
  }

  /**
   * 获取所有消极情绪预测值与真实值的总差值。
   *
   * @return 所有消极情绪预测值与真实值的总差值
   */
  public int getClassificationNegativeTotalDifference() {
    if (igParagraphCount == 0) {
      return 0;
    }
    int iTotalDiff = 0;
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i]) {
        iTotalDiff += Math.abs(igNegCorrect[i] + igNegClass[i]);
      }
    }

    return iTotalDiff;
  }

  /**
   * 获取所有消极情绪预测值与真实值的平均差值。
   *
   * @return 所有消极情绪预测值与真实值的平均差值
   */
  public double getClassificationNegativeMeanDifference() {
    if (igParagraphCount == 0) {
      return 0.0D;
    }
    double fTotalDiff = 0.0D;
    int iTotal = 0;
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    for (int i = 1; i <= igParagraphCount; i++) {
      if (bgSupcorpusMember[i]) {
        fTotalDiff += Math.abs(igNegCorrect[i] + igNegClass[i]);
        iTotal++;
      }
    }
    if (iTotal > 0) {
      return fTotalDiff / (double) iTotal;
    } else {
      return 0.0D;
    }
  }

  /**
   * 将分类结果输出到指定文件（函数未完成汇总功能）。
   *
   * @param sOutFilenameAndPath 输出文件路径
   * @return 是否输出成功
   */
  public boolean printClassificationResultsSummary_NOT_DONE(String sOutFilenameAndPath) {
    if (!bgCorpusClassified) {
      calculateCorpusSentimentScores();
    }
    try {
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutFilenameAndPath));
      for (int i = 1; i <= igParagraphCount; i++) {
        boolean _tmp = bgSupcorpusMember[i];
        // TODO 此处循环只做了局部变量的声明，没有进行其他操作
      }

      wWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 在忽略子语料库的条件下生成整个语料库的情感ID列表。
   */
  public void makeSentimentIDListForCompleteCorpusIgnoringSubcorpus() {
    igSentimentIDListCount = 0;
    for (int i = 1; i <= igParagraphCount; i++) {
      paragraph[i].makeSentimentIDList();
      if (paragraph[i].getSentimentIDList() != null) {
        igSentimentIDListCount += paragraph[i].getSentimentIDList().length;
      }
    }

    if (igSentimentIDListCount > 0) {
      igSentimentIDList = new int[igSentimentIDListCount + 1];
      igSentimentIDParagraphCount = new int[igSentimentIDListCount + 1];
      igSentimentIDListCount = 0;
      for (int i = 1; i <= igParagraphCount; i++) {
        int[] sentenceIDList = paragraph[i].getSentimentIDList();
        if (sentenceIDList != null) {
          for (int j = 0; j < sentenceIDList.length; j++) {
            if (sentenceIDList[j] != 0) {
              igSentimentIDList[++igSentimentIDListCount] = sentenceIDList[j];
            }
          }
        }
      }

      Sort.quickSortInt(igSentimentIDList, 1, igSentimentIDListCount);
      for (int i = 1; i <= igParagraphCount; i++) {
        int[] sentenceIDList = paragraph[i].getSentimentIDList();
        if (sentenceIDList != null) {
          for (int j = 0; j < sentenceIDList.length; j++) {
            if (sentenceIDList[j] != 0) {
              igSentimentIDParagraphCount[Sort.i_FindIntPositionInSortedArray(sentenceIDList[j], igSentimentIDList, 1, igSentimentIDListCount)]++;
            }
          }
        }
      }

    }
    bSentimentIDListMade = true;
  }

  /**
   * 进行多次十折交叉验证。
   *
   * @param iMinImprovement     最小优化量
   * @param bUseTotalDifference 是否使用总差值
   * @param iReplications       复制次数
   * @param iMultiOptimisations 多重优化
   * @param sWriter             输出缓冲区
   * @param wTermStrengthWriter 术语强度写入缓冲区
   */
  private void run10FoldCrossValidationMultipleTimes(int iMinImprovement, boolean bUseTotalDifference, int iReplications, int iMultiOptimisations, BufferedWriter sWriter, BufferedWriter wTermStrengthWriter) {
    for (int i = 1; i <= iReplications; i++) {
      run10FoldCrossValidationOnce(iMinImprovement, bUseTotalDifference, iMultiOptimisations, sWriter, wTermStrengthWriter);
    }

    System.out.println("Set of " + iReplications + " 10-fold cross validations finished");
  }

  /**
   * 进行多次十折交叉验证（将数据集分为十份，九份为训练集，一份为验证集）。
   *
   * @param iMinImprovement     最小提升数目
   * @param bUseTotalDifference 是否记录总变化量
   * @param iReplications       复制数目
   * @param iMultiOptimisations 多重优化
   * @param sOutFileName        输出文件路径
   */
  public void run10FoldCrossValidationMultipleTimes(int iMinImprovement, boolean bUseTotalDifference, int iReplications, int iMultiOptimisations, String sOutFileName) {
    try {
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutFileName));
      BufferedWriter wTermStrengthWriter = new BufferedWriter(new FileWriter(FileOps.s_ChopFileNameExtension(sOutFileName) + "_termStrVars.txt"));
      options.printClassificationOptionsHeadings(wWriter);
      writeClassificationStatsHeadings(wWriter);
      options.printClassificationOptionsHeadings(wTermStrengthWriter);
      resources.sentimentWords.printSentimentTermsInSingleHeaderRow(wTermStrengthWriter);
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wWriter, wTermStrengthWriter);
      wWriter.close();
      wTermStrengthWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 将所有行进行分类并且记录 ID。
   *
   * @param sInputFile  输入文件路径
   * @param iTextCol    文本列数
   * @param iIDCol      ID 列数
   * @param sOutputFile 输出文件路径
   */
  public void classifyAllLinesAndRecordWithID(String sInputFile, int iTextCol, int iIDCol, String sOutputFile) {
    int iPos = 0;
    int iNeg = 0;
    int iTrinary = -3;
    int iScale = -10;
    int iCount1 = 0;
    String sLine = "";
    try {
      BufferedReader rReader = new BufferedReader(new FileReader(sInputFile));
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutputFile));
      while (rReader.ready()) {
        sLine = rReader.readLine();
        iCount1++;
        if (!sLine.equals("")) {
          String[] sData = sLine.split("\t");
          if (sData.length > iTextCol && sData.length > iIDCol) {
            Paragraph paragraph = new Paragraph();
            paragraph.setParagraph(sData[iTextCol], resources, options);
            if (options.bgTrinaryMode) {
              iTrinary = paragraph.getParagraphTrinarySentiment();
              wWriter.write(sData[iIDCol] + "\t" + iTrinary + "\n");
            } else if (options.bgScaleMode) {
              iScale = paragraph.getParagraphScaleSentiment();
              wWriter.write(sData[iIDCol] + "\t" + iScale + "\n");
            } else {
              iPos = paragraph.getParagraphPositiveSentiment();
              iNeg = paragraph.getParagraphNegativeSentiment();
              wWriter.write(sData[iIDCol] + "\t" + iPos + "\t" + iNeg + "\n");
            }
          }
        }
      }
      Thread.sleep(10L);
      // 进程睡眠
      if (rReader.ready()) {
        System.out.println("Reader ready again after pause!");
      }
      int character;
      if ((character = rReader.read()) != -1) {
        System.out.println("Reader returns char after reader.read() false! " + character);
      }
      rReader.close();
      wWriter.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find input file: " + sInputFile);
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("Error reading or writing from file: " + sInputFile);
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("Error reading from or writing to file: " + sInputFile);
      e.printStackTrace();
    }
    System.out.println("Processed " + iCount1 + " lines from file: " + sInputFile + ". Last line was:\n" + sLine);
  }

  /**
   * 将输入文件的所有行进行注释。
   *
   * @param sInputFile 输入文件路径
   * @param iTextCol   文本列数
   */
  public void annotateAllLinesInInputFile(String sInputFile, int iTextCol) {
    int iPos = 0;
    int iNeg = 0;
    int iTrinary = -3;
    int iScale = -10;
    String sTempFile = sInputFile + "_temp";
    try {
      BufferedReader rReader = new BufferedReader(new FileReader(sInputFile));
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sTempFile));
      while (rReader.ready()) {
        String sLine = rReader.readLine();
        if (!sLine.equals("")) {
          String[] sData = sLine.split("\t");
          if (sData.length > iTextCol) {
            // TODO
            Paragraph paragraph = new Paragraph();
            paragraph.setParagraph(sData[iTextCol], resources, options);
            if (options.bgTrinaryMode) {
              iTrinary = paragraph.getParagraphTrinarySentiment();
              wWriter.write(sLine + "\t" + iTrinary + "\n");
            } else if (options.bgScaleMode) {
              iScale = paragraph.getParagraphScaleSentiment();
              wWriter.write(sLine + "\t" + iScale + "\n");
            } else {
              iPos = paragraph.getParagraphPositiveSentiment();
              iNeg = paragraph.getParagraphNegativeSentiment();
              wWriter.write(sLine + "\t" + iPos + "\t" + iNeg + "\n");
            }
          } else {
            wWriter.write(sLine + "\n");
          }
        }
      }
      rReader.close();
      wWriter.close();
      File original = new File(sInputFile);
      original.delete();
      File newFile = new File(sTempFile);
      newFile.renameTo(original);
    } catch (FileNotFoundException e) {
      System.out.println("Could not find input file: " + sInputFile);
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("Error reading or writing from file: " + sInputFile);
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("Error reading from or writing to file: " + sInputFile);
      e.printStackTrace();
    }
  }

  /**
   * 将输入文件中的所有行进行分类。
   *
   * @param sInputFile  输入文件路径
   * @param iTextCol    文本列数
   * @param sOutputFile 输出文件路径
   */
  public void classifyAllLinesInInputFile(String sInputFile, int iTextCol, String sOutputFile) {
    int iPos = 0; // 积极情绪强度
    int iNeg = 0; // 消极情绪强度
    int iTrinary = -3; // Trinary情绪分类
    int iScale = -10; // scale value
    int iFileTrinary = -2; // 文件的Trinary情绪分类
    int iFileScale = -9; // 文件的 scale value
    int iClassified = 0; // 是否被分类
    int iCorrectPosCount = 0; // 正确的积极情绪数目
    int iCorrectNegCount = 0; // 正确的消极情绪数目
    int iCorrectTrinaryCount = 0; // 正确的Trinary分类数目
    int iCorrectScaleCount = 0; // 正确的scale value 数目
    int iPosAbsDiff = 0; // 积极情绪绝对差
    int iNegAbsDiff = 0; // 消极情绪绝对差
    int[][] confusion = { // 一个二维的混淆矩阵 new int[3][3]
            new int[3], new int[3], new int[3]
    };
    int maxClassifyForCorrelation = 20000; // 最大相关分类
    int[] iPosClassCorr = new int[maxClassifyForCorrelation]; // 正确积极分类
    int[] iNegClassCorr = new int[maxClassifyForCorrelation];
    int[] iPosClassPred = new int[maxClassifyForCorrelation]; // 预测积极分类
    int[] iNegClassPred = new int[maxClassifyForCorrelation];
    int[] iScaleClassCorr = new int[maxClassifyForCorrelation];
    int[] iScaleClassPred = new int[maxClassifyForCorrelation];
    String sRationale = ""; // 分类理由
    String sOutput = "";
    try {
      BufferedReader rReader;
      BufferedWriter wWriter;
      if (options.bgForceUTF8) {
        wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sOutputFile), StandardCharsets.UTF_8));
        rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sInputFile), StandardCharsets.UTF_8));
      } else {
        wWriter = new BufferedWriter(new FileWriter(sOutputFile));
        rReader = new BufferedReader(new FileReader(sInputFile));
      }
      if (options.bgTrinaryMode || options.bgScaleMode) {
        wWriter.write("Overall\tText");
      } else if (options.bgTensiStrength) {
        wWriter.write("Relax\tStress\tText");
      } else {
        wWriter.write("Positive\tNegative\tText");
      }
      if (options.bgExplainClassification) {
        wWriter.write("\tExplanation\n");
      } else {
        wWriter.write("\n");
      }
      while (rReader.ready()) {
        // 按行进行分类
        String sLine = rReader.readLine();
        if (!sLine.equals("")) {
          int iTabPos = sLine.lastIndexOf("\t");
          int iFilePos = 0;
          int iFileNeg = 0;
          if (iTabPos >= 0) {
            String[] sData = sLine.split("\t");
            if (sData.length > 1) {
              if (iTextCol > -1) {
                wWriter.write(sLine + "\t");
                if (iTextCol < sData.length) {
                  sLine = sData[iTextCol];
                }
              } else if (options.bgTrinaryMode) {
                iFileTrinary = -2;
                try {
                  iFileTrinary = Integer.parseInt(sData[0].trim()); // 以制表符分割的第一个字符串
                  if (iFileTrinary > 1 || iFileTrinary < -1) {
                    System.out.println("Invalid trinary sentiment " + iFileTrinary + " (expected -1,0,1) at line: " + sLine);
                    iFileTrinary = 0;
                  }
                } catch (NumberFormatException numberformatexception) {
                  numberformatexception.printStackTrace();
                }
              } else if (options.bgScaleMode) {
                iFileScale = -9;
                try {
                  iFileScale = Integer.parseInt(sData[0].trim());
                  if (iFileScale > 4 || iFileScale < -4) {
                    System.out.println("Invalid overall sentiment " + iFileScale + " (expected -4 to +4) at line: " + sLine);
                    iFileScale = 0;
                  }
                } catch (NumberFormatException numberformatexception1) {
                  numberformatexception1.printStackTrace();
                }
              } else {
                try {
                  iFilePos = Integer.parseInt(sData[0].trim());
                  iFileNeg = Integer.parseInt(sData[1].trim());
                  if (iFileNeg < 0) {
                    iFileNeg = -iFileNeg; // 消极强度为负表示其绝对值的强度
                  }
                } catch (NumberFormatException numberformatexception2) {
                  numberformatexception2.printStackTrace();
                }
              }
            }
            sLine = sLine.substring(iTabPos + 1);
          }
          // 读取段落
          Paragraph paragraph = new Paragraph();
          paragraph.setParagraph(sLine, resources, options);
          if (options.bgTrinaryMode) {
            iTrinary = paragraph.getParagraphTrinarySentiment();
            if (options.bgExplainClassification) {
              sRationale = "\t" + paragraph.getClassificationRationale();
            }
            sOutput = iTrinary + "\t" + sLine + sRationale + "\n";
          } else if (options.bgScaleMode) {
            iScale = paragraph.getParagraphScaleSentiment();
            if (options.bgExplainClassification) {
              sRationale = "\t" + paragraph.getClassificationRationale();
            }
            sOutput = iScale + "\t" + sLine + sRationale + "\n";
          } else {
            iPos = paragraph.getParagraphPositiveSentiment();
            iNeg = paragraph.getParagraphNegativeSentiment();
            if (options.bgExplainClassification) {
              sRationale = "\t" + paragraph.getClassificationRationale();
            }
            sOutput = iPos + "\t" + iNeg + "\t" + sLine + sRationale + "\n";
          }
          wWriter.write(sOutput);
          if (options.bgTrinaryMode) {
            // 计算二维大小为三的混淆矩阵
            if (iFileTrinary > -2 && iFileTrinary < 2 && iTrinary > -2 && iTrinary < 2) {
              iClassified++;
              if (iFileTrinary == iTrinary) {
                iCorrectTrinaryCount++;
              }
              confusion[iTrinary + 1][iFileTrinary + 1]++;
            }
          } else if (options.bgScaleMode) {
            // 计算scale value
            if (iFileScale > -9) {
              iClassified++;
              if (iFileScale == iScale) {
                iCorrectScaleCount++;
              }
              if (iClassified < maxClassifyForCorrelation) {
                iScaleClassCorr[iClassified] = iFileScale;
              }
              iScaleClassPred[iClassified] = iScale;
            }
          } else if (iFileNeg != 0) {
            // 计算Pos Neg
            iClassified++;
            if (iPos == iFilePos) {
              iCorrectPosCount++;
            }
            iPosAbsDiff += Math.abs(iPos - iFilePos);
            if (iClassified < maxClassifyForCorrelation) {
              iPosClassCorr[iClassified] = iFilePos;
            }
            iPosClassPred[iClassified] = iPos;
            if (iNeg == -iFileNeg) {
              iCorrectNegCount++;
            }
            iNegAbsDiff += Math.abs(iNeg + iFileNeg);
            if (iClassified < maxClassifyForCorrelation) {
              iNegClassCorr[iClassified] = iFileNeg;
            }
            iNegClassPred[iClassified] = iNeg;
          }
        }
      }
      rReader.close();
      wWriter.close();
      if (iClassified > 0) {
        // 分类完成，输出分类结果
        if (options.bgTrinaryMode) {
          System.out.println("Trinary correct: " + iCorrectTrinaryCount + " (" + ((float) iCorrectTrinaryCount / (float) iClassified) * 100F + "%).");
          System.out.println("Correct -> -1   0   1");
          System.out.println("Est = -1   " + confusion[0][0] + " " + confusion[0][1] + " " + confusion[0][2]);
          System.out.println("Est =  0   " + confusion[1][0] + " " + confusion[1][1] + " " + confusion[1][2]);
          System.out.println("Est =  1   " + confusion[2][0] + " " + confusion[2][1] + " " + confusion[2][2]);
        } else if (options.bgScaleMode) {
          System.out.println("Scale correct: " + iCorrectScaleCount + " (" + ((float) iCorrectScaleCount / (float) iClassified) * 100F + "%) out of " + iClassified);
          System.out.println("  Correlation: " + ClassificationStatistics.correlation(iScaleClassCorr, iScaleClassPred, iClassified));
        } else {
          System.out.print(options.sgProgramPos + " correct: " + iCorrectPosCount + " (" + ((float) iCorrectPosCount / (float) iClassified) * 100F + "%).");
          System.out.println(" Mean abs diff: " + (float) iPosAbsDiff / (float) iClassified);
          if (iClassified < maxClassifyForCorrelation) {
            System.out.println(" Correlation: " + ClassificationStatistics.correlationAbs(iPosClassCorr, iPosClassPred, iClassified));
            int corrWithin1 = ClassificationStatistics.accuracyWithin1(iPosClassCorr, iPosClassPred, iClassified, false);
            System.out.println(" Correct +/- 1: " + corrWithin1 + " (" + (float) (100 * corrWithin1) / (float) iClassified + "%)");
          }
          System.out.print(options.sgProgramNeg + " correct: " + iCorrectNegCount + " (" + ((float) iCorrectNegCount / (float) iClassified) * 100F + "%).");
          System.out.println(" Mean abs diff: " + (float) iNegAbsDiff / (float) iClassified);
          if (iClassified < maxClassifyForCorrelation) {
            System.out.println(" Correlation: " + ClassificationStatistics.correlationAbs(iNegClassCorr, iNegClassPred, iClassified));
            int corrWithin1 = ClassificationStatistics.accuracyWithin1(iNegClassCorr, iNegClassPred, iClassified, true);
            System.out.println(" Correct +/- 1: " + corrWithin1 + " (" + (float) (100 * corrWithin1) / (float) iClassified + "%)");
          }
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("Could not find input file: " + sInputFile);
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("Error reading from input file: " + sInputFile + " or writing to output file " + sOutputFile);
      e.printStackTrace();
    }
  }

  /**
   * 写入分类统计头部。
   *
   * @param w 输出缓存
   * @throws IOException 写入文件错误
   */
  private void writeClassificationStatsHeadings(BufferedWriter w)
          throws IOException {
    String sPosOrScale;
    if (options.bgScaleMode) {
      sPosOrScale = "ScaleCorrel";
    } else {
      sPosOrScale = "PosCorrel";
    }
    w.write("\tPosCorrect\tiPosCorrect/Total\tNegCorrect\tNegCorrect/Total\tPosWithin1\tPosWithin1/Total\tNegWithin1\tNegWithin1/Total\t" + sPosOrScale + "\tNegCorrel" + "\tPosMPE\tNegMPE\tPosMPEnoDiv\tNegMPEnoDiv" + "\tTrinaryOrScaleCorrect\tTrinaryOrScaleCorrect/TotalClassified" + "\tTrinaryOrScaleCorrectWithin1\tTrinaryOrScaleCorrectWithin1/TotalClassified" + "\test-1corr-1\test-1corr0\test-1corr1" + "\test0corr-1\test0corr0\test0corr1" + "\test1corr-1\test1corr0\test1corr1" + "\tTotalClassified\n");
  }

  /**
   * 进行所有的选项变种的十折交叉验证。
   *
   * @param iMinImprovement     最小优化量
   * @param bUseTotalDifference 是否打印总变化量
   * @param iReplications       复制量
   * @param iMultiOptimisations 多重优化
   * @param sOutFileName        输出文件路径
   */
  public void run10FoldCrossValidationForAllOptionVariations(int iMinImprovement, boolean bUseTotalDifference, int iReplications, int iMultiOptimisations, String sOutFileName) {
    try {
      // 验证参数初始化
      if (igPosClass == null || igPosClass.length < igPosCorrect.length) {
        igPosClass = new int[igParagraphCount + 1];
        igNegClass = new int[igParagraphCount + 1];
        igTrinaryClass = new int[igParagraphCount + 1];
      }
      BufferedWriter wResultsWriter = new BufferedWriter(new FileWriter(sOutFileName));
      BufferedWriter wTermStrengthWriter = new BufferedWriter(new FileWriter(FileOps.s_ChopFileNameExtension(sOutFileName) + "_termStrVars.txt"));
      options.printClassificationOptionsHeadings(wResultsWriter);
      // 打印验证文件头部
      writeClassificationStatsHeadings(wResultsWriter);
      options.printClassificationOptionsHeadings(wTermStrengthWriter);
      resources.sentimentWords.printSentimentTermsInSingleHeaderRow(wTermStrengthWriter);
      System.out.println("About to start classifications for 20 different option variations");
      if (options.bgTrinaryMode) {
        ClassificationStatistics.baselineAccuracyMakeLargestClassPrediction(igTrinaryCorrect, igTrinaryClass, igParagraphCount, false);
      } else if (options.bgScaleMode) {
        ClassificationStatistics.baselineAccuracyMakeLargestClassPrediction(igScaleCorrect, igScaleClass, igParagraphCount, false);
      } else {
        ClassificationStatistics.baselineAccuracyMakeLargestClassPrediction(igPosCorrect, igPosClass, igParagraphCount, false);
        ClassificationStatistics.baselineAccuracyMakeLargestClassPrediction(igNegCorrect, igNegClass, igParagraphCount, true);
      }
      options.printBlankClassificationOptions(wResultsWriter);
      if (options.bgTrinaryMode) {
        printClassificationResultsRow(igPosClass, igNegClass, igTrinaryClass, wResultsWriter);
      } else {
        printClassificationResultsRow(igPosClass, igNegClass, igScaleClass, wResultsWriter);
      }
      options.printClassificationOptions(wResultsWriter, igParagraphCount, bUseTotalDifference, iMultiOptimisations);
      calculateCorpusSentimentScores();
      if (options.bgTrinaryMode) {
        printClassificationResultsRow(igPosClass, igNegClass, igTrinaryClass, wResultsWriter);
      } else {
        printClassificationResultsRow(igPosClass, igNegClass, igScaleClass, wResultsWriter);
      }
      options.printBlankClassificationOptions(wTermStrengthWriter);
      resources.sentimentWords.printSentimentValuesInSingleRow(wTermStrengthWriter);
      // 从这往下是遍历所有的options的成员变量的可能取值，每次修改一次取值，进行一次十折交叉验证，具体变量取值详见ClassificationOptions类
      // time : 1
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.igEmotionParagraphCombineMethod = 1 - options.igEmotionParagraphCombineMethod;
      // time : 2
      // changed values : igEmotionParagraphCombineMethod
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.igEmotionParagraphCombineMethod = 1 - options.igEmotionParagraphCombineMethod; // igEmotionParagraphCombinedMethod return initial value
      options.igEmotionSentenceCombineMethod = 1 - options.igEmotionSentenceCombineMethod;
      // time : 3
      // changed values : igEmotionSentenceCombineMethod...
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.igEmotionSentenceCombineMethod = 1 - options.igEmotionSentenceCombineMethod;
      options.bgReduceNegativeEmotionInQuestionSentences = !options.bgReduceNegativeEmotionInQuestionSentences;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgReduceNegativeEmotionInQuestionSentences = !options.bgReduceNegativeEmotionInQuestionSentences;
      options.bgMissCountsAsPlus2 = !options.bgMissCountsAsPlus2;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgMissCountsAsPlus2 = !options.bgMissCountsAsPlus2;
      options.bgYouOrYourIsPlus2UnlessSentenceNegative = !options.bgYouOrYourIsPlus2UnlessSentenceNegative;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgYouOrYourIsPlus2UnlessSentenceNegative = !options.bgYouOrYourIsPlus2UnlessSentenceNegative;
      options.bgExclamationInNeutralSentenceCountsAsPlus2 = !options.bgExclamationInNeutralSentenceCountsAsPlus2;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgExclamationInNeutralSentenceCountsAsPlus2 = !options.bgExclamationInNeutralSentenceCountsAsPlus2;
      options.bgUseIdiomLookupTable = !options.bgUseIdiomLookupTable;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgUseIdiomLookupTable = !options.bgUseIdiomLookupTable;
      final int iTemp = options.igMoodToInterpretNeutralEmphasis;
      options.igMoodToInterpretNeutralEmphasis = -options.igMoodToInterpretNeutralEmphasis;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.igMoodToInterpretNeutralEmphasis = 0;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.igMoodToInterpretNeutralEmphasis = iTemp;
      System.out.println("About to start 10th option variation classification");
      options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = !options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = !options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion;
      options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = !options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = !options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion;
      options.bgIgnoreBoosterWordsAfterNegatives = !options.bgIgnoreBoosterWordsAfterNegatives;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgIgnoreBoosterWordsAfterNegatives = !options.bgIgnoreBoosterWordsAfterNegatives;
      options.bgMultipleLettersBoostSentiment = !options.bgMultipleLettersBoostSentiment;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgMultipleLettersBoostSentiment = !options.bgMultipleLettersBoostSentiment;
      options.bgBoosterWordsChangeEmotion = !options.bgBoosterWordsChangeEmotion;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgBoosterWordsChangeEmotion = !options.bgBoosterWordsChangeEmotion;
      if (options.bgNegatingWordsFlipEmotion) {
        options.bgNegatingWordsFlipEmotion = !options.bgNegatingWordsFlipEmotion;
        run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
        options.bgNegatingWordsFlipEmotion = !options.bgNegatingWordsFlipEmotion;
      } else {
        options.bgNegatingPositiveFlipsEmotion = !options.bgNegatingPositiveFlipsEmotion;
        run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
        options.bgNegatingPositiveFlipsEmotion = !options.bgNegatingPositiveFlipsEmotion;
        options.bgNegatingNegativeNeutralisesEmotion = !options.bgNegatingNegativeNeutralisesEmotion;
        run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
        options.bgNegatingNegativeNeutralisesEmotion = !options.bgNegatingNegativeNeutralisesEmotion;
      }
      options.bgCorrectSpellingsWithRepeatedLetter = !options.bgCorrectSpellingsWithRepeatedLetter;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgCorrectSpellingsWithRepeatedLetter = !options.bgCorrectSpellingsWithRepeatedLetter;
      options.bgUseEmoticons = !options.bgUseEmoticons;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgUseEmoticons = !options.bgUseEmoticons;
      options.bgCapitalsBoostTermSentiment = !options.bgCapitalsBoostTermSentiment;
      run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      options.bgCapitalsBoostTermSentiment = !options.bgCapitalsBoostTermSentiment;
      if (iMinImprovement > 1) {
        run10FoldCrossValidationMultipleTimes(iMinImprovement - 1, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      }
      run10FoldCrossValidationMultipleTimes(iMinImprovement + 1, bUseTotalDifference, iReplications, iMultiOptimisations, wResultsWriter, wTermStrengthWriter);
      wResultsWriter.close();
      wTermStrengthWriter.close();
      SummariseMultiple10FoldValidations(sOutFileName, sOutFileName + "_sum.txt");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 进行一次十折交叉验证。<br/>
   * 将数据集分为十份，九份为训练集，一份为验证集。
   *
   * @param iMinImprovement     最小优化量
   * @param bUseTotalDifference 是否使用总差值
   * @param iMultiOptimisations 多重优化
   * @param wWriter             输出缓冲
   * @param wTermStrengthWriter 术语强度输出缓冲
   */
  private void run10FoldCrossValidationOnce(int iMinImprovement, boolean bUseTotalDifference, int iMultiOptimisations, BufferedWriter wWriter, BufferedWriter wTermStrengthWriter) {
    int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
    int[] iParagraphRand = new int[igParagraphCount + 1];
    int[] iPosClassAll = new int[igParagraphCount + 1];
    int[] iNegClassAll = new int[igParagraphCount + 1];
    int[] iTrinaryOrScaleClassAll = new int[igParagraphCount + 1];
    int iTotalClassified = 0;
    Sort.makeRandomOrderList(iParagraphRand);
    int[] iOriginalSentimentStrengths = new int[iTotalSentimentWords + 1];
    for (int i = 1; i < iTotalSentimentWords; i++) {
      iOriginalSentimentStrengths[i] = resources.sentimentWords.getSentiment(i);
    }

    for (int iFold = 1; iFold <= 10; iFold++) {
      selectDecileAsSubcorpus(iParagraphRand, iFold, true);
      reCalculateCorpusSentimentScores();
      optimiseDictionaryWeightingsForCorpusMultipleTimes(iMinImprovement, bUseTotalDifference, iMultiOptimisations);
      options.printClassificationOptions(wTermStrengthWriter, iMinImprovement, bUseTotalDifference, iMultiOptimisations);
      resources.sentimentWords.printSentimentValuesInSingleRow(wTermStrengthWriter);
      selectDecileAsSubcorpus(iParagraphRand, iFold, false);
      reCalculateCorpusSentimentScores();
      for (int i = 1; i <= igParagraphCount; i++) {
        if (bgSupcorpusMember[i]) {
          iPosClassAll[i] = igPosClass[i];
          iNegClassAll[i] = igNegClass[i];
          if (options.bgTrinaryMode) {
            iTrinaryOrScaleClassAll[i] = igTrinaryClass[i];
          } else {
            iTrinaryOrScaleClassAll[i] = igScaleClass[i];
          }
        }
      }

      iTotalClassified += igSupcorpusMemberCount;
      for (int i = 1; i < iTotalSentimentWords; i++) {
        resources.sentimentWords.setSentiment(i, iOriginalSentimentStrengths[i]);
      }

    }

    useWholeCorpusNotSubcorpus();
    options.printClassificationOptions(wWriter, iMinImprovement, bUseTotalDifference, iMultiOptimisations);
    printClassificationResultsRow(iPosClassAll, iNegClassAll, iTrinaryOrScaleClassAll, wWriter);
  }

  /**
   * 打印一行分类结果。
   *
   * @param iPosClassAll            所有积极情绪预测
   * @param iNegClassAll            所有消极情绪预测
   * @param iTrinaryOrScaleClassAll 所有的Trinary或Scale预测
   * @param wWriter                 输出缓冲
   * @return 是否打印成功
   */
  private boolean printClassificationResultsRow(int[] iPosClassAll, int[] iNegClassAll, int[] iTrinaryOrScaleClassAll, BufferedWriter wWriter) {
    int iPosCorrect = -1;
    int iNegCorrect = -1;
    int iPosWithin1 = -1;
    int iNegWithin1 = -1;
    int iTrinaryCorrect = -1;
    int iTrinaryCorrectWithin1 = -1;
    double fPosCorrectPoportion = -1D;
    double fNegCorrectPoportion = -1D;
    double fPosWithin1Poportion = -1D;
    double fNegWithin1Poportion = -1D;
    double fTrinaryCorrectPoportion = -1D;
    double fTrinaryCorrectWithin1Poportion = -1D;
    double fPosOrScaleCorr = 9999D;
    double fNegCorr = 9999D;
    double fPosMPE = 9999D;
    double fNegMPE = 9999D;
    double fPosMPEnoDiv = 9999D;
    double fNegMPEnoDiv = 9999D;
    int[][] estCorr = {// 预测正确矩阵
           new int[3], new int[3], new int[3]
    };
    try {
      if (options.bgTrinaryMode) {
        iTrinaryCorrect = ClassificationStatistics.accuracy(igTrinaryCorrect, iTrinaryOrScaleClassAll, igParagraphCount, false);
        iTrinaryCorrectWithin1 = ClassificationStatistics.accuracyWithin1(igTrinaryCorrect, iTrinaryOrScaleClassAll, igParagraphCount, false);
        fTrinaryCorrectPoportion = (float) iTrinaryCorrect / (float) igParagraphCount;
        fTrinaryCorrectWithin1Poportion = (float) iTrinaryCorrectWithin1 / (float) igParagraphCount;
        ClassificationStatistics.TrinaryOrBinaryConfusionTable(iTrinaryOrScaleClassAll, igTrinaryCorrect, igParagraphCount, estCorr);
      } else if (options.bgScaleMode) {
        iTrinaryCorrect = ClassificationStatistics.accuracy(igScaleCorrect, iTrinaryOrScaleClassAll, igParagraphCount, false);
        iTrinaryCorrectWithin1 = ClassificationStatistics.accuracyWithin1(igScaleCorrect, iTrinaryOrScaleClassAll, igParagraphCount, false);
        fTrinaryCorrectPoportion = (float) iTrinaryCorrect / (float) igParagraphCount;
        fTrinaryCorrectWithin1Poportion = (float) iTrinaryCorrectWithin1 / (float) igParagraphCount;
        fPosOrScaleCorr = ClassificationStatistics.correlation(igScaleCorrect, iTrinaryOrScaleClassAll, igParagraphCount);
      } else {
        iPosCorrect = ClassificationStatistics.accuracy(igPosCorrect, iPosClassAll, igParagraphCount, false);
        iNegCorrect = ClassificationStatistics.accuracy(igNegCorrect, iNegClassAll, igParagraphCount, true);
        iPosWithin1 = ClassificationStatistics.accuracyWithin1(igPosCorrect, iPosClassAll, igParagraphCount, false);
        iNegWithin1 = ClassificationStatistics.accuracyWithin1(igNegCorrect, iNegClassAll, igParagraphCount, true);
        fPosOrScaleCorr = ClassificationStatistics.correlationAbs(igPosCorrect, iPosClassAll, igParagraphCount);
        fNegCorr = ClassificationStatistics.correlationAbs(igNegCorrect, iNegClassAll, igParagraphCount);
        fPosMPE = ClassificationStatistics.absoluteMeanPercentageError(igPosCorrect, iPosClassAll, igParagraphCount, false);
        fNegMPE = ClassificationStatistics.absoluteMeanPercentageError(igNegCorrect, iNegClassAll, igParagraphCount, true);
        fPosMPEnoDiv = ClassificationStatistics.absoluteMeanPercentageErrorNoDivision(igPosCorrect, iPosClassAll, igParagraphCount, false);
        fNegMPEnoDiv = ClassificationStatistics.absoluteMeanPercentageErrorNoDivision(igNegCorrect, iNegClassAll, igParagraphCount, true);
        fPosCorrectPoportion = (float) iPosCorrect / (float) igParagraphCount;
        fNegCorrectPoportion = (float) iNegCorrect / (float) igParagraphCount;
        fPosWithin1Poportion = (float) iPosWithin1 / (float) igParagraphCount;
        fNegWithin1Poportion = (float) iNegWithin1 / (float) igParagraphCount;
      }
      wWriter.write("\t" + iPosCorrect + "\t" + fPosCorrectPoportion + "\t" + iNegCorrect + "\t" + fNegCorrectPoportion + "\t" + iPosWithin1 + "\t" + fPosWithin1Poportion + "\t" + iNegWithin1 + "\t" + fNegWithin1Poportion + "\t" + fPosOrScaleCorr + "\t" + fNegCorr + "\t" + fPosMPE + "\t" + fNegMPE + "\t" + fPosMPEnoDiv + "\t" + fNegMPEnoDiv + "\t" + iTrinaryCorrect + "\t" + fTrinaryCorrectPoportion + "\t" + iTrinaryCorrectWithin1 + "\t" + fTrinaryCorrectWithin1Poportion + "\t" + estCorr[0][0] + "\t" + estCorr[0][1] + "\t" + estCorr[0][2] + "\t" + estCorr[1][0] + "\t" + estCorr[1][1] + "\t" + estCorr[1][2] + "\t" + estCorr[2][0] + "\t" + estCorr[2][1] + "\t" + estCorr[2][2] + "\t" + igParagraphCount + "\n");
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 选取十等分点作为子语料库。
   *
   * @param iParagraphRand 段落随机表
   * @param iDecile        十等分点序号
   * @param bInvert        是否进行转化
   */
  private void selectDecileAsSubcorpus(int[] iParagraphRand, int iDecile, boolean bInvert) {
    if (igParagraphCount == 0) {
      return;
    }
    int iMin = (int) (((float) igParagraphCount / 10F) * (float) (iDecile - 1)) + 1;
    int iMax = (int) (((float) igParagraphCount / 10F) * (float) iDecile);
    if (iDecile == 10) {
      iMax = igParagraphCount;
    }
    if (iDecile == 0) {
      iMin = 0;
    }
    igSupcorpusMemberCount = 0;
    for (int i = 1; i <= igParagraphCount; i++) {
      if (i >= iMin && i <= iMax) {
        bgSupcorpusMember[iParagraphRand[i]] = !bInvert;
        if (!bInvert) {
          igSupcorpusMemberCount++;
        }
      } else {
        bgSupcorpusMember[iParagraphRand[i]] = bInvert;
        if (bInvert) {
          igSupcorpusMemberCount++;
        }
      }
    }

  }

  /**
   * 多次进行语料库的字典权重优化。
   *
   * @param iMinImprovement     最小优化量
   * @param bUseTotalDifference 是否记录总变化量
   * @param iOptimisationTotal  总优化量
   */
  public void optimiseDictionaryWeightingsForCorpusMultipleTimes(int iMinImprovement, boolean bUseTotalDifference, int iOptimisationTotal) {
    if (iOptimisationTotal < 1) {
      return;
    }
    if (iOptimisationTotal == 1) {
      optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
      return;
    }
    int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
    int[] iOriginalSentimentStrengths = new int[iTotalSentimentWords + 1];
    for (int j = 1; j <= iTotalSentimentWords; j++) {
      iOriginalSentimentStrengths[j] = resources.sentimentWords.getSentiment(j);
    }

    int[] iTotalWeight = new int[iTotalSentimentWords + 1];
    for (int j = 1; j <= iTotalSentimentWords; j++) {
      iTotalWeight[j] = 0;
    }

    for (int i = 0; i < iOptimisationTotal; i++) {
      optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
      for (int j = 1; j <= iTotalSentimentWords; j++) {
        iTotalWeight[j] += resources.sentimentWords.getSentiment(j);
      }

      for (int j = 1; j <= iTotalSentimentWords; j++) {
        resources.sentimentWords.setSentiment(j, iOriginalSentimentStrengths[j]);
      }

    }

    for (int j = 1; j <= iTotalSentimentWords; j++) {
      resources.sentimentWords.setSentiment(j, (int) ((double) ((float) iTotalWeight[j] / (float) iOptimisationTotal) + 0.5D));
    }
    optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
  }

  /**
   * 单次进行语料库的字典权重优化。
   *
   * @param iMinImprovement     最小优化量
   * @param bUseTotalDifference 是否记录总变化量
   */
  public void optimiseDictionaryWeightingsForCorpus(int iMinImprovement, boolean bUseTotalDifference) {
    if (options.bgTrinaryMode) {
      optimiseDictionaryWeightingsForCorpusTrinaryOrBinary(iMinImprovement);
    } else if (options.bgScaleMode) {
      optimiseDictionaryWeightingsForCorpusScale(iMinImprovement);
    } else {
      optimiseDictionaryWeightingsForCorpusPosNeg(iMinImprovement, bUseTotalDifference);
    }
  }

  /**
   * 进行语料库的 Scale 值的权重优化。
   *
   * @param iMinImprovement 最小优化量
   */
  public void optimiseDictionaryWeightingsForCorpusScale(int iMinImprovement) {
    boolean bFullListChanges = true;
    int iLastScaleNumberCorrect = getClassificationScaleNumberCorrect();
    int iNewScaleNumberCorrect = 0;
    int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
    int[] iWordRand = new int[iTotalSentimentWords + 1];
    while (bFullListChanges) {
      Sort.makeRandomOrderList(iWordRand);
      bFullListChanges = false;
      for (int i = 1; i <= iTotalSentimentWords; i++) {
        int iOldTermSentimentStrength = resources.sentimentWords.getSentiment(iWordRand[i]);
        boolean bCurrentIDChange = false;
        int iAddOneImprovement = 0;
        int iSubtractOneImprovement = 0;
        if (iOldTermSentimentStrength < 4) {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldTermSentimentStrength + 1);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
          iNewScaleNumberCorrect = getClassificationScaleNumberCorrect();
          iAddOneImprovement = iNewScaleNumberCorrect - iLastScaleNumberCorrect;
          if (iAddOneImprovement >= iMinImprovement) {
            bCurrentIDChange = true;
            iLastScaleNumberCorrect += iAddOneImprovement;
          }
        }
        if (iOldTermSentimentStrength > -4 && !bCurrentIDChange) {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldTermSentimentStrength - 1);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
          iNewScaleNumberCorrect = getClassificationScaleNumberCorrect();
          iSubtractOneImprovement = iNewScaleNumberCorrect - iLastScaleNumberCorrect;
          if (iSubtractOneImprovement >= iMinImprovement) {
            bCurrentIDChange = true;
            iLastScaleNumberCorrect += iSubtractOneImprovement;
          }
        }
        if (bCurrentIDChange) {
          bFullListChanges = true;
        } else {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldTermSentimentStrength);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
        }
      }

    }
  }

  /**
   * 进行语料库的 Trinary 值或 Binary 值的权重优化。
   *
   * @param iMinImprovement 最小优化量
   */
  public void optimiseDictionaryWeightingsForCorpusTrinaryOrBinary(int iMinImprovement) {
    boolean bFullListChanges = true;
    int iLastTrinaryCorrect = getClassificationTrinaryNumberCorrect();
    int iNewTrinary = 0;
    int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
    int[] iWordRand = new int[iTotalSentimentWords + 1];
    while (bFullListChanges) {
      Sort.makeRandomOrderList(iWordRand);
      bFullListChanges = false;
      for (int i = 1; i <= iTotalSentimentWords; i++) {
        int iOldSentimentStrength = resources.sentimentWords.getSentiment(iWordRand[i]);
        boolean bCurrentIDChange = false;
        int iAddOneImprovement = 0;
        int iSubtractOneImprovement = 0;
        if (iOldSentimentStrength < 4) {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength + 1);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
          iNewTrinary = getClassificationTrinaryNumberCorrect();
          iAddOneImprovement = iNewTrinary - iLastTrinaryCorrect;
          if (iAddOneImprovement >= iMinImprovement) {
            bCurrentIDChange = true;
            iLastTrinaryCorrect += iAddOneImprovement;
          }
        }
        if (iOldSentimentStrength > -4 && !bCurrentIDChange) {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength - 1);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
          iNewTrinary = getClassificationTrinaryNumberCorrect();
          iSubtractOneImprovement = iNewTrinary - iLastTrinaryCorrect;
          if (iSubtractOneImprovement >= iMinImprovement) {
            bCurrentIDChange = true;
            iLastTrinaryCorrect += iSubtractOneImprovement;
          }
        }
        if (bCurrentIDChange) {
          bFullListChanges = true;
        } else {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
        }
      }

    }
  }

  /**
   * 进行语料库的积极消极情绪强度值的权重优化。
   *
   * @param iMinImprovement     最小优化量
   * @param bUseTotalDifference 是否记录总变化量
   */
  public void optimiseDictionaryWeightingsForCorpusPosNeg(int iMinImprovement, boolean bUseTotalDifference) {
    boolean bFullListChanges = true;
    int iLastPos = 0;
    int iLastNeg = 0;
    int iLastPosTotalDiff = 0;
    int iLastNegTotalDiff = 0;
    if (bUseTotalDifference) {
      iLastPosTotalDiff = getClassificationPositiveTotalDifference();
      iLastNegTotalDiff = getClassificationNegativeTotalDifference();
    } else {
      iLastPos = getClassificationPositiveNumberCorrect();
      iLastNeg = getClassificationNegativeNumberCorrect();
    }
    int iNewPos = 0;
    int iNewNeg = 0;
    int iNewPosTotalDiff = 0;
    int iNewNegTotalDiff = 0;
    int iTotalSentimentWords = resources.sentimentWords.getSentimentWordCount();
    int[] iWordRand = new int[iTotalSentimentWords + 1];
    while (bFullListChanges) {
      Sort.makeRandomOrderList(iWordRand);
      bFullListChanges = false;
      for (int i = 1; i <= iTotalSentimentWords; i++) {
        int iOldSentimentStrength = resources.sentimentWords.getSentiment(iWordRand[i]);
        boolean bCurrentIDChange = false;
        if (iOldSentimentStrength < 4) {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength + 1);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
          if (bUseTotalDifference) {
            iNewPosTotalDiff = getClassificationPositiveTotalDifference();
            iNewNegTotalDiff = getClassificationNegativeTotalDifference();
            if (((iNewPosTotalDiff - iLastPosTotalDiff) + iNewNegTotalDiff) - iLastNegTotalDiff <= -iMinImprovement) {
              bCurrentIDChange = true;
            }
          } else {
            iNewPos = getClassificationPositiveNumberCorrect();
            iNewNeg = getClassificationNegativeNumberCorrect();
            if (((iNewPos - iLastPos) + iNewNeg) - iLastNeg >= iMinImprovement) {
              bCurrentIDChange = true;
            }
          }
        }
        if (iOldSentimentStrength > -4 && !bCurrentIDChange) {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength - 1);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
          if (bUseTotalDifference) {
            iNewPosTotalDiff = getClassificationPositiveTotalDifference();
            iNewNegTotalDiff = getClassificationNegativeTotalDifference();
            if (((iNewPosTotalDiff - iLastPosTotalDiff) + iNewNegTotalDiff) - iLastNegTotalDiff <= -iMinImprovement) {
              bCurrentIDChange = true;
            }
          } else {
            iNewPos = getClassificationPositiveNumberCorrect();
            iNewNeg = getClassificationNegativeNumberCorrect();
            if (((iNewPos - iLastPos) + iNewNeg) - iLastNeg >= iMinImprovement) {
              bCurrentIDChange = true;
            }
          }
        }
        if (bCurrentIDChange) {
          if (bUseTotalDifference) {
            iLastNegTotalDiff = iNewNegTotalDiff;
            iLastPosTotalDiff = iNewPosTotalDiff;
            bFullListChanges = true;
          } else {
            iLastNeg = iNewNeg;
            iLastPos = iNewPos;
            bFullListChanges = true;
          }
        } else {
          resources.sentimentWords.setSentiment(iWordRand[i], iOldSentimentStrength);
          reClassifyClassifiedCorpusForSentimentChange(iWordRand[i], 1);
        }
      }

    }
  }

  /**
   * 总结十折交叉验证。
   *
   * @param sInputFile  输入文件路径
   * @param sOutputFile 输出文件路径
   */
  public void SummariseMultiple10FoldValidations(String sInputFile, String sOutputFile) {
    int iDataRows = 28;
    int iLastOptionCol = 24;
    BufferedReader rResults = null;
    BufferedWriter wSummary = null;
    String sLine = null;
    String[] sPrevData = null;
    String[] sData = null;
    float[] total = new float[iDataRows];
    int iRows = 0;
    int i = 0;
    try {
      rResults = new BufferedReader(new FileReader(sInputFile));
      wSummary = new BufferedWriter(new FileWriter(sOutputFile));
      sLine = rResults.readLine();
      wSummary.write(sLine + "\tNumber\n");
      while (rResults.ready()) {
        sLine = rResults.readLine();
        sData = sLine.split("\t");
        boolean bMatching = true;
        if (sPrevData != null) {
          for (i = 0; i < iLastOptionCol; i++) {
            if (!sData[i].equals(sPrevData[i])) {
              bMatching = false;
            }
          }
        }
        if (!bMatching) {
          for (i = 0; i < iLastOptionCol; i++) {
            wSummary.write(sPrevData[i] + "\t");
          }
          for (i = 0; i < iDataRows; i++) {
            wSummary.write(total[i] / (float) iRows + "\t");
          }
          wSummary.write(iRows + "\n");
          for (i = 0; i < iDataRows; i++) {
            total[i] = 0.0F;
          }

          iRows = 0;
        }
        for (i = iLastOptionCol; i < iLastOptionCol + iDataRows; i++) {
          try {
            total[i - iLastOptionCol] += Float.parseFloat(sData[i]);
          } catch (Exception e) {
            total[i - iLastOptionCol] += 9999999F;
          }
        }

        iRows++;
        sPrevData = sLine.split("\t");
      }
      for (i = 0; i < iLastOptionCol; i++) {
        // TODO bug 没有进行sPrevData为null的检查
        wSummary.write(sPrevData[i] + "\t");
      }

      for (i = 0; i < iDataRows; i++) {
        wSummary.write(total[i] / (float) iRows + "\t");
      }

      wSummary.write(iRows + "\n");
      wSummary.close();
      rResults.close();
    } catch (IOException e) {
      System.out.println("SummariseMultiple10FoldValidations: File I/O error: " + sInputFile);
      e.printStackTrace();
    } catch (Exception e) {
      System.out.println("SummariseMultiple10FoldValidations: Error at line: " + sLine);
      System.out.println("Value of i: " + i);
      e.printStackTrace();
    }
  }
}