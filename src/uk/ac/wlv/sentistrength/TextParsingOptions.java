// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   TextParsingOptions.java

package uk.ac.wlv.sentistrength;


/**
 * 文本语法分析选项
 */
public class TextParsingOptions
{

    public boolean bgIncludePunctuation;
    public int igNgramSize;
    public boolean bgUseTranslations;
    public boolean bgAddEmphasisCode;

    /**
     * TextParsingOptions构造函数
     */
    public TextParsingOptions()
    {
        bgIncludePunctuation = true;
        igNgramSize = 1;
        bgUseTranslations = true;
        bgAddEmphasisCode = false;
    }
}
