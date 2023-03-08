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
 * 分类选项类。
 */
public class ClassificationOptions {
  /**
   * 是否为 TensiStrength; 在 SentiStrength 中默认为 false.
   */
  public boolean bgTensiStrength = false;
  /**
   * 程序名称，默认 SentiStrength.
   */
  public String sgProgramName = "SentiStrength";
  /**
   * 衡量维度，默认 sentiment.
   */
  public String sgProgramMeasuring = "sentiment";
  /**
   * 积极维度名，默认 positive sentiment.
   */
  public String sgProgramPos = "positive sentiment";
  /**
   * 消极维度名，默认 negative sentiment.
   */
  public String sgProgramNeg = "negative sentiment";
  /**
   * 启用 Scale 模式，输出 positive & negative 的范围分。
   */
  public boolean bgScaleMode = false;
  /**
   * 启用 Trinary 模式，综合 positive/negative/neutral 输出。
   */
  public boolean bgTrinaryMode = false;
  /**
   * 启用 Binary 模式。
   */
  public boolean bgBinaryVersionOfTrinaryMode = false;
  /**
   * Binary 模式下，积极和消极分数相同时的默认值。
   */
  public int igDefaultBinaryClassification = 1;
  /**
   * 段落情感组合方法; 取值见常量 {@link #igCombineMax}, {@link #igCombineAverage}, {@link #igCombineTotal}.
   */
  public int igEmotionParagraphCombineMethod = 0;
  /**
   * 最大值组合。
   */
  final int igCombineMax = 0;
  /**
   * 平均值组合。
   */
  final int igCombineAverage = 1;
  /**
   * 总和组合。
   */
  final int igCombineTotal = 2;
  /**
   * 语句情感组合方法。
   */
  public int igEmotionSentenceCombineMethod = 0;
  /**
   * 消极情感放大倍数；在 {@link #igEmotionSentenceCombineMethod} 采用 {@link #igCombineTotal} 时启用。
   */
  public float fgNegativeSentimentMultiplier = 1.5F;
  /**
   * 减少疑问句中的消极情感。
   */
  public boolean bgReduceNegativeEmotionInQuestionSentences = false;
  /**
   * 将 "miss" 记为 +2.
   */
  public boolean bgMissCountsAsPlus2 = true;
  /**
   * 语句不为消极时将 "you/your/whats" 记为 +2.
   */
  public boolean bgYouOrYourIsPlus2UnlessSentenceNegative = false;
  /**
   * 中性语句中的感叹号记为 +2.
   */
  public boolean bgExclamationInNeutralSentenceCountsAsPlus2 = false;
  /**
   * 最小改变语句情感的感叹号数。
   */
  public int igMinPunctuationWithExclamationToChangeSentenceSentiment = 0;
  /**
   * 使用 IdiomLookupTable.
   * @see ClassificationResources#sgIdiomLookupTableFile
   */
  public boolean bgUseIdiomLookupTable = true;
  /**
   * 使用 ObjectEvaluationTable.
   * @see EvaluativeTerms
   */
  public boolean bgUseObjectEvaluationTable = false;
  /**
   * 将中性情感识别为积极。
   */
  public boolean bgCountNeutralEmotionsAsPositiveForEmphasis1 = true;
  /**
   * 对中性词语的情感解读, 1 为积极, -1 为消极, 0 为忽略。
   */
  public int igMoodToInterpretNeutralEmphasis = 1;
  /**
   * 多积极词叠加以加强积极情感。
   */
  public boolean bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = true;
  /**
   * 多消极词叠加以加强消极情感。
   */
  public boolean bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = true;
  /**
   * 是否省略消极词后的助推词。
   */
  public boolean bgIgnoreBoosterWordsAfterNegatives = true;
  /**
   * 是否使用词典纠正拼写。
   */
  public boolean bgCorrectSpellingsUsingDictionary = true;
  /**
   * 是否纠正单词中的额外重复字母，比如：heyyyy.
   */
  public boolean bgCorrectExtraLetterSpellingErrors = true;
  /**
   * 从不在词中重复的单词。
   */
  public String sgIllegalDoubleLettersInWordMiddle = "ahijkquvxyz";
  /**
   * 从不在词尾重复的单词。
   */
  public String sgIllegalDoubleLettersAtWordEnd = "achijkmnpqruvwxyz";
  /**
   * 重复字母加强情感。
   */
  public boolean bgMultipleLettersBoostSentiment = true;
  /**
   * 助推词改变情感。
   */
  public boolean bgBoosterWordsChangeEmotion = true;
  /**
   * 在撇号处分割单词。
   */
  public boolean bgAlwaysSplitWordsAtApostrophes = false;
  /**
   * 用否定词来翻转之前单词的情感。
   */
  public boolean bgNegatingWordsOccurBeforeSentiment = true;
  /**
   * 否定词往前最多翻转的单词数。
   * @see #bgNegatingWordsOccurBeforeSentiment
   */
  public int igMaxWordsBeforeSentimentToNegate = 0;
  /**
   * 用否定词来翻转之后单词的情感。
   */
  public boolean bgNegatingWordsOccurAfterSentiment = false;
  /**
   * 否定词往后最多翻转的单词数。
   * @see #bgNegatingWordsOccurAfterSentiment
   */
  public int igMaxWordsAfterSentimentToNegate = 0;
  /**
   * 用否定词来翻转积极词的情感。
   */
  public boolean bgNegatingPositiveFlipsEmotion = true;
  /**
   * 用否定词来翻转消极中性词的情感。
   */
  public boolean bgNegatingNegativeNeutralisesEmotion = true;
  /**
   * 否定词翻转情感。
   */
  public boolean bgNegatingWordsFlipEmotion = false;
  /**
   * 否定词翻转情感时的放大倍数。
   */
  public float fgStrengthMultiplierForNegatedWords = 0.5F;
  /**
   * 纠正有重复单词的拼写。
   */
  public boolean bgCorrectSpellingsWithRepeatedLetter = true;
  /**
   * 使用表情词。
   */
  public boolean bgUseEmoticons = true;
  /**
   * 大写字母加强情感。
   */
  public boolean bgCapitalsBoostTermSentiment = false;
  /**
   * 加强情感的最小连续字母。
   */
  public int igMinRepeatedLettersForBoost = 2;
  /**
   * 情感关键词。
   */
  public String[] sgSentimentKeyWords = null;
  /**
   * 忽略不含关键词的语句。
   */
  public boolean bgIgnoreSentencesWithoutKeywords = false;
  /**
   * 关键词前包含的单词数。
   */
  public int igWordsToIncludeBeforeKeyword = 4;
  /**
   * 关键词后包含的单词数。
   */
  public int igWordsToIncludeAfterKeyword = 4;
  /**
   * 解释分类原因。
   */
  public boolean bgExplainClassification = false;
  /**
   * 打印目标文本。
   */
  public boolean bgEchoText = false;
  /**
   * 强制使用 UTF-8.
   */
  public boolean bgForceUTF8 = false;
  /**
   * 是否使用 {@link Lemmatiser} 进行词性还原。
   */
  public boolean bgUseLemmatisation = false;
  /**
   * 判断引用为反语的最小积极分值。
   */
  public int igMinSentencePosForQuotesIrony = 10;
  /**
   * 判断标点符号为反语的最小积极分值。
   */
  public int igMinSentencePosForPunctuationIrony = 10;
  /**
   * 判断词为反语的最小积极分值。
   */
  public int igMinSentencePosForTermsIrony = 10;

  public ClassificationOptions() {
  }

  /**
   * 解析关键词列表。
   *
   * @param sKeywordList 关键词列表文本，由 "," 间隔
   */
  public void parseKeywordList(String sKeywordList) {
    this.sgSentimentKeyWords = sKeywordList.split(",");
    this.bgIgnoreSentencesWithoutKeywords = true;
  }

  /**
   * 输出分类选项。
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
   * 输出空分类选项。
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
   * 输出分类选项条目名。
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

  /**
   * 加载分类选项。
   *
   * @param sFilename 文件名
   * @return 是否正常加载
   */
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

  /**
   * 为程序命名。
   * <table>
   *   <caption>取值组合</caption>
   *   <tr>
   *     <td>Name</td>
   *     <td>TensiStrength</td>
   *     <td>SentiStrength</td>
   *   </tr>
   *   <tr>
   *     <td>Measuring</td>
   *     <td>stress and relaxation</td>
   *     <td>sentiment</td>
   *   </tr>
   *   <tr>
   *     <td>Pos</td>
   *     <td>relaxation</td>
   *     <td>positive sentiment</td>
   *   </tr>
   *   <tr>
   *     <td>Neg</td>
   *     <td>stress</td>
   *     <td>negative sentiment</td>
   *   </tr>
   * </table>
   * @param bTensiStrength 是否是 TensiStrength
   */
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
