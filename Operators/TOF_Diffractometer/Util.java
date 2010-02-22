/* 
 * File: Util.java
 *
 * Copyright (C) 2010, Ruth Mikkelson
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
 * This work was supported by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge, TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author:$
 *  $Date:$            
 *  $Rev:$
 */
package Operators.TOF_Diffractometer;

import gov.anl.ipns.MathTools.Geometry.DetectorPosition;
import gov.anl.ipns.MathTools.Geometry.Vector3D;
import gov.anl.ipns.Util.File.*;
import DataSetTools.dataset.*;
import EventTools.EventList.*;
import EventTools.Histogram.*;
import DataSetTools.instruments.*;


/**
 * This class contains utility methods and some static methods that are
 * converted to operators.
 * 
 * @author ruth
 *
 */
public class Util
{
   /**
    * Makes a DataSet in d-spacing for each detector from Event Data
    * 
    * @param EventFileName    The name of the file with events
    * @param DetCalFileName   The name of the file with the detector 
    *                         calibrations
    * @param bankInfoFileName The name of the file with bank and pixelID(nex) 
    *                         info
    * @param MappingFileName  The name of the file that maps DAS pixel_id's
    *                         to NeXus pixel_id's
    * @param firstToLoad      The first Event to load
    * @param numToLoad        The number of events to load
    * @param min              The minimum d-spacing to consider
    * @param max              The maximum d-spacing to consider
    * @param isLog            If true use log binning, otherwise use uniform
    *                         binnings           
    * @param nUniformbins     The number of uniform bins( isLog=false )
    * @param first_logStep    The length of first interval( isLog = true )
    * 
    * @return  A DataSet in d-spacing whose spectra are the summed d-spacing
    *           for a detector.
    */
   public static DataSet Make_d_DataSet( String  EventFileName,
                                         String  DetCalFileName,
                                         String  bankInfoFileName,
                                         String  MappingFileName,
                                         float   firstToLoad,
                                         float   numToLoad,
                                         float   min,
                                         float   max,
                                         boolean isLog,
                                         float   first_logStep,
                                         int     nUniformbins
                                         )
                         throws Exception
   {
         String Instrument = FileIO.getSNSInstrumentName( EventFileName );
         SNS_Tof_to_Q_map SMap = 
                     new SNS_Tof_to_Q_map( DetCalFileName, null, Instrument);

         SNS_TofEventList STOF = new  SNS_TofEventList(EventFileName);
         
         IEventBinner binner;
         if( isLog)
            binner = new LogEventBinner( min, max, first_logStep);
         else
            binner = new UniformEventBinner( min,max,nUniformbins);

         long firstEvent = (long)firstToLoad;
         long NumEventsToLoad = (long)numToLoad;

         if ( firstEvent >= STOF.numEntries() )
           throw new IllegalArgumentException("first event " + firstEvent +
                    " exceeds number of events in file " + STOF.numEntries());

                                                       // keep events in range
         long last = firstEvent + NumEventsToLoad - 1;
         if ( last >= STOF.numEntries() )
           last =  STOF.numEntries() - 1;

         long num_to_load = last - firstEvent + 1;
                                                       // keep each segment
                                                       // small enough for int
         long seg_size   = 10000000;
         seg_size = Math.min( seg_size, Integer.MAX_VALUE / 2 );

         long num_segments = num_to_load / seg_size + 1;
         seg_size = num_to_load / num_segments;

         int[][] Histograms = null;
         boolean first_time = true;
         long num_loaded = 0;
         for ( int i = 0; i < num_segments; i ++ )
         {
           seg_size = Math.min( seg_size, num_to_load - num_loaded );

           int[] buffer = STOF.rawEvents( firstEvent, seg_size );

           TofEventList sublist = new TofEventList(buffer,buffer.length,false);

           int[][]temp = SMap.Make_d_Histograms( sublist, 
                                                 0, 
                                                (int)seg_size, 
                                                 binner );
           if ( first_time && temp != null )
           {
             Histograms = temp;
             first_time = false;
           }
           else if ( temp != null )          // add in the new histogram data
           {
             for ( int row = 0; row < Histograms.length; row++ )
               if ( temp[row] != null && Histograms[row] != null )
               for ( int col = 0; col < Histograms[row].length; col++ )
                 Histograms[row][col] += temp[row][col];
           }
           num_loaded += seg_size;
           firstEvent += seg_size;
         }

         System.out.println("NUMBER OF EVENTS LOADED = " + num_loaded );

         if( Histograms == null)
            return null;

         DataSet DS = new DataSet( "d Graphs","Converted Each detector to d");
         DS.setX_units( "Angstroms");
         DS.setX_label( "d-Spacing" );
         DS.setY_units( "Counts" );
         DS.setY_label("Intensity");
         
         float[] xs = new float[ binner.numBins( )+1];
         for( int i=0; i< xs.length;i++)
            xs[i]=(float)binner.minVal( i );
         
         xs[xs.length-1]=(float)binner.maxVal( xs.length-1 );
         
         VariableXScale xscl = new VariableXScale( xs );
         
         for( int i=0; i < Histograms.length; i++)
         {
            if( Histograms[i] != null)
            {
               float[] yvals = new float[Histograms[i].length];
               for( int j=0; j<yvals.length; j++)
                  yvals[j] = Histograms[i][j];
               
               HistogramTable D = new  HistogramTable( xscl,
                     yvals,i) ;
               
               DS.addData_entry( D );
            }
         }
         
         DataSetFactory.addOperators( DS, InstrumentType.TOF_DIFFRACTOMETER );
         float L1 = SMap.getL1( );
         float T0 = SMap.getT0( );
         Attribute L1Attr = new FloatAttribute( Attribute.INITIAL_PATH, L1);
         Attribute T0Attr = new FloatAttribute( Attribute.T0_SHIFT, T0);

         DS.setAttribute( L1Attr );
         DS.setAttribute( T0Attr );

         return DS;
   }
   

   /**
    * Makes a DataSet from Event Data where each detector is time focused
    * 
    * @param EventFileName    The name of the file with events
    * @param DetCalFileName   The name of the file with the detector 
    *                         calibrations
    * @param bankInfoFileName The name of the file with bank and pixelID(nex) 
    *                         info
    * @param MappingFileName  The name of the file that maps DAS pixel_id's
    *                         to NeXus pixel_id's
    * @param firstToLoad      The first Event to load
    * @param numToLoad        The number of events to load
    * @param angle_deg        The "virtual" scattering angle, two theta, 
    *                         (in degrees) to which the data should be focused
    *
    * @param final_L_m        The final flight path length (in meters) to which
    *                         the data should be focused
    * @param min              The minimum time to consider
    * @param max              The maximum time to consider
    * @param isLog            If true use log binning, otherwise use uniform
    *                            binnings           
    * @param nUniformbins     The number of uniform bins( isLog=false )
    * @param first_logStep    The length of first interval( isLog = true )
    * 
    * @return a DataSet from Event Data where each detector is time focused
    */
   public static DataSet MakeTimeFocusedDataSet( 
                                         String  EventFileName,
                                         String  DetCalFileName,
                                         String  bankInfoFileName,
                                         String  MappingFileName,
                                         float   firstToLoad,
                                         float   numToLoad,
                                         float   angle_deg,
                                         float   final_L_m, 
                                         float   min,
                                         float   max,
                                         boolean isLog,
                                         float   first_logStep,
                                         int     nUniformbins
                                         )
                         throws Exception
   {
         String Instrument = FileIO.getSNSInstrumentName( EventFileName );
         SNS_Tof_to_Q_map SMap =
                    new SNS_Tof_to_Q_map( DetCalFileName, null, Instrument);

         SNS_TofEventList STOF = new SNS_TofEventList(EventFileName);
         
         IEventBinner binner;
         if( isLog)
            binner = new LogEventBinner( min, max, first_logStep);
         else
            binner = new UniformEventBinner( min,max,nUniformbins);

         long firstEvent = (long)firstToLoad;
         long NumEventsToLoad = (long)numToLoad;

         if ( firstEvent >= STOF.numEntries() )
           throw new IllegalArgumentException("first event " + firstEvent +
                    " exceeds number of events in file " + STOF.numEntries());

                                                       // keep events in range
         long last = firstEvent + NumEventsToLoad - 1;
         if ( last >= STOF.numEntries() )
           last =  STOF.numEntries() - 1;
  
         long num_to_load = last - firstEvent + 1;        
                                                       // keep each segment 
                                                       // small enough for int
         long seg_size   = 10000000;
         seg_size = Math.min( seg_size, Integer.MAX_VALUE / 2 );  

         long num_segments = num_to_load / seg_size + 1;
         seg_size = num_to_load / num_segments;
 
         int[][] Histograms = null;
         boolean first_time = true;
         long num_loaded = 0;
         for ( int i = 0; i < num_segments; i ++ )
         {
           seg_size = Math.min( seg_size, num_to_load - num_loaded );

           int[] buffer = STOF.rawEvents( firstEvent, seg_size );
          
           TofEventList sublist = new TofEventList(buffer,buffer.length,false);

           int[][]temp = SMap.Make_Time_Focused_Histograms( sublist,   
                                                            0, 
                                                           (int)seg_size, 
                                                            binner ,
                                                            angle_deg , 
                                                            final_L_m );
           if ( first_time && temp != null )
           {
             Histograms = temp;
             first_time = false;
           }
           else if ( temp != null )          // add in the new histogram data
           {
             for ( int row = 0; row < Histograms.length; row++ )
               if ( temp[row] != null && Histograms[row] != null )
               for ( int col = 0; col < Histograms[row].length; col++ )
                 Histograms[row][col] += temp[row][col];
           }
           num_loaded += seg_size;
           firstEvent += seg_size;
         }

         System.out.println("NUMBER OF EVENTS LOADED = " + num_loaded );

         if( Histograms == null)
            return null;
         
         DataSet DS = new DataSet( "TimeFocused","Time Focused Each detecot");
         DS.setX_units( "us");
         DS.setX_label( "time" );
         DS.setY_units( "Counts" );
         DS.setY_label("Intensity");
         
         float[] xs = new float[ binner.numBins( )+1];
         for( int i=0; i< xs.length;i++)
            xs[i]=(float)binner.minVal( i );
         
         xs[xs.length-1]=(float)binner.maxVal( xs.length-1 );
         
         VariableXScale xscl = new VariableXScale( xs );

         float[] position=new float[3];
         float angleRad = (float)(angle_deg*Math.PI/180);
         position[2] =0;
         position[0] =(float)( final_L_m*Math.cos( angleRad ));
         position[1] =(float)( final_L_m*Math.sin( angleRad ));
         DetectorPosition dp = new DetectorPosition(new Vector3D(position));
         
         float L1 = SMap.getL1( );
         float T0 = SMap.getT0( );
         Attribute L1Attr = new FloatAttribute( Attribute.INITIAL_PATH, L1);
         Attribute T0Attr = new FloatAttribute( Attribute.T0_SHIFT, T0);
         int pixelNum = 0;
         
         for( int i=0; i < Histograms.length; i++)
         {
            if( Histograms[i] != null)
            {
               float[] yvals = new float[Histograms[i].length];
               for( int j=0; j<yvals.length; j++)
                  yvals[j] = Histograms[i][j];
               
               HistogramTable D = new  HistogramTable( xscl,
                     yvals, i ) ;
               
               Vector3D pos = new Vector3D(position[0],position[1],
                     position[2]);
               D.setAttribute(  new DetPosAttribute(Attribute.DETECTOR_POS,
                     new DetectorPosition( pos)));
               D.setAttribute(  L1Attr );
               D.setAttribute(T0Attr);
               
               UniformGrid grid = 
                       new UniformGrid(i,"m",pos, new Vector3D(1,0,0),
                                        new Vector3D(0,1,0),.2f,.2f,.2f,1,1);
               DetectorPixelInfo pix = new DetectorPixelInfo(pixelNum,(short)1,
                     (short)1,grid);
               D.setAttribute(  new PixelInfoListAttribute( 
                     Attribute.PIXEL_INFO_LIST, new PixelInfoList(pix)) );
               DS.addData_entry( D );
            }
         }
         
         DataSetFactory.addOperators( DS, InstrumentType.TOF_DIFFRACTOMETER );

         DS.setAttribute( L1Attr );
         DS.setAttribute( T0Attr ); 
          
         return DS;
   }


   /**
    * @param args
    */
   public static void main_Make_dDS(String[] args) throws Exception
   {
      String Instrument ="SNAP";
      String EventFileName=
                "C:/Users/ruth/SNS/EventData/Snap_240_neutron_event.dat";

      String DetCalFileName="C:/ISAW/InstrumentInfo/SNS/SNAP/SNAP.DetCal";
      String bankInfoFileName=null;
      String MappingFileName=null;
      int firstEvent=1;
      int NumEventsToLoad=8452339;
      boolean  isLog=true;
      float min=.2f;
      float max=10;
      int nUniformbins=10000;
      float first_logStep=.0002f;
      DataSet D = Util.Make_d_DataSet( EventFileName,  
                                       DetCalFileName,
                                       bankInfoFileName, 
                                       MappingFileName, 
                                       firstEvent, 
                                       NumEventsToLoad, 
                                       min, 
                                       max,
                                       isLog,  
                                       first_logStep, 
                                       nUniformbins );
      Command.ScriptUtil.display( D );
   }
   
   public static void main(String[] args) throws Exception
   {
      String EventFileName=
                 "C:/Users/ruth/SNS/EventData/Snap_240_neutron_event.dat";

      String DetCalFileName="C:/ISAW/InstrumentInfo/SNS/SNAP/SNAP.DetCal";
      String bankInfoFileName=null;
      String MappingFileName=null;
      int firstEvent=1;
      int NumEventsToLoad=8452339;
      boolean  isLog=false;
      float min=1000f;
      float max=20000;
      int nUniformbins=1000;
      float first_logStep=.0002f;
      DataSet D = Util.MakeTimeFocusedDataSet( EventFileName, 
                                               DetCalFileName,
                                               bankInfoFileName, 
                                               MappingFileName, 
                                               firstEvent, 
                                               NumEventsToLoad,
                                               90f,
                                               .5f, 
                                               min, 
                                               max,
                                               isLog, 
                                               first_logStep, 
                                               nUniformbins );
      Command.ScriptUtil.display( D );
   }

}