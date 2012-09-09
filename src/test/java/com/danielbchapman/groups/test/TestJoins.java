package com.danielbchapman.groups.test;

import com.danielbchapman.groups.Group;
import com.danielbchapman.groups.Groups;
import com.danielbchapman.groups.Item;
import com.danielbchapman.groups.SubGroup;

public class TestJoins
{
  final static String AAA = "aaa";
  final static String BBB = "bbb";
  final static String CCC = "ccc";
  final static String DDD = "ddd";
  final static String JOIN = "join_id";
  final static String INDEX = "index";
  final static String TARGET = "target";
  final static String VALUE_ONE = "ONE";
  final static String VALUE_TWO = "TWO";
  final static String VALUE_THREE = "THREE";
  
  
  public static void main(String ... args)
  {
    Group one = Groups.getGroup("master");
    Group two = Groups.getGroup("conditional");
    

    for(int i = 0; i < 30; i++)
    {
      Item tmp = new Item();
      tmp.setValueIgnore(AAA, "a" + i);
      tmp.setValueIgnore(BBB, "b" + i);
      tmp.setValueIgnore(CCC, "c" + i);
      if(i % 4 == 0)
        tmp.setValueIgnore(DDD, "The quick brown");
      if(i % 4 == 1)
        tmp.setValueIgnore(DDD, " fox jumps over");
      if(i % 4 == 2)
        tmp.setValueIgnore(DDD, "the lazy");
      if(i % 4 == 3)
        tmp.setValueIgnore(DDD, "dog");
      one.put(tmp);
    }
    
    {
      int i = 0;

      for(Item item : one.contains(DDD, "dog").getAllItems())
      {
        i++;
        Item tmp = new Item();
        tmp.setValueIgnore(JOIN, item.getId());
        if(i % 3 == 0)
          tmp.setValueIgnore(TARGET, VALUE_ONE);
        if(i % 3 == 1)
          tmp.setValueIgnore(TARGET, VALUE_TWO);
        if(i % 3 == 2)
          tmp.setValueIgnore(TARGET, VALUE_THREE);
        
        tmp.setValueIgnore(INDEX, "Index of " + i);
        two.put(tmp);
      }
    }
    
    {
      System.out.println("===================PRINTING JOIN DATA===================");
      System.out.println("---------GROUP ONE");
      System.out.println(one.print());
      System.out.println("---------GROUP TWO");
      System.out.println(two.print());  
    }
    
    {
      System.out.println("===================TESTING INNER JOIN===================");
      System.out.println("----two.innerJoin(JOIN, one, Item.ID, false).print());");
      SubGroup inner = two.innerJoin(JOIN, one, Item.ID, false);
      System.out.println(inner);  
    }
    
    {
      System.out.println("===================TESTING OUTER JOINS===================");
      System.out.println("----one.outerJoin(Item.ID, two, JOIN, false);");
      SubGroup outer = one.outerJoin(Item.ID, two, JOIN, false);
      System.out.println(outer);  
      
      System.out.println("===================SEARCHING OUTER FOR JOINED INFORMATION===================");
      System.out.println("----outer.contains(TARGET, VALUE_THREE)");
      System.out.println(outer.contains(TARGET, VALUE_THREE));
    }
    
    //BREAK DATA
    
    SubGroup broken = new SubGroup("broken");
    {
      int i = 0;
      for(Item item : two.getAllItems())
      {
        i++;
        item.setId(null);
        if(i % 3 == 0)
        {
          Item dup = new Item();
          for(String key : item.getKeys())
            dup.setValueIgnore(key, item.getValue(key));
          broken.put(dup);
          System.out.println("adding dup -> " + dup);
        }        
        broken.put(item);
        System.out.println("adding -> " + item);

      }
        
    }
    
    System.out.println("===================BROKEN DATA===================");
    System.out.println(broken);
    {
      try
      {
        System.out.println("\n\n===================TESTING INNER NON-STRICT EXCEPTION===================");
        System.out.println("----broken.innerJoin(JOIN, one, Item.ID, false).print());");
        SubGroup inner = broken.innerJoin(JOIN, one, Item.ID, false);
        System.out.println(inner);  
        
        System.out.println("===================TESTING INNER STRICT EXCEPTION===================");
        System.out.println("----broken.innerJoin(JOIN, one, Item.ID, true).print());");
        inner = broken.innerJoin(JOIN, one, Item.ID, true);
        System.out.println(inner);  
      }
      catch(Throwable t)
      {
        t.printStackTrace();
      }
    }
    
    {
      try
      {
        System.out.println("\n\n===================TESTING OUTER NON-STRICT EXCEPTION===================");
        System.out.println("----one.outerJoin(Item.ID, broken, JOIN, false);");
        SubGroup outer = one.outerJoin(Item.ID, broken, JOIN, false);
        System.out.println(outer);  
        
        System.out.println("===================TESTING OUTER STRICT EXCEPTION===================");
        System.out.println("----one.outerJoin(Item.ID, broken, JOIN, true);");
        outer = one.outerJoin(Item.ID, broken, JOIN, true);
        System.out.println(outer);  
      }
      catch (Throwable t)
      {
        t.printStackTrace();
      } 
    }
  }
}

