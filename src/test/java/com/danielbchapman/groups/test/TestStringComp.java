package com.danielbchapman.groups.test;

import com.danielbchapman.groups.Utility;

public class TestStringComp
{
  public static void main(String ... args)
  {
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

    System.out.println("=======================TESTS");
    System.out.println(i++ + " | " + Utility.compareStringNumericData("13", "13"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("13", "12B"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("13", "12A"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("13", "12"));
    
    System.out.println("=======================TESTS");
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12B", "13"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12B", "12B"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12B", "12A"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12B", "12"));
    
    System.out.println("=======================TESTS");
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12", "13"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12", "12B"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12", "12A"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12", "12"));
//    
    System.out.println("=======================TESTS");
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12a", "13"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12a", "12B"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12a", "12A"));
    System.out.println(i++ + " | " + Utility.compareStringNumericData("12a", "12"));
  }
}
