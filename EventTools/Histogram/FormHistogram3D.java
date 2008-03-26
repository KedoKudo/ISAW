/* 
 * File: FormHistogram.java
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

package EventTools.Histogram;

import java.io.*;
import java.util.*;

import EventTools.EventList.ByteFile16EventList3D;
import EventTools.EventList.IEventList3D;
import EventTools.Viewers.ShowHistogram;

/**
 *  This is a rough, initial test of the Histogram3D class.  It requires
 *  a binary file of bytes that can be read into a ByteFile16EventList3D. 
 */
public class FormHistogram3D 
{

  public static void main( String args[] ) throws IOException
  {
    int HIST_SIZE = 512;


    //------------
    long start_time = System.nanoTime();

    IEventList3D events = new ByteFile16EventList3D( args[0] );

    long end_time = System.nanoTime();
    System.out.println("Time to read Byte16FileEventList3D (ms) = " + 
                        (end_time - start_time) / 1000000 );

    int num_points = events.getNumEntries();
    double x_min = events.getXExtent().getMin();
    double x_max = events.getXExtent().getMax();
    double y_min = events.getYExtent().getMin();
    double y_max = events.getYExtent().getMax();
    double z_min = events.getZExtent().getMin();
    double z_max = events.getZExtent().getMax();

    System.out.println("NUMBER OF EVENTS = " + num_points );
    System.out.println("X range: " + events.getXExtent() );
    System.out.println("Y range: " + events.getYExtent() );
    System.out.println("Z range: " + events.getZExtent() );

    IEventBinner x_bin = new UniformEventBinner( x_min, x_max, HIST_SIZE );
    IEventBinner y_bin = new UniformEventBinner( y_min, y_max, HIST_SIZE );
    IEventBinner z_bin = new UniformEventBinner( z_min, z_max, HIST_SIZE );


    //------------
    start_time = System.nanoTime();

    Histogram3D hist_3D = new Histogram3D( x_bin, y_bin, z_bin );

    end_time = System.nanoTime();
    System.out.println("Time to allocate histogram (ms) = " +
                        (end_time - start_time) / 1000000 );


    //------------
    start_time = System.nanoTime();

    double n_events = hist_3D.addEvents( events );

    end_time = System.nanoTime();
    System.out.println("Time to form histogram (ms) = " +
                        (end_time - start_time) / 1000000 );
    System.out.println("ADDED " + n_events );
    System.out.println("min = " + hist_3D.getMin() );
    System.out.println("max = " + hist_3D.getMax() );
    System.out.println("sum = " + hist_3D.getTotal() );


    //------------- 
    boolean do_scan = false;
    if ( do_scan )
    {
      start_time = System.nanoTime();

      hist_3D.scanHistogram();

      end_time = System.nanoTime();
      System.out.println("Time to ScanHistogram (ms) = " + 
                        (end_time - start_time) / 1000000 );
      System.out.println("min = " + hist_3D.getMin() );
      System.out.println("max = " + hist_3D.getMax() );
      System.out.println("sum = " + hist_3D.getTotal() );
    }

    IEventBinner binner = new UniformEventBinner( 10, 100, 15 );
    //-------------
    boolean get_events = false;

    if ( get_events )
    {
      start_time = System.nanoTime();
      Vector all_events = (Vector)(hist_3D.getEventLists( binner ));
      end_time = System.nanoTime();
      System.out.println("Time to getEventLists  = " +
                          (end_time - start_time) / 1000000 );

      System.out.println("all_events size = " + all_events.size() );
      for ( int i = 0; i < all_events.size(); i++ )
      {
        Vector lists = (Vector)(all_events.elementAt(i));
        System.out.println("Sublist length = " + lists.size() );
/*
        for ( int k = 0; k < lists.size(); k++ )
        {
          IMultiEventList3D list = (IMultiEventList3D)(lists.elementAt(k));
          System.out.println( list );
        }
*/
      } 
    }


    binner = new UniformEventBinner( 5, 100, 15 );
    ShowHistogram.show_histogram( hist_3D, binner );
  }

}
