/*
 * File:  GenericTOF_DG_Spectrometer.java 
 *
 * Copyright (C) 2001, Dennis Mikkelson
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
 *  Revision 1.2  2002/03/05 19:28:17  pfpeterson
 *  Updated @see references in javadocs.
 *
 *  Revision 1.1  2002/02/22 20:58:51  pfpeterson
 *  Operator reorganization.
 *
 *  Revision 1.1  2002/01/11 22:13:44  dennis
 *  Abstract base class for Generic TOF Direct Geometry
 *  Spectrometer operators.
 *
 */

package DataSetTools.operator.Generic.TOF_DG_Spectrometer;

import java.io.*;
import DataSetTools.operator.Generic.GenericOperator;

/**
 *   Base class for generic operators that work with time-of-flight direct
 * geometry spectrometers. 
 *
 * @see GenericOperator
 * @see DataSetTools.operator.Generic.Save.WriteNexus 
 *
 */

abstract public class GenericTOF_DG_Spectrometer extends    GenericOperator 
                                                 implements Serializable
{
   protected GenericTOF_DG_Spectrometer( String title )
   {
      super( title );
   } 

  /* -------------------------- getCategory -------------------------------- */
  /**
   * Get the category of this operator
   *
   * @return  A String specifying the category of this operator.  This is
   *          actually the category of the abstract base class from which
   *          the current operator is directly derived.
   */
  public String getCategory()
  {
    return "TOF DG Spectrometer";
  }

  /* ------------------------ getCategoryList ------------------------------ */
  /**
   * Get an array of strings listing the operator category names of base
   * classes for this operator.  The first entry in the array is the string:
   *
   *      Operator.OPERATOR
   *
   * The last entry is the category of the last abstract base class that is
   * is a base class for the current operator.
   * 
   * @return  A list of Strings specifying the category names of the abstract
   * base classes from which this operator is derived.
   */
  public String[] getCategoryList()
  {
    String partial_list[] = super.getCategoryList();  // get list of ancestor
                                                      // categories and put 
                                                      // them in a new larger
                                                      // list.
    return AppendCategory( getCategory(), partial_list );
  }
} 
