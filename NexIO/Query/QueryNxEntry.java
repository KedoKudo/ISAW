
/*
 * File:  QueryNxEntry.java
 *
 * Copyright (C) 2003, Ruth Mikkelson
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
 * Contact :  Ruth Mikkelson <mikkelsonr@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.1  2003/11/23 23:47:35  rmikk
 * Initial Checkin
 *
 * Revision 1.1  2003/11/16 21:37:15  rmikk
 * Initial Checkin
 *
 */

package NexIO.Query;
import NexIO.*;
import NexIO.State.*;
import NexIO.Util.*;

import NexIO.Process.*;

/**
 *   This class's getNxDataProcessor returns the correct IProcessNxData 
 *   depending on the State information
 */
public class QueryNxEntry {

  
  /**
   *   Returns the proper IProcessNxData class that is also properly configured
   *   @param State The linked list of state information
   *   @param NxDataNode  An NxNode with information on the NeXus NXdata class.
   *   @param NxinstrumentNode An NxNode with information on the NeXus 
   *                        NXinstrument class.
   */
  public static IProcessNxEntry getNxEntryProcessor(NxfileStateInfo State, 
              NxNode NxDataNode, NxNode NxInstrumentNode, int startGroupID){
    
     if( State == null)
        return null;
    
     if( NxDataNode == null)
        return new ProcessNxEntry();
     NxDataStateInfo dataState = new NxDataStateInfo(NxDataNode,NxInstrumentNode,
          State, startGroupID);
     if( dataState.linkName == null)
        return new ProcessOldNxEntry();
     else
        return new ProcessNxEntry();
  }
  
 }


