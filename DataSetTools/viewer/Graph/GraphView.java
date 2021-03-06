/**
 * File:  GraphView.java  
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
 *  Revision 1.27  2007/07/13 16:52:45  dennis
 *  Added getDisplayComponent() method to return just the data display
 *  panel without any controls.
 *
 *  Revision 1.26  2004/03/15 06:10:55  dennis
 *  Removed unused import statements.
 *
 *  Revision 1.25  2004/03/15 03:29:00  dennis
 *  Moved view components, math and utils to new source tree
 *  gov.anl.ipns.*
 *
 *  Revision 1.24  2002/11/27 23:24:43  pfpeterson
 *  standardized header
 *
 *  Revision 1.23  2002/10/04 14:42:26  dennis
 *  Now handles "Pointed At" messages properly.
 *
 *  Revision 1.22  2002/07/12 18:40:21  dennis
 *  Now traps for invalid (null) XScale from the x_scale_ui
 *  and uses the result from getXRange() for the DataSet as
 *  the default.
 *
 *  Revision 1.21  2002/06/14 20:58:41  dennis
 *  Fixed bug with XRange not updating graphs.
 *  Improved performance by:
 *  1.UpdateHGraphRange() now only sets the autoY_bounds
 *    if it is not already set.
 *  2.Added method DrawWithNewXRange() that just adjusts
 *    the graphs if a redraw request is received with the
 *    the reason that the XRange changed.
 *
 *  Revision 1.20  2002/05/30 22:57:29  chatterjee
 *  Added print feature
 *
 *  Revision 1.19  2002/03/13 16:12:18  dennis
 *  Converted to new abstract Data class.
 *
 */
package DataSetTools.viewer.Graph;

import DataSetTools.dataset.*;
import DataSetTools.viewer.*;
import DataSetTools.viewer.util.*;
import gov.anl.ipns.Util.Messaging.*;
import gov.anl.ipns.Util.Numeric.*;
import gov.anl.ipns.ViewTools.Panels.Graph.*;
import gov.anl.ipns.ViewTools.UI.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import DataSetTools.components.ui.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * A graph view of a Data Set.
 *
 * @see DataSetTools.dataset.DataSet
 *
 */

public class GraphView extends    DataSetViewer
                       implements Serializable
{ 
  private static final String HORIZONTAL_SCROLL = "Horizontal Scroll";
  private static final String DO_REBIN          = "Graph Rebinned Data";
  private static final int    GRAPH_HEIGHT      = 100;

                                                    // Image and border
  SplitPaneWithState   main_split_pane = null;

  private JPanel          display_controls = new JPanel();
  private XScaleChooserUI x_scale_ui       = null;

  private JPanel       graph_data_panel  = new JPanel();
  private DataSetXConversionsTable graph_table;

  private GraphJPanel  h_graph[]        = null;
  private boolean      was_selected[]   = null;
  private boolean      graph_sent_pointed_at = false;

                                              // panel for the horizontal graph
  JPanel viewport = new JPanel();
  int    viewport_preferred_width = 300;
  private JScrollPane  hgraph_scroll_pane = new JScrollPane( viewport, 
                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                                          );

  private JSlider      hgraph_scale_slider = new JSlider( JSlider.HORIZONTAL,
                                                          0, 1000, 0 );
  ClosedInterval       y_range;

/* --------------------------------------------------------------------------
 *
 * CONSTRUCTORS
 *
 */

/* ------------------------------------------------------------------------ */

public GraphView( DataSet data_set, ViewerState state ) 
{
  super( data_set, state );       
  AddOptionsToMenu();

  if ( !validDataSet() )
    return;
  JMenuBar jmb= getMenuBar();
  gov.anl.ipns.Util.Sys.PrintComponentActionListener.setUpMenuItem( jmb, this);
  init();
  DrawGraphs();
}

/* -----------------------------------------------------------------------
 *
 *  PUBLIC METHODS
 *
 */


/* ----------------------------- redraw --------------------------------- */
/**
 * Redraw all or part of the graphs. The amount that needs to be redrawn is
 * determined by the "reason" parameter.  
 *
 * @param  reason  The reason the redraw is needed.
 */

public void redraw( String reason )
{
  if ( !validDataSet() )
    return;

  if ( reason.equals( IObserver.SELECTION_CHANGED ))
    DrawSelectedGraphs();

  else if ( reason.equals( IObserver.POINTED_AT_CHANGED ))
    if ( graph_sent_pointed_at )
      graph_sent_pointed_at = false;             // ignore message from itself
    else
      DrawPointedAtGraph();

  else if ( reason.equals( XScaleChooserUI.X_RANGE_CHANGED ) )
    DrawWithNewXRange();

  else
    DrawGraphs();
}


/* ----------------------------- setDataSet ------------------------------- */
/**
 * Specify a different DataSet to be shown by this viewer.
 *
 * @param  ds   The new DataSet to view
 *
 */

public void setDataSet( DataSet ds )
{
  setVisible(false);
  super.setDataSet( ds );

  if ( !validDataSet() )
    return;

  init();
  redraw( NEW_DATA_SET );
  setVisible(true);
}


/* ------------------------ getXConversionScale -------------------------- */
 /**
  *  Return an XScale specified by the user to be used to control X-Axis
  *  conversions.
  *
  *  @return  The current values from the number of bins control and the
  *           x range control form the Xscale that is returned, if no valid
  *           XScale is present, the XScale returned by the DataSet's
  *           getXRange() method is used..
  *
  */
 public XScale getXConversionScale()
  {
    XScale x_scale = x_scale_ui.getXScale();
    if ( x_scale == null )                       // if no valid one specified
      x_scale = getDataSet().getXRange();        // use default

    return x_scale;
  }


/* ------------------------- getDisplayComponent -------------------------- */
 /**
  *  Get the scrolled pane that contains the graph displays, without
  *  any associated controls or auxillary displays.
  */
  public JComponent getDisplayComponent()
  {
    return hgraph_scroll_pane;
  }


/* ----------------------------- main ------------------------------------ */
/*
 *  For testing purposes only
 */
public static void main(String[] args)
{
  DataSet   data_set   = new DataSet("Sample DataSet", "Sample log-info");
  data_set.setX_units( "Test X Units" );
  data_set.setX_label("Text X Label" );
  data_set.setY_units( "Test Y Units" );
  data_set.setY_label("Text Y Label" );

  Data          spectrum;     // data block that will hold a "spectrum"
  float[]       y_values;     // array to hold the "counts" for the spectrum
  UniformXScale x_scale;      // "time channels" for the spectrum

  for ( int id = 1; id < 10; id++ )            // for each id
  {
    x_scale = new UniformXScale( 1, 5, 50 );   // build list of time channels

    y_values = new float[50];                       // build list of counts
    for ( int channel = 0; channel < 50; channel++ )
      y_values[ channel ] = (float)Math.sin( id * channel / 10.0 );

    spectrum = Data.getInstance( x_scale, y_values, id );   
                                                    // put it into a "Data"
                                                    // object and then add
    data_set.addData_entry( spectrum );             // that data object to
                                                    // the data set
  }

  GraphView graph_view = new GraphView( data_set, null );
  JFrame f = new JFrame("Test for GraphView class");
  f.setBounds(0,0,600,400);
  f.setJMenuBar( graph_view.getMenuBar() );
  f.getContentPane().add(graph_view);
  f.setVisible(true);
}
   
/* -----------------------------------------------------------------------
 *
 * PRIVATE METHODS
 *
 */ 

/* -------------------------------- init -------------------------------- */
private void init()
{
  if ( main_split_pane != null )     // get rid of the old components first
  {
    graph_data_panel.removeAll();
    hgraph_scroll_pane.removeAll();
    main_split_pane.removeAll();
    removeAll();
  }

  hgraph_scroll_pane = new JScrollPane( viewport,
                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
                                          );
  hgraph_scroll_pane.getHorizontalScrollBar().addAdjustmentListener(
                                            new HScrollBarListener() );

  main_split_pane = new SplitPaneWithState( JSplitPane.HORIZONTAL_SPLIT,
                                            MakeHorGraphArea(),
                                            MakeControlArea(),
                                            0.7f );

  String title = getDataSet().toString();
  AttributeList attr_list = getDataSet().getAttributeList();
  Attribute     attr      = attr_list.getAttribute(Attribute.RUN_TITLE); 
  if ( attr != null )
   title = attr.getStringValue();

  TitledBorder border = new TitledBorder(
                                    LineBorder.createBlackLineBorder(),
                                    title );
  border.setTitleFont( FontUtil.BORDER_FONT );
  main_split_pane.setBorder( border );

  this.setLayout( new GridLayout(1,1) );
  this.add( main_split_pane );
  MakeConnections();                       // Add event handlers

  y_range = getDataSet().getYRange();
}

/* ------------------------- AddOptionsToMenu --------------------------- */

private void AddOptionsToMenu()
{
  OptionMenuHandler option_menu_handler = new OptionMenuHandler();
  JMenu option_menu = menu_bar.getMenu( OPTION_MENU_ID );
                                                    // Horizontal scroll option
  JCheckBoxMenuItem cb_button = new JCheckBoxMenuItem( HORIZONTAL_SCROLL );
  cb_button.setState( getState().get_boolean( ViewerState.H_SCROLL ) );
  cb_button.addActionListener( option_menu_handler );
  option_menu.add( cb_button );

  cb_button = new JCheckBoxMenuItem( DO_REBIN );
  cb_button.setState( getState().get_boolean( ViewerState.REBIN ) );
  cb_button.addActionListener( option_menu_handler );
  option_menu.add( cb_button );
}



/* ---------------------------- MakeControlArea --------------------------- */
/*
 *  Construct the group of controls for color, logscale, etc. 
 */
private Component MakeControlArea()
{
  Box control_area = new Box( BoxLayout.Y_AXIS );
  if ( getDataSet().getNum_entries() == 0 )
  {
    control_area.add( new JLabel( "ERROR: NO DATA" ) );
    return control_area;
  }
                                                     // Add the x-range control
  String label = getDataSet().getX_units();
  UniformXScale x_scale  = getDataSet().getXRange();
  float x_min = x_scale.getStart_x();
  float x_max = x_scale.getEnd_x();
  int n_steps = getDataSet().getMaxXSteps();
  if ( getDataSet().getData_entry(0).isHistogram() )
    n_steps = n_steps - 1;
  x_scale_ui = new XScaleChooserUI( "X Scale", label, x_min, x_max, n_steps );
  control_area.add( x_scale_ui );

                                                     // Add the y-range slider
  hgraph_scale_slider.setPreferredSize( new Dimension(120,50) );
  int value = (int)(10 * getState().get_float( ViewerState.AUTO_SCALE ));
  hgraph_scale_slider.setValue( value );

  TitledBorder border = new TitledBorder(LineBorder.createBlackLineBorder(), 
                                         "Auto-Scale" );
  border.setTitleFont( FontUtil.BORDER_FONT );
  hgraph_scale_slider.setBorder( border );

  control_area.add( hgraph_scale_slider );
  control_area.add( display_controls );      

  graph_table = new DataSetXConversionsTable( getDataSet() );
  border = new TitledBorder(LineBorder.createBlackLineBorder(), "Graph Data" );
  border.setTitleFont( FontUtil.BORDER_FONT );
  graph_data_panel.setBorder( border );

  graph_data_panel.setLayout( new GridLayout(1,1) );
  graph_data_panel.add( graph_table.getTable() );
  control_area.add( graph_data_panel );

  JPanel filler = new JPanel();
  filler.setPreferredSize( new Dimension( 120, 2000 ) );
  control_area.add( filler );

  return control_area;
}

/* ------------------------ MakeHorizontalGraphArea ----------------------- */
/*
 * Construct panel for the horizontal graph.
 */

private JScrollPane MakeHorGraphArea( )
{
  String title;

  OperationLog op_log = getDataSet().getOp_log();
  if ( op_log.numEntries() <= 0 )
    title = "GRAPH DISPLAY AREA";
  else
    title = op_log.getEntryAt( op_log.numEntries() - 1 );

  TitledBorder border = new TitledBorder( LineBorder.createBlackLineBorder(),
                                          title );
  border.setTitleFont( FontUtil.BORDER_FONT ); 
  hgraph_scroll_pane.setBorder( border ); 

  JScrollBar scroll_bar = hgraph_scroll_pane.getVerticalScrollBar();
  scroll_bar.setUnitIncrement( 25 );
  return hgraph_scroll_pane;
}

/*--------------------------- MakeConnections ---------------------------- */
/*
 *  Add listeners for events that need to be handled.
 */
private void MakeConnections()
{
   hgraph_scale_slider.addChangeListener( new HGraphScaleEventHandler() );   
   x_scale_ui.addActionListener( new XScaleListener() );
}


/* --------------------- SetHorizontalScrolling ------------------------ */
private void SetHorizontalScrolling( boolean state )
{
  getState().set_boolean( ViewerState.H_SCROLL, state );
  if ( state )
  {
    XScale x_scale = getXConversionScale();
    viewport_preferred_width = x_scale.getNum_x();
    JScrollBar scroll_bar = hgraph_scroll_pane.getHorizontalScrollBar();
    int min = scroll_bar.getMinimum();
    int max = scroll_bar.getMaximum();
    int position = (int) ((max-min) 
                        * getState().get_float(ViewerState.H_SCROLL_POSITION) 
                        + min );
    scroll_bar.setValue( position );
  }
  else
    viewport_preferred_width = 300;

  int  num_data_blocks = getDataSet().getNum_entries();
  viewport.setPreferredSize( new Dimension( viewport_preferred_width, 
                                            GRAPH_HEIGHT*num_data_blocks ) );

  hgraph_scroll_pane.doLayout();
}


/* -------------------------- SyncVScrollBar ------------------------- */

private void SyncVScrollBar()
{
  JScrollBar v_bar = hgraph_scroll_pane.getVerticalScrollBar();

  if ( v_bar == null || !v_bar.isVisible() )
    return;

  DataSet ds = getDataSet();
  if ( ds == null )
    return;

  int n_rows = ds.getNum_entries();
  if ( n_rows <= 3 )
    return;

  int row = ds.getPointedAtIndex();
  if ( row == DataSet.INVALID_INDEX )
    return;

  float v_bar_min  = v_bar.getMinimum();
  float v_bar_max  = v_bar.getMaximum();
  float bar_height  = v_bar.getSize().height;

  if ( bar_height >= n_rows*GRAPH_HEIGHT )      // scrolling not needed
    return;

  float fraction = 0;
  int   v_bar_value;

  if ( v_bar_max == v_bar_min )
    v_bar_value = 0;
  else
  {
    fraction = ((row+0.5f)*GRAPH_HEIGHT-bar_height/2) /   
               (float)(n_rows*GRAPH_HEIGHT-bar_height);

    if ( fraction < 0 )
      fraction = 0;
    if ( fraction > 1 )
      fraction = 1;
    v_bar_value = (int)( v_bar_min +
                        (v_bar_max - v_bar_min - bar_height) * fraction );
  }
  v_bar.setValue( v_bar_value );

  getState().set_float( ViewerState.V_SCROLL_POSITION, fraction );
}


/* ----------------------------- SetRebin ------------------------------- */
private void SetRebin( boolean state )
{
  getState().set_boolean( ViewerState.REBIN, state );
  DrawGraphs();
}


/* ---------------------------- DrawGraphs ----------------------------- */

private void DrawGraphs( )
{
  if ( h_graph != null )               // get rid of old graphs, if necessary
  {  
    if ( getDataSet().getNum_entries() != h_graph.length )
    {
      for ( int i = 0; i < h_graph.length; i++ )
        h_graph[i] = null;
      h_graph = null;
      viewport.removeAll();
      viewport.setVisible(false);
    }
  }

  int  num_data_blocks = getDataSet().getNum_entries();
  
  if ( h_graph == null )               // allocate new graphs, if needed
  {
    was_selected = new boolean[ num_data_blocks ];
    h_graph      = new GraphJPanel[ num_data_blocks ];
    viewport.setLayout( new GridLayout(num_data_blocks, 1) );
    SetHorizontalScrolling( getState().get_boolean( ViewerState.H_SCROLL ) );

    SelectionKeyAdapter key_adapter = new SelectionKeyAdapter();
    for ( int i = 0; i < num_data_blocks; i++ )
    {
      h_graph[i] = new GraphJPanel();
      h_graph[i].addKeyListener( key_adapter ); 
      h_graph[i].addMouseMotionListener( new HGraphMouseMotionAdapter() );
      h_graph[i].addMouseListener( new HGraphMouseAdapter() );

      JPanel border_panel =new JPanel();
      TitledBorder border =new TitledBorder(LineBorder.createBlackLineBorder());
      border.setTitleFont( FontUtil.BORDER_FONT );
      border_panel.setBorder( border );

      border_panel.setLayout( new GridLayout( 1, 1) );
      border_panel.add( h_graph[i] );
      viewport.add( border_panel );
    }
  }

  for ( int i = 0; i < num_data_blocks; i++ )
    DrawSpecifiedGraph( i );

  UpdateHGraphRange();

  viewport.setVisible(true);
}

/* --------------------------- DrawWithNewXRange ------------------------ */

private void DrawWithNewXRange()
{
  if ( h_graph == null )
    return;

  XScale x_scale = getXConversionScale();

  float x_min  = x_scale.getStart_x();
  float x_max  = x_scale.getEnd_x();
  for ( int i = 0; i < h_graph.length; i++ )
  {
    h_graph[i].setX_bounds( x_min, x_max );
    h_graph[i].repaint();
  }
}



/* -------------------------- DrawSelectedGraphs ------------------------- */

private void DrawSelectedGraphs()
{
  if ( h_graph == null )              // nothing to do yet
    return;

  int  num_data_blocks = getDataSet().getNum_entries();

                                      // redraw the graphs with a different
                                      // selection status
  for ( int i = 0; i < num_data_blocks; i++ )
    if ( getDataSet().isSelected(i) && !was_selected[i]  || 
        !getDataSet().isSelected(i) &&  was_selected[i]   )
      DrawSpecifiedGraph( i );

                                      // update the selection status
  for ( int i = 0; i < num_data_blocks; i++ )
    was_selected[i] = getDataSet().isSelected(i);
}



/* -------------------------- DrawPointedAtGraph ------------------------- */

private void DrawPointedAtGraph()
{
  int index = getDataSet().getPointedAtIndex();

  GraphJPanel graph = null;
  if ( h_graph != null )
    if ( index >= 0 && index < h_graph.length )
      graph = h_graph[index];

  if ( graph != null )
  {
    Point pt = new Point(0,0);
    for ( int i = 0; i < h_graph.length; i++ )    // turn off all crosshairs
      if ( h_graph[i].isDoingCrosshair() )        // before scrolling
         h_graph[i].stop_crosshair(pt);       

    SyncVScrollBar(); 

    floatPoint2D float_pt = new floatPoint2D();
    float_pt.x = getDataSet().getPointedAtX();
    float_pt.y = graph.getY_value( float_pt.x, 0 );
    graph.set_crosshair_WC( float_pt ); 

    UpdateHGraphReadout( graph );
  }

}

/* -------------------------- DrawSpecifiedGraph ------------------------- */

private boolean DrawSpecifiedGraph( int index )
{
  if ( h_graph == null )              // nothing to do yet
    return false;

  int  num_data_blocks = getDataSet().getNum_entries();

  if ( index < 0 || index >= num_data_blocks )
    return false;                      // index is invalid, so nothing to do

  Data data_block = getDataSet().getData_entry( index );

  JPanel border_panel = (JPanel)h_graph[ index ].getParent();
  TitledBorder border = (TitledBorder)border_panel.getBorder();
  String border_label = DS_Util.getData_ID_String( getDataSet(), index );

  if ( data_block.isSelected() )
  {
    h_graph[index].setColor( Color.blue, 0, false );
    border.setTitleColor( Color.blue );
    was_selected[index] = true;
  }
  else
  {
    h_graph[index].setColor( Color.black, 0, false );
    border.setTitleColor( Color.black );
    was_selected[index] = false;
  }

  float x[];
  float y[];
  if ( getState().get_boolean( ViewerState.REBIN ) )
  {
    XScale x_scale = getXConversionScale();
    x = x_scale.getXs();
    y = data_block.getY_values( x_scale, IData.SMOOTH_LINEAR );
  }
  else
  {
    x = data_block.getX_scale().getXs();
    y = data_block.getY_values();
  }

  h_graph[index].setData( x, y, 0, false );

  XScale x_scale = getXConversionScale();
  float  x_min  = x_scale.getStart_x();
  float x_max  = x_scale.getEnd_x();
  h_graph[index].setX_bounds( x_min, x_max );

  border.setTitle( border_label );
  return true;
}



/* ---------------------------- getBlockNumber --------------------------- */

private int getBlockNumber( GraphJPanel gp )
{
  int  num_data_blocks = getDataSet().getNum_entries();
  for ( int i = 0; i < num_data_blocks; i++ )
    if ( gp == h_graph[i] )
      return i;

  System.out.println("ERROR: GraphJPanel not found in getBlockNumber");
  return -1; 
}


/* -------------------------- UpdateHGraphReadout ------------------------- */

private void UpdateHGraphReadout( GraphJPanel gp )
{
  floatPoint2D float_pt = gp.getCurrent_WC_point();
  int row = getDataSet().getPointedAtIndex();
  graph_table.showConversions( float_pt.x, float_pt.y, row );
}


/* -------------------------- UpdateHGraphRange --------------------------- */

private void UpdateHGraphRange()
{
  int value = hgraph_scale_slider.getValue();
  getState().set_float( ViewerState.AUTO_SCALE, value/10.0f );

  if ( value == 0 )                                     // ranges
  {
    for ( int i = 0; i < getDataSet().getNum_entries(); i++ )
      if ( !h_graph[i].is_autoY_bounds() )
        h_graph[i].autoY_bounds();

    TitledBorder border = new TitledBorder(
                                LineBorder.createBlackLineBorder(),
                                "Auto-Scale" );
    border.setTitleFont( FontUtil.BORDER_FONT );
    hgraph_scale_slider.setBorder( border );
  }
  else
  {
    float y_min = y_range.getStart_x();
    float y_max = y_range.getEnd_x();
    float range = ( y_max - y_min ) * value / 1000.0f;
    if ( h_graph != null)
      for ( int i = 0; i < getDataSet().getNum_entries(); i++ )
        h_graph[i].setY_bounds( y_min, y_min + range );

    TitledBorder border = new TitledBorder(
                            LineBorder.createBlackLineBorder(),
                            ""+value/10.0f+"%(max)" );
    border.setTitleFont( FontUtil.BORDER_FONT );
    hgraph_scale_slider.setBorder( border );
  }
}


/* -------------------------- doUpdateForMouse --------------------------- */

private void doUpdateForMouse( MouseEvent e )
{
   GraphJPanel gp = (GraphJPanel)e.getComponent();
   DataSet     ds = getDataSet();

   int   last_data_block = ds.getPointedAtIndex();
   float last_x          = ds.getPointedAtX();

   int   row   = getBlockNumber( gp );
   float new_x = gp.getCurrent_WC_point().x;

   if ( row   != last_data_block ||                  // only update if needed
        new_x != last_x          ||
        ds.getNum_entries() == 1   )
   {
     ds.setPointedAtIndex( row );
     ds.setPointedAtX( new_x );
     UpdateHGraphReadout( gp );
     graph_sent_pointed_at = true;
     ds.notifyIObservers( IObserver.POINTED_AT_CHANGED );
   }
}


/* -------------------------------------------------------------------------
 *
 *  INTERNAL CLASSES
 *
 */

/* ------------------------- HGraphScaleEventHandler ----------------------- */

private class HGraphScaleEventHandler implements ChangeListener,
                                                 Serializable
{
  public void stateChanged(ChangeEvent e)
  {
    UpdateHGraphRange();
  }
}


/* ----------------------- HGraphMouseMotionAdapter ------------------------ */

private class HGraphMouseMotionAdapter extends    MouseMotionAdapter
                                       implements Serializable
{
   public void mouseDragged( MouseEvent e )
   {
     doUpdateForMouse( e );
   }
}

/* ----------------------- HGraphMouseAdapter ---------------------------- */

private class HGraphMouseAdapter extends    MouseAdapter
                                 implements Serializable
{
   public void mousePressed( MouseEvent e )
   {
     doUpdateForMouse( e );
   }
}


/* ------------------------------ finalize ----------------------------- */
  /**
   *  Trace the finalization of objects
   */
/*
  protected void finalize() throws IOException
  {
    System.out.println( "finalize GraphView" );
  }
*/

/* ----------------------- OptionMenuHandler --------------------------- */

  private class OptionMenuHandler implements ActionListener,
                                             Serializable
  {
    public void actionPerformed( ActionEvent e )
    {
      String action = e.getActionCommand();

       if ( action.equals( HORIZONTAL_SCROLL ))
       {
         boolean state = ((JCheckBoxMenuItem)e.getSource()).getState();
         SetHorizontalScrolling( state );
       }
       else if ( action.equals( DO_REBIN ))
       {
         boolean state = ((JCheckBoxMenuItem)e.getSource()).getState();
         SetRebin( state );
       }
    }
  }

/* ------------------------ XScaleListener ----------------------------- */

  private class XScaleListener implements ActionListener,
                                          Serializable
  {
     public void actionPerformed(ActionEvent e)
     {
       String action  = e.getActionCommand();
       getDataSet().notifyIObservers( action );
     }
  }

/* ------------------------- SelectionKeyAdapter ------------------------ */

private class SelectionKeyAdapter extends    KeyAdapter
                                  implements Serializable
{
  public void keyPressed( KeyEvent e )
  {
    GraphJPanel gp = (GraphJPanel)e.getComponent();
    int row = getBlockNumber( gp );

    KeySelect.ProcessKeySelection( getDataSet(), row, e );

    e.consume();
  }
}

/* ----------------------- HScrollBarListener ---------------------------- */

  private class HScrollBarListener implements AdjustmentListener,
                                              Serializable
  {
    public void adjustmentValueChanged( AdjustmentEvent e )
    {
      // record the new scroll bar postion
  
      JScrollBar bar = hgraph_scroll_pane.getHorizontalScrollBar();
      float bar_min   = bar.getMinimum();
      float bar_max   = bar.getMaximum();
      float bar_value = bar.getValue();
      float fraction  = 0;
      if ( bar_max == bar_min )
        fraction = 0;
      else
        fraction = (bar_value - bar_min)/(bar_max - bar_min);
      getState().set_float( ViewerState.H_SCROLL_POSITION, fraction );
    }
  }

}
