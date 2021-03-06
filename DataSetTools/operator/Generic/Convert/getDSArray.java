/*
 * File: getDSArray.java 
 *             
 * Copyright (C) 2005, Ruth Mikkelson
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
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 * This work was supported by the National Science Foundation under
 * grant number DMR-0218882
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.6  2007/02/22 19:46:40  rmikk
 * Added an (output) vector as the last argument. If present, it will contain
 *   the number of row, columns and channels for the given detector
 *
 * Revision 1.5  2006/10/30 15:12:14  rmikk
 * Changed the third parameter to a choice of C, java or FORTRAN format for the
 *   resultant array
 *
 * Revision 1.4  2005/05/16 20:17:42  rmikk
 * Got the indexing for Fortran arrays fixed.  May have to add C arrays later
 *
 * Revision 1.3  2005/01/10 16:38:18  dennis
 * Added getCategoryList method to place operator in menu system.
 *
 * Revision 1.2  2005/01/07 20:00:24  rmikk
 * Made loop more efficient
 *
 * Revision 1.1  2005/01/06 15:47:01  rmikk
 * Initial Checkin
 *
 */

package DataSetTools.operator.Generic.Convert;

import DataSetTools.operator.*;
import DataSetTools.dataset.*;
import gov.anl.ipns.Util.SpecialStrings.*;
import java.util.*;
/**
 * This wrappable operator creates an array out of the y values from a
 * dataset. It has an option to create this array for FORTRAN jni programs
 *
 */
public class getDSArray implements Wrappable, IWrappableWithCategoryList {

   /**
    *  The Data Set with the y values
    */
   public DataSet  DS;
   
   
   /**
    *  The detector ID from which to get the y value. The result will
    *  be a 3 dimensional array with dimensions of row, col, and time 
    */
   public int  DetectorId =-1;
   
   /**
    *  If false, a multidimensional Java float array is returned with dimensions
    *   row, col, time
    *  If true, a one dimension float array will be returned. The fastest
    *  changing dimension is time then col then row
    */
  // public boolean FortArray = false;
  public StringChoiceList format = new StringChoiceList(
            
            new String[]{"C","java","FORTRAN"}
            
           );
  
  public Vector OthData= new Vector();

  /**
   * Get an array of strings listing the operator category names  for 
   * this operator. The first entry in the array is the 
   * string: Operator.OPERATOR. Subsequent elements of the array determine
   * which submenu this operator will reside in.
   * 
   * @return  A list of Strings specifying the category names for the
   *          menu system 
   *        
   */
  public String[] getCategoryList()
  {
    return Operator.UTILS_CONVERSIONS;
  }

   
   /**
    *  Returns getDSArray, the name used to invoke this operator in scripts
    * @return the name that invokes this operator in scripts
    */
   public String getCommand(){
     return "getDSArray";
   }
   
   
   /**
    *   Extracts the appropriate y values from the data set and places them
    *   in an array.  Either a 3 dimensional FORTRAN array or a 1-D
    *   Fortran array in Fortran order
    */
   public Object calculate(){
     
     IDataGrid grid = null;
     if( DetectorId < 0){
       int[] ids = NexIO.Write.NxWriteData.getAreaGrids( DS );
       if( (ids == null) ||(ids.length < 1))
         return new ErrorString("No Area Grids in DataSet");
      DetectorId = ids[0]; 
     }
     
     grid = NexIO.Write.NxWriteData.getAreaGrid( DS, DetectorId);

     if( grid == null)
       return new ErrorString( "Detector not found");
     
     if(OthData== null )
        OthData = new Vector();
     if( OthData.size()>=1)
        OthData.setElementAt( new Integer(grid.num_rows()),0);
     else
        OthData.addElement( new Integer(grid.num_rows()));
     if( OthData.size()>=2)
        OthData.setElementAt( new Integer(grid.num_cols()),1);
     else
        OthData.addElement( new Integer(grid.num_cols()));
     
     int nzs = grid.getData_entry(1,1).getY_values().length;
     
     if( OthData.size()>=3)
        OthData.setElementAt( new Integer(nzs),2);
     else
        OthData.addElement( new Integer(nzs));
       
     int nrows = grid.num_rows();
     int ncols =grid.num_cols() ;
     if( !grid.isData_entered())
         grid.setData_entries( DS );
         
     int ntimes = grid.getData_entry(1,1).getY_values().length;
     float[][][] data = null ;
     float[]fdata = null;
     boolean fort,jav,c;
     fort=jav=c=false;
     String Format = format.toString();
     if( Format.equals("FORTRAN")){
        fdata =new float[ncols*nrows*ntimes];
        fort = true;
     }else if( Format.equals("java")){
        data = new float[nrows][ncols][ntimes];
        jav = true;
     }else if( Format.equals("C")){
        fdata = new float[ncols*nrows*ntimes];
        c = true;
     }else
        return new ErrorString( format +" is improper format for array");
        
    
     for( int i=0; i< nrows; i++)
        for( int j = 0; j < ncols; j++){

          Data D= grid.getData_entry(i+1,j+1);
          if( D.getY_values().length != ntimes)
            return new ErrorString("Not All Spectra have the same # of times"); 
          float[] V = D.getY_values();
          
          for(int t=0; t < ntimes; t++ ){
          
             if(fort )// C array??
                fdata[(i)+(j)*nrows + t*nrows*ncols] = V[t];
             else if( jav)
                data[i][j][t] = V[t];
             else
                fdata[ t+ntimes*j+ntimes*ncols*i] = V[t];
          
           }
        }   
     if( data != null)
       return data;
     else return fdata;
   }
   
   
   public String getDocumentation(){
     StringBuffer s = new StringBuffer();

     s.append("@overview  This wrappable operator creates an array out");
     s.append(" of the y values from a dataset. It has an option to ");
     s.append("create this array for FORTRAN jni programs");
     s.append("@param DS  The Data Set with the y values that are to ");            
     s.append("be put in the resultant array");
     s.append("@param DetectorId  The detector ID from which to get ");
     s.append("the y value. The result will be a 3 dimensional array with ");
     s.append("dimensions of row, col, and time ");  
 
     s.append("@param format either java, C, or FORTRAN ");
     s.append("The C and FORTRAN format yield linear float arrays in their standard");
     s.append("order. The format, java, yields a 2D java float array that can be ragged,");
     s.append("@param a vector containing the number of rows, columns, and time");
     s.append(" channels for row 1 ,col 1 of the given grid.");
     s.append("@return The array in the proper format for one detector");
       
     return s.toString();
   }

}
