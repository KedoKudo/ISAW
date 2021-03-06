/*
 * File:  Retriever.java
 *
 * Copyright (C) 1999-2004, Dennis Mikkelson
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
 *  Revision 1.14  2004/05/07 18:41:47  dennis
 *  Reset default mode to load all attributes.
 *
 *  Revision 1.13  2004/04/12 21:27:43  dennis
 *  Set default to NOT include diagnostic attributes.
 *  Added DETECTOR_CEN_ANGLE and DETECTOR_CEN_DISTANCE to
 *  java docs of the attribute levels.
 *
 *  Revision 1.12  2004/04/09 19:26:55  dennis
 *  Added "add_sas_attrs" flag and "ANALYSIS" level.
 *
 *  Revision 1.11  2004/04/08 22:32:32  dennis
 *  Added final Strings and boolean flags to control the level of
 *  attributes included with DataSets retrieved.  The levels are set
 *  by the SetAttrLevel() method.
 *
 *  Revision 1.10  2002/11/27 23:23:16  pfpeterson
 *  standardized header
 *
 *  Revision 1.9  2002/10/10 21:10:50  dennis
 *  Added method getDataSet( data_set_num, ids[] ).  At this base class
 *  level, it just calls getDataSet( data_set_num ), and issues a warning
 *  message, so that derived classes can still function without implementing
 *  this method.  All derived classes should eventually implement this.
 *
 */

package DataSetTools.retriever;

import  DataSetTools.dataset.*;
import  java.io.*;

/**
 * Base class for objects that retrieve DataSet objects from files, or via 
 * a network connection.  Derived classes for particular types of data
 * sources must actually implement the methods to get specified DataSets
 * and their types.  
 */

public abstract class Retriever implements Serializable
{

    public static final String  NONE = "NONE";
    public static final String  MINIMAL_VISUALIZATION = "MINIMAL VISUALIZATION";
    public static final String  MINIMAL_ANALYSIS = "MINIMAL ANALYSIS";
    public static final String  TOFNSAS_ANALYSIS = "TOFNSAS ANALYSIS";
    public static final String  TOFNPD_ANALYSIS  = "TOFNPD  ANALYSIS";
    public static final String  TOFNSCD_ANALYSIS = "TOFNSCD ANALYSIS";
    public static final String  TOFNDGS_ANALYSIS = "TOFNDGS ANALYSIS";
    public static final String  TOFNIGS_ANALYSIS = "TOFNIGS ANALYSIS";
    public static final String  ANALYSIS         = "ANALYSIS";
    public static final String  DIAGNOSTIC       = "DIAGNOSTIC";

    protected static boolean add_vis_attrs = true;
    protected static boolean add_run_attrs = true;
    protected static boolean add_sas_attrs = true;
    protected static boolean add_npd_attrs = true;
    protected static boolean add_scd_attrs = true;
    protected static boolean add_dgs_attrs = true;
    protected static boolean add_igs_attrs = true;
    protected static boolean add_diagnostic_attrs = true;

    public static final int  INVALID_DATA_SET      = 0;
    public static final int  MONITOR_DATA_SET      = 1;
    public static final int  HISTOGRAM_DATA_SET    = 2;
    public static final int  PULSE_HEIGHT_DATA_SET = 3;

    public static boolean  debug_retriever = false;

    protected String data_source_name = null;

    /* ------------------------ Constructor -------------------------- */
    /**
     * Construct the retriever for the specified source name.
     *
     * @param data_source_name   This identifies the data source.  For file
     *                           data retrievers, this should be the fully 
     *                           qualified file name
     */
    public Retriever( String data_source_name )
    {
      this.data_source_name = data_source_name;
    }


    /* ------------------------ numDataSets -------------------------- */
    /**
     * Get the number of distinct DataSets that can be obtained from the
     * current data source.
     *
     *  @return The number of distinct DataSets available.  This function
     *          may return values < 0 as an error code if there are no
     *          DataSets available.
     */
    public abstract int numDataSets();

    
    /* -------------------------- getDataSet ---------------------------- */
    /**
     * Get the specified DataSet from the current data source.
     *
     * @param data_set_num  The number of the DataSet in this runfile
     *                      that is to be read from the runfile.  data_set_num
     *                      must be between 0 and numDataSets()-1
     *
     * @return The specified DataSet, if it exists, or null if no such
     *         DataSet exists.
     */
    public abstract DataSet getDataSet( int data_set_num );

  
    /* -------------------------- getDataSet ---------------------------- */
    /**
     *  Get a DataSet from the current data source containing only the the 
     *  specified group IDs from within the specified DataSet.
     *  NOTE: The list of group IDs must be in increasing order.
     *
     *  @param  data_set_num  The number of the DataSet in this runfile
     *                        that is to be read from the runfile.  data_set_num
     *                        must be between 0 and numDataSets()-1
     *
     *  @param  ids           The list of group IDs from the specified DataSet
     *                        that are to be read from the runfile and returned
     *                        in the DataSet, in increasing order.
     *
     *  @return a DataSet containing only the specified groups, if the 
     *          data_set_num and ID list specify a non-empty set of 
     *          Data blocks, or null otherwise.
     */
    public DataSet getDataSet( int data_set_num, int ids[] )
    {
      System.out.println("Warning: getDataSet( data_set_num, ids ) not " +
                         "implemented in this retriever, defaulting " +
                         "getting all ids " );
      return getDataSet( data_set_num );
    }


    /* ---------------------------- getType ------------------------------ */
    /**
     *  Get the type code of a particular DataSet in this runfile.
     *  The type codes include:
     *
     *     Retriever.INVALID_DATA_SET
     *     Retriever.MONITOR_DATA_SET
     *     Retriever.HISTOGRAM_DATA_SET
     *     Retriever.PULSE_HEIGHT_DATA_SET
     *
     *  @param  data_set_num  The number of the DataSet in this runfile whose
     *                        type code is needed.  data_set_num must be between
     *                        0 and numDataSets()-1
     *
     *  @return the type code for the specified DataSet.
     */
    public abstract int getType( int data_set_num );


    /* ----------------------- SetAttrLevel --------------------------- */
    /**
     *  Specify how many attributes are to be included when DataSets are
     *  retrieved, base on a string code.  The common attributes for
     *  Time-of-Flight Neutron Scattering experiments can be grouped as:<BR>
     *  <table border="1">
     * <tr><td align="center"> --- 1--- </td></tr>
     *  
     *  <tr><td>GROUP_ID</td><td>( required )</td></tr>
     *
     *  <tr><td align="center"> --- 2 (vis_attrs) --- </td></tr>
     *  
     *  <tr><td>PIXEL_INFO_LIST</td><td>( Used by newer codes, keeps info on pixels<Br>
     *                        used and references to their data grids. This<Br>
     *                        has methods to calculate effective position,<Br>
     *                        total solid angles and delta 2 theta, so this<Br>
     *                        "should" replace other attributes.  )</td></tr>
     * <tr><td> DELTA_2THETA </td><td>( used for "True Angle" view and QE display<br>
     *                        should be replaced by data grid methods )</td></tr>
     *
     * <tr><td align="center"> --- 3(run_attrs) --- </td>
     * <tr><td> RUN_NUM</td><td>( shared )</td></tr>
     *  <tr><td>INITIAL_PATH</td><td>( shared )</td></tr>
     *  <tr><td>DETECTOR_POS</td><td>( per group, weighted by solid angles, should be<br>
     *                         replaced by pixel info list method )</td></tr>
     *  <tr><td>TOTAL_COUNTS</td><td>( used for SAND, UpStreamMonitorID, etc. )</td></tr>
     *
     * <tr><td align="center"> --- 4 (npd_attrs ) --- </td></tr>
     * <tr><td> RAW_ANGLE </td><td>( used for GSAS )</td></tr>
     *   <tr><td>OMEGA </td><td>( only added for TOF_DIFFRACTOMETER )</td></tr>
     *
     * <tr><td align="center"> --- 5 (scd_attrs) --- </td></tr>
     *   <tr><td>NUMBER_OF_PULSES</td><td>( shared, used for SCD )</td></tr>
     *   <tr><td>SAMPLE_ORIENTATION</td><td>( only added for SCD, shared by all spectra in run)</td></tr>
     *   <tr><td>DETECTOR_CEN_DISTANCE</td></tr>
     *   <tr><td>DETECTOR_CEN_ANGLE</td></tr>
     *
     * <tr><td align="center"> --- 6 (dgs_attrs) --- </td></tr>
     *   <tr><td>RAW_DISTANCE</td><td>( used for HRMCS )</td></tr>
     *   <tr><td>SOLID_ANGLE</td><td>( total of group, used by HRMCS )</td></tr>
     *   <tr><td>NOMINAL_ENERGY_IN</td><td>( only added for TOF_DG_SPECTROMETER )</td></tr>
     *   <tr><td>ENERGY_IN</td><td>( only added for TOF_DG_SPECTROMETER )</td></tr>
     *   <tr><td>NOMINAL_SOURCE_TO_SAMPLE_TOF</td><td>( only added for TOF_DG_SPECTROMETER )</td></tr>
     *   <tr><td>SOURCE_TO_SAMPLE_TOF</td><td>( only added for TOF_DG_SPECTROMETER )</td></tr>
     *
     * <tr><td align="center"> --- 7 (igs_attrs ) --- </td></tr>
     *   <tr><td>ENERGY_OUT</td><td>( only added for TOF_IDG_SPECTROMETER )</td></tr>
     *
     * <tr><td align="center"> --- 9  --- (diagnostic_attrs)</td></tr>
     *   <tr><td>CRATE</td></tr>
     *  <tr><td>SLOT</td></tr>
     *   <tr><td>INPUT</td></tr>
     *   <tr><td>DETECTOR_IDS </td><td>( can be replaced by pixel info list methods, if<br>
     *                         the grid IDs and the detector IDs match )
     *   <tr><td>SEGMENT_IDS</td><td>( can "almost" be replaced by pixel info list<br>
     *                         and data grid methods.  The numbering scheme<br>
     *                         may not match the ids in the runfiles. )</td></tr>
     *  </table>
     *
     *  @param level  <ul>The string specifying the attributes that are to be
     *                included.  This should be one of the following Strings:
     *               <li> "NONE"                     means 1
     *               <li> "MINIMAL VISUALIZATION"    means 1,2
     *               <li> "MINIMAL ANALYSIS"         means 1,2,3
     *               <li> "TOFNSAS ANALYSIS"         means 1,2,3
     *               <li> "TOFNPD  ANALYSIS"         means 1,2,3,4,8
     *               <li> "TOFNSCD ANALYSIS"         means 1,2,3,5
     *               <li> "TOFNDGS ANALYSIS"         means 1,2,3,6,8
     *               <li> "TOFNIGS ANALYSIS"         means 1,2,3,7
     *               <li> "ANALYSIS"                 means 1,2,3,4,5,6,7,8
     *               <li> "DIAGNOSTIC"               means everything
     *         </ul>
     *  @return This returns true if a valid level was specified and returns
     *          false otherwise.  If the level was not valid, it will 
     *          default to "DIAGNOSTIC", and all possible attributes will
     *          be included.
     */
    public static boolean SetAttrLevel( String level )
    {  
       SetAttrFlags( false );
       if ( level.equalsIgnoreCase( NONE ) )
         return true;

       add_vis_attrs = true;
       if ( level.equalsIgnoreCase( MINIMAL_VISUALIZATION ) )
         return true; 
      
       add_run_attrs = true;
       if ( level.equalsIgnoreCase( MINIMAL_ANALYSIS ) )
         return true;

       if ( level.equalsIgnoreCase( TOFNSAS_ANALYSIS ) )
       {
         add_sas_attrs = true;
         return true;
       }
       else if ( level.equalsIgnoreCase( TOFNPD_ANALYSIS ) )
       {
         add_npd_attrs = true;
         return true;
       }
       else if ( level.equalsIgnoreCase( TOFNSCD_ANALYSIS ) )
       {
         add_scd_attrs = true;
         return true;
       }
       else if ( level.equalsIgnoreCase( TOFNDGS_ANALYSIS ) )
       {
         add_dgs_attrs = true;
         return true;
       }
       else if ( level.equalsIgnoreCase( TOFNIGS_ANALYSIS ) )
       {
         add_igs_attrs = true;
         return true;
       }
       else if ( level.equalsIgnoreCase( ANALYSIS ) )
       {
         add_sas_attrs = true;
         add_npd_attrs = true;
         add_scd_attrs = true;
         add_dgs_attrs = true;
         add_igs_attrs = true;
         return true;
       }
                               // default to everything, but return false 
                               // as an error flag if invalid request
       SetAttrFlags( true ); 
       if ( level.equalsIgnoreCase( DIAGNOSTIC ) )
         return true;
       else
         return false;
    }


    /* -------------------------- SetAttrFlags ---------------------------- */
    /**
     *  Select whether to include all or none of the possible attributes
     *  when DataSets are obtained.
     *
     *  @param  onoff  If true, include all possible attributes, if false,
     *                 include no attributes.
     */
    static public void SetAttrFlags( boolean onoff )
    {
      add_vis_attrs = onoff; 
      add_run_attrs = onoff;
      add_sas_attrs = onoff;
      add_npd_attrs = onoff;
      add_scd_attrs = onoff;
      add_dgs_attrs = onoff;
      add_igs_attrs = onoff;
      add_diagnostic_attrs = onoff;
    }

}

