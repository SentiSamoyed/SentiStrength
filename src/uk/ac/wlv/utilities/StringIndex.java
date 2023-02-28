package uk.ac.wlv.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class StringIndex {
   private int igTextMax = 10000000;
   public String[] sgText;
   public String[] sgTextComment;
   private int[] igTextLessPtr;
   private int[] igTextMorePtr;
   private int[] igTextCount;
   private int igTextLast = -1;
   private boolean bgIncludeCounts = false;

   public void initialise(int iVocabMaxIfOverrideDefault, boolean bIncludeCounts, boolean bIncludeComments) {
      this.bgIncludeCounts = bIncludeCounts;
      if (iVocabMaxIfOverrideDefault > 0) {
         this.igTextMax = iVocabMaxIfOverrideDefault;
      }

      this.sgText = new String[this.igTextMax];
      this.igTextLessPtr = new int[this.igTextMax];
      this.igTextMorePtr = new int[this.igTextMax];
      this.igTextLast = -1;
      int i;
      if (this.bgIncludeCounts) {
         this.igTextCount = new int[this.igTextMax];

         for(i = 0; i < this.igTextMax; ++i) {
            this.igTextCount[i] = 0;
         }
      }

      if (bIncludeComments) {
         this.sgTextComment = new String[this.igTextMax];

         for(i = 0; i < this.igTextMax; ++i) {
            this.igTextCount[i] = 0;
         }
      }

   }

   public boolean load(String sVocabTermPtrsCountFileName) {
      File f = new File(sVocabTermPtrsCountFileName);
      if (!f.exists()) {
         System.out.println("Could not find the vocab file: " + sVocabTermPtrsCountFileName);
         return false;
      } else {
         try {
            BufferedReader rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sVocabTermPtrsCountFileName), "UTF8"));
            String sLine = rReader.readLine();
            String[] sData = sLine.split("\t");
            this.igTextLast = -1;

            while(rReader.ready()) {
               sLine = rReader.readLine();
               if (sLine.length() > 0) {
                  sData = sLine.split("\t");
                  if (sData.length > 2) {
                     if (this.igTextLast == this.igTextMax - 1) {
                        this.increaseArraySizes(this.igTextMax * 2);
                     }

                     this.sgText[++this.igTextLast] = sData[0];
                     this.igTextLessPtr[this.igTextLast] = Integer.parseInt(sData[1]);
                     this.igTextMorePtr[this.igTextLast] = Integer.parseInt(sData[2]);
                     this.igTextCount[this.igTextLast] = Integer.parseInt(sData[3]);
                  }
               }
            }

            rReader.close();
            return true;
         } catch (IOException var7) {
            System.out.println("Could not open file for reading or read from file: " + sVocabTermPtrsCountFileName);
            var7.printStackTrace();
            return false;
         }
      }
   }

   public int getLastWordID() {
      return this.igTextLast;
   }

   public boolean save(String sVocabTermPtrsCountFileName) {
      if (!FileOps.backupFileAndDeleteOriginal(sVocabTermPtrsCountFileName, 10)) {
         System.out.println("Could not backup vocab! Perhaps no index exists yet.");
      }

      try {
         BufferedWriter wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sVocabTermPtrsCountFileName), "UTF8"));
         wWriter.write("Word\tLessPtr\tMorePtr\tAllTopics");

         for(int i = 0; i <= this.igTextLast; ++i) {
            wWriter.write(this.sgText[i] + "\t" + this.igTextLessPtr[i] + "\t" + this.igTextMorePtr[i] + "\t" + this.igTextCount[i] + "\n");
         }

         wWriter.close();
         return true;
      } catch (IOException var4) {
         System.out.println("Could not open file for writing or write to file: " + sVocabTermPtrsCountFileName);
         var4.printStackTrace();
         return false;
      }
   }

   public int addString(String sText, boolean bRecordCount) {
      boolean iPos = true;
      if (this.igTextLast == this.igTextMax - 1) {
         this.increaseArraySizes(this.igTextMax * 2);
      }

      int iPos1;
      if (bRecordCount) {
         iPos1 = Trie.i_GetTriePositionForStringAndAddCount(sText, this.sgText, this.igTextCount, this.igTextLessPtr, this.igTextMorePtr, 0, this.igTextLast, false, 1);
      } else {
         iPos1 = Trie.i_GetTriePositionForString(sText, this.sgText, this.igTextLessPtr, this.igTextMorePtr, 0, this.igTextLast, false);
      }

      if (iPos1 > this.igTextLast) {
         this.igTextLast = iPos1;
      }

      return iPos1;
   }

   public int findString(String sText) {
      return Trie.i_GetTriePositionForString(sText, this.sgText, this.igTextLessPtr, this.igTextMorePtr, 0, this.igTextLast, true);
   }

   public void add1ToCount(int iStringPos) {
      int var10002 = this.igTextCount[iStringPos]++;
   }

   private void increaseArraySizes(int iNewArraySize) {
      if (iNewArraySize > this.igTextMax) {
         String[] sgTextTemp = new String[iNewArraySize];
         int[] iTextLessPtrTemp = new int[iNewArraySize];
         int[] iTextMorePtrTemp = new int[iNewArraySize];
         System.arraycopy(this.sgText, 0, sgTextTemp, 0, this.igTextMax);
         System.arraycopy(this.igTextLessPtr, 0, iTextLessPtrTemp, 0, this.igTextMax);
         System.arraycopy(this.igTextMorePtr, 0, iTextMorePtrTemp, 0, this.igTextMax);
         if (this.bgIncludeCounts) {
            int[] iVocabCountTemp = new int[iNewArraySize];
            System.arraycopy(this.igTextCount, 0, iVocabCountTemp, 0, this.igTextMax);

            for(int i = this.igTextMax; i < iNewArraySize; ++i) {
               this.igTextCount[i] = 0;
            }

            this.igTextCount = iVocabCountTemp;
         }

         this.igTextMax = iNewArraySize;
         this.igTextLessPtr = iTextLessPtrTemp;
         this.igTextMorePtr = iTextMorePtrTemp;
      }
   }

   private static String[] increaseArraySize(String[] sArray, int iCurrentArraySize, int iNewArraySize) {
      if (iNewArraySize <= iCurrentArraySize) {
         return sArray;
      } else {
         String[] sArrayTemp = new String[iNewArraySize];
         System.arraycopy(sArray, 0, sArrayTemp, 0, iCurrentArraySize);
         return sArrayTemp;
      }
   }

   private static int[] increaseArraySize(int[] iArray, int iCurrentArraySize, int iNewArraySize) {
      if (iNewArraySize <= iCurrentArraySize) {
         return iArray;
      } else {
         int[] iArrayTemp = new int[iNewArraySize];
         System.arraycopy(iArray, 0, iArrayTemp, 0, iCurrentArraySize);
         return iArrayTemp;
      }
   }

   public String getString(int iStringPos) {
      return this.sgText[iStringPos];
   }

   public String getComment(int iStringPos) {
      return this.sgTextComment[iStringPos] == null ? "" : this.sgTextComment[iStringPos];
   }

   public void addComment(int iStringPos, String sComment) {
      this.sgTextComment[iStringPos] = sComment;
   }

   public int getCount(int iStringPos) {
      return this.igTextCount[iStringPos];
   }

   public void setCountToZero(int iStringPos) {
      this.igTextCount[iStringPos] = 0;
   }

   public void setAllCountsToZero() {
      for(int i = 0; i <= this.igTextLast; ++i) {
         this.igTextCount[i] = 0;
      }

   }
}
