/*
 * File:  UniformGrid.java
 *
 * Copyright (C) 2003, Dennis Mikkelson
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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 * 
 *  $Log$
 *  Revision 1.1  2003/02/04 18:15:06  dennis
 *  Initial version.
 *
 */

package  DataSetTools.dataset;

import java.io.*;
import DataSetTools.math.*;

/**
 *   A UniformGrid is an abstraction of a detector with equal size "pixels"
 * uniformly spaced in 3D at which data is measured.  Each position where a 
 * measurement is taken is approximated by a rectangular 3D "box" with 
 * arbitrary orientation and dimensions.  The grid manages "topological" 
 * information about the positions.  That is, it organizes the positions 
 * into 2D arrays of 3D positions where successive entries in rows and
 * columns of the 2D array are adjacent in 3D.  
 */  

public class UniformGrid implements IDataGrid 
{
  private int       id;
  private String    units;

  private float     center[] = { 0, 0, 0 };         // 3D point giving the  
                                                    // center of the grid

  private float     x_vector[] = { 1, 0, 0 },       // local coordinate system
                    y_vector[] = { 0, 1, 0 },       // for the grid
                    z_vector[] = { 0, 0, 1 };

  private float     width  = 1,      // overall dimensions of the grid
                    height = 1,
                    depth  = 1;

  private int       n_rows = 1,      // number of rows and columns that the
                    n_cols = 1;      // grid is divided into

  private float     dx,              // width per column on the grid
                    dy;              // height per row on the grid

  private float     col_x_offset,    // offsets from center to center of boxs
                    row_y_offset;    // in row 0 and/or column 0;

  /**
   *  Construct a new UniformGrid object.
   *
   *  Note: The pixel locations are at the center of the boxes.  For example,
   *        to describe the uniform grid consisting of unit cubes filling 
   *        the region of space: 0 <= x <= 5, 0 <= y < 10 and 0 <= z <= 1
   *        we would specify:
   *
   *            center   = (2.5,5,0.5)
   *            x_vector = (1,0,0)
   *            y_vector = (0,1,0)
   *            width    = 5 
   *            height   = 10
   *            depth    = 1
   *            n_rows   = 10
   *            n_cols   = 5
   *     
   *        The "box" in row 0, col 0 of the grid will be in position
   *        (0.5,0.5,0.5).  The "box" in row 9, col 4 will be in position
   *        (4.5,9.5,0.5) 
   *
   *  @param  id         Unique integer ID to be used for this data grid
   *  @param  units      The measurement units for the position, width, height,
   *                     etc. for this data grid. 
   *  @param  center     The position of the center of this DataGrid
   *  @param  x_vector   Vector in the "x" direction of the local coordinate
   *                     system for this grid.
   *  @param  y_vector   Vector in the "y" direction of the local coordinate
   *                     system for this grid.
   *  @param  width      Overall width of the whole grid in the "x" direction.
   *                     The width must be positive.
   *  @param  height     Overall height of the whole grid in the "y" direction 
   *                     The height must be positive.
   *  @param  n_rows     The number of rows of pixels in the whole grid. 
   *                     NOTE: This is the number of "boxes" the grid is 
   *                     divided into in the "y" direction. 
   *                     This must be at least 1. 
   *  @param  n_cols     The number of columns of pixels in the whole grid. 
   *                     NOTE: This is the number of "boxes" the grid is 
   *                     divided into in the "x" direction. 
   *                     This must be at least 1. 
   */
  public UniformGrid( int       id, 
                      String    units,
                      Vector3D  center, 
                      Vector3D  x_vector,
                      Vector3D  y_vector,
                      float     width, 
                      float     height, 
                      float     depth, 
                      int       n_rows,
                      int       n_cols  )
  {
    this.id       = id;
    this.units    = units;

    if ( n_cols > 0 )
      this.n_cols = n_cols;

    if ( n_rows > 0 )
      this.n_rows = n_rows;

    setHeight( height );
    setWidth( width );
    setDepth( depth );

    setCenter( center );
    setOrientation( x_vector, y_vector );
  }

  /**
   *  Get the ID of the current data grid (i.e. detector).  This ID should be 
   *  unique within the set of all detectors on an instrument.
   *
   *  @return a unique integer ID for this data grid.
   */
  public int ID()
  {
    return id;
  }

  /**
   *  Get the total number of points that make up this grid.  This will equal
   *  num_rows() * num_cols().
   *
   *  @return a non-negative integer giving the total number of points in
   *          this data grid.
   */
  public int num_points()
  {
    return n_rows * n_cols;
  }
 
  /**
   *  Get the units for this data grid.  The points of this IDataGrid are 
   *  positioned in some three dimensional space.  The units on each dimension
   *  of this space are assumed to be the same.
   *
   *  @return A string describing the units used.  
   */
  public String units() 
  {
    return units; 
  }

  /**
   *  Get the vector in the "x" direction for the local coordinate system 
   *  of the data grid.  If the data grid is non-planar, this will be a vector
   *  in the local "x" direction of a planar approximation to the data grid.
   *
   *  @return A vector giving the components of a vector in the local
   *          "x" direction for this data grid.
   */
  public Vector3D x_vec()
  {
    return new Vector3D( x_vector ); 
  }

  /**
   *  Get the vector in the "y" direction for the local coordinate system 
   *  of the data grid.  If the data grid is non-planar, this will be a vector
   *  in the local "y" direction of a planar approximation to the data grid.
   *
   *  @return A vector giving the components of a vector in the local
   *          "y" direction for this data grid.
   */
  public Vector3D y_vec()
  {
    return new Vector3D( y_vector ); 
  }

  /**
   *  Get the vector in the "z" direction for the local coordinate system 
   *  of the data grid.  If the data grid is planar, this will be in the
   *  direction of the normal to the plane.  If the data grid is non-planar, 
   *  this will be a vector normal to a planar approximation to the data grid.
   *  The "z" direction must be chosen to form a right handed coordinate 
   *  system.
   *
   *  @return A vector giving the components of a vector in the local
   *          "z" direction for this data grid.
   */
  public Vector3D z_vec()
  {
    return new Vector3D( z_vector ); 
  }

  /**
   *  Get the nominal position of the center of the data grid.
   *
   *  @return A vector giving the nominal center of this data grid.
   */
  public Vector3D position()
  {
    return new Vector3D( center ); 
  }

  /**
   *  Get the nominal width of the data grid.  The width is the size of the
   *  grid in the direction given by x_vec().
   *
   *  @return the width of the data grid.
   */
  public float width()
  {
    return width;
  }

  /**
   *  Get the nominal height of the data grid.  The height is the size of the
   *  grid in the direction given by y_vec().
   *
   *  @return the height of the data grid.
   */
  public float height()
  {
    return height;
  }

  /**
   *  Get the nominal depth of the data grid.  The depth is the size of the
   *  grid in the direction given by z_vec().
   *
   *  @return the depth of the data grid.
   */
  public float depth()
  {
    return depth;
  }

  /**
   *  Get the number of rows in this data grid.  
   *
   *  @return the number of rows.
   */
  public int num_rows()
  {
    return n_rows; 
  }

  /**
   *  Get the number of columns in this data grid.  
   *
   *  @return the number of columns.
   */
  public int num_cols()
  {
    return n_cols;  
  }

  /**
   *  Find the offset in the directon of "x_vec()", from the center of the 
   *  grid to the specified position.  If row and col are integers, this 
   *  will be the offset to the center of the specified "box".  
   *
   *  @return the offset in the "x" direction to the specified position on
   *          the grid.
   */
  public float x( float row, float col )
  {
    return (col * dx + col_x_offset); 
  }

  /**
   *  Find the offset in the directon of "y_vec()", from the center of the 
   *  grid to the specified position.  If row and col are integers, this 
   *  will be the offset to the center of the specified "box".  
   *
   *  @return the offset in the "y" direction to the specified position on
   *          the grid.
   */
  public float y( float row, float col )
  {
    return (row * dy + row_y_offset); 
  }

  /**
   *  Find the "row" number for the specified position on the grid.  The 
   *  position is specified in terms of offsets from the center of the 
   *  grid in the directions of "x_vec()" and "y_vec()".  
   *  The row value is returned as a float, to allow for 
   *  positions that are NOT at the exact center of a "box".  If an integer
   *  row number is needed, the value returned by this method should be
   *  rounded.
   *
   *  @return the fractional row number corresponding to the specified 
   *          position on the grid.
   */ 
  public float row( float x, float y )
  {
    return ( (y - row_y_offset)/dy );
  }

  /**
   *  Find the "column" number for the specified position on the grid.  The 
   *  position is specified in terms of offsets from the center of the 
   *  grid in the directions of "x_vec()" and "y_vec()".  
   *  The column value is returned as a float, to allow for 
   *  positions that are NOT at the exact center of a "box".  If an integer
   *  column number is needed, the value returned by this method should be
   *  rounded.
   *
   *  @return the fractional column number corresponding to the specified 
   *          position on the grid.
   */ 
  public float col( float x, float y )
  {
    return ( (x - col_x_offset)/dx );
  }

  /**
   *  Get the position in 3D of the specified point on the grid.  If the
   *  row and col values are integers, this will be the center point of
   *  a grid "box".  If row and/or col are not integers, the position
   *  returned will be offset from the center of the grid "box". 
   *
   *  @return A vector giving the position in 3D of the specified row
   *          and column values.
   */
  public Vector3D position( float row, float col )
  {
    float delta_x = x(row,col);
    float delta_y = y(row,col);

    float pos[] = new float[3];
    for ( int i = 0; i < 3; i++ )
      pos[i] = center[i] + delta_x * x_vector[i] + delta_y * y_vector[i];

    return new Vector3D( pos );
  }

  /**
   *  Get the width of the specified grid "box".  If row and col are not
   *  integers, they will be rounded to obtain integer values that 
   *  specifiy a particular grid "box".
   *
   *  @return The width of the specified grid "box" in the direction of 
   *          the "x_vec(row,col)".
   */
  public float width( float row, float col )
  {
    return dx;
  }

  /**
   *  Get the height of the specified grid "box".  If row and col are not
   *  integers, they will be rounded to obtain integer values that 
   *  specifiy a particular grid "box".
   *
   *  @return The height of the specified grid "box" in the direction of 
   *          the "y_vec(row,col)".
   */
  public float height( float row, float col )
  {
    return dy;
  }

  /**
   *  Get the depth of the specified grid "box".  If row and col are not
   *  integers, they will be rounded to obtain integer values that 
   *  specifiy a particular grid "box".
   *
   *  @return The depth of the specified grid "box" in the direction of 
   *          the "z_vec(row,col)".
   */
  public float depth( float row, float col )
  {
    return this.depth;
  }

  /**
   *  Get the vector in the "x" direction for a particular grid "box".
   *  If the data grid is planar, this may return the same vector as
   *  the x_vec() method.  If the data grid is non-planar, this will be 
   *  a vector in the local "x" direction for the particular grid "box".
   *
   *  @return A vector giving the components of a vector in the local
   *          "x" direction for this grid "box".
   */
  public Vector3D x_vec( float row, float col )
  {
    return new Vector3D( x_vector );   // same for all pixels on uniform grid
  }

  /**
   *  Get the vector in the "y" direction for a particular grid "box".
   *  If the data grid is planar, this may return the same vector as
   *  the y_vec() method.  If the data grid is non-planar, this will be 
   *  a vector in the local "y" direction for the particular grid "box".
   *
   *  @return A vector giving the components of a vector in the local
   *          "y" direction for this grid "box".
   */
  public Vector3D y_vec( float row, float col ) 
  {
    return new Vector3D( y_vector );   // same for all pixels on uniform grid
  }

  /**
   *  Get the vector in the "z" direction for a particular grid "box".
   *  If the data grid is planar, this will return the same vector as
   *  the z_vec() method.  If the data grid is non-planar, this will be 
   *  a vector in the local "z" direction for the particular grid "box".
   *
   *  @return A vector giving the components of a vector in the local
   *          "z" direction for this grid "box".
   */
  public Vector3D z_vec( float row, float col )
  {
    return new Vector3D( z_vector );   // same for all pixels on uniform grid
  }

  /**
   *  Get the solid angle subtended by this grid "box" from the origin. 
   *  If the row and column values are not integers, they will be rounded
   *  to obtain integer values that specify a particular grid "box".
   *  
   *  @return the solid angle subtended by the specified grid box.
   */
  public float SolidAngle( float row, float col )
  {
    Vector3D pos = position( row, col );
    float r = pos.length();

    if ( r == 0 )
      return 0;

    return dx * dy / (float)( 4 * Math.PI * r * r );
  }

  /**
   *  Get the approximate range of scattering angles subtended by the 
   *  specified grid "box".  If the row and column values are not integers, 
   *  they will be rounded to obtain integer values that specify a particular 
   *  grid "box".  The box is assumed to be oriented so that the "z" vector
   *  points towards the origin.  In that case the delta two theta value
   *  will be determined by the height and width of the box.
   *
   *  @return the range of scattering angles for the specified grid box,
   *          in degrees.
   */
  public float Delta2Theta( float row, float col )
  {
    Vector3D pos = position( row, col );
    float r = pos.length();

    if ( r == 0 )
      return 180.0f;

    double angle = 2 * Math.atan( Math.sqrt(dx*dx + dy*dy)/2/r );

    return (float)( angle * 180 / Math.PI );
  }


  /**
   *  Move the UniformGrid to a new center point.
   *
   *  @param center  The new position of the center of the grid.
   */
  public void setCenter( Vector3D center )
  {
    float temp[] = center.get();

    for (int i = 0; i < 3; i++ )
      this.center[i] = temp[i]; 
  }


  /**
   *  Change the orientation of the UniformGrid by specifying new x_vec and
   *  y_vec direction vectors.  The x_vec and y_vec directions should be
   *  perpendicular, but must at least not be parallel.  The x_vec and y_vec
   *  is used to calculate the vector z_vec in the direction of the
   *  cross product (x_vec X y_vec).  Subsequently the value of the y_vec
   *  direction is recalculated to be in the direction (z_vec X x_vec).
   *  All resulting vectors are normalized to be of unit length, so they
   *  will form an orthonormal coordinate system.
   *
   *  @param x_vector  The direction to be used as the "x" direction of the
   *                   DataGrid.
   *  @param y_vector  The direction to be used as the "y" direction of the
   *                   DataGrid.
   *
   *  @return This returns true if the input vectors were valid (not null,
   *          non-zero and not colinear) and returns false otherwise.  If
   *          the input vectors are not valid, the orientation will not be
   *          changed.
   */
  public boolean setOrientation( Vector3D x_vector, Vector3D y_vector )
  {
    if ( x_vector == null || y_vector == null )
      return false;

    Vector3D temp_z = new Vector3D();              // calculate vector in z dir
    temp_z.cross( x_vector, y_vector );

    if ( temp_z.length() == 0 )
      return false;

    Vector3D temp_x = new Vector3D( x_vector );
    Vector3D temp_y = new Vector3D( y_vector );

    temp_x.normalize();                            // make sure they're unit
    temp_y.normalize();                            // vectors
    temp_z.normalize();
                                                   // copy into local arrays
    for ( int i = 0; i < 3; i++ )
    {
      this.x_vector[i] = temp_x.get()[i];
      this.y_vector[i] = temp_y.get()[i];
      this.z_vector[i] = temp_z.get()[i];
    }
    return true;
  }


  /**
   *  Set a new height for the grid.  The specified height must be positive, 
   *  or the specified value will be ignored.
   *
   *  @param  height   The new height for the UniformGrid.
   */
  public boolean setHeight( float height )
  {
    if ( height <= 0 )
      return false;

    this.height = height;
    dy = height/n_rows;                     // calculate height of one row
    row_y_offset = -dy *(n_rows-1)/2.0f;    // and the offset to row 0
    return true;
  }


  /**
   *  Set a new width for the grid.  The specified width must be positive,
   *  or the specified value will be ignored.
   *
   *  @param  width   The new width for the UniformGrid.
   */
  public boolean setWidth( float width )
  {
    if ( width <= 0 )
      return false;

    this.width = width;
    dx = width/n_cols;                      // calculate width of one column 
    col_x_offset = -dx *(n_cols-1)/2.0f;    // and the offset to column 0
    return true;
  }


  /**
   *  Set a new depth for the grid.  The specified depth must be positive,
   *  or the specified value will be ignored.
   *
   *  @param  depth   The new depth for the UniformGrid.
   */
  public boolean setDepth( float depth )
  {
    if ( depth <= 0 )
      return false;

    this.depth = depth;
    return true;
  }

  /**
   *  Write the data that defines the grid in a multi-line string.
   *
   *  @return A multi-line String listing the internal state information 
   *          for the grid. 
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("ID    = " + ID() +'\n' );
    buffer.append("units = " + units() +'\n');
    buffer.append("x_vec = " + x_vec() +'\n');
    buffer.append("y_vec = " + y_vec() +'\n');
    buffer.append("z_vec = " + z_vec() +'\n');
    buffer.append("center   = " + position() +'\n');
    buffer.append("width    = " + width() +'\n');
    buffer.append("depth    = " + depth() +'\n');
    buffer.append("height   = " + height() +'\n');
    buffer.append("num_rows = " + num_rows() +'\n');
    buffer.append("num_cols = " + num_cols() +'\n');
    return buffer.toString();
  }


  /* ------------------------------------------------------------------- */
  /**
   *  Main program for testing purposes.
   */ 
  public static void main( String args[] )
  {
    int      id       =  1;
    String   units    = "meters";
    Vector3D center   = new Vector3D( 2.5f, 5, 0.5f );
    Vector3D x_vector = new Vector3D( 1, 0, 0 );
    Vector3D y_vector = new Vector3D( 0, 1, 0 );
    float    width    = 5;
    float    height   = 10;
    float    depth    = 1;
    int      n_rows   = 10;
    int      n_cols   = 5;

    UniformGrid test = new UniformGrid( id, units, 
                                        center, x_vector, y_vector,
                                        width, height, depth,
                                        n_rows, n_cols );
   
    System.out.print( test.toString() );          // show basic grid info
 
    int row = 9;                                  // show info on one pixel
    int col = 4;
    float x = 2.5f;
    float y = 5f;
    System.out.println("---------------------------------------");
    System.out.println("At row = " + row + " col = " + col );
    System.out.println("At x   = " + x   + " y   = " + y );
    System.out.println("x(row,col) = " + test.x(row,col) );
    System.out.println("y(row,col) = " + test.y(row,col) );
    System.out.println("row(x,y)   = " + test.row(x,y) );
    System.out.println("col(x,y)   = " + test.col(x,y) );
    System.out.println("position(row,col) = " + test.position(row,col) );
    System.out.println("x_vec(row,col)    = " + test.x_vec(row,col) );
    System.out.println("y_vec(row,col)    = " + test.y_vec(row,col) );
    System.out.println("z_vec(row,col)    = " + test.z_vec(row,col) );
    System.out.println("width(row,col)    = " + test.width(row,col) );
    System.out.println("depth(row,col)    = " + test.depth(row,col) );
    System.out.println("height(row,col)   = " + test.height(row,col) );
    System.out.println("SolidAngle(row,col)  = " + test.SolidAngle(row,col) );
    System.out.println("Delta2Theta(row,col) = " + test.Delta2Theta(row,col) );
  }

} 
