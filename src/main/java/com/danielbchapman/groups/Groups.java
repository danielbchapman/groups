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

import java.awt.image.ReplicateScaleFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
  private static Logger logger;
  public static String CURRENT_VERSION = "0.0.1-SNAPSHOT";
  private static final String VERSION = "version";
  private static final String ROOT = "groups";
  private static final String GROUP = "group";
  private static final String NAME = "name";
  private static final String TYPE = "type";
  private static final String ITEM = "item";
  private static final String FIELD = "field";
  private static final String VALUE = "value";
  private static final String NEXT_ID = "nextId";
  private static final String COUNT = "maximumIdentification";
  private static final String ID = "id";
  
  static{
    logger = Logger.getLogger(SubGroup.class.getName());
    logger.setLevel(Level.OFF);
  }
  
  public static void setLevel(Level level)
  {
    logger.setLevel(level == null ? Level.OFF : level);
  }
  
  public static void logWarning(String message)
  {
    logger.warning(message);
  }
  
  public static void logError(String message)
  {
    logger.severe(message);
  }
  
  public static void logInfo(String message)
  {
    logger.info(message);
  }
  
  public static Level getLevel()
  {
    return logger.getLevel();
  }
  
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
  
  private static void overrideGroup(String name, Group group)
  {
    groups.put(name, group);
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
  
  public static void toXml(OutputStream os, String title, String comments, String ... groups) 
      throws ParserConfigurationException, TransformerException
  {    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    doc.createComment(title + "\n" + comments);
    Element root = doc.createElement("groups");
    root.setAttribute(VERSION, Groups.VERSION);
    
    for(String key : groups)
    {
      Group g = getGroup(key);
      if(g == null)
        continue;
      
      Element group = doc.createElement(GROUP);
      group.setAttribute(NAME, g.getName());
      group.setAttribute(NEXT_ID, g.nextId.toString());
      group.setAttribute(COUNT, Integer.toString(g.count));
      
      for(Item i : g.all())
      {
        if(i == null)
          continue;
        
        Element item = doc.createElement(ITEM);
        item.setAttribute(ID, i.getId());
        
        for(String s : i.keySet())
        {
          JSON value = i.get(s);
          //Escape keys
          s = s.replaceAll("\"", "&quot;")
              .replaceAll("/", "-")
              .replaceAll("'", "&apos;")
              .replaceAll("<", "&lt;")
              .replaceAll(">", "&gt;")
              .replaceAll("&", "&amp;")
              .replaceAll(" ", "_");
          
          try
          {
            Element field = doc.createElement(s);
            String content = i.string(s);
            field.setTextContent(content);
            field.setAttribute(TYPE, value.getType().toString());
            item.appendChild(field);
          }
          catch(org.w3c.dom.DOMException e)
          {
            System.err.println("Unable to process key: " + s + " and value " + value.getType().toString());
            throw e;
          }
        }
        
        group.appendChild(item);
      }
      
      root.appendChild(group);
    }
    doc.appendChild(root);
    
    // write the content into xml file
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(os);
 
    transformer.transform(source, result);   
    //DEBUG
    transformer.transform(source, new StreamResult(System.out));
    try
    {
      os.flush();
      os.close();      
    }
    catch (IOException e)
    {
      throw new RuntimeException(e.getMessage(), e);
    }
    finally 
    {  
      try
      {
        os.flush();
        os.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      finally
      {
        try
        {
          os.close();  
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        finally
        {
        }
      }
    }
  }
  
  private static boolean isWhitespaceNode(Node node)
  {
    if(node == null)
      return false;
    
    if(node.getNodeType() == Node.TEXT_NODE && node.getTextContent().trim().length() < 1)
      return true;
    
    return false;
  }
  public static ArrayList<Item> readItemsFromXml(InputStream is) throws ParserConfigurationException, SAXException, IOException, GroupFormatException 
  {
    ArrayList<Item> ret = new ArrayList<Item>();
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(is);
    
    Element root = doc.getDocumentElement();
    //TODO Check Version
    if(!"list".equals(root.getTagName()))
      throw new RuntimeException("Malformed XML, root node is not of type 'list'");
    
    NodeList groups = root.getChildNodes();
    
    for(int i = 0; i < root.getChildNodes().getLength(); i++)
    {
      Node item = root.getChildNodes().item(i);
      if(item.getNodeType() == Node.TEXT_NODE && item.getNodeValue().trim().length() < 1)
        continue; //skip whitespace
      
      Item toLoad = new Item();
      toLoad.setId(item.getAttributes().getNamedItem("id").getNodeValue());
      
      if(item != null && item.hasChildNodes())
      {
        for(int j = 0; j < item.getChildNodes().getLength(); j++)
        {
          Node field = item.getChildNodes().item(j);
          if(field == null || isWhitespaceNode(field))
            continue;
          
          String name = field.getAttributes().getNamedItem("name").getNodeValue();
          String type = field.getAttributes().getNamedItem("type").getNodeValue();
          Node value = field.getFirstChild();
          String nodeValue = value == null ? "" : value.getNodeValue();
          
          toLoad.setValue(name, new JSON(nodeValue, JSONType.fromString(type)));
        }
      }
      
      ret.add(toLoad);
    }
    
    return ret;
  }
  
  public static void fromXml(InputStream is) throws ParserConfigurationException, GroupFormatException, SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(is);
    
    Element root = doc.getDocumentElement();
    //TODO Check Version
    if(!ROOT.equals(root.getTagName()))
      throw new RuntimeException("Malformed XML, root node is not of type 'groups'");
    
    NodeList groups = root.getChildNodes();
    for(int i = 0; i < groups.getLength(); i++)
    {
      Node group = groups.item(i);
      if(!GROUP.equals(group.getNodeName()))
      {
        System.err.println("Debug Information -> " + group.toString());
        throw new GroupFormatException("Illegal format, the node '" + group.getNodeName() + "' is not '" + GROUP + "'");
      }
        
      
      String name = group.getAttributes().getNamedItem(NAME).getNodeValue();
      int count = Integer.valueOf(group.getAttributes().getNamedItem(COUNT).getTextContent());
      BigInteger nextId = new BigInteger(group.getAttributes().getNamedItem(NEXT_ID).getTextContent());
      
      Group toLoad = new Group();
      toLoad.setName(name);
      toLoad.nextId = nextId;
      
      NodeList items = group.getChildNodes();
      
      for(int j = 0; j < items.getLength(); j++)
      {
        Node item = items.item(j);
        if(!ITEM.equals(item.getNodeName()))
          throw new GroupFormatException("The node '" + item.getNodeName() + "' does not equal '" + ITEM + "'");
        String id = item.getAttributes().getNamedItem(ID).getNodeValue();
        Item itemToLoad = new Item(id);
        
        for(int k = 0; k < item.getChildNodes().getLength(); k++)
        {
          Node json = item.getChildNodes().item(k);
          json.getNodeName();
          String jsonType = json.getAttributes().getNamedItem(TYPE).getNodeValue().toString();
          JSONType type = JSONType.fromString(jsonType);
          JSON value = new JSON(json.getTextContent(), type);
          String nodeName = json.getNodeName();
          nodeName = nodeName
              .replaceAll("&quot;", "\"")
              .replaceAll("&apos;", "'")
              .replaceAll("&lt;", "<")
              .replaceAll("&gt;", ">")
              .replaceAll("&amp;", "&");
          itemToLoad.setValue(json.getNodeName(), value);
        }
        toLoad.put(itemToLoad);
      }
      
      overrideGroup(name, toLoad);
    }
  }
  
  public static class GroupFormatException extends Exception
  {
    private static final long serialVersionUID = 1L;
    
    public GroupFormatException(String message)
    {
      super(message);
    }
  }
}