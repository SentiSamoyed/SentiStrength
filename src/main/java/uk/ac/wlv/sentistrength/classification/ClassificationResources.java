// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   ClassificationResources.java

package uk.ac.wlv.sentistrength.classification;

import common.SentiData;
import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.resource.EvaluativeTerms;
import uk.ac.wlv.sentistrength.classification.resource.Resource;
import uk.ac.wlv.sentistrength.classification.resource.concrete.*;
import uk.ac.wlv.sentistrength.classification.resource.factory.SingletonResourceFactory;
import uk.ac.wlv.sentistrength.classification.resource.factory.ResourceFactory;
import uk.ac.wlv.utilities.FileOps;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Objects;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            EmoticonsList, CorrectSpellingsList, SentimentWords, NegatingWordList, 
//            QuestionWords, BoosterWordsList, IdiomList, EvaluativeTerms, 
//            IronyList, Lemmatiser, ClassificationOptions

/**
 * 分类器资源。
 */
@Log4j2
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
  public String sentimentWordsFile;
  /**
   * 情感查询表的文件名："SentimentLookupTable.txt"。<br/>
   * 此表是另一情感查询表 EmotionLookupTable 的可能替代表，此表暂不存在。
   */
  public String sgSentimentWordsFile2;
  /**
   * 表情词查询表的文件名："EmoticonLookupTable.txt"。
   */
  public String emoticonsFile;
  /**
   * 正确拼写词表的文件名："Dictionary.txt"。<br/>
   * 此表是另一正确拼写词表 EnglishWordList 的可能替代表，此表暂不存在。
   */
  public String correctSpellingsFile;
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
  public String negatingWordsFile;
  /**
   * 助推词表的文件名："BoosterWordList.txt"。
   */
  public String boosterWordsFile;
  /**
   * 习语查询表的文件名："IdiomLookupTable.txt"。
   */
  public String idiomListFile;
  /**
   * 疑问词表的文件名："QuestionWords.txt"。
   */
  public String questionWordsFile;
  /**
   * 反语词表的文件名："IronyTerms.txt"。
   */
  public String ironyListFile;
  /**
   * 可扩展文件的文件名，默认为：""。
   */
  public String sgAdditionalFile;
  /**
   * 可扩展词性还原表，默认为：""。
   */
  public String lemmatiserFile;

  private final ResourceFactory resourceFactory;


  /**
   * ClassificationResources 构造函数。
   */
  public ClassificationResources() {
    this.resourceFactory = SingletonResourceFactory.getInstance();

    sgSentiStrengthFolder = SentiData.SENTI_DATA_DIR_PATH;
    evaluativeTerms = new EvaluativeTerms();

    sentimentWordsFile = "EmotionLookupTable.txt";
    sgSentimentWordsFile2 = "SentimentLookupTable.txt";
    emoticonsFile = "EmoticonLookupTable.txt";
    correctSpellingsFile = "Dictionary.txt";
    sgCorrectSpellingFileName2 = "EnglishWordList.txt";
    sgSlangLookupTable = "SlangLookupTable_NOT_USED.txt";
    negatingWordsFile = "NegatingWordList.txt";
    boosterWordsFile = "BoosterWordList.txt";
    idiomListFile = "IdiomLookupTable.txt";
    questionWordsFile = "QuestionWords.txt";
    ironyListFile = "IronyTerms.txt";
    sgAdditionalFile = "";
    lemmatiserFile = "";
  }

  /**
   * 分类资源初始化。
   *
   * @param options 分类选项
   * @return 是否成功初始化
   */
  @SuppressWarnings("unchecked")
  public boolean initialise(ClassificationOptions options) {
    int iExtraLinesToReserve = 0;

    // 检查是否添加扩展文件，并记录行数在 iExtraLinesToReserve 中。
    if (!sgAdditionalFile.isBlank()) {
      iExtraLinesToReserve = FileOps.i_CountLinesInTextFile(Path.of(sgSentiStrengthFolder, sgAdditionalFile).toString());
      if (iExtraLinesToReserve < 0) {
        System.out.println("No lines found in additional file! Ignoring " + sgAdditionalFile);
        return false;
      }
    }

    // 载入存在的情感查询表
    File f = new File(Path.of(sgSentiStrengthFolder, sentimentWordsFile).toString());
    if (!f.exists() || f.isDirectory()) {
      sentimentWordsFile = sgSentimentWordsFile2;
    }
    //载入存在的正确拼写词表
    File f2 = new File(Path.of(sgSentiStrengthFolder, correctSpellingsFile).toString());
    if (!f2.exists() || f2.isDirectory()) {
      correctSpellingsFile = sgCorrectSpellingFileName2;
    }

    /* 用反射加载所有资源 */
    Class<? extends ClassificationResources> me = this.getClass();
    try {
      for (Field field : me.getFields()) {
        Class<?> clazz = field.getType();
        Object val = field.get(this);
        // 只对未初始化的 WordList 子类 fields 赋值
        if (Objects.isNull(val) && Resource.class.isAssignableFrom(clazz)) {
          Class<? extends Resource> clazz1 = (Class<? extends Resource>) clazz;
          String name = field.getName();
          // 对应文件为 xxxFile
          String filename = (String) me.getField(name + "File").get(this);
          String path = Path.of(sgSentiStrengthFolder, filename).toString();
          // 调用工厂创建实例
          Resource instance = resourceFactory.buildResource(clazz1, path, options, iExtraLinesToReserve);
          field.set(this, instance);
        }
      }

    } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
      log.fatal(e.getLocalizedMessage());
      return false;
    }

    if (iExtraLinesToReserve > 0) {
      return evaluativeTerms.initialise(Path.of(sgSentiStrengthFolder, sgAdditionalFile).toString(), options, idiomList, sentimentWords);
    } else {
      return true;
    }
  }
}
