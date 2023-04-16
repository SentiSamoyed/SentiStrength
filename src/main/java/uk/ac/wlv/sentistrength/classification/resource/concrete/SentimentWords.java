// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   SentimentWords.java

package uk.ac.wlv.sentistrength.classification.resource.concrete;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.resource.Resource;
import uk.ac.wlv.sentistrength.core.Corpus;
import uk.ac.wlv.utilities.Sort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            Corpus, ClassificationOptions

/**
 * 情感词列表类，用于存储情感词及其情感强度等信息，并提供初始化、存取、更新等操作。
 */
@Log4j2
public class SentimentWords extends Resource {
  /**
   * 存储不带星号前缀的情感词的数组。
   */
  private String[] sgSentimentWords;
  /**
   * 存储不带星号前缀的情感词的情感强度值的数组。
   */
  private int[] igSentimentWordsStrengthTake1;
  /**
   * 不带星号前缀的情感词的数量。
   */
  private int igSentimentWordsCount;
  /**
   * 存储带星号前缀的情感词的数组。
   */
  private String[] sgSentimentWordsWithStarAtStart;
  /**
   * 存储带星号前缀的情感词的情感强度值的数组。
   */
  private int[] igSentimentWordsWithStarAtStartStrengthTake1;
  /**
   * 带星号前缀的情感词的数量。
   */
  private int igSentimentWordsWithStarAtStartCount;
  /**
   * 存储带星号前缀的情感词是否以星号结尾的布尔数组。
   */
  private boolean[] bgSentimentWordsWithStarAtStartHasStarAtEnd;

  /**
   * 构造函数，初始化不带星号前缀的情感词数量和带星号前缀的情感词数量。
   */
  public SentimentWords() {
    igSentimentWordsCount = 0;
    igSentimentWordsWithStarAtStartCount = 0;
  }

  /**
   * 获取指定编号的情感词。
   *
   * @param iWordID 情感词的编号
   * @return 指定编号的情感词，如果编号无效则返回空字符串
   */
  public String getSentimentWord(int iWordID) {
    if (iWordID > 0) {
      if (iWordID <= igSentimentWordsCount) {
        return sgSentimentWords[iWordID];
      }
      if (iWordID <= igSentimentWordsCount + igSentimentWordsWithStarAtStartCount) {
        return sgSentimentWordsWithStarAtStart[iWordID - igSentimentWordsCount];
      }
    }
    return "";
  }

  /**
   * 获取指定情感词的情感强度值。
   *
   * @param sWord 情感词
   * @return 指定情感词的情感强度值，如果情感词未找到则返回 999
   */
  public int getSentiment(String sWord) {
    int iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(),
        sgSentimentWords, 1, igSentimentWordsCount);
    if (iWordID >= 0) {
      return igSentimentWordsStrengthTake1[iWordID];
    }
    int iStarWordID = getMatchingStarAtStartRawWordID(sWord);
    if (iStarWordID >= 0) {
      return igSentimentWordsWithStarAtStartStrengthTake1[iStarWordID];
    } else {
      return 999;
    }
  }

  /**
   * 获取情感值。
   *
   * @param iWordID 词的 ID
   * @return 词的情感值，如果 ID 大于 0，则返回 igSentimentWordsStrengthTake1[iWordID]；否则返回 999
   */
  public int getSentiment(int iWordID) {
    if (iWordID > 0) {
      if (iWordID <= igSentimentWordsCount) {
        return igSentimentWordsStrengthTake1[iWordID];
      } else {
        return igSentimentWordsWithStarAtStartStrengthTake1[iWordID - igSentimentWordsCount];
      }
    } else {
      return 999;
    }
  }

  /**
   * 设置指定情感词的情感强度值。
   *
   * @param sWord         情感词
   * @param iNewSentiment 新的情感词的情感强度值
   * @return 更新成功则返回 true,否则返回 false
   */
  public boolean setSentiment(String sWord, int iNewSentiment) {
    int iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(),
        sgSentimentWords, 1, igSentimentWordsCount);
    if (iWordID >= 0) {
      if (iNewSentiment > 0) {
        setSentiment(iWordID, iNewSentiment - 1);
      } else {
        setSentiment(iWordID, iNewSentiment + 1);
      }
      return true;
    }
    if (sWord.indexOf("*") == 0) {
      sWord = sWord.substring(1);
      if (sWord.indexOf("*") > 0) {
        sWord.substring(0, sWord.length() - 1);
      }
    }
    if (igSentimentWordsWithStarAtStartCount > 0) {
      for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
        if (sWord == sgSentimentWordsWithStarAtStart[i]) {
          if (iNewSentiment > 0) {
            setSentiment(igSentimentWordsCount + i, iNewSentiment - 1);
          } else {
            setSentiment(igSentimentWordsCount + i, iNewSentiment + 1);
          }
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 设置情感值。
   *
   * @param iWordID       词的 ID
   * @param iNewSentiment 新的情感值
   */
  public void setSentiment(int iWordID, int iNewSentiment) {
    if (iWordID <= igSentimentWordsCount) {
      igSentimentWordsStrengthTake1[iWordID] = iNewSentiment;
    } else {
      igSentimentWordsWithStarAtStartStrengthTake1[iWordID - igSentimentWordsCount] = iNewSentiment;
    }
  }

  /**
   * 将情感词汇列表保存到指定文件中。
   *
   * @param sFilename 保存情感词汇列表的文件路径
   * @param c         语料库对象，用于判断是否强制使用 UTF-8 编码保存文件
   * @return 如果成功保存情感词汇列表，则返回 true；否则返回 false
   */
  public boolean saveSentimentList(String sFilename, Corpus c) {
    try {
      BufferedWriter wWriter = new BufferedWriter(new FileWriter(sFilename));
      for (int i = 1; i <= igSentimentWordsCount; i++) {
        int iSentimentStrength = igSentimentWordsStrengthTake1[i];
        if (iSentimentStrength < 0) {
          iSentimentStrength--;
        } else {
          iSentimentStrength++;
        }
        String sOutput = (new StringBuilder(String.valueOf(sgSentimentWords[i]))).append("\t")
            .append(iSentimentStrength).append("\n").toString();
        if (c.options.bgForceUTF8) {
          try {
            sOutput = new String(sOutput.getBytes("UTF-8"), "UTF-8");
          } catch (UnsupportedEncodingException e) {
            System.out.println("UTF-8 not found on your system!");
            e.printStackTrace();
          }
        }
        wWriter.write(sOutput);
      }

      for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
        int iSentimentStrength = igSentimentWordsWithStarAtStartStrengthTake1[i];
        if (iSentimentStrength < 0) {
          iSentimentStrength--;
        } else {
          iSentimentStrength++;
        }
        String sOutput = (new StringBuilder("*")).append(sgSentimentWordsWithStarAtStart[i])
            .toString();
        if (bgSentimentWordsWithStarAtStartHasStarAtEnd[i]) {
          sOutput = (new StringBuilder(String.valueOf(sOutput))).append("*").toString();
        }
        sOutput = (new StringBuilder(String.valueOf(sOutput))).append("\t")
            .append(iSentimentStrength).append("\n").toString();
        if (c.options.bgForceUTF8) {
          try {
            sOutput = new String(sOutput.getBytes("UTF-8"), "UTF-8");
          } catch (UnsupportedEncodingException e) {
            System.out.println("UTF-8 not found on your system!");
            e.printStackTrace();
          }
        }
        wWriter.write(sOutput);
      }

      wWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 打印情感词的情感值到单行中。
   *
   * @param wWriter 要写入的缓冲区写入器
   * @return 如果成功写入，返回 true；否则返回 false
   */
  public boolean printSentimentValuesInSingleRow(BufferedWriter wWriter) {
    try {
      for (int i = 1; i <= igSentimentWordsCount; i++) {
        int iSentimentStrength = igSentimentWordsStrengthTake1[i];
        wWriter.write((new StringBuilder("\t")).append(iSentimentStrength).toString());
      }

      for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
        int iSentimentStrength = igSentimentWordsWithStarAtStartStrengthTake1[i];
        wWriter.write((new StringBuilder("\t")).append(iSentimentStrength).toString());
      }

      wWriter.write("\n");
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 打印情感词到单行的头部。
   *
   * @param wWriter 要写入的缓冲区写入器
   * @return 如果成功写入，返回 true；否则返回 false
   */
  public boolean printSentimentTermsInSingleHeaderRow(BufferedWriter wWriter) {
    try {
      for (int i = 1; i <= igSentimentWordsCount; i++) {
        wWriter.write((new StringBuilder("\t")).append(sgSentimentWords[i]).toString());
      }
      for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
        wWriter.write((new StringBuilder("\t*")).append(sgSentimentWordsWithStarAtStart[i])
            .toString());
        if (bgSentimentWordsWithStarAtStartHasStarAtEnd[i]) {
          wWriter.write("*");
        }
      }

      wWriter.write("\n");
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * 返回给定情感词的索引。
   *
   * @param sWord 要查找的情感词
   * @return 给定情感词的索引，如果词不存在，则返回 -1
   */
  public int getSentimentID(String sWord) {
    int iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(),
        sgSentimentWords, 1, igSentimentWordsCount);
    if (iWordID >= 0) {
      return iWordID;
    }
    iWordID = getMatchingStarAtStartRawWordID(sWord);
    if (iWordID >= 0) {
      return iWordID + igSentimentWordsCount;
    } else {
      return -1;
    }
  }

  /**
   * 返回以星号开头的情感词的索引。
   *
   * @param sWord 要查找的情感词
   * @return 以星号开头的情感词的索引，如果不是以星号开头的词，返回 -1
   */
  private int getMatchingStarAtStartRawWordID(String sWord) {
    int iSubStringPos = 0;
    if (igSentimentWordsWithStarAtStartCount > 0) {
      for (int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++) {
        iSubStringPos = sWord.indexOf(sgSentimentWordsWithStarAtStart[i]);
        if (iSubStringPos >= 0 && (bgSentimentWordsWithStarAtStartHasStarAtEnd[i]
            || iSubStringPos + sgSentimentWordsWithStarAtStart[i].length() == sWord.length())) {
          return i;
        }
      }
    }
    return -1;
  }

  /**
   * 返回情感词总数。
   *
   * @return 情感词总数
   */
  public int getSentimentWordCount() {
    return igSentimentWordsCount;
  }

  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    AtomicInteger iWordsWithStarAtStart = new AtomicInteger();
    // 备份，供星号查找使用
    ArrayList<String> bakLines = new ArrayList<>(nrLines);
    igSentimentWordsStrengthTake1 = new int[nrLines + 1 + extraBlankArrayEntriesToInclude];
    sgSentimentWords = new String[nrLines + 1 + extraBlankArrayEntriesToInclude];
    igSentimentWordsCount = 0;

    lines
        .filter(line -> !line.isBlank())
        .forEachOrdered(line -> {
          bakLines.add(line);
          if (line.indexOf("*") == 0) {
            iWordsWithStarAtStart.getAndIncrement();
          } else {
            WordAndStrength ws = parseColumns(line);
            if (Objects.isNull(ws)) {
              return;
            }

            sgSentimentWords[++igSentimentWordsCount] = ws.word();
            int iWordStrength = ws.strength();
            iWordStrength += iWordStrength > 0 ? -1 : 1;
            igSentimentWordsStrengthTake1[igSentimentWordsCount] = iWordStrength;
          }
        });

    sortSentimentList();

    if (iWordsWithStarAtStart.get() > 0) {
      return initialiseWordsWithStarAtStart(bakLines.stream(), iWordsWithStarAtStart.get(), extraBlankArrayEntriesToInclude);
    } else {
      return true;
    }
  }

  @Override
  public boolean haveOptionsChanged(ClassificationOptions old, ClassificationOptions now) {
    return false;
  }

  /**
   * 从文件中初始化以星号开头的单词，根据参数设置数组大小。
   *
   * @param iWordsWithStarAtStart            以星号开头的单词数量
   * @param iExtraBlankArrayEntriesToInclude 额外的空数组条目
   * @return 如果文件读取成功则返回 true，否则返回 false
   */
  private boolean initialiseWordsWithStarAtStart(Stream<String> lines, int iWordsWithStarAtStart, int iExtraBlankArrayEntriesToInclude) {
    igSentimentWordsWithStarAtStartStrengthTake1 = new int[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
    sgSentimentWordsWithStarAtStart = new String[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
    bgSentimentWordsWithStarAtStartHasStarAtEnd = new boolean[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
    igSentimentWordsWithStarAtStartCount = 0;

    lines
        .filter(line -> line.indexOf('*') == 0)
        .map(this::parseColumns)
        .filter(Objects::nonNull)
        .forEachOrdered(ws -> {
          String line = ws.word();
          int iWordStrength = ws.strength();

          if (line.indexOf("*") > 0) {
            line = line.substring(0, line.indexOf("*"));
            bgSentimentWordsWithStarAtStartHasStarAtEnd[++igSentimentWordsWithStarAtStartCount] = true;
          } else {
            bgSentimentWordsWithStarAtStartHasStarAtEnd[++igSentimentWordsWithStarAtStartCount] = false;
          }

          sgSentimentWordsWithStarAtStart[igSentimentWordsWithStarAtStartCount] = line;
          iWordStrength += iWordStrength > 0 ? -1 : 1;
          igSentimentWordsWithStarAtStartStrengthTake1[igSentimentWordsWithStarAtStartCount] = iWordStrength;
        });

    return true;
  }

  /**
   * 添加或修改情感词汇条目。
   *
   * @param sTerm                             待添加或修改的情感词汇
   * @param iTermStrength                     情感词汇的强度值
   * @param bSortSentimentListAfterAddingTerm 是否添加完毕后对情感词汇列表进行排序
   * @return 如果成功添加或修改情感词汇，则返回 true；否则返回 false
   */
  public boolean addOrModifySentimentTerm(String sTerm, int iTermStrength, boolean bSortSentimentListAfterAddingTerm) {
    int iTermPosition = getSentimentID(sTerm);
    if (iTermPosition > 0) {
      if (iTermStrength > 0) {
        iTermStrength--;
      } else if (iTermStrength < 0) {
        iTermStrength++;
      }
      igSentimentWordsStrengthTake1[iTermPosition] = iTermStrength;
    } else {
      try {
        sgSentimentWords[++igSentimentWordsCount] = sTerm;
        if (iTermStrength > 0) {
          iTermStrength--;
        } else if (iTermStrength < 0) {
          iTermStrength++;
        }
        igSentimentWordsStrengthTake1[igSentimentWordsCount] = iTermStrength;
        if (bSortSentimentListAfterAddingTerm) {
          Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
        }
      } catch (Exception e) {
        System.out.println("Could not add extra sentiment term: " + sTerm);
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  /**
   * 对情感词汇列表进行排序。
   */
  public void sortSentimentList() {
    Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
  }
}
