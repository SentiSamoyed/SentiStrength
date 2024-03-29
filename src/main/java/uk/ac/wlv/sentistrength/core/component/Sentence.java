//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package uk.ac.wlv.sentistrength.core.component;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;
import uk.ac.wlv.sentistrength.core.UnusedTermsClassificationIndex;
import uk.ac.wlv.sentistrength.option.TextParsingOptions;
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
   * 获取语句中词语的数量。
   *
   * @return 词语的数量
   */
  public int getIgTermCount() {
    return igTermCount;
  }

  /**
   * 获取语句中情绪词的数量。
   *
   * @return 情绪词的数量
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
   * 将语句添加到未使用词语的分类索引。
   *
   * @param unusedTermClassificationIndex 未使用词语的分类索引
   */
  public void addSentenceToIndex(UnusedTermsClassificationIndex unusedTermClassificationIndex) {
    for (int i = 1; i <= this.igTermCount; ++i) {
      unusedTermClassificationIndex.addTermToNewTermIndex(this.term[i].getText());
    }
  }

  /**
   * 将语句添加到字符串索引。
   *
   * @param stringIndex        字符串索引
   * @param textParsingOptions 文本解析选项
   * @param bRecordCount       是否计数
   * @param bArffIndex         是否是arff文件索引
   * @return 语句中经过检验的词语数量
   */
  public int addToStringIndex(StringIndex stringIndex, TextParsingOptions textParsingOptions, boolean bRecordCount, boolean bArffIndex) {
    String sEncoded = "";
    int iStringPos = 1;
    int iTermsChecked = 0;
    if (textParsingOptions.bgIncludePunctuation && textParsingOptions.igNgramSize == 1 && !textParsingOptions.bgUseTranslations && !textParsingOptions.bgAddEmphasisCode) {
      for (int i = 1; i <= this.igTermCount; ++i) {
        stringIndex.addString(this.term[i].getText(), bRecordCount);
      }

      iTermsChecked = this.igTermCount;
    } else {
      StringBuilder sText = new StringBuilder();
      int iCurrentTerm = 0;
      int iTermCount = 0;

      while (iCurrentTerm < this.igTermCount) {
        ++iCurrentTerm;
        if (textParsingOptions.bgIncludePunctuation || !this.term[iCurrentTerm].isPunctuation()) {
          ++iTermCount;
          if (iTermCount > 1) {
            sText.append(" ");
          } else {
            sText = new StringBuilder();
          }

          if (textParsingOptions.bgUseTranslations) {
            sText.append(this.term[iCurrentTerm].getTranslation());
          } else {
            sText.append(this.term[iCurrentTerm].getOriginalText());
          }

          if (textParsingOptions.bgAddEmphasisCode && this.term[iCurrentTerm].containsEmphasis()) {
            sText.append("+");
          }
        }

        if (iTermCount == textParsingOptions.igNgramSize) {
          if (bArffIndex) {
            sEncoded = Arff.arffSafeWordEncode(sText.toString().toLowerCase(), false);
            iStringPos = stringIndex.findString(sEncoded);
            iTermCount = 0;
            if (iStringPos > -1) {
              stringIndex.add1ToCount(iStringPos);
            }
          } else {
            stringIndex.addString(sText.toString().toLowerCase(), bRecordCount);
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
   * 设定一条语句。
   *
   * @param sSentence                语句文本
   * @param classResources           分类资源
   * @param newClassificationOptions 新的分类选项
   */
  public void setSentence(String sSentence, ClassificationResources classResources, ClassificationOptions newClassificationOptions) {
    this.resources = classResources;
    this.options = newClassificationOptions;
    if (this.options.bgAlwaysSplitWordsAtApostrophes && sSentence.contains("'")) {
      sSentence = sSentence.replace("'", " ");
    }

    int iMaxTermListLength = sSentence.length() + 1;
    this.term = new Term[iMaxTermListLength];
    this.bgSpaceAfterTerm = new boolean[iMaxTermListLength];
    int iPos = 0;
    this.igTermCount = 0;
    String[] sSegmentList = sSentence.split(" ");

    for (String s : sSegmentList) {
      for (iPos = 0; iPos >= 0 && iPos < s.length(); this.bgSpaceAfterTerm[this.igTermCount] = false) {
        this.term[++this.igTermCount] = new Term();
        int iOffset = this.term[this.igTermCount].extractNextWordOrPunctuationOrEmoticon(s.substring(iPos), this.resources, this.options);
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
   * 获取情感词ID列表。
   *
   * @return 情感词ID列表
   */
  public int[] getSentimentIDList() {
    if (!this.bSentimentIDListMade) {
      this.makeSentimentIDList();
    }
    return this.igSentimentIDList;
  }

  /**
   * 生成情绪词ID列表。
   */
  public void makeSentimentIDList() {
    int iSentimentIDTemp = 0;
    this.igSentimentIDListCount = 0;

    int i;
    for (i = 1; i <= this.igTermCount; ++i) {
      if (this.term[i].getSentimentID() > 0) {
        ++this.igSentimentIDListCount;
      }
    }

    if (this.igSentimentIDListCount > 0) {
      this.igSentimentIDList = new int[this.igSentimentIDListCount + 1];
      this.igSentimentIDListCount = 0;

      for (i = 1; i <= this.igTermCount; ++i) {
        iSentimentIDTemp = this.term[i].getSentimentID();
        if (iSentimentIDTemp > 0) {
          for (int j = 1; j <= this.igSentimentIDListCount; ++j) {
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
   * 获取标记好的语句。
   *
   * @return 被标记的语句
   */
  public String getTaggedSentence() {
    StringBuilder sTagged = new StringBuilder();

    for (int i = 1; i <= this.igTermCount; ++i) {
      if (this.bgSpaceAfterTerm[i]) {
        sTagged.append(this.term[i].getTag()).append(" ");
      } else {
        sTagged.append(this.term[i].getTag());
      }
    }

    return sTagged + "<br>";
  }

  /**
   * 获取分类的原理解释。
   *
   * @return 分类的原理解释
   */
  public String getClassificationRationale() {
    return this.sgClassificationRationale;
  }

  /**
   * 获取翻译好的语句。
   *
   * @return 翻译好的语句
   */
  public String getTranslatedSentence() {
    StringBuilder sTranslated = new StringBuilder();

    for (int i = 1; i <= this.igTermCount; ++i) {
      if (this.term[i].isWord()) {
        sTranslated.append(this.term[i].getTranslatedWord());
      } else if (this.term[i].isPunctuation()) {
        sTranslated.append(this.term[i].getTranslatedPunctuation());
      } else if (this.term[i].isEmoticon()) {
        sTranslated.append(this.term[i].getEmoticon());
      }

      if (this.bgSpaceAfterTerm[i]) {
        sTranslated.append(" ");
      }
    }

    return sTranslated + "<br>";
  }

  /**
   * 重新计算语句的情绪分数。
   */
  public void recalculateSentenceSentimentScore() {
    this.calculateSentenceSentimentScore();
  }

  /**
   * 因语气词更新，重新分类已经分类好的语句。
   *
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

  /**
   * 获取该语句的积极分数。<br>
   * 如果没有计算过，
   * 则调用方法 {@link #calculateSentenceSentimentScore()} 计算。
   *
   * @return Positive Score
   */
  public int getSentencePositiveSentiment() {
    if (this.igPositiveSentiment == 0) {
      this.calculateSentenceSentimentScore();
    }

    return this.igPositiveSentiment;
  }

  /**
   * 获取该语句的消极分数。<br>
   * 如果没有计算过，
   * 则调用方法 {@link #calculateSentenceSentimentScore()} 计算。
   *
   * @return Negative Score
   */
  public int getSentenceNegativeSentiment() {
    if (this.igNegativeSentiment == 0) {
      this.calculateSentenceSentimentScore();
    }

    return this.igNegativeSentiment;
  }

  /**
   * 标记出该语句中有效的词。<br>
   * 如果 Ignore Sentences Without Keywords 为 true，则检验是否有关键词，
   * 并通过一定的规则选出需要进行情感分析的词。<br>
   * 否则, 将所有的词标记为有效。
   */
  private void markTermsValidToClassify() {
    this.bgIncludeTerm = new boolean[this.igTermCount + 1];
    int iTermsSinceValid;
    if (this.options.bgIgnoreSentencesWithoutKeywords) {
      this.bgNothingToClassify = true;

      int iTerm;
      for (iTermsSinceValid = 1; iTermsSinceValid <= this.igTermCount; ++iTermsSinceValid) {
        this.bgIncludeTerm[iTermsSinceValid] = false;
        // 如果是一个 Word, 检验是否在 SentimentKeyWords 中
        if (this.term[iTermsSinceValid].isWord()) {
          for (iTerm = 0; iTerm < this.options.sgSentimentKeyWords.length; ++iTerm) {
            if (this.term[iTermsSinceValid].matchesString(this.options.sgSentimentKeyWords[iTerm], true)) {
              this.bgIncludeTerm[iTermsSinceValid] = true;
              this.bgNothingToClassify = false;
            }
          }
        }
      }

      if (!this.bgNothingToClassify) {
        iTermsSinceValid = 100000;

        for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
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

        for (iTerm = this.igTermCount; iTerm >= 1; --iTerm) {
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
      // 如果 Ignore Sentences Without Keywords 为 false, 则所有的 Term 都有效
      for (iTermsSinceValid = 1; iTermsSinceValid <= this.igTermCount; ++iTermsSinceValid) {
        this.bgIncludeTerm[iTermsSinceValid] = true;
      }

      this.bgNothingToClassify = false;
    }

  }

  /**
   * 计算该语句的情感强度。
   */
  private void calculateSentenceSentimentScore() {
    if (this.options.bgExplainClassification && this.sgClassificationRationale.length() > 0) {
      this.sgClassificationRationale = "";
    }

    this.igNegativeSentiment = -1;
    this.igPositiveSentiment = 1;

    if (this.igTermCount == 0) {
      // 该语句没有词
      this.bgNothingToClassify = true;
      /* Exit 1 */
      return;
    }

    this.markTermsValidToClassify();

    if (this.bgNothingToClassify) {
      this.igNegativeSentiment = -1;
      this.igPositiveSentiment = 1;
      /* Exit 2 */
      return;
    }

    if (this.options.bgUseIdiomLookupTable) {
      this.overrideTermStrengthsWithIdiomStrengths(false);
    }

    if (this.options.bgUseObjectEvaluationTable) {
      this.overrideTermStrengthsWithObjectEvaluationStrengths(false);
    }


    StringBuilder rationale = new StringBuilder(this.sgClassificationRationale);
    calculateSentenceSentimentScore(rationale);
    if (options.bgExplainClassification) {
      this.sgClassificationRationale = rationale.toString();
    }
  }

  private void calculateSentenceSentimentScore(StringBuilder rationale) {
    int iWordsSinceNegative = this.options.igMaxWordsBeforeSentimentToNegate + 2;
    boolean bSentencePunctuationBoost = false;
    int iWordTotal = 0;
    int iLastBoosterWordScore = 0;
    float[] fSentiment = new float[this.igTermCount + 1];

    for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
      if (!this.bgIncludeTerm[iTerm]) {
        continue;
      }

      if (!this.term[iTerm].isWord()) {
        // 非单词
        if (this.term[iTerm].isEmoticon()) {
          // 是表情符
          int iTermsChecked = this.term[iTerm].getEmoticonSentimentStrength();
          if (iTermsChecked != 0) {
            if (iWordTotal > 0) {
              fSentiment[iWordTotal] += (float) this.term[iTerm].getEmoticonSentimentStrength();
              rationale
                  .append(this.term[iTerm].getEmoticon())
                  .append(" [").append(this.term[iTerm].getEmoticonSentimentStrength())
                  .append(" emoticon] ");
            } else {
              ++iWordTotal;
              fSentiment[iWordTotal] = (float) iTermsChecked;
              rationale
                  .append(this.term[iTerm].getEmoticon())
                  .append(" [").append(this.term[iTerm].getEmoticonSentimentStrength())
                  .append(" emoticon]");
            }
          }
        } else if (this.term[iTerm].isPunctuation()) {
          // 是标点符号
          if (this.term[iTerm].getPunctuationEmphasisLength() >= this.options.igMinPunctuationWithExclamationToChangeSentenceSentiment
              && this.term[iTerm].punctuationContains("!")
              && iWordTotal > 0) {
            bSentencePunctuationBoost = true;
          }
          rationale.append(this.term[iTerm].getOriginalText());
        }
      } else {
        // 是单词
        ++iWordTotal;
        if (iTerm == 1
            || !this.term[iTerm].isProperNoun()
            || this.term[iTerm - 1].getOriginalText().equals(":")
            || this.term[iTerm - 1].getOriginalText().length() > 3
            && this.term[iTerm - 1].getOriginalText().charAt(0) == '@') {
          fSentiment[iWordTotal] = (float) this.term[iTerm].getSentimentValue();

          if (this.options.bgExplainClassification) {
            int iTemp = this.term[iTerm].getSentimentValue();
            if (iTemp < 0) {
              --iTemp;
            } else {
              ++iTemp;
            }

            if (iTemp == 1) {
              rationale.append(this.term[iTerm].getOriginalText()).append(" ");
            } else {
              rationale.append(this.term[iTerm].getOriginalText()).append("[").append(iTemp).append("] ");
            }
          }
        } else {
          rationale.append(this.term[iTerm].getOriginalText()).append(" [proper noun] ");
        }

        if (this.options.bgMultipleLettersBoostSentiment
            && this.term[iTerm].getWordEmphasisLength() >= this.options.igMinRepeatedLettersForBoost
            && (iTerm == 1
            || !this.term[iTerm - 1].isPunctuation()
            || !this.term[iTerm - 1].getOriginalText().equals("@"))) {

          String sEmphasis = this.term[iTerm].getWordEmphasis().toLowerCase();

          if (!sEmphasis.contains("xx") && !sEmphasis.contains("ww") && !sEmphasis.contains("ha")) {
            if (fSentiment[iWordTotal] < 0.0F) {
              fSentiment[iWordTotal] = (float) ((double) fSentiment[iWordTotal] - 0.6D);
              rationale.append("[-0.6 spelling emphasis] ");
            } else if (fSentiment[iWordTotal] > 0.0F) {
              fSentiment[iWordTotal] = (float) ((double) fSentiment[iWordTotal] + 0.6D);
              rationale.append("[+0.6 spelling emphasis] ");
            } else if (this.options.igMoodToInterpretNeutralEmphasis > 0) {
              fSentiment[iWordTotal] = (float) ((double) fSentiment[iWordTotal] + 0.6D);
              rationale.append("[+0.6 spelling mood emphasis] ");
            } else if (this.options.igMoodToInterpretNeutralEmphasis < 0) {
              fSentiment[iWordTotal] = (float) ((double) fSentiment[iWordTotal] - 0.6D);
              rationale.append("[-0.6 spelling mood emphasis] ");
            }
          }
        }

        int var10002;
        if (this.options.bgCapitalsBoostTermSentiment && fSentiment[iWordTotal] != 0.0F && this.term[iTerm].isAllCapitals()) {
          if (fSentiment[iWordTotal] > 0.0F) {
            var10002 = (int) fSentiment[iWordTotal]++;
            rationale.append("[+1 CAPITALS] ");
          } else {
            var10002 = (int) fSentiment[iWordTotal]--;
            rationale.append("[-1 CAPITALS] ");
          }
        }

        if (this.options.bgBoosterWordsChangeEmotion) {
          if (iLastBoosterWordScore != 0) {
            if (fSentiment[iWordTotal] > 0.0F) {
              fSentiment[iWordTotal] += (float) iLastBoosterWordScore;
              rationale.append("[+").append(iLastBoosterWordScore).append(" booster word] ");
            } else if (fSentiment[iWordTotal] < 0.0F) {
              fSentiment[iWordTotal] -= (float) iLastBoosterWordScore;
              rationale.append("[-").append(iLastBoosterWordScore).append(" booster word] ");
            }
          }

          iLastBoosterWordScore = this.term[iTerm].getBoosterWordScore();
        }

        if (this.options.bgNegatingWordsOccurBeforeSentiment) {
          if (this.options.bgNegatingWordsFlipEmotion) {
            if (iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
              fSentiment[iWordTotal] = -fSentiment[iWordTotal] * this.options.fgStrengthMultiplierForNegatedWords;
              rationale.append("[*-").append(this.options.fgStrengthMultiplierForNegatedWords).append(" approx. negated multiplier] ");
            }
          } else {
            if (this.options.bgNegatingNegativeNeutralisesEmotion && fSentiment[iWordTotal] < 0.0F && iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
              fSentiment[iWordTotal] = 0.0F;
              rationale.append("[=0 negation] ");
            }

            if (this.options.bgNegatingPositiveFlipsEmotion && fSentiment[iWordTotal] > 0.0F && iWordsSinceNegative <= this.options.igMaxWordsBeforeSentimentToNegate) {
              fSentiment[iWordTotal] = -fSentiment[iWordTotal] * this.options.fgStrengthMultiplierForNegatedWords;
              rationale.append("[*-").append(this.options.fgStrengthMultiplierForNegatedWords).append(" approx. negated multiplier] ");
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
          int iTermsChecked = 0;

          for (int iPriorWord = iWordTotal - 1; iPriorWord > 0; --iPriorWord) {
            if (this.options.bgNegatingWordsFlipEmotion) {
              fSentiment[iPriorWord] = -fSentiment[iPriorWord] * this.options.fgStrengthMultiplierForNegatedWords;
              rationale.append("[*-").append(this.options.fgStrengthMultiplierForNegatedWords).append(" approx. negated multiplier] ");
            } else {
              if (this.options.bgNegatingNegativeNeutralisesEmotion && fSentiment[iPriorWord] < 0.0F) {
                fSentiment[iPriorWord] = 0.0F;
                rationale.append("[=0 negation] ");
              }

              if (this.options.bgNegatingPositiveFlipsEmotion && fSentiment[iPriorWord] > 0.0F) {
                fSentiment[iPriorWord] = -fSentiment[iPriorWord] * this.options.fgStrengthMultiplierForNegatedWords;
                rationale.append("[*-").append(this.options.fgStrengthMultiplierForNegatedWords).append(" approx. negated multiplier] ");
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
          rationale.append("[-1 consecutive negative words] ");
        }

        if (this.options.bgAllowMultiplePositiveWordsToIncreasePositiveEmotion && fSentiment[iWordTotal] > 1.0F && iWordTotal > 1 && fSentiment[iWordTotal - 1] > 1.0F) {
          var10002 = (int) fSentiment[iWordTotal]++;
          rationale.append("[+1 consecutive positive words] ");
        }

      }
    }

    calcPhase2(rationale, bSentencePunctuationBoost, iWordTotal, fSentiment);
  }

  private void calcPhase2(StringBuilder rationale, boolean bSentencePunctuationBoost, int iWordTotal, float[] fSentiment) {
    float fTotalNeg = 0.0F;
    float fTotalPos = 0.0F;
    float fMaxNeg = 0.0F;
    float fMaxPos = 0.0F;
    int iPosWords = 0;
    int iNegWords = 0;

    for (int iTerm = 1; iTerm <= iWordTotal; ++iTerm) {
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

    igSentiCount = iNegWords + iPosWords;

    --fMaxNeg;
    ++fMaxPos;

    int combineMethod = this.options.igEmotionSentenceCombineMethod;
    if (combineMethod == 1) {
      if (iPosWords == 0) {
        this.igPositiveSentiment = 1;
      } else {
        this.igPositiveSentiment = (int) Math.round(((double) (fTotalPos + (float) iPosWords) + 0.45D) / (double) iPosWords);
      }

      if (iNegWords == 0) {
        this.igNegativeSentiment = -1;
      } else {
        this.igNegativeSentiment = (int) Math.round(((double) (fTotalNeg - (float) iNegWords) + 0.55D) / (double) iNegWords);
      }
    } else {
      if (combineMethod == 2) {
        this.igPositiveSentiment = Math.round(fTotalPos) + iPosWords;
        this.igNegativeSentiment = Math.round(fTotalNeg) - iNegWords;
      } else {
        this.igPositiveSentiment = Math.round(fMaxPos);
        this.igNegativeSentiment = Math.round(fMaxNeg);
      }
    }

    if (this.options.bgReduceNegativeEmotionInQuestionSentences && this.igNegativeSentiment < -1) {
      for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isWord()) {
          if (this.resources.questionWords.questionWord(this.term[iTerm].getTranslatedWord().toLowerCase())) {
            ++this.igNegativeSentiment;
            rationale.append("[+1 negative for question word]");
            break;
          }
        } else if (this.term[iTerm].isPunctuation() && this.term[iTerm].punctuationContains("?")) {
          ++this.igNegativeSentiment;
          rationale.append("[+1 negative for question mark ?]");
          break;
        }
      }
    }

    if (this.igPositiveSentiment == 1 && this.options.bgMissCountsAsPlus2) {
      for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isWord() && this.term[iTerm].getTranslatedWord().toLowerCase().compareTo("miss") == 0) {
          this.igPositiveSentiment = 2;
          rationale.append("[pos = 2 for term 'miss']");
          break;
        }
      }
    }

    if (bSentencePunctuationBoost) {
      if (this.igPositiveSentiment < -this.igNegativeSentiment) {
        --this.igNegativeSentiment;
        rationale.append("[-1 punctuation emphasis] ");
      } else if (this.igPositiveSentiment > -this.igNegativeSentiment) {
        ++this.igPositiveSentiment;
        rationale.append("[+1 punctuation emphasis] ");
      } else if (this.options.igMoodToInterpretNeutralEmphasis > 0) {
        ++this.igPositiveSentiment;
        rationale.append("[+1 punctuation mood emphasis] ");
      } else if (this.options.igMoodToInterpretNeutralEmphasis < 0) {
        --this.igNegativeSentiment;
        rationale.append("[-1 punctuation mood emphasis] ");
      }
    }

    if (this.igPositiveSentiment == 1 && this.igNegativeSentiment == -1 && this.options.bgExclamationInNeutralSentenceCountsAsPlus2) {
      for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isPunctuation() && this.term[iTerm].punctuationContains("!")) {
          this.igPositiveSentiment = 2;
          rationale.append("[pos = 2 for !]");
          break;
        }
      }
    }

    if (this.igPositiveSentiment == 1 && this.igNegativeSentiment == -1 && this.options.bgYouOrYourIsPlus2UnlessSentenceNegative) {
      for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isWord()) {
          String sTranslatedWord = this.term[iTerm].getTranslatedWord().toLowerCase();
          if (sTranslatedWord.compareTo("you") == 0 || sTranslatedWord.compareTo("your") == 0 || sTranslatedWord.compareTo("whats") == 0) {
            this.igPositiveSentiment = 2;
            rationale.append("[pos = 2 for you/your/whats]");
            break;
          }
        }
      }
    }

    this.adjustSentimentForIrony();
    if (combineMethod != 2) {
      if (this.igPositiveSentiment > 5) {
        this.igPositiveSentiment = 5;
      }

      if (this.igNegativeSentiment < -5) {
        this.igNegativeSentiment = -5;
      }
    }

    rationale
        .append("[sentence: ")
        .append(this.igPositiveSentiment)
        .append(",")
        .append(this.igNegativeSentiment)
        .append("]");
  }

  /**
   * 考虑反语对情感值的影响并调整，分为以下三种情况:
   * <ul>
   * <li>
   * 1. 积极分数超过 options 中的设定值，且语句包含引号，
   * 且积极分数大于消极分数的绝对值，
   * 则 Positive Score = 1, Negative Score = 1 - Positive Score.
   * </li>
   * <li>
   * 2. 语句包含感叹号，条件和对情感值的调整如上述。
   * </li>
   * <li>
   * 3. 语句包含反语，条件和对情感值的调整如上述。
   * </li>
   * </ul>
   */
  private void adjustSentimentForIrony() {
    StringBuilder rationale = new StringBuilder(this.sgClassificationRationale);

    if (this.igPositiveSentiment >= this.options.igMinSentencePosForQuotesIrony) {
      for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isPunctuation() && this.term[iTerm].getText().indexOf(34) >= 0) {
          if (this.igNegativeSentiment > -this.igPositiveSentiment) {
            this.igNegativeSentiment = 1 - this.igPositiveSentiment;
          }

          this.igPositiveSentiment = 1;
          rationale.append("[Irony change: pos = 1, neg = ").append(this.igNegativeSentiment).append("]");
          return;
        }
      }
    }

    if (this.igPositiveSentiment >= this.options.igMinSentencePosForPunctuationIrony) {
      for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isPunctuation() && this.term[iTerm].punctuationContains("!") && this.term[iTerm].getPunctuationEmphasisLength() > 0) {
          if (this.igNegativeSentiment > -this.igPositiveSentiment) {
            this.igNegativeSentiment = 1 - this.igPositiveSentiment;
          }

          this.igPositiveSentiment = 1;
          rationale.append("[Irony change: pos = 1, neg = ").append(this.igNegativeSentiment).append("]");
          return;
        }
      }
    }

    if (this.igPositiveSentiment >= this.options.igMinSentencePosForTermsIrony) {
      for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.resources.ironyList.termIsIronic(this.term[iTerm].getText())) {
          if (this.igNegativeSentiment > -this.igPositiveSentiment) {
            this.igNegativeSentiment = 1 - this.igPositiveSentiment;
          }

          this.igPositiveSentiment = 1;
          // 对情感值的调整进行记录
          rationale.append("[Irony change: pos = 1, neg = ").append(this.igNegativeSentiment).append("]");
          return;
        }
      }
    }

    if (options.bgExplainClassification) {
      this.sgClassificationRationale = rationale.toString();
    }
  }

  /**
   * 如果使用 Object Evaluation Table, 则对词的强度进行重写。
   *
   * @param recalculateIfAlreadyDone 如果已经完成过重写，是否仍需重写。
   */
  public void overrideTermStrengthsWithObjectEvaluationStrengths(boolean recalculateIfAlreadyDone) {
    StringBuilder rationale = new StringBuilder(this.sgClassificationRationale);

    boolean bMatchingObject;
    boolean bMatchingEvaluation;
    if (!this.bgObjectEvaluationsApplied || recalculateIfAlreadyDone) {
      for (int iObject = 1; iObject < this.resources.evaluativeTerms.igObjectEvaluationCount; ++iObject) {
        bMatchingObject = false;
        bMatchingEvaluation = false;

        int iTerm;
        for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
          if (this.term[iTerm].isWord() && this.term[iTerm].matchesStringWithWildcard(this.resources.evaluativeTerms.sgObject[iObject], true)) {
            bMatchingObject = true;
            break;
          }
        }

        if (bMatchingObject) {
          for (iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
            if (this.term[iTerm].isWord() && this.term[iTerm].matchesStringWithWildcard(this.resources.evaluativeTerms.sgObjectEvaluation[iObject], true)) {
              bMatchingEvaluation = true;
              break;
            }
          }
        }

        if (bMatchingEvaluation) {
          rationale.append("[term weight changed by object/evaluation]");

          this.term[iTerm].setSentimentOverrideValue(this.resources.evaluativeTerms.igObjectEvaluationStrength[iObject]);
        }
      }

      if (options.bgExplainClassification) {
        this.sgClassificationRationale = rationale.toString();
      }


      this.bgObjectEvaluationsApplied = true;
    }

  }

  /**
   * 如果使用 Idiom Lookup Table, 则对词的强度进行重写。
   *
   * @param recalculateIfAlreadyDone 如果已经完成过重写，是否仍需进行重写。
   */
  public void overrideTermStrengthsWithIdiomStrengths(boolean recalculateIfAlreadyDone) {
    StringBuilder rationale = new StringBuilder(this.sgClassificationRationale);

    if (!this.bgIdiomsApplied || recalculateIfAlreadyDone) {
      for (int iTerm = 1; iTerm <= this.igTermCount; ++iTerm) {
        if (this.term[iTerm].isWord()) {
          for (int iIdiom = 1; iIdiom <= this.resources.idiomList.igIdiomCount; ++iIdiom) {
            if (iTerm + this.resources.idiomList.igIdiomWordCount[iIdiom] - 1 <= this.igTermCount) {
              boolean bMatchingIdiom = true;

              int iIdiomTerm;
              for (iIdiomTerm = 0; iIdiomTerm < this.resources.idiomList.igIdiomWordCount[iIdiom]; ++iIdiomTerm) {
                if (!this.term[iTerm + iIdiomTerm].matchesStringWithWildcard(this.resources.idiomList.sgIdiomWords[iIdiom][iIdiomTerm], true)) {
                  bMatchingIdiom = false;
                  break;
                }
              }

              if (bMatchingIdiom) {
                rationale.append("[term weight(s) changed by idiom ").append(this.resources.idiomList.getIdiom(iIdiom)).append("]");

                this.term[iTerm].setSentimentOverrideValue(this.resources.idiomList.igIdiomStrength[iIdiom]);

                for (iIdiomTerm = 1; iIdiomTerm < this.resources.idiomList.igIdiomWordCount[iIdiom]; ++iIdiomTerm) {
                  this.term[iTerm + iIdiomTerm].setSentimentOverrideValue(0);
                }
              }
            }
          }
        }
      }

      this.bgIdiomsApplied = true;
    }

    if (options.bgExplainClassification) {
      this.sgClassificationRationale = rationale.toString();
    }
  }
}
