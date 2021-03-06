/*
 * File: DataSetViewerMaker1.java
 *
 * Copyright (C) 2003, Ruth Mikkelson 
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
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
 *  Revision 1.20  2005/05/25 18:39:18  dennis
 *  Removed unused imports.
 *
 *  Revision 1.19  2005/05/25 18:01:16  dennis
 *  Replaced direct call to .show() method for window,
 *  since .show() is deprecated in java 1.5.
 *  Now calls WindowShower.show() to create a runnable
 *  that is run from the Swing thread and sets the
 *  visibility of the window true.
 *
 *  Revision 1.18  2005/04/10 18:50:19  rmikk
 *  Implement IPreserveState and sends ObjectState info to the
 *   DataBlockSelector and LargeJTableViewComponent
 *
 *  Revision 1.17  2005/01/10 15:55:08  dennis
 *  Removed empty statement.
 *
 *  Revision 1.16  2004/06/25 14:47:49  rmikk
 *  Interpreted the value of ControlPanelWidth as a percent(not a decimal)
 *
 *  Revision 1.15  2004/06/24 19:11:39  rmikk
 *  Incorporated the state variable ControlPanelWidth(%)
 *
 *  Revision 1.14  2004/05/26 16:55:03  rmikk
 *  Added one input verification to a method
 *
 *  Revision 1.13  2004/05/17 13:54:40  rmikk
 *  Now deals with ImageViewComponents, except for pointed at and selected
 *
 *  Revision 1.12  2004/05/14 15:06:08  rmikk
 *  Removed unused variables
 *
 *  Revision 1.11  2004/05/06 17:32:05  rmikk
 *  Eliminated bad log messages.
 *  Set the time in the ArrayMaker when the pointed at message occurs
 *
 *  Revision 1.10  2004/03/15 19:33:58  dennis
 *  Removed unused imports after factoring out view components,
 *  math and utilities.
 *
 *  Revision 1.9  2004/03/15 03:28:58  dennis
 *  Moved view components, math and utils to new source tree
 *  gov.anl.ipns.
 *
 *
 *  Revision 1.8  2004/03/10 23:40:57  millermi
 *  - Changed IViewComponent interface, no longer
 *    distinguish between private and shared controls/
 *    menu items.
 *  - Combined private and shared controls/menu items.
 *
 *  Revision 1.7  2004/01/24 22:02:38  bouzekc
 *  Removed unused imports.
 *
 *  Revision 1.6  2003/12/11 22:09:20  rmikk
 *  Added a kill command to remove orphaned windows
 *
 *  Revision 1.5  2003/12/11 19:28:19  rmikk
 *  Made a public field, ImagePortion, for the portion of the
 *    split pane given to the View.
 *
 *  Revision 1.4  2003/12/04 20:45:29  rmikk
 *  Added some input checking
 *
 *  Revision 1.3  2003/11/06 21:27:26  rmikk
 *  Now can handle selected Regions for specifying
 *    data sets and time ranges to be selected
 *
 *  Revision 1.2  2003/11/06 19:56:18  rmikk
 *  Changed the proportion on the split pane so the control
 *    panel takes less area
 *
 *  Revision 1.1  2003/10/27 15:09:43  rmikk
 *
 *  Initial Checkin
 *
 *
 */ 

package DataSetTools.viewer;

import gov.anl.ipns.Util.Messaging.*;
import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Components.TwoD.*;
import gov.anl.ipns.ViewTools.Components.Menu.*;
import gov.anl.ipns.ViewTools.Components.ViewControls.ViewControl;
import gov.anl.ipns.ViewTools.UI.*;

import javax.swing.*;
import DataSetTools.dataset.*;
import DataSetTools.components.View.*;
import java.awt.event.*;
import java.awt.*;
import DataSetTools.viewer.Table.*;
import Command.*;
import DataSetTools.components.ui.*;
import DataSetTools.util.*;
import java.util.*;
import javax.swing.event.*;

/**
*    This class creates DataSetViewers given an IViewComponent and an
*    IVirtualArray. Although, it can be used standalone, it is best to
*    subclass the class so that the constructor requires only a DataSet and
*    a ViewerState.  In this form, it can be used by the ViewManager.
*    
*    This class just places the IVirtualComponent in the left part of a SplitPane,
*    get the Controls and adds then to the Right part of the split pane, and maintains
*    the DataSetXConversionsTable.
*/
public class DataSetViewerMaker1  extends DataSetViewer   {
   DataSet ds;
   ViewerState state;
   IArrayMaker_DataSet viewArray;
   IViewComponent2D viewComp;
   DataSetData update_array;
   DataSetXConversionsTable  Conversions;
   public float ImagePortion = .8f;
   /** 
   *   Constructor
   *   @param  ds  the DataSet that is to be viewed
   *   @param  state  The viewer State
   *   @param viewArray  the IVirtualArray(produces array values when needed)
   *   @param viewComp   the IVirtualComponent that displays the DataSet in the form
   *                     supplied by a compatible IVirtualArray
   */

  public DataSetViewerMaker1( DataSet             ds, 
                              ViewerState         state, 
                              IArrayMaker_DataSet       viewArray, 
                              IViewComponent2D      viewComp )
    {
     super( ds, state);
     this.viewArray = viewArray;
     this.viewComp = viewComp;
     this.ds = ds;
     this.state = state;
     /*if( !(viewComp instanceof DataSetViewerMethods)){
       SharedData.addmsg("The view component is missing the DataSetViewerMethods");
       return;
     }*/
        
     try{
        viewComp.dataChanged( (IVirtualArray2D)viewArray.getArray());
     }catch(Exception ss){
          SharedData.addmsg(ss.toString());
          return;
     }
     JPanel East = new JPanel( new GridLayout( 1,1));
     
     BoxLayout blayout = new BoxLayout( East,BoxLayout.Y_AXIS);
     
     East.setLayout( blayout);
     JComponent[] ArrayScontrols =viewArray.getSharedControls();
     if( ArrayScontrols != null)
       for( int i=0; i< ArrayScontrols.length; i++)
         East.add( ArrayScontrols[i]);

     JComponent[] Arraycontrols =viewArray.getPrivateControls();
     if( Arraycontrols != null)
       for( int i=0; i< Arraycontrols.length; i++)
         East.add( Arraycontrols[i]);
      
     ViewControl[] Compcontrols = viewComp.getControls();
     if( Compcontrols != null)
       for( int i=0; i< Compcontrols.length; i++)
         East.add( Compcontrols[i]);   
     
     ViewMenuItem[] MenItem1 = viewComp.getMenuItems();
     ViewMenuItem[] MenItem2 = viewArray.getSharedMenuItems();
     
     PrintComponentActionListener.setUpMenuItem( getMenuBar(), this);
     String[]paths = null;
     if( viewComp instanceof DataSetViewerMethods)
         paths = ((DataSetViewerMethods)viewComp).getSharedMenuItemPath();
     SetUpMenuBar( getMenuBar(), MenItem1, paths );
 
     SetUpMenuBar( getMenuBar(), MenItem2, viewArray.getSharedMenuItemPath());

     Conversions = new DataSetXConversionsTable( ds);
     East.add( Conversions.getTable());
     East.add( Box.createVerticalGlue()); 
     
     viewArray.addActionListener( new ArrayActionListener());
     if(viewComp instanceof DataSetViewerMethods)
       viewComp.addActionListener( new CompActionListener());
     setLayout( new GridLayout( 1,1));
     JComponent Controls = (JComponent)East;
    if( state != null){
       float f = state.get_float("ControlPanelWidth(%)");
       if( !Float.isNaN(f))
        ImagePortion = 1-f/100f;
     }
    
     if( ArrayScontrols.length+Compcontrols.length > 7){
     
          Controls = new JScrollPane( East);
          ImagePortion =ImagePortion*.85f;
     }
     add( new SplitPaneWithState(JSplitPane.HORIZONTAL_SPLIT,
                  viewComp.getDisplayPanel(), Controls, ImagePortion));

     invalidate();
     addAncestorListener( new myAncestorListener());
    }

  /**
  *    Causes everything to be repainted
  */
  public void Repaint()
    {
     repaint();
    }

  private void SetUpMenuBar( JMenuBar bar, ViewMenuItem[] items, String[] paths){
     if( items == null)
       return;
     if( bar == null)
       return;
     for( int i = 0; i< items.length; i++){
        String path=null;
        if( paths != null)
           path = paths[i];
        else
           path = items[i].getPath();
        if( path != null)
          if( path.length() > 1){
            int p1= 0;
            int p = path.indexOf('.');
            if( p < 0) 
               p = path.length();
            JMenu jm = getSubMenu( bar, path.substring( p1,p));
            p1 = p+1;
            while( p1 < path.length()){
               p = path.indexOf('.',p1);
               if( p < 0) 
                 p = path.length();
               JMenu jm1 = getSubMenu( jm, path.substring(p1,p));
               p1 = p+1;
               jm = jm1;
             }
            jm.add( items[i].getItem());
            
          }
        }//for


     }
   private JMenu getSubMenu( JMenuBar jm, String path){
      if( jm == null)
         return null;
      if( path == null)
         return null;
      for( int i = 0; i < jm.getMenuCount(); i++)
        if( jm.getMenu(i).getText().equals( path))
             return jm.getMenu(i);
      JMenu Men = new JMenu(path);
      jm.add( Men);
      return Men;
   }

  
   private JMenu getSubMenu( JMenu jm, String path){
      
      if( jm == null)
         return null;
      if( path == null)
         return null;
      for( int i = 0; i < jm.getItemCount(); i++){
        if( jm.getItem(i) != null)
          if( jm.getItem(i).getText().equals(path))
            if( jm.getItem(i) instanceof JMenu)
               return (JMenu)jm.getItem(i);
      }
      JMenu Men = new JMenu(path);
      jm.add( Men);
      return Men;

   }
 
  Vector InternalPointedAts = new Vector();

  /**
  *   Causes the display to be redrawn and also the data changed to reflect a 
  *   POINTED_AT_CHANGED event
  */
  public void redraw( String reason)
    {
     if ( !validDataSet() )
        return;

     if( reason.equals( "SELECTION CHANGED" ) )
       { 
        //update_array = new DataSetData( getDataSet() );
        // viewComp.dataChanged(update_array);
        // viewComp.getGraphJPanel().repaint();
       }
     else if( reason.equals( "POINTED AT CHANGED" )) 
       { 
        int Group = ds.getPointedAtIndex();
        float time = ds.getPointedAtX();
        if( !eliminate( Group,time, InternalPointedAts)){
           if(!Float.isNaN(time))
               viewArray.setTime( time);
           ((DataSetViewerMethods)viewComp).setPointedAt( 
                      viewArray.getSelectedData( Group,time));
        }
        Conversions.showConversions( time, Group);                          
            
       }
    }


  // To make sure internal events are not dealt with like external events,
  // all external notifications are saved( values are saved). If an event 
  // with the same values return, this method returns false and eliminates 
  // those values from the saved set.
  private  boolean eliminate( int Group, float time, Vector V)
    {
     for( int i = 0; i < V.size(); i++)
       {
        Vector Pt = (Vector)(V.elementAt(i));
        if( Pt.firstElement() instanceof Integer)
           if( ((Integer)(Pt.firstElement())).intValue() == Group)
              if( Pt.lastElement() instanceof Float)
                 if( ((Float)(Pt.lastElement())).floatValue() == time)
                   {
                    V.remove(i);
                    return true;
                   }
                
       }
     return false;

    }


 
  // Listens for action events coming from the IVirtualArray
  class ArrayActionListener  implements ActionListener
    {
     public void actionPerformed( ActionEvent evt)
       {
       // if( viewComp instanceof ImageViewComponent)
           viewComp.dataChanged( (IVirtualArray2D)(viewArray.getArray()) );
       // else
		//    viewComp.dataChanged();
        Repaint();
       }
    }

  //Listens for events coming from the IViewComponent
  class CompActionListener implements ActionListener
    {
     public void actionPerformed( ActionEvent evt)
       {
        if( evt.getActionCommand().equals(IViewComponent.POINTED_AT_CHANGED) )
          {
           SelectedData2D X = (SelectedData2D)(((DataSetViewerMethods)viewComp).
                                IgetPointedAt());
           int Group = viewArray.getGroupIndex( X);

           float Time = viewArray.getTime( X);
           if( Group < 0) return;
           if( Float.isNaN(Time)) return;
           Conversions.showConversions( Time, Group);
           Vector V = new Vector();
           V.addElement( new Integer( Group));
           V.addElement( new Float( Time));
           InternalPointedAts.addElement( V);
           ds.setPointedAtX( Time);
           ds.setPointedAtIndex( Group);
           ds.notifyIObservers( IObserver.POINTED_AT_CHANGED);
          }
         else if( evt.getActionCommand().equals(IViewComponent.SELECTED_CHANGED)){
             ISelectedRegion selRegion = ((DataSetViewerMethods)viewComp).IgetSelectedRegion();
             viewArray.SelectRegion( selRegion);
         }
         
       }
    }
  
  class myAncestorListener implements AncestorListener{
     public void ancestorAdded(AncestorEvent event){
     }
    
     public void ancestorRemoved(AncestorEvent event){
       
       viewComp.kill();
       viewArray.kill();
     }
     public void ancestorMoved(AncestorEvent event){

     }
     
  }


  public static void main( String args[])
    {
     DataSet[] DSS = null;
     try
       {
        DSS = ScriptUtil.load( "C:/Isaw/SampleRuns/SCD06496.RUN");
       }
     catch( Exception ss)
       {
        System.exit(0);
       }
     DataSet DS = DSS[DSS.length-1];
     JFrame jf = new JFrame("Test");
     jf.setSize( 500,500);
     DataSetViewer dsv = new DataSetViewerMaker1(DS,null,
                       new RowColTimeVirtualArray( DS,1000f,false,false,null),
                       new LargeJTableViewComponent( null, null));
     jf.getContentPane().add(dsv );

     jf.setJMenuBar( dsv.getMenuBar());
   
     WindowShower.show(jf);
     jf.invalidate();

    }
  
  //-------------------- IPreserveState Methods & Variables-----------------
   ObjectState Ostate= null;
   
   public void setObjectState( ObjectState new_state){
      Ostate = new_state;
      if( viewArray instanceof DataBlockSelector)
          ((DataBlockSelector)viewArray).setObjectState( 
                         (ObjectState)Ostate.get("DataBlockSelector"));
      if(viewComp instanceof LargeJTableViewComponent)
          ((LargeJTableViewComponent)viewComp).setObjectState( 
                  (ObjectState)Ostate.get("JTable"));
      
   }
   public ObjectState getObjectState( boolean is_default){
       ObjectState state = new ObjectState();

       if( viewArray instanceof DataBlockSelector)
           state.insert("DataBlockSelector",
                   ((DataBlockSelector)viewArray).getObjectState( is_default));
                          
       if(viewComp instanceof LargeJTableViewComponent)
           state.insert("JTable",((LargeJTableViewComponent)viewComp).getObjectState 
                   (is_default));
       return state;
       
   }
   
  }//DataSetViewerMaker1
