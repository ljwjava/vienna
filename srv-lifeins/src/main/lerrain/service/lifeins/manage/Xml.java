package lerrain.service.lifeins.manage;

import lerrain.tool.Common;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.InputStream;
import java.util.*;

public class Xml
{
	private static DocumentBuilder docBuilder;

	String text;

	List<Xml> children = new ArrayList();

	String name;

	Map<String, String> attr = new HashMap<>();

	public Xml(Element e)
	{
		name = e.getTagName();

		NamedNodeMap nnm = e.getAttributes();
		int len = nnm.getLength();
		for (int i = 0; i < len; i++)
		{
			Node node = nnm.item(i);
			attr.put(node.getNodeName(), node.getNodeValue());
		}

		NodeList list = e.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
		{
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				children.add(new Xml((Element)node));
			}
		}

		if (children.isEmpty())
			text = e.getTextContent();
	}

	public Xml(String name)
	{
		this.name = name;
	}

	public Xml firstChild()
	{
		return children.size() > 0 ? children.get(0) : null;
	}

	public Xml firstChild(String name)
	{
		for (Iterator iter = children.iterator(); iter.hasNext(); )
		{
			Xml n1 = (Xml)iter.next();
			if (name.equals(n1.getName()))
				return n1;
		}

		return null;
	}

	public void replace(Xml xml)
	{
		for (Xml c : children)
		{
			if (c.getName().equals(xml.getName()))
			{
				c.overwrite(xml);
				return;
			}
		}
	}

	public void overwrite(Xml xml)
	{
		this.name = xml.name;
		this.text = xml.text;
		this.attr = xml.attr;

		this.children = xml.children;
	}

	public List<Xml> getChildren()
	{
		return children;
	}
	
	public List<Xml> getChildren(String name)
	{
		List<Xml> r = new ArrayList();
		
		for (Iterator iter = children.iterator(); iter.hasNext(); )
		{
			Xml n1 = (Xml)iter.next();
			if (name.equals(n1.getName()))
				r.add(n1);
		}
		
		return r;
	}
	
	public boolean hasChildren()
	{
		return !children.isEmpty();
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getText()
	{
		return text;
	}
	
	public String getAttribute(String name)
	{
		if (hasAttribute(name))
			return attr.get(name);
		
		return null;
	}
	
	public String getAttribute(String[] name)
	{
		for (int i = 0; i < name.length; i++)
		{
			if (hasAttribute(name[i]))
				return attr.get(name[i]);
		}
		
		return null;
	}

	public void setAttribute(String name, String value)
	{
		attr.put(name, value);
	}

	public void addChild(Xml xml)
	{
		children.add(xml);
	}

	public void setText()
	{
		this.text = text;
	}

	public String getAttribute(String name, String def)
	{
		return hasAttribute(name) ? getAttribute(name) : def;
	}

	public boolean hasAttribute(String name)
	{
		return attr.containsKey(name);
	}

	public static Xml nodeOf(File f)
	{
		try
		{
			return new Xml(docBuilder().parse(f).getDocumentElement());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static Xml nodeOf(InputStream is)
	{
		try
		{
			return new Xml(docBuilder().parse(is).getDocumentElement());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private static DocumentBuilder docBuilder()
	{
		if (docBuilder == null)
		{
			try 
			{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setValidating(false);
				docBuilder = dbf.newDocumentBuilder();
			}
			catch (ParserConfigurationException e) 
			{
				e.printStackTrace();
			}
		}

		return docBuilder;
	}

	public String toString()
	{
		return this.toString("");
	}

	public String toString(String space)
	{
		StringBuffer s = new StringBuffer();
		s.append(space + "<" + name);

		for (Map.Entry<String, String> e : attr.entrySet())
			s.append(" " + e.getKey() + "=\"" + e.getValue() + "\"");

		if (children.isEmpty())
		{
			if (Common.isEmpty(text))
			{
				s.append("/>\n");
			}
			else
			{
				s.append(">");
				s.append(text);
				s.append("</" + name + ">\n");
			}
		}
		else
		{
			s.append(">\n");
			for (Xml x : children)
				s.append(x.toString(space + "    "));
			s.append(space + "</" + name + ">\n");
		}

		return s.toString();
	}
}
