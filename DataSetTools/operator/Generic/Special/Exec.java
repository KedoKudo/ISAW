/*
 * File:  Exec.java   
 *
 * Copyright (C) 2002, Peter F. Peterson
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
 * Modified:
 *
 *  $Log$
 *  Revision 1.19  2007/07/04 20:28:47  dennis
 *  Removed some unused variables, and did some minor format fixes.
 *
 *  Revision 1.18  2007/06/22 16:03:26  rmikk
 *  For cases with no input to the process, created a ByteArrayInputStream with
 *    a 0 byte length buffer and did not close.  This prevented an exception in
 *    unix
 *
 *  Revision 1.17  2007/06/22 15:49:14  rmikk
 *  Fixed the reporting of exceptions
 *
 *  Revision 1.16  2007/06/22 15:29:12  rmikk
 *  Eliminated System.out and Sytem.err in arguments to monq.stuff.Exec so
 *    these streams will not be closed. Used their defaults when null is passed in.
 *
 *  Revision 1.15  2007/06/22 14:13:27  rmikk
 *  Replaced the System.in for input to the exec by a closed ByteArrayInputStream to prevent hanging in this case
 *
 *  Replaced a while by an if
 *
 *  Revision 1.14  2007/06/21 21:16:03  dennis
 *  Now passes System.in to the lower level Exec() object constructor.
 *  This fixes null pointer problem if an input file was not passed
 *  in to this Operator.
 *
 *  Revision 1.13  2007/06/21 20:52:26  rmikk
 *  Fixed the getDocumentation to reflect the addition of an imput file to the
 *    command.
 *
 *  Revision 1.12  2007/06/21 20:36:05  rmikk
 *  Used the exec command with an array of commands so filenames with
 *    spaces do not cause problems in windows(XP) and Dennis' Linux
 *
 *  Revision 1.11  2007/06/21 16:47:54  rmikk
 *  Added an input file parameter and used the new Exec in ExtTools.monq.stuff
 *    to execute the external program.  This Exec used threads an asynchronous
 *    input and output.  A hung application must be terminated by an outside
 *    source to continue
 *
 *  Revision 1.10  2005/01/10 15:18:17  dennis
 *  Added getCategoryList method to put operator in new position in
 *  menus.
 *
 *  Revision 1.9  2004/01/24 19:58:59  bouzekc
 *  Removed/commented out unused variables/imports.
 *
 *  Revision 1.8  2003/12/15 01:56:37  bouzekc
 *  Removed unused imports.
 *
 *  Revision 1.7  2003/01/23 19:28:54  dennis
 *  Added getDocumentation() method and java docs on getResult().
 *  (Chris Bouzek)
 *
 *  Revision 1.6  2002/11/27 23:21:43  pfpeterson
 *  standardized header
 *
 *  Revision 1.5  2002/09/17 22:32:28  pfpeterson
 *  Modified to take advantage of new SysUtil class.
 *
 *  Revision 1.4  2002/07/29 18:22:56  pfpeterson
 *  Started working out the details of calling code interactively.
 *
 *  Revision 1.3  2002/07/29 16:35:09  pfpeterson
 *  Now redirects stderr from the process to the console as well.
 *
 *  Revision 1.2  2002/07/29 16:00:07  pfpeterson
 *  Fixed two bugs:
 *   - Confirms that process was started before returning.
 *   - Stops printing once encounters a null string.
 *
 *  Revision 1.1  2002/07/26 22:45:32  pfpeterson
 *  Added to CVS.
 */

package DataSetTools.operator.Generic.Special;

import java.io.*;
import java.util.Vector;

import Command.ScriptUtil;
import DataSetTools.operator.*;
import DataSetTools.util.SharedData;
import gov.anl.ipns.Parameters.*;
import gov.anl.ipns.Util.SpecialStrings.*;
//import ExtTools.monq.stuff.*;

/**
 * This operator makes system calls. It does grab the process's STDOUT
 * stream and prints it to the screen, otherwise it blindly calls the
 * method and returns the result (an integer). The method called is
 * assumed to be run without interaction. If it requires interaction
 * ISAW WILL HANG.
 */
public class Exec extends    GenericSpecial {

    /* ----------------------- DEFAULT CONSTRUCTOR ------------------------- */
    /**
     * Construct an operator with a default parameter list.
     */
    public Exec( ){
        super( "Execute" );
    }

    /* ---------------------- FULL CONSTRUCTOR ---------------------------- */
    /**
     *  Construct operator to execute a system command.
     *
     *  @param  command  The command to be executed.
     */
    
    public Exec( String command ){
        this();
        
        parameters=new Vector();
        addParameter(new Parameter("Command",command));
    }

    
    /* ------------------------- setDefaultParameters ----------------------- */
    /**
     *  Set the parameters to default values.
     */
    public void setDefaultParameters(){
        parameters = new Vector();  // must do this to create empty list of 
                                    // parameters

        parameters=new Vector();
        addParameter( new LoadFilePG("Command",""));
        addParameter( new LoadFilePG( "InputFileName", ""));
        addParameter( new DataDirPG("Working Directory", null));
    }


  /* ------------------------ getCategoryList ------------------------------ */
  /**
   * Get an array of strings listing the operator category names  for 
   * this operator. The first entry in the array is the 
   * string: Operator.OPERATOR. Subsequent elements of the array determine
   * which submenu this operator will reside in.
   * 
   * @return  A list of Strings specifying the category names for the
   *          menu system 
   *        
   */
  public String[] getCategoryList()
  {
    return Operator.UTILS_SYSTEM;
  }

    
    /* --------------------------- getCommand ------------------------------ */
    /**
     * @return	the command name to be used with script processor.
     */
    public String getCommand(){
        return "Exec";
    }

    /* ---------------------- getDocumentation --------------------------- */
    /**
     *  Returns the documentation for this method as a String.  The format
     *  follows standard JavaDoc conventions.
     */
    public String getDocumentation()
    {
      StringBuffer s = new StringBuffer("");
      s.append("@overview This operator makes system calls.\n"); 
      s.append("@assumptions The command  output and errors appear in the terminal \n");
      s.append("@algorithm This operator calls the specified command with th given" +
            " input file(if there is one).  It ");
      s.append("also grabs the process's STDOUT stream (if available) and ");
      s.append("prints it to the screen.\n");
      s.append("@param command  The command to be executed.\n");
      s.append("@param InputFile  The data to be input or a blank string\n");
      s.append("@param WorkingDir  The working directory or a blank string\n");
      s.append("@return Integer object which represents the exit value of the ");
      s.append("command.\n");
      s.append("@error If the command may hang.  Use op system kill command to" +
            "   terminate and return to ISAW.(Windows task Manager can kill jobs)");
      s.append("@error Returns an error if the process is interrupted.\n");
      s.append("@error Returns an error message if any input/output errors ");
      s.append("occur.\n");
      s.append("@error Returns an error message if the process could not be ");
      s.append("started.\n");
      return s.toString();
    }

    /* --------------------------- getResult ------------------------------- */
    /*  Grabs the process's STDOUT stream and prints it to the screen.  If no 
     *  STDOUT stream is available, it blindly calls the method associated 
     *  with the process.
     *  @return Integer object which represents the exit value of the 
     *  command.
     */
    public Object getResult(){
        String command=(String)(getParameter(0).getValue());

        String InputFile = getParameter(1).getValue().toString();
        String WorkingDir = getParameter(2).getValue().toString();
        if( WorkingDir != null && WorkingDir.length()<1)
           WorkingDir = null;
        if( InputFile == null || InputFile.length() < 1)
           InputFile = null;
        else if( !(new java.io.File( InputFile)).exists())
           InputFile = null;
        Process process=null;
    
        // Put in an array so command name does not have to parse a command
        //  line
        String[] cmd ={command};
        File WorkingFile = null;
        if( WorkingDir != null){
           WorkingFile = new File( WorkingDir);
           if( !WorkingFile.exists())
              WorkingFile = null;
        }
        try{
         if( WorkingFile != null)        
            process = Runtime.getRuntime().exec( cmd, null ,WorkingFile); 
         else      
            process = Runtime.getRuntime().exec( cmd, null);
      
         InputStream fin = null;
         if( InputFile != null ) {
            fin = new FileInputStream( InputFile );
         }else{
            fin = new ByteArrayInputStream( new byte[0]);
            //fin.close();        
         }
       
         ExtTools.monq.stuff.Exec ex = null;
         ex = new ExtTools.monq.stuff.Exec(process, fin, null, null);
         
         if(!ex.done()){
         }
         
         System.out.println( ex.getOutputText());
         String ErrText = ex.getErrorText();
         if( ErrText != null  && ErrText.trim().length()> 0){
            System.out.println("------------- Error Text---------------");
            System.out.println( ErrText );
            System.out.println("------------- End Error Text---------------");
         }
         
         if( ex.ok())
            return "Finished OK";

         if( !ex.finished())
            return new ErrorString("Application did not finish");
         
         Exception except = ex.getException();
         if( except == null)
            return  new ErrorString(
                     "Application terminated with a non-zero exit status");
         
         
         String[] trace = ScriptUtil.GetExceptionStackInfo( except, true, 3);
         String errString ="Error of "+except.getClass()+"type  ocurred:"+
                                                         except.getMessage()+"\n";
         if(trace != null)
            for( int i=0; i < trace.length; i++)
               errString +="    "+ trace[i]+"\n";
        
         return new gov.anl.ipns.Util.SpecialStrings.ErrorString(  errString );
      }
      catch( IOException e ) {
            SharedData.addmsg("IOException reported: "+e.getMessage());
            return new gov.anl.ipns.Util.SpecialStrings.ErrorString(
                                          "IOException reported: "+e.getMessage());
        }
    }
    
}