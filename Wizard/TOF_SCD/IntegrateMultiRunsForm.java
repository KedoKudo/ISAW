/*
 * File:  IntegrateMultiRunsForm.java
 *
 * Copyright (C) 2003, Chris M. Bouzek
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
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 *           Chris M. Bouzek <coldfusion78@yahoo.com>
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 * 
 * $Log$
 * Revision 1.47  2009/09/28 23:21:48  vickie
 * Use Shoebox integration for small peaks < max_shoebox
 *
 * Revision 1.46  2006/09/28 23:21:48  rmikk
 * Specifically set forward slashes on all filenames
 *
 * Revision 1.45  2006/07/10 16:26:14  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.44  2006/02/26 00:09:38  dennis
 * Removed unused constant.
 *
 * Revision 1.43  2005/08/26 18:18:39  rmikk
 * Made MaxIToSogI the default integrate choice
 *
 * Revision 1.42  2005/05/27 04:12:57  dennis
 * Fixed javadoc comment for createIntegrateOperators() method.
 *
 * Revision 1.41  2005/03/06 03:13:09  dennis
 * Fixed error in parameter name in javadoc comment.
 *
 * Revision 1.40  2005/03/06 00:31:49  dennis
 * Added d_min as a parameter to this wizard form.
 *
 * Revision 1.39  2005/01/10 15:47:32  dennis
 * Removed unused imports.
 *
 * Revision 1.38  2004/08/19 19:22:35  rmikk
 * Now uses the Global logging facility to save the log information
 *
 * Revision 1.37  2004/08/17 05:18:00  rmikk
 * Fixed an error in the parameters
 *
 * Revision 1.36  2004/08/06 14:41:47  rmikk
 * Now uses the new Integrate1 class.  This class allows for several different
 * integrate one Peak options, including an experimental option.
 *
 * Revision 1.35  2004/05/21 19:03:21  dennis
 * Changed instrument name from SCD to SCD0.  This was needed to fix
 * file name problems when SCD run numbers went from 9999 to 10000.
 * The runfile names went from SCD09999.RUN to SCD010000.RUN, not
 * SCD10000.RUN
 *
 * Revision 1.34  2004/03/15 03:37:40  dennis
 * Moved view components, math and utils to new source tree
 * gov.anl.ipns.*
 *
 * Revision 1.33  2004/02/25 00:45:46  bouzekc
 * Calls clear() from ChooserPG to eliminate references to the DataSet
 * inside the loop.  This should eliminate the memory leak, as
 * DataSets are no longer being consecutively added to the list.
 *
 * Revision 1.32  2004/02/11 04:10:55  bouzekc
 * Uses the new Wizard classes that have indeterminate progress bars.
 *
 * Revision 1.31  2004/01/09 00:17:08  bouzekc
 * Reformatted and removed unused imports.
 *
 * Revision 1.30  2003/12/15 02:17:29  bouzekc
 * Removed unused imports.
 *
 * Revision 1.29  2003/11/05 02:20:30  bouzekc
 * Changed to work with new Wizard and Form design.
 *
 * Revision 1.28  2003/10/27 01:30:58  bouzekc
 * Result parameter is now the last parameter.  This is to facilitate
 * remote execution.
 *
 * Revision 1.27  2003/10/26 19:17:37  bouzekc
 * Now returns the name of the file written rather than Boolean.TRUE when
 * getResult() executes successfully.
 *
 * Revision 1.26  2003/09/20 23:11:18  dennis
 * Minor change to some prompt strings.
 *
 * Revision 1.25  2003/09/15 22:23:12  dennis
 * made boxDeltaX and boxDeltaY Strings, as needed by the IntArrayPG.
 *
 * Revision 1.24  2003/09/15 18:02:45  bouzekc
 * Added parameters for Integrate's new "shoe box" parameters.
 *
 * Revision 1.23  2003/09/11 21:22:32  bouzekc
 * Updated to work with new Form class.
 *
 * Revision 1.22  2003/08/28 20:55:27  bouzekc
 * Set histDS and Operator DataSet parameters to null when they are not used.
 * This should avoid an out of memory error.
 *
 * Revision 1.21  2003/07/14 16:34:15  bouzekc
 * Made integrated peaks file parameter's initial value empty.
 *
 * Revision 1.20  2003/07/14 15:35:09  bouzekc
 * Made run numbers parameter non-constant in all cases.
 *
 * Revision 1.19  2003/07/09 14:20:11  bouzekc
 * No longer has a specific default directory for the SCD
 * instprm.dat file.
 *
 * Revision 1.18  2003/07/08 23:08:13  bouzekc
 * Removed brackets from within getDocumentation().
 *
 * Revision 1.17  2003/07/03 14:30:32  bouzekc
 * Added all missing javadoc comments and formatted existing
 * comments.  Arranged methods according to access privileges.
 *
 * Revision 1.16  2003/06/25 20:25:37  bouzekc
 * Unused private variables removed, reformatted for
 * consistency.
 *
 * Revision 1.15  2003/06/19 16:21:15  bouzekc
 * Changed SCD calibration file line to be a constant parameter
 * when the HAS_CONSTANTS flag is on.
 *
 * Revision 1.14  2003/06/18 23:34:25  bouzekc
 * Parameter error checking now handled by superclass Form.
 *
 * Revision 1.13  2003/06/18 19:57:21  bouzekc
 * Uses super.getResult() for initializing PropertyChanger
 * variables.
 *
 * Revision 1.12  2003/06/17 20:35:44  bouzekc
 * Fixed setDefaultParameters so all parameters have a
 * visible checkbox.  Added more robust error checking on
 * the raw and output directory parameters.  Fixed progress
 * bar bug.
 *
 * Revision 1.11  2003/06/17 17:06:30  bouzekc
 * Now uses InstrumentType.formIPNSFileName to get the
 * file name.  Changed to work with new PropChangeProgressBar.
 *
 * Revision 1.10  2003/06/16 23:04:31  bouzekc
 * Now set up to use the multithreaded progress bar in
 * DataSetTools.components.ParametersGUI.
 *
 * Revision 1.9  2003/06/11 23:04:07  bouzekc
 * No longer uses StringUtil.setFileSeparator as DataDirPG
 * now takes care of this.
 *
 * Revision 1.8  2003/06/11 22:45:19  bouzekc
 * Moved calls to setFileSeparator() out of the loop.
 *
 * Revision 1.7  2003/06/10 19:55:27  bouzekc
 * Moved Operator creation into a private method to avoid
 * excessive Object recreation.
 * Added parameter to specify SCD calibration file line.
 * Updated documentation.
 *
 * Revision 1.6  2003/06/09 21:53:23  bouzekc
 * Updated documentation.
 * Added constructor to set HAS_CONSTANTS to reduce
 * the number of calls to setDefaultParameters().
 * Removed unused matrix name parameter and associate code.
 *
 * Revision 1.5  2003/06/06 15:12:02  bouzekc
 * Added log message header to file.
 *
 */
package Wizard.TOF_SCD;

import DataSetTools.dataset.DataSet;

import DataSetTools.instruments.InstrumentType;

import DataSetTools.operator.DataSet.Attribute.LoadSCDCalib;

import DataSetTools.operator.Generic.Load.LoadOneHistogramDS;
import DataSetTools.operator.Generic.TOF_SCD.Integrate_new;
import DataSetTools.parameter.DataSetPG;

import gov.anl.ipns.Parameters.*;

import DataSetTools.util.FilenameUtil;
import DataSetTools.util.SharedData;

import DataSetTools.wizard.Form;

import gov.anl.ipns.Parameters.BooleanPG;
import gov.anl.ipns.Parameters.ChoiceListPG;
import gov.anl.ipns.Parameters.DataDirPG;
import gov.anl.ipns.Parameters.FloatPG;
import gov.anl.ipns.Parameters.IntArrayPG;
import gov.anl.ipns.Parameters.IntegerPG;
import gov.anl.ipns.Parameters.LoadFilePG;
import gov.anl.ipns.Parameters.StringPG;
import gov.anl.ipns.Util.Numeric.IntList;
import gov.anl.ipns.Util.SpecialStrings.ErrorString;

import java.util.Vector;
import Operators.TOF_SCD.*;
/**
 * This Form is a "port" of the script used to integrate multiple SCD runs.  It
 * "knows" to apply the lsxxxx.expName.mat file to the SCDxxxx.run in the
 * peaks file.
 */
public class IntegrateMultiRunsForm extends Form {

  //~ Instance fields **********************************************************

  private Vector choices;
  protected final String SCDName = "SCD0";
  private LoadOneHistogramDS loadHist;
  private Integrate1 integrate;
  private LoadSCDCalib loadSCD;

  //~ Constructors *************************************************************

  /**
   * Construct a Form with a default parameter list.
   */
  public IntegrateMultiRunsForm(  ) {
    super( "IntegrateMultiRunsForm" );
    this.setDefaultParameters(  );
  }

  /**
   * Construct a Form using the default parameter list.
   *
   * @param hasConstParams boolean indicating whether this Form should have
   *        constant parameters.
   */
  public IntegrateMultiRunsForm( boolean hasConstParams ) {
    super( "IntegrateMultiRunsForm", hasConstParams );
    this.setDefaultParameters(  );
  }

  /**
   * Full constructor for IntegrateMultiRunsForm.
   *
   * @param rawpath The raw data path.
   * @param outpath The output data path for the .integrate file.
   * @param runnums The run numbers to load.
   * @param expname The experiment name (i.e. "quartz").
   * @param ctype Number for the centering type.
   * @param calibfile SCD calibration file.
   * @param time_slice_range The time-slice range
   * @param increase_amt Amount to increase slice size by.
   * @param d_min  The minimum d-spacing for peaks that are
   *               integrated.
   * @param line2use SCD calibration file line to use.
   * @param append Append to file (yes/no).
   */
  public IntegrateMultiRunsForm( 
          String  rawpath, 
          String  outpath, 
          String  runnums,  
          String  expname, 
          int     ctype,
          String  calibfile, 
          String  time_slice_range, 
          int     increase_amt, 
          float   d_min,
          int     line2use,
          boolean append ) {
    this(  );
    getParameter(  0 ).setValue( rawpath );
    getParameter(  1 ).setValue( outpath );
    getParameter(  2 ).setValue( runnums );
    getParameter(  3 ).setValue( expname );
    getParameter(  4 ).setValue( choices.elementAt( ctype ) );
    getParameter(  5 ).setValue( calibfile );
    getParameter(  6 ).setValue( time_slice_range );
    getParameter(  7 ).setValue( new Integer( increase_amt ) );
    getParameter(  8 ).setValue( new Float( d_min ) );
    getParameter(  9 ).setValue( new Integer( line2use ) );
    getParameter( 10 ).setValue( new Boolean( append ) );
  }

  //~ Methods ******************************************************************

  /**
   * @return the String command used for invoking this Form in a Script.
   */
  public String getCommand(  ) {
    return "INTEGRATEMULTIRUNSFORM";
  }

  /**
   * Attempts to set reasonable default parameters for this form.
   */
  public void setDefaultParameters(  ) {
    parameters = new Vector(  );

    if( ( choices == null ) || ( choices.size(  ) == 0 ) ) {
      init_choices(  );
    }
    addParameter( new DataDirPG( "Raw Data Path", "" ) );           //0
    addParameter( new DataDirPG( "Peaks File Output Path", "" ) );  //1
    addParameter( new IntArrayPG( "Run Numbers", "" ) );            //2
    addParameter( new StringPG( "Experiment name", "quartz" ) );    //3

    ChoiceListPG clpg = 
        	  new ChoiceListPG("Centering Type", choices.elementAt( 0 )); 
    clpg.addItems( choices );
    addParameter( clpg );                                                  //4

    addParameter( new LoadFilePG( "SCD Calibration File", null ) );        //5
    addParameter( new IntArrayPG( "Time-Slice Range", "-1:3" ) );          //6
    addParameter( 
      new IntegerPG( 
        "Amount to Increase Slice Size By", new Integer( 1 ) ) );          //7
    addParameter( new FloatPG( "Minimum d-spacing", new Float(0) ) );      //8
    addParameter( 
      new IntegerPG( 
        "SCD Calibration File Line to Use", new Integer( -1 ) ) );         //9
    addParameter( new BooleanPG( "Append to File?", Boolean.FALSE ) );   //10

    ChoiceListPG 
    clPG= new ChoiceListPG("Integrate 1 peak method",Integrate1.NEW_INTEGRATE);
    clPG.addItem(Integrate1.SHOE_BOX);
    clPG.addItem(Integrate1.TOFINT);
    clPG.addItem( Integrate_new.FIT_PEAK );
    clPG.addItem(Integrate1.EXPERIMENTAL);
    clPG.addItem( Integrate1.OLD_INTEGRATE);
    addParameter(clPG);                                                   //11

    addParameter( new IntArrayPG( "Box Delta x (col) Range", "-2:2" ) );  //12
    addParameter( new IntArrayPG( "Box Delta y (row) Range", "-2:2" ) );  //13
    addParameter( new FloatPG( "Use Shoe Box integration for peaks below this I/sig(I) ratio", new Float(0) ) );      //15
    setResultParam( new LoadFilePG( "Integrated Peaks File ", " " ) );    //14

    if( HAS_CONSTANTS ) {
      setParamTypes( 
        new int[]{ 0, 1, 3, 5, 9 }, new int[]{ 2, 4, 6, 7, 8, 10, 11, 12, 13 },
        new int[]{ 14, 15 } );
    } else {
      setParamTypes( 
        null, new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 },
        new int[]{ 14, 15 } );
    }
  }

  /**
   * @return documentation for this OperatorForm.  Follows javadoc conventions.
   */
  public String getDocumentation(  ) {
    StringBuffer s = new StringBuffer(  );
    s.append( "@overview This Form is designed to find integrate peaks from " );
    s.append( "multiple SCD RunFiles. " );
    s.append( "It \"knows\" to apply the lsxxxx.expName.mat file to the " );
    s.append( "SCDxxxx.run in the peaks file.\n" );
    s.append( "@assumptions It is assumed that:\n" );
    s.append( "1. Data of interest is in histogram 2.\n" );
    s.append( "2. There is a matrix file for each run, in the format \"ls" );
    s.append( "+ experiment name + run number + .mat\" in the same " );
    s.append( "directory as the peaks file\n." );
    s.append( 
           "@algorithm This Form first gets all the user input parameters, " );
    s.append( "then for each runfile, it loads the first histogram, the SCD " );
    s.append( "calibration data, and calls Integrate.\n" );
    s.append( "@param rawpath The raw data path.\n" );
    s.append( 
           "@param outpath The output data path for the *.integrate file.\n" );
    s.append( "@param runnums The run numbers to load.\n" );
    s.append( "@param expname The experiment name (i.e. \"quartz\").\n" );
    s.append( "@param ctype Number for the centering type.\n" );
    s.append( "@param calibfile SCD calibration file.\n" );
    s.append( "@param time_slice_range The time-slice range.\n" );
    s.append( "@param increase_amt Amount to increase slice size by.\n" );
    s.append( "@param d_min  Minimum d-spacing to include\n");
    s.append( "@param line2use SCD calibration file line to use.\n" );
    s.append( "@param append True/false indicating whether to append to the " );
    s.append( ".integrate file.\n" );
    s.append( "@param  Intgrate method, MaxItoSigI-old or new, TOFINT, "); 
    s.append( "or Experimental" );
    s.append( "@return A Boolean indicating success or failure of the Form's ");
    s.append( "execution.\n" );
    s.append( "@error Invalid raw data path.\n" );
    s.append( "@error Invalid peaks file path.\n" );
    s.append( "@error Invalid run numbers.\n" );
    s.append( "@error Invalid experiment name.\n" );
    s.append( "@error Invalid calibration file name.\n" );

    return s.toString(  );
  }

  /**
   * This Form first gets all the user input parameters, then for each runfile,
   * it loads the first histogram, the SCD calibration data, and calls
   * Integrate.
   *
   * @return A Boolean indicating success or failure.
   */
  public Object getResult(  ) {
    SharedData.addmsg( "Executing...\n" );

    IParameterGUI param;
    Object  obj;
    String  outputDir;
    String  matrixName;
    String  calibFile;
    String  expName;
    String  rawDir;
    String  centerType;
    String  integName;
    String  sliceRange;
    String  loadName;
    String  IPNSName;
    boolean append;
    boolean first;
    int     timeSliceDelta;
    float   d_min;
    int     SCDline;
    DataSet histDS;
    int[]   runsArray;
    String  boxDeltaX;
    String  boxDeltaY;
    float   max_shoebox;
    String  IntegMethod;
    //get raw data directory
    param            = ( IParameterGUI )super.getParameter( 0 );
    rawDir           = param.getValue(  ).toString(  );

    //get output directory
    param            = ( IParameterGUI )getParameter( 1 );
    outputDir        = param.getValue(  ).toString(  );
    outputDir = FilenameUtil.setForwardSlash( outputDir );
    //gets the run numbers
    param            = ( IParameterGUI )super.getParameter( 2 );
    runsArray        = IntList.ToArray( param.getValue(  ).toString(  ) );

    //get experiment name
    param            = ( IParameterGUI )getParameter( 3 );
    expName          = param.getValue(  ).toString(  );

    //get centering type - this still needs to be checked here rather than Form
    param            = ( IParameterGUI )getParameter( 4 );
    obj              = param.getValue(  );

    if( obj != null ) {
      centerType = obj.toString(  );
      param.setValidFlag( true );
    } else {
      return errorOut( param, "ERROR: you must enter a valid centering type." );
    }

    //get calibration file name
    param            = ( IParameterGUI )getParameter( 5 );
    calibFile        = param.getValue(  ).toString(  );
    calibFile = FilenameUtil.setForwardSlash( calibFile );
    //get time slice range
    param            = ( IParameterGUI )getParameter( 6 );
    sliceRange       = param.getValue(  ).toString(  );

    //get time slice increase increment
    param            = ( IParameterGUI )getParameter( 7 );
    timeSliceDelta   = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get minimum d-spacing to integrate
    param            = ( IParameterGUI )getParameter( 8 );
    d_min            = ( ( Float )param.getValue(  ) ).floatValue(  );

    //get line number for SCD calibration file
    param            = ( IParameterGUI )super.getParameter( 9 );
    SCDline          = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get append to file value
    param            = ( IParameterGUI )super.getParameter( 10 );
    append           = ( ( BooleanPG )param ).getbooleanValue(  );

    //shoebox parameters
    IntegMethod  = super.getParameter( 11 ).getValue().toString();
    
    param            = ( IParameterGUI )super.getParameter( 12 );
    boxDeltaX        = ( ( IntArrayPG )param ).getStringValue(  );
    param            = ( IParameterGUI )super.getParameter( 13 );
    boxDeltaY        = ( ( IntArrayPG )param ).getStringValue(  );

    //get maximum size peak for Shoe box integration
    param            = ( IParameterGUI )getParameter( 14 );
    max_shoebox         = ( ( Float )param.getValue(  ) ).floatValue(  );

    //the name for the saved *.integrate file
    integName        = outputDir + expName + ".integrate";

    //first time through the file
    first            = true;

    //to avoid excessive object creation, we'll create all of the 
    //Operators here, then just set their parameters in the loop
 
    createIntegrateOperators( 
      calibFile, SCDline, integName, sliceRange, timeSliceDelta, d_min,
      append, centerType, IntegMethod, boxDeltaX, boxDeltaY, max_shoebox );

    //validate the parameters and set the progress bar variables
    Object validCheck = validateSelf(  );

    //had an error, so return
    if( validCheck instanceof ErrorString ) {
      return validCheck;
    }
    String logfile = integName;
    int index=logfile.lastIndexOf("/");
    logfile=logfile.substring(0,index)+"/integrate.log";
    gov.anl.ipns.Util.Sys.SharedMessages.openLog( logfile);
    for( int i = 0; i < runsArray.length; i++ ) {
      IPNSName   = InstrumentType.formIPNSFileName( SCDName, runsArray[i] );
      loadName   = rawDir + IPNSName;
      SharedData.addmsg( "Loading " + loadName + "." );

      //load the histogram
      loadHist.getParameter( 0 ).setValue( loadName );
      obj = loadHist.getResult(  );

      //make sure it is a DataSet
      if( obj instanceof DataSet ) {
        histDS = ( DataSet )obj;
      } else {
        return errorOut( "LoadOneHistogramDS failed: " + obj.toString(  ) );
      }
      SharedData.addmsg( "Integrating peaks for " + loadName );

      //load calibration data 
      loadSCD.setDataSet( histDS );
      obj = loadSCD.getResult(  );
      loadSCD.setDataSet( null );

      if( obj instanceof ErrorString ) {
        return errorOut( "LoadSCDCalib failed: " + obj.toString(  ) );
      }

      //Gets matrix file "lsxxxx.mat" for each run
      //pull the run number off of the IPNS name
      IPNSName     = IPNSName.substring( 
          IPNSName.indexOf( SCDName ) + SCDName.length(  ),
          IPNSName.indexOf( '.' ) );
      matrixName   = outputDir + "ls" + expName + IPNSName + ".mat";
      SharedData.addmsg( "Integrating run " + IPNSName + "." );
      integrate.getParameter( 0 ).setValue( histDS );
      integrate.getParameter( 2 ).setValue( matrixName );
      obj = integrate.getResult(  );
      ( ( DataSetPG )integrate.getParameter( 0 ) ).clear(  );

      if( obj instanceof ErrorString ) {
        return errorOut( "Integrate failed: " + obj.toString(  ) );
      }

      if( first ) {
        first    = false;  //no longer our first time through
        append   = true;  //start appending to the file
      }

      histDS = null;
    }
    SharedData.addmsg( "--- IntegrateMultiRunsForm is done. ---" );
    SharedData.addmsg( "Peaks are listed in " + integName );

    //set the integrate file name for the result
    param = ( IParameterGUI )getParameter( 15 );
    param.setValue( integName.toString(  ) );
    param.setValidFlag( true );

    gov.anl.ipns.Util.Sys.SharedMessages.closeLog( );
    //not really sure what to return
    return integName.toString(  );
  }

  /**
   * Creates the Operators necessary for this Form and sets their constant
   * values.
   *
   * @param calibFile      SCD calibration file.
   * @param SCDline        The line to use from the SCD calib file.
   * @param integName      The name of the .integrate file.
   * @param sliceRange     The time slice range.
   * @param timeSliceDelta Amount to increase slice size by.
   * @param d_min          Minimum d for peaks that are integrated.
   * @param append         Whether to append to peaks file.
   * @param centerType     Centering type.
   * @param IntegMethod    String specifying which integration method 
   *                       to use.
   * @param boxDeltaX      The range of x (delta col) values to use 
   *                       around the peak position
   * @param boxDeltaY      The range of y (delta row) values to use 
   *                       around the peak position
   * @param max_shoebox       Maximum size peak for Shoe box integration
   */
  private void createIntegrateOperators( 
      String  calibFile, 
      int     SCDline, 
      String  integName, 
      String  sliceRange,
      int     timeSliceDelta, 
      float   d_min, 
      boolean append, 
      String  centerType, 
      String  IntegMethod,
      String  boxDeltaX, 
      String  boxDeltaY,
      float   max_shoebox ) {
    loadHist    = new LoadOneHistogramDS(  );
    integrate   = new Integrate1(  );
    loadSCD     = new LoadSCDCalib(  );

    //LoadOneHistogramDS
    //get the histogram.  A value of "1" will retrieve the first histogram
    //DataSet
    loadHist.getParameter( 1 ).setValue( new Integer( 1 ) );

    /*If you want to be able to use a group mask,
       change the "" below to a String variable.
       I've been told this is not used. -CMB*/
    loadHist.getParameter( 2 ).setValue( "" );

    //Integrate
    integrate.getParameter( 1 ).setValue( integName );
    integrate.getParameter( 3 ).setValue( centerType );
    integrate.getParameter( 4 ).setValue( sliceRange );
    integrate.getParameter( 5 ).setValue( new Integer( timeSliceDelta ) );
    integrate.getParameter( 6 ).setValue( new Float( d_min ) );

    integrate.getParameter( 7 ).setValue( new Integer( 1 ) );
     
    integrate.getParameter( 8 ).setValue( new Boolean( append ) );
    integrate.getParameter( 9 ).setValue( IntegMethod);
    integrate.getParameter( 10 ).setValue( boxDeltaX );
    integrate.getParameter( 11 ).setValue( boxDeltaY );
//
//  integrate.getParameter( 12 ).setValue( new Float( max_shoebox ) );
//
//  NOTE integrate is an instance of the "old" IPNS Integrate1 class
//       and does not have a parameter 12. 
//

    //LoadSCDCalib
    loadSCD.getParameter( 0 ).setValue( calibFile );
    loadSCD.getParameter( 1 ).setValue( new Integer( SCDline ) );
    loadSCD.getParameter( 2 ).setValue( "" );
  }

  /**
   * Create the vector of choices for the ChoiceListPG of centering.
   */
  private void init_choices(  ) {
    choices = new Vector(  );
    for ( int i = 0; i < IntegrateUtils.CenteringNames.length; i++)
      choices.add(IntegrateUtils.CenteringNames[i]);
  }
}
