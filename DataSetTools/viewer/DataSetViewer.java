/*
 * File:  DataSetViewer.java 
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.7  2001/04/26 14:21:33  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.6  2001/01/29 21:26:39  dennis
 *  Now uses CVS version numbers.
 *
 *  Revision 1.5  2000/12/07 23:04:17  dennis
 *  Now includes basic support for maintaining ViewerState.
 *
 *  Revision 1.4  2000/07/11 22:40:01  dennis
 *  @see reference fixed
 *
 *  Revision 1.3  2000/07/10 22:58:58  dennis
 *  Now Using CVS 
 *
 *  Revision 1.15  2000/06/12 19:58:19  dennis
 *  Now implements Serializable
 *
 *  Revision 1.14  2000/05/16 22:37:36  dennis
 *  Added default implementation of getXConversionScale() and a couple of
 *  constant Strings to help communication between the viewer and ViewManager.
 *
 *  Revision 1.13  2000/05/11 15:50:58  dennis
 *  Added RCS logging
 *
 *   1.01  Added methods to redraw() and set the DataSet to be viewed.  Also
 *         reorganized the menu bar and removed some unneeded methods. 
 */

package DataSetTools.viewer;

import  DataSetTools.dataset.*;
import  java.awt.*;
import  java.io.*;
import  javax.swing.*;

/**
 *  DataSetViewer is the abstract base class for objects that provide views of
 *  DataSets.  
 *
 *  @see DataSetTools.viewer.Image.ImageView
 *  @see DataSetTools.viewer.Graph.GraphView
 *  @see OverplotView.SelectedGraphView
 *
 */ 

public abstract class DataSetViewer extends    JPanel
                                    implements Serializable
{
    public static final int FILE_MENU_ID   = 0;
    public static final int EDIT_MENU_ID   = 1;
    public static final int VIEW_MENU_ID   = 2;
    public static final int OPTION_MENU_ID = 3;

    public static final String NEW_DATA_SET    = "New Data Set";
    public static final String BINS_CHANGED    = "Bins Changed";
    public static final String X_RANGE_CHANGED = "X Range Changed";

    private   DataSet     data_set; // The Data Set being viewed 
    private   ViewerState state;    // state information for the DataSetViewer
    
    protected JMenuBar menu_bar;    // NOTE: it is the responsibility 
                                    // of the user of this class to add the 
                                    // MenuBar to whatever frame the viewer 
                                    // is placed in. 
 
    JMenu fileMenu    = new JMenu("File");      // Meus for the menu bar 
    JMenu editMenu    = new JMenu("Edit");
    JMenu viewMenu    = new JMenu("View");
    JMenu optionsMenu = new JMenu("Options");

    /**
     * Accepts a DataSet creates an instance of a viewer for the data set, 
     * using default options.
     *
     * @param  data_set   The DataSet to be viewed
     */
    public DataSetViewer( DataSet data_set )
    {
        this( data_set, null );
    }
    
    /** 
     * Accepts a DataSet and a ViewerState object and creates an instance of 
     * a viewer for the data set, with it's options initialized from the
     * state paramter.  If the state parameter is null, a new default state
     * is created.
     * 
     * @param  data_set   The DataSet to be viewed
     * @param  state      The state of options for this viewer
     */
    public DataSetViewer( DataSet data_set, ViewerState state )
    {
        this.data_set = data_set;
        if ( state == null )
          this.state    = new ViewerState();
        else
          this.state    = state;

        //neccesary so popup menus show up above heavyweight containers
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        menu_bar = new JMenuBar();
        menu_bar.add(fileMenu); 
        menu_bar.add(editMenu); 
        menu_bar.add(viewMenu); 
        menu_bar.add(optionsMenu);
    }

    /**
     *  Change the DataSet being viewed to the specified DataSet.  Derived
     *  classes should override this and take what additional steps are needed
     *  to change the specific viewer to the deal with the new DataSet.
     *
     *  @param  ds  The new DataSet to be viewed
     */
    public void setDataSet( DataSet ds )
    {
      data_set = ds;
    }
    
    /**
     *  Get a reference to the DataSet currently being viewed.
     */
    public DataSet getDataSet()
    {
      return data_set;
    }


    /**
     *  Get a reference to a state object containing information about the
     *  state of the options for this viewer. 
     */
    public ViewerState getState()
    {
      return state;
    }


    /**
     *  Redraw the view of the DataSet, since the DataSet has been changed.
     *
     *  @param  reason  The reason that the redraw was suggested.  The reasons
     *                  will be the reasons sent out via notification
     *                  of DataSet observers, or the message NEW_DATA_SET,
     *                  if the entire DataSet was replaced.  
     */
    public abstract void redraw( String reason );

    /**
     *  Get a reference to the menu bar for this viewer.
     */
    public JMenuBar getMenuBar()
    { 
      return menu_bar; 
    }

    /**
     *  Return a range of X values specified by the user to be used to 
     *  control X-Axis conversions.  This may be overridden in derived 
     *  classes.  ( The default behavior is to just returns null. )
     *
     *  @return  null.  Derived classes may override this an return a 
     *                  meaningful X scale.  In that case, number of bins
     *                  and the start and end X values will be used for 
     *                  any subsequent conversions operations.
     *
     */
    public UniformXScale getXConversionScale()
    {
      return null;
    } 


}
