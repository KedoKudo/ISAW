/*
 * @(#)UniformXScale.java     
 *
 * Programmer:  Dennis Mikkelson
 *
 *  $Log$
 *  Revision 1.4  2000/12/07 22:27:21  dennis
 *  Added methods getStep(),
 *                extend(),
 *
 *  Added main() with test code for extend() method.
 *
 *  Uses partial double precision for calculating the x values.
 *
 *  Revision 1.3  2000/07/10 22:24:06  dennis
 *  July 10, 2000 version... many changes, added to CVS repository
 *
 *  Revision 1.3  2000/05/12 15:41:35  dennis
 *  Made UniformXScales IMMUTABLE so that they may be shared.  This required
 *  two changes:
 *  1. Removed method:  setNum_x()
 *  2. Modified method:  expand()  so that it creates and returns a new
 *     UniformXScale rather than altering the contents of the current one.
 *
 *  Revision 1.2  2000/05/11 16:00:45  dennis
 *  Added RCS logging
 *
 */

package DataSetTools.dataset;
import java.io.*;

/**
 * The class for "X" scales that consist of evenly spaced points along a
 * a specified interval.  The evenly spaced points are specified by giving
 * the end points of the interval and the number of points to create.
 *
 * @see DataSetTools.dataset.Data
 * @see DataSetTools.dataset.XScale
 * @see DataSetTools.dataset.VariableXScale
 *
 * @version 1.0  
 */

public class UniformXScale extends XScale implements Serializable
{
  /**
   * Constructs a UniformXScale object by specifying the starting x, ending x 
   * and number of x values to be used.   For example, if the scale was to run 
   * from 0 to 1000 in steps of 10, the values 0, 1000, 101 would be passed 
   * to this constructor.  The number of x values must be at least 1.  If
   * the number of x values is greater than 1, then the start and end x values
   * must be unequal.  If the number of x values is equal to 1, then the
   * start and end x values should be the same.  If they are not, only the
   * start value will actually be used.
   *
   * @param   start_x  the starting x
   * @param   end_x    the ending x  
   * @param   num_x    the number of x values.  
   *
   * @see DataSetTools.dataset.XScale
   */
   public UniformXScale( float start_x, float end_x, int num_x )
   {
     super( start_x, end_x, num_x );
   }


  /**
   * Returns the array of "X" values.  The array will have num_x entries.   
   * The "X" values are uniformly spaced and are calculated from start_x, 
   * end_x and num_x.
   */
  public float[] getXs()
  {
    double step = getStep();
    float x[]   = new float[num_x];

    for ( int i = 0; i < num_x; i++ )
      x[i] = (float)( start_x + i * step );

    return x;
  }

  /**
   * Get the separation between successive x values for this XScale.
   *
   * @return  The distance between two successive x values in this XScale.
   */
  public double getStep()
  {
    double step    = 0;

    if ( num_x > 1 )
      step = (end_x - start_x) / (double)(num_x - 1);

    return step;
  }


  /**
   *  Constructs a new UniformXScale that extends over the smallest interval
   *  containing both this XScale and the specifed XScale.  The number of 
   *  x-values to use is extended to the larger of the numbers of x-values 
   *  in this XScale and the specified XScale. 
   */
   public UniformXScale expand( XScale scale )
   {
     float temp_start_x = Math.min( this.start_x, scale.start_x );
     float temp_end_x   = Math.max( this.end_x,   scale.end_x );
     int   temp_num_x   = Math.max( this.num_x,   scale.num_x );

     return new UniformXScale( temp_start_x, temp_end_x, temp_num_x ); 
   }


  /**
   *  Constructs a new UniformXScale that extends over the smallest interval
   *  containing both this XScale and the specifed XScale.  The spacing between
   *  points is the same as for the current XScale and the start_x of the
   *  new XScale is choosen so that the division points of the current XScale
   *  are still used.
   *
   *  @param  other_scale  The x scale that is used to extend the current XScale
   *
   *  @return  A new UniformXScale is returned that covers the union
   *           of the intervals covered by the current XScale and the
   *           other XScale.
   */

   public XScale extend( XScale other_scale )
   {
     float temp_start_x = Math.min( this.start_x, other_scale.start_x );
     float temp_end_x   = Math.max( this.end_x,   other_scale.end_x );
     int   temp_num_x   = Math.max( this.num_x,   other_scale.num_x );

                                                       // keep current delta_x
     double delta_x;
     if ( num_x > 1 )
       delta_x = ( end_x - start_x ) / (double)(num_x-1); 
     else
       return new UniformXScale( temp_start_x, temp_end_x, temp_num_x );

     int n_steps_to_start = (int)Math.round((start_x-temp_start_x)/delta_x);
     temp_start_x = (float)(start_x - n_steps_to_start * delta_x);

     int n_steps_to_end = (int)Math.round((temp_end_x-temp_start_x)/delta_x); 
     temp_end_x = (float)(temp_start_x + n_steps_to_end * delta_x);

     temp_num_x = (int)Math.round( (temp_end_x - temp_start_x) / delta_x ) + 1;

     return new UniformXScale( temp_start_x, temp_end_x, temp_num_x );
   }


  /**
   * Creates a new UniformXScale object with the same data as the original
   * UniformXScale object.
   */
  public Object clone()
  {
    UniformXScale copy = new UniformXScale( start_x, end_x, num_x );
    return( copy );
  }

  /*
   * main program for basic testing only
   */
  public static void main( String args[] )
  {
    XScale    scale = new UniformXScale(    10,    12, 11 );
    XScale u1_scale = new UniformXScale(  8.5f,  9.5f, 11 );
    XScale u2_scale = new UniformXScale(  9.5f, 10.5f, 11 );
    XScale u3_scale = new UniformXScale( 10.5f, 11.5f, 11 );
    XScale u4_scale = new UniformXScale( 11.5f, 12.5f, 11 );
    XScale u5_scale = new UniformXScale( 12.5f, 13.5f, 11 );
    XScale u6_scale = new UniformXScale(  9.5f, 12.5f, 11 );

    float v1_x[] = {   8.5f,  8.75f,  9.0f,  9.25f,  9.5f };
    float v2_x[] = {   9.5f,  9.75f, 10.0f, 10.25f, 10.5f };
    float v3_x[] = {  10.5f, 10.75f, 11.0f, 11.25f, 11.5f };
    float v4_x[] = {  11.5f, 11.75f, 12.0f, 12.25f, 12.5f };
    float v5_x[] = {  12.5f, 12.75f, 13.0f, 13.25f, 13.5f };
    float v6_x[] = {   9.5f, 10.0f,  11.0f, 12.0f,  12.5f };

    XScale v1_scale = new VariableXScale ( v1_x );
    XScale v2_scale = new VariableXScale ( v2_x );
    XScale v3_scale = new VariableXScale ( v3_x );
    XScale v4_scale = new VariableXScale ( v4_x );
    XScale v5_scale = new VariableXScale ( v5_x );
    XScale v6_scale = new VariableXScale ( v6_x );

//    XScale extended = scale.extend( u1_scale );     // uniform scale extend
//    XScale extended = scale.extend( u2_scale );
//    XScale extended = scale.extend( u3_scale );
//    XScale extended = scale.extend( u4_scale );
//    XScale extended = scale.extend( u5_scale );
    XScale extended = scale.extend( u6_scale );

//    XScale extended = scale.extend( v1_scale );    // variable scale extend
//    XScale extended = scale.extend( v2_scale );
//    XScale extended = scale.extend( v3_scale );
//    XScale extended = scale.extend( v4_scale );
//    XScale extended = scale.extend( v5_scale );
//    XScale extended = scale.extend( v6_scale );

    float x[] = extended.getXs();
    for ( int i = 0; i < x.length; i++ )
      System.out.println( x[i] );
  }
}
