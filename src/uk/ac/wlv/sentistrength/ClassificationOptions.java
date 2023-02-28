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

public class ClassificationOptions {
    public boolean bgTensiStrength = false;
    public String sgProgramName = "SentiStrength";
    public String sgProgramMeasuring = "sentiment";
    public String sgProgramPos = "positive sentiment";
    public String sgProgramNeg = "negative sentiment";
    public boolean bgScaleMode = false;
    public boolean bgTrinaryMode = false;
    public boolean bgBinaryVersionOfTrinaryMode = false;
    public int igDefaultBinaryClassification = 1;
    public int igEmotionParagraphCombineMethod = 0;
    final int igCombineMax = 0;
    final int igCombineAverage = 1;
    final int igCombineTotal = 2;
    public int igEmotionSentenceCombineMethod = 0;
    public float fgNegativeSentimentMultiplier = 1.5F;
    public boolean bgReduceNegativeEmotionInQuestionSentences = false;
    public boolean bgMissCountsAsPlus2 = true;
    public boolean bgYouOrYourIsPlus2UnlessSentenceNegative = false;
    public boolean bgExclamationInNeutralSentenceCountsAsPlus2 = false;
    public int igMinPunctuationWithExclamationToChangeSentenceSentiment = 0;
    public boolean bgUseIdiomLookupTable = true;
    public boolean bgUseObjectEvaluationTable = false;
    public boolean bgCountNeutralEmotionsAsPositiveForEmphasis1 = true;
    public int igMoodToInterpretNeutralEmphasis = 1;
    public boolean bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = true;
    public boolean bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = true;
    public boolean bgIgnoreBoosterWordsAfterNegatives = true;
    public boolean bgCorrectSpellingsUsingDictionary = true;
    public boolean bgCorrectExtraLetterSpellingErrors = true;
    public String sgIllegalDoubleLettersInWordMiddle = "ahijkquvxyz";
    public String sgIllegalDoubleLettersAtWordEnd = "achijkmnpqruvwxyz";
    public boolean bgMultipleLettersBoostSentiment = true;
    public boolean bgBoosterWordsChangeEmotion = true;
    public boolean bgAlwaysSplitWordsAtApostrophes = false;
    public boolean bgNegatingWordsOccurBeforeSentiment = true;
    public int igMaxWordsBeforeSentimentToNegate = 0;
    public boolean bgNegatingWordsOccurAfterSentiment = false;
    public int igMaxWordsAfterSentimentToNegate = 0;
    public boolean bgNegatingPositiveFlipsEmotion = true;
    public boolean bgNegatingNegativeNeutralisesEmotion = true;
    public boolean bgNegatingWordsFlipEmotion = false;
    public float fgStrengthMultiplierForNegatedWords = 0.5F;
    public boolean bgCorrectSpellingsWithRepeatedLetter = true;
    public boolean bgUseEmoticons = true;
    public boolean bgCapitalsBoostTermSentiment = false;
    public int igMinRepeatedLettersForBoost = 2;
    public String[] sgSentimentKeyWords = null;
    public boolean bgIgnoreSentencesWithoutKeywords = false;
    public int igWordsToIncludeBeforeKeyword = 4;
    public int igWordsToIncludeAfterKeyword = 4;
    public boolean bgExplainClassification = false;
    public boolean bgEchoText = false;
    public boolean bgForceUTF8 = false;
    public boolean bgUseLemmatisation = false;
    public int igMinSentencePosForQuotesIrony = 10;
    public int igMinSentencePosForPunctuationIrony = 10;
    public int igMinSentencePosForTermsIrony = 10;

    public ClassificationOptions() {
    }

    public void parseKeywordList(String sKeywordList) {
        this.sgSentimentKeyWords = sKeywordList.split(",");
        this.bgIgnoreSentencesWithoutKeywords = true;
    }

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

            wWriter.write("\t" + iMultiOptimisations + "\t" + this.bgReduceNegativeEmotionInQuestionSentences + "\t" + this.bgMissCountsAsPlus2 + "\t" + this.bgYouOrYourIsPlus2UnlessSentenceNegative + "\t" + this.bgExclamationInNeutralSentenceCountsAsPlus2 + "\t" + this.bgUseIdiomLookupTable + "\t" + this.igMoodToInterpretNeutralEmphasis + "\t" + this.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion + "\t" + this.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion + "\t" + this.bgIgnoreBoosterWordsAfterNegatives + "\t" + this.bgMultipleLettersBoostSentiment + "\t" + this.bgBoosterWordsChangeEmotion + "\t" + this.bgNegatingWordsFlipEmotion + "\t" + this.bgNegatingPositiveFlipsEmotion + "\t" + this.bgNegatingNegativeNeutralisesEmotion + "\t" + this.bgCorrectSpellingsWithRepeatedLetter + "\t" + this.bgUseEmoticons + "\t" + this.bgCapitalsBoostTermSentiment + "\t" + this.igMinRepeatedLettersForBoost + "\t" + this.igMaxWordsBeforeSentimentToNegate + "\t" + iMinImprovement);
            return true;
        } catch (IOException var6) {
            var6.printStackTrace();
            return false;
        }
    }

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

            while(rReader.ready()) {
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
