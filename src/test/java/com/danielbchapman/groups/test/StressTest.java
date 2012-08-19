  /******************************************************************************
  * Copyright (c) 2005-2012 Daniel B. Chapman
  * 
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  * Daniel B. Chapman - Initial APU/Implementation
  * https://github.com/danielbchapman/groups/
  *****************************************************************************/
package com.danielbchapman.groups.test;

import java.io.File;
import java.io.FileNotFoundException;

import com.danielbchapman.groups.Group;
import com.danielbchapman.groups.Groups;
import com.danielbchapman.groups.Item;

public class StressTest
{
  public static Group stress(int total, String collection)
  {
    Group stressTest = Groups.getGroup(collection);
    for(int i = 0; i < total; i++)
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
        tmp.setValue("search", "lazy");
      if (i % 4 == 3)
        tmp.setValue("search", "fox");
      
      stressTest.put(tmp);
    }
    return stressTest;
  }
  
  public static void main(String ... args) throws FileNotFoundException
  {
    runStress(1000);
    runStress(10000);
    runStress(100000);
    runStress(200000);//Out of Memory Error
  }
  
  public static void runStress(int i) throws FileNotFoundException
  {
    String name = "test@" + i;
    System.out.println("\nTesting " + i + " records");
    long start = System.nanoTime();
    Group tmp = stress(i, "test@" + i);
    long end = System.nanoTime();
    System.out.println("Created in " + (end - start) + " nanos");
    File parent = new File("tests/stress/");
    
    start = System.nanoTime();
    tmp.save(parent);
    end = System.nanoTime();
    System.out.println("Saved in " + (end - start) + " nanos");
    
    start = System.nanoTime();
    Group read = Group.read(parent, name);
    end = System.nanoTime();
    System.out.println("Read in " + (end - start) + " nanos");
  }
}
