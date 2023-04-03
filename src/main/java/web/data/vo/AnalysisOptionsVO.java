package web.data.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author tanziyue
 * @date 2023/4/3
 * @description 情感分析选项
 */
@Data
@AllArgsConstructor
@Builder
public class AnalysisOptionsVO {

  /**
   * 总是在撇号处分词
   */
  @NotNull
  Boolean alwaysSplitWordsAtApostrophes;
  /**
   * 不使用助推词
   */
  @NotNull
  Boolean noBoosters;
  /**
   * 不使用否定词反转情绪
   */
  @NotNull
  Boolean noNegatingPositiveFlipsEmotion;
  /**
   * 不使用否定词中和消极词
   */
  @NotNull
  Boolean noNegatingNegativeNeutralisesEmotion;
  /**
   * 否定词的强度倍数
   */
  @NotNull
  Double negatedWordStrengthMultiplier;
  /**
   * 否定词与
   */
  @NotNull
  Integer maxWordsBeforeSentimentToNegate;
  /**
   * 不使用习语
   */
  @NotNull
  Boolean noIdioms;
  /**
   * 在问句中减少消极情感
   */
  @NotNull
  Boolean questionsReduceNeg;
  /**
   * 不使用表情符号
   */
  @NotNull
  Boolean noEmoticons;
  /**
   * 感叹号分数加二
   */
  @NotNull
  Boolean exclamations2;
  /**
   * 心情
   */
  @NotNull
  Integer mood;
  /**
   * 不允许多个积极词增加积极情感
   */
  @NotNull
  Boolean noMultiplePosWords;
  /**
   * 不允许多个消极词增加消极情感
   */
  @NotNull
  Boolean noMultipleNegWords;
  /**
   * 不忽略否定词后的助推词
   */
  @NotNull
  Boolean noIgnoreBoosterWordsAfterNegatives;
  /**
   * 不使用字典纠正拼写
   */
  @NotNull
  Boolean noDictionary;
  /**
   * 不删除单词中额外的重复字母
   */
  @NotNull
  Boolean noDeleteExtraDuplicateLetters;
  /**
   * 单词中的非法重复字母
   */
  @NotNull
  String illegalDoubleLettersInWordMiddle;
  /**
   * 单词末尾的非法重复字母
   */
  @NotNull
  String illegalDoubleLettersAtWordEnd;
  /**
   * 不允许重复单词
   */
  @NotNull
  Boolean noMultipleLetters;

  public AnalysisOptionsVO() {
    this.alwaysSplitWordsAtApostrophes = false;
    this.noBoosters = false;
    this.noNegatingPositiveFlipsEmotion = false;
    this.noNegatingNegativeNeutralisesEmotion = false;
    this.negatedWordStrengthMultiplier = 0.5;
    this.maxWordsBeforeSentimentToNegate = 0;
    this.noIdioms = false;
    this.questionsReduceNeg = false;
    this.noEmoticons = false;
    this.exclamations2 = false;
    this.mood = 0;
    this.noMultiplePosWords = false;
    this.noMultipleNegWords = false;
    this.noIgnoreBoosterWordsAfterNegatives = false;
    this.noDictionary = false;
    this.noDeleteExtraDuplicateLetters = false;
    this.illegalDoubleLettersInWordMiddle = "ahijkquvxyz";
    this.illegalDoubleLettersAtWordEnd = "achijkmnpqruvwxyz";
    this.noMultipleLetters = false;
  }
}
