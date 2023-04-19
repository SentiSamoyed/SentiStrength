package uk.ac.wlv.sentistrength;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.core.Corpus;
import uk.ac.wlv.sentistrength.core.component.Paragraph;
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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SentiStrength 运行驱动类。
 */
@Log4j2
public class SentiStrength {
  Corpus c;

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
    Corpus corpus = this.c;

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
        log.fatal("Unrecognised command - wrong spelling or case?: " + args[i]);
        this.showBriefHelp();
        return;
      }
    }

    // 初始化语料库
    if (corpus.initialise()) {
      if (!sTextToParse.isEmpty()) {
        if (bURLEncoded) {
          sTextToParse = URLDecoder.decode(sTextToParse, StandardCharsets.UTF_8);
        } else {
          sTextToParse = sTextToParse.replace("+", " ");
        }

        this.parseOneText(corpus, sTextToParse, bURLEncoded);
      } else if (iListenPort > 0) {
        this.listenAtPort(corpus, iListenPort);
      } else if (bCmd) {
        this.listenForCmdInput(corpus);
      } else if (bStdIn) {
        this.listenToStdIn(corpus, iTextCol);
      } else if (!bWait) {
        if (!sOptimalTermStrengths.equals("")) {
          if (sInputFile.equals("")) {
            log.fatal("Input file must be specified to optimise term weights");
            return;
          }

          if (corpus.setCorpus(sInputFile)) {
            corpus.optimiseDictionaryWeightingsForCorpus(iMinImprovement, bUseTotalDifference);
            corpus.resources.sentimentWords.saveSentimentList(sOptimalTermStrengths, corpus);
            log.fatal("Saved optimised term weights to " + sOptimalTermStrengths);
          } else {
            log.fatal("Error: Too few texts in " + sInputFile);
          }
        } else if (bReportNewTermWeightsForBadClassifications) {
          if (corpus.setCorpus(sInputFile)) {
            corpus.printCorpusUnusedTermsClassificationIndex(FileOps.s_ChopFileNameExtension(sInputFile) + "_unusedTerms.txt", 1);
          } else {
            log.fatal("Error: Too few texts in " + sInputFile);
          }
        } else if (iTextCol > 0 && iIdCol > 0) {
          this.classifyAndSaveWithID(corpus, sInputFile, sInputFolder, iTextCol, iIdCol);
        } else if (iTextColForAnnotation > 0) {
          this.annotationTextCol(corpus, sInputFile, sInputFolder, sFileSubString, iTextColForAnnotation, bOkToOverwrite);
        } else {
          if (!sInputFolder.equals("")) {
            log.fatal("Input folder specified but textCol and IDcol or annotateCol needed");
          }

          if (sInputFile.equals("")) {
            log.fatal("No action taken because no input file nor text specified");
            this.showBriefHelp();
            return;
          }

          String sOutputFile = FileOps.getNextAvailableFilename(FileOps.s_ChopFileNameExtension(sInputFile), sResultsFileExtension);
          if (sResultsFolder.length() > 0) {
            sOutputFile = sResultsFolder + (new File(sOutputFile)).getName();
          }

          if (bTrain) {
            this.runMachineLearning(corpus, sInputFile, bDoAll, iMinImprovement, bUseTotalDifference, iIterations, iMultiOptimisations, sOutputFile);
          } else {
            --iTextCol;
            corpus.classifyAllLinesInInputFile(sInputFile, iTextCol, sOutputFile);
          }

          log.info("Finished! Results in: " + sOutputFile);
        }
      }
    } else {
      log.fatal("Failed to initialise!");

      try {
        File f = new File(corpus.resources.sgSentiStrengthFolder);
        if (!f.exists()) {
          log.fatal("Folder does not exist! " + corpus.resources.sgSentiStrengthFolder);
        }
      } catch (Exception var30) {
        log.fatal("Folder doesn't exist! " + corpus.resources.sgSentiStrengthFolder);
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
    for (String arg : oneArgs) {
      parser.addArgument(arg, 1, ArgParser.BOY_NEXT_DOOR);
    }

    String[] boolArgs = new String[]{
        "sentiment", "stress", "trinary", "binary", "scale",
        "questionsReduceNeg", "exclamations2", "negatingWordsOccurAfterSentiment",
        "alwaysSplitWordsAtApostrophes", "capitalsBoostTermSentiment",
        "explain", "echo", "UTF8"
    };
    for (String arg : boolArgs) {
      parser.addArgument(arg, 0, ArgParser.SET_TRUE);
    }

    String[] falseArgs = new String[]{
        "noBoosters", "noNegatingPositiveFlipsEmotion", "noNegatingNegativeNeutralisesEmotion",
        "noNegators", "noIdioms", "noEmoticons", "noMultiplePosWords",
        "noMultipleNegWords", "noIgnoreBoosterWordsAfterNegatives",
        "noDictionary", "noDeleteExtraDuplicateLetters", "noMultipleLetters",
        "negatingWordsDontOccurBeforeSentiment"
    };
    for (String arg : falseArgs) {
      parser.addArgument(arg, 0, ArgParser.SET_FALSE);
    }

    String[] intArgs = new String[]{
        "wordsBeforeKeywords", "wordsAfterKeywords",
        "sentenceCombineAv", "sentenceCombineTot", "paragraphCombineAv",
        "paragraphCombineTot", "minPunctuationWithExclamation", "mood",
        "maxWordsBeforeSentimentToNegate", "maxWordsAfterSentimentToNegate",
        "MinSentencePosForQuotesIrony", "MinSentencePosForPunctuationIrony",
        "MinSentencePosForTermsIrony",
        "MinSentencePosForAllIrony",
    };
    for (String arg : intArgs) {
      parser.addArgument(arg, 1, ArgParser.INT_NEXT_DOOR);
    }

    String[] doubleArgs = new String[]{
        "negativeMultiplier",
        "negatedWordStrengthMultiplier",
    };
    for (String arg : doubleArgs) {
      parser.addArgument(arg, 1, ArgParser.DOUBLE_NEXT_DOOR);
    }

    parser.addArgument("keywords", 1, (cur, as) -> {
      this.c.options.parseKeywordList(as[cur + 1].toLowerCase());
      return new ArgParser.Value(true);
    });

    boolean success = parser.parseArgs(args);
    if (!success) {
      return;
    }

    this.c.resources.sgSentiStrengthFolder = parser.extract("sentidata", this.c.resources.sgSentiStrengthFolder);
    this.c.resources.sentimentWordsFile = parser.extract("emotionlookuptable", this.c.resources.sentimentWordsFile);
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
      log.fatal("Must choose binary/trinary OR scale mode");
      throw new IllegalArgumentException("Must choose binary/trinary OR scale mode");
    }

    if (this.c.options.bgBinaryVersionOfTrinaryMode) {
      this.c.options.bgTrinaryMode = true;
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
  }

  /**
   * 仅初始化语料库。
   *
   * @param args 初始化参数
   */
  public void initialise(String[] args) {
    boolean[] bArgumentRecognised = new boolean[args.length];
    Arrays.fill(bArgumentRecognised, false);

    this.parseParametersForCorpusOptions(args, bArgumentRecognised);
    for (int i = 0; i < args.length; ++i) {
      if (!bArgumentRecognised[i]) {
        log.fatal("Unrecognised command - wrong spelling or case?: " + args[i]);
        this.showBriefHelp();
        return;
      }
    }

    if (!this.c.initialise()) {
      log.fatal("Failed to initialise!");
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
    SentimentScoreResult r = computeSentimentScoresSeparate(sentence);
    ClassificationOptions o = c.options;

    char sep = ' ';
    StringBuilder sb = new StringBuilder();
    sb.append(r.pos).append(sep).append(r.neg);

    if (o.bgTrinaryMode) { // Trinary mode & Binary mode
      sb.append(sep).append(r.trinary);
    } else if (o.bgScaleMode) { /// Scale mode
      sb.append(sep).append(r.scale);
    }

    if (o.bgExplainClassification) {
      sb.append(sep).append(r.rationale);
    }

    return sb.toString();
  }

  public record SentimentScoreResult(int neg, int pos, int trinary, int scale, String rationale) {
  }

  /**
   * 计算情绪分数，同 {@link SentiStrength#computeSentimentScores(String)}. 但返回结果对象。
   *
   * @return 计算结果
   */
  public SentimentScoreResult computeSentimentScoresSeparate(String sentence) {
    Paragraph paragraph = new Paragraph();
    paragraph.setParagraph(sentence, this.c.resources, this.c.options);

    int iNeg = paragraph.getParagraphNegativeSentiment();
    int iPos = paragraph.getParagraphPositiveSentiment();
    int iTrinary = paragraph.getParagraphTrinarySentiment();
    int iScale = paragraph.getParagraphScaleSentiment();

    String sRationale;
    if (this.c.options.bgExplainClassification) {
      sRationale = paragraph.getClassificationRationale();
    } else if (this.c.options.bgEchoText) {
      sRationale = sentence;
    } else {
      sRationale = "";
    }

    return new SentimentScoreResult(iNeg, iPos, iTrinary, iScale, sRationale);
  }

  private void runMachineLearning(Corpus c, String sInputFile, boolean bDoAll, int iMinImprovement, boolean bUseTotalDifference, int iIterations, int iMultiOptimisations, String sOutputFile) {
    if (iMinImprovement < 1) {
      log.fatal("No action taken because min improvement < 1");
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

      log.info(" out of " + c.getCorpusSize());
      if (bDoAll) {
        log.info("Running " + iIterations + " iteration(s) of all options on file " + sInputFile + "; results in " + sOutputFile);
        c.run10FoldCrossValidationForAllOptionVariations(iMinImprovement, bUseTotalDifference, iIterations, iMultiOptimisations, sOutputFile);
      } else {
        log.info("Running " + iIterations + " iteration(s) for standard or selected options on file " + sInputFile + "; results in " + sOutputFile);
        c.run10FoldCrossValidationMultipleTimes(iMinImprovement, bUseTotalDifference, iIterations, iMultiOptimisations, sOutputFile);
      }

    }
  }

  private void classifyAndSaveWithID(Corpus c, String sInputFile, String sInputFolder, int iTextCol, int iIdCol) {
    if (!sInputFile.isBlank()) {
      c.classifyAllLinesAndRecordWithID(sInputFile, iTextCol - 1, iIdCol - 1, FileOps.s_ChopFileNameExtension(sInputFile) + "_classID.txt");
      return;
    }

    if (sInputFolder.isBlank()) {
      log.fatal("No annotations done because no input file or folder specified");
      this.showBriefHelp();
      return;
    }

    File folder = new File(sInputFolder);
    File[] listOfFiles = folder.listFiles();
    if (listOfFiles == null) {
      log.fatal("Incorrect or empty input folder specified");
      this.showBriefHelp();
      return;
    }

    Arrays.stream(listOfFiles)
        .filter(File::isFile)
        .forEach(listOfFile -> {
          log.info("Classify + save with ID: " + listOfFile.getName());
          c.classifyAllLinesAndRecordWithID(sInputFolder + "/" + listOfFile.getName(), iTextCol - 1, iIdCol - 1, sInputFolder + "/" + FileOps.s_ChopFileNameExtension(listOfFile.getName()) + "_classID.txt");
        });
  }

  private void annotationTextCol(Corpus c, String sInputFile, String sInputFolder, String sFileSubString, int iTextColForAnnotation, boolean bOkToOverwrite) {
    if (!bOkToOverwrite) {
      log.fatal("Must include parameter overwrite to annotate");
      return;
    }


    if (!sInputFile.isBlank()) {
      c.annotateAllLinesInInputFile(sInputFile, iTextColForAnnotation - 1);
      return;
    }

    if (sInputFolder.isBlank()) {
      log.fatal("No annotations done because no input file or folder specified");
      this.showBriefHelp();
      return;
    }

    File folder = new File(sInputFolder);
    File[] listOfFiles = folder.listFiles();
    if (Objects.isNull(listOfFiles)) {
      log.fatal("输入文件夹异常: " + sInputFolder);
      return;
    }

    for (File listOfFile : listOfFiles) {
      if (listOfFile.isFile()) {
        if (!sFileSubString.equals("") && listOfFile.getName().indexOf(sFileSubString) <= 0) {
          log.info("  Ignoring " + listOfFile.getName());
        } else {
          log.info("Annotate: " + listOfFile.getName());
          c.annotateAllLinesInInputFile(sInputFolder + "/" + listOfFile.getName(), iTextColForAnnotation - 1);
        }
      }
    }
  }

  private void parseOneText(Corpus c, String sTextToParse, boolean bURLEncodedOutput) {
    String sOutput = computeSentimentScores(sTextToParse);

    if (bURLEncodedOutput) {
      System.out.println(URLEncoder.encode(sOutput, StandardCharsets.UTF_8));
    } else if (c.options.bgForceUTF8) {
      System.out.println(new String(sOutput.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    } else {
      System.out.println(sOutput);
    }

  }

  private void listenToStdIn(Corpus c, int iTextCol) {
    try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
      String textToParse;
      while (Objects.nonNull(textToParse = stdin.readLine())) {
        boolean bSuccess;

        // FIXME: 这是在做什么？
        if (textToParse.contains("#Change_TermWeight")) {
          String[] sData = textToParse.split("\t");
          bSuccess = c.resources.sentimentWords.setSentiment(sData[1], Integer.parseInt(sData[2]));
          if (bSuccess) {
            System.out.println("1");
          } else {
            System.out.println("0");
          }
          continue;
        }

        if (iTextCol > -1) {
          String[] sData = textToParse.split("\t");
          if (sData.length <= iTextCol) {
            log.fatal("该行列数小于 %d: \"%s\"".formatted(iTextCol + 1, textToParse));
            return;
          }
          textToParse = sData[iTextCol];
        }

        parseOneText(c, textToParse, false);
      }
    } catch (IOException e) {
      log.fatal(e);
    }
  }

  private void listenForCmdInput(Corpus c) {
    try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
      String textToParse;
      while (Objects.nonNull(textToParse = stdin.readLine())) {
        if (textToParse.equalsIgnoreCase("@end")) {
          return;
        }

        parseOneText(c, textToParse, false);
      }
    } catch (IOException e) {
      log.fatal(e);
    }
  }

  private void listenAtPort(Corpus c, int iListenPort) {
    try (ServerSocket serverSocket = new ServerSocket(iListenPort)) {
      log.info("Listening on port: " + iListenPort + " IP: " + serverSocket.getInetAddress());
      Pattern pattern = Pattern.compile("GET /(.*) HTTP.*");

      while (true) {
        try (Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
          String inputLine = in.readLine();
          try {
            if (Objects.isNull(inputLine)) {
              continue;
            }

            Matcher matcher = pattern.matcher(inputLine);
            if (!matcher.matches()) {
              throw new IllegalArgumentException();
            }
            String content = matcher.group(1);

            if (Objects.isNull(content)) {
              throw new IllegalArgumentException();
            }

            String decodedText = URLDecoder.decode(content, StandardCharsets.UTF_8);
            log.info("Analysis of text: " + decodedText);

            String sOutput = computeSentimentScores(decodedText);
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/plain");
            out.println("Content-Length: " + sOutput.getBytes().length);
            out.println();
            if (c.options.bgForceUTF8) {
              out.println(new String(sOutput.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            } else {
              out.println(sOutput);
            }

          } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Failed on text " + inputLine);
            out.println("HTTP/1.1 405 Method Not Allowed\n");
          }

        } catch (IOException e) {
          log.fatal("Accept failed at port: " + iListenPort);
          log.fatal(e.getLocalizedMessage());
        }
      }
    } catch (IOException e) {
      log.fatal("Could not listen on port " + iListenPort + " because\n" + e.getMessage());
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
