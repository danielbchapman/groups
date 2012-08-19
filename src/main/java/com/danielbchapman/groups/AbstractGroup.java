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
package com.danielbchapman.groups;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * The AbstractGroup is a container for the methods used in Group and SubGroup respectively. This
 * class provides all information implementations except the IO to disk or other
 * specializations.
 * </p>
 * <p>
 * This is an early implementation and the contract is likely to change.
 * </p> 
 *
 ***************************************************************************
 * @author Daniel B. Chapman 
 * @link http://www.danielbchapman.com
 * @link https://github.com/danielbchapman/groups/
 ***************************************************************************
 */
public abstract class AbstractGroup implements Serializable
{
  private static final long serialVersionUID = 1L;

  public static void print(java.util.Collection<Item> collection)
  {
    if (collection != null)
    {
      for (Item i : collection)
        System.out.println(i);
    }
    else
      System.out.println("Null collection: ");
  }

  public static Group read(File directory, String name) throws FileNotFoundException
  {
    File toRead = new File(directory.getAbsolutePath() + File.separator + name);
    if (!toRead.getParentFile().exists())
      throw new FileNotFoundException("The file " + toRead + " does not exist");
    else
    {
      try
      {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(toRead));
        Group ret = (Group) in.readObject();
        in.close();
        Groups.setGroup(ret);
        return ret;
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
        return Groups.getGroup(name);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return Groups.getGroup(name);
      }
      catch (ClassNotFoundException e)
      {
        throw new RuntimeException("SEVERE: Build problem " + e.getMessage(), e);
      }
    }
  }

  public static List<Item> sort(AbstractGroup group, String ... fields)
  {
    return sort(group.getAllItems(), fields);
  }

  public static List<Item> sort(java.util.Collection<Item> items, String... fields)
  {
    if (items instanceof List)
    {
      Collections.sort((List<Item>) items, Item.getComparitor(fields));
      return (List<Item>) items;
    }
    else
    {
      ArrayList<Item> ret = new ArrayList<Item>();
      for (Item i : items)
        ret.add(i);

      Collections.sort(ret, Item.getComparitor(fields));
      return ret;
    }
  }

  int count = 0;
  
  private HashMap<String, Item> ids = new HashMap<String, Item>();

  private BigInteger nextId = BigInteger.ZERO;

  // HashMap [field/set<id>]
  // private HashMap<String, JSON[][]> indecies = new HashMap<String, JSON[][]>();

  public AbstractGroup()
  {
  }

  public AbstractGroup(java.util.Collection<Item> items)
  {
    for (Item i : items)
      put(i);
  }

  public AbstractGroup(java.util.Set<Item> set)
  {
    for (Item i : set)
      put(i);
  }

  public SubGroup contains(String field, String value)
  {
    return wrap(containsSet(field, value));
  }

  public Set<Item> containsSet(String field, String value)
  {
    return findSet(field, JSON.wrap(value), InstructionType.CONTAINS);
  }
  
  public SubGroup doesNotContain(String field, String value)
  {
    return wrap(doesNotContainSet(field, value));
  }
  
  public Set<Item> doesNotContainSet(String field, String value)
  {
    return findSet(field, JSON.wrap(value), InstructionType.DOES_NOT_CONTAIN);
  }
  
  public SubGroup equal(String name, JSON value)
  {
    return new SubGroup(equalSet(name, value), getName());
  }

  public SubGroup equal(String name, Object value)
  {
    return new SubGroup(equalSet(name, value), getName());
  }

  public Set<Item> equalSet(String name, JSON value)
  {
    return findSet(name, value, InstructionType.EQUAL);
  }

  public Set<Item> equalSet(String name, Object value)
  {
    return findSet(name, JSON.wrap(value), InstructionType.EQUAL);
  }

  /**
   * Find an element in the collection by ID.
   * @param id
   * @return a copy of the object with the id specified.
   * 
   */
  public Item find(String id)
  {
    Item find = ids.get(id);
    return find == null ? null : find.copy();
  }
  
  /**
   * Find a set of items and wrap them into a SubGroup for chainable API
   * @see #findSet(String, JSON, InstructionType)
   * @param field the field to search
   * @param value the value (Number/String/Boolean etc...), if you pass garbage you will get garbage.
   * @param instruction the instruction for comparison
   * @return A SubGroup of the set that was searched representing the results.
   * @See {@link #findSet(String, JSON, InstructionType)} 
   */
  public SubGroup find(String field, JSON json, InstructionType type)
  {
    return new SubGroup(findSet(field, json, type), getName());
  }
  
  /**
   * Find a set of items and wrap them into a SubGroup for chainable API
   * @see #findSet(String, JSON, InstructionType)
   * @param field the field to search
   * @param value the value (Number/String/Boolean etc...), if you pass garbage you will get garbage.
   * @param instruction the instruction for comparison
   * @return A SubGroup of the set that was searched representing the results.
   * @See {@link #find(String, JSON, InstructionType)}
   */
  public SubGroup find(String field, Object value, InstructionType type)
  {
    return find(field, JSON.wrap(value), type);
  }

  public SubGroup find(String[] strings, JSON[] jsons, InstructionType[] instructions)
  {
    return new SubGroup(findSet(strings, jsons, instructions), getName());
  }

  /**
   * Find an element in the collection by a query.
   * @param field the field to search
   * @param value the value of that field
   * @param instruction the instruction to use (==, !=, etc...)
   * @return A Set&lt;Item&gt; of the items with the provided instruction
   * 
   */
  public Set<Item> findSet(String field, JSON value, InstructionType instruction)
  {
    if (value == null)
      value = JSON.NULL;

    if (instruction == null)
      instruction = InstructionType.EQUAL;

    HashSet<Item> find = new HashSet<Item>(64); // Seems reasonable
    // TODO Do we have a synchronization problem?
    for (Item i : ids.values())
    {
      JSON compare = i.getValue(field);
      if (compare == null)
        compare = JSON.NULL;
      int result = compare.compareTo(value);
      switch (instruction)
      {
      case EQUAL:
        if (result == 0)
          find.add(i.copy());
        break;

      case NOT_EQUAL:
        if (result != 0)
          find.add(i.copy());
        break;
        
      case LESS_THAN:
        if (result < 0)
          find.add(i.copy());
        break;
      case GREATER_THAN:
        if (result > 0)
          find.add(i.copy());
        break;
        
      case LESS_THAN_EQUAL_TO:
        if (result <= 0)
          find.add(i.copy());
        break;
        
      case GREATER_THAN_EQUAL_TO:
        if (result >= 0)
          find.add(i.copy());
        break;
        
      case CONTAINS:
        if(result == 0)
        {
          find.add(i.copy());
          break;
        }
          
        if (!compare.isNullOrUndefined() && compare.getType().equals(JSONType.STRING))
          if(compare.getRegexSafeString().contains(value.getRegexSafeString()))
            find.add(i.copy());
        break;
      case DOES_NOT_CONTAIN:
        if(result == 0)
          break;
        
        if (compare.isNullOrUndefined() || !compare.getType().equals(JSONType.STRING))
          break;
        else
          if(!compare.getRegexSafeString().contains(value.getRegexSafeString()))
            find.add(i.copy());
        break;
      }
    }

    return find;
  }

  /**
   * @param field the field to search
   * @param value the value (Number/String/Boolean etc...), if you pass garbage you will get garbage.
   * @param instruction the instruction for comparison
   * @return A Set&lt;Item&gt; of the items with the provided instruction
   * @See {@link #findSet(String, JSON, InstructionType)} 
   * 
   */
  public Set<Item> findSet(String field, Object value, InstructionType instruction)
  {
    return findSet(field, JSON.wrap(value), instruction);
  }

  /**
   * 
   * This method is a batch operation on the find providing a union of 
   * the sets.
   * @see #find(String, JSON, InstructionType)
   * @param fields the fields to search
   * @param values the values for those fields
   * @param instructions the instructions to compare those values
   * @return The resulting set of items
   * @throws IllegalArgumentException if the array sets do not match in argument.
   */
  @SuppressWarnings("unchecked")
  public Set<Item> findSet(String[] fields, JSON[] values, InstructionType[] instructions)
  {
    if (fields == null)
      return null;

    if (values == null)
      return null;

    if (instructions == null)
      return null;

    if (fields.length != values.length && values.length != instructions.length)
      throw new IllegalArgumentException("Fields must match values length fields[" + fields + "] values[ " + values + "] instructions[" + instructions + "]");

    Set<Item>[] sets = new Set[fields.length];
    for (int i = 0; i < fields.length; i++)
      sets[i] = findSet(fields[i], values[i], instructions[i]);

    for (int i = 0; i < sets.length; i++)
      if (sets[i] == null || sets[i].size() == 0)
        return new HashSet<Item>(); // No union

    HashSet<Item> merge = null;
    for (int i = 0; i < sets.length; i++)
    {
      if (merge == null)
        merge = (HashSet<Item>) sets[i];
      else
        merge.retainAll(sets[i]);

      if (merge.size() == 0)
        return new HashSet<Item>(); // We found nothing.
    }

    return merge;
  }

  /**
   * Gets all the items in the collection
   * @return a copy of this collection top to bottom (no order is guarenteed)
   * 
   */
  public ArrayList<Item> getAllItems()
  {
    ArrayList<Item> ret = new ArrayList<Item>();

    for (Item i : ids.values())
      ret.add(i.copy());

    return ret;
  }

  /**
   * @return the name of this Group 
   * 
   */
  public abstract String getName();

  /**
   * @param field the field to search
   * @param limitBottom the limit on the bottom.
   * @return A set for these parameters
   * 
   */
  public SubGroup greaterThan(String field, JSON limitBottom)
  {
    return new SubGroup(findSet(field, limitBottom, InstructionType.GREATER_THAN), getName());
  }

  public SubGroup greaterThan(String field, Object limitBottom)
  {
    return greaterThan(field, JSON.wrap(limitBottom));
  }

  /**
   * @param field the field to search
   * @param limitBottom the limit on the bottom.
   * @return A set for these parameters
   * 
   */
  public SubGroup greaterThanAndEqualTo(String field, JSON limitBottom)
  {
    return new SubGroup(findSet(field, limitBottom, InstructionType.GREATER_THAN_EQUAL_TO), getName());
  }

  /**
   * @param field the field to search
   * @param limitBottom the limit on the bottom.
   * @return A set for these parameters
   * 
   */
  public SubGroup greaterThanAndEqualTo(String field, Object limitBottom)
  {
    return greaterThanAndEqualTo(field, JSON.wrap(limitBottom));
  }

  /**
   * @param field the field to search
   * @param limitBottom the limit on the bottom.
   * @return A set for these parameters
   * 
   */
  public Set<Item> greaterThanAndEqualToSet(String field, JSON limitBottom)
  {
    return findSet(field, limitBottom, InstructionType.GREATER_THAN_EQUAL_TO);
  }

  /**
   * @param field the field to search
   * @param limitBottom the limit on the bottom.
   * @return A set for these parameters
   * 
   */
  public Set<Item> greaterThanAndEqualToSet(String field, Object limitBottom)
  {
    return greaterThanAndEqualToSet(field, JSON.wrap(limitBottom));
  }

  /**
   * Return a set of items greaterThan the field specified and the bottom
   * limit.
   * @param field the field to search
   * @param limitBottom the limit on the bottom.
   * @return A set for these parameters
   * 
   */
  public Set<Item> greaterThanSet(String field, JSON limitBottom)
  {
    return findSet(field, limitBottom, InstructionType.GREATER_THAN);
  }

  /**
   * Return a set of items greaterThan the field specified and the bottom
   * limit.
   * @param field the field to search
   * @param limitBottom the limit on the bottom.
   * @return A set for these parameters
   * 
   */
  public Set<Item> greaterThanSet(String field, Object limitBottom)
  {
    return greaterThanSet(field, JSON.wrap(limitBottom));
  }

  /**
   * @param field the field to search
   * @return a set of objects where the field is false/null/undefined
   * 
   */
  public SubGroup isFalse(String field)
  {
    return wrap(isFalseSet(field));  
  }

  /**
   * @param field the field to search
   * @return a set of objects where the field is false/null/undefined
   * 
   */
  public Set<Item> isFalseSet(String field)
  {
    return findSet(
        new String[]{field, field}, 
        new JSON[]{JSON.FALSE, JSON.NULL}, 
        new InstructionType[]{InstructionType.EQUAL, InstructionType.EQUAL}
        );
  }
  
  public SubGroup isNull(String field)
  {
    return new SubGroup(isNullSet(field), getName());
  }
  public Set<Item> isNullSet(String field)
  {
    return findSet(field, JSON.NULL, InstructionType.EQUAL);
  }

  /**
   * @param field the field to search
   * @return where the field is true or has a positive value that is not NULL or UNDEFINED
   * 
   */
  public SubGroup isTrue(String field)
  {
    return wrap(isTrueSet(field));
  }
  
  /**
   * @param field the field to search
   * @return where the field is true or has a positive value that is not NULL or UNDEFINED
   * 
   */
  public Set<Item> isTrueSet(String field)
  {
    return findSet(
        new String[]{field, field}, 
        new JSON[]{JSON.TRUE, JSON.NULL}, 
        new InstructionType[]{InstructionType.EQUAL, InstructionType.NOT_EQUAL}        
        );
  }
  
  /**
   * @param field the field to search
   * @param limitBottom the limit on the top.
   * @return A set for these parameters
   * 
   */
  public SubGroup lessThan(String field, JSON limitTop)
  {
    return new SubGroup(findSet(field, limitTop, InstructionType.LESS_THAN), getName());
  }
  
  /**
   * @param field the field to search
   * @param limitBottom the limit on the top.
   * @return A set for these parameters
   * 
   */
  public SubGroup lessThan(String field, Object limitTop)
  {
    return lessThan(field, JSON.wrap(limitTop));
  }
  /**
   * @param field the field to search
   * @param limitBottom the limit on the top.
   * @return A set for these parameters
   * 
   */
  public SubGroup lessThanAndEqualTo(String field, JSON limitTop)
  {
    return new SubGroup(findSet(field, limitTop, InstructionType.LESS_THAN_EQUAL_TO), getName());
  }
  
  /**
   * @param field the field to search
   * @param limitBottom the limit on the top.
   * @return A set for these parameters
   * 
   */
  public SubGroup lessThanAndEqualTo(String field, Object limitTop)
  {
    return lessThanAndEqualTo(field, JSON.wrap(limitTop));
  }
  
  /**
   * @param field the field to search
   * @param limitBottom the limit on the top.
   * @return A set for these parameters
   * 
   */
  public Set<Item> lessThanAndEqualToSet(String field, JSON limitTop)
  {
    return findSet(field, limitTop, InstructionType.LESS_THAN_EQUAL_TO);
  }
  
  /**
   * @param field the field to search
   * @param limitBottom the limit on the top.
   * @return A set for these parameters
   * 
   */
  public Set<Item> lessThanAndEqualToSet(String field, Object limitTop)
  {
    return lessThanAndEqualToSet(field, JSON.wrap(limitTop));
  }
  
  /**
   * @param field the field to search
   * @param limitBottom the limit on the top.
   * @return A set for these parameters
   * 
   */
  public Set<Item> lessThanSet(String field, JSON limitTop)
  {
    return findSet(field, limitTop, InstructionType.LESS_THAN);
  }
  
  /**
   * @param field the field to search
   * @param limitBottom the limit on the top.
   * @return A set for these parameters
   * 
   */
  public Set<Item> lessThanSet(String field, Object limitTop)
  {
    return lessThanSet(field, JSON.wrap(limitTop));
  }
  
  public synchronized void merge(final Item item, final Map<String, JSON> values)
  {
    Map<String, JSON> unmod = Collections.unmodifiableMap(values);
    for(String key : unmod.keySet())
      item.setValue(key, unmod.get(key));
    
    put(item);
  }
  
  public synchronized void merge(final String id, final Map<String, JSON> values)
  {
    Item toFind = find(id);
    if(toFind != null)
      merge(toFind, values);
    else
      put(new Item(id, values));
  }
  
  public SubGroup notEqual(String field, JSON value)
  {
    return new SubGroup(notEqualSet(field, value), getName());
  }
  public SubGroup notEqual(String field, Object value)
  {
    return new SubGroup(notEqualSet(field, JSON.wrap(value)), getName());
  }
  public Set<Item> notEqualSet(String field, JSON value)
  {
    return findSet(field, value, InstructionType.NOT_EQUAL);
  }
  public Set<Item> notEqualSet(String field, Object value)
  {
    return findSet(field, value, InstructionType.NOT_EQUAL);
  }
  public SubGroup notNull(String field)
  {
    return new SubGroup(notNullSet(field), getName());
  }
  public Set<Item> notNullSet(String field)
  {
    return findSet(field, JSON.NULL, InstructionType.NOT_EQUAL);
  }
  public synchronized void put(Item item)
  {
    if (item.getId() == null)
      item.setId(getNextId().toString());

    ids.put(item.getId(), item);
    count++;
  }
  public void remove(AbstractGroup group)
  {
    for (Item i : group.getAllItems())
      remove(i);
  }

  public synchronized void remove(Item item)
  {
    ids.remove(item.getId());
    count--;
  }

  public void remove(Item... items)
  {
    if (items != null && items.length > 0)
      for (Item i : items)
        remove(i);
  }

  public void remove(final java.util.Collection<Item> items)
  {
    for (Item i : items)
      remove(i);
  }

  public void remove(final Set<Item> items)
  {
    for (Item i : items)
      remove(i);
  }

  public List<Item> sort(String ... fields)
  {
    return AbstractGroup.sort(this, fields);
  }
  
  public synchronized void stomp(final String id, final Map<String, JSON> values)
  {
    put(new Item(id, values));
  }

  private synchronized BigInteger getNextId()
  {
    nextId = nextId.add(BigInteger.ONE);
    return nextId;
  }

  private SubGroup wrap(Set<Item> items)
  {
    return new SubGroup(items, getName());
  }
}

