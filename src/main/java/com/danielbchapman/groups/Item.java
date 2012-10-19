/******************************************************************************
* Copyright (c) 2012- Daniel B. Chapman
* 
* --
* (file name) - a short description what it does
* Copyright (C) (2012) (Daniel B. Chapman) (chapman@danielbchapman.com)
*
* This software comes with ABSOLUTELY NO WARRANTY. For details, see
* the enclosed file COPYING for license information (AGPL). If you
* did not receive this file, see http://www.gnu.org/licenses/agpl.html.
* --
* Contributors:
* Daniel B. Chapman - Initial API/Implementation
* https://github.com/danielbchapman/groups/
*****************************************************************************/
package com.danielbchapman.groups;

import java.io.Serializable;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
  public final static String ID = "__id";
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

  private static JSON getValue(final Item item, final String field)
  {
    if(item.data.containsKey(field))
    {
      JSON ret = item.data.get(field);
      if (ret == null)
        return JSON.NULL;

      return ret.copy();
    }
    else
      return null;
  }
  
  private HashMap<String, JSON> data = new HashMap<String, JSON>();

  private String id;

  private HashMap<String, Item> joins;

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
  public synchronized Item copy()
  {
    Item ret = new Item();
    ret.setId(getId());

    for (String key : data.keySet())
      ret.setValue(key, getValue(key).copy());
    
    if(isJoined())
      for(String key : joinKeySet())
        ret.setJoin(getJoin(key), key);

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

    for (String key : item.keySet())
      if (Item.compareToNullSafe(item.getValue(key), getValue(key)) != 0)
        return false;

    return true;
  }
  
  /**
   * An alias for getValue()
   * @see {@link #getValue(String)}
   * 
   */
  public JSON get(final String field)
  {
    return getValue(field);
  }
  
  public String getId()
  {
    return id;
  }

  /**
   * A method to retrieve the base item i.e. customer joins address would return customer.
   * @return a copy of the base item of this join (removing all join information)   
   * 
   */
  public synchronized Item getJoinBase()
  {
    Item ret = new Item();
    ret.setId(getId());

    for (String key : data.keySet())
      ret.setValue(key, getValue(key).copy());
    
    return ret;
  }
  /**
   * Return the joined item for the group key
   * @param key the key naming the group "i.e. customers"
   * @return The item that is joined in this composite, or null if it does not exist.
   * 
   */
  public synchronized Item getJoin(String key)
  {
    if(joins != null)
      return joins.get(key).copy();
    else
      return null;
  }

  public final synchronized ArrayList<Item> getJoinedItems()
  {
    ArrayList<Item> ret = new ArrayList<Item>();
    if(isJoined())
      for(Item i : joins.values())
        ret.add(i.copy());
    
    return ret;
  }
  
  /**
   * A hook for applications like JasperReports or other "Bean based" containers
   * that don't deal well with specific fields.
   * @return <Return Description>  
   * 
   */
  public Item getThis()
  {
    return this;
  }
  /**
   * <p>
   * Return the value for this field. If the value is not set
   * and is not in the key-set it will return UNDEFINED else
   * it will return NULL;
   * </p>
   * <p>
   * If the value exists that value will be returned.
   * </p>
   * 
   * <p>
   * If this Item is joined to other items, the key will be cascaded down
   * to the items stored in the join. This should allow search methods to be
   * dynamically cascaded across multiple joins. <em>it should be noted that
   * if the items that are joined contain the same key only the first key will
   * be searched. The joins will effectively be masked.</em>
   * </p>
   * @param field the field to look for (key)
   * @return the JSON value of that field, NULL or UNDEFINED.
   */
  public JSON getValue(final String field)
  {
    if(Item.ID.equals(field))
      return JSON.wrap(getId());

    if(data.containsKey(field))
    {
      JSON ret = data.get(field);
      if (ret == null)
        return JSON.NULL;

      return ret.copy();
    }
    else
      if(isJoined())
      {
        for(Item i : getJoinedItems())
        {
          JSON ret = getValue(i, field);
          if(ret != null)
            return ret;
        }
        return JSON.UNDEFINED;
      }
      else
        return JSON.UNDEFINED;
  }
  
  /**
   * Return the contents of this JSON field
   * as an integer
   * @see {@link #getValue(String)}
   * @param field the field to use
   * @return the return value 
   */
  public Integer integer(String field)
  {
    return get(field).getNumber().intValue();
  }
  
  /**
   * Return the contents of this JSON field
   * as a number;
   * @see {@link #getValue(String)}
   * @param field the field to use
   * @return the return value 
   */
  public Double decimal(String field)
  {
    return get(field).getNumber();
  }
  
  /**
   * Return the contents of this JSON field
   * as a string
   * @see {@link #getValue(String)}
   * @param field the field to use
   * @return the return value 
   */
  public String string(String field)
  {
    return get(field).getString();
  }
  /**
   * Return the contents of this JSON field
   * as a boolean
   * @see {@link #getValue(String)}
   * @param field the field to use
   * @return the return value 
   */
  public Boolean bool(String field)
  {
    return get(field).getBoolean();
  }
  /**
   * Return the contents of this JSON field
   * as a Date
   * @see {@link #getValue(String)}
   * @param field the field to use
   * @return the return value 
   */
  public Date date(String field)
  {
    return get(field).getDate();
  }
  
  public Map<String, JSON> getValues()
  {
    return Collections.unmodifiableMap(data);
  }

  public boolean has(String field)
  {
    JSON j = getValue(field); 
    if(j.isNullOrUndefined())
      return true;
    
    return JSON.empty(j);
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

  public final boolean isNull(String key)
  {
    if(get(key).isNullOrUndefined())
      return true;
    else
      return false;
  }
  
  public final boolean isEmpty(String key)
  {
    if(isNull(key))
      return true;
    else
    {
      String data = string(key);
      if(data == null)
        return true;
      
      return data.trim().isEmpty();
    }
  }
  
  public final boolean isJoined()
  {
    if(joins != null)
      return true;
    else
      return false;
  }
  
  public final Set<String> joinKeySet()
  {
    HashSet<String> set = new HashSet<String>();
    if(isJoined())
      for(String s : joins.keySet())
        set.add(s);
    
    return set;
  }

  /**
   * @return an unmodifiable set of all keys for the data in this Item including keys in
   * the joined Item(s). 
   * 
   */
  public Set<String> keySet()
  {
    Set<String> newKeys = new HashSet<String>();
    
      for(String s : data.keySet())
        newKeys.add(s); 
     
    if(isJoined())
      for(Item i : joins.values())
        for(String s : i.keySet())
          newKeys.add(s);
    
    return Collections.unmodifiableSet(newKeys);
  }

  /**
   * @return an unmodifiable set of all the keys for the data in the primary Item. This will
   * not return the keys associated with any joins.  
   * 
   */
  public Set<String> keySetNoJoins()
  {
    Set<String> newKeys = new HashSet<String>();
    
    for(String s : data.keySet())
      newKeys.add(s);
    
    return Collections.unmodifiableSet(newKeys);
  }

  public void removeKey(String field)
  {
    data.remove(field);
  }

  public void setId(String id)
  {
    this.id = id;
  }
  
  public synchronized void setValue(String field, Object value)
  {
    if(isJoined())
      if(!data.containsKey(field))
      {
        for(Item j : joins.values())
          if(j.data.containsKey(field))
          {
            j.setValue(field, value);
            return;
          }
      }
      
    //Default behavior
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

    Set<String> joins = joinKeySet();
    Set<String> dataKeys = data.keySet();
    if(isJoined())
      build.append(" Joins: ");
    
    if(isJoined())
      for(String s : joins)
      {
        build.append("{");
        build.append(s);
        build.append("} ");
      }
    
    if(isJoined())
      build.append("| ");
    
    for (String key : dataKeys)
    {
      build.append("{\"");
      build.append(key);
      build.append("\" : ");
      build.append(data.get(key).toString());
      build.append("}");
    }

    if(isJoined())
      for(String s : joins)
      {
        build.append("\t{");
        build.append(s);
        build.append("}");
        build.append(getJoin(s));
      }
    return build.toString();
  }
  
  protected synchronized void clearJoin(String key)
  {
    if(joins != null)
    {
      joins.remove(key);
      if(joins.isEmpty())
        joins = null;
    }
      
  }
  
  protected synchronized Item setJoin(final Item item, final String key)
  {
    if(joins == null)
      joins = new HashMap<String, Item>();
    
    joins.put(key, item.copy());
    return this;
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
