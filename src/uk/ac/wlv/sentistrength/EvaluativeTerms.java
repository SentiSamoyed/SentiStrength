// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   EvaluativeTerms.java

package uk.ac.wlv.sentistrength;

import java.io.*;


import uk.ac.wlv.utilities.FileOps;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions, IdiomList, SentimentWords

public class EvaluativeTerms
{

    private int igObjectEvaluationMax;
    public String sgObject[];
    public String sgObjectEvaluation[];
    public int igObjectEvaluationStrength[];
    public int igObjectEvaluationCount;

    public EvaluativeTerms()
    {
        igObjectEvaluationMax = 0;
        igObjectEvaluationCount = 0;
    }

    public boolean initialise(String sSourceFile, ClassificationOptions options, IdiomList idiomList, SentimentWords sentimentWords)
    {
        if(igObjectEvaluationCount > 0)
            return true;
        File f = new File(sSourceFile);
        if(!f.exists())
        {
            System.out.println((new StringBuilder("Could not find additional (object/evaluation) file: ")).append(sSourceFile).toString());
            return false;
        }
        int iStrength = 0;
        boolean bIdiomsAdded = false;
        boolean bSentimentWordsAdded = false;
        try
        {
            igObjectEvaluationMax = FileOps.i_CountLinesInTextFile(sSourceFile) + 2;
            igObjectEvaluationCount = 0;
            sgObject = new String[igObjectEvaluationMax];
            sgObjectEvaluation = new String[igObjectEvaluationMax];
            igObjectEvaluationStrength = new int[igObjectEvaluationMax];
            BufferedReader rReader;
            if(options.bgForceUTF8)
                rReader = new BufferedReader(new InputStreamReader(new FileInputStream(sSourceFile), "UTF8"));
            else
                rReader = new BufferedReader(new FileReader(sSourceFile));
            String sLine;
            while((sLine = rReader.readLine()) != null) 
                if(sLine != "" && sLine.indexOf("##") != 0 && sLine.indexOf("\t") > 0)
                {
                    String sData[] = sLine.split("\t");
                    if(sData.length > 2 && sData[2].indexOf("##") != 0)
                    {
                        sgObject[++igObjectEvaluationCount] = sData[0];
                        sgObjectEvaluation[igObjectEvaluationCount] = sData[1];
                        try
                        {
                            igObjectEvaluationStrength[igObjectEvaluationCount] = Integer.parseInt(sData[2].trim());
                            if(igObjectEvaluationStrength[igObjectEvaluationCount] > 0)
                                igObjectEvaluationStrength[igObjectEvaluationCount]--;
                            else
                            if(igObjectEvaluationStrength[igObjectEvaluationCount] < 0)
                                igObjectEvaluationStrength[igObjectEvaluationCount]++;
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Failed to identify integer weight for object/evaluation! Ignoring object/evaluation");
                            System.out.println((new StringBuilder("Line: ")).append(sLine).toString());
                            igObjectEvaluationCount--;
                        }
                    } else
                    if(sData[0].indexOf(" ") > 0)
                        try
                        {
                            iStrength = Integer.parseInt(sData[1].trim());
                            idiomList.addExtraIdiom(sData[0], iStrength, false);
                            bIdiomsAdded = true;
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Failed to identify integer weight for idiom in additional file! Ignoring it");
                            System.out.println((new StringBuilder("Line: ")).append(sLine).toString());
                        }
                    else
                        try
                        {
                            iStrength = Integer.parseInt(sData[1].trim());
                            sentimentWords.addOrModifySentimentTerm(sData[0], iStrength, false);
                            bSentimentWordsAdded = true;
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Failed to identify integer weight for sentiment term in additional file! Ignoring it");
                            System.out.println((new StringBuilder("Line: ")).append(sLine).toString());
                            igObjectEvaluationCount--;
                        }
                }
            rReader.close();
            if(igObjectEvaluationCount > 0)
                options.bgUseObjectEvaluationTable = true;
            if(bSentimentWordsAdded)
                sentimentWords.sortSentimentList();
            if(bIdiomsAdded)
                idiomList.convertIdiomStringsToWordLists();
        }
        catch(FileNotFoundException e)
        {
            System.out.println((new StringBuilder("Could not find additional (object/evaluation) file: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            System.out.println((new StringBuilder("Found additional (object/evaluation) file but could not read from it: ")).append(sSourceFile).toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
