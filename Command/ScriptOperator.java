/*
 * File:  ScriptOperator.java 
 *             
 * Copyright (C) 2001, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.26  2003/06/18 18:16:23  pfpeterson
 * Removed dead code, constructor throws an exception on failure, implements
 * PropertyChanger rather than Customizer, pushed off more work onto the
 * ScriptProcessor that the instance posesses.
 *
 * Revision 1.25  2003/06/17 16:39:39  pfpeterson
 * Uses methods in ScriptProcessor for getCommand, getTitle, getDocumentation,
 * and getCategoryList.
 *
 * Revision 1.24  2003/06/13 20:08:12  pfpeterson
 * Returns ScriptProcessor's getDocumentation if possible.
 *
 * Revision 1.23  2003/05/28 18:53:45  pfpeterson
 * Changed System.getProperty to SharedData.getProperty
 *
 * Revision 1.22  2003/03/25 22:49:13  pfpeterson
 * Added a EOL when printing the return of getResult in main.
 *
 * Revision 1.21  2003/03/10 19:27:06  pfpeterson
 * Uses StringBuffer rather than Document. Also modernized some code.
 *
 * Revision 1.20  2003/02/21 19:28:17  rmikk
 * Changed the Parameter argument to setParameter and addParameter
 *   to use IParameter instead of Parameter
 *
 * Revision 1.19  2002/11/27 23:12:10  pfpeterson
 * standardized header
 *
 * Revision 1.18  2002/09/19 15:57:21  pfpeterson
 * Now uses IParameters rather than Parameters.
 *
 * Revision 1.17  2002/08/19 17:07:05  pfpeterson
 * Reformated file to make it easier to read.
 *
 * Revision 1.16  2002/02/22 20:33:41  pfpeterson
 * Operator Reorganization.
 *
 */
package Command;


import javax.swing.text.*;
//import Command.*;
import java.lang.*;
import java.awt.event.*;
import DataSetTools.operator.*;
import DataSetTools.operator.Generic.*;
import DataSetTools.components.ParametersGUI.*;
import DataSetTools.parameter.*;
import DataSetTools.util.*;
import java.beans.*;
import java.io.*;

/**
 * Adds features to a ScriptProcessor to be more of an "Operator"
 */
public class ScriptOperator extends GenericOperator
         implements IObservable, PropertyChanger{  //for property change events
    private String filename;
    
    private ScriptProcessor SP;
    public static String ER_FILE_ERROR             = "File error ";
   
    
    /**
     * Creates an operator wrapper around a script The command Name
     * and category list are derived from the filename The title is
     * determined by the $title = from the script
     *
     * @param  filename  The file with a script
     */
    public ScriptOperator(  String filename ) throws InstantiationError{
        super("UNKNOWN");
        
        this.filename = filename;
        SP = new ScriptProcessor( filename );
        String errorMessage=SP.getErrorMessage().trim();
        if( errorMessage!=null && errorMessage.length( ) > 0 ){
          throw new InstantiationError(errorMessage);
        }
    }
    
    /**
     * Shows this operator. For debugging
     */
    public void show(){
        System.out.println( "Command ="+this.getCommand() );
        String[] categList=this.getCategoryList();
        if(categList == null )
            System.out.println( "Cat list is null" );
        else{
            System.out.println("Cat leng="+categList.length);
            for(int i = 0; i < categList.length; i++ ){
                System.out.print( categList[i]+",");
            }
            System.out.println("");
        }
    }

    /**
     * Gets the title
     *
     * NOTE: A line "Title= prompt or title" is needed for special
     * titles
     */
    public String getTitle(){
        return SP.getTitle();
    }

    /**
     * @return category list
     *
     * NOTE: It is calculated from the filename
     */
    public String[] getCategoryList(){
        return SP.getCategoryList();
    }

    public String getErrorMessage(){
        return SP.getErrorMessage();
    }
    
    /**
     * Sets the default parameters for this operator
     *
     * The parameters determine the data types of the arguments.  This
     * is an important part of a function
     */
    public void setDefaultParameters(){
      if(SP!=null)
        SP.setDefaultParameters();
    }

    public String getDocumentation(){
        return SP.getDocumentation();
    }

    /**
     * Returns the Command that can be used by the ScriptProcessor to
     * execute this script
     *
     *NOTE: This is determined by the filename
     */
    public String getCommand(){
        return SP.getCommand();
    }
    
    public String getFileName(){
        return SP.getFileName();
    }

    public int getErrorCharPos(){
        return SP.getErrorCharPos();
    }

    public int getErrorLine(){
        return SP.getErrorLine();
    }
    
    /**
     * Gives the number of arguments to this "Script" function
     */
    public int getNum_parameters(){
        return SP.getNum_parameters();
    }

    public boolean setParameter(IParameter parameter, int index){
        return SP.setParameter( parameter,index);
    }
    
    public IParameter getParameter( int index){
        return SP.getParameter( index );
    }

    public void addParameter( IParameter P) throws IllegalArgumentException{
        throw new IllegalArgumentException("Cannot add parameters to scripts");
    }
    
    public void CopyParametersFrom( Operator op){
        SP.CopyParametersFrom( op );
    }
    
    public void addPropertyChangeListener( PropertyChangeListener pl ){
        SP.addPropertyChangeListener( pl );
    }

    public void addPropertyChangeListener( String property,
                                           PropertyChangeListener pl ){
        SP.addPropertyChangeListener( property, pl );
    }

    public void removePropertyChangeListener(PropertyChangeListener listener){
        SP.removePropertyChangeListener( listener);
    }

    public void addIObserver( IObserver iobs ){
        SP.addIObserver( iobs );
    }
    
    
    public void deleteIObserver( IObserver iobs ){
        SP.deleteIObserver( iobs ) ; 
    }

    /**
     * deletes all the Iobserver 
     */
    public void deleteIObservers(){
        SP.deleteIObservers() ;   
    }
    
    /**
     * Sets the document to log information
     *
     * @param doc the document that gets the log information
     *
     * NOTE: This document in the future will allow reexecuting a
     * session
     */
    public void setLogDoc( Document doc ){
        SP.setLogDoc(doc );
    }
    
    /**
     * Executes the script and returns the result
     */
    public Object getResult(){
        return SP.getResult();
    }

    /**
     * Allows running of Scripts without Isaw and/or the CommandPane
     */
    public static void main( String args [] ){
        DataSetTools.util.SharedData sd = new DataSetTools.util.SharedData();

        if( args == null)
            System.exit( 0 );
        if( args.length < 1)
            System.exit( 0 );
        ScriptOperator SO=null;
        try{
          SO = new ScriptOperator( args[ 0 ] );
        }catch(InstantiationError e){
          System.out.println("ERROR:"+e.getMessage());
          System.exit(-1);
        }
        if( SO!=null && SO.getErrorMessage().length() > 0){
            System.out.println("Error ="+args[0]+"--"+SO.getErrorMessage());
            System.exit(-1);
        }
        boolean dialogbox=false;
        if( SO.getNum_parameters() > 0){
            JParametersDialog JP = new JParametersDialog(SO, null, null, null );
            JP.addWindowListener( new WindowAdapter(){
                public void windowClosed(WindowEvent e){
                  System.exit(0);
                }
              } );
            dialogbox=true;
        }else{
            Object XX = SO.getResult();
            System.out.println("Result =" +XX );        
        }

        if( SO != null)
            if( SO.getErrorMessage() != null )
                if( SO.getErrorMessage().length() > 0)
                    System.out.println("An Error occurred "
                                       +SO.getErrorMessage());
        if(!dialogbox)System.exit( 0 );
    }
}
