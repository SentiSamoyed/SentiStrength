package uk.ac.wlv.sentistrength;

import uk.ac.wlv.util.ArgParser;
import uk.ac.wlv.utilities.FileOps;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

/**
 * SentiStrength 运行驱动类。
 */
public class SentiStrength {
  Corpus c = new Corpus();

  /**
   * 无参数构造方法。
   */
  public SentiStrength() {
    this.c = new Corpus();
  }

  /**
   * 有参数构造方法。
   *
   * @param args 运行参数
   */
  public SentiStrength(String[] args) {
    this.c = new Corpus();
    this.initialiseAndRun(args);
  }

  /**
   * Main 方法。
   *
   * @param args 运行参数
   */
  public static void main(String[] args) {
    SentiStrength classifier = new SentiStrength();

    classifier.initialiseAndRun(args);
  }

  /**
   * 无作用方法。
   *
   * @return null
   */
  @Deprecated
  public String[] getinput() {
    return null;
  }

  /**
   * 驱动方法。
   *
   * @param args 运行参数
   */
  public void initialiseAndRun(String[] args) {
    Corpus c = this.c;

    String sInputFile = "";
    String sInputFolder = "";
    String sTextToParse = "";
    String sOptimalTermStrengths = "";
    String sFileSubString = "\t";
    String sResultsFolder = "";
    String sResultsFileExtension = "_out.txt";

    boolean[] bArgumentRecognised = new boolean[args.length];

    int iIterations = 1;
    int iMinImprovement = 2;
    int iMultiOptimisations = 1;
    int iListenPort = 0;
    int iTextColForAnnotation = -1;
    int iIdCol = -1;
    int iTextCol = -1;

    boolean bDoAll = false;
    boolean bOkToOverwrite = false;
    boolean bTrain = false;
    boolean bReportNewTermWeightsForBadClassifications = false;
    boolean bStdIn = false;
    boolean bCmd = false;
    boolean bWait = false;
    boolean bUseTotalDifference = true;
    boolean bURLEncoded = false;

    String sLanguage = "";

    Arrays.fill(bArgumentRecognised, false);

    /* ---------- 参数识别 ---------- */
    ArgParser parser = new ArgParser(bArgumentRecognised);

    // 只包含一个后续参数
    String[] oneArgs = new String[]{
        "input", "inputfolder", "outputfolder",
        "resultextension", "resultsextension",
        "filesubstring", "text", "optimise", "lang",
    };

    for (String arg : oneArgs) {
      parser.addArgument(arg, 1, ArgParser.BOY_NEXT_DOOR);
    }

    // 不包含后续参数，仅设置为 true
    String[] boolArgs = new String[]{
        "overwrite", "urlencoded", "stdin", "cmd",
        "train", "all", "numcorrect", "termWeights", "wait",
        "help"
    };

    for (String arg : boolArgs) {
      parser.addArgument(arg, 0, ArgParser.SET_TRUE);
    }

    // 将后续参数转换为 int 类型的
    String[] intArgs = new String[]{
        "listen", "annotatecol", "textcol", "idcol",
        "iterations", "minimprovement", "multi"
    };

    for (String arg : intArgs) {
      parser.addArgument(arg, 1, ArgParser.INT_NEXT_DOOR);
    }

    // 解析参数
    boolean success = parser.parseArgs(args);
    if (!success) {
      return;
    }

    // 对于 Help，直接打印并返回
    if (parser.extract("help", false)) {
      this.printCommandLineOptions();
      return;
    }

    sInputFile = parser.extract("input", sInputFile);
    sInputFolder = parser.extract("inputfolder", sInputFolder);
    sResultsFolder = parser.extract("outputfolder", sResultsFolder);
    sResultsFileExtension = parser.extract("resultextension", sResultsFileExtension);
    sResultsFileExtension = parser.extract("resultsextension", sResultsFileExtension);
    sFileSubString = parser.extract("filesubstring", sFileSubString);
    bOkToOverwrite = parser.extract("overwrite", bOkToOverwrite);
    sTextToParse = parser.extract("text", sTextToParse);
    bURLEncoded = parser.extract("urlencoded", bURLEncoded);
    iListenPort = parser.extract("listen", iListenPort);
    bStdIn = parser.extract("stdin", bStdIn);
    bCmd = parser.extract("cmd", bCmd);
    sOptimalTermStrengths = parser.extract("optimise", sOptimalTermStrengths);
    iTextColForAnnotation = parser.extract("annotatecol", iTextColForAnnotation);
    iTextCol = parser.extract("textcol", iTextCol);
    iIdCol = parser.extract("idcol", iIdCol);
    sLanguage = parser.extract("lang", sLanguage);
    bTrain = parser.extract("train", bTrain);
    bDoAll = parser.extract("all", bDoAll);
    bUseTotalDifference = parser.extract("numcorrect", bUseTotalDifference);
    iIterations = parser.extract("iterations", iIterations);
    iMinImprovement = parser.extract("minimprovement", iMinImprovement);
    iMultiOptimisations = parser.extract("multi", iMultiOptimisations);
    bReportNewTermWeightsForBadClassifications = parser.extract("termWeights", bReportNewTermWeightsForBadClassifications);
    bWait = parser.extract("wait", bWait);

    if (bDoAll || bUseTotalDifference
        || parser.containsArg("iterations")
        || parser.containsArg("minimprovement")
        || parser.containsArg("multi")) {
      bTrain = true;
    }

    /* ---------- 解析语料库选项 ---------- */
    this.parseParametersForCorpusOptions(args, bArgumentRecognised);
    if (sLanguage.length() > 1) {
      Locale l = new Locale(sLanguage);
      Locale.setDefault(l);
    }

    for (int i = 0; i < args.length; ++i) {
      if (!bArgumentRecognised[i]) {
        System.out.println("Unrecognised command - wrong spelling or case?: " + args[i]);
        this.showBriefHelp();
        return;
      }
    }

    // 初始化语料库
    if (c.initialise()) {
      if (!sTextToParse.equals("")) {
        if (bURLEncoded) {
          sTextToParse = URLDecoder.decode(sTextToParse, StandardCharsets.UTF_8);
        } else {
          sTextToParse = sTextToParse.replace("+", " ");
        }

        this.parseOneText(c, sTextToParse, bURLEncoded);
      } else if (iListenPort > 0) {
        this.listenAtPort(c, iListenPort);
      } else if (bCmd) {
        this.listenForCmdInput(c);
      } else if (bStdIn) {
        this.listenToStdIn(c, iTextCol);
      } else if (!bWait) {
        if (!sOptimalTermStrengths.equals("")) {
          if (sInputFile.equals("")) {
            System.out.println("Input file must be specified to optimise term weights");
            return;
          }

          if (c.setCorpus(sInputFile)) {
            c.optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
            c.resources.sentimentWords.saveSentimentList(sOptimalTermStrengths, c);
            System.out.println("Saved optimised term weights to " + sOptimalTermStrengths);
          } else {
            System.out.println("Error: Too few texts in " + sInputFile);
          }
        } else if (bReportNewTermWeightsForBadClassifications) {
          if (c.setCorpus(sInputFile)) {
            c.printCorpusUnusedTermsClassificationIndex(FileOps.s_ChopFileNameExtension(sInputFile) + "_unusedTerms.txt", 1);
          } else {
            System.out.println("Error: Too few texts in " + sInputFile);
          }
        } else if (iTextCol > 0 && iIdCol > 0) {
          this.classifyAndSaveWithID(c, sInputFile, sInputFolder, iTextCol, iIdCol);
        } else if (iTextColForAnnotation > 0) {
          this.annotationTextCol(c, sInputFile, sInputFolder, sFileSubString, iTextColForAnnotation, bOkToOverwrite);
        } else {
          if (!sInputFolder.equals("")) {
            System.out.println("Input folder specified but textCol and IDcol or annotateCol needed");
          }

          if (sInputFile.equals("")) {
            System.out.println("No action taken because no input file nor text specified");
            this.showBriefHelp();
            return;
          }

          String sOutputFile = FileOps.getNextAvailableFilename(FileOps.s_ChopFileNameExtension(sInputFile), sResultsFileExtension);
          if (sResultsFolder.length() > 0) {
            sOutputFile = sResultsFolder + (new File(sOutputFile)).getName();
          }

          if (bTrain) {
            this.runMachineLearning(c, sInputFile, bDoAll, iMinImprovement, bUseTotalDifference, iIterations, iMultiOptimisations, sOutputFile);
          } else {
            --iTextCol;
            c.classifyAllLinesInInputFile(sInputFile, iTextCol, sOutputFile);
          }

          System.out.println("Finished! Results in: " + sOutputFile);
        }
      }
    } else {
      System.out.println("Failed to initialise!");

      try {
        File f = new File(c.resources.sgSentiStrengthFolder);
        if (!f.exists()) {
          System.out.println("Folder does not exist! " + c.resources.sgSentiStrengthFolder);
        }
      } catch (Exception var30) {
        System.out.println("Folder doesn't exist! " + c.resources.sgSentiStrengthFolder);
      }

      this.showBriefHelp();
    }

  }

  /**
   * 语料库参数解析。
   *
   * @param args                运行参数
   * @param bArgumentRecognised 参数 Bitset
   */
  private void parseParametersForCorpusOptions(String[] args, boolean[] bArgumentRecognised) {
    ArgParser parser = new ArgParser(bArgumentRecognised);

    String[] oneArgs = new String[]{
        "sentidata", "emotionlookuptable", "additionalfile",
        "illegalDoubleLettersInWordMiddle", "illegalDoubleLettersAtWordEnd",
        "lemmaFile",
    };

    String[] boolArgs = new String[]{
        "sentiment", "stress", "trinary", "binary", "scale",
        "questionsReduceNeg", "exclamations2", "negatingWordsOccurAfterSentiment",
        "alwaysSplitWordsAtApostrophes", "capitalsBoostTermSentiment",
        "explain", "echo", "UTF8"
    };

    String[] falseArgs = new String[]{
        "noBoosters", "noNegatingPositiveFlipsEmotion", "noNegatingNegativeNeutralisesEmotion",
        "noNegators", "noIdioms", "noEmoticons", "noMultiplePosWords",
        "noMultipleNegWords", "noIgnoreBoosterWordsAfterNegatives",
        "noDictionary", "noDeleteExtraDuplicateLetters", "noMultipleLetters",
        "negatingWordsDontOccurBeforeSentiment"
    };

    String[] intArgs = new String[]{
        "wordsBeforeKeywords", "wordsAfterKeywords",
        "sentenceCombineAv", "sentenceCombineTot", "paragraphCombineAv",
        "paragraphCombineTot", "minPunctuationWithExclamation", "mood",
        "maxWordsBeforeSentimentToNegate", "maxWordsAfterSentimentToNegate",
        "MinSentencePosForQuotesIrony", "MinSentencePosForPunctuationIrony",
        "MinSentencePosForTermsIrony",
        "MinSentencePosForAllIrony", // TODO
    };

    String[] doubleArgs = new String[]{
        "negativeMultiplier",
        "negatedWordStrengthMultiplier",
    };

    parser.addArgument("keywords", 1, (cur, as) -> {
      this.c.options.parseKeywordList(as[cur + 1].toLowerCase());
      return new ArgParser.Value(true);
    });

    this.c.resources.sgSentiStrengthFolder = parser.extract("sentidata", this.c.resources.sgSentiStrengthFolder);
    this.c.resources.sgSentimentWordsFile = parser.extract("emotionlookuptable", this.c.resources.sgSentimentWordsFile);
    this.c.resources.sgAdditionalFile = parser.extract("additionalfile", this.c.resources.sgAdditionalFile);
    this.c.options.igWordsToIncludeBeforeKeyword = parser.extract("wordsBeforeKeywords", this.c.options.igWordsToIncludeBeforeKeyword);
    this.c.options.igWordsToIncludeAfterKeyword = parser.extract("wordsAfterKeywords", this.c.options.igWordsToIncludeAfterKeyword);
    this.c.options.bgTrinaryMode = parser.extract("trinary", this.c.options.bgTrinaryMode);
    this.c.options.bgBinaryVersionOfTrinaryMode = parser.extract("binary", this.c.options.bgBinaryVersionOfTrinaryMode);
    this.c.options.bgScaleMode = parser.extract("scale", this.c.options.bgScaleMode);
    this.c.options.igEmotionSentenceCombineMethod = parser.extract("sentenceCombineAv", this.c.options.igEmotionSentenceCombineMethod);
    this.c.options.igEmotionSentenceCombineMethod = parser.extract("sentenceCombineTot", this.c.options.igEmotionSentenceCombineMethod);
    this.c.options.igEmotionParagraphCombineMethod = parser.extract("paragraphCombineAv", this.c.options.igEmotionParagraphCombineMethod);
    this.c.options.igEmotionParagraphCombineMethod = parser.extract("paragraphCombineTot", this.c.options.igEmotionParagraphCombineMethod);
    this.c.options.fgNegativeSentimentMultiplier = parser.extract("negativeMultiplier", this.c.options.fgNegativeSentimentMultiplier);
    this.c.options.bgBoosterWordsChangeEmotion = parser.extract("noBoosters", this.c.options.bgBoosterWordsChangeEmotion);
    this.c.options.bgNegatingPositiveFlipsEmotion = parser.extract("noNegatingPositiveFlipsEmotion", this.c.options.bgNegatingPositiveFlipsEmotion);
    this.c.options.bgNegatingNegativeNeutralisesEmotion = parser.extract("noNegatingNegativeNeutralisesEmotion", this.c.options.bgNegatingNegativeNeutralisesEmotion);
    this.c.options.bgNegatingWordsFlipEmotion = parser.extract("noNegators", this.c.options.bgNegatingWordsFlipEmotion);
    this.c.options.bgUseIdiomLookupTable = parser.extract("noIdioms", this.c.options.bgUseIdiomLookupTable);
    this.c.options.bgReduceNegativeEmotionInQuestionSentences = parser.extract("questionsReduceNeg", this.c.options.bgReduceNegativeEmotionInQuestionSentences);
    this.c.options.bgUseEmoticons = parser.extract("noEmoticons", this.c.options.bgUseEmoticons);
    this.c.options.bgExclamationInNeutralSentenceCountsAsPlus2 = parser.extract("exclamations2", this.c.options.bgExclamationInNeutralSentenceCountsAsPlus2);
    this.c.options.igMinPunctuationWithExclamationToChangeSentenceSentiment = parser.extract("minPunctuationWithExclamation", this.c.options.igMinPunctuationWithExclamationToChangeSentenceSentiment);
    this.c.options.igMoodToInterpretNeutralEmphasis = parser.extract("mood", this.c.options.igMoodToInterpretNeutralEmphasis);
    this.c.options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = parser.extract("noMultiplePosWords", this.c.options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion);
    this.c.options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = parser.extract("noMultipleNegWords", this.c.options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion);
    this.c.options.bgIgnoreBoosterWordsAfterNegatives = parser.extract("noIgnoreBoosterWordsAfterNegatives", this.c.options.bgIgnoreBoosterWordsAfterNegatives);
    this.c.options.bgCorrectSpellingsUsingDictionary = parser.extract("noDictionary", this.c.options.bgCorrectSpellingsUsingDictionary);
    this.c.options.bgCorrectExtraLetterSpellingErrors = parser.extract("noDeleteExtraDuplicateLetters", this.c.options.bgCorrectExtraLetterSpellingErrors);
    this.c.options.sgIllegalDoubleLettersInWordMiddle = parser.extract("illegalDoubleLettersInWordMiddle", this.c.options.sgIllegalDoubleLettersInWordMiddle);
    this.c.options.sgIllegalDoubleLettersAtWordEnd = parser.extract("illegalDoubleLettersAtWordEnd", this.c.options.sgIllegalDoubleLettersAtWordEnd);
    this.c.options.bgMultipleLettersBoostSentiment = parser.extract("noMultipleLetters", this.c.options.bgMultipleLettersBoostSentiment);
    this.c.options.fgStrengthMultiplierForNegatedWords = parser.extract("negatedWordStrengthMultiplier", this.c.options.fgStrengthMultiplierForNegatedWords);
    this.c.options.igMaxWordsBeforeSentimentToNegate = parser.extract("maxWordsBeforeSentimentToNegate", this.c.options.igMaxWordsBeforeSentimentToNegate);
    this.c.options.bgNegatingWordsOccurBeforeSentiment = parser.extract("negatingWordsDontOccurBeforeSentiment", this.c.options.bgNegatingWordsOccurBeforeSentiment);
    this.c.options.igMaxWordsAfterSentimentToNegate = parser.extract("maxWordsAfterSentimentToNegate", this.c.options.igMaxWordsAfterSentimentToNegate);
    this.c.options.bgNegatingWordsOccurAfterSentiment = parser.extract("negatingWordsOccurAfterSentiment", this.c.options.bgNegatingWordsOccurAfterSentiment);
    this.c.options.bgAlwaysSplitWordsAtApostrophes = parser.extract("alwaysSplitWordsAtApostrophes", this.c.options.bgAlwaysSplitWordsAtApostrophes);
    this.c.options.bgCapitalsBoostTermSentiment = parser.extract("capitalsBoostTermSentiment", this.c.options.bgCapitalsBoostTermSentiment);
    this.c.options.bgUseLemmatisation = parser.extract("lemmaFile", this.c.options.bgUseLemmatisation);
    this.c.options.igMinSentencePosForQuotesIrony = parser.extract("MinSentencePosForQuotesIrony", this.c.options.igMinSentencePosForQuotesIrony);
    this.c.options.igMinSentencePosForPunctuationIrony = parser.extract("MinSentencePosForPunctuationIrony", this.c.options.igMinSentencePosForPunctuationIrony);
    this.c.options.igMinSentencePosForTermsIrony = parser.extract("MinSentencePosForTermsIrony", this.c.options.igMinSentencePosForTermsIrony);
    this.c.options.igMinSentencePosForTermsIrony = parser.extract("MinSentencePosForAllIrony", this.c.options.igMinSentencePosForTermsIrony);
    this.c.options.bgExplainClassification = parser.extract("explain", this.c.options.bgExplainClassification);
    this.c.options.bgEchoText = parser.extract("echo", this.c.options.bgEchoText);
    this.c.options.bgForceUTF8 = parser.extract("UTF8", this.c.options.bgForceUTF8);

    if (this.c.options.bgTrinaryMode && this.c.options.bgScaleMode) {
      System.out.println("Must choose binary/trinary OR scale mode");
      return;
    }

    if (parser.extract("sentiment", false)) {
      this.c.options.nameProgram(false);
    }

    if (parser.extract("stress", false)) {
      this.c.options.nameProgram(true);
    }

    if (parser.containsArg("lemmaFile")) {
      this.c.options.bgUseLemmatisation = true;
    }

    if (parser.containsArg("MinSentencePosForAllIrony")) {
      this.c.options.igMinSentencePosForPunctuationIrony = this.c.options.igMinSentencePosForTermsIrony;
      this.c.options.igMinSentencePosForQuotesIrony = this.c.options.igMinSentencePosForTermsIrony;
    }


    for (int i = 0; i < args.length; ++i) {
      try {
        if (args[i].equalsIgnoreCase("sentidata")) {
          this.c.resources.sgSentiStrengthFolder = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("emotionlookuptable")) {
          this.c.resources.sgSentimentWordsFile = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("additionalfile")) {
          this.c.resources.sgAdditionalFile = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("keywords")) {
          this.c.options.parseKeywordList(args[i + 1].toLowerCase());
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("wordsBeforeKeywords")) {
          this.c.options.igWordsToIncludeBeforeKeyword = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("wordsAfterKeywords")) {
          this.c.options.igWordsToIncludeAfterKeyword = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("sentiment")) {
          this.c.options.nameProgram(false);
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("stress")) {
          this.c.options.nameProgram(true);
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("trinary")) {
          this.c.options.bgTrinaryMode = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("binary")) {
          this.c.options.bgBinaryVersionOfTrinaryMode = true;
          this.c.options.bgTrinaryMode = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("scale")) {
          this.c.options.bgScaleMode = true;
          bArgumentRecognised[i] = true;
          if (this.c.options.bgTrinaryMode) {
            System.out.println("Must choose binary/trinary OR scale mode");
            return;
          }
        }

        ClassificationOptions var10000;
        if (args[i].equalsIgnoreCase("sentenceCombineAv")) {
          this.c.options.igEmotionSentenceCombineMethod = 1;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("sentenceCombineTot")) {
          var10000 = this.c.options;
          this.c.options.getClass();
          var10000.igEmotionSentenceCombineMethod = 2;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("paragraphCombineAv")) {
          var10000 = this.c.options;
          this.c.options.getClass();
          var10000.igEmotionParagraphCombineMethod = 1;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("paragraphCombineTot")) {
          var10000 = this.c.options;
          this.c.options.getClass();
          var10000.igEmotionParagraphCombineMethod = 2;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("negativeMultiplier")) {
          this.c.options.fgNegativeSentimentMultiplier = Float.parseFloat(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("noBoosters")) {
          this.c.options.bgBoosterWordsChangeEmotion = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noNegatingPositiveFlipsEmotion")) {
          this.c.options.bgNegatingPositiveFlipsEmotion = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noNegatingNegativeNeutralisesEmotion")) {
          this.c.options.bgNegatingNegativeNeutralisesEmotion = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noNegators")) {
          this.c.options.bgNegatingWordsFlipEmotion = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noIdioms")) {
          this.c.options.bgUseIdiomLookupTable = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("questionsReduceNeg")) {
          this.c.options.bgReduceNegativeEmotionInQuestionSentences = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noEmoticons")) {
          this.c.options.bgUseEmoticons = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("exclamations2")) {
          this.c.options.bgExclamationInNeutralSentenceCountsAsPlus2 = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("minPunctuationWithExclamation")) {
          this.c.options.igMinPunctuationWithExclamationToChangeSentenceSentiment = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("mood")) {
          this.c.options.igMoodToInterpretNeutralEmphasis = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("noMultiplePosWords")) {
          this.c.options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noMultipleNegWords")) {
          this.c.options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noIgnoreBoosterWordsAfterNegatives")) {
          this.c.options.bgIgnoreBoosterWordsAfterNegatives = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noDictionary")) {
          this.c.options.bgCorrectSpellingsUsingDictionary = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("noDeleteExtraDuplicateLetters")) {
          this.c.options.bgCorrectExtraLetterSpellingErrors = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("illegalDoubleLettersInWordMiddle")) {
          this.c.options.sgIllegalDoubleLettersInWordMiddle = args[i + 1].toLowerCase();
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("illegalDoubleLettersAtWordEnd")) {
          this.c.options.sgIllegalDoubleLettersAtWordEnd = args[i + 1].toLowerCase();
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("noMultipleLetters")) {
          this.c.options.bgMultipleLettersBoostSentiment = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("negatedWordStrengthMultiplier")) {
          this.c.options.fgStrengthMultiplierForNegatedWords = Float.parseFloat(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("maxWordsBeforeSentimentToNegate")) {
          this.c.options.igMaxWordsBeforeSentimentToNegate = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("negatingWordsDontOccurBeforeSentiment")) {
          this.c.options.bgNegatingWordsOccurBeforeSentiment = false;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("maxWordsAfterSentimentToNegate")) {
          this.c.options.igMaxWordsAfterSentimentToNegate = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("negatingWordsOccurAfterSentiment")) {
          this.c.options.bgNegatingWordsOccurAfterSentiment = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("alwaysSplitWordsAtApostrophes")) {
          this.c.options.bgAlwaysSplitWordsAtApostrophes = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("capitalsBoostTermSentiment")) {
          this.c.options.bgCapitalsBoostTermSentiment = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("lemmaFile")) {
          this.c.options.bgUseLemmatisation = true;
          this.c.resources.sgLemmaFile = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("MinSentencePosForQuotesIrony")) {
          this.c.options.igMinSentencePosForQuotesIrony = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("MinSentencePosForPunctuationIrony")) {
          this.c.options.igMinSentencePosForPunctuationIrony = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("MinSentencePosForTermsIrony")) {
          this.c.options.igMinSentencePosForTermsIrony = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("MinSentencePosForAllIrony")) {
          this.c.options.igMinSentencePosForTermsIrony = Integer.parseInt(args[i + 1]);
          this.c.options.igMinSentencePosForPunctuationIrony = this.c.options.igMinSentencePosForTermsIrony;
          this.c.options.igMinSentencePosForQuotesIrony = this.c.options.igMinSentencePosForTermsIrony;
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("explain")) {
          this.c.options.bgExplainClassification = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("echo")) {
          this.c.options.bgEchoText = true;
          bArgumentRecognised[i] = true;
        }

        if (args[i].equalsIgnoreCase("UTF8")) {
          this.c.options.bgForceUTF8 = true;
          bArgumentRecognised[i] = true;
        }

      } catch (NumberFormatException var5) {
        System.out.println("Error in argument for " + args[i] + ". Integer expected!");
        return;
      } catch (Exception var6) {
        System.out.println("Error in argument for " + args[i] + ". Argument missing?");
        return;
      }
    }

  }

  /**
   * 仅初始化语料库。
   *
   * @param args 初始化参数
   */
  public void initialise(String[] args) {
    boolean[] bArgumentRecognised = new boolean[args.length];

    int i;
    for (i = 0; i < args.length; ++i) {
      bArgumentRecognised[i] = false;
    }

    this.parseParametersForCorpusOptions(args, bArgumentRecognised);

    for (i = 0; i < args.length; ++i) {
      if (!bArgumentRecognised[i]) {
        System.out.println("Unrecognised command - wrong spelling or case?: " + args[i]);
        this.showBriefHelp();
        return;
      }
    }

    if (!this.c.initialise()) {
      System.out.println("Failed to initialise!");
    }

  }

  /**
   * 计算情绪分数。
   *
   * @param sentence 输入语句
   * @return 分数结果:
   * <code>
   * &lt;iPos&gt; &lt;iNeg&gt; [&lt;iTrinary&gt;|&lt;iScale&gt;] &lt;sRationale&gt;
   * </code>
   */
  public String computeSentimentScores(String sentence) {
    Paragraph paragraph = new Paragraph();
    paragraph.setParagraph(sentence, this.c.resources, this.c.options);
    int iNeg = paragraph.getParagraphNegativeSentiment();
    int iPos = paragraph.getParagraphPositiveSentiment();
    int iTrinary = paragraph.getParagraphTrinarySentiment();
    int iScale = paragraph.getParagraphScaleSentiment();
    String sRationale = "";
    if (this.c.options.bgEchoText) {
      sRationale = " " + sentence;
    }

    if (this.c.options.bgExplainClassification) {
      sRationale = " " + paragraph.getClassificationRationale();
    }

    if (this.c.options.bgTrinaryMode) {
      return iPos + " " + iNeg + " " + iTrinary + sRationale;
    } else {
      return this.c.options.bgScaleMode ? iPos + " " + iNeg + " " + iScale + sRationale : iPos + " " + iNeg + sRationale;
    }
  }

  private void runMachineLearning(Corpus c, String sInputFile, boolean bDoAll, int iMinImprovement, boolean bUseTotalDifference, int iIterations, int iMultiOptimisations, String sOutputFile) {
    if (iMinImprovement < 1) {
      System.out.println("No action taken because min improvement < 1");
      this.showBriefHelp();
    } else {
      c.setCorpus(sInputFile);
      c.calculateCorpusSentimentScores();
      int corpusSize = c.getCorpusSize();
      if (c.options.bgTrinaryMode) {
        if (c.options.bgBinaryVersionOfTrinaryMode) {
          System.out.print("Before training, binary accuracy: " + c.getClassificationTrinaryNumberCorrect() + " " + (float) c.getClassificationTrinaryNumberCorrect() / (float) corpusSize * 100.0F + "%");
        } else {
          System.out.print("Before training, trinary accuracy: " + c.getClassificationTrinaryNumberCorrect() + " " + (float) c.getClassificationTrinaryNumberCorrect() / (float) corpusSize * 100.0F + "%");
        }
      } else if (c.options.bgScaleMode) {
        System.out.print("Before training, scale accuracy: " + c.getClassificationScaleNumberCorrect() + " " + (float) c.getClassificationScaleNumberCorrect() * 100.0F / (float) corpusSize + "% corr " + c.getClassificationScaleCorrelationWholeCorpus());
      } else {
        System.out.print("Before training, positive: " + c.getClassificationPositiveNumberCorrect() + " " + c.getClassificationPositiveAccuracyProportion() * 100.0F + "% negative " + c.getClassificationNegativeNumberCorrect() + " " + c.getClassificationNegativeAccuracyProportion() * 100.0F + "% ");
        System.out.print("   Positive corr: " + c.getClassificationPosCorrelationWholeCorpus() + " negative " + c.getClassificationNegCorrelationWholeCorpus());
      }

      System.out.println(" out of " + c.getCorpusSize());
      if (bDoAll) {
        System.out.println("Running " + iIterations + " iteration(s) of all options on file " + sInputFile + "; results in " + sOutputFile);
        c.run10FoldCrossValidationForAllOptionVariations(iMinImprovement, bUseTotalDifference, iIterations, iMultiOptimisations, sOutputFile);
      } else {
        System.out.println("Running " + iIterations + " iteration(s) for standard or selected options on file " + sInputFile + "; results in " + sOutputFile);
        c.run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iIterations, iMultiOptimisations, sOutputFile);
      }

    }
  }

  private void classifyAndSaveWithID(Corpus c, String sInputFile, String sInputFolder, int iTextCol, int iIdCol) {
    if (!sInputFile.equals("")) {
      c.classifyAllLinesAndRecordWithID(sInputFile, iTextCol - 1, iIdCol - 1, FileOps.s_ChopFileNameExtension(sInputFile) + "_classID.txt");
    } else {
      if (sInputFolder.equals("")) {
        System.out.println("No annotations done because no input file or folder specfied");
        this.showBriefHelp();
        return;
      }

      File folder = new File(sInputFolder);
      File[] listOfFiles = folder.listFiles();
      if (listOfFiles == null) {
        System.out.println("Incorrect or empty input folder specfied");
        this.showBriefHelp();
        return;
      }

      for (int i = 0; i < listOfFiles.length; ++i) {
        if (listOfFiles[i].isFile()) {
          System.out.println("Classify + save with ID: " + listOfFiles[i].getName());
          c.classifyAllLinesAndRecordWithID(sInputFolder + "/" + listOfFiles[i].getName(), iTextCol - 1, iIdCol - 1, sInputFolder + "/" + FileOps.s_ChopFileNameExtension(listOfFiles[i].getName()) + "_classID.txt");
        }
      }
    }

  }

  private void annotationTextCol(Corpus c, String sInputFile, String sInputFolder, String sFileSubString, int iTextColForAnnotation, boolean bOkToOverwrite) {
    if (!bOkToOverwrite) {
      System.out.println("Must include parameter overwrite to annotate");
    } else {
      if (!sInputFile.equals("")) {
        c.annotateAllLinesInInputFile(sInputFile, iTextColForAnnotation - 1);
      } else {
        if (sInputFolder.equals("")) {
          System.out.println("No annotations done because no input file or folder specfied");
          this.showBriefHelp();
          return;
        }
        File folder = new File(sInputFolder);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; ++i) {
          if (listOfFiles[i].isFile()) {
            if (!sFileSubString.equals("") && listOfFiles[i].getName().indexOf(sFileSubString) <= 0) {
              System.out.println("  Ignoring " + listOfFiles[i].getName());
            } else {
              System.out.println("Annotate: " + listOfFiles[i].getName());
              c.annotateAllLinesInInputFile(sInputFolder + "/" + listOfFiles[i].getName(), iTextColForAnnotation - 1);
            }
          }
        }
      }

    }
  }

  private void parseOneText(Corpus c, String sTextToParse, boolean bURLEncodedOutput) {
    Paragraph paragraph = new Paragraph();
    paragraph.setParagraph(sTextToParse, c.resources, c.options);
    int iNeg = paragraph.getParagraphNegativeSentiment();
    int iPos = paragraph.getParagraphPositiveSentiment();
    int iTrinary = paragraph.getParagraphTrinarySentiment();
    int iScale = paragraph.getParagraphScaleSentiment();
    String sRationale = "";
    if (c.options.bgEchoText) {
      sRationale = " " + sTextToParse;
    }

    if (c.options.bgExplainClassification) {
      sRationale = " " + paragraph.getClassificationRationale();
    }

    String sOutput = "";
    if (c.options.bgTrinaryMode) {
      sOutput = iPos + " " + iNeg + " " + iTrinary + sRationale;
    } else if (c.options.bgScaleMode) {
      sOutput = iPos + " " + iNeg + " " + iScale + sRationale;
    } else {
      sOutput = iPos + " " + iNeg + sRationale;
    }

    if (bURLEncodedOutput) {
      System.out.println(URLEncoder.encode(sOutput, StandardCharsets.UTF_8));
    } else if (c.options.bgForceUTF8) {
      System.out.println(new String(sOutput.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    } else {
      System.out.println(sOutput);
    }

  }

  private void listenToStdIn(Corpus c, int iTextCol) {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    String sTextToParse;
    try {
      while ((sTextToParse = stdin.readLine()) != null) {
        boolean bSuccess;
        if (sTextToParse.contains("#Change_TermWeight")) {
          String[] sData = sTextToParse.split("\t");
          bSuccess = c.resources.sentimentWords.setSentiment(sData[1], Integer.parseInt(sData[2]));
          if (bSuccess) {
            System.out.println("1");
          } else {
            System.out.println("0");
          }
        } else {
          int iPos = 1;
          bSuccess = false;
          int iTrinary = 0;
          int iScale = 0;
          Paragraph paragraph = new Paragraph();
          if (iTextCol > -1) {
            String[] sData = sTextToParse.split("\t");
            if (sData.length >= iTextCol) {
              paragraph.setParagraph(sData[iTextCol], c.resources, c.options);
            }
          } else {
            paragraph.setParagraph(sTextToParse, c.resources, c.options);
          }

          int iNeg = paragraph.getParagraphNegativeSentiment();
          iPos = paragraph.getParagraphPositiveSentiment();
          iTrinary = paragraph.getParagraphTrinarySentiment();
          iScale = paragraph.getParagraphScaleSentiment();
          String sRationale = "";
          String sOutput;
          if (c.options.bgEchoText) {
            sOutput = sTextToParse + "\t";
          } else {
            sOutput = "";
          }

          if (c.options.bgExplainClassification) {
            sRationale = paragraph.getClassificationRationale();
          }

          if (c.options.bgTrinaryMode) {
            sOutput = sOutput + iPos + "\t" + iNeg + "\t" + iTrinary + "\t" + sRationale;
          } else if (c.options.bgScaleMode) {
            sOutput = sOutput + iPos + "\t" + iNeg + "\t" + iScale + "\t" + sRationale;
          } else {
            sOutput = sOutput + iPos + "\t" + iNeg + "\t" + sRationale;
          }

          if (c.options.bgForceUTF8) {
            System.out.println(new String(sOutput.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
          } else {
            System.out.println(sOutput);
          }
        }
      }
    } catch (IOException var14) {
      System.out.println("Error reading input");
      var14.printStackTrace();
    }

  }

  private void listenForCmdInput(Corpus c) {
    BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    while (true) {
      while (true) {
        try {
          while (true) {
            String sTextToParse = stdin.readLine();
            if (sTextToParse.equalsIgnoreCase("@end")) {
              return;
            }

            Paragraph paragraph = new Paragraph();
            paragraph.setParagraph(sTextToParse, c.resources, c.options);
            int iNeg = paragraph.getParagraphNegativeSentiment();
            int iPos = paragraph.getParagraphPositiveSentiment();
            int iTrinary = paragraph.getParagraphTrinarySentiment();
            int iScale = paragraph.getParagraphScaleSentiment();
            String sRationale = "";
            if (c.options.bgEchoText) {
              sRationale = " " + sTextToParse;
            }

            if (c.options.bgExplainClassification) {
              sRationale = " " + paragraph.getClassificationRationale();
            }

            String sOutput;
            if (c.options.bgTrinaryMode) {
              sOutput = iPos + " " + iNeg + " " + iTrinary + sRationale;
            } else if (c.options.bgScaleMode) {
              sOutput = iPos + " " + iNeg + " " + iScale + sRationale;
            } else {
              sOutput = iPos + " " + iNeg + sRationale;
            }

            if (!c.options.bgForceUTF8) {
              System.out.println(sOutput);
            } else {
              System.out.println(new String(sOutput.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            }
          }
        } catch (IOException var13) {
          System.out.println(var13);
        }
      }
    }
  }

  private void listenAtPort(Corpus c, int iListenPort) {
    ServerSocket serverSocket = null;
    String decodedText = "";
    boolean var6 = false;

    try {
      serverSocket = new ServerSocket(iListenPort);
    } catch (IOException var23) {
      System.out.println("Could not listen on port " + iListenPort + " because\n" + var23.getMessage());
      return;
    }

    System.out.println("Listening on port: " + iListenPort + " IP: " + serverSocket.getInetAddress());

    while (true) {
      Socket clientSocket = null;

      try {
        clientSocket = serverSocket.accept();
      } catch (IOException var20) {
        System.out.println("Accept failed at port: " + iListenPort);
        return;
      }

      PrintWriter out;
      try {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
      } catch (IOException var19) {
        System.out.println("IOException clientSocket.getOutputStream " + var19.getMessage());
        var19.printStackTrace();
        return;
      }

      BufferedReader in;
      try {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      } catch (IOException var18) {
        System.out.println("IOException InputStreamReader " + var18.getMessage());
        var18.printStackTrace();
        return;
      }

      String inputLine;
      try {
        while ((inputLine = in.readLine()) != null) {
          if (inputLine.indexOf("GET /") == 0) {
            int lastSpacePos = inputLine.lastIndexOf(" ");
            if (lastSpacePos < 5) {
              lastSpacePos = inputLine.length();
            }

            decodedText = URLDecoder.decode(inputLine.substring(5, lastSpacePos), StandardCharsets.UTF_8);
            System.out.println("Analysis of text: " + decodedText);
            break;
          }

          if (inputLine.equals("MikeSpecialMessageToEnd.")) {
            break;
          }
        }
      } catch (IOException var24) {
        System.out.println("IOException " + var24.getMessage());
        var24.printStackTrace();
        decodedText = "";
      } catch (Exception var25) {
        System.out.println("Non-IOException " + var25.getMessage());
        decodedText = "";
      }

      int iPos = 1;
      int iNeg = 1;
      int iTrinary = 0;
      int iScale = 0;
      Paragraph paragraph = new Paragraph();
      paragraph.setParagraph(decodedText, c.resources, c.options);
      iNeg = paragraph.getParagraphNegativeSentiment();
      iPos = paragraph.getParagraphPositiveSentiment();
      iTrinary = paragraph.getParagraphTrinarySentiment();
      iScale = paragraph.getParagraphScaleSentiment();
      String sRationale = "";
      if (c.options.bgEchoText) {
        sRationale = " " + decodedText;
      }

      if (c.options.bgExplainClassification) {
        sRationale = " " + paragraph.getClassificationRationale();
      }

      String sOutput;
      if (c.options.bgTrinaryMode) {
        sOutput = iPos + " " + iNeg + " " + iTrinary + sRationale;
      } else if (c.options.bgScaleMode) {
        sOutput = iPos + " " + iNeg + " " + iScale + sRationale;
      } else {
        sOutput = iPos + " " + iNeg + sRationale;
      }

      if (c.options.bgForceUTF8) {
        out.print(new String(sOutput.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
      } else {
        out.print(sOutput);
      }

      try {
        out.close();
        in.close();
        clientSocket.close();
      } catch (IOException var21) {
        System.out.println("IOException closing streams or sockets" + var21.getMessage());
        var21.printStackTrace();
      }
    }
  }

  private void showBriefHelp() {
    System.out.println();
    System.out.println("====" + this.c.options.sgProgramName + "Brief Help====");
    System.out.println("For most operations, a minimum of two parameters must be set");
    System.out.println("1) folder location for the linguistic files");
    System.out.println("   e.g., on Windows: C:/mike/Lexical_Data/");
    System.out.println("   e.g., on Mac/Linux/Unix: /usr/Lexical_Data/");
    if (this.c.options.bgTensiStrength) {
      System.out.println("TensiiStrength_Data can be downloaded from...[not completed yet]");
    } else {
      System.out.println("SentiStrength_Data can be downloaded with the Windows version of SentiStrength from sentistrength.wlv.ac.uk");
    }

    System.out.println();
    System.out.println("2) text to be classified or file name of texts to be classified");
    System.out.println("   e.g., To classify one text: text love+u");
    System.out.println("   e.g., To classify a file of texts: input /bob/data.txt");
    System.out.println();
    System.out.println("Here is an example complete command:");
    if (this.c.options.bgTensiStrength) {
      System.out.println("java -jar TensiStrength.jar sentidata C:/a/Stress_Data/ text am+stressed");
    } else {
      System.out.println("java -jar SentiStrength.jar sentidata C:/a/SentStrength_Data/ text love+u");
    }

    System.out.println();
    if (!this.c.options.bgTensiStrength) {
      System.out.println("To list all commands: java -jar SentiStrength.jar help");
    }

  }

  /**
   * 打印 Help 信息。
   */
  private void printCommandLineOptions() {
    System.out.println("====" + this.c.options.sgProgramName + " Command Line Options====");
    System.out.println("=Source of data to be classified=");
    System.out.println(" text [text to process] OR");
    System.out.println(" input [filename] (each line of the file is classified SEPARATELY");
    System.out.println("        May have +ve 1st col., -ve 2nd col. in evaluation mode) OR");
    System.out.println(" annotateCol [col # 1..] (classify text in col, result at line end) OR");
    System.out.println(" textCol, idCol [col # 1..] (classify text in col, result & ID in new file) OR");
    System.out.println(" inputFolder  [foldername] (all files in folder will be *annotated*)");
    System.out.println(" outputFolder [foldername where to put the output (default: folder of input)]");
    System.out.println(" resultsExtension [file-extension for output (default _out.txt)]");
    System.out.println("  fileSubstring [text] (string must be present in files to annotate)");
    System.out.println("  Ok to overwrite files [overwrite]");
    System.out.println(" listen [port number to listen at - call http://127.0.0.1:81/text]");
    System.out.println(" cmd (wait for stdin input, write to stdout, terminate on input: @end");
    System.out.println(" stdin (read from stdin input, write to stdout, terminate when stdin finished)");
    System.out.println(" wait (just initialise; allow calls to public String computeSentimentScores)");
    System.out.println("=Linguistic data source=");
    System.out.println(" sentidata [folder for " + this.c.options.sgProgramName + " data (end in slash, no spaces)]");
    System.out.println("=Options=");
    System.out.println(" keywords [comma-separated list - " + this.c.options.sgProgramMeasuring + " only classified close to these]");
    System.out.println("   wordsBeforeKeywords [words to classify before keyword (default 4)]");
    System.out.println("   wordsAfterKeywords [words to classify after keyword (default 4)]");
    System.out.println(" trinary (report positive-negative-neutral classifcation instead)");
    System.out.println(" binary (report positive-negative classifcation instead)");
    System.out.println(" scale (report single -4 to +4 classifcation instead)");
    System.out.println(" emotionLookupTable [filename (default: EmotionLookupTable.txt)]");
    System.out.println(" additionalFile [filename] (domain-specific terms and evaluations)");
    System.out.println(" lemmaFile [filename] (word tab lemma list for lemmatisation)");
    System.out.println("=Classification algorithm parameters=");
    System.out.println(" noBoosters (ignore sentiment booster words (e.g., very))");
    System.out.println(" noNegators (don't use negating words (e.g., not) to flip sentiment) -OR-");
    System.out.println(" noNegatingPositiveFlipsEmotion (don't use negating words to flip +ve words)");
    System.out.println(" bgNegatingNegativeNeutralisesEmotion (negating words don't neuter -ve words)");
    System.out.println(" negatedWordStrengthMultiplier (strength multiplier when negated (default=0.5))");
    System.out.println(" negatingWordsOccurAfterSentiment (negate " + this.c.options.sgProgramMeasuring + " occurring before negatives)");
    System.out.println("  maxWordsAfterSentimentToNegate (max words " + this.c.options.sgProgramMeasuring + " to negator (default 0))");
    System.out.println(" negatingWordsDontOccurBeforeSentiment (don't negate " + this.c.options.sgProgramMeasuring + " after negatives)");
    System.out.println("   maxWordsBeforeSentimentToNegate (max from negator to " + this.c.options.sgProgramMeasuring + " (default 0))");
    System.out.println(" noIdioms (ignore idiom list)");
    System.out.println(" questionsReduceNeg (-ve sentiment reduced in questions)");
    System.out.println(" noEmoticons (ignore emoticon list)");
    System.out.println(" exclamations2 (sentence with ! counts as +2 if otherwise neutral)");
    System.out.println(" minPunctuationWithExclamation (min punctuation with ! to boost term " + this.c.options.sgProgramMeasuring + ")");
    System.out.println(" mood [-1,0,1] (default 1: -1 assume neutral emphasis is neg, 1, assume is pos");
    System.out.println(" noMultiplePosWords (multiple +ve words don't increase " + this.c.options.sgProgramPos + ")");
    System.out.println(" noMultipleNegWords (multiple -ve words don't increase " + this.c.options.sgProgramNeg + ")");
    System.out.println(" noIgnoreBoosterWordsAfterNegatives (don't ignore boosters after negating words)");
    System.out.println(" noDictionary (don't try to correct spellings using the dictionary)");
    System.out.println(" noMultipleLetters (don't use additional letters in a word to boost " + this.c.options.sgProgramMeasuring + ")");
    System.out.println(" noDeleteExtraDuplicateLetters (don't delete extra duplicate letters in words)");
    System.out.println(" illegalDoubleLettersInWordMiddle [letters never duplicate in word middles]");
    System.out.println("    default for English: ahijkquvxyz (specify list without spaces)");
    System.out.println(" illegalDoubleLettersAtWordEnd [letters never duplicate at word ends]");
    System.out.println("    default for English: achijkmnpqruvwxyz (specify list without spaces)");
    System.out.println(" sentenceCombineAv (average " + this.c.options.sgProgramMeasuring + " strength of terms in each sentence) OR");
    System.out.println(" sentenceCombineTot (total the " + this.c.options.sgProgramMeasuring + " strength of terms in each sentence)");
    System.out.println(" paragraphCombineAv (average " + this.c.options.sgProgramMeasuring + " strength of sentences in each text) OR");
    System.out.println(" paragraphCombineTot (total the " + this.c.options.sgProgramMeasuring + " strength of sentences in each text)");
    System.out.println("  *the default for the above 4 options is the maximum, not the total or average");
    System.out.println(" negativeMultiplier [negative total strength polarity multiplier, default 1.5]");
    System.out.println(" capitalsBoostTermSentiment (" + this.c.options.sgProgramMeasuring + " words in CAPITALS are stronger)");
    System.out.println(" alwaysSplitWordsAtApostrophes (e.g., t'aime -> t ' aime)");
    System.out.println(" MinSentencePosForQuotesIrony [integer] quotes in +ve sentences indicate irony");
    System.out.println(" MinSentencePosForPunctuationIrony [integer] +ve ending in !!+ indicates irony");
    System.out.println(" MinSentencePosForTermsIrony [integer] irony terms in +ve sent. indicate irony");
    System.out.println(" MinSentencePosForAllIrony [integer] all of the above irony terms");
    System.out.println(" lang [ISO-639 lower-case two-letter langauge code] set processing language");
    System.out.println("=Input and Output=");
    System.out.println(" explain (explain classification after results)");
    System.out.println(" echo (echo original text after results [for pipeline processes])");
    System.out.println(" UTF8 (force all processing to be in UTF-8 format)");
    System.out.println(" urlencoded (input and output text is URL encoded)");
    System.out.println("=Advanced - machine learning [1st input line ignored]=");
    System.out.println(" termWeights (list terms in badly classified texts; must specify inputFile)");
    System.out.println(" optimise [Filename for optimal term strengths (eg. EmotionLookupTable2.txt)]");
    System.out.println(" train (evaluate " + this.c.options.sgProgramName + " by training term strengths on results in file)");
    System.out.println("   all (test all option variations rather than use default)");
    System.out.println("   numCorrect (optimise by # correct - not total classification difference)");
    System.out.println("   iterations [number of 10-fold iterations] (default 1)");
    System.out.println("   minImprovement [min. accuracy improvement to change " + this.c.options.sgProgramMeasuring + " weights (default 1)]");
    System.out.println("   multi [# duplicate term strength optimisations to change " + this.c.options.sgProgramMeasuring + " weights (default 1)]");
  }

  /**
   * 获取语料库。
   *
   * @return 语料库
   */
  public Corpus getCorpus() {
    return this.c;
  }
}
