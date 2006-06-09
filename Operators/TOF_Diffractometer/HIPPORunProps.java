/*
 * File:  HIPPORunProps.java
 *
 * Copyright (C) 2006 J. Tao
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
 * Contact : Julian Tao <taoj@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 South Cass Avenue, Bldg 360
 *           Argonne, IL 60439-4845, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 * 
 *
 */

package Operators.TOF_Diffractometer;

import DataSetTools.dataset.Attribute;
import DataSetTools.util.SharedData;
import DataSetTools.dataset.DataSet;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.ReferenceGrid;
import Operators.Generic.Load.LoadUtil;
import DataSetTools.retriever.RunfileRetriever;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Iterator;
//import java.lang.StringBuffer;
import java.util.ArrayList;
import gov.anl.ipns.Util.File.TextFileReader;

/**
 * This class provides the LANSCE HIPPO instrument, experimental
 * and analysis specific information.
 * 
 */
public class HIPPORunProps {

  public static final String HIPPO_PARM = "HIPPO_Detector_Parameters";
  public static final String HIPPO_PROP = "HIPPO_Running_Info";
  public static String ISAWDBDirectory;
  public static String ISAWINSTDirectory;
  public static String HIPPODefInstProps;
  public static String HIPPODetTable;
  public static HashMap defHIPPOProps;

  static final String SYMBOL = ".SYMBOL",
    FORMULA = ".FORMULA",
    DENSITY = ".DENSITY",
    EFFDENSITY = ".EFFDENSITY",
    SIZE = ".SIZE",
    SIGMA_A = ".SIGMA_A",
    PROFILE = ".PROFILE",
    ASTEP = ".ASTEP",
    MSTEP = ".MSTEP",
    ANGLES = ".ANGLES";

  //   find the pathname of default HIPPO property file hippoprops.dat
  //   and detector setup file hippodets.par;
  static {
    ISAWINSTDirectory = SharedData.getProperty("ISAW_HOME");
    if (ISAWINSTDirectory == null) {
      ISAWINSTDirectory = SharedData.getProperty("user.home");
      ISAWINSTDirectory += java.io.File.separator + "ISAW";
    }
    ISAWINSTDirectory = ISAWINSTDirectory.trim();
    ISAWDBDirectory = ISAWINSTDirectory + java.io.File.separator + "Databases";
    ISAWINSTDirectory += java.io.File.separator
      + "InstrumentInfo"
      + java.io.File.separator
      + "LANSCE";
    HIPPODefInstProps =
      ISAWINSTDirectory + java.io.File.separator + "hippoprops.dat";
    HIPPODetTable =
      ISAWINSTDirectory + java.io.File.separator + "hippodets.par";

    if (new File(HIPPODetTable).exists()) {
      // do nothing
    } else {
      HIPPODetTable = null;
      throw new RuntimeException("!!!!!!\nHIPPO detector file not found!!!!!!");
    }

    if (new File(HIPPODefInstProps).exists()) {
      try {
        defHIPPOProps = loadExpProps(HIPPODefInstProps);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    } else {
      HIPPODefInstProps = null;
      throw new RuntimeException("!!!!!!\ndefault HIPPO property file not found!!!!!!\n");
    }

  }

  //storing the info;
  HashMap ExpConfiguration;
    static int[][] BankDet2lpsdID;
    boolean badLPSD[];
    int dataID2gridID[]; //data block ID used as the index of an array;
    int gridID2dataID[][];
    int dataID2index[];
    int deadDet[], removedDataID[];
    float dataID2tofs[][];
  static float[][][] detParamVals;
  static int bids[] = new int[] {10, 20, 40, 90, 150};

  private HIPPORunProps() {
    ExpConfiguration = defHIPPOProps;
  }

  /**
   *  read in the info from a user given file.
  */
  private HIPPORunProps(String fprops) {
    try {
      ExpConfiguration = loadExpProps(fprops);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   *  Set the parameters to default values.
   */
  public HIPPORunPropAttribute getAttribute(String name, Object[] value) {
    return new HIPPORunPropAttribute(name, value);
  }

  //HIPPORunPropAttribute object will be used to ship the info within ISAW;    
  protected class HIPPORunPropAttribute extends Attribute {
    private Object[] value;

    public HIPPORunPropAttribute(String name, Object[] value) {
      super(name);
      this.value = value;
    }

    public Object getValue() {
      return value.clone();
    }

    public String getStringValue() {
      StringBuffer svs = new StringBuffer();
      for (int i = 0; i < value.length; i++)
        svs.append(i + " ");
      return svs.toString();
    }

    public double getNumericValue() {
      return 0.0;
    }

    public String toString() {
      return getName() + "::" + getStringValue();
    }
  }

  public static HIPPORunProps getExpProps() {
    return new HIPPORunProps();
  }

  public static HIPPORunProps getExpProps(String fin) {
    return new HIPPORunProps(fin);
  }

  public static HashMap loadExpProps(String fin)
    throws IOException, InterruptedException {
    Properties props = new Properties();
    FileInputStream in = new FileInputStream(fin);
    props.load(in);
    in.close();
    //    props.list(System.out);

    HashMap config = trExpProps(props);
    if (System.getProperty("Default_Instrument").equals("HIPPO")) {
      System.out.println("\nLoad HIPPO properties from \"" + fin + "\":");
      printExpHashMap(config);
    }

    return config;
  }

  public static HashMap trExpProps(Properties expprop) {
    HashMap expHmap = new HashMap(expprop);
    Iterator ekeys = expHmap.keySet().iterator();

    Float Fvalue;
    Integer Ivalue;
    float fvals[];
    String key, svalue, list[];
    int n;
    Pattern regi = Pattern.compile("^(\\d+)$");
    Pattern regf = Pattern.compile("^(\\d*\\.?\\d*e?\\d*)$");
    Pattern regfvs = Pattern.compile("^\\[(.+)\\]$");
    Pattern regsvs = Pattern.compile("^\\{(.+)\\}$");
    Pattern token = Pattern.compile("[\\s,]+");
    Pattern pxe = Pattern.compile("(.+?)\\.?(\\d*)$");
    Matcher m;

    while (ekeys.hasNext()) {
      key = (String) ekeys.next();
      svalue = ((String) expHmap.get(key)).trim();
      if (!svalue.equals("")) {

        m = regi.matcher(svalue);
        if (m.matches()) {
          svalue = m.group(1);
          Ivalue = new Integer(svalue);
          expHmap.put(key, Ivalue);
          //          System.out.println("key: "+key+" Integer: "+Ivalue);
          continue;
        }

        m = regf.matcher(svalue);
        if (m.matches()) {
          svalue = m.group(1);
          Fvalue = new Float(svalue);
          expHmap.put(key, Fvalue);
          //          System.out.println("key: "+key+" Float: "+Fvalue);
          continue;
        }

        m = regfvs.matcher(svalue);
        if (m.matches()) {
          svalue = m.group(1);
          list = token.split(svalue);
          n = list.length;
          fvals = new float[n];
          //          System.out.print("key: "+key+" ");
          while (--n >= 0) {
            fvals[n] = (new Float(list[n])).floatValue();
            /*System.out.print("fvals["+n+"]: "+fvals[n]+" ");*/
          }
          //          System.out.print("\n");
          expHmap.put(key, fvals);
          continue;
        }

        m = regsvs.matcher(svalue);
        if (m.matches()) {
          svalue = m.group(1);
          list = token.split(svalue);
          n = list.length;
          //          System.out.print("key: "+key+" ");
          //          while (--n>=0) System.out.print("list["+n+"]: "+list[n]+" ");
          //          System.out.print("\n");
          expHmap.put(key, list);
          continue;
        }

        //       System.out.println("key: "+key+" svalue: "+svalue);
        expHmap.put(key, svalue);
      } else { /*System.out.println("key: "+key+" value: null");*/
        expHmap.put(key, null);
      }
    }

    setExpKeys(expHmap, "HIPPO.EXP.CALIB", pxe);
    setExpKeys(expHmap, "HIPPO.EXP.SMP", pxe);

    if (expHmap.get("HIPPO.EXP.CAN") != null)
      setExpKeys(expHmap, "HIPPO.EXP.CAN", pxe);

    return expHmap;
  }

  private static boolean setExpKeys(HashMap hmap, String expkeyh, Pattern p) {

    if (hmap.get(expkeyh) != null) {
      String dbkeyh = (String) hmap.get(expkeyh);
      Matcher m = p.matcher(dbkeyh);
      if (m.matches()) {
        dbkeyh = m.group(1);
        String sizetag = m.group(2);
        hmap.put(expkeyh + SYMBOL, hmap.get(dbkeyh + SYMBOL));
        hmap.put(expkeyh + FORMULA, hmap.get(dbkeyh + FORMULA));
        hmap.put(expkeyh + DENSITY, hmap.get(dbkeyh + DENSITY));
        hmap.put(expkeyh + SIGMA_A, hmap.get(dbkeyh + SIGMA_A));
        //        System.out.print("dbkeyh: "+dbkeyh+" sizetag: "+sizetag);
        dbkeyh += (sizetag.equals(""))
          ? SIZE
          : (SIZE + "." + Integer.parseInt(sizetag));
        //        System.out.println(" expkeyh+size: "+expkeyh+sizetag+" dbkeyh: "+dbkeyh);
        hmap.put(expkeyh + SIZE, hmap.get(dbkeyh));
      }
      return true;
    } else if (hmap.get(expkeyh + SYMBOL) == null) {
      System.out.println(
        "\n******WARNINGS******\n"
          + expkeyh
          + " KEYS NOT SET IN EXP CONFIG FILE\n");
      return false;
    }

    return false;
  }

  public static void printExpHashMap(HashMap hmap) {
    Iterator keys = hmap.keySet().iterator();
    String key, aboutv;
    //maybe change key to a stringbuffer if this method is used often?
    Object value, vs[];
    float fvs[];
    Class vclass;

    while (keys.hasNext()) {
      key = (String) keys.next();
      value = hmap.get(key);

      if (value != null) {
        vclass = value.getClass();

        if ((vclass).isArray()) {
          aboutv = "";
          //float[] case;
          if (vclass.getName().equals("[F")) {
            fvs = (float[]) value;
            for (int i = 0; i < fvs.length; i++)
              aboutv += fvs[i] + " ";
          }
          //(nonprimitive) Object[];        
          else {
            vs = (Object[]) value;
            for (int i = 0; i < vs.length; i++)
              aboutv += vs[i] + " ";
          }
        }
        //regular object;              
        else
          aboutv = String.valueOf(value);
        aboutv += "::" + vclass.getName();
      } else
        aboutv = String.valueOf(value); //"null" string;

      System.out.println(key + "--->" + aboutv);
    }

  }

  public static float getfloatKey(HashMap hmap, String key) {
    Object o = hmap.get(key);
    if (o == null)
      return 0.0f;
    else if (Integer.class.isInstance(o))
      return ((Integer) o).floatValue();
    else
      return ((Float) o).floatValue();
    //    return (o == null)?0.0f:((Float) o).floatValue();
  }

  public static int getintKey(HashMap hmap, String key) {
    Object o = hmap.get(key);
    return (o == null) ? 0 : ((Integer) o).intValue();
  }

  public static String getarrayKey(HashMap hmap, String key) {
    Object o = hmap.get(key);
    if (o == null)
      return "";
    else if (!o.getClass().isArray())
      return "";
    else {
      Object[] os = (Object[]) o;
      StringBuffer a2s = new StringBuffer();
      for (int i = 0; i < os.length; i++) {
        a2s.append(os[i].toString() + " ");
      }
      return a2s.toString();
    }
  }

  private static void getBadDataList (HashSet baddets, String hipporunpar) throws IOException, InterruptedException {

    BufferedReader fr_input = new BufferedReader(new FileReader(hipporunpar));
    String line = null, list[];
    Pattern token = Pattern.compile("\\s+");
    Integer gID;      
    while((line = fr_input.readLine()) != null ) {
      if(line.charAt(0) == '#') continue;
      list = token.split(line.trim());
      System.out.println("\""+hipporunpar+"\""+" indicates the following data groups not to be used:\n"
                          +line);
      for (int i = 0; i < list.length; i++){
        gID = new Integer(list[i]);
        baddets.add(gID);
      }
//      break;
    }
    fr_input.close();
  } 

  public void linkLPSDtoDataSet (DataSet ds, String gladrunpar) {

    int pid, ndt = ds.getNum_entries();
    int maxid = 0;
    for (int i = 0; i < ndt; i++) {
      pid = ds.getData_entry(i).getGroup_ID();
      if (pid > maxid) maxid = pid;
    }
    boolean bad_data[] = new boolean[maxid+1];
    dataID2gridID = new int[maxid+1];
    dataID2index = new int[maxid+1];
    for (int i = 0; i < ndt; i++) {
      pid = ds.getData_entry(i).getGroup_ID();
      dataID2index[pid] = i;
    }
         
    ReferenceGrid rgrid, rgrids[] = ReferenceGrid.MakeDataReferenceGrids( ds );
    int nlpsd = rgrids.length;
    int gid, gids[] = new int[nlpsd];
    maxid = 0;
    for (int i = 0; i < nlpsd; i++) {
      gid = rgrids[i].ID();
      gids[i] = gid;
      if (gid > maxid) maxid = gid;
    }
    ArrayList gridid2dataid[] = new ArrayList[maxid+1];
    gridID2dataID = new int[maxid+1][];
    for (int i = 0; i < maxid+1; i++) {
      gridid2dataid[i] = new ArrayList();       
    }
    badLPSD = new boolean[maxid+1]; //grid id starting at 1;

/*
    try {
      getBadDataList(bad_data, gladrunpar);
    } catch(Throwable t) {
      System.out.println("unexpected error");
      t.printStackTrace();
    }
*/
    
    Data dt;
    HashMap hmap = new HashMap();
    Integer pID;
    for (int i = 0; i < nlpsd; i++) {
      rgrid = rgrids[i];
      gid = gids[i];     
      for (int j = 0; j < rgrid.num_rows(); j++) {
        for (int k = 0; k < rgrid.num_cols(); k++) {
          if ( (dt = (Data)rgrid.getData_entry(j+1, k+1)) != null ) {
            pid = dt.getGroup_ID();
            pID = new Integer(pid);
            if (hmap.get(pID) == null){
              hmap.put(pID, pID);
              dataID2gridID[pid] = gid;
              gridid2dataid[gid].add(pID);
              if (bad_data[pid] == true)
                badLPSD[gid] = true;                                               
            }
          }
        }
      }    
    }

    for (int i = 0; i < maxid+1; i++) {
      if (gridid2dataid[i].isEmpty()) {
        gridID2dataID[i] = null;
      } else {
        ndt = gridid2dataid[i].size();
        gridID2dataID[i] = new int[ndt];
        for (int j = 0; j < ndt; j++) {
          gridID2dataID[i][j] = ((Integer) gridid2dataid[i].get(j)).intValue();
        }
      }
    }
  }

  public void setBadDataGroups(DataSet ds, String hipporunpar, float lcutoff){

//    ArrayList LDeadDet = new ArrayList();
    ArrayList LRemovedDataID = new ArrayList();
    HashSet hset = new HashSet();   
//    StringBuffer deadDetList = new StringBuffer();
    int pid, gid, nbaddet;
    Integer gID;
    int ndata = ds.getNum_entries();
    System.out.println("\nSetting up dead detector and corresponding data group ID lists...");
    
    try {
          getBadDataList(hset, hipporunpar);
        } catch(Throwable t) {
          System.out.println("unexpected error");
          t.printStackTrace();
    }
    

    int nbadgrp;
    StringBuffer removedDataIDList = new StringBuffer();
    Data dt;
    float sumcts;
    float[] y_vals_d;
    for (int i = 0; i < ndata; i++){
      dt = ds.getData_entry(i);
      y_vals_d = dt.getY_values();
      pid = dt.getGroup_ID();
//      gid = dataID2gridID[pid];
      gID = new Integer(pid);
      sumcts = 0.0f;
      for (int k = 0; k < y_vals_d.length; k++) sumcts += y_vals_d[k];
//      if (badLPSD[gid] == true ||
//          ((Float)(dt.getAttributeValue(Attribute.TOTAL_COUNT))).floatValue() <= lcutoff ) 
      if (hset.contains(gID) || sumcts <= lcutoff) 
      {/*
        gID = new Integer(gid);
        if(hmap.get(gID) == null){
          LDeadDet.add(gID);
          deadDetList.append(gid + " ");
          hmap.put(gID, gID); 
        }*/
        LRemovedDataID.add(new Integer(pid));
        removedDataIDList.append(pid + " "); 
      } 
    }
//    nbaddet = LDeadDet.size();
    nbadgrp = LRemovedDataID.size();
    System.out.println(nbadgrp+" out of "+ndata+" data groups will be removed:\n"+"removedDataList: "+removedDataIDList+"\n"
                          +"Analyze "+(ndata-nbadgrp)+" data groups.");     
//    deadDet = new int[nbaddet];
    removedDataID = new int[nbadgrp];
/*
    for (int i = 0; i < nbaddet; i++) {
      deadDet[i] = ((Integer)LDeadDet.get(i)).intValue();
    }
*/
    for (int i = 0; i < nbadgrp; i++) {
      removedDataID[i] = ((Integer)LRemovedDataID.get(i)).intValue();
    }
    System.out.println("Done.\n");
  }


  /**
   *  Load detector information for the LANSCE HIPPO instrument from the 
   *  configuration file "/InstrumentInfo/LANSCE/hippodets.par".
   *
   *  @param  file_name  Name of file containing the HIPPO detector information 
   */
  public static void Load_SED_Info(String file_name) {
    int bank;
    int ndets;
    int index;
    detParamVals = new float[6][400][4];
    try {
      TextFileReader f = new TextFileReader(file_name);
      f.SkipLinesStartingWith("#");
      while (!f.end_of_data()) {
        if (!f.read_String().equalsIgnoreCase("BANK"))
          throw new RuntimeException("!!!!!!Unexpected error reading the detector file!!!!!!");
        bank = f.read_int();
        ndets = f.read_int();
        System.out.println("bank: "+bank+" ndets: "+ndets);
        switch (bank) {
          case 10 :
            index = 0;
            break;
          case 20 :
            index = 1;
            break;
          case 40 :
            index = 2;
            break;
          case 90 :
            index = 3;
            break;
          case 150 :
            index = 4;
            break;
          default :
            index = -1;
            break;
        }
        //        f.SkipLinesStartingWith("#");
        for (int i = 0; i < ndets; i++) {
          f.read_int();
          for (int j = 0; j < 4; j++) {
            detParamVals[index][i][j] = f.read_float();
          }
        }
      } //file reading loop;
    } catch (Exception e) {
      System.out.println("Exception reading file " + file_name + "\n" + e);
      e.printStackTrace();
    }
  }

  //unit testing;    
  public static void main(String[] args) {
    /*        
        HIPPORunProps runinfo = getExpProps();
        RunfileRetriever rr = new RunfileRetriever( "/IPNShome/taoj/cvs/ISAW/SampleRuns/glad8094.run" );
        DataSet ds = rr.getDataSet(1);
        long start = System.currentTimeMillis();   
        LoadUtil.Load_GLAD_LPSD_Info( ds, GLADDetTable );
        long time = System.currentTimeMillis()-start;
        System.out.println("\nLoadUtil.Load_GLAD_LPSD_Info() takes "+time+" ms.");
        
        start = System.currentTimeMillis();
        try {
          runinfo.linkLPSDtoDataSet(ds, "/IPNShome/taoj/cvs/ISAW/Databases/gladrun2.par");
        } catch(Throwable t) {
          System.out.println("unexpected error");
          t.printStackTrace();
        }    
        time = System.currentTimeMillis()-start;
        System.out.println("\nlinkLPSDtoDataSet() takes "+time+" ms.");
    
        
        start = System.currentTimeMillis();
        runinfo.setBadDataGroups(ds, 20.0f);
        time = System.currentTimeMillis()-start;
        System.out.println("setBadDataGroups() takes "+time+" ms.");
    */
    Load_SED_Info(HIPPODetTable);
    for (int j = 0; j < 6; j++)
    {
      for (int i = 0; i < 400; i++) {
        System.out.println(
          i
            + " "
            + detParamVals[j][i][0]
            + " "
            + detParamVals[j][i][1]
            + " "
            + detParamVals[j][i][2]
            + " "
            + detParamVals[j][i][3]);
      }
      System.out.println("   ");
    }    
  }

}