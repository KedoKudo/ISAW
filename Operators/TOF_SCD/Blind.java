/*
 * File:  Blind.java   
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
 *  Revision 1.14  2003/04/02 19:50:26  dennis
 *  Added minimal getDocumentation() method. (Mike Miller)
 *
 *  Revision 1.13  2003/02/18 22:59:00  pfpeterson
 *  Updated calls to deprecated method fixSparator.
 *
 *  Revision 1.12  2003/01/02 21:54:07  pfpeterson
 *  Fixed small error in full constructor.
 *
 *  Revision 1.11  2002/11/27 23:31:01  pfpeterson
 *  standardized header
 *
 *  Revision 1.10  2002/11/21 21:54:38  pfpeterson
 *  Updated to use new error checking feature in SysUtil.
 *
 *  Revision 1.9  2002/11/01 15:50:18  pfpeterson
 *  Commented out lines which check if the directory is writable
 *  (win32 java bug), modified returned ErrorStrings, only looks
 *  for the wrapped code on the first call (saved result in a
 *  static String).
 *
 *  Revision 1.8  2002/10/23 19:09:09  pfpeterson
 *  Reformatted
 *
 *  Revision 1.7  2002/10/23 19:05:51  pfpeterson
 *  Changed to use IParameterGUI.
 *
 *  Revision 1.6  2002/10/14 16:49:46  pfpeterson
 *  Added some more comments, made ErrorStrings slightly more useful,
 *  and hardwired that the output file will be 'blind.mat'.
 *
 *  Revision 1.5  2002/10/07 18:41:59  pfpeterson
 *  Made getResult() more windows friendly.
 *
 *  Revision 1.4  2002/10/02 21:59:30  pfpeterson
 *  Fixed bug where it wouldn't try to execute on windows machines.
 *
 *  Revision 1.3  2002/09/30 20:17:41  pfpeterson
 *  Added support for windows executable.
 *
 *  Revision 1.2  2002/09/30 14:45:50  pfpeterson
 *  Changed the unsuccesful returns to be ErrorString and the
 *  successful return to be the name of the saved matrix file.
 *
 *  Revision 1.1  2002/09/17 22:31:55  pfpeterson
 *  Added to CVS.
 *
 *
 *   
 */

package Operators.TOF_SCD;

import  java.io.*;
import  java.util.Vector;
import  DataSetTools.util.*;
import  DataSetTools.operator.Parameter;
import  DataSetTools.operator.Generic.TOF_SCD.*;
import  DataSetTools.parameter.*;

/**
 * This operator is intended to run A.J. Schultz's "blind"
 * program. This is not heavily tested but works fairly well.
 */
public class Blind extends    GenericTOF_SCD {
  private static String command=null;
  /* ----------------------- DEFAULT CONSTRUCTOR ------------------------- */
  /**
   * Construct an operator with a default parameter list.
   */
  public Blind( ){
    super( "Blind" );
  }
  
  /* ---------------------- FULL CONSTRUCTOR ---------------------------- */
  /**
   *  Construct operator to execute blind
   *
   *  @param file     The peaks file to use with blind
   *  @param seq_nums The sequence numbers of peaks to use
   */
  public Blind( LoadFileString file, IntListString seq_nums ){
    this();
    
    getParameter(0).setValue(file.toString());
    getParameter(1).setValue(seq_nums.toString());
  }
  
    
  /* ------------------------- setDefaultParmeters ----------------------- */
  /**
   *  Set the parameters to default values.
   */
  public void setDefaultParameters(){
    parameters = new Vector();  // must do this to create empty list of 
    // parameters
    
    parameters=new Vector();
    LoadFilePG lfpg=new LoadFilePG("Peaks File",null);
    lfpg.setFilter(new PeaksFilter());
    addParameter(lfpg);
    addParameter( new IntArrayPG("Sequence Numbers",null));
  }

 /* ---------------------------getDocumentation--------------------------- */
 /**
  *  Returns a string of the description/attributes of Blind
  *   for a user activating the Help System
  */
  public String getDocumentation()
  {
    StringBuffer s = new StringBuffer();
                                                                              //      
    s.append("@overview This operator is intended to run A.J. Schultz's ");
    s.append("\"blind\" program. This is not heavily tested but works ");
    s.append("fairly well.\n");

    s.append("@algorithm (Sorry, no further documentation at this time.)\n");
    
    s.append("@param file  The peaks file to use with blind ");
    s.append("@param seq_nums  The sequence numbers of peaks to use "); 
    
    return s.toString();
    
  }
  
  /* --------------------------- getCommand ------------------------------ */
  /**
   * @return the command name to be used with script processor, in
   * this case Blind.
   */
  public String getCommand(){
    return "Blind";
  }
  
  /* --------------------------- getResult ------------------------------- */
  /*
   * Runs blind.
   */
  public Object getResult(){
    ErrorString eString   = null;
    String      peaksfile = (getParameter(0).getValue()).toString();
    String      seq_nums  = (getParameter(1).getValue()).toString();
    int         index;
    String      direc;
    String      matfile   = "blind.mat";
    int         seqs[]    = IntList.ToArray(seq_nums);
    
    // first check if the OS is acceptable
    if(! SysUtil.isOSokay(SysUtil.LINUX_WINDOWS) )
      return new ErrorString("must be using linux or windows system");
    
    // confirm that the name of the peaksfile is a non-null string
    if( peaksfile==null || peaksfile.length()==0 )
      return new ErrorString("must specify a peaks file");
    
    // standardize the peaks filename
    peaksfile=FilenameUtil.setForwardSlash(peaksfile);
    
    // then confirm the peaks file exists
    if(! SysUtil.fileExists(peaksfile) )
      return new ErrorString("peaks file does not exist");
    
    // find out the file directory
    index=peaksfile.lastIndexOf("/");
    if(index>0){
      direc=peaksfile.substring(0,index);
    }else{
      return new ErrorString("directory not found");
    }
    peaksfile=peaksfile.substring(index+1);
    
    // confirm that the directory is writable DOES NOT WORK ON WIN32
    /*File dirF=new File(direc);
      if(! dirF.canWrite() )
      return new ErrorString("cannot write to specified directory "+dirF);*/
    
    // strip the end off of the peaks filename
    index=peaksfile.lastIndexOf(".peaks");
    if(index>0){
      peaksfile=peaksfile.substring(0,index);
    }
    
    // declare some things
    Process process=null;
    String output=null;
    File dir=new File(direc);
    if(command==null){
	command=this.getFullBlindName();
    }
    
    // exit out early if no blind executable found
    if(command==null)
      return new ErrorString("could not find blind executable");
    
    try{
      process=SysUtil.startProcess(command,direc);
      BufferedReader in=SysUtil.getSTDINreader(process);
      BufferedReader err=SysUtil.getSTDERRreader(process);
      BufferedWriter out=SysUtil.getSTDOUTwriter(process);
      
      // skip over the first couple of lines
      SysUtil.jumpline(in,err,"LAUE INDEXER");
      
      // We are going to use a peaks file
      output=SysUtil.readline(in,err);
      while( output==null || output.indexOf("Input reflection from")<0 ){
        if( output!=null && output.length()>0){
          System.out.println(output);
        }
        output=SysUtil.readline(in,err);
      }
      SysUtil.writeline(out,"y");
      System.out.println(output+"y");
      
      // enter the name of the peaks file
      output=SysUtil.readline(in,err);
      while( output==null || output.indexOf("Experiment name")<0 ){
        if(output!=null) System.out.print(output);
        output=SysUtil.readline(in,err);
      }
      SysUtil.writeline(out,peaksfile);
      System.out.println(output+peaksfile);
      
      // enter the reflections
      for(int i=0 ; i<seqs.length ; i++ ){
        output=SysUtil.readline(in,err);
        SysUtil.writeline(out,Integer.toString(seqs[i]));
        System.out.println(output+seqs[i]);
      }
      output=SysUtil.readline(in,err);
      SysUtil.writeline(out,"");
      System.out.println(output);
      
      // print out all the other information give from program
      output=SysUtil.readline(in,err);
      while( output==null || (output.indexOf("STORE THE MATRIX")<0
                              && output.indexOf("PROGRAM TERMINATING")<0)){
        if(output!=null) System.out.println(output);
        output=SysUtil.readline(in,err);
      }
      if(output.indexOf("TERMINATING")>0){
        while( output==null || output.indexOf("D=")<0 ){
          if(output!=null) System.out.println(output);
          output=SysUtil.readline(in,err) ;
        }
        System.out.println(output);
        return new ErrorString("Abnormal termination of Blind");
      }
      
      // save to a matrix file
      SysUtil.writeline(out,"y");
      System.out.println(output+"y");
      output=SysUtil.readline(in,err);
      SysUtil.writeline(out,"1");      // must choose (1) since experiment
      System.out.println(output+"1");  // file does not exist
      output=SysUtil.readline(in,err);
      SysUtil.writeline(out,matfile);
      System.out.println(output+matfile);
      
      // keep writing out information until the last line
      output=SysUtil.readline(in,err);
      SysUtil.jumpline(in,err,"To analyze the cell");
      
      // wait for the process to terminate
      process.waitFor();
      if(process.exitValue()!=0)
        return new ErrorString("("+process.exitValue()+")");
    }catch(IOException e){
      SharedData.addmsg("IOException reported: "+e.getMessage());
      process=null;
    }catch(InterruptedException e){
      SharedData.addmsg("InterruptedException reported: "+e.getMessage());
      process=null;
    }

    if(process==null){
      return new ErrorString("Something went wrong");
    }else if(eString!=null){
      return eString;
    }else{
      return direc+'/'+matfile;
    }
  }  
  
  /* ------------------------------ clone ------------------------------- */
  /**
   * Get a copy of the current SpectrometerEvaluator Operator. The
   * list of parameters is also copied.
   */
  
  public Object clone(){
    Blind new_op = new Blind( );
    
    new_op.CopyParametersFrom( this );
    
    return new_op;
  }
  
  /* ------------------------------ PRIVATE METHODS -------------------- */
  /**
   * Method to get the location of the blind executable. Assumed to be
   * right next to the class file.
   */
  private String getFullBlindName(){
    if(SysUtil.isOSokay(SysUtil.LINUX_ONLY)){
      return SysUtil.getBinLocation(this.getClass(),"blind");
    }else if(SysUtil.isOSokay(SysUtil.WINDOWS_ONLY)){
      return SysUtil.getBinLocation(this.getClass(),"blind.exe");
    }else{
      return null;
    }
  }
  
  /* --------------------------- MAIN METHOD --------------------------- */
  public static void main(String[] args){
    LoadFileString file=new LoadFileString("/IPNShome/pfpeterson/ISAW/"
                                     +"Operators/TOF_SCD/quartz_isaw.peaks");
    IntListString seq_nums=new IntListString("1:5");
    
    Blind op;
    
    op=new Blind(file,seq_nums);
    System.out.println("RESULT: "+op.getResult());
    System.exit(0);
  }
}
