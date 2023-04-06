package web.service;

import mock.MockSentiStrengthFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.wlv.sentistrength.ClassificationOptions;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.data.vo.AnalysisOptionsVO;
import web.data.vo.TextRequestVO;
import web.enums.AnalysisModeEnum;
import web.factory.SentiStrengthFactory;
import web.service.impl.AnalysisServiceImpl;
import web.util.TextRequestVOGenerator;

import java.util.Objects;

/**
 * @author tanziyue
 * @date 2023/4/6
 * @description 分析业务逻辑测试
 */
@SpringBootTest
public class AnalysisServiceTest {

  @Autowired
  SentiStrengthFactory sentiStrengthFactory;

  @Autowired
  AnalysisServiceImpl analysisService;

  @BeforeEach
  void setUp() {
    analysisService.setSentiStrengthFactory(sentiStrengthFactory);
  }

  /**
   * SentiStrength 配置解析测试
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

  @Test
  void testModeAndExplain() {
    AnalysisOptionsVO optionsVO = new AnalysisOptionsVO();
    for (AnalysisModeEnum mode : AnalysisModeEnum.values()) {
      for (boolean explain : new boolean[]{true, false}) {
        SentiStrength sentiStrength = sentiStrengthFactory.build(
            mode, explain, optionsVO
        );
        ClassificationOptions co = sentiStrength.getCorpus().options;

        switch (mode) {
          case DEFAULT -> {
            Assertions.assertFalse(co.bgTrinaryMode);
            Assertions.assertFalse(co.bgBinaryVersionOfTrinaryMode);
            Assertions.assertFalse(co.bgScaleMode);
          }
          case BINARY -> Assertions.assertTrue(co.bgBinaryVersionOfTrinaryMode);
          case TRINARY -> Assertions.assertFalse(co.bgScaleMode);
          case SCALE -> {
            Assertions.assertFalse(co.bgTrinaryMode);
            Assertions.assertFalse(co.bgBinaryVersionOfTrinaryMode);
            Assertions.assertTrue(co.bgScaleMode);
          }
          default -> Assertions.fail();
        }

        Assertions.assertEquals(explain, co.bgExplainClassification);
      }
    }
  }

  @Test
  void testAnalysis() {
    analysisService.setSentiStrengthFactory(new MockSentiStrengthFactory());
    TextRequestVO requestVO = TextRequestVOGenerator.generate(false);

    for (AnalysisModeEnum mode : AnalysisModeEnum.values()) {
      for (boolean explain : new boolean[]{true, false}) {
        var result = analysisService.textAnalysis(requestVO.getText(), mode, explain, requestVO.getOptions());
        Assertions.assertEquals(1, result.size());
        var as = result.get(0);

        switch (mode) {
          case DEFAULT -> {
            Assertions.assertNotEquals(0, as.getVal1());
            Assertions.assertNotEquals(0, as.getVal2());
            Assertions.assertEquals(0, as.getVal3());
          }
          case BINARY, TRINARY, SCALE -> {
            Assertions.assertNotEquals(0, as.getVal1());
            Assertions.assertNotEquals(0, as.getVal2());
            Assertions.assertNotEquals(0, as.getVal3());
          }
        }

        Assertions.assertEquals(explain, Objects.nonNull(as.getExplain()));
      }
    }

  }


}
