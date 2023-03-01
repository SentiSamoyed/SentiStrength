// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   CorrectSpellingsList.java

package uk.ac.wlv.sentistrength;

import java.io.*;


import uk.ac.wlv.utilities.FileOps;
import uk.ac.wlv.utilities.Sort;

// Referenced classes of package uk.ac.wlv.sentistrength:
//            ClassificationOptions

public class CorrectSpellingsList
{

  private String sgCorrectWord[];
  private int igCorrectWordCount;
  private int igCorrectWordMax;

  public CorrectSpellingsList()
  {
    igCorrectWordCount = 0;
    igCorrectWordMax = 0;
  }

  public boolean initialise(String sFilename, ClassificationOptions options)
  {
    if(igCorrectWordMax > 0)
      return true;
    if(!options.bgCorrectSpellingsUsingDictionary)
      return true;
    igCorrectWordMax = FileOps.i_CountLinesInTextFile(sFilename) + 2;
    sgCorrectWord = new String[igCorrectWordMax];
    igCorrectWordCount = 0;
    File f = new File(sFilename);
    if(!f.exists())
    {
      System.out.println((new StringBuilder("Could not find the spellings file: ")).append(sFilename).toString());
      return false;
    }
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
          igCorrectWordCount++;
          sgCorrectWord[igCorrectWordCount] = sLine;
        }
      rReader.close();
      Sort.quickSortStrings(sgCorrectWord, 1, igCorrectWordCount);
    }
    catch(FileNotFoundException e)
    {
      System.out.println((new StringBuilder("Could not find the spellings file: ")).append(sFilename).toString());
      e.printStackTrace();
      return false;
    }
    catch(IOException e)
    {
      System.out.println((new StringBuilder("Found spellings file but could not read from it: ")).append(sFilename).toString());
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean correctSpelling(String sWord)
  {
    return Sort.i_FindStringPositionInSortedArray(sWord, sgCorrectWord, 1, igCorrectWordCount) >= 0;
  }
}
