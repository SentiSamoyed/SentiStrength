//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package uk.ac.wlv.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SentiStrengthOld {
    private String[] sgSentimentWords;
    private int[] igSentimentWordsStrengthTake1;
    private int igSentimentWordsCount = 0;
    private String[] sgEmoticon;
    private int[] igEmoticonStrength;
    private int igEmoticonCount = 0;
    private int igEmoticonMax = 0;
    private String[] sgCorrectWord;
    private int igCorrectWordCount = 0;
    private int igCorrectWordMax = 0;
    private String[] sgSlangWord;
    private String[] sgSlangStandardWord;
    private int igSlangWordCount = 0;
    private int igSlangWordMax = 0;
    private String[] sgBoosterWord;
    private int[] igBoosterWordStrength;
    private int igBoosterWordCount = 0;
    private String[] sgNegatingWord;
    private int igNegatingWordCount = 0;
    private int igNegatingWordMax = 0;
    private String sgSentiStrengthFolder = "/SentiStrength_Data/";
    private String sgSentimentWordsFile = "EmotionLookupTable.txt";
    private String sgEmoticonLookupTable = "EmoticonLookupTable.txt";
    private String sgCorrectSpellingFileName = "EnglishWordList.txt";
    private String sgSlangLookupTable = "SlangLookupTable_NOT_USED.txt";
    private String sgNegatingWordListFile = "NegatingWordList.txt";
    private String sgBoosterListFile = "BoosterWordList.txt";
    private String sgIdiomLookupTableFile = "IdiomLookupTable_NOT_USED.txt";
    int igEmotionParagraphCombineMethod = 0;
    final int igEmotionParagraphCombineMax = 0;
    final int igEmotionParagraphCombineAverage = 1;
    int igEmotionSentenceCombineMethod = 0;
    final int igEmotionSentenceCombineMax = 0;
    final int igEmotionSentenceCombineAverage = 1;
    boolean bgIgnoreNegativeEmotionInQuestionSentences = true;
    boolean bgMissCountsAsPlus2 = true;
    boolean bgYouOrYourIsPlus2UnlessSentenceNegative = false;
    boolean bgExclamationCountsAsPlus2 = true;
    boolean bgUseIdiomLookupTable_NOT_USED = true;
    boolean bgCountNeutralEmotionsAsPositiveForEmphasis = true;
    boolean bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = true;
    boolean bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = true;
    boolean bgIgnoreBoosterWordsAfterNegatives = true;
    boolean bgCountMultipleLettersAsEmotionBoosters = true;
    boolean bgBoosterWordsIncreaseEmotion = true;
    boolean bgNegatingWordsFlipEmotion = true;
    boolean bgCorrectSpellingsWithRepeatedLetter = true;
    boolean bgUseEmoticons = true;
    int igMinRepeatedLettersForBoost = 2;
    int igMaxWordsSinceNegativeToFlip = 1;
    private final int igWordOriginal = 0;
    private final int igWordTranslated = 1;
    private final int igWordEmphasis = 2;
    private final int igWordPuncOrig = 3;
    private final int igWordPuncTrans = 4;
    private final int igWordPuncEmph = 5;
    private final int igWordEmoOrig = 6;
    private final int igWordEmoEmph = 7;
    private int igTaggedSentenceCount = 0;
    private int igTaggedSentenceMax = 1000;
    private String[] sgTaggedSentence;
    private String sgTempEmoticonEmphasis;
    private String sgErrorLog;
    private String sgOriginalText;
    private String sgTaggedText;
    private int igTextPos;
    private int igTextNeg;

    public SentiStrengthOld() {
        this.sgTaggedSentence = new String[this.igTaggedSentenceMax + 1];
        this.sgTempEmoticonEmphasis = "";
        this.sgErrorLog = "";
        this.sgOriginalText = "";
        this.sgTaggedText = "";
        this.igTextPos = 1;
        this.igTextNeg = -1;
    }

    public static void main(String[] args) {
        System.out.println("sentiStrength configuration data must be in C:\\SentStrength_Data\\\" for applet");
        System.out.println("For SentiStrength class call as follows:");
        System.out.println("ss = new sentiStrength();");
        System.out.println("ss.setInitalisationFilesFolder( \"[your SentiStrength_Data folder path]\")");
        System.out.println("ss.initialise(); // reads from SentiStrength_Data folder - returns false if can't read data from above folder");
        System.out.println("ss.detectEmotionInText(\"[your text to be classified]\");");
        System.out.println("Positive sentiment of text will be in + getPositiveClassification(), negative in ss.getNegativeClassification()");
    }

    public void setInitalisationFilesFolder(String folderName) {
        this.sgSentiStrengthFolder = folderName;
    }

    public boolean initialise() {
        this.sgErrorLog = "";
        if (!this.b_LoadEmoticonLookupTable(false)) {
            this.sgErrorLog = this.sgErrorLog + "Can't load emoticons from " + this.sgSentiStrengthFolder + this.sgEmoticonLookupTable + "\n";
        }

        if (!this.b_LoadSentimentWords()) {
            this.sgErrorLog = this.sgErrorLog + "Can't load sentiment words from " + this.sgSentiStrengthFolder + this.sgSentimentWordsFile + "\n";
        }

        if (!this.b_LoadCorrectSpellingWords()) {
            this.sgErrorLog = this.sgErrorLog + "Can't load dictionary from " + this.sgSentiStrengthFolder + this.sgCorrectSpellingFileName + "\n";
        }

        if (!this.b_LoadBoosterWords()) {
            this.sgErrorLog = this.sgErrorLog + "Can't load booster words from " + this.sgSentiStrengthFolder + this.sgBoosterListFile + "\n";
        }

        if (!this.b_LoadNegatingWords()) {
            this.sgErrorLog = this.sgErrorLog + "Can't load negating words from " + this.sgSentiStrengthFolder + this.sgNegatingWordListFile + "\n";
        }

        if (this.sgErrorLog != "") {
            return false;
        } else {
            Sort.quickSortStrings(this.sgEmoticon, 1, this.igEmoticonCount);
            return true;
        }
    }

    public void detectEmotionInText(String textToClassify) {
        this.sgOriginalText = textToClassify;
        this.DetectEmotionInText();
    }

    public int getPositiveClassification() {
        return this.igTextPos;
    }

    public int getNegativeClassification() {
        return this.igTextNeg;
    }

    public String getOriginalText() {
        return this.sgOriginalText;
    }

    public String getTaggedText() {
        return this.sgTaggedText;
    }

    public String getErrorLog() {
        return this.sgErrorLog;
    }

    public boolean classifyAllTextInFile(String sInFilenameAndPath, String sOutFilenameAndPath) {
        try {
            BufferedReader rReader = new BufferedReader(new FileReader(sInFilenameAndPath));
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sOutFilenameAndPath));

            while(rReader.ready()) {
                String sLine = rReader.readLine();
                if (sLine != "") {
                    int iTabPos = sLine.lastIndexOf("\t");
                    if (iTabPos >= 0) {
                        sLine = sLine.substring(iTabPos + 1);
                    }

                    this.sgOriginalText = sLine;
                    this.DetectEmotionInText();
                    wWriter.write(sLine + "\t" + this.sgTaggedText + "\t" + this.igTextPos + "\t" + this.igTextNeg + "\n");
                }
            }

            rReader.close();
            wWriter.close();
            return true;
        } catch (FileNotFoundException var8) {
            var8.printStackTrace();
            return false;
        } catch (IOException var9) {
            var9.printStackTrace();
            return false;
        }
    }

    private boolean b_TagOriginalText() {
        this.sgTaggedText = "";
        int iCharacter =1;
        int iPunctuation =1;
        int iSpace =1;
        int iEmoticon =1;
        int iPos = 0;
        int iStartOfElement = 0;
        int iLastCharType = 0;
        String sEmoticon = "";
        String sEm = "";
        String sChar = "";

        for(int iTextLength = this.sgOriginalText.length(); iPos < iTextLength; ++iPos) {
            sChar = this.sgOriginalText.substring(iPos, iPos + 1);
            if (sChar.compareTo("'") != 0 && (sChar.compareToIgnoreCase("a") < 0 || sChar.compareToIgnoreCase("z") > 0)) {
                if (sChar.compareTo(" ") == 0) {
                    switch(iLastCharType) {
                    case 1:
                        this.sgTaggedText = this.sgTaggedText + this.s_MakeWordTag(this.sgOriginalText.substring(iStartOfElement, iPos));
                        iStartOfElement = iPos;
                        break;
                    case 2:
                        this.sgTaggedText = this.sgTaggedText + this.s_PunctuationTag(this.sgOriginalText.substring(iStartOfElement, iPos));
                        iStartOfElement = iPos;
                    case 3:
                    default:
                        break;
                    case 4:
                        iStartOfElement = iPos;
                    }

                    iLastCharType = 3;
                } else if (sChar.compareTo("<") == 0) {
                    switch(iLastCharType) {
                    case 1:
                        this.sgTaggedText = this.sgTaggedText + this.s_MakeWordTag(this.sgOriginalText.substring(iStartOfElement, iPos));
                        iStartOfElement = iPos;
                        break;
                    case 2:
                    case 3:
                        this.sgTaggedText = this.sgTaggedText + this.sgOriginalText.substring(iStartOfElement, iPos);
                        iStartOfElement = iPos;
                        break;
                    case 4:
                        iStartOfElement = iPos;
                    }

                    if (iPos + 3 < iTextLength && this.sgOriginalText.substring(iPos, iPos + 4).compareToIgnoreCase("<br>") == 0) {
                        if (iLastCharType == 2) {
                            this.sgTaggedText = this.sgTaggedText + this.s_PunctuationTag(this.sgOriginalText.substring(iStartOfElement, iPos));
                            iStartOfElement = iPos;
                        }

                        iPos += 3;
                        iLastCharType = 3;
                    } else {
                        iLastCharType = 2;
                    }
                } else if (sChar.compareTo(">") != 0 && sChar.compareTo("(") != 0 && sChar.compareTo(")") != 0 && sChar.compareTo("\"") != 0 && sChar.compareTo(",") != 0 && sChar.compareTo(":") != 0 && sChar.compareTo(";") != 0 && sChar.compareTo(".") != 0 && sChar.compareTo("?") != 0 && sChar.compareTo("!") != 0 && sChar.compareTo("~") != 0 && sChar.compareTo("-") != 0 && sChar.compareTo("*") != 0) {
                    switch(iLastCharType) {
                    case 3:
                        this.sgTaggedText = this.sgTaggedText + this.sgOriginalText.substring(iStartOfElement, iPos);
                        iStartOfElement = iPos;
                    case 1:
                    case 2:
                    default:
                        iLastCharType = 1;
                    }
                } else {
                    switch(iLastCharType) {
                    case 1:
                        this.sgTaggedText = this.sgTaggedText + this.s_MakeWordTag(this.sgOriginalText.substring(iStartOfElement, iPos));
                        iStartOfElement = iPos;
                    case 2:
                    default:
                        break;
                    case 3:
                        this.sgTaggedText = this.sgTaggedText + this.sgOriginalText.substring(iStartOfElement, iPos);
                        iStartOfElement = iPos;
                        break;
                    case 4:
                        iStartOfElement = iPos;
                    }

                    sEmoticon = this.s_ExtractEmoticon(this.sgOriginalText, iPos);
                    sEm = this.sgTempEmoticonEmphasis;
                    if (sEmoticon != "") {
                        this.sgTaggedText = this.sgTaggedText + "<e em=\"" + sEm + "\">" + sEmoticon + "</e>";
                        iPos += sEmoticon.length() - 1;
                        iStartOfElement = 10000;
                        iLastCharType = 4;
                    } else {
                        iLastCharType = 2;
                    }
                }
            } else {
                switch(iLastCharType) {
                case 1:
                default:
                    break;
                case 2:
                    this.sgTaggedText = this.sgTaggedText + this.s_PunctuationTag(this.sgOriginalText.substring(iStartOfElement, iPos));
                    iStartOfElement = iPos;
                    break;
                case 3:
                    this.sgTaggedText = this.sgTaggedText + this.sgOriginalText.substring(iStartOfElement, iPos);
                    iStartOfElement = iPos;
                    break;
                case 4:
                    iStartOfElement = iPos;
                }

                iLastCharType = 1;
            }
        }

        switch(iLastCharType) {
        case 1:
            this.sgTaggedText = this.sgTaggedText + this.s_MakeWordTag(this.sgOriginalText.substring(iStartOfElement));
            break;
        case 2:
            this.sgTaggedText = this.sgTaggedText + this.s_PunctuationTag(this.sgOriginalText.substring(iStartOfElement));
            break;
        case 3:
            this.sgTaggedText = this.sgTaggedText + this.sgOriginalText.substring(iStartOfElement);
        }

        return true;
    }

    private String s_PunctuationTag(String sPunctuation) {
        String sPuncNew = "";
        String sEm = "";
        String sBr = "";
        String sChar = "";
        int iLast = sPunctuation.length() - 1;

        for(int i = 0; i <= iLast; ++i) {
            sChar = sPunctuation.substring(i, i + 1);
            if (sChar.compareTo(".") == 0 || sChar.compareTo("!") == 0 || sChar.compareTo("?") == 0) {
                sBr = "<br>";
                break;
            }
        }

        if (sPunctuation.length() > 1) {
            sEm = sPunctuation.substring(1);
            sPuncNew = sPunctuation.substring(0, 1);
        }

        return sEm == "" ? "<p>" + sPunctuation + "</p>" + sBr : "<p equiv=\"" + sPuncNew + "\" em=\"" + sEm + "\">" + sPunctuation + "</p>" + sBr;
    }

    private String s_ExtractEmoticon(String sText, int iStartPos) {
        if (!this.bgUseEmoticons) {
            return "";
        } else {
            int iRemainingChars = sText.length() - iStartPos;
            String sEmoticon = "";
            this.sgTempEmoticonEmphasis = "";
            if (iRemainingChars < 2) {
                return "";
            } else if (sText.substring(iStartPos, iStartPos + 1).compareTo(".") == 0) {
                return "";
            } else {
                int iEndpos = sText.indexOf(" ", iStartPos + 1);
                int iPos = sText.indexOf("<br>", iStartPos + 1);
                if (iEndpos < 0) {
                    if (iPos > 0) {
                        iEndpos = iPos;
                    } else {
                        iEndpos = iRemainingChars + iStartPos;
                    }
                } else if (iPos > 0 && iPos < iEndpos) {
                    iEndpos = iPos;
                }

                if (iEndpos - iStartPos < 2) {
                    return "";
                } else {
                    sEmoticon = sText.substring(iStartPos, iEndpos);
                    int iEmoticon = Sort.i_FindStringPositionInSortedArray(sEmoticon, this.sgEmoticon, 1, this.igEmoticonCount);
                    if (iEmoticon < 1) {
                        return "";
                    } else {
                        switch(this.igEmoticonStrength[iEmoticon]) {
                        case -1:
                            this.sgTempEmoticonEmphasis = "-";
                            break;
                        case 0:
                            this.sgTempEmoticonEmphasis = "";
                            break;
                        case 1:
                            this.sgTempEmoticonEmphasis = "+";
                        }

                        return sEmoticon;
                    }
                }
            }
        }
    }

    private String s_MakeWordTag(String sWord) {
        String sWordNew = "";
        String sEm = "";
        int iSameCount = 0;
        int iLastCopiedPos = 0;
        if (!this.bgCorrectSpellingsWithRepeatedLetter) {
            return "<w>" + sWord + "</w>";
        } else {
            int iWordEnd = sWord.length() - 1;

            int iPos;
            for(iPos = 1; iPos <= iWordEnd; ++iPos) {
                if (sWord.substring(iPos, iPos + 1).compareTo(sWord.substring(iPos - 1, iPos)) == 0) {
                    ++iSameCount;
                } else {
                    if (iSameCount > 0 && "ahijkquvxyz".indexOf(sWord.substring(iPos - 1, iPos)) >= 0) {
                        ++iSameCount;
                    }

                    if (iSameCount > 1) {
                        if (sEm == "") {
                            sWordNew = sWord.substring(0, iPos - iSameCount + 1);
                            sEm = sWord.substring(iPos - iSameCount, iPos - 1);
                            iLastCopiedPos = iPos;
                        } else {
                            sWordNew = sWordNew + sWord.substring(iLastCopiedPos, iPos - iSameCount + 1);
                            sEm = sEm + sWord.substring(iPos - iSameCount, iPos - 1);
                            iLastCopiedPos = iPos;
                        }
                    }

                    iSameCount = 0;
                }
            }

            if (iSameCount > 0 && "achijkmnpqruvwxyz".indexOf(sWord.substring(iPos - 1, iPos)) >= 0) {
                ++iSameCount;
            }

            if (iSameCount > 1) {
                if (sEm == "") {
                    sWordNew = sWord.substring(0, iPos - iSameCount + 1);
                    sEm = sWord.substring(iPos - iSameCount + 1);
                } else {
                    sWordNew = sWordNew + sWord.substring(iLastCopiedPos, iPos - iSameCount + 1);
                    sEm = sEm + sWord.substring(iPos - iSameCount + 1);
                }
            } else if (sEm != "") {
                sWordNew = sWordNew + sWord.substring(iLastCopiedPos);
            }

            if (sWordNew == "") {
                sWordNew = sWord;
            }

            this.sgTempEmoticonEmphasis = sEm;
            sWordNew = this.s_CorrectSpellingInWord(sWordNew);
            sEm = this.sgTempEmoticonEmphasis;
            if (sWordNew.indexOf(" ") > 0) {
                if (sEm == "") {
                    return sWordNew + "<w equiv=\"" + sWordNew.substring(sWordNew.indexOf(" ")) + "\">" + sWord + "</w>";
                } else {
                    return sWordNew + "<w equiv=\"" + sWordNew.substring(sWordNew.indexOf(" ")) + "\" em=\"" + sEm + "\">" + sWord + "</w>";
                }
            } else if (sEm == "" && sWordNew.compareTo(sWord) == 0) {
                return "<w>" + sWord + "</w>";
            } else {
                return "<w equiv=\"" + sWordNew + "\" em=\"" + sEm + "\">" + sWord + "</w>";
            }
        }
    }

    private boolean b_LoadEmoticonLookupTable(boolean bOverrideAllLoadedData) {
        if (this.igEmoticonCount > 0 && !bOverrideAllLoadedData) {
            return true;
        } else {
            this.igEmoticonMax = FileOps.i_CountLinesInTextFile(this.sgSentiStrengthFolder + this.sgEmoticonLookupTable) + 1;
            if (this.igEmoticonMax == -1) {
                return false;
            } else {
                this.igEmoticonCount = 0;
                String[] sEmoticonTemp = new String[this.igEmoticonMax];
                this.sgEmoticon = sEmoticonTemp;
                int[] iEmoticonStrengthTemp = new int[this.igEmoticonMax];
                this.igEmoticonStrength = iEmoticonStrengthTemp;

                try {
                    BufferedReader rReader = new BufferedReader(new FileReader(this.sgSentiStrengthFolder + this.sgEmoticonLookupTable));

                    while(rReader.ready()) {
                        String sLine = rReader.readLine();
                        if (sLine != "") {
                            String[] sData = sLine.split("\t");
                            if (sData.length >= 1) {
                                ++this.igEmoticonCount;
                                this.sgEmoticon[this.igEmoticonCount] = sData[0];
                                this.igEmoticonStrength[this.igEmoticonCount] = Integer.parseInt(sData[1]);
                            }
                        }
                    }

                    rReader.close();
                    return true;
                } catch (FileNotFoundException var8) {
                    var8.printStackTrace();
                    return false;
                } catch (IOException var9) {
                    var9.printStackTrace();
                    return false;
                }
            }
        }
    }

    private boolean b_LoadSentimentWords() {
        int iLinesInFile =0;
        int iWordStrength =0;
        if (this.sgSentimentWordsFile == "") {
            return false;
        } else {
            iLinesInFile = FileOps.i_CountLinesInTextFile(this.sgSentiStrengthFolder + this.sgSentimentWordsFile);
            if (iLinesInFile < 2) {
                return false;
            } else {
                String[] sSentiWordTemp = new String[iLinesInFile + 1];
                int[] iSentimentWordsStrength = new int[iLinesInFile + 1];
                this.igSentimentWordsStrengthTake1 = iSentimentWordsStrength;
                this.sgSentimentWords = sSentiWordTemp;
                this.igSentimentWordsCount = 0;

                try {
                    BufferedReader rReader = new BufferedReader(new FileReader(this.sgSentiStrengthFolder + this.sgSentimentWordsFile));

                    while(rReader.ready()) {
                        String sLine = rReader.readLine();
                        if (sLine != "") {
                            int iFirstTabLocation = sLine.indexOf("\t");
                            if (iFirstTabLocation >= 0) {
                                int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                                try {
                                    if (iSecondTabLocation > 0) {
                                        iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation));
                                    } else {
                                        iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1));
                                    }
                                } catch (NumberFormatException var10) {
                                    this.sgErrorLog = this.sgErrorLog + "Number format exception at line: " + sLine;
                                    var10.printStackTrace();
                                    return false;
                                }

                                sLine = sLine.substring(0, iFirstTabLocation);
                                if (sLine.indexOf(" ") >= 0) {
                                    sLine = sLine.trim();
                                }

                                if (sLine != "") {
                                    ++this.igSentimentWordsCount;
                                    this.sgSentimentWords[this.igSentimentWordsCount] = sLine;
                                    if (iWordStrength > 0) {
                                        --iWordStrength;
                                    } else {
                                        ++iWordStrength;
                                    }

                                    this.igSentimentWordsStrengthTake1[this.igSentimentWordsCount] = iWordStrength;
                                }
                            }
                        }
                    }

                    rReader.close();
                    return true;
                } catch (FileNotFoundException var11) {
                    var11.printStackTrace();
                    return false;
                } catch (IOException var12) {
                    var12.printStackTrace();
                    return false;
                }
            }
        }
    }

    private boolean b_LoadBoosterWords() {
        int iLinesInFile =0;
        int iWordStrength =0;
        if (this.sgBoosterListFile == "") {
            return false;
        } else {
            iLinesInFile = FileOps.i_CountLinesInTextFile(this.sgSentiStrengthFolder + this.sgBoosterListFile);
            if (iLinesInFile < 2) {
                return false;
            } else {
                String[] sTemp = new String[iLinesInFile + 1];
                int[] iTemp = new int[iLinesInFile + 1];
                this.igBoosterWordStrength = iTemp;
                this.sgBoosterWord = sTemp;
                this.igBoosterWordCount = 0;

                try {
                    BufferedReader rReader = new BufferedReader(new FileReader(this.sgSentiStrengthFolder + this.sgBoosterListFile));

                    while(rReader.ready()) {
                        String sLine = rReader.readLine();
                        if (sLine != "") {
                            int iFirstTabLocation = sLine.indexOf("\t");
                            if (iFirstTabLocation >= 0) {
                                int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                                try {
                                    if (iSecondTabLocation > 0) {
                                        iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation));
                                    } else {
                                        iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1));
                                    }
                                } catch (NumberFormatException var10) {
                                    this.sgErrorLog = this.sgErrorLog + "Number format exception at line: " + sLine;
                                    var10.printStackTrace();
                                    return false;
                                }

                                sLine = sLine.substring(0, iFirstTabLocation);
                                if (sLine.indexOf(" ") >= 0) {
                                    sLine = sLine.trim();
                                }

                                if (sLine != "") {
                                    ++this.igBoosterWordCount;
                                    this.sgBoosterWord[this.igBoosterWordCount] = sLine;
                                    this.igBoosterWordStrength[this.igBoosterWordCount] = iWordStrength;
                                }
                            }
                        }
                    }

                    rReader.close();
                    return true;
                } catch (FileNotFoundException var11) {
                    var11.printStackTrace();
                    return false;
                } catch (IOException var12) {
                    var12.printStackTrace();
                    return false;
                }
            }
        }
    }

    private String s_CorrectSpellingInWord(String sWord) {
        int iLastChar = sWord.length() - 1;

        for(int iPos = 1; iPos <= iLastChar; ++iPos) {
            if (sWord.substring(iPos, iPos + 1).compareTo(sWord.substring(iPos - 1, iPos)) == 0) {
                String sReplaceWord = sWord.substring(0, iPos) + sWord.substring(iPos + 1);
                if (Sort.i_FindStringPositionInSortedArray(sWord, this.sgCorrectWord, 1, this.igCorrectWordCount) > 0) {
                    return sWord;
                }

                if (Sort.i_FindStringPositionInSortedArray(sReplaceWord, this.sgCorrectWord, 1, this.igCorrectWordCount) > 0) {
                    this.sgTempEmoticonEmphasis = this.sgTempEmoticonEmphasis + sWord.substring(iPos, iPos + 1);
                    return sReplaceWord;
                }
            }
        }

        if (iLastChar > 5) {
            if (sWord.indexOf("haha") > 0) {
                this.sgTempEmoticonEmphasis = this.sgTempEmoticonEmphasis + sWord.substring(3, sWord.indexOf("haha") + 2);
                return "haha";
            }

            if (sWord.indexOf("hehe") > 0) {
                this.sgTempEmoticonEmphasis = this.sgTempEmoticonEmphasis + sWord.substring(3, sWord.indexOf("hehe") + 2);
                return "hehe";
            }
        }

        return sWord;
    }

    private boolean b_LoadNegatingWords() {
        if (this.igNegatingWordMax > 0) {
            return true;
        } else {
            this.igNegatingWordMax = FileOps.i_CountLinesInTextFile(this.sgSentiStrengthFolder + this.sgNegatingWordListFile) + 1;
            if (this.igNegatingWordMax == -1) {
                return false;
            } else {
                this.sgNegatingWord = new String[this.igNegatingWordMax];
                this.igNegatingWordCount = 0;

                try {
                    BufferedReader rReader = new BufferedReader(new FileReader(this.sgSentiStrengthFolder + this.sgNegatingWordListFile));

                    while(rReader.ready()) {
                        String sLine = rReader.readLine();
                        if (sLine != "") {
                            ++this.igNegatingWordCount;
                            this.sgNegatingWord[this.igNegatingWordCount] = sLine;
                        }
                    }

                    rReader.close();
                    return true;
                } catch (FileNotFoundException var4) {
                    var4.printStackTrace();
                    return false;
                } catch (IOException var5) {
                    var5.printStackTrace();
                    return false;
                }
            }
        }
    }

    private boolean b_LoadCorrectSpellingWords() {
        if (this.igCorrectWordMax > 0) {
            return true;
        } else {
            this.igCorrectWordMax = FileOps.i_CountLinesInTextFile(this.sgSentiStrengthFolder + this.sgCorrectSpellingFileName) + 1;
            if (this.igCorrectWordMax == -1) {
                return false;
            } else {
                this.sgCorrectWord = new String[this.igCorrectWordMax];
                this.igCorrectWordCount = 0;

                try {
                    BufferedReader rReader = new BufferedReader(new FileReader(this.sgSentiStrengthFolder + this.sgCorrectSpellingFileName));

                    while(rReader.ready()) {
                        String sLine = rReader.readLine();
                        if (sLine != "") {
                            ++this.igCorrectWordCount;
                            this.sgCorrectWord[this.igCorrectWordCount] = sLine;
                        }
                    }

                    rReader.close();
                    return true;
                } catch (FileNotFoundException var4) {
                    var4.printStackTrace();
                    return false;
                } catch (IOException var5) {
                    var5.printStackTrace();
                    return false;
                }
            }
        }
    }

    private String s_ReplaceWithEquivalentSlangWordOrPhrase_dont_use(String sWord) {
        int iNewWord =0;
        if (this.igSlangWordCount > 0) {
            iNewWord = Sort.i_FindStringPositionInSortedArray(sWord, this.sgSlangWord, 1, this.igSlangWordCount);
            if (iNewWord > 0) {
                return this.sgSlangStandardWord[iNewWord];
            }
        }

        return sWord;
    }

    private boolean b_LoadSlang() {
        int iLinesInFile =0;
        if (this.sgSlangLookupTable == "") {
            return false;
        } else {
            iLinesInFile = FileOps.i_CountLinesInTextFile(this.sgSentiStrengthFolder + this.sgSlangLookupTable);
            if (iLinesInFile < 2) {
                return false;
            } else {
                this.igSlangWordMax = iLinesInFile + 1;
                String[] sSlangTemp1 = new String[this.igSlangWordMax];
                String[] sSlangTemp2 = new String[this.igSlangWordMax];
                this.sgSlangWord = sSlangTemp1;
                this.sgSlangStandardWord = sSlangTemp2;
                this.igSlangWordCount = 0;

                try {
                    BufferedReader rReader = new BufferedReader(new FileReader(this.sgSentiStrengthFolder + this.sgSlangLookupTable));

                    while(rReader.ready()) {
                        String sLine = rReader.readLine();
                        if (sLine != "") {
                            int iFirstTabLocation = sLine.indexOf("\t");
                            if (iFirstTabLocation >= 0) {
                                ++this.igSlangWordCount;
                                int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                                if (iSecondTabLocation > 0) {
                                    this.sgSlangStandardWord[this.igSlangWordCount] = sLine.substring(iFirstTabLocation + 1, iSecondTabLocation);
                                } else {
                                    this.sgSlangStandardWord[this.igSlangWordCount] = sLine.substring(iFirstTabLocation + 1);
                                }

                                sLine = sLine.substring(0, iFirstTabLocation);
                                if (sLine.indexOf(" ") >= 0) {
                                    sLine = sLine.trim();
                                }

                                if (sLine != "") {
                                    this.sgSlangWord[this.igSlangWordCount] = sLine;
                                } else {
                                    --this.igSlangWordCount;
                                }
                            }
                        }
                    }

                    rReader.close();
                    return true;
                } catch (FileNotFoundException var9) {
                    var9.printStackTrace();
                    return false;
                } catch (IOException var10) {
                    var10.printStackTrace();
                    return false;
                }
            }
        }
    }

    private void DetectEmotionInText() {
        int iPosition = 0;
        int[] iPosSent = new int[1000];
        int[] iNegSent = new int[1000];
        int iPosTotal = 0;
        int iPosMax = 0;
        int iNegTotal = 0;
        int iNegMax = 0;
        if (this.sgOriginalText != "") {
            if (this.b_TagOriginalText()) {
                this.igTextPos = 1;
                this.igTextNeg = -1;
                this.igTaggedSentenceCount = 0;
                if (this.igSentimentWordsCount >= 1 || this.initialise()) {
                    iPosition = this.sgTaggedText.indexOf("<br>", iPosition + 1);
                    if (iPosition < 0) {
                        iPosition = this.sgTaggedText.length();
                    }

                    int iLastEnd = 0;

                    while(iPosition > 0 && this.igTaggedSentenceCount < this.igTaggedSentenceMax) {
                        ++this.igTaggedSentenceCount;
                        this.sgTaggedSentence[this.igTaggedSentenceCount] = this.sgTaggedText.substring(iLastEnd, iPosition);
                        this.DetectEmotionInSentence(iPosSent, iNegSent, this.igTaggedSentenceCount);
                        iLastEnd = iPosition + 4;
                        if (iPosition < this.sgTaggedText.length()) {
                            iPosition = this.sgTaggedText.indexOf("<br>", iPosition + 1);
                            if (iPosition < 0 && iLastEnd < this.sgTaggedText.length()) {
                                iPosition = this.sgTaggedText.length();
                            }
                        } else {
                            iPosition = 0;
                        }
                    }

                    this.igTextPos = 0;
                    this.igTextNeg = 0;

                    for(int iSentence = 1; iSentence <= this.igTaggedSentenceCount; ++iSentence) {
                        iNegTotal += iNegSent[iSentence];
                        if (iNegMax > iNegSent[iSentence]) {
                            iNegMax = iNegSent[iSentence];
                        }

                        iPosTotal += iPosSent[iSentence];
                        if (iPosMax < iPosSent[iSentence]) {
                            iPosMax = iPosSent[iSentence];
                        }
                    }

                    if (this.igEmotionParagraphCombineMethod == 1) {
                        this.igTextPos = (int)(((double)iPosTotal + 0.5D) / (double)this.igTaggedSentenceCount);
                        this.igTextNeg = (int)(((double)iNegTotal + 0.5D) / (double)this.igTaggedSentenceCount);
                    } else if (this.igEmotionParagraphCombineMethod == 0) {
                        this.igTextPos = iPosMax;
                        this.igTextNeg = iNegMax;
                    }

                    if (this.igTextPos == 0) {
                        this.igTextPos = 1;
                    }

                    if (this.igTextNeg == 0) {
                        this.igTextNeg = -1;
                    }

                }
            }
        }
    }

    private void DetectEmotionInSentence(int[] iPositive, int[] iNegative, int iSentence) {
        float[] fWordEmotion = new float[1000];
        int iMaxPos = 0;
        int iTotalPos = 0;
        int iMaxNeg = 0;
        int iTotalNeg = 0;
        int iWordTotal = this.i_GetEmotionWordList(fWordEmotion, iSentence);
        if (iWordTotal == 0) {
            iPositive[iSentence] = 0;
            iNegative[iSentence] = 0;
        } else {
            for(int iWord = 1; iWord <= iWordTotal; ++iWord) {
                if (fWordEmotion[iWord] < 0.0F) {
                    iTotalNeg = (int)((float)iTotalNeg + fWordEmotion[iWord]);
                    if ((float)iMaxNeg > fWordEmotion[iWord]) {
                        iMaxNeg = Math.round(fWordEmotion[iWord]);
                    }
                } else if (fWordEmotion[iWord] > 0.0F) {
                    iTotalPos = (int)((float)iTotalPos + fWordEmotion[iWord]);
                    if ((float)iMaxPos < fWordEmotion[iWord]) {
                        iMaxPos = Math.round(fWordEmotion[iWord]);
                    }
                }
            }

            --iMaxNeg;
            ++iMaxPos;
            iTotalNeg -= iWordTotal;
            iTotalPos += iWordTotal;
            if (this.igEmotionSentenceCombineMethod == 1) {
                iPositive[iSentence] = (int)(((double)iTotalPos + 0.5D) / (double)iWordTotal);
                iNegative[iSentence] = (int)(((double)iTotalNeg + 0.5D) / (double)iWordTotal);
            } else if (this.igEmotionSentenceCombineMethod == 0) {
                iPositive[iSentence] = iMaxPos;
                iNegative[iSentence] = iMaxNeg;
            }

            if (this.bgIgnoreNegativeEmotionInQuestionSentences && iNegative[iSentence] < -1 && (this.sgTaggedSentence[iSentence].indexOf("?") > 0 || this.sgTaggedSentence[iSentence].indexOf("what's") > 0 || this.sgTaggedSentence[iSentence].indexOf("whats ") > 0 || this.sgTaggedSentence[iSentence].indexOf("what<") > 0)) {
                iNegative[iSentence] = -1;
            }

            if (iPositive[iSentence] == 1 && this.bgMissCountsAsPlus2 && this.sgTaggedSentence[iSentence].indexOf(">miss<") >= 0) {
                iPositive[iSentence] = 2;
            }

            if (this.bgExclamationCountsAsPlus2 && iPositive[iSentence] == 1 && iNegative[iSentence] == -1 && this.sgTaggedSentence[iSentence].indexOf("!") >= 0) {
                iPositive[iSentence] = 2;
            }

            if (this.bgYouOrYourIsPlus2UnlessSentenceNegative && iPositive[iSentence] == 1 && iNegative[iSentence] == -1 && this.sgTaggedSentence[iSentence].indexOf(">you") >= 0) {
                iPositive[iSentence] = 2;
            }

            if (iPositive[iSentence] > 5) {
                iPositive[iSentence] = 5;
            }

            if (iNegative[iSentence] < -5) {
                iNegative[iSentence] = -5;
            }

        }
    }

    private int i_GetEmotionWordList(float[] fWordEmotion, int iSentence) {
        String[] sWord = new String[8];
        String sNextChar = "";
        int iPos = 0;
        int iWordID =0;
        int iWordsSinceNegative = 0;
        int iWordTotal = 0;
        int iLastWordBoosterStrength = 0;
        int iEnd = this.sgTaggedSentence[iSentence].length() - 1;
        boolean bLastWordNegates = false;

        while(iPos < iEnd && iWordTotal < 100) {
            sNextChar = this.sgTaggedSentence[iSentence].substring(iPos, iPos + 1);
            if (sNextChar.compareTo("<") != 0) {
                ++iPos;
            } else {
                sNextChar = this.sgTaggedSentence[iSentence].substring(iPos + 1, iPos + 2);
                int var10002;
                if (sNextChar.compareTo("w") == 0) {
                    iPos = this.i_GetWordFromTaggedText(iSentence, iPos, sWord);
                    if (sWord[1] != "") {
                        ++iWordTotal;
                        iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord[1].toLowerCase(), this.sgSentimentWords, 1, this.igSentimentWordsCount);
                        if (iWordID >= 0) {
                            fWordEmotion[iWordTotal] = (float)this.igSentimentWordsStrengthTake1[iWordID];
                        } else {
                            fWordEmotion[iWordTotal] = 0.0F;
                        }

                        if (this.bgCountMultipleLettersAsEmotionBoosters && sWord[2].length() >= this.igMinRepeatedLettersForBoost) {
                            if (fWordEmotion[iWordTotal] < 0.0F) {
                                fWordEmotion[iWordTotal] = (float)((double)fWordEmotion[iWordTotal] - 0.6D);
                            } else if ((this.bgCountNeutralEmotionsAsPositiveForEmphasis || fWordEmotion[iWordTotal] > 1.0F) && sWord[2].indexOf("xx") < 0 && sWord[2].indexOf("ww") < 0 && (sWord[2].indexOf("h") < 0 || sWord[2].indexOf("a") < 0)) {
                                fWordEmotion[iWordTotal] = (float)((double)fWordEmotion[iWordTotal] + 0.6D);
                            }
                        }

                        if (iLastWordBoosterStrength != 0 && this.bgBoosterWordsIncreaseEmotion) {
                            if (fWordEmotion[iWordTotal] < 0.0F) {
                                fWordEmotion[iWordTotal] -= (float)iLastWordBoosterStrength;
                            } else if (fWordEmotion[iWordTotal] > 1.0F) {
                                fWordEmotion[iWordTotal] += (float)iLastWordBoosterStrength;
                            }
                        }

                        iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord[1].toLowerCase(), this.sgBoosterWord, 1, this.igBoosterWordCount);
                        if (iWordID >= 0) {
                            iLastWordBoosterStrength = this.igBoosterWordStrength[iWordID];
                        } else {
                            iLastWordBoosterStrength = 0;
                        }

                        if (fWordEmotion[iWordTotal] != 0.0F && bLastWordNegates && this.bgNegatingWordsFlipEmotion && iWordsSinceNegative <= this.igMaxWordsSinceNegativeToFlip) {
                            fWordEmotion[iWordTotal] = -fWordEmotion[iWordTotal];
                        }

                        iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord[1].toLowerCase(), this.sgNegatingWord, 1, this.igNegatingWordCount);
                        if (iWordID >= 0) {
                            bLastWordNegates = true;
                            iWordsSinceNegative = 0;
                        } else if (iLastWordBoosterStrength == 0 || !this.bgIgnoreBoosterWordsAfterNegatives) {
                            ++iWordsSinceNegative;
                        }

                        if (iWordTotal > 1) {
                            if (fWordEmotion[iWordTotal] > 1.0F && fWordEmotion[iWordTotal - 1] > 1.0F) {
                                if (this.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion) {
                                    var10002 = (int) fWordEmotion[iWordTotal]++;
                                }
                            } else if (fWordEmotion[iWordTotal] < -1.0F && fWordEmotion[iWordTotal - 1] < -1.0F && this.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion) {
                                var10002 = (int) fWordEmotion[iWordTotal]--;
                            }
                        }
                    }
                } else if (sNextChar.compareTo("p") == 0) {
                    iPos = this.i_GetPunctuationFromTaggedText(iSentence, iPos, sWord);
                    if (sWord[5] != "" && sWord[5].indexOf("!") >= 0) {
                        if (fWordEmotion[iWordTotal] < 0.0F) {
                            fWordEmotion[iWordTotal] = (float)((double)fWordEmotion[iWordTotal] - 0.6D);
                        } else if (this.bgCountNeutralEmotionsAsPositiveForEmphasis || fWordEmotion[iWordTotal] > 1.0F) {
                            fWordEmotion[iWordTotal] = (float)((double)fWordEmotion[iWordTotal] + 0.6D);
                        }
                    }
                } else if (sNextChar.compareToIgnoreCase("b") == 0) {
                    ++iPos;
                } else if (sNextChar.compareTo("e") == 0) {
                    iPos = this.i_GetEmoticonFromTaggedText(iSentence, iPos, sWord);
                    if (iWordTotal == 0) {
                        iWordTotal = 1;
                    }

                    if (sWord[7] != "") {
                        if (sWord[7].indexOf("-") >= 0) {
                            var10002 = (int) fWordEmotion[iWordTotal]--;
                        } else {
                            var10002 = (int) fWordEmotion[iWordTotal]++;
                        }
                    }
                } else {
                    ++iPos;
                }
            }
        }

        return iWordTotal;
    }

    private int i_GetWordFromTaggedText(int iSentence, int iPos, String[] sWordAspects) {
        int iOriginalStart = 0;
        if (sWordAspects[0] != "") {
            sWordAspects[0] = "";
        }

        if (sWordAspects[1] != "") {
            sWordAspects[1] = "";
        }

        if (sWordAspects[2] != "") {
            sWordAspects[2] = "";
        }

        int iOriginalEnd = this.sgTaggedSentence[iSentence].indexOf("</w>", iPos + 3);
        if (iOriginalEnd > 0) {
            iOriginalStart = this.sgTaggedSentence[iSentence].lastIndexOf(">", iOriginalEnd - 1);
        }

        if (iOriginalStart > 0) {
            sWordAspects[0] = this.sgTaggedSentence[iSentence].substring(iOriginalStart + 1, iOriginalEnd);
            sWordAspects[1] = sWordAspects[0];
            int iTranslatedStart = this.sgTaggedSentence[iSentence].indexOf("equiv=\"", iPos + 2);
            if (iTranslatedStart > 0 && iTranslatedStart < iOriginalStart) {
                iTranslatedStart += 7;
                int iTranslatedEnd = this.sgTaggedSentence[iSentence].indexOf("\"", iTranslatedStart + 1);
                if (iTranslatedEnd > 0) {
                    sWordAspects[1] = this.sgTaggedSentence[iSentence].substring(iTranslatedStart, iTranslatedEnd);
                }
            }

            int iEmStart = this.sgTaggedSentence[iSentence].indexOf("em=\"", iPos + 3);
            if (iEmStart > 0 && iEmStart < iOriginalStart) {
                iEmStart += 4;
                int iEmEnd = this.sgTaggedSentence[iSentence].indexOf("\"", iEmStart + 1);
                if (iEmEnd > 0) {
                    sWordAspects[2] = this.sgTaggedSentence[iSentence].substring(iEmStart, iEmEnd);
                }
            }

            iPos = iOriginalEnd + 4;
        } else {
            iPos = this.sgTaggedSentence[iSentence].length() + 1;
        }

        return iPos;
    }

    private int i_GetPunctuationFromTaggedText(int iSentence, int iPos, String[] sWordAspects) {
        int iOriginalStart = 0;
        if (sWordAspects[3] != "") {
            sWordAspects[3] = "";
        }

        if (sWordAspects[4] != "") {
            sWordAspects[4] = "";
        }

        if (sWordAspects[5] != "") {
            sWordAspects[5] = "";
        }

        int iOriginalEnd = this.sgTaggedSentence[iSentence].indexOf("</p>", iPos + 3);
        if (iOriginalEnd > 0) {
            iOriginalStart = this.sgTaggedSentence[iSentence].lastIndexOf(">", iOriginalEnd - 1);
        }

        if (iOriginalStart > 0) {
            sWordAspects[3] = this.sgTaggedSentence[iSentence].substring(iOriginalStart + 1, iOriginalEnd);
            sWordAspects[4] = sWordAspects[3];
            int iTranslatedStart = this.sgTaggedSentence[iSentence].indexOf("equiv=\"", iPos + 2);
            if (iTranslatedStart > 0 && iTranslatedStart < iOriginalStart) {
                iTranslatedStart += 7;
                int iTranslatedEnd = this.sgTaggedSentence[iSentence].indexOf("\"", iTranslatedStart + 1);
                if (iTranslatedEnd > 0) {
                    sWordAspects[4] = this.sgTaggedSentence[iSentence].substring(iTranslatedStart, iTranslatedEnd);
                }
            }

            int iEmStart = this.sgTaggedSentence[iSentence].indexOf("em=\"", iPos + 3);
            if (iEmStart > 0 && iEmStart < iOriginalStart) {
                iEmStart += 4;
                int iEmEnd = this.sgTaggedSentence[iSentence].indexOf("\"", iEmStart + 1);
                if (iEmEnd > 0) {
                    sWordAspects[5] = this.sgTaggedSentence[iSentence].substring(iEmStart, iEmEnd);
                }
            }

            iPos = iOriginalEnd + 4;
        } else {
            iPos = this.sgTaggedSentence[iSentence].length() + 1;
        }

        return iPos;
    }

    private int i_GetEmoticonFromTaggedText(int iSentence, int iPos, String[] sWordAspects) {
        int iOriginalStart = 0;
        if (sWordAspects[6] != "") {
            sWordAspects[6] = "";
        }

        if (sWordAspects[7] != "") {
            sWordAspects[7] = "";
        }

        int iOriginalEnd = this.sgTaggedSentence[iSentence].indexOf("</e>", iPos + 2);
        if (iOriginalEnd > 0) {
            iOriginalStart = this.sgTaggedSentence[iSentence].lastIndexOf(">", iOriginalEnd - 1);
        }

        if (iOriginalStart > 0) {
            sWordAspects[6] = this.sgTaggedSentence[iSentence].substring(iOriginalStart + 1, iOriginalEnd);
            int iEmStart = this.sgTaggedSentence[iSentence].indexOf("em=\"", iPos + 3);
            if (iEmStart > 0 && iEmStart < iOriginalStart) {
                iEmStart += 4;
                int iEmEnd = this.sgTaggedSentence[iSentence].indexOf("\"", iEmStart + 1);
                if (iEmEnd > 0) {
                    sWordAspects[7] = this.sgTaggedSentence[iSentence].substring(iEmStart, iEmEnd);
                }
            }

            iPos = iOriginalEnd + 4;
        } else {
            iPos = this.sgTaggedSentence[iSentence].length() + 1;
        }

        return iPos;
    }
}
