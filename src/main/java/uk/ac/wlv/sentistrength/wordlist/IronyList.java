// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst
// Source File Name:   IronyList.java

package uk.ac.wlv.sentistrength.wordlist;

import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;
import uk.ac.wlv.utilities.Sort;

import java.io.File;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 存放反语词的列表，数据来自文件 {@link ClassificationResources#sgIronyWordListFile}.
 *
 * @see ClassificationResources
 */
public class IronyList extends WordList {

  private String[] sgIronyTerm;
  private int igIronyTermCount;
  private int igIronyTermMax;

  /**
   * 构造函数。
   */
  public IronyList() {
    igIronyTermCount = 0;
    igIronyTermMax = 0;
  }

  /**
   * 判断一个词是否是反语词。
   *
   * @param term 词
   * @return 是否是反语词
   */
  public boolean termIsIronic(String term) {
    int iIronyTermCount = Sort.i_FindStringPositionInSortedArray(term, sgIronyTerm, 1, igIronyTermCount);
    return iIronyTermCount >= 0;
  }

  @Override
  public boolean initialise(String filename, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    if (Objects.isNull(filename) || !(new File(filename)).exists() || igIronyTermCount > 0) {
      return true;
    }

    return super.initialise(filename, options, extraBlankArrayEntriesToInclude);
  }

  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    igIronyTermMax = nrLines + 2;
    igIronyTermCount = 0;
    sgIronyTerm = new String[igIronyTermMax];

    lines
        .filter(line -> !line.isBlank())
        .forEachOrdered(sLine -> {
          String[] sData = sLine.split("\t");
          if (sData.length > 0) {
            sgIronyTerm[++igIronyTermCount] = sData[0];
          }
        });

    Sort.quickSortStrings(sgIronyTerm, 1, igIronyTermCount);
    return true;
  }

  @Override
  public boolean haveOptionsChanged(ClassificationOptions old, ClassificationOptions now) {
    return false;
  }
}
