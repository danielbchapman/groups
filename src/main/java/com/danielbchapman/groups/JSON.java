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
import java.util.Date;

/**
 * A primitive immutable JSON data-type
 *
 ***************************************************************************
 * @author Daniel B. Chapman 
 * @link http://www.danielbchapman.com
 * @link https://github.com/danielbchapman/groups/
 ***************************************************************************
 */
public class JSON implements Comparable<JSON>, Serializable
{
  private static final long serialVersionUID = -8562994439940666907L;
  public static final JSON FALSE = new JSON(false);
  public static final JSON NULL;
  public static final JSON TRUE = new JSON(true);
  public static final JSON UNDEFINED;

  static
  {
    NULL = new JSON(null, JSONType.NULL);
    UNDEFINED = new JSON(null, JSONType.UNDEFINED);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    if(data == null)
      return 23;
    /* Our data is strings, so we're using that hash, the number 12 should has 
     * to "12" in JSON as they are equivalent 
     * */
    return data.hashCode();
    
  }
  
  public static boolean empty(JSON j)
  {
    if(j == null)
      return true;
    
    if(j.isNullOrUndefined())
      return true;
    
    if(j.data == null)
      return true;
    
    if(j.data.trim().length() < 1)
      return true;
    
    return false;
  }
  public static int compare(JSON a, JSON b)
  {

    if (a == null && b == null)
      return 0;

    if (a == null)
      return -1;

    if (b == null)
      return 1;

    if (a.type == b.type)
    {
      if (a.data == null && b.data == null)
        return 0;

      if (a.data == null)
        return 0;

      if (b.data == null)
        return 0;

      switch (a.type)
      {
      case STRING:
        a.getString().compareTo(b.getString());
      case BOOLEAN:
        return a.getBoolean().compareTo(b.getBoolean());
      case NUMBER:
        return a.getNumber().compareTo(b.getNumber());
      case NULL:
        return 0;
      default: // Undefined
        return 0;
      }
    }
    else
    {
      if (a.data == null && b.data == null)
        return 0;

      if (a.data == null)
        return -1;

      if (b.data == null)
        return 1;

      return a.data.compareTo(b.data);
    }

  }
  /**
   * A factory style constructor.
   * @see #JSON(Object)
   * @param value the value to wrap
   * @return the JSON wrapper for this object
   * 
   */
  public static JSON wrap(Object value)
  {
    return new JSON(value);
  }

  private String data;

  private JSONType type;

  /**
   * Construct a new JSON object from a String/Boolean/Number. Currently
   * any other object will be set to null.
   * @param value
   */
  public JSON(Object value)
  {
    mutateValue(value);
  }

  private JSON(String data, JSONType type)
  {
    this.type = type;
    this.data = data;
  }

  public int compareTo(JSON toCompare)
  {
    if (toCompare == null)
      return 1;

    if (toCompare.type == type)
    {
      switch (type)
      {
      case NULL:
        return 0;
      case UNDEFINED:
        return 0;
      case STRING:
      {
        return Utility.compareStringNumericData(data, toCompare.data);
      }
      case NUMBER:
      case DATE:
        return Double.valueOf(data).compareTo(Double.valueOf(toCompare.data));
      case BOOLEAN:
        return Boolean.valueOf(data).compareTo(Boolean.valueOf(toCompare.data));
      default:
        return 0;
      }
    }
    else
    {
      if (isNullOrUndefined() && toCompare.isNullOrUndefined())
        return 0;

      if (isNullOrUndefined())
        return -1;

      if (toCompare.isNullOrUndefined())
        return 1;

      return Utility.compareStringNumericData(data, toCompare.data);
    }
  }

  public JSON copy()
  {
    return new JSON(data, type);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    if(type == JSONType.NULL || type == JSONType.UNDEFINED)
      if(obj == null)
        return true; //NULL type == null
    
    JSON comp = null;

    if (obj instanceof JSON)
      comp = (JSON) obj;

    if (comp == null)
      return false;

    if (data == null)
      return false;

    if(data == null && comp.data == null)
      return true;
    
    if(data != null && comp.data == null)
      return false;
    
    if(data == null && comp.data != null)
      return false;
    
    return data.compareTo(comp.data) == 0;
  }

  public Boolean getBoolean()
  {
    return Boolean.valueOf(data);
  }
  
  public Date getDate()
  {
    return new Date(Long.valueOf(data)); 
  }

  public Integer getInteger()
  { 
    return Integer.valueOf((int) Math.floor(getNumber()));
  }

  public Double getNumber()
  {
    if(JSONType.NUMBER.equals(data))
      return Double.valueOf(data);
    else
      try
      {
        return Double.valueOf(data);    
      }
      catch(NumberFormatException e)
      {
        return null;
      }
  }

  public String getRegexSafeString()
  {
    return getString();
  }

  public String getString()
  {
    return data == null ? "null" : data;
  }

  public JSONType getType()
  {
    return type;
  }

  public boolean isNullOrUndefined()
  {
    if (type == JSONType.NULL || type == JSONType.UNDEFINED)
      return true;
    else
      return false;
  }

  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("\"");
    builder.append(data);
    builder.append("\"");
    return builder.toString();
  }
  
  protected void mutateValue(Object value)
  {
    if (value == null)
    {
      type = JSONType.NULL;
      data = null;
    }
    else
      if (value instanceof JSON)
      {
        type = ((JSON) value).getType();
        data = ((JSON) value).getString();
      }
      else
        if (value instanceof String)
        {
          type = JSONType.STRING;
          data = (String) value;
        }

        else
          if (value instanceof Number)
          {
            type = JSONType.NUMBER;
            data = Double.valueOf(value.toString()).toString();
          }

          else
            if (value instanceof Boolean)
            {
              type = JSONType.BOOLEAN;
              data = value.toString();
            }
            else if (value instanceof Date)
            {
              type = JSONType.DATE;
              data = Long.toString(((Date) value).getTime());
            }
            else
            {
              type = JSONType.NULL;
              data = null;
            }
  }
}
