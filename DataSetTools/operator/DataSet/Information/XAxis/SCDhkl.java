/*
 * File:  SCDhkl.java 
 *             
 * Copyright (C) 2002, Dennis Mikkelson
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
 * $Log$
 * Revision 1.2  2002/09/30 19:55:33  pfpeterson
 * Fixed bug with updated orientation matrix not being used.
 *
 * Revision 1.1  2002/09/25 16:45:24  pfpeterson
 * Added to CVS.
 *
 * Revision 1.2  2002/09/19 16:01:30  pfpeterson
 * Now uses IParameters rather than Parameters.
 *
 * Revision 1.1  2002/07/31 16:28:32  dennis
 * Calculate vector Q for a diffractometer in a common frame
 * of reference relative to a crystal.  The laboratory frame
 * of reference is mapped back to a common frame by reversing
 * the rotations defined by Phi, Chi and Omega.
 *
 *
 */

package DataSetTools.operator.DataSet.Information.XAxis;

import java.io.*;
import java.util.*;
import java.text.*; 
import DataSetTools.dataset.*;
import DataSetTools.instruments.*;
import DataSetTools.math.*;
import DataSetTools.util.*;
import DataSetTools.operator.Parameter;
import DataSetTools.operator.DataSet.DataSetOperator;
import DataSetTools.parameter.*;

/**
 *  This operator uses the Q-vector produced by SCDQxyz and the
 *  orientation matrix, UB, produced by blind to calculate the <hkl>
 *  of a given point.
 */

public class SCDhkl extends  XAxisInformationOp implements Serializable{
    private static final double PI  = Math.PI;
    private static final double HOM = 0.3955974;
    private float[][] invU;
    private float[][] UB;

    /**
     * Construct an operator with a default parameter list.  If this
     * constructor is used, the operator must be subsequently added to the
     * list of operators of a particular DataSet.  Also, meaningful values for
     * the parameters should be set ( using a GUI ) before calling getResult()
     * to apply the operator to the DataSet this operator was added to.
     */
    public SCDhkl(){
        super( "Find h, k, l" );
    }

    /**
     *  Construct an operator for a specified DataSet and with the specified
     *  parameter values so that the operation can be invoked immediately
     *  by calling getResult().
     *
     *  @param  ds    The DataSet to which the operation is applied
     *  @param  i     index of the Data block to use 
     *  @param  tof   the time-of-flight at which Qx,Qy,Qz is to be obtained
     */
    public SCDhkl( DataSet ds, int i, float tof ){
        this();                        
        
        getParameter(0).setValue( new Integer(i) );
        getParameter(1).setValue( new Float(tof) );
        
        setDataSet( ds );           // record reference to the DataSet that
                                    // this operator should operate on
    }

    /**
     * @return the command name to be used with script processor: 
     *         in this case, SCDhkl 
     */
    public String getCommand(){
        return "SCDhkl";
    }


    /**
     *  Set the parameters to default values.
     */
    public void setDefaultParameters(){
        parameters = new Vector();  // must do this to clear any old parameters
        
        Parameter parameter = new Parameter("Data block index",new Integer(0));
        addParameter( parameter );
        
        parameter = new Parameter( "TOF(us)" , new Float(0) );
        addParameter( parameter );
    }

    /**
     * Get string label for the xaxis information.
     *
     *  @param  x    the x-value for which the axis label is to be obtained.
     *  @param  i    the index of the Data block that will be used for obtaining
     *               the label.
     *
     *  @return  String describing the information provided by X_Info(),
     *           "h,k,l".
     */
    public String PointInfoLabel( float x, int i ){
        return "h,k,l";
    }
    

    /**
     * Get <hkl> at the specified point.
     *
     *  @param  x    the x-value (tof) for which the axis information is to be 
     *               obtained.
     *
     *  @param  i    the index of the Data block for which the axis information
     *               is to be obtained.
     *
     *  @return Position3D of the hkl. Only the cartesian coordinates
     *  of this have physical meaning.
     */
    public Object getResult(){
        DataSet ds = this.getDataSet();

        // have SCDQxyz calculate the Q-vector
        SCDQxyz Qop = (SCDQxyz)ds.getOperator("Find Qx, Qy, Qz");
        if(Qop==null) return null;
        Qop.getParameter(0).setValue((Integer)getParameter(0).getValue());
        Qop.getParameter(1).setValue((Float)getParameter(1).getValue());
        Position3D Qpos=(Position3D)Qop.getResult();
        if(Qpos==null) return null;

        // convert the Q to 1/d
        float[] Q=Qpos.getCartesianCoords();
        for( int i=0 ; i<3 ; i++ ){
            Q[i]=(float)(Q[i]/(2.*PI));
        }
          
        // check the current UB matrix
        float[][] UBtemp=
                (float[][])ds.getAttributeValue(Attribute.ORIENT_MATRIX);
        if(UBtemp==null) return null;
        // calculate the inverse of the orientation matrix
        if(! UBtemp.equals(this.UB)){
            if(this.invU==null) this.invU=new float[3][3];
            this.UB=(float[][])ds.getAttributeValue(Attribute.ORIENT_MATRIX);
            if(this.UB==null) return null;
            this.invU=this.findInverse( this.UB );
            if(this.invU==null) return null;
        }

        // apply the inverse orientation matrix to get hkl
        float h,k,l;
        h=(invU[0][0]*Q[0]+invU[0][1]*Q[1]+invU[0][2]*Q[2]);
        k=(invU[1][0]*Q[0]+invU[1][1]*Q[1]+invU[1][2]*Q[2]);
        l=(invU[2][0]*Q[0]+invU[2][1]*Q[1]+invU[2][2]*Q[2]);

        Position3D hklpos=new Position3D();
        hklpos.setCartesianCoords(h,k,l);

        return hklpos;
    }
    
    /**
     * Calls getResult then formats the Position3D nicely. If anything
     * is wrong this returns "N/A".
     *
     *  @param  x    the x-value (tof) for which the axis information is to be 
     *               obtained.
     *
     *  @param  i    the index of the Data block for which the axis information
     *               is to be obtained.
     */
    public String PointInfo( float x, int i ){
        // set the parameters for getResult
        getParameter(0).setValue(new Integer(i));
        getParameter(1).setValue(new Float(x));
        
        // set up a number format to display the result
        NumberFormat fmt = NumberFormat.getInstance();
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        
        // let getResult calculate Q
        Position3D hklpos=(Position3D)this.getResult();
        if(hklpos==null) return "N/A";
        float[] hkl=hklpos.getCartesianCoords();
        
        return fmt.format(hkl[0])+","+fmt.format(hkl[1])+","+fmt.format(hkl[2]);
    }  

    /**
     * Calculate the inverse of a 3x3 matrix using a method other than
     * Gauss-Jordan reduction. In particular this uses determinates of
     * reduced matrices.
     */
    private float[][] findInverse( float[][] A){
        if(A==null) return null;
        float[][] invA=new float[3][3];
        int row,col;
        for( row=0  ; row<3 ; row++ ){
            for( col=0 ; col<3 ; col++ ){
                invA[row][col]=0f;
            }
        }

        double detA=0f;

        double a=A[0][0];
        double b=A[0][1];
        double c=A[0][2];
        double d=A[1][0];
        double e=A[1][1];
        double f=A[1][2];
        double g=A[2][0];
        double h=A[2][1];
        double i=A[2][2];

        detA=a*(e*i-h*f)-b*(d*i-g*f)+c*(d*h-g*e);
        if(detA==0.0) return null;

        invA[0][0]=(float)((e*i-h*f)/detA);
        invA[0][1]=(float)((h*c-b*i)/detA);
        invA[0][2]=(float)((b*f-e*c)/detA);
        invA[1][0]=(float)((g*f-d*i)/detA);
        invA[1][1]=(float)((a*i-g*c)/detA);
        invA[1][2]=(float)((d*c-a*f)/detA);
        invA[2][0]=(float)((d*h-g*e)/detA);
        invA[2][1]=(float)((g*b-h*a)/detA);
        invA[2][2]=(float)((a*e-d*b)/detA);

        return invA;
    }

    /**
     * Get a copy of the current DateTime Operator.  The list 
     * of parameters and the reference to the DataSet to which it applies are
     * also copied.
     */
    public Object clone(){
        SCDhkl new_op = new SCDhkl( );
        // copy the data set associated
        // with this operator
        new_op.setDataSet( this.getDataSet() );
        new_op.CopyParametersFrom( this );
        
        return new_op;
    }
}
