// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   IdiomList.java

package uk.ac.wlv.sentistrength;

import java.io.*;


import uk.ac.wlv.utilities.FileOps;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

public class IdiomList
{

    public String sgIdioms[];
    public int igIdiomStrength[];
    public int igIdiomCount;
    public String sgIdiomWords[][];
    int igIdiomWordCount[];

    public IdiomList()
    {
        igIdiomCount = 0;
    }

    public boolean initialise(String sFilename, ClassificationOptions options, int iExtraBlankArrayEntriesToInclude)
    {
        int iLinesInFile = 0;
        int iIdiomStrength = 0;
        if(sFilename == "")
            return false;
        File f = new File(sFilename);
        if(!f.exists())
        {
            System.out.println((new StringBuilder("Could not find idiom list file: ")).append(sFilename).toString());
            return false;
        }
        iLinesInFile = FileOps.i_CountLinesInTextFile(sFilename);
        sgIdioms = new String[iLinesInFile + 2 + iExtraBlankArrayEntriesToInclude];
        igIdiomStrength = new int[iLinesInFile + 2 + iExtraBlankArrayEntriesToInclude];
        igIdiomCount = 0;
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
                                iIdiomStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1, iSecondTabLocation).trim());
                            else
                                iIdiomStrength = Integer.parseInt(sLine.substring(iFirstTabLocation + 1).trim());
                            if(iIdiomStrength > 0)
                                iIdiomStrength--;
                            else
                            if(iIdiomStrength < 0)
                                iIdiomStrength++;
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Failed to identify integer weight for idiom! Ignoring idiom");
                            System.out.println((new StringBuilder("Line: ")).append(sLine).toString());
                            iIdiomStrength = 0;
                        }
                        sLine = sLine.substring(0, iFirstTabLocation);
                        if(sLine.indexOf(" ") >= 0)
                            sLine = sLine.trim();
                        if(sLine.indexOf("  ") > 0)
                            sLine = sLine.replace("  ", " ");
                        if(sLine.indexOf("  ") > 0)
                            sLine = sLine.replace("  ", " ");
                        if(sLine != "")
                        {
                            igIdiomCount++;
                            sgIdioms[igIdiomCount] = sLine;
                            igIdiomStrength[igIdiomCount] = iIdiomStrength;
                        }
                    }
                }
            rReader.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println((new StringBuilder("Could not find idiom list file: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Found idiom list file but could not read from it: ")).append(sFilename).toString());
            e.printStackTrace();
            return false;
        }
        convertIdiomStringsToWordLists();
        return true;
    }

    public boolean addExtraIdiom(String sIdiom, int iIdiomStrength, boolean bConvertIdiomStringsToWordListsAfterAddingIdiom)
    {
        try
        {
            igIdiomCount++;
            sgIdioms[igIdiomCount] = sIdiom;
            if(iIdiomStrength > 0)
                iIdiomStrength--;
            else
            if(iIdiomStrength < 0)
                iIdiomStrength++;
            igIdiomStrength[igIdiomCount] = iIdiomStrength;
            if(bConvertIdiomStringsToWordListsAfterAddingIdiom)
                convertIdiomStringsToWordLists();
        }
        catch(Exception e)
        {
            System.out.println((new StringBuilder("Could not add extra idiom: ")).append(sIdiom).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void convertIdiomStringsToWordLists()
    {
        sgIdiomWords = new String[igIdiomCount + 1][10];
        igIdiomWordCount = new int[igIdiomCount + 1];
        for(int iIdiom = 1; iIdiom <= igIdiomCount; iIdiom++)
        {
            String sWordList[] = sgIdioms[iIdiom].split(" ");
            if(sWordList.length >= 9)
            {
                System.out.println((new StringBuilder("Ignoring idiom! Too many words in it! (>9): ")).append(sgIdioms[iIdiom]).toString());
            } else
            {
                igIdiomWordCount[iIdiom] = sWordList.length;
                for(int iTerm = 0; iTerm < sWordList.length; iTerm++)
                    sgIdiomWords[iIdiom][iTerm] = sWordList[iTerm];

            }
        }

    }

    public int getIdiomStrength_oldNotUseful(String sPhrase)
    {
        sPhrase = sPhrase.toLowerCase();
        for(int i = 1; i <= igIdiomCount; i++)
            if(sPhrase.indexOf(sgIdioms[i]) >= 0)
                return igIdiomStrength[i];

        return 999;
    }

    public String getIdiom(int iIdiomID)
    {
        if(iIdiomID > 0 && iIdiomID < igIdiomCount)
            return sgIdioms[iIdiomID];
        else
            return "";
    }
}
