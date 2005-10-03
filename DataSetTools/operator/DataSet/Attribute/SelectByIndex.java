/*
 * File: SelectByIndex.java
 *
 * Copyright (C) 2005, Dennis Mikkelson
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
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.1  2005/10/03 02:29:45  dennis
 * Initial version of operator to set or clear selection flags
 * with indices specified by an IntListString.
 *
 */

package DataSetTools.operator.DataSet.Attribute;

import DataSetTools.dataset.Data;
import DataSetTools.dataset.DataSet;
import gov.anl.ipns.Util.SpecialStrings.*;
import gov.anl.ipns.Util.Numeric.*;

import java.io.Serializable;
import java.util.Vector;
import DataSetTools.parameter.*;
import DataSetTools.operator.Parameter;

import Command.*;

/**
 * Selects or de-selects Data objects in a DataSet object based on 
 * their current position(index) in the DataSet.
 */

public class SelectByIndex extends    DS_Attribute
                           implements Serializable
{
  public static final String TITLE   = "Select Groups by Index";

  public static final String CLEAR   = "Clear Selected";
  public static final String SET     = "Set Selected";
  String[] Clear  = { CLEAR, SET };
  

  /* ------------------------ default constructor ----------------------- */
  /**
   * Construct an operator with a default parameter list.  If this construtor
   * is used, meaningful values for the parameters should be set before 
   * calling getResult().
   */
  public SelectByIndex()
  {
    super( TITLE );
  }


  /* ---------------------------- constructor ---------------------------- */
  /**
   *  Construct this operator for the specified DataSet. This operator sets 
   *  or clears the selection flags on all Data blocks with the specified
   *  indices in the DataSet.  This is a "union" type operation in that 
   *  the selection flags of Data block that do not satisfy the criteria are 
   *  NOT changed.  If ONLY the specified groups should have their selection
   *  flags set, it may be necessary to first apply the ClearSelect() operator
   *  to clear all selection flags before using this operator to set the
   *  selection flags on particular Data blocks.
   *
   *  @param  ds         The DataSet to which the operation is applied
   *  @param  indices    String specifying the list of indices for which 
   *                     selected flags are set or cleared.  
   *  @param  clear_set  String specifying if the select flag on a group 
   *                     will be cleared (use "Clear Selected") or 
   *                     set (use "Set Selected")
   */
  public SelectByIndex( DataSet  ds, 
                        String   indices,
                        String   clear_set)
  {
    this();
    setDataSet( ds );
    setDefaultParameters();
    getParameter(0).setValue( new IntListString( indices ) );
    getParameter(1).setValue( clear_set );
  }


  /* --------------------------- getDocumentation ------------------------ */
  /**
   *  Get the end user documentation for this operator.
   *
   *  @return   A multi-line string describing this operator.
   */
  public String getDocumentation()
  {
    StringBuffer Res = new StringBuffer();
    Res.append("@overview This operator, when the getResult method is ");
    Res.append("invoked sets(clears) the selection flag on the data ");
    Res.append("blocks at the specified positions in the DataSet."); 
    Res.append("This is a \"union\" type operation in that the ");
    Res.append("selection flags of groups that do not satisfy the ");
    Res.append("criteria are NOT changed.");
    Res.append("@param ds  The DataSet whose selection flags are to be set\n");
    Res.append("@param indices  The list of indices to set/unset \n");
    Res.append("(eg1:10,15)\n");
    Res.append("@param  clear_set   Determines if the select flag on a ");
    Res.append("group will be Cleared (use \"Clear Selected\") or set (use ");
    Res.append("\"Set Selected\")");
  
    Res.append("@return Success"); 
    
    return Res.toString();
  }


  /* ------------------------ setDefaultParameters ------------------------ */ 
  /**
   *  Set up the parameter list for this operator.  
   */
  public void setDefaultParameters()
  { 
    parameters = new Vector();  //clear old parameters
   
    addParameter( new Parameter( "Indices to Select or Clear", 
                                  new IntListString("-1") ) );

    ChoiceListPG Clear = new ChoiceListPG("Clear?", SET ) ;
      Clear.addItem( SET );
      Clear.addItem( CLEAR );
      addParameter( Clear );
  }


  /* ----------------------------- getResult ---------------------------- */
  /**
   *  Sets or Clears the Select Flags on the Data blocks at the specified
   *  indices.
   *
   *  @return  "Success"
   */
  public Object getResult()
  { 
    DataSet ds        = getDataSet();
    String  index_str = ((IntListString)getParameter(0).getValue()).toString();
    String  clear     = getParameter(1).getValue().toString();

    boolean select = true;
    if( clear == CLEAR )
      select = false;
    
    int index_list[] = IntList.ToArray(index_str);
    for( int i = 0; i < index_list.length; i++)
    {
      Data d = ds.getData_entry( index_list[i] );
      if ( d != null )
        d.setSelected( select );
    }
  
    if ( select )
      ds.addLog_entry("Applied SelectByIndex() operator, " +
                      " to select positions " + index_str );
    else
      ds.addLog_entry("Applied SelectByIndex() operator, " +
                      " to un-select positions " + index_str );

    return "Success";
  }


  /* ------------------------------ main -------------------------------- */
  /**
  *  Test program.
  *
  *   args[0] contains the filename to use
  */
  public static void main( String args[] )
  {
    System.out.println("START OF TEST");

    DataSet[] dss = null;
    try
    {
       dss = ScriptUtil.load( args[0] );
    }
    catch( Exception ss)
    { 
      System.exit(0);
    }

    DataSet ds = dss[ dss.length-1 ];
    System.out.println( "Loaded DataSet " + ds );

    SelectByIndex select_op = new SelectByIndex( ds, "1:10", "Set Selected" );
    select_op.getResult();

    System.out.println("The selected indices should be 1:10" );
    for ( int i = 0; i < ds.getNum_entries(); i++ )
    {
      Data d = ds.getData_entry( i );
      if ( d.isSelected() )
        System.out.print( ", " + i );
    }
    System.out.println();

  }
}