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

/**
 * A temporary group that can be used to search against or as a smaller set of data. These
 * groups can then be merged or transformed into new data-sets.
 *
 ***************************************************************************
 * @author Daniel B. Chapman 
 * @link http://www.danielbchapman.com
 * @link https://github.com/danielbchapman/groups/
 ***************************************************************************
 */
public class SubGroup extends AbstractGroup
{
  private static final long serialVersionUID = 1L;

  final protected String derivedFrom;

  public SubGroup(final java.util.Collection<Item> items, final String name)
  {
    super(items);
    derivedFrom = name;
  }

  public SubGroup(final java.util.Set<Item> items, final String name)
  {
    super(items);
    derivedFrom = name;
  }

  public SubGroup(final String name)
  {
    super();
    derivedFrom = name;
  }

  public SubGroup chainLog(String message)
  {
    System.out.println("\n=========" + message + "=========");
    System.out.println(print());
    return this;
  }

  @Override
  public String getName()
  {
    return derivedFrom;
  }

  public String print()
  {
    StringBuilder build = new StringBuilder();

    build.append("SubGroup of [" + derivedFrom + "]");
    build.append("\n");
    for (Item i : getAllItems())
    {
      build.append("\t");
      build.append(i);
      build.append("\n");
    }

    return build.toString();
  }
}
