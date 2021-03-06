/*
 * File:  HIPPOWeightingjava
 *
 * Copyright (C) 2006 J. Tao
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
 * Contact : Julian Tao <taoj@anl.gov>
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
 * Revision 1.3  2006/07/27 15:11:16  dennis
 * Added logging tag, so future CVS commits will be
 * recorded in the file.
 *
 */
package Operators.TOF_Diffractometer;

import DataSetTools.operator.*;
import DataSetTools.math.tof_calc;
import DataSetTools.dataset.AttributeList;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.XScale;


/**
 * not yet used;
 */

public class HIPPOWeighting implements Wrappable, IWrappableWithCategoryList {
  //~ Instance fields **********************************************************
  
  private boolean DEBUG = false;
  /* @param runfile absolute path of the runfile;
   * @param ISvan the vanadium calibration's beam monitor spectrum is needed for later use;
   */

  public DataSet smo_van;
  public DataSet dm_van;
//  private String[] target;
//  private float[] formula;
  public float Wmin = 0.1f;
  public float Wmax = 6.0f;

  //~ Methods ******************************************************************

  /* ------------------------ getCategoryList ------------------------------ */
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
    return Operator.TOF_NGLAD;
  }


  /**
   * @return The script name for this Operator.
   */
  public String getCommand(  ) {
    return "HIPPO_Weight";
  }

  /**
   * Returns the documentation for this method as a String.  The format follows
   * standard JavaDoc conventions.
   */
  public String getDocumentation(  ) {
    StringBuffer s = new StringBuffer( "" );
    return s.toString(  );
  }

  
  /**
   * Removes dead detectors from the specified DataSet.
   *
   * @return The crunched DataSet.
   */
  public Object calculate(  ) {

    System.out.println("prepare the flux weighting function analytically...");
    Data dv, van_dm_Q;
    AttributeList attr_list_v;
    float scattering_angle_v, lambda_v, q_v;
    float[] Q_vals_v, y_vals_v, W_vals_vm, Q_vals_vm, y_vals_vm;
    float[] data_params_v = new float[4];
    XScale Q_scale_vm;
    int ngrps, nmonchannel;
      
    ngrps = smo_van.getNum_entries();    
/*
    W_vals_vm = (dm_van.getData_entry(0)).getX_values();
    y_vals_vm = (dm_van.getData_entry(0)).getCopyOfY_values();
    nmonchannel = y_vals_vm.length;
    Q_vals_vm = new float[nmonchannel+1];
    arrayUtil.Reverse( y_vals_vm );
*/        
    for (int i = 0; i < ngrps; i++){

      dv = smo_van.getData_entry(i);

      attr_list_v = dv.getAttributeList();
          
      data_params_v = (float[])attr_list_v.getAttributeValue(HIPPORunProps.HIPPO_PARM);
      scattering_angle_v = data_params_v[0];
      Q_vals_v = dv.getX_scale().getXs();
      y_vals_v = dv.getY_values();
          
/*                         
      for (int k = 0; k <= nmonchannel; k++){
        Q_vals_vm[k] = tof_calc.DiffractometerQofWavelength(scattering_angle_v, W_vals_vm[k]);
      }
          
      arrayUtil.Reverse( Q_vals_vm );
      Q_scale_vm = new VariableXScale(Q_vals_vm);
      van_dm_Q = Data.getInstance(Q_scale_vm, y_vals_vm, dv.getGroup_ID());
      van_dm_Q.resample( dv.getX_scale(), IData.SMOOTH_NONE );
*/ 
      for (int k = 0; k < y_vals_v.length; k++){
        q_v = 0.5f*(Q_vals_v[k]+Q_vals_v[k+1]);
        lambda_v = tof_calc.WavelengthofDiffractometerQ(scattering_angle_v, q_v);            
        if (lambda_v < Wmin || lambda_v > Wmax) y_vals_v[k] = 0.0f;
//        else y_vals_v[k] = (float)Math.sin(scattering_angle_v/2)/q_v/q_v*van_dm_Q.getY_values()[k];
        else y_vals_v[k] = (float)Math.sin(scattering_angle_v/2)/q_v/q_v;
//        else y_vals_v[k] = 1.0f;
      }
    }                               


    smo_van.setTitle(smo_van.getTitle()+" "+"--->FLUX");
    smo_van.setY_units("counts");
    smo_van.setY_label("calculated vanadium scattering");
    System.out.println("Done.");
    return Boolean.TRUE;
  }

}

