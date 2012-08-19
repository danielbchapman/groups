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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A collection is a set of items that have been serialized in some manner
 * and can be accessed by thier id and queried.
 ***************************************************************************
 * @author Daniel B. Chapman 
 * @link http://www.danielbchapman.com
 * @link https://github.com/danielbchapman/groups/
 ***************************************************************************
 */
public class Group extends AbstractGroup
{
  private static final long serialVersionUID = 1L;
  private String name;
  
  public boolean delete(File directory)
  {
    File toDelete = new File(directory.getAbsolutePath() + File.separator + name);
    if(!toDelete.exists())
      return false;
    
    return toDelete.delete();
  }

  /**
   * The name of this collection
   * @return the name of this collection  
   * 
   */
  public String getName()
  {
    return name;
  }
  
  public String print()
  {
    StringBuilder build = new StringBuilder();
  
    build.append("Collection [" + name + "]");
    build.append("\n");
    for (Item i : getAllItems())
    {
      build.append("\t");
      build.append(i);
      build.append("\n");
    }
  
    return build.toString();
  }
  
  public void save(File directory)
  {
    try
    {
      File toSave = new File(directory.getAbsolutePath() + File.separator + name);
      if (!toSave.getParentFile().exists())
        toSave.getParentFile().mkdirs();
      if (!toSave.exists())
        toSave.createNewFile();

      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(toSave));
      out.writeObject(this);
      out.flush();
      out.close();
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
}
