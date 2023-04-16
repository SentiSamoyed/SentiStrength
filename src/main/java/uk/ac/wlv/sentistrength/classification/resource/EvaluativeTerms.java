// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   EvaluativeTerms.java

package uk.ac.wlv.sentistrength.classification.resource;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;
import uk.ac.wlv.sentistrength.classification.resource.concrete.IdiomList;
import uk.ac.wlv.sentistrength.classification.resource.concrete.SentimentWords;
import uk.ac.wlv.utilities.FileOps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * 存放额外的 Object, Evaluation 和 Strength.
 * 仅当开启参数 <code>additionalFile</code> 时，才会使用该类。
 *
 * @see ClassificationResources
 * @see IdiomList
 * @see SentimentWords
 */
@Log4j2
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

    if (!new File(sSourceFile).exists()) {
      System.out.println("Could not find additional (object/evaluation) file: " + sSourceFile);
      return false;
    }

    AtomicBoolean bIdiomsAdded = new AtomicBoolean(false);
    AtomicBoolean bSentimentWordsAdded = new AtomicBoolean(false);

    final String
        COMMENT_PREFIX = "##",
        IDIOM = "idiom",
        SENTIMENT = "sentiment",
        OE = "object/evaluation";

    try (Stream<String> lines = FileOps.getFileStream(sSourceFile, options.bgForceUTF8)) {
      igObjectEvaluationMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
      igObjectEvaluationCount = 0;
      sgObject = new String[igObjectEvaluationMax];
      sgObjectEvaluation = new String[igObjectEvaluationMax];
      igObjectEvaluationStrength = new int[igObjectEvaluationMax];

      lines
          .filter(l -> !l.startsWith(COMMENT_PREFIX))
          .filter(l -> l.indexOf('\t') > 0)
          .map(l -> l.split("\t"))
          .forEachOrdered(sData -> {
            int iStrength;
            String branch = "";
            try {
              if (sData.length > 2 && !sData[2].startsWith(COMMENT_PREFIX)) {
                branch = OE;
                int cur = igObjectEvaluationCount + 1;
                sgObject[cur] = sData[0];
                sgObjectEvaluation[cur] = sData[1];

                igObjectEvaluationStrength[cur] = Integer.parseInt(sData[2].trim());
                if (igObjectEvaluationStrength[cur] > 0) {
                  igObjectEvaluationStrength[cur]--;
                } else if (igObjectEvaluationStrength[cur] < 0) {
                  igObjectEvaluationStrength[cur]++;
                }
                igObjectEvaluationCount = cur;

              } else if (sData[0].indexOf(" ") > 0) {
                branch = IDIOM;
                iStrength = Integer.parseInt(sData[1].trim());
                // 向 idiomList 中添加额外的词组
                idiomList.addExtraIdiom(sData[0], iStrength, false);
                bIdiomsAdded.set(true);

              } else {
                branch = SENTIMENT;
                iStrength = Integer.parseInt(sData[1].trim());
                sentimentWords.addOrModifySentimentTerm(sData[0], iStrength, false);
                bSentimentWordsAdded.set(true);

              }
            } catch (NumberFormatException e) {
              log.error("Failed to identify integer weight for %s! Ignoring it".formatted(branch));
              log.error("Line: " + String.join("\t", sData));
            }
          });

    } catch (FileNotFoundException e) {
      log.fatal("Could not find file: " + sSourceFile);
      return false;
    } catch (IOException e) {
      log.fatal(e.getLocalizedMessage());
      return false;
    }

    if (igObjectEvaluationCount > 0) {
      options.bgUseObjectEvaluationTable = true;
    }
    if (bSentimentWordsAdded.get()) {
      sentimentWords.sortSentimentList();
    }
    if (bIdiomsAdded.get()) {
      idiomList.convertIdiomStringsToWordLists();
    }

    return true;
  }
}
