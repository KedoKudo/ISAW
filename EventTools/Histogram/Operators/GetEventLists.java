/* 
 * File: getEventLists.java
 *
 * Copyright (C) 2008, Dennis Mikkelson
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
package EventTools.Histogram.Operators;

import java.util.*;

import EventTools.EventList.FloatArrayEventList3D;
import EventTools.EventList.IEventList3D;
import EventTools.Histogram.IEventBinner;
import EventTools.Histogram.IProjectionBinner3D;
import EventTools.Histogram.ProjectionBinner3D;

import gov.anl.ipns.Operator.*;

/**
 * This IOperator extracts information from the specified 3D array in the form
 * of lists of events at bin centers, for bins whose count values are in
 * specified intervals.  The code for the "event" is the index of the binner
 * interval containing the bin count, if the bin count is in the half open
 * interval covered by the binner.  If the bin count is greater than the
 * right end point of the interval covered by the binner, then the 
 * event code is the number of bins in the binner.  If the bin count is less
 * than the left endpoint NO event is associated with the bin.  Several 
 * objects of this class, covering different ranges of "pages" in the 3D 
 * array are run in separate threads by a method in the Histogram3D class, 
 * to get events from the entire Histogram3D object.  
 */

public class GetEventLists implements IOperator
{
  private float[][][]  histogram;
  private int          first_page;
  private int          last_page;
  private IEventBinner binner;      // Binner to categorize the values stored
                                    // in the histogram.

  private IProjectionBinner3D x_edge_binner;
  private IProjectionBinner3D y_edge_binner;
  private IProjectionBinner3D z_edge_binner;
                                    // The x_edge, y_edge, z_edge binners are
                                    // the "edge" binners required to re-
                                    // construct the bin center positions from
                                    // the col, row and page indices.  They
                                    // will be the same as the x,y,z binners
                                    // used to place events in histogram bins
                                    // IF AND ONLY IF the direction vectors for
                                    // the x,y,z binners are mutually 
                                    // orthogonal and follow a right hand rule.

  /**
   * Construct an operator to extract events from the specified range of
   * pages of the specified histogram array.  All events are assumed to 
   * occur at the bin centers.  Suppose the one-dimensional binner has bins 
   * [ai,bi) for i = 0 to NUM_BINS-1.  A bin with counts=C where C lies in 
   * [ai,bi) will give an event with code i at the x,y,z values corresponding 
   * to the center of the bin's column, row and page.  
   *   The mapping from column, row, page to a point in 3D space is determined
   * by the x, y and z "edge binners".  These are the binners that specify 
   * the edges of the histogram bins (parallelepipeds).
   *  
   * @param histogram       The 3D array from which the events are extracted.
   * @param first_page      The first page of the portion of the 3D histogram 
   *                        that this operator will use.
   * @param last_page       The last page of the portion of the 3D histogram 
   *                        that this operator will use.
   * @param x_edge_binner   The binner passed to 3D histogram constructor
   *                        that determines the edges of the histogram bins
   *                        in the local "X" direction.
   * @param y_edge_binner   The binner passed to 3D histogram constructor
   *                        that determines the edges of the histogram bins
   *                        in the local "y" direction.
   * @param z_edge_binner   The binner passed to 3D histogram constructor
   *                        that determines the edges of the histogram bins
   *                        in the local "z" direction.
   * @param binner          This binner specifies the count values that will
   *                        be returned as events when the getResult() 
   *                        method is called.
   */
  public GetEventLists( float[][][]         histogram, 
                        int                 first_page, 
                        int                 last_page,
                        IProjectionBinner3D x_edge_binner,
                        IProjectionBinner3D y_edge_binner,
                        IProjectionBinner3D z_edge_binner,
                        IEventBinner        binner     )
  {
    this.histogram  = histogram;
    this.first_page = first_page;
    this.last_page  = last_page;

    this.x_edge_binner = x_edge_binner;
    this.y_edge_binner = y_edge_binner;
    this.z_edge_binner = z_edge_binner;

    this.binner = binner;
  }


  /**
   *  getResult() returns lists of events from the specified range of 
   *  pages of the histogram array.  For each bin of the specified 
   *  IEventBinner, a (possibly empty) list of events will be returned
   *  with each event corresponding to a bin in the histogram with count
   *  value in the corresponding interval.  For example, if the specified
   *  binner split the interval [10,100) into 9 uniform bins, then 9 
   *  lists of events will be returned.  The first list of events will 
   *  have x,y,z values at bin centers, for bins with counts in the 
   *  interval [10,20).  The second list of events will have x,y,z values
   *  at bin centers, for bins with counts in the interval [20,30), etc.
   *  The event code for a bin with count in the 0th interval, [10,20), will 
   *  be zero; the event code for a bin with count in the 1st interval, 
   *  [20,30), will be 1, etc.  The event code for a bin with count >= 100
   *  will be 9.
   *
   *  @return A vector containing one IEventList3D object for each 
   *          interval [ai,bi) of the given binner, and on IEventList3D
   *          object for bins exceeding the maximum value of the binner. 
   *          NOTE: If there were no events in an interval [ai,bi) then
   *          the corresponding entry in the Vector will be null. 
   *          CAUTION: IT IS NECESSARY TO CHECK IF EACH RETURNED VECTOR 
   *                   ENTRY IS NULL.
   */
  public Object getResult()
  {
    int       n_bins    = binner.numBins();
    int[]     bin_count = new int[ n_bins + 1 ];
    
    int       index;
    float[][] one_page;
    float[]   one_row;

    int n_cols = histogram[0][0].length;
    int n_rows = histogram[0].length;

    for ( int page = first_page; page <= last_page; page++ )
    {
      one_page = histogram[page];
      for ( int row = 0; row < n_rows; row++ )
      {
        one_row = one_page[row];
        for ( int col = 0; col < n_cols; col++ )
        {
          index = binner.index( one_row[col] );
          if ( index >= 0 && index < n_bins )
            bin_count[index]++;
          else if ( index >= n_bins )
            bin_count[n_bins]++;
        }
      }
    }

    float[][] weights  = new float[n_bins+1][] ;
    float[][] xyz_vals = new float[3*(n_bins+1)][];

    Vector result = new Vector( n_bins );
    int n_events;
    for ( int i = 0; i < n_bins+1; i++ )
    {
      n_events = bin_count[i];
      if ( n_events > 0 )
      { 
        weights[i]  = new float[ n_events ];
        xyz_vals[i] = new float[ n_events * 3 ]; 
      }
    }
                                     // Since the x,y,z- binners use direction
                                     // vectors, we need to calculate the
                                     // vector position as a vector sum for
                                     // each bin.  The array coords will
    float[] coords = new float[3];   // be set to that vector sum

    int[] ilist = new int[n_bins+1];
    int   i_index;
    for ( int page = first_page; page <= last_page; page++ )
    {
      one_page = histogram[page];
      for ( int row = 0; row < n_rows; row++ )
      {
        one_row = one_page[row];
        for ( int col = 0; col < n_cols; col++ )
        {
          index = binner.index( one_row[col] );
          if ( index >= n_bins )
            index = n_bins;
          if ( index >= 0 )
          {
            weights[index] [ ilist[index] ] = index;
            ProjectionBinner3D.centerPoint( col, row, page,
                                            x_edge_binner, 
                                            y_edge_binner, 
                                            z_edge_binner,
                                            coords );
            i_index = 3 * ilist[index];
            xyz_vals[index][ i_index++ ] = coords[0];
            xyz_vals[index][ i_index++ ] = coords[1];
            xyz_vals[index][ i_index   ] = coords[2];
            ilist[index]++;
          }
        }
      }
    }

    for ( int i = 0; i < n_bins; i++ )
    {
      n_events = bin_count[i];
      if ( n_events > 0 )
      {
        IEventList3D events = new
         FloatArrayEventList3D( weights[i], xyz_vals[i] );

        result.add( events );
      }
      else
        result.add( null );
    }

    return result;
  }
}
