/*
 * File:  Zero.java
 *
 * Copyright (C) 2003, Ruth Mikkelson
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
 * Contact : Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.8  2006/07/10 16:25:59  dennis
 * Change to new Parameter GUIs in gov.anl.ipns.Parameters
 *
 * Revision 1.7  2005/08/24 20:11:30  dennis
 * Added/moved to Macros->Data Set->Edit List menu.
 *
 * Revision 1.6  2004/05/04 19:03:09  dennis
 * Now clears DataSetPG after getting value, to avoid memory leak.
 *
 * Revision 1.5  2004/03/15 19:33:53  dennis
 * Removed unused imports after factoring out view components,
 * math and utilities.
 *
 * Revision 1.4  2004/03/15 03:28:35  dennis
 * Moved view components, math and utils to new source tree
 * gov.anl.ipns.*
 *
 * Revision 1.3  2004/01/24 21:48:04  dennis
 * Add GPL and old log information
 *
 *
 * revision 1.2  2004/01/24 19:58:59  bouzekc
 * Removed/commented out unused variables/imports.
 *
 * revision 1.1  2003/08/25 14:37:47  rmikk
 * Initial Checkin
 */

package DataSetTools.operator.Generic.Special;

import DataSetTools.operator.*;
import DataSetTools.dataset.*;
import DataSetTools.parameter.*;
import gov.anl.ipns.Parameters.IntegerPG;
import gov.anl.ipns.Util.SpecialStrings.*;

import java.util.*;

public class Zero extends GenericSpecial{


    public Zero(){
       super("Zero channels");
    }

    public Zero( DataSet ds, int GroupIndex, int firstChannel, int lastChannel){
       this();
       parameters = new Vector();
       parameters.add( new DataSetPG("Enter Data Set", ds));
       parameters.add( new IntegerPG("Group Index to zero", GroupIndex));
       parameters.add( new IntegerPG("first Channel to zero", firstChannel));
       parameters.add( new IntegerPG("last Channel to zero", lastChannel));

    }
   
   public void setDefaultParameters(){
       parameters = new Vector();
       parameters.add( new DataSetPG("Enter Data Set", null));
       parameters.add( new IntegerPG("Group Index to zero", new Integer(0)));
       parameters.add( new IntegerPG("first Channel to zero", new Integer(0)));
       parameters.add( new IntegerPG("last Channel to zero", new Integer(10)));

    }


   /* ---------------------------- getCategoryList -------------------------- */
   /**
    *  Get the list of categories describing where this operator should appear
    *  in the menu system.
    *
    *  @return an array of strings listing the menu where the operator 
    *  should appear.
    */
    public String[] getCategoryList()
    {
      return Operator.DATA_SET_EDIT_LIST_MACROS;
    }


   public Object getResult(){

     DataSet ds = (DataSet)(getParameter(0).getValue());
     ((DataSetPG)getParameter(0)).clear();    // needed to avoid memory leak

     int Group = ((Integer)(getParameter(1).getValue())).intValue();
     int firstIndex = ((Integer)(getParameter(2).getValue())).intValue();
     int lastIndex = ((Integer)(getParameter(3).getValue())).intValue();

     if( ds == null)
       return new ErrorString(" No DataSet Selected");
     if( Group < 0)
       return new ErrorString( "Improper Group Index");
     if( Group >= ds.getNum_entries())
       return new ErrorString("No such Group Index");

     Data D = ds.getData_entry( Group);
     if( firstIndex > lastIndex)
       return new ErrorString("first Index must be less than last index");
     if( firstIndex < 0)
       firstIndex = 0;
     if( lastIndex >= D.getY_values().length)
       lastIndex = D.getY_values().length-1;
     float[] yvalues = D.getY_values();
     for( int i = firstIndex; i <= lastIndex; i++)
        yvalues[i]=0.0f;

     return "Success";
   }

}
