/*
 * @(#)DataSetSort.java   0.2  99/07/28   Dennis Mikkelson
 *                             99/08/16   Added constructor to allow
 *                                        calling operator directly
 *             
 * ---------------------------------------------------------------------------
 *  $Log$
 *  Revision 1.4  2000/07/10 22:36:00  dennis
 *  July 10, 2000 version... many changes
 *
 *  Revision 1.9  2000/06/09 16:12:35  dennis
 *  Added getCommand() method to return the abbreviated command string for
 *  this operator
 *
 *  Revision 1.8  2000/06/08 15:25:59  dennis
 *  Changed type casting of attribute names from (SpecialString) to
 *  (AttributeNameString).
 *
 *  Revision 1.7  2000/05/16 15:36:34  dennis
 *  Fixed clone() method to also copy the parameter values from
 *  the current operator.
 *
 *  Revision 1.6  2000/05/11 16:41:28  dennis
 *  Added RCS logging
 *
 *
 */

package DataSetTools.operator;

import  java.io.*;
import  java.util.Vector;
import  DataSetTools.util.*;
import  DataSetTools.dataset.*;

/**
  * This operator sorts a DataSet based on an attribute of the Data entries.
  */

public class DataSetSort  extends    DataSetOperator 
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

  public DataSetSort( )
  {
    super( "Sort on ONE group attribute" );
  }

  /* ---------------------- FULL CONSTRUCTOR ---------------------------- */
  /**
   *  Construct an operator for a specified DataSet and with the specified
   *  parameter values so that the operation can be invoked immediately 
   *  by calling getResult().
   *
   *  @param  ds          The DataSet to which the operation is applied
   *  @param  attr_name   The name of that attribute to be used for the
   *                      sort criterion
   *  @param  increasing  Flag that indicates whether the sort should put
   *                      the Data blocks in increasing or decreasing order
   *                      based on the specified attribute
   *  @param  make_new_ds Flag that determines whether the sort creates a
   *                      new DataSet and returns the new DataSet as a value,
   *                      or just does the sort "in place" and just returns
   *                      a message indicating the sort was done.
   */

  public DataSetSort( DataSet   ds,
                      String    attr_name,
                      boolean   increasing,
                      boolean   make_new_ds   )
  {
    this();                         // do the default constructor, then set
                                    // the parameter value(s) by altering a
                                    // reference to each of the parameters

    Parameter parameter = getParameter( 0 );
    parameter.setValue( new AttributeNameString(attr_name) );

    parameter = getParameter( 1 );
    parameter.setValue( new Boolean( increasing ) );

    parameter = getParameter( 2 );
    parameter.setValue( new Boolean( make_new_ds ) );

    setDataSet( ds );               // record reference to the DataSet that
                                    // this operator should operate on
  }


  /* ---------------------------- getCommand ------------------------------- */
  /**
   * Returns the abbreviated command string for this operator.
   */
   public String getCommand()
   {
     return "Sort";
   }


 /* -------------------------- setDefaultParmeters ------------------------- */
 /**
  *  Set the parameters to default values.
  */
  public void setDefaultParameters()
  {
    parameters = new Vector();  // must do this to clear any old parameters

    Parameter parameter = new Parameter("Group Attribute to Sort on",
                               new AttributeNameString(Attribute.RAW_ANGLE) );
    addParameter( parameter );

    parameter = new Parameter("Sort in Increasing Order?", new Boolean(true) );
    addParameter( parameter );

    parameter = new Parameter("Create new DataSet?", new Boolean(false) );
    addParameter( parameter );
  }


  /* ---------------------------- getResult ------------------------------- */

  public Object getResult()
  {                                  // get the parameters

    String attr_name = 
              ((AttributeNameString)getParameter(0).getValue()).toString();
    boolean increasing  = ((Boolean)getParameter(1).getValue()).booleanValue();
    boolean make_new_ds = ((Boolean)getParameter(2).getValue()).booleanValue();

    DataSet ds     = this.getDataSet();

    DataSet new_ds = ds;             // set new_ds to either a reference to ds
    if ( make_new_ds )               // or a clone of ds
      new_ds = (DataSet)ds.clone();


    if ( new_ds.Sort(attr_name, increasing) )
    {                                          // if sort worked ok, return
                                               // new_ds, or message string
      new_ds.addLog_entry( "Sorted by " + attr_name );
      if ( make_new_ds )                           
        return new_ds;
      else
      {
        new_ds.notifyIObservers( IObserver.DATA_REORDERED );
        return new String("DataSet sorted");
      }                           
    }
    else
      {
        ErrorString message = new ErrorString(
                           "ERROR: Sort failed... no attribute: " + attr_name );
        System.out.println( message );
        return message;
      }
  }  

  /* ------------------------------ clone ------------------------------- */
  /**
   * Get a copy of the current DataSetSort Operator.  The list of 
   * parameters and the reference to the DataSet to which it applies are 
   * also copied.
   */
  public Object clone()
  {
    DataSetSort new_op    = new DataSetSort( );
                                                 // copy the data set associated
                                                 // with this operator
    new_op.setDataSet( this.getDataSet() );
    new_op.CopyParametersFrom( this );

    return new_op;
  }


}
