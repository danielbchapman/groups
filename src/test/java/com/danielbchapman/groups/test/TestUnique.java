package com.danielbchapman.groups.test;

import java.util.Set;

import com.danielbchapman.groups.Group;
import com.danielbchapman.groups.Groups;
import com.danielbchapman.groups.Item;
import com.danielbchapman.groups.JSON;

public class TestUnique
{
  public static void main(String ... args)
  {
    Group test = Groups.getGroup("test");

    final String FIELD = "field";
    for (int i = 1; i < 30; i++)
    {
      Item tmp = new Item();
      if((i % 3) == 0)
      {
        tmp.setValue(FIELD, null);
      }
      
      if((i % 2) == 0)
      {
        tmp.setValue(FIELD, "value" + 2);
      }
      
      if((i % 5) == 0)
      {
        tmp.setValue(FIELD, "value" + 1);
      }
      test.put(tmp);
    }
    
    System.out.println("SET=============================");
    for(Item i : test.all())
      System.out.println(i);
    
    
    System.out.println();
    System.out.println();
    System.out.println("UNIQUE=============================");
    
    Set<JSON> unique = test.unique(FIELD);
    
    for(JSON j : unique)
      System.out.print("[" + j + "] ");
    
    
  }
}
