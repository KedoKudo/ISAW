/* 
 * File: DataSetDisplayable.java 
 *  
 * Copyright (C) 2007     Dennis Mikkelson
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu>
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2007/07/12 19:28:03  dennis
 * Initial version of DataSetDisplayable.
 * Does not yet implement attribute setting, and does not
 * yet use the with_controls parameter to determine whether
 * or not to include the viewer controls.
 *
 */

package DataSetTools.viewer.DisplayDevices;

import javax.swing.*;
import DataSetTools.dataset.*;
import DataSetTools.viewer.*;
import DataSetTools.retriever.*;
import gov.anl.ipns.DisplayDevices.*;

/**
 *  This class configures a view of a DataSet and produces a JComponent that
 *  can be printed, saved to a file or displayed on the screen by a specific 
 *  GraphicsDevice.
 */
public class DataSetDisplayable extends Displayable
{
  private DataSet     dataSet;
  private ViewManager viewManager;


 /**
  *  Construct an IDisplayable object to handle the specified DataSet
  *  and ViewType.
  *
  *  @param  ds        The DataSet to be displayed
  *  @param  view_type The type of DataSet viewer to use for the display
  */
  public DataSetDisplayable( DataSet ds, String view_type )
  {
    viewManager = new ViewManager( ds, view_type, false );  
    dataSet     = ds;
  }


 /**
  *  This method returns a JComponent that can be displayed in a Frame,
  *  printed, or saved to a file.
  *
  *  @param  with_controls   If this is false, any interactive controls
  *                          associated with the view of the data will
  *                          NOT be visible on the JComponent
  *
  *  @return A reference to a JComponent containing the configured 
  *          display.
  */
  public JComponent getJComponent( boolean with_controls )
  {
    JPanel panel = (JPanel)viewManager.getContentPane(); 
    return panel;
  }


 /**
  *  This method sets an attribute of the displayable that pertains
  *  to the overall display, such as a background color.
  *
  *  @param  name     The name of the attribute being set.
  *  @param  value    The value to use for the attribute.
  */
  public void setViewAttribute( String name, Object value )
  {
    // NOP for now
  }


 /**
  *  This method sets an attribute of the displayable that pertains
  *  to a particular portion of the display, such as one particular
  *  line. 
  *
  *  @param  index    An index identifying the part of the display
  *                   that the attribute applies to, such as a 
  *                   specific line number.
  *  @param  name     The name of the attribute being set.
  *  @param  value    The value to use for the attribute.
  */
  public void setLineAttribute( int index, String name, Object value )
  {
    // NOP for now
  }


  public static void main( String args[] )
  {
    String directory = "/home/dennis/WORK/ISAW/SampleRuns";
    String file_name = directory + "/GPPD12358.RUN";
    RunfileRetriever rr = new RunfileRetriever( file_name );
    DataSet ds = rr.getDataSet(1);

    ds.setSelectFlag(  5, true );
    ds.setSelectFlag( 10, true );
    ds.setSelectFlag( 15, true );
//  Displayable disp = new DataSetDisplayable(ds, "Image View");
//  Displayable disp = new DataSetDisplayable(ds, "Selected Graph View");
    Displayable disp = new DataSetDisplayable(ds, "Difference Graph View");
//  Displayable disp = new DataSetDisplayable(ds, "3D View");
    
    GraphicsDevice gd = new ScreenDevice();
    gd.setRegion( 400, 500, 600, 400 );
    gd.display( disp );
  }
}
