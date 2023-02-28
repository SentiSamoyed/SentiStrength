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

public class SentimentWords
{

    private String sgSentimentWords[];
    private int igSentimentWordsStrengthTake1[];
    private int igSentimentWordsCount;
    private String sgSentimentWordsWithStarAtStart[];
    private int igSentimentWordsWithStarAtStartStrengthTake1[];
    private int igSentimentWordsWithStarAtStartCount;
    private boolean bgSentimentWordsWithStarAtStartHasStarAtEnd[];

    public SentimentWords()
    {
        igSentimentWordsCount = 0;
        igSentimentWordsWithStarAtStartCount = 0;
    }

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

    public void setSentiment(int iWordID, int iNewSentiment)
    {
        if(iWordID <= igSentimentWordsCount)
            igSentimentWordsStrengthTake1[iWordID] = iNewSentiment;
        else
            igSentimentWordsWithStarAtStartStrengthTake1[iWordID - igSentimentWordsCount] = iNewSentiment;
    }

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

    public int getSentimentWordCount()
    {
        return igSentimentWordsCount;
    }

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

    public void sortSentimentList()
    {
        Sort.quickSortStringsWithInt(sgSentimentWords, igSentimentWordsStrengthTake1, 1, igSentimentWordsCount);
    }
}
