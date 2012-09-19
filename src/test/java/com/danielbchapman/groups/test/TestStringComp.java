package com.danielbchapman.groups.test;

import com.danielbchapman.groups.Utility;

public class TestStringComp
{
  public static void main(String ... args)
  {
    System.out.println("Consider a more robust test...");
    int i = 1;
    System.out.println(i++ + " | " + Utility.compareStringNumericData("abcdef", "ABCDEF"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("abcdef", "abcdef"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12abcdef", "2ABCDEF"));
    
    System.out.println(i++ + " | " + Utility.compareStringNumericData("2abcdef", "12ABCDEF"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("", "1"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("1", "1"));
    
    System.out.println(i++ + " | " + Utility.compareStringNumericData("123", "12-3"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12-3", "123"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("1-23", "12-3"));
  }
}
