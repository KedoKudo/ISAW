/*

 * @(#)DoubleAttribute.java       1.0 99/06/09  Dennis Mikkelson

 *

 */



package  DataSetTools.dataset;



import java.text.*;



/**

 * The concrete class for an attribute whose value is a double.  

 *

 * @see DataSetTools.dataset.Data

 * @see DataSetTools.dataset.Attribute

 * @see DataSetTools.dataset.StringAttribute

 * @see DataSetTools.dataset.IntegerAttribute

 * @see DataSetTools.dataset.FloatAttribute

 * @see DataSetTools.dataset.DetPosAttribute

 *

 * @version 1.0  

 */



public class DoubleAttribute extends Attribute

{

  private double value;



  /**

   * Constructs a DoubleAttribute object using the specified name and value.

   */

  public DoubleAttribute( String name, double value )

  {

    super( name );

    this.value = value;

  }





  /**

   * Returns the double value of this attribute, as a generic object.

   */

  public Object getValue( )

  {

    return( new Double(value) );

  } 



  /**

   * Set the value for the double attribute using a generic object.  The actual

   * class of the object must be a Double object.

   */

  public boolean setValue( Object obj )

  {

    if ( obj instanceof Double )

      value = ((Double)obj).doubleValue();

    else if ( obj instanceof Float )

      value = ((Float)obj).doubleValue();

    else if ( obj instanceof Integer )

      value = ((Integer)obj).doubleValue();

    else

      return false;



    return true;

  }   



  /**

   * Returns the value of this attribute as a double.

   */

   public double getDoubleValue( )

   {

     return value;

   }



  /**

   * Set the value for the double attribute using a double.

   */

  public void setDoubleValue( double value )

  {

    this.value = value;

  }



  /**

   * Combine the value of this attribute with the value of the attribute

   * passed as a parameter to obtain a new value for this attribute.  The

   * new value is just the average of the values of the two attributes.

   *

   *  @param   attr   An attribute whose value is to be "combined" with the 

   *                  value of the this attribute.

   *

   */

  public void combine( Attribute attr )

  {

     this.value = ( this.value + attr.getNumericValue() ) / 2;

  }





  /**

   * Get a numeric value to be used for sorting based on this attribute.

   */

   public double getNumericValue()

   {

     return value;

   }



  /**

   * Returns a string representation of the double value of this attribute

   */

  public String getStringValue()

  {

     NumberFormat f = NumberFormat.getInstance();

     return f.format( value );

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

    return new DoubleAttribute( this.getName(), value );

  }

}

