// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   ClassificationResources.java

package uk.ac.wlv.sentistrength;

import java.io.File;


import java.io.PrintStream;

import uk.ac.wlv.utilities.FileOps;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            EmoticonsList, CorrectSpellingsList, SentimentWords, NegatingWordList, 
//            QuestionWords, BoosterWordsList, IdiomList, EvaluativeTerms, 
//            IronyList, Lemmatiser, ClassificationOptions

/**
 * 分类器资源。
 */
public class ClassificationResources {

    /**
     * 表情词列表。
     */
    public EmoticonsList emoticons;
    /**
     * 单词正确拼写列表。
     */
    public CorrectSpellingsList correctSpellings;
    /**
     * 情感词列表类。
     */
    public SentimentWords sentimentWords;
    /**
     * 否定词列表。
     */
    public NegatingWordList negatingWords;
    /**
     * 疑问词列表。
     */
    public QuestionWords questionWords;
    /**
     * 助推词列表。
     */
    public BoosterWordsList boosterWords;
    /**
     * 习语列表。
     */
    public IdiomList idiomList;
    /**
     * 评估词，存放额外的 Object, Evaluation, Strength。
     */
    public EvaluativeTerms evaluativeTerms;
    /**
     * 反语词列表。
     */
    public IronyList ironyList;
    /**
     * 词形还原的词根与衍生词对应关系列表。
     */
    public Lemmatiser lemmatiser;
    /**
     * SentStrength_Data 文件路径。
     */
    public String sgSentiStrengthFolder;
    /**
     * 情感查询表的文件名："EmotionLookupTable.txt"。
     */
    public String sgSentimentWordsFile;
    /**
     * 情感查询表的文件名："SentimentLookupTable.txt"。<br/>
     * 此表是另一情感查询表 EmotionLookupTable 的可能替代表，此表暂不存在。
     */
    public String sgSentimentWordsFile2;
    /**
     * 表情词查询表的文件名："EmoticonLookupTable.txt"。
     */
    public String sgEmoticonLookupTable;
    /**
     * 正确拼写词表的文件名："Dictionary.txt"。<br/>
     * 此表是另一正确拼写词表 EnglishWordList 的可能替代表，此表暂不存在。
     */
    public String sgCorrectSpellingFileName;
    /**
     * 正确拼写词表的文件名："EnglishWordList.txt"。
     */
    public String sgCorrectSpellingFileName2;
    /**
     * 词语查询表的文件名："SlangLookupTable_NOT_USED.txt"。<br/>
     * 此表暂不存在，此表暂未使用。
     */
    public String sgSlangLookupTable;
    /**
     * 否定词表的文件名："NegatingWordList.txt"。
     */
    public String sgNegatingWordListFile;
    /**
     * 助推词表的文件名："BoosterWordList.txt"。
     */
    public String sgBoosterListFile;
    /**
     * 习语查询表的文件名："IdiomLookupTable.txt"。
     */
    public String sgIdiomLookupTableFile;
    /**
     * 疑问词表的文件名："QuestionWords.txt"。
     */
    public String sgQuestionWordListFile;
    /**
     * 反语词表的文件名："IronyTerms.txt"。
     */
    public String sgIronyWordListFile;
    /**
     * 可扩展文件的文件名，默认为：""。
     */
    public String sgAdditionalFile;
    /**
     * 可扩展词性还原表，默认为：""。
     */
    public String sgLemmaFile;

    /**
     * ClassificationResources 构造函数。
     */
    public ClassificationResources() {
        emoticons = new EmoticonsList();
        correctSpellings = new CorrectSpellingsList();
        sentimentWords = new SentimentWords();
        negatingWords = new NegatingWordList();
        questionWords = new QuestionWords();
        boosterWords = new BoosterWordsList();
        idiomList = new IdiomList();
        evaluativeTerms = new EvaluativeTerms();
        ironyList = new IronyList();
        lemmatiser = new Lemmatiser();
        sgSentiStrengthFolder = System.getProperty("user.dir") + "/src/SentStrength_Data/";
        sgSentimentWordsFile = "EmotionLookupTable.txt";
        sgSentimentWordsFile2 = "SentimentLookupTable.txt";
        sgEmoticonLookupTable = "EmoticonLookupTable.txt";
        sgCorrectSpellingFileName = "Dictionary.txt";
        sgCorrectSpellingFileName2 = "EnglishWordList.txt";
        sgSlangLookupTable = "SlangLookupTable_NOT_USED.txt";
        sgNegatingWordListFile = "NegatingWordList.txt";
        sgBoosterListFile = "BoosterWordList.txt";
        sgIdiomLookupTableFile = "IdiomLookupTable.txt";
        sgQuestionWordListFile = "QuestionWords.txt";
        sgIronyWordListFile = "IronyTerms.txt";
        sgAdditionalFile = "";
        sgLemmaFile = "";
    }

    /**
     * 分类资源初始化。
     * @param options 分类选项
     * @return 是否成功初始化
     */
    public boolean initialise(ClassificationOptions options) {
        int iExtraLinesToReserve = 0;
        // 检查是否添加扩展文件，并记录行数在 iExtraLinesToReserve 中。
        if (sgAdditionalFile.compareTo("") != 0) {
            iExtraLinesToReserve = FileOps.i_CountLinesInTextFile((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgAdditionalFile).toString());
            if (iExtraLinesToReserve < 0) {
                System.out.println((new StringBuilder("No lines found in additional file! Ignoring ")).append(sgAdditionalFile).toString());
                return false;
            }
        }
        if (options.bgUseLemmatisation && !lemmatiser.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgLemmaFile).toString(), false)) {
            System.out.println((new StringBuilder("Can't load lemma file! ")).append(sgLemmaFile).toString());
            return false;
        }
        // 载入存在的情感查询表
        File f = new File((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgSentimentWordsFile).toString());
        if (!f.exists() || f.isDirectory())
            sgSentimentWordsFile = sgSentimentWordsFile2;
        //载入存在的正确拼写词表
        File f2 = new File((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgCorrectSpellingFileName).toString());
        if (!f2.exists() || f2.isDirectory())
            sgCorrectSpellingFileName = sgCorrectSpellingFileName2;
        if (emoticons.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgEmoticonLookupTable).toString(), options)
                && correctSpellings.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgCorrectSpellingFileName).toString(), options)
                && sentimentWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgSentimentWordsFile).toString(), options, iExtraLinesToReserve)
                && negatingWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgNegatingWordListFile).toString(), options)
                && questionWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgQuestionWordListFile).toString(), options)
                && ironyList.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgIronyWordListFile).toString(), options)
                && boosterWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgBoosterListFile).toString(), options, iExtraLinesToReserve)
                && idiomList.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgIdiomLookupTableFile).toString(), options, iExtraLinesToReserve)) {
            if (iExtraLinesToReserve > 0)
                return evaluativeTerms.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgAdditionalFile).toString(), options, idiomList, sentimentWords);
            else
                return true;
        } else {
            return false;
        }
    }
}
