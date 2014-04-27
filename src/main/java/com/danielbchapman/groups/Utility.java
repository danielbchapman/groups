package com.danielbchapman.groups;

import java.util.Arrays;

public class Utility
{
  /**
   * <p>
   * A comparison function that orders integers embedded in strings ahead of letters.
   * This function also does some utility like using a lower-case comparison as well 
   * as removing whitespace that might affect the sort.
   * </p> 
   * Examples:
   * <pre>
   * <tt>'12abc' &gt '2abc'</tt> (2 is less than 12)
   * <tt>'abc3asf' &gt 'abc111asd'</tt> (3 is less than 111)
   * <tt>'ABC123' == '   abc123   '</tt> (leading whitespace and all capitalization are ignored)
   * </pre>
   * @param one string one
   * @param two string two
   * @return 1 if one &gt; two, -1 if one &lt; two and 0 if equal.   
   * 
   */
  public final static int compareStringNumericData(final String one, final String two)
  {
    if(one == null && two == null)
      return 0;
    
    if(one == null && two != null)
      return -1;
    
    if(one != null && two == null)
      return 1;
    
    String a = one.trim().toLowerCase();
    String b = two.trim().toLowerCase();
    
    if(a.equals(b))
      return 0;
    
    if(a.isEmpty() && !b.isEmpty())
      return -1;
    
    if(b.isEmpty() && !a.isEmpty())
      return 1;
    
    char[] oneC = a.toCharArray();
    char[] twoC = b.toCharArray();
    
    int i = 0;
    int j = 0;
    
    while(true)
    {
      if(i >= oneC.length || j >= twoC.length)
      {
        if(i >= oneC.length && j >= twoC.length)
          return 0;
        
        if(i >= oneC.length)
          return -1;
        else
          return 1;
      }
      
      if(oneC[i] != twoC[j])
      {
        int forwardOne = lookahead(oneC, i);
        int forwardTwo = lookahead(twoC, j);
        
        /* NUMBERS!!! */
        if(forwardOne - i != 0 || forwardTwo -i != 0)
        {
          if(forwardOne - i != 0 && forwardTwo -i != 0)
          {
            char[] numOne = Arrays.copyOfRange(oneC, i, forwardOne);
            char[] numTwo = Arrays.copyOfRange(twoC, j, forwardTwo);
            
            try
            {
              Integer i1 = Integer.valueOf(String.valueOf(numOne));
              Integer i2 = Integer.valueOf(String.valueOf(numTwo));
              
              int comp = Integer.compare(i1, i2);
              if(comp == 0)
              {
                i = forwardOne + 1;
                j = forwardTwo + 1;
              }
              else
                return comp;
            }
            catch(NumberFormatException e)
            {
              throw new RuntimeException(e.getMessage(), e); //We all make mistakes, but this shouldn't happen
            }
          }
          else
            /* The first contains a number and is by default FIRST */
            if(forwardOne - i != 0)
              return 1;
            else
              return -1;
        }
        else
          return Character.compare(oneC[i], twoC[j]);
      }
      else
      {
        i++;
        j++;        
      }
    }
  }
  
  private static boolean intCheck(char c)
  {
    switch(c)
    {
//      case '.':
//      case ',':
//        return false;
//        
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return true;
        
      default:
        return false;
    }
  }
  
  /**
   * @param string the characters to search
   * @param start the start index of that search
   * @return the index of the last numeric
   */
  private static int lookahead(char[] string, int start)
  { 
    if(!intCheck(string[start]))
      return start;
      
    for(int i = start + 1; i < string.length; i++)
    {
      if(!intCheck(string[i]))
        return i - 1;
    }
    
    return string.length - 1;
  }
  
}
