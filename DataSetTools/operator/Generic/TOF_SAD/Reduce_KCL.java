package DataSetTools.operator.Generic.TOF_SAD;


import java.io.*;
import java.lang.String;
import java.lang.Math;
import java.math.*;
import java.lang.Object;
import java.util.Vector;
import java.util.*;

import Command.*;
import DataSetTools.components.ParametersGUI.*;
import DataSetTools.components.ui.*;
import DataSetTools.dataset.*;
import DataSetTools.components.containers.*;
import DataSetTools.operator.DataSet.*;
import DataSetTools.instruments.*;
import DataSetTools.operator.*;
import DataSetTools.retriever.*;
import DataSetTools.util.*;
import DataSetTools.viewer.*;
import DataSetTools.viewer.Table.*;
import DataSetTools.math.*;
import DataSetTools.operator.DataSet.Math.DataSet.*;
import DataSetTools.operator.DataSet.Math.Analyze.*;
import DataSetTools.operator.DataSet.*;
import DataSetTools.parameter.*;
import DataSetTools.operator.DataSet.EditList.*;
import DataSetTools.operator.DataSet.Math.DataSet.*;
import DataSetTools.operator.Parameter;
import DataSetTools.parameter.*;
import DataSetTools.operator.DataSet.Attribute.*;
import DataSetTools.operator.DataSet.Conversion.XAxis.*;
import DataSetTools.operator.DataSet.Math.Scalar.*;
import java.io.*;

/**
 * TITLE:        Convert Fortran To JAVA
 * Description:  SMALL ANGLE NEUTRON SCATTERING ANALYSIS ROUTINE
 PRODUCE either A RADIALLY AVERAGED S(Q) VS Q ARRAY; SN(RUN#).dat
 OR PRODUCING AN S(QX,QY) ARRAY.	SN@D(RUN#).BIN

 PROGRAM REQUIRES
 File AREA DETECTOR SENSITIVITY and CELLS MASK FROM AREADETSEN
 File EFFICIENCY RATIO of AD/M1 FROM EFRATIO
 Files TRANSMISSION COEFFICIENTS FILE
 Scale factor and thickness in cm.

 Putting additional mask due to the nonlinearity near the left
 side of the area detector. Throw first 20 chans in the x direction.
 Search for "nedge" to locate the masking info - 11/15/96- PT,JK

 ANY RANGE OF TIME CHANNELS MAY BE EXAMINED, AND ANY CHANNEL OR
 GROUP OF CHANNELS MAY BE OMITTED.

 * @version 1.0
 */

public class Reduce_KCL {

    public static int Nedge = 1;      //mask off edge detectors or those
    public static float Radmin = 1.5f / 100;//too close or too far from the origin
    public static float Radmax = 100.0f / 100;
    DataSet TransS, TransB, Eff, Sens;
    float[] qu;
    DataSet[] RUNBds = new DataSet[2];
    DataSet[]RUNCds = new DataSet[2];;
    DataSet[]RUNSds = new DataSet[2];
    public String INSTR;
    public String INAME;
    String str1;
    int MaxSlice;
    /**
     *Parameters
     */

    XScale xscl;
    int MAXDET = 100;
    int MAXCST = 512;
    int MAXMON = 1100;
    int MAXX = 128;
    int MAXY = 128;
    int MAXQBIN = 512;
    int MAXSLC = MAXX * MAXY;
    int MAXCRV = 3;
    int MAXPTS = 512;
    int maxqxbin = 250;
    int maxqybin = 250;

    /**
     *   Variables
     */
    String ANS, ANSDN, ANSCH, ANSNL, ANSSD, ANSCOL, ANISOT, ANSAGN, ANSPLT, ANSCL, ANSD;
    String ansbt;
    //char[]  NAMCOL,NAMLIS,NAMLIS2,NAMLIS3,namsq2,namsq1,namsq=new char[20];
    String  REDUCEOUT;
    char[]  tttt1 = new char[80];
    char[]  users1 = new char[20];
    byte[] tttt = new byte[80];
    byte[] users = new byte[20];
    //char[] edt[1]= new char[9];
    byte[] sdt = new byte[9];
    byte[] edt = new byte[9];
    char[] stm1, etm1 = new char[9];
    byte[] stm, etm = new byte[8];
    String LABELX, LABELY;
    String PTITLE;
    String MESSAGE;

    FileOutputStream F1;
    FileOutputStream F3;
    FileOutputStream F5;
    FileOutputStream F6;
    FileOutputStream F7;
    FileOutputStream F8;
    FileOutputStream F9;
    FileOutputStream F10;

    byte buffer[] = new byte[80];
    String error = null;

    int SLICE, SLICE1, SLICE2, IFDELAY, IF2D, IFNONLIN;
    int LTEMP, IERR, HISTNUM, qbins;
    int NUMX, NUMY, NUMW, smonsum, smonpot;
    int pulses, nleft, nright;
    int OUTUNIT, NDIV, ncenter;
    int MICRO, NHS, NCH1D, NCHTOT;
    int Nx, Ny;
    int NDIVx, NDIVy;
    DataSet RelSamp;
  
    boolean IBIN;
    float CHANWID;

    double MINQ, MAXQ, QMIN, QMAX;
    double[] QMINQ = new double[5];
    double minqx, maxqx, MINQy, MAXQy;
    float Qxmin, Qxmax;
    double[] DIV, QMAXQ = new double[5];
    double  LLOW, LHIGH, LAMAVG, LAMMAX;
    double IOFQMAX, IOFQMIN;
    double[] DELQ = new double[5];
    double THMAX;
    double SINTHMAX;
    double SINxMAX;
    double SINxMin;
    double SINyMAX;
    double SINyMin;
    float THICK;

    float L1, L2, CLK;
    double XDIM, YDIM, Qres;
    int DIVx, DIVy;
    float Qymin, Qymax, xDELTAQ, yDELTAQ;
    int INET = 0;
    int NEDGE = 1;

    /**
     * Arrays
     */
    int[] ITEMP = new int[3];
    int[] NSHIFT = new int[5];
    int[] IFOMIT = new int[MAXCST + MAXCST];
    int[] IFGOOD = new int[MAXSLC + MAXSLC];
    //int[] IFERR=new int[MAXCRV+MAXCRV];

    double[] DETSEN, DETERR = new double[MAXSLC + MAXSLC];
    double[] AREATOM1, AREATOM1ERR = new double[MAXCST + 1];
    double[] QVECTOR, IOFQ, ERROR = new double[MAXQBIN + 1];

    double[] siofq, serror = new double[MAXQBIN + 1];
    double[] biofq, berror = new double[MAXQBIN + 1];

    float[] LAMBDA = new float[MAXCST + 1];

    double[] TRANS, TRANB, TSERR, TBERR = new double[MAXCST + 1];
    double[] XX = new double[MAXX + 1];
    double[] YY = new double[MAXY + 1];
    double[] QUE1, QUE2, QUE3, QUE4 = new double[MAXQBIN + 1];
    double[] QUEx1, QUEx2 = new double[maxqxbin + 1];
    double[] QUEx3, QUEx4 = new double[maxqxbin + 1];
    double[] QUEY1, QUEY2 = new double[maxqybin + 1];
    double[] QUEY3, QUEY4 = new double[maxqybin + 1];
    float[] SAMPM1, BACKM1, CADM1 = new float[MAXCST + 1];
    double[] SAMPDN, BACKDN, CADDN, SAMPLE = new double[MAXSLC + 1];
    double[] SINRAD = new double[MAXSLC + 1];
    double[][] sinx = new double[MAXX + 1][MAXY + 1];
    double[][] siny = new double[MAXX + 1][MAXY + 1];

    double[] SAMPINT = new double[MAXSLC + MAXSLC];
    double[] WEIGHT = new double[MAXSLC + MAXSLC];;
    double[] BACKINT = new double[MAXSLC + MAXSLC];
    double[] BACKVAR = new double[MAXSLC + MAXSLC];
    double[] SAMPVAR = new double[MAXSLC + MAXSLC];
    float[][] WTQXQY = new float[maxqxbin + 1][maxqxbin + 1];
    double[] Qxx = new double[maxqxbin + 1];
    float[][] SQXQY = new float[maxqxbin + 1][maxqxbin + 1];
    float[] Qyy = new float[maxqxbin + 1];
    float[][] SERRXY = new float[maxqxbin + 1][maxqxbin + 1];
    float[][] BQXQY = new float[maxqxbin + 1][maxqxbin + 1];
    float[][] BERRXY = new float[maxqxbin + 1][maxqxbin + 1];

    float[][] SBQXQY = new float[maxqxbin + 1][maxqxbin + 1];
    float[][] SBERRXY = new float[maxqxbin + 1][maxqxbin + 1];

    double[] WEIGHT1 = new double[MAXQBIN + 1];
    double[] SOFQS = new double[MAXQBIN + 1];
    double[] SOFQB = new double[MAXQBIN + 1];
    double[] SOQSMB = new double[MAXQBIN + 1];
    double[] SMPERR = new double[MAXQBIN + 1];
    double[] BKGERR = new double[MAXQBIN + 1];
    double[] SMBERR = new double[MAXQBIN + 1];

    String NAMCOL = "SN.DAT";
    String NAMLIS = "SMB.DAT";
    String NAMLIS2 = "S.DAT";
    String NAMLIS3 = "B.DAT";
    String namsq = "S2D.dat";
    String namsq1 = "B2D.dat";
    String namsq2 = "SN2D.DAT";
  
    double SOURCEFREQ = 30.0;
    //float BETADN = 0.0042f;

    double THMIN;//= 0.5*Math.atan(0.01*RADMIN/L2);
    double SINTHMIN;//= Math.sin(THMIN);

    String TITLE = "      -     -     -(2)";
    double Q0;// = 4.*PI/LAMAVG;

    double QMINS;//= Q0*SINTHMIN;
    double QMAXS;// = Q0*SINTHMAX;

    /**
     * INITIALIZE SOME COUNTERS FOR COUNTING THE EVENTS:
     C	TOTAL, NET
     */


    double CNTTOT = 0.0;
    double CNTNET = 0.0;
    double CNT1 = 0.0;
    double CNT2 = 0.0;
    double CNT3 = 0.0;
    double CNT4 = 0.0;
    double PI = 3.14159265;
    double TWOPI = 2.0 * PI;
    double ZERO = 0.0;

    float SCALE;
 
    int RUNC, RUNB, RUNS;
    float XOFF, YOFF;

    /**
    *    Constructor for Reduce_KCL.
    *    @param TransS   The sample Transmission data set
    *    @param TransB   The background Transmission data set
    *    @param  Eff     The Efficiency data set
    *    @param Sens     The sensitivity data set
    *    @param qu       The q bins if 1d or qxmin,qxmax, qymin, qymax
    *    @param RUNSds   the monitor([0]) and Histogram([1]) for the sample
    *    @param RUNBds  the monitor([0]) and Histogram([1]) for the Background
    *    @param RUCBds  null or the monitor([0]) and Histogram([1]) for the 
    *                     Cadmium run
    *    @param BETADN  The delayed neutron fraction
    *    @param SCALE   The scale factor to be applied to all data
    *    @param  THICK  The sample thickness in m
    *    @param  XOFF    The Xoffset of beam from the center in meters
    *    @param  YOFF    The Yoffset of beam from center in meters
    *    @param NQxBins  The number of Qx bins if 2D,otherwise use a neg number
    *    @param NQyBins  The number of Qx bins if 2D,otherwise use a neg number 
   */
 

    public Reduce_KCL(DataSet TransS, DataSet TransB, DataSet Eff, DataSet Sens,
        float[] qu, DataSet[] RUNSds, DataSet[] RUNBds, 
        DataSet[] RUNCds, float BETADN, float SCALE, float THICK,
        float XOFF, float YOFF, int NQxBins, int NQyBins) {
        Object Result;

        this.SCALE = SCALE;
        this.XOFF = XOFF;
        this.YOFF = YOFF;
        REDUCEOUT = "S.OUT";
        DIVx = NQxBins;
        DIVy = NQyBins;
        if ((NQxBins < 1) || (NQyBins < 1))
            IF2D = 0;
        else
            IF2D = 1;
        MaxSlice = RUNSds[1].getData_entry(0).getX_scale().getXs().length;
        this.TransS = TransS;
        this.TransB = TransB;
        this.Eff = Eff;
        this.Sens = Sens;
        this.RUNSds = RUNSds;
        this.RUNBds = RUNBds;
        this.RUNCds = RUNCds;
        this.qu = qu;
        RUNS = (((IntListAttribute) (RUNSds[0].getAttribute(Attribute.RUN_NUM))).getIntegerValue())[0];

        RUNB = (((IntListAttribute) (RUNBds[0].getAttribute(Attribute.RUN_NUM))).getIntegerValue())[0];
        if( RUNCds != null)
          RUNC = (((IntListAttribute) (RUNCds[0].getAttribute(Attribute.RUN_NUM))).getIntegerValue())[0];
        else RUNC = -1;
        ITEMP[1] = LTEMP;
        sdt[1] = sdt[1];
        edt[1] = edt[1];
        tttt[1] = tttt[1];
        users[1] = users[1];

        LABELX = "Q    (A**-1)";
        LABELY = "I(Q)   (CM**-1)";
        int NCHRX = 12;
        int NCHRY = 15;
        int NCHRM = 0;
        int ICURVE = -1;
        int IFERR = 1;
        BufferedReader NCHR;

        /***
         * INITIALIZE
         */


        int IUI = 5;

        OUTUNIT = 9;

        HISTNUM = 1;

        IFDELAY = 1;
        ANSDN = "Y";

        double PLANK = 6.626176E-27;
        double CMASS = 1.67495E-24;
        double PL = PLANK / CMASS;
        double TOTTIM = 1.0E6 / SOURCEFREQ;

        /**
         * INITIALIZE ARRAYS
         */
        for (int k = 1; k <= MAXSLC; k++) {  
            WEIGHT[k] = 0.0f;

            SAMPINT[k] = 0.0f;
            SAMPVAR[k] = 0.0f;

            BACKINT[k] = 0.0f;
            BACKVAR[k] = 0.0f;

        }

        for (int i = 1; i <= maxqxbin; i++) {
            for (int j = 1; j <= maxqybin; j++) {

                WTQXQY[i][j] = 0.0f;

                SQXQY[i][j] = 0.0f;
                SERRXY[i][j] = 0.0f;

                BQXQY[i][j] = 0.0f;
                BERRXY[i][j] = 0.0f;
                SBQXQY[i][j] = 0.0f;
                SBERRXY[i][j] = 0.0f;

            }
        }
        for (int i = 1; i <= MAXQBIN; i++) {
            WEIGHT1[i] = 0.0;

            SOFQS[i] = 0.0;
            SOFQB[i] = 0.0;
            SOQSMB[i] = 0.0;

            SMPERR[i] = 0.0;
            BKGERR[i] = 0.0;
            SMBERR[i] = 0.0;

        }

        /**
         * OPEN .OUT FILE
         */

        try {
            F1 = new FileOutputStream(REDUCEOUT);
        } catch (Exception ex) {
            System.out.println("error");
        }
 
        if (BETADN > 0) 
            IFDELAY = 1;
        IFNONLIN = 0;

        /**
         * TYPE SOME PARAMETERS
         */

        System.out.println("CERTAIN STARTING PARAMETERS ARE THE FOLLOWING");
        System.out.flush();
        System.out.println();
        System.out.flush();
        System.out.println("HISTOGRAM NUMBER IS =" + String.valueOf(HISTNUM));
        System.out.println("BEAM STOP RADIUS IN CM  =" + String.valueOf(Radmin));
        System.out.println(" DELAYED NEUTRON CORRECTION IS MADE =");
        System.out.println("THE DELAYED NEUTRON FRACTION =" + String.valueOf(BETADN));
        System.out.println(" MGO FILTER IS IN THE BEAM ");
        System.out.println("Number of X and Y edge Chans masked for AD=" + String.valueOf(NEDGE));
        // System.out.flush();

        PixelInfoListAttribute pAttr = (PixelInfoListAttribute) (
                RUNSds[1].getData_entry(0).getAttribute(Attribute.PIXEL_INFO_LIST));
        PixelInfoList pilist = (PixelInfoList) (pAttr.getValue());
        IPixelInfo ipInfo = pilist.pixel(0);
        IDataGrid grid = ipInfo.DataGrid();
        
        NUMX = grid.num_rows();
        NUMY = grid.num_cols();
        FloatAttribute At = (FloatAttribute) (RUNSds[1].getData_entry(0).getAttribute(Attribute.INITIAL_PATH));
        float L1 = At.getFloatValue();

        L2 = grid.position().length();
        int NSLICE = NUMX * NUMY;

        double TOFDIST = L1 + L2;

        LAMMAX = (PL / TOFDIST) * TOTTIM;

        THMIN = 0.5 * Math.atan(0.01 * Radmin / L2);
        SINTHMIN = Math.sin(THMIN);

        XDIM = grid.width();
        YDIM = grid.height();
        double SFX = XDIM / NUMX;
        double SFY = YDIM / NUMY;
        
        tof_data_calc.SubtractDelayedNeutrons((TabulatedData) RUNSds[0].getData_entry(0),
            30f, BETADN);
        tof_data_calc.SubtractDelayedNeutrons((TabulatedData) RUNSds[1].getData_entry(1),
            30f, BETADN);
        tof_data_calc.SubtractDelayedNeutrons((TabulatedData) RUNBds[0].getData_entry(0),
            30f, BETADN);
        tof_data_calc.SubtractDelayedNeutrons((TabulatedData) RUNBds[1].getData_entry(1),
            30f, BETADN);
        tof_data_calc.SubtractDelayedNeutrons((TabulatedData) RUNCds[0].getData_entry(0),
            30f, BETADN);
        tof_data_calc.SubtractDelayedNeutrons((TabulatedData) RUNCds[1].getData_entry(1),
            30f, BETADN);
        RUNSds[1] = convertToLambda(RUNSds[1]);
        RUNSds[0] = convertToLambda(RUNSds[0]);
   
        RUNBds[1] = convertToLambda(RUNBds[1]);
        RUNBds[0] = convertToLambda(RUNBds[0]);
   
        RUNCds[1] = convertToLambda(RUNCds[1]);
        RUNCds[0] = convertToLambda(RUNCds[0]);
        
        xscl = RUNSds[1].getData_entry(NUMX * NUMY / 2).getX_scale();
         
        RUNSds[0].getData_entry(0).resample(xscl, IData.SMOOTH_NONE);
 
        RUNBds[0].getData_entry(0).resample(xscl, IData.SMOOTH_NONE);
        if (RUNCds[0] != null)
            RUNCds[0].getData_entry(0).resample(xscl, IData.SMOOTH_NONE);
        RUNSds[0].getData_entry(2).resample(xscl, IData.SMOOTH_NONE);
        RUNBds[0].getData_entry(2).resample(xscl, IData.SMOOTH_NONE);
        if (RUNCds[0] != null)
            RUNCds[0].getData_entry(2).resample(xscl, IData.SMOOTH_NONE);
        float[] yy = RUNSds[0].getData_entry(0).getY_values();

        yy = RUNCds[0].getData_entry(0).getY_values();

        Resample(RUNSds[1], xscl);
        Resample(RUNBds[1], xscl);
        Resample(RUNCds[1], xscl);
        
        LAMBDA = xscl.getXs();
  
        this.SCALE = this.SCALE / THICK;

        /**
         * SET UP SIN(RADII), sinx(x,y), siny(x,y) AND MASK OUT BEAM STOP
         AND BEYOND ANY SPECIFIED RADIUS.  Set up the azimuthal angle array.
         */
        for (int J = 1; J <= NUMY; J++) {
            for (int i = 1; i <= NUMX; i++) {
                int K = i + (J - 1) * NUMX ;
                PixelInfoListAttribute pilistAt = (PixelInfoListAttribute) 
                     (RUNSds[1].getData_entry(K - 1).
                         getAttribute(Attribute.PIXEL_INFO_LIST));
                IPixelInfo ipinf = ((PixelInfoList) pilistAt.getValue()).pixel(0);
                int row = (int) ipinf.row();
                int col = (int) ipinf.col();
                Vector3D dis = new Vector3D(grid.position((float) row, (float) col));
                Vector3D di = new Vector3D(grid.position());
                dis.subtract(di);

                XX[row] = dis.dot(ipinf.x_vec());
                YY[col] = dis.dot(ipinf.y_vec()); 
                //RAD = SQRT(XX(I)**2 + YY(J)**2)
                double RAD = Math.sqrt((XX[row] * XX[row]) + (YY[col] * YY[col]));

                SINRAD[K] = (double) Math.sin(0.5 * Math.atan(0.01 * RAD / L2));
                sinx[row][col] = (double) Math.sin(0.5 * Math.atan(0.01 * 
                                    XX[row] / L2));
                siny[row][col] = (double) Math.sin(0.5 * Math.atan(0.01 * 
                                   YY[col] / L2));

            }

        }

        /**
         * CONSTRUCT THE TITLE
         *
         */
        try {
            str1 = TITLE;
            for (int ii1 = 0; ii1 < str1.length(); ii1++)
                F1.write(str1.charAt(ii1));

            str1 = " ";
            for (int ii1 = 0; ii1 < str1.length(); ii1++)
                F1.write(str1.charAt(ii1));

            str1 = String.valueOf(RUNS);
            for (int ii1 = 0; ii1 < str1.length(); ii1++)
                F1.write(str1.charAt(ii1));

            str1 = TITLE;
            for (int ii1 = 0; ii1 < str1.length(); ii1++)
                F1.write(str1.charAt(ii1));

            str1 = " ";
            for (int ii1 = 0; ii1 < str1.length(); ii1++)
                F1.write(str1.charAt(ii1));

            str1 = String.valueOf(RUNB);
            for (int ii1 = 0; ii1 < str1.length(); ii1++)
                F1.write(str1.charAt(ii1));

            if (RUNC != 0) {
                str1 = TITLE;
                for (int ii1 = 0; ii1 < str1.length(); ii1++)
                    F1.write(str1.charAt(ii1));
                str1 = " ";
                for (int ii1 = 0; ii1 < str1.length(); ii1++)
                    F1.write(str1.charAt(ii1));

                str1 = String.valueOf(RUNC);
                for (int ii1 = 0; ii1 < str1.length(); ii1++)
                    F1.write(str1.charAt(ii1));

            }

        } catch (IOException e) {
        }

    }

    /* ---------------------------- getResult ------------------------------- */
    
    /**  Executes the operator using the parameters that were set up
     *@return  "Success" if there were no errors otherwise  the ErrorString
     *             "No Data Set Selected" is returned.<P>
     *
     *NOTE: A SelectedGraph View will also pop up
     */
    public Object getResult() { 

        //----------------- Divide by Monitor ------------------------
        Object Res = (new DataSetDivide_1(RUNSds[1], RUNSds[0], 1, true)).getResult();

        if (Res instanceof ErrorString)
            return new ErrorString("AA1:" + Res.toString());
        if (Res instanceof String)
            return new ErrorString("AB" + (String) Res);
        RelSamp = (DataSet) Res;

        RelSamp.setTitle("Samp to MOnitor");

        Res = (new DataSetDivide_1(RUNCds[1], RUNCds[0], 1, true)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("AB:" + Res.toString());;
        if (Res instanceof String)
            return new ErrorString("AC:" + (String) Res);
        DataSet RelCadmium = (DataSet) Res;

        Res = (new DataSetDivide_1(RUNBds[1], RUNBds[0], 1, true)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("AD:" + Res.toString());;
        if (Res instanceof String)
            return new ErrorString("AE:" + (String) Res);
        DataSet RelBackground = (DataSet) Res;

        //---------------- Subtract out Cadmium Run-----------------
        Res = (new DataSetSubtract(RelSamp, RelCadmium, true)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("AF:" + Res.toString());;
        if (Res instanceof String)
            return new ErrorString("AG:" + (String) Res);
  

        RelSamp = (DataSet) Res;
        RelSamp.setTitle("Sample rel Monitor - CAdmium");
        Res = (new DataSetSubtract(RelBackground, RelCadmium, true)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("AH:" + Res.toString());;
        if (Res instanceof String)
            return new ErrorString("AI:" + (String) Res);
        RelBackground = (DataSet) Res;

        //----------------- Divide by Transmission ----------------------
        TransS.setX_units(RelSamp.getX_units());
        Res = (new DataSetDivide_1(RelSamp, TransS, TransS.getData_entry(0).getGroup_ID(), true)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("AJ:" + Res.toString());;
        if (Res instanceof String)
            return new ErrorString("AK:" + (String) Res);
        RelSamp = (DataSet) Res;
        RelSamp.setTitle("Adj Samp div by Transm");

        
        TransB.setX_units(RelBackground.getX_units());
        Res = (new DataSetDivide_1(RelBackground, TransB, TransB.getData_entry(0).getGroup_ID(), true)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("AL:" + Res.toString());;
        if (Res instanceof String)
            return new ErrorString("AM:" + (String) Res);
        RelBackground = (DataSet) Res;

        // float[] eff = Eff.getData_entry(0).getY_values();
        //  Divide Sample by sens and efficiency
        DataSet weight = makeNewSensEffDS(Sens, Eff, xscl, RelSamp, XOFF, YOFF);

        //ScriptUtil.display(weight.clone() );
        RelSamp.setTitle("XXXXXXXXXXXXXXXx");
        //ScriptUtil.display( RelSamp.clone());

        //-------------------- Divide by Sens and Eff -------------
        Res = (new DataSetDivide(RelSamp, weight, true)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("AL:" + Res.toString());;
        if (Res instanceof String)
            return new ErrorString("AM:" + (String) Res);
        RelSamp = (DataSet) Res;

        Res = (new DataSetDivide(RelBackground, weight, true)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("AL:" + Res.toString());;
        if (Res instanceof String)
            return new ErrorString("AM:" + (String) Res);
        RelBackground = (DataSet) Res;

        //ScriptUtil.display( RelSamp);
        //ScriptUtil.display(RelBackground);
   
        RelSamp.setTitle("Sample Intensities.corrected -cadmium,eta, trans");
        RelBackground.setTitle("Backgound Intensities.corrected -cadmium,eta, trans");
        //ScriptUtil.display( RelSamp.clone());
        //ScriptUtil.display( RelBackground.clone());
        //----------------------- After MonGet--------------------
        //QUE1 is the "xscale" for 1 to N 
  

        //------------------ Calculate Weight DS -------------------
        (new DataSetMultiply_1(weight, RUNSds[0], 1, false)).getResult();
        
       

        Operator opp = null;

        AdjustGrid(RelSamp, this.XOFF, this.YOFF);
        AdjustGrid(RelBackground, this.XOFF, this.YOFF);
        AdjustGrid(weight, this.XOFF, this.YOFF);

        RelSamp.setTitle("After Adj grid in RelSamp in lambda");
        weight.setTitle("After Adj grid in weight in lambda");

        Res = (new DiffractometerWavelengthToQ(RelSamp, 0f, 100f, 0)).getResult();
        if (Res instanceof ErrorString)
            return Res;
        RelSamp = (DataSet) Res;
    
        Res = (new DiffractometerWavelengthToQ(RelBackground, 0f, 100f, 0)).getResult();
        if (Res instanceof ErrorString)
            return Res;
        RelBackground = (DataSet) Res;

        Res = (new DiffractometerWavelengthToQ(weight, 0f, 100f, 0)).getResult();
        if (Res instanceof ErrorString)
            return Res;
        weight = (DataSet) Res;

        try {
            ScriptUtil.save("RelSampAdjQ.isd", RelSamp);
            ScriptUtil.save("weightAdjQ.isd", weight);
        } catch (Exception s9) {
        }
        RelSamp.setTitle("RelSamp Q0");
        // ScriptUtil.display( RelSamp.clone());
    
      
        //ScriptUtil.display(RelBackground.clone());
        //ScriptUtil.display( weight.clone());
        //if( 3==3) return null;
   
        Res = (new DataSetMultiply(RelSamp, weight, false)).getResult();
        Res = (new DataSetScalarMultiply(RelSamp, SCALE, false)).getResult();

        try {
            ScriptUtil.save("RelSamp_WeightSCALE.isd", RelSamp);
        } catch (Exception sss) {
        }
        if (Res instanceof ErrorString)
            return new ErrorString("AA:" + Res.toString());
       
        Res = (new DataSetMultiply(RelBackground, weight, false)).getResult();

        Res = (new DataSetScalarMultiply(RelBackground, SCALE, false)).getResult();
        if (Res instanceof ErrorString)
            return new ErrorString("BB:" + Res.toString());
       
        RelSamp.setTitle("Weighted Resamp Q for Sample");
        //ScriptUtil.display( RelSamp.clone());
        // if( 3==3) return null;
        //EliminateBadDetectors(RelSamp, Sens);
        ////EliminateBadDetectors(RelBackground, Sens);
        EliminateBadDetectors(weight, Sens);

        Res = (new DataSetSubtract(RelSamp, RelBackground, true)).getResult();
        if (Res instanceof ErrorString)
            return Res;
        DataSet RelDiff = (DataSet) Res;
         
        if (IF2D != 1) {
             
	    xscl = new VariableXScale(qu);
            Resample(RelSamp, xscl);
            Resample(RelBackground, xscl);
            Resample(weight, xscl);
      
            //  EliminateBadDetectors( RelSamp, Sens);
            // EliminateBadDetectors( RelBackground, Sens);
            //EliminateBadDetectors( weight, Sens);
            Res = (new DataSetSubtract(RelSamp, RelBackground, true)).getResult();
            if (Res instanceof ErrorString)
                return Res;
            RelDiff = (DataSet) Res;
            RelSamp.setTitle("RelSamp Before summing");
            weight.setTitle("weight Before Summing");
            ScriptUtil.display(RelSamp);
            ScriptUtil.display(weight);
            DataSet SSampQ = SumAllDetectors(RelSamp);
            DataSet SBackQ = SumAllDetectors(RelBackground);
            DataSet SDifQ = SumAllDetectors(RelDiff);
            DataSet Sweight = SumAllDetectors(weight);
            SSampQ.setTitle("SampSumQ");
            //ScriptUtil.display( SSampQ.clone() );
            Res = (new DataSetDivide(SSampQ, Sweight, false)).getResult();
            if (Res instanceof ErrorString)
                return Res;      
            Res = (new DataSetDivide(SBackQ, Sweight, false)).getResult();
            if (Res instanceof ErrorString)
                return Res;      
            Res = (new DataSetDivide(SDifQ, Sweight, false)).getResult();
            if (Res instanceof ErrorString)
                return Res;      
    
            SSampQ.setTitle("Neutron Corrected Sample-" + StringUtil.toString(SSampQ.getData_entry(0).getAttributeValue(Attribute.RUN_NUM)));

            SBackQ.setTitle("Neutron Corrected Background-" + StringUtil.toString(SBackQ.getData_entry(0).getAttributeValue(Attribute.RUN_NUM)));

            SDifQ.setTitle("Neutron Corrected Sample-Background-" + StringUtil.toString(SDifQ.getData_entry(0).getAttributeValue(Attribute.RUN_NUM)));
  
            Vector V = new Vector();
    
            int[] RunNums;
            if( RUNC >0)
              RunNums = new int[3];
            else
              RunNums = new int[2];
            RunNums[0] = RUNS;
            RunNums[1] = RUNB;
            if( RUNC > 0)
             RunNums[2] = RUNC;
            SSampQ.setAttribute( new IntListAttribute( Attribute.RUN_NUM,RunNums));
            SSampQ.getData_entry(0).setAttribute( new IntListAttribute( Attribute.RUN_NUM,RunNums));
            SBackQ.setAttribute( new IntListAttribute( Attribute.RUN_NUM,RunNums));
            SBackQ.getData_entry(0).setAttribute( new IntListAttribute( Attribute.RUN_NUM,RunNums));
            SDifQ.setAttribute( new IntListAttribute( Attribute.RUN_NUM,RunNums));
            SDifQ.getData_entry(0).setAttribute( new IntListAttribute( Attribute.RUN_NUM,RunNums));
            V.addElement(SSampQ);
            V.addElement(SBackQ);
            V.addElement(SDifQ);
            return V;
        }
        LLOW = .5*(LAMBDA[0]+  //Change to subrange of times
               LAMBDA[1]);
        PixelInfoList pilistx = ((PixelInfoList)RelSamp.getData_entry(0).
              getAttributeValue( Attribute.PIXEL_INFO_LIST));
        IPixelInfo ipinfx =pilistx.pixel(0);
        IDataGrid SampGrid=ipinfx.DataGrid();
        SampGrid.clearData_entries();
        SampGrid.setData_entries(RelSamp);
        float[] mins = SampGrid.position(1f,1f).get();
        float[] maxs = SampGrid.position( (float) NUMX, (float) NUMY).get();

        double sinxMax = java.lang.Math.sin(.5*java.lang.Math.atan(-maxs[1]/L2));
        double sinyMax = java.lang.Math.sin(.5*java.lang.Math.atan(maxs[2]/L2));
        double sinxMin = java.lang.Math.sin(.5*java.lang.Math.atan(-mins[1]/L2));
        double sinyMin = java.lang.Math.sin(.5*java.lang.Math.atan(mins[2]/L2));
        Qxmin = (float)(4*java.lang.Math.PI*sinxMin/LLOW);
        Qymin = (float)(4*java.lang.Math.PI*sinyMin/LLOW);
        Qxmax = (float)(4*java.lang.Math.PI*sinxMax/LLOW);
        Qymax = (float)(4*java.lang.Math.PI*sinyMax/LLOW);
	if( qu != null)
	  if( qu.length >=4){
	     Qxmin =(float) java.lang.Math.max(Qxmin, qu[0]);
	     Qxmax = (float)java.lang.Math.min( Qxmax, qu[1]);
	     Qymin = (float)java.lang.Math.max(Qymin, qu[2]);
	     Qymax = (float)java.lang.Math.min( Qymax, qu[3]);
	  }
        xDELTAQ = ((Qxmax - Qxmin)/DIVx);
        yDELTAQ =((Qymax - Qymin)/DIVy);

       
        IDataGrid BackGrid= ((PixelInfoList)(RelBackground.getData_entry(0).
               getAttributeValue(Attribute.PIXEL_INFO_LIST))).pixel(0).DataGrid();
        
        BackGrid.clearData_entries();
        BackGrid.setData_entries(RelBackground);
        IDataGrid weightGrid= ((PixelInfoList)(weight.getData_entry(0).
               getAttributeValue(Attribute.PIXEL_INFO_LIST))).pixel(0).DataGrid();
        weightGrid.clearData_entries();
        weightGrid.setData_entries(weight);
	int nn =0;
	for( int i = 0; i< RelSamp.getNum_entries(); i++){
	   Data Dsamp =RelSamp.getData_entry(i);
	   IPixelInfo ipx = ((PixelInfoList)(RelSamp.getData_entry(0).
	          getAttributeValue(Attribute.PIXEL_INFO_LIST))).pixel(0);
	   int row = (int)(ipx.row());
	   int col = (int)(ipx.col());
	   Data Dback = RelBackground.getData_entry(i);
                           //BackGrid.getData_entry( row,col);
	   Data Dweight =weight.getData_entry(i); 
                       //weightGrid.getData_entry( row,col);
	   DetectorPosition detPos = ((DetectorPosition)Dsamp.getAttributeValue
	                          ( Attribute.DETECTOR_POS));
	   float[]Qxy = tof_calc.DiffractometerVecQ( detPos,L1,1000f).
	            getCartesianCoords();
	   float Len = (float)(java.lang.Math.sqrt( Qxy[0]*Qxy[0]+
	      Qxy[1]*Qxy[1]+Qxy[2]*Qxy[2]));
	   Qxy[0] = Qxy[0]/Len; Qxy[1] = Qxy[1]/Len;Qxy[2] = Qxy[2]/Len;
	   float[] SampYvals = Dsamp.getY_values();
	   float[] BackYvals = Dback.getY_values();
	   float[] weightYvals = Dweight.getY_values();
	   
	   float[] SampErrs = Dsamp.getErrors();
	   float[] BackErrs = Dback.getErrors();
	   float[] weightErrs = Dweight.getErrors();
	   float[] qvals = Dsamp.getX_scale().getXs();
	   for( int q =0; q+1 < qvals.length;q++){
	      int k=q;
	      float Q = .5f*(qvals[q]+qvals[q+1]);
	      float Qx = -Q*Qxy[1]; 
	      float Qy = Q * Qxy[2];
             
              float DNx = ((Qx -Qxmin)/xDELTAQ);
	      float DNy = ((Qy -Qymin)/xDELTAQ);
              int Nx=-1, Ny=-1;
              Nx = (int)java.lang.Math.floor(DNx);
              Ny = (int) java.lang.Math.floor(DNy);
	      if( Nx >=0)if(Ny>=0)if(Qx <Qxmax)if(Qy<Qymax){
                WTQXQY[Nx][Ny] = WTQXQY[Nx][Ny] + weightYvals[k];
                   
                
                   
                SQXQY[Nx][Ny]=SQXQY[Nx][Ny]+SampYvals[k];
                BQXQY[Nx][Ny]=BQXQY[Nx][Ny]+BackYvals[k];
                SBQXQY[Nx][Ny] = SQXQY[Nx][Ny]-BQXQY[Nx][Ny];
 
                SERRXY[Nx][Ny]=SERRXY[Nx][Ny]+
                                  (float)Math.pow(SampErrs[k],2);
                BERRXY[Nx][Ny]=BERRXY[Nx][Ny]+(
                                 float)Math.pow(BackErrs[k],2);
                SBERRXY[Nx][Ny]=BERRXY[Nx][Ny]+SERRXY[Nx][Ny];
	      
	      }
	      else{
	        //System.out.println("out of bounds"+Qxmin+","+Q+","+Qxmax+"::"+
		         //  Qymin+","+Qy+","+Qymax);
	      }
	   }
	   
	  }//for( int i = 0; i< RelSamp.getNum_entries(); i++)
		  
        
	

        for( int i = 0; i < DIVx; i++)
	  for( int j = 0; j < DIVy; j++){
	    if(WTQXQY[i][j] == 0){
	      SQXQY[i][j] = 0f;
	      BQXQY[i][j] =0f;
	      SBQXQY[i][j] =0f;
	      SERRXY[i][j] =0f;
	      BERRXY[i][j] =0f;
	      SBERRXY[i][j] = 0f;
	    }else{
	      SQXQY[i][j] = SQXQY[i][j]/WTQXQY[i][j];
	      BQXQY[i][j] =BQXQY[i][j]/WTQXQY[i][j];
	      SBQXQY[i][j] = SBQXQY[i][j]/WTQXQY[i][j];
	      SERRXY[i][j] =(float) java.lang.Math.sqrt(SERRXY[i][j])/WTQXQY[i][j];
	      BERRXY[i][j] =(float) java.lang.Math.sqrt(BERRXY[i][j])/WTQXQY[i][j];
	      SBERRXY[i][j] = (float)java.lang.Math.sqrt(SBERRXY[i][j])/WTQXQY[i][j];
            }
	  }
        Vector  V = new Vector();

	Object O1=show( Qxmin,Qymin,xDELTAQ,yDELTAQ,DIVx,DIVy, SQXQY,SERRXY,"s2d19990");
	Object O2=show( Qxmin,Qymin,xDELTAQ,yDELTAQ,DIVx,DIVy, BQXQY,BERRXY,"b2d19990");
	Object O3=show( Qxmin,Qymin,xDELTAQ,yDELTAQ,DIVx,DIVy, SBQXQY,SBERRXY,"sn2d19990");
	if( O1 instanceof ErrorString)
          return O1;
	if( O2 instanceof ErrorString)
          return O2;
	if( O3 instanceof ErrorString)
          return O3;

        V.addElement( O1); V.addElement(O2); V.addElement( O3);
        return V;
               
    
    }//end of getResult

    private DataSet convertToLambda(DataSet ds) {
        DataSetOperator opBackground;
        Object Result;
  
        opBackground = ds.getOperator("Convert to Wavelength");
        if (opBackground == null)
            opBackground = ds.getOperator("Monitor to Wavelength");
        if (opBackground != null) {
            opBackground.setDefaultParameters();
            opBackground.getParameter(0).setValue(new Float(-1.0f));
            opBackground.getParameter(1).setValue(new Float(-1.0f));
            opBackground.getParameter(2).setValue(new Integer(0));
            Result = opBackground.getResult();
            if ((Result  instanceof ErrorString)) {
                error = ((ErrorString) Result).toString();
                System.out.println("C:" + error);
                return  null;
            }
            if (Result == null) {
                error = ("Could not Convert Sample to Llamda");
                System.out.println("D:" + error);
                return null;
            }
            DataSet ds1 = (DataSet) Result;

            ds1.setTitle(ds.getTitle() + "-lambda and scaled");
            ds1.setX_units("Angstroms");
            return ds1;
        }
        return null;

    }

    private void Resample(DataSet DS, XScale xscl) {
        //System.out.println("in void Resample, xscl="+StringUtil.toString( xscl.getXs()));
        for (int i = 0; i < DS.getNum_entries(); i++)
            DS.getData_entry(i).resample(xscl, IData.SMOOTH_NONE);

    }

    /**
     *    This makes the initial weights data set. If XOFF and YOFF have already
     *    been applied to RelSamp, use 0'f for these
     */
    private DataSet makeNewSensEffDS(DataSet Sens, DataSet Eff, XScale xscl, 
        DataSet RelSamp, float XOFF, float YOFF) {

        DataSet Res = new DataSet("Sens_Eff_Prod", new OperationLog(), RelSamp.getX_units(),
                RelSamp.getX_label(), RelSamp.getY_units(), RelSamp.getY_label());

        //DataSetFactory.addOperators( Res);
        float[] Eff_yvals = Eff.getData_entry(0).getY_values();
        float[] Eff_errors = Eff.getData_entry(0).getErrors();
        PixelInfoList pxinflist = (PixelInfoList) (Sens.getData_entry(0).getAttribute(Attribute.PIXEL_INFO_LIST).getValue());
        IPixelInfo ipinf = pxinflist.pixel(0);
        IDataGrid Sensgrid = ipinf.DataGrid();

        Sensgrid.setData_entries(Sens);
        IPixelInfo ipinf1 = ((PixelInfoList) RelSamp.getData_entry(0).getAttributeValue(Attribute.PIXEL_INFO_LIST)).pixel(0);
        IDataGrid Sampgrid = ipinf.DataGrid();

        Sampgrid.setData_entries(RelSamp);
        UniformGrid SensEffgrid = new UniformGrid(77, Sampgrid.units(), Sampgrid.position(),
                Sampgrid.x_vec(), Sampgrid.y_vec(), Sampgrid.width(), Sampgrid.height(),
                Sampgrid.depth(), Sensgrid.num_rows(), Sensgrid.num_cols());

        for (int i = 0; i < Sens.getNum_entries(); i++) {
    
            float[] yvals = new float[Eff_yvals.length];
            float[] errors = new float[Eff_errors.length];

            System.arraycopy(Eff_yvals, 0, yvals, 0, Eff_yvals.length);
     
            System.arraycopy(Eff_errors, 0, errors, 0, Eff_errors.length);

            PixelInfoList pilist9 = (PixelInfoList) (Sens.getData_entry(i).getAttributeValue( 
                        Attribute.PIXEL_INFO_LIST));
            IPixelInfo pinf9 = pilist9.pixel(0);
            IDataGrid grid = pinf9.DataGrid();
    
            int row = (int) pinf9.row();
            int col = (int) pinf9.col();
    
            float this_sens = Sens.getData_entry(i).getY_values()[0];

            if ((row < Nedge) || (row > grid.num_rows() - Nedge) || (col < Nedge) ||
                (col > grid.num_cols() - Nedge))
                this_sens = 0;

                /* float[] pos = Sampgrid.position( row, col).get();
                 pos[1] -= XOFF;
                 pos[2] += YOFF;
                 */
            float[] pos = ((DetectorPosition) (Sampgrid.getData_entry(row, col).getAttributeValue( 
                            Attribute.DETECTOR_POS))).getCartesianCoords();
    
            pos[1] += XOFF;
            pos[2] -= YOFF;

            float rad = pos[1] * pos[1] + pos[2] * pos[2];

            if ((rad < Radmin * Radmin) || (rad > Radmax * Radmax))
                this_sens = 0;
 
            for (int j = 0; j < Eff_yvals.length; j++) {
                yvals[j] = yvals[j] * this_sens;
                errors[j] = errors[j] * this_sens;
            }
            int GroupID = Sampgrid.getData_entry(row, col).getGroup_ID();
            HistogramTable D = new HistogramTable(xscl, yvals, errors, GroupID);
            DetectorPixelInfo dpi = new DetectorPixelInfo(GroupID, (short) row, (short) col, SensEffgrid);
            DetectorPixelInfo[] pilist = new DetectorPixelInfo[1];

            pilist[0] = dpi;
            D.setAttribute(new PixelInfoListAttribute(Attribute.PIXEL_INFO_LIST, 
                    new PixelInfoList(pilist)));
            Data Dsamp = RelSamp.getData_entry_with_id(GroupID);

            D.setAttribute(Dsamp.getAttribute(Attribute.DETECTOR_POS));
            Res.addData_entry(D);
  
        }
        return Res;

    }//makeNewSensEffDS

    private void EliminateBadDetectors(DataSet ds, DataSet Sens) {
        Object Res = (new ClearSelect(Sens)).getResult();

        if (Res instanceof ErrorString)
            return;
        StringChoiceList sl1 = new StringChoiceList();

        sl1.setString("Between Max and Min");

        StringChoiceList sl2 = new StringChoiceList();

        sl2.setString("Set Selected");
        DataSet Sens1 = (DataSet) (Sens.clone());

        Res = (new SelectGroups(Sens1, new AttributeNameString("TOTAL COUNT"), 0f, .0001f,
                        sl1, sl2)).getResult();

        if (Res instanceof ErrorString)
            return;
 
        int[] selInd = Sens1.getSelectedIndices();

        for (int i = 0; i < selInd.length; i++)
            ds.setSelectFlag(selInd[i], true);
        (new ExtractCurrentlySelected(ds, false, false)).getResult();
  
    }//EliminateBadDetectors

    private DataSet SumAllDetectors(DataSet ds) {
  
        Object Res = (new SumByAttribute(ds, "Group ID", true, 0f, 20f + ds.getNum_entries())).getResult();

        if (Res instanceof ErrorString)
            return null;
        ds.clearSelections();
        System.out.println("End sunAll Det Res class=" + Res.getClass() + "," + Res.toString());
        return (DataSet) Res;

    }

    public void AdjustGrid1(DataSet ds, float xoff, float yoff) {
        for (int i = 0; i < ds.getNum_entries(); i++) {
            Data D = ds.getData_entry(i);
            float[] Coords = ((DetectorPosition)
                    D.getAttributeValue(Attribute.DETECTOR_POS)).getCartesianCoords();

            Coords[1] += xoff;
            Coords[2] -= yoff;

            DetectorPosition pos = new DetectorPosition( 
                    new Vector3D(Coords));

            D.setAttribute(new DetPosAttribute(Attribute.DETECTOR_POS,
                    pos));

        }
    }

    public void AdjustGrid(DataSet ds, float xoff, float yoff) { 

        int ids[] = Grid_util.getAreaGridIDs(ds);

        if (ids.length != 1)
            System.out.println("ERROR: wrong number of data grids " + ids.length);
        IDataGrid grid = Grid_util.getAreaGrid(ds, ids[0]);

        grid.setData_entries(ds);
        Vector3D pos = grid.position();

        pos.add(new Vector3D(0, xoff, -yoff));
        ((UniformGrid) grid).setCenter(pos);
    
        Grid_util.setEffectivePositions(ds, grid.ID());
    }

  private int hasDataGrid( DataSet ds){
     Object pilist = ds.getData_entry(0).getAttributeValue( Attribute.PIXEL_INFO_LIST);
     if( pilist == null) return 1;
     IPixelInfo pinf =((PixelInfoList)pilist).pixel(0);
     if( pinf == null) return 2;
     if( pinf.DataGrid() == null) return 3;
     return 0;


  }
public  Object show( float Qxmin,float Qymin,float Dx, float Dy, int Nx, int Ny,
    float[][] list, float[][] err, String DataSetName){
    DataSet DS = new DataSet(DataSetName,new OperationLog(),"per Angstrom",
          "","Rel Counts", "Rel Counts");
    DataSetFactory.addOperators( DS);
    UniformGrid grid = new UniformGrid(47,"per Angstrom",new Vector3D(0f,0f,0f),
            new Vector3D(0f,Dy,0f), new Vector3D(0f,0f,Dx), Dx*Nx,Dy*Ny, 0.0001f,Ny,Nx);

    UniformXScale xscl = new UniformXScale( 0,1,2);
    int[] RunNums;
    if( RUNC <= 0)
      RunNums = new int[2];
    else 
      RunNums = new int[3];
    RunNums[0]= RUNS;
    RunNums[1] = RUNB;
    if( RUNC >0)
       RunNums[2] = RUNC;
    for(int row = 1; row<= Ny; row++)
      for( int col = 1; col <= Nx; col++){
       float[] yvals = new float[1];
       float[] errs = new float[1];
       yvals[0] = list[row-1][col-1];
       errs[0] = err[row-1][col-1];
       HistogramTable Dat = new HistogramTable( xscl, yvals, errs, (row-1)*Nx+col);

       DetectorPixelInfo dpi = new DetectorPixelInfo((row-1)*Nx+col,(short)row,
                                                     (short)col,grid);
       Dat.setAttribute( new PixelInfoListAttribute(Attribute.PIXEL_INFO_LIST,
               new PixelInfoList( dpi)));


       Dat.setAttribute( new FloatAttribute( Attribute.INITIAL_PATH, 3));
       Dat.setAttribute( new FloatAttribute( Attribute.TOTAL_COUNT, yvals[0]));
       Dat.setAttribute( new IntListAttribute(Attribute.RUN_NUM, RunNums));
       DS.addData_entry(Dat);
    }
    DS.addOperator( new DataSetTools.operator.DataSet.Attribute.GetPixelInfo_op());
    DS.setAttribute( new StringAttribute(Attribute.INST_NAME,"SAND"));

    DS.setAttribute( new IntListAttribute(Attribute.RUN_NUM, RunNums));
    grid.setDataEntriesInAllGrids( DS);
   
    Grid_util.setEffectivePositions( DS, 47);
    return DS;
   
    }
    public static void main(String[] args) {

        IsawGUI.Util util = new IsawGUI.Util();

        DataSet[] RUNSds = null, RUNBds = null, RUNCds = null;
        DataSet[] TransS = null, TransB = null, Eff = null, Sens = null;
        float[] qu = new float[117];
        String Path, Instrument;
        float BETADN, SCALE;

        BETADN = 0.0011f;
        SCALE = 843000f;
        //
        qu[0] = 0.0035f;
        for (int i = 1; i < 117; i++) {
            qu[i] = qu[i - 1] * 1.05f;
            //System.out.println("qu ....." +qu[i]);
        }
 
        RUNSds = util.loadRunfile("C:\\Argonne\\sand\\wrchen03\\sand19990.run");
        RUNBds = util.loadRunfile("C:\\Argonne\\sand\\wrchen03\\sand19935.run");
        RUNCds = util.loadRunfile("C:\\Argonne\\sand\\wrchen03\\sand19936.run");
        try {
            TransS = ScriptUtil.load("C:\\ISAW\\DataSetTools\\operator\\Generic\\TOF_SAD\\tr1999019934.isd");
            TransB = ScriptUtil.load("C:\\ISAW\\DataSetTools\\operator\\Generic\\TOF_SAD\\tr1993519934.isd");
            Eff = ScriptUtil.load("C:\\ISAW\\DataSetTools\\operator\\Generic\\TOF_SAD\\efr19452.isd");
            Sens = ScriptUtil.load("C:\\ISAW\\SampleRuns\\sens19878.isd");
        } catch (Exception sss) {
            System.out.println("Error:" + sss);
        }
        Reduce_KCL Reduce_KCL = new Reduce_KCL(TransS[0], TransB[0], 
                Eff[0], Sens[0],new float[]{-.5f,.5f,-.5f,.5f}, RUNSds, 
		RUNBds, RUNCds, BETADN, SCALE, .1f,
                //     0f,0f);
                .000725f, .006909f, 200, 200);
        Object O = Reduce_KCL.getResult();
//new float[]{-.5f,.5f,-.5f,.5f}
        System.out.println("Finished O=" + O);
        Vector V = (Vector) O;
        ScriptUtil.display(((DataSet)(V.elementAt(0))).getAttributeValue(Attribute.RUN_NUM));
        ScriptUtil.display(V.elementAt(0));
        ScriptUtil.display(V.elementAt(1));
        ScriptUtil.display(V.elementAt(2));

    }
}

