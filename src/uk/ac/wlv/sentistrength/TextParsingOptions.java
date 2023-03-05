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

    /**
     * 是否包含标点符号，默认为是
     */
    public boolean bgIncludePunctuation;
    /**
     * n-gram的大小，默认为1
     * n-gram表示连续n个词出现的频率，n通常是1，2，3
     */
    public int igNgramSize;
    /**
     * 是否使用翻译，默认为是
     */
    public boolean bgUseTranslations;
    /**
     * 是否加入强调码，默认为否
     */
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
