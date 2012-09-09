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
 * An exception that is thrown on joins and other "Strict" operations in the groups. All
 * methods indicate when they throw this exception, though it is a runtime exception so it
 * is implicitly not checked. 
 ***************************************************************************
 * @author Daniel B. Chapman 
 * <br /><i><b>Light Assistant</b></i> copyright Daniel B. Chapman
 * @since Sep 8, 2012
 * @version 2 Development
 * @link http://www.lightassistant.com
 ***************************************************************************
 */
public class MultipleResultException extends RuntimeException
{
  private static final long serialVersionUID = 1756506764755022398L;

  public MultipleResultException(String message)
  {
    super(message);
  }
}
