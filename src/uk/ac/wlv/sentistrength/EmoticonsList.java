// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   EmoticonsList.java

package uk.ac.wlv.sentistrength;

import java.io.*;


import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

public class EmoticonsList
{

    private String sgEmoticon[];
    private int igEmoticonStrength[];
    private int igEmoticonCount;
    private int igEmoticonMax;

    public EmoticonsList()
    {
        igEmoticonCount = 0;
        igEmoticonMax = 0;
    }

    public int getEmoticon(String emoticon)
    {
        int iEmoticon = Sort.i_FindStringPositionInSortedArray(emoticon, sgEmoticon, 1, igEmoticonCount);
        if(iEmoticon >= 0)
            return igEmoticonStrength[iEmoticon];
        else
            return 999;
    }

    public boolean initialise(String sSourceFile, ClassificationOptions options)
    {
        if(igEmoticonCount > 0)
            return true;
        File f = new File(sSourceFile);
        if(!f.exists())
        {
            System.out.println((new StringBuilder("Could not find file: ")).append(sSourceFile).toString());
            return false;
        }
        try
        {
            igEmoticonMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
            igEmoticonCount = 0;
            String sEmoticonTemp[] = new String[igEmoticonMax];
            sgEmoticon = sEmoticonTemp;
            int iEmoticonStrengthTemp[] = new int[igEmoticonMax];
            igEmoticonStrength = iEmoticonStrengthTemp;
            BufferedReader rReader;
            if(options.bgForceUTF8)
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSourceFile), "UTF8"));
            else
                rReader = new BufferedReader(new FileReader(sSourceFile));
            String sLine;
            while((sLine = rReader.readLine()) != null) 
                if(sLine != "")
                {
                    String sData[] = sLine.split("\t");
                    if(sData.length > 1)
                    {
                        igEmoticonCount++;
                        sgEmoticon[igEmoticonCount] = sData[0];
                        try
                        {
                            igEmoticonStrength[igEmoticonCount] = Integer.parseInt(sData[1].trim());
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Failed to identify integer weight for emoticon! Ignoring emoticon");
                            System.out.println((new StringBuilder("Line: ")).append(sLine).toString());
                            igEmoticonCount--;
                        }
                    }
                }
            rReader.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println((new StringBuilder("Could not find emoticon file: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Found emoticon file but could not read from it: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        }
        if(igEmoticonCount > 1)
            Sort.quickSortStringsWithInt(sgEmoticon, igEmoticonStrength, 1, igEmoticonCount);
        return true;
    }
}
