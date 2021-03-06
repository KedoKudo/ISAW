/*
 * File:  FindMultiplePeaksForm.java
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
 * Revision 1.36  2006/07/10 16:26:13  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.35  2006/02/26 00:09:37  dennis
 * Removed unused constant.
 *
 * Revision 1.34  2004/05/21 19:03:20  dennis
 * Changed instrument name from SCD to SCD0.  This was needed to fix
 * file name problems when SCD run numbers went from 9999 to 10000.
 * The runfile names went from SCD09999.RUN to SCD010000.RUN, not
 * SCD10000.RUN
 *
 * Revision 1.33  2004/04/21 19:29:00  dennis
 * Made names for parameter indices protected instead of private,
 * so that they can be used by wizards that use this form.
 *
 * Revision 1.32  2004/04/21 19:13:21  dennis
 * Added min and max time channel parameters and pass them through
 * to FindPeaks() operator.
 * Added name variables to hold parameter indices to improve
 * maintainability.
 *
 * Revision 1.31  2004/03/15 03:37:40  dennis
 * Moved view components, math and utils to new source tree
 * gov.anl.ipns.*
 *
 * Revision 1.30  2004/02/11 04:10:55  bouzekc
 * Uses the new Wizard classes that have indeterminate progress bars.
 *
 * Revision 1.29  2003/12/15 02:47:33  bouzekc
 * Removed unused imports.
 *
 * Revision 1.28  2003/11/05 02:20:30  bouzekc
 * Changed to work with new Wizard and Form design.
 *
 * Revision 1.27  2003/10/27 01:30:56  bouzekc
 * Result parameter is now the last parameter.  This is to facilitate
 * remote execution.
 *
 * Revision 1.26  2003/10/26 19:17:36  bouzekc
 * Now returns the name of the file written rather than Boolean.TRUE when
 * getResult() executes successfully.
 *
 * Revision 1.25  2003/09/11 21:22:28  bouzekc
 * Updated to work with new Form class.
 *
 * Revision 1.24  2003/08/28 20:55:25  bouzekc
 * Set histDS and Operator DataSet parameters to null when they are not used.
 * This should avoid an out of memory error.
 *
 * Revision 1.23  2003/08/25 19:45:12  bouzekc
 * Changed minimum peak intensity to 10.
 *
 * Revision 1.22  2003/07/14 16:32:44  bouzekc
 * Made run number, experiment name, and peaks file
 * parameters' initial values empty.
 *
 * Revision 1.21  2003/07/09 19:57:42  bouzekc
 * Added pixel border restriction parameter.
 *
 * Revision 1.20  2003/07/09 14:20:10  bouzekc
 * No longer has a specific default directory for the SCD
 * instprm.dat file.
 *
 * Revision 1.19  2003/07/08 21:01:24  bouzekc
 * Changed default values for some parameters.
 *
 * Revision 1.18  2003/07/03 14:26:39  bouzekc
 * Added all missing javadoc comments and formatted existing
 * comments.
 *
 * Revision 1.17  2003/06/26 22:24:21  bouzekc
 * Added to getDocumentation() to explain error that occurred
 * when trying to append to a peaks file that does not exist.
 *
 * Revision 1.16  2003/06/25 20:25:34  bouzekc
 * Unused private variables removed, reformatted for
 * consistency.
 *
 * Revision 1.15  2003/06/20 16:32:20  bouzekc
 * Removed space from the "Peaks File " parameter name.
 *
 * Revision 1.14  2003/06/18 23:34:23  bouzekc
 * Parameter error checking now handled by superclass Form.
 *
 * Revision 1.13  2003/06/18 19:56:09  bouzekc
 * Uses super.getResult() for initializing PropertyChanger
 * variables.
 *
 * Revision 1.12  2003/06/17 20:34:54  bouzekc
 * Fixed setDefaultParameters so all parameters have a
 * visible checkbox.  Added more robust error checking on
 * the raw and output directory parameters.
 *
 * Revision 1.11  2003/06/17 16:49:56  bouzekc
 * Now uses InstrumentType.formIPNSFileName to get the
 * file name.  Changed to work with new PropChangeProgressBar.
 *
 * Revision 1.10  2003/06/16 23:04:30  bouzekc
 * Now set up to use the multithreaded progress bar in
 * DataSetTools.components.ParametersGUI.
 *
 * Revision 1.9  2003/06/11 23:04:05  bouzekc
 * No longer uses StringUtil.setFileSeparator as DataDirPG
 * now takes care of this.
 *
 * Revision 1.8  2003/06/11 22:39:20  bouzekc
 * Updated documentation.  Moved file separator "/" code out
 * of loop.
 *
 * Revision 1.7  2003/06/10 19:54:00  bouzekc
 * Fixed bug where the peaks file was not written with every
 * run.
 * Updated documentation.
 *
 * Revision 1.6  2003/06/10 16:45:48  bouzekc
 * Moved creation of Operators out of the for loop and
 * into a private method to avoid excessive Object re-creation.
 * Added parameter to specify line in SCD calibration file.
 *
 * Revision 1.5  2003/06/06 15:12:00  bouzekc
 * Added log message header to file.
 *
 */
package Wizard.TOF_SCD;

import gov.anl.ipns.Parameters.BooleanPG;
import gov.anl.ipns.Parameters.DataDirPG;
import gov.anl.ipns.Parameters.IntArrayPG;
import gov.anl.ipns.Parameters.IntegerPG;
import gov.anl.ipns.Parameters.LoadFilePG;
import gov.anl.ipns.Parameters.StringPG;
import gov.anl.ipns.Parameters.IParameterGUI;
import gov.anl.ipns.Util.Numeric.IntList;
import gov.anl.ipns.Util.SpecialStrings.ErrorString;

import java.util.Vector;

import DataSetTools.dataset.DataSet;
import DataSetTools.instruments.InstrumentType;
import DataSetTools.operator.DataSet.Attribute.LoadSCDCalib;
import DataSetTools.operator.DataSet.Math.Analyze.IntegrateGroup;
import DataSetTools.operator.Generic.Load.LoadMonitorDS;
import DataSetTools.operator.Generic.Load.LoadOneHistogramDS;
import DataSetTools.operator.Generic.TOF_SCD.CentroidPeaks;
import DataSetTools.operator.Generic.TOF_SCD.FindPeaks;
import DataSetTools.operator.Generic.TOF_SCD.Peak;
import DataSetTools.operator.Generic.TOF_SCD.WriteExp;
import DataSetTools.operator.Generic.TOF_SCD.WritePeaks;

import DataSetTools.util.SharedData;
import DataSetTools.wizard.Form;


/**
 * This Form is a "port" of the script used to find peaks in multiple SCD
 * files.
 */
public class FindMultiplePeaksForm extends Form {

  //~ Instance fields **********************************************************

  protected final String SCDName = "SCD0";
  private LoadOneHistogramDS loadHist;
  private LoadMonitorDS loadMon;
  private IntegrateGroup integGrp;
  private LoadSCDCalib loadSCD;
  private FindPeaks fPeaks;
  private CentroidPeaks cenPeaks;
  private WriteExp wrExp;
  private WritePeaks wrPeaks;
                                               // FYI, parameter indices, as set in
                                               // setDefaultParameters() method
  protected int DATA_DIR_PARAM     = 0;
  protected int OUT_DIR_PARAM      = 1;
  protected int RUN_NUM_PARAM      = 2;
  protected int EXP_NAME_PARAM     = 3;
  protected int NUM_PEAKS_PARAM    = 4;
  protected int MIN_INTENS_PARAM   = 5;
  protected int MIN_TIME_PARAM     = 6;
  protected int MAX_TIME_PARAM     = 7;
  protected int APPEND_PARAM       = 8;
  protected int CALIB_LINE_PARAM   = 9;
  protected int CALIB_FILE_PARAM   = 10;
  protected int ROWS_TO_KEEP_PARAM = 11;
  protected int PEAK_FILE_PARAM    = 12;

  //~ Constructors *************************************************************

  /**
   * Construct a Form with a default parameter list.
   */
  public FindMultiplePeaksForm(  ) {
    super( "FindMultiplePeaksForm" );
    this.setDefaultParameters(  );
  }

  /**
   * Full constructor for FindMultiplePeaksForm.
   *
   * @param rawpath       The raw data path.
   * @param outpath       The output data path for the .peaks file.
   * @param runnums       The run numbers to load.
   * @param expname       The experiment name (i.e. "quartz").
   * @param num_peaks     The maximum number of peaks to return.
   * @param min_int       The minimum peak intensity to look for.
   * @param min_time_chan The minimum time channel to use.
   * @param max_time_chan The maximum time channel to use.
   * @param append Append to file (yes/no).
   * @param line2use      SCD calibration file line to use.
   * @param calibfile     SCD calibration file.
   */
  public FindMultiplePeaksForm( String  rawpath, 
                                String  outpath, 
                                String  runnums, 
                                String  expname,
                                int     num_peaks, 
                                int     min_int, 
                                int     min_time_chan, 
                                int     max_time_chan, 
                                boolean append, 
                                int     line2use, 
                                String calibfile ) {
    this(  );
    getParameter( DATA_DIR_PARAM )
      .setValue( rawpath );
    getParameter( OUT_DIR_PARAM )
      .setValue( outpath );
    getParameter( RUN_NUM_PARAM )
      .setValue( runnums );
    getParameter( EXP_NAME_PARAM )
      .setValue( expname );
    getParameter( NUM_PEAKS_PARAM )
      .setValue( new Integer( num_peaks ) );
    getParameter( MIN_INTENS_PARAM )
      .setValue( new Integer( min_int ) );
    getParameter( MIN_TIME_PARAM )
      .setValue( new Integer( min_time_chan ) );
    getParameter( MAX_TIME_PARAM )
      .setValue( new Integer( max_time_chan ) );
    getParameter( APPEND_PARAM )
      .setValue( new Boolean( append ) );
    getParameter( CALIB_LINE_PARAM )
      .setValue( new Integer( line2use ) );
    getParameter( CALIB_FILE_PARAM )
      .setValue( calibfile );
  }

  //~ Methods ******************************************************************

  /**
   * @return the String command used for invoking this Form in a Script.
   */
  public String getCommand(  ) {
    return "FINDMULTIPEAKSFORM";
  }

  /**
   * Attempts to set reasonable default parameters for this form.
   */
  public void setDefaultParameters(  ) {

    parameters = new Vector(  );
    addParameter( new DataDirPG( "Raw Data Path", null ) );           //0
    DATA_DIR_PARAM = 0;

    addParameter( new DataDirPG( "Peaks File Output Path", null ) );  //1
    OUT_DIR_PARAM = 1;

    addParameter( new IntArrayPG( "Run Numbers", "" ) );              //2
    RUN_NUM_PARAM = 2;

    addParameter( new StringPG( "Experiment name", "" ) );            //3
    EXP_NAME_PARAM = 3;

    addParameter( 
      new IntegerPG( "Maximum Number of Peaks", new Integer( 30 ) ) );//4
    NUM_PEAKS_PARAM = 4;

    addParameter( 
      new IntegerPG( "Minimum Peak Intensity", new Integer( 10 ) ) ); //5
    MIN_INTENS_PARAM = 5;

    addParameter( 
      new IntegerPG( "Minimum Time Channel", new Integer( 0 ) ) );    //6
    MIN_TIME_PARAM = 6;

    addParameter( 
      new IntegerPG( "Maximum Time Channel", new Integer( 1000 ) ) ); //7
    MAX_TIME_PARAM = 7;

    addParameter( 
      new BooleanPG( "Append Data to File?", new Boolean( false ) ) );//8
    APPEND_PARAM = 8;

    addParameter( 
      new IntegerPG( 
        "SCD Calibration File Line to Use", new Integer( -1 ) ) );    //9
    CALIB_LINE_PARAM = 9;

    addParameter( new LoadFilePG( "SCD Calibration File", null ) );  //10
    CALIB_FILE_PARAM = 10;

    addParameter( 
      new IntArrayPG( "Pixel Rows and Columns to Keep", "0:100" ) ); //11
    ROWS_TO_KEEP_PARAM = 11;

    setResultParam( new LoadFilePG( "Peaks File", " " ) );           //12
    PEAK_FILE_PARAM = 12;

    // Now mark which parameters are constant, user specified, or results

    setParamTypes( null, 
                   new int[]{ DATA_DIR_PARAM,
                              OUT_DIR_PARAM,
                              RUN_NUM_PARAM,
                              EXP_NAME_PARAM,
                              NUM_PEAKS_PARAM,
                              MIN_INTENS_PARAM,
                              MIN_TIME_PARAM,
                              MAX_TIME_PARAM,
                              APPEND_PARAM,
                              CALIB_LINE_PARAM,
                              CALIB_FILE_PARAM,
                              ROWS_TO_KEEP_PARAM },
                   new int[]{ PEAK_FILE_PARAM } );
  }

  /**
   * @return documentation for this OperatorForm.  Follows javadoc conventions.
   */
  public String getDocumentation(  ) {
    StringBuffer s = new StringBuffer(  );
    s.append( "@overview This Form is designed to find peaks from multiple" );
    s.append( "SCD RunFiles. " );
    s.append( "@assumptions It is assumed that:\n" );
    s.append( "1. Data of interest is in the first histogram.\n" );
    s.append( "2. If the calibration file is not specified then the real " );
    s.append( "space conversion is not performed.\n" );
    s.append( "@algorithm First the calibration data from the SCD file is " );
    s.append( "loaded.\n" );
    s.append( "Then the FindPeaks Operator is used to find the peaks, based " );
    s.append( "on user input.\n" );
    s.append( "Then the CentroidPeaks Operator is used to find the peak " );
    s.append( "centers.\n" );
    s.append( "Next it writes the results to the specified *.peaks file.\n" );
    s.append( "Finally it writes the SCD experiment (*.x) file.\n" );
    s.append( "@param rawpath The raw data path.\n" );
    s.append( "@param outpath The output data path for the *.peaks file.\n" );
    s.append( "@param runnums The run numbers to load.\n" );
    s.append( "@param expname The experiment name (i.e. \"quartz\").\n" );
    s.append( "@param num_peaks The maximum number of peaks to return.\n" );
    s.append( "@param min_int The minimum peak intensity to look for.\n" );
    s.append( "@param min_time_chan The minimum time channel to use.\n" );
    s.append( "@param max_time_chan The maximum time channel to use.\n" );
    s.append( "@param append Whether to append data to the peaks file.\n" );
    s.append( "@param line2use SCD calibration file line to use.\n" );
    s.append( "@param calibfile SCD calibration file.\n" );
    s.append( "@param peaksFile Peaks filename that data is written to.\n" );
    s.append( "@param keepPixels The detector pixel range to keep.\n" );
    s.append( "@return A Boolean indicating success or failure of the Form's " );
    s.append( "execution.\n" );
    s.append( "@error If you specify that you want to append to the peaks " );
    s.append( "file and the file does not exist, you will get an error " );
    s.append( "from WriteSCDExp saying that it cannot find the file.  " );
    s.append( "To fix this, uncheck the \"append to file\" box.\n" );
    s.append( "@error An error is returned if a valid experiment name is not " );
    s.append( "entered.\n" );
    s.append( "@error An error is returned if a valid number of peaks is not " );
    s.append( "entered.\n" );
    s.append( "@error An error is returned if a valid minimum peak intensity " );
    s.append( "is not entered.\n" );
    s.append( "@error An error is returned if a valid calibration file name " );
    s.append( "is not entered.\n" );

    return s.toString(  );
  }

  /**
   * getResult() finds multiple peaks using the following algorithm: First the
   * calibration data from the SCD file is loaded.  Then the FindPeaks
   * Operator is used to find the peaks, based on user input. Then the
   * CentroidPeaks Operator is used to find the peak centers. Next it writes
   * the results to the specified .peaks file.  Finally it writes the SCD
   * experiment (.x) file.
   *
   * @return A Boolean indicating success or failure.
   */
  public Object getResult(  ) {
    SharedData.addmsg( "Executing...\n" );

    IParameterGUI param;
    int maxPeaks;
    int minIntensity;
    int minTimeChan;
    int maxTimeChan;
    int SCDline;
    int lowerLimit;
    int upperLimit;
    Float monCount;
    String rawDir;
    String outputDir;
    String saveName;
    String expName;
    String calibFile;
    String loadName;
    String expFile;
    String IPNSName;
    boolean appendToFile;
    boolean first;
    Vector peaksVec;
    DataSet histDS;
    DataSet monDS;
    Object obj;
    Peak peak       = null;
    int[] runsArray;
    int[] keepRange;

    //get raw data directory
    param          = ( IParameterGUI )super.getParameter( DATA_DIR_PARAM );
    rawDir         = param.getValue(  )
                          .toString(  );

    //get output directory
    param          = ( IParameterGUI )super.getParameter( OUT_DIR_PARAM );
    outputDir      = param.getValue(  )
                          .toString(  );

    //gets the run numbers
    param          = ( IParameterGUI )super.getParameter( RUN_NUM_PARAM );
    runsArray      = IntList.ToArray( param.getValue(  ).toString(  ) );

    //get experiment name
    param          = ( IParameterGUI )super.getParameter( EXP_NAME_PARAM );
    expName        = param.getValue(  )
                          .toString(  );

    //get maximum number of peaks to find
    param          = ( IParameterGUI )super.getParameter( NUM_PEAKS_PARAM );
    maxPeaks       = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get minimum intensity of peaks
    param          = ( IParameterGUI )super.getParameter( MIN_INTENS_PARAM );
    minIntensity   = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get minimum time channel to use 
    param          = ( IParameterGUI )super.getParameter( MIN_TIME_PARAM );
    minTimeChan    = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get maximum time channel to use 
    param          = ( IParameterGUI )super.getParameter( MAX_TIME_PARAM );
    maxTimeChan    = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get append to file value
    param          = ( IParameterGUI )super.getParameter( APPEND_PARAM );
    appendToFile   = ( ( BooleanPG )param ).getbooleanValue(  );

    //get line number for SCD calibration file
    param          = ( IParameterGUI )super.getParameter( CALIB_LINE_PARAM );
    SCDline        = ( ( Integer )param.getValue(  ) ).intValue(  );

    //get calibration file name
    param          = ( IParameterGUI )super.getParameter( CALIB_FILE_PARAM );
    calibFile      = param.getValue(  )
                          .toString(  );

    //get the detector border range
    keepRange      = ( ( IntArrayPG )getParameter( ROWS_TO_KEEP_PARAM ) )
                     .getArrayValue(  );

    if( keepRange != null ) {
      lowerLimit   = keepRange[0];  //lower limit of range

      //upper limit of range
      upperLimit   = keepRange[keepRange.length - 1];
    } else {  //shouldn't happen, but default to 0:MAX_VALUE
      lowerLimit   = 0;
      upperLimit   = Integer.MAX_VALUE;
    }

    //first time through the file
    first      = true;

    //the name for the saved file
    saveName   = outputDir + expName + ".peaks";
    expFile    = outputDir + expName + ".x";

    //to avoid excessive object creation, we'll create all of the 
    //Operators here, then just set their parameters in the loop
    createFindPeaksOperators( calibFile, 
                              maxPeaks, 
                              minIntensity, 
                              minTimeChan, 
                              maxTimeChan, 
                              saveName, 
                              expFile, 
                              SCDline );

    //validate the parameters and set the progress bar variables
    Object validCheck = validateSelf(  );

    //had an error, so return
    if( validCheck instanceof ErrorString ) {
      return validCheck;
    }

    for( int i = 0; i < runsArray.length; i++ ) {
      IPNSName   = InstrumentType.formIPNSFileName( SCDName, runsArray[i] );
      loadName   = rawDir + IPNSName;
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

      //load the monitor
      loadMon.getParameter( 0 )
             .setValue( loadName );
      obj = loadMon.getResult(  );

      //make sure it is a DataSet
      if( obj instanceof DataSet ) {
        monDS = ( DataSet )obj;
      } else {
        return errorOut( "LoadMonitorDS failed: " + obj.toString(  ) );
      }
      SharedData.addmsg( "Finding peaks for " + loadName + "." );

      //integrate
      integGrp.setDataSet( monDS );
      obj = integGrp.getResult(  );

      if( obj instanceof Float ) {
        monCount = ( Float )obj;
      } else {
        return errorOut( "IntegrateGroup failed: " + obj.toString(  ) );
      }

      //load calibration data 
      loadSCD.setDataSet( histDS );
      obj = loadSCD.getResult(  );
      loadSCD.setDataSet( null );

      if( obj instanceof ErrorString ) {
        return errorOut( "LoadSCDCalib failed: " + obj.toString(  ) );
      }

      // find peaks
      fPeaks.getParameter( 0 )
            .setValue( histDS );
      fPeaks.getParameter( 1 )
            .setValue( monCount );
      obj = fPeaks.getResult(  );
      fPeaks.getParameter( 0 )
            .setValue( null );

      if( obj instanceof Vector ) {
        peaksVec = ( Vector )obj;
      } else {
        return errorOut( "FindPeaks failed: " + obj.toString(  ) );
      }

      // trim out edge peaks (defined by the "pixels to keep" parameter)
      for( int k = peaksVec.size(  ) - 1; k >= 0; k-- ) {
        peak = ( Peak )peaksVec.elementAt( k );

        //see if the peak pixels are within the user defined array.  We are
        //assuming a SQUARE detector, so we'll reject it if the x or y position
        //is not within our range
        if( 
          ( peak.x(  ) > upperLimit ) || ( peak.x(  ) < lowerLimit ) ||
            ( peak.y(  ) > upperLimit ) || ( peak.y(  ) < lowerLimit ) ) {
          peaksVec.remove( k );
        }
      }

      //"centroid" (find the center) the peaks
      cenPeaks.getParameter( 0 )
              .setValue( histDS );
      cenPeaks.getParameter( 1 )
              .setValue( peaksVec );
      obj = cenPeaks.getResult(  );
      cenPeaks.getParameter( 0 )
              .setValue( null );

      if( obj instanceof Vector ) {
        peaksVec = ( Vector )obj;
      } else {
        return errorOut( "CentroidPeaks failed: " + obj.toString(  ) );
      }
      SharedData.addmsg( "Writing peaks for " + loadName + "." );

      // write out the results to the .peaks file
      wrPeaks.getParameter( 1 )
             .setValue( peaksVec );
      wrPeaks.getParameter( 2 )
             .setValue( new Boolean( appendToFile ) );
      obj = wrPeaks.getResult(  );

      if( obj instanceof ErrorString ) {
        return errorOut( "WritePeaks failed: " + obj.toString(  ) );
      }

      //write the SCD experiment file
      wrExp.getParameter( 0 )
           .setValue( histDS );
      wrExp.getParameter( 1 )
           .setValue( monDS );
      wrExp.getParameter( 4 )
           .setValue( new Boolean( appendToFile ) );
      obj = wrExp.getResult(  );
      wrExp.getParameter( 0 )
           .setValue( null );

      if( obj instanceof ErrorString ) {
        return errorOut( "WriteExp failed: " + obj.toString(  ) );
      }

      if( first ) {
        first          = false;
        appendToFile   = true;
      }

      histDS = null;
    }
    SharedData.addmsg( "--- Done finding peaks. ---" );
    SharedData.addmsg( "Peaks are listed in " );
    SharedData.addmsg( saveName );

    //set the peaks file name
    param = ( IParameterGUI )super.getParameter( PEAK_FILE_PARAM );
    param.setValue( saveName );
    param.setValidFlag( true );

    return saveName;
  }

  /**
   * Creates the Operators necessary for this Form and sets their constant
   * values.
   *
   * @param calibFile SCD calibration file.
   * @param maxPeaks Maximum number of peaks.
   * @param minInten Minimum peak intensity.
   * @param minTimeChan Minimum time channel number.
   * @param maxTimeChan Maximum time channel number.
   * @param peaksName Fully qualified peaks file name.
   * @param expFile Fully qualified experiment file name.
   * @param SCDline The line to use from the SCD calib file.
   */
  private void createFindPeaksOperators( String calibFile, 
                                         int    maxPeaks, 
                                         int    minInten, 
                                         int    minTimeChan, 
                                         int    maxTimeChan, 
                                         String peaksName,
                                         String expFile, 
                                         int    SCDline ) {
    loadHist   = new LoadOneHistogramDS(  );
    loadMon    = new LoadMonitorDS(  );
    integGrp   = new IntegrateGroup(  );
    loadSCD    = new LoadSCDCalib(  );
    fPeaks     = new FindPeaks(  );
    cenPeaks   = new CentroidPeaks(  );
    wrExp      = new WriteExp(  );
    wrPeaks    = new WritePeaks(  );

    //LoadOneHistogramDS
    //get the histogram.  We want to retrieve the first one.
    loadHist.getParameter( 1 )
            .setValue( new Integer( 1 ) );

    /*If you want to be able to use a group mask,
       change the "" below to a String variable.
       I've been told this is not used. -CMB*/
    loadHist.getParameter( 2 )
            .setValue( "" );

    //IntegrateGroup
    integGrp.getParameter( 0 )
            .setValue( new Integer( 1 ) );
    integGrp.getParameter( 1 )
            .setValue( new Float( 0 ) );
    integGrp.getParameter( 2 )
            .setValue( new Float( 50000 ) );

    //LoadSCDCalib
    loadSCD.getParameter( 0 )
           .setValue( calibFile );
    loadSCD.getParameter( 1 )
           .setValue( new Integer( SCDline ) );
    loadSCD.getParameter( 2 )
           .setValue( "" );

    //FindPeaks
    fPeaks.getParameter( 2 )
          .setValue( new Integer( maxPeaks ) );
    fPeaks.getParameter( 3 )
          .setValue( new Integer( minInten ) );
    fPeaks.getParameter( 4 )
          .setValue( new Integer( minTimeChan ) );
    fPeaks.getParameter( 5 )
          .setValue( new Integer( maxTimeChan ) );

    //WritePeaks
    wrPeaks.getParameter( 0 )
           .setValue( peaksName );

    //WriteExp
    wrExp.getParameter( 2 )
         .setValue( expFile );
    wrExp.getParameter( 3 )
         .setValue( new Integer( 1 ) );
  }
}
