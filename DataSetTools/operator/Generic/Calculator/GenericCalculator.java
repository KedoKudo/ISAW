/*
 * File:  GenericCalculator.java 
 *
 * Copyright (C) 2002, Peter Peterson
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
 * $Log$
 * Revision 1.5  2005/01/10 15:21:19  dennis
 * Modified getCategoryList to put derived classes in proper place in
 * menu system.
 *
 * Revision 1.4  2003/06/16 19:06:33  pfpeterson
 * Removed old code and updated to work with new getCategoryList() code
 * in base operator class.
 *
 * Revision 1.3  2002/11/27 23:21:04  pfpeterson
 * standardized header
 *
 *
 */

package DataSetTools.operator.Generic.Calculator;

import java.io.*;
import DataSetTools.operator.*;
import DataSetTools.operator.Generic.GenericOperator;

/**
 *   Base class for generic operators that calculate information based
 *   on only a few parameters and do not depend on any DataSet.
 *
 * @see DataSetTools.operator.Generic.TOF_DG_Spectrometer.GenericTOF_DG_Spectrometer
 * @see DataSetTools.operator.Generic.TOF_SCD.GenericTOF_SCD
 * @see GenericOperator
 * @see DataSetTools.operator.Generic.Save.WriteNexus 
 *
 */

abstract public class GenericCalculator extends    GenericOperator 
                                                 implements Serializable
{
  private static String[] categoryList=null;

   protected GenericCalculator( String title )
   {
      super( title );
   } 

  /* ------------------------ getCategoryList ------------------------------ */
  /**
   * Get a list of strings giving the categories to be used when placing
   * the operator in menus.  The first entry in the array must be the string:
   *
   *      Operator.OPERATOR
   *
   * @return  An array of Strings specifying the category names to use 
   *          for this operator. 
   */
  public String[] getCategoryList()
  {
    if(categoryList==null)
      categoryList = Operator.UTILS_CALCULATORS;

    return categoryList;
  }


} 
