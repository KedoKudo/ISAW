/* 
 * File: VectorToCylindricalDetectorPosition.java
 *  
 * Copyright (C) 2005     Kurtiss Olson
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
 * Contact :  Dennis Mikkelson<mikkelsond@uwstout.edu> 
 *            MSCS Department
 *            HH237H
 *            Menomonie, WI. 54751
 *            (715)-232-2291
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0426797, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2005/11/21 17:54:42  dennis
 * Hidden operators to pack and unpack DetectorPosition objects.
 * (Kurtiss Olson)
 *
 *
 */

package Operators.Generic.TypeConversion;
import DataSetTools.operator.*;
import DataSetTools.operator.Generic.*;
import DataSetTools.parameter.*;

import gov.anl.ipns.Util.SpecialStrings.*;

import Command.*;
public class VectorToCylindricalDetectorPosition extends GenericOperator
                                                 implements HiddenOperator
{
   public VectorToCylindricalDetectorPosition(){
     super("VectorToCylindricalDetectorPosition");
     }

   public String getCommand(){
      return "VectorToCylindricalDetectorPosition";
   }

   public void setDefaultParameters(){
      clearParametersVector();
      addParameter( new PlaceHolderPG("Vector with DetectorPosition parameters",null));
   }

   public String getDocumentation(){
      StringBuffer S = new StringBuffer();
      S.append("@overview    "); 
      S.append("Convert the values from a Vector containing numeric values into a");
      S.append("DetectorPosition object.");
      S.append("@algorithm    "); 
      S.append("Numerical values from the Vector are used for the cylindrical radius,");
      S.append("azimuth angle, and z, and a DetectorPosition object is created from");
      S.append("those values.");
      S.append("@assumptions    "); 
      S.append("The Vector must have three numeric values.");
      S.append("@param   ");
      S.append("A Vector containing cylindrical radius, azimuth angle, and z for the");
      S.append("cylindrical coordinates.");
      S.append("@return A DetectorPosition object, with cylindrical coordinates from the ");
      S.append("values from the Vector, or an ErrorString.");
      S.append("@error ");
      S.append("If the Vector passed in does not have three numeric values,");
      S.append("then an ErrorString will be returned");
      return S.toString();
   }


   public String[] getCategoryList(){
            return new String[]{
                     "Macros",
                     "Utils",
                     "Convert"
                     };
   }


   public Object getResult(){
      try{

         java.lang.Object det_vector = (java.lang.Object)(getParameter(0).getValue());
         java.lang.Object Xres=Operators.Generic.TypeConversion.Convert.VectorToCylindricalDetectorPostion(det_vector);
         return Xres;
       }catch( Throwable XXX){
         return new ErrorString( XXX.toString()+":"
             +ScriptUtil.GetExceptionStackInfo(XXX,true,1)[0]);
      }
   }

}


