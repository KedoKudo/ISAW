/*
 * File:  DataSetCrossSection.java 
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
 *  Revision 1.10  2006/07/10 21:28:22  dennis
 *  Removed unused imports, after refactoring the PG concept.
 *
 *  Revision 1.9  2006/07/10 16:25:55  dennis
 *  Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 *  Revision 1.8  2004/03/15 06:10:49  dennis
 *  Removed unused import statements.
 *
 *  Revision 1.7  2004/03/15 03:28:30  dennis
 *  Moved view components, math and utils to new source tree
 *  gov.anl.ipns.*
 *
 *  Revision 1.6  2002/11/27 23:18:38  pfpeterson
 *  standardized header
 *
 *  Revision 1.5  2002/11/18 21:42:48  dennis
 *  Added getDocumentation() method, trivial main() program, and
 *  documentation for getResult() method. (Tyler Stelzer)
 *
 *  Revision 1.4  2002/09/19 16:01:54  pfpeterson
 *  Now uses IParameters rather than Parameters.
 *
 *  Revision 1.3  2002/03/13 16:19:17  dennis
 *  Converted to new abstract Data class.
 *
 *  Revision 1.2  2002/03/05 19:25:43  pfpeterson
 *  Updated @see references in javadocs.
 *
 *  Revision 1.1  2002/02/22 21:02:29  pfpeterson
 *  Operator reorganization.
 *
 */

package DataSetTools.operator.DataSet.Math.Analyze;

import gov.anl.ipns.MathTools.*;
import gov.anl.ipns.Parameters.FloatPG;
import gov.anl.ipns.Parameters.IParameter;
import gov.anl.ipns.Util.SpecialStrings.*;

import  java.io.*;
import  java.util.Vector;
import  DataSetTools.dataset.*;
import  DataSetTools.operator.Parameter;

/**
  *  Form a new DataSet that contains the integrated crossection of the
  * current DataSet.
  *
  *  This operator calculates the integral over a specified interval for each
  * Data block in a DataSet and forms a new DataSet with one entry: a Data
  * block whose value at each of the original Data blocks is the value of the
  * integral for the original Data block.  The new Data block will have an
  * X-Scale taken from an attribute of one of the original Data blocks.  The
  * integral values will be ordered according to increasing attribute value.
  * If several Data blocks have the same value of the attribute, their integral
  * values are averaged.  For example, if the "Raw Detector Angle" attribute
  * is used and several Data blocks have the same angle value, the integral
  * values for that angle are averaged to form the y-value that corresponds to
  * that angle in the new Data block.
  *
  *  @see DataSetTools.operator.DataSet.DataSetOperator
  *  @see DataSetTools.operator.Operator
  */

public class DataSetCrossSection extends    AnalyzeOp 
                                 implements Serializable
{
  /* --------------------------- CONSTRUCTOR ------------------------------ */
  /**
   * Construct an operator with a default parameter list.  If this
   * constructor is used, the operator must be subsequently added to the
   * list of operators of a particular DataSet.  Also, meaningful values for
   * the parameters should be set ( using a GUI ) before calling getResult()
   * to apply the operator to the DataSet this operator was added to.
   */

  public DataSetCrossSection( )
  {
    super( "Integrated Cross Section" );
  }


  /* ---------------------- FULL CONSTRUCTOR ---------------------------- */
  /**
   *  Construct an operator for a specified DataSet and with the specified
   *  parameter values so that the operation can be invoked immediately 
   *  by calling getResult().
   *
   *  @param  ds          The DataSet to which the operation is applied
   *  @param  a           The left hand endpoint of the interval [a, b] over
   *                      which each Data block is integrated
   *  @param  b           The righ hand endpoint of the interval [a, b] over
   *                      which each Data block is integrated
   *  @param  attr_name   The name of that attribute to be used for ordering
   *                      the integrated Data block values
   */

  public DataSetCrossSection( DataSet  ds,
                              float    a,
                              float    b,
                              String   attr_name )
  {
    this();                         // do the default constructor, then set
                                    // the parameter value(s) by altering a
                                    // reference to each of the parameters

    IParameter parameter = getParameter( 0 );
    parameter.setValue( new Float( a ) );

    parameter = getParameter( 1 );
    parameter.setValue( new Float( b ) );

    parameter = getParameter( 2 );
    parameter.setValue( new AttributeNameString(attr_name) );

    setDataSet( ds );               // record reference to the DataSet that
                                    // this operator should operate on
  }


  /* ---------------------------- getCommand ------------------------------- */
  /**
   * @return	the command name to be used with script processor: in this case CrossSect
   */
   public String getCommand()
   {
     return "CrossSect";
   }


 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the parameters to default values.
  */
  public void setDefaultParameters()
  {
    parameters = new Vector();  // must do this to clear any old parameters

   // Parameter parameter = new Parameter("Left end point (a)", new Float(0));
    addParameter( new FloatPG("Left end point (a)", new Float(0)) );

   // parameter = new Parameter("Right end point (b)", new Float(0));
    addParameter( new FloatPG("Right end point (b)", new Float(0))  );

    Parameter parameter = new Parameter(
                          "Group Attribute to Order Crossection by",
                           new AttributeNameString(Attribute.RAW_ANGLE) );
    addParameter( parameter );
  }


  /* ---------------------------- getResult ------------------------------- */
  /**
  *  @return returns a new DataSet or an Error String
  *	If the operation is not successful, error strings are returned when
  *	there are no Data groups in DataSet or there is no attribute, 
  */
  

  public Object getResult()
  {
    float a = ( (Float)(getParameter(0).getValue()) ).floatValue();
    float b = ( (Float)(getParameter(1).getValue()) ).floatValue();

    String attr_name = 
                ((AttributeNameString)getParameter(2).getValue()).toString();

                                     // get the current data set
    DataSet ds   = this.getDataSet();
    int num_data = ds.getNum_entries();
    Data data;
    float x_vals[] = null;
    float y_vals[] = null;

    if ( num_data == 0 )
      {
        ErrorString error = new ErrorString( "ERROR: In DataSetCrossSection, " +
                                       " there are no Data groups in " + ds );
        return error;
      }
                                    // now proceed to make the new DataSet
    DataSetFactory factory = new DataSetFactory( ds.getTitle() );
    DataSet new_ds = factory.getDataSet();

    new_ds.setOp_log( ds.getOp_log() );
    new_ds.addLog_entry( "Integrated CrossSection " +
                          a + " to " + b + " " +
                          ds.getX_units() );

    new_ds.setX_label( attr_name );
    if ( attr_name.equals( Attribute.DETECTOR_POS )  ||
         attr_name.equals( Attribute.RAW_ANGLE )     ||
         attr_name.equals( Attribute.TEMPERATURE )    )
      new_ds.setX_units( "Degrees" );
    else
       new_ds.setX_units( attr_name );

                                     // clone the DataSet and sort the clone
                                     // based on the specified attribute.
    ds = (DataSet)ds.clone();
    if ( !ds.Sort( attr_name, true, DataSet.Q_SORT ) )
      {
        ErrorString message = new ErrorString(
                           "ERROR: DataSetCrossSection faiiled...no attribute:"                            + attr_name );

        System.out.println( message );
        return message;
      }

    float  integral_val[]  = new float[num_data];
    float  attribute_val[] = new float[num_data];

                                            // do the integration for each Data 
                                            // block and get the attribute val
    boolean has_histograms = false;
    for ( int i = 0; i < num_data; i++ )
    {
      data = ds.getData_entry( i );        // get reference to the data entry
      x_vals = data.getX_scale().getXs();
      y_vals = data.getY_values();

      if ( x_vals.length == y_vals.length + 1 )  // histogram
      {
        integral_val[i] = NumericalAnalysis.IntegrateHistogram( x_vals, y_vals, 
                                                                a,      b );
        has_histograms = true;
      }
      else                                       // tabulated function
        integral_val[i] = NumericalAnalysis.TrapIntegrate( x_vals, y_vals,
                                                                a,      b );

      AttributeList attr_list = data.getAttributeList();
      Attribute     attr      = attr_list.getAttribute( attr_name );
      attribute_val[i]        = (float)attr.getNumericValue(); 

      if ( attr_name.equalsIgnoreCase( Attribute.DETECTOR_POS ) )
       attribute_val[i] *= 180.0f / (float)Math.PI;     // convert to degrees
    }

                                            // next combine the integral
                                            // results from groups with the 
                                            // same attribute.

    float distinct_integral_val[]  = new float[ num_data ];
    float distinct_attribute_val[] = new float[ num_data ];
    int num_distinct = 0;
    int i = 0;
    while ( i < num_data )
    {
       float total_val = integral_val[i];
       int   num_same  = 1;
       while ( i + 1 < num_data                       && 
               attribute_val[i] == attribute_val[i+1] )
       {
         total_val += integral_val[i+1];
         num_same++;
         i++;
       }
       distinct_integral_val[ num_distinct  ] = total_val/num_same;
       distinct_attribute_val[ num_distinct ] = attribute_val[i];
       num_distinct++;
       i++;
    }

                                            // finally, copy the distinct vals
                                            // into arrays of proper length 
    float x[] = new float[ num_distinct ];
    float y[] = new float[ num_distinct ];

    System.arraycopy( distinct_attribute_val, 0, x, 0, num_distinct );
    System.arraycopy( distinct_integral_val, 0, y, 0, num_distinct );

    XScale x_scale = new VariableXScale( x );
    Data new_data = Data.getInstance( x_scale, y, 0 );

    new_ds.addData_entry( new_data );      
                                            // set the labels and units based
                                            // on whether or not we have
                                            // histograms
    if ( has_histograms ) 
    {
      new_ds.setY_label( "Summed " + ds.getY_label() );
      new_ds.setY_units( ds.getY_units() );
    }
    else
    {
      new_ds.setY_label( "Integrated " + ds.getY_label() );
      new_ds.setY_units( ds.getY_units()+"*"+ds.getX_units() );
    }

    return new_ds;
  }  

  /* ------------------------------ clone ------------------------------- */
  /**
   * Get a copy of the current DataSetCrossSection.  The list of 
   * parameters and the reference to the DataSet to which it applies are 
   * also copied.
   */
  public Object clone()
  {
    DataSetCrossSection new_op = new DataSetCrossSection( );
                                                 // copy the data set associated
                                                 // with this operator
    new_op.setDataSet( this.getDataSet() );
    new_op.CopyParametersFrom( this );

    return new_op;
  }
  
  public String getDocumentation()
  {
    StringBuffer Res = new StringBuffer();
	
    Res.append("@overview Forms a new DataSet that contains the integrated ");
     Res.append("crossection of the current DataSet over the specified interval.");
    
    Res.append("@algorithm This operator calculates the integral over a specified"); 
     Res.append(" interval for each Data block in a DataSet and forms a new DataSet");
     Res.append(" with one entry: a Data block whose value at each of the original");
     Res.append(" Data blocks is the value of the integral for the original Data");
     Res.append(" block.  The new Data block will have an X-Scale taken from an");
     Res.append(" attribute of one of the original Data blocks.  The integral values");
     Res.append(" will be ordered according to increasing attribute value. If several");
     Res.append(" Data blocks have the same value of the attribute, their integral");
     Res.append(" values are averaged.  For example, if the \"Raw Detector Angle\"");
     Res.append(" attribute is used and several Data blocks have the same angle");
     Res.append(" value, the integral values for that angle are averaged to form");
     Res.append(" the y-value that corresponds to that angle in the new Data block.");
    
    Res.append("@param  ds - The DataSet to which the operation is applied");
    Res.append("@param  a - The left hand endpoint of the interval [a, b] over");
     Res.append("which each Data block is integrated");
    Res.append("@param  b - The righ hand endpoint of the interval [a, b] over");
     Res.append(" which each Data block is integrated");
    Res.append("@param  attr_name - The name of that attribute to be used for");
     Res.append(" ordering the integrated Data block values");
   
    Res.append("@return returns a new DataSet or an Error String");
     Res.append(" If the operation is not successful, error strings are returned when");
     Res.append(" there are no Data groups in DataSet or there is no attribute, ");
    
    Res.append("@error No Data groups in DataSetCrossSection");
    Res.append("@error DataSetCrossSection faiiled...no attribute");
  
    return Res.toString();
  }

  /* --------------------------- main ----------------------------------- */
  /*
   *  Main program for testing purposes
   */
  public static void main( String[] args )
  {
    
    DataSetCrossSection op = new DataSetCrossSection();

    String list[] = op.getCategoryList();
    System.out.println( "Categories are: " );
    for ( int i = 0; i < list.length; i++ )
      System.out.println( list[i] );
      
    String documentation = op.getDocumentation();
    System.out.println(documentation);
    
    //THE FOLLOWING CALL TO getResult() USING DEFAULT PARAMETERS 
    //CAUSES A NULL-POINTER EXCEPTION

    //System.out.println("/n" + op.getResult().toString());

  }


}
