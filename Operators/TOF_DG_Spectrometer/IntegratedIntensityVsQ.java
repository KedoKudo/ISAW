/*
 * File:  IntegratedIntensityVsQ.java 
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
 *           Intense Pulsed Neutron Source
 *           Argonne National Laboratory
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.2  2002/10/02 20:14:19  pfpeterson
 * Fixed a missing missing factor of 1/2.
 *
 * Revision 1.1  2002/09/25 22:18:33  pfpeterson
 * Added to CVS.
 *
 *
 */
package Operators.TOF_DG_Spectrometer;

import DataSetTools.operator.*;
import DataSetTools.operator.Generic.TOF_DG_Spectrometer.*;
import DataSetTools.operator.DataSet.EditList.*;
import DataSetTools.retriever.*;
import DataSetTools.dataset.*;
import DataSetTools.viewer.*;
import DataSetTools.util.*;
import DataSetTools.math.*;
import java.util.*;

/** 
 *  This operator produces a DataSet with one entry, a Data block giving the
 *  integrated intensity of a histogram over a specified interval, as a 
 *  tabulated function of the scattering angle 2*theta.
 */
public class IntegratedIntensityVsQ extends GenericTOF_DG_Spectrometer{
    private static final String TITLE = "Integrated Intensity Vs Q";
    private static final float h_fac=2.0721f;

    /** 
     *  Creates operator with title "Integrated Intensity Vs Q" and a
     *  default list of parameters.
     */  
    public IntegratedIntensityVsQ(){
        super( TITLE );
    }

    /** 
     *  Construct a IntegratedIntensityVsQ operator that integrates
     *  the histogram values over the interval [a,b].  Creates
     *  operator with title "Integrated Intensity Vs Q" and the
     *  specified list of parameters.  The getResult method must still
     *  be used to execute the operator.
     *
     *  @param ds DataSet for which the integrated intensity vs group
     *  angle will be calculated.
     *  @param a Left endpoint of interval where the histogram is
     *  integrated.
     *  @param b Right endpoint of interval where the histogram is
     *  integrated.
     */
    public IntegratedIntensityVsQ(DataSet ds, float a, float b){
        this(); 
        parameters = new Vector();
        addParameter(new Parameter("DataSet parameter",ds));
        addParameter(new Parameter("Left endpoint",new Float(a)));
        addParameter(new Parameter("Right endpoint",new Float(b)));
    }

    /** 
     * Get the name of this operator to use in scripts
     * 
     * @return  "IntensityVsQ", the command used to invoke this 
     *           operator in Scripts
     */
    public String getCommand(){
        return "IntensityVsQ";
    }

    /** 
     * Sets default values for the parameters.  This must match the
     * data types of the parameters.
     */
    public void setDefaultParameters(){
        parameters = new Vector();
        addParameter(new Parameter("DataSet parameter",DataSet.EMPTY_DATA_SET));
        addParameter(new Parameter("Left endpoint",new Float(-50f)));
        addParameter(new Parameter("Right endpoint",new Float(50f)));
    }

    /** 
     *  Executes this operator using the values of the current parameters.
     *
     *  @return If successful, this operator produces a DataSet
     *  containing the integrated intensity of a spectrum over the
     *  specified interval [a,b].  If the original DataSet is null, or
     *  the interval is invalid, or some Data block of the original
     *  DataSet does not have a detector position attribute, an error
     *  message is returned.
     */
    public Object getResult(){
        DataSet ds =  (DataSet)(getParameter(0).getValue());
        float   a  = ((Float)  (getParameter(1).getValue())).floatValue();
        float   b  = ((Float)  (getParameter(2).getValue())).floatValue();
        
        // check for degenerate cases
        if ( ds == null )
            return new ErrorString("DataSet is null in IntegratedIntensityVsQ");
        
        if ( a >= b ) //|| a < 0 || b < 0 )
            return new ErrorString("[a,b] invalid in IntegratedIntensityVsQ: "
                                   +"[ " + a + ", " + b + " ]" );
        
        // get the original y units and label
        String y_units = ds.getY_units();
        String y_label = ds.getY_label();
        String title   = "Integrated Intensity";
        // make DataSet with new title and modified y units and label
        DataSetFactory ds_factory = new DataSetFactory( title,
                                                        "Inverse Angstroms",
                                                        "Q",
                                                        y_units,  
                                                        "Integrated "+ y_label);
        DataSet new_ds = ds_factory.getDataSet();
        // copy and update the log copy the list of attributes
        new_ds.copyOp_log(ds);
        new_ds.addLog_entry("Calculated Integrated Intensity vs Q ");
        new_ds.addLog_entry("on the interval [ " + a + ", " + b + " ]");
        new_ds.setAttributeList( ds.getAttributeList() );
        
        // Sort the DataSet based on the effective position.  This
        // orders it by scattering angle.
        Operator sort_op=new DataSetSort(ds,Attribute.DETECTOR_POS,true,false);
        sort_op.getResult();
        // for each Data block, find the integrated intensity on [a,b]
        // and the scattering angle
        float area[]  = new float[ ds.getNum_entries() ];
        float Q[] = new float[ ds.getNum_entries() ];
        float xs[], ys[];
        float Ei=0f;
        float Eavg=(b+a)/2f;
        double Qtemp=0.;
        for ( int i = 0; i < ds.getNum_entries(); i++ ){
            Data d = ds.getData_entry( i );  // use method IntegrateHistogram to
            xs = d.getX_scale().getXs(); // take care of partial bins
            ys = d.getY_values();
            if(d.isHistogram()){
                area[i]=NumericalAnalysis.IntegrateHistogram(xs,ys,a,b);
            }else{
                area[i]=NumericalAnalysis.IntegrateFunctionTable(xs,ys,a,b);
            }
            
            // NOTE: a DetectorPosition object can provide the
            // position in cartesian, cylindrical or polar
            // coordinates, as well as the scattering angle 2*theta
            DetectorPosition pos = 
                (DetectorPosition)d.getAttributeValue( Attribute.DETECTOR_POS );
            
            if ( pos == null )
                return new ErrorString("NO DetectorPosition for group " + 
                                       d.getGroup_ID() );

            Ei=((Float)d.getAttributeValue(Attribute.ENERGY_IN)).floatValue();
            Qtemp=Math.cos(Math.abs(pos.getScatteringAngle()));
            Qtemp=Math.sqrt((2*Ei-Eavg-2.*Qtemp*Math.sqrt(Ei*(Ei-Eavg)))/h_fac);
            Q[i] = (float)Qtemp;
        }

        // There may be several groups with the same Q, so we need to
        // combine them to keep distinct x's
        float average_area[]  = new float[area.length];
        float average_Q[] = new float[area.length];
        int   n_used = 0;
        int   i      = 0;
        float x;
        float y;
        float sum;
        int   n_sum;
        while( i < Q.length ){
            x     = Q[i];
            y     = area[i];
            sum   = area[i];
            n_sum = 1;
            i++;
            while ( i < Q.length && Q[i] == x ) {
                sum += y;
                n_sum++;
                i++; 
            }
            average_Q[n_used] = x;
            average_area [n_used] = sum/n_sum;
            n_used++;
        }

        // copy non-duplicates into arrays of the proper size
        area  = new float[ n_used ];
        Q = new float[ n_used ];
        System.arraycopy( average_Q, 0, Q, 0, n_used );
        System.arraycopy( average_area,  0, area,  0, n_used );
        
        // make a new Data block with the new x and y values and group
        // ID 1 the x values must be increasing, and they will be
        // since the DataSet was sorted on the detector position
        XScale x_scale = new VariableXScale( Q );
        Data new_d = Data.getInstance( x_scale, area, 1 );
        
        new_ds.addData_entry( new_d );
        return new_ds;
    }

    /** 
     *  Creates a clone of this operator.
     */
    public Object clone(){
        Operator op = new IntegratedIntensityVsQ();
        op.CopyParametersFrom( this );
        return op;
    }
}
