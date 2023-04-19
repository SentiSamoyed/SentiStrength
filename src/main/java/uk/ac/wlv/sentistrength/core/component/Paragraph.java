package uk.ac.wlv.sentistrength.core.component;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;
import uk.ac.wlv.sentistrength.core.UnusedTermsClassificationIndex;
import uk.ac.wlv.sentistrength.option.TextParsingOptions;
import uk.ac.wlv.utilities.Sort;
import uk.ac.wlv.utilities.StringIndex;
import uk.ac.wlv.wkaclass.Arff;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 段落类
 */
public class Paragraph {
  private Sentence[] sentence;
  private int igSentenceCount = 0;
  private int[] igSentimentIDList;
  private int igSentimentIDListCount = 0;
  private boolean bSentimentIDListMade = false;
  private int igPositiveSentiment = 0;
  private int igNegativeSentiment = 0;
  private int igTrinarySentiment = 0;
  private int igScaleSentiment = 0;
  private ClassificationResources resources;
  private ClassificationOptions options;
  private final Random generator = new Random();
  private String sgClassificationRationale = "";

  /**
   * 用消极和积极分值将段落添加到索引。
   *
   * @param unusedTermsClassificationIndex 目标索引
   * @param iCorrectPosClass               正确积极分类
   * @param iEstPosClass                   预估积极分类
   * @param iCorrectNegClass               正确消极分类
   * @param iEstNegClass                   预估消极分类
   */
  public void addParagraphToIndexWithPosNegValues(UnusedTermsClassificationIndex unusedTermsClassificationIndex, int iCorrectPosClass, int iEstPosClass, int iCorrectNegClass, int iEstNegClass) {
    for (int i = 1; i <= this.igSentenceCount; ++i) {
      this.sentence[i].addSentenceToIndex(unusedTermsClassificationIndex);
    }

    unusedTermsClassificationIndex.addNewIndexToMainIndexWithPosNegValues(iCorrectPosClass, iEstPosClass, iCorrectNegClass, iEstNegClass);
  }

  /**
   * 用 Scale Values 将段落添加到索引。
   *
   * @param unusedTermsClassificationIndex 目标索引
   * @param iCorrectScaleClass             正确 scale 分类
   * @param iEstScaleClass                 预估 scale 分类
   */
  public void addParagraphToIndexWithScaleValues(UnusedTermsClassificationIndex unusedTermsClassificationIndex, int iCorrectScaleClass, int iEstScaleClass) {
    for (int i = 1; i <= this.igSentenceCount; ++i) {
      this.sentence[i].addSentenceToIndex(unusedTermsClassificationIndex);
    }

    unusedTermsClassificationIndex.addNewIndexToMainIndexWithScaleValues(iCorrectScaleClass, iEstScaleClass);
  }

  /**
   * 用 Binary Values 将段落添加到索引。
   *
   * @param unusedTermsClassificationIndex 目标索引
   * @param iCorrectBinaryClass            正确 binary 分类
   * @param iEstBinaryClass                预估 binary 分类
   */
  public void addParagraphToIndexWithBinaryValues(UnusedTermsClassificationIndex unusedTermsClassificationIndex, int iCorrectBinaryClass, int iEstBinaryClass) {
    for (int i = 1; i <= this.igSentenceCount; ++i) {
      this.sentence[i].addSentenceToIndex(unusedTermsClassificationIndex);
    }

    unusedTermsClassificationIndex.addNewIndexToMainIndexWithBinaryValues(iCorrectBinaryClass, iEstBinaryClass);
  }

  /**
   * 将段落添加到字符串索引。
   *
   * @param stringIndex        目标索引
   * @param textParsingOptions 文本解析选项
   * @param bRecordCount       是否作为记录统计
   * @param bArffIndex         是 Arff Index
   * @return Checked terms 数
   * @see Arff
   */
  public int addToStringIndex(StringIndex stringIndex, TextParsingOptions textParsingOptions, boolean bRecordCount, boolean bArffIndex) {
    return IntStream.range(0, this.igSentenceCount)
        .map(i -> this.sentence[i].addToStringIndex(stringIndex, textParsingOptions, bRecordCount, bArffIndex))
        .sum();
  }

  /**
   * 用 Trinary Values 将段落添加到索引。
   *
   * @param unusedTermsClassificationIndex 目标索引
   * @param iCorrectTrinaryClass           正确 trinary 分类
   * @param iEstTrinaryClass               预估 trinary 分类
   */
  public void addParagraphToIndexWithTrinaryValues(UnusedTermsClassificationIndex unusedTermsClassificationIndex, int iCorrectTrinaryClass, int iEstTrinaryClass) {
    for (int i = 1; i <= this.igSentenceCount; ++i) {
      this.sentence[i].addSentenceToIndex(unusedTermsClassificationIndex);
    }

    unusedTermsClassificationIndex.addNewIndexToMainIndexWithTrinaryValues(iCorrectTrinaryClass, iEstTrinaryClass);
  }

  /**
   * 设置段落
   *
   * @param sParagraph               段落字符串
   * @param classResources           分类需要的资源
   * @param newClassificationOptions 分类选项
   */
  public void setParagraph(String sParagraph, ClassificationResources classResources, ClassificationOptions newClassificationOptions) {
    this.resources = classResources;
    this.options = newClassificationOptions;
    if (sParagraph.contains("\"")) {
      sParagraph = sParagraph.replace("\"", "'");
    }

    int iSentenceEnds = 2;

    /* 分句 */
    final String[] delimiters = new String[]{
        "<br>", ".", "!", "?"
    };

    for (String delim : delimiters) {
      for (int pos = 0; pos >= 0 && pos < sParagraph.length(); ) {
        pos = sParagraph.indexOf(delim, pos);
        if (pos >= 0) {
          pos += delim.length();
          ++iSentenceEnds;
        }
      }
    }

    this.sentence = new Sentence[iSentenceEnds];
    this.igSentenceCount = 0;
    int iLastSentenceEnd = -1;
    boolean bPunctuationIndicatesSentenceEnd = false;
    int iNextBr = sParagraph.indexOf("<br>");

    for (int iPos = 0; iPos < sParagraph.length(); ++iPos) {
      String sNextChar = sParagraph.substring(iPos, iPos + 1);

      String sNextSentence = "";
      if (iPos == sParagraph.length() - 1) {
        sNextSentence = sParagraph.substring(iLastSentenceEnd + 1);
      } else if (iPos == iNextBr) {
        sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
        iLastSentenceEnd = iPos + 3;
        iNextBr = sParagraph.indexOf("<br>", iNextBr + 2);
      } else if (this.isSentenceEndPunctuation(sNextChar)) {
        bPunctuationIndicatesSentenceEnd = true;
      } else if (sNextChar.isBlank()) {
        if (bPunctuationIndicatesSentenceEnd) {
          sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
          iLastSentenceEnd = iPos;
        }
      } else if (this.isAlphanumeric(sNextChar) && bPunctuationIndicatesSentenceEnd) {
        sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
        iLastSentenceEnd = iPos - 1;
      }

      if (!sNextSentence.isEmpty()) {
        ++this.igSentenceCount;
        this.sentence[this.igSentenceCount] = new Sentence();
        this.sentence[this.igSentenceCount].setSentence(sNextSentence, this.resources, this.options);
        bPunctuationIndicatesSentenceEnd = false;
      }
    }

  }

  /**
   * 获取 Sentiment ID 列表。
   *
   * @return Sentiment ID 列表
   */
  public int[] getSentimentIDList() {
    if (!this.bSentimentIDListMade) {
      this.makeSentimentIDList();
    }

    return this.igSentimentIDList;
  }

  /**
   * 获取分类原因
   *
   * @return 分类原因
   */
  public String getClassificationRationale() {
    return this.sgClassificationRationale;
  }

  /**
   * 创建 Sentiment ID 列表。
   */
  public void makeSentimentIDList() {
    this.igSentimentIDListCount =
        Arrays.stream(this.sentence, 1, this.igSentenceCount + 1)
            .filter(Objects::nonNull)
            .mapToInt(s -> s.getSentimentIDList().length)
            .sum();

    this.igSentimentIDList = Arrays.stream(this.sentence, 1, this.igSentenceCount + 1)
        .filter(Objects::nonNull)
        .map(Sentence::getSentimentIDList)
        .flatMapToInt(Arrays::stream)
        .distinct()
        .sorted()
        .toArray();

    this.igSentimentIDListCount = this.igSentimentIDList.length;
    if (this.igSentimentIDListCount > 0) {
      this.bSentimentIDListMade = true;
    }
  }

  /**
   * 获取 HTML Tag 表示的段落
   *
   * @return 段落
   * @see Sentence#getTaggedSentence()
   */
  public String getTaggedParagraph() {
    return Arrays.stream(this.sentence, 1, this.igSentenceCount + 1)
        .map(Sentence::getTaggedSentence)
        .collect(Collectors.joining());
  }

  /**
   * 获取翻译段落。
   *
   * @return 翻译段落
   * @see Sentence#getTranslatedSentence()
   */
  public String getTranslatedParagraph() {
    return Arrays.stream(this.sentence, 1, this.igSentenceCount + 1)
        .map(Sentence::getTranslatedSentence)
        .collect(Collectors.joining());
  }

  /**
   * 重计算段落情感分值
   *
   * @see Sentence#recalculateSentenceSentimentScore()
   */
  public void recalculateParagraphSentimentScores() {
    Arrays.stream(this.sentence, 1, this.igSentenceCount + 1)
        .forEach(Sentence::recalculateSentenceSentimentScore);
    this.calculateParagraphSentimentScores();
  }

  /**
   * 对情感变化单词，重计算段落情感分类
   *
   * @param iSentimentWordID 变化情感的单词 ID
   * @see Sentence#reClassifyClassifiedSentenceForSentimentChange(int)
   */
  public void reClassifyClassifiedParagraphForSentimentChange(int iSentimentWordID) {
    if (this.igNegativeSentiment == 0) {
      this.calculateParagraphSentimentScores();
    } else {
      if (!this.bSentimentIDListMade) {
        this.makeSentimentIDList();
      }
      if (this.igSentimentIDListCount != 0) {
        if (Sort.i_FindIntPositionInSortedArray(iSentimentWordID, this.igSentimentIDList, 1, this.igSentimentIDListCount) >= 0) {
          for (int iSentence = 1; iSentence <= this.igSentenceCount; ++iSentence) {
            this.sentence[iSentence].reClassifyClassifiedSentenceForSentimentChange(iSentimentWordID);
          }
          this.calculateParagraphSentimentScores();
        }

      }
    }
  }

  /**
   * 获取段落的积极情感分值。
   */
  public int getParagraphPositiveSentiment() {
    if (this.igPositiveSentiment == 0) {
      this.calculateParagraphSentimentScores();
    }
    return this.igPositiveSentiment;
  }

  /**
   * 获取段落的消极情感分值。
   */
  public int getParagraphNegativeSentiment() {
    if (this.igNegativeSentiment == 0) {
      this.calculateParagraphSentimentScores();
    }
    return this.igNegativeSentiment;
  }

  /**
   * 获取段落的 trinary 情感分值。
   */
  public int getParagraphTrinarySentiment() {
    if (this.igNegativeSentiment == 0) {
      this.calculateParagraphSentimentScores();
    }
    return this.igTrinarySentiment;
  }

  /**
   * 获取段落的 scale 情感分值。
   */
  public int getParagraphScaleSentiment() {
    if (this.igNegativeSentiment == 0) {
      this.calculateParagraphSentimentScores();
    }
    return this.igScaleSentiment;
  }

  private boolean isSentenceEndPunctuation(String sChar) {
    return sChar.compareTo(".") == 0 || sChar.compareTo("!") == 0 || sChar.compareTo("?") == 0;
  }

  private boolean isAlphanumeric(String sChar) {
    return sChar.compareToIgnoreCase("a") >= 0 && sChar.compareToIgnoreCase("z") <= 0 || sChar.compareTo("0") >= 0 && sChar.compareTo("9") <= 0 || sChar.compareTo("$") == 0 || sChar.compareTo("£") == 0 || sChar.compareTo("'") == 0;
  }

  private void calculateParagraphSentimentScores() {
    this.igPositiveSentiment = 1;
    this.igNegativeSentiment = -1;
    this.igTrinarySentiment = 0;
    if (this.options.bgExplainClassification && this.sgClassificationRationale.length() > 0) {
      this.sgClassificationRationale = "";
    }

    if (this.igSentenceCount == 0) {
      return;
    }

    StringBuilder rationale = new StringBuilder(this.sgClassificationRationale);
    calculateParagraphSentimentScores(rationale);
    if (options.bgExplainClassification) {
      this.sgClassificationRationale = rationale.toString();
    }
  }

  private void calculateParagraphSentimentScores(StringBuilder rationale) {
    int iPosTotal = 0;
    int iPosMax = 0;
    int iNegTotal = 0;
    int iNegMax = 0;
    int iSentencesUsed = 0;
    for (int i = 1; i <= this.igSentenceCount; ++i) {
      Sentence sen = this.sentence[i];
      int iNegTemp = sen.getSentenceNegativeSentiment();
      int iPosTemp = sen.getSentencePositiveSentiment();
      /*
       * Useless codes
       * wordNum += this.sentence[iNegTot].getIgTermCount();
       * sentiNum += this.sentence[iNegTot].getIgSentiCount();
       */
      if (iNegTemp != 0 || iPosTemp != 0) {
        iNegTotal += iNegTemp;
        ++iSentencesUsed;
        if (iNegMax > iNegTemp) {
          iNegMax = iNegTemp;
        }

        iPosTotal += iPosTemp;
        if (iPosMax < iPosTemp) {
          iPosMax = iPosTemp;
        }
      }

      rationale.append(sen.getClassificationRationale()).append(' ');
    }

    int combineMethod = this.options.igEmotionParagraphCombineMethod;
    if (iNegTotal == 0) {
      if (combineMethod != 2) {
        this.igPositiveSentiment = 0;
        this.igNegativeSentiment = 0;
        this.igTrinarySentiment = this.binarySelectionTieBreaker();
        return;
      }
    }

    if (combineMethod == 1) {
      this.igPositiveSentiment = (int) ((double) ((float) iPosTotal / (float) iSentencesUsed) + 0.5D);
      this.igNegativeSentiment = (int) ((double) ((float) iNegTotal / (float) iSentencesUsed) - 0.5D);
      rationale.append("[result = average (%d and %d) of %d sentences]".formatted(iPosTotal, iNegTotal, iSentencesUsed));
    } else {
      if (combineMethod == 2) {
        this.igPositiveSentiment = iPosTotal;
        this.igNegativeSentiment = iNegTotal;
        rationale.append("[result: total positive; total negative]");
      } else {
        this.igPositiveSentiment = iPosMax;
        this.igNegativeSentiment = iNegMax;
        rationale.append("[result: max + and - of any sentence]");
      }
    }

    if (combineMethod != 2) {
      if (this.igPositiveSentiment == 0) {
        this.igPositiveSentiment = 1;
      }

      if (this.igNegativeSentiment == 0) {
        this.igNegativeSentiment = -1;
      }
    }

    if (this.options.bgScaleMode) {
      this.igScaleSentiment = this.igPositiveSentiment + this.igNegativeSentiment;
      rationale.append("[scale result = sum of pos and neg scores]");
      return;
    }

    // Not scale mode
    if (combineMethod == 2) {
      if (this.igPositiveSentiment == 0 && this.igNegativeSentiment == 0) {
        if (this.options.bgBinaryVersionOfTrinaryMode) {
          this.igTrinarySentiment = this.options.igDefaultBinaryClassification;
          rationale.append("[binary result set to default value]");
        } else {
          this.igTrinarySentiment = 0;
          rationale.append("[trinary result 0 as pos=1, neg=-1]");
        }
      } else {
        if ((float) this.igPositiveSentiment > this.options.fgNegativeSentimentMultiplier * (float) (-this.igNegativeSentiment)) {
          this.igTrinarySentiment = 1;
          rationale
              .append("[overall result 1 as pos > -neg * ")
              .append(this.options.fgNegativeSentimentMultiplier)
              .append("]");
          return;
        }

        if ((float) this.igPositiveSentiment < this.options.fgNegativeSentimentMultiplier * (float) (-this.igNegativeSentiment)) {
          this.igTrinarySentiment = -1;
          rationale
              .append("[overall result -1 as pos < -neg * ")
              .append(this.options.fgNegativeSentimentMultiplier)
              .append("]");
          return;
        }

        if (this.options.bgBinaryVersionOfTrinaryMode) {
          this.igTrinarySentiment = this.options.igDefaultBinaryClassification;
          rationale
              .append("[binary result = default value as pos = -neg * ")
              .append(this.options.fgNegativeSentimentMultiplier)
              .append("]");
        } else {
          this.igTrinarySentiment = 0;
          rationale
              .append("[trinary result = 0 as pos = -neg * ")
              .append(this.options.fgNegativeSentimentMultiplier)
              .append("]");
        }
      }
    } else {
      if (this.igPositiveSentiment == 1 && this.igNegativeSentiment == -1) {
        if (this.options.bgBinaryVersionOfTrinaryMode) {
          this.igTrinarySentiment = this.binarySelectionTieBreaker();
          rationale.append("[binary result = default value as pos=1 neg=-1]");
        } else {
          this.igTrinarySentiment = 0;
          rationale.append("[trinary result = 0 as pos=1 neg=-1]");
        }

        return;
      }

      if (this.igPositiveSentiment > -this.igNegativeSentiment) {
        this.igTrinarySentiment = 1;
        rationale.append("[overall result = 1 as pos>-neg]");

        return;
      }

      if (this.igPositiveSentiment < -this.igNegativeSentiment) {
        this.igTrinarySentiment = -1;
        rationale.append("[overall result = -1 as pos<-neg]");

        return;
      }

      int iNegTot = 0;
      int iPosTot = 0;

      for (int iSentence = 1; iSentence <= this.igSentenceCount; ++iSentence) {
        iNegTot += this.sentence[iSentence].getSentenceNegativeSentiment();
        iPosTot = this.sentence[iSentence].getSentencePositiveSentiment();
      }

      if (this.options.bgBinaryVersionOfTrinaryMode && iPosTot == -iNegTot) {
        this.igTrinarySentiment = this.binarySelectionTieBreaker();
        rationale.append("[binary result = default as posSentenceTotal>-negSentenceTotal]");
      } else {
        rationale.append("[overall result = largest of posSentenceTotal, negSentenceTotal]");

        if (iPosTot > -iNegTot) {
          this.igTrinarySentiment = 1;
        } else {
          this.igTrinarySentiment = -1;
        }
      }
    }
  }

  private int binarySelectionTieBreaker() {
    if (this.options.igDefaultBinaryClassification != 1 && this.options.igDefaultBinaryClassification != -1) {
      return this.generator.nextDouble() > 0.5D ? 1 : -1;
    } else {
      return this.options.igDefaultBinaryClassification;
    }
  }
}
