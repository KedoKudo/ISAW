/*
 * File:  InitialPeaksWizard.java
 *
 * Copyright (C) 2003, Chris M. Bouzek
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
 *           Chris Bouzek <coldfusion78@yahoo.com>
 *
 * This work was supported by the National Science Foundation under grant
 * number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * $Log$
 * Revision 1.8  2003/07/03 14:17:43  bouzekc
 * Added comments and ordered methods according to access
 * privilege.
 *
 * Revision 1.7  2003/06/30 16:05:53  bouzekc
 * Now takes --nogui command line arguments.
 *
 * Revision 1.6  2003/06/26 16:31:18  bouzekc
 * Unlinked the LsqrsJForm matrix file parameter from the
 * other Forms.
 *
 * Revision 1.5  2003/06/25 20:25:36  bouzekc
 * Unused private variables removed, reformatted for
 * consistency.
 *
 * Revision 1.4  2003/06/19 20:51:39  bouzekc
 * Now uses constant parameters for the OperatorForms.
 *
 * Revision 1.3  2003/06/19 16:20:32  bouzekc
 * Now uses Wizard's linkFormParameters() to link the
 * parameters in the parameter table.
 *
 * Revision 1.2  2003/06/11 22:44:31  bouzekc
 * Added Wizard help message.
 *
 * Revision 1.1  2003/06/10 21:06:15  bouzekc
 *
 * Added to CVS
 *
 *
 */
package Wizard.TOF_SCD;

import DataSetTools.operator.*;

import DataSetTools.parameter.*;

import DataSetTools.util.*;

import DataSetTools.wizard.*;

import Operators.TOF_SCD.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.util.*;

import javax.swing.*;


/**
 * This class constructs a Wizard used for initially finding peaks.  In this
 * Wizard, BlindJ is used for creating a matrix file.
 */
public class InitialPeaksWizard extends Wizard {
  //~ Static fields/initializers ***********************************************

  private static final String LOADFILETYPE = "LoadFile";

  //~ Constructors *************************************************************

  /**
   * Default constructor.  Sets standalone in Wizard to true.
   */
  public InitialPeaksWizard(  ) {
    this( true );
  }

  /**
   * Constructor for setting the standalone variable in Wizard.
   *
   * @param standalone Boolean indicating whether the Wizard stands alone
   *        (true) or is contained in something else (false).
   */
  public InitialPeaksWizard( boolean standalone ) {
    super( "Initial SCD Peaks Wizard", standalone );
    this.createAllForms(  );

    StringBuffer s = new StringBuffer(  );

    s.append( "This Wizard is designed to be used as an initial\n" );
    s.append( "tool for finding peaks from SCD run files.  It\n" );
    s.append( "applies BlindJ, IndexJ, ScalarJ, and LsqrsJ\n" );
    s.append( "to the output .peaks file from the first Form.\n" );
    this.setHelpMessage( s.toString(  ) );
  }

  //~ Methods ******************************************************************

  /**
   * Method for running the Initial Peaks wizard as standalone.
   */
  public static void main( String[] args ) {
    InitialPeaksWizard w = new InitialPeaksWizard( true );

    //specified a --nogui switch but forgot to give a filename
    if( args.length == 1 ) {
      System.out.println( 
        "USAGE: java Wizard.TOF_SCD.InitialPeaksWizard " +
        "[--nogui] <Wizard Save File>" );
    } else if( args.length == 2 ) {
      w.executeNoGUI( args[1] );
    } else {
      w.showForm( 0 );
    }
  }

  /**
   * Adds and coordinates the necessary Forms for this Wizard.
   */
  private void createAllForms(  ) {
    int[][] fpi = {
      { 9, 0, 0, -1, 0 },  //peaks file 
      { -1, 2, 1, 0, -1 }
    };  //matrix file

    FindMultiplePeaksForm peaksform = new FindMultiplePeaksForm(  );

    //the return types of all of these Operator Forms is LoadFilePG,
    //hence the "LoadFile"
    OperatorForm blindjform = new OperatorForm( 
        new BlindJ(  ), LOADFILETYPE, "Matrix file", new int[]{ 0 } );
    OperatorForm indexjform = new OperatorForm( 
        new IndexJ(  ), LOADFILETYPE, "IndexJ log file", new int[]{ 0, 1 } );
    OperatorForm scalarjform = new OperatorForm( 
        new ScalarJ(  ), LOADFILETYPE, "ScalarJ log file", new int[]{ 0 } );
    OperatorForm lsqrsjform = new OperatorForm( 
        new LsqrsJ(  ), LOADFILETYPE, "LsqrsJ matrix file", new int[]{ 0 } );

    this.addForm( peaksform );
    this.addForm( blindjform );
    this.addForm( indexjform );
    this.addForm( scalarjform );
    this.addForm( lsqrsjform );

    super.linkFormParameters( fpi );
  }
}
