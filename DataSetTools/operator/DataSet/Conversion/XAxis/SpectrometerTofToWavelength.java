/*
 * File:  SpectrometerTofToWavelength.java 
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
 * $Log$
 * Revision 1.2  2002/03/13 16:19:17  dennis
 * Converted to new abstract Data class.
 *
 * Revision 1.1  2002/02/22 21:00:58  pfpeterson
 * Operator reorganization.
 *
 * Revision 1.9  2001/06/01 21:18:00  rmikk
 * Improved documentation for getCommand() method
 *
 * Revision 1.8  2001/04/26 19:11:43  dennis
 * Added copyright and GPL info at the start of the file.
 *
 * Revision 1.7  2000/11/10 22:41:34  dennis
 *    Introduced additional abstract classes to better categorize the operators.
 * Existing operators were modified to be derived from one of the new abstract
 * classes.  The abstract base class hierarchy is now:
 *
 *  Operator
 *
 *   -GenericOperator
 *      --GenericLoad
 *      --GenericBatch
 *
 *   -DataSetOperator
 *     --DS_EditList
 *     --DS_Math
 *        ---ScalarOp
 *        ---DataSetOp
 *        ---AnalyzeOp
 *     --DS_Attribute
 *     --DS_Conversion
 *        ---XAxisConversionOp
 *        ---YAxisConversionOp
 *        ---XYAxesConversionOp
 *     --DS_Special
 *
 *    To allow for automatic generation of hierarchial menus, each new operator
 * should fall into one of these categories, or a new category should be
 * constructed within this hierarchy for the new operator.
 *
 * Revision 1.6  2000/08/08 21:20:21  dennis
 * Now propagate errors, rather than set them to SQRT(counts)
 *
 * Revision 1.5  2000/08/02 01:42:44  dennis
 * Changed to use Data.ResampleUniformly() so that the operation can be
 * applied to functions as well as to histograms.
 *
 * Revision 1.4  2000/07/10 22:36:23  dennis
 * Now Using CVS 
 *
 * Revision 1.13  2000/06/09 16:12:35  dennis
 * Added getCommand() method to return the abbreviated command string for
 * this operator
 *
 * Revision 1.12  2000/05/25 18:48:16  dennis
 * Fixed bug: DataSet attributes were not copied properly.
 *
 * Revision 1.11  2000/05/16 15:36:34  dennis
 * Fixed clone() method to also copy the parameter values from
 * the current operator.
 *
 * Revision 1.10  2000/05/15 21:43:45  dennis
 * now uses constant Parameter.NUM_BINS rather than the string
 * "Number of Bins"
 *
 * Revision 1.9  2000/05/11 16:41:28  dennis
 * Added RCS logging
 * 
 * 2000/04/21  Added methods to set better
 *             default parameters. Now it
 *             is derived from the class
 *             XAxisConversionOp
 *
 * 99/08/16    Added constructor to allow
 *             calling operator directly
 */

package DataSetTools.operator.DataSet.Conversion.XAxis;

import  java.io.*;
import  java.util.Vector;
import  DataSetTools.dataset.*;
import  DataSetTools.math.*;
import  DataSetTools.util.*;
import  DataSetTools.operator.Parameter;

/**
 * This operator converts a neutron time-of-flight DataSet for a Spectrometer
 * to wavelength.  The DataSet must contain spectra with an attribute giving 
 * the detector position. In addition, it is assumed that the XScale for the 
 * spectra represents the time-of-flight from the SAMPLE to the detector. 
 *
 */

public class SpectrometerTofToWavelength extends    XAxisConversionOp 
                                         implements Serializable
{
  /* ------------------------ DEFAULT CONSTRUCTOR -------------------------- */
  /**
   * Construct an operator with a default parameter list.  If this
   * constructor is used, the operator must be subsequently added to the
   * list of operators of a particular DataSet.  Also, meaningful values for
   * the parameters should be set ( using a GUI ) before calling getResult()
   * to apply the operator to the DataSet this operator was added to.
   */

  public SpectrometerTofToWavelength( )
  {
    super( "Convert to Wavelength" );
  }

  /* ---------------------- FULL CONSTRUCTOR ---------------------------- */
  /**
   *  Construct an operator for a specified DataSet and with the specified
   *  parameter values so that the operation can be invoked immediately
   *  by calling getResult().
   *
   *  @param  ds          The DataSet to which the operation is applied
   *  @param  min_wl      The minimum wavelength value to be binned
   *  @param  max_wl      The maximum wavelength value to be binned
   *  @param  num_wl      The number of "bins" to be used between min_wl and
   *                      max_wl
   */

  public SpectrometerTofToWavelength( DataSet     ds,
                                      float       min_wl,
                                      float       max_wl,
                                      int         num_wl )
  {
    this();                         // do the default constructor, then set
                                    // the parameter value(s) by altering a
                                    // reference to each of the parameters

    Parameter parameter = getParameter( 0 );
    parameter.setValue( new Float( min_wl ) );

    parameter = getParameter( 1 );
    parameter.setValue( new Float( max_wl ) );

    parameter = getParameter( 2 );
    parameter.setValue( new Integer( num_wl ) );

    setDataSet( ds );               // record reference to the DataSet that
                                    // this operator should operate on
  }


  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return the command name to be used with script processor: in this case, ToWL
   */
   public String getCommand()
   {
     return "ToWL";
   }


 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the parameters to default values.
  */
  public void setDefaultParameters()
  {
    UniformXScale scale = getXRange();

    parameters = new Vector();  // must do this to clear any old parameters
    Parameter parameter;

    if ( scale == null )
      parameter = new Parameter( "Min Wavelength(A)", new Float(0.0) );
    else
      parameter = new Parameter( "Min Wavelength(A)",
                                  new Float(scale.getStart_x()) );
    addParameter( parameter );

    if ( scale == null )
      parameter = new Parameter( "Max Wavelength(A)", new Float(5.0) );
    else
      parameter = new Parameter( "Max Wavelength(A)",
                                  new Float(scale.getEnd_x()) );
    addParameter( parameter );

    parameter = new Parameter( Parameter.NUM_BINS, new Integer( 500 ) );
    addParameter( parameter );
  }

  /* -------------------------- new_X_label ---------------------------- */
  /**
   * Get string label for converted x values.
   *
   *  @return  String describing the x label and units for converted x values.
   */
   public String new_X_label()
   {
     return new String( "\u03bb" + "(A)" );
   }


  /* ---------------------- convert_X_Value ------------------------------- */
  /**
   * Evaluate the axis conversion function at one point only.
   *
   *  @param  x    the x-value where the axis conversion function is to be
   *               evaluated.
   *
   *  @param  i    the index of the Data block for which the axis conversion
   *               function is to be evaluated.
   *
   *  @return  the value of the axis conversion function at the specified x.
   */
  public float convert_X_Value( float x, int i )
  {
    DataSet ds = this.getDataSet();          // make sure we have a DataSet
    if ( ds == null )
      return Float.NaN;

    int num_data = ds.getNum_entries();      // make sure we have a valid Data
    if ( i < 0 || i >= num_data )            // index
      return Float.NaN;

    Data data               = ds.getData_entry( i );
    AttributeList attr_list = data.getAttributeList();

                                             // get the detector position
    DetectorPosition position=(DetectorPosition)
                       attr_list.getAttributeValue( Attribute.DETECTOR_POS);

    if( position == null )                             // make sure it has the
      return Float.NaN;                                // needed attributes
                                                       // to convert it to D

    float spherical_coords[] = position.getSphericalCoords();

    return tof_calc.Wavelength( spherical_coords[0], x );
  }



  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {
                                     // get the current data set
    DataSet ds = this.getDataSet();
                                     // construct a new data set with the same
                                     // title, units, and operations as the
                                     // current DataSet, ds
    DataSetFactory factory = new DataSetFactory( 
                                     ds.getTitle(),
                                     "Angstroms",
                                     "Wavelength",
                                     "Counts",
                                     "Scattering Intensity" );

    // #### must take care of the operation log... this starts with it empty
    DataSet new_ds = factory.getDataSet(); 
    new_ds.copyOp_log( ds );
    new_ds.addLog_entry( "Converted to Wavelength" );

    // copy the attributes of the original data set
    new_ds.setAttributeList( ds.getAttributeList() );

                                     // get the wavelength scale parameters 
    float min_wl = ( (Float)(getParameter(0).getValue()) ).floatValue();
    float max_wl = ( (Float)(getParameter(1).getValue()) ).floatValue();
    int   num_wl = ( (Integer)(getParameter(2).getValue()) ).intValue() + 1;

                                     // validate wavelength bounds
    if ( min_wl > max_wl )             // swap bounds to be in proper order
    {
      float temp = min_wl;
      min_wl = max_wl;
      max_wl = temp;
    }

    UniformXScale new_wl_scale;
    if ( num_wl < 2 || min_wl >= max_wl )      // no valid scale set
      new_wl_scale = null;
    else
      new_wl_scale = new UniformXScale( min_wl, max_wl, num_wl );  

                                            // now proceed with the operation 
                                            // on each data block in DataSet 
    Data             data,
                     new_data;
    DetectorPosition position;
    float            y_vals[];            // y_values from one spectrum
    float            errors[];            // errors from one spectrum
    float            wl_vals[];           // wavelength values at bin boundaries
                                          // calculated from tof bin bounds
    XScale           wl_scale;
    float            spherical_coords[];
    int              num_data = ds.getNum_entries();
    AttributeList    attr_list;

    for ( int j = 0; j < num_data; j++ )
    {
      data = ds.getData_entry( j );        // get reference to the data entry
      attr_list = data.getAttributeList();

                                           // get the detector position and
                                           // initial path length 
      position=(DetectorPosition)
                   attr_list.getAttributeValue(Attribute.DETECTOR_POS);

      if( position != null )           // has needed attributes so convert it 
                                       // to wavelength
      { 
                                       // calculate wavelength at bin boundaries
        spherical_coords = position.getSphericalCoords();
        wl_vals          = data.getX_scale().getXs();
        for ( int i = 0; i < wl_vals.length; i++ )
          wl_vals[i] = tof_calc.Wavelength( spherical_coords[0], wl_vals[i] );
  
        wl_scale = new VariableXScale( wl_vals );

        y_vals = data.getY_values();
        errors = data.getErrors();

        new_data = Data.getInstance( wl_scale, 
                                     y_vals, 
                                     errors, 
                                     data.getGroup_ID() );
                                                 // create new data block with 
                                                 // non-uniform E_scale and 
                                                 // the original y_vals.
        new_data.setAttributeList( attr_list );  // copy the attributes

                                                 // resample if a valid
        if ( new_wl_scale != null )              // scale was specified
          new_data.resample( new_wl_scale, IData.SMOOTH_NONE ); 

        new_ds.addData_entry( new_data );      
      }
    }

    return new_ds;
  }  


  /* ------------------------------ clone ------------------------------- */
  /**
   * Get a copy of the current SpectrometerTofToWavelength Operator.  The list 
   * of parameters and the reference to the DataSet to which it applies are
   * also copied.
   */
  public Object clone()
  {
    SpectrometerTofToWavelength new_op = new SpectrometerTofToWavelength( );
                                                 // copy the data set associated
                                                 // with this operator
    new_op.setDataSet( this.getDataSet() );
    new_op.CopyParametersFrom( this );

    return new_op;
  }

}
