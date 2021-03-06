/*
 * File:  GL_RecipPlaneView.java
 *
 * Copyright (C) 2003, 2004 Dennis Mikkelson
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *  Last Modified:
 * 
 *  $Author$
 *  $Date$            
 *  $Revision$
 *
 * Modified:
 *
 * $Log$
 * Revision 1.31  2006/11/04 20:24:35  dennis
 * Minor efficiency improvment for switch to new non-array Vector3D.
 *
 * Revision 1.30  2006/06/13 14:17:10  dennis
 * Now includes a control to select specific detectors to display,
 * project peaks, etc.
 *
 * Revision 1.29  2006/06/08 22:36:21  dennis
 * Now works OK if the user presses the "Cancel" button on the
 * file dialog for writing a Peaks File.
 *
 * Revision 1.28  2006/03/15 21:21:40  dennis
 * Added control to restrict peaks displayed to a specified
 * range of Q values.
 * Added SCDhkl operator to DataSets if an orientation matrix
 * was loaded, so that HKL values can be shown in the
 * conversions table, for peaks that are picked.
 *
 * Revision 1.27  2006/03/15 17:10:56  dennis
 * Added run number and detector ID to 3D display of
 * detector coverage in Q.
 *
 * Revision 1.26  2006/03/14 23:41:25  dennis
 * Added the XConversions table readout for pointed at peaks.
 *
 * Revision 1.25  2006/02/21 03:28:37  dennis
 * Now uses the Grid_util.getAllDataGrids() method to get all the
 * DataGrids for simple tubes, LPSDs or area detectors.  Finished
 * adapting this 3D reciprocal lattice viewer to be used to view
 * data from instruments other than just single crystal diffractometers.
 * Also, removed a few unused variables.
 *
 * Revision 1.24  2006/02/06 00:19:29  dennis
 * Now uses the SCD_util.DetectorToMinMaxHKL() method to find the
 * extent of the region in HKL space covered by a detector.
 *
 * Revision 1.23  2006/01/16 05:50:44  dennis
 * Increased number of one-dimensional FFTs from 20 to 30.
 * Now displays row,col,tof, etc. in status pane for pointed at peak.
 *
 * Revision 1.22  2006/01/13 18:35:46  dennis
 * Added code to dump out information about a peak:
 *   Qx,  Qy,  Qz,
 *   Col, Row, Channel
 *   Xcm, yCm, Wavelength
 *   Col, Row, Time-of-Flight
 * when the peak is "clicked on" for debugging purposes.
 * Increased the minimum number of Fourier transforms displayed to 20.
 *
 * Revision 1.21  2005/06/20 03:13:42  dennis
 * Added new constructor that takes an array of DataSets.
 *
 * Revision 1.20  2005/06/02 22:34:19  dennis
 * Modified to just use IVirtualArray2D methods on a
 * VirtualArray2D object.
 *
 * Revision 1.19  2005/05/25 20:24:43  dennis
 * Now calls convenience method WindowShower.show() to show
 * the window, instead of instantiating a WindowShower object
 * and adding it to the event queue.
 *
 * Revision 1.18  2005/04/20 21:27:01  dennis
 * Now checks for co-planar vectors (singular UB) before
 * trying to calculate and write the orientation matrix.
 *
 * Revision 1.17  2004/09/16 18:12:09  dennis
 * Made calibrations on both image axes linear and provided somewhat
 * meaningful ranges of values ( |Q| ) for the axis calibrations.
 *
 * Revision 1.16  2004/09/15 22:03:50  millermi
 * - Updated LINEAR, TRU_LOG, and PSEUDO_LOG setting for AxisInfo class.
 *   Adding a second log required the boolean parameter to be changed
 *   to an int. These changes may affect any ObjectState saved configurations
 *   made prior to this version.
 *
 * Revision 1.15  2004/09/06 20:34:02  dennis
 * Added option to write the list of bins that are above the currently
 * selected threshold, in the form of a "modified" peaks file with each
 * bin listed separately.
 *
 * Revision 1.14  2004/09/06 18:22:27  dennis
 * Now writes the transpose of the orientation matrix, following the
 * convention for the SCD at IPNS.
 *
 * Revision 1.13  2004/08/11 21:32:07  dennis
 * Placed controls in tabbed pane to save screen space.
 * Write now writes the orientation matrix to a file as well as to screen.
 * loadFiles() now returns false if it fails to load the files.
 *
 * Revision 1.12  2004/08/11 05:22:46  dennis
 * Put some debug prints in  if (debug) statements.
 * The calculated integer hkl values now more nearly cover the
 * full range covered by the detector.
 * Added mechanism for user to selectively filter peaks to be on
 * planes of constant h, constant k and or constant l.
 * Also, user may now specify the d-spacing corresponding to a
 * family of planes.
 *
 * Revision 1.11  2004/08/10 01:45:03  dennis
 * Added controls to separately turn iso-surfaces, detector coverage
 * regions and hkl marks on/off.  Added separate threshold control for
 * iso-surfaces.  Added reset button to reset the center of the view
 * to the origin.
 *
 * Revision 1.10  2004/08/09 15:27:00  dennis
 * Moved code that assigns hkl values to peaks using the currently
 * selected plane normal directions, into it's own private method.
 * Added code to calculate least squares fitted orientation matrix,
 * based on assigned hkl values.
 *
 * Revision 1.9  2004/08/04 23:11:18  dennis
 * Removed redundant MouseMotionListener.
 * "Center" point is now just changed by changing the VRP, since the
 * AltAz view controller was updated to adjust the COP when the VRP
 * is set.
 *
 * Revision 1.8  2004/07/30 18:55:09  dennis
 * Now ignores threshold values that are less than 3.
 * The initialize() method now uses the SetThresholdScale method.
 * "Apply" button name changed to "Calc FFTs".
 *
 * Revision 1.7  2004/07/30 13:36:14  dennis
 * Number of points needed for the wire frame drawing the region
 * of Q covered by the detector is now based on the number of rows
 * and columns in the detector.
 *
 * Revision 1.6  2004/07/29 14:04:40  dennis
 * Now uses Ruth's FinishJFrame rather than a JFrame, so that more
 * of the resources associated with the window are freed.  This is
 * an attempt to fix a problem with opening the view a second time
 * on Ruth's XP laptop.
 *
 * Revision 1.5  2004/07/28 15:44:05  dennis
 * Added public methods to draw  contours, hkl marks and Q-regions
 * covered by detectors.
 * Added constructor that takes the path, run numbers, etc., so it
 * can be constructed from an operator.
 * No longer makes a new grid for each PeakData object, but refers to
 * the grids from the DataSet.
 * Made more variables private.
 *
 * Revision 1.4  2004/07/26 21:50:51  dennis
 * Now displays "voxel" extending between the eight corners of a bin that
 * is above the current threshold, rather than just a cube centered at the
 * bin center.
 * Contour lines are now also omitted for regions in a specified border
 * region.
 * Changed name of PeakData to PeakData_d.
 *
 * Revision 1.3  2004/07/23 13:19:32  dennis
 * Added capabilities to:
 *   - Load an orientation matrix
 *   - Draw markers at integer hkl positions
 *   - Restrict data to a selected plane in hkl
 *   - Mark region of reciprocal space covered by a detector
 *
 * Revision 1.2  2004/07/16 15:04:01  dennis
 * Added calibrated axes.
 * Readout of QxQyQz positions of peaks now supported.
 * Picking of 3 peaks to select a plane now supported.
 *
 * Revision 1.1  2004/07/14 16:53:43  dennis
 * Initial port of SCD reciprocal space view & selection tool to
 * OpenGL based 3D tools.
 */

package DataSetTools.trial;

import DataSetTools.retriever.*;
import DataSetTools.viewer.*;
import DataSetTools.dataset.*;
import DataSetTools.util.*;
import DataSetTools.operator.*;
import DataSetTools.operator.DataSet.EditList.*;
import DataSetTools.operator.DataSet.Information.XAxis.*;
import DataSetTools.operator.Generic.TOF_SCD.*;
import DataSetTools.math.*;
import DataSetTools.instruments.*;
import DataSetTools.components.ui.*;
import gov.anl.ipns.MathTools.*;
import gov.anl.ipns.MathTools.Geometry.*;
import gov.anl.ipns.Util.File.*;
import gov.anl.ipns.Util.Messaging.*;
import gov.anl.ipns.Util.Numeric.*;
import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.ViewTools.Components.*;
import gov.anl.ipns.ViewTools.Panels.Image.*;
import gov.anl.ipns.ViewTools.Panels.GL_ThreeD.*;
import gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Shapes.*;
import gov.anl.ipns.ViewTools.Panels.GL_ThreeD.ViewControls.*;
import gov.anl.ipns.ViewTools.Panels.GL_ThreeD.Fonts.*;
import gov.anl.ipns.ViewTools.Panels.Contour.*;
import gov.anl.ipns.ViewTools.UI.*;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import jnt.FFT.*;

public class GL_RecipPlaneView
{
  public static final String NORMAL_ATTRIBUTE    = "Plane Normal";
  public static final String FREQUENCY_ATTRIBUTE = "FFT Peak Frequency";
  public static final String LSQ_ERROR_ATTRIBUTE = "LSQ errors";
  public static final String D_SPACING_ATTRIBUTE = "d-Spacing";
  public static final String Q_SPACING_ATTRIBUTE = "Q-Spacing";
  public static final String PEAK_OBJECTS        = "Peaks_";
  public static final String CONTOUR_OBJECTS     = "Contours_";
  public static final String BOUNDARY_OBJECTS    = "Boundaries_";
  public static final String MARK_OBJECTS        = "Marks_";

  public static final String UNDEFINED = "undefined";

  public static final String ORIGIN = " origin ";
  public static final String VEC_1  = " (+)";
  public static final String VEC_2  = " (*)";

  public static final String CONST_H_SLICE = "Const h Slice";
  public static final String CONST_K_SLICE = "Const k Slice";
  public static final String CONST_L_SLICE = "Const l Slice";
  public static final int    SLICE_STEPS = 700;

  static File input_orientation_matrix_dir = null;
                                                    // flags for various options
  private boolean iso_surface_shown         = false;

  private final int DIMENSION = 3;       // set to 4 to allow affine transform
                                         // set to 3 to just use rotation and
                                         // scaling.
  private float MIN_Q_DEF       = 0;
  private float MAX_Q_DEF       = 30;
  private float INDEX_TOLERANCE = 0.1f;
  private float SLICE_SIZE_IN_Q = 20;
  private int   FFT_DATA_LENGTH = 512;
  private int   SLIDER_DEF      = 60;
  private int   SLIDER_MIN      = 3;
  private int   SLIDER_MAX      = 250;

  private float min_Q_threshold    = MIN_Q_DEF;
  private float max_Q_threshold    = MAX_Q_DEF;
  private float peak_threshold     = SLIDER_DEF;
  private float contour_threshold  = SLIDER_DEF;

  private float LSQ_THRESHOLD     = 0.10f;
  private final float YELLOW[]    = { 0.8f, 0.8f, 0.2f };
  private final float CYAN[]      = { 0.2f, 0.8f, 0.8f };
  private final float GRAY[]      = { 0.4f, 0.4f, 0.4f };
  private final float LIGHTGRAY[] = { 0.6f, 0.6f, 0.6f };
  private final float RED[]       = { 0.8f, 0.3f, 0.3f };
  private final float GREEN[]     = { 0.3f, 0.8f, 0.3f };
  private final float BLUE[]      = { 0.3f, 0.3f, 0.8f };

  private FinishJFrame scene_f;

  private ImageFrame2 h_frame = null;
  private ImageFrame2 k_frame = null;
  private ImageFrame2 l_frame = null;

  private String path       = null;
  private String run_nums   = null;
  private String calib_file = null;
  private String orient_file = null;

  private int    runs[];
  private String threshold = "";
  private String border_size = "";
  private int    edge_pix = 0;

  private ThreeD_GL_Panel vec_Q_space;
  private AltAzController controller;
  private Color           colors[];
  private float           rgb_colors[][];
      
  private TextRangeUI     q_range_control;
  private TextIntListUI   det_choice_control;
  private JSlider         peak_threshold_slider;
  private JSlider         contour_threshold_slider;

  private DataSetXConversionsTable conv_table = null;

  private JLabel          q_readout;
  private SimpleVectorReadout   origin_vec;
  private SimpleVectorReadout   vec_1;
  private SimpleVectorReadout   vec_2;

  private LatticePlaneUI  h_plane_ui;
  private LatticePlaneUI  k_plane_ui;
  private LatticePlaneUI  l_plane_ui;

  private Vector          vec_q_transformer = null;
  private Vector          ds_of_transformer = null;
  private Vector          all_peaks;

  private int             global_obj_index = 0;  // needed to keep the pick 
                                                 // ids distinct
//  private String          file_names[];
  private Vector          data_sets;
  private Hashtable       calibrations = null;
  private Tran3D          orientation_matrix = null;
  private Tran3D          orientation_matrix_inverse = null;

  private Vector3D        all_vectors[];
  private double          QR_Rmat[][];   // "R" factor of QR factorization
  private double          QR_Umat[][];   // Matrix containing unit vectors U
                                         // describing the matrix Q, from QR
                                         // factorization.
  private DataSet         projection_ds;
  private DataSet         all_fft_ds;
  private DataSet         filtered_fft_ds;

  private boolean         debug = false;

  /* ---------------------------- Constructor ----------------------------- */

  public GL_RecipPlaneView( String path, 
                            String run_nums, 
                            String calib_file,
                            String orient_file )
  {
    this(); 

    this.path        = path;
    this.run_nums    = run_nums;

    File temp = new File( calib_file );
    if ( temp.exists() && temp.isFile() )
      this.calib_file  = calib_file;
 
    temp = new File( orient_file );
    if ( temp.exists() && temp.isFile() )
      this.orient_file = orient_file;
  }

  /* ---------------------------- Constructor ----------------------------- */

  public GL_RecipPlaneView( DataSet ds_array[], int threshold )
  {
     this();
     System.out.println("Start constructor for GL_RecipPlaneView");

     SetThresholdScale( threshold );     
     
     if ( ds_array == null || ds_array.length < 1 )
     {
       System.out.println("NULL OR EMPTY DATA SET LIST in GL_RecipPlaneView");
       return;
     }

     System.out.println("Adding " + ds_array.length + " DataSets" );
                                         // for now just assume 1 detector per
                                         // run in this case
     runs = new int[ ds_array.length ];
     for ( int i = 0; i < ds_array.length; i++ )
     {
       int[] run_num_list = AttrUtil.getRunNumber( ds_array[i] );
       if ( run_num_list != null && run_num_list.length >= 1 )
         runs[i] = run_num_list[0];
       else
         runs[i] = 1000000 + i;
       data_sets.add( ds_array[i] );
     }

     initialize( true );
  }


  /* ---------------------------- Constructor ----------------------------- */

  public GL_RecipPlaneView()
  {
//  System.out.println("In default constructor for GL_RecipPlaneView");

    scene_f = new FinishJFrame("Reciprocal Lattice Plane Viewer");
    scene_f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

    JPanel q_panel = new JPanel();

    vec_Q_space = new ThreeD_GL_Panel();
    controller  = new AltAzController( 45, 45, 1, 100, 25 );
    controller.setPerspective( false );

// ---- Display tabbed pane 

    q_range_control = new TextRangeUI( "Min_|Q| Max_|Q|",
                                        MIN_Q_DEF,MAX_Q_DEF );
    TitledBorder border = new TitledBorder( LineBorder.createBlackLineBorder(),
                                           "Range of |Q| Shown");
    border.setTitleFont( FontUtil.BORDER_FONT );
    q_range_control.setBorder( border );

    det_choice_control = new TextIntListUI( "Detector IDs:  ", "" );
                border = new TitledBorder( LineBorder.createBlackLineBorder(),
                                           "Detectors Shown");

    border.setTitleFont( FontUtil.BORDER_FONT );
    det_choice_control.setBorder( border );

    peak_threshold_slider = new JSlider(SLIDER_MIN,SLIDER_MAX,SLIDER_DEF);
    peak_threshold_slider.setMajorTickSpacing(20);
    peak_threshold_slider.setMinorTickSpacing(5);
    peak_threshold_slider.setPaintTicks(true);
    border = new TitledBorder( LineBorder.createBlackLineBorder(),
                                    "Peaks Threshold = " + SLIDER_DEF );
    border.setTitleFont( FontUtil.BORDER_FONT );
    peak_threshold_slider.setBorder( border );

    contour_threshold_slider = MakeSlider("Iso-surface Threshold",
                                           new ContourThresholdScaleHandler());

    JPanel checkbox_panel = new JPanel();
    checkbox_panel.setLayout( new GridLayout( 4, 1 ) );
    JCheckBox show_iso_surface = new JCheckBox( "Iso-surface" );
    JCheckBox show_coverage    = new JCheckBox( "Detector Coverage" );
    JCheckBox show_integer_hkl = new JCheckBox( "Integer HKL points" );
    JButton calc_fft_button    = new JButton("Calculate FFTs of Projections");

    checkbox_panel.add( show_iso_surface );
    checkbox_panel.add( show_coverage );
    checkbox_panel.add( show_integer_hkl );
    checkbox_panel.add( calc_fft_button );

    JPanel table_panel = new JPanel();
    table_panel.setLayout( new GridLayout(1,1) );
    conv_table = new DataSetXConversionsTable( DataSet.EMPTY_DATA_SET );
    border = new TitledBorder(LineBorder.createBlackLineBorder(),
                              "Selected Point Data" );
    border.setTitleFont( FontUtil.BORDER_FONT );
    table_panel.setBorder( border );
    table_panel.add( conv_table.getTable() );

// ----

    q_readout = new JLabel( UNDEFINED );

    origin_vec = new SimpleVectorReadout( ORIGIN, 
                                          "Select",
                                          new Vector3D(0,0,0));
    vec_1  = new SimpleVectorReadout( VEC_1, "Select +" );
    vec_2  = new SimpleVectorReadout( VEC_2, "Select *" );

    h_plane_ui = new LatticePlaneUI( "h" );
    k_plane_ui = new LatticePlaneUI( "k" );
    l_plane_ui = new LatticePlaneUI( "l" );

    border = new TitledBorder( LineBorder.createBlackLineBorder(),"Qxyz");
    border.setTitleFont( FontUtil.BORDER_FONT );
    q_panel.setBorder( border );

    q_readout.setFont( FontUtil.LABEL_FONT );
    q_readout.setHorizontalAlignment( JTextField.CENTER );
    q_readout.setBackground( Color.white );
    q_readout.setForeground( Color.black );
    q_panel.setBackground( Color.white );
    q_panel.setLayout( new GridLayout(1,1) );
    q_panel.add( q_readout );

    Box control_panel = new Box( BoxLayout.Y_AXIS );
    control_panel.add( controller );

    JTabbedPane tabbed_pane = new JTabbedPane();
    tabbed_pane.setFont( FontUtil.LABEL_FONT );

    Box view_controls = new Box(BoxLayout.Y_AXIS);    
    view_controls.add( q_range_control );
    view_controls.add( det_choice_control );
    view_controls.add( peak_threshold_slider );
    view_controls.add( contour_threshold_slider );
    view_controls.add( checkbox_panel );
    view_controls.add( table_panel );

    tabbed_pane.addTab( "View", view_controls );
    JPanel filler1 = new JPanel();
    filler1.setPreferredSize( new Dimension( 120, 2000 ) );
    view_controls.add( filler1 );

    Box plane_controls = new Box(BoxLayout.Y_AXIS);    
    plane_controls.add( q_panel );
    plane_controls.add( origin_vec );
    plane_controls.add( vec_1 );
    plane_controls.add( vec_2 );
    plane_controls.add( h_plane_ui );
    plane_controls.add( k_plane_ui );
    plane_controls.add( l_plane_ui );

    JButton write_matrix_file        = new JButton("Write Orientation Matrix");
    JButton read_matrix_file         = new JButton("Read Orientation Matrix");
    JButton clear_orientation_matrix = new JButton("Clear Orientation Matrix");
    JButton write_peaks_file = new JButton("Write Peak Data File");
    JPanel button_panel = new JPanel();
    button_panel.setLayout( new GridLayout(4,1) );
    button_panel.add( write_matrix_file );
    button_panel.add( read_matrix_file );
    button_panel.add( clear_orientation_matrix );
    button_panel.add( write_peaks_file );
    plane_controls.add( button_panel );
    tabbed_pane.addTab( "HKL-Planes", plane_controls );
    tabbed_pane.setSelectedIndex(0);
    control_panel.add( tabbed_pane );

    JPanel filler = new JPanel();
    filler.setPreferredSize( new Dimension( 120, 2000 ) );
    control_panel.add( filler );

    JPanel gl_container = new JPanel();
    gl_container.setLayout( new GridLayout(1,1) );
    gl_container.add( vec_Q_space.getDisplayComponent() );
    SplitPaneWithState split_pane =
                  new SplitPaneWithState( JSplitPane.HORIZONTAL_SPLIT,
                                          gl_container,
                                          control_panel,
                                          0.75f );

    colors = IndexColorMaker.getColorTable(
                             IndexColorMaker.HEATED_OBJECT_SCALE, 128 );
    rgb_colors = new float[ colors.length ][3];
    for ( int i = 0; i < colors.length; i++ )
    {
      rgb_colors[i][0] = colors[i].getRed()   / 255.0f;
      rgb_colors[i][1] = colors[i].getGreen() / 255.0f;
      rgb_colors[i][2] = colors[i].getBlue()  / 255.0f;
    }

    scene_f.getContentPane().add( split_pane );
    scene_f.setSize(970,750);

    // add listeners..........................
    show_iso_surface.addActionListener( new IsoSurfaceListener() );
    show_coverage.addActionListener( new DetectorCoverageListener() );
    show_integer_hkl.addActionListener( new IntegerHKLListener() );

    ViewControlListener c_listener = new ViewControlListener( vec_Q_space );
    controller.addActionListener( c_listener );

    calc_fft_button.addActionListener( new CalcFFTButtonHandler() );

    q_range_control.addActionListener( new Q_Range_Listener() );
    det_choice_control.addActionListener( new DetChoiceListener() );

    peak_threshold_slider.addChangeListener( new PeakThresholdScaleHandler() );
    vec_Q_space.getDisplayComponent().addMouseListener( 
                 new ViewMouseInputAdapter() );

    ReadoutListener listener = new ReadoutListener();
    origin_vec.addActionListener( listener );
    vec_1.addActionListener( listener );
    vec_2.addActionListener( listener );

    PlaneListener plane_listener = new PlaneListener();
    h_plane_ui.addActionListener( plane_listener );    
    k_plane_ui.addActionListener( plane_listener );    
    l_plane_ui.addActionListener( plane_listener );    

    write_matrix_file.addActionListener( new WriteMatrixFileListener() );
    read_matrix_file.addActionListener( new ReadMatrixFileListener() );
    clear_orientation_matrix.addActionListener( new ClearMatrixListener() );
    write_peaks_file.addActionListener( new WritePeaksFileListener() );

    vec_q_transformer = new Vector();
    data_sets = new Vector();
    all_peaks = new Vector();

    WindowShower.show( scene_f );
//    System.out.println("End default constructor for GL_RecipPlaneView");
  }


/* ----------------------------- loadCalibrations -------------------- */
  public void loadCalibrations( String file_name )
  {
    try 
    {
      TextFileReader tfr = new TextFileReader( file_name );
      calibrations = new Hashtable();
      tfr.read_line();
      boolean done = false;
      while ( !done && !tfr.end_of_data() )
      {
        int det_num = tfr.read_int();
        float det_A   = tfr.read_float();
        float det_D   = tfr.read_float();
        float l1      = tfr.read_float();
        float t0      = tfr.read_float();
        float x2cm    = tfr.read_float();
        float y2cm    = tfr.read_float();
        float xleft   = tfr.read_float();
        float ylower  = tfr.read_float();
        String calib_name = tfr.read_line();

        float calib[] = { det_A, det_D, l1, t0, x2cm, y2cm, xleft, ylower };
        Integer key = new Integer( det_num );
        if ( calibrations.get( key ) == null )    // new detector
        {
          System.out.println("Using calibration: " +calib_name + 
                             " for " + det_num );
          calibrations.put( key, calib );
        }
        else
          done = true;                           // just use first calibrations
      }
    }
    catch ( Exception e )
    {
      System.out.println("Exception reading calibration file is " + e );
      e.printStackTrace();
      calibrations = null;
    }

    if ( calibrations != null )
    {
      Enumeration e = calibrations.elements();
      while ( e.hasMoreElements() )
        LinearAlgebra.print( (float[])e.nextElement() );
    }
  }


  /* -------------------- applyCalibrations --------------------------- */

  public void applyCalibrations()
  {
     for ( int i = 0; i < data_sets.size(); i++ )
       applyCalibration( (DataSet)data_sets.elementAt(i) );
  }


  /* --------------------- applyCalibration ---------------------------- */

  private void applyCalibration( DataSet ds )
  {
    if ( calibrations == null )
    {
      System.out.println("No calibrations for DataSet " + ds );
      return;
    }
  
    int ids[] = Grid_util.getAreaGridIDs( ds );
    for ( int i = 0; i < ids.length; i++ )
    {
      UniformGrid grid = (UniformGrid)Grid_util.getAreaGrid( ds, ids[i] );
      float cal[] = (float[])calibrations.get( new Integer( ids[i] ) );
      if ( cal == null )
      {
        System.out.println("ERROR: No calibration for detector ID " + ids[i] );
        return;
      }
                                        // First adjust the grid according to
                                        // the calibration information
      int n_rows = grid.num_rows();
      int n_cols = grid.num_cols();
      
      float width  = n_cols * cal[4]/100;
      float height = n_rows * cal[5]/100;

      grid.setWidth( width );
      grid.setHeight( height );

      float xleft   = cal[6]/100;
      float ylower  = cal[7]/100;
      Vector3D base = grid.x_vec();
      Vector3D up   = grid.y_vec();  
      base.normalize();
      up.normalize();

      float xcenter = xleft  + width / 2;
      float ycenter = ylower + height / 2;
      Vector3D center = grid.position();

      if ( debug )
      {
        System.out.println("ORGINAL CENTER IS: " + center );
        System.out.println("Shift in X is : " + xcenter );
        System.out.println("Shift in Y is : " + ycenter );
      }

      base.multiply( xcenter );
      up.multiply( ycenter );
      center.add( base );
      center.add( up );
      grid.setCenter( center );

      if ( debug )
        System.out.println("NEW CENTER IS: " + center );
      
      Attribute T0_attribute = new FloatAttribute( Attribute.T0_SHIFT, cal[3] );
      Attribute l1_attribute = new FloatAttribute( Attribute.INITIAL_PATH, 
                                                   cal[2]/100 );
      for ( int row = 1; row <= n_rows; row++ )
        for ( int col = 1; col <= n_cols; col++ )
        {
          grid.getData_entry( row, col ).setAttribute( l1_attribute );
          grid.getData_entry( row, col ).setAttribute( T0_attribute );
        }

      if ( debug )
      {
        System.out.println ("GRID IS " );
        System.out.println ("" + grid );
      }
                                               // Finally, adjust the 
                                               // effective detector pixel
                                               // positions     
      Grid_util.setEffectivePositions( ds, ids[i] );
    }
  }


/* ------------------------- loadOrientationMatrix -------------------- */
  public void loadOrientationMatrix( String file_name )
  {
    float or_mat[][] = new float[3][3];
    try
    {
      TextFileReader tfr = new TextFileReader( file_name );
      or_mat = new float[3][3];

      for ( int col = 0; col < 3; col++ )
        for ( int row = 0; row < 3; row++ )
          or_mat[row][col] = tfr.read_float();
    }
    catch ( Exception e )
    {
      System.out.println("Exception reading orientation matrix is " + e );
      e.printStackTrace();
      orientation_matrix = null;
      orientation_matrix_inverse = null;
      return;
    }

/* // TEST CODE 
    double or_mat_d[][] = new double[3][3];
    for ( int i = 0; i < 3; i++ )
      for ( int j = 0; j < 3; j++ )
        or_mat_d[i][j] = or_mat[i][j];
    double lat_par[] = lattice_calc.LatticeParamsOfUB( or_mat_d );
    System.out.println("Lattice parameters = ");
    LinearAlgebra.print( lat_par );
*/

    for ( int i = 0; i < 3; i++ )
      for ( int j = 0; j < 3; j++ )
        or_mat[i][j] *= ((float)Math.PI * 2);

    orientation_matrix = new Tran3D( or_mat );

    orientation_matrix_inverse = new Tran3D( orientation_matrix );
    if ( !orientation_matrix_inverse.invert() )
    {
      System.out.println("ERROR...INVALID ORIENTATION MATRIX, NO INVERSE");
      System.out.println("Setting the orientation matrix and it's inverse ");
      System.out.println("to null.");
      orientation_matrix = null;
      orientation_matrix_inverse = null;
    }
  }


 /* ---------------------------- loadFiles --------------------------- */
  public boolean loadFiles()
  {
    System.out.println("Specified calibration file is : " + calib_file );
    if ( calib_file != null && calib_file.length() > 0 )
      loadCalibrations( calib_file );

    System.out.println("Specified orientation file is : " + orient_file );
    if ( orient_file != null && orient_file.length() > 0 )
      loadOrientationMatrix( orient_file );

    runs = IntList.ToArray( run_nums );
    String file_names[] = new String[ runs.length ];

    for ( int i = 0; i < runs.length; i++ )
     file_names[i] = path+InstrumentType.formIPNSFileName("scd",runs[i]);

    System.out.println("Loading all files....");
    RunfileRetriever rr;
    DataSet ds;
    global_obj_index = 0;
    for ( int count = 0; count < file_names.length; count++ )
    {
      System.out.println("Loading file: " + file_names[count]);
      rr = new RunfileRetriever( file_names[count] );
      ds = rr.getFirstDataSet( Retriever.HISTOGRAM_DATA_SET );
      rr = null;
      if ( ds == null )
      {
        System.out.println("File not found: " + file_names[count]);
        return false;
      }
      else
      {
        Attribute attr = ds.getAttribute( Attribute.RUN_TITLE );
        System.out.println("Loaded run: " + attr.toString() + " -------------");
        data_sets.addElement(ds);
      }
      ds = null;
    }

    applyCalibrations();

    System.out.println("DONE loading DataSets : " + data_sets.size() );
    return true;
  }


 /* ---------------------------- initialize ------------------------- */

  public void initialize( boolean extract_peaks )
  {
    makeVecQTransformers();

    if ( vec_q_transformer != null )
    {
      int[] id_array = new int[ vec_q_transformer.size() ];
      for ( int i = 0; i < vec_q_transformer.size(); i++ )
      {
        VecQToTOF transformer = (VecQToTOF)vec_q_transformer.elementAt(i);
        IDataGrid grid = transformer.getDataGrid();
        id_array[i] = grid.ID();
      }
      String id_list_string = IntList.ToString( id_array );
      det_choice_control.setListString( id_list_string );
    } 


    if ( orientation_matrix != null )         // add the orientation matrix
    {                                         // to all DataSets and add HKL op
      float temp[][] = orientation_matrix.get();
      float matrix[][] = new float[3][3];
      for ( int row = 0; row < 3; row++ )
        for ( int col = 0; col < 3; col++ )
          matrix[row][col] = (float)(temp[row][col] / (2.0*Math.PI));
     
      Attribute matrix_attr = 
                new Float2DAttribute( Attribute.ORIENT_MATRIX, matrix );
      for ( int i = 0; i < data_sets.size(); i++ )
      {
        DataSet ds = (DataSet)data_sets.elementAt(i);
        ds.setAttribute( matrix_attr );
        ds.addOperator( new SCDhkl() );
      }
    }

    if ( extract_peaks )
      ExtractPeaks();

    draw_Axes();

    Redraw();
  }


/* -------------------------- CalculateFFTs --------------------------- */

  public void CalculateFFTs()
  {
    int MIN_FFTS = 30;

    if ( debug )
      System.out.println("Projecting points...");

    projection_ds = ProjectPointsUniformly( all_vectors, 15 );

    if ( debug )
      System.out.println("DONE");
//  vm = new ViewManager( projection_ds, IViewManager.IMAGE );

    if ( debug )
      System.out.println("Doing FFT on all projections....");

    all_fft_ds = FFT( projection_ds );
    all_fft_ds.addIObserver( new FFTListener() );

    if ( debug )
      System.out.println("DONE");

//    new ViewManager( all_fft_ds, IViewManager.IMAGE );  // ########

    if ( debug )
      System.out.println("Filtering FFTs of all projections....");

    float threshold = 0.5f * LSQ_THRESHOLD;
    boolean done = false;
    while (threshold < 4 * LSQ_THRESHOLD && !done )
    {
      System.out.println("Filtering FFT using threshold = " + threshold );
      filtered_fft_ds = FilterFFTds( all_fft_ds, threshold );
      if ( filtered_fft_ds.getNum_entries() < MIN_FFTS )
      {
        threshold *= 1.4142135f;
        if ( debug )
        {
          System.out.println("WARNING: recalculating FFT since too few found");
          System.out.println("new threshold = " + threshold );
        }
      }
      else
        done = true;
    }

    filtered_fft_ds.addIObserver( new FFTListener() );
    if ( debug )
      System.out.println("DONE");
    new ViewManager( filtered_fft_ds, IViewManager.IMAGE );
  }


/* ------------------------ ShowBoundaries ----------------------------- */

  public void ShowBoundaries( boolean is_on )
  {
    if ( is_on )
    {
      for ( int i = 0; i < vec_q_transformer.size(); i++ )
      {
        GL_Shape bounds[] = getBoundaries( i );
        vec_Q_space.setObjects( BOUNDARY_OBJECTS+i, bounds );
      }
    }
    else
      for ( int i = 0; i < vec_q_transformer.size(); i++ )
        vec_Q_space.removeObjects( BOUNDARY_OBJECTS+i );
  }


/* ------------------------ ShowHKL_Marks ----------------------------- */

  public void ShowHKL_Marks( boolean is_on )
  {
    if ( is_on )
    {
      for ( int i = 0; i < vec_q_transformer.size(); i++ )
      {
        GL_Shape marks[] = getHKL_Marks( i );
        vec_Q_space.setObjects( MARK_OBJECTS+i, marks );
      }
    }
    else
      for ( int i = 0; i < vec_q_transformer.size(); i++ )
        vec_Q_space.removeObjects( MARK_OBJECTS+i );
  }


/* ------------------------ ShowContours ----------------------------- */

  public void ShowContours( boolean is_on, float level )
  {
    if ( is_on )
    {
      for ( int i = 0; i < vec_q_transformer.size(); i++ )
      {
        GL_Shape contours[] = getContours( i, level );
        vec_Q_space.setObjects( CONTOUR_OBJECTS+i, contours );
      }
    }
    else
      for ( int i = 0; i < vec_q_transformer.size(); i++ )
        vec_Q_space.removeObjects( CONTOUR_OBJECTS+i );
  }


/* ------------------------ SetThresholdScale ---------------------- */

  public void SetThresholdScale( int value )
  {
    peak_threshold = Math.abs( value );
    peak_threshold_slider.setValue(value);
  }


/* ---------------------------------------------------------------------
 *
 *  PRIVATE METHODS
 *
 */

/* --------------------------- getHKL_extent --------------------------- */

  private Vector3D[] getHKL_extent( int index )
  {
    if ( orientation_matrix == null )
    {
      System.out.println("NO ORIENTATION MATRIX LOADED");
      return null;
    }

    VecQToTOF transformer = (VecQToTOF)vec_q_transformer.elementAt(index);
    IDataGrid grid = transformer.getDataGrid();

    Data d = grid.getData_entry(1,1);
    float initial_path = 9.378f;
    Attribute attr = d.getAttribute(Attribute.INITIAL_PATH);
    if ( attr != null )
      initial_path = (float)attr.getNumericValue();

    float t0 = 0;
    attr = d.getAttribute(Attribute.T0_SHIFT);
    if ( attr != null )
      t0 = (float)attr.getNumericValue();

    d = grid.getData_entry(1,1);
    float xs[] = d.getX_scale().getXs();
    float t_min = xs[0];
    float t_max = xs[ xs.length - 1 ];

    Tran3D combinedR = transformer.getGoniometerRotationInverse();

    Tran3D inverse = new Tran3D( orientation_matrix );
    if ( !inverse.invert() )
    {
      System.out.println("COULDN'T INVERT THE ORIENTATION MATRIX");
      return null;
    }

    Vector3D new_result[] = SCD_util.DetectorToMinMaxHKL( grid, 
                                                          initial_path,
                                                          t_min + t0,
                                                          t_max + t0,
                                                          combinedR,
                                                          inverse );
    return new_result;
  }


/* ---------------------------- getHKL_Marks --------------------------- */

  private GL_Shape[] getHKL_Marks( int index )
  {
    if ( orientation_matrix == null )
    {
      System.out.println("NO ORIENTATION MATRIX LOADED");
      return null;
    }

    Vector3D  hkl_min_max[] = getHKL_extent( index );

    if ( hkl_min_max == null || hkl_min_max.length == 0 )
    {
      System.out.println("INVALID HKL EXTENT....");
      return null;
    }

    Vector3D  start,
              end;
    Vector3D  h_step = new Vector3D(0.05f,0,0);
    Vector3D  k_step = new Vector3D(0,0.05f,0);
    Vector3D  l_step = new Vector3D(0,0,0.05f);
    orientation_matrix.apply_to( h_step, h_step );
    orientation_matrix.apply_to( k_step, k_step );
    orientation_matrix.apply_to( l_step, l_step );

    Vector h_line_list = new Vector(10000);
    Vector k_line_list = new Vector(10000);
    Vector l_line_list = new Vector(10000);
    VecQToTOF transformer = (VecQToTOF)vec_q_transformer.elementAt(index);
    IDataGrid grid = transformer.getDataGrid();
    Data d = grid.getData_entry(1,1);
    float x_vals[] = d.getX_values();
    float min[] = hkl_min_max[0].get();
    float max[] = hkl_min_max[1].get();
    float rctof[];
    Vector3D point = new Vector3D(); 
/*
    for ( int h = (int)min[0]; h <= (int)max[0]; h++ )
      for ( int k = (int)min[1]; k <= (int)max[1]; k++ )
        for ( int l = (int)min[2]; l <= (int)max[2]; l++ )
*/
    for ( int h = -10; h <= 10; h++ )       // Temporarily just use fixed
      for ( int k = -10; k <= 10; k++ )     // range.  ARCS covered too
        for ( int l = -10; l <= 10; l++ )   // much volume and took too long.
        {
           point.set( h, k, l ); 
           orientation_matrix.apply_to( point, point );
           rctof = transformer.QtoRowColTOF( point );    // check if in data
           if ( rctof != null           && 
                rctof[2] >= x_vals[0]   &&
                rctof[2] <= x_vals[ x_vals.length - 1 ] )
           {
             start = new Vector3D( point );
             start.subtract( h_step );
             end = new Vector3D( point );
             end.add( h_step );
             h_line_list.add( start );
             h_line_list.add( end );

             start = new Vector3D( point );
             start.subtract( k_step );
             end = new Vector3D( point );
             end.add( k_step );
             k_line_list.add( start );
             k_line_list.add( end );

             start = new Vector3D( point );
             start.subtract( l_step );
             end = new Vector3D( point );
             end.add( l_step );
             l_line_list.add( start );
             l_line_list.add( end );
          } 
        }

     if ( h_line_list.size() > 0 )
     {
       int n_points = h_line_list.size()/2;

       Vector3D start_vec[] = new Vector3D[ n_points ];
       Vector3D end_vec[] = new Vector3D[ n_points ];
       for ( int i = 0; i < n_points; i++ )
       {
         start_vec[i] = (Vector3D)h_line_list.elementAt( 2*i );
         end_vec[i] = (Vector3D)h_line_list.elementAt( 2*i + 1 );
       }
       Lines h_lines = new Lines( vec_Q_space, start_vec, end_vec );
       h_lines.setColor( RED );

       for ( int i = 0; i < n_points; i++ )
       {
         start_vec[i] = (Vector3D)k_line_list.elementAt( 2*i );
         end_vec[i] = (Vector3D)k_line_list.elementAt( 2*i + 1 );
       }
       Lines k_lines = new Lines( vec_Q_space, start_vec, end_vec );
       k_lines.setColor( GREEN );

       for ( int i = 0; i < n_points; i++ )
       {
         start_vec[i] = (Vector3D)l_line_list.elementAt( 2*i );
         end_vec[i] = (Vector3D)l_line_list.elementAt( 2*i + 1 );
       }
       Lines l_lines = new Lines( vec_Q_space, start_vec, end_vec );
       l_lines.setColor( BLUE );

       GL_Shape[] result = new GL_Shape[3]; 
       result[0] = h_lines;
       result[1] = k_lines;
       result[2] = l_lines;
       return result;
     }
     else 
       return null;
  } 


/* ------------------------ ExtractPeaks --------------------------- */

  private void ExtractPeaks()
  {
    if ( vec_q_transformer == null || vec_q_transformer.size() <= 0 )
    {
      System.out.println("ExtractPeaks called, but Data not available yet");
      return;
    }
//    if ( debug )
    {
      System.out.println("Applying threshold to extract peaks above " +
                         peak_threshold );
      System.out.println("in specified Q range.....");

      System.out.println("There are " + vec_q_transformer.size() + " grids");
    }

    Hashtable id_table = new Hashtable();   // use to check if the current grid
                                            // should be used.
    int id_array[] = det_choice_control.getList();
    for ( int i = 0; i < id_array.length; i++ )
      id_table.put( new Integer(id_array[i]), new Integer(id_array[i]) );
 
    all_peaks = new Vector();
    for ( int i = 0; i < vec_q_transformer.size(); i++ )
    {
      VecQToTOF transformer = (VecQToTOF)vec_q_transformer.elementAt(i);
      IDataGrid grid = transformer.getDataGrid();
      int id = grid.ID();
      if ( id_table.get( new Integer(id) ) != null )
      {
        GL_Shape non_zero_objs[] = getPeaks( i, peak_threshold );
        vec_Q_space.setObjects( PEAK_OBJECTS+i, non_zero_objs);
        System.out.println("Found peaks : " + non_zero_objs.length );
      }
      else
        vec_Q_space.removeObjects( PEAK_OBJECTS+i );
    }
                                       // initialize the list of all q vectors
                                       // and the QR factorization
    all_vectors = get_data_points();
    makeQR_factors();

    System.out.println("DONE");
  }

 
  /* ---------------------- makeVecQTransformers --------------------- */

  private void makeVecQTransformers()
  {
     System.out.println("Start makeVecQTransformers " );
     vec_q_transformer = new Vector();
     ds_of_transformer = new Vector();
     for ( int index = 0; index < data_sets.size(); index++ )
     {
        System.out.println("Making VecQTransformer for DS #" + index); 
        DataSet ds = (DataSet)data_sets.elementAt(index);
        try
        {
          Hashtable grid_hash = Grid_util.getAllDataGrids( ds );
          if ( grid_hash != null )
            System.out.println("Got hashtable of grids, size = " + 
                                grid_hash.size() );
          else
            System.out.println("ERROR no grids found!");

          Enumeration e = grid_hash.elements();
          while ( e.hasMoreElements() )
          {
            IDataGrid grid = (IDataGrid)e.nextElement();
            VecQToTOF transformer = new VecQToTOF( ds, grid );
            if ( transformer != null )
              System.out.println("Got transformer OK");
            else
              System.out.println("Failed to get VecQ tranformer");
            vec_q_transformer.add( transformer );
            ds_of_transformer.add( ds );
          }
        }
        catch (InstantiationError e )
        {
          if ( debug )
          {
            System.out.println( "Error finding transformer for DataSet " + ds );
            System.out.println( e );
          }
        }
     }
  }


  /* ------------------------- getPeaks ---------------------------- */
  /*
   *  Get an array of peaks from the specified grid, based on the specified
   *  threshold scale factor.
   *
   *  @param index            The index of the DataGrid in the list. 
   *  @param peak_threshold   The absolute threshold in counts.
   *
   *  @return an array of ThreeD_Objects representing the points above the
   *                      threshold.
   */
  private GL_Shape[] getPeaks( int index, float peak_threshold )
  {
      Data  d;
      float t;
      float ys[];
      float times[];
      float cart_coords[];
      Position3D q_pos;
      float c[];
      GL_Shape objs[] = null;
      Vector3D pts[] = new Vector3D[1];
      pts[0]         = new Vector3D();
      Attribute attr;
                                            // Assume all runs have the same
                                            // number of detectors in them
                                            // and each detector has a grid.
      int dets_per_run = vec_q_transformer.size()/runs.length; 
      int run_num_index = index/dets_per_run;

      int obj_index = 0;
      VecQToTOF transformer = (VecQToTOF)vec_q_transformer.elementAt(index);
      IDataGrid grid = transformer.getDataGrid();
      d = grid.getData_entry(1,1);

      SampleOrientation orientation = transformer.getSampleOrientation();

      float initial_path = 9.378f; 
      attr = d.getAttribute(Attribute.INITIAL_PATH);
      if ( attr != null )
        initial_path = (float)attr.getNumericValue();

      float t0 = 0; 
      attr = d.getAttribute(Attribute.T0_SHIFT);
      if ( attr != null )
        t0 = (float)attr.getNumericValue();

      int n_bins = d.getX_scale().getNum_x() - 1;
      int n_objects = grid.num_rows() * grid.num_cols() * n_bins;
      objs = new GL_Shape[n_objects];

      Tran3D combinedR = transformer.getGoniometerRotationInverse();

      if ( edge_pix > grid.num_rows() / 3 )   // can't discard more than 1/3
        edge_pix = grid.num_rows() / 3;       // of the rows and columns

      if ( edge_pix > grid.num_cols() / 3 )   // can't discard more than 1/3
        edge_pix = grid.num_cols() / 3;       // of the rows and columns

      if ( debug )
        System.out.println("Discarding " + edge_pix + " edge rows and columns");

//    float base_levels[] = getBaseLevels( grid, 10 );
      for ( int row = 1+edge_pix; row <= grid.num_rows()-edge_pix; row++ )
        for ( int col = 1+edge_pix; col <= grid.num_cols()-edge_pix; col++ )
        {
          d = grid.getData_entry(row,col);
          Vector3D pos_vec = grid.position(row,col);
          DetectorPosition pos = new DetectorPosition( pos_vec );
          times = d.getX_scale().getXs();
          ys    = d.getY_values();
          for ( int j = 0; j < ys.length; j++ )
          {
//          if ( ys[j] > (peak_threshold / 10) * base_levels[j] )
            if ( ys[j] > peak_threshold )
            {
              t = (times[j] + times[j+1]) / 2;     // shift by calibrated T)
              q_pos = tof_calc.DiffractometerVecQ(pos,initial_path, t + t0 );

              cart_coords = q_pos.getCartesianCoords();
              pts[0].set( cart_coords[0], cart_coords[1], cart_coords[2] );
              combinedR.apply_to( pts[0], pts[0] );

              float mag_Q = pts[0].length();
              if ( keep_peak(pts[0]) && mag_Q >= min_Q_threshold &&
                                        mag_Q <= max_Q_threshold )
              {
                int color_index = (int)( ys[j] * 30 / peak_threshold );
                if ( color_index > 127 )
                  color_index = 127;
                c = rgb_colors[ color_index ];

                objs[ obj_index ] = getVoxel( grid, row, col, times, j, 
                                              t0, combinedR, initial_path );

                objs[ obj_index ].setColor( c );
                objs[ obj_index ].setLighting( true );

                objs[obj_index].setPickID( global_obj_index );
                obj_index++;
                global_obj_index++;

                float chan = j + 0.5f;
                Peak_new pk_new = new Peak_new( runs[run_num_index], 
                                                0, 
                                                col, 
                                                row, 
                                                chan, 
                                                grid, 
                                                orientation,  
                                                t + t0,
                                                initial_path, 
                                                t0 );
                pk_new.ipkobs( (int)ys[j] );
                all_peaks.add( pk_new );
              }
            }
          }
        }
      GL_Shape non_zero_objs[] = new GL_Shape[obj_index];
      for ( int i = 0; i < obj_index; i++ )
        non_zero_objs[i] = objs[i];

      return non_zero_objs;
  }


  /* -------------------------- getQ ---------------------------------- */
  /*
   *  Calculate the Q vector for the specified position and TOF
   */
  private Vector3D getQ( Tran3D   combinedR,
                         Vector3D pos_vec,
                         float    tof,
                         float    initial_path )
  {
     DetectorPosition pos = new DetectorPosition( pos_vec );
     Position3D q_pos = tof_calc.DiffractometerVecQ( pos, initial_path, tof );
     Vector3D q_vec  = new Vector3D( q_pos );
     combinedR.apply_to( q_vec, q_vec );
     return q_vec;
  }


  /* ------------------------------ getVoxel ----------------------------- */
  /*
   *  Calculate the voxel for this the specified row, col, time
   */
  private GL_Shape getVoxel( IDataGrid grid, 
                             int       row,
                             int       col,
                             float     times[],
                             int       index,
                             float     t0,
                             Tran3D    combinedR,
                             float     initial_path )
  {
      float first_t = times[index  ] + t0;
      float last_t  = times[index+1] + t0;
      Vector3D p00 = grid.position( row - 0.5f, col - 0.5f );
      Vector3D p01 = grid.position( row - 0.5f, col + 0.5f );
      Vector3D p11 = grid.position( row + 0.5f, col + 0.5f );
      Vector3D p10 = grid.position( row + 0.5f, col - 0.5f );

      Vector3D corner[][][] = new Vector3D[2][2][2];
      corner[0][0][0] = getQ( combinedR, p00, first_t, initial_path );
      corner[0][1][0] = getQ( combinedR, p10, first_t, initial_path );
      corner[0][1][1] = getQ( combinedR, p11, first_t, initial_path );
      corner[0][0][1] = getQ( combinedR, p01, first_t, initial_path );
      corner[1][0][0] = getQ( combinedR, p00, last_t, initial_path );
      corner[1][1][0] = getQ( combinedR, p10, last_t, initial_path );
      corner[1][1][1] = getQ( combinedR, p11, last_t, initial_path );
      corner[1][0][1] = getQ( combinedR, p01, last_t, initial_path );

      Voxel region = new Voxel( vec_Q_space, corner );
      return region;
  }


  /* ---------------------------- getContours ---------------------------- */
  /*
   *  Get list of contour lines om the specified grid, based on the specified
   *  threshold scale factor.
   *
   *  @param index   The index of the DataGrid in the list. 
   *  @param level   The intensity level for which the contour lines are drawn.
   *
   *  @return an array of ThreeD_Objects representing the iso-surfaces the
   *          the specified level. 
   */
  private GL_Shape[] getContours( int index, float level )
  {
      Data  d;
      Vector3D pts[] = new Vector3D[1];
      pts[0]         = new Vector3D();
      Attribute attr;
                                            // Assume all runs have the same
                                            // number of detectors in them
                                            // and each detector has a grid.
      VecQToTOF transformer = (VecQToTOF)vec_q_transformer.elementAt(index);
      IDataGrid grid = transformer.getDataGrid();
      d = grid.getData_entry(1,1);

      float initial_path = 9.378f;
      attr = d.getAttribute(Attribute.INITIAL_PATH);
      if ( attr != null )
        initial_path = (float)attr.getNumericValue();

      float t0 = 0;
      attr = d.getAttribute(Attribute.T0_SHIFT);
      if ( attr != null )
        t0 = (float)attr.getNumericValue();

      Tran3D combinedR = transformer.getGoniometerRotationInverse();

      GL_Shape result[] = new GL_Shape[3];
      result[0] = getTimeContours( grid, combinedR, initial_path, t0, level );
      result[1] = getRowContours( grid, combinedR, initial_path, t0, level );
      result[2] = getColContours( grid, combinedR, initial_path, t0, level );

     return result;
  }


  /* ------------------------ getTimeContours ------------------------- */
  /*
   *  Get Lines object containing contour lines with constant TOF value
   */
  private GL_Shape getTimeContours( IDataGrid grid,
                                    Tran3D    combinedR,
                                    float     initial_path,
                                    float     t0,
                                    float     level     )
  {
    Data d = grid.getData_entry(1,1);

    float times[] = d.getX_scale().getXs();
    int   n_tbins  = d.getY_values().length;
    float t;

    float arr[][] = 
      new float[ grid.num_rows() - 2*edge_pix ][ grid.num_cols() - 2*edge_pix ];
    Vector contours;
    Vector start = new Vector();
    Vector end   = new Vector();
    for ( int j = 0; j < n_tbins-1; j++ )
    {                               // make array at this time slice using
                                    // values inside of the edge_pix border
      for ( int row = 1+edge_pix; row <= grid.num_rows()-edge_pix; row++ )
        for ( int col = 1+edge_pix; col <= grid.num_cols()-edge_pix; col++ )
          arr[row-1-edge_pix][col-1-edge_pix] = 
                             grid.getData_entry(row,col).getY_values()[j];

      contours = Contour2D.contour( arr, level );
      t = (times[j] + times[j+1])/2;
      for ( int i = 0; i < contours.size()/2; i++ )
      {
        floatPoint2D p1 = (floatPoint2D)contours.elementAt( 2*i );
        Vector3D pos_vec  = grid.position( 1+p1.y+edge_pix, 1+p1.x+edge_pix );
        Vector3D q_vec = getQ( combinedR, pos_vec, t + t0, initial_path );
        start.add( new Vector3D(q_vec) );

        floatPoint2D p2 = (floatPoint2D)contours.elementAt( 2*i + 1 );
        pos_vec  = grid.position( 1+p2.y+edge_pix, 1+p2.x+edge_pix );
        q_vec = getQ( combinedR, pos_vec, t + t0, initial_path );
        end.add( new Vector3D(q_vec) );
      }
    }

    Vector3D start_vec[] = new Vector3D[ start.size() ];
    Vector3D end_vec[]   = new Vector3D[ end.size() ];
    for ( int i = 0; i < start_vec.length; i++ )
    {
      start_vec[i] = (Vector3D)start.elementAt(i);
      end_vec[i]   = (Vector3D)end.elementAt(i);
    }
    Lines lines = new Lines( vec_Q_space, start_vec, end_vec );
    lines.setColor( GRAY );
    return lines;
  }

  /* ------------------------ getRowContours ------------------------- */
  /*
   *  Get Lines object containing contour lines with constant row value
   */
  private GL_Shape getRowContours( IDataGrid grid,
                                   Tran3D    combinedR,
                                   float     initial_path,
                                   float     t0,
                                   float     level    )
  {
    Data d = grid.getData_entry(1,1);

    float times[] = d.getX_scale().getXs();
    int   n_tbins = d.getY_values().length;
    float fract,
          t;
    int   t_index;

    float arr[][] = new float[ grid.num_cols()-edge_pix ][ n_tbins ];
    Vector contours;
    Vector start = new Vector();
    Vector end   = new Vector();
                                        // only make contours for rows inside
                                        // border that is edge_pix wide. 
    for ( int row = 1+edge_pix; row <= grid.num_rows()-edge_pix; row++ )
    {
                                        // copy data from each col in array
                                        // array arr[] with shifted col index
      for ( int col = 1+edge_pix; col <= grid.num_cols()-edge_pix; col++ )
        arr[col-1-edge_pix] = grid.getData_entry(row,col).getY_values();

      contours = Contour2D.contour( arr, level );
      for ( int i = 0; i < contours.size()/2; i++ )
      {
        floatPoint2D p1 = (floatPoint2D)contours.elementAt( 2*i );
        Vector3D pos_vec  = grid.position( row, 1+p1.y+edge_pix );
        t_index = (int)p1.x;
        fract = p1.x - t_index;
        t = times[t_index] + fract * (times[t_index+1] - times[t_index]);
        Vector3D q_vec = getQ( combinedR, pos_vec, t + t0, initial_path );
        start.add( new Vector3D(q_vec) );

        floatPoint2D p2 = (floatPoint2D)contours.elementAt( 2*i + 1 );
        pos_vec  = grid.position( row, 1+p2.y+edge_pix );
        t_index = (int)p2.x;
        fract = p2.x - t_index;
        t = times[t_index] + fract * (times[t_index+1] - times[t_index]);
        q_vec = getQ( combinedR, pos_vec, t + t0, initial_path );
        end.add( new Vector3D(q_vec) );
      }
    }

    Vector3D start_vec[] = new Vector3D[ start.size() ];
    Vector3D end_vec[]   = new Vector3D[ end.size() ];
    for ( int i = 0; i < start_vec.length; i++ )
    {
      start_vec[i] = (Vector3D)start.elementAt(i);
      end_vec[i]   = (Vector3D)end.elementAt(i);
    }
    Lines lines = new Lines( vec_Q_space, start_vec, end_vec );
    lines.setColor( GRAY );
    return lines;
  }


  /* ------------------------ getColContours ------------------------- */
  /*
   *  Get Lines object containing contour lines with constant column value
   */
  private GL_Shape getColContours( IDataGrid grid,
                                   Tran3D    combinedR,
                                   float     initial_path,
                                   float     t0,
                                   float     level  )
  {
    Data d = grid.getData_entry(1,1);

    float times[] = d.getX_scale().getXs();
    int   n_tbins = d.getY_values().length;
    float fract,
          t;
    int   t_index;

    float arr[][] = new float[ grid.num_rows()-edge_pix ][ n_tbins ];
    Vector contours;
    Vector start = new Vector();
    Vector end   = new Vector();
                                        // only make contours for cols inside
                                        // border that is edge_pix wide.  
    for ( int col = 1+edge_pix; col <= grid.num_cols()-edge_pix; col++ )
    {
                                        // copy data from each row in array
                                        // array arr[] with shifted row index
      for ( int row = 1+edge_pix; row <= grid.num_rows()-edge_pix; row++ )
        arr[row-1-edge_pix] = grid.getData_entry(row,col).getY_values();

      contours = Contour2D.contour( arr, level );
      for ( int i = 0; i < contours.size()/2; i++ )
      {
        floatPoint2D p1 = (floatPoint2D)contours.elementAt( 2*i );
        Vector3D pos_vec  = grid.position( 1+p1.y+edge_pix, col );
        t_index = (int)p1.x;
        fract = p1.x - t_index;
        t = times[t_index] + fract * (times[t_index+1] - times[t_index]);
        Vector3D q_vec = getQ( combinedR, pos_vec, t + t0, initial_path );
        start.add( new Vector3D(q_vec) );

        floatPoint2D p2 = (floatPoint2D)contours.elementAt( 2*i + 1 );
        pos_vec  = grid.position( 1+p2.y+edge_pix, col );
        t_index = (int)p2.x;
        fract = p2.x - t_index;
        t = times[t_index] + fract * (times[t_index+1] - times[t_index]);
        q_vec = getQ( combinedR, pos_vec, t + t0, initial_path );
        end.add( new Vector3D(q_vec) );
      }
    }

    Vector3D start_vec[] = new Vector3D[ start.size() ];
    Vector3D end_vec[]   = new Vector3D[ end.size() ];
    for ( int i = 0; i < start_vec.length; i++ )
    {
      start_vec[i] = (Vector3D)start.elementAt(i);
      end_vec[i]   = (Vector3D)end.elementAt(i);
    }
    Lines lines = new Lines( vec_Q_space, start_vec, end_vec );
    lines.setColor( GRAY );
    return lines;
  }


  /* ---------------------------- getBoundaries ---------------------------- */
  /*
   *  Get outline of region of Q covered by the specified grid.
   *
   *  @param index   The index of the DataGrid in the list. 
   *
   *  @return an array of ThreeD_Objects representing the region covered.
   */
  private GL_Shape[] getBoundaries( int index )
  {
      Data  d;
      Vector3D pts[] = new Vector3D[1];
      pts[0]         = new Vector3D();
      Attribute attr;
                                            // Assume all runs have the same
                                            // number of detectors in them
                                            // and each detector has a grid.
      VecQToTOF transformer = (VecQToTOF)vec_q_transformer.elementAt(index);
      DataSet   ds          = (DataSet)ds_of_transformer.elementAt(index);

      IDataGrid grid = transformer.getDataGrid();
      d = grid.getData_entry(1,1);

      float initial_path = 9.378f;
      attr = d.getAttribute(Attribute.INITIAL_PATH);
      if ( attr != null )
        initial_path = (float)attr.getNumericValue();

      float t0 = 0;
      attr = d.getAttribute(Attribute.T0_SHIFT);
      if ( attr != null )
        t0 = (float)attr.getNumericValue();

      Tran3D combinedR = transformer.getGoniometerRotationInverse();

                                                   // make a label for the
                                                   // region from run & det ID
      String label = "";

      int run_list[] = AttrUtil.getRunNumber( ds );
      if ( run_list == null )
        run_list = AttrUtil.getRunNumber( d );

      if ( run_list != null )
        label = IntList.ToString( run_list );

      label += " : " + grid.ID();

      GL_Shape result[];
      result = GetRegion( grid, combinedR, initial_path, t0, label );

     return result;
  }


  /*
   *  Get Lines object containing contour lines with constant column value
   */
  private GL_Shape[] GetRegion( IDataGrid grid,
                                Tran3D    combinedR,
                                float     initial_path,
                                float     t0,
                                String    label     )
  {
    GL_Shape boundaries[] = new GL_Shape[4];  // will contain edges, outer face,
                                              // inner face and labels
    Data d = grid.getData_entry(1,1);

    float times[] = d.getX_scale().getXs();
    int   n_tbins  = d.getY_values().length;
    float t_start = times[0];
    float t_end   = times[n_tbins-1]; 
    int   n_rows = grid.num_rows();
    int   n_cols = grid.num_cols();
                                                          // edge lines
    Vector3D corners[] = { grid.position(      1, 1 ),
                           grid.position( n_rows, 1 ),
                           grid.position( n_rows, n_cols ),
                           grid.position(      1, n_cols ) };

    Vector3D start[] = new Vector3D[4];
    Vector3D end[]   = new Vector3D[4];
    for ( int i = 0; i < 4; i++ )
    {
      start[i] = getQ( combinedR, corners[i], t_start + t0, initial_path );
      end[i]   = getQ( combinedR, corners[i], t_end   + t0, initial_path );
    }

    boundaries[0] = new Lines( vec_Q_space, start, end );

    Vector3D points[] = new Vector3D[2*n_rows + 2*n_cols - 3];   // outer face
    int index = 0;                                               // at t min
    for ( int col = 1; col <= n_cols; col++ )
    {
      points[index] = 
           getQ( combinedR, grid.position(1,col), t_start + t0, initial_path );
      index++;
    }

    for ( int row = 2; row <= n_rows; row++ )
    {
      points[index] = 
          getQ(combinedR, grid.position(row,n_cols), t_start+t0, initial_path);
      index++;
    }

    for ( int col = n_cols-1; col >= 1; col-- )
    {
      points[index] = 
          getQ(combinedR, grid.position(n_rows,col), t_start+t0, initial_path);
      index++;
    }

    for ( int row = n_rows-1; row >= 1; row-- )
    {
      points[index] = 
          getQ(combinedR, grid.position(row,1), t_start+t0, initial_path);
      index++;
    }

    boundaries[1] = new LineStrip( vec_Q_space, points ); 
                                                        // inner face at t_max
    index = 0;
    for ( int col = 1; col <= n_cols; col++ )
    {
      points[index] = 
           getQ( combinedR, grid.position(1,col), t_end + t0, initial_path );
      index++;
    }

    for ( int row = 2; row <= n_rows; row++ )
    {
      points[index] =
          getQ(combinedR, grid.position(row,n_cols), t_end+t0, initial_path);
      index++;
    }

    for ( int col = n_cols-1; col >= 1; col-- )
    {
      points[index] =
          getQ(combinedR, grid.position(n_rows,col), t_end+t0, initial_path);
      index++;
    }
    
    for ( int row = n_rows-1; row >= 1; row-- )
    {
      points[index] =
          getQ(combinedR, grid.position(row,1), t_end+t0, initial_path);
      index++;
    }

    boundaries[2] = new LineStrip( vec_Q_space, points );

                                                   // set label for region 
    Vector3D text_pt = getQ( combinedR,
                             grid.position(),
                             t_start+t0,
                             initial_path );

    Vector3D base = new Vector3D( 1, 0, 0 );       // calculate small row and
    Vector3D up   = new Vector3D( 0, 1, 0 );       // column change vectors
    if ( n_rows > 1 && n_cols > 1 )                // relative to center, then
    {                                              // map to Q to get base & up
      base = new Vector3D( grid.position( 1, n_cols ) );
      base.subtract( new Vector3D( grid.position( 1, 1 ) ) );
      base.multiply( 1.0f/n_cols );
      base.add( grid.position() );
      up = new Vector3D( grid.position( n_rows, 1 ) );
      up.subtract( new Vector3D( grid.position( 1, 1 ) ) );
      up.multiply( 1.0f/n_rows );
      up.add( grid.position() );
    }
    base = getQ( combinedR, base, t_start+t0, initial_path );
    base.subtract( text_pt );
    base.multiply( -1 );
    up   = getQ( combinedR, up,   t_start+t0, initial_path );
    up.subtract( text_pt );
    if ( up.dot( new Vector3D(0,0,1) ) < 0 )         // flip the text to make
    {                                                // it's up vector be 
      base.multiply( -1 );                           // generally in z direction
      up.multiply( -1 );
    }
                                                     // make the label centered 
    StrokeFont font = new RomanComplex();            // on back face of region
                                                     // with up toward det top.
    StrokeText text = new StrokeText( vec_Q_space, label, font ); 

    text.setPosition( text_pt );
    text.setOrientation( base, up );
    text.setAlignment( StrokeText.HORIZ_CENTER, StrokeText.VERT_HALF );
    boundaries[3] = text;

    for ( int i = 0; i < boundaries.length; i++ )
      boundaries[i].setColor( GRAY );

    text.setColor( LIGHTGRAY );                      // make the label lighter

    return boundaries;
  }


  /* ---------------------- getBaseLevels -------------------------- */
  /*
   *  This method calculates threshold levels that vary with TOF, by
   *  approximating the median value of the measurements in each time
   *  slice.  NOTE: It seems that simple constant value thresholds actually 
   *  work better than this.   D.M.
   */
/*  
  private float[] getBaseLevels( IDataGrid grid, int width )
  {
    width = 10;
    int border = 10;

    if ( width > grid.num_rows()/2 - 2 )
      width = grid.num_rows()/2 - 2;

    if ( width > grid.num_cols()/2 - 2 )
      width = grid.num_cols()/2 - 2;

    Data d = grid.getData_entry(1,1);
    if ( d == null )
      return null;

    int   n_bins   = d.getY_values().length;
    int   n_rows   = grid.num_rows();
    int   n_cols   = grid.num_cols();
    float levels[] = new float[ n_bins ]; 
                                             // use median of values along a
                                             // horizontal and vertical cut
                                             // through the detector center
    float sort_list[] = new float[ n_rows + n_cols ];
    int offset;
    for ( int i = 0; i < n_bins; i++ )
    { 
      for ( int row = 1 + border; row < grid.num_rows() - border; row++ )
      {
        sort_list[row-1] = 0; 
        for ( int w = -width; w <= width; w++ )
          sort_list[row-1] += 
                     grid.getData_entry( row, n_cols/2 + w ).getY_values()[i];
      }

      offset = grid.num_rows() - 1;
      for ( int col = 1 + border; col < grid.num_cols() - border; col++ )
      {
        sort_list[ col + offset ] = 0;
        for ( int w = -width; w <= width; w++ )
          sort_list[ col + offset ] += 
                   grid.getData_entry( n_rows/2 + w, col ).getY_values()[i];
      }

      java.util.Arrays.sort( sort_list );

      levels[i] = sort_list[ sort_list.length/2 ] / (2*width + 1);
      levels[i] = levels[i] + 5*(float)Math.sqrt( levels[i] );
//    System.out.print( " "+levels[i] );
    }

    return levels;
  }
*/


  /* ----------------------- get_data_points ---------------------------- */
  private Vector3D[] get_data_points()
  {
    Vector3D all_vectors[] = new Vector3D[ all_peaks.size() ];
    Peak_new pk_new;
    for ( int i = 0; i < all_vectors.length; i++ )
    {
      pk_new = (Peak_new)all_peaks.elementAt(i);
      all_vectors[i] = new Vector3D( pk_new.getUnrotQ() );
      all_vectors[i].multiply((float)(2*Math.PI));  //#### work around Peak_new
                                                    // using q/(2*PI)
    }

    return all_vectors;
  }


  /* -------------------------- ProjectPoints ----------------------- */
 
  private Data ProjectPoints( Vector3D points[], Vector3D normal, int id )
  {
    float dist;
    int   bin;
    float y[] = new float[FFT_DATA_LENGTH];
    XScale scale = new UniformXScale( -SLICE_SIZE_IN_Q,
                                       SLICE_SIZE_IN_Q,
                                       FFT_DATA_LENGTH + 1 );

    Plane3D plane = new Plane3D();
    plane.set( normal, 0 );
    for ( int i = 0; i < points.length; i++ )
    {
      dist = plane.getDistance( points[i] );
      if ( Math.abs(dist) < SLICE_SIZE_IN_Q )          // otherwise, clip it
      {
        bin = Math.round( (FFT_DATA_LENGTH/2-1) * dist / SLICE_SIZE_IN_Q ) +
                           FFT_DATA_LENGTH/2;
        y[bin]++;
      }
    }
    Data d = Data.getInstance( scale, y, id );
    d.setAttribute( new Float1DAttribute( NORMAL_ATTRIBUTE, normal.get() ));

    return d;
  }


/* --------------------------- ProjectPointsUniformly --------------------- */

  private DataSet ProjectPointsUniformly( Vector3D points[], int n_steps )
  {
    DataSetFactory ds_factory = new DataSetFactory(
                                       "Projection parallel to planes" );
    DataSet ds = ds_factory.getDataSet();
    Vector3D normal;
    float    components[] = new float[3];
    Vector3D normals[] = new Vector3D[ n_steps * n_steps * 10 ];
    int n_used = 0;
 
    int  id = 1;
    double phi_step = Math.PI / (2*n_steps);
//    System.out.println( "phi_step = " + phi_step );
    for ( double phi = 0; phi <= (1.000001)*Math.PI/2; phi += phi_step )
    {
      double r = Math.sin(phi);
      int n_theta = (int)Math.round( 4 * r * n_steps );
      double theta_step;
      if ( n_theta == 0 )                        // n = ( 0, 1, 0 );
         theta_step = 7;                         // just use one vector
      else
         theta_step = 2*Math.PI/( n_theta );
//      System.out.println( "theta_step = " + theta_step );
      double last_theta = 2*Math.PI - theta_step/2;

      if ( Math.abs(phi - Math.PI/2) < phi_step/2 )    // use half the equator
        last_theta = Math.PI - theta_step/2;

      for ( double theta = 0; theta < last_theta; theta += theta_step )
      {
        components[0] = (float)(r*Math.cos(theta));
        components[1] = (float)(Math.cos(phi));
        components[2] = (float)(r*Math.sin(theta));
        normal = new Vector3D( components );
        normals[n_used] = normal;
        n_used++;
        Data d = ProjectPoints(points, normal, id);
        ds.addData_entry( d );
//        System.out.println( "id = " + id + " normal = " + normal );
        id++;
      }
    }

/*
    Polymarker normals_used[] = new Polymarker[n_used];
    for ( int i = 0; i < n_used; i++ )
    {
      Vector3D verts[] = new Vector3D[1];
      verts[0] = normals[i];
      normals_used[i] = new Polymarker( verts, Color.cyan );
      normals_used[i].setSize( 4 );
      normals_used[i].setType( Polymarker.BOX );
    }
    vec_Q_space.setObjects( "NORMALS", normals_used );
*/
    return ds;
  }


  /* ---------------------------- FFT -------------------------------- */
  private DataSet FFT( DataSet ds )
  {
    DataSetFactory ds_factory = 
           new DataSetFactory( "FFT of projections", 
                                "Bin", "Magnitude",
                                "Counts", "Projected Counts");
    DataSet fft_ds = ds_factory.getDataSet();
   
    for ( int i = 0; i < ds.getNum_entries(); i++ )
    {
      Data original_d = ds.getData_entry(i);
      Data d = FFT(original_d);
      fft_ds.addData_entry( d );
    }

//    addErrorAttribute( fft_ds );

    return fft_ds;
  }


  /* ---------------------------- FFT -------------------------------- */
 
  private Data FFT( Data d )
  {
     float complex_data[] = new float[2*FFT_DATA_LENGTH];
     float re,
           im;

     float y[] = d.getY_values();
     for ( int j = 0; j < FFT_DATA_LENGTH; j++ )
     {
       complex_data[2*j  ] = y[j];
       complex_data[2*j+1] = 0;
     }

     ComplexFloatFFT fft = new ComplexFloatFFT_Radix2( FFT_DATA_LENGTH );
     fft.transform( complex_data );
     float magnitude[] = new float[FFT_DATA_LENGTH/2];
     for ( int j = 0; j < magnitude.length; j++ )
     {
       re = complex_data[2*j];
       im = complex_data[2*j+1];
       magnitude[j] = (float)(Math.sqrt(re*re+im*im));
     }
  
     XScale scale = new UniformXScale(0, magnitude.length-1, magnitude.length); 
     Data fft_d = Data.getInstance( scale, magnitude, d.getGroup_ID() );

     float peak_center = findFundamental( magnitude );
     fft_d.setAttribute( d.getAttribute( NORMAL_ATTRIBUTE ) );
     fft_d.setAttribute( new FloatAttribute( FREQUENCY_ATTRIBUTE, peak_center));
     return fft_d;
  }


  /* ---------------------------- findFundamental ---------------------- */
  /**
   *  find the fractional channel number that is the center of mass of the
   *  largest peak frequency beyond the "DC term".  This turns out to be the
   *  fundamental frequency in the type of spectra we encounter here.
   */
  private float findFundamental( float fft_array[] )
  {
                               // find the first valley using moving averages
    int n_averaged = 4;
    boolean decreasing = true;
    float sum = 0;
    for ( int i = 0; i < n_averaged; i++ )
      sum += fft_array[i];
    
    float new_sum;
    int i = 0;
    while ( decreasing && (i + n_averaged < fft_array.length) )
    {
      new_sum = sum - fft_array[i] + fft_array[i+n_averaged];

      if ( new_sum >= sum )                 // starting to increase
        decreasing = false;
      else                                  // keep advancing
      {
        sum = new_sum;
        i++;
      }
    }

    int valley = i + n_averaged/2 - 1;     
                                                     // now find the max 
                                                     // beyond the first valley
    float max_value = 0;
    int   max_channel = valley;
    for ( int j = valley; j < fft_array.length; j++ ) 
      if ( fft_array[j] > max_value )
      {
        max_value = fft_array[j];
        max_channel = j;
      }

    float peak_center = 0;
    float peak_area = 0;
    int half_width = valley/3;                        // guess half-peak width
                                                      // based on width of DC
                                                      // term
    for ( int j = max_channel-half_width; j <= max_channel+half_width; j++ )
    { 
      if ( j >= 0 && j < fft_array.length )
      {
        peak_center += fft_array[j] * j;
        peak_area += fft_array[j];
      }
    }
    peak_center /= peak_area;
 
    return peak_center;
  }


/* -------------------------- makeQR_factors ------------------------- */

  private boolean makeQR_factors()
  {
    if ( all_vectors == null || all_vectors.length < DIMENSION )
    {
       System.out.println("Need >= " + DIMENSION + 
                          " points in makeQR_factors");
       if ( all_vectors != null )
         System.out.println("Got " + all_vectors.length + " points.");
       else
         System.out.println("Got 0 points");
       return false;
    }

    QR_Rmat = new double[all_vectors.length][DIMENSION];
    for ( int row = 0; row < all_vectors.length; row++ )
    {
      float coords[] = all_vectors[row].get();
      for ( int col = 0; col < DIMENSION; col++ )
        QR_Rmat[row][col] = coords[col];
    }

    QR_Umat = LinearAlgebra.QR_factorization( QR_Rmat );
    return true;
  }


/* ---------------------------- assignHKLs -------------------------------- */

  private void assignHKLs()
  {
    float h_vals[] = h_plane_ui.get_normal();
    float k_vals[] = k_plane_ui.get_normal();
    float l_vals[] = l_plane_ui.get_normal();

    if ( h_vals == null || k_vals == null || l_vals == null )
    {
      System.out.println("ERROR: must have h,k,l normals set");
      return;
    }

    Vector3D a = new Vector3D( h_vals );
    Vector3D b = new Vector3D( k_vals );
    Vector3D c = new Vector3D( l_vals );

    a.multiply( h_plane_ui.get_d_spacing() );
    b.multiply( k_plane_ui.get_d_spacing() );
    c.multiply( l_plane_ui.get_d_spacing() );

    Vector3D q;
    float mag_a = (float)(2*Math.PI/a.length());
    float mag_b = (float)(2*Math.PI/b.length());
    float mag_c = (float)(2*Math.PI/c.length());
    a.normalize();
    b.normalize();
    c.normalize();

    for ( int i = 0; i < all_peaks.size(); i++ )
    {
       Peak_new pk_new = (Peak_new)all_peaks.elementAt(i);
       q = new Vector3D( pk_new.getUnrotQ() );
       q.multiply( (float)(2*Math.PI) );        // #### work around Peak_new
                                                // using q/(2*PI)
       float h = q.dot(a)/mag_a;
       float k = q.dot(b)/mag_b;
       float l = q.dot(c)/mag_c;
       pk_new.sethkl(h,k,l);
    }
  }


/* ------------------------- refinePlane ----------------------------- */
/**
 *  Calculate a refined normal vector as a 3 or 4 dimensional vector and
 *  calculate the corresponding least squares errors and d_spacing.
 *
 *  @return If DIMENSION == 3, this returns: {n1,n2,n3,err,d}, and if
 *          DIMENSION == 4, this returns: {n1,n2,n3,n4,err,d}, where n4
 *          represents a shift.  
 */
  private float[] refinePlane(Vector3D normal, float q_step)
  {
    double r[] = new double[all_vectors.length];
    double q_dist;
    for ( int row = 0; row < all_vectors.length; row++ )
    {
      q_dist = normal.dot( all_vectors[row] );
      r[row] = Math.round( q_dist/q_step );
    }

    double err = LinearAlgebra.QR_solve( QR_Rmat, QR_Umat, r );

    float result[] = new float[DIMENSION + 2];
    for ( int i = 0; i < DIMENSION; i++ )
      result[i] = (float)r[i];

    float sigma;
    if ( all_vectors.length > 2 )
      sigma = (float)(err / Math.sqrt( all_vectors.length-1 ));
    else
      sigma = (float)err;
     
    result[DIMENSION] = (float)sigma;              // fill in error values

    Vector3D new_normal = new Vector3D( result );
    float q_spacing  = 1/new_normal.length();
    float d_spacing = (float)(2*Math.PI / q_spacing);
    result[DIMENSION+1] = d_spacing;            // and d-spacing value

    return result;
  }


/* ------------------------- refinePlane ----------------------------- */
/**
 *  Calculate a refined normal vector as a 3 or 4 dimensional vector and
 *  calculate the corresponding least squares errors and d_spacing.
 *
 *  @return If DIMENSION == 3, this returns: {n1,n2,n3,err,d}, and if
 *          DIMENSION == 4, this returns: {n1,n2,n3,n4,err,d}, where n4
 *          represents a shift.  
 */
  private float[] refinePlane(Vector3D normal)
  {
    Data d = ProjectPoints( all_vectors, normal, 1 );
    d = FFT( d );

    float q_spacing;
                                           // get FREQ and calculate q_spacing
    Attribute attr = d.getAttribute( FREQUENCY_ATTRIBUTE );
    if ( attr != null && attr instanceof FloatAttribute )
    {
      float max_chan = ((FloatAttribute)attr).getFloatValue();
      q_spacing = (2*SLICE_SIZE_IN_Q)/(max_chan);
    }
    else
    {
      System.out.println("ERROR: Frequency attribute wrong");
      return null;
    }

    boolean changed = true;
    int     count = 0;
    float   values[] = null;
    Vector3D new_normal = new Vector3D();
    while ( changed && count < 10 )
    {
      values = refinePlane( normal, q_spacing );
      new_normal.set(values);
      if ( debug && new_normal.length() <= 0 )
        System.out.println("************ Warning, 0 length normal ********");

      q_spacing  = 1/new_normal.length();
      new_normal.normalize();
      if ( Math.abs(normal.dot(new_normal)) > 0.99999)
        changed = false;
      else
        normal.set( new_normal );
      count++;
    }

    return values;
  }


/* ------------------------- FilterFFTds ------------------------------- */
/*
 * Go through the FFT ds refine the normals and only keep the FFT's for 
 * which the refined normals are distinct.
 */
  private DataSet FilterFFTds( DataSet fft_ds, float err_threshold )
  {
    DataSet new_ds = fft_ds.empty_clone();
    new_ds.setTitle("Filtered FFT DataSet");
    Vector   normals = new Vector();

    int      n_data = fft_ds.getNum_entries();
    Object   value;
    Vector3D normal;
    float    q_spacing;
    float    d_spacing;
    float    sigma;
    for ( int i = 0; i < n_data; i++ )
    {
      Data d = fft_ds.getData_entry( i );
                                            // get normal
      value = d.getAttributeValue( NORMAL_ATTRIBUTE );
      normal = new Vector3D( (float[])value );

                                            // get FREQ and calculate q_spacing
      Attribute attr = d.getAttribute( FREQUENCY_ATTRIBUTE );
      if ( attr != null && attr instanceof FloatAttribute )
      {
        float max_chan = ((FloatAttribute)attr).getFloatValue();
        q_spacing = (2*SLICE_SIZE_IN_Q)/(max_chan);
      }
      else
      {
        System.out.println("ERROR: Frequency attribute wrong");
        return fft_ds;
      }
                                             // refine the plane for this normal
      boolean changed = true;
      int     count = 0;
      float   values[] = null;
      Vector3D new_normal = new Vector3D();
      while ( changed && count < 10 )
      {
        values     = refinePlane( normal, q_spacing );
        new_normal.set(values);
        if ( new_normal.length() <= 0 )
          System.out.println("************ Warning, 0 length normal ********");

        q_spacing  = 1/new_normal.length();
        new_normal.normalize();
        if ( Math.abs(normal.dot(new_normal)) > 0.99999)
          changed = false;
        else
          normal.set( new_normal );
        count++;
      }
/*
      if ( !changed )
        System.out.println("Stable after " + count );
      else
        System.out.println("Failed to stablize " + count );
*/
      d_spacing = (float)(2*Math.PI / q_spacing);
      sigma = values[DIMENSION];

      if ( sigma < err_threshold && d_spacing > 0 )
      { 
        new_normal.normalize();

        boolean duplicate = false;
        int j = 0;
        float old_vals[];
        float new_vals[];
        while (!duplicate && j < normals.size())
        {
          Vector3D saved_vec = (Vector3D)(normals.elementAt(j));
          old_vals = saved_vec.get();
          new_vals = new_normal.get();
          duplicate = true;
          for ( int k = 0; k < 3; k++ )
            if ( Math.abs( old_vals[k] - new_vals[k] ) > 0.000001f )
              duplicate = false;

          if ( !duplicate )            // try negative value
          {
            duplicate = true;
            for ( int k = 0; k < 3; k++ )
              if ( Math.abs( old_vals[k] + new_vals[k] ) > 0.000001f )
                duplicate = false;
          }
          j++;
        }
        if ( !duplicate )
        {  
          normals.addElement( new_normal );
          Data new_d = ProjectPoints( all_vectors, new_normal, normals.size());
          Data fft_d = FFT(new_d);
          fft_d.setAttribute( 
                    new Float1DAttribute( NORMAL_ATTRIBUTE, new_normal.get()));
          fft_d.setAttribute( 
                    new FloatAttribute( Q_SPACING_ATTRIBUTE, q_spacing ) );
          fft_d.setAttribute( 
                    new FloatAttribute( D_SPACING_ATTRIBUTE, d_spacing ) );
          fft_d.setAttribute( 
                    new FloatAttribute( LSQ_ERROR_ATTRIBUTE, sigma ) );
                                       // now reset the frequency attribute
                                       // based on refined normal & q values
          float max_chan = 2 * SLICE_SIZE_IN_Q / q_spacing;
          fft_d.setAttribute( 
                    new FloatAttribute( FREQUENCY_ATTRIBUTE, max_chan ) );
          new_ds.addData_entry( fft_d );
        }
      }
    } 

    Operator op = new DataSetSort( new_ds, D_SPACING_ATTRIBUTE, true, false );
    op.getResult();
    System.out.println("Filtered FFT ds has : " + normals.size() );
    return new_ds;
  }


  /* ------------------------- parseArgs ----------------------------- */
  /**
   *  Parse a list of command line arguments to extract values for the
   *  the data directories and run numbers  The commands supported are
   *  -D -R and -H
   *
   *  @param args  Array of strings from the command line, containing
   *               command characters and arguments.
   */
   private void parseArgs( String args[] )
   {
     if ( args == null || args.length < 2        ||
          StringUtil.commandPresent("-h", args ) ||
          StringUtil.commandPresent("-H", args )  )
     {
       showUsage();
       System.exit(0);
     }

     path        = StringUtil.getCommand( 1, "-D", args );
     run_nums    = StringUtil.getCommand( 1, "-R", args );
     calib_file  = StringUtil.getCommand( 1, "-C", args );
     orient_file = StringUtil.getCommand( 1, "-O", args );

     if ( calib_file != null && calib_file.length() > 0 )
       calib_file = path + "/" + calib_file;

     if ( orient_file != null && orient_file.length() > 0 )
       orient_file = path + "/" + orient_file;

     if ( path.length() <= 0 || run_nums.length() <= 0 )
     {
       showUsage();
       System.exit(0);
     }

     path = path + "/";

     border_size = StringUtil.getCommand( 1, "-B", args );
     threshold   = StringUtil.getCommand( 1, "-T", args );
   }


  /* ----------------------- showUsage ----------------------- */
  /**
   *  Print list of supported commands.
   */
   private void showUsage()
   {
    System.out.println(
       "  -D<dir name>  specifies directory for data files (required)");
    System.out.println(
       "  -R<list of run numbers> specify runs to load (required)");
    System.out.println(
       "  -C<calibration file name> specify name of calibration file");
    System.out.println(
       "  -O<orientation matrix file name> specify name of calibration file");
    System.out.println(
       "  -T<relative threshold> specify scale factor to apply to the");
    System.out.println(
       "                         default value for the threshold.");
    System.out.println(
       "                        (values < 1 increase number of points shown)");
    System.out.println(
       "                        (values > 1 decrease number of points shown)");
    System.out.println(
       "  -B<border size> specifies number of border rows and column to skip");
    System.out.println("  -H,-h  print this message");
   }


  /* ------------------------- Redraw ------------------------------ */
  /*
   *  Redraw the current origin and basis vectors, and set the new
   *  values for the cosines of the angles between the basis vectors.
   */
  private void Redraw()
  {
    Vector3D origin = origin_vec.getVector();
    MarkPoint( origin, ORIGIN, Polymarker.BOX );

    Vector3D first_vec = vec_1.getVector();
    if ( first_vec == null )
    {
      vec_Q_space.removeObjects( VEC_1 );
      vec_Q_space.removeObjects( VEC_1+"LINE" );
    }
    else
    {
      BasisVector( first_vec, VEC_1+"LINE", YELLOW );
      first_vec.add( origin );
      MarkPoint( first_vec, VEC_1, Polymarker.PLUS );
    }

    Vector3D second_vec = vec_2.getVector();
    if ( second_vec == null )
    {
      vec_Q_space.removeObjects( VEC_2 );
      vec_Q_space.removeObjects( VEC_2+"LINE" );
    }
    else
    {
      BasisVector( second_vec, VEC_2+"LINE", YELLOW );
      second_vec.add( origin );
      MarkPoint( second_vec, VEC_2, Polymarker.STAR );
    }

    vec_Q_space.Draw();
  }


  /* ---------------------------- draw_Axes ----------------------------- */
  /*
   * Draw the appropriate type of axes, depending on whether or not an
   * orienation transformation was specified.
   */
  private void draw_Axes()
  {
                                            // First clear out the old axes
    vec_Q_space.removeObjects( "QX-AXIS" );
    vec_Q_space.removeObjects( "QY-AXIS" );
    vec_Q_space.removeObjects( "QZ-AXIS" );
    vec_Q_space.removeObjects( "a*-AXIS" );
    vec_Q_space.removeObjects( "b*-AXIS" );
    vec_Q_space.removeObjects( "c*-AXIS" );
                                            // Then draw the right new one
    if ( orientation_matrix == null )       // NOTE: this could also be
      draw_Q_axes( 15, vec_Q_space );       //       controlled by a checkbox
    else
      draw_HKL_axes( 15, vec_Q_space );
  }


  /* ---------------------------- draw_Q_axes ----------------------------- */
  /*
   *  Draw orthogonal axes in "Q".
   */
  private  void draw_Q_axes( float length, ThreeD_GL_Panel threeD_panel  )
  {
    Axis x_axis = Axis.getInstance( threeD_panel,
                                    new Vector3D( -length/20, 0, 0 ),
                                    new Vector3D(  length,    0, 0 ),
                                    "Qx" );
    Axis y_axis = Axis.getInstance( threeD_panel,
                                    new Vector3D( 0, -length/20, 0 ),
                                    new Vector3D( 0,  length,    0 ),
                                    "Qy" );
    Axis z_axis = Axis.getInstance( threeD_panel,
                                    new Vector3D( 0, 0, -length/20 ),
                                    new Vector3D( 0, 0,  length    ),
                                    "Qz" );
    z_axis.setSkipValue( 0 );
    x_axis.setColor( RED );
    y_axis.setColor( GREEN );
    z_axis.setColor( BLUE );
    
    threeD_panel.setObject( "QX-AXIS", x_axis );
    threeD_panel.setObject( "QY-AXIS", y_axis );
    threeD_panel.setObject( "QZ-AXIS", z_axis );
  }


  /* -------------------------- keep_peak ------------------------------ */
  /*
   *  Check whether or not the peak should be kept.  Peaks are kept if
   *  there is no orientation matrix, or if there is an orientation matrix,
   *  they are kept if they line near integer "h" planes
   */
  private boolean keep_peak( Vector3D peak )
  {
/*
    if ( orientation_matrix != null )      // use orientation matrix to filter
    {
      Vector3D temp = new Vector3D( peak );
      orientation_matrix_inverse.apply_to( temp, temp );

      float peak_dot_h = temp.get()[0];
      float peak_h = (float)Math.round(peak_dot_h);

      if ( Math.abs( peak_dot_h - peak_h ) < INDEX_TOLERANCE )
        return true;
      else
        return false;
    }
*/
                                       // filter to constant h, k or l planes
    if ( h_plane_ui.filter_on() )
      if ( !on_plane_family( h_plane_ui, peak ) )
        return false; 

    if ( k_plane_ui.filter_on() )
      if ( !on_plane_family( k_plane_ui, peak ) )
        return false; 

    if ( l_plane_ui.filter_on() )
      if ( !on_plane_family( l_plane_ui, peak ) )
        return false; 

    return true;      // no reason to reject the peak.
  }

 /* ------------------------- on_plane_family -------------------- */
 /*
  *  Check whether or not the specified peak lies on the family of
  *  planes in reciprocal space corresponding to the normal direction
  *  and d-spacing recorded in the plane UI.
  */
  private boolean on_plane_family( LatticePlaneUI plane_ui, Vector3D peak )
  {
    float normal[] = plane_ui.get_normal();
                                             // use normal vector to filter
    if ( normal != null && 
        (normal[0] != 0 || normal[1] != 0 || normal[2] != 0) )
    {
      float d = plane_ui.get_d_spacing();
      if ( d <= 0 )
        return true;   // keep the peak if there is not a valid d-spacing

      Vector3D normal_vec = new Vector3D( normal );
      float q_spacing = (float)( 2*Math.PI/plane_ui.get_d_spacing() );

      float f_miller_index = peak.dot(normal_vec)/q_spacing;
      float miller_index   = (float)Math.round( f_miller_index );

      if ( Math.abs( f_miller_index - miller_index ) < INDEX_TOLERANCE )
        return true;      
      else
        return false;
    }

    return true;   // keep the peak if no valid normal was set
  } 


  /* -------------------------- draw_HKL_axes --------------------------- */
  /*  
   *  Draw axes for the HKL coordinate system (assuming there is an 
   *  orientation matrix. 
   */
  private  void draw_HKL_axes( float length, ThreeD_GL_Panel threeD_panel  )
  { 
    if ( orientation_matrix == null )
    {
      System.out.println("NO ORIENTATION MATRIX LOADED");
      return;
    }

    Vector3D  h_dir = new Vector3D(1,0,0);
    Vector3D  k_dir = new Vector3D(0,1,0);
    Vector3D  l_dir = new Vector3D(0,0,1);
    orientation_matrix.apply_to( h_dir, h_dir );
    orientation_matrix.apply_to( k_dir, k_dir );
    orientation_matrix.apply_to( l_dir, l_dir );

    h_dir.multiply( length );    
    k_dir.multiply( length );    
    l_dir.multiply( length );    

    Vector3D minus_h_dir = new Vector3D( h_dir );
    Vector3D minus_k_dir = new Vector3D( k_dir );
    Vector3D minus_l_dir = new Vector3D( l_dir );
    minus_h_dir.multiply( -1 );
    minus_k_dir.multiply( -1 );
    minus_l_dir.multiply( -1 );
 
    Axis h_axis = Axis.getInstance( threeD_panel, 
                                    minus_h_dir, h_dir,"              a*-Axis");
    Axis k_axis = Axis.getInstance( threeD_panel,
                                    minus_k_dir, k_dir,"              b*-Axis");
    Axis l_axis = Axis.getInstance( threeD_panel,
                                    minus_l_dir, l_dir,"              c*-Axis");

    h_axis.setSkipValue( 0 );
    k_axis.setSkipValue( 0 );
    l_axis.setSkipValue( 0 );
    h_axis.setColor( RED );
    k_axis.setColor( GREEN );
    l_axis.setColor( BLUE );

    h_axis.setCharHeight( length/50 );
    k_axis.setCharHeight( length/50 );
    l_axis.setCharHeight( length/50 );
    
    h_axis.setMinMax( -length, length );
    k_axis.setMinMax( -length, length );
    l_axis.setMinMax( -length, length );
    
    threeD_panel.setObject( "a*-AXIS", h_axis );
    threeD_panel.setObject( "b*-AXIS", k_axis );
    threeD_panel.setObject( "c*-AXIS", l_axis );
  } 


  /* ------------------------- MarkPoint --------------------------- */
  /*
   * Mark the specified point with a polymarker of the specified type.
   *
   *  @param  vec   the point to mark
   *  @param  name  the name of the Polymarker, so that it can be removed
   *                when the point is cleared
   *  @param  type  the type of marker to draw
   */
  private void MarkPoint( Vector3D vec, String name, int type )
  {
    GL_Shape objects[] = new GL_Shape[ 1 ];
    Vector3D points[] = new Vector3D[1];

    points[0] = vec;
    Polymarker marker = new Polymarker( vec_Q_space, points, type, 0.1f );
    marker.setColor( YELLOW );
    objects[0] = marker;

    vec_Q_space.setObjects( name, objects );
  }

  /* ------------------------- BasisVector --------------------------- */
  /*
   * Draw a segmented line from the "origin" point in the direction given
   * by the specified vector.
   *
   * @param  vec    The vector offset from the origin.  This must be a
   *                valid non-zero vector.
   * @param  name   the name for the line object, so that it can be
   *                removed when it is not needed.
   * @param  color  the color to draw the line with
   */
  private void BasisVector( Vector3D vec, String name, float color[] )
  {
    Vector3D start[] = new Vector3D[1];
    Vector3D end[]   = new Vector3D[1];
    start[0] = new Vector3D( origin_vec.getVector() );
    end[0]   = new Vector3D( origin_vec.getVector() );
    end[0].add( vec );

    Lines lines = new Lines( vec_Q_space, start, end );
    lines.setColor( color );

    vec_Q_space.setObject( name, lines );
  }


  /* ------------------------- make_slice ---------------------------- */

  private float[][] make_slice( Vector3D origin,
                                Vector3D normal,
                                Vector3D base,
                                Vector3D up    )
  {
    System.out.println("Start of make_slice......");
    if( origin == null || base == null || up == null )
      return null;

    int n_rows = SLICE_STEPS;
    int n_cols = SLICE_STEPS;
    float image[][] = new float[n_rows][n_cols];

    float size = 2*SLICE_SIZE_IN_Q;
                                             // make two orthonormal vectors
    Vector3D base2 = new Vector3D();
    base2.cross( normal, base );
    base2.normalize();

    Vector3D base1 = new Vector3D();
    base1.cross( normal, base2 );
    base1.normalize();   

    float b1[] = base1.get();
    float b2[] = base2.get();
    System.out.println("Origin = " + origin );
    System.out.println("base1  = " + base1 );
    System.out.println("base2  = " + base2 );

    float orig[] = origin.get();
    Vector3D q = new Vector3D();
    float step = size/n_rows;
    float d_row, d_col;
    float value;
    int   n_non_zero;
                                             // for each point in the plane...
    VecQToTOF transformer;
    float sum;
    for ( int row = 0; row < n_rows; row++ )
      for ( int col = 0; col < n_cols; col++ )
      {
        d_row = (n_rows/2 - row)*step;
        d_col = (col - n_cols/2)*step;

        q.set( orig[0] + d_row * b2[0] + d_col * b1[0],
               orig[1] + d_row * b2[1] + d_col * b1[1],
               orig[2] + d_row * b2[2] + d_col * b1[2]  );

         sum = 0;
         n_non_zero = 0;
         for ( int i = 0; i < vec_q_transformer.size(); i++ )
         {
           transformer = (VecQToTOF)(vec_q_transformer.elementAt(i));
           value = transformer.intensityAtQ( q );
           if ( value >= 0 )
           {
             sum += value;
             n_non_zero++;
           }
         }
         if ( n_non_zero > 0 )
           image[row][col] = sum / n_non_zero;
         else
           image[row][col] = 0;
      }
    System.out.println("DONE");
    return image;
  }


/* ------------------------- showLatticeParameters ----------------------- */
/*
private void showLatticeParameters( Vector3D a, Vector3D b, Vector3D c )
{
  Vector3D a_normal = new Vector3D( a );
  a_normal.normalize();

  Vector3D b_normal = new Vector3D( b );
  b_normal.normalize();

  Vector3D c_normal = new Vector3D( c );
  c_normal.normalize();

  double alpha = (180/Math.PI) * Math.acos( b_normal.dot( c_normal ) );
  double beta  = (180/Math.PI) * Math.acos( a_normal.dot( c_normal ) );
  double gamma = (180/Math.PI) * Math.acos( a_normal.dot( b_normal ) );

  Vector3D temp = new Vector3D();
  temp.cross( a, b );
  float volume = Math.abs(c.dot( temp ));

  System.out.println("-----------------------------------------------------");
  System.out.println("Lattice Parameters:");
  System.out.println("");
  System.out.println( "  "  + Format.real( a.length(), 5, 5 ) +
                      "  "  + Format.real( b.length(), 5, 5 ) +
                      "  "  + Format.real( c.length(), 5, 5 ) +
                      " : " + Format.real( alpha, 5, 5 )      +
                      "  "  + Format.real( beta, 5, 5 )       +
                      "  "  + Format.real( gamma, 5, 5 )      + 
                      " : " + Format.real( volume, 5, 5 )     );
  System.out.println("");
  System.out.println("-------------------------------------------------------");
}
*/

/* -------------------------- MakeSlider ----------------------------- */

private JSlider MakeSlider( String title, ChangeListener listener )
{
  JSlider slider = new JSlider(SLIDER_MIN,SLIDER_MAX,SLIDER_DEF);
  slider.setMajorTickSpacing(20);
  slider.setMinorTickSpacing(5);
  slider.setPaintTicks(true);
  TitledBorder border = new TitledBorder( LineBorder.createBlackLineBorder(),
                                    title + " = " + SLIDER_DEF );
  border.setTitleFont( FontUtil.BORDER_FONT );
  slider.setBorder( border );
  slider.addChangeListener( listener );
  return slider;
}

/* ------------------------ WriteMatrixFile ---------------------------- */
/**
 *  Write the best fit orientation matrix and lattice parameters to the 
 *  specified file and to the status pane.
 */
public void WriteMatrixFile( String filename )
{
  if ( all_peaks.size() < 3 )
  {
    System.out.println("ERROR: Not enough peaks to fit: " + all_peaks.size());
    return;
  }

  assignHKLs();

  int k = all_peaks.size();                 // number of vectors for BestFit
  double M[][]   = new double[3][3];
  double q[][]   = new double[k][3];
  double hkl[][] = new double[k][3];
  float[] unrotQ;
  for ( int i = 0; i < all_peaks.size(); i++ )
  {
    Peak_new pk_new = (Peak_new)all_peaks.elementAt(i);
    unrotQ = pk_new.getUnrotQ();

    for ( int component = 0; component < 3; component++ )
      q[i][component] = (float)(unrotQ[component] * 2 * Math.PI);
                                                // #### work around Peak_new
                                                // using q/(2*PI)
    hkl[i][0] = Math.round( pk_new.h() );
    hkl[i][1] = Math.round( pk_new.k() );
    hkl[i][2] = Math.round( pk_new.l());
  }

  double std_dev = LinearAlgebra.BestFitMatrix( M, hkl, q );
  if ( Double.isNaN( std_dev ) )
  {
    System.out.println("Singular matrix when doing best fit...can't solve");
    return;
  }

  std_dev = std_dev / Math.sqrt(k-1);
  System.out.println("Standard deviation for fit = " + std_dev );
  if ( std_dev > 1 )
  {
    System.out.println("ERROR: vectors co-planar");
    return;
  }

  for ( int i = 0; i < 3; i++ )
    for ( int j = 0; j < 3; j++ )
       M[i][j] /= (2 * Math.PI );

  boolean lattice_ok = true;
  double lat_parms[] = lattice_calc.LatticeParamsOfUB( M );
  if (lat_parms == null )
    lattice_ok = false;

  if ( lattice_ok )
    for ( int i = 0; i < lat_parms.length; i++ )
      if ( lat_parms[i] == 0 )
        lattice_ok = false;
  
  if ( !lattice_ok )
  {
    System.out.println("ERROR: Matrix was singular");
    return;
  }

  StringBuffer sb = new StringBuffer();
  for ( int i = 0; i < 3; i++ )
  {
    for ( int j = 0; j < 3; j++ )                    // Write the TRANSPOSE of
      sb.append( Format.real( M[j][i], 10, 6 ) );    // the orientation matrix
    sb.append( "\n" );
  }

  for ( int i = 0; i < 7; i++ )
    sb.append( Format.real( lat_parms[i], 10, 3 ) );
  sb.append( "\n" );
                                            // write zeros for error estimates
  for ( int i = 0; i < 7; i++ )             // for now.
    sb.append( Format.real( 0, 10, 3 ) );
  sb.append( "\n" );

  SharedData.addmsg( sb.toString() );

  boolean write_ok = false;
  try
  {
    FileWriter fw = new FileWriter( filename, false );
    fw.write( sb.toString() );
    fw.flush();
    fw.close();
    write_ok = true;
  }
  catch ( IOException execption )
  {
    System.out.println("ERROR: Couldn't write to file: " + filename );
  }

  System.out.println("Wrote lattice parameters to: " + filename );

  if ( write_ok )
    loadOrientationMatrix( filename );
}


/* ----------------------------- WritePeaksFile ---------------------------- */
/**
 *  Write the data grids and lists of Peak_new objects to the specified
 *  file.
 */
public void WritePeaksFile( String filename )
{
  assignHKLs();
  try
  {
    Peak_new_IO.WritePeaks_new( filename, all_peaks, false );
  }
  catch( IOException ex )
  {
    SharedData.addmsg( "Could not write peaks file: " + filename );
  }
}

/* --------------------------------------------------------------------------
 *
 *  PRIVATE CLASSES
 *
 */


/* ------------------------- ViewMouseInputAdapter ----------------------- */
/**
 *  Handles mouse events for picking data points displayed.
 */
private class ViewMouseInputAdapter extends MouseInputAdapter
{
   int last_index = GL_Shape.INVALID_PICK_ID;

   public void mousePressed( MouseEvent e )
   {
     handle_event(e);
   }

   public void mouseDragged( MouseEvent e )
   {
     handle_event(e);
   }

   private void handle_event( MouseEvent e )
   {
     int index = vec_Q_space.pickID( e.getX(), e.getY(), 5 );
     System.out.println("Pick ID = " + index );
     if ( index != last_index )
     {
       last_index = index;
       if ( index != GL_Shape.INVALID_PICK_ID )
       {
         Vector3D position = vec_Q_space.pickedPoint( e.getX(), e.getY() );
         System.out.println("Point = " + position );
         if ( position != null )
         {
           Toolkit.getDefaultToolkit().beep();
           float coords[] = position.get();
           String result = new String( Format.real( coords[0], 6, 3 ) );
           result += ", " + Format.real( coords[1], 6, 3 );
           result += ", " + Format.real( coords[2], 6, 3 );
           q_readout.setText( result );

           VecQToTOF transformer = null;
           DataSet   this_ds     = null;
           for ( int i = 0; i < vec_q_transformer.size(); i++ )
           {
             transformer = (VecQToTOF)(vec_q_transformer.elementAt(i));
             this_ds = (DataSet)ds_of_transformer.elementAt(i);
             float row_col_ch[]  = transformer.QtoRowColChan( position );
             float xcm_ycm_wl[]  = transformer.QtoXcmYcmWl( position );
             float row_col_tof[] = transformer.QtoRowColTOF( position );
             if ( row_col_ch != null )
             {
               SharedData.addmsg("\nData for Q = " + position );
               int[] run_numbers = AttrUtil.getRunNumber( this_ds );
               if ( run_numbers != null && run_numbers.length > 0 )
                 SharedData.addmsg(" Run Number = " + run_numbers[0] );
             }
             if ( row_col_ch != null )
             {
               String msg = " \n COL ROW CHAN = " +
                            "   " + row_col_ch[1] + 
                            "   " + row_col_ch[0] + 
                            "   " + row_col_ch[2];
               SharedData.addmsg( msg );
             }    
             if ( xcm_ycm_wl != null )
             {
               String msg = " \n Xcm Ycm Wl = " +
                            "   " + xcm_ycm_wl[0] + 
                            "   " + xcm_ycm_wl[1] + 
                            "   " + xcm_ycm_wl[2]; 
               SharedData.addmsg( msg );
             }
             if ( row_col_tof != null )
             {
               String msg = " \n COL ROW TOF = " +
                            "   " + row_col_tof[1] + 
                            "   " + row_col_tof[0] + 
                            "   " + row_col_tof[2]; 
               SharedData.addmsg( msg );

               // also, update the x-conversions table readout
               IDataGrid grid = transformer.getDataGrid();
               Data d = grid.getData_entry( (int)row_col_tof[0], 
                                            (int)row_col_tof[1] );
              
               int data_index = this_ds.getIndex_of_data( d );
               if ( index >= 0 )
               {
                 this_ds.setPointedAtIndex( data_index );
                 this_ds.setPointedAtX( row_col_tof[2] );
                 conv_table.setDataSet( this_ds );
                 conv_table.showConversions( row_col_tof[2], data_index );
               }

             }
           }
         }
       }
       else
         q_readout.setText( UNDEFINED );
     }
   }
}


/* ---------------------- DetectorCoverageListener ------------------- */

private class DetectorCoverageListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    JCheckBox checkbox = (JCheckBox)e.getSource();
    ShowBoundaries( checkbox.isSelected() ); 
    vec_Q_space.Draw();
  }
}


/* ---------------------- IntegerHKLListener ------------------- */

private class IntegerHKLListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    JCheckBox checkbox = (JCheckBox)e.getSource();
    scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    ShowHKL_Marks( checkbox.isSelected() );
    vec_Q_space.Draw();
    scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }
}


/* ---------------------- IsoSurfaceListener ------------------- */
  
private class IsoSurfaceListener implements ActionListener
{    
  public void actionPerformed( ActionEvent e )
  {
    JCheckBox checkbox = (JCheckBox)e.getSource();
    iso_surface_shown = checkbox.isSelected();
    scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    ShowContours( iso_surface_shown, contour_threshold );
    vec_Q_space.Draw();
    scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }    
}      


/* -------------------- PeakThresholdScaleHandler ------------------- */

private class PeakThresholdScaleHandler implements ChangeListener
{
  public void stateChanged(ChangeEvent e)
  {
    JSlider slider = (JSlider)e.getSource();

    if ( !slider.getValueIsAdjusting() )
    {
      int value = slider.getValue();
      TitledBorder border = new TitledBorder(LineBorder.createBlackLineBorder(),
                                             "Peaks Threshold = " + value );
      border.setTitleFont( FontUtil.BORDER_FONT );
      slider.setBorder( border );

      peak_threshold = value;
      scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      ExtractPeaks();
      draw_Axes();
      vec_Q_space.Draw();
      scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }
}


/* ------------------------ Q_Range_Listener ----------------------------- */

  private class Q_Range_Listener implements ActionListener
  {
     float last_Q_min;
     float last_Q_max;

     public Q_Range_Listener()
     {
       last_Q_min = q_range_control.getMin();
       last_Q_max = q_range_control.getMax();
     }

     public void actionPerformed(ActionEvent e)
     {
       float Q_min = q_range_control.getMin();
       float Q_max = q_range_control.getMax();

       if ( last_Q_min != Q_min || last_Q_max != Q_max )
       {
         last_Q_min = Q_min;
         last_Q_max = Q_max;
         min_Q_threshold = Q_min;
         max_Q_threshold = Q_max;

         scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         ExtractPeaks();
         draw_Axes();
         vec_Q_space.Draw();
         scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
       }
     }
  }


/* ------------------------ det_choice_Listener --------------------------- */
  
  private class DetChoiceListener implements ActionListener
  {
     String last_list;

     public DetChoiceListener()
     {
       last_list = det_choice_control.getListString();
     }

     public void actionPerformed(ActionEvent e) 
     {
       String list = det_choice_control.getListString();

       if ( !list.equals( last_list ) )
       {
         scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         ExtractPeaks();
         draw_Axes();
         vec_Q_space.Draw();
         scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
       }
     }
  }


/* -------------------- ContourThresholdScaleHandler ------------------- */

private class ContourThresholdScaleHandler implements ChangeListener
{
  public void stateChanged(ChangeEvent e) 
  {
    JSlider slider = (JSlider)e.getSource();
  
    if ( !slider.getValueIsAdjusting() )
    {
      int value = slider.getValue();
      TitledBorder border = new TitledBorder(LineBorder.createBlackLineBorder(),
                                           "Iso-surface Threshold = " + value );
      border.setTitleFont( FontUtil.BORDER_FONT );
      slider.setBorder( border );
  
      contour_threshold = value;
      scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      ShowContours( iso_surface_shown, contour_threshold );
      vec_Q_space.Draw();
      scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }
}


/* ----------------------- CalcFFTButtonHandler ------------------- */

private class CalcFFTButtonHandler implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    CalculateFFTs();
    scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }
}


/* ----------------------- ReadMatrixFileListener ------------------------ */
/*
 *  Load the orientation matrix and lattice parameters from a file and 
 *  redraw with a*, b* and c* axes.
 */
private class ReadMatrixFileListener implements ActionListener
{

  public void actionPerformed( ActionEvent e )
  {
    RobustFileFilter filter = new RobustFileFilter();
    filter.addExtension("mat");

    JFileChooser chooser = null;
    if ( input_orientation_matrix_dir == null )
      chooser = new JFileChooser();
    else
      chooser = new JFileChooser( input_orientation_matrix_dir );

    chooser.setFileFilter( filter );

    int returnVal = chooser.showOpenDialog( scene_f );
    if(returnVal == JFileChooser.APPROVE_OPTION)
    {
      input_orientation_matrix_dir = chooser.getCurrentDirectory();
      loadOrientationMatrix( chooser.getSelectedFile().toString() );
      draw_Axes();
      vec_Q_space.Draw();
    }
  }
}


/* ----------------------- ClearMatrixListener ------------------------ */
/*
 *  Reset the orientation matrix back to null and 
 *  redraw with Qx, Qy and Qz axes.
 */
private class ClearMatrixListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    orientation_matrix = null;
    orientation_matrix_inverse = null;
     
    draw_Axes();
    vec_Q_space.Draw();
  }
}


/* ----------------------- WriteMatrixFileListener ------------------------ */
/*
 *  Write the orientation matrix and lattice parameters to a file and to
 *  the StatusPane.
 */
private class WriteMatrixFileListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    JFileChooser chooser = new JFileChooser();
    int returnVal = chooser.showOpenDialog( scene_f );
    if(returnVal == JFileChooser.APPROVE_OPTION) 
      WriteMatrixFile( chooser.getSelectedFile().toString() );
  }
}


/* ----------------------- WritePeaksFileListener ------------------------ */
/*
 *  Write the list of peak data objects to a file.
 */
private class WritePeaksFileListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    JFileChooser chooser   = new JFileChooser();
    int          returnVal = chooser.showOpenDialog( scene_f );
    String       file_name = "";

    if(returnVal == JFileChooser.APPROVE_OPTION)
    {
      try
      {
        file_name = chooser.getSelectedFile().toString();
        WritePeaksFile( file_name );

        System.out.println("Wrote Peak Data to " + file_name );
      }
      catch ( Exception io_exception )
      {
        SharedData.addmsg( "Couldn't write Peaks file: " + file_name );
        SharedData.addmsg( "  " + io_exception.toString() );
      }
    }
  }
}


/* ---------------------- LatticeParameterListener ------------------------ */
/*
private class LatticeParameterListener implements ActionListener
{
  public void actionPerformed( ActionEvent e )
  {
    float h_vals[] = h_plane_ui.get_normal(); 
    float k_vals[] = k_plane_ui.get_normal(); 
    float l_vals[] = l_plane_ui.get_normal(); 

    if ( h_vals == null || k_vals == null || l_vals == null )
    {
      System.out.println("ERROR: must have h,k,l normals set");
      return;
    }

    Vector3D a = new Vector3D( h_vals );
    Vector3D b = new Vector3D( k_vals );
    Vector3D c = new Vector3D( l_vals );

    a.multiply( h_plane_ui.get_d_spacing() ); 
    b.multiply( k_plane_ui.get_d_spacing() ); 
    c.multiply( l_plane_ui.get_d_spacing() ); 
    
    showLatticeParameters( a, b, c );
  }
}
*/


/* ------------------------- ReadoutListener ----------------------- */
/**
 *  Class to handle user selection and scale factor change events
 *  from the SimpleVectorReadout components for origin, vec_1 and vec_2
 */
private class ReadoutListener implements ActionListener
{
   public void actionPerformed( ActionEvent e )
   {
     String              action  = e.getActionCommand();
     SimpleVectorReadout readout = (SimpleVectorReadout)e.getSource();

     if ( action.startsWith( "Select" ) )
     {
       Vector3D position;
       if ( q_readout.getText().startsWith( UNDEFINED ) )
         position = null;
       else
         position = vec_Q_space.pickedPoint();

       if ( position != null && position.length() < 50 )  // selecting valid 
       {                                                  // position 
         if ( readout.getTitle().equals(ORIGIN) )
         {
           readout.setVector( position );                // just move the origin
           controller.setVRP( position );
         }
         else                                            // get vector relative
         {                                               // to the origin
           Vector3D vec = new Vector3D( position );
           Vector3D start = new Vector3D( origin_vec.getVector() );
           start.multiply( -1 );
           vec.add( start );
           readout.setVector( vec );
         }
       }
       else                                              // selecting invalid
       {                                                 // position
         if ( readout.getTitle().equals(ORIGIN) )
         {                                               // reset origin to 
           position = readout.getDefault();              // default position
           readout.setVector( position );                
           controller.setVRP( position );
         }
         else                                            // reset to null
           readout.setVector( null );
       } 
     }
     else if ( action.startsWith("Reset") && readout.getTitle().equals(ORIGIN))
       controller.setVRP( origin_vec.getVector() ); 

     Redraw();
   }
 }


/* ------------------------- PlaneListener ----------------------- */
/**
 *  Class to handle user selection of plane from vectors or FFT DataSet
 */
private class PlaneListener implements ActionListener
{
   public void actionPerformed( ActionEvent e )
   {
     String         action  = e.getActionCommand();

     if ( action.equals(LatticePlaneUI.USER_SET) )
     {
       LatticePlaneUI plane_ui = (LatticePlaneUI)e.getSource();
       Vector3D origin = origin_vec.getVector();
       Vector3D v1     = vec_1.getVector();
       Vector3D v2     = vec_2.getVector();
       if ( origin == null || v1 == null || v2 == null )
         return;
 
       Vector3D e1 = new Vector3D( v1 );
       Vector3D e2 = new Vector3D( v2 );

       Vector3D normal = new Vector3D();
       normal.cross( e1, e2 );
       normal.normalize();
       System.out.println("USER NORMAL = " + normal );

       float d_spacing = plane_ui.get_d_spacing();
       float value[] = null;
       if ( d_spacing > 0 )                          // if d is set, use it
       {
         float q_spacing = (float)( 2*Math.PI / d_spacing );
         value = refinePlane( normal, q_spacing ); 
       }
       else
         value = refinePlane( normal );             // else get estimate then
                                                    // refine it
       float sigma = value[DIMENSION];
       d_spacing = value[DIMENSION+1];

       plane_ui.set_normal( value );
       plane_ui.set_d_sigma( d_spacing, sigma );
     }

     else if ( action.equals( LatticePlaneUI.FFT_SET) )
     {
       if ( filtered_fft_ds == null )
         return;

       LatticePlaneUI plane_ui = (LatticePlaneUI)e.getSource();
       int index = filtered_fft_ds.getPointedAtIndex();
       if ( index < 0 )
         return;
       Data d = filtered_fft_ds.getData_entry(index);

       float value[] = (float[])d.getAttributeValue( NORMAL_ATTRIBUTE );

       Attribute attr = d.getAttribute( D_SPACING_ATTRIBUTE );
       float d_spacing = (float)attr.getNumericValue();

       attr = d.getAttribute( LSQ_ERROR_ATTRIBUTE );
       float sigma = (float)attr.getNumericValue();

       if ( DIMENSION == 3 )
         value[DIMENSION] = 0;

       plane_ui.set_normal( value );      
       plane_ui.set_d_sigma( d_spacing, sigma );
     }

     else if ( action.equals( LatticePlaneUI.FILTER_OFF )  || 
               action.equals( LatticePlaneUI.FILTER_ON )   )
     {
        scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ExtractPeaks();
        draw_Axes();
        vec_Q_space.Draw();
        scene_f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
     }

     else                              // must be request to extract slice 
     {
       LatticePlaneUI plane_ui = (LatticePlaneUI)e.getSource();
       float miller_index = plane_ui.get_miller_index();
       float d_spacing    = plane_ui.get_d_spacing();

       System.out.println("Redraw using miller index : " + miller_index );

       Vector3D normal = new Vector3D( plane_ui.get_normal() );
       if ( normal == null || normal.length() < 0.99 )
         return;

       String title      = null;
       ImageFrame2 frame = null;
       Vector3D    base  = null;
       Vector3D    up    = null;

       if ( plane_ui == h_plane_ui )
       {
         base = new Vector3D( k_plane_ui.get_normal() );
         up   = new Vector3D( l_plane_ui.get_normal() );
         if ( base == null || base.length() < 0.99 ||
                up == null || up.length()   < 0.99 )
           return;
         frame = h_frame;
         title = "h = " + miller_index;
       }
       else if ( plane_ui == k_plane_ui )
       {
         base = new Vector3D( l_plane_ui.get_normal() );
         up   = new Vector3D( h_plane_ui.get_normal() );
         if ( base == null || base.length() < 0.99 ||
                up == null || up.length()   < 0.99 )
           return;
         frame = k_frame;
         title = "k = " + miller_index;
       }
       else  // plane_ui == l_plane_ui
       {
         base = new Vector3D( h_plane_ui.get_normal() );
         up   = new Vector3D( k_plane_ui.get_normal() );
         if ( base == null || base.length() < 0.99 ||
                up == null || up.length()   < 0.99 )
           return;
         frame = l_frame;
         title = "l = " + miller_index;
       }

       Vector3D origin = new Vector3D( normal );
       origin.multiply((float)(miller_index * Math.PI * 2/d_spacing) );
       float image[][] = make_slice( origin, normal, base, up );
       IVirtualArray2D va2d = new VirtualArray2D( image );

       // ##### patch
       va2d.setAxisInfo( AxisInfo.X_AXIS, -SLICE_SIZE_IN_Q, SLICE_SIZE_IN_Q,
                         " ","Change in |Q|", AxisInfo.LINEAR );
       va2d.setAxisInfo( AxisInfo.Y_AXIS, -SLICE_SIZE_IN_Q, SLICE_SIZE_IN_Q,
                         " ","Change in |Q|", AxisInfo.LINEAR );

       va2d.setTitle( title );
       if ( frame == null )
         frame = new ImageFrame2( va2d );
       else 
         frame.setData( va2d );

       if ( plane_ui == h_plane_ui )
         h_frame = frame;
       else if ( plane_ui == k_plane_ui )
         k_frame = frame;
       else  // plane_ui == l_plane_ui
         l_frame = frame;
     }
   }
 }


/* ------------------------ FFTListener ---------------------------- */
/**
 *  Listen to the FFT DataSet and record the ID & Normal information
 *  from the "Pointed At" Data block.
 */
private class FFTListener implements IObserver
{
  public void update( Object observed_object, Object reason )
  {
    float q_spacing = 1;

    if ( reason instanceof String && observed_object instanceof DataSet )
     if ( reason.equals( IObserver.POINTED_AT_CHANGED ) )
     {
       int index = ((DataSet)observed_object).getPointedAtIndex();
       Data d = ((DataSet)observed_object).getData_entry(index);
       Object value = d.getAttributeValue( NORMAL_ATTRIBUTE );
       if ( value == null )
         System.out.println( "ERROR: Missing normal " );

       if ( value instanceof float[] )
       {
         Vector3D normal = new Vector3D( (float[])value );
//         System.out.println( "Normal for this Data block is " + normal );
/*
         Polyline normal_line[] = new Polyline[1];
         Vector3D verts[] = new Vector3D[2];
         verts[0] = origin_vec.getVector();
         verts[1] = origin_vec.getVector();
         verts[1].add(normal);

         normal_line[0] = new Polyline( verts, Color.cyan );
         vec_Q_space.setObjects( "Selected Normal", normal_line );
*/
         Attribute attr = d.getAttribute( FREQUENCY_ATTRIBUTE );
         if ( attr instanceof FloatAttribute )
         {
           float max_chan = ((FloatAttribute)attr).getFloatValue();
           q_spacing = (2*SLICE_SIZE_IN_Q)/(max_chan);
//           System.out.println("old Q-spacing = " + q_spacing );
//           System.out.println("old d-spacing = " + 2*Math.PI/q_spacing );
           Vector3D poly_verts[] = new Vector3D[15];
           poly_verts[0] = new Vector3D( origin_vec.getVector() );
           Vector3D step_vec = new Vector3D( normal );
           step_vec.multiply( q_spacing );
           for ( int i = 1; i < poly_verts.length; i++ )
           {
             poly_verts[i] = new Vector3D( poly_verts[i-1] );
             poly_verts[i].add( step_vec );
           }
           Polymarker plane_marks = 
             new Polymarker( vec_Q_space, poly_verts, Polymarker.BOX, q_spacing/5 );
           plane_marks.setColor( CYAN );
           vec_Q_space.setObject( "Selected Plane Spacing", plane_marks );

           float values[] = refinePlane( normal, q_spacing );
//           System.out.print("Refined normal values : " );
//           for ( int i = 0; i < values.length; i++ )
//             System.out.print(" " + values[i] );
//             System.out.println();
           Vector3D new_normal = new Vector3D( values );
           new_normal.normalize();

//           attr = d.getAttribute( LSQ_ERROR_ATTRIBUTE );
//           if ( attr != null )
//             System.out.println("LSQ ERROR = " + attr.getNumericValue() );
         }
         else
         {
           vec_Q_space.removeObjects( "Selected Plane Spacing" );
//           System.out.println("Removing plane marks" );
         }

         Redraw();
       }
     }
  }
}


  /* ---------------------------- main ---------------------------------- */
  public static void main( String args[] )
  {
    /*
    GL_RecipPlaneView viewer = new GL_RecipPlaneView();
   
    viewer.parseArgs( args );
    if ( viewer.threshold.length() > 0 )
      try
      {
        viewer.peak_threshold = (new Float(viewer.threshold)).floatValue();
        viewer.peak_threshold  = Math.abs( viewer.peak_threshold );
        if ( viewer.peak_threshold < viewer.SLIDER_MIN )
        {
          viewer.peak_threshold = viewer.SLIDER_DEF;
          System.out.println("threshold less than " + viewer.SLIDER_MIN +
        viewer.SetThresholdScale( (int)viewer.peak_threshold );
      }
      catch ( Exception e )
      {
        System.out.println("Invalid threshold value, ignored");
      }

    if ( viewer.border_size.length() > 0 )
      try
      {
        viewer.edge_pix = (new Integer(viewer.border_size)).intValue();
        if ( viewer.edge_pix < 0 )
          throw new Exception("");
      }
      catch ( Exception e )
      {
        System.out.println("Invalid border size, ignored");
      }

    viewer.loadFiles(); 
    viewer.initialize( true ); 
    */

    String file_name = "/usr2/SCD_TEST/scd08336.run";
    RunfileRetriever rr = new RunfileRetriever( file_name );
    DataSet ds_arr[] = new DataSet[1];
    ds_arr[0] = rr.getDataSet(2);
    System.out.println("Loaded " + ds_arr[0] );
    GL_RecipPlaneView viewer = new GL_RecipPlaneView(ds_arr,60);
    viewer.initialize( true ); 
  }

}
