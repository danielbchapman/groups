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

/**
 * A simple enum for designating the types. They are self evident.
 *
 ***************************************************************************
 * @author Daniel B. Chapman 
 * @link http://www.danielbchapman.com
 * @link https://github.com/danielbchapman/groups/
 ***************************************************************************
 */
public enum JSONType
{
  NUMBER, STRING, BOOLEAN, UNDEFINED, DATE, NULL;
  
  public String toString()
  {
    if(this == NUMBER)
      return "number";
    
    if(this == STRING)
      return "string";
    
    if(this == BOOLEAN)
      return "bool";
    
    if(this == DATE)
      return "date";
    
    if(this == NULL)
      return "null";
    
    return "undefined";
  }
  
  public static JSONType fromString(String string)
  {
    if(string == null)
      return UNDEFINED;
    
    if(string.equals("undefined"))
      return UNDEFINED;
    if("null".equals(string))
      return NULL;
    if("bool".equals(string))
      return BOOLEAN; 
    if("number".equals(string))
      return NUMBER;
    if("date".equals(string))
      return DATE;
    if("string".equals(string))
      return STRING;

    return UNDEFINED;
  }
}
