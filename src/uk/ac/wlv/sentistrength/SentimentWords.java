// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   SentimentWords.java

package uk.ac.wlv.sentistrength;

import java.io.*;


import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            Corpus, ClassificationOptions

/**
 * 情感词列表类，用于存储情感词及其情感强度等信息，并提供初始化、存取、更新等操作
 */
public class SentimentWords
{
    /**
     * 存储不带星号前缀的情感词的数组
     */
    private String sgSentimentWords[];
    /**
     * 存储不带星号前缀的情感词的情感强度值的数组
     */
    private int igSentimentWordsStrengthTake1[];
    /**
     * 不带星号前缀的情感词的数量
     */
    private int igSentimentWordsCount;
    /**
     * 存储带星号前缀的情感词的数组
     */
    private String sgSentimentWordsWithStarAtStart[];
    /**
     * 存储带星号前缀的情感词的情感强度值的数组
     */
    private int igSentimentWordsWithStarAtStartStrengthTake1[];
    /**
     * 带星号前缀的情感词的数量
     */
    private int igSentimentWordsWithStarAtStartCount;
    /**
     * 存储带星号前缀的情感词是否以星号结尾的布尔数组
     */
    private boolean bgSentimentWordsWithStarAtStartHasStarAtEnd[];

    /**
     * 构造函数，初始化不带星号前缀的情感词数量和带星号前缀的情感词数量
     */
    public SentimentWords()
    {
        igSentimentWordsCount = 0;
        igSentimentWordsWithStarAtStartCount = 0;
    }

    /**
     * 获取指定编号的情感词
     * @param iWordID 情感词的编号
     * @return 指定编号的情感词，如果编号无效则返回空字符串
     */
    public String getSentimentWord(int iWordID)
    {
        if(iWordID > 0)
        {
            if(iWordID <= igSentimentWordsCount)
                return sgSentimentWords[iWordID];
            if(iWordID <= igSentimentWordsCount + igSentimentWordsWithStarAtStartCount)
                return sgSentimentWordsWithStarAtStart[iWordID - igSentimentWordsCount];
        }
        return "";
    }

    /**
     * 获取指定情感词的情感强度值
     * @param sWord 情感词
     * @return 指定情感词的情感强度值，如果情感词未找到则返回999
     */
    public int getSentiment(String sWord)
    {
        int iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(), sgSentimentWords, 1, igSentimentWordsCount);
        if(iWordID >= 0)
            return igSentimentWordsStrengthTake1[iWordID];
        int iStarWordID = getMatchingStarAtStartRawWordID(sWord);
        if(iStarWordID >= 0)
            return igSentimentWordsWithStarAtStartStrengthTake1[iStarWordID];
        else
            return 999;
    }

    /**
     * 设置指定情感词的情感强度值
     * @param sWord 情感词
     * @param iNewSentiment 新的情感词的情感强度值
     * @return 更新成功则返回 {@code true},否则返回{@code false}
     */
    public boolean setSentiment(String sWord, int iNewSentiment)
    {
        int iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(), sgSentimentWords, 1, igSentimentWordsCount);
        if(iWordID >= 0)
        {
            if(iNewSentiment > 0)
                setSentiment(iWordID, iNewSentiment - 1);
            else
                setSentiment(iWordID, iNewSentiment + 1);
            return true;
        }
        if(sWord.indexOf("*") == 0)
        {
            sWord = sWord.substring(1);
            if(sWord.indexOf("*") > 0)
                sWord.substring(0, sWord.length() - 1);
        }
        if(igSentimentWordsWithStarAtStartCount > 0)
        {
            for(int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++)
                if(sWord == sgSentimentWordsWithStarAtStart[i])
                {
                    if(iNewSentiment > 0)
                        setSentiment(igSentimentWordsCount + i, iNewSentiment - 1);
                    else
                        setSentiment(igSentimentWordsCount + i, iNewSentiment + 1);
                    return true;
                }

        }
        return false;
    }

    /**
     *将情感词汇列表保存到指定文件中
     * @param sFilename 保存情感词汇列表的文件路径
     * @param c 语料库对象，用于判断是否强制使用 UTF-8 编码保存文件
     * @return 如果成功保存情感词汇列表，则返回 true；否则返回 false
     */
    public boolean saveSentimentList(String sFilename, Corpus c)
    {
        try
        {
            BufferedWriter wWriter = new BufferedWriter(new FileWriter(sFilename));
            for(int i = 1; i <= igSentimentWordsCount; i++)
            {
                int iSentimentStrength = igSentimentWordsStrengthTake1[i];
                if(iSentimentStrength < 0)
                    iSentimentStrength--;
                else
                    iSentimentStrength++;
                String sOutput = (new StringBuilder(String.valueOf(sgSentimentWords[i]))).append("\t").append(iSentimentStrength).append("\n").toString();
                if(c.options.bgForceUTF8)
                    try
                    {
                        sOutput = new String(sOutput.getBytes("UTF-8"), "UTF-8");
                    }
                    catch(UnsupportedEncodingException e)
                    {
                        System.out.println("UTF-8 not found on your system!");
                        e.printStackTrace();
                    }
                wWriter.write(sOutput);
            }

            for(int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++)
            {
                int iSentimentStrength = igSentimentWordsWithStarAtStartStrengthTake1[i];
                if(iSentimentStrength < 0)
                    iSentimentStrength--;
                else
                    iSentimentStrength++;
                String sOutput = (new StringBuilder("*")).append(sgSentimentWordsWithStarAtStart[i]).toString();
                if(bgSentimentWordsWithStarAtStartHasStarAtEnd[i])
                    sOutput = (new StringBuilder(String.valueOf(sOutput))).append("*").toString();
                sOutput = (new StringBuilder(String.valueOf(sOutput))).append("\t").append(iSentimentStrength).append("\n").toString();
                if(c.options.bgForceUTF8)
                    try
                    {
                        sOutput = new String(sOutput.getBytes("UTF-8"), "UTF-8");
                    }
                    catch(UnsupportedEncodingException e)
                    {
                        System.out.println("UTF-8 not found on your system!");
                        e.printStackTrace();
                    }
                wWriter.write(sOutput);
            }

            wWriter.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打印情感词的情感值到单行中
     * @param wWriter 要写入的缓冲区写入器
     * @return 如果成功写入，返回 true；否则返回 false
     */
    public boolean printSentimentValuesInSingleRow(BufferedWriter wWriter)
    {
        try
        {
            for(int i = 1; i <= igSentimentWordsCount; i++)
            {
                int iSentimentStrength = igSentimentWordsStrengthTake1[i];
                wWriter.write((new StringBuilder("\t")).append(iSentimentStrength).toString());
            }

            for(int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++)
            {
                int iSentimentStrength = igSentimentWordsWithStarAtStartStrengthTake1[i];
                wWriter.write((new StringBuilder("\t")).append(iSentimentStrength).toString());
            }

            wWriter.write("\n");
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打印情感词到单行的头部
     * @param wWriter 要写入的缓冲区写入器
     * @return 如果成功写入，返回 true；否则返回 false
     */
    public boolean printSentimentTermsInSingleHeaderRow(BufferedWriter wWriter)
    {
        try
        {
            for(int i = 1; i <= igSentimentWordsCount; i++)
                wWriter.write((new StringBuilder("\t")).append(sgSentimentWords[i]).toString());

            for(int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++)
            {
                wWriter.write((new StringBuilder("\t*")).append(sgSentimentWordsWithStarAtStart[i]).toString());
                if(bgSentimentWordsWithStarAtStartHasStarAtEnd[i])
                    wWriter.write("*");
            }

            wWriter.write("\n");
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取情感值
     * @param iWordID 词的 ID
     * @return 词的情感值，如果 ID 大于 0，则返回 igSentimentWordsStrengthTake1[iWordID]；否则返回 999
     */
    public int getSentiment(int iWordID)
    {
        if(iWordID > 0)
        {
            if(iWordID <= igSentimentWordsCount)
                return igSentimentWordsStrengthTake1[iWordID];
            else
                return igSentimentWordsWithStarAtStartStrengthTake1[iWordID - igSentimentWordsCount];
        } else
        {
            return 999;
        }
    }

    /**
     * 设置情感值
     * @param iWordID 词的 ID
     * @param iNewSentiment  新的情感值
     */
    public void setSentiment(int iWordID, int iNewSentiment)
    {
        if(iWordID <= igSentimentWordsCount)
            igSentimentWordsStrengthTake1[iWordID] = iNewSentiment;
        else
            igSentimentWordsWithStarAtStartStrengthTake1[iWordID - igSentimentWordsCount] = iNewSentiment;
    }

    /**
     *返回给定情感词的索引
     * @param sWord 要查找的情感词
     * @return 给定情感词的索引，如果词不存在，则返回-1
     */
    public int getSentimentID(String sWord)
    {
        int iWordID = Sort.i_FindStringPositionInSortedArrayWithWildcardsInArray(sWord.toLowerCase(), sgSentimentWords, 1, igSentimentWordsCount);
        if(iWordID >= 0)
            return iWordID;
        iWordID = getMatchingStarAtStartRawWordID(sWord);
        if(iWordID >= 0)
            return iWordID + igSentimentWordsCount;
        else
            return -1;
    }

    /**
     * 返回以星号开头的情感词的索引
     * @param sWord 要查找的情感词
     * @return 以星号开头的情感词的索引，如果不是以星号开头的词，返回-1
     */
    private int getMatchingStarAtStartRawWordID(String sWord)
    {
        int iSubStringPos = 0;
        if(igSentimentWordsWithStarAtStartCount > 0)
        {
            for(int i = 1; i <= igSentimentWordsWithStarAtStartCount; i++)
            {
                iSubStringPos = sWord.indexOf(sgSentimentWordsWithStarAtStart[i]);
                if(iSubStringPos >= 0 && (bgSentimentWordsWithStarAtStartHasStarAtEnd[i] || iSubStringPos + sgSentimentWordsWithStarAtStart[i].length() == sWord.length()))
                    return i;
            }

        }
        return -1;
    }

    /**
     * 返回情感词总数
     * @return 情感词总数
     */
    public int getSentimentWordCount()
    {
        return igSentimentWordsCount;
    }

    /**
     * 从给定的情感词汇表文件中初始化情感词汇表
     * @param sFilename 给定的情感词汇表文件名
     * @param options 分类选项
     * @param iExtraBlankArrayEntriesToInclude 初始化数组的额外空白条目数
     * @return 如果成功初始化，则为{@code true}，否则为{@code false}
     */
    public boolean initialise(String sFilename, ClassificationOptions options, int iExtraBlankArrayEntriesToInclude)
    {
        int iWordStrength = 0;
        int iWordsWithStarAtStart = 0;
        if(sFilename == "")
        {
            System.out.println("No sentiment file specified");
            return false;
        }
        File f = new File(sFilename);
        if(!f.exists())
        {
            System.out.println((new StringBuilder("Could not find sentiment file: ")).append(sFilename).toString());
            return false;
        }
        int iLinesInFile = FileOps.i_CountLinesInTextFile(sFilename);
        if(iLinesInFile < 2)
        {
            System.out.println((new StringBuilder("Less than 2 lines in sentiment file: ")).append(sFilename).toString());
            return false;
        }
        igSentimentWordsStrengthTake1 = new int[iLinesInFile + 1 + iExtraBlankArrayEntriesToInclude];
        sgSentimentWords = new String[iLinesInFile + 1 + iExtraBlankArrayEntriesToInclude];
        igSentimentWordsCount = 0;
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
                    if(sLine.indexOf("*") == 0)
                    {
                        iWordsWithStarAtStart++;
                    } else
                    {
                        int iFirstTabLocation = sLine.indexOf("\t");
                        if(iFirstTabLocation >= 0)
                        {
                            int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                            try
                            {
                                if(iSecondTabLocation > 0)
                                    iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation).trim());
                                else
                                    iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1).trim());
                            }
                            catch(NumberFormatException e)
                            {
                                System.out.println((new StringBuilder("Failed to identify integer weight for sentiment word! Ignoring word\nLine: ")).append(sLine).toString());
                                iWordStrength = 0;
                            }
                            sLine = sLine.substring(0, iFirstTabLocation);
                            if(sLine.indexOf(" ") >= 0)
                                sLine = sLine.trim();
                            if(sLine != "")
                            {
                                sgSentimentWords[++igSentimentWordsCount] = sLine;
                                if(iWordStrength > 0)
                                    iWordStrength--;
                                else
                                if(iWordStrength < 0)
                                    iWordStrength++;
                                igSentimentWordsStrengthTake1[igSentimentWordsCount] = iWordStrength;
                            }
                        }
                    }
            rReader.close();
            Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
        }
        catch(FileNotFoundException e)
        {
            System.out.println((new StringBuilder("Couldn't find sentiment file: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Found sentiment file but couldn't read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        if(iWordsWithStarAtStart > 0)
            return initialiseWordsWithStarAtStart(sFilename, options, iWordsWithStarAtStart, iExtraBlankArrayEntriesToInclude);
        else
            return true;
    }

    /**
     * 从文件中初始化以星号开头的单词，根据参数设置数组大小
     * @param sFilename 文件名
     * @param options 分类选项
     * @param iWordsWithStarAtStart 以星号开头的单词数量
     * @param iExtraBlankArrayEntriesToInclude 额外的空数组条目
     * @return 如果文件读取成功则返回true，否则返回false
     */
    public boolean initialiseWordsWithStarAtStart(String sFilename, ClassificationOptions options, int iWordsWithStarAtStart, int iExtraBlankArrayEntriesToInclude)
    {
        int iWordStrength = 0;
        File f = new File(sFilename);
        if(!f.exists())
        {
            System.out.println((new StringBuilder("Could not find sentiment file: ")).append(sFilename).toString());
            return false;
        }
        igSentimentWordsWithStarAtStartStrengthTake1 = new int[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
        sgSentimentWordsWithStarAtStart = new String[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
        bgSentimentWordsWithStarAtStartHasStarAtEnd = new boolean[iWordsWithStarAtStart + 1 + iExtraBlankArrayEntriesToInclude];
        igSentimentWordsWithStarAtStartCount = 0;
        try
        {
            BufferedReader rReader;
            if(options.bgForceUTF8)
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sFilename), "UTF8"));
            else
                rReader = new BufferedReader(new FileReader(sFilename));
            while(rReader.ready())
            {
                String sLine = rReader.readLine();
                if(sLine != "" && sLine.indexOf("*") == 0)
                {
                    int iFirstTabLocation = sLine.indexOf("\t");
                    if(iFirstTabLocation >= 0)
                    {
                        int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                        try
                        {
                            if(iSecondTabLocation > 0)
                                iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation));
                            else
                                iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1));
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println((new StringBuilder("Failed to identify integer weight for *sentiment* word! Ignoring word\nLine: ")).append(sLine).toString());
                            iWordStrength = 0;
                        }
                        sLine = sLine.substring(1, iFirstTabLocation);
                        if(sLine.indexOf("*") > 0)
                        {
                            sLine = sLine.substring(0, sLine.indexOf("*"));
                            bgSentimentWordsWithStarAtStartHasStarAtEnd[++igSentimentWordsWithStarAtStartCount] = true;
                        } else
                        {
                            bgSentimentWordsWithStarAtStartHasStarAtEnd[++igSentimentWordsWithStarAtStartCount] = false;
                        }
                        if(sLine.indexOf(" ") >= 0)
                            sLine = sLine.trim();
                        if(sLine != "")
                        {
                            sgSentimentWordsWithStarAtStart[igSentimentWordsWithStarAtStartCount] = sLine;
                            if(iWordStrength > 0)
                                iWordStrength--;
                            else
                            if(iWordStrength < 0)
                                iWordStrength++;
                            igSentimentWordsWithStarAtStartStrengthTake1[igSentimentWordsWithStarAtStartCount] = iWordStrength;
                        } else
                        {
                            igSentimentWordsWithStarAtStartCount--;
                        }
                    }
                }
            }
            rReader.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println((new StringBuilder("Couldn't find *sentiment file*: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Found *sentiment file* but couldn't read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 添加或修改情感词汇条目
     * @param sTerm 待添加或修改的情感词汇
     * @param iTermStrength 情感词汇的强度值
     * @param bSortSentimentListAfterAddingTerm 是否添加完毕后对情感词汇列表进行排序
     * @return 如果成功添加或修改情感词汇，则返回 true；否则返回 false
     */
    public boolean addOrModifySentimentTerm(String sTerm, int iTermStrength, boolean bSortSentimentListAfterAddingTerm)
    {
        int iTermPosition = getSentimentID(sTerm);
        if(iTermPosition > 0)
        {
            if(iTermStrength > 0)
                iTermStrength--;
            else
            if(iTermStrength < 0)
                iTermStrength++;
            igSentimentWordsStrengthTake1[iTermPosition] = iTermStrength;
        } else
        {
            try
            {
                sgSentimentWords[++igSentimentWordsCount] = sTerm;
                if(iTermStrength > 0)
                    iTermStrength--;
                else
                if(iTermStrength < 0)
                    iTermStrength++;
                igSentimentWordsStrengthTake1[igSentimentWordsCount] = iTermStrength;
                if(bSortSentimentListAfterAddingTerm)
                    Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
            }
            catch(Exception e)
            {
                System.out.println((new StringBuilder("Could not add extra sentiment term: ")).append(sTerm).toString());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 对情感词汇列表进行排序
     */
    public void sortSentimentList()
    {
        Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
    }
}
