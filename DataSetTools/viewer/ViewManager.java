/*
 * File:  ViewManager.java
 *
 * Copyright (C) 1999, Dennis Mikkelson
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
 *  $Log$
 *  Revision 1.81  2007/03/30 19:24:36  amoe
 *  - Added to setView(..): If the viewer is a DataSetViewerMaker, then attempt to zoom in on the graph based on preset axis min/max
 *  variables in the IsawProps.dat file.
 *
 *  Revision 1.80  2007/01/12 14:41:39  dennis
 *  Added constructor that takes a third boolean parameter, show_now,
 *  that controls whether or not the ViewManager is immediately
 *  displayed when it is constructed.
 *
 *  Revision 1.79  2006/07/19 18:51:47  rmikk
 *  Removed an unused string constatn
 *
 *  Revision 1.78  2006/07/10 21:28:25  dennis
 *  Removed unused imports, after refactoring the PG concept.
 *
 *  Revision 1.77  2006/07/10 16:26:01  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.76  2006/06/26 16:30:25  amoe
 *  - added code getDataSetView(..) so it would handle the Difference
 *    Graph View
 *  - added button for Difference Graph and appended to view_menu in
 *    BuildViewMenu()
 *
 *  Revision 1.75  2006/06/22 22:39:54  amoe
 *  - Changed getDataSetView(..) 's call to new
 *    DifferenceViewComponent, back to new FunctionViewComponent;
 *    this is due to design issues.  (Un-doing the previous change)
 *
 *  Revision 1.74  2006/06/22 20:50:39  amoe
 *  - Commented out '... = new FunctionViewComponent(...)' and added
 *   '... = new DifferenceViewComponent(...)'
 *
 *  Revision 1.73  2006/06/02 21:44:06  rmikk
 *  Put pointed at Table view under the options menu
 *  Only a maximum of one of these are now available at any time
 *  A new one is created with the axis conversions
 *  A listener for when this QuickView is closed was added to adjust the
 *     menu item referring to this Quick pointed at table view
 *
 *  Revision 1.72  2006/05/31 14:31:20  rmikk
 *  Added the Pointed At table view to the menu system.  This is not a data set
 *  viewer
 *
 *  Revision 1.71  2006/03/13 20:24:11  rmikk
 *  Now implements IPreserveState so it can be used with the ObjectState operators
 *
 *  Revision 1.70  2006/01/05 17:26:46  rmikk
 *  Eliminated a debug print
 *
 *  Revision 1.69  2005/12/02 18:13:22  rmikk
 *  Fixed a typo error.
 *
 *  Revision 1.68  2005/12/01 19:18:54  rmikk
 *  Now uses the new Browser
 *
 *  Revision 1.67  2005/10/14 15:00:15  dennis
 *  Added missing javadocs on one method.
 *  Minor formatting improvement.
 *
 *  Revision 1.66  2005/08/27 13:21:55  rmikk
 *  Updated Object state before saving it so last changes are retained
 *
 *  Revision 1.65  2005/08/13 21:14:34  rmikk
 *  Changed Help submenu from About to Viewer
 *
 *  Revision 1.64  2005/08/13 17:10:17  rmikk
 *  Now use ActionListener and submenu for the help menu
 *
 *  Revision 1.63  2005/08/12 15:33:09  rmikk
 *  Eliminated some of the hover pop up behavior for the Viewer help menus
 *
 *  Revision 1.62  2005/08/10 14:13:26  rmikk
 *  Made the help Frame uneditable
 *  Connected documentation to the Instrument Table, GRX_Y, and 
 *     Parallel y(x) data set viewers
 *
 *  Revision 1.61  2005/08/05 14:00:10  rmikk
 *  Changed title and size of the help frame that pop up from the help button on
 *  each of the viewers
 *
 *  Revision 1.60  2005/08/03 19:07:26  rmikk
 *  Changed 3d_view.html to 3D_view.html for the 3D case only.  All the other
 *    viewers.html had lower case letters
 *
 *  Revision 1.59  2005/08/02 22:31:30  rmikk
 *  Added methods and listeners to build the help menu
 *
 *  Revision 1.58  2005/05/27 03:53:31  dennis
 *  Removed unused import.
 *
 *  Revision 1.57  2005/05/25 20:24:46  dennis
 *  Now calls convenience method WindowShower.show() to show
 *  the window, instead of instantiating a WindowShower object
 *  and adding it to the event queue.
 *
 *  Revision 1.56  2005/04/11 00:49:13  rmikk
 *  Added a Save Object State and Load Object State options under the 
 *  File Menu
 *
 *  Revision 1.55  2005/04/11 00:16:07  rmikk
 *  Added a setObjectState and getObjectState methods
 *
 *  Revision 1.54  2005/04/10 18:52:21  rmikk
 *  Updated the ObjectState for a DataSetViewer right before it is being
 *  replaced by another DataSetViewer.
 *
 *  Revision 1.53  2005/02/02 21:53:18  dennis
 *  Added string constants for PRINT and SAVE_IMAGE options.
 *  Re-ordered File menu items.  So close option is last.
 *  Since some individual viewers already add a "PRINT" entry to
 *  the file menu, additional work with viewers will be needed
 *  to place "PRINT" third in the list of FILE actions.
 *
 *  Revision 1.52  2005/01/05 15:38:11  rmikk
 *  Now adds the Print and Save Image menu items automatically to the
 *  File menu if the DataSetViewer does not do it.
 *
 *  Revision 1.51  2004/08/24 18:52:18  rmikk
 *  Caught errors on viewer initialization and returned null
 *
 *  Revision 1.50  2004/08/04 22:14:23  rmikk
 *  DataSetViewers that implement IPreserveState can now support the Object 
 *  State
 *
 *  Revision 1.49  2004/05/10 22:46:53  dennis
 *  Test program now just instantiates a ViewManager to diplay
 *  calculated DataSet, rather than keeping a reference to it.
 *  This removes an Eclipse warning about a local variable that is
 *  not read.
 *
 *  Revision 1.48  2004/04/16 20:29:13  millermi
 *  - DataSetData no longer used as a parameter, now used to
 *    convert DataSets to IVirtualArrayList1D objects.
 *
 *  Revision 1.47  2004/03/23 15:51:44  rmikk
 *  Listeners were added to finalized the ViewManager(JFrame) after it is
 *     disposed.
 *
 *  Revision 1.46  2004/03/19 17:18:43  dennis
 *  Removed unused variables
 *
 *  Revision 1.45  2004/03/15 06:10:55  dennis
 *  Removed unused import statements.
 *
 *  Revision 1.44  2004/03/15 03:28:59  dennis
 *  Moved view components, math and utils to new source tree
 *  gov.anl.ipns.*
 *
 *  Revision 1.43  2004/02/11 22:37:48  millermi
 *  - Added getView() which returns the viewer type currently
 *    being displayed in the ViewManager.
 *
 *  Revision 1.42  2004/01/29 00:02:57  dennis
 *  Added HKL_SliceView
 *
 *  Revision 1.41  2004/01/24 22:02:38  bouzekc
 *  Removed unused imports.
 *
 *  Revision 1.40  2003/12/12 18:11:34  dennis
 *  Now uses the WindowShower utility class to display the ViewManager
 *  from the Swing event handling thread, instead of showing it directly.
 *
 *  Revision 1.39  2003/10/30 17:23:17  dennis
 *  Removed import of DataSetTools.viewer.OverplotView
 *
 *  Revision 1.38  2003/10/30 17:16:06  dennis
 *  Removed "OLD Selected Graph View" that used SGT.
 *
 *  Revision 1.37  2003/10/15 03:56:38  bouzekc
 *  Fixed javadoc errors.
 *
 *  Revision 1.36  2003/09/11 17:22:41  rmikk
 *  Added a constructor that takes the ViewerState
 *  Extracted code to return a DataSetViewer given a DataSet,
 *     View Name and ViewerStata
 *
 *  Revision 1.35  2003/08/08 17:54:10  dennis
 *  Added option to change to New Selected Graph (Brent's) view.
 *
 *  Revision 1.34  2003/03/18 14:42:44  dennis
 *  Added option for popping up an additional ViewManager to the
 *  view menu of an existing ViewManager
 *
 *  Revision 1.33  2003/03/04 20:25:35  dennis
 *  Title on window is now set properly if the contents of the DataSet
 *  are changed to a different run.
 *
 *  Revision 1.32  2002/12/09 13:11:21  dennis
 *  Now checks for valid "pointed at" index, before using it as index
 *  into list of (possibly) reordered Data blocks.
 *
 *  Revision 1.31  2002/11/27 23:24:18  pfpeterson
 *  standardized header
 *
 *  Revision 1.30  2002/11/07 16:33:04  pfpeterson
 *  Closes viewer when message is recieved from the DataSet.
 *
 *  Revision 1.29  2002/10/16 19:22:15  dennis
 *  Added option to "link/unlink" views.  If the view is not linked to
 *  other views, the POINTED_AT_CHANGED messages are not passed out
 *  to other viewers, or acted on if they come from other viewers.
 *
 *  Revision 1.28  2002/10/08 15:44:06  dennis
 *  Added conversions of "Pointed At X" to proper units when the X-Axis
 *  of the DataSet has been converted in the ViewManager.
 *
 *  Revision 1.27  2002/10/07 19:35:57  dennis
 *  "Clear Selections" menu option now clears selections in the temporary
 *  DataSet as well as the original DataSet.  This fixes a bug where the
 *  "Clear Selection" failed to clear the selections on the viewer if an
 *  Axis Conversion had been done.
 *
 *  Revision 1.26  2002/10/02 22:04:15  dennis
 *  Now check the result of calling the conversion operator.  If some Data
 *  blocks are not converted, don't try to set the selection flags or
 *  PointedAt indices, since the meaning of the indices has changes.
 *  If a DataSet is not returned, just use the empty clone of the current
 *  DataSet.
 *
 *  Revision 1.25  2002/09/20 16:46:53  dennis
 *  Now uses IParameter rather than Parameter
 *
 *  Revision 1.24  2002/07/23 18:22:44  dennis
 *  Now passes "pointed at" x values between the DataSet
 *  and the tempDataSet.
 *
 *  Revision 1.23  2002/07/18 22:08:55  dennis
 *  Moved separate OverplotView hiearchy into DataSetTools/viewer
 *  hierarchy.
 *
 *  Revision 1.22  2002/07/17 19:10:55  rmikk
 *  Fixed up the table views menu choices and reordered
 *    the view menu
 *
 *  Revision 1.21  2002/07/16 21:37:55  rmikk
 *  Introduced support for the other quick table views
 *
 *  Revision 1.20  2002/07/12 18:26:15  rmikk
 *  Used the Constructor with the state variable for starting
 *    the Selected Graph view.
 *
 *  Revision 1.19  2002/07/10 19:39:03  rmikk
 *  Added code to incorporate the Contour View
 *
 *  Revision 1.18  2002/02/22 20:37:11  pfpeterson
 *  Operator reorganization.
 *
 */
 
package DataSetTools.viewer;
 
import DataSetTools.dataset.*;
import DataSetTools.operator.*;
import DataSetTools.operator.DataSet.*;
import DataSetTools.operator.DataSet.EditList.*;
import DataSetTools.operator.DataSet.Math.DataSet.*;
import DataSetTools.operator.DataSet.Conversion.XAxis.*;
import DataSetTools.components.ui.*;
//import DataSetTools.util.SysUtil;
import DataSetTools.viewer.Graph.*;
import DataSetTools.viewer.Image.*;
import DataSetTools.viewer.ThreeD.*;
import DataSetTools.viewer.Table.*;
import DataSetTools.viewer.Contour.*;
import DataSetTools.viewer.SCD_ReciprocalSpaceSlice.*;
import DataSetTools.components.View.*;
import gov.anl.ipns.MathTools.*;
import gov.anl.ipns.Parameters.IParameter;
import gov.anl.ipns.Util.Messaging.*;
import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.ViewTools.Components.OneD.*;
import gov.anl.ipns.ViewTools.Components.*;

import java.awt.event.*;
import java.io.*;
import javax.swing.*;
//import javax.swing.event.*;
import java.util.*;
import DataSetTools.util.PropertiesLoader;

/**
 *  A ViewManager object manages viewers for a DataSet in an external 
 *  frame.  It contains a menu bar that allows the user to select the type 
 *  of viewer to be used to view the DataSet and appropriate view options.  
 *  It also is an observer of the DataSet and will be notified of changes 
 *  in the DataSet.  Since a ViewManager is a JFrame, it can 
 *  be closed by the user, or by the program.
 */

public class ViewManager extends    JFrame
                         implements IViewManager,
                                    Serializable,
                                    IPreserveState,
                                    IFinish
{
   public static boolean debug_view_manager   = false;

   private   ViewManager     view_manager = null;
   private   DataSetViewer   viewer = null;
   private   String          viewType = IMAGE;
   private   ViewerState     state = null;
   private   DataSet         dataSet;
   private   DataSet         tempDataSet;
   private   XAxisConversionOp conversion_operator = null;
   private   int[]           original_index;      // records the index in the
                                                  // original dataSet that 
                                                  // corresponds to an index in
                                                  // the tempDataSet; 
   private   int[]           new_index;           // records the index in
                                                  // tempDataSet that 
                                                  // corresponds to an index in
                                                  // the original DataSet  
   private JCheckBoxMenuItem show_all_button;
   private JCheckBoxMenuItem link_viewers_button;
   private JCheckBoxMenuItem ShowPointedAtTable;

   private ObjectState Ostate = new ObjectState();

   private static final String SAVE_NEW_DATA_SET    = "Save As New DataSet";
   private static final String SAVE_IMAGE           = "Save Image";
   private static final String PRINT                = "Print";
   private static final String CLOSE_LABEL          = "Close Viewer";

   private static final String SUM_MENU           = "Sum";
   private static final String SUM_SELECTED       = "Sum Selected Data";
   private static final String SUM_UNSELECTED     = "Sum Unselected Data";

   private static final String DELETE_MENU          = "Delete";
   private static final String DELETE_SELECTED      = "Delete Selected Data";
   private static final String DELETE_UNSELECTED    = "Delete Unselected Data";

   private static final String CLEAR_MENU           = "Clear";
   private static final String CLEAR_SELECTED       = "Clear Selected Flags";

   private static final String SHOW_ALL             = "Show All";
   private static final String LINK_VIEWS           = "Link Views";
   private static final String NO_CONVERSION_OP     = "None";
   private static TableViewMenuComponents table_MenuComp   = null;
   private QuickTableViewer PointedAtView             =null;
   
    
   /**  
    *  Accepts a DataSet and view type and creates an instance of a 
    *  ViewManager for the data set.  
    *
    *  @param  ds        The DataSet to be viewed
    *  @param  view_type String describing the initial type of viewer to be 
    *                    used.  The valid strings are listed in the interface,
    *                    IViewManager
    * 
    *  @see DataSetTools.viewer.IViewManager
    *  @see DataSetTools.viewer.DataSetViewer
    *  @see DataSetTools.dataset.DataSet
    */
   public ViewManager(DataSet ds, String view_type )
   { 
      this( ds, view_type, null, true );
   }

   /**  
    *  Accepts a DataSet and view type and creates an instance of a 
    *  ViewManager for the data set.  
    *
    *  @param  ds        The DataSet to be viewed
    *  @param  view_type String describing the initial type of viewer to be 
    *                    used.  The valid strings are listed in the interface,
    *                    IViewManager
    *  @param  show_now  Flag indicating whether or not the ViewManager should
    *                    be immediately visible.
    * 
    *  @see DataSetTools.viewer.IViewManager
    *  @see DataSetTools.viewer.DataSetViewer
    *  @see DataSetTools.dataset.DataSet
    */
   public ViewManager(DataSet ds, String view_type, boolean show_now )
   {  
      this( ds, view_type, null, show_now );
   }


   /**  
    *  Accepts a DataSet and view type and creates an instance of a 
    *  ViewManager for the data set.  
    *
    *  @param  ds        The DataSet to be viewed
    *  @param  view_type String describing the initial type of viewer to be 
    *                    used.  The valid strings are listed in the interface,
    *                    IViewManager
    *  @param  state     The viewer state
    *  @param  show_now  Flag indicating whether or not the ViewManager should
    *                    be immediately visible.
    * 
    *  @see DataSetTools.viewer.IViewManager
    *  @see DataSetTools.viewer.DataSetViewer
    *  @see DataSetTools.dataset.DataSet
    */  
   public ViewManager(DataSet     ds, 
                      String      view_type, 
                      ViewerState state,
                      boolean     show_now )
   {
      super( ds.toString() );
      view_manager = this;
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      
      dataSet = ds; 
      if ( ds == null )
        System.out.println("ERROR: ds is null in ViewManager constructor");
      else
        dataSet.addIObserver( this );
      addWindowListener( new FinishWindowListener());
      addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent ev)
        {
          free_resources();
        }
      });

      setBounds(0,0,600,425);
      makeTempDataSet( true );
      this.state = state;
      setView( view_type ); 
  
      if ( show_now )                     // actually show the viewer from
        WindowShower.show( this );        // the Swing event handling thread
      
      conversion_operator = null;
      System.gc();
   }


   /**
    *  Specify a new DataSet to be viewed.
    *
    *  @param  ds   The new DataSet to be used by this ViewManager
    */ 
   public void setDataSet( DataSet ds )
   {
     if ( ds == dataSet )               // no change, just the same DataSet
     {
       if ( ds != null )
         setTitle( ds.toString() );
       return;
     }

     dataSet.deleteIObserver( this );
     dataSet = ds;
     makeTempDataSet( true );
     if ( ds != null )
     {
       ds.addIObserver( this );
       setTitle( ds.toString() );
     }

     if ( viewer != null )
       viewer.setDataSet( tempDataSet ); 

     System.gc();
   }

   /**
    *  Get the DataSet that is currently used by this ViewManager
    */   
   public DataSet getDataSet()
   {
     return dataSet;
   }

   /**
    *  Set a new viewer type for this ViewManager.  The available view 
    *  types are specified in the interface IViewManager.  Currently, the 
    *  supported view types are IViewManager.IMAGE and 
    *  IViewManager.SCROLLED_GRAPHS
    *
    *  @param   view_type  String specifying the type of viewer to be used.  
    *                      The valid strings are listed in the interface, 
    *                      IViewManager.
    */
   public void setView( String view_type )
   {

     
     getContentPane().setVisible(false);
     getContentPane().removeAll();
    	 
     if ( viewer != null ){
         state = viewer.getState();
         ObjectState st= viewer.getObjectState( false);
         Ostate.reset( viewType, st);
     }

      viewer = ViewManager.getDataSetView( tempDataSet,view_type, state);
      if( viewer instanceof ImageView)
          viewType = IMAGE;
      else 
          viewType = view_type;

      if( viewer instanceof IPreserveState){
         ObjectState st = (ObjectState)Ostate.get(viewType);
         
         if( st == null){
             st = ((IPreserveState)viewer).getObjectState( true);
             Ostate.insert( viewType, st);
         }
         
         if(viewer instanceof DataSetViewerMaker)
         {
        	 ObjectState graphRange_objSt = ((ObjectState)((ObjectState)((ObjectState)st.get("View")).get("FunctionControls")).get("Graph Range"));
        	 ObjectState functContr_objSt = ((ObjectState)((ObjectState)st.get("View")).get("FunctionControls"));
        	 ObjectState view_objSt = (ObjectState)st.get("View");
        	         	 
        	 PropertiesLoader propLoad = new PropertiesLoader("IsawProps.dat");
        	 
        	 String script_xmin = propLoad.get("XRange_"+dataSet.getX_units()+"_min");
        	 String script_xmax = propLoad.get("XRange_"+dataSet.getX_units()+"_max");
        	 String script_ymin = propLoad.get("YRange_"+dataSet.getY_units()+"_min");
        	 String script_ymax = propLoad.get("YRange_"+dataSet.getY_units()+"_max");
        	         	 
        	 //System.out.println("xmin: "+script_xmin);
        	 //System.out.println("xmax: "+script_xmax);
        	 //System.out.println("ymin: "+script_ymin);
        	 //System.out.println("ymax: "+script_ymax);
        	 
        	 if(script_xmin != null)
        	 {
        		 float xRange_time_min = Float.parseFloat(script_xmin);
    	         graphRange_objSt.reset("Min Range0",xRange_time_min);
        	 }        	 
        	 if(script_xmax != null)
        	 {
        		 float xRange_time_max = Float.parseFloat(script_xmax);
        		 graphRange_objSt.reset("Max Range0",xRange_time_max);
        	 }
        	 if(script_ymin != null)
        	 {
        		 float yRange_counts_min = Float.parseFloat(script_ymin);
    	         graphRange_objSt.reset("Min Range1",yRange_counts_min);
        	 }
        	 if(script_ymax != null)
        	 {
        		 float yRange_counts_max = Float.parseFloat(script_ymax);
        		 graphRange_objSt.reset("Max Range1",yRange_counts_max);
        	 }        	 
	         
	         functContr_objSt.reset("Graph Range",graphRange_objSt);
	         view_objSt.reset("FunctionControls",functContr_objSt);
	         st.reset("View",view_objSt);	         
         }
         
         ((IPreserveState)viewer).setObjectState(st);
         
      }
      
      getContentPane().add(viewer);
      getContentPane().setVisible(true);

      setJMenuBar( viewer.getMenuBar() );
      BuildFileMenu();
      BuildEditMenu();
      BuildViewMenu();
      BuildConversionsMenu();
      BuildOptionMenu();
      BuildHelpMenu( view_type);
      System.gc();
   }
   
   /**
    * Get the view type for this view manager. The view type Strings are
    * listed in the IViewManager interface. Use getDataSetView() to actually
    * get a DataSetViewer.
    *
    *  @return viewtype
    *  @see DataSetTools.viewer.IViewManager
    */
   public String getView()
   {
     return viewType;
   }


   /**
    *  Construct a DataSetViewer object for the specified type of view
    *
    *  @param  tempDataSet  The DataSet for which the viewer is constructed
    *  @param  view_type    The type of view to construct 
    *  @param  state        The initial viewer state for this DataSetViewer
    *
    *  @return The DataSetViewer object constructed for the DataSet
    */
   public static DataSetViewer getDataSetView( DataSet tempDataSet,
                                               String view_type,
                                               ViewerState state){
      DataSetViewer viewer = null;
      try{
        if ( view_type.equals( IMAGE ))
          viewer = new ImageView( tempDataSet, state );
        else if ( view_type.equals( SCROLLED_GRAPHS ))
          viewer = new GraphView( tempDataSet, state );
        else if ( view_type.equals( THREE_D ))
          viewer = new ThreeDView( tempDataSet, state );
        else if ( view_type.equals( HKL_SLICE ) )
          viewer = new HKL_SliceView( tempDataSet, state );
        else if ( view_type.equals( SELECTED_GRAPHS ))             // Brent's 
        {       
          VirtualArrayList1D varray = DataSetData.convertToVirtualArray(
	                                                         tempDataSet );
          FunctionViewComponent viewComp = new FunctionViewComponent(varray);
          
          viewer = new DataSetViewerMaker(tempDataSet, state, varray, viewComp);
        }
        else if ( view_type.equals( DIFFERENCE_GRAPH ))
        {
        	VirtualArrayList1D varray = DataSetData.convertToVirtualArray(
                    tempDataSet );
        	FunctionViewComponent viewComp = new DifferenceViewComponent(varray);
        	viewer = new DataSetViewerMaker(tempDataSet, state, varray, viewComp);
        }
        else if ( view_type.equals( TABLE))
           viewer = new TabView( tempDataSet, state ); 
        else if ( view_type.equals( CONTOUR ) )
          viewer = new ContourView( tempDataSet, state ); 
        else
        { 
          if( table_MenuComp == null)
             table_MenuComp= new TableViewMenuComponents();
          viewer = table_MenuComp.getDataSetViewer(view_type,tempDataSet,state);
          if( viewer == null)
          {
             System.out.println("ERROR: Unsupported view type in ViewManager:");
             System.out.println( "      " + view_type );
             System.out.println( "using " + IMAGE + " by default" );
             viewer = new ImageView( tempDataSet, state );
          }
        }
      }catch(Throwable ss){
         System.out.println( "ERROR: Creating View:" +ss);
         
         System.out.println( "      " + view_type );
        
         String[] SS= Command.ScriptUtil.GetExceptionStackInfo(ss, true,1);
         if( SS !=null)if(SS.length>1)
         System.out.println(" line No and class are "+SS[0]);
         System.out.println( "using " + IMAGE + " by default" );
         viewer = new ImageView( tempDataSet, state );
      }

      return viewer;
   }

  /**
   *  Send WINDOW_CLOSING event to shutdown the ViewManager cleanly and
   *  completely.
   */
   public void destroy()
   {
     WindowEvent win_ev = new WindowEvent( view_manager,
                                           WindowEvent.WINDOW_CLOSING );
     view_manager.dispatchEvent( win_ev );
   }


   /**
    *  Update the ViewManager due to a change in the DataSet.  This 
    *  method should be called by the DataSet's notification method, when 
    *  the DataSet is changed.
    *
    *  @param  observed  If all is well, this will be a reference to the 
    *                    DataSet that is being managed.
    *  @param  reason    Object telling the nature of the change and/or a
    *                    command.  The valid reasons are listed in the interface
    *                    IObserver
    *
    *  @see IObserver                     
    */
   public void update( Object observed, Object reason )
   {
     if ( viewer == null )
     {
       if ( debug_view_manager )
         System.out.println("ERROR: ViewManager Previously Destroyed .......");
       return;
     }

     if ( !( reason instanceof String) )   // we only deal with Strings
       return;

     String r_string = (String)reason;
  
     if ( debug_view_manager )
       System.out.println("ViewManager UPDATE : " + r_string );

     if ( observed == dataSet )             // message about original dataSet
     {
       if ( r_string.equals( DESTROY ))
         destroy();
       else if ( r_string.equals( CLOSE_VIEWERS ))
         destroy();

       else if (  r_string.equals( DATA_DELETED )   ||
                  r_string.equals( DATA_REORDERED ) ||
                  r_string.equals( DATA_CHANGED )   ||
                  r_string.equals( HIDDEN_CHANGED )  )
       {
         makeTempDataSet( false );
         viewer.setDataSet( tempDataSet );
         setTitle( dataSet.toString() );
         System.gc();
       }
       else if ( r_string.equals( POINTED_AT_CHANGED )  )
       {
         if ( !link_viewers_button.getState() )      // nothing to do
           return;
                                                     // tell the viewer the new
         int index = dataSet.getPointedAtIndex();    // "pointed at" index if
         if ( index != DataSet.INVALID_INDEX )       //  valid and different 
           if ( new_index[ index ] != DataSet.INVALID_INDEX )
             {
               tempDataSet.setPointedAtIndex( new_index[ index ] );
               float new_x = dataSet.getPointedAtX();
               if ( !Float.isNaN(new_x) )            // valid new_x is clamped
               {                                     // and mapped by the conv.
                 Data d = dataSet.getData_entry( index );          // operator
                 float x_min = d.getX_scale().getStart_x();             
                 float x_max = d.getX_scale().getEnd_x();             
                 if ( new_x < x_min )
                   new_x = x_min;
                 if ( new_x > x_max )
                   new_x = x_max;
                 if ( conversion_operator != null )
                   new_x = conversion_operator.convert_X_Value( new_x, index );
               }
               tempDataSet.setPointedAtX( new_x );
               viewer.redraw( (String)reason );
             }  
       }
       else if ( r_string.equals( GROUPS_CHANGED )    ||
                 r_string.equals( SELECTION_CHANGED ) ||
                 r_string.equals( FIELD_CHANGED )     ||
                 r_string.equals( ATTRIBUTE_CHANGED )  ) 
       {
         viewer.redraw( (String)reason );
       }
       else
         System.out.println("Message " + reason + " not handled for dataSet "+
                            "in ViewManager.update()");
     }     

     else if ( observed == tempDataSet )    // message about temporary dataSet
     {                                      // translate to original dataSet
                                            // and notify it's observers
       if ( r_string.equals( POINTED_AT_CHANGED )) 
       {
         if ( !link_viewers_button.getState() )      // call redraw for tempDS 
         {
           viewer.redraw( r_string );
           return;
         }

         int i = tempDataSet.getPointedAtIndex();
         if ( i != DataSet.INVALID_INDEX ) 
           dataSet.setPointedAtIndex( original_index[i] ); 

         float new_x = tempDataSet.getPointedAtX();
         float orig_x = new_x;
         if ( conversion_operator != null && !Float.isNaN(new_x) )
           orig_x = solve( new_x );
         dataSet.setPointedAtX( orig_x ); 
         dataSet.notifyIObservers( POINTED_AT_CHANGED );
       }

       else if ( r_string.equals( SELECTION_CHANGED )) 
                                                  // synchonize selections and
       {                                          // notify dataSet's observers 
         for ( int i = 0; i < tempDataSet.getNum_entries(); i++ )
           dataSet.setSelectFlag(original_index[i], 
                                 tempDataSet.getData_entry(i) ); 

         dataSet.notifyIObservers( reason );
       }

       else if ( r_string.equals( XScaleChooserUI.N_STEPS_CHANGED ) ||
                 r_string.equals( XScaleChooserUI.X_RANGE_CHANGED )  )
       {
         if ( conversion_operator == null )
           viewer.redraw( (String)reason );
         else
         {
           makeTempDataSet( false );
           viewer.setDataSet( tempDataSet );
         } 
       }

       else
         System.out.println("Message "+reason+" not handled for tempDataSet "+
                            "in ViewManager.update()");
     }


     else
     {
       System.out.println("ERROR: bad DataSet in ViewManager.update()" );
     }
   }

  /**
   *  Trace the finalization of objects
   */
/*
  protected void finalize() throws IOException
  {
    System.out.println( "finalize ViewManager" );
  }
*/

/* --------------------------------------------------------------------------
 *
 *  Private Methods
 */

  /**
   *  Destroy the current ViewManager and remove it from the list of
   *  observers of the current DataSet when window closing event is received.
   */
   private void free_resources()
   {
     dataSet.deleteIObserver( this );
     tempDataSet.deleteIObserver( this );
     viewer = null;
     dispose();
     System.gc();
   }


   private void makeTempDataSet( boolean use_default_conversion_range )
   {
                                                // degnerate case, use original
     if ( dataSet == null || dataSet.getNum_entries() <= 0 )       
     {
       tempDataSet = dataSet;
       return;
     }                                          // otherwise, fabricate a new
                                                // data set and original_index
                                                // list

                                           // first copy the non-hidden spectra
     tempDataSet    = dataSet.empty_clone();
     original_index = new int[ dataSet.getNum_entries() ];
     new_index      = new int[ dataSet.getNum_entries() ];
     int  num_new   = 0;
     Data d;
     for ( int i = 0; i < dataSet.getNum_entries(); i++ )
     { 
       d = dataSet.getData_entry( i );
       if ( !d.isHidden() || show_all_button.getState() )
       {
         tempDataSet.addData_entry( d );
         original_index[ num_new ] = i;
         new_index[i] = num_new;
         num_new++; 
       }
       else
         new_index[i] = DataSet.INVALID_INDEX; // since not in tempDataSet
     }
                                               // do the conversion and record
     if ( conversion_operator != null )        // the indices, unchanged 
     {
       DataSetOperator op = tempDataSet.getOperator( CurrentConversionName() ); 
       
       UniformXScale x_scale;
       XScale temp_scale = viewer.getXConversionScale();
       if ( temp_scale == null )
         x_scale = null;
       else if ( temp_scale instanceof UniformXScale )
         x_scale = (UniformXScale)temp_scale;
       else
         x_scale = new UniformXScale( temp_scale.getStart_x(),
                                      temp_scale.getEnd_x(),
                                      temp_scale.getNum_x() );
         
       if ( x_scale == null || use_default_conversion_range ) 
       {
         op.setDefaultParameters();
         IParameter p = op.getParameter(2);                // 0 means use the
         if ( p.getName().equals( Parameter.NUM_BINS ))   // number of bins
           p.setValue( new Integer( 0 ) );                // in the DataSet
       }
       else
       {                                           // try to set the parameters
         IParameter p = op.getParameter(2);
         if ( !p.getName().equals( Parameter.NUM_BINS ))
           op.setDefaultParameters();              // fall back to defaults
         else
         {
           p.setValue( new Integer( x_scale.getNum_x() ) );

           p = op.getParameter(0);
           p.setValue( new Float( x_scale.getStart_x() ) );

           p = op.getParameter(1);
           p.setValue( new Float( x_scale.getEnd_x() ) );
         }
       }  

       Object result = op.getResult();
       if ( result instanceof DataSet )
       {
         tempDataSet = (DataSet)op.getResult();
         if ( tempDataSet.getNum_entries() == num_new )
                                            // we didn't lose Data blocks, so
         {
           for ( int i = 0; i < num_new; i++ ) // preserve the selection flags
           {
             d = dataSet.getData_entry( original_index[i] );
             tempDataSet.setSelectFlag( i, d );
           }
                                                // preserve the pointed at index
                                                // if possible
           int k = dataSet.getPointedAtIndex(); 
           if ( k != DataSet.INVALID_INDEX )
             if ( new_index[k] != DataSet.INVALID_INDEX )
               tempDataSet.setPointedAtIndex( new_index[k] );
         }
       }
       else
         tempDataSet = dataSet.empty_clone();
    }

     tempDataSet.addIObserver( this );
   }


private void BuildFileMenu()
{
                                                // set up file menu items
  FileMenuHandler file_menu_handler = new FileMenuHandler();
  JMenu file_menu = viewer.getMenuBar().getMenu(DataSetViewer.FILE_MENU_ID);

  JMenuItem button = new JMenuItem( SAVE_NEW_DATA_SET );
  button.addActionListener( file_menu_handler );
  file_menu.add( button );

  boolean hasSaveImage = false;
  boolean hasPrint = false;
  for( int i=0; (i< file_menu.getItemCount()); i++)
  {
     String text = file_menu.getItem( i ).getText();
     if( text.equals(SAVE_IMAGE))
        hasSaveImage = true;
     else if( text.equals(PRINT))
        hasPrint = true;
  }
  
  if( !hasSaveImage)
     file_menu.add( gov.anl.ipns.Util.Sys.SaveImageActionListener.
             getActiveMenuItem(SAVE_IMAGE, viewer)); 

  if( !hasPrint ) 
     file_menu.add( gov.anl.ipns.Util.Sys.PrintComponentActionListener.
          getActiveMenuItem(PRINT, viewer));
  
  JMenuItem SaveState= new JMenuItem( "Save Object State");
  JMenuItem LoadState = new JMenuItem("Load Object State");
  file_menu.add( SaveState );
  file_menu.add( LoadState);
  SaveState.addActionListener( file_menu_handler);
  LoadState.addActionListener( file_menu_handler);
  button = new JMenuItem( CLOSE_LABEL );
  button.addActionListener( file_menu_handler );
  file_menu.add( button );
}


private void BuildEditMenu()
{
                                                // set up edit menu items
  EditMenuHandler edit_menu_handler = new EditMenuHandler();
  JMenu edit_menu = viewer.getMenuBar().getMenu(DataSetViewer.EDIT_MENU_ID);

  JMenu group_menu = new JMenu( SUM_MENU );            // group menu
  edit_menu.add( group_menu );

  JMenuItem button = new JMenuItem( SUM_SELECTED );
  button.addActionListener( edit_menu_handler );
  group_menu.add( button );

  button = new JMenuItem( SUM_UNSELECTED );
  button.addActionListener( edit_menu_handler );
  group_menu.add( button );

  JMenu delete_menu = new JMenu( DELETE_MENU );            // delete menu
  edit_menu.add( delete_menu );

  button = new JMenuItem( DELETE_SELECTED );
  button.addActionListener( edit_menu_handler );
  delete_menu.add( button );

  button = new JMenuItem( DELETE_UNSELECTED );
  button.addActionListener( edit_menu_handler );
  delete_menu.add( button );

  JMenu clear_menu = new JMenu( CLEAR_MENU );            // clear menu
  edit_menu.add( clear_menu );

  button = new JMenuItem( CLEAR_SELECTED );
  button.addActionListener( edit_menu_handler );
  clear_menu.add( button );

                                              // Add sort options
  if ( dataSet.getNum_entries() > 1 )
  {
    JMenu sort_menu = new JMenu( "Sort by..." );
    edit_menu.add( sort_menu );
    AttributeList attr_list = dataSet.getData_entry(0).getAttributeList();
    for ( int i = 0; i < attr_list.getNum_attributes(); i++ )
    {
      Attribute attr = attr_list.getAttribute(i);
      button = new JMenuItem( attr.getName() );
      button.addActionListener( edit_menu_handler );
      sort_menu.add( button );
    }
  }
}

private void BuildViewMenu()
{                                                   // set up view menu items
  ViewMenuHandler view_menu_handler = new ViewMenuHandler();
  JMenu view_menu = viewer.getMenuBar().getMenu(DataSetViewer.VIEW_MENU_ID);

  JMenuItem button = new JMenuItem( ADDITIONAL_VIEW );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );

  button = new JMenuItem( IMAGE );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );

  button = new JMenuItem( THREE_D );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );

  button = new JMenuItem( HKL_SLICE );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );
  
  button = new JMenuItem( CONTOUR );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );

  button = new JMenuItem( SCROLLED_GRAPHS );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );

  button = new JMenuItem( SELECTED_GRAPHS );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );
  
  button = new JMenuItem( DIFFERENCE_GRAPH );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );

  JMenu Tables = new JMenu( "Selected Table View");
  view_menu.add( Tables);
  
  BuildTableMenu( Tables);

  button = new JMenuItem( TABLE );
  button.addActionListener( view_menu_handler );
  view_menu.add( button );
}

 public void BuildTableMenu( JMenu Tables )
 { 
    ViewMenuHandler view_menu_handler = new ViewMenuHandler();
    if( table_MenuComp == null)
      table_MenuComp = new TableViewMenuComponents();
   
    table_MenuComp.addMenuItems( Tables , view_menu_handler);
    
   /* Tables.addSeparator();  
    JMenuItem button;
    button = new JMenuItem( "Advanced Table");
    button.addActionListener( view_menu_handler );
    Tables.add( button );
   */
 }

/*
 * Build the menu of conversion options and turn on the radio button for the 
 * currently active conversion operator.
 */
private void BuildConversionsMenu()
{ 
  JMenu conversion_menu = new JMenu("Axis Conversions...");
  ConversionMenuHandler conversion_menu_handler = new ConversionMenuHandler();
  ButtonGroup group = new ButtonGroup();

  JRadioButtonMenuItem button = new JRadioButtonMenuItem(NO_CONVERSION_OP);
  if ( CurrentConversionName().equals( NO_CONVERSION_OP ))
    button.setSelected(true);
  button.addActionListener( conversion_menu_handler );
  conversion_menu.add( button );
  group.add( button );

  DataSetOperator op;

  int n_ops         = dataSet.getNum_operators();
  for ( int i = 0; i < n_ops; i++ )
  {
    op = dataSet.getOperator(i);
    if ( op instanceof XAxisConversionOp )
    {
      button = new JRadioButtonMenuItem( op.getTitle() );
      button.addActionListener( conversion_menu_handler );
      if ( CurrentConversionName().equals( op.getTitle() ))
        button.setSelected(true);

      conversion_menu.add( button );     
      group.add( button );
    }
  } 

  JMenu view_menu = viewer.getMenuBar().getMenu(DataSetViewer.VIEW_MENU_ID);
  view_menu.add( conversion_menu );         
}


private void BuildOptionMenu()
{
                                                // set up option menu items
  OptionMenuHandler option_menu_handler = new OptionMenuHandler();
  JMenu option_menu = viewer.getMenuBar().getMenu(DataSetViewer.OPTION_MENU_ID);

  link_viewers_button = new JCheckBoxMenuItem(LINK_VIEWS);
  link_viewers_button.addActionListener( option_menu_handler );
  link_viewers_button.setState( true );
  ShowPointedAtTable = new JCheckBoxMenuItem( POINTEDAT_TABLE);
  ShowPointedAtTable.addActionListener( option_menu_handler );
  ShowPointedAtTable.setState(false);
 	 
 
  option_menu.add( link_viewers_button );
  option_menu.add(  ShowPointedAtTable );
/*
  show_all_button = new JCheckBoxMenuItem(SHOW_ALL);
  show_all_button.addActionListener( option_menu_handler );
  show_all_button.setState( false );
  option_menu.add( show_all_button );
*/
}

private void BuildHelpMenu( String viewType){
  String view= viewType.replace(' ','_').toLowerCase();
  String F = System.getProperty("ISAW_HOME");
  if( viewType.startsWith("3D"))
      view ="3D"+view.substring(2);
  if( F== null) return;
  if( !F.endsWith(File.separator))
      F+=File.separator;
  F+="IsawHelp"+File.separator+"Viewers"+File.separator+view+".html";
  if( !(new File(F)).exists())
     if( ";grx_y;parallel_y(x);instrument_table;".indexOf(";"+view+";") < 0)
     return;
  JMenu HelpMenu = new JMenu("Help");
  JMenuItem About = new JMenuItem("Viewer");
  About.addActionListener( new HelpActionListener(view));
  HelpMenu.add( About );
  viewer.getMenuBar().add( HelpMenu);
  
  
}
private String CurrentConversionName()     // get current conversion name
{
  String name;     
  if ( conversion_operator == null )
    name = NO_CONVERSION_OP;
  else
    name = conversion_operator.getTitle();
 
  return name;
}


private float solve( float new_x ) // find what x in the original DataSet maps
{                                  // maps to new_x in converted tempDataSet
  if ( conversion_operator == null )
    return new_x;

  int index = dataSet.getPointedAtIndex();
  XScale x_scale = dataSet.getData_entry(index).getX_scale();
  float a = x_scale.getStart_x();
  float b = x_scale.getEnd_x();
  float f_a = conversion_operator.convert_X_Value( a, index );
  float f_b = conversion_operator.convert_X_Value( b, index );

  float f_min = Math.min( f_a, f_b );
  float f_max = Math.max( f_a, f_b );
  if ( new_x <= f_min )                // clamp the values at the ends of the
  {                                    // Data blocks.  There are two cases,
    if ( f_a <= f_b )                  // since the conversion may reverse the
      return a;                        // order.
    else
      return b;
  }

  if ( new_x >= f_max )
  {
    if ( f_a <= f_b )
      return b;
    else
      return a;
  }

  ConversionFunction f =
                  new ConversionFunction(index, conversion_operator, new_x );

  return (float)NumericalAnalysis.BisectionMethod( f, a, b, 20 );
}


  public class ConversionFunction implements IOneVariableFunction
  {
    private  int               index;
    private  XAxisConversionOp op;
    private  float             new_x;

    public  ConversionFunction( int               index, 
                                XAxisConversionOp op, 
                                float             new_x )
    {
      this.index = index;
      this.op    = op;
      this.new_x = new_x;
    }

    public double getValue( double x )
    {
      return ( new_x - op.convert_X_Value( (float)x, index ) );
    }
  }


/* -------------------------------------------------------------------------
 *
 *   Event handling objects for the Frame and menu bar
 */

  private class FileMenuHandler implements ActionListener,
                                           Serializable
  {
    public void actionPerformed( ActionEvent e )
    {
      String action = e.getActionCommand();

      if ( action.equals( CLOSE_LABEL ))
        destroy();
      else if ( action.equals( SAVE_NEW_DATA_SET ))
      {
        DataSet new_ds = (DataSet)tempDataSet.clone();
        dataSet.notifyIObservers( new_ds );
      }else if( action.equals("Save Object State")){
        ObjectState st= viewer.getObjectState( false);
        Ostate.reset( viewType, st);   
        Ostate.openFileChooser(true);
      }else if( action.equals("Load Object State")){
        
         Ostate.openFileChooser(false);
         setObjectState(Ostate);
      }
      
      else
        System.out.println( action );
    }
  }


  private class EditMenuHandler implements ActionListener,
                                           Serializable
  {
    public void actionPerformed( ActionEvent e )
    {
      String action = e.getActionCommand();

      if ( action.equals( SUM_SELECTED ))
      {
        DataSetOperator op = new SumCurrentlySelected( dataSet, true, false );
        op.getResult();
        dataSet.notifyIObservers( IObserver.DATA_DELETED );
      }
      else if ( action.equals( SUM_UNSELECTED ))
      {
        DataSetOperator op = new SumCurrentlySelected( dataSet, false, false );
        op.getResult();
        dataSet.notifyIObservers( IObserver.DATA_DELETED );
        dataSet.notifyIObservers( IObserver.SELECTION_CHANGED );
      }

/* //  delete by hiding...
      else if ( action.equals( DELETE_SELECTED ))
      {
         dataSet.hideSelected( true );
         dataSet.notifyIObservers( IObserver.HIDDEN_CHANGED );
         if ( dataSet.clearSelections() )
           dataSet.notifyIObservers( IObserver.SELECTION_CHANGED );
      }
      else if ( action.equals( DELETE_UNSELECTED ))
      {
         dataSet.hideSelected( false );
         dataSet.notifyIObservers( IObserver.HIDDEN_CHANGED );
         if ( dataSet.clearSelections() )
           dataSet.notifyIObservers( IObserver.SELECTION_CHANGED );
      }
*/
   // delete for real
      else if ( action.equals( DELETE_SELECTED ))
      {
        DataSetOperator op = new DeleteCurrentlySelected( 
                                              dataSet, true, false );
        op.getResult();
        dataSet.notifyIObservers( IObserver.DATA_DELETED );
      }
      else if ( action.equals( DELETE_UNSELECTED ))
      {
        DataSetOperator op = new DeleteCurrentlySelected( 
                                              dataSet, false, false );
        op.getResult();
        dataSet.notifyIObservers( IObserver.DATA_DELETED );
        dataSet.notifyIObservers( IObserver.SELECTION_CHANGED );
      }
      else if ( action.equals( CLEAR_SELECTED ))
      {
        if ( tempDataSet.clearSelections() || dataSet.clearSelections() )
          dataSet.notifyIObservers( IObserver.SELECTION_CHANGED );
      } 
      else 
      {
        DataSetOperator op = new DataSetSort( dataSet, action, true, false );
        op.getResult();
      }
    }
  }

  private void HandlePointedAtView(){
	 
	  if( this.ShowPointedAtTable.isSelected()){
	       PointedAtView = new QuickTableViewer( this, tempDataSet);
	       PointedAtView.addWindowListener( new MyWindowListener());   //For when it is closed
	  }else if(PointedAtView != null){
		  PointedAtView.destroy();
		  PointedAtView = null;
		  
	  }
  }
  private class OptionMenuHandler implements ActionListener,
                                             Serializable
  {
    public void actionPerformed( ActionEvent e )
    {
      String action = e.getActionCommand();
      if ( action.equals( SHOW_ALL ) && dataSet.getNumHidden() > 0 )
      {
         makeTempDataSet( false ); 
         viewer.setDataSet( tempDataSet );
      }else if( action.equals( ViewManager.POINTEDAT_TABLE)){
    	 HandlePointedAtView();
      }
    }
  }


  private class ViewMenuHandler implements ActionListener,
                                           Serializable
  {  
     boolean errors = false, 
              index = false;
    
    public void actionPerformed( ActionEvent e )
    {
      String action = e.getActionCommand();
      if ( action.equals( ADDITIONAL_VIEW ) )
        new ViewManager( dataSet, viewType );
      else
        setView( action ); 
    }
  }


  private class ConversionMenuHandler implements ActionListener,
                                                 Serializable
  {
    public void actionPerformed( ActionEvent e )
    {
      String action  = e.getActionCommand();
                                                 // if the request is differnt
                                                 // do the new conversion
      if ( !action.equals( CurrentConversionName() ) ) 
      {
        JRadioButtonMenuItem button = (JRadioButtonMenuItem)e.getSource();
        button.setSelected(true);
        conversion_operator = (XAxisConversionOp)dataSet.getOperator( action ); 
        makeTempDataSet( true );
        viewer.setDataSet( tempDataSet );
        PointedAtView.destroy();
        PointedAtView = null;
        HandlePointedAtView();
      }
    }
  }


 public void finish(){

    try{
      finalize();
    }catch( Throwable ss){
      System.out.println(" finalize error "+ss);
    }
 }
 
 /**
  * Sets the ObjectState and updates the ObjectState for the current 
  * DataSetViewer to the corresponding ObjectState
  * 
  * @param ObjState  The new ObjectState
  */
 public void setObjectState( ObjectState ObjState ){
   
   this.Ostate = ObjState;
   if(Ostate == null)
       Ostate = new ObjectState();
   ObjectState st =(ObjectState) Ostate.get(viewType);
   if( st !=null)
      viewer.setObjectState( st);
 }

 
 public ObjectState getObjectState( boolean is_default){
   ObjectState st = viewer.getObjectState( false );
   if(  st != null)
       Ostate.reset( viewType, st);
   return Ostate;
   
 }

 
 class HelpActionListener implements ActionListener{
    String viewName;
    public HelpActionListener( String viewName){
      this.viewName = viewName;
      if(";grx_y;parallel_y(x);instrument_table;".indexOf(";"+viewName+";")>=0)
        this.viewName="selected_table_view"; 
    }
  
   public void actionPerformed(ActionEvent e){
    
      /*FinishJFrame jf = new FinishJFrame( viewName+" Viewer");
      jf.setSize( 700,700);
     
      String url=DataSetTools.util.FilenameUtil.helpDir(
                                             "Viewers/"+viewName+".html");
    
      if( url==null){
      
         JOptionPane.showMessageDialog(null,
                           "Cannot find this viewer's documentation");
         ((JMenu)e.getSource()).setSelected(false);
         return;
      }
      try{
         JEditorPane edPane= new JEditorPane(url);
         edPane.setEditable( false );
         jf.getContentPane().add(new JScrollPane(edPane));
         
      }catch( Exception s){
        JOptionPane.showMessageDialog(null,
                         "Cannot show this viewer's documentation:"+s);
        ((JMenu)e.getSource()).setSelected(false);
        return;
      }
     
     WindowShower.show( jf);
     */
     
      String dir = System.getProperty("Help_Directory");
      if( dir == null) 
         return;
      dir = dir.replace('\\','/');
      if( !dir.endsWith("/")) dir +='/';
      dir += "Viewers/"+viewName+".html";
      File F = new File(dir);
      if( !F.exists()) return;
      
      String url ;
      try{
          url=F.toURL().toExternalForm();
      }catch(Exception ss){
         return;
      }
     
     IsawGUI.Browser x=(new IsawGUI.Browser( url ));
    }
   
 }
 class MyWindowListener extends WindowAdapter{
	public void windowClosed(WindowEvent e) {
		if( e.getSource().equals( PointedAtView)){
			ShowPointedAtTable.setState( false );
			
		}
	}
 }
}
