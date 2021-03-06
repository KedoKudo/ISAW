/*
 * File:  LansceUtil.java
 *
 * Copyright (C) 2005, Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 * $Log$
 * Revision 1.16  2006/07/27 20:17:42  dennis
 * Commented out some unused variables and code, since the
 * Nexus retriever automatically applies the required fixes.
 *
 * Revision 1.15  2006/07/19 18:07:15  dennis
 * Removed unused imports.
 *
 * Revision 1.14  2006/02/06 19:26:28  dennis
 * Removed debug print.
 *
 * Revision 1.13  2006/01/16 01:22:12  dennis
 * Now includes temporary code to shift the detector.  The shift
 * parameters should be passed in from Ruth's XML fix file.
 *
 * Revision 1.12  2006/01/13 05:37:56  dennis
 * Added javadocs to method for adding SampleOrientation attributes.
 * Fixed bug where the default Sample orientation was for the IPNS
 * SCD goniometer, rather than for the LANSCE SCD goniometer.
 *
 * Revision 1.11  2006/01/12 18:40:59  dennis
 * Added code to rebin the detector to 128x128, rather than 256x256
 *
 * Revision 1.10  2006/01/12 00:04:18  dennis
 * Added list of run numbers as attribute.
 *
 * Revision 1.9  2006/01/11 22:30:10  dennis
 * Added method to set sample orientation.
 * Added attributes for instrument name and instrument type.
 *
 * Revision 1.8  2006/01/10 19:05:33  dennis
 * Removed one small shift in the detector position added for testing
 * purposes.  Rearranged main test program.
 *
 * Revision 1.7  2006/01/10 17:40:36  dennis
 * Fixed row vs. column ordering in method to "fix" the
 * LANSCE SCD data.  The order is now known to be correct
 * and was verified using run SCD_E000005_R000781.nx.hdf
 * which is a flood pattern with a rectangular mask in
 * the upper left corner and a circular mask in the
 * lower right corner (when looking at the detector
 * from the sample position).
 *
 * Revision 1.6  2005/08/14 21:44:47  dennis
 * Now adds SampleOrientation to each Data block.
 *
 * Revision 1.5  2005/08/11 21:50:29  dennis
 * Expanded main program to make it easier to view multiple runs
 * in 3D for testing purposes.  Phi, chi & omega values for LANCE
 * SCD are are still not interpreted properly.
 *
 * Revision 1.4  2005/08/10 15:52:28  dennis
 * Swapped rows and cols, hopefully to the order used by LANSCE.
 * Went back to default interpretation of phi, chi & omega
 * rotation angles.
 * NOTE: This still does not give consistent results from
 * multiple runs.
 *
 * Revision 1.3  2005/08/10 15:02:16  dennis
 * Added test code to load three LANSCE SCD runs and put them
 * in the 3D Reciporcal Lattice view, in an attempt to verify
 * that the values of phi, chi and omega are being interpreted
 * correctly.
 *
 * Revision 1.2  2005/06/20 15:50:29  dennis
 * Minor reformatting.
 *
 * Revision 1.1  2005/06/20 03:16:29  dennis
 * Initial checkin.  Currently just contains method to fix
 * Lansce SCD DataSets, which are ordered by column and don't
 * have detector position informaiton.
 *
 */
package Operators.Example;

import DataSetTools.dataset.*;
import DataSetTools.retriever.*;
import DataSetTools.instruments.*;
import DataSetTools.trial.*;

import gov.anl.ipns.MathTools.Geometry.*;

/**
 *  This class contains a static method for converting a DataSet from the
 *  LANSCE SCD to a form that can be used by ISAW.
 */

public class LansceUtil 
{
  private LansceUtil()
  {};

  /**
   *  Rearrange the data from the SCD at LANSCE and add detector position
   *  information to the file.
   *
   *  @param  ds         LANSCE SCD DataSet where each Data block holds Data 
   *                     from one column of pixels on the area detector, at one 
   *                     time-of-flight
   *
   *  @param  t_min      Minimum time-of-flight for this data
   *
   *  @param  t_max      Maximum time-of-flight for this data
   *
   *  @param  det_width  Detector width in meters
   *
   *  @param  det_height Detector width in meters
   *
   *  @param  det_dist   Sample to detector distance
   *
   *  @param  length_0   Initial flight path length
   *
   *  @return A new DataSet with each Data block corresponding to the
   *          time-of-flight spectrum for one pixel on the area detector.
   */
  public static DataSet FixSCD_Data( DataSet ds, 
                                     float t_min,     float t_max,
                                     float det_width, float det_height,
                                     float det_dist,
                                     float length_0 )
  {
    String name = "SCD_FP5";

    float det_offset_x = -8.373E-4f;
    float det_offset_y = -0.00165f;
/*
    System.out.println(ds);
    System.out.println("x label = " + ds.getX_label() );
    System.out.println("x units = " + ds.getX_units() );
    System.out.println("x range = " + ds.getXRange() );
    System.out.println("y label = " + ds.getY_label() );
    System.out.println("y units = " + ds.getY_units() );
    System.out.println("y range = " + ds.getYRange() );
*/
                        // assume data comes in as sequences of values from
                        // columns of the area detector, starting with column 1
                        // column 2, etc of the first time slice.
    int N_PAGES = 325;
    int N_COLS  = 256;
    int N_ROWS  = 256; 
    int AREA_DET_ID = 5;
                                // in most cases we'll rebin the 256x256 data
    int n_rows = N_ROWS;        // to 128x128
    int n_cols = N_COLS;
    boolean bin_2_by_2 = true;
    if ( bin_2_by_2 )
    {
      n_rows = N_ROWS/2;
      n_cols = N_COLS/2;
    }

    float counts[][][] = new float[N_PAGES][N_COLS][N_ROWS];

    String error = null;
    if ( ds.getNum_entries() != N_COLS * N_PAGES )
    {
      error = "Need DataSet with " + (N_COLS * N_PAGES) + " entries " + 
              "got " + ds.getNum_entries(); 
      throw ( new IllegalArgumentException( error ) );  
    }
                       // first, extract all of the data values into a 
                       // full resolution 3D array
    int index = 0;
    float ys[];
    for ( int page = 0; page < N_PAGES; page++ )
      for ( int col = 0; col < N_COLS; col++ )
      {
         Data d = ds.getData_entry( index );
         index++;

         ys = d.getY_values();
         if ( ys.length != N_ROWS )
         {
           error = "Need Data blocks with " + N_ROWS + " y-values " + 
              "got " + ys.length; 
           throw ( new IllegalArgumentException( error ) );  
         } 
         counts[page][col] = ys;
      }
                                       // now form a new DataSet by extracting 
                                       // the values in the 3D array of counts
                                       // in a different order, omitting edge 
                                       // pixels
    DataSetFactory factory = new DataSetFactory( ds.getTitle() );
    DataSet new_ds = factory.getTofDataSet( InstrumentType.TOF_SCD );
    new_ds.setAttributeList( ds.getAttributeList() );

    int type[] = { InstrumentType.TOF_SCD };
    IntListAttribute list_attr = new IntListAttribute(Attribute.INST_TYPE,type);
    new_ds.setAttribute( list_attr ); 

    StringAttribute str_attr = new StringAttribute( Attribute.INST_NAME, name );
    new_ds.setAttribute( str_attr );
    
    XScale x_scale = new UniformXScale( t_min, t_max, N_PAGES + 1 );
    index = 0;
    if ( bin_2_by_2 )
    {
      for ( int row = 0; row < n_rows; row ++ )
        for ( int col = 0; col < n_cols; col ++ )
        {
          ys = new float[N_PAGES];
          if ( row > 0 && col > 0 && row < n_rows-1 && col < n_cols-1 )
            for ( int page = 0; page < N_PAGES; page++ )
              ys[page] = counts[page][2*col  ][2*row  ] +
                         counts[page][2*col+1][2*row  ] +
                         counts[page][2*col  ][2*row+1] +
                         counts[page][2*col+1][2*row+1];
          index++; 

          Data d = new HistogramTable( x_scale, ys, index );
          d.setSqrtErrors( true );            // NOT RIGHT FOR 2x2 bins...fix
          new_ds.addData_entry( d );
      }
    }
    else
    {
      for ( int row = 0; row < N_ROWS; row++ )
        for ( int col = 0; col < N_COLS; col++ )
        {
          ys = new float[N_PAGES];
          if ( row > 0 && col > 0 && row < N_ROWS-1 && col < N_COLS-1 )
            for ( int page = 0; page < N_PAGES; page++ )
              ys[page] = counts[page][col][row];

          index++;

          Data d = new HistogramTable( x_scale, ys, index );
          d.setSqrtErrors( true );
          new_ds.addData_entry( d );
        }
    }
                                      // set some basic attributes
                                      // set initial path attribute after
                                      // converting to positive value in meters
    FloatAttribute initial_path = new FloatAttribute("Initial Path", length_0);
    new_ds.setAttribute( initial_path );
    int num_data = new_ds.getNum_entries();
    for ( int i = 0; i < num_data; i++ )
    {
       new_ds.getData_entry(i).setAttribute( initial_path );
    }

                                      // Add the detector position info.
                                      // First add a Data grid for the detector
    float     depth  =  0.001f;
    float     center_x = 0.0f;
    float     center_y = (float)(-det_dist / Math.sqrt(2));
    float     center_z = (float)( det_dist / Math.sqrt(2));
    Vector3D  center =  new Vector3D(  center_x, center_y, center_z );
    Vector3D  x_vec  =  new Vector3D( -1,      0f,      0f );
    Vector3D  y_vec  =  new Vector3D(  0,  .7071f,  .7071f );

                                      // adjust detector position for offsets
    Vector3D  x_shift = new Vector3D( x_vec );
    Vector3D  y_shift = new Vector3D( y_vec );
    x_shift.multiply( det_offset_x );
    y_shift.multiply( det_offset_y );
    center.add( x_shift );
    center.add( y_shift );

    IDataGrid grid   = new UniformGrid( AREA_DET_ID, "m", 
                                        center, x_vec, y_vec,
                                        det_width, det_height, depth,
                                        n_rows, n_cols );
                       
                                      // Next add the pixel info to each 
                                      // Data block
    IPixelInfo              list[];
    PixelInfoList           pil;
    PixelInfoListAttribute  pil_attr;
    int seg_id = 0;                                       
    for ( int row = 0; row < n_rows; row++ )
      for ( int col = 0; col < n_cols; col++ )
      {
        list     = new IPixelInfo[1];
        list[0]  = new DetectorPixelInfo( seg_id, 
                                         (short)(row+1), (short)(col+1),grid);
        pil      = new PixelInfoList( list );
        pil_attr = new PixelInfoListAttribute(Attribute.PIXEL_INFO_LIST, pil );
        new_ds.getData_entry( seg_id ).setAttribute( pil_attr );
        seg_id++;
      }
                                      // finally, use some utilities to set
                                      // references to the Data blocks to the
                                      // DataGrid, and to fill out 
                                      // position info for each pixel 
    grid.setData_entries( new_ds );
    Grid_util.setEffectivePositions( new_ds, AREA_DET_ID );

                                      // Add default sample orientation 
                                      // and fix the run list attribute
    float phi   = 0;
    float chi   = -135;
    float omega = 0;

    SampleOrientation orientation = 
                               new LANSCE_SCD_SampleOrientation(phi,chi,omega);
    Attribute orientation_attr = new SampleOrientationAttribute(
                                        Attribute.SAMPLE_ORIENTATION, 
                                        orientation );

    int run_list[] = AttrUtil.getRunNumber( new_ds );
    if ( run_list == null )
    {
      int run_num = AttrUtil.getIntValue( Attribute.RUN_NUM, new_ds );
      run_list = new int[1];
      run_list[0] = run_num;
    }
    IntListAttribute run_list_attr =
                     new IntListAttribute( Attribute.RUN_NUM, run_list );

    new_ds.setAttribute( orientation_attr );
    new_ds.setAttribute( run_list_attr );
    for ( int i = 0; i < new_ds.getNum_entries(); i++ )
    {
      new_ds.getData_entry(i).setAttribute( orientation_attr );
      new_ds.getData_entry(i).setAttribute( run_list_attr );
    }

    return new_ds;
  }



  /**
   *  Add the sample orientation attribute to the DataSet and all of
   *  it's Data blocks.  The rotation is initially through an angle of
   *  phi degrees about the vertical (z-axis).  The second rotation is
   *  through an angle of chi degrees about the horizontal (x-axis) pointed
   *  in the direction the neutron beam travels.  The third rotation
   *  is through an angle of omega degrees about the vertical (z-axis).
   *  The (0,0,0) position of the goniometer is assumed to be where the
   *  phi axis is pointing straight up, the chi circle is perpendicular to
   *  the x-axis, and the positive direction of rotation is determined by
   *  a right hand rule around the x and z axes.
   *  When used with the LANSCE SCD, an offset of 90 degrees MUST be added
   *  to the omega angle BEFORE calling this routine, since the omega=0
   *  point on the LANSCE SCD goniomter has the chi circle perpendicular
   *  to the y-axis, not the x-axis. 
   *
   *  @param  ds    The DataSet to which the sample orientation is
   *                added.
   *  @param  phi   The goniometer phi angle in degrees.
   *  @param  chi   The goniometer phi angle in degrees.
   *  @param  omega The goniometer phi angle in degrees.
   */
  public static void AddSampleOrientationAttribute( DataSet ds,
                                                    float   phi, 
                                                    float   chi, 
                                                    float   omega )
  {
     SampleOrientation samp_or =
                          new LANSCE_SCD_SampleOrientation( phi, chi, omega );
     SampleOrientationAttribute attr =
       new SampleOrientationAttribute( Attribute.SAMPLE_ORIENTATION, samp_or );

     ds.setAttribute( attr );

     for ( int db_index = 0; db_index < ds.getNum_entries(); db_index++ )
       ds.getData_entry(db_index).setAttribute( attr );
  }


  /**
   *  Main program for testing purposes.
   */
  public static void main( String args[] )
  {
//  String prefix = "SCD_E000005_R0000";
    String prefix = "SCD_E000005_R000";
    String suffix = ".nx.hdf";
/*
    int run_[]   = { 725, 728, 731 }; 
    int omega_[] = { 125,  85,  35 };
    int phi_[]   = { 320,  10,   0 };
    int chi_[]   = { 120, 120, 120 };
*/
    // NOVEMBER LANSCE RUNS, detector distance 0.265 meter
    int START  = 0;
    int N_RUNS = 13;
//  float det_dist = 0.258f; // 9.75" det face to detector + 10mm face 
                             // thickness
//  float det_dist = 0.245f;
//  float det_dist = 0.25f;
//  float det_dist = 0.265f;  // value from spreadsheet 
//  float det_dist = 0.275f;
/*
    int run_[]   = { 725, 726, 727, 728, 729, 730, 731, 734, 735, 736, 737 };
    int omega_[] = { 125,  90,  60,  85,  72, 108,  35,  55, 130, 95,  60 };
    int phi_[]   = { 320, 335,   0,  10,  42,  50,   0, 280, 220, 200, 200 };
    int chi_[]   = { 120, 120, 120, 120, 120, 120, 120, 120, 120, 120, 120 };
*/
    int run_[]   = { 725, 726, 727, 728, 729, 730, 731, 732, 733, 734, 735, 
                     736, 737 };
    /*
    int omega_[] = { 125,  90,  60,  85,  72, 108,  35, 100,  78,  55, 130,
                     95,  60 };
    int phi_[]   = { 320, 335,   0,  10,  42,  50,   0, 300, 290, 280, 220, 
                     200, 200 };
    int chi_[]   = { 120, 120, 120, 120, 120, 120, 120, 120, 120, 120, 120, 
                     120, 120 };
*/
/*
    // NOVEMBER LANSCE RUNS, detector distance 0.465 meter
    int run_[]   = { 783, 784, 785, 786, 787, 788, 789, 790, 791, 792, 793,
                     794, 795 };
    int omega_[] = { 125,  90,  60,  85,  72, 108,  35, 100,  78,  55, 130,
                      95,  60 };
    int phi_[]   = { 320, 335,   0,  10,  42,  50,   0, 300, 290, 280, 220, 
                     200, 200 };
    int chi_[]   = { 120, 120, 120, 120, 120, 120, 120, 120, 120, 120, 120,
                     120, 120 };
*/
/*
    int run_[]   = {  96,    98,   97,   95,   94,   92 };
    int phi_[]   = {  245,  290,  290,  245,  325,  325 };
    int chi_[]   = { -135, -135, -135, -135, -135, -135 };
    int omega_[] = {   90,  135,   90,  135,  135,   90 };
*/
/*
    int run_[]   = {  96, 96, 96, 96 };
    int phi_[]   = {   0,    0,   0,   245 };
    int chi_[]   = {   0,    0, -135, -135 };
    int omega_[] = {   0,   90,   90,   90 };
*/
    DataSet ds[] = new DataSet[ N_RUNS ];
                                              
//  String dir_name  = "/home/dennis/LANSCE_DATA/RUBY/";
    String dir_name  = "/home/dennis/LANSCE_1_9_06/RUBY_11_x_05/";
    String file_name;
    Retriever retriever;

/*                                              // fix the data 
    float det_width  = 0.20f; 
    float det_height = 0.20f; 
//    float det_width  = 0.192f; 
//    float det_height = 0.192f; 
    float length_0   = 7.499858f;             // 295.27 inches based on
                                              // engineering drawings
    float phi,
          chi,
          omega;
    float phi_offset   = 0;                   // set these to adjust for 
    float chi_offset   = 0;                   // different zero positions
    float omega_offset = 90;

    float phi_sign   = 1;                     // set these to +-1 to change
    float chi_sign   = 1;                     // the direction of rotation
    float omega_sign = 1;
*/
    DataSet one_ds;
    for ( int i = START; i < START+N_RUNS; i++ )
    {
      file_name = dir_name + prefix + run_[i] + suffix;
      retriever = new NexusRetriever( file_name );
      System.out.println("NOW LOADING RUN " + file_name );
      one_ds = retriever.getDataSet(3);
/* 
      // NOTE: Fix is no done in NexusRetriever
      one_ds = FixSCD_Data( one_ds, 
                            1500, 8000, 
                            det_width, det_height, 
                            det_dist,
                            length_0 ); 

      phi   = phi_sign   * phi_[i]   + phi_offset;
      chi   = chi_sign   * chi_[i]   + chi_offset;
      omega = omega_sign * omega_[i] + omega_offset;

      System.out.println("-----------------------------------");
      System.out.println(" run = " + run_[i] + 
                         ", phi = " + phi +
                         ", chi = " + chi +
                         ", omega = " + omega );

      AddSampleOrientationAttribute( one_ds, phi, chi, omega );
*/
      ds[i-START] = one_ds;
    }

/*
                                              // make a huge virtual array
                                              // to hold all of the spectra
                                              // as rows of the iamge
    int n_groups = ds1.getNum_entries();
    int n_times  = ds1.getData_entry(0).getY_values().length;
    XScale x_scale = ds1.getData_entry(0).getX_scale();

    IVirtualArray2D va2D = new VirtualArray2D( n_groups, n_times );
    va2D.setAxisInfo(AxisInfo.X_AXIS, x_scale.getStart_x(), x_scale.getEnd_x(),
                      "Time-of-Flight","microseconds", AxisInfo.LINEAR );
    va2D.setAxisInfo( AxisInfo.Y_AXIS, 1f, n_groups,
                        "Group ID","", AxisInfo.LINEAR );
    va2D.setTitle(file_name);
                                               // copy the data to the 
                                               // virtual array
    for ( int i = 0; i < n_groups; i++ )
    {
      float[] ys = ds1.getData_entry(i).getY_values();
      va2D.setRowValues( ys, i, 0 );
    }
                                               // give the data to a display
    Display2D display = new Display2D( va2D, 
                                       Display2D.IMAGE,
                                       Display2D.CTRL_ALL);
*/
                                               // pop up 3D view of reciprocal
                                               // space as "sanity check"
    DataSet ds_arr[] = new DataSet[ N_RUNS ];
    for ( int i = 0; i < N_RUNS; i++ )
      ds_arr[i] = ds[i];

    new GL_RecipPlaneView( ds_arr, 30 );
//  recip_plane_view.loadOrientationMatrix("/home/dennis/Ruby_1_12_06_D.mat");
//  recip_plane_view.loadOrientationMatrix("/home/dennis/Ruby1.mat");

/*
                                               // now try sending the DataSet
                                               // through peak finding, etc
    for ( int i = 0; i < N_RUNS; i++ )
    {
      FindPeaks find_op = new FindPeaks( ds_arr[i], 100000, 50, 5, 1, 324, 
                                  new IntListString( "1:255" ) ); 
      Vector peaks = (Vector)(find_op.getResult());

      CentroidPeaks centroid_op = new CentroidPeaks( ds_arr[i], peaks );
      peaks = (Vector)(centroid_op.getResult());

      String peaks_file = "/home/dennis/LANSCE_1_9_06/RUBY_11_x_05/Ruby.peaks";
      WritePeaks write_peaks_op;
      if ( i == 0 ) 
        write_peaks_op = new WritePeaks( peaks_file, peaks, false );
      else
        write_peaks_op = new WritePeaks( peaks_file, peaks, true );
      System.out.println( write_peaks_op.getResult() );

    String exp_file = "/home/dennis/LANSCE_1_9_06/RUBY_11_x_05/Ruby.x";
    WriteExp write_exp_op = new WriteExp( ds_arr[0], null, exp_file, 1, false );
    System.out.println( write_exp_op.getResult() );

      for ( int k = 0; k < peaks.size(); k++ )
        System.out.println( peaks.elementAt(k) );
    }
*/

//  new ViewManager( ds1, "3D View" );

//    WindowShower.show(display);
  }
}
