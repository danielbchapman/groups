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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An item is simply a piece of information that can be serialized and searched. It 
 * simply allows the user to search by a field (string) and behaves similarly to JSON in 
 * that if a piece of data does not exist it return 
 *
 ***************************************************************************
 * @author Daniel B. Chapman 
 * @link http://www.danielbchapman.com
 * @link https://github.com/danielbchapman/groups/
 ***************************************************************************
 */
public class Item implements Serializable, Comparable<Item>
{
  private static final long serialVersionUID = 1L;

  /**
   * A simple compareTo call that handles nulls without issue
   * @param one the first object
   * @param two the second object to compare against the first
   * @return -1,0,1  
   * 
   */
  public static <T extends Comparable<T>> int compareToNullSafe(T one, T two)
  {
    if (one == null && two != null)
      return 1;

    if (two == null && one != null)
      return -1;

    if (one == null && two == null)
      return 0;

    return one.compareTo(two);
  }
  /**
   * @param fields the fields to compare by
   * @return a comparitor for these objects
   * 
   */
  // FIXME Java Doc Needed
  public static Comparator<Item> getComparitor(String[] fields)
  {
    return new ItemComparator(fields);
  }

  /**
   * <JavaDoc>
   * @param fields
   * @param ignore
   * @return <Return Description>  
   * 
   */
  // FIXME Java Doc Needed
  public static Comparator<Item> getComparitor(String[] fields, String ignore)
  {
    ItemComparator comp = (ItemComparator) getComparitor(fields);
    comp.setIgnore(ignore);
    return comp;
  }

  private HashMap<String, JSON> data = new HashMap<String, JSON>();

  private String id;

  public Item()
  {
    this(null, null, null);
  }

  public Item(String id)
  {
    this(id, null, null);
  }

  public Item(final String id, final Map<String, JSON> values)
  {
    this.id = id;

    Map<String, JSON> unmod = Collections.unmodifiableMap(values);

    for (String key : unmod.keySet())
      setValue(key, unmod.get(key));
  }

  public Item(String id, String[] fields, JSON[] values)
  {
    this.id = id;

    if (fields == null)
      return;

    if (fields != null && values != null && (fields.length == values.length))
      for (int i = 0; i < fields.length; i++)
        setValue(fields[i], values[i] == null ? JSON.NULL : values[i]);
    else
      throw new IllegalArgumentException("The object could not be constructed due to mismatched parameters");
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Item item)
  {
    if (item == null)
      return 1;

    if (getId() == null && item.getId() == null)
      return 0;

    if (getId() == null)
      return -1;

    if (item.getId() == null)
      return 1;

    return getId().compareTo(item.getId());
  }

  /**
   * @return a copy of the object so the original object is not replaced unless a put 
   * command is issued.  
   * 
   */
  public Item copy()
  {
    Item ret = new Item();
    ret.setId(getId());

    for (String key : getKeys())
      ret.setValue(key, getValue(key));

    return ret;
  }

  public boolean equals(Object obj)
  {
    Item item = null;
    if (obj instanceof Item)
      item = (Item) obj;

    if (item == null)
      return false;

    if (!item.id.equals(id))
      return false;

    for (String key : item.getKeys())
      if (Item.compareToNullSafe(item.getValue(key), getValue(key)) != 0)
        return false;

    return true;
  }

  public String getId()
  {
    return id;
  }

  /**
   * @return an unmodifiable set set of all keys for the data in this item. 
   * 
   */
  public Set<String> getKeys()
  {
    return Collections.unmodifiableSet(data.keySet());
  }

  public JSON getValue(final String field)
  {
    if (id == null)
      return null;

    if (data.containsKey(field))
    {
      JSON ret = data.get(field);
      if (ret == null)
        return JSON.NULL;

      return ret;
    }
    else
      return JSON.UNDEFINED;
  }

  public Map<String, JSON> getValues()
  {
    return Collections.unmodifiableMap(data);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    // Use the ID
    return id.hashCode();
  }

  public void removeKey(String field)
  {
    data.remove(field);
  }
  
  public void setId(String id)
  {
    this.id = id;
  }

  public void setValue(String field, Object value)
  {
    if (value == null)
      data.put(field, JSON.NULL);
    else
      data.put(field, new JSON(value));
  }

  /**
   * A null safe setter that when passed null (as opposed to JSON.NULL or UNDEFINED
   * will pass over a field. This allows methods to have a more dynamic signature.
   * @param field the field to set
   * @param value the value to set to, if null it will be ignored (unchanged).  
   * 
   */
  public void setValueIgnore(String field, Object value)
  {
    if (value == null)
      return;
    else
      setValue(field, value);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    StringBuilder build = new StringBuilder();
    build.append("Item [");
    build.append(id);
    build.append("]");

    for (String key : data.keySet())
    {
      build.append("{\"");
      build.append(key);
      build.append("\" : ");
      build.append(data.get(key).toString());
      build.append("}");
    }

    return build.toString();
  }

  public static class ItemComparator implements Comparator<Item>
  {

    private String[] fields;
    private String ignore;

    public ItemComparator(String[] fields)
    {
      if (fields == null)
        this.fields = new String[0];
      else
        this.fields = fields;
    }

    public int compare(Item a, Item b)
    {
      if (a == null && b == null)
        return 0;

      if (a == null)
        return -1;

      if (b == null)
        return 1;

      if (fields.length == 0)
        return a.compareTo(b);

      int ret = 0;

      for (int i = 0; i < fields.length; i++)
      {
        if (fields[i].equals(ignore))
          continue;

        ret = a.getValue(fields[i]).compareTo(b.getValue(fields[i]));
        if (ret != 0)
          return ret;
      }

      return ret;
    }

    public void setIgnore(String ignore)
    {
      this.ignore = ignore;
    }

  }
}
