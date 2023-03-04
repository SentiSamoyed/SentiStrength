//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package uk.ac.wlv.sentistrength;

import uk.ac.wlv.utilities.Sort;
import uk.ac.wlv.utilities.StringIndex;
import uk.ac.wlv.wkaclass.Arff;

/**
 * 语句类
 */
public class Sentence {
  private Term[] term;
  private boolean[] bgSpaceAfterTerm;
  private int igTermCount = 0;

  /**
   * 获取语句中词语的数量
   * @return 词语的数量
   */
  public int getIgTermCount() {
    return igTermCount;
  }

  /**
   * 获取语句中语气词的数量
   * @return 语气词的数量
   */
  public int getIgSentiCount() {
    return igSentiCount;
  }
  private int igSentiCount = 0;
  private int igPositiveSentiment = 0;
  private int igNegativeSentiment = 0;
  private boolean bgNothingToClassify = true;
  private ClassificationResources resources;
  private ClassificationOptions options;
  private int[] igSentimentIDList;
  private int igSentimentIDListCount = 0;
  private boolean bSentimentIDListMade = false;
  private boolean[] bgIncludeTerm;
  private boolean bgIdiomsApplied = false;
  private boolean bgObjectEvaluationsApplied = false;
  private String sgClassificationRationale = "";

  public Sentence() {
  }

  /**
   * 将语句添加到未使用词语的分类索引
   * @param unusedTermClassificationIndex 未使用词语的分类索引
   */
  public void addSentenceToIndex(UnusedTermsClassificationIndex unusedTermClassificationIndex) {
    for(int i = 1; i <= this.igTermCount; ++i) {
      unusedTermClassificationIndex.addTermToNewTermIndex(this.term[i].getText());
    }

  }

  /**
   * 将语句添加到字符串索引
   * @param stringIndex 字符串索引
   * @param textParsingOptions 文本解析选项
   * @param bRecordCount 是否计数
   * @param bArffIndex 是否是arff文件索引
   * @return 语句中经过检验的词语数量
   */
  public int addToStringIndex(StringIndex stringIndex, TextParsingOptions textParsingOptions, boolean bRecordCount, boolean bArffIndex) {
    String sEncoded = "";
    int iStringPos = 1;
    int iTermsChecked = 0;
    if (textParsingOptions.bgIncludePunctuation && textParsingOptions.igNgramSize == 1 && !textParsingOptions.bgUseTranslations && !textParsingOptions.bgAddEmphasisCode) {
      for(int i = 1; i <= this.igTermCount; ++i) {
        stringIndex.addString(this.term[i].getText(), bRecordCount);
      }

      iTermsChecked = this.igTermCount;
    } else {
      String sText = "";
      int iCurrentTerm = 0;
      int iTermCount = 0;

      while(iCurrentTerm < this.igTermCount) {
        ++iCurrentTerm;
        if (textParsingOptions.bgIncludePunctuation || !this.term[iCurrentTerm].isPunctuation()) {
          ++iTermCount;
          if (iTermCount > 1) {
            sText = sText + " ";
          } else {
            sText = "";
          }

          if (textParsingOptions.bgUseTranslations) {
            sText = sText + this.term[iCurrentTerm].getTranslation();
          } else {
            sText = sText + this.term[iCurrentTerm].getOriginalText();
          }

          if (textParsingOptions.bgAddEmphasisCode && this.term[iCurrentTerm].containsEmphasis()) {
            sText = sText + "+";
          }
        }

        if (iTermCount == textParsingOptions.igNgramSize) {
          if (bArffIndex) {
            sEncoded = Arff.arffSafeWordEncode(sText.toLowerCase(), false);
            iStringPos = stringIndex.findString(sEncoded);
            iTermCount = 0;
            if (iStringPos > -1) {
              stringIndex.add1ToCount(iStringPos);
            }
          } else {
            stringIndex.addString(sText.toLowerCase(), bRecordCount);
            iTermCount = 0;
          }

          iCurrentTerm += 1 - textParsingOptions.igNgramSize;
          ++iTermsChecked;
        }
      }
    }

    return iTermsChecked;
  }

  /**
   * 设定一条语句
   * @param sSentence 语句文本
   * @param classResources 分类资源
   * @param newClassificationOptions 新的分类选项
   */
  public void setSentence(String sSentence, ClassificationResources classResources, ClassificationOptions newClassificationOptions) {
    this.resources = classResources;
    this.options = newClassificationOptions;
    if (this.options.bgAlwaysSplitWordsAtApostrophes && sSentence.indexOf("'") >= 0) {
      sSentence = sSentence.replace("'", " ");
    }

    String[] sSegmentList = sSentence.split(" ");
    int iSegmentListLength = sSegmentList.length;
    int iMaxTermListLength = sSentence.length() + 1;
    this.term = new Term[iMaxTermListLength];
    this.bgSpaceAfterTerm = new boolean[iMaxTermListLength];
    int iPos = 0;
    this.igTermCount = 0;

    for(int iSegment = 0; iSegment < iSegmentListLength; ++iSegment) {
      for(iPos = 0; iPos >= 0 && iPos < sSegmentList[iSegment].length(); this.bgSpaceAfterTerm[this.igTermCount] = false) {
        this.term[++this.igTermCount] = new Term();
        int iOffset = this.term[this.igTermCount].extractNextWordOrPunctuationOrEmoticon(sSegmentList[iSegment].substring(iPos), this.resources, this.options);
        if (iOffset < 0) {
          iPos = iOffset;
        } else {
          iPos += iOffset;
        }
      }

      this.bgSpaceAfterTerm[this.igTermCount] = true;
    }

    this.bgSpaceAfterTerm[this.igTermCount] = false;
  }

  /**
   * 获取语气词ID列表
   * @return 语气词ID列表
   */
  public int[] getSentimentIDList() {
    if (!this.bSentimentIDListMade) {
      this.makeSentimentIDList();
    }

    return this.igSentimentIDList;
  }

  /**
   * 生成语气词ID列表
   */
  public void makeSentimentIDList() {
    int iSentimentIDTemp = 0;
    this.igSentimentIDListCount = 0;

    int i;
    for(i = 1; i <= this.igTermCount; ++i) {
      if (this.term[i].getSentimentID() > 0) {
        ++this.igSentimentIDListCount;
      }
    }

    if (this.igSentimentIDListCount > 0) {
      this.igSentimentIDList = new int[this.igSentimentIDListCount + 1];
      this.igSentimentIDListCount = 0;

      for(i = 1; i <= this.igTermCount; ++i) {
        iSentimentIDTemp = this.term[i].getSentimentID();
        if (iSentimentIDTemp > 0) {
          for(int j = 1; j <= this.igSentimentIDListCount; ++j) {
            if (iSentimentIDTemp == this.igSentimentIDList[j]) {
              iSentimentIDTemp = 0;
              break;
            }
          }

          if (iSentimentIDTemp > 0) {
            this.igSentimentIDList[++this.igSentimentIDListCount] = iSentimentIDTemp;
          }
        }
      }

      Sort.quickSortInt(this.igSentimentIDList, 1, this.igSentimentIDListCount);
    }

    this.bSentimentIDListMade = true;
  }

  /**
   * 获取标记好的语句
   * @return 被标记的语句
   */
  public String getTaggedSentence() {
    String sTagged = "";

    for(int i = 1; i <= this.igTermCount; ++i) {
      if (this.bgSpaceAfterTerm[i]) {
        sTagged = sTagged + this.term[i].getTag() + " ";
      } else {
        sTagged = sTagged + this.term[i].getTag();
      }
    }

    return sTagged + "<br>";
  }

  /**
   * 获取分类的原理解释
   * @return 分类的原理解释
   */
  public String getClassificationRationale() {
    return this.sgClassificationRationale;
  }

  /**
   * 获取翻译好的语句
   * @return 翻译好的语句
   */
  public String getTranslatedSentence() {
    String sTranslated = "";

    for(int i = 1; i <= this.igTermCount; ++i) {
      if (this.term[i].isWord()) {
        sTranslated = sTranslated + this.term[i].getTranslatedWord();
      } else if (this.term[i].isPunctuation()) {
        sTranslated = sTranslated + this.term[i].getTranslatedPunctuation();
      } else if (this.term[i].isEmoticon()) {
        sTranslated = sTranslated + this.term[i].getEmoticon();
      }

      if (this.bgSpaceAfterTerm[i]) {
        sTranslated = sTranslated + " ";
      }
    }

    return sTranslated + "<br>";
  }

  /**
   * 重新计算语句的情绪分数
   */
  public void recalculateSentenceSentimentScore() {
    this.calculateSentenceSentimentScore();
  }

  /**
   * 因语气词更新，重新分类已经分类好的语句
   * @param iSentimentWordID 语气词ID
   */
  public void reClassifyClassifiedSentenceForSentimentChange(int iSentimentWordID) {
    if (this.igNegativeSentiment == 0) {
      this.calculateSentenceSentimentScore();
    } else {
      if (!this.bSentimentIDListMade) {
        this.makeSentimentIDList();
      }

      if (this.igSentimentIDListCount != 0) {
        if (Sort.i_FindIntPositionInSortedArray(iSentimentWordID, this.igSentimentIDList, 1, this.igSentimentIDListCount) >= 0) {
          this.calculateSentenceSentimentScore();
        }

      }
    }
  }

  public int getSentencePositiveSentiment() {
    if (this.igPositiveSentiment == 0) {
      this.calculateSentenceSentimentScore();
    }

    return this.igPositiveSentiment;
  }

  public int getSentenceNegativeSentiment() {
    if (this.igNegativeSentiment == 0) {
      this.calculateSentenceSentimentScore();
    }

    return this.igNegativeSentiment;
  }

  private void markTermsValidToClassify() {
    this.bgIncludeTerm = new boolean[this.igTermCount + 1];
    int iTermsSinceValid;
    if (this.options.bgIgnoreSentencesWithoutKeywords) {
      this.bgNothingToClassify = true;

      int iTerm;
      for(iTermsSinceValid = 1; iTermsSinceValid <= this.igTermCount; ++iTermsSinceValid) {
        this.bgIncludeTerm[iTermsSinceValid] = false;
        if (this.term[iTermsSinceValid].isWord()) {
          for(iTerm = 0; iTerm < this.options.sgSentimentKeyWords.length; ++iTerm) {
            if (this.term[iTermsSinceValid].matchesString(this.options.sgSentimentKeyWords[iTerm], true)) {
              this.bgIncludeTerm[iTermsSinceValid] = true;
              this.bgNothingToClassify = false;
            }
          }
        }
      }

      if (!this.bgNothingToClassify) {
        iTermsSinceValid = 100000;

        for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
          if (this.bgIncludeTerm[iTerm]) {
            iTermsSinceValid = 0;
          } else if (iTermsSinceValid < this.options.igWordsToIncludeAfterKeyword) {
            this.bgIncludeTerm[iTerm] = true;
            if (this.term[iTerm].isWord()) {
              ++iTermsSinceValid;
            }
          }
        }

        iTermsSinceValid = 100000;

        for(iTerm = this.igTermCount; iTerm >= 1; --iTerm) {
          if (this.bgIncludeTerm[iTerm]) {
            iTermsSinceValid = 0;
          } else if (iTermsSinceValid < this.options.igWordsToIncludeBeforeKeyword) {
            this.bgIncludeTerm[iTerm] = true;
            if (this.term[iTerm].isWord()) {
              ++iTermsSinceValid;
            }
          }
        }
      }
    } else {
      for(iTermsSinceValid = 1; iTermsSinceValid <= this.igTermCount; ++iTermsSinceValid) {
        this.bgIncludeTerm[iTermsSinceValid] = true;
      }

      this.bgNothingToClassify = false;
    }

  }

  private void calculateSentenceSentimentScore() {
    if (this.options.bgExplainClassification && this.sgClassificationRationale.length() > 0) {
      this.sgClassificationRationale = "";
    }

    this.igNegativeSentiment = 1;
    this.igPositiveSentiment = 1;
    int iWordTotal = 0;
    int iLastBoosterWordScore = 0;
    int iTemp =0;
    if (this.igTermCount == 0) {
      this.bgNothingToClassify = true;
      this.igNegativeSentiment = -1;
      this.igPositiveSentiment = 1;
    } else {
      this.markTermsValidToClassify();
      if (this.bgNothingToClassify) {
        this.igNegativeSentiment = -1;
        this.igPositiveSentiment = 1;
      } else {
        boolean bSentencePunctuationBoost = false;
        int iWordsSinceNegative = this.options.igMaxWordsBeforeSentimentToNegate + 2;
        float[] fSentiment = new float[this.igTermCount + 1];
        if (this.options.bgUseIdiomLookupTable) {
          this.overrideTermStrengthsWithIdiomStrengths(false);
        }

        if (this.options.bgUseObjectEvaluationTable) {
          this.overrideTermStrengthsWithObjectEvaluationStrengths(false);
        }

        for(int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
          if (this.bgIncludeTerm[iTerm]) {
            int iTermsChecked;
            if (!this.term[iTerm].isWord()) {
              if (this.term[iTerm].isEmoticon()) {
                iTermsChecked = this.term[iTerm].getEmoticonSentimentStrength();
                if (iTermsChecked != 0) {
                  if (iWordTotal > 0) {
                    fSentiment[iWordTotal] += (float)this.term[iTerm].getEmoticonSentimentStrength();
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + this.term[iTerm].getEmoticon() + " [" + this.term[iTerm].getEmoticonSentimentStrength() + " emoticon] ";
                    }
                  } else {
                    ++iWordTotal;
                    fSentiment[iWordTotal] = (float)iTermsChecked;
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + this.term[iTerm].getEmoticon() + " [" + this.term[iTerm].getEmoticonSentimentStrength() + " emoticon]";
                    }
                  }
                }
              } else if (this.term[iTerm].isPunctuation()) {
                if (this.term[iTerm].getPunctuationEmphasisLength() >= this.options.igMinPunctuationWithExclamationToChangeSentenceSentiment && this.term[iTerm].punctuationContains("!") && iWordTotal > 0) {
                  bSentencePunctuationBoost = true;
                  if (this.options.bgExplainClassification) {
                    this.sgClassificationRationale = this.sgClassificationRationale + this.term[iTerm].getOriginalText();
                  }
                } else if (this.options.bgExplainClassification) {
                  this.sgClassificationRationale = this.sgClassificationRationale + this.term[iTerm].getOriginalText();
                }
              }
            } else {
              ++iWordTotal;
              if (iTerm == 1 || !this.term[iTerm].isProperNoun() || this.term[iTerm - 1].getOriginalText().equals(":") || this.term[iTerm - 1].getOriginalText().length() > 3 && this.term[iTerm - 1].getOriginalText().substring(0, 1).equals("@")) {
                fSentiment[iWordTotal] = (float)this.term[iTerm].getSentimentValue();

                if (this.options.bgExplainClassification) {
                  iTemp = this.term[iTerm].getSentimentValue();
                  if (iTemp < 0) {
                    --iTemp;
                  } else {
                    ++iTemp;
                  }

                  if (iTemp == 1) {
                    this.sgClassificationRationale = this.sgClassificationRationale + this.term[iTerm].getOriginalText() + " ";
                  } else {
                    this.sgClassificationRationale = this.sgClassificationRationale + this.term[iTerm].getOriginalText() + "[" + iTemp + "] ";
                  }
                }
              } else if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + this.term[iTerm].getOriginalText() + " [proper noun] ";
              }

              if (this.options.bgMultipleLettersBoostSentiment && this.term[iTerm].getWordEmphasisLength() >= this.options.igMinRepeatedLettersForBoost && (iTerm == 1 || !this.term[iTerm - 1].isPunctuation() || !this.term[iTerm - 1].getOriginalText().equals("@"))) {
                String sEmphasis = this.term[iTerm].getWordEmphasis().toLowerCase();
                if (sEmphasis.indexOf("xx") < 0 && sEmphasis.indexOf("ww") < 0 && sEmphasis.indexOf("ha") < 0) {
                  if (fSentiment[iWordTotal] < 0.0F) {
                    fSentiment[iWordTotal] = (float)((double)fSentiment[iWordTotal] - 0.6D);
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[-0.6 spelling emphasis] ";
                    }
                  } else if (fSentiment[iWordTotal] > 0.0F) {
                    fSentiment[iWordTotal] = (float)((double)fSentiment[iWordTotal] + 0.6D);
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[+0.6 spelling emphasis] ";
                    }
                  } else if (this.options.igMoodToInterpretNeutralEmphasis > 0) {
                    fSentiment[iWordTotal] = (float)((double)fSentiment[iWordTotal] + 0.6D);
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[+0.6 spelling mood emphasis] ";
                    }
                  } else if (this.options.igMoodToInterpretNeutralEmphasis < 0) {
                    fSentiment[iWordTotal] = (float)((double)fSentiment[iWordTotal] - 0.6D);
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[-0.6 spelling mood emphasis] ";
                    }
                  }
                }
              }

              int var10002;
              if (this.options.bgCapitalsBoostTermSentiment && fSentiment[iWordTotal] != 0.0F && this.term[iTerm].isAllCapitals()) {
                if (fSentiment[iWordTotal] > 0.0F) {
                  var10002 = (int) fSentiment[iWordTotal]++;
                  if (this.options.bgExplainClassification) {
                    this.sgClassificationRationale = this.sgClassificationRationale + "[+1 CAPITALS] ";
                  }
                } else {
                  var10002 = (int) fSentiment[iWordTotal]--;
                  if (this.options.bgExplainClassification) {
                    this.sgClassificationRationale = this.sgClassificationRationale + "[-1 CAPITALS] ";
                  }
                }
              }

              if (this.options.bgBoosterWordsChangeEmotion) {
                if (iLastBoosterWordScore != 0) {
                  if (fSentiment[iWordTotal] > 0.0F) {
                    fSentiment[iWordTotal] += (float)iLastBoosterWordScore;
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[+" + iLastBoosterWordScore + " booster word] ";
                    }
                  } else if (fSentiment[iWordTotal] < 0.0F) {
                    fSentiment[iWordTotal] -= (float)iLastBoosterWordScore;
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[-" + iLastBoosterWordScore + " booster word] ";
                    }
                  }
                }

                iLastBoosterWordScore = this.term[iTerm].getBoosterWordScore();
              }

              if (this.options.bgNegatingWordsOccurBeforeSentiment) {
                if (this.options.bgNegatingWordsFlipEmotion) {
                  if (iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
                    fSentiment[iWordTotal] = -fSentiment[iWordTotal] * this.options.fgStrengthMultiplierForNegatedWords;
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[*-" + this.options.fgStrengthMultiplierForNegatedWords + " approx. negated multiplier] ";
                    }
                  }
                } else {
                  if (this.options.bgNegatingNegativeNeutralisesEmotion && fSentiment[iWordTotal] < 0.0F && iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
                    fSentiment[iWordTotal] = 0.0F;
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[=0 negation] ";
                    }
                  }

                  if (this.options.bgNegatingPositiveFlipsEmotion && fSentiment[iWordTotal] > 0.0F && iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
                    fSentiment[iWordTotal] = -fSentiment[iWordTotal] * this.options.fgStrengthMultiplierForNegatedWords;
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[*-" + this.options.fgStrengthMultiplierForNegatedWords + " approx. negated multiplier] ";
                    }
                  }
                }
              }

              if (this.term[iTerm].isNegatingWord()) {
                iWordsSinceNegative = -1;
              }

              if (iLastBoosterWordScore == 0) {
                ++iWordsSinceNegative;
              }

              if (this.term[iTerm].isNegatingWord() && this.options.bgNegatingWordsOccurAfterSentiment) {
                iTermsChecked = 0;

                for(int iPriorWord = iWordTotal - 1; iPriorWord > 0; --iPriorWord) {
                  if (this.options.bgNegatingWordsFlipEmotion) {
                    fSentiment[iPriorWord] = -fSentiment[iPriorWord] * this.options.fgStrengthMultiplierForNegatedWords;
                    if (this.options.bgExplainClassification) {
                      this.sgClassificationRationale = this.sgClassificationRationale + "[*-" + this.options.fgStrengthMultiplierForNegatedWords + " approx. negated multiplier] ";
                    }
                  } else {
                    if (this.options.bgNegatingNegativeNeutralisesEmotion && fSentiment[iPriorWord] < 0.0F) {
                      fSentiment[iPriorWord] = 0.0F;
                      if (this.options.bgExplainClassification) {
                        this.sgClassificationRationale = this.sgClassificationRationale + "[=0 negation] ";
                      }
                    }

                    if (this.options.bgNegatingPositiveFlipsEmotion && fSentiment[iPriorWord] > 0.0F) {
                      fSentiment[iPriorWord] = -fSentiment[iPriorWord] * this.options.fgStrengthMultiplierForNegatedWords;
                      if (this.options.bgExplainClassification) {
                        this.sgClassificationRationale = this.sgClassificationRationale + "[*-" + this.options.fgStrengthMultiplierForNegatedWords + " approx. negated multiplier] ";
                      }
                    }
                  }

                  ++iTermsChecked;
                  if (iTermsChecked > this.options.igMaxWordsAfterSentimentToNegate) {
                    break;
                  }
                }
              }

              if (this.options.bgAllowMultipleNegativeWordsToIncreaseNegativeEmotion && fSentiment[iWordTotal] < -1.0F && iWordTotal > 1 && fSentiment[iWordTotal - 1] < -1.0F) {
                var10002 = (int) fSentiment[iWordTotal]--;
                if (this.options.bgExplainClassification) {
                  this.sgClassificationRationale = this.sgClassificationRationale + "[-1 consecutive negative words] ";
                }
              }

              if (this.options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion && fSentiment[iWordTotal] > 1.0F && iWordTotal > 1 && fSentiment[iWordTotal - 1] > 1.0F) {
                var10002 = (int) fSentiment[iWordTotal]++;
                if (this.options.bgExplainClassification) {
                  this.sgClassificationRationale = this.sgClassificationRationale + "[+1 consecutive positive words] ";
                }
              }

            }
          }
        }

        float fTotalNeg = 0.0F;
        float fTotalPos = 0.0F;
        float fMaxNeg = 0.0F;
        float fMaxPos = 0.0F;
        int iPosWords = 0;
        int iNegWords = 0;

        int iTerm;
        for(iTerm = 1; iTerm <= iWordTotal; ++iTerm) {
          if (fSentiment[iTerm] < 0.0F) {
            fTotalNeg += fSentiment[iTerm];
            ++iNegWords;
            if (fMaxNeg > fSentiment[iTerm]) {
              fMaxNeg = fSentiment[iTerm];
            }
          } else if (fSentiment[iTerm] > 0.0F) {
            fTotalPos += fSentiment[iTerm];
            ++iPosWords;
            if (fMaxPos < fSentiment[iTerm]) {
              fMaxPos = fSentiment[iTerm];
            }
          }
        }
        igSentiCount=iNegWords+iPosWords;
        --fMaxNeg;
        ++fMaxPos;
        int var10000 = this.options.igEmotionSentenceCombineMethod;
        this.options.getClass();
        if (var10000 == 1) {
          if (iPosWords == 0) {
            this.igPositiveSentiment = 1;
          } else {
            this.igPositiveSentiment = (int)Math.round(((double)(fTotalPos + (float)iPosWords) + 0.45D) / (double)iPosWords);
          }

          if (iNegWords == 0) {
            this.igNegativeSentiment = -1;
          } else {
            this.igNegativeSentiment = (int)Math.round(((double)(fTotalNeg - (float)iNegWords) + 0.55D) / (double)iNegWords);
          }
        } else {
          var10000 = this.options.igEmotionSentenceCombineMethod;
          this.options.getClass();
          if (var10000 == 2) {
            this.igPositiveSentiment = Math.round(fTotalPos) + iPosWords;
            this.igNegativeSentiment = Math.round(fTotalNeg) - iNegWords;
          } else {
            this.igPositiveSentiment = Math.round(fMaxPos);
            this.igNegativeSentiment = Math.round(fMaxNeg);
          }
        }

        if (this.options.bgReduceNegativeEmotionInQuestionSentences && this.igNegativeSentiment < -1) {
          for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
            if (this.term[iTerm].isWord()) {
              if (this.resources.questionWords.questionWord(this.term[iTerm].getTranslatedWord().toLowerCase())) {
                ++this.igNegativeSentiment;
                if (this.options.bgExplainClassification) {
                  this.sgClassificationRationale = this.sgClassificationRationale + "[+1 negative for question word]";
                }
                break;
              }
            } else if (this.term[iTerm].isPunctuation() && this.term[iTerm].punctuationContains("?")) {
              ++this.igNegativeSentiment;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[+1 negative for question mark ?]";
              }
              break;
            }
          }
        }

        if (this.igPositiveSentiment == 1 && this.options.bgMissCountsAsPlus2) {
          for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
            if (this.term[iTerm].isWord() && this.term[iTerm].getTranslatedWord().toLowerCase().compareTo("miss") == 0) {
              this.igPositiveSentiment = 2;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[pos = 2 for term 'miss']";
              }
              break;
            }
          }
        }

        if (bSentencePunctuationBoost) {
          if (this.igPositiveSentiment < -this.igNegativeSentiment) {
            --this.igNegativeSentiment;
            if (this.options.bgExplainClassification) {
              this.sgClassificationRationale = this.sgClassificationRationale + "[-1 punctuation emphasis] ";
            }
          } else if (this.igPositiveSentiment > -this.igNegativeSentiment) {
            ++this.igPositiveSentiment;
            if (this.options.bgExplainClassification) {
              this.sgClassificationRationale = this.sgClassificationRationale + "[+1 punctuation emphasis] ";
            }
          } else if (this.options.igMoodToInterpretNeutralEmphasis > 0) {
            ++this.igPositiveSentiment;
            if (this.options.bgExplainClassification) {
              this.sgClassificationRationale = this.sgClassificationRationale + "[+1 punctuation mood emphasis] ";
            }
          } else if (this.options.igMoodToInterpretNeutralEmphasis < 0) {
            --this.igNegativeSentiment;
            if (this.options.bgExplainClassification) {
              this.sgClassificationRationale = this.sgClassificationRationale + "[-1 punctuation mood emphasis] ";
            }
          }
        }

        if (this.igPositiveSentiment == 1 && this.igNegativeSentiment == -1 && this.options.bgExclamationInNeutralSentenceCountsAsPlus2) {
          for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
            if (this.term[iTerm].isPunctuation() && this.term[iTerm].punctuationContains("!")) {
              this.igPositiveSentiment = 2;
              if (this.options.bgExplainClassification) {
                this.sgClassificationRationale = this.sgClassificationRationale + "[pos = 2 for !]";
              }
              break;
            }
          }
        }

        if (this.igPositiveSentiment == 1 && this.igNegativeSentiment == -1 && this.options.bgYouOrYourIsPlus2UnlessSentenceNegative) {
          for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
            if (this.term[iTerm].isWord()) {
              String sTranslatedWord = this.term[iTerm].getTranslatedWord().toLowerCase();
              if (sTranslatedWord.compareTo("you") == 0 || sTranslatedWord.compareTo("your") == 0 || sTranslatedWord.compareTo("whats") == 0) {
                this.igPositiveSentiment = 2;
                if (this.options.bgExplainClassification) {
                  this.sgClassificationRationale = this.sgClassificationRationale + "[pos = 2 for you/your/whats]";
                }
                break;
              }
            }
          }
        }

        this.adjustSentimentForIrony();
        var10000 = this.options.igEmotionSentenceCombineMethod;
        this.options.getClass();
        if (var10000 != 2) {
          if (this.igPositiveSentiment > 5) {
            this.igPositiveSentiment = 5;
          }

          if (this.igNegativeSentiment < -5) {
            this.igNegativeSentiment = -5;
          }
        }

        if (this.options.bgExplainClassification) {
          this.sgClassificationRationale = this.sgClassificationRationale + "[sentence: " + this.igPositiveSentiment + "," + this.igNegativeSentiment + "]";
        }

      }
    }
  }

  private void adjustSentimentForIrony() {
    int iTerm;
    if (this.igPositiveSentiment >= this.options.igMinSentencePosForQuotesIrony) {
      for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isPunctuation() && this.term[iTerm].getText().indexOf(34) >= 0) {
          if (this.igNegativeSentiment > -this.igPositiveSentiment) {
            this.igNegativeSentiment = 1 - this.igPositiveSentiment;
          }

          this.igPositiveSentiment = 1;
          this.sgClassificationRationale = this.sgClassificationRationale + "[Irony change: pos = 1, neg = " + this.igNegativeSentiment + "]";
          return;
        }
      }
    }

    if (this.igPositiveSentiment >= this.options.igMinSentencePosForPunctuationIrony) {
      for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isPunctuation() && this.term[iTerm].punctuationContains("!") && this.term[iTerm].getPunctuationEmphasisLength() > 0) {
          if (this.igNegativeSentiment > -this.igPositiveSentiment) {
            this.igNegativeSentiment = 1 - this.igPositiveSentiment;
          }

          this.igPositiveSentiment = 1;
          this.sgClassificationRationale = this.sgClassificationRationale + "[Irony change: pos = 1, neg = " + this.igNegativeSentiment + "]";
          return;
        }
      }
    }

    if (this.igPositiveSentiment >= this.options.igMinSentencePosForTermsIrony) {
      for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.resources.ironyList.termIsIronic(this.term[iTerm].getText())) {
          if (this.igNegativeSentiment > -this.igPositiveSentiment) {
            this.igNegativeSentiment = 1 - this.igPositiveSentiment;
          }

          this.igPositiveSentiment = 1;
          this.sgClassificationRationale = this.sgClassificationRationale + "[Irony change: pos = 1, neg = " + this.igNegativeSentiment + "]";
          return;
        }
      }
    }

  }

  public void overrideTermStrengthsWithObjectEvaluationStrengths(boolean recalculateIfAlreadyDone) {
    boolean bMatchingObject = false;
    boolean bMatchingEvaluation = false;
    if (!this.bgObjectEvaluationsApplied || recalculateIfAlreadyDone) {
      for(int iObject = 1; iObject < this.resources.evaluativeTerms.igObjectEvaluationCount; ++iObject) {
        bMatchingObject = false;
        bMatchingEvaluation = false;

        int iTerm;
        for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
          if (this.term[iTerm].isWord() && this.term[iTerm].matchesStringWithWildcard(this.resources.evaluativeTerms.sgObject[iObject], true)) {
            bMatchingObject = true;
            break;
          }
        }

        if (bMatchingObject) {
          for(iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
            if (this.term[iTerm].isWord() && this.term[iTerm].matchesStringWithWildcard(this.resources.evaluativeTerms.sgObjectEvaluation[iObject], true)) {
              bMatchingEvaluation = true;
              break;
            }
          }
        }

        if (bMatchingEvaluation) {
          if (this.options.bgExplainClassification) {
            this.sgClassificationRationale = this.sgClassificationRationale + "[term weight changed by object/evaluation]";
          }

          this.term[iTerm].setSentimentOverrideValue(this.resources.evaluativeTerms.igObjectEvaluationStrength[iObject]);
        }
      }

      this.bgObjectEvaluationsApplied = true;
    }

  }

  public void overrideTermStrengthsWithIdiomStrengths(boolean recalculateIfAlreadyDone) {
    if (!this.bgIdiomsApplied || recalculateIfAlreadyDone) {
      for(int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isWord()) {
          for(int iIdiom = 1; iIdiom <= this.resources.idiomList.igIdiomCount; ++iIdiom) {
            if (iTerm + this.resources.idiomList.igIdiomWordCount[iIdiom] - 1 <= this.igTermCount) {
              boolean bMatchingIdiom = true;

              int iIdiomTerm;
              for(iIdiomTerm = 0; iIdiomTerm < this.resources.idiomList.igIdiomWordCount[iIdiom]; ++iIdiomTerm) {
                if (!this.term[iTerm + iIdiomTerm].matchesStringWithWildcard(this.resources.idiomList.sgIdiomWords[iIdiom][iIdiomTerm], true)) {
                  bMatchingIdiom = false;
                  break;
                }
              }

              if (bMatchingIdiom) {
                if (this.options.bgExplainClassification) {
                  this.sgClassificationRationale = this.sgClassificationRationale + "[term weight(s) changed by idiom " + this.resources.idiomList.getIdiom(iIdiom) + "]";
                }

                this.term[iTerm].setSentimentOverrideValue(this.resources.idiomList.igIdiomStrength[iIdiom]);

                for(iIdiomTerm = 1; iIdiomTerm < this.resources.idiomList.igIdiomWordCount[iIdiom]; ++iIdiomTerm) {
                  this.term[iTerm + iIdiomTerm].setSentimentOverrideValue(0);
                }
              }
            }
          }
        }
      }

      this.bgIdiomsApplied = true;
    }

  }
}
