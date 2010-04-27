
package EventTools.ShowEventsApp.DataHandlers;

import java.util.Arrays;
import java.util.Vector;

import gov.anl.ipns.MathTools.LinearAlgebra;
import gov.anl.ipns.MathTools.Geometry.Vector3D;
import gov.anl.ipns.MathTools.Geometry.Vector3D_d;

import DataSetTools.components.ui.Peaks.OrientMatrixControl;
import DataSetTools.operator.Generic.Special.ViewASCII;
import DataSetTools.operator.Generic.TOF_SCD.*;

import Operators.TOF_SCD.IndexPeaks_Calc;
import Operators.TOF_SCD.LsqrsJ_base;
import Operators.TOF_SCD.ARCS_Index_Calc;

import MessageTools.IReceiveMessage;
import MessageTools.Message;
import MessageTools.MessageCenter;

import EventTools.ShowEventsApp.Command.Commands;
import EventTools.ShowEventsApp.Command.IndexPeaksCmd;
import EventTools.ShowEventsApp.Command.IndexARCS_PeaksCmd;
import EventTools.ShowEventsApp.Command.SelectionInfoCmd;
import EventTools.ShowEventsApp.Command.UBwTolCmd;
import EventTools.ShowEventsApp.Command.Util;


public class PeakListHandler implements IReceiveMessage
{
  private MessageCenter message_center;
  private Vector<PeakQ>    peakQ_list   = new Vector<PeakQ>();
  private Vector<Peak_new> peakNew_list = new Vector<Peak_new>();
  private float[][] UB = null;
  private float tolerance =.12f;

  public PeakListHandler( MessageCenter message_center )
  {
    this.message_center = message_center;
    message_center.addReceiver( this, Commands.SET_PEAK_Q_LIST );
    message_center.addReceiver( this, Commands.SET_ORIENTATION_MATRIX);
    message_center.addReceiver( this, Commands.SET_PEAK_NEW_LIST );
    message_center.addReceiver( this, Commands.WRITE_PEAK_FILE );
    message_center.addReceiver( this, Commands.SHOW_PEAK_FILE );
    message_center.addReceiver( this, Commands.INIT_HISTOGRAM );
    message_center.addReceiver( this, Commands.ADD_PEAK_LIST_INFO );
 
    message_center.addReceiver( this, Commands.INDEX_PEAKS );
    message_center.addReceiver( this, Commands.INDEX_PEAKS_ARCS );
    message_center.addReceiver( this, Commands.INDEX_PEAKS_ROSS );
    message_center.addReceiver( this, 
                                Commands.INDEX_PEAKS_WITH_ORIENTATION_MATRIX);
  }


  public boolean receive( Message message )
  {
//    System.out.println("***PeakListHandler in thread " 
//                       + Thread.currentThread());

    if ( message.getName().equals(Commands.SET_PEAK_Q_LIST) )
    {
      Object obj = message.getValue();
      if ( obj == null )
        return( false );

      if ( obj instanceof Vector ) 
      {
         Vector<PeakQ> new_peaks = (Vector<PeakQ>)obj;
         peakQ_list = new Vector<PeakQ>();
         for ( int i = 0; i < new_peaks.size(); i++ )
           peakQ_list.add( new_peaks.elementAt(i) );

         Message get_peak_new_list = new Message( Commands.GET_PEAK_NEW_LIST,
                                                 peakQ_list,
                                                 true,
                                                 true );
         message_center.send( get_peak_new_list );

         System.out.println("IN PeakListHandler set PEAK Q, #PeakQ = " + 
                             peakQ_list.size() +
                             " #Peak_new = " + peakNew_list.size() );
      } 
    }

    else if ( message.getName().equals(Commands.SET_PEAK_NEW_LIST) )
    {
      Object obj = message.getValue();
      if ( obj == null )
        return false;

      if ( obj instanceof Vector )
      {
         Vector<Peak_new> new_peaks = (Vector<Peak_new>)obj;
         peakNew_list = new Vector<Peak_new>();
         for ( int i = 0; i < new_peaks.size(); i++ )
           peakNew_list.add( new_peaks.elementAt(i) );

         System.out.println("IN PeakListHandler set NEW PEAKS, #PeakQ = " + 
                             peakQ_list.size() +
                             " #Peak_new = " + peakNew_list.size() );
         if( UB != null)
            indexAllPeaks(new_peaks,UB,tolerance);
      }
      } else if ( message.getName( ).equals( Commands.INIT_HISTOGRAM ) )
      {

         peakQ_list = new Vector< PeakQ >( );
         peakNew_list = new Vector< Peak_new >( );
         UB = null;

      } else if ( message.getName( ).equals( Commands.SET_ORIENTATION_MATRIX ) )
      {
        
         Object obj = message.getValue();
        if( obj instanceof float[][])
           UB = (float[][])obj;
        
        else if( obj instanceof Vector && ((Vector)obj).size()==2 && 
              ((Vector)obj).firstElement( ) instanceof float[][] )
           UB= (float[][]) ((Vector)obj).firstElement( );
           
           
          return false;
          
      } else if( message.getName( ).equals( Commands.ADD_PEAK_LIST_INFO ))
       
       {
       Object val = message.getValue();
       if ( val instanceof SelectionInfoCmd )         // fill in counts field
       {
         SelectionInfoCmd select_info_cmd = (SelectionInfoCmd)val;
         Peak_new[]Sav = new Peak_new[ peakNew_list.size()];
         Copy( Sav, peakNew_list);
         Arrays.sort( Sav, new Peak_newBasicComparator() );
         select_info_cmd.setSeqNum( getNearestSeqNum(Sav, select_info_cmd.getQxyz( ),
               (int)select_info_cmd.getDetNum( )) );
         message_center.send( new Message( Commands.SHOW_SELECTED_POINT_INFO,
               select_info_cmd, false));
         
         
       }
    }

    else if ( message.getName().equals(Commands.WRITE_PEAK_FILE ) )
    {
      if ( peakNew_list == null || peakNew_list.size() <= 0 )
      {
        Util.sendError( "ERROR: No Peaks to write" );
      }
      
      String file_name = (String)message.getValue();
      try 
      {
        Peak_new_IO.WritePeaks_new( file_name, (Vector)peakNew_list, false );
      }
      catch ( Exception ex )
      {
        Util.sendError( "ERROR: could not write peaks to file " +
                        file_name );
        return false;
      }
    }

    else if ( message.getName().equals(Commands.SHOW_PEAK_FILE ) )
    {
      if ( peakNew_list == null || peakNew_list.size() <= 0 )
      {
        Util.sendError( "ERROR: No Peaks to write" );
        return false;
      }

     
      String file_name = gov.anl.ipns.Util.File.FileIO.appendPath( 
            System.getProperty( "user.home" ), "ISAW/tmp/ppp.peaks" );
      try 
      {
        Peak_new_IO.WritePeaks_new( file_name, (Vector)peakNew_list, false );
        (new ViewASCII(file_name)).getResult();        
       
      }
      catch ( Exception ex )
      {
        Util.sendError( "ERROR: could not write peaks to file " +
                        file_name );
        return false;
      }
      Wizard.TOF_SCD.Util.ClearFiles( "ppp" , "peaks" );
    }

    else if ( message.getName().equals(Commands.INDEX_PEAKS ) )
    {
      Object obj = message.getValue();
      if ( obj == null || !(obj instanceof IndexPeaksCmd) )
      {
        Util.sendError("ERROR: wrong value object in INDEX_PEAKS command");
        return false;
      }

      if ( peakNew_list == null || peakNew_list.size() <= 0 )
      {
        Util.sendError( "ERROR: No peaks found... can't index yet");
        return false;
      }

      IndexPeaksCmd cmd = (IndexPeaksCmd)obj;

      float[][] UB = null;
      try
      {
        tolerance = cmd.getTolerance();
        Util.sendInfo("Starting to index peaks, PLEASE WAIT...");
        UB = IndexPeaks_Calc.IndexPeaksWithOptimizer( 
                                                  peakNew_list,
                                                  cmd.getA(),
                                                  cmd.getB(),
                                                  cmd.getC(),
                                                  cmd.getAlpha(),
                                                  cmd.getBeta(),
                                                  cmd.getGamma(),
                                                  tolerance,
                                                  cmd.getRequiredFraction(),
                                                  cmd.getFixedPeakIndex() );

        this.UB = getErrors( UB, Convert2IPeak(peakNew_list), tolerance ); 

        Util.sendInfo( "Finished Indexing" );
      }
      catch ( Exception ex )
      {
        Util.sendError( "ERROR: Failed to index Peaks " + ex);
        return false;
      } 

     /*
      Message set_peaks = new Message( Commands.SET_PEAK_NEW_LIST,
                                       peakNew_list,
                                       true );
      message_center.send( set_peaks );

      Message set_or = new Message( Commands.SET_ORIENTATION_MATRIX,
                                    UB, true );
      message_center.send( set_or );
      
      message_center.send(  new Message( 
               Commands.INDEX_PEAKS_WITH_ORIENTATION_MATRIX,
               new UBwTolCmd( UB, cmd.getTolerance()),false) );
     */
      return false;
    }

    else if( message.getName().equals( Commands.INDEX_PEAKS_ARCS) )
    {
      Object obj = message.getValue();
      System.out.println("IndexPeaksCmd = \n " + obj );
      if ( obj == null || !(obj instanceof IndexARCS_PeaksCmd) )
      {
        Util.sendError("ERROR: wrong value object in INDEX_PEAKS command");
        return false;
      }

      IndexARCS_PeaksCmd cmd = (IndexARCS_PeaksCmd)obj;
      tolerance = cmd.getTolerance();
      Vector results   = null;
      try
      {
        results = ARCS_Index_Calc.index ( cmd.getLatticeParameters(),
                                          peakNew_list,
                                          cmd.getPSI(),
                                          cmd.getU_hkl(),
                                          cmd.getV_hkl(),
                                          tolerance,
                                          cmd.getInitialNum(),
                                          cmd.getRequiredFraction() );
       }
       catch ( Exception ex )
       {
         Util.sendError( "ERROR: Failed to index Peaks " + ex);
         return false;
       }                                        
  
       float[][] UB          = (float[][]) results.elementAt(0);
       double psi            = (Double)    results.elementAt(1);
       Vector3D_d u_proj_hkl = (Vector3D_d)results.elementAt(2);
       Vector3D_d v_proj_hkl = (Vector3D_d)results.elementAt(3);
       
       this.UB = null;
       if ( UB != null )
       {
//       System.out.println("UB = ");
//       LinearAlgebra.print( UB );
         String psiStr = String.format("PSI = %8.4f\n", psi);
         String uStr = String.format("U Projected HKL  = %7.4f  %7.4f  %7.4f\n",
                    u_proj_hkl.getX(), u_proj_hkl.getY(), u_proj_hkl.getZ() );
         String vStr = String.format("V Projected HKL  = %7.4f  %7.4f  %7.4f\n",
                    v_proj_hkl.getX(), v_proj_hkl.getY(), v_proj_hkl.getZ() );
      
         Util.sendInfo( "\n" + psiStr + uStr + vStr );

         UB = getErrors( UB, Convert2IPeak(peakNew_list), tolerance );

         Util.sendInfo( "Finished Indexing" );
         this.UB = UB;
       }
       else
         Util.sendInfo( "ARCS Indexing FAILED, use different U,V,PSI" );
    } 

    else if( message.getName().equals( 
             Commands.INDEX_PEAKS_WITH_ORIENTATION_MATRIX ))
    {
       
       UBwTolCmd UBB = (UBwTolCmd)message.getValue();
       
       indexAllPeaks( peakNew_list, UBB.getUB(), UBB.getOffIntMax());
       Message set_peaks = new Message( Commands.SET_PEAK_NEW_LIST,
                                        peakNew_list,
                                        true );
       this.UB = UBB.getUB( );
       tolerance = UBB.getOffIntMax( );
       message_center.send( set_peaks );
       return false;
    } 

    else if( message.getName().equals( Commands.INDEX_PEAKS_ROSS ))
    {
       float[] value = (float[])message.getValue();
       if( value == null || value.length < 3)
          return false;
       
       tolerance = value[2];
       
       Util.sendInfo( "Starting long calculation. Please wait..." );
       GetUB.DMIN = value[0];
       GetUB.ELIM_EQ_CRYSTAL_PARAMS = true;
       
       Vector<float[][]> OrientationMatrices =
                GetUB.getAllOrientationMatrices( peakNew_list , null , 
                                                .01f , value[1] );
       GetUB.DMIN = 1f;
       GetUB.ELIM_EQ_CRYSTAL_PARAMS = false;
       if( OrientationMatrices == null)
       {
           Util.sendError( 
                 "No Orientation Matrix found in Auto no Crystal Parameters" );
           return false;
       }
       Vector<IPeak> Peaks = Convert2IPeak(peakNew_list);
       float[][]UB = OrientMatrixControl.showCurrentOrientationMatrices(
                Peaks , OrientationMatrices );
        
       if( UB == null)
       {
           Util.sendError( "No Orientation Matrix was selected" );
           this.UB = null;
           return false;
       }
        
       this.UB = getErrors( UB, Peaks, value[2]); 
        
      //  message_center.send(  
      //           new Message(Commands.INDEX_PEAKS_WITH_ORIENTATION_MATRIX,
      //           new UBwTolCmd(UBT,value[2]) ,false) );
       return false;
    }

    return false;
  }
  
  private void Copy( Peak_new[] Sav, Vector<Peak_new> PeakList)
  {
     if( Sav == null || PeakList== null ||Sav.length != PeakList.size())
        throw new IllegalArgumentException("null or improper array sizes. Cannot Sort");
     for( int i=0; i< Sav.length ; i++)
        Sav[i]= PeakList.elementAt( i );
  }

  private int getNearestSeqNum(Peak_new[] peakNew_list, Vector3D Qxyz, int detNum)
  {
     if( peakNew_list == null ||  Qxyz == null)
        return 0;
     if( peakNew_list.length < 1)
        return 0;
     float minQ=Float.MAX_VALUE;
     int seqNum =0;
     for( int i=0; i < peakNew_list.length; i++)
     {
        Peak_new peak =peakNew_list[i];
         if ( peak.detnum( ) == detNum )
         {
            Vector3D Q = new Vector3D( peak.getUnrotQ( ) );

            float d = Qxyz.distance( Q );
            if ( d < minQ )
            {
               minQ = d;
               seqNum = i + 1;
            }
         }
     }
    return seqNum; 
  }
  private float[][] getErrors(float[][] UB, Vector<IPeak>Peaks, float tolerance)
  {
    float[][] UBT = null;

    try
    {
      indexAllPeaks( peakNew_list, LinearAlgebra.getTranspose(UB), tolerance);
      double[][] UB2 = new double[3][3];
      double[] abc = new double[7];
      double[] sig_abc = new double[7];
      if( Double.isNaN( LsqrsJ_base.LeastSquaresSCD( UB2, 
           LsqrsJ_base.getHKLArrays( Peaks,null, -1f,null, null, -1),
           LsqrsJ_base.getQArray( Peaks ,-1f,null, null, -1), 
           abc, 
           sig_abc)))
           {
              Util.sendError( "LeastSquares Error. No error estimates" );
              UB2 = LinearAlgebra.float2double( UB );
              abc= sig_abc = null;
           }
     
      UBT = LinearAlgebra.double2float( LinearAlgebra.getTranspose( UB2 ) );
      Object messageValue = UBT;
      if( sig_abc != null)
      {
        messageValue = new Vector(2);
        ((Vector)messageValue).addElement( UBT);
        ((Vector)messageValue).add(LinearAlgebra.double2float( sig_abc ));
      }
      message_center.send( new Message( Commands.SET_ORIENTATION_MATRIX,
                                        messageValue, false));

      Message set_peaks = new Message( Commands.SET_PEAK_NEW_LIST,
                                       peakNew_list,
                                       true );
      message_center.send( set_peaks );

      Message mark_indexed  = new Message( Commands.MARK_INDEXED_PEAKS,
                                           peakNew_list,
                                           true,
                                           true );
      message_center.send( mark_indexed );
    }
    catch ( Exception ex )
    {
      Util.sendInfo("Indexing FAILED");
    } 
    return UBT;

  }


  private Vector<IPeak> Convert2IPeak( Vector<Peak_new> Peaks)
  {
     if( Peaks == null)
        return null;
     
     Vector<IPeak> Res = new Vector<IPeak>( Peaks.size());
     for( int i=0; i< Peaks.size(); i++)
        Res.add( (IPeak )Peaks.elementAt(i));
     
     return Res;
  }


  public static void indexAllPeaks(Vector Peaks, float[][]UBT, float tolerance)
  {
    float[][]UB = LinearAlgebra.getTranspose( UBT );
    int n=0;
    for( int i=0; i<Peaks.size(); i++)
    { 
       Object peak = Peaks.elementAt(i);
      
       if(peak != null && peak instanceof IPeak)
         {
            IPeak pk = (IPeak)peak;
            pk.sethkl( 0f,0f,0f);
            pk.UB( UB );
            pk.UB( null );
            if( distanceToInt(pk.h()) > tolerance ||
                distanceToInt(pk.k()) > tolerance ||
                distanceToInt(pk.l()) > tolerance )
            {
               pk.sethkl( 0f , 0f , 0f );
               n++; 
            }
         }
    }
    n = Peaks.size() -n;
    Util.sendInfo( "Indexed " + n + 
                   " out of " + Peaks.size() + 
                   " peaks to within "+tolerance );
  }


  private int numIndexed( Vector peaks, float tolerance )
  {
    if ( peaks == null )
      return 0;
    
    if ( tolerance <= 0 )
      return 0;

    int total_peaks = peaks.size();
    int count = 0;
    for ( int i = 0; i < total_peaks; i++ )
    {
      IPeakQ peak = peakNew_list.elementAt(i);
      if ( distanceToInt( peak.h() ) <= tolerance &&
           distanceToInt( peak.k() ) <= tolerance &&
           distanceToInt( peak.l() ) <= tolerance &&
           ( peak.h() != 0 || peak.k() != 0 || peak.l() != 0 ) )
        count++;
    }
    return count;
  }


  private static float distanceToInt( float val )
  {
    float rounded = Math.round(val);
    return Math.abs( val - rounded );
  }

}
