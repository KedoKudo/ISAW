/*
 * File:  FileReflector.java
 *
 * Copyright (C) 2004 Dominic Kramer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 *
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Dominic Kramer <kramerd@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA and by
 * the National Science Foundation under grant number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2004/02/07 05:10:46  bouzekc
 * Added to CVS.  Changed package name.  Uses RobustFileFilter
 * rather than ExampleFileFilter.  Added copyright header for
 * Dominic.
 *
 */
package devTools.Hawk.classDescriptor.tools;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import DataSetTools.util.RobustFileFilter;
import devTools.Hawk.classDescriptor.gui.frame.ProgressGUI;
import devTools.Hawk.classDescriptor.gui.frame.UnableToLoadClassGUI;
import devTools.Hawk.classDescriptor.modeledObjects.AttributeDefn;
import devTools.Hawk.classDescriptor.modeledObjects.ConstructorDefn;
import devTools.Hawk.classDescriptor.modeledObjects.Interface;
import devTools.Hawk.classDescriptor.modeledObjects.InterfaceDefn;
import devTools.Hawk.classDescriptor.modeledObjects.MethodDefn;

public class FileReflector
{
	private UnableToLoadClassGUI gui;
	private final int CLASSNAME = 0;
	private final int FILENAME = 1;
	
	public FileReflector()
	{
		gui = new UnableToLoadClassGUI();
	}
	
	public FileReflector(UnableToLoadClassGUI GUI)
	{
		gui = GUI;
	}
	
	/**
	 * 
	 * @param fileNameVec The Vector of filenames to add the new filenames to
	 */
	public static Vector getVectorOfInterfacesGUI(Vector fileNameVec, ProgressGUI gui)
	{
		String fileName = "";

		JFrame frame = new JFrame();
		frame.setSize(500,400);
		Container framePane = frame.getContentPane();
			
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
			
		JFileChooser chooser = new JFileChooser();
			RobustFileFilter filter = new RobustFileFilter();
			filter.addExtension("class");
			filter.addExtension("jar");
			chooser.setFileFilter(filter);
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = chooser.showDialog(frame, "Select");
		
		mainPanel.add(chooser, BorderLayout.CENTER);
						
		framePane.add(mainPanel);
		framePane.setVisible(true);
			
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			fileName = chooser.getSelectedFile().getAbsoluteFile().toString();
		}
		
		gui.setVisible(true);
		gui.setProgressBarString("");
		fileNameVec = getAllFiles(fileName,fileNameVec,gui);
		if (!gui.isCancelled())
		{
			gui.appendMessage("Done");
			gui.dispose();
		}
		
		return fileNameVec;
	}	

	public Vector getVectorOfInterfaces(Vector fileNameVec, ProgressGUI gui)
	{
		String name = "";
		Vector newVec = new Vector();
		int i = 0;
		int totalClassNumber = 0;
		while (i<fileNameVec.size() && !gui.isCancelled())
		{			
			name = (String)fileNameVec.elementAt(i);
			if (name.endsWith(".class"))
			{
				getInterfaceObjectsFromClassFile(name, newVec);
				gui.setValue(totalClassNumber+1);
				gui.setText("Loading class "+(totalClassNumber+1)+"\nof "+gui.getMaximum()+" total");
				totalClassNumber++;
			}
			else if (name.endsWith(".jar"))
			{
				totalClassNumber = getInterfaceObjectsFromJarFile(name, newVec,gui,totalClassNumber);
			}
			i++;
		}
		
		return newVec;
	}

	/**
	 * This scans through the filename given (if it is a directory) and returns a Vector of Strings of all the .class
	 * or .jar files in that directory.
	 * @param dir The absolute filename to a file or directory.
	 * @param vec The Vector to add the filenames to
	 * @return
	 */
	public static Vector getAllFiles(String dir, Vector vec, ProgressGUI gui)
	{
		File dirFile = new File(dir);
		File[] F = new File[0];
		
		if (dirFile.isDirectory())
			F = dirFile.listFiles();
		else
		{
				F = new File[1];
				F[0] = dirFile;
		}
		
		for (int i=0; i<F.length; i++)
		{
			if (F[i].isDirectory())
				getAllFiles(F[i].getAbsolutePath(), vec, gui);
			else if (F[i].getPath().endsWith(".class") || F[i].getPath().endsWith(".jar"))
				vec.add(F[i].getAbsolutePath());
		}
		
		return vec;
	}
		
		private Vector getInterfaceObjectsFromClassFile(String fileName, Vector vec)
		{
			Interface intf = getInterfaceObject(fileName, FILENAME, fileName);
			if (intf != null)
				vec.add(intf);
			
			return vec;
		}
		    
		private int getInterfaceObjectsFromJarFile( String jarname, Vector vec, ProgressGUI gui, int classNumber)
		{
			if (!jarname.trim().equals(""))
			{
				String fileName = "";
				String newName = "";
				try
				{
					Interface intf = null;
					JarFile jarfile = new JarFile(jarname);
					Enumeration jarEntries = jarfile.entries();
					ZipEntry entry = null;
					
					//now to scan through the entries in the jar file
					while (jarEntries.hasMoreElements())
					{
						entry = (ZipEntry)jarEntries.nextElement();
						fileName = entry.toString();
						if (fileName.endsWith(".jar"))
							getInterfaceObjectsFromJarFile(fileName, vec,gui,classNumber);
						else if (fileName.endsWith(".class"))
						{
							String delim = System.getProperty("file.separator")+".";
							StringTokenizer tokenizer = new StringTokenizer(fileName, delim);
							newName = "";
							int num = tokenizer.countTokens();
							for (int i=1; i<=(num-1); i++)
							{
								if (i != num-1)
									newName = newName + tokenizer.nextToken()+".";
								else
									newName = newName + tokenizer.nextToken();
							}
							intf = getInterfaceObject(newName, CLASSNAME, jarname);
							gui.setValue(classNumber+1);
							gui.setText("Loading class "+(classNumber+1)+"\nof "+gui.getMaximum()+" total");
							classNumber++;
							if (intf != null)
								vec.add(intf);				
						}
					}
				}
				catch (ZipException e)
				{
					System.err.println(e);
				}
				catch(IOException e)
				{
					System.err.println(e);
					StackTraceElement[] traceArray = e.getStackTrace();
					for (int i = 0; i < traceArray.length; i++)
						System.out.println("  "+traceArray[i]);				
				}
			}
			
			return classNumber;
		}
			
	/**
	* This creates an Interface object from a .class file with the name "fileName."
	* The method using this method should make sure "fileName" is a .class file.
	* If it isn't, or if the Interface object could not be created, then null is returned.
	* The Class object is made by reading the .class file byte by byte.  Then, the bytes
	* are used to create the Class object.
	* @param fileName the name of the .class file.  This has to be a .class file (not a directory or .jar file)
	* @param fileType either CLASSNAME or FILENAME
	* @param location Where the class is found at and it must be the files absolute file name.  This is used 
	* only when printing error messages and is used to to tell the classloader where to find the class.  
	* @return The Interface object which represents this class
	*/
	private Interface getInterfaceObject(String fileName, int fileType, String location)
	{
		Interface foundIntF = null;
		File selectedFile = new File(fileName);
		Vector fieldVec = new Vector();
		Vector constructorVec = new Vector();
		Vector methodVec = new Vector();
		InterfaceDefn intDefn = new InterfaceDefn();
		boolean go = true;
		String className = "";

		try
		{
			if (fileType == CLASSNAME)
				go = true;
			else if (fileType == FILENAME)
			{
				if (fileName.endsWith(".class"))
					go = true;
				else
					go = false;
			}
			if (go)
			{
					//so the file has to be a file (not a directory) and has to end
					//in ".class" to get in here
					Class foundClass = null;
					URL[] urlArr = getURLArray(location);				
					URLClassLoader loader = new URLClassLoader(urlArr);
					
					className = "";
					if (fileType == CLASSNAME)
						className = fileName;
					else if (fileType == FILENAME)
					{
						StringTokenizer tokenizer = new StringTokenizer(fileName, System.getProperty("file.separator"));
						while (tokenizer.hasMoreTokens())
						{
							className = tokenizer.nextToken();
						}
						//now to get rid of the .class at the end of the name
						className = className.substring(0,className.length() - 6);
					}
					
					try
					{
						foundClass = loader.loadClass(className);
					}
					catch(NoClassDefFoundError e)
					{						
						String errorMessage = e.getMessage().trim();
						//this is in the form
						// <name that was tried> (wrong name: <correct name>)
						
						StringTokenizer tokenizer1 = new StringTokenizer(errorMessage, ":");
						String errorPart1 = tokenizer1.nextToken();
						errorPart1 = tokenizer1.nextToken();  //this is " <corrent name>)"
												
						StringTokenizer tokenizer2 = new StringTokenizer(errorPart1, ")");
						String errorPart2 = tokenizer2.nextToken();  //this returns " <correct name>"
						
						String correctName = errorPart2.trim();
//						URL parentDir2 = new URL(parentDir);			
//						URLClassLoader loader2 = new URLClassLoader(urlArr);
						foundClass = loader.loadClass(correctName);
					}
					
					if (foundClass != null)
					{
						int i = 0;
												
						Field[] fieldArray = foundClass.getDeclaredFields();
							for (i = 0; i < fieldArray.length; i++)
								fieldVec.add(getAttributeDefn(fieldArray[i]));		
							
						Constructor[] constructorArray = foundClass.getDeclaredConstructors();
							for (i = 0; i < constructorArray.length; i++)
								constructorVec.add(getConstructorDefn(constructorArray[i]));
						
						Method[] methodArray = foundClass.getDeclaredMethods();
							for (i = 0; i < methodArray.length; i++)
								methodVec.add(getMethodDefn(methodArray[i]));
								
						intDefn = getInterfaceDefn(foundClass);
						
						foundIntF = (new Interface(intDefn, fieldVec, constructorVec, methodVec, "", ""));
					}
			}
		}
		catch(NoSuchElementException e)
		{
		}
		catch(Throwable e)
		{
			gui.processThrowable(location,className,e);
		}
		
		return foundIntF;
	}
		/**
		 * This returns an array of URLs used to load the file filename.  The array contains all of the files
		 * from the CLASSPATH.  If filename is a jarfile it is added to the array.  If filename is a class file
		 * then filename's parent, filename's parent's parent, filename's parent's paren'ts parent, etc. are added
		 * to the array
		 * @param filename The absoulte file location
		 * @return An array of URL objects (representing local files) which the classloader should look at when trying
		 * to load a class from filename.
		 */
		private URL[] getURLArray(String filename)
		{
//			System.out.println("filename="+filename);
			
			Vector vec = new Vector();
			try 
			{
				final String pathSep = System.getProperty("path.separator");
				final String fileSep = System.getProperty("file.separator");
				String newName = "";
				StringTokenizer tokenizer = new StringTokenizer(System.getProperty("java.class.path"),pathSep);
				while (tokenizer.hasMoreTokens())
				{
					newName = "file:" + tokenizer.nextToken();
					if (!newName.endsWith(fileSep) && !newName.endsWith(".jar"))
						newName += fileSep;

					vec.add(new URL(newName));
				}
				
				if (filename.endsWith(".jar"))
				{
					vec.add(new URL("file:"+filename));
				}
				else
				{
					File newFile = new File(filename);
					
					while (newFile.getParentFile() != null)
					{
						newName = "file:"+newFile.getParent();
						if (!newName.endsWith(fileSep))
							newName += fileSep;
						vec.add(new URL(newName));
						newFile = newFile.getParentFile();
					}
				}
			} 
			catch (MalformedURLException e)
			{
				System.out.println("In getURLArray(String) in FileReflector.java");
				System.err.println(e);
			}

			URL[] arr = new URL[vec.size()];
			for (int i=0; i<vec.size(); i++)
				arr[i]=(URL)(vec.elementAt(i));

			return arr;
		}
			
	private Vector getCharacteristicVector(Object obj, String str)
	{
		Vector vec = new Vector();
		int modNum = 0;
				
		if (str.toUpperCase().equals("FIELD"))
			modNum = ((Field)obj).getModifiers();
		else if (str.toUpperCase().equals("CONSTRUCTOR"))
			modNum = ((Constructor)obj).getModifiers();
		else if (str.toUpperCase().equals("METHOD"))
			modNum = ((Method)obj).getModifiers();
		else if (str.toUpperCase().equals("CLASS"))
			modNum = ((Class)(obj)).getModifiers();
		
		if (Modifier.isAbstract(modNum))
			vec.add("abstract");
		else if (Modifier.isFinal(modNum))
			vec.add("final");
		else if (Modifier.isNative(modNum))
			vec.add("native");
		else if (Modifier.isPrivate(modNum))
			vec.add("private");
		else if (Modifier.isProtected(modNum))
			vec.add("protected");
		else if (Modifier.isPublic(modNum))
			vec.add("public");
		else if (Modifier.isStatic(modNum))
			vec.add("static");		
		else if (Modifier.isStrict(modNum))
			vec.add("strict");
		else if (Modifier.isSynchronized(modNum))
			vec.add("synchronized");
		else if (Modifier.isTransient(modNum))
			vec.add("transient");
		else if (Modifier.isVolatile(modNum))
			vec.add("volatile");
																														
		return vec;
	}
	
	private Vector getConstructorParameterVector(Constructor Cnst)
	{
		Vector vec = new Vector();
		Class[] pArray =Cnst.getParameterTypes();
			
		for (int i = 0; i < pArray.length; i++)
			vec.add(pArray[i].getName());
			
		return vec;
	}

	private Vector getMethodParameterVector(Method methd)
	{
		Vector vec = new Vector();
		Class[] pArray = methd.getParameterTypes();
		
		for (int i = 0; i < pArray.length; i++)
			vec.add(pArray[i].getName());
			
		return vec;
	}
	
	private AttributeDefn getAttributeDefn(Field fd)
	{
		String attName = fd.getName();
		String attType = fd.getType().getName();
		Vector vec = getCharacteristicVector(fd, "FIELD");
		return (new AttributeDefn(vec, attName, attType));
	}
	
	private ConstructorDefn getConstructorDefn(Constructor cnst)
	{
		String constName = cnst.getName();
		Vector charVec = getCharacteristicVector(cnst, "CONSTRUCTOR");
		Vector pVec =  getConstructorParameterVector(cnst);
		
		return (new ConstructorDefn(charVec, pVec, constName));
	}
	
	private MethodDefn getMethodDefn(Method mthd)
	{
		String methName = mthd.getName();
		String methReturnType = mthd.getReturnType().getName();
		Vector charVec = getCharacteristicVector(mthd, "METHOD");
		Vector pVec = getMethodParameterVector(mthd);
		return (new MethodDefn(charVec, pVec, methName, methReturnType));
	}
	
	private InterfaceDefn getInterfaceDefn(Class clss)
	{
		String intType = "";
			if (!(clss.isPrimitive()) && !(clss.isArray()))
				{
					if (clss.isInterface())
						intType = "interface";
					else
						intType = "class";
				}
		String intName = clss.getName();
		String intExtends = "";
		if (clss.getSuperclass() != null)
			intExtends = clss.getSuperclass().getName();
		String intPackage = "";
		Package intPackageObject = clss.getPackage();
			if (intPackageObject != null)
				intPackage = clss.getPackage().getName();
		Vector charVec = getCharacteristicVector(clss, "CLASS");
		Vector intImplementsVec = new Vector();
		Class[] implArray = clss.getInterfaces();
		for (int i = 0; i < implArray.length; i++)
			intImplementsVec.add(implArray[i].getName());
		Vector intImportsVec = new Vector();
		
		return (new InterfaceDefn(charVec, intType, intName, intExtends, intImplementsVec, intImportsVec, intPackage));
	}
}
