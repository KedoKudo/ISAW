/*
 * File:  PyOperatorFactory.java
 *
 * Copyright (C) 2003, Peter Peterson
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
 * Contact : Peter F. Peterson <pfpeterson@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.1  2003/06/11 21:24:07  pfpeterson
 * Added to CVS.
 *
 */
package DataSetTools.operator;

import java.io.File;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import org.python.core.Py;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * This class is to be used to create operators from jython code.
 * 
 * NOTE: All operators created from a single instance of the factory
 * will share name-space. In practice this should not be a problem,
 * and can be remidied by creating a new instance of the
 * PyOperatorFactory.
 */
public class PyOperatorFactory extends Object{
  protected PythonInterpreter interp=null;

  /**
   * This constructor does not allow passing information to the
   * interpreter.
   */
  public PyOperatorFactory(){
    this(null);
  }

  /**
   * This constructor allows passing information into to the
   * interpreter when it initializes.
   *
   * NOTE: It is a bad idea to pass the filename to the initialization
   * process.
   */
  private PyOperatorFactory(String[] args){
    this.initInterpreter(args);
  }

  protected void initInterpreter(String[] argv){
    // get preProperties, postProperties, and systemProperties
    Properties postProps = new Properties();
    Properties sysProps=System.getProperties();

    // put systemProperties (those set with -D) in postProps
    Enumeration e=sysProps.propertyNames();
    while(e.hasMoreElements()){
      String name=(String)e.nextElement();
      if(name.startsWith("python."))
        postProps.put(name,System.getProperty(name));
    }

    // here's the initialization step
    PythonInterpreter.initialize(sysProps,postProps,argv);

    // instatiate AFTER initialize
    interp=new PythonInterpreter();
  }

  /**
   * generate an operator instance from the given python file
   */
  public Operator getInstance(String filename) throws IllegalStateException,
                                                      ClassCastException,
                                                      MissingResourceException{
    String classname;

    // execute the file
    this.interp.execfile(filename);

    // get the name of the class within the file
    int start=filename.lastIndexOf(File.separator);
    if(start<0)
      start=0;
    else
      start++;

    // get the class
    classname=filename.substring(start,filename.length()-3);
    PyObject opClass=interp.get(classname);
    if(opClass==null)
      throw new MissingResourceException("Class \""+classname
                                         +"\" not found in file",classname,
                                         "class");

    // get an instance
    PyObject opInstance=opClass.__call__();//new PyObject());
    Operator instance=(Operator)opInstance.__tojava__(Operator.class);
    if(instance==Py.NoConversion)
      throw new ClassCastException("Could not cast \""+classname
                                   +"\" as an Operator");

    return instance;
  }

  /**
   * This just tests whether the script specified will create an
   * instance.
   */
  static public void main(String[] args){
    if(args.length<=0){
      System.out.println("must specify a filename");
      System.exit(-1);
    }

    File file=new File(args[0]);
    if(! file.isFile() ){
      System.out.println(args[0]+" is not a regular file");
      System.exit(-1);
    }

    if(! file.canRead()){
      System.out.println("file must be user readable");
      System.exit(-1);
    }

    if(! args[0].endsWith(".py") ){
      System.out.println("filename must end with \".py\"");
      System.exit(-1);
    }
    file=null;

    PyOperatorFactory pyFactory=new PyOperatorFactory();
//    PyOperatorFactory pyFactory=new PyOperatorFactory(args);
    Operator mine=pyFactory.getInstance(args[0]);
    System.out.println("COMMAND="+mine.getCommand());
    System.out.println("NUMPARM="+mine.getNum_parameters());
    System.out.println("CLASS  ="+mine.getClass().getName());
    System.out.println("IS OP  ="+(mine instanceof Operator));
    System.out.println("PARENT ="+mine.getClass().getSuperclass().getName());
  }
}
