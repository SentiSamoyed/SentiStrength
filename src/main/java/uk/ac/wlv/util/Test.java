// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 
// Source File Name:   Test.java

package uk.ac.wlv.util;

import java.net.URLEncoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 一个测试草稿类，没有用处
 */
public class Test {

  /**
   * 空构造方法
   */
  public Test() {
  }

  /**
   * Main 方法
   *
   * @param args 运行参数，未使用
   */
  public static void main(String[] args) {
    CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();

    String test = "R\351al";

    System.out.println(test + " isPureAscii() : " + asciiEncoder.canEncode(test));

    for (int i = 0; i < test.length(); i++) {
      if (!asciiEncoder.canEncode(test.charAt(i))) {
        System.out.println(test.charAt(i) + " isn't Ascii() : ");
      }
    }

    test = "Real";
    System.out.println(test + " isPureAscii() : " + asciiEncoder.canEncode(test));

    test = "a\u2665c";
    System.out.println(test + " isPureAscii() : " + asciiEncoder.canEncode(test));

    for (int i = 0; i < test.length(); i++) {
      if (!asciiEncoder.canEncode(test.charAt(i))) {
        System.out.println(test.charAt(i) + " isn't Ascii() : ");
      }
    }

    System.out.println("Encoded Word = " + URLEncoder.encode(test));
  }
}
