/* 
 * File: integrate.java
 *  
 * Copyright (C) 2008     Ruth Mikkelson
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
 * Contact :  Ruth Mikkelson<Mikkelsonr@uwstout.edu>
 *            Department of Mathematics, Statistics and Computer Science
 *            Menomonie, WI 54751
 *
 * This work was supported by the Spallation Neutron Source, Oak  Ridge National
 * Laboratory
 *
 *
 * Modified:
 *
 * $Log:$
 *
 */

package Wizard.TOF_SCD;
import DataSetTools.operator.*;
import DataSetTools.operator.Generic.*;
import DataSetTools.operator.Generic.TOF_SCD.Integrate_new;
import gov.anl.ipns.Parameters.*;
import DataSetTools.parameter.*;
import Operators.TOF_SCD.IntegrateUtils;
import gov.anl.ipns.Util.SpecialStrings.*;

import Command.*;

/**
 * This class has been dynamically created using the Method2OperatorWizard
 * and usually should not be edited.
 */
public class integrate extends GenericOperator{


   /**
	* Constructor for the operator.  Calls the super class constructor.
    */
   public integrate(){
     super("integrate");
     }


   /**
    * Gives the user the command for the operator.
    *
	 * @return  The command for the operator, a String.
    */
   public String getCommand(){
      return "integrate";
   }


   /**
    * Sets the default parameters for the operator.
    */
   public void setDefaultParameters(){
      clearParametersVector();
      addParameter( new DataSetPG("Data Set",DataSetTools.dataset.DataSet.EMPTY_DATA_SET));
      addParameter( new IntegerPG("Centering(0..7)",0));
      addParameter( new PlaceHolderPG("Zmin/max",new int[0]));
      addParameter( new IntegerPG("Increment slice size",1));
      addParameter( new FloatPG("min d-spacing",0));
      addParameter( new FloatPG("Max unit Cell Length",12));
      addParameter( new IntArrayPG("Pixel Rows to keep(blank for all","1:300"));
      addParameter( new IntArrayPG("Pixel Cols to keep","1,300"));
      addParameter( new IntegerPG("log every Nth peak",1));
        java.util.Vector V = new java.util.Vector();
        V.addElement( "MaxItoSigI"); V.addElement("Shoe Box"); V.addElement("MaxIToSigI-old");
        V.addElement("TOFINT");V.addElement( Integrate_new.FIT_PEAK );
      ChoiceListPG choice = new ChoiceListPG("Peak algorithm",V);
      addParameter( choice );
      addParameter( new PlaceHolderPG("Min/Max col change",new int[]{-1,3}));
      addParameter( new PlaceHolderPG("Min/Max row change",new int[]{-1,3}));
      addParameter( new FloatPG("max shoebox",0));
      addParameter( new FloatPG("Monitor Count", 10000));
      addParameter( new PlaceHolderPG("Log buffer",null));
   }


   /**
    * Writes a string for the documentation of the operator provided by
    * the user.
    *
	 * @return  The documentation for the operator.
    */
   public String getDocumentation(){
      StringBuffer S = new StringBuffer();
      S.append("@overview    "); 
      S.append("Integrates one data set");
      S.append("@algorithm    "); 
      S.append("There is a choice of several algorithms for integrating a peak");
      S.append("@assumptions    "); 
      S.append("The data set has the Orientation Matrix attribute set");
      S.append("@param   ");
      S.append("The Data Set to to find peaks to integrate");
      S.append("@param   ");
      S.append("Centering type, where: \n");
      for ( int i = 0; i < IntegrateUtils.CenteringNames.length; i++ )
        S.append(IntegrateUtils.CenteringNames[i] + " is at " + i +"\n" );
      S.append("@param   ");
      S.append("left and right offset around Peak time channel to consider");
      S.append("@param   ");
      S.append("The incremental amount to increase the slice size by.");
      S.append("@param   ");
      S.append("the minimum d-spacing allowed");
      S.append("@param   ");
      S.append("Maximum unit cell length in real space");
      S.append("@param   ");
      S.append("Rows to keep or blank for all. Currently all rows from ");
      S.append( "  the minimum to the maximum in the list will be used" );
      S.append("@param   ");
      S.append("Maximum unit cell length in real space");
      S.append("@param   ");
      S.append("Log every nth peak");
      S.append("@param   ");
      S.append("Peak Algorithm identifier. Use only the Strings below MaxItoSigI, Shoe Box, MaxIToSigI-old, TOFINT");
      S.append("@param   ");
      S.append("left and   right offset around Peak column to consider");
      S.append("@param   ");
      S.append("left and right offset around Peak row  to consider");
      S.append("@param   ");
      S.append("the maximum for shoe_box integration");
      S.append( "@param monCount  the monitor Count" );
      S.append("@param   ");
      S.append("if this a non-null StringBuffer, the log informationwill be appended to it.");
      S.append("@return The Peaks with the integrated values and errors set.  Also, a buffer may contain the integrate log info");
      S.append("");
      S.append(" if the last argument is  a non-null StringBuffer");
      S.append("@error ");
      S.append("UB matrix is not loaded into the dataset");
      S.append(" initial flight path is zero");
      S.append(" invalid detector number");
      S.append(" Could not create pixel map for det");
      S.append(" Detector information invalid");
      return S.toString();
   }


   /**
    * Returns a string array with the category the operator is in.
    *
    * @return  An array containing the category the operator is in.
    */
   public String[] getCategoryList(){
            return new String[]{
                     "HiddenOperator"
                     };
   }


   /**
    * Returns the result of the operator, otherwise and ErrorString.
    *
    * @return  The result of the operator, or an ErrorString.
    */
   public Object getResult(){
      try{

         DataSetTools.dataset.DataSet ds = (DataSetTools.dataset.DataSet)(getParameter(0).getValue());
         int centering = ((IntegerPG)(getParameter(1))).getintValue();
         int[] timeZrange = (int[])(getParameter(2).getValue());
         int incrSlice = ((IntegerPG)(getParameter(3))).getintValue();
         float dmin = ((FloatPG)(getParameter(4))).getfloatValue();
         float maxUnitCellLength = ((FloatPG)(getParameter(5))).getfloatValue();
         int[] PixRows =((IntArrayPG)getParameter(6)).getArrayValue( );
         int[] PixCols =((IntArrayPG)getParameter(7)).getArrayValue( );
         int listNthPeak = ((IntegerPG)(getParameter(8))).getintValue();
         java.lang.String PeakAlg = getParameter(9).getValue().toString();
         int[] colXrange = (int[])(getParameter(10).getValue());
         int[] rowYrange = (int[])(getParameter(11).getValue());
         float max_shoebox = ((FloatPG)(getParameter(12))).getfloatValue();
         float monCount = ((FloatPG)getParameter(13)).getfloatValue();
         java.lang.Object logbuffer = (java.lang.Object)(getParameter(14).getValue());
         java.lang.Object Xres=DataSetTools.operator.Generic.TOF_SCD.Integrate_new.integrate(ds,centering,timeZrange,incrSlice,dmin,
                            maxUnitCellLength,PixRows,PixCols,listNthPeak,PeakAlg,colXrange,rowYrange,
                       max_shoebox,monCount,logbuffer );
         ds.removeAllOperators();
         ds.removeAll_data_entries();
         ds = null;
         return Xres;
       }catch( Throwable XXX){
        String[]Except = ScriptUtil.
            GetExceptionStackInfo(XXX,true,1);
        String mess="";
        if(Except == null) Except = new String[0];
        for( int i =0; i< Except.length; i++)
           mess += Except[i]+"\r\n            "; 
        return new ErrorString( XXX.toString()+":"
             +mess);
                }
   }
}



