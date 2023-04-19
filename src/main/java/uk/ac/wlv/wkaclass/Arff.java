package uk.ac.wlv.wkaclass;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;
import uk.ac.wlv.sentistrength.core.component.Paragraph;
import uk.ac.wlv.sentistrength.option.TextParsingOptions;
import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;
import uk.ac.wlv.utilities.StringIndex;

import java.io.*;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Arff {
  public static final int igArffNone = 0;
  public static final int igArffBinary = 1;
  public static final int igArffTrinary = 2;
  public static final int igArffScale = 3;
  public static final int igArffPosNeg = 4;
  public static boolean bgSaveArffAsCondensed = true;

  public void main(String[] args) {
    boolean[] bArgumentRecognised = new boolean[args.length];
    String sUnlabelledTextFile = "";
    String sLabelledTextFile = "";
    String sArffFileIn = "";
    String sTextFileOut = "";
    String sClassifier = "smo";
    int iNGrams = 3;
    int iMaxFeatures = 0;
    int iClassType = 4;
    int iClassFor0 = 0;
    int iMinFeatureFrequency = 1;
    boolean bCompleteProcessing = false;
    overallHelp();

    int i;
    for (i = 0; i < args.length; ++i) {
      bArgumentRecognised[i] = false;
    }

    for (i = 0; i < args.length; ++i) {
      if (args[i].equalsIgnoreCase("arff")) {
        bArgumentRecognised[i] = true;
      }

      if (args[i].equalsIgnoreCase("complete")) {
        bCompleteProcessing = true;
        bArgumentRecognised[i] = true;
      }

      if (args[i].equalsIgnoreCase("scale")) {
        iClassType = 3;
        bArgumentRecognised[i] = true;
      }

      if (args[i].equalsIgnoreCase("binary")) {
        iClassType = 1;
        bArgumentRecognised[i] = true;
      }

      if (args[i].equalsIgnoreCase("trinary")) {
        iClassType = 2;
        bArgumentRecognised[i] = true;
      }

      if (args[i].equalsIgnoreCase("posneg")) {
        iClassType = 4;
        bArgumentRecognised[i] = true;
      }

      if (i < args.length - 1) {
        if (args[i].equalsIgnoreCase("unlabelledText")) {
          sUnlabelledTextFile = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("labelledText")) {
          sLabelledTextFile = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("arffFileIn")) {
          sArffFileIn = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("textFileOut")) {
          sTextFileOut = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("nGrams")) {
          iNGrams = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("maxFeatures")) {
          iMaxFeatures = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("classifier")) {
          sClassifier = args[i + 1];
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("zeros")) {
          iClassFor0 = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }

        if (args[i].equalsIgnoreCase("minFeatureFreq")) {
          iMinFeatureFrequency = Integer.parseInt(args[i + 1]);
          bArgumentRecognised[i] = true;
          bArgumentRecognised[i + 1] = true;
        }
      }
    }

    for (i = 0; i < args.length; ++i) {
      if (!bArgumentRecognised[i]) {
        System.out.println("Unrecognised Arff command - wrong spelling or case?: " + args[i]);
        return;
      }
    }

    if (bCompleteProcessing) {
      if (sUnlabelledTextFile.length() == 0) {
        System.out.println("An unlabelled text file must be specified [complete]");
        return;
      }

      if (sLabelledTextFile.length() == 0) {
        System.out.println("A labelled text file must be specified [complete]");
        return;
      }

      System.out.println("Complete processing starting...");
      System.out.println();
      System.out.println("Convert unlabelled texts " + sUnlabelledTextFile + " to Arff based on labelled text file " + sLabelledTextFile);
      System.out.println("Options: classtype " + iClassType + " Ngrams: 1-" + iNGrams + " max features: " + iMaxFeatures + " min freq for features: " + iMinFeatureFrequency);
      System.out.println(" Classtype: None=0, Binary=1, Trinary=2, Scale=3, PosNeg=4. max features = 0 => use all features (100 per 1k is optimal)");
      String[] sLabelledUnlabelled = convertUnlabelledTextFileToArffBasedOnLabelledTextFile(sLabelledTextFile, iClassType, iNGrams, iMinFeatureFrequency, iMaxFeatures, sUnlabelledTextFile);
      String sClassifiedUnlabelledArff;
      String sClassifiedUnlabelledTextFile;
      String sMergedTextFile;
      if (iClassType == 4) {
        System.out.println("predictArffClass " + sLabelledUnlabelled[0] + " training for " + sLabelledUnlabelled[2]);
        System.out.println();
        sClassifiedUnlabelledArff = PredictClass.predictArffClass(sLabelledUnlabelled[0], sClassifier, sLabelledUnlabelled[2], iClassFor0);
        sClassifiedUnlabelledTextFile = FileOps.s_ChopFileNameExtension(sClassifiedUnlabelledArff) + "_Nout.txt";
        System.out.println("convertArffToText " + sClassifiedUnlabelledArff + " -> " + sClassifiedUnlabelledTextFile);
        System.out.println();
        convertArffToText(sClassifiedUnlabelledArff, sClassifiedUnlabelledTextFile);
        sMergedTextFile = FileOps.s_ChopFileNameExtension(sClassifiedUnlabelledTextFile) + "_Nmerged.txt";
        System.out.println("mergeLabelledAndUnlabelledTextFiles " + sClassifiedUnlabelledTextFile + ", " + sUnlabelledTextFile + " -> " + sMergedTextFile);
        mergeLabelledAndUnlabelledTextFiles(sClassifiedUnlabelledTextFile, sUnlabelledTextFile, sMergedTextFile);
        System.out.println("predictArffClass " + sLabelledUnlabelled[1] + " training for " + sLabelledUnlabelled[3]);
        System.out.println();
        sClassifiedUnlabelledArff = PredictClass.predictArffClass(sLabelledUnlabelled[1], sClassifier, sLabelledUnlabelled[3], iClassFor0);
        sClassifiedUnlabelledTextFile = FileOps.s_ChopFileNameExtension(sClassifiedUnlabelledArff) + "_Pout.txt";
        System.out.println("convertArffToText " + sClassifiedUnlabelledArff + " -> " + sClassifiedUnlabelledTextFile);
        System.out.println();
        convertArffToText(sClassifiedUnlabelledArff, sClassifiedUnlabelledTextFile);
        sMergedTextFile = FileOps.s_ChopFileNameExtension(sClassifiedUnlabelledTextFile) + "_Pmerged.txt";
        System.out.println("mergeLabelledAndUnlabelledTextFiles " + sClassifiedUnlabelledTextFile + ", " + sUnlabelledTextFile + " -> " + sMergedTextFile);
        mergeLabelledAndUnlabelledTextFiles(sClassifiedUnlabelledTextFile, sUnlabelledTextFile, sMergedTextFile);
      } else {
        System.out.println("predictArffClass " + sLabelledUnlabelled[0] + " training for " + sLabelledUnlabelled[1]);
        System.out.println();
        sClassifiedUnlabelledArff = PredictClass.predictArffClass(sLabelledUnlabelled[0], sClassifier, sLabelledUnlabelled[1], iClassFor0);
        sClassifiedUnlabelledTextFile = FileOps.s_ChopFileNameExtension(sClassifiedUnlabelledArff) + "_out.txt";
        System.out.println("convertArffToText " + sClassifiedUnlabelledArff + " -> " + sClassifiedUnlabelledTextFile);
        System.out.println();
        convertArffToText(sClassifiedUnlabelledArff, sClassifiedUnlabelledTextFile);
        sMergedTextFile = FileOps.s_ChopFileNameExtension(sClassifiedUnlabelledTextFile) + "_merged.txt";
        System.out.println("mergeLabelledAndUnlabelledTextFiles " + sClassifiedUnlabelledTextFile + ", " + sUnlabelledTextFile + " -> " + sMergedTextFile);
        mergeLabelledAndUnlabelledTextFiles(sClassifiedUnlabelledTextFile, sUnlabelledTextFile, sMergedTextFile);
      }
    } else if (sUnlabelledTextFile.length() > 0 && sLabelledTextFile.length() > 0 && sTextFileOut.length() > 0) {
      System.out.println("mergeLabelledAndUnlabelledTextFiles " + sLabelledTextFile + ", " + sUnlabelledTextFile + ", " + sTextFileOut);
      mergeLabelledAndUnlabelledTextFiles(sLabelledTextFile, sUnlabelledTextFile, sTextFileOut);
    } else if (sLabelledTextFile.length() > 0 && sUnlabelledTextFile.length() > 0) {
      System.out.println("convertUnlabelledTextFileToArffBasedOnLabelledTextFile " + sLabelledTextFile + ", " + sUnlabelledTextFile);
      convertUnlabelledTextFileToArffBasedOnLabelledTextFile(sLabelledTextFile, iClassType, iNGrams, iMinFeatureFrequency, iMaxFeatures, sUnlabelledTextFile);
    } else if (sArffFileIn.length() > 0 && sTextFileOut.length() > 0) {
      System.out.println("convertArffToText " + sArffFileIn + ", " + sTextFileOut);
      convertArffToText(sArffFileIn, sTextFileOut);
    } else {
      System.out.println("Not enough parameters entered to run a process from the arff submenu. Must enter one of the following:");
      System.out.println(" complete - and parameters, to make classify unclassified text with ML");
      System.out.println(" labelledText, unlabelledText and textFileOut - merges labelled and unlabelled files");
      System.out.println(" labelledText, unlabelledText - converts unlabelled to ARFF based on labelled");
      System.out.println(" arffFileIn, textFileOut - converts ARFF to plain text");
    }

    System.out.println("[arff] finished");
  }

  private static String[] convertUnlabelledTextFileToArffBasedOnLabelledTextFile(String sLabelledTextFile, int iClassType, int iNGrams, int iMinFeatureFrequency, int iMaxFeatures, String sUnlabelledTextFile) {
    TextParsingOptions textParsingOptions = new TextParsingOptions();
    ClassificationOptions classOptions = new ClassificationOptions();
    textParsingOptions.igNgramSize = iNGrams;
    ClassificationResources resources = new ClassificationResources();
    resources.sgSentiStrengthFolder = "c:/SentStrength_Data/";
    resources.initialise(classOptions);
    String[] sLabelledArffFiles = convertSentimentTextToArffMultiple(sLabelledTextFile, true, textParsingOptions, classOptions, resources, iClassType, iMinFeatureFrequency, "");

    int i;
    for (i = 0; i < 99 && sLabelledArffFiles[i] != null && !sLabelledArffFiles[i].equals(""); ++i) {
    }

    int iLabelledArffFileCount = i;
    String[] sLabelledArffFilesReduced;
    if (iMaxFeatures > 0) {
      sLabelledArffFilesReduced = new String[sLabelledArffFiles.length];

      for (i = 0; i < iLabelledArffFileCount; ++i) {
        sLabelledArffFilesReduced[i] = FileOps.s_ChopFileNameExtension(sLabelledArffFiles[i]) + " " + iMaxFeatures + ".arff";
        makeArffWithTopNAttributes(sLabelledArffFiles[i], iMaxFeatures, sLabelledArffFilesReduced[i]);
      }

      sLabelledArffFiles = sLabelledArffFilesReduced;
    }

    sLabelledArffFilesReduced = convertSentimentTextToArffMultiple(sUnlabelledTextFile, true, textParsingOptions, classOptions, resources, iClassType, 1, sLabelledArffFiles[i - 1]);
    String[] sResults;
    if (iClassType == 4) {
      sResults = new String[]{sLabelledArffFiles[iLabelledArffFileCount - 1], sLabelledArffFiles[iLabelledArffFileCount - 2], sLabelledArffFilesReduced[iLabelledArffFileCount - 1], sLabelledArffFilesReduced[iLabelledArffFileCount - 2]};
    } else {
      sResults = new String[]{sLabelledArffFiles[iLabelledArffFileCount - 1], sLabelledArffFilesReduced[iLabelledArffFileCount - 1]};
    }

    return sResults;
  }

  private static void overallHelp() {
    System.out.println("--------------------------------------------------------------------------");
    System.out.println("- Text processing and ML prediction commands - arff to trigger this menu -");
    System.out.println("--------------------------------------------------------------------------");
    System.out.println("NB There is no command to convert labelled text to ARFF");
    System.out.println("A) Convert unlabelled textfile to ARFF using features in labelled textfile");
    System.out.println("unlabelledText [filename]");
    System.out.println("labelledText [filename]");
    System.out.println(" nGrams [3] 3 means all 1-3grams");
    System.out.println(" maxFeatures [0] 0=no feature reduction");
    System.out.println(" minFeatureFreq [1] ignore less frequent features");
    System.out.println(" scale binary trinary posneg(default)");
    System.out.println(" zeros [class] - class if 0 predicted. Default 0");
    System.out.println("B) Convert Arff to labelled text file");
    System.out.println("arffFileIn [filename] convert to textfile");
    System.out.println("textFileOut [filename] target textfile");
    System.out.println("C) Merge Unlabelled and labelled text files");
    System.out.println("unlabelledText [filename]");
    System.out.println("labelledText [filename]");
    System.out.println("textFileOut [filename]");
    System.out.println("D) Do all above");
    System.out.println("complete - input labelled, unlabelled, output classified text");
    System.out.println(" classifier [smo] classifier name for complete (slog, smoreg, ada, dec, libsvm, j48, mlp, jrip, bayes, liblin");
    System.out.println("*run this via command line in parallel with wkaMachineLearning");
    System.out.println("*ALL DATA must have header row, unless specified otherwise");
    System.out.println("-----------------------------------------------------------------------------");
  }

  public static boolean convertSentimentTextToArff(String sSentiTextFileIn, String sArffFileOut, boolean bHeaderLine, TextParsingOptions textParsingOptions, ClassificationOptions classOptions, ClassificationResources resources, int iSentimentType, int iMinFeatureFrequency, StringIndex arffStringIndex) {
    if (arffStringIndex != null) {
      buildIndexFromTextFile(sSentiTextFileIn, bHeaderLine, textParsingOptions, classOptions, resources, iSentimentType, arffStringIndex, true);
      writeArffFromIndex(sSentiTextFileIn, arffStringIndex, bHeaderLine, textParsingOptions, classOptions, resources, iSentimentType, iMinFeatureFrequency, sArffFileOut, true);
    } else {
      StringIndex stringIndex = new StringIndex();
      stringIndex.initialise(0, true, false);
      buildIndexFromTextFile(sSentiTextFileIn, bHeaderLine, textParsingOptions, classOptions, resources, iSentimentType, stringIndex, false);
      writeArffFromIndex(sSentiTextFileIn, stringIndex, bHeaderLine, textParsingOptions, classOptions, resources, iSentimentType, iMinFeatureFrequency, sArffFileOut, false);
    }

    return true;
  }

  private static boolean writeArffFromIndex(String sSentiTextFileIn, StringIndex stringIndex, boolean bHeaderLine, TextParsingOptions textParsingOptions, ClassificationOptions classOptions, ClassificationResources resources, int iSentimentType, int iMinFeatureFrequency, String sArffFileOut, boolean bArffIndex) {
    String[] sData = null;
    boolean[] bIndexEntryUsed = new boolean[stringIndex.getLastWordID() + 2];
    boolean bOnlyCountNgramsUsed = false;

    try {
      BufferedWriter wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sArffFileOut), "UTF8"));
      writeArffHeadersFromIndex(sSentiTextFileIn, stringIndex, iSentimentType, textParsingOptions.igNgramSize, iMinFeatureFrequency, bArffIndex, bIndexEntryUsed, wWriter);
      BufferedReader rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSentiTextFileIn), "UTF8"));
      String sLine;
      if (bHeaderLine && rReader.ready()) {
        sLine = rReader.readLine();
      }

      while (true) {
        do {
          if (!rReader.ready()) {
            rReader.close();
            wWriter.close();
            return true;
          }

          sLine = rReader.readLine();
        } while (sLine.length() <= 0);

        boolean iNgramCount = false;
        stringIndex.setAllCountsToZero();
        Paragraph para = new Paragraph();
        if (iSentimentType == 4) {
          sData = sLine.split("\t");
          if (sData.length > 2) {
            para.setParagraph(sData[2], resources, classOptions);
          }

          if (sData.length == 1 && sLine.length() > 0) {
            para.setParagraph(sLine, resources, classOptions);
          }
        } else if (iSentimentType == 0) {
          para.setParagraph(sLine, (ClassificationResources) null, (ClassificationOptions) null);
        } else {
          sData = sLine.split("\t");
          if (sData.length > 1) {
            para.setParagraph(sData[1], resources, classOptions);
          }

          if (sData.length == 1 && sLine.length() > 0) {
            para.setParagraph(sLine, resources, classOptions);
          }
        }

        int iNgramCount1 = para.addToStringIndex(stringIndex, textParsingOptions, true, bArffIndex);
        if (bOnlyCountNgramsUsed) {
          iNgramCount1 = 0;
        }

        int iClassOffset = 0;
        if (iSentimentType == 4) {
          iClassOffset = 2;
          if (sData.length > 2) {
            if (sData[1].length() > 1 && sData[1].substring(0, 1).equals("-")) {
              sData[1] = sData[1].substring(1);
            }

            if (bgSaveArffAsCondensed) {
              wWriter.write("{0 " + sData[0].trim() + ",1 " + sData[1].trim() + ",");
            } else {
              wWriter.write(sData[0].trim() + "," + sData[1].trim() + ",");
            }
          } else if (bgSaveArffAsCondensed) {
            wWriter.write("{0 1,1 1,");
          } else {
            wWriter.write("1,1,");
          }
        } else if (iSentimentType != 0) {
          iClassOffset = 1;
          if (sData.length > 1) {
            if (bgSaveArffAsCondensed) {
              wWriter.write("{0 " + sData[0].trim() + ",");
            } else {
              wWriter.write(sData[0].trim() + ",");
            }
          } else if (bgSaveArffAsCondensed) {
            wWriter.write("{0 1,");
          } else {
            wWriter.write("1,");
          }
        } else if (bgSaveArffAsCondensed) {
          wWriter.write("{");
        }

        int iAttUsed = -1;

        for (int w = 0; w <= stringIndex.getLastWordID(); ++w) {
          if (bIndexEntryUsed[w]) {
            ++iAttUsed;
            if (bgSaveArffAsCondensed) {
              if (stringIndex.getCount(w) != 0) {
                wWriter.write(Integer.toString(iAttUsed + iClassOffset) + " " + stringIndex.getCount(w) + ",");
              }
            } else {
              wWriter.write(stringIndex.getCount(w) + ",");
            }
          }

          if (bOnlyCountNgramsUsed) {
            iNgramCount1 += stringIndex.getCount(w);
          }
        }

        if (bgSaveArffAsCondensed) {
          ++iAttUsed;
          wWriter.write(Integer.toString(iAttUsed + iClassOffset) + " " + iNgramCount1 + "}\n");
        } else {
          wWriter.write(iNgramCount1 + "\n");
        }
      }
    } catch (IOException var21) {
      System.out.println("Could not open file for writing or write to file: " + sArffFileOut);
      var21.printStackTrace();
      return false;
    }
  }

  private static void mergeLabelledAndUnlabelledTextFiles(String sLabelledTextFileIn, String sUnlabelledTextFileIn, String sTextFileOut) {
    try {
      BufferedReader rLabelled = new BufferedReader(new InputStreamReader(new FileInputStream(sLabelledTextFileIn), "UTF8"));
      BufferedReader rUnlabelled = new BufferedReader(new InputStreamReader(new FileInputStream(sUnlabelledTextFileIn), "UTF8"));
      BufferedWriter wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sTextFileOut)));

      while (rLabelled.ready() && rUnlabelled.ready()) {
        String sLineL = rLabelled.readLine();
        String[] sDataL = sLineL.split("\t");
        String sLineU = rUnlabelled.readLine();
        if (sDataL.length > 1) {
          wWriter.write(sDataL[0] + "\t" + sLineU + "\t" + sDataL[1] + "\n");
        } else if (sDataL.length == 1) {
          wWriter.write("0\t" + sLineU + "\t" + sDataL[0] + "\n");
        } else {
          System.out.println("short labelled line [mergeLabelledAndUnlabelledTextFiles]\n" + sLineL);
        }
      }

      rLabelled.close();
      rUnlabelled.close();
      wWriter.close();
    } catch (Exception var9) {
      System.out.println("Error [mergeLabelledAndUnlabelledTextFiles]");
      var9.printStackTrace();
    }

  }

  private static boolean writeArffHeadersFromIndex(String sSourceFile, StringIndex stringIndex, int iSentimentType, int iNgram, int iMinFeatureFrequency, boolean bArffIndex, boolean[] bArffIndexEntryUsed, BufferedWriter wWriter) {
    String sIndexWord = "";

    try {
      wWriter.write("%Arff file from Arff.java\n");
      DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Date date = new Date();
      wWriter.write("%Date: " + dateFormat.format(date) + "\n");
      wWriter.write("%filename: " + sSourceFile + "\n");
      wWriter.write("@relation AllTerms\n");
      if (iSentimentType == 4) {
        wWriter.write("@attribute Pos {1,2,3,4,5}\n");
        wWriter.write("@attribute Neg {1,2,3,4,5}\n");
      } else if (iSentimentType == 1) {
        wWriter.write("@attribute Binary {-1,1}\n");
      } else if (iSentimentType == 2) {
        wWriter.write("@attribute Trinary {-1,0,1}\n");
      } else if (iSentimentType == 3) {
        wWriter.write("@attribute Scale {-4,-3,-2,-1,0,1,2,3,4}\n");
      }

      for (int w = 0; w <= stringIndex.getLastWordID(); ++w) {
        if (bArffIndex) {
          sIndexWord = stringIndex.getString(w);
          if (i_CharsInString(sIndexWord, "+".charAt(0)) < iNgram && stringIndex.getCount(w) >= iMinFeatureFrequency) {
            bArffIndexEntryUsed[w] = true;
            if (sIndexWord.indexOf("Q_") != 0 && sIndexWord.indexOf("R_") != 0) {
              wWriter.write("@attribute " + sIndexWord + " numeric\n");
            } else {
              wWriter.write("@attribute " + stringIndex.getComment(w) + " numeric\n");
            }
          } else {
            bArffIndexEntryUsed[w] = false;
          }
        } else if (i_CharsInString(stringIndex.getString(w), " ".charAt(0)) < iNgram && stringIndex.getCount(w) >= iMinFeatureFrequency) {
          bArffIndexEntryUsed[w] = true;
          wWriter.write("@attribute " + arffSafeWordEncode(stringIndex.getString(w), true) + " numeric\n");
        } else {
          bArffIndexEntryUsed[w] = false;
        }
      }

      wWriter.write("@attribute Ngram_" + iNgram + "count numeric\n");
      wWriter.write("@data\n");
      return true;
    } catch (IOException var12) {
      System.out.println("Could not write ARFF headers to file [writeArffHeadersFromIndex]");
      var12.printStackTrace();
      return false;
    }
  }

  private static int i_CharsInString(String sText, char sChar) {
    int iCount = 0;

    for (int i = 0; i < sText.length(); ++i) {
      try {
        if (sText.charAt(i) == sChar) {
          ++iCount;
        }
      } catch (Exception var5) {
        System.out.println("i_CharsInString error with text [" + sText + "] at position i = " + i);
        System.out.println(var5.getMessage());
      }
    }

    return iCount;
  }

  public static String arffSafeWordEncode(String sWord, boolean bCodeNumbersForQuestionMarksNotUsed) {
    String sEncodedWord = "";

    try {
      sEncodedWord = URLEncoder.encode(sWord, "UTF-8");
    } catch (UnsupportedEncodingException var4) {
      System.out.print("Fatal UnsupportedEncodingException UTF-8");
      var4.printStackTrace();
    }

    if (sEncodedWord.equals(sWord)) {
      return "U_" + sWord;
    } else {
      if (sEncodedWord.indexOf("%") >= 0) {
        sEncodedWord = sEncodedWord.replace("%", "_pc");
      }

      if (sEncodedWord.indexOf("}") >= 0) {
        sEncodedWord = sEncodedWord.replace("}", "_brak");
      }

      return "E_" + sEncodedWord;
    }
  }

  private static boolean buildIndexFromTextFile(String sSentiTextFileIn, boolean bHeaderLine, TextParsingOptions textParsingOptions, ClassificationOptions classOptions, ClassificationResources resources, int iSentimentType, StringIndex stringIndex, boolean bArffIndex) {
    File f = new File(sSentiTextFileIn);
    if (!f.exists()) {
      System.out.println("Could not find the vocab file: " + sSentiTextFileIn);
      return false;
    } else {
      try {
        BufferedReader rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSentiTextFileIn), "UTF8"));
        String sLine;
        if (bHeaderLine && rReader.ready()) {
          sLine = rReader.readLine();
        }

        while (rReader.ready()) {
          sLine = rReader.readLine();
          if (sLine.length() > 0) {
            Paragraph para = new Paragraph();
            String[] sData;
            if (iSentimentType == 4) {
              sData = sLine.split("\t");
              if (sData.length > 2) {
                para.setParagraph(sData[2], resources, classOptions);
              }

              if (sData.length == 1 && sLine.length() > 0) {
                para.setParagraph(sLine, resources, classOptions);
              }
            } else if (iSentimentType == 0) {
              para.setParagraph(sLine, (ClassificationResources) null, (ClassificationOptions) null);
            } else {
              sData = sLine.split("\t");
              if (sData.length > 1) {
                para.setParagraph(sData[1], resources, classOptions);
              }

              if (sData.length == 1 && sLine.length() > 0) {
                para.setParagraph(sLine, resources, classOptions);
              }
            }

            para.addToStringIndex(stringIndex, textParsingOptions, true, bArffIndex);
          }
        }

        rReader.close();
        return true;
      } catch (IOException var14) {
        System.out.println("Could not open file for reading or read from file: " + sSentiTextFileIn);
        var14.printStackTrace();
        return false;
      }
    }
  }

  private static StringIndex buildIndexFromArff(String sArffFileIn) {
    File f = new File(sArffFileIn);
    if (!f.exists()) {
      System.out.println("Could not find the ARFF file: " + sArffFileIn);
      return null;
    } else {
      StringIndex stringIndex = new StringIndex();
      stringIndex.initialise(0, true, true);
      int iPos = 0;
      int iStringLastOld = 0;
      int var8 = 898989;

      try {
        BufferedReader rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sArffFileIn)));

        while (rReader.ready()) {
          String sLine = rReader.readLine();
          if (sLine.indexOf("@data") >= 0) {
            break;
          }

          if (sLine.length() > 0) {
            String[] sData = sLine.split(" ");
            if (sData.length == 3 && sData[0].equals("@attribute") && sData[2].equals("numeric") && sData[1].length() > 2 && sData[1].indexOf("Ngram") < 0) {
              int iStringLastOld1 = stringIndex.getLastWordID();
              if (sData[1].substring(1).equals("Q")) {
                iPos = sData[1].indexOf("_");
                if (iPos > 0) {
                  stringIndex.addString(sData[1].substring(iPos), false);
                  if (iStringLastOld1 != stringIndex.getLastWordID()) {
                    stringIndex.addComment(iStringLastOld1 + 1, sLine);
                  }
                } else {
                  System.out.println("Invalid Q index entry: " + sLine + " in " + sArffFileIn);
                }
              } else {
                stringIndex.addString(sData[1], false);
              }

              for (; iStringLastOld1 == stringIndex.getLastWordID(); System.out.println("Invalid or duplicate index entry: " + sLine + " in " + sArffFileIn)) {
                stringIndex.addString("R_" + var8++, false);
                if (iStringLastOld1 != stringIndex.getLastWordID()) {
                  stringIndex.addComment(iStringLastOld1 + 1, sLine);
                }
              }
            }
          }
        }

        rReader.close();
        return stringIndex;
      } catch (IOException var10) {
        System.out.println("Couldn't open/read from: " + sArffFileIn);
        var10.printStackTrace();
        return null;
      }
    }
  }

  public static boolean combineTwoARFFs(String sArffFile1, String sArffFile2, boolean bVerbose, String sMergedArffFile) {
    File f = new File(sArffFile1);
    if (!f.exists()) {
      System.out.println("Couldn't find Arff file: " + sArffFile1);
      return false;
    } else {
      f = new File(sArffFile2);
      if (!f.exists()) {
        System.out.println("Couldn't find Arff file: " + sArffFile2);
        return false;
      } else {
        int[] iAttributeArray = new int[1];

        try {
          BufferedReader rReader1 = new BufferedReader(new InputStreamReader(new FileInputStream(sArffFile1)));
          BufferedReader rReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(sArffFile2)));
          BufferedWriter wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sMergedArffFile)));
          printArffHeader(rReader1, wWriter, false);
          printArffHeader(rReader2, wWriter, true);
          String[] sAttributes1 = loadArffAttributes(rReader1, iAttributeArray);
          int iAttributes1Count = iAttributeArray[0];
          String[] sAttributes2 = loadArffAttributes(rReader2, iAttributeArray);
          int iAttributes2Count = iAttributeArray[0];
          boolean[] bDuplicate2 = printNonDuplicateAttributes(sAttributes1, iAttributes1Count, sAttributes2, iAttributes2Count, bVerbose, wWriter);
          printDataWithoutDuplicates(rReader1, rReader2, bDuplicate2, iAttributes1Count, iAttributes2Count, wWriter);
          rReader1.close();
          rReader2.close();
          wWriter.close();
          return true;
        } catch (IOException var14) {
          System.out.println("I/O error with input or output file, e.g.,: " + sArffFile1);
          var14.printStackTrace();
          return false;
        }
      }
    }
  }

  public static boolean deleteColAndMoveRemainingFirstColToEnd(String sArffFileIn, int iColToDelete, String sArffFileOut) {
    File f = new File(sArffFileIn);
    if (!f.exists()) {
      System.out.println("Could not find Arff file: " + sArffFileIn);
      return false;
    } else {
      String sArffTemp = sArffFileIn + ".temp";
      f = new File(sArffTemp);
      if (f.exists()) {
        f.delete();
      }

      deleteColumnFromArff(sArffFileIn, iColToDelete, sArffTemp);
      moveColumnToEndOfArff(sArffTemp, 1, sArffFileOut);
      f = new File(sArffTemp);
      if (f.exists()) {
        f.delete();
      }

      return true;
    }
  }

  public static boolean moveColumnToEndOfArff(String sArffFileIn, int iColToMove, String sArffFileOut) {
    File f = new File(sArffFileIn);
    if (!f.exists()) {
      System.out.println("Could not find Arff file: " + sArffFileIn);
      return false;
    } else {
      String[] sAttributes = null;
      int[] iAttArr = new int[1];

      try {
        BufferedReader srArff = new BufferedReader(new InputStreamReader(new FileInputStream(sArffFileIn)));
        BufferedWriter swNew = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sArffFileOut)));
        printArffHeader(srArff, swNew, true);
        sAttributes = loadArffAttributes(srArff, iAttArr);
        int iAttributesCount = iAttArr[0];
        boolean[] bDelete = new boolean[iAttributesCount + 1];

        for (int i = 0; i <= iAttributesCount; ++i) {
          bDelete[i] = false;
        }

        bDelete[iColToMove] = true;
        printSelectedAttributes(sAttributes, iAttributesCount, bDelete, swNew, false);
        swNew.write(sAttributes[iColToMove] + "\n");
        printSelectedData(srArff, bDelete, iAttributesCount, swNew, true, false);
        swNew.close();
        srArff.close();
        return true;
      } catch (IOException var11) {
        System.out.println("File i/o error [moveColumnToEndOfArff]" + sArffFileIn + " or " + sArffFileOut);
        var11.printStackTrace();
        return false;
      }
    }
  }

  public static String ngramFileName(String sSentiTextFileIn, int iNgram) {
    return FileOps.s_ChopFileNameExtension(sSentiTextFileIn) + "_" + iNgram + ".arff";
  }

  public static String oneToNgramFileName(String sSentiTextFileIn, int iNgram) {
    return FileOps.s_ChopFileNameExtension(sSentiTextFileIn) + "_1-" + iNgram + ".arff";
  }

  public static String ngramFileNamePosNeg(String sSentiTextFileIn, int iNgram, boolean bPos) {
    return bPos ? FileOps.s_ChopFileNameExtension(sSentiTextFileIn) + "_" + iNgram + "pos.arff" : FileOps.s_ChopFileNameExtension(sSentiTextFileIn) + "_" + iNgram + "neg.arff";
  }

  public static String oneToNgramFileNamePosNeg(String sSentiTextFileIn, int iNgram, boolean bPos) {
    return bPos ? FileOps.s_ChopFileNameExtension(sSentiTextFileIn) + "_1-" + iNgram + "pos.arff" : FileOps.s_ChopFileNameExtension(sSentiTextFileIn) + "_1-" + iNgram + "neg.arff";
  }

  public static String[] convertSentimentTextToArffMultiple(String sSentiTextFileIn, boolean bHeaderLine, TextParsingOptions textParsingOptions, ClassificationOptions classOptions, ClassificationResources resources, int iSentimentType, int iMinFeatureFrequency, String sArffFileForPermittedFeaturesList) {
    File f = new File(sSentiTextFileIn);
    if (!f.exists()) {
      System.out.println("Could not find sentiment file: " + sSentiTextFileIn);
      return null;
    } else {
      StringIndex arffStringIndex = null;
      if (!sArffFileForPermittedFeaturesList.equals("")) {
        arffStringIndex = buildIndexFromArff(sArffFileForPermittedFeaturesList);
      }

      int iNgramMax = textParsingOptions.igNgramSize;
      String sLastCombinedOutFile = "";

      int iNgram;
      String sOutFile;
      for (iNgram = 1; iNgram <= iNgramMax; ++iNgram) {
        textParsingOptions.igNgramSize = iNgram;
        sOutFile = ngramFileName(sSentiTextFileIn, iNgram);
        f = new File(sOutFile);
        if (f.exists()) {
          f.delete();
        }

        convertSentimentTextToArff(sSentiTextFileIn, sOutFile, bHeaderLine, textParsingOptions, classOptions, resources, iSentimentType, iMinFeatureFrequency, arffStringIndex);
        if (iNgram > 1) {
          String sNewCombinedOutFile = oneToNgramFileName(sSentiTextFileIn, iNgram);
          f = new File(sNewCombinedOutFile);
          if (f.exists()) {
            f.delete();
          }

          combineTwoARFFs(sLastCombinedOutFile, sOutFile, false, sNewCombinedOutFile);
          sLastCombinedOutFile = sNewCombinedOutFile;
        } else {
          sLastCombinedOutFile = sOutFile;
        }
      }

      int iOutfileLast = -1;
      String[] sFinalOutFile = new String[100];
      if (iSentimentType == 4) {
        for (iNgram = 1; iNgram <= iNgramMax; ++iNgram) {
          sOutFile = ngramFileName(sSentiTextFileIn, iNgram);
          ++iOutfileLast;
          sFinalOutFile[iOutfileLast] = ngramFileNamePosNeg(sSentiTextFileIn, iNgram, true);
          deleteColAndMoveRemainingFirstColToEnd(sOutFile, 2, sFinalOutFile[iOutfileLast]);
          ++iOutfileLast;
          sFinalOutFile[iOutfileLast] = ngramFileNamePosNeg(sSentiTextFileIn, iNgram, false);
          deleteColAndMoveRemainingFirstColToEnd(sOutFile, 1, sFinalOutFile[iOutfileLast]);
          f = new File(sOutFile);
          f.delete();
          if (iNgram > 1) {
            sOutFile = oneToNgramFileName(sSentiTextFileIn, iNgram);
            ++iOutfileLast;
            sFinalOutFile[iOutfileLast] = oneToNgramFileNamePosNeg(sSentiTextFileIn, iNgram, true);
            deleteColAndMoveRemainingFirstColToEnd(sOutFile, 2, sFinalOutFile[iOutfileLast]);
            ++iOutfileLast;
            sFinalOutFile[iOutfileLast] = oneToNgramFileNamePosNeg(sSentiTextFileIn, iNgram, false);
            deleteColAndMoveRemainingFirstColToEnd(sOutFile, 1, sFinalOutFile[iOutfileLast]);
            f = new File(sOutFile);
            f.delete();
          }
        }
      } else if (iSentimentType == 1 || iSentimentType == 2 || iSentimentType == 3) {
        for (iNgram = 1; iNgram <= iNgramMax; ++iNgram) {
          ++iOutfileLast;
          sFinalOutFile[iOutfileLast] = ngramFileName(sSentiTextFileIn, iNgram);
          File g = new File(sFinalOutFile[iOutfileLast] + ".temp");
          f = new File(sFinalOutFile[iOutfileLast]);
          f.renameTo(g);
          moveColumnToEndOfArff(sFinalOutFile[iOutfileLast] + ".temp", 1, sFinalOutFile[iOutfileLast]);
          g.delete();
          if (iNgram > 1) {
            ++iOutfileLast;
            sFinalOutFile[iOutfileLast] = oneToNgramFileName(sSentiTextFileIn, iNgram);
            g = new File(sFinalOutFile[iOutfileLast] + ".temp");
            f = new File(sFinalOutFile[iOutfileLast]);
            f.renameTo(g);
            moveColumnToEndOfArff(sFinalOutFile[iOutfileLast] + ".temp", 1, sFinalOutFile[iOutfileLast]);
            g.delete();
          }
        }
      }

      return sFinalOutFile;
    }
  }

  public static boolean deleteColumnFromArff(String sArffFile, int iColToRemove, String sNewArffFile) {
    File f = new File(sArffFile);
    if (!f.exists()) {
      System.out.println("Could not find Arff file: " + sArffFile);
      return false;
    } else {
      String[] sAttributes = null;
      int iAttributesCount = 0;
      int[] iAttArr = new int[1];

      try {
        BufferedReader rArff = new BufferedReader(new InputStreamReader(new FileInputStream(sArffFile)));
        BufferedWriter wNew = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sNewArffFile)));
        printArffHeader(rArff, wNew, true);
        sAttributes = loadArffAttributes(rArff, iAttArr);
        iAttributesCount = iAttArr[0];
        boolean[] bDelete = new boolean[iAttributesCount + 1];

        for (int i = 0; i <= iAttributesCount; ++i) {
          bDelete[i] = false;
        }

        bDelete[iColToRemove] = true;
        printSelectedAttributes(sAttributes, iAttributesCount, bDelete, wNew, false);
        printSelectedData(rArff, bDelete, iAttributesCount, wNew, false, true);
        wNew.close();
        rArff.close();
        return true;
      } catch (IOException var11) {
        System.out.println("I/O error with input or output file, e.g.,: " + sArffFile);
        var11.printStackTrace();
        return false;
      }
    }
  }

  private static boolean printSelectedAttributes(String[] sAttributes, int iAttributesCount, boolean[] bDelete, BufferedWriter swNew, boolean bVerbose) {
    int iDelCount = 0;
    String sDelList = "";

    try {
      if (sAttributes[0] != null) {
        swNew.write(sAttributes[0] + "\n");
      }

      for (int i = 1; i <= iAttributesCount; ++i) {
        if (!bDelete[i]) {
          swNew.write(sAttributes[i] + "\n");
        } else {
          ++iDelCount;
          if (bVerbose) {
            sDelList = sDelList + sAttributes[i];
          }
        }
      }

      if (bVerbose) {
        System.out.println(iDelCount + " deleted out of " + iAttributesCount + "\n" + sDelList);
      }

      return true;
    } catch (IOException var9) {
      System.out.println("Error writing [printSelectedAttributes]");
      var9.printStackTrace();
      return false;
    }
  }

  private static boolean printSelectedData(BufferedReader srArff, boolean[] bDeleteCol, int iAttributeCount, BufferedWriter swOutput, boolean bPrintDeletedColsAtEnd, boolean bVerbose) {
    int[] iAttID = new int[iAttributeCount + 1];
    int[] iData = new int[iAttributeCount + 1];
    int iPairs = 1;
    int iCount = 0;
    int iLastPrintedAttribute = 0;
    int[] iNewAttributeID = new int[iAttributeCount + 1];
    int iAttUsed = 0;

    int iCol;
    for (iCol = 1; iCol <= iAttributeCount; ++iCol) {
      if (!bDeleteCol[iCol]) {
        iLastPrintedAttribute = iCol;
        iNewAttributeID[iCol - 1] = iAttUsed++;
      }
    }

    for (iCol = 1; iCol <= iAttributeCount; ++iCol) {
      if (bDeleteCol[iCol]) {
        iNewAttributeID[iCol - 1] = iAttUsed++;
      }
    }

    try {
      swOutput.write("@data\n");

      label105:
      while (true) {
        while (true) {
          String sLine;
          do {
            if (!srArff.ready()) {
              break label105;
            }

            sLine = srArff.readLine();
            ++iCount;
          } while (sLine.length() <= 0);

          String[] sData;
          if (bgSaveArffAsCondensed) {
            iPairs = -1;
            sData = sLine.substring(1, sLine.length() - 1).split(",");

            for (int iPair = 0; iPair < sData.length; ++iPair) {
              if (sData[iPair].length() > 2) {
                String[] sIDVal = sData[iPair].trim().split(" ");
                int iSourceID = Integer.parseInt(sIDVal[0]);
                if (bPrintDeletedColsAtEnd || !bDeleteCol[iSourceID + 1]) {
                  ++iPairs;
                  iAttID[iPairs] = iNewAttributeID[iSourceID];

                  try {
                    iData[iPairs] = Integer.parseInt(sIDVal[1]);
                  } catch (Exception var21) {
                    iData[iPairs] = 0;
                  }
                }
              }
            }

            printCondensedData(swOutput, iAttID, iData, iPairs);
          } else {
            String sDeletedCols = "";
            sData = sLine.split(",");

            for (iCol = 1; iCol < iLastPrintedAttribute; ++iCol) {
              if (!bDeleteCol[iCol]) {
                swOutput.write(sData[iCol - 1] + ",");
              } else {
                sDeletedCols = sDeletedCols + sData[iCol - 1] + ",";
              }
            }

            if (!bPrintDeletedColsAtEnd) {
              swOutput.write(sData[iLastPrintedAttribute - 1] + "\n");
            } else {
              for (iCol = iLastPrintedAttribute; iCol <= iAttributeCount; ++iCol) {
                if (bDeleteCol[iCol]) {
                  sDeletedCols = sDeletedCols + sData[iCol - 1] + ",";
                }
              }

              if (sDeletedCols.length() > 0) {
                swOutput.write(sData[iLastPrintedAttribute - 1] + "," + sDeletedCols.substring(0, sDeletedCols.length() - 1) + "\n");
              } else {
                swOutput.write(sData[iLastPrintedAttribute - 1] + "\n");
              }
            }
          }
        }
      }
    } catch (IOException var22) {
      System.out.println("I/O error with input or output file [printSelectedData]");
      var22.printStackTrace();
      return false;
    }

    if (bVerbose) {
      System.out.println(iCount + " lines of data saved");
    }

    return true;
  }

  private static void printCondensedData(BufferedWriter swArff, int[] iAtt, int[] iData, int iLastPair) {
    Sort.quickSortIntWithInt(iAtt, iData, 0, iLastPair);

    try {
      swArff.write("{");
      if (iLastPair > -1) {
        swArff.write(iAtt[0] + " " + iData[0]);
      }

      for (int iPair = 1; iPair <= iLastPair; ++iPair) {
        swArff.write("," + iAtt[iPair] + " " + iData[iPair]);
      }

      swArff.write("}\n");
    } catch (IOException var5) {
      var5.printStackTrace();
    }

  }

  private static void printDataWithoutDuplicates(BufferedReader rArff1, BufferedReader rArff2, boolean[] bDuplicate2, int iAttributes1Count, int iAttributes2Count, BufferedWriter wMerged) {
    int iAttUsed = 0;
    int[] iAttribute2ID = new int[iAttributes2Count + 1];

    int iCol;
    for (iCol = 1; iCol <= iAttributes2Count; ++iCol) {
      if (!bDuplicate2[iCol]) {
        iAttribute2ID[iCol] = iAttUsed++ + iAttributes1Count;
      }
    }

    try {
      wMerged.write("@data\n");

      while (true) {
        while (true) {
          String sLine1;
          String sLine2;
          do {
            if (!rArff1.ready() || !rArff2.ready()) {
              return;
            }

            sLine1 = rArff1.readLine();
            sLine2 = rArff2.readLine();
          } while (sLine2.equals(""));

          String[] sData2;
          if (bgSaveArffAsCondensed) {
            wMerged.write(sLine1.substring(0, sLine1.length() - 1));
            iAttUsed = 0;
            sData2 = sLine2.substring(1, sLine2.length() - 1).split(",");

            for (int iPair = 0; iPair < sData2.length; ++iPair) {
              try {
                String[] sIDValue = sData2[iPair].trim().split(" ");
                iCol = Integer.parseInt(sIDValue[0]) + 1;
                if (!bDuplicate2[iCol]) {
                  wMerged.write(", " + iAttribute2ID[iCol] + " " + sIDValue[1]);
                }
              } catch (Exception var15) {
                System.out.println("Error processing ID value pair " + sData2[iPair] + " [printDataWithoutDuplicates]");
                var15.printStackTrace();
              }
            }

            wMerged.write("}\n");
          } else {
            wMerged.write(sLine1);
            sData2 = sLine2.split(",");

            for (iCol = 1; iCol <= iAttributes2Count; ++iCol) {
              if (!bDuplicate2[iCol]) {
                wMerged.write("," + sData2[iCol - 1]);
              }
            }

            wMerged.write("\n");
          }
        }
      }
    } catch (IOException var16) {
      System.out.println("Error writing to file [printDataWithoutDuplicates]");
      var16.printStackTrace();
    }
  }

  private static boolean[] printNonDuplicateAttributes(String[] sAttributes1, int iAttributes1Count, String[] sAttributes2, int iAttributes2Count, boolean bVerbose, BufferedWriter wMerged) {
    int iDupCount = 0;
    String sDuplicateList = "";
    boolean[] bDuplicate2 = new boolean[iAttributes2Count + 1];

    try {
      int i;
      for (i = 1; i <= iAttributes1Count; ++i) {
        wMerged.write(sAttributes1[i] + "\n");
      }

      for (int j = 1; j <= iAttributes2Count; ++j) {
        for (i = 1; i <= iAttributes1Count; ++i) {
          if (sAttributes2[j].equals(sAttributes1[i])) {
            if (bVerbose) {
              sDuplicateList = sDuplicateList + sAttributes1[i] + " | ";
            }

            bDuplicate2[j] = true;
            ++iDupCount;
            break;
          }
        }

        if (!bDuplicate2[j]) {
          wMerged.write(sAttributes2[j] + "\n");
        }
      }
    } catch (IOException var12) {
      System.out.println("Error writing to file file [printNonDuplicateAttributes]");
      var12.printStackTrace();
    }

    if (bVerbose) {
      System.out.println(iDupCount + " duplicates found out of " + iAttributes1Count + "\n" + sDuplicateList);
    }

    return bDuplicate2;
  }

  private static int printArffHeader(BufferedReader rArffIn, BufferedWriter wArffOut, boolean bPrintRelation) {
    int iLineCount = 0;
    String sLine = "";

    try {
      if (rArffIn.ready()) {
        sLine = rArffIn.readLine();
      }

      while (rArffIn.ready() && sLine.indexOf("@relation ") != 0) {
        wArffOut.write(sLine + "\n");
        ++iLineCount;
        sLine = rArffIn.readLine();
      }

      if (bPrintRelation) {
        wArffOut.write(sLine + "\n");
      }
    } catch (IOException var6) {
      var6.printStackTrace();
    }

    return iLineCount;
  }

  private static String[] loadArffAttributes(BufferedReader rArffIn, int[] iAttributeCountArr) {
    String sLine = "";
    int iMaxAttributes = 10000;
    int iAttributesCount = 0;
    String[] sAttributes = new String[iMaxAttributes];

    try {
      if (rArffIn.ready()) {
        sLine = rArffIn.readLine();
      }

      for (; rArffIn.ready() && sLine.indexOf("@data") != 0; sLine = rArffIn.readLine()) {
        if (!sLine.equals("") && !sLine.substring(0, 1).equals("%")) {
          if (sLine.indexOf("@relation ") == 0) {
            sAttributes[0] = sLine;
          } else {
            ++iAttributesCount;
            if (iAttributesCount == iMaxAttributes - 1) {
              sAttributes = increaseArraySize(sAttributes, iMaxAttributes, 2 * iMaxAttributes);
              iMaxAttributes *= 2;
            }

            sAttributes[iAttributesCount] = sLine;
          }
        }
      }
    } catch (IOException var7) {
      var7.printStackTrace();
    }

    iAttributeCountArr[0] = iAttributesCount;
    return sAttributes;
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

  private static void selectTopNAttributes(double[] fColIG, int iAttributeCount, int iFeaturesToSelect, boolean[] bUseCol) {
    int[] iIndex = new int[iAttributeCount + 1];

    int i;
    for (i = 1; i <= iAttributeCount; ++i) {
      iIndex[i] = i;
      bUseCol[i] = false;
    }

    Sort.quickSortNumbersDescendingViaIndex(fColIG, iIndex, 1, iAttributeCount);
    bUseCol[iAttributeCount] = true;
    bUseCol[0] = true;
    if (iFeaturesToSelect > 0) {
      bUseCol[iIndex[1]] = true;
      --iFeaturesToSelect;

      for (i = 2; i <= iAttributeCount && iFeaturesToSelect >= 1; ++i) {
        bUseCol[iIndex[i]] = true;
        --iFeaturesToSelect;
      }
    }

  }

  private static void printInformationGainValues(double[] fColIG, String[] sAttributes, int iAttributeCount, String sIGListOut) {
    DecimalFormat df = new DecimalFormat("#.######");
    int[] iIndex = new int[iAttributeCount + 1];

    int iCol;
    for (iCol = 1; iCol <= iAttributeCount; iIndex[iCol] = iCol++) {
    }

    Sort.quickSortNumbersDescendingViaIndex(fColIG, iIndex, 1, iAttributeCount);

    try {
      BufferedWriter wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sIGListOut)));

      for (iCol = 1; iCol <= iAttributeCount; ++iCol) {
        wWriter.write(sAttributes[iIndex[iCol]] + " " + df.format(fColIG[iIndex[iCol]]) + "\r\n");
      }

      wWriter.close();
    } catch (IOException var8) {
      var8.printStackTrace();
    }

  }

  private static void calculateInformationGainOfData(int[][] iData, int iAttributeCount, int iDataCount, double[] fColIG) {
    int iClassAttribute = iAttributeCount;
    int[] iAttributeValue = new int[1001];
    int iFirstClass = findFirstClassInData(iData, iDataCount, iAttributeCount);
    int iLastClass = findLastClassInData(iData, iDataCount, iAttributeCount);
    int[] iClass = new int[iLastClass + 1];
    double fOverallEntropy = calculateClassesAndEntropyOfData(iData, iAttributeCount, iDataCount, iClass, iFirstClass, iLastClass);
    int[] iAttributeValueClassCount = new int[iLastClass + 1];

    for (int iCol = 1; iCol < iAttributeCount; ++iCol) {
      int iAttributeValueCount = 0;

      int iRow;
      int i;
      for (iRow = 1; iRow <= iDataCount; ++iRow) {
        boolean bFound = false;

        for (i = 1; i <= iAttributeValueCount; ++i) {
          if (iAttributeValue[i] == iData[iCol][iRow]) {
            bFound = true;
            break;
          }
        }

        if (!bFound) {
          ++iAttributeValueCount;
          iAttributeValue[iAttributeValueCount] = iData[iCol][iRow];
        }
      }

      double fAttributeEntropySum = 0.0D;

      for (i = 1; i <= iAttributeValueCount; ++i) {
        int iAttributeValueFreq = 0;

        int j;
        for (j = iFirstClass; j <= iLastClass; ++j) {
          iAttributeValueClassCount[j] = 0;
        }

        for (iRow = 1; iRow <= iDataCount; ++iRow) {
          if (iAttributeValue[i] == iData[iCol][iRow]) {
            ++iAttributeValueClassCount[iData[iClassAttribute][iRow]];
            ++iAttributeValueFreq;
          }
        }

        double fAttributeEntropy = 0.0D;

        for (j = iFirstClass; j <= iLastClass; ++j) {
          double p = (double) iAttributeValueClassCount[j] / (double) iAttributeValueFreq;
          if (p > 0.0D) {
            fAttributeEntropy -= p * Math.log(p) / Math.log(2.0D);
          }
        }

        fAttributeEntropySum += fAttributeEntropy * (double) iAttributeValueFreq / (double) iDataCount;
      }

      fColIG[iCol] = fOverallEntropy - fAttributeEntropySum;
    }

  }

  private static double calculateClassesAndEntropyOfData(int[][] iData, int iAttributeCount, int iDataCount, int[] iClass, int iFirstClass, int iLastClass) {
    double fOverallEntropy = 0.0D;

    int i;
    for (i = 1; i <= iDataCount; ++i) {
      ++iClass[iData[iAttributeCount][i]];
    }

    for (i = iFirstClass; i <= iLastClass; ++i) {
      double p = (double) iClass[i] / (double) iDataCount;
      if (p > 0.0D) {
        fOverallEntropy -= p * Math.log(p) / Math.log(2.0D);
      }
    }

    return fOverallEntropy;
  }

  private static int findFirstClassInData(int[][] iData, int iDataCount, int iAttributeCount) {
    int iFirstClass = 999999;

    for (int i = 1; i <= iDataCount; ++i) {
      if (iData[iAttributeCount][i] < iFirstClass) {
        iFirstClass = iData[iAttributeCount][i];
      }
    }

    return iFirstClass;
  }

  private static int findLastClassInData(int[][] iData, int iDataCount, int iAttributeCount) {
    int iLastClass = 0;

    for (int i = 1; i <= iDataCount; ++i) {
      if (iData[iAttributeCount][i] > iLastClass) {
        iLastClass = iData[iAttributeCount][i];
      }
    }

    return iLastClass;
  }

  public static void convertArffToTextMultiple(String[] sArffIn, int iArffInCount, String[] sTextOut) {
    for (int i = 0; i < iArffInCount; ++i) {
      sTextOut[i] = FileOps.s_ChopFileNameExtension(sArffIn[i]) + " out.txt";
      convertArffToText(sArffIn[i], sTextOut[i]);
    }

  }

  public static void convertArffToText(String sArffIn, String sTextOut) {
    int[] iAttData = countAttributesAndDataInArff(sArffIn);
    int iAttributeCount = iAttData[0];
    int iDataCount = iAttData[1];
    int[][] iData = new int[iAttributeCount + 1][iDataCount + 1];
    String[] sAttributes = new String[iAttributeCount + 1];
    readArffAttributesAndData(sArffIn, iAttributeCount, iDataCount, sAttributes, iData);
    writeArffAttributesAndDataToText(sAttributes, iData, iAttributeCount, iDataCount, sTextOut);
  }

  public static void makeArffsWithTopNAttributes(String[] sArffIn, int iArffInCount, int iTopNAttributes, String[] sArffOut) {
    for (int i = 0; i < iArffInCount; ++i) {
      sArffOut[i] = FileOps.s_ChopFileNameExtension(sArffIn[i]) + " " + iTopNAttributes + ".arff";
      makeArffWithTopNAttributes(sArffIn[i], iTopNAttributes, sArffOut[i]);
    }

  }

  public static void makeArffWithTopNAttributes(String sArffIn, int iTopNAttributes, String sArffOut) {
    int[] iAttData = countAttributesAndDataInArff(sArffIn);
    int iAttributeCount = iAttData[0];
    int iDataCount = iAttData[1];

    try {
      System.out.println("AttributeSelection: Attributes " + iAttributeCount + " data " + iDataCount + " attribute x data " + Long.toString((long) (iAttributeCount + 1) * (long) (iAttributeCount + 1)));
      int[][] iData = new int[iAttributeCount + 1][iDataCount + 1];
      double[] fColIG = new double[iAttributeCount + 1];
      boolean[] bUseCol = new boolean[iAttributeCount + 1];
      String[] sAttributes = new String[iAttributeCount + 1];
      String sHeader = readArffAttributesAndData(sArffIn, iAttributeCount, iDataCount, sAttributes, iData);
      calculateInformationGainOfData(iData, iAttributeCount, iDataCount, fColIG);
      selectTopNAttributes(fColIG, iAttributeCount, iTopNAttributes, bUseCol);
      printInformationGainValues(fColIG, sAttributes, iAttributeCount, sArffOut + "_IG.txt");
      writeArffAttributesAndData(sHeader, sAttributes, iData, iAttributeCount, iDataCount, bUseCol, sArffOut);
    } catch (Exception var11) {
      System.out.println("makeArffWithTopNAttributes error - probably insufficient to create attribute x data array");
      System.out.println("attribute " + iAttributeCount + " data " + iDataCount + " attribute x data " + Integer.toString((iAttributeCount + 1) * (iAttributeCount + 1)));
      var11.printStackTrace();
      System.exit(0);
    }

  }

  private static int[] countAttributesAndDataInArff(String sArffIn) {
    int iAttCount = 0;
    int iDataCount = 0;

    try {
      BufferedReader rArff = new BufferedReader(new InputStreamReader(new FileInputStream(sArffIn)));
      String sLine = "";
      if (rArff.ready()) {
        sLine = rArff.readLine();
      }

      for (; rArff.ready() && sLine.indexOf("@data") != 0; sLine = rArff.readLine()) {
        if (sLine.length() > 0 && sLine.indexOf("@attribute ") == 0) {
          ++iAttCount;
        }
      }

      iDataCount = 0;

      while (rArff.ready()) {
        sLine = rArff.readLine();
        if (sLine.length() > 0) {
          ++iDataCount;
        }
      }

      rArff.close();
    } catch (Exception var5) {
      System.out.println("[countAttributesAndDataInArff]Error reading file " + sArffIn);
      var5.printStackTrace();
    }

    int[] iAttData = new int[]{iAttCount, iDataCount};
    return iAttData;
  }

  private static String readArffAttributesAndData(String sArffIn, int iAttributeCount, int iDataCount, String[] sAttributes, int[][] iData) {
    String sHeader = "";
    String sLine = "";
    int iAtt = 0;
    boolean var10 = false;

    try {
      BufferedReader rArff = new BufferedReader(new InputStreamReader(new FileInputStream(sArffIn)));
      if (rArff.ready()) {
        sLine = rArff.readLine();
      }

      for (; rArff.ready() && sLine.indexOf("@data") != 0; sLine = rArff.readLine()) {
        if (sLine.length() > 0 && sLine.charAt(0) != "%".charAt(0)) {
          if (sLine.indexOf("@relation ") == 0) {
            sAttributes[0] = sLine;
          } else {
            ++iAtt;
            sAttributes[iAtt] = sLine;
          }
        } else {
          sHeader = sHeader + sLine + "\n";
        }
      }

      iDataCount = 0;

      while (true) {
        String[] sData;
        do {
          while (true) {
            do {
              if (!rArff.ready()) {
                rArff.close();
                return sHeader;
              }

              sLine = rArff.readLine();
            } while (sLine.length() <= 1);

            ++iDataCount;
            if (sLine.indexOf("{") >= 0) {
              for (iAtt = 1; iAtt <= iAttributeCount; ++iAtt) {
                iData[iAtt][iDataCount] = 0;
              }
              break;
            }

            sData = sLine.split(",");

            for (iAtt = 1; iAtt <= iAttributeCount; ++iAtt) {
              iData[iAtt][iDataCount] = Integer.parseInt(sData[iAtt - 1].trim());
            }
          }
        } while (sLine.length() <= 4);

        sData = sLine.substring(1, sLine.length() - 1).split(",");

        for (int iPair = 0; iPair < sData.length; ++iPair) {
          String[] sIDValue = sData[iPair].trim().split(" ");
          iData[Integer.parseInt(sIDValue[0]) + 1][iDataCount] = Integer.parseInt(sIDValue[1]);
        }
      }
    } catch (Exception var12) {
      System.out.println("[readArffAttributesAndData]Error reading file " + sArffIn);
      var12.printStackTrace();
      return sHeader;
    }
  }

  private static void writeArffAttributesAndData(String sHeader, String[] sAttribute, int[][] iData, int iAttributeCount, int iDataCount, boolean[] bUseCol, String sArffOut) {
    try {
      BufferedWriter wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sArffOut)));
      wWriter.write(sHeader);

      int iCol;
      for (iCol = 0; iCol <= iAttributeCount; ++iCol) {
        if (bUseCol[iCol]) {
          wWriter.write(sAttribute[iCol] + "\n");
        }
      }

      wWriter.write("@data\n");

      for (int iDat = 1; iDat <= iDataCount; ++iDat) {
        if (bgSaveArffAsCondensed) {
          wWriter.write("{");
          int iColUsed = 0;

          for (iCol = 1; iCol < iAttributeCount; ++iCol) {
            if (bUseCol[iCol]) {
              ++iColUsed;
              if (iData[iCol][iDat] > 0) {
                wWriter.write(iColUsed - 1 + " " + iData[iCol][iDat] + ",");
              }
            }
          }

          wWriter.write(iColUsed + " " + iData[iAttributeCount][iDat] + "}\n");
        } else {
          for (iCol = 1; iCol < iAttributeCount; ++iCol) {
            if (bUseCol[iCol]) {
              wWriter.write(iData[iCol][iDat] + ",");
            }
          }

          wWriter.write(iData[iAttributeCount][iDat] + "\n");
        }
      }

      wWriter.close();
    } catch (Exception var11) {
      System.out.println("[writeArffAttributesAndData]Error writing file " + sArffOut);
      var11.printStackTrace();
    }

  }

  private static void writeArffAttributesAndDataToText(String[] sAttribute, int[][] iData, int iAttributeCount, int iDataCount, String sTextOut) {
    int iCol;
    for (iCol = 1; iCol <= iAttributeCount; ++iCol) {
      String[] sData = sAttribute[iCol].split(" ");
      sAttribute[iCol] = sData[1];
      int iPos = sAttribute[iCol].indexOf("_");
      if (iPos > 0) {
        sAttribute[iCol] = sAttribute[iCol].substring(iPos + 1);
      }

      iPos = sAttribute[iCol].indexOf("_pc");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("_pc", "%");
      }

      iPos = sAttribute[iCol].indexOf("%2C");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%2C", ",");
      }

      iPos = sAttribute[iCol].indexOf("%28");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%28", "(");
      }

      iPos = sAttribute[iCol].indexOf("%29");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%29", ")");
      }

      iPos = sAttribute[iCol].indexOf("%3F");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%3F", "?");
      }

      iPos = sAttribute[iCol].indexOf("%21");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%21", "!");
      }

      iPos = sAttribute[iCol].indexOf("%25");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%25", "%");
      }

      iPos = sAttribute[iCol].indexOf("%26");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%26", "&");
      }

      iPos = sAttribute[iCol].indexOf("%27");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%27", "'");
      }

      iPos = sAttribute[iCol].indexOf("%2F");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%2F", "/");
      }

      iPos = sAttribute[iCol].indexOf("%3A");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%3A", ":");
      }

      iPos = sAttribute[iCol].indexOf("%3B");
      if (iPos >= 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("%3B", ";");
      }

      iPos = sAttribute[iCol].indexOf("+");
      if (iPos > 0) {
        sAttribute[iCol] = sAttribute[iCol].replace("+", "_");
      }
    }

    try {
      BufferedWriter wWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sTextOut)));
      wWriter.write(sAttribute[iAttributeCount] + "\tText\n");

      for (int iDat = 1; iDat <= iDataCount; ++iDat) {
        wWriter.write(iData[iAttributeCount][iDat] + "\t");

        for (iCol = 1; iCol < iAttributeCount; ++iCol) {
          if (iData[iCol][iDat] > 1) {
            wWriter.write(sAttribute[iCol] + "[" + iData[iCol][iDat] + "] ");
          } else if (iData[iCol][iDat] == 1) {
            wWriter.write(sAttribute[iCol] + " ");
          }
        }

        wWriter.write("\r\n");
      }

      wWriter.close();
    } catch (Exception var10) {
      System.out.println("[writeArffAttributesAndDataToText]Error writing file " + sTextOut);
      var10.printStackTrace();
    }

  }
}
