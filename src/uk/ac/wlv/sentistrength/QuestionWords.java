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

public class QuestionWords
{

    private String sgQuestionWord[];
    private int igQuestionWordCount;
    private int igQuestionWordMax;

    public QuestionWords()
    {
        igQuestionWordCount = 0;
        igQuestionWordMax = 0;
    }

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

    public boolean questionWord(String sWord)
    {
        return Sort.i_FindStringPositionInSortedArray(sWord, sgQuestionWord, 1, igQuestionWordCount) >= 0;
    }
}
