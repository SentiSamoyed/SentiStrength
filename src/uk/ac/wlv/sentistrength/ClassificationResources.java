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
 * 分类器资源
 */
public class ClassificationResources
{

    /**
     * 表情符号列表
     */
    public EmoticonsList emoticons;
    /**
     * 单词正确拼写列表
     */
    public CorrectSpellingsList correctSpellings;
    /**
     * 情感词列表类
     */
    public SentimentWords sentimentWords;
    /**
     * 否定词列表
     */
    public NegatingWordList negatingWords;
    /**
     * 问题词列表
     */
    public QuestionWords questionWords;
    /**
     * 助推词列表
     */
    public BoosterWordsList boosterWords;
    /**
     * 习语列表
     */
    public IdiomList idiomList;
    /**
     * 存放额外的Object,Evaluation,Strength
     */
    public EvaluativeTerms evaluativeTerms;
    /**
     * 反语词列表
     */
    public IronyList ironyList;
    /**
     * 词形还原的词根与衍生词对应关系列表
     */
    public Lemmatiser lemmatiser;
    /**
     * SentStrength_Data文件路径
     */
    public String sgSentiStrengthFolder;
    /**
     * "EmotionLookupTable.txt"
     */
    public String sgSentimentWordsFile;
    /**
     * "SentimentLookupTable.txt"
     */
    public String sgSentimentWordsFile2;
    /**
     * "EmoticonLookupTable.txt"
     */
    public String sgEmoticonLookupTable;
    /**
     * "Dictionary.txt"
     */
    public String sgCorrectSpellingFileName;
    /**
     * "EnglishWordList.txt"
     */
    public String sgCorrectSpellingFileName2;
    /**
     * "SlangLookupTable_NOT_USED.txt"
     */
    public String sgSlangLookupTable;
    /**
     * "NegatingWordList.txt"
     */
    public String sgNegatingWordListFile;
    /**
     * "BoosterWordList.txt"
     */
    public String sgBoosterListFile;
    /**
     * "IdiomLookupTable.txt"
     */
    public String sgIdiomLookupTableFile;
    /**
     * "QuestionWords.txt"
     */
    public String sgQuestionWordListFile;
    /**
     * "IronyTerms.txt"
     */
    public String sgIronyWordListFile;
    /**
     * 扩展文件的文件名，默认为""
     */
    public String sgAdditionalFile;
    /**
     * 扩展词形还原的词根与衍生词对应关系列表，默认为""
     */
    public String sgLemmaFile;

    /**
     * ClassificationResources构造函数
     */
    public ClassificationResources()
    {
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
        sgSentiStrengthFolder = System.getProperty("user.dir")+"/src/SentStrength_Data/";
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
     * @param options 分类器选项
     * @return 是否成功初始化
     */
    public boolean initialise(ClassificationOptions options)
    {   
    	int iExtraLinesToReserve = 0;
        if(sgAdditionalFile.compareTo("") != 0)
        {
            iExtraLinesToReserve = FileOps.i_CountLinesInTextFile((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgAdditionalFile).toString());
            if(iExtraLinesToReserve < 0)
            {
                System.out.println((new StringBuilder("No lines found in additional file! Ignoring ")).append(sgAdditionalFile).toString());
                return false;
            }
        }
        if(options.bgUseLemmatisation && !lemmatiser.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgLemmaFile).toString(), false))
        {
            System.out.println((new StringBuilder("Can't load lemma file! ")).append(sgLemmaFile).toString());
            return false;
        }
        File f = new File((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgSentimentWordsFile).toString());
        if(!f.exists() || f.isDirectory())
            sgSentimentWordsFile = sgSentimentWordsFile2;
        File f2 = new File((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgCorrectSpellingFileName).toString());
        if(!f2.exists() || f2.isDirectory())
            sgCorrectSpellingFileName = sgCorrectSpellingFileName2;
        if(emoticons.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgEmoticonLookupTable).toString(), options) && correctSpellings.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgCorrectSpellingFileName).toString(), options) && sentimentWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgSentimentWordsFile).toString(), options, iExtraLinesToReserve) && negatingWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgNegatingWordListFile).toString(), options) && questionWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgQuestionWordListFile).toString(), options) && ironyList.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgIronyWordListFile).toString(), options) && boosterWords.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgBoosterListFile).toString(), options, iExtraLinesToReserve) && idiomList.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgIdiomLookupTableFile).toString(), options, iExtraLinesToReserve))
        {
            if(iExtraLinesToReserve > 0)
                return evaluativeTerms.initialise((new StringBuilder(String.valueOf(sgSentiStrengthFolder))).append(sgAdditionalFile).toString(), options, idiomList, sentimentWords);
            else
                return true;
        } else
        {
            return false;
        }
    }
}
