/*
 * @(#)IntegrateGroup.java   0.2  99/07/26   Dennis Mikkelson
 *                                99/08/16   Added constructor to allow
 *                                           calling operator directly
 *                               2000/06/09  Renamed from just Integrate
 *             
 * $Log$
 * Revision 1.1  2000/07/10 22:36:10  dennis
 * July 10, 2000 version... many changes
 *
 * Revision 1.2  2000/06/09 16:12:35  dennis
 * Added getCommand() method to return the abbreviated command string for
 * this operator
 *
 * Revision 1.1  2000/06/09 14:58:19  dennis
 * Initial revision
 *
 * Revision 1.5  2000/06/01 21:43:26  dennis
 * fixed error in documentation.
 *
 * Revision 1.4  2000/05/16 15:36:34  dennis
 * Fixed clone() method to also copy the parameter values from
 * the current operator.
 *
 * Revision 1.3  2000/05/11 16:41:28  dennis
 * Added RCS logging
 *
 */

package DataSetTools.operator;

import  java.io.*;
import  java.util.Vector;
import  DataSetTools.dataset.*;
import  DataSetTools.util.*;
import  DataSetTools.math.*;

/**
  *  This operator calculates the integral of the data values of one Data 
  *  block.  The Group ID of the Data block to be integrated is specified by 
  *  the parameter "Group ID".  The interval [a,b] over which the integration 
  *  is done is specified by the two endpoints a, b where it is assumed that
  *  a < b.  This operator just produces a numerical result that is displayed 
  *  in the operator dialog box.
  */

public class  IntegrateGroup  extends    DataSetOperator 
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

  public IntegrateGroup( )
  {
    super( "Integrate a group" );
  }

  /* ---------------------- FULL CONSTRUCTOR ---------------------------- */
  /**
   *  Construct an operator for a specified DataSet and with the specified
   *  parameter values so that the operation can be invoked immediately
   *  by calling getResult().
   *
   *  @param  ds          The DataSet to which the operation is applied
   *  @param  group_id    The group_id of the Data block that is to be
   *                      integrated
   *  @param  a           The left hand endpoint of the interval [a, b] over
   *                      which each Data block is integrated
   *  @param  b           The righ hand endpoint of the interval [a, b] over
   *                      which each Data block is integrated
   */

  public IntegrateGroup( DataSet      ds,
                         int          group_id,
                         float        a,
                         float        b  )
  {
    this();                         // do the default constructor, then set
                                    // the parameter value(s) by altering a
                                    // reference to each of the parameters
    Parameter parameter = getParameter(0);
    parameter.setValue( new Integer( group_id ) );

    parameter = getParameter( 1 );
    parameter.setValue( new Float( a ) );

    parameter = getParameter( 2 );
    parameter.setValue( new Float( b ) );

    setDataSet( ds );               // record reference to the DataSet that
                                    // this operator should operate on
  }


  /* ---------------------------- getCommand ------------------------------- */
  /**
   * Returns the abbreviated command string for this operator.
   */
   public String getCommand()
   {
     return "IntegGrp";
   }


 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the parameters to default values.
  */
  public void setDefaultParameters()
  {
    parameters = new Vector();  // must do this to clear any old parameters

    Parameter parameter = new Parameter("Group ID to Integrate",new Integer(0));
    addParameter( parameter );

    parameter = new Parameter("Left end point (a)", new Float(0));
    addParameter( parameter );

    parameter = new Parameter("Right end point (b)", new Float(0));
    addParameter( parameter );
  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {                                  // get the parameters

    int group_id = ( (Integer)(getParameter(0).getValue()) ).intValue();

    float a = ( (Float)(getParameter(1).getValue()) ).floatValue();
    float b = ( (Float)(getParameter(2).getValue()) ).floatValue();

                                     // get the current data set and do the 
                                     // operation
    DataSet ds = this.getDataSet();

    Data data = ds.getData_entry_with_id( group_id );
    if ( data == null )
    {
      ErrorString message = new ErrorString( 
                           "ERROR: no data entry with the group_ID "+group_id );
      System.out.println( message );
      return message;
    }
    else
    {
      float x_vals[] = data.getX_scale().getXs();
      float y_vals[] = data.getY_values();

      float result = NumericalAnalysis.IntegrateHistogram(x_vals, y_vals, a, b);
      return new Float( result );  
    }
  }  

  /* ------------------------------ clone ------------------------------- */
  /**
   * Get a copy of the current IntegrateGroup Operator.  The list of 
   * parameters and the reference to the DataSet to which it applies are 
   * also copied.
   */
  public Object clone()
  {
    IntegrateGroup new_op = new IntegrateGroup( );
                                                 // copy the data set associated
                                                 // with this operator
    new_op.setDataSet( this.getDataSet() );
    new_op.CopyParametersFrom( this );

    return new_op;
  }


}
