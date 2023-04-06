package web.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.wlv.sentistrength.ClassificationOptions;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.data.vo.AnalysisOptionsVO;
import web.data.vo.TextRequestVO;
import web.factory.SentiStrengthFactory;
import web.util.TextRequestVOGenerator;

@SpringBootTest
public class AnalysisServiceTest {

  @Autowired
  SentiStrengthFactory sentiStrengthFactory;

  /**
   * @author tanziyue
   * @date 2023/4/6
   * @description 测试选项是否有被正确解析
   */
  @Test
  void testOptions() {
    TextRequestVO requestVO = TextRequestVOGenerator.generate(true);
    AnalysisOptionsVO options = requestVO.getOptions();
    SentiStrength sentiStrength = sentiStrengthFactory.build(
        requestVO.getMode(),
        requestVO.getExplain(),
        options
    );

    ClassificationOptions co = sentiStrength.getCorpus().options;

    /* yes */
    // alwaysSplitWordsAtApostrophes
    Assertions.assertTrue(co.bgAlwaysSplitWordsAtApostrophes);
    // questionsReduceNeg
    Assertions.assertTrue(co.bgReduceNegativeEmotionInQuestionSentences);
    // exclamations2
    Assertions.assertTrue(co.bgExclamationInNeutralSentenceCountsAsPlus2);

    /* no */
    // noBoosters
    Assertions.assertFalse(co.bgBoosterWordsChangeEmotion);
    // noNegatingPositiveFlipsEmotion
    Assertions.assertFalse(co.bgNegatingPositiveFlipsEmotion);
    // noNegatingNegativeNeutralisesEmotion
    Assertions.assertFalse(co.bgNegatingNegativeNeutralisesEmotion);
    // noIdioms
    Assertions.assertFalse(co.bgUseIdiomLookupTable);
    // noEmoticons
    Assertions.assertFalse(co.bgUseEmoticons);
    // noMultiplePosWords
    Assertions.assertFalse(co.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion);
    // noMultipleNegWords
    Assertions.assertFalse(co.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion);
    // noIgnoreBoosterWordsAfterNegatives
    Assertions.assertFalse(co.bgIgnoreBoosterWordsAfterNegatives);
    // noDictionary
    Assertions.assertFalse(co.bgCorrectSpellingsUsingDictionary);
    // noDeleteExtraDuplicateLetters
    Assertions.assertFalse(co.bgCorrectExtraLetterSpellingErrors);
    // noMultipleLetters
    Assertions.assertFalse(co.bgMultipleLettersBoostSentiment);

    /* content */
    // negatedWordStrengthMultiplier
    Assertions.assertEquals(options.getNegatedWordStrengthMultiplier().floatValue(), co.fgStrengthMultiplierForNegatedWords);
    // maxWordsBeforeSentimentToNegate
    Assertions.assertEquals(options.getMaxWordsBeforeSentimentToNegate(), co.igMaxWordsBeforeSentimentToNegate);
    // mood
    Assertions.assertEquals(options.getMood(), co.igMoodToInterpretNeutralEmphasis);
    // illegalDoubleLettersInWordMiddle
    Assertions.assertEquals(options.getIllegalDoubleLettersInWordMiddle(), co.sgIllegalDoubleLettersInWordMiddle);
    // illegalDoubleLettersAtWordEnd
    Assertions.assertEquals(options.getIllegalDoubleLettersAtWordEnd(), co.sgIllegalDoubleLettersAtWordEnd);
  }
}
