/*
 * File:  DetPosAttribute.java
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
 *  $Log$
 *  Revision 1.6  2001/04/25 19:03:44  dennis
 *  Added copyright and GPL info at the start of the file.
 *
 *  Revision 1.5  2000/07/26 14:52:21  dennis
 *  Now includes method to add() attributes.
 *
 *  Revision 1.4  2000/07/18 18:17:34  dennis
 *  Rewrote using the toString() method of DetectorPosition.java
 *
 *  Revision 1.3  2000/07/10 22:23:56  dennis
 *  Now using CVS 
 *
 *  Revision 1.7  2000/05/11 16:00:45  dennis
 *  Added RCS logging
 *
 */

package  DataSetTools.dataset;

import   java.text.*;
import   DataSetTools.math.*;

/**
 * The concrete class for an attribute whose value is a Position3D object.  
 *
 * @see DataSetTools.dataset.Data
 * @see DataSetTools.dataset.Attribute
 * @see DataSetTools.dataset.StringAttribute
 * @see DataSetTools.dataset.IntAttribute
 * @see DataSetTools.dataset.FloatAttribute
 * @see DataSetTools.dataset.DoubleAttribute
 *
 * @version 1.0  
 */

public class DetPosAttribute extends Attribute
{
  private DetectorPosition value;

  /**
   * Constructs a DetPosAttribute object using the specified name and value.
   */
  public DetPosAttribute( String name, DetectorPosition value )
  {
    super( name );
    this.value = new DetectorPosition( value );
  }


  /**
   * Returns a copy the DetectorPosition object that is the value of this
   * attribute, as a generic object.
   */
  public Object getValue( )
  {
     return new DetectorPosition( value );
  } 

  /**
   * Set the value for the int attribute using a generic object.  The actual
   * class of the object must be a Position3D object.
   */
  public boolean setValue( Object obj )
  {
    if ( obj instanceof Position3D )
      value = new DetectorPosition( (Position3D)obj );
    else
      return false;

    return true;
  }   

  /**
   * Returns a copy the DetectorPosition object that is the value of this
   * attribute.
   */
   public DetectorPosition getDetectorPosition( )
   {
     return new DetectorPosition( value );
   }


  /**
   * Combine the value of this attribute with the value of the attribute
   * passed as a parameter to obtain a new value for this attribute.  The
   * new value is obtained by averaging the 3D Positions represented by
   * the two attributes.
   *
   *  @param   attr   A DetPosAttribute whose position is to be averaged 
   *                  with the value of the this attribute.
   */
  public void combine( Attribute attr )
  {
     if ( !(attr instanceof DetPosAttribute) )       // can't combine
       return;

     float xyz[] = new float[3]; 
     float this_xyz[],
           other_xyz[];

     this_xyz  = this.value.getCartesianCoords();
     other_xyz = ((DetectorPosition)attr.getValue()).getCartesianCoords();

     for ( int i = 0; i < 3; i++ )
       xyz[i] = ( this_xyz[i] + other_xyz[i] ) / 2.0f;
  
     this.value.setCartesianCoords( xyz[0], xyz[1], xyz[2] );
  }


  /**
   * Add the specified position to this position to obtain an new position
   * value for this attribute.  
   *
   *  @param   attr   An attribute whose position value is to be "added" to the
   *                  position value of the this attribute.
   *
   */
  public void add( Attribute attr )
  {
     if ( !(attr instanceof DetPosAttribute) )       // can't combine
       return;

     float xyz[] = new float[3];
     float this_xyz[],
           other_xyz[];

     this_xyz  = this.value.getCartesianCoords();
     other_xyz = ((DetectorPosition)attr.getValue()).getCartesianCoords();

     for ( int i = 0; i < 3; i++ )
       xyz[i] = this_xyz[i] + other_xyz[i];

     this.value.setCartesianCoords( xyz[0], xyz[1], xyz[2] );
  }



  /**
   * Get a numeric value to be used for sorting based on this attribute.
   */
   public double getNumericValue()
   {
     return value.getScatteringAngle();
   }


  /**
   * Returns a string representation of the value of this attribute
   */
   public String getStringValue()
   {
     return value.toString();
   }

  /**
   * Returns a string representation of the (name,value) pair for this
   * attribute
   */
  public String toString()
  {
     return this.getName() + ": " + this.getStringValue();
  }

  /**
   * Returns a copy of the current attribute
   */
  public Object clone()
  {
    return new DetPosAttribute( this.getName(), value );
  }

}
