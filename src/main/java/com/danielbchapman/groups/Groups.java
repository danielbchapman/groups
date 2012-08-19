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

import java.util.HashMap;

/**
 * The manager class provides a set of static methods to access a set of groups. Each
 * group is designated by its unique name and its serialization. In general each group
 * is just a lump sum of String data.
 *
 ***************************************************************************
 * @author Daniel B. Chapman 
 * @link http://www.danielbchapman.com
 * @link https://github.com/danielbchapman/groups/
 ***************************************************************************
 */
public class Groups
{
  private static HashMap<String, Group> groups = new HashMap<String, Group>();

  public static Group getGroup(String name)
  {
    if (name == null)
      throw new IllegalArgumentException("The collection name can not be null.");

    Group ret = groups.get(name);

    if (ret == null)
    {
      ret = new Group();
      ret.setName(name);
      groups.put(name, ret);
    }

    return ret;
  }

  public static String[] getKnownGroups()
  {
    String[] total = new String[groups.size()];
    int i = 0;
    for (String s : groups.keySet())
      total[i++] = s;

    return total;

  }

  public static void setGroup(Group collection)
  {
    groups.put(collection.getName(), collection);
  }
}
