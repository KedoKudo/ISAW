/*
 * File:  IntegrateFilter.java   
 *
 * Copyright (C) 2003, Peter F. Peterson
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
 *           9700 South Cass Avenue, Bldg 360
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
 *  Revision 1.3  2004/03/15 03:28:38  dennis
 *  Moved view components, math and utils to new source tree
 *  gov.anl.ipns.*
 *
 *  Revision 1.2  2003/06/13 21:59:11  bouzekc
 *  Now extends RobustFileFilter to take care of common
 *  functionality.
 *
 *  Revision 1.1  2003/05/08 19:50:28  pfpeterson
 *  Added to CVS.
 *
 */

package DataSetTools.operator.Generic.TOF_SCD;

import gov.anl.ipns.Util.File.RobustFileFilter;

/**
 * FileFilter for integrate files.
 */
public class IntegrateFilter extends RobustFileFilter {
  /**
   *  Default constructor.  Calls the super constructor,
   *  sets the description, and sets the file extensions.
   */
  public IntegrateFilter()
  {
    super();
    super.setDescription("Integrate files (*.integrate)");
    super.addExtension(".integrate");
  }
}
