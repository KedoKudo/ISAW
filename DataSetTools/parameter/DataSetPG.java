/*
 * File:  DataSetPG.java 
 *
 * Copyright (C) 2002, Peter F. Peterson
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
 * Contact : Peter F. Peterson <pfpeterson@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.16  2003/12/15 01:51:44  bouzekc
 *  Removed unused imports.
 *
 *  Revision 1.15  2003/11/19 04:13:22  bouzekc
 *  Is now a JavaBean.
 *
 *  Revision 1.14  2003/10/11 19:22:11  bouzekc
 *  Removed clone() as the superclass now implements it using reflection.
 *
 *  Revision 1.13  2003/09/15 18:19:26  dennis
 *  Added test for this.vals != null in clone() method. (Ruth)
 *
 *  Revision 1.12  2003/09/13 23:29:46  bouzekc
 *  Moved calls from setValid(true) to validateSelf().
 *
 *  Revision 1.11  2003/09/09 23:06:28  bouzekc
 *  Implemented validateSelf().
 *
 *  Revision 1.10  2003/08/22 20:12:07  bouzekc
 *  Modified to work with getEntryWidget().
 *
 *  Revision 1.9  2003/08/15 23:50:04  bouzekc
 *  Modified to work with new IParameterGUI and ParameterGUI
 *  classes.  Commented out testbed main().
 *
 *  Revision 1.8  2003/03/26 23:19:37  pfpeterson
 *  Implements IObserver so can drop references to DESTROYed DataSets.
 *  Also improved error checking in addItem and setValue.
 *
 *  Revision 1.7  2003/03/25 19:40:47  pfpeterson
 *  Sets value to EMPTY_DATA_SET when attempt is made to set value to null.
 *
 *  Revision 1.6  2003/02/24 20:59:14  pfpeterson
 *  Now extends ChooserPG rather than ArrayPG.
 *
 *  Revision 1.5  2002/11/27 23:22:42  pfpeterson
 *  standardized header
 *
 *  Revision 1.4  2002/10/10 22:11:50  pfpeterson
 *  Fixed a bug with the clone method not getting the choices copied over.
 *
 *  Revision 1.3  2002/10/07 15:27:36  pfpeterson
 *  Another attempt to fix the clone() bug.
 *
 *  Revision 1.2  2002/09/30 15:20:46  pfpeterson
 *  Update clone method to return an object of this class.
 *
 *  Revision 1.1  2002/08/01 18:40:03  pfpeterson
 *  Added to CVS.
 *
 *
 */

package DataSetTools.parameter;

import DataSetTools.components.ParametersGUI.HashEntry;
import DataSetTools.dataset.*;
import DataSetTools.util.SharedData;
import DataSetTools.util.IObserver;

/**
 * This is a superclass to take care of many of the common details of
 * Array Parameter GUIs.
 */
public class DataSetPG extends ChooserPG implements IObserver{
    // static variables
    private   static String TYPE     = "DataSet";
    protected static int    DEF_COLS = ChooserPG.DEF_COLS;

    // ********** Constructors **********
    public DataSetPG(String name, Object value){
        super(name,value);
        this.setType(TYPE);
        if(value==null || value==DataSet.EMPTY_DATA_SET) return;
        if(!(value instanceof DataSet))
            SharedData.addmsg("WARN: Non-"+this.getType()
                              +" in DataSetPG constructor");
    }

    public DataSetPG(String name, Object value, boolean valid){
        super(name,value,valid);
        this.setType(TYPE);
        if(value==null || value==DataSet.EMPTY_DATA_SET) return;
        if(!(value instanceof DataSet))
            SharedData.addmsg("WARN: Non-"+this.getType()
                              +" in DataSetPG constructor");
    }

    // ********** Methods to deal with the hash **********

    /**
     * Add a single DataSet to the vector of choices. This calls the
     * superclass's method once it confirms the value to be added is a
     * DataSet.
     */
    public void addItem( Object val){
      if(val==null){
        super.addItem(DataSet.EMPTY_DATA_SET);
      }else{
        if(val instanceof DataSet){
          super.addItem(val);
          ((DataSet)val).addIObserver(this);
        }else{
          throw new ClassCastException(val+" cannot be cast as a DataSet");
        }
      }
    }

    // ********** IObserver requirements **********
    public void update(Object observed, Object reason){
      if( !(reason instanceof String) ) return; // reason should be a string
      if( ! (IObserver.DESTROY.equals((String)reason)) )
        return;                                      // must be a destroy event
      if( !(observed instanceof DataSet) ) return; // must be a DataSet

      // -- remove references to the DataSet
      // from choices
      this.vals.remove(observed);
      // from GUI
      if(this.getInitialized()){
        ((HashEntry)getEntryWidget().getComponent(0)).removeItem(observed);
      }
      // from the value      
      if(getValue()==observed){
        if(this.vals!=null && this.vals.size()>0)
          setValue(this.vals.elementAt(0)); // set to first choice
        else
          setValue(DataSet.EMPTY_DATA_SET); // or empty dataset
      }

      // stop listening
      ((DataSet)observed).deleteIObserver(this);
    }

    // ********** IParameter requirements **********

    /**
     * Returns the value of the parameter. While this is a generic
     * object specific parameters will return appropriate
     * objects. There can also be a 'fast access' method which returns
     * a specific object (such as String or DataSet) without casting.
     */
    public DataSet getDataSetValue(){
        Object value=this.getValue();
        if(value instanceof DataSet){
            return (DataSet)value;
        }else{
            return null;
        }
    }

    /**
     * Calls the parent method with DataSet.EMPTY_DATA_SET if value is
     * null.
     */
    public void setValue(Object value){
      if(value==null){
        super.setValue(DataSet.EMPTY_DATA_SET);
      }else{
        if( value instanceof DataSet )
          super.setValue(value);
        else
          throw new ClassCastException(value+" cannot be cast as a DataSet");
      }
    }

    /*
     * Main method for testing purposes.
     */
    /*public static void main(String args[]){
        DataSetPG fpg;
        int y=0, dy=70;

        String filename=null;
        if(args.length==1){
            filename=args[0];
        }else{
            filename="/home/students/bouzekc/ISAW/SampleRuns/SCD06497.RUN";
        }

        RunfileRetriever rr=new RunfileRetriever(filename);
        DataSet[] ds=new DataSet[rr.numDataSets()];
        for( int i=0 ; i<rr.numDataSets() ; i++ ){
            ds[i]=rr.getDataSet(i);
        }

        fpg=new DataSetPG("a",ds[0]);
        System.out.println(fpg);
        fpg.initGUI(ds);
        fpg.showGUIPanel(0,y);
        y+=dy;

        fpg=new DataSetPG("b",ds[0]);
        System.out.println(fpg);
        fpg.setEnabled(false);
        fpg.initGUI(ds);
        fpg.showGUIPanel(0,y);
        y+=dy;

        fpg=new DataSetPG("c",ds[0],false);
        System.out.println(fpg);
        fpg.setEnabled(false);
        fpg.initGUI(ds);
        fpg.showGUIPanel(0,y);
        y+=dy;

        fpg=new DataSetPG("d",ds[0],true);
        System.out.println(fpg);
        fpg.setDrawValid(true);
        fpg.initGUI(ds);
        fpg.showGUIPanel(0,y);
        y+=dy;
    }*/

    /**
     * Validates this DataSetPG.  A DataSetPG is considered valid if its value
     * is not null and is not a DataSet.EMPTY_DATA_SET.
     */
    public void validateSelf(  ) {
      Object obj = getValue(  );

      if( ( obj != null ) && ( obj != DataSet.EMPTY_DATA_SET ) ) {
        setValid( true );
      } else {
        setValid( false );
      }
    }
}
