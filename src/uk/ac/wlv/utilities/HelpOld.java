// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   HelpOld.java

package uk.ac.wlv.utilities;

import java.io.PrintStream;

// Referenced classes of package uk.ac.wlv.utilities:
//            SentiStrengthOld

public class HelpOld
{

    public HelpOld()
    {
    }

    public static void main(String args[])
    {
        System.out.println("Methods available in this package:");
        System.out.println("sentiStrength - all SentiStrength classification features except idioms");
        System.out.println("sentiStrengthTestApplet - for testing SentiStrength in an applet");
        System.out.println();
        System.out.println("sentiStrength configuration data must be in C:\\SentStrength_Data\\ for applet");
        System.out.println("To use sentiStrength class:");
        System.out.println("sentiStrength ss = new sentiStrength();");
        System.out.println("ss.setInitalisationFilesFolder(\"[your SentiStrength_Data folder path]\");");
        System.out.println("ss.initialise(); // returns false if can't read from above folder");
        System.out.println("ss.detectEmotionInText(\"[your text to be classified]\");");
        System.out.println("Positive sentiment result in ss.getPositiveClassification()");
        System.out.println("Positive sentiment result in ss.getNegativeClassification()");
        System.out.println();
        System.out.println("OR can test from command line with text following call, e.g.");
        System.out.println("java -jar SentiStrength.jar I hate you :)");
        System.out.println();
        System.out.println("Get any error details from getErrorLog()");
        System.out.println("The remaining public variables are optional classification parameters");
        System.out.println("e.g., whether to use negating words");
        System.out.println();
        String sTextToTranslate = "";
        for(int i = 0; i < args.length; i++)
            sTextToTranslate = (new StringBuilder(String.valueOf(sTextToTranslate))).append(args[i]).append(" ").toString();

        if(sTextToTranslate != "")
        {
            SentiStrengthOld ss = new SentiStrengthOld();
            if(ss.initialise())
            {
                ss.detectEmotionInText(sTextToTranslate.trim());
                System.out.println(ss.getOriginalText());
                System.out.println("was tagged as:");
                System.out.println(ss.getTaggedText());
                System.out.println((new StringBuilder("Positive sentiment of text: ")).append(ss.getPositiveClassification()).append(", negative: ").append(ss.getNegativeClassification()).toString());
            }
        }
    }
}
