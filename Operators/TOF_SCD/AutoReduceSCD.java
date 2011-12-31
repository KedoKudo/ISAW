/* 
 * File: AutoReduceSCD.java
 *
 * Copyright (C) 2011, Dennis Mikkelson
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
 *
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author: eu7 $
 *  $Date: 2011-10-13 12:11:06 -0500 (Thu, 13 Oct 2011) $            
 *  $Revision: 21309 $
 */
package Operators.TOF_SCD;

import java.util.*;
import java.io.*;

import EventTools.EventList.*;
import EventTools.Histogram.*;
import EventTools.ShowEventsApp.DataHandlers.QMapperHandler;
import EventTools.ShowEventsApp.DataHandlers.PeakListHandler;
import EventTools.Integrate.*;

import DataSetTools.operator.Generic.TOF_SCD.BasicPeakInfo;
import DataSetTools.operator.Generic.TOF_SCD.FindPeaksViaSort;
import DataSetTools.operator.Generic.TOF_SCD.Peak_new;
import DataSetTools.operator.Generic.TOF_SCD.Peak_new_IO;
import DataSetTools.operator.Generic.TOF_SCD.PeakQ;
import DataSetTools.operator.Generic.TOF_SCD.Util;

import gov.anl.ipns.MathTools.Geometry.*;

import Operators.TOF_SCD.IndexingUtils;

/**
 *  This class provides various methods for the basic reduction and
 *  analysis of raw event data for single crystal diffraction experiments.
 *  The principle method reduce() takes the most commonly used parameters
 *  that control the reduction process.  If the method completes successfully,
 *  the result is a .integrate file, suitable for use by ANVRED.  Most of 
 *  the lower level calculation and data structures are provided by the ISAW
 *  system, so a current version of ISAW will need to be installed on the
 *  system and the ISAW .jar files and supporting files will need to be on
 *  the CLASSPATH. 
 */
public class AutoReduceSCD
{
  Histogram3D       histogram;
  SNS_Tof_to_Q_map  mapper;
  Vector<Peak_new>  peak_list;
  Tran3D            UB;

  /**
   *  Construct an AutoReduceSCD object for the specified instrument.
   *  @param instrument  Instrument name, such as TOPAZ or SNAP.  This must
   *                     be the name of a supported instrument from the SNS.
   *                     The name determines which default instrument geometry
   *                     files (.DetCal, bank and mapping files) from the
   *                     ISAW distribution will be used.
   *  @param num_bins    The number of steps in each direction to use for the
   *                     underlying 3D histogram in reciprocal space.  The 
   *                     required storage is 4*num_bins^3.  A num_bins = 768
   *                     requires 1.8 Gb of memory and is a reasonable 
   *                     compromize between resolution and memory size and
   *                     compute time.  
   *  @param max_Q       The maximum |Q| to use for the histogram region and
   *                     for events loaded from the raw event file.  NOTE:
   *                     the value for max_Q is specified here using the 
   *                     physics convention that Q = 2*PI/d, NOT the 
   *                     crystallographic conventions that Q = 1/d.
   *  @param wavelength_power The power on the wavelength term in the Lorentz
   *                          correction factor.  Theoretically, this should 
   *                          be 4, but a value of 2.4 works better for finding
   *                          peaks in small molecule data and a value of 1
   *                          works better for large molecules, such as 
   *                          protiens.  The Lorentz correction is "backed out"
   *                          before writing the .integrate file, since it is
   *                          applied in ANVRED, so this parameter is only a
   *                          fudge factor to even out the data for finding
   *                          peaks.
   */
  public AutoReduceSCD( String instrument, 
                        int    num_bins, 
                        float  max_Q,
                        float  wavelength_power ) 
         throws IOException
  {
    float qx_min = -40.0f;
    float qx_max =   0.0f;
    float qy_min = -25.0f;
    float qy_max =  25.0f;
    float qz_min = -25.0f;
    float qz_max =  25.0f;
                                // clamp bounds on histogram to be between
    if ( qx_min < -max_Q )      // -max_Q and +max_Q
      qx_min = -max_Q;

    if ( qx_max > max_Q )
      qx_max = max_Q;

    if ( qy_min < -max_Q )
      qy_min = -max_Q;

    if ( qy_max > max_Q )
      qy_max = max_Q;

    if ( qz_min < -max_Q )
      qz_min = -max_Q;

    if ( qz_max > max_Q )
      qz_max = max_Q;

    Vector3D xVec = new Vector3D( 1, 0, 0 );
    Vector3D yVec = new Vector3D( 0, 1, 0 );
    Vector3D zVec = new Vector3D( 0, 0, 1 );

    UniformEventBinner x_bin1D = new UniformEventBinner( 
                                                 qx_min, qx_max, num_bins );  
    UniformEventBinner y_bin1D = new UniformEventBinner( 
                                                 qy_min, qy_max, num_bins );  
    UniformEventBinner z_bin1D = new UniformEventBinner( 
                                                 qz_min, qz_max, num_bins );  
    ProjectionBinner3D x_binner = new ProjectionBinner3D( x_bin1D, xVec );
    ProjectionBinner3D y_binner = new ProjectionBinner3D( y_bin1D, yVec );
    ProjectionBinner3D z_binner = new ProjectionBinner3D( z_bin1D, zVec );

    histogram = new Histogram3D( x_binner, y_binner, z_binner );

    float radius = 0;        // NOT DOING ANVRED CORRECTIONS HERE 
    float smu    = 1;
    float amu    = 1;
    mapper    = new SNS_Tof_to_Q_map( instrument, null, null, null, null,
                                      wavelength_power, radius, smu, amu );
    mapper.setMinQ( 0 );
    mapper.setMaxQ( max_Q );
  }


  /**
   *  This method will load the specified raw event file, convert the
   *  time-of-flight events to reciprocal space Q-vectors and add them
   *  to the underlying histogram. This method MUST be called before 
   *  any of the other methods that find peaks, etc.
   *
   *  @param eventfilename  The name of an SNS raw neutron_event.dat file.
   */
  public long LoadHistogram( String eventfilename )
  {
    histogram.clear();
    SNS_TofEventList tof_list = new SNS_TofEventList( eventfilename );
    IEventList3D q_events;
    long n_events    = tof_list.numEntries();
    long first_index = 0;
    long seg_size    = 65536;
    long n_extra     = n_events % seg_size;
    int  n_segments  = (int)(n_events/seg_size);

    for ( int i = 0; i < n_segments; i++ ) 
    {
      q_events = mapper.MapEventsToQ( tof_list, first_index, seg_size );
      histogram.addEvents( q_events, true );
      first_index += seg_size;
    }

    q_events = mapper.MapEventsToQ( tof_list, first_index, n_extra );
    histogram.addEvents( q_events, true );

    return histogram.numAdded();
  }


  /**
   *  This method will search for peaks in the underlying histogram 
   *  file, and store the list of found peaks in this object.  
   *
   *  @param num_to_find   The maximum number of peaks to look for in the
   *                       histogram.
   *  @param min_intensity The minimum histogram value that will be considered
   *                       for a possible peak.
   */
  public int FindPeaks( int num_to_find, float min_intensity )
  {
    int num_pages = histogram.zBinner().numBins();
    int num_rows  = histogram.yBinner().numBins();
    int num_cols  = histogram.xBinner().numBins();

    float[][][] histogram_array = new float[num_pages][][];
    for ( int page = 0; page < num_pages; page++ )
      histogram_array[page] = histogram.pageSlice( page );

    boolean smooth_data = false;

    int[] row_list = new int[num_rows];
    for ( int k = 0; k < num_rows; k++ )
      row_list[k] = k + 1;

    int[] col_list = new int[num_cols];
    for ( int k = 0; k < num_cols; k++ )
      col_list[k] = k + 1;

    StringBuffer log = new StringBuffer();

    float min_val = (float)histogram.minVal();
    float max_val = (float)histogram.maxVal();

    BasicPeakInfo[] peaks = FindPeaksViaSort.getPeaks( histogram_array,
                                                       smooth_data,
                                                       num_to_find,
                                                       min_intensity,
                                                       row_list,
                                                       col_list,
                                                       0,
                                                       num_pages-1,
                                                       min_val,
                                                       max_val,
                                                       log );

    IProjectionBinner3D x_binner = histogram.xEdgeBinner();
    IProjectionBinner3D y_binner = histogram.yEdgeBinner();
    IProjectionBinner3D z_binner = histogram.zEdgeBinner();

    Vector3D   zero    = new Vector3D();
    Vector3D[] verts   = new Vector3D[ peaks.length ];
    int        counter = 0;
    for ( int k = 0; k < verts.length; k++ )
    {
      if ( peaks[k].isValid() )
      {
        counter++;

        float col  = peaks[k].getColMean();
        float row  = peaks[k].getRowMean();
        float page = peaks[k].getChanCenter();

        Vector3D point = x_binner.Vec( page );
        Vector3D temp  = y_binner.Vec( col );
        point.add( temp );
        temp = z_binner.Vec( row );
        point.add( temp );
        verts[k] = point;
      }
      else
        verts[k] = zero;
    }

    Vector<PeakQ> q_peaks = new Vector<PeakQ>();
    for ( int k = 0; k < verts.length; k++ )
    {
      if ( verts[k] != zero )
      {
        float qx = verts[k].getX();
        float qy = verts[k].getY();
        float qz = verts[k].getZ();
        float ipk_f = histogram.valueAt( qx, qy, qz );
        qx = (float)(qx / (2 * Math.PI)) ;
        qy = (float)(qy / (2 * Math.PI)) ;
        qz = (float)(qz / (2 * Math.PI)) ;
        q_peaks.add( new PeakQ( qx, qy, qz, (int)ipk_f ) );
      }
    }

    peak_list = QMapperHandler.ConvertPeakQToPeakNew( mapper, q_peaks );
    PeakListHandler.Sort( peak_list );

    return peak_list.size();
  }


  /**
   *  After peaks have been found, this method will try to find an orientation
   *  matrix that would index the peaks to within the specified tolerance on
   *  h,k,l, using the specified lattice parameters.
   *
   *  @param a         Lattice parameter 'a'.
   *  @param b         Lattice parameter 'b'.
   *  @param c         Lattice parameter 'c'.
   *  @param alpha     Lattice parameter 'alpha'.
   *  @param beta      Lattice parameter 'beta'.
   *  @param gamma     Lattice parameter 'gamma'.
   *  @param tolerance The required tolerance on h,k and l for a peak to
   *                   be considered to be indexed.  If a peak is not indexed
   *                   the h,k,l values are recorded as 0,0,0
   *  @return the number of peaks that were successfully indexed.
   */
  public int FindUB_AndIndexPeaks( float a,     float b,    float c,
                                   float alpha, float beta, float gamma,
                                   float tolerance )
  {
    UB = new Tran3D();
    Vector<Vector3D> q_vectors = new Vector<Vector3D>();
    for ( int i = 0; i < peak_list.size(); i++ )
    {
      Vector3D q_vec = new Vector3D( peak_list.elementAt(i).getQ() );
      q_vectors.add( q_vec );
    }

    int   base_index  = -1;
    int   num_initial = 35;
    float angle_step  = 1.5f;
    IndexingUtils.Find_UB( UB, 
                           q_vectors,
                           a, b, c, alpha, beta, gamma,
                           tolerance,
                           base_index,
                           num_initial,
                           angle_step );

    Tran3D UB_inv = new Tran3D( UB );
    UB_inv.invert();
    Vector3D hkl = new Vector3D(); 
    int num_indexed = 0;
    for ( int i = 0; i < peak_list.size(); i++ )
    {
      UB_inv.apply_to( q_vectors.elementAt(i), hkl );
      if ( IndexingUtils.ValidIndex( hkl, tolerance ) )
      {
        peak_list.elementAt(i).sethkl( hkl.getX(), hkl.getY(), hkl.getZ() );
        num_indexed++;
      }
      else
        peak_list.elementAt(i).sethkl( 0, 0, 0 );
    }

    return num_indexed;
  }

 
  /**
   * This method will carry out a rough integration of the peaks by summing 
   * all histogram bins whose centers are within the specified radius of the
   * peak center.  A somewhat larger radius is used to obtain an estimate of
   * the background.  The net integrated intensity ( intensity - background )
   * and an estimate of the standard deviation of the integrated intensity
   * is recorded in the list of peaks, for each peak that is successfully
   * integrated.
   *
   * @param radius         The radius of the peak region to integrate, 
   *                       specified in Q, using the physics convention that
   *                       Q = 2*PI/d.
   * @param integrate_all  If true, predict peak positions from the UB matrix
   *                       and integrate all predicted peak positions.
   *                       (THIS IS NOT CURRENTLY IMPLEMENTED).  If false,
   *                       just integrate the peaks that were actually found. 
   *
   * @return the number of peaks that were integrated, and stored in the
   *         current peaks file. 
   */
  public int IntegratePeaks( float radius, boolean integrate_all )
  {
    Vector<Peak_new> peaks = peak_list;    // eventually, if integrate_all is
                                           // true we will generate predicted 
                                           // peak positions.  For now just 
                                           // use the peaks that were found.
    Vector<float[]> i_sigi_vec = new Vector<float[]>();
    Vector<Peak_new> peaks_kept = new Vector<Peak_new>();
    float bkg_radius  = 1.5f * radius;
    float[] radii = { radius, bkg_radius };

    for ( int i = 0; i < peaks.size(); i++ )
    {
      float[] q_arr = peaks.elementAt(i).getUnrotQ();
      float qx = (float)(q_arr[0] * 2 * Math.PI);
      float qy = (float)(q_arr[1] * 2 * Math.PI);
      float qz = (float)(q_arr[2] * 2 * Math.PI);
      float[] i_sigi = getI_and_sigI( qx, qy, qz, radii );
      if ( i_sigi != null )
      {
        peaks_kept.add( peaks.elementAt(i) );
        i_sigi_vec.add( i_sigi );
      }
    }
    
    if ( peaks_kept.size() == 0 )         // no peaks were integrated
      return 0;
                                          // reverse weight the peaks and set
                                          // the I and SIGI values
    float[] i_sigi;
    for ( int i = peaks_kept.size()-1; i >= 0; i-- )
    {
      Peak_new peak = peaks_kept.elementAt( i );
      i_sigi = i_sigi_vec.elementAt(i);
      peak.inti( i_sigi[0] );
      peak.sigi( i_sigi[1] );
      
      ReverseWeightIntegral( peak );
    }

    peak_list = peaks_kept;
    return peak_list.size();
  }


  /**
   *  Get an array containing the net integrated intensity and standard
   *  deviation, at the specified point, using the specified values for the 
   *  peak and background radii.
   *  
   *  @param x        The x-coordinate of the point to integrate, specified 
   *                  in Q, using the physics convention that Q = 2*PI/d.
   *  @param y        The y-coordinate of the point to integrate.
   *  @param z        The z-coordinate of the point to integrate.
   *  @param radii    Array with two floats.  The radii[0] specifies the radius
   *                  of the peak region and radii[1] specified the radius
   *                  of the background region.
   *  @return an array of two floats, the first specifying the net integrated
   *          intensity and the second specifying an estimate of the standard
   *          deviation of the intensity.
   */
  private float[] getI_and_sigI( float x, float y, float z, float[] radii )
  {
    if ( histogram == null )
      return null;

    float region_count = histogram.totalNear( x, y, z, 1 );
    if ( region_count <= 0 )                         // skip peaks with zero
      return null;                                   // counts in 27 bin region 

    float cradius = radii[0] / 2;
    Vector3D centroid = new Vector3D( x, y, z );
    for ( int i = 0; i < 3; i++ )
    {
       centroid = histogram.centroid( centroid.getX(),
                                      centroid.getY(),
                                      centroid.getZ(), cradius );
       if ( centroid == null )
         centroid = new Vector3D( x, y, z );        // reset centroid
    }

    Vector result = histogram.sphereIntegrals( centroid.getX(),
                                               centroid.getY(),
                                               centroid.getZ(), radii );
    if ( result == null )
      return null;

    float[] sums    = (float[])result.elementAt(0);
    float[] volumes = (float[])result.elementAt(1);

    float peak_count = sums[0];
    float bkg_count  = sums[1] - peak_count;

    float peak_volume = volumes[0];
    float bkg_volume  = volumes[1] - peak_volume;

    float[] i_sigi = IntegrateTools.getI_and_sigI( peak_count, peak_volume,
                                                    bkg_count, bkg_volume );
    return i_sigi;
  }


  /**
   *  Since ANVRED also does the Lorentz correction, this method is used
   *  to remove the Lorentz correction from the integrated peak intensity.
   *
   *  @param peak  The integrated peak that will have the Lorentz correction
   *               removed.
   *
   *  @return true if the Lorentz correction was successfully removed,
   *          and false if the calculation could not be carried out.
   */
  private boolean ReverseWeightIntegral( Peak_new peak )
  {
    float[] q  = peak.getUnrotQ();
    float   qx = q[0];
    float   qy = q[1];
    float   qz = q[2];
    Vector3D Qxyz = new Vector3D( qx, qy, qz );

    float  magnitude_Q = Qxyz.length();
    float  d           = 1/magnitude_Q;
    double off_axis    = Math.sqrt( qy * qy + qz * qz );
    double beam_comp   = qx;
    float  alpha       = (float)Math.atan2(off_axis,beam_comp);
    float  two_theta   = (float)(2*Math.abs(alpha) - Math.PI);

    float  wl     = (float) (2 * d * Math.sin( two_theta/2 ));
    float  weight = mapper.getEventWeight( wl, two_theta );

    if ( weight != 0 )
    {
      peak.inti( peak.inti() / weight );
      peak.sigi( peak.sigi() / (float)Math.sqrt(weight) );
      peak.reflag( 510 );
      return true;
    }
    else
      return false;
  }

  
  /**
   *  Write the current list of peaks to the specified file.
   *
   *  @param filename   The name of the file to write.
   *  @throws An IOException will be thrown if the file cannot be written.
   */
  public void SavePeaks( String filename ) throws IOException
  {
    if ( peak_list == null )
      throw new IOException("Peak list is NULL, can't write " + filename );
    Peak_new_IO.WritePeaks_new( filename, peak_list, false );
  }


  /**
   *  Write the current list of peaks to the specified file.
   *
   *  @param filename   The name of the file to write.
   *  @throws An IOException will be thrown if the file cannot be written.
   */
  public void SaveUB( String filename ) throws IOException
  {
    if ( UB == null )
      throw new IOException("UB matrix is NULL, can't write " + filename );

    float[][] UB_float = UB.get();
    double[][] UB_double = new double[3][3];
    for ( int i = 0; i < 3; i++ )
      for ( int j = 0; j < 3; j++ )
        UB_double[i][j] = UB_float[i][j];

    double[] abc = Util.abc( UB_double );
    float[]  abc_float = new float[ abc.length ];
    for ( int i = 0; i < abc.length; i++ )
      abc_float[i] = (float)abc[i];

    float[]  sig_abc = { 0, 0, 0, 0, 0, 0, 0 };

    Util.writeMatrix(filename, UB_float, abc_float, sig_abc);
  }


  /**
   *  Carry out all of the initial SCD data reduction steps on ONE event
   *  file, producing a file of integrated intensities.
   *  @param instrument       Instrument name, such as TOPAZ or SNAP.  This 
   *                          must be the name of a supported instrument at 
   *                          the SNS.  The name determines which default 
   *                          instrument geometry files (.DetCal, bank and 
   *                          mapping files) from the ISAW distribution will 
   *                          be used.
   *  @param num_bins         The number of steps in each direction to use for
   *                          the underlying 3D histogram in reciprocal space.
   *                          The required storage is 4*num_bins^3.  A num_bins
   *                          value of 768 requires 1.8 Gb of memory and is a 
   *                          reasonable compromize between resolution and 
   *                          memory size and compute time.  
   *  @param max_Q            The maximum |Q| to use for the histogram region
   *                          and for events loaded from the raw event file.  
   *                          NOTE: the value for max_Q is specified here using
   *                                the physics convention that Q = 2*PI/d,
   *                                NOT the crystallographic conventions that 
   *                                Q = 1/d.
   *  @param wavelength_power The power on the wavelength term in the Lorentz
   *                          correction factor.  Theoretically, this should 
   *                          be 4, but a value of 2.4 works better for finding
   *                          peaks in small molecule data and a value of 1
   *                          works better for large molecules, such as 
   *                          protiens.  The Lorentz correction is "backed out"
   *                          before writing the .integrate file, since it is
   *                          applied in ANVRED, so this parameter is only a
   *                          fudge factor to even out the data for finding
   *                          peaks.
   *  @param event_file       The name of an SNS raw neutron_event.dat file.
   *  @param num_to_find      The maximum number of peaks to look for in the
   *                          histogram.
   *  @param threshold        The minimum histogram value that will be 
   *                          considered for a possible peak.
   *  @param peaks_file       The name of the file to which the initial list of
   *                          found peaks will be written. 
   *  @param a                Lattice parameter 'a'.
   *  @param b                Lattice parameter 'b'.
   *  @param c                Lattice parameter 'c'.
   *  @param alpha            Lattice parameter 'alpha'.
   *  @param beta             Lattice parameter 'beta'.
   *  @param gamma            Lattice parameter 'gamma'.
   *  @param tolerance        The required tolerance on h,k and l for a peak to
   *                          be considered to be indexed.  If a peak is not 
   *                          indexed the h,k,l values are recorded as 0,0,0.
   *  @param indexed_file     The name of the file to which the indexed peaks
   *                          will be written.
   *  @param matrix_file      The name of the file to which the orientation
   *                          matrix will be written.
   *  @param radius           The radius of the peak region to integrate, 
   *                          specified in Q, using the physics convention.
   *  @param integrate_all    If true, predict peak positions from the UB 
   *                          matrix and integrate all predicted peak 
   *                          positions. (THIS IS NOT CURRENTLY IMPLEMENTED). 
   *                          If false, just integrate the peaks that were 
   *                          actually found. 
   *
   *  @param integrate_file   The name of the .integrate file that will be
   *                          written, containing the integrated intensities.
   *  @throws Exceptions      Exceptions will be thrown if the calculation 
   *                          fails at some point, or if specified files cannot
   *                          be read or written.
   */
  public static void reduce( String  instrument,
                             int     num_bins,
                             float   max_Q,
                             float   wavelength_power,
                             String  event_file,
                             int     num_to_find,
                             float   threshold,
                             String  peaks_file,
                             float   a,
                             float   b,
                             float   c,
                             float   alpha,
                             float   beta,
                             float   gamma,
                             float   tolerance,
                             String  indexed_file,
                             String  matrix_file,
                             float   radius,
                             boolean integrate_all,
                             String  integrate_file )
                      throws Exception
  {
    AutoReduceSCD reducer = new AutoReduceSCD( instrument,
                                               num_bins,
                                               max_Q,
                                               wavelength_power );

    long n_loaded = reducer.LoadHistogram( event_file );
    System.out.println("Number added to histogram = " + n_loaded );

    int n_found = reducer.FindPeaks( num_to_find, threshold );
    System.out.println("Number of peaks found = " + n_found );

    reducer.SavePeaks( peaks_file );
    System.out.println("Wrote basic peaks to " + peaks_file );

    int n_indexed = reducer.FindUB_AndIndexPeaks( a, b, c, alpha, beta, gamma,
                                                  tolerance );
    System.out.println("Number of peaks indexed = " + n_indexed );
    reducer.SavePeaks( indexed_file );
    System.out.println("Wrote indexed peaks to " + indexed_file );

    reducer.SaveUB( matrix_file );
    System.out.println("Saved UB to matrix file " + matrix_file );

    int n_integrated = reducer.IntegratePeaks( radius,
                                               integrate_all );
    System.out.println( "Number of peaks integrated = " + n_integrated );
    if ( n_integrated > 0 )
    {
      reducer.SavePeaks( integrate_file );
      System.out.println("Wrote integrated peaks to "+ integrate_file);
    }
  }


  /**
   *  Main program can be invoked from command line.  This can be modified
   *  to pass any/all of the parameters on the command line.
   */
  public static void main( String[] args ) throws Exception
  {
    String  instrument       = "TOPAZ";
    int     num_bins         =  768;
    float   max_Q            = 14.0f;
    float   wavelength_power =  2.4f;

    String  event_file = "/usr2/TOPAZ_SAPPHIRE/TOPAZ_2480_neutron_event.dat";

    int     num_to_find = 100;
    float   threshold   = 5;
    String  peaks_file = "demo.peaks";

    float   a = 4.75f;
    float   b = 4.75f;
    float   c = 12.99f;
    float   alpha =  90;
    float   beta  =  90;
    float   gamma = 120;
    float   tolerance = 0.12f;
    String  indexed_peaks_file = "demo_indexed.peaks";

    String  matrix_file = "demo.mat";

    float   integration_radius    = 0.15f;
    boolean integrate_all         = false;
    String  integrated_peaks_file = "demo.integrate";

    reduce( instrument, num_bins, max_Q, wavelength_power,
            event_file, 
            num_to_find, threshold, peaks_file,
            a, b, c, alpha, beta, gamma, tolerance, indexed_peaks_file,
            matrix_file,
            integration_radius, integrate_all, integrated_peaks_file );
  }
}

