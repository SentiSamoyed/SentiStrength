package uk.ac.wlv.sentistrength;

import java.util.Random;

import uk.ac.wlv.utilities.Sort;
import uk.ac.wlv.utilities.StringIndex;
import uk.ac.wlv.wkaclass.Arff;

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
    int iTermsChecked = 0;

    for (int i = 1; i <= this.igSentenceCount; ++i) {
      iTermsChecked += this.sentence[i].addToStringIndex(stringIndex, textParsingOptions, bRecordCount, bArffIndex);
    }

    return iTermsChecked;
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
    int iPos = 0;

    while (iPos >= 0 && iPos < sParagraph.length()) {
      iPos = sParagraph.indexOf("<br>", iPos);
      if (iPos >= 0) {
        iPos += 3;
        ++iSentenceEnds;
      }
    }

    iPos = 0;

    while (iPos >= 0 && iPos < sParagraph.length()) {
      iPos = sParagraph.indexOf(".", iPos);
      if (iPos >= 0) {
        ++iPos;
        ++iSentenceEnds;
      }
    }

    iPos = 0;

    while (iPos >= 0 && iPos < sParagraph.length()) {
      iPos = sParagraph.indexOf("!", iPos);
      if (iPos >= 0) {
        ++iPos;
        ++iSentenceEnds;
      }
    }

    iPos = 0;

    while (iPos >= 0 && iPos < sParagraph.length()) {
      iPos = sParagraph.indexOf("?", iPos);
      if (iPos >= 0) {
        ++iPos;
        ++iSentenceEnds;
      }
    }

    this.sentence = new Sentence[iSentenceEnds];
    this.igSentenceCount = 0;
    int iLastSentenceEnd = -1;
    boolean bPunctuationIndicatesSentenceEnd = false;
    int iNextBr = sParagraph.indexOf("<br>");
    String sNextSentence = "";

    for (iPos = 0; iPos < sParagraph.length(); ++iPos) {
      String sNextChar = sParagraph.substring(iPos, iPos + 1);
      if (iPos == sParagraph.length() - 1) {
        sNextSentence = sParagraph.substring(iLastSentenceEnd + 1);
      } else if (iPos == iNextBr) {
        sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
        iLastSentenceEnd = iPos + 3;
        iNextBr = sParagraph.indexOf("<br>", iNextBr + 2);
      } else if (this.b_IsSentenceEndPunctuation(sNextChar)) {
        bPunctuationIndicatesSentenceEnd = true;
      } else if (sNextChar.compareTo(" ") == 0) {
        if (bPunctuationIndicatesSentenceEnd) {
          sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
          iLastSentenceEnd = iPos;
        }
      } else if (this.b_IsAlphanumeric(sNextChar) && bPunctuationIndicatesSentenceEnd) {
        sNextSentence = sParagraph.substring(iLastSentenceEnd + 1, iPos);
        iLastSentenceEnd = iPos - 1;
      }

      if (!sNextSentence.equals("")) {
        ++this.igSentenceCount;
        this.sentence[this.igSentenceCount] = new Sentence();
        this.sentence[this.igSentenceCount].setSentence(sNextSentence, this.resources, this.options);
        sNextSentence = "";
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
    boolean bIsDuplicate = false;
    this.igSentimentIDListCount = 0;

    int i;
    for (i = 1; i <= this.igSentenceCount; ++i) {
      if (this.sentence[i].getSentimentIDList() != null) {
        this.igSentimentIDListCount += this.sentence[i].getSentimentIDList().length;
      }
    }

    if (this.igSentimentIDListCount > 0) {
      this.igSentimentIDList = new int[this.igSentimentIDListCount + 1];
      this.igSentimentIDListCount = 0;

      for (i = 1; i <= this.igSentenceCount; ++i) {
        int[] sentenceIDList = this.sentence[i].getSentimentIDList();
        if (sentenceIDList != null) {
          for (int j = 1; j < sentenceIDList.length; ++j) {
            if (sentenceIDList[j] != 0) {
              bIsDuplicate = false;

              for (int k = 1; k <= this.igSentimentIDListCount; ++k) {
                if (sentenceIDList[j] == this.igSentimentIDList[k]) {
                  bIsDuplicate = true;
                  break;
                }
              }

              if (!bIsDuplicate) {
                this.igSentimentIDList[++this.igSentimentIDListCount] = sentenceIDList[j];
              }
            }
          }
        }
      }

      Sort.quickSortInt(this.igSentimentIDList, 1, this.igSentimentIDListCount);
    }

    this.bSentimentIDListMade = true;
  }

  /**
   * 获取 HTML Tag 表示的段落
   *
   * @return 段落
   * @see Sentence#getTaggedSentence()
   */
  public String getTaggedParagraph() {
    StringBuilder sTagged = new StringBuilder();
    for (int i = 1; i <= this.igSentenceCount; ++i) {
      sTagged.append(this.sentence[i].getTaggedSentence());
    }
    return sTagged.toString();
  }

  /**
   * 获取翻译段落。
   *
   * @return 翻译段落
   * @see Sentence#getTranslatedSentence()
   */
  public String getTranslatedParagraph() {
    StringBuilder sTranslated = new StringBuilder();
    for (int i = 1; i <= this.igSentenceCount; ++i) {
      sTranslated.append(this.sentence[i].getTranslatedSentence());
    }
    return sTranslated.toString();
  }

  /**
   * 重计算段落情感分值
   *
   * @see Sentence#recalculateSentenceSentimentScore()
   */
  public void recalculateParagraphSentimentScores() {
    for (int iSentence = 1; iSentence <= this.igSentenceCount; ++iSentence) {
      this.sentence[iSentence].recalculateSentenceSentimentScore();
    }
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

  private boolean b_IsSentenceEndPunctuation(String sChar) {
    return sChar.compareTo(".") == 0 || sChar.compareTo("!") == 0 || sChar.compareTo("?") == 0;
  }

  private boolean b_IsAlphanumeric(String sChar) {
    return sChar.compareToIgnoreCase("a") >= 0 && sChar.compareToIgnoreCase("z") <= 0 || sChar.compareTo("0") >= 0 && sChar.compareTo("9") <= 0 || sChar.compareTo("$") == 0 || sChar.compareTo("£") == 0 || sChar.compareTo("'") == 0;
  }

  private void calculateParagraphSentimentScores() {
    this.igPositiveSentiment = 1;
    this.igNegativeSentiment = -1;
    this.igTrinarySentiment = 0;
    if (this.options.bgExplainClassification && this.sgClassificationRationale.length() > 0) {
      this.sgClassificationRationale = "";
    }

    int iPosTotal = 0;
    int iPosMax = 0;
    int iNegTotal = 0;
    int iNegMax = 0;
    int iPosTemp = 0;
    int iNegTemp = 0;
    int iSentencesUsed = 0;
    int wordNum = 0;
    int sentiNum = 0;
    if (this.igSentenceCount != 0) {
      int iNegTot;
      for (iNegTot = 1; iNegTot <= this.igSentenceCount; ++iNegTot) {
        iNegTemp = this.sentence[iNegTot].getSentenceNegativeSentiment();
        iPosTemp = this.sentence[iNegTot].getSentencePositiveSentiment();
        wordNum += this.sentence[iNegTot].getIgTermCount();
        sentiNum += this.sentence[iNegTot].getIgSentiCount();
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

        if (this.options.bgExplainClassification) {
          this.sgClassificationRationale = this.sgClassificationRationale + this.sentence[iNegTot].getClassificationRationale() + " ";
        }
      }

      int var10000;
      if (iNegTotal == 0) {
        var10000 = this.options.igEmotionParagraphCombineMethod;
        this.options.getClass();
        if (var10000 != 2) {
          this.igPositiveSentiment = 0;
          this.igNegativeSentiment = 0;
          this.igTrinarySentiment = this.binarySelectionTieBreaker();
          return;
        }
      }

      var10000 = this.options.igEmotionParagraphCombineMethod;
      this.options.getClass();
      if (var10000 == 1) {
        this.igPositiveSentiment = (int) ((double) ((float) iPosTotal / (float) iSentencesUsed) + 0.5D);
        this.igNegativeSentiment = (int) ((double) ((float) iNegTotal / (float) iSentencesUsed) - 0.5D);
        if (this.options.bgExplainClassification) {
          this.sgClassificationRationale = this.sgClassificationRationale + "[result = average (" + iPosTotal + " and " + iNegTotal + ") of " + iSentencesUsed + " sentences]";
        }
      } else {
        var10000 = this.options.igEmotionParagraphCombineMethod;
        this.options.getClass();
        if (var10000 == 2) {
          this.igPositiveSentiment = iPosTotal;
          this.igNegativeSentiment = iNegTotal;
          if (this.options.bgExplainClassification) {
            this.sgClassificationRationale = this.sgClassificationRationale + "[result: total positive; total negative]";
          }
        } else {
          this.igPositiveSentiment = iPosMax;
          this.igNegativeSentiment = iNegMax;
          if (this.options.bgExplainClassification) {
            this.sgClassificationRationale = this.sgClassificationRationale + "[result: max + and - of any sentence]";
          }
        }
      }

      var10000 = this.options.igEmotionParagraphCombineMethod;
      this.options.getClass();
      if (var10000 != 2) {
        if (this.igPositiveSentiment == 0) {
          this.igPositiveSentiment = 1;
        }

        if (this.igNegativeSentiment == 0) {
          this.igNegativeSentiment = -1;
        }
      }

      if (this.options.bgScaleMode) {
        this.igScaleSentiment = this.igPositiveSentiment + this.igNegativeSentiment;
        if (this.options.bgExplainClassification) {
          this.sgClassificationRationale = this.sgClassificationRationale + "[scale result = sum of pos and neg scores]";
        }

      } else {
        var10000 = this.options.igEmotionParagraphCombineMethod;
        this.options.getClass();
        if (var10000 == 2) {
          if (this.igPositiveSentiment == 0 && this.igNegativeSentiment == 0) {
            if (this.options.bgBinaryVersionOfTrinaryMode) {
              this.igTrinarySentiment = this.options.igDefaultBinaryClassification;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[binary result set to default value]";
              }
            } else {
              this.igTrinarySentiment = 0;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[trinary result 0 as pos=1, neg=-1]";
              }
            }
          } else {
            if ((float) this.igPositiveSentiment > this.options.fgNegativeSentimentMultiplier * (float) (-this.igNegativeSentiment)) {
              this.igTrinarySentiment = 1;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[overall result 1 as pos > -neg * " + this.options.fgNegativeSentimentMultiplier + "]";
              }

              return;
            }

            if ((float) this.igPositiveSentiment < this.options.fgNegativeSentimentMultiplier * (float) (-this.igNegativeSentiment)) {
              this.igTrinarySentiment = -1;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[overall result -1 as pos < -neg * " + this.options.fgNegativeSentimentMultiplier + "]";
              }

              return;
            }

            if (this.options.bgBinaryVersionOfTrinaryMode) {
              this.igTrinarySentiment = this.options.igDefaultBinaryClassification;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[binary result = default value as pos = -neg * " + this.options.fgNegativeSentimentMultiplier + "]";
              }
            } else {
              this.igTrinarySentiment = 0;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[trinary result = 0 as pos = -neg * " + this.options.fgNegativeSentimentMultiplier + "]";
              }
            }
          }
        } else {
          if (this.igPositiveSentiment == 1 && this.igNegativeSentiment == -1) {
            if (this.options.bgBinaryVersionOfTrinaryMode) {
              this.igTrinarySentiment = this.binarySelectionTieBreaker();
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[binary result = default value as pos=1 neg=-1]";
              }
            } else {
              this.igTrinarySentiment = 0;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[trinary result = 0 as pos=1 neg=-1]";
              }
            }

            return;
          }

          if (this.igPositiveSentiment > -this.igNegativeSentiment) {
            this.igTrinarySentiment = 1;
            if (this.options.bgExplainClassification) {
              this.sgClassificationRationale = this.sgClassificationRationale + "[overall result = 1 as pos>-neg]";
            }

            return;
          }

          if (this.igPositiveSentiment < -this.igNegativeSentiment) {
            this.igTrinarySentiment = -1;
            if (this.options.bgExplainClassification) {
              this.sgClassificationRationale = this.sgClassificationRationale + "[overall result = -1 as pos<-neg]";
            }

            return;
          }

          iNegTot = 0;
          int iPosTot = 0;

          for (int iSentence = 1; iSentence <= this.igSentenceCount; ++iSentence) {
            iNegTot += this.sentence[iSentence].getSentenceNegativeSentiment();
            iPosTot = this.sentence[iSentence].getSentencePositiveSentiment();
          }

          if (this.options.bgBinaryVersionOfTrinaryMode && iPosTot == -iNegTot) {
            this.igTrinarySentiment = this.binarySelectionTieBreaker();
            if (this.options.bgExplainClassification) {
              this.sgClassificationRationale = this.sgClassificationRationale + "[binary result = default as posSentenceTotal>-negSentenceTotal]";
            }
          } else {
            if (this.options.bgExplainClassification) {
              this.sgClassificationRationale = this.sgClassificationRationale + "[overall result = largest of posSentenceTotal, negSentenceTotal]";
            }

            if (iPosTot > -iNegTot) {
              this.igTrinarySentiment = 1;
            } else {
              this.igTrinarySentiment = -1;
            }
          }
        }

      }
    }
  }

  private int binarySelectionTieBreaker() {
    if (this.options.igDefaultBinaryClassification != 1 && this.options.igDefaultBinaryClassification != -1) {
      return this.generator.nextDouble() > 0.5D ? 1 : -1;
    } else {
      return this.options.igDefaultBinaryClassification != 1 && this.options.igDefaultBinaryClassification != -1 ? this.options.igDefaultBinaryClassification : this.options.igDefaultBinaryClassification;
    }
  }
}
