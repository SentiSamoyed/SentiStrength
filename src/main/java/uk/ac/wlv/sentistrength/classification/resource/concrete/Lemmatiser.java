// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   Lemmatiser.java

package uk.ac.wlv.sentistrength.classification.resource.concrete;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;
import uk.ac.wlv.sentistrength.classification.resource.Resource;
import uk.ac.wlv.utilities.Sort;

import java.util.stream.Stream;

/**
 * 存放词根与衍生词对应关系的词形还原器，数据来自文件 {@link ClassificationResources#lemmatiserFile}.
 *
 * @see ClassificationResources
 */
public class Lemmatiser extends Resource {

  private String[] sgWord;
  private String[] sgLemma;
  private int igWordLast;
  // TODO ClassificationResources中，LemmaFile = “”，同时CorrectSpellingFile有两个，
  //  第一个Dictionary文件是不存在的，代码逻辑中第一个不存在会指向第二个文件进行读取

  /**
   * Lemmatiser 构造函数
   */
  public Lemmatiser() {
    igWordLast = -1;
  }


  /**
   * 词形还原，根据单词在语境中的形式找出单词对应的词根。
   *
   * @param sWord 单词文本
   * @return 单词的词根
   */
  public String lemmatise(String sWord) {
    // 根据初始化过的词形还原表，搜索衍生词表中是否有sWord，若存在返回其对应下标在lemma表中的内容，否则其为词根本身
    int iLemmaId = Sort.i_FindStringPositionInSortedArray(sWord, sgWord, 0, igWordLast);
    if (iLemmaId >= 0) {
      return sgLemma[iLemmaId];
    } else {
      return sWord;
    }
  }

  @Override
  public boolean initialise(String filename, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    if (!options.bgUseLemmatisation) {
      return true;
    }

    return super.initialise(filename, options, extraBlankArrayEntriesToInclude);
  }

  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    sgWord = new String[nrLines + 1];
    sgLemma = new String[nrLines + 1];
    igWordLast = -1;

    lines
        .filter(line -> !line.isBlank())
        .map(super::parseColumnsToStrings)
        .forEachOrdered(ss -> {
          // 第一个制表符前的为词根，第一个制表符到第二个制表符之间或者第一个制表符往后是该制表符的衍生词
          String first = ss[0], second = ss[1];

          sgWord[++igWordLast] = first;
          sgLemma[igWordLast] = second;

          // 将word及lemma字符串去除首尾多余空格
          if (sgWord[igWordLast].contains(" ")) {
            sgWord[igWordLast] = sgWord[igWordLast].trim();
          }
          if (sgLemma[igWordLast].contains(" ")) {
            sgLemma[igWordLast] = sgLemma[igWordLast].trim();
          }
        });


    return true;
  }

  @Override
  public boolean haveOptionsChanged(ClassificationOptions old, ClassificationOptions now) {
    return old.bgUseLemmatisation != now.bgUseLemmatisation;
  }
}
