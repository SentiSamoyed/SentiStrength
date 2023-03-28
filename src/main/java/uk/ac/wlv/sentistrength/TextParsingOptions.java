// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   TextParsingOptions.java

package uk.ac.wlv.sentistrength;


/**
 * 文本语法分析选项。
 */
public class TextParsingOptions
{

    /**
     * 是否包含标点符号，默认为是。
     */
    public boolean bgIncludePunctuation;
    /**
     * n-gram 的大小，默认为1。<br/>
     * n-gram 表示连续 n 个词出现的频率，n 通常是 1，2，3。
     */
    public int igNgramSize;
    /**
     * 是否使用翻译，默认为是。
     */
    public boolean bgUseTranslations;
    /**
     * 是否加入强调码，默认为否。
     */
    public boolean bgAddEmphasisCode;

    /**
     * TextParsingOptions 构造函数。<br/>
     * 默认包含标点符号，<br/>
     * 默认 n-gram 的大小为 1，<br/>
     * 默认使用翻译，<br/>
     * 默认不加入强调码。
     */
    public TextParsingOptions()
    {
        bgIncludePunctuation = true;
        igNgramSize = 1;
        bgUseTranslations = true;
        bgAddEmphasisCode = false;
    }
}
