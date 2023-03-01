// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   BoosterWordsList.java

package uk.ac.wlv.sentistrength;

import java.io.*;

import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

/**
 * something
 */
public class BoosterWordsList
{

    private String sgBoosterWords[];
    private int igBoosterWordStrength[];
    private int igBoosterWordsCount;

    public BoosterWordsList()
    {
        igBoosterWordsCount = 0;
    }

    public boolean initialise(String sFilename, ClassificationOptions options, int iExtraBlankArrayEntriesToInclude)
    {
        int iLinesInFile = 0;
        int iWordStrength = 0;
        if(sFilename == "")
        {
            System.out.println("No booster words file specified");
            return false;
        }
        File f = new File(sFilename);
        if(!f.exists())
        {
            System.out.println((new StringBuilder("Could not find booster words file: ")).append(sFilename).toString());
            return false;
        }
        iLinesInFile = FileOps.i_CountLinesInTextFile(sFilename);
        if(iLinesInFile < 1)
        {
            System.out.println("No booster words specified");
            return false;
        }
        sgBoosterWords = new String[iLinesInFile + 1 + iExtraBlankArrayEntriesToInclude];
        igBoosterWordStrength = new int[iLinesInFile + 1 + iExtraBlankArrayEntriesToInclude];
        igBoosterWordsCount = 0;
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
                    int iFirstTabLocation = sLine.indexOf("\t");
                    if(iFirstTabLocation >= 0)
                    {
                        int iSecondTabLocation = sLine.indexOf("\t", iFirstTabLocation + 1);
                        try
                        {
                            if(iSecondTabLocation > 0)
                                iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation));
                            else
                                iWordStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1).trim());
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Failed to identify integer weight for booster word! Assuming it is zero");
                            System.out.println((new StringBuilder("Line: ")).append(sLine).toString());
                            iWordStrength = 0;
                        }
                        sLine = sLine.substring(0, iFirstTabLocation);
                        if(sLine.indexOf(" ") >= 0)
                            sLine = sLine.trim();
                        if(sLine != "")
                        {
                            igBoosterWordsCount++;
                            sgBoosterWords[igBoosterWordsCount] = sLine;
                            igBoosterWordStrength[igBoosterWordsCount] = iWordStrength;
                        }
                    }
                }
            Sort.quickSortStringsWithInt(sgBoosterWords, igBoosterWordStrength, 1, igBoosterWordsCount);
            rReader.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println((new StringBuilder("Could not find booster words file: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Found booster words file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addExtraTerm(String sText, int iWordStrength, boolean bSortBoosterListAfterAddingTerm)
    {
        try
        {
            igBoosterWordsCount++;
            sgBoosterWords[igBoosterWordsCount] = sText;
            igBoosterWordStrength[igBoosterWordsCount] = iWordStrength;
            if(bSortBoosterListAfterAddingTerm)
                Sort.quickSortStringsWithInt(sgBoosterWords, igBoosterWordStrength, 1, igBoosterWordsCount);
        }
        catch(Exception e)
        {
            System.out.println((new StringBuilder("Could not add extra booster word: ")).append(sText).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void sortBoosterWordList()
    {
        Sort.quickSortStringsWithInt(sgBoosterWords, igBoosterWordStrength, 1, igBoosterWordsCount);
    }

    public int getBoosterStrength(String sWord)
    {
        int iWordID = Sort.i_FindStringPositionInSortedArray(sWord.toLowerCase(), sgBoosterWords, 1, igBoosterWordsCount);
        if(iWordID >= 0)
            return igBoosterWordStrength[iWordID];
        else
            return 0;
    }
}
