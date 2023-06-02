package web.util;

import web.entity.vo.AnalysisOptionsVO;
import web.entity.vo.TextRequestVO;
import web.enums.AnalysisModeEnum;

public class TextRequestVOGenerator {
  public static TextRequestVO generate(boolean allSetOptions) {
    return TextRequestVO.builder()
        .text("Fly me to the moon~")
        .mode(AnalysisModeEnum.TRINARY)
        .explain(true)
        .options(allSetOptions ? generateAllSetOptions() : new AnalysisOptionsVO())
        .build();
  }

  public static AnalysisOptionsVO generateAllSetOptions() {
    return AnalysisOptionsVO.builder()
        .alwaysSplitWordsAtApostrophes(true)
        .noBoosters(true)
        .noNegatingPositiveFlipsEmotion(true)
        .noNegatingNegativeNeutralisesEmotion(true)
        .negatedWordStrengthMultiplier(1.14)
        .maxWordsBeforeSentimentToNegate(5)
        .noIdioms(true)
        .questionsReduceNeg(true)
        .noEmoticons(true)
        .exclamations2(true)
        .mood(1)
        .noMultiplePosWords(true)
        .noMultipleNegWords(true)
        .noIgnoreBoosterWordsAfterNegatives(true)
        .noDictionary(true)
        .noDeleteExtraDuplicateLetters(true)
        .illegalDoubleLettersInWordMiddle("abcd")
        .illegalDoubleLettersAtWordEnd("edgh")
        .noMultipleLetters(true)
        .build();
  }
}
