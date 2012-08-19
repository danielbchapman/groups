package com.danielbchapman.collection.tests;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import com.danielbchapman.collections.Group;
import com.danielbchapman.collections.InstructionType;
import com.danielbchapman.collections.Item;
import com.danielbchapman.collections.JSON;
import com.danielbchapman.collections.Groups;
import com.danielbchapman.collections.SubGroup;

public class TestCollection
{
  final static String ITERATION = "iteration";
  
  public static void main(String... args)
  {
    Group test = Groups.getGroup("test");

    for (int i = 0; i < 30; i++)
    {
      Item tmp = new Item();
      if (i % 2 == 0)
        tmp.setValue("field1", "String[" + i + "]");
      else
        tmp.setValue("field1", i);

      tmp.setValue("iteration", i);
      if (i % 4 == 0)
        tmp.setValue("search", "the");
      if (i % 4 == 1)
        tmp.setValue("search", "brown");
      if (i % 4 == 2)
      {
        tmp.setValue("search", "lazy");
        tmp.setValue("bogus", "bogusValue " + i);
      }
      if (i % 4 == 3)
        tmp.setValue("search", "fox");
      test.put(tmp);
    }
    System.out.println("//===================== TEST ===================");
    System.out.println(test.print());

    System.out.println("//===================== BY ID ===================");
    System.out.println("searching for '2' ");
    Item byId = test.find("2");
    System.out.println(byId);

    System.out.println("//===================== SEARCH ===================");
    printSearch(test, "iteration", new JSON(2), InstructionType.LESS_THAN);
    printSearch(test, "iteration", new JSON(2), InstructionType.LESS_THAN_EQUAL_TO);
    printSearch(test, "iteration", new JSON(28), InstructionType.GREATER_THAN);
    printSearch(test, "iteration", new JSON(28), InstructionType.GREATER_THAN_EQUAL_TO);
    printSearch(test, "iteration", new JSON(16), InstructionType.EQUAL);
    printSearch(test, "iteration", new JSON(88), InstructionType.EQUAL);
    printSearch(test, "iteration", null, InstructionType.EQUAL);
    printSearch(test, "iteration", JSON.UNDEFINED, InstructionType.EQUAL);
    
    System.out.println("//===================== CHAINABLE API ===================");
    
    test
      .greaterThan(ITERATION, 10)
      .chainLog("Greater than 10")
      .lessThan(ITERATION, 20)
      .chainLog("Less Than 20 and Greater than 10")
      .lessThan(ITERATION, 5)
      .notNull("bogus")
      .chainLog("Less Than 20 AND Greater than 10 AND less than 5 (hint, zero)");
    
    SubGroup subset = test
        .greaterThan(ITERATION, 10)
        .lessThan(ITERATION, 20);
    
    subset
      .contains("search", "f")
      .chainLog("contains('f') 10 -> 20");
    
    subset
      .doesNotContain("search", "f")
      .chainLog("doesNotContains('f') 10 -> 10");
    
    System.out.println("//===================== SORT ===================");
    Set<Item> search = test.findSet("iteration", new JSON("20"), InstructionType.GREATER_THAN);
    List<Item> sort = Group.sort(search, new String[]{"search", "iteration"});
    print(sort);
    
    System.out.println("//===================== REMOVE ===================");
    System.out.println("test.remove(test.greaterThan(\"iteration\", 4));");
    test.remove(test.greaterThan("iteration", 4));
    System.out.println(test.print());
    
    System.out.println("//===================== SAVE/READ ===================");
    File target = new File("test/save/");
    test.save(target);
    try
    {
      Group read = Group.read(target, "test");
      System.out.println("Reading collection....");
      System.out.println(read.print());
    }
    catch (FileNotFoundException e)
    {
      System.out.println("KABOOM");
      e.printStackTrace();
    } 
  //Cleanup...
    System.out.println("Deleted? " + test.delete(target));
    
  }
  
  public static void printSearch(Group collection, String field, JSON value, InstructionType type)
  {
    long start = System.nanoTime();
    Set<Item> items = collection.findSet(field, value, type);
    long end = System.nanoTime();
    
    System.out.println("\nsearching for " + field + " " + value + " with instruction " + type + " in " + (end - start) + " nanos");
    if (items == null)
      System.out.println("null set");
    else
      for (Item i : items)
        System.out.println(i);    
  }
  
  public static void print(java.util.Collection<Item> items)
  {
    for(Item i : items)
      System.out.println(i);
  }
  
  public static void print(SubGroup group)
  {
    group.print();
  }
}
