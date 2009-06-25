/* 
 * File: SNS_Tof_to_Q_map.java
 *
 * Copyright (C) 2009, Dennis Mikkelson
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
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 */

package EventTools.EventList;

import java.util.*;
import java.io.*;

import gov.anl.ipns.MathTools.Geometry.*;

import DataSetTools.operator.Generic.TOF_SCD.*;
import DataSetTools.dataset.*;
import DataSetTools.math.*;

/** 
 *  This class constructs the time-of-flight to vector Q mapping information
 *  needed to efficiently process raw SNS event data into reciprocal space.
 *  The information can be constructed from an ISAW .DetCal file, or from
 *  the detector position information at the start of a peaks file for the 
 *  instrument.  NOTE: The order and size of the detectors listed in the 
 *  .DetCal or .Peaks file MUST be the same as in the *neutron_event.dat
 *  file and ids must be stored by column in order of increasing row number,
 *  so that the pixel ids match.
 */
public class SNS_Tof_to_Q_map 
{
  public static final String SNAP = "SNAP";

  private String       filename;
  private IDataGrid[]  grid_arr; 
  private float        L1;             // L1 in meters.
  private float        t0;             // t0 shift in 100ns units
  private float[]      QUxyz;
  private float[]      tof_to_MagQ;

  /**
   *  Construct the mapping from (tof,id) to Qxyz from the information at
   *  the start of a .peaks file or .DetCal file.  NOTE: There MUST be an
   *  entry for each detector in the instrument, so a .peaks file may not 
   *  work, if some detectors are missing.
   *
   *  @param  filename  The name of the .DetCal or .peaks file with 
   *                    position information about EVERY detector and
   *                    L1 and t0 values.
   */
  public SNS_Tof_to_Q_map( String filename, String instrument_name )  
         throws IOException
  {
                                                  // First bring in the grids
     FileReader     f_in        = new FileReader( filename );
     BufferedReader buff_reader = new BufferedReader( f_in );
     Scanner        sc          = new Scanner( buff_reader );

     String version_title = sc.next();
     while ( version_title.startsWith("#") )      // Skip any comment lines
     {                                            // and the version info
       sc.nextLine();
       version_title = sc.next();
     }

     float[]   L1_t0          = Peak_new_IO.Read_L1_T0( sc );
     Hashtable grids          = Peak_new_IO.Read_Grids( sc );
     sc.close();

     L1 = L1_t0[0];
     t0 = L1_t0[1] * 10;                          // Need factor of 10, since
                                                  // SNS data is in terms of
                                                  // 100ns clock ticks.

                                                  // Sort the grids on ID
     Object[] obj_arr = (grids.values()).toArray();
     Arrays.sort( obj_arr, new GridID_Comparator() );
                        
                                                  // and record them in our
                                                  // local list.
     grid_arr = new IDataGrid[ obj_arr.length ];
     for ( int i = 0; i < grid_arr.length; i++ )
       grid_arr[i] = (IDataGrid)obj_arr[i]; 

     if ( instrument_name.equalsIgnoreCase("SNAP") )
       ReorderSNAP_grids( grid_arr );

     BuildMaps();

     System.out.println( "L1 = " + L1 );
     System.out.println( "t0 = " + t0 );
  }


  /**
   *  Map the specified time-of-flight events to a packed array of events
   *  in reciprocal space, listing Qx,Qy,Qz for each event, interleaved
   *  in one array.  NOTE: due to array size limitations in Java,
   *  at most (2^31-1)/3 = 715.8 million events can be processed in one
   *  batch by this method. 
   *
   *  @param tofs  List of integer time-of-flight values, giving the number
   *               of 100ns clock ticks since t0 for this event.
   *
   *  @param ids   List of detector pixel ids corresponding to the listed
   *               tofs.
   *
   *  @return an array of floats containing values (Qx,Qy,Qz) for each 
   *          event, interleaved in the array. 
   */
  public float[] BuildPackedQxyz( int[] tofs, int[] ids )
  {
     if ( tofs == null )
       throw new IllegalArgumentException( "Time-of-flight array is null" );

     if ( ids == null )
       throw new IllegalArgumentException( "Pixel id array is null" );

     if ( tofs.length != ids.length )
       throw new IllegalArgumentException("TOF array length " + tofs.length +
                                         " != id array length " + ids.length);

     if ( tofs.length > Integer.MAX_VALUE/3 )
       throw new IllegalArgumentException("TOF array length " + tofs.length +
                                         " exceeds " + Integer.MAX_VALUE/3 );
     if ( tofs.length == 0 )
       return new float[0];

     float[] Qxyz =  new float[ 3 * tofs.length ];
     int     id;
     int     id_offset;
     int     index;
     float   magQ;
     for ( int i = 0; i < tofs.length; i++ )
     {
       id        = ids[i];
       id_offset = 3*id;
       if ( id > 0 && id < tof_to_MagQ.length )
       {
         magQ = tof_to_MagQ[id]/(t0 + tofs[i]);

         index = i * 3;
         Qxyz[index] = magQ * QUxyz[id_offset];
         index++;
         Qxyz[index] = magQ * QUxyz[id_offset + 1];
         index++;
         Qxyz[index] = magQ * QUxyz[id_offset + 2];
       }
     } 

     for ( int i = 0; i < 10; i++ )
      System.out.printf("tof: %8.1f  id: %7d " +
                        "  Qx: %6.2f  Qy: %6.2f  Qz: %6.2f\n",
                        (tofs[i]/10.0), ids[i], 
                        Qxyz[3*i], Qxyz[3*i+1], Qxyz[3*i+2]);
     return Qxyz;
  }


  /**
   *  Build the tables giving the unit vector in the direction of Q and
   *  the conversion constant from time of flight to magnitude of Q for
   *  each pixel in the detector.
   */
  private void BuildMaps()
  {
    int pix_count = 0;                             // first count the pixels
    for ( int  i = 0; i < grid_arr.length; i++ )
    {
      IDataGrid grid = grid_arr[i];
      pix_count += grid.num_rows() * grid.num_cols(); 
    }                                          
                                                   // NOTE: the pixel IDs 
                                                   // start at 1 in the file
                                                   // so to avoid shifting we
                                                   // will also start at 1
    tof_to_MagQ = new float[  (pix_count + 1)];    // Scale factor to convert
                                                   // tof to Magnitude of Q
    QUxyz       = new float[3*(pix_count + 1)];    // Interleaved components
                                                   // of unit vector in the
                                                   // direction of Q for this
                                                   // pixel.

                                                   // Since SNS tofs are in
                                                   // units of 100ns, we need
                                                   // the factor of 10 in this
                                                   // partial constant
    float     part = (float)(10 * 4 * Math.PI / tof_calc.ANGST_PER_US_PER_M);
                                                   
    float     two_theta;
    float     L2;
    Vector3D  pix_pos;
    Vector3D  unit_qvec;
    int       n_rows,
              n_cols;
    float[]   coords;
    IDataGrid grid;
    int       index;
    pix_count = 1;
    for ( int  i = 0; i < grid_arr.length; i++ )
    {
      grid = grid_arr[i];
      n_rows = grid.num_rows();
      n_cols = grid.num_cols();

      for ( int col = 1; col <= n_cols; col++ )
        for ( int row = 1; row <= n_rows; row++ )
        {
           pix_pos    = grid.position( row, col );
           L2         = pix_pos.length();
           if ( row == 1 && col == 1 )
             System.out.println("L2 = " + L2);

           coords     = pix_pos.get();
           coords[0] -= L2;                        // internally using IPNS
                                                   // coordinates
           unit_qvec = new Vector3D(coords);
           unit_qvec.normalize();
           index = pix_count * 3;
           QUxyz[ index     ] = unit_qvec.getX();
           QUxyz[ index + 1 ] = unit_qvec.getY();
           QUxyz[ index + 2 ] = unit_qvec.getZ();

           two_theta   = (float)Math.acos( pix_pos.getX() / L2 );
           tof_to_MagQ[pix_count] = 
                          (float)(part * (L1 + L2) * Math.sin(two_theta/2));
           pix_count++; 
        }
    }

    for ( int i = 0; i < 10; i++ )
      System.out.printf( "For detector #%2d  " +
                         "MagQ conv factor = %7.4f  " + 
                         "Qx = %6.3f  Qy = %6.3f  Qz = %6.3f\n",
                          i,
                          tof_to_MagQ[i],
                          QUxyz[ 3*i     ],  
                          QUxyz[ 3*i + 1 ],  
                          QUxyz[ 3*i + 2 ]  );
  }


  /**
   *  Reorder the grids, so that they appear in the array of grids in the
   *  order that corresponds to the DAS pixel numbering.
   *  Looking at the face of the detector array from the sample position,
   *  the DAS numbers the 3x3 array of detectors as:
   *  
   *    2   5   8
   *    1   4   7
   *    0   3   6
   *
   *  The detectors as read into ISAW from the NeXus file are arranged
   *  and given detector numbers as:
   *
   *    4   3   2
   *    7   6   5
   *   10   9   8 
   *
   *  These initially occupy grid array positions numbered starting at 
   *  zero, as shown:
   *
   *    2   1   0
   *    5   4   3
   *    8   7   6
   *
   *  This method re-forms the grid array, so that the correct IDataGrid
   *  occupies the position indexed by the id information from the DAS.
   *  For example, the DAS expects that the pixels on its detector 0,
   *  are positioned and orgranized like ISAW/NeXus detector number 10
   *  in position 8 of the original array.
   *
   *  @param grid_arr  List of IDataGrids in order of increasing
   *                   detector number (according to ISAW/NeXus).
   */
  private void ReorderSNAP_grids( IDataGrid[] grid_arr )
  {
    if ( grid_arr.length != 9 )
      System.out.println("WARNING: SNAP configuration changed. \n" +
                         "Update SNS_Tof_to_Q_map.ReorderSNAP_grids!" );

    IDataGrid[] temp = new IDataGrid[ grid_arr.length ];
 
    for ( int i = 0; i < grid_arr.length; i++ )
      temp[i] = grid_arr[i];

    grid_arr[0] = temp[8];
    grid_arr[1] = temp[5];
    grid_arr[2] = temp[2];
    grid_arr[3] = temp[7];
    grid_arr[4] = temp[4];
    grid_arr[5] = temp[1];
    grid_arr[6] = temp[6];
    grid_arr[7] = temp[3];
    grid_arr[8] = temp[0];
     
  }


  public class  GridID_Comparator implements Comparator
  {
   /**
    *  Compare two IDataGrid objects based on their IDs.
    *
    *  @param  grid_1   The first grid 
    *  @param  grid_2   The second grid 
    *
    *  @return An integer indicating whether grid_1's ID is greater than,
    *          equal to or less than grid_2's ID.
    */
    public int compare( Object obj_1, Object obj_2 )
    {
      int id_1 = ((IDataGrid)obj_1).ID();
      int id_2 = ((IDataGrid)obj_2).ID();

      if ( id_1 > id_2 )
        return 1;
      else if ( id_1 == id_2 )
        return 0;
      else 
        return -1;
    }
  }


  public static void main( String args[] ) throws IOException
  {
     String filename =  "/usr2/SNS_SCD_TEST_3/SNAP_1_Panel.DetCal";
     SNS_Tof_to_Q_map mapper = new SNS_Tof_to_Q_map( filename, SNAP );
  }

} 

