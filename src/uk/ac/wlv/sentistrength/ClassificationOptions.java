//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package uk.ac.wlv.sentistrength;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 分类选项类
 */
public class ClassificationOptions {
  /**
   * 是否为 TensiStrength; 在 SentiStrength 中默认为 false.
   */
  public boolean bgTensiStrength = false;
  /**
   * 程序名称，默认 SentiStrength
   */
  public String sgProgramName = "SentiStrength";
  /**
   * 衡量维度，默认 sentiment
   */
  public String sgProgramMeasuring = "sentiment";
  /**
   * 正向维度名，默认 positive sentiment
   */
  public String sgProgramPos = "positive sentiment";
  /**
   * 负向维度名，默认 negative sentiment
   */
  public String sgProgramNeg = "negative sentiment";
  /**
   * 启用 Scale 模式，额外输出正负分之和
   */
  public boolean bgScaleMode = false;
  /**
   * 启用 Trinary 模式，综合 positive/negative/neutral 输出
   */
  public boolean bgTrinaryMode = false;
  /**
   * 启用 Binary 模式
   */
  public boolean bgBinaryVersionOfTrinaryMode = false;
  /**
   * Binary 模式正负相同时的默认值
   */
  public int igDefaultBinaryClassification = 1;
  /**
   * 段落情感组合方法; 取值见常量 igCombine___
   */
  public int igEmotionParagraphCombineMethod = 0;
  /**
   * 最大值组合
   */
  final int igCombineMax = 0;
  /**
   * 平均值组合
   */
  final int igCombineAverage = 1;
  /**
   * 总和组合
   */
  final int igCombineTotal = 2;
  /**
   * 语句情感组合方法
   */
  public int igEmotionSentenceCombineMethod = 0;
  /**
   * 消极情感放大倍数；在 EmotionParagraphCombineMethod 采用 CombineTotal 时启用
   */
  public float fgNegativeSentimentMultiplier = 1.5F;
  /**
   * 忽略疑问句中的消极情感
   */
  public boolean bgReduceNegativeEmotionInQuestionSentences = false;
  /**
   * 将 "miss" 记为 +2
   */
  public boolean bgMissCountsAsPlus2 = true;
  /**
   * 语句不为消极时将 "you/your/whats" 记为 +2
   */
  public boolean bgYouOrYourIsPlus2UnlessSentenceNegative = false;
  /**
   * 中性语句中的感叹号记为 +2
   */
  public boolean bgExclamationInNeutralSentenceCountsAsPlus2 = false;
  /**
   * 最小改变语句情感的感叹号数
   */
  public int igMinPunctuationWithExclamationToChangeSentenceSentiment = 0;
  /**
   * 使用 IdiomLookupTable
   */
  public boolean bgUseIdiomLookupTable = true;
  /**
   * 使用 ObjectEvaluationTable
   */
  public boolean bgUseObjectEvaluationTable = false;
  /**
   * 将中性情感识别为积极
   */
  public boolean bgCountNeutralEmotionsAsPositiveForEmphasis1 = true;
  /**
   * 对中性词语的情感解读, 1 为积极, -1 为消极, 0 为忽略
   */
  public int igMoodToInterpretNeutralEmphasis = 1;
  /**
   * 多积极词叠加以加强积极情感
   */
  public boolean bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = true;
  /**
   * 多消极词叠加以加强消极情感
   */
  public boolean bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = true;
  /**
   * 是否省略消极词后的 booster word
   */
  public boolean bgIgnoreBoosterWordsAfterNegatives = true;
  /**
   * 是否使用词典纠正拼写
   */
  public boolean bgCorrectSpellingsUsingDictionary = true;
  /**
   * 是否纠正单词中的额外重复字母，比如：heyyyy
   */
  public boolean bgCorrectExtraLetterSpellingErrors = true;
  /**
   * 从不在词中重复的单词
   */
  public String sgIllegalDoubleLettersInWordMiddle = "ahijkquvxyz";
  /**
   * 从不在词尾重复的单词
   */
  public String sgIllegalDoubleLettersAtWordEnd = "achijkmnpqruvwxyz";
  /**
   * 重复字母加强情感
   */
  public boolean bgMultipleLettersBoostSentiment = true;
  /**
   * Booster words 改变情感
   */
  public boolean bgBoosterWordsChangeEmotion = true;
  /**
   * 在撇号处分割单词
   */
  public boolean bgAlwaysSplitWordsAtApostrophes = false;
  /**
   * 用消极词来翻转之前单词的 sentiment
   */
  public boolean bgNegatingWordsOccurBeforeSentiment = true;
  /**
   * 消极词往前最多翻转的单词数
   */
  public int igMaxWordsBeforeSentimentToNegate = 0;
  /**
   * 翻转消极词后单词的 sentiment
   */
  public boolean bgNegatingWordsOccurAfterSentiment = false;
  /**
   * 消极词往后最多翻转的单词数
   */
  public int igMaxWordsAfterSentimentToNegate = 0;
  /**
   * 用消极词来翻转积极词的情感
   */
  public boolean bgNegatingPositiveFlipsEmotion = true;
  /**
   * 用消极词来翻转消极中性词的情感
   */
  public boolean bgNegatingNegativeNeutralisesEmotion = true;
  /**
   * 消极词翻转情感
   */
  public boolean bgNegatingWordsFlipEmotion = false;
  /**
   * 消极词反转情感时的乘数
   */
  public float fgStrengthMultiplierForNegatedWords = 0.5F;
  /**
   * 纠正有重复单词的拼写
   */
  public boolean bgCorrectSpellingsWithRepeatedLetter = true;
  /**
   * 使用 emoticon
   */
  public boolean bgUseEmoticons = true;
  /**
   * 大写字母加强情感
   */
  public boolean bgCapitalsBoostTermSentiment = false;
  /**
   * 加强情感的最小连续字母
   */
  public int igMinRepeatedLettersForBoost = 2;
  /**
   * 情感关键词
   */
  public String[] sgSentimentKeyWords = null;
  /**
   * 忽略不含关键词的语句
   */
  public boolean bgIgnoreSentencesWithoutKeywords = false;
  /**
   * 关键词前包含的单词数
   */
  public int igWordsToIncludeBeforeKeyword = 4;
  /**
   * 关键词后包含的单词数
   */
  public int igWordsToIncludeAfterKeyword = 4;
  /**
   * 解释分类原因
   */
  public boolean bgExplainClassification = false;
  /**
   * 打印目标文版
   */
  public boolean bgEchoText = false;
  /**
   * 强制使用 UTF-8
   */
  public boolean bgForceUTF8 = false;
  /**
   * 是否适用 <a href="https://en.wikipedia.org/wiki/Lemmatisation">Lemmatisation</a>
   */
  public boolean bgUseLemmatisation = false;
  /**
   * 判断引用为讽刺的最小积极分值
   */
  public int igMinSentencePosForQuotesIrony = 10;
  /**
   * 判断标点符号为讽刺的最小积极分值
   */
  public int igMinSentencePosForPunctuationIrony = 10;
  /**
   * 判断 Term 为讽刺的最小积极分值
   */
  public int igMinSentencePosForTermsIrony = 10;

  public ClassificationOptions() {
  }

  /**
   * 解析关键词列表
   *
   * @param sKeywordList 关键词列表文本，由 "," 间隔
   */
  public void parseKeywordList(String sKeywordList) {
    this.sgSentimentKeyWords = sKeywordList.split(",");
    this.bgIgnoreSentencesWithoutKeywords = true;
  }

  /**
   * 输出分类选项
   *
   * @param wWriter             输出 Writer
   * @param iMinImprovement     机器学习参数：min improvement
   * @param bUseTotalDifference 机器学习参数：使用 total difference
   * @param iMultiOptimisations 机器学习参数：使用 multi optimizations
   * @return 正常输出
   * @see SentiStrength#initialiseAndRun(String[])
   */
  public boolean printClassificationOptions(BufferedWriter wWriter, int iMinImprovement, boolean bUseTotalDifference, int iMultiOptimisations) {
    try {
      if (this.igEmotionParagraphCombineMethod == 0) {
        wWriter.write("Max");
      } else if (this.igEmotionParagraphCombineMethod == 1) {
        wWriter.write("Av");
      } else {
        wWriter.write("Tot");
      }

      if (this.igEmotionSentenceCombineMethod == 0) {
        wWriter.write("\tMax");
      } else if (this.igEmotionSentenceCombineMethod == 1) {
        wWriter.write("\tAv");
      } else {
        wWriter.write("\tTot");
      }

      if (bUseTotalDifference) {
        wWriter.write("\tTotDiff");
      } else {
        wWriter.write("\tExactCount");
      }

      wWriter.write("\t" + iMultiOptimisations +
          "\t" + this.bgReduceNegativeEmotionInQuestionSentences +
          "\t" + this.bgMissCountsAsPlus2 +
          "\t" + this.bgYouOrYourIsPlus2UnlessSentenceNegative +
          "\t" + this.bgExclamationInNeutralSentenceCountsAsPlus2 +
          "\t" + this.bgUseIdiomLookupTable +
          "\t" + this.igMoodToInterpretNeutralEmphasis +
          "\t" + this.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion +
          "\t" + this.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion +
          "\t" + this.bgIgnoreBoosterWordsAfterNegatives +
          "\t" + this.bgMultipleLettersBoostSentiment +
          "\t" + this.bgBoosterWordsChangeEmotion +
          "\t" + this.bgNegatingWordsFlipEmotion +
          "\t" + this.bgNegatingPositiveFlipsEmotion +
          "\t" + this.bgNegatingNegativeNeutralisesEmotion +
          "\t" + this.bgCorrectSpellingsWithRepeatedLetter +
          "\t" + this.bgUseEmoticons +
          "\t" + this.bgCapitalsBoostTermSentiment +
          "\t" + this.igMinRepeatedLettersForBoost +
          "\t" + this.igMaxWordsBeforeSentimentToNegate +
          "\t" + iMinImprovement
      );
      return true;
    } catch (IOException var6) {
      var6.printStackTrace();
      return false;
    }
  }

  /**
   * 输出空分类选项
   *
   * @param wWriter 输出 writer
   * @return 是否正常输出
   */
  public boolean printBlankClassificationOptions(BufferedWriter wWriter) {
    try {
      wWriter.write("~");
      wWriter.write("\t~");
      wWriter.write("\tBaselineMajorityClass");
      wWriter.write("\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~\t~");
      return true;
    } catch (IOException var3) {
      var3.printStackTrace();
      return false;
    }
  }

  /**
   * 输出分类选项条目名
   * <ol>
   * <li>EmotionParagraphCombineMethod</li>
   * <li>EmotionSentenceCombineMethod</li>
   * <li>DifferenceCalculationMethodForTermWeightAdjustments</li>
   * <li>MultiOptimisations</li>
   * <li>ReduceNegativeEmotionInQuestionSentences</li>
   * <li>MissCountsAsPlus2</li>
   * <li>YouOrYourIsPlus2UnlessSentenceNegative</li>
   * <li>ExclamationCountsAsPlus2</li>
   * <li>UseIdiomLookupTable</li>
   * <li>MoodToInterpretNeutralEmphasis</li>
   * <li>AllowMultiplePositiveWordsToIncreasePositiveEmotion</li>
   * <li>AllowMultipleNegativeWordsToIncreaseNegativeEmotion</li>
   * <li>IgnoreBoosterWordsAfterNegatives</li>
   * <li>MultipleLettersBoostSentiment</li>
   * <li>BoosterWordsChangeEmotion</li>
   * <li>NegatingWordsFlipEmotion</li>
   * <li>NegatingPositiveFlipsEmotion</li>
   * <li>NegatingNegativeNeutralisesEmotion</li>
   * <li>CorrectSpellingsWithRepeatedLetter</li>
   * <li>UseEmoticons</li>
   * <li>CapitalsBoostTermSentiment</li>
   * <li>MinRepeatedLettersForBoost</li>
   * <li>WordsBeforeSentimentToNegate</li>
   * <li>MinImprovement</li>
   * </ol>
   *
   * @param wWriter 输出 writer
   * @return 是否正常输出
   */
  public boolean printClassificationOptionsHeadings(BufferedWriter wWriter) {
    try {
      wWriter.write("EmotionParagraphCombineMethod\tEmotionSentenceCombineMethod\tDifferenceCalculationMethodForTermWeightAdjustments\tMultiOptimisations\tReduceNegativeEmotionInQuestionSentences\tMissCountsAsPlus2\tYouOrYourIsPlus2UnlessSentenceNegative\tExclamationCountsAsPlus2\tUseIdiomLookupTable\tMoodToInterpretNeutralEmphasis\tAllowMultiplePositiveWordsToIncreasePositiveEmotion\tAllowMultipleNegativeWordsToIncreaseNegativeEmotion\tIgnoreBoosterWordsAfterNegatives\tMultipleLettersBoostSentiment\tBoosterWordsChangeEmotion\tNegatingWordsFlipEmotion\tNegatingPositiveFlipsEmotion\tNegatingNegativeNeutralisesEmotion\tCorrectSpellingsWithRepeatedLetter\tUseEmoticons\tCapitalsBoostTermSentiment\tMinRepeatedLettersForBoost\tWordsBeforeSentimentToNegate\tMinImprovement");
      return true;
    } catch (IOException var3) {
      var3.printStackTrace();
      return false;
    }
  }

  public boolean setClassificationOptions(String sFilename) {
    try {
      BufferedReader rReader = new BufferedReader(new FileReader(sFilename));

      while (rReader.ready()) {
        String sLine = rReader.readLine();
        int iTabPos = sLine.indexOf("\t");
        if (iTabPos > 0) {
          String[] sData = sLine.split("\t");
          if (sData[0] == "EmotionParagraphCombineMethod") {
            if (sData[1].indexOf("Max") >= 0) {
              this.igEmotionParagraphCombineMethod = 0;
            }

            if (sData[1].indexOf("Av") >= 0) {
              this.igEmotionParagraphCombineMethod = 1;
            }

            if (sData[1].indexOf("Tot") >= 0) {
              this.igEmotionParagraphCombineMethod = 2;
            }
          } else if (sData[0] == "EmotionSentenceCombineMethod") {
            if (sData[1].indexOf("Max") >= 0) {
              this.igEmotionSentenceCombineMethod = 0;
            }

            if (sData[1].indexOf("Av") >= 0) {
              this.igEmotionSentenceCombineMethod = 1;
            }

            if (sData[1].indexOf("Tot") >= 0) {
              this.igEmotionSentenceCombineMethod = 2;
            }
          } else if (sData[0] == "IgnoreNegativeEmotionInQuestionSentences") {
            this.bgReduceNegativeEmotionInQuestionSentences = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "MissCountsAsPlus2") {
            this.bgMissCountsAsPlus2 = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "YouOrYourIsPlus2UnlessSentenceNegative") {
            this.bgYouOrYourIsPlus2UnlessSentenceNegative = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "ExclamationCountsAsPlus2") {
            this.bgExclamationInNeutralSentenceCountsAsPlus2 = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "UseIdiomLookupTable") {
            this.bgUseIdiomLookupTable = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "Mood") {
            this.igMoodToInterpretNeutralEmphasis = Integer.parseInt(sData[1]);
          } else if (sData[0] == "AllowMultiplePositiveWordsToIncreasePositiveEmotion") {
            this.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "AllowMultipleNegativeWordsToIncreaseNegativeEmotion") {
            this.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "IgnoreBoosterWordsAfterNegatives") {
            this.bgIgnoreBoosterWordsAfterNegatives = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "MultipleLettersBoostSentiment") {
            this.bgMultipleLettersBoostSentiment = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "BoosterWordsChangeEmotion") {
            this.bgBoosterWordsChangeEmotion = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "NegatingWordsFlipEmotion") {
            this.bgNegatingWordsFlipEmotion = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "NegatingWordsFlipEmotion") {
            this.bgNegatingPositiveFlipsEmotion = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "NegatingWordsFlipEmotion") {
            this.bgNegatingNegativeNeutralisesEmotion = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "CorrectSpellingsWithRepeatedLetter") {
            this.bgCorrectSpellingsWithRepeatedLetter = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "UseEmoticons") {
            this.bgUseEmoticons = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "CapitalsAreSentimentBoosters") {
            this.bgCapitalsBoostTermSentiment = Boolean.parseBoolean(sData[1]);
          } else if (sData[0] == "MinRepeatedLettersForBoost") {
            this.igMinRepeatedLettersForBoost = Integer.parseInt(sData[1]);
          } else if (sData[0] == "WordsBeforeSentimentToNegate") {
            this.igMaxWordsBeforeSentimentToNegate = Integer.parseInt(sData[1]);
          } else if (sData[0] == "Trinary") {
            this.bgTrinaryMode = true;
          } else if (sData[0] == "Binary") {
            this.bgTrinaryMode = true;
            this.bgBinaryVersionOfTrinaryMode = true;
          } else {
            if (sData[0] != "Scale") {
              rReader.close();
              return false;
            }

            this.bgScaleMode = true;
          }
        }
      }

      rReader.close();
      return true;
    } catch (FileNotFoundException var7) {
      var7.printStackTrace();
      return false;
    } catch (IOException var8) {
      var8.printStackTrace();
      return false;
    }
  }

  public void nameProgram(boolean bTensiStrength) {
    this.bgTensiStrength = bTensiStrength;
    if (bTensiStrength) {
      this.sgProgramName = "TensiStrength";
      this.sgProgramMeasuring = "stress and relaxation";
      this.sgProgramPos = "relaxation";
      this.sgProgramNeg = "stress";
    } else {
      this.sgProgramName = "SentiStrength";
      this.sgProgramMeasuring = "sentiment";
      this.sgProgramPos = "positive sentiment";
      this.sgProgramNeg = "negative sentiment";
    }

  }
}
