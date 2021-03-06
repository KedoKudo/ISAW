/*
 * File:  DataSetDemo1.java
 *
 * Copyright (C) 2000, Dennis Mikkelson
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
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.8  2004/05/10 20:42:22  dennis
 * Test program now just instantiates a ViewManager to diplay
 * calculated DataSet, rather than keeping a reference to it.
 * This removes an Eclipse warning about a local variable that is
 * not read.
 *
 * Revision 1.7  2004/03/15 06:10:52  dennis
 * Removed unused import statements.
 *
 * Revision 1.6  2004/03/14 20:23:45  dennis
 * Put in package DataSetTools.trial
 *
 * Revision 1.5  2002/11/27 23:23:30  pfpeterson
 * standardized header
 *
 * Revision 1.4  2002/02/28 19:57:36  dennis
 * Modified import statements due to operator reorganization.
 *
 */
package DataSetTools.trial;

import DataSetTools.dataset.*;
import DataSetTools.operator.DataSet.*;
import DataSetTools.operator.DataSet.Math.DataSet.*;
import DataSetTools.viewer.*;
import DataSetTools.retriever.*;

/**
  *    This class provides a basic demo of reading DataSets from runfiles, 
  *  showing the DataSets as images and scrolled graphs and invoking operators
  *  on the DataSets.  It loads and displays monitors and sample spectra from
  *  two runfiles. It then adds the monitor data together and adds the sample 
  *  spectra from the two runs together to form new DataSets and displays the
  *  combined monitor and sample spectra DataSets.
  *
  *    IMPORTANT!! Since this demo does NOT provide for any user interaction, 
  *  you will have to edit this source file to specify different runfiles to 
  *  load, or to call different operators, etc. 
  *
  *  To see how to write new operators, see the following examples in the
  *  package DataSetTools/operators
  * 
  *          Integrate.java                produces one value
  *          DataSetAdd.java               produces new DataSet
  *          SpectrometerTofToEnergy.java  produces new DataSet
  *
  *  Note that where possible, the basic calculations themselves (adding 
  *  spectra, doing a numerical integration, calculating energy at a particular
  *  distance/tof) have been implemented at a lower level, to allow reuse.
  */  

public class DataSetDemo1
{

  /**
    *  The main program method for this object
    */
  public static void main(String args[])
  {
    DataSet      A_monitor_ds,      // We'll fill out these DataSets from
                 B_monitor_ds,      // two runfiles A & B and add the 
                 A_histogram_ds,    // resulting monitor and histogram
                 B_histogram_ds;    // data sets.

    DataSet      monitor_ds,        // These DataSets will hold the sum
                 histogram_ds;      // of the data from runsfiles A & B 

    String       run_A = "/usr/home/dennis/ARGONNE_DATA/gppd9898.run";
    String       run_B = "/usr/home/dennis/ARGONNE_DATA/gppd9899.run";


    // Get the DataSets from the runfiles.  Show the monitors as scrolled 
    // graphs and the sample spectra as an image.  When getting the DataSets 
    // from the runfile, DataSet 0, should be the monitors, DataSet 1, the 
    // first histogram, etc.  There are methods to check this, but for now 
    // this should work.

    RunfileRetriever rr;    // The RunfileRetriever object calls John's runfile
                            // package and returns the data as DataSets

                                           // Load and show run A monitors &
    rr = new RunfileRetriever( run_A );    // histograms
    A_monitor_ds = rr.getDataSet( 0 );
    new ViewManager( A_monitor_ds, IViewManager.SCROLLED_GRAPHS);

    A_histogram_ds = rr.getDataSet( 1 );
    new ViewManager( A_histogram_ds, IViewManager.IMAGE );

                                            // Load and show run B monitors &
    rr = new RunfileRetriever( run_B );     // histograms
    B_monitor_ds = rr.getDataSet( 0 );
    new ViewManager( B_monitor_ds, IViewManager.SCROLLED_GRAPHS);

    B_histogram_ds = rr.getDataSet( 1 );
    new ViewManager( B_histogram_ds, IViewManager.IMAGE );

                                            // add the monitors together
    DataSetOperator adder;                  // and show the results
    adder = new DataSetAdd( A_monitor_ds, B_monitor_ds, true );
    monitor_ds = (DataSet)adder.getResult();
    new ViewManager( monitor_ds, IViewManager.SCROLLED_GRAPHS);

                                            // add the histograms together
                                            // and show the results
    adder = new DataSetAdd( A_histogram_ds, B_histogram_ds, true );
    histogram_ds = (DataSet)adder.getResult();
    new ViewManager( histogram_ds, IViewManager.IMAGE );
  } 
} 
