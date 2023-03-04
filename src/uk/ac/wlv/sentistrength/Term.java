package uk.ac.wlv.sentistrength;

/**
 * Term类为处理单个词的逻辑主体类，包含单个词处理的查询、提取等主要操作
 */
public class Term {
   /**
    * igContentTypeWord常量表示术语类型为单词
    */
   private final int igContentTypeWord = 1;
   /**
    * igContentTypePunctuation常量表示术语类型为标点符号
    */
   private final int igContentTypePunctuation = 2;
   /**
    * igContentTypeEmoticon常量表示术语类型为表情符号
    */
   private final int igContentTypeEmoticon = 3;
   /**
    * igContentType表示术语类型
    */
   private int igContentType = 0;
   /**
    * sgOriginalWord表示术语原始单词
    */
   private String sgOriginalWord = "";
   /**
    * sgLCaseWord表示术语的小写版本
    */
   private String sgLCaseWord = "";
   /**
    * sgTranslatedWord表示术语的翻译版本
    */
   private String sgTranslatedWord = "";
   /**
    * sgWordEmphasis表示术语的强调
    */
   private String sgWordEmphasis = "";
   /**
    * igWordSentimentID表示术语情感识别ID
    */
   private int igWordSentimentID = 0;
   /**
    * bgNegatingWord表示术语是否是否定词
    */
   private boolean bgNegatingWord = false;
   /**
    * bgNegatingWordCalculated表示是否已计算否定词
    */
   private boolean bgNegatingWordCalculated = false;
   /**
    * bgWordSentimentIDCalculated表示是否已计算术语情感识别ID
    */
   private boolean bgWordSentimentIDCalculated = false;
   /**
    * bgProperNoun表示术语是否为专有名词
    */
   private boolean bgProperNoun = false;
   /**
    * bgProperNounCalculated表示是否已计算术语为专有名词
    */
   private boolean bgProperNounCalculated = false;
   /**
    * sgPunctuation表示标点符号
    */
   private String sgPunctuation = "";
   /**
    * sgPunctuationEmphasis表示标点符号的强调
    */
   private String sgPunctuationEmphasis = "";
   /**
    * sgEmoticon表示表情符号
    */
   private String sgEmoticon = "";
   /**
    * igEmoticonStrength表示表情符号强度
    */
   int igEmoticonStrength = 0;
   /**
    * igBoosterWordScore表示助词词的得分
    */
   private int igBoosterWordScore = 999;
   /**
    * resources表示分类资源
    */
   private ClassificationResources resources;
   /**
    * options表示分类选项
    */
   private ClassificationOptions options;
   /**
    * bgAllCapitals表示术语是否全部大写
    */
   private boolean bgAllCapitals = false;
   /**
    * bgAllCaptialsCalculated表示是否已计算术语全部大写
    */
   private boolean bgAllCaptialsCalculated = false;
   /**
    * bgOverrideSentimentScore表示是否覆盖术语情感分数
    */
   private boolean bgOverrideSentimentScore = false;
   /**
    * igOverrideSentimentScore表示覆盖情绪得分
    */
   private int igOverrideSentimentScore = 0;

   /**
    * 提取下一个词语、标点符号或表情符号
    * @param sWordAndPunctuation 包含要提取内容的字符串
    * @param classResources 分类资源
    * @param classOptions 分类选项
    * @return 返回下一个要提取的字符在字符串中的位置，如果没有更多字符，则返回-1
    */
   public int extractNextWordOrPunctuationOrEmoticon(String sWordAndPunctuation, ClassificationResources classResources, ClassificationOptions classOptions) {
      int iWordCharOrAppostrophe =1;
      int iPunctuation =1;
      int iPos = 0;
      int iLastCharType = 0;
      String sChar = "";
      this.resources = classResources;
      this.options = classOptions;
      int iTextLength = sWordAndPunctuation.length();
      if (this.codeEmoticon(sWordAndPunctuation)) {
         return -1;
      } else {
         for(; iPos < iTextLength; ++iPos) {
            sChar = sWordAndPunctuation.substring(iPos, iPos + 1);
            if (!Character.isLetterOrDigit(sWordAndPunctuation.charAt(iPos)) && (this.options.bgAlwaysSplitWordsAtApostrophes || !sChar.equals("'") || iPos <= 0 || iPos >= iTextLength - 1 || !Character.isLetter(sWordAndPunctuation.charAt(iPos + 1))) && !sChar.equals("$") && !sChar.equals("£") && !sChar.equals("@") && !sChar.equals("_")) {
               if (iLastCharType == 1) {
                  this.codeWord(sWordAndPunctuation.substring(0, iPos));
                  return iPos;
               }

               iLastCharType = 2;
            } else {
               if (iLastCharType == 2) {
                  this.codePunctuation(sWordAndPunctuation.substring(0, iPos));
                  return iPos;
               }

               iLastCharType = 1;
            }
         }

         switch(iLastCharType) {
            case 1:
               this.codeWord(sWordAndPunctuation);
               break;
            case 2:
               this.codePunctuation(sWordAndPunctuation);
         }

         return -1;
      }
   }

   /**
    * 获取Term的标记形式，1对应单词，2对应标点符号，3对应表情
    * @return Term的标记形式，以XML格式返回
    */
   public String getTag() {
      switch(this.igContentType) {
         case 1:
            if (this.sgWordEmphasis != "") {
               return "<w equiv=\"" + this.sgTranslatedWord + "\" em=\"" + this.sgWordEmphasis + "\">" + this.sgOriginalWord + "</w>";
            }

            return "<w>" + this.sgOriginalWord + "</w>";
         case 2:
            if (this.sgPunctuationEmphasis != "") {
               return "<p equiv=\"" + this.sgPunctuation + "\" em=\"" + this.sgPunctuationEmphasis + "\">" + this.sgPunctuation + this.sgPunctuationEmphasis + "</p>";
            }

            return "<p>" + this.sgPunctuation + "</p>";
         case 3:
            if (this.igEmoticonStrength == 0) {
               return "<e>" + this.sgEmoticon + "</e>";
            } else {
               if (this.igEmoticonStrength == 1) {
                  return "<e em=\"+\">" + this.sgEmoticon + "</e>";
               }

               return "<e em=\"-\">" + this.sgEmoticon + "</e>";
            }
         default:
            return "";
      }
   }

   /**
    * 获取情感词的ID，如果未计算过，则进行计算
    * @return 返回情感词的ID
    */
   public int getSentimentID() {
      if (!this.bgWordSentimentIDCalculated) {
         this.igWordSentimentID = this.resources.sentimentWords.getSentimentID(this.sgTranslatedWord.toLowerCase());
         this.bgWordSentimentIDCalculated = true;
      }

      return this.igWordSentimentID;
   }

   /**
    * 设置情感分数的覆盖值
    * @param iSentiment 要设置的情感分数
    */
   public void setSentimentOverrideValue(int iSentiment) {
      this.bgOverrideSentimentScore = true;
      this.igOverrideSentimentScore = iSentiment;
   }

   /**
    * 获取情感分数，如果已经设置了覆盖值，则返回覆盖值，否则返回情感词的分数
    * @return 返回情感分数
    */
   public int getSentimentValue() {
      if (this.bgOverrideSentimentScore) {
         return this.igOverrideSentimentScore;
      } else {
         return this.getSentimentID() < 1 ? 0 : this.resources.sentimentWords.getSentiment(this.igWordSentimentID);
      }
   }

   /**
    * 获取词的强调长度
    * @return 返回强调长度
    */
   public int getWordEmphasisLength() {
      return this.sgWordEmphasis.length();
   }

   /**
    * 获取强调词
    * @return 返回强调词
    */
   public String getWordEmphasis() {
      return this.sgWordEmphasis;
   }

   /**
    * 判断词是否包含强调
    * @return 如果包含强调则返回true，否则返回false
    */
   public boolean containsEmphasis() {
      if (this.igContentType == 1) {
         return this.sgWordEmphasis.length() > 1;
      } else if (this.igContentType == 2) {
         return this.sgPunctuationEmphasis.length() > 1;
      } else {
         return false;
      }
   }

   /**
    * 获取词的翻译
    * @return 返回翻译
    */
   public String getTranslatedWord() {
      return this.sgTranslatedWord;
   }

   /**
    * 获取翻译后的词组成的短语
    * @return 如果是单词则返回单词，否则返回标点符号或表情符号
    */
   public String getTranslation() {
      if (this.igContentType == 1) {
         return this.sgTranslatedWord;
      } else if (this.igContentType == 2) {
         return this.sgPunctuation;
      } else {
         return this.igContentType == 3 ? this.sgEmoticon : "";
      }
   }

   /**
    * 获取助词的情感分数
    * @return 返回助词的情感分数
    */
   public int getBoosterWordScore() {
      if (this.igBoosterWordScore == 999) {
         this.setBoosterWordScore();
      }

      return this.igBoosterWordScore;
   }

   /**
    * 判断词是否全为大写
    * @return 如果全为大写则返回true，否则返回false
    */
   public boolean isAllCapitals() {
      if (!this.bgAllCaptialsCalculated) {
         if (this.sgOriginalWord == this.sgOriginalWord.toUpperCase()) {
            this.bgAllCapitals = true;
         } else {
            this.bgAllCapitals = false;
         }

         this.bgAllCaptialsCalculated = true;
      }

      return this.bgAllCapitals;
   }

   /**
    * 设置助词的得分，调用 resources.boosterWords 中的 getBoosterStrength 方法获取得分值
    */
   public void setBoosterWordScore() {
      this.igBoosterWordScore = this.resources.boosterWords.getBoosterStrength(this.sgTranslatedWord);
   }

   /**
    * 判断标点符号是否包含给定的标点符号字符串 sPunctuation
    * @param sPunctuation 要查找的标点符号字符串
    * @return 若包含则返回 true，否则返回 false
    */
   public boolean punctuationContains(String sPunctuation) {
      if (this.igContentType != 2) {
         return false;
      } else if (this.sgPunctuation.indexOf(sPunctuation) > -1) {
         return true;
      } else {
         return this.sgPunctuationEmphasis != "" && this.sgPunctuationEmphasis.indexOf(sPunctuation) > -1;
      }
   }

   /**
    * 获取标点符号强调的长度
    * @return 标点符号强调长度
    */
   public int getPunctuationEmphasisLength() {
      return this.sgPunctuationEmphasis.length();
   }

   /**
    * 获取表情符号情感强度
    * @return 表情符号情感强度
    */
   public int getEmoticonSentimentStrength() {
      return this.igEmoticonStrength;
   }

   /**
    * 获取表情符号字符串
    * @return 表情符号字符串
    */
   public String getEmoticon() {
      return this.sgEmoticon;
   }

   /**
    * 获取翻译后的标点符号字符串
    * @return 翻译后的标点符号字符串
    */
   public String getTranslatedPunctuation() {
      return this.sgPunctuation;
   }

   /**
    * 判断是否为单词
    * @return 若是单词则返回 true，否则返回 false
    */
   public boolean isWord() {
      return this.igContentType == 1;
   }

   /**
    * 判断是否为标点符号
    * @return 若是标点符号则返回 true，否则返回 false
    */
   public boolean isPunctuation() {
      return this.igContentType == 2;
   }

   /**
    * 判断是否为专有名词
    * @return 若是专有名词则返回 true，否则返回 false
    */
   public boolean isProperNoun() {
      if (this.igContentType != 1) {
         return false;
      } else {
         if (!this.bgProperNounCalculated) {
            if (this.sgOriginalWord.length() > 1) {
               String sFirstLetter = this.sgOriginalWord.substring(0, 1);
               if (!sFirstLetter.toLowerCase().equals(sFirstLetter.toUpperCase()) && !this.sgOriginalWord.substring(0, 2).toUpperCase().equals("I'")) {
                  String sWordRemainder = this.sgOriginalWord.substring(1);
                  if (sFirstLetter.equals(sFirstLetter.toUpperCase()) && sWordRemainder.equals(sWordRemainder.toLowerCase())) {
                     this.bgProperNoun = true;
                  }
               }
            }

            this.bgProperNounCalculated = true;
         }

         return this.bgProperNoun;
      }
   }

   /**
    * 判断是否为表情符号
    * @return 若是表情符号则返回 true，否则返回 false
    */
   public boolean isEmoticon() {
      return this.igContentType == 3;
   }

   public String getText() {
      if (this.igContentType == 1) {
         return this.sgTranslatedWord.toLowerCase();
      } else if (this.igContentType == 2) {
         return this.sgPunctuation;
      } else {
         return this.igContentType == 3 ? this.sgEmoticon : "";
      }
   }

   public String getOriginalText() {
      if (this.igContentType == 1) {
         return this.sgOriginalWord;
      } else if (this.igContentType == 2) {
         return this.sgPunctuation + this.sgPunctuationEmphasis;
      } else {
         return this.igContentType == 3 ? this.sgEmoticon : "";
      }
   }

   public boolean isNegatingWord() {
      if (!this.bgNegatingWordCalculated) {
         if (this.sgLCaseWord.length() == 0) {
            this.sgLCaseWord = this.sgTranslatedWord.toLowerCase();
         }

         this.bgNegatingWord = this.resources.negatingWords.negatingWord(this.sgLCaseWord);
         this.bgNegatingWordCalculated = true;
      }

      return this.bgNegatingWord;
   }

   public boolean matchesString(String sText, boolean bConvertToLowerCase) {
      if (sText.length() != this.sgTranslatedWord.length()) {
         return false;
      } else {
         if (bConvertToLowerCase) {
            if (this.sgLCaseWord.length() == 0) {
               this.sgLCaseWord = this.sgTranslatedWord.toLowerCase();
            }

            if (sText.equals(this.sgLCaseWord)) {
               return true;
            }
         } else if (sText.equals(this.sgTranslatedWord)) {
            return true;
         }

         return false;
      }
   }

   public boolean matchesStringWithWildcard(String sTextWithWildcard, boolean bConvertToLowerCase) {
      int iStarPos = sTextWithWildcard.lastIndexOf("*");
      if (iStarPos >= 0 && iStarPos == sTextWithWildcard.length() - 1) {
         sTextWithWildcard = sTextWithWildcard.substring(0, iStarPos);
         if (bConvertToLowerCase) {
            if (this.sgLCaseWord.length() == 0) {
               this.sgLCaseWord = this.sgTranslatedWord.toLowerCase();
            }

            if (sTextWithWildcard.equals(this.sgLCaseWord)) {
               return true;
            }

            if (sTextWithWildcard.length() >= this.sgLCaseWord.length()) {
               return false;
            }

            if (sTextWithWildcard.equals(this.sgLCaseWord.substring(0, sTextWithWildcard.length()))) {
               return true;
            }
         } else {
            if (sTextWithWildcard.equals(this.sgTranslatedWord)) {
               return true;
            }

            if (sTextWithWildcard.length() >= this.sgTranslatedWord.length()) {
               return false;
            }

            if (sTextWithWildcard.equals(this.sgTranslatedWord.substring(0, sTextWithWildcard.length()))) {
               return true;
            }
         }

         return false;
      } else {
         return this.matchesString(sTextWithWildcard, bConvertToLowerCase);
      }
   }

   private void codeWord(String sWord) {
      String sWordNew = "";
      String sEm = "";
      if (this.options.bgCorrectExtraLetterSpellingErrors) {
         int iSameCount = 0;
         int iLastCopiedPos = 0;
         int iWordEnd = sWord.length() - 1;

         int iPos;
         for(iPos = 1; iPos <= iWordEnd; ++iPos) {
            if (sWord.substring(iPos, iPos + 1).compareToIgnoreCase(sWord.substring(iPos - 1, iPos)) == 0) {
               ++iSameCount;
            } else {
               if (iSameCount > 0 && this.options.sgIllegalDoubleLettersInWordMiddle.indexOf(sWord.substring(iPos - 1, iPos)) >= 0) {
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

         if (iSameCount > 0 && this.options.sgIllegalDoubleLettersAtWordEnd.indexOf(sWord.substring(iPos - 1, iPos)) >= 0) {
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
      }

      if (sWordNew == "") {
         sWordNew = sWord;
      }

      this.igContentType = 1;
      this.sgOriginalWord = sWord;
      this.sgWordEmphasis = sEm;
      this.sgTranslatedWord = sWordNew;
      if (this.sgTranslatedWord.indexOf("@") < 0) {
         if (this.options.bgCorrectSpellingsUsingDictionary) {
            this.correctSpellingInTranslatedWord();
         }

         if (this.options.bgUseLemmatisation) {
            if (this.sgTranslatedWord.equals("")) {
               sWordNew = this.resources.lemmatiser.lemmatise(this.sgOriginalWord);
               if (!sWordNew.equals(this.sgOriginalWord)) {
                  this.sgTranslatedWord = sWordNew;
               }
            } else {
               this.sgTranslatedWord = this.resources.lemmatiser.lemmatise(this.sgTranslatedWord);
            }
         }
      }

   }

   private void correctSpellingInTranslatedWord() {
      if (!this.resources.correctSpellings.correctSpelling(this.sgTranslatedWord.toLowerCase())) {
         int iLastChar = this.sgTranslatedWord.length() - 1;

         for(int iPos = 1; iPos <= iLastChar; ++iPos) {
            if (this.sgTranslatedWord.substring(iPos, iPos + 1).compareTo(this.sgTranslatedWord.substring(iPos - 1, iPos)) == 0) {
               String sReplaceWord = this.sgTranslatedWord.substring(0, iPos) + this.sgTranslatedWord.substring(iPos + 1);
               if (this.resources.correctSpellings.correctSpelling(sReplaceWord.toLowerCase())) {
                  this.sgWordEmphasis = this.sgWordEmphasis + this.sgTranslatedWord.substring(iPos, iPos + 1);
                  this.sgTranslatedWord = sReplaceWord;
                  return;
               }
            }
         }

         if (iLastChar > 5) {
            if (this.sgTranslatedWord.indexOf("haha") > 0) {
               this.sgWordEmphasis = this.sgWordEmphasis + this.sgTranslatedWord.substring(3, this.sgTranslatedWord.indexOf("haha") + 2);
               this.sgTranslatedWord = "haha";
               return;
            }

            if (this.sgTranslatedWord.indexOf("hehe") > 0) {
               this.sgWordEmphasis = this.sgWordEmphasis + this.sgTranslatedWord.substring(3, this.sgTranslatedWord.indexOf("hehe") + 2);
               this.sgTranslatedWord = "hehe";
               return;
            }
         }

      }
   }

   private boolean codeEmoticon(String sPossibleEmoticon) {
      int iEmoticonStrength = this.resources.emoticons.getEmoticon(sPossibleEmoticon);
      if (iEmoticonStrength != 999) {
         this.igContentType = 3;
         this.sgEmoticon = sPossibleEmoticon;
         this.igEmoticonStrength = iEmoticonStrength;
         return true;
      } else {
         return false;
      }
   }

   private void codePunctuation(String sPunctuation) {
      if (sPunctuation.length() > 1) {
         this.sgPunctuation = sPunctuation.substring(0, 1);
         this.sgPunctuationEmphasis = sPunctuation.substring(1);
      } else {
         this.sgPunctuation = sPunctuation;
         this.sgPunctuationEmphasis = "";
      }

      this.igContentType = 2;
   }
}
