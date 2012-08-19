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
  public static final JSON FALSE = new JSON(false);
  public static final JSON NULL;
  public static final JSON TRUE = new JSON(true);
  public static final JSON UNDEFINED;
  private static final long serialVersionUID = -8562994439940666907L;

  static
  {
    NULL = new JSON(null, JSONType.NULL);
    UNDEFINED = new JSON(null, JSONType.UNDEFINED);
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

  private final String data;

  private final JSONType type;

  /**
   * Construct a new JSON object from a String/Boolean/Number. Currently
   * any other object will be set to null.
   * @param value
   */
  public JSON(Object value)
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
            data = Float.valueOf(value.toString()).toString();
          }

          else
            if (value instanceof Boolean)
            {
              type = JSONType.BOOLEAN;
              data = value.toString();
            }
            else
            {
              type = JSONType.NULL;
              data = null;
            }
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
        return data.compareTo(toCompare.data);
      case NUMBER:
        return Float.valueOf(data).compareTo(Float.valueOf(toCompare.data));
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

      return data.compareTo(toCompare.data);
    }
  }

  public JSON copy()
  {
    return new JSON(data);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
    JSON comp = null;

    if (obj instanceof JSON)
      comp = (JSON) obj;

    if (comp == null)
      return false;

    if (data == null)
      return false;

    return data.compareTo(comp.data) == 0;
  }

  public Boolean getBoolean()
  {
    return Boolean.valueOf(data);
  }

  public Integer getInteger()
  {
    return Integer.valueOf((int) Math.floor(getNumber()));
  }

  public Float getNumber()
  {
    return Float.valueOf(data);
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
}
