/*
 * File:  Float2DAttribute.java
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
 *           Intense Pulsed Neutron Source Division
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
 *  $Log$
 *  Revision 1.1  2002/08/22 14:55:59  pfpeterson
 *  Added to CVS.
 *
 */

package  DataSetTools.dataset;

import java.text.*;
import java.io.*;
import java.util.Vector;
import DataSetTools.util.StringUtil;

/**
 * The concrete class for an attribute whose value is a float.  
 *
 * @see DataSetTools.dataset.Data
 * @see DataSetTools.dataset.Attribute
 * @see DataSetTools.dataset.StringAttribute
 * @see DataSetTools.dataset.IntAttribute
 * @see DataSetTools.dataset.FloatAttribute
 * @see DataSetTools.dataset.DoubleAttribute
 * @see DataSetTools.dataset.DetPosAttribute
 */

public class Float2DAttribute extends Attribute{
    // NOTE: any field that is static or transient is NOT serialized.
    //
    // CHANGE THE "serialVersionUID" IF THE SERIALIZATION IS INCOMPATIBLE WITH
    // PREVIOUS VERSIONS, IN WAYS THAT CAN NOT BE FIXED BY THE readObject()
    // METHOD.  SEE "IsawSerialVersion" COMMENTS BELOW.  CHANGING THIS CAUSES
    // JAVA TO REFUSE TO READ DIFFERENT VERSIONS.
    //
    public  static final long serialVersionUID = 1L;
    
    
    // NOTE: The following fields are serialized.  If new fields are added that
    //       are not static, reasonable default values should be assigned in the
    //       readObject() method for compatibility with old servers, until the
    //       servers can be updated.
    
    private int IsawSerialVersion = 1;       // CHANGE THIS WHEN ADDING OR
                                             // REMOVING FIELDS, IF
                                             // readObject() CAN FIX ANY
                                             // COMPATIBILITY PROBLEMS
    private float[][] value;
    
    /**
     * Constructs a Float2DAttribute object using the specified name
     * and value.
     */
    public Float2DAttribute( String name, float[][] value ){
        super( name );
        if(this.isRectangular(value)){
            this.value = value;
        }else{
            this.value=new float[1][1];
            this.value[0][0]=0f;
        }
    }
    
    public Float2DAttribute(){
        super( "" );
        this.value =new float[1][1];
        this.value[0][0]=0f;
    }

    /**
     * Returns the float value of this attribute, as a generic object.
     */
    public Object getValue(){
        return( value );
    }

    /**
     * Set the value for the float attribute using a generic object.
     * The actual class of the object must be a Float object.
     */
    public boolean setValue( Object obj ){
        if ( obj instanceof float[][] ){
            this.setFloatValue( (float[][])obj );
            return true;
        }else{
            return false;
        }
    }   

    /**
     * Returns the float value of this attribute as a float.
     */
    public float[][] getFloatValue(){
        return value;
    }

    /**
     * Set the value for the float attribute using a float.
     */
    public void setFloatValue( float[][] value ){
        this.value = value;
    }
    
    /**
     * Combine the value of this attribute with the value of the
     * attribute passed as a parameter to obtain a new value for this
     * attribute.  The new value is just the average of the values of
     * the two attributes.
     *
     *  @param attr An attribute whose value is to be "combined" with
     *  the value of the this attribute.
     */
    public void combine( Attribute attr ){
        float[][] other;
        if(attr instanceof Float2DAttribute)
            other=((Float2DAttribute)attr).getFloatValue();
        else
            return; // can't do anything

        // confirm that we are comparing to a rectangular attribute
        if( !this.isRectangular(other) ) return;
        // first dimension must be the same size
        if( this.value.length != other.length ) return;
        // second dimension must be the same size
        if( this.value[0].length != other[0].length ) return;

        // it is okay to do the operation
        for( int i=0 ; i<this.value.length ; i++ ){
            for( int j=0 ; j<this.value[i].length ; j++ ){
                this.value[i][j]=(this.value[i][j]+other[i][j])/2f;
            }
        }
    }
    
    /**
     * Add the value of the specified attribute to the value of this
     * attribute obtain a new value for this attribute.
     *
     *  @param attr An attribute whose value is to be "added" to the
     *  value of the this attribute.
     */
    public void add( Attribute attr ){
        float[][] other;
        if(attr instanceof Float2DAttribute)
            other=((Float2DAttribute)attr).getFloatValue();
        else
            return; // can't do anything

        // confirm that we are comparing to a rectangular attribute
        if( !this.isRectangular(other) ) return;
        // first dimension must be the same size
        if( this.value.length != other.length ) return;
        // second dimension must be the same size
        if( this.value[0].length != other[0].length ) return;

        // it is okay to do the operation
        for( int i=0 ; i<this.value.length ; i++ ){
            for( int j=0 ; j<this.value[i].length ; j++ ){
                this.value[i][j]=this.value[i][j]+other[i][j];
            }
        }
    }

    public boolean XMLwrite( OutputStream stream, int mode ){
        int nRow=this.value.length;
        int nCol=this.value[0].length;
        
        try{
            StringBuffer sb=new StringBuffer(80);
            sb.append("<Float2DAttribute>\n<name>");
            sb.append(this.name);
            sb.append("</name>\n<value>\n");
            sb.append("<2dFloat nRow=");
            sb.append(nRow);
            sb.append(" nCol=");
            sb.append(nCol);
            sb.append(">\n");
            for( int i=0 ; i<nRow ; i++ ){
                for( int j=0 ; j<nCol ; j++ ){
                    sb.append(this.value[i][j]);
                    if( j+1<nCol ) sb.append(" ");
                }
                sb.append("\n");
            }
            sb.append("</2dFloat>\n");
            sb.append("</value>\n</Float2DAttribute>\n");

            stream.write(sb.substring(0).getBytes());
            return true;
        }catch(IOException e){
            return xml_utils.setError("IO Err="+e.getMessage());
        }
     }

    public boolean XMLread( InputStream stream ){
        Vector       attr        = null;
        int          nRow        = 0;
        int          nCol        = 0;
        Integer      tempI       = null;
        Float        tempF       = null;
        float[][]    read_value  = null;
        StringBuffer float_array = null;

        //-----------------get name v
        String Tag = xml_utils.getTag( stream );
        if( Tag == null)
            return xml_utils.setError( xml_utils.getErrorMessage());
        if(!xml_utils.skipAttributes( stream))
            return xml_utils.setError( xml_utils.getErrorMessage());
        if( !Tag.equals("name"))
            return xml_utils.setError("name Tag Missing in Float2D");
        this.name = xml_utils.getValue( stream);
        if( name == null)
            return xml_utils.setError("name Tag Missing in Float2D");
        
        Tag =xml_utils.getEndTag( stream );
        if( Tag == null)
            return xml_utils.setError( xml_utils.getErrorMessage());
        if( !Tag.equals("/name"))
            return xml_utils.setError("name Tag not nested in Float2D");
        if(!xml_utils.skipAttributes( stream))
            return xml_utils.setError( xml_utils.getErrorMessage());
        
        //----------------  get value field----------------
        Tag =xml_utils.getTag( stream );
        if( Tag == null)
            return xml_utils.setError( xml_utils.getErrorMessage());
        if( !Tag.equals("value"))
            return xml_utils.setError("missing value tag in Float2D"+Tag); 
        if(!xml_utils.skipAttributes( stream))
            return xml_utils.setError( xml_utils.getErrorMessage());
        //-----------actual values
        Tag =xml_utils.getTag( stream );
        if( Tag == null)
            return xml_utils.setError( xml_utils.getErrorMessage());
        if( !Tag.equals("2dFloat"))
            return xml_utils.setError("missing 2dFloat tag in Float2D"+Tag); 
        // get the number of rows
        attr=xml_utils.getNextAttribute(stream);
        if(! ((String)attr.elementAt(0)).equals("nRow"))
            return xml_utils.setError("missing nRow attribute in 2dFloat");
        try{
            tempI=Integer.getInteger((String)attr.elementAt(1));
            if(tempI!=null)
                nRow=tempI.intValue();
            else
                return xml_utils.setError("Null Pointer while reading nRow "
                                          +"attribute of 2dFloat");
        }catch(NumberFormatException e){
            return xml_utils.setError("NumberFormatException in nRow "
                                      +"attribute of 2dFloat");
        }
        // get the number of columns
        attr=xml_utils.getNextAttribute(stream);
        if(! ((String)attr.elementAt(0)).equals("nCol"))
            return xml_utils.setError("missing nCol attribute in 2dFloat");
        try{
            tempI=Integer.getInteger((String)attr.elementAt(1));
            if(tempI!=null)
                nCol=tempI.intValue();
            else
                return xml_utils.setError("Null Pointer while reading nCol "
                                          +"attribute of 2dFloat");
        }catch(NumberFormatException e){
            return xml_utils.setError("NumberFormatException in nCol "
                                      +"attribute of 2dFloat");
        }
        // skip any other attributes
        if(!xml_utils.skipAttributes( stream))
            return xml_utils.setError( xml_utils.getErrorMessage());
        
        //create the float array
        if(nRow<=0 || nCol<=0)
            return xml_utils.setError("Bad array dimension: "+nRow
                                      +" by "+nCol);
        
        read_value=new float[nRow][nCol];
        
        // read in the float array
        float_array=new StringBuffer(xml_utils.getValue(stream));
        for( int i=0 ; i<nRow ; i++ ){
            for( int j=0 ; j<nCol ; j++ ){
                read_value[i][j]=StringUtil.getFloat(float_array);
            }
        }
        this.setValue(read_value);

        //-------------------- get End tags
        Tag =xml_utils.getTag( stream ); 
        if( Tag == null)
            return xml_utils.setError( xml_utils.getErrorMessage());
        if( !Tag.equals("/value"))
            return xml_utils.setError("Tags not nested in Float2D"+Tag);
        if(!xml_utils.skipAttributes( stream ))
            return xml_utils.setError( xml_utils.getErrorMessage());
        
        Tag =xml_utils.getTag( stream ); 
        if( Tag == null)
            return xml_utils.setError( xml_utils.getErrorMessage());
        if( !Tag.equals("/Float2DAttribute"))
            return xml_utils.setError("Tags not nested in Float2D"+Tag);
        if(!xml_utils.skipAttributes( stream ))
            return xml_utils.setError( xml_utils.getErrorMessage());
        return true;
    }
    
    /**
     * Get a numeric value to be used for sorting based on this
     * attribute. It is the sum of all of the floats in the array.
     */
    public double getNumericValue(){
        float val=0f;
        for( int i=0 ; i<this.value.length ; i++ ){
            for( int j=0 ; j<this.value[0].length ; j++ ){
                val+=this.value[i][j];
            }
        }
        return val;
    }
    
    /**
     * Returns a string representation of the float value of this
     * attribute
     */
    public String getStringValue(){
        StringBuffer sb=new StringBuffer();
        for( int i=0 ; i<this.value.length ; i++ ){
            for(int j=0 ; j<this.value[0].length ; j++ ){
                sb.append(this.value[i][j]);
                if( j+1<this.value[0].length ) sb.append(", ");
            }
            if(i+1<this.value.length) sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a string representation of the (name,value) pair for
     * this attribute
     */
    public String toString(){
        return this.getName() + ": " + this.getStringValue();
    }
    
    /**
     * Returns a copy of the current attribute
     */
    public Object clone(){
        return new Float2DAttribute( this.getName(), value );
    }

    /* ------------------------- PRIVATE METHODS --------------------------- */

    /**
     *  The readObject method is called when objects are read from a
     *  serialized ojbect stream, such as a file or network stream.
     *  The non-transient and non-static fields that are common to the
     *  serialized class and the current class are read by the
     *  defaultReadObject() method.  The current readObject() method
     *  MUST include code to fill out any transient fields and new
     *  fields that are required in the current version but are not
     *  present in the serialized version being read.
     */

    private void readObject( ObjectInputStream s ) throws IOException,
                                                         ClassNotFoundException{
        s.defaultReadObject();               // read basic information
        
        if ( IsawSerialVersion != 1 )
            System.out.println("Warning:Float2DAttribute "
                               +"IsawSerialVersion != 1");
    }

    private boolean isRectangular(float[][] val){
        int nCols=val[0].length;
        for( int i=0 ; i<val.length ; i++ ){
            if( nCols!=val[i].length ) return false;
        }
        return true;
    }

    public static void main( String[] args ){
        Float2DAttribute fa;
        float [][] f={{0f,1f,2f},{3f,4f,5f}};
        fa=new Float2DAttribute();
        System.out.println("01:"+fa);
        fa=new Float2DAttribute("Lattice Parameters",f);
        System.out.println("02:"+fa);
        System.exit(0);
    }
}
