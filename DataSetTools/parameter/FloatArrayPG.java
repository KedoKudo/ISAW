/*
 * File:  FloatArrayPG.java
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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA and by
 * the National Science Foundation under grant number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 *
 * $Log$
 * Revision 1.16  2005/06/10 15:27:40  rmikk
 * Gave a more descriptive label for what is to be entered
 *
 * Revision 1.15  2005/06/07 15:05:47  rmikk
 * Made the initial button better  represent the data to be entered
 *
 * Revision 1.14  2005/04/23 13:13:12  rmikk
 * Converted all initial values to a Vector of Floats or null if not possible
 *
 * Revision 1.13  2004/05/11 18:23:48  bouzekc
 * Added/updated javadocs and reformatted for consistency.
 *
 * Revision 1.12  2003/12/16 00:06:00  bouzekc
 * Removed unused imports.
 *
 * Revision 1.11  2003/10/11 19:19:16  bouzekc
 * Removed clone() as the superclass now implements it using reflection.
 *
 * Revision 1.10  2003/09/09 23:06:28  bouzekc
 * Implemented validateSelf().
 *
 * Revision 1.9  2003/08/28 03:38:40  bouzekc
 * Changed innerParameter assignment to call to setParam().
 *
 * Revision 1.8  2003/08/28 02:32:36  bouzekc
 * Modified to work with new VectorPG.
 *
 * Revision 1.7  2003/08/15 23:50:04  bouzekc
 * Modified to work with new IParameterGUI and ParameterGUI
 * classes.  Commented out testbed main().
 *
 * Revision 1.6  2003/06/23 20:18:14  bouzekc
 * Added GPL info.
 *
 */
package DataSetTools.parameter;
import java.util.*;
/**
 * Subclass of VectorPG to deal with one-dimensional float arrays.
 */
public class FloatArrayPG extends VectorPG {
  //~ Constructors *************************************************************

  /**
   * Creates a new FloatArrayPG object.
   *
   * @param name The name of this FloatArrayPG.
   * @param val The value of this FloatArrayPG.
   */
  public FloatArrayPG( String name, Object val ) {
    super( name, FloatArrayPG.cnvrtFloat(val) );
    setParam( new FloatPG( "Enter a Float", 0.0f ) );
  }

  /**
   * Creates a new FloatArrayPG object.
   *
   * @param name The name of this FloatArrayPG.
   * @param val The value of this FloatArrayPG.
   * @param valid True if this FloatArrayPG should be considered initially
   *        valid.
   */
  public FloatArrayPG( String name, Object val, boolean valid ) {
    super( name, FloatArrayPG.cnvrtFloat(val), valid );
    setParam( new FloatPG( "Enter Float", 0.0f ) );
  }

  //~ Methods ******************************************************************
   /**
    * Converts an Object to a Vector of Floats or null if this is not possible
    *
    */
   private  static java.util.Vector cnvrtFloat( Object val ){
     if( val == null)
       return null;
      if( val.getClass().isArray()){
        Vector vval = new Vector();
        for(int i=0; i< java.lang.reflect.Array.getLength( val); i++)
          vval.addElement( java.lang.reflect.Array.get(val,i));
        return cnvrtFloat( vval);
      }else if( val instanceof java.util.Vector){
         Vector vval =(Vector)val;
         for (int i=0; i< vval.size(); i++)
            if( (vval).elementAt(i) instanceof Float){}
            else if(vval.elementAt(i) instanceof Number){
              float f  = ((Number)vval.elementAt(i)).floatValue();
              vval.setElementAt( new Float(f), i);
            }else
              return null;
         return vval;     
              
      }else
        return null;
   }
  /*
   * Testbed.
   */
  /*public static void main( String args[] ){
     JFrame jf = new JFrame("Test");
     jf.getContentPane().setLayout( new GridLayout( 1,2));
     FloatArrayPG IaPg = new FloatArrayPG( "Enter Int list", null);
     IaPg.initGUI(null);
     jf.getContentPane().add(IaPg.getGUIPanel());
     JButton  jb = new JButton("Result");
     jf.getContentPane().add(jb);
     jb.addActionListener( new PGActionListener( IaPg));
     jf.setSize( 500,100);
     jf.invalidate();
     jf.show();
     }*/

  /**
   * Validates this FloatArrayPG.  A FloatArrayPG is considered valid if it
   * contains all Float elements.
   */
  public void validateSelf(  ) {
    validateElements( new Float( 0.0f ).getClass(  ) );
  }
}
