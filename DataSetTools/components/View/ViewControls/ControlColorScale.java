/*
 * File: ControlColorScale.java
 *
 * Copyright (C) 2003, Mike Miller
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
 * Primary   Mike Miller <millermi@uwstout.edu>
 * Contact:  Student Developer, University of Wisconsin-Stout
 *           
 * Contact : Dennis Mikkelson <mikkelsond@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882, and by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.3  2003/06/18 13:30:03  dennis
 *  - Restructured to enable calibrations. Added constructor for
 *    calibration color scale.
 *  - Added implementation of IAxisAddible2D.
 *  - FIX NEEDED - Vertical scale, if too small,
 *    will not display all of the color scale.
 *  - Added listener to color and log scale changes.
 *
 *  Revision 1.2  2003/06/09 22:36:54  dennis
 *  - Added setEventListening(false) method call for ColorScaleImage to
 *    ignore keyboard/mouse events. (Mike Miller)
 *
 *  Revision 1.1  2003/05/29 14:25:27  dennis
 *  Initial version, displays the possible colors of the ImageViewComponent.
 *  (Mike Miller)
 *
 */
  
 package DataSetTools.components.View.ViewControls;
 
 import javax.swing.*;
 import javax.swing.event.*;
 import java.awt.event.*;
 import java.awt.*;
 
 import DataSetTools.components.image.IndexColorMaker;
 import DataSetTools.components.ui.ColorScaleImage;
 import DataSetTools.components.View.TwoD.ImageViewComponent;
 import DataSetTools.components.View.TwoD.IColorScaleAddible;
 import DataSetTools.components.View.TwoD.IAxisAddible2D;
 import DataSetTools.components.View.VirtualArray2D;
 import DataSetTools.components.View.AxisInfo2D;
 import DataSetTools.components.View.Transparency.AxisOverlay2D;
 import DataSetTools.components.image.CoordBounds;

/**
 * This class is a ViewControl (ActiveJPanel) with a color scale for use 
 * by ViewComponents. No messages are sent by this control.
 */ 
public class ControlColorScale extends ViewControl
                                         implements IAxisAddible2D 
{
   public static final boolean HORIZONTAL = true;
   public static final boolean VERTICAL   = false;
   
   private IColorScaleAddible component;
   private AxisOverlay2D axis;
   private JPanel background;
   private ColorScaleImage csi;
   private String colorscheme;
   private boolean isBasic = true; // basic vs calibrated color scales
   private Font font;
   
  /* Possible color schemes as designated by 
   * DataSetTools/components/image/IndexColorMaker.java
   *
   * GRAY_SCALE		 
   * NEGATIVE_GRAY_SCALE   
   * GREEN_YELLOW_SCALE	 
   * HEATED_OBJECT_SCALE   
   * HEATED_OBJECT_SCALE_2 
   * RAINBOW_SCALE	 
   * OPTIMAL_SCALE	 
   * MULTI_SCALE  	 
   * SPECTRUM_SCALE
   */	 
   
  /**
   * Constructor used for calibrated color scale.
   *
   *  @param  colorscaleaddible component
   *  @param  orientation
   */ 
   public ControlColorScale( IColorScaleAddible icsa, boolean orientation )
   {  
      super("");
      component = icsa; 
      isBasic = false;     
      setOrientation(orientation); 
      this.setLayout( new OverlayLayout(this) );
      this.add( axis );
      this.add(background);
      colorscheme = component.getColorScale(); 
      csi.setNamedColorModel( colorscheme, true ); 
      setAxisVisible(true); 
      font = component.getFont();
      component.addActionListener( new ColorChangedListener() );
   }
   
  /**
   * Constructor creates a basic colorscale without calibration.
   * Possible color schemes are listed in IndexColorMaker.java.
   *
   *  @param  colorscale
   */ 
   public ControlColorScale( String colorscale )
   {  
      super("");
      this.setLayout( new GridLayout(1,1) );
      isBasic = true;
      csi = new ColorScaleImage();
      csi.setEventListening(false);
      this.add(csi);
      colorscheme = colorscale; 
      csi.setNamedColorModel( colorscale, true );   
   }
  
  /**
   * This method sets the color scheme of the ColorScaleImage.
   * Possible color schemes are listed in IndexColorMaker.java.
   *
   *  @param  colorscale
   */
   public void setColorScale( String colorscale )
   {
      colorscheme = colorscale;
      csi.setNamedColorModel( colorscale, true );
   } 
   
  /**
   * This method gets the color scheme of the ColorScaleImage.
   * The scheme will be one listed in IndexColorMaker.java.
   *
   *  @return colorscheme
   */
   public String getColorScale()
   {
      if( !isBasic )
         colorscheme = component.getColorScale();
      return colorscheme;
   }  
  
  /**
   * This method sets the color scheme of the ColorScaleImage.
   * Possible color schemes are listed in IndexColorMaker.java.
   *
   *  @param  colorscale
   */
   public void setLogScale( double value )
   {
      csi.changeLogScale( value, true );
   } 
   
  /**
   * This method sets the orientation of the ColorScaleImage to either
   * horizontal or vertical.
   *
   *  @param  showAxis
   */
   public void setAxisVisible( boolean showAxis)
   {
      if( !isBasic )
      {
         if( showAxis )
         {
            background.getComponent(1).setVisible( true );
            background.getComponent(2).setVisible( true );
            background.getComponent(3).setVisible( true );
            background.getComponent(4).setVisible( true );
	    axis.setVisible( true );
         }
         else
         {
            background.getComponent(1).setVisible( false );
            background.getComponent(2).setVisible( false );
            background.getComponent(3).setVisible( false );
            background.getComponent(4).setVisible( false );
	    axis.setVisible( false );
         }
      }
      else
      {
         System.out.println("setAxisVisible() is not available with the " +
	                    "constructor chosen. Use constructor: " +
			    "public ControlColorScale( " +
			    "IColorScaleAddible icsa, boolean orientation ) " +
			    "to enable this method." );
      }
      
   }            

  /**
   * The boolean, either true for x, or false for y, will determine which axis
   * to get information for. The information is wrapped in an AxisInfo2D object.
   *
   *  @param  isX
   *  @return X or Y axisinfo of the component
   *          If this is a basic color scale, a dumby value is returned.
   */
   public AxisInfo2D getAxisInfo(boolean isX)
   {
      if( !isBasic )
         return new AxisInfo2D( component.getAxisInfo(isX).getMin(),
                                        component.getAxisInfo(isX).getMax(),
					"","",true );
      else
      {
         System.out.println("getAxisInfo() is not available with the " +
	                    "constructor chosen. Use constructor: " +
			    "public ControlColorScale( " +
			    "IColorScaleAddible icsa, boolean orientation ) " +
			    "to enable this method." );
         return new AxisInfo2D( 0,1,"","",true );
      }
   }
   
  /**
   * This method will return a rectangle with pixel coordinates corresponding
   * to the desired region.
   *
   *  @return bounds of the center image.
   */ 
   public Rectangle getRegionInfo()
   {
      return csi.getBounds();
   }

  /**
   * This method will return the precision specified by the user. Precision
   * will be assumed to be 4 if not specified. The axis overlays will call
   * this method to determine the precision.
   *
   *  @return precision of the component
   *          If a basic color scale, -1 is returned.
   */
   public int getPrecision()
   {
      if( !isBasic )
         return component.getPrecision();
      else
      {
         System.out.println("getPrecision() is not available with the " +
	                    "constructor chosen. Use constructor: " +
			    "public ControlColorScale( " +
			    "IColorScaleAddible icsa, boolean orientation ) " +
			    "to enable this method." );
         return -1;
      }
   } 
   
  /**
   * This method will return the font used on by the overlays. The axis overlay
   * will call this to determine what font to use.
   *
   *  @return font of component
   *          If a basic color scale, the view control font is returned.
   */
   public Font getFont()
   {
      if( !isBasic )
         return font;
      else
         return super.getFont();
   }
   
  /**
   * This method overloads the ViewControls getTitle() so that no title is
   * passed. Because the axis overlay displays the title, we need to set it
   * to an empty string. The title is already displayed by the titled border.
   *
   *  @return empty string
   */
   public String getTitle()
   {
      return "";
   }   
   
  /**
   * This method will return the local coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   *
   *  @return local bounds of the component
   *          If a basic color scale, dumby value returned.
   */
   public CoordBounds getLocalCoordBounds()
   {
      if( !isBasic )
         return component.getLocalCoordBounds();
      else
      {
         System.out.println("getLocalCoordBounds() is not available with " +
	                    "the constructor chosen. Use constructor: " +
			    "public ControlColorScale( " +
			    "IColorScaleAddible icsa, boolean orientation ) " +
			    "to enable this method." );
         return new CoordBounds( 0,0,1,1 );
      }
   }
      
  /**
   * This method will return the global coordinate bounds of the center
   * jpanel. To be implemented, the center may have to be a coordjpanel.
   *
   *  @return global bounds of the component
   *          If a basic color scale, dumby value returned.
   */
   public CoordBounds getGlobalCoordBounds()
   {
      if( !isBasic )
         return component.getGlobalCoordBounds();
      else
      {
         System.out.println("getGlobalCoordBounds() is not available with " +
	                    "the constructor chosen. Use constructor: " +
			    "public ControlColorScale( " +
			    "IColorScaleAddible icsa, boolean orientation ) " +
			    "to enable this method." );
         return new CoordBounds( 0,0,1,1 );
      }
   }
/*   
   public void paint( Graphics g )
   {
      
   }
*/   
  /**
   * This private method sets the orientation of the ColorScaleImage to either
   * horizontal or vertical and adjusts the structure accordingly.
   *
   *  @param  orientation
   */
   private void setOrientation( boolean orientation)
   {
      axis = new AxisOverlay2D( this );
       
      JPanel north = new JPanel();
      JPanel east = new JPanel(); 
      JPanel south = new JPanel(); 
      JPanel west = new JPanel(); 
      
      // if true, horizontal alignment
      if( orientation )
      {
         axis.setDisplayAxes(AxisOverlay2D.X_AXIS); 
         north.setPreferredSize(new Dimension( 0, 0 ) );
         east.setPreferredSize(new Dimension( 20, 0 ) ); 
         south.setPreferredSize(new Dimension( 0, 35 ) );
         west.setPreferredSize(new Dimension( 20, 0 ) ); 
         csi = new ColorScaleImage(ColorScaleImage.HORIZONTAL);
      }
      // else vertical alignment
      else
      { 
         axis.setDisplayAxes(AxisOverlay2D.Y_AXIS); 
         north.setPreferredSize(new Dimension( 0, 10 ) );
         east.setPreferredSize(new Dimension( 0, 0 ) ); 
         south.setPreferredSize(new Dimension( 0, 10 ) );
         west.setPreferredSize(new Dimension( 60, 0 ) ); 
         csi = new ColorScaleImage(ColorScaleImage.VERTICAL);
      }
      csi.setEventListening(false);
      
      background = new JPanel( new BorderLayout() );
      background.add( csi, "Center" );
      background.add( north, "North" ); 
      background.add( east, "East" ); 
      background.add( south, "South" );
      background.add( west, "West" );
   }
   
   private class ColorChangedListener implements ActionListener
   {
      public void actionPerformed( ActionEvent e )
      {
         String message = e.getActionCommand();
	 if( message.equals( component.getColorScale() ) )
	    setColorScale( message );
         else if ( message == IViewControl.SLIDER_CHANGED )
         { 
	    IColorScaleAddible temp = (IColorScaleAddible)e.getSource();
	    setLogScale( temp.getLogScale() );
         }
      }
   }   

  /*
   *  For testing purposes only
   */
   public static void main(String[] args)
   {      
      int col = 250;
      int row = 250;	
      //Make a sample 2D array
      VirtualArray2D va2D = new VirtualArray2D(row, col); 
      va2D.setAxisInfoVA( AxisInfo2D.XAXIS, 0f, .0001f, 
                           "TestX","TestUnits", true );
      va2D.setAxisInfoVA( AxisInfo2D.YAXIS, 0f, .001f, 
                            "TestY","TestYUnits", true );
      va2D.setTitle("Main Test");
      //Fill the 2D array with the function x*y
      float ftemp;
      for(int i = 0; i < row; i++)
      {
         for(int j = 0; j < col; j++)
         {
            ftemp = i*j;
            if ( i % 25 == 0 )
	       va2D.setDataValue(i, j, i*col); //put float into va2D
            else if ( j % 25 == 0 )
	       va2D.setDataValue(i, j, j*row); //put float into va2D
            else
	       va2D.setDataValue(i, j, ftemp); //put float into va2D
	 }
      }
      ImageViewComponent ivc = new ImageViewComponent(va2D);
      ControlColorScale color = 
                  new ControlColorScale(ivc, ControlColorScale.HORIZONTAL);
      /*ControlColorScale color = 
                  new ControlColorScale(IndexColorMaker.GRAY_SCALE);*/ 
      
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      frame.getContentPane().setLayout( new GridLayout(1,1) );
      frame.setTitle("ControlColorScale Test");
      frame.setBounds(0,0,300,300);
      frame.getContentPane().add(color);
      color.setTitle("myColorScale");
      frame.setVisible(true);
   }
}
