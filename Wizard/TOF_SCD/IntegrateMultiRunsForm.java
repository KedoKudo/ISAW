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

import DataSetTools.operator.DataSet.Attribute.*;
import DataSetTools.operator.DataSet.Math.Analyze.*;

import DataSetTools.operator.Generic.Load.LoadOneHistogramDS;
import DataSetTools.operator.Generic.TOF_SCD.*;

import DataSetTools.parameter.*;

import DataSetTools.util.*;

import DataSetTools.wizard.*;

import java.io.File;

import java.util.Vector;


/**
 * This Form is a "port" of the script used to integrate multiple SCD runs.  It
 * "knows" to apply the lsxxxx.expName.mat file to the SCDxxxx.run in the
 * peaks file.
 */
public class IntegrateMultiRunsForm extends Form {
  //~ Static fields/initializers ***********************************************

  protected static int RUN_NUMBER_WIDTH = 5;

  //~ Instance fields **********************************************************

  private Vector choices;
  protected final String SCDName        = "SCD";
  private LoadOneHistogramDS loadHist;
  private Integrate integrate;
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
   * @param line2use SCD calibration file line to use.
   * @param append Append to file (yes/no).
   */
  public IntegrateMultiRunsForm( 
    String rawpath, String outpath, String runnums, String expname, int ctype,
    String calibfile, String time_slice_range, int increase_amt, int line2use,
    boolean append ) {
    this(  );
    getParameter( 0 )
      .setValue( rawpath );
    getParameter( 1 )
      .setValue( outpath );
    getParameter( 2 )
      .setValue( runnums );
    getParameter( 3 )
      .setValue( expname );
    getParameter( 4 )
      .setValue( choices.elementAt( ctype ) );
    getParameter( 5 )
      .setValue( calibfile );
    getParameter( 6 )
      .setValue( time_slice_range );
    getParameter( 7 )
      .setValue( new Integer( increase_amt ) );
    getParameter( 8 )
      .setValue( new Integer( line2use ) );
    getParameter( 9 )
      .setValue( new Boolean( append ) );
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

    //0
    addParameter( new DataDirPG( "Raw Data Path", "", false ) );

    //1
    addParameter( new DataDirPG( "Peaks File Output Path", "", false ) );

    //2
    addParameter( new IntArrayPG( "Run Numbers", "", false ) );

    //3
    addParameter( new StringPG( "Experiment name", "quartz", false ) );

    //4
    ChoiceListPG clpg = new ChoiceListPG( 
        "Centering Type", choices.elementAt( 0 ), false );

    clpg.addItems( choices );
    addParameter( clpg );

    //5
    addParameter( new LoadFilePG( "SCD Calibration File", null, false ) );

    //6
    addParameter( new IntArrayPG( "The Time-Slice Range", "-1:3", false ) );

    //7
    addParameter( 
      new IntegerPG( 
        "Amount to Increase Slice Size By", new Integer( 1 ), false ) );

    //8
    addParameter( 
      new IntegerPG( 
        "SCD Calibration File Line to Use", new Integer( -1 ), false ) );

    //9
    addParameter( new BooleanPG( "Append to File?", Boolean.FALSE, false ) );

    //10
    addParameter( new LoadFilePG( "Integrated Peaks File ", "", false ) );

    if( HAS_CONSTANTS ) {
      setParamTypes( 
        new int[]{ 0, 1, 2, 3, 5, 8 }, new int[]{ 4, 6, 7, 9 }, new int[]{ 10 } );
    } else {
      setParamTypes( 
        null, new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, new int[]{ 10 } );
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
    s.append( "@param line2use SCD calibration file line to use.\n" );
    s.append( "@param append True/false indicating whether to append to the " );
    s.append( ".integrate file.\n" );
    s.append( "@return A Boolean indicating success or failure of the Form's " );
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
    Object obj;
    String outputDir;
    String matrixName;
    String calibFile;
    String expName;
    String rawDir;
    String centerType;
    String integName;
    String sliceRange;
    String loadName;
    String IPNSName;
    boolean append;
    boolean first;
    int timeSliceDelta;
    int SCDline;
    DataSet histDS;
    int[] runsArray;

    //get raw data directory
    param    = ( IParameterGUI )super.getParameter( 0 );
    rawDir   = param.getValue(  )
                    .toString(  );

    //get output directory
    param       = ( IParameterGUI )getParameter( 1 );
    outputDir   = param.getValue(  )
                       .toString(  );

    //gets the run numbers
    param       = ( IParameterGUI )super.getParameter( 2 );
    runsArray   = IntList.ToArray( param.getValue(  ).toString(  ) );

    //get experiment name
    param     = ( IParameterGUI )getParameter( 3 );
    expName   = param.getValue(  )
                     .toString(  );

    //get centering type - this still needs to be checked here rather than Form
    param   = ( IParameterGUI )getParameter( 4 );
    obj     = param.getValue(  );

    if( obj != null ) {
      centerType = obj.toString(  );
      param.setValid( true );
    } else {
      return errorOut( param, "ERROR: you must enter a valid centering type." );
    }

    //get calibration file name
    param       = ( IParameterGUI )getParameter( 5 );
    calibFile   = param.getValue(  )
                       .toString(  );

    //get time slice range
    param        = ( IParameterGUI )getParameter( 6 );
    sliceRange   = param.getValue(  )
                        .toString(  );

    //get time slice increase increment
    param            = ( IParameterGUI )getParameter( 7 );
    timeSliceDelta   = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get line number for SCD calibration file
    param     = ( IParameterGUI )super.getParameter( 8 );
    SCDline   = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get append to file value
    param    = ( IParameterGUI )super.getParameter( 9 );
    append   = ( ( BooleanPG )param ).getbooleanValue(  );

    //the name for the saved *.integrate file
    integName   = outputDir + expName + ".integrate";

    //first time through the file
    first = true;

    //to avoid excessive object creation, we'll create all of the 
    //Operators here, then just set their parameters in the loop
    createIntegrateOperators( 
      calibFile, SCDline, integName, sliceRange, timeSliceDelta, append,
      centerType );

    //validate the parameters and set the progress bar variables
    Object superRes = super.getResult(  );

    //had an error, so return
    if( superRes instanceof ErrorString ) {
      return superRes;
    }

    //set the increment amount
    increment = ( 1.0f / runsArray.length ) * 100.0f;

    for( int i = 0; i < runsArray.length; i++ ) {
      IPNSName   = InstrumentType.formIPNSFileName( SCDName, runsArray[i] );

      loadName = rawDir + IPNSName;

      SharedData.addmsg( "Loading " + loadName + "." );

      //load the histogram
      loadHist.getParameter( 0 )
              .setValue( loadName );
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

      if( obj instanceof ErrorString ) {
        return errorOut( "LoadSCDCalib failed: " + obj.toString(  ) );
      }

      //Gets matrix file "lsxxxx.mat" for each run
      //pull the run number off of the IPNS name
      IPNSName   = IPNSName.substring( 
          IPNSName.indexOf( SCDName ) + SCDName.length(  ),
          IPNSName.indexOf( '.' ) );

      matrixName = outputDir + "ls" + expName + IPNSName + ".mat";

      SharedData.addmsg( "Integrating run " + IPNSName + "." );

      integrate.getParameter( 0 )
               .setValue( histDS );
      integrate.getParameter( 2 )
               .setValue( matrixName );
      integrate.getParameter( 7 )
               .setValue( new Boolean( append ) );
      obj = integrate.getResult(  );

      if( obj instanceof ErrorString ) {
        return errorOut( "Integrate failed: " + obj.toString(  ) );
      }

      if( first ) {
        first    = false;  //no longer our first time through
        append   = true;  //start appending to the file
      }

      //fire a property change event off to any listeners
      oldPercent = newPercent;
      newPercent += increment;
      super.fireValueChangeEvent( ( int )oldPercent, ( int )newPercent );
    }

    SharedData.addmsg( "--- IntegrateMultiRunsForm is done. ---" );
    SharedData.addmsg( "Peaks are listed in " + integName );

    //set the integrate file name for the result
    param = ( IParameterGUI )getParameter( 10 );
    param.setValue( integName.toString(  ) );
    param.setValid( true );

    //not really sure what to return
    return new Boolean( true );
  }

  /**
   * Creates the Operators necessary for this Form and sets their constant
   * values.
   *
   * @param calibFile SCD calibration file.
   * @param SCDline The line to use from the SCD calib file.
   * @param integName The name of the .integrate file.
   * @param sliceRange The time slice range.
   * @param timeSliceDelta Amount to increase slice size by.
   * @param append Whether to append to peaks file.
   * @param centerType Centering type.
   */
  private void createIntegrateOperators( 
    String calibFile, int SCDline, String integName, String sliceRange,
    int timeSliceDelta, boolean append, String centerType ) {
    loadHist    = new LoadOneHistogramDS(  );
    integrate   = new Integrate(  );
    loadSCD     = new LoadSCDCalib(  );

    //LoadOneHistogramDS
    //get the histogram.  A value of "1" will retrieve the first histogram
    //DataSet
    loadHist.getParameter( 1 )
            .setValue( new Integer( 1 ) );

    /*If you want to be able to use a group mask,
       change the "" below to a String variable.
       I've been told this is not used. -CMB*/
    loadHist.getParameter( 2 )
            .setValue( "" );

    //Integrate
    integrate.getParameter( 1 )
             .setValue( integName );
    integrate.getParameter( 3 )
             .setValue( centerType );
    integrate.getParameter( 4 )
             .setValue( sliceRange );
    integrate.getParameter( 5 )
             .setValue( new Integer( timeSliceDelta ) );
    integrate.getParameter( 6 )
             .setValue( new Integer( 1 ) );
    integrate.getParameter( 7 )
             .setValue( new Boolean( append ) );

    //LoadSCDCalib
    loadSCD.getParameter( 0 )
           .setValue( calibFile );
    loadSCD.getParameter( 1 )
           .setValue( new Integer( SCDline ) );
    loadSCD.getParameter( 2 )
           .setValue( "" );
  }

  /**
   * Create the vector of choices for the ChoiceListPG of centering.
   */
  private void init_choices(  ) {
    choices = new Vector(  );
    choices.add( "primitive" );  // 0 
    choices.add( "a centered" );  // 1
    choices.add( "b centered" );  // 2
    choices.add( "c centered" );  // 3
    choices.add( "[f]ace centered" );  // 4
    choices.add( "[i] body centered" );  // 5
    choices.add( "[r]hombohedral centered" );  // 6
  }
}
