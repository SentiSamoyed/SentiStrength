package uk.ac.wlv.sentistrength;

public class Term {
   private final int igContentTypeWord = 1;
   private final int igContentTypePunctuation = 2;
   private final int igContentTypeEmoticon = 3;
   private int igContentType = 0;
   private String sgOriginalWord = "";
   private String sgLCaseWord = "";
   private String sgTranslatedWord = "";
   private String sgWordEmphasis = "";
   private int igWordSentimentID = 0;
   private boolean bgNegatingWord = false;
   private boolean bgNegatingWordCalculated = false;
   private boolean bgWordSentimentIDCalculated = false;
   private boolean bgProperNoun = false;
   private boolean bgProperNounCalculated = false;
   private String sgPunctuation = "";
   private String sgPunctuationEmphasis = "";
   private String sgEmoticon = "";
   int igEmoticonStrength = 0;
   private int igBoosterWordScore = 999;
   private ClassificationResources resources;
   private ClassificationOptions options;
   private boolean bgAllCapitals = false;
   private boolean bgAllCaptialsCalculated = false;
   private boolean bgOverrideSentimentScore = false;
   private int igOverrideSentimentScore = 0;

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

   public int getSentimentID() {
      if (!this.bgWordSentimentIDCalculated) {
         this.igWordSentimentID = this.resources.sentimentWords.getSentimentID(this.sgTranslatedWord.toLowerCase());
         this.bgWordSentimentIDCalculated = true;
      }

      return this.igWordSentimentID;
   }

   public void setSentimentOverrideValue(int iSentiment) {
      this.bgOverrideSentimentScore = true;
      this.igOverrideSentimentScore = iSentiment;
   }

   public int getSentimentValue() {
      if (this.bgOverrideSentimentScore) {
         return this.igOverrideSentimentScore;
      } else {
         return this.getSentimentID() < 1 ? 0 : this.resources.sentimentWords.getSentiment(this.igWordSentimentID);
      }
   }

   public int getWordEmphasisLength() {
      return this.sgWordEmphasis.length();
   }

   public String getWordEmphasis() {
      return this.sgWordEmphasis;
   }

   public boolean containsEmphasis() {
      if (this.igContentType == 1) {
         return this.sgWordEmphasis.length() > 1;
      } else if (this.igContentType == 2) {
         return this.sgPunctuationEmphasis.length() > 1;
      } else {
         return false;
      }
   }

   public String getTranslatedWord() {
      return this.sgTranslatedWord;
   }

   public String getTranslation() {
      if (this.igContentType == 1) {
         return this.sgTranslatedWord;
      } else if (this.igContentType == 2) {
         return this.sgPunctuation;
      } else {
         return this.igContentType == 3 ? this.sgEmoticon : "";
      }
   }

   public int getBoosterWordScore() {
      if (this.igBoosterWordScore == 999) {
         this.setBoosterWordScore();
      }

      return this.igBoosterWordScore;
   }

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

   public void setBoosterWordScore() {
      this.igBoosterWordScore = this.resources.boosterWords.getBoosterStrength(this.sgTranslatedWord);
   }

   public boolean punctuationContains(String sPunctuation) {
      if (this.igContentType != 2) {
         return false;
      } else if (this.sgPunctuation.indexOf(sPunctuation) > -1) {
         return true;
      } else {
         return this.sgPunctuationEmphasis != "" && this.sgPunctuationEmphasis.indexOf(sPunctuation) > -1;
      }
   }

   public int getPunctuationEmphasisLength() {
      return this.sgPunctuationEmphasis.length();
   }

   public int getEmoticonSentimentStrength() {
      return this.igEmoticonStrength;
   }

   public String getEmoticon() {
      return this.sgEmoticon;
   }

   public String getTranslatedPunctuation() {
      return this.sgPunctuation;
   }

   public boolean isWord() {
      return this.igContentType == 1;
   }

   public boolean isPunctuation() {
      return this.igContentType == 2;
   }

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
