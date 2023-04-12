// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   EmoticonsList.java

package uk.ac.wlv.sentistrength.wordlist;

import lombok.extern.log4j.Log4j2;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.utilities.Sort;

import java.util.stream.Stream;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 表情符列表类。表情符带有一定的情感强度。<br/>
 * 数据来自EmoticonLookUpTable.txt。
 *
 * @see ClassificationOptions
 */
@Log4j2
public class EmoticonsList extends WordList {

  private String[] sgEmoticon;
  private int[] igEmoticonStrength;
  private int igEmoticonCount;
  private int igEmoticonMax;

  public EmoticonsList() {
    igEmoticonCount = 0;
    igEmoticonMax = 0;
  }

  /**
   * 获取一个emoticon的强度。
   *
   * @param emoticon emoticon文本
   * @return 输入的emoticon的强度
   */
  public int getEmoticon(String emoticon) {
    int iEmoticon = Sort.i_FindStringPositionInSortedArray(emoticon, sgEmoticon, 1, igEmoticonCount);
    if (iEmoticon >= 0) {
      return igEmoticonStrength[iEmoticon];
    } else {
      return 999;
    }
  }

  @Override
  public boolean initialise(String filename, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    if (igEmoticonCount > 0) {
      return true;
    }

    return super.initialise(filename, options, extraBlankArrayEntriesToInclude);
  }

  /**
   * 初始化Emoticon和其对应强度的列表，并按字典序排序。
   *
   * @param options 分类选项
   * @return 是否初始化成功
   */
  @Override
  protected boolean initialise(Stream<String> lines, int nrLines, ClassificationOptions options, int extraBlankArrayEntriesToInclude) {
    igEmoticonMax = nrLines + 2;
    igEmoticonCount = 0;
    sgEmoticon = new String[igEmoticonMax];
    igEmoticonStrength = new int[igEmoticonMax];

    lines.filter(line -> !line.isBlank())
        .map(line -> line.split("\t"))
        .filter(a -> a.length > 1)
        .forEachOrdered(sData -> {
          igEmoticonCount++;
          sgEmoticon[igEmoticonCount] = sData[0];
          try {
            igEmoticonStrength[igEmoticonCount] = Integer.parseInt(sData[1].trim());
          } catch (NumberFormatException e) {
            log.error("Failed to identify integer weight for emoticon! Ignoring emoticon");
            log.error("Line: " + String.join("\t", sData));
            igEmoticonCount--;
          }
        });

    if (igEmoticonCount > 1) {
      Sort.quickSortStringsWithInt(sgEmoticon, igEmoticonStrength, 1, igEmoticonCount);
    }

    return true;
  }
}
