/*
 * File:  SpectrometerPlotter
 *
 * Copyright (C) 2000, Dongfeng Chen
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
 * Revision 1.6  2001/08/15 18:35:17  rmikk
 * Redid to show several spectra and also to use Kevin's
 * SelectedGraphView from the ViewManager
 *
 * Revision 1.5  2001/06/01 21:18:00  rmikk
 * Improved documentation for getCommand() method
 *
 * Revision 1.4  2001/04/26 19:11:18  dennis
 * Added copyright and GPL info at the start of the file.
 * 
 */

package DataSetTools.operator;

import  java.io.*;
import  java.util.Vector;
import  DataSetTools.dataset.*;
import  DataSetTools.math.*;
import  DataSetTools.util.*;
import  DataSetTools.viewer.*;

/** Draws selected Spectra to a window<P>
* The Title, <B>Data Plotter</b>, refers to this operator in Menu's<BR>
* The Command, <B>Plot</b>, refers to this operator in Scripts
*/
public class SpectrometerPlotter extends    DataSetOperator 
                                            implements Serializable
{

  public SpectrometerPlotter( )
  {
    super( "Data Plotter" );
  }

    /** Constructor used in Java code to set up this operator.  Use
    *   getResult to execute the operator.
    *@param ds  the data set which is to be plotted
    *@param GroupIndx  <Ul>The list of Groups( their indices) that are to 
    *                       be plotted.  Ex: 1,3:5</ul>
    */
    public SpectrometerPlotter( DataSet ds, IntListString GroupIndx)
    {
	super( "Data Plotter");
        parameters = new Vector();
        setDataSet( ds);
        addParameter( new Parameter("Indices", GroupIndx));

    }
 /**
  *  Set the parameters to default values.
  */
  public void setDefaultParameters()
  {
    parameters = new Vector();  // must do this to clear any old parameters

    Parameter parameter;

    parameter = new Parameter( "Indices",new IntListString("1,3:5" ) );
    addParameter( parameter );
  }


  /* ---------------------------- getCommand ------------------------------- */
  /**Returns <B>Plot</b>, the command used in Scripts to refer to this operator
   * @return the command name to be used with script processor: in this case,\    * Plot
   */
   public String getCommand()
   {
     return "Plot";
   }


  /* ---------------------------- getResult ------------------------------- */
  /**  Executes the operator using the parameters that were set up
  *@return  "Success" if there were no errors otherwise  the ErrorString
  *             "No Data Set Selected" is returned.<P>
  *
  *NOTE: A SelectedGraph View will also pop up
  */
  public Object getResult()
  {
                                     // get the current data set
     /*DataSetTools.dataset.DataSet ds = this.getDataSet();
     int   det_ID = ( (Integer)(getParameter(0).getValue()) ).intValue() ;
     
    ChopTools.chop_dataDrawer.drawgraphDataEntry(ds, det_ID);
    return null;
    */
    //Will use Kevin's Selected Graph View\
    DataSet ds = getDataSet( );
    if( ds == null)
       return new ErrorString( "No Data Set Selected");
    IntListString Ilist = (IntListString)(getParameter(0).getValue());
    // Set the selected groups
     DataSetOperator op = new SetField( ds,
			new DSSettableFieldString( 
                        DSFieldString.SELECTED_GROUPS), Ilist );
     op.getResult();
    //Call Kevin's Viewer
    
   ViewManager Vm= new ViewManager( ds, IViewManager.SELECTED_GRAPHS);
   return "Success";
  }  

  /* ------------------------------ clone ------------------------------- */
  /**
   * Get a copy of the current SpectrometerPlotter Operator.  The list 
   * of parameters and the reference to the DataSet to which it applies are
   * also copied.
   */
  public Object clone()
  {
    SpectrometerPlotter new_op = new SpectrometerPlotter( );
                                                 // copy the data set associated
                                                 // with this operator
    new_op.setDataSet( this.getDataSet() );
    new_op.CopyParametersFrom( this );

    return new_op;
  }
}
