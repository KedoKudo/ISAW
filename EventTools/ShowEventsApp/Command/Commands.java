package EventTools.ShowEventsApp.Command;

public class Commands 
{
  public static final String LOAD_FILE = "LOAD_FILE";

  public static final String ADD_EVENTS = "ADD_EVENTS";
  public static final String CLEAR_HISTOGRAM = "CLEAR_HISTOGRAM";
  
  public static final String ADD_EVENTS_TO_VIEW = "ADD_EVENTS_TO_VIEW";
  public static final String CLEAR_EVENTS_VIEW = "CLEAR_EVENTS_VIEW";

  public static final String SET_WEIGHTS_FROM_HISTOGRAM = 
                                              "SET_WEIGHTS_FROM_HISTOGRAM";
  public static final String SET_WEIGHTS_FROM_HISTOGRAM_ACK = 
                                              "SET_WEIGHTS_FROM_HISTOGRAM_ACK";

  public static final String SELECT_POINT = "SELECT_POINT";
  public static final String SELECTED_POINT_INFO = "SELECTED_POINT_INFO";

  /**
   *  An ADD_HISTOGRAM_INFO message will be sent to request that some class 
   *  (HistogramHandler) should find the histogram intensity at each
   *  Qxyz point in a Vector of IPeak objects, and set that intensity
   *  in the IPeak objects.  
   *  The class doing this (HistogramHandler) should a send an 
   *  acknowlegement, ADD_HISTOGRAM_INFO_ACK, and return the modified Vector 
   *  of IPeak objects, when it has finished.
   *  This can also be applied to ONE SelectionInfoCmd object, in which
   *  case it sets the counts field and the histogram page.
   */
  public static final String ADD_HISTOGRAM_INFO = "ADD_HISTOGRAM_INFO";
  public static final String ADD_HISTOGRAM_INFO_ACK = "ADD_HISTOGRAM_INFO_ACK";

  public static final String FIND_PEAKS     = "FIND_PEAKS";
  public static final String FIND_PEAKS_ACK = "FIND_PEAKS_ACK";
  
  public static final String INDEX_PEAKS = "INDEX_PEAKS";
  public static final String MARK_PEAKS = "MARK_PEAKS";
  public static final String WRITE_PEAK_FILE = "WRITE_PEAK_FILE";

  public static final String CHANGE_PANEL = "CHANGE_PANEL";
  public static final String SET_COLOR_SCALE = "SET_COLOR_SCALE";
  public static final String SET_FILTER_OPTIONS = "SET_FILTER_OPTIONS";
  public static final String SET_ORIENTATION_MATRIX = "SET_ORIENTATION_MATRIX";
  public static final String PLANE_CHANGED = "PLANE_CHANGED";
  public static final String SLICE_MODE_CHANGED = "SLICE_MODE_CHANGED";
  public static final String SET_SLICE_1 = "SET_SLICE_1";
  
  /*
//  public static final String SET_POSITION_INFO = "SET_POSITION_INFO";
  public static final String SET_SLICE_2 = "Set Slice 2";
  public static final String SET_SLICE_3 = "Set Slice 3";  
  public static final String SET_COLOR_TABLE = "Set New Color Table";
  public static final String SET_VIEW = "Set View";
  public static final String SET_MARKER = "Set New Marker";
  public static final String SET_WAYPOINT = "Set New Waypoint";
  public static final String SET_HISTOGRAM = "Set New Histogram";
  public static final String ROTATE_CAM_ABOUT_COP = "Rotate cam about cop";
  public static final String ROTATE_CAM_ABOUT_VRP = "Rotate cam about vrp";
  public static final String SHOW_SLICE_CONTROL = "Show Slice Control";
  public static final String LOAD_MATRIX = "Load Orientation Matrix";
  public static final String EXIT = "Exit";
  public static final String SHOW_SLICE = "Show Slice";
  public static final String SHOW_LOAD_FILE = "Show Load File";
  public static final String LOAD_PEAK_FILE = "Load Peak File";
  public static final String WRITE_ORIENTATION_FILE = "Write Orientation File";
  public static final String WRITE_INDEX_FILE = "Write Index File";
  public static final String OMIT_PEAKS = "Omit Peaks";*/
  //*/
}
