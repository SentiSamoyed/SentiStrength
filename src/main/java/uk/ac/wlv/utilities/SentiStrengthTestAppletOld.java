// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   SentiStrengthTestAppletOld.java

package uk.ac.wlv.utilities;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Referenced classes of package uk.ac.wlv.utilities:
//            SentiStrengthOld

public class SentiStrengthTestAppletOld extends Applet
    implements ActionListener
{

    private static final long serialVersionUID = 0x858280b1L;
    Font fntTimesNewRoman;
    String sgEnteredText;
    TextField tfField;
    String sgWordList[];
    SentiStrengthOld ss;
    boolean bgSentiStrengthOK;

    public SentiStrengthTestAppletOld()
    {
        fntTimesNewRoman = new Font("TimesRoman", 1, 36);
        sgEnteredText = "";
        tfField = new TextField(12);
        bgSentiStrengthOK = false;
    }

    public void init()
    {
        ss = new SentiStrengthOld();
        bgSentiStrengthOK = ss.initialise();
        setBackground(Color.lightGray);
        tfField.addActionListener(this);
        add(tfField);
    }

    public void paint(Graphics g)
    {
        g.setFont(fntTimesNewRoman);
        if(bgSentiStrengthOK)
        {
            g.drawString("sentiStrength successfully initialised", 100, 75);
        } else
        {
            g.drawString("Error - can't initalise sentiStrength", 100, 75);
            g.drawString(ss.getErrorLog(), 100, 125);
        }
        if(sgEnteredText != "")
            if(sgEnteredText.indexOf("\\") >= 0)
            {
                if(ss.classifyAllTextInFile(sgEnteredText, (new StringBuilder(String.valueOf(sgEnteredText))).append("_output.txt").toString()))
                    g.drawString("No problem with text file classification", 10, 275);
                else
                    g.drawString("Text file classification failed", 10, 275);
            } else
            {
                ss.detectEmotionInText(sgEnteredText);
                g.drawString(ss.getOriginalText(), 10, 225);
                g.drawString("was tagged as:", 100, 275);
                g.drawString(ss.getTaggedText(), 10, 325);
                g.drawString((new StringBuilder("Positive sentiment of text: ")).append(ss.getPositiveClassification()).append(", negative: ").append(ss.getNegativeClassification()).toString(), 10, 375);
            }
    }

    public void actionPerformed(ActionEvent e)
    {
        sgEnteredText = tfField.getText();
        repaint();
    }
}
