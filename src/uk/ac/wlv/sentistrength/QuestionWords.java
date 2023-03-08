// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   QuestionWords.java

package uk.ac.wlv.sentistrength;

import java.io.*;

import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * 疑问词类，用于处理疑问词的相关操作，包括初始化、判断是否为疑问词等功能，疑问词按字典序存储。
 */
public class QuestionWords
{
    /**
     * 存储疑问词的字符串数组
     */
    private String sgQuestionWord[];
    /**
     * 存储疑问词汇的数量
     */
    private int igQuestionWordCount;
    /**
     * 存储疑问词汇的最大数量
     */
    private int igQuestionWordMax;

    /**
     * 构造函数，初始化 igQuestionWordCount和igQuestionWordMax 为 0
     */
    public QuestionWords()
    {
        igQuestionWordCount = 0;
        igQuestionWordMax = 0;
    }

    /**
     * 初始化疑问词列表，如果疑问词列表已经被初始化，则直接返回 true
     * @param sFilename 疑问词列表路径
     * @param options 分类选项
     * @return 如果疑问词列表初始化成功则返回 true，否则返回 false
     */
    public boolean initialise(String sFilename, ClassificationOptions options)
    {
        if(igQuestionWordMax > 0)
            return true;
        File f = new File(sFilename);
        if(!f.exists())
        {
            System.out.println((new StringBuilder("Could not find the question word file: ")).append(sFilename).toString());
            return false;
        }
        igQuestionWordMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
        sgQuestionWord = new String[igQuestionWordMax];
        igQuestionWordCount = 0;
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
                    igQuestionWordCount++;
                    sgQuestionWord[igQuestionWordCount] = sLine;
                }
            rReader.close();
            Sort.quickSortStrings(sgQuestionWord, 1, igQuestionWordCount);
        }
        catch(FileNotFoundException e)
        {
            System.out.println((new StringBuilder("Could not find the question word file: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Found question word file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断一个单词是否为疑问词
     * @param sWord 待判断的单词
     * @return 如果该单词是疑问词则返回 true，否则返回 false
     */
    public boolean questionWord(String sWord)
    {
        return Sort.i_FindStringPositionInSortedArray(sWord, sgQuestionWord, 1, igQuestionWordCount) >= 0;
    }
}
