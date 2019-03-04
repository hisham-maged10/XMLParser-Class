/**
 * @author ${hisham_maged10}
 *https://github.com/hisham-maged10
 * ${DesktopApps}
 */
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.net.URL;
import java.io.InputStream;
import java.net.MalformedURLException;
public class XmlParser
{
	private File source;
	private HashMap<String,ArrayList<String>> parsedInfo;	
	private URL url;
	/*public XmlParser()
	{
		this(null);	
	}*/
	public XmlParser(URL source)
	{
		this.url=source;
		try{
		parsedInfo=parseXML(this.url);
		}catch(Exception ex){ex.printStackTrace();}
	}
	public XmlParser(File source)
	{
		this.source=source==null?getFile():source;
		parsedInfo=parseXML(this.source);
	}
	public HashMap<String,ArrayList<String>> getParsedInfo()
	{
		return parsedInfo;
	}
	public File getFile()
	{
		JFileChooser chooser=new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter=new FileNameExtensionFilter("Atom Files","atom","ATOM","xml");
		File file=null;
		chooser.setFileFilter(filter);
		try{
		do{
			System.out.println("Choose an XML File to parse");
			chooser.showOpenDialog(null);
		}while((file=chooser.getSelectedFile())==null || !file.isFile());
		}catch(NullPointerException ex){System.out.println("Incorrect response"); return getFile();}
		return file;
	}
	private HashMap<String,ArrayList<String>> parseXML(File source)
	{
		HashMap<String,ArrayList<String>> map=new HashMap<>();		
		initHashMap(map,source);
		fillMapValues(map,source);	
		return map;
	}
	//overloaded method
	private HashMap<String,ArrayList<String>> parseXML(URL source)
	{
		HashMap<String,ArrayList<String>> map=new HashMap<>();		
		initHashMap(map,source);
	//	System.out.println("ey");
		fillMapValues(map,source);	
		return map;
	}
	private void fillMapValues(HashMap<String,ArrayList<String>> map,File source)
	{
		try(Scanner input=new Scanner(source))
		{
			String line=null;
			while(input.hasNextLine())
			{
				if((line=input.nextLine()).startsWith("<?xml") || line.startsWith("<feed") || !input.hasNextLine())
				continue;
				analyzeLine(map,line);
			}
		}catch(IOException ex){System.out.println("incorrect file!"); ex.printStackTrace();return;}
	}
	//overloaded method
	private void fillMapValues(HashMap<String,ArrayList<String>> map,URL source)
	{
		try(Scanner input=new Scanner(source.openStream()))
		{
			String line=null;
			while(input.hasNextLine())
			{
				
				if((line=input.nextLine()).startsWith("<?xml") || line.startsWith("<feed") || !input.hasNextLine())
				continue;
				analyzeLine(map,line);
			}
		}catch(Exception ex){System.out.println("incorrect file!"); ex.printStackTrace();return;}
	}
	private void analyzeLine(HashMap<String,ArrayList<String>> map,String line)
	{
		
		String start=null;
		String end=null;
		for(String e:map.keySet())
		{
			start="<"+e+">";
			end="</"+e+">";
			//System.out.println(start + " " + end);
			map.get(e).add(line.substring(line.indexOf(start)+start.length(),line.indexOf(end)));
		}
	}
	private void initHashMap(HashMap<String,ArrayList<String>> map, File source)
	{
		String line;
		try(Scanner input=new Scanner(source))
		{
			while(input.hasNextLine())
			{
				if((line=input.nextLine()).startsWith("<?xml") || line.startsWith("<feed"))
				continue;
				else
				{
					HashSet<String> types=getEntryTypes(line);
					for(String e:types)
						map.put(e,new ArrayList<String>());
					return;
				}
			}

		}catch(Exception ex){System.out.println("incorrect file!");	}
	}
	//overloaded method
	private void initHashMap(HashMap<String,ArrayList<String>> map,URL source)
	{
		String line;
		try(Scanner input=new Scanner(source.openStream()))
		{
			while(input.hasNextLine())
			{
				if((line=input.nextLine()).startsWith("<?xml") || line.startsWith("<feed"))
				continue;
				else
				{
					HashSet<String> types=getEntryTypes(line);
					for(String e:types)
						map.put(e,new ArrayList<String>());
					return;
				}
			}

		}catch(Exception ex){System.out.println("incorrect file!");	}
	}
	private HashSet<String> getEntryTypes(String line)
	{
		String entryType="";
		HashSet<String> types=new HashSet<>();
		Scanner input=new Scanner(line);
		entryType=(entryType=input.useDelimiter(">").next()).substring(entryType.indexOf("<")+1);
		line=line.substring(line.indexOf("<"+entryType+">")+entryType.length()+2,line.indexOf("</"+entryType+">"));
		input=new Scanner(line);
		try{
			fillTypes(input,types);
		}catch(NoSuchElementException ex){input.close(); return types;}
		input.close();
		return types;
	}
	private void fillTypes(Scanner input,HashSet<String> types)
	{
		String entryName="";
		while(input.hasNext())
		{
		if(!entryName.isEmpty() && !entryName.matches("</.*"))input.useDelimiter("><").next();
		entryName=input.useDelimiter(">").next();
		if(entryName.matches("<.*/") || entryName.matches("<.* type=.*") || entryName.matches("<.* href=.*"))
		{
			input.useDelimiter("<").next();				
			continue;
		}
		else if(entryName.matches("</.*"))
		{
			continue;
		}
		else if(entryName.matches("<!.*"))
		{
			//System.out.println("entered");
			input.useDelimiter("]]><").next();
			continue;
		}
		types.add((entryName=entryName.substring(entryName.indexOf("<")+1)));
		input.useDelimiter("</"+entryName+">").next();
		}
	}
}