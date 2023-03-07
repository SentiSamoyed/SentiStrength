// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   NegatingWordList.java

package uk.ac.wlv.sentistrength;

import java.io.*;


import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 *NegatingWordList类用于存储否定词词典，包含sgNegatingWord、igNegatingWordCount和igNegatingWordMax成员变量，并提供了初始化和判断是否为否定词的方法,
 *初始化后的否定词在sgNegatingWord中按字典序排序。
 */
public class NegatingWordList
{
    /**
     * 存储否定词的字符串数组
     */
    private String sgNegatingWord[];
    /**
     * 否定词数量计数器
     */
    private int igNegatingWordCount;
    /**
     * 否定词数组最大容量
     */
    private int igNegatingWordMax;

    /**
     * 构造函数，初始化计数器和最大容量，将字段igNegatingWordCount与igNegatingWordMax都置为0。
     */
    public NegatingWordList()
    {
        igNegatingWordCount = 0;
        igNegatingWordMax = 0;
    }

    /**
     * 初始化NegatingWordList对象
     * @param sFilename 否定词表文件路径
     * @param options 分类的选项
     * @return 若初始化成功或者已经初始化过，则返回true,否则返回false
     */
    public boolean initialise(String sFilename, ClassificationOptions options)
    {
        if(igNegatingWordMax > 0)
            return true;
        File f = new File(sFilename);
        if(!f.exists())
        {
            System.out.println((new StringBuilder("Could not find the negating words file: ")).append(sFilename).toString());
            return false;
        }
        igNegatingWordMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
        sgNegatingWord = new String[igNegatingWordMax];
        igNegatingWordCount = 0;
        try
        {
            BufferedReader rReader;
            if(options.bgForceUTF8)
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), "UTF8"));
            else
                rReader = new BufferedReader(new FileReader(sFilename));
            String sLine;
            while((sLine = rReader.readLine()) != null)
                if(sLine != "")
                {
                    igNegatingWordCount++;
                    sgNegatingWord[igNegatingWordCount] = sLine;
                }
            rReader.close();
            Sort.quickSortStrings(sgNegatingWord, 1, igNegatingWordCount);
        }
        catch(FileNotFoundException e)
        {
            System.out.println((new StringBuilder("Could not find negating words file: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Found negating words file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断给定词是否为否定词
     * @param sWord 需要判断的词
     * @return 如果sWord是否定词，返回true；否则返回false
     */
    public boolean negatingWord(String sWord)
    {
        return Sort.i_FindStringPositionInSortedArray(sWord, sgNegatingWord, 1, igNegatingWordCount) >= 0;
    }
}
