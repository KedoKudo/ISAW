/*
 * File:  ChooserPG.java 
 *
 * Copyright (C) 2003, Peter F. Peterson
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
 *  $Log$
 *  Revision 1.17  2003/09/16 22:46:53  bouzekc
 *  Removed addition of this as a PropertyChangeListener.  This is already done
 *  in ParameterGUI.  This should fix the excessive events being fired.
 *
 *  Revision 1.16  2003/09/15 18:15:25  dennis
 *  Moved addItem() call from constructors to initGUI(). (Ruth)
 *
 *  Revision 1.15  2003/09/13 23:29:46  bouzekc
 *  Moved calls from setValid(true) to validateSelf().
 *
 *  Revision 1.14  2003/09/13 23:16:40  bouzekc
 *  Removed calls to setEnabled in initGUI(Vector), since ParameterGUI.init()
 *  already calls this.
 *
 *  Revision 1.13  2003/09/12 20:22:01  rmikk
 *  AddItem(one item only) now adds the item to the entrywidget too. If the
 *    ParameterGUI has been initialized, new entries can be
 *    added afterwards.
 *
 *  Revision 1.12  2003/09/09 23:06:28  bouzekc
 *  Implemented validateSelf().
 *
 *  Revision 1.11  2003/08/28 02:28:11  bouzekc
 *  Removed setEnabled() method.
 *
 *  Revision 1.10  2003/08/28 01:40:28  bouzekc
 *  Fixed bug in constructor where the passed in value was not added.
 *
 *  Revision 1.9  2003/08/28 01:36:56  bouzekc
 *  Modified to work with new ParameterGUI.
 *
 *  Revision 1.8  2003/08/22 20:12:07  bouzekc
 *  Modified to work with EntryWidget.
 *
 *  Revision 1.7  2003/08/15 23:56:23  bouzekc
 *  Modified to work with new IParameterGUI and ParameterGUI.
 *
 *  Revision 1.6  2003/08/15 03:55:34  bouzekc
 *  Removed unnecessary initialized=true statement.
 *
 *  Revision 1.5  2003/08/02 04:52:23  bouzekc
 *  Fixed bug in init() which caused a reinitialization every time entrywidget
 *  was shown.  Now properly updates the GUI when init() is called.
 *
 *  Revision 1.4  2003/06/05 22:34:34  bouzekc
 *  Added method to retrieve the index of a given item.
 *
 *  Revision 1.3  2003/03/25 19:39:57  pfpeterson
 *  Fixed bug with updating the DataSets listed in the combo box by
 *  allowing multiple calls to init.
 *
 *  Revision 1.2  2003/03/03 16:32:06  pfpeterson
 *  Only creates GUI once init is called.
 *
 *  Revision 1.1  2003/02/24 20:58:31  pfpeterson
 *  Added to CVS.
 *
 */

package DataSetTools.parameter;

import java.util.Vector;
import DataSetTools.components.ParametersGUI.HashEntry;
import DataSetTools.dataset.DataSet;
import DataSetTools.components.ParametersGUI.EntryWidget;

/**
 * This is a superclass to take care of many of the common details of
 * Parameter GUIs that use a combobox.
 */
abstract public class ChooserPG extends ParameterGUI{
  // static variables
  private   static String TYPE     = "Chooser";
  protected static int    DEF_COLS = 20;

  // instance variables
  protected Vector vals=null;

  // ********** Constructors **********
  public ChooserPG(String name, Object val){
    super(name, val);
    //this.addItem(val);
    setValue(val);
    this.type=TYPE;
  }

  public ChooserPG(String name, Object val, boolean valid){
    super(name, val, valid);
    //this.addItem(val);
    setValue(val);
    this.type=TYPE;
  }

  // ********** Methods to deal with the hash **********

  /**
   * Add a single item to the vector of choices.
   */
  public void addItem( Object val){
    if(this.vals==null) this.vals=new Vector(); // initialize if necessary
    if(val==null) return; // don't add null to the vector
    if(this.vals.indexOf(val)<0) {this.vals.add(val);
    if( initialized)
       ((HashEntry)(entrywidget.getComponent(0))).addItem( val);}
  }

  /**
   * Add a set of items to the vector of choices at once.
   */
  public void addItems( Vector values){
    for( int i=0 ; i<values.size() ; i++ ){
      addItem(values.elementAt(i));
    }
  }

  /**
   * Remove an item from the hash based on its key.
   */
  public void removeItem( Object val ){
    int index=vals.indexOf(val);
    if(index>=0) vals.remove(index);
  }

  /**
   *  Get the index of an item.
   */
  public int getIndex(Object val){
    return vals.indexOf(val);
  }

  // ********** IParameter requirements **********

  /**
   * Returns the value of the selected item if this ParameterGUI has been
   * initialized.  Otherwise, it returns the internal value.
   */
  public Object getValue(){
    Object value=null;
    if(this.initialized){
      value=((HashEntry)(entrywidget.getComponent(0))).getSelectedItem();
    }else{
      value=this.value;
    }
    return value;
  }

  /**
   * Sets the value of the parameter.
   */
  public void setValue(Object value){
    if(this.initialized){
      if(value==null){
        // do nothing
      }else{
        ((HashEntry)(entrywidget.getComponent(0))).setSelectedItem(value);
      }
    }else{
      this.value=value;
    }
    validateSelf();
  }

  // ********** IParameterGUI requirements **********
  /**
   * Allows for initialization of the GUI after instantiation.
   */
  public void initGUI(Vector init_values){
    if(this.initialized) return;
    addItem(getValue());
    if(init_values!=null && init_values.size()>0){
      this.vals=new Vector();
      if(this.value!=null && this.value!=DataSet.EMPTY_DATA_SET)
        this.addItem(this.value);
      if(init_values.size()==1){
        this.setValue(init_values.elementAt(0));
      }else{
        for( int i=0 ; i<init_values.size() ; i++ ){
          this.addItem(init_values.elementAt(i));
        }
      }
    }else{
      // something is not right, should throw an exception
    }

    // set up the combobox
    entrywidget=new EntryWidget(new HashEntry(this.vals));
    super.initGUI();
    //GUI won't properly update without this
    setValue(value);
  }

  /**
   * Since this is an array parameter, better allow an array to
   * initialize the GUI.
   */
  public void initGUI(Object init_values[]){
    Vector init_vec=new Vector();
    for( int i=0 ; i<init_values.length ; i++ ){
      init_vec.add(init_values[i]);
    }
    initGUI(init_vec);
  }

  /**
   * Validates this ChooserPG.  This just checks to be sure that getValue()
   * does not return null.  A derived class may want to do more stringent
   * checks.
   */
  public void validateSelf(  ) {
    setValid( ( getValue(  ) != null ) );
  }
}
