/*
 * File:  CommandPane.java
 *
 * Copyright (C) 2001, Ruth Mikkelson
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
 *           Menomonie, WI. 54751
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log: CommandPane.java,v $
 * Revision 1.79  2008/02/14 16:53:06  rmikk
 * Changed catch Exception to catch Throwable in two places to catch more
 *     errors  when using the IntrinsicJavaScript class
 *
 * Revision 1.78  2008/02/13 18:19:50  rmikk
 * Changed catch Exception to catch Throwable to catch more errors
 *
 * Revision 1.77  2008/02/13 16:41:04  rmikk
 * Placed a try catch block around the new class that uses java 1.6's scripting
 *   system
 *
 * Revision 1.76  2007/05/08 17:01:42  rmikk
 * Displays result in the status pane if no ParameterDialog box appears
 *
 * Revision 1.75  2007/05/03 14:26:03  rmikk
 * Incorporated scripts that can be handled by the new  IntrinsicJavaScript
 *    operators
 *
 * Revision 1.74  2005/05/25 18:01:10  dennis
 * Replaced direct call to .show() method for window,
 * since .show() is deprecated in java 1.5.
 * Now calls WindowShower.show() to create a runnable
 * that is run from the Swing thread and sets the
 * visibility of the window true.
 *
 * Revision 1.73  2005/03/08 02:33:39  dennis
 * Changed title on Clear button from "Clear" to "Reset"
 *
 * Revision 1.72  2004/05/14 04:12:27  bouzekc
 * Fixed bug that switched the index back to "Isaw Script" when a Jython
 * script with an error is parsed.
 *
 * Revision 1.71  2004/05/14 03:29:24  bouzekc
 * Fixed annoying bug that would switch the script selector combo box to
 * "Isaw Script" no matter what type of script was loaded.  It now changes
 * based on script type.
 *
 * Revision 1.70  2004/03/15 19:34:45  dennis
 * Removed unused imports after factoring out view components,
 * math and utilities.
 *
 * Revision 1.69  2004/03/15 03:30:14  dennis
 * Moved view components, math and utils to new source tree
 * gov.anl.ipns.*
 *
 * Revision 1.68  2004/02/07 19:39:23  rmikk
 * Fixed an error that prevented changing from Jython scripts to Isaw Scripts
 *
 * Revision 1.67  2004/01/08 19:40:01  bouzekc
 * Removed unused variables.
 *
 * Revision 1.66  2003/12/14 19:18:06  bouzekc
 * Removed unused import statements.
 *
 * Revision 1.65  2003/10/28 19:55:42  rmikk
 * Fixed javadoc error
 *
 * Revision 1.64  2003/10/20 16:25:05  rmikk
 * Fixed javadoc errors
 *
 * Revision 1.63  2003/10/10 01:17:36  bouzekc
 * Changed instantiation references from ScriptProcessor to ScriptOperator.
 *
 * Revision 1.62  2003/10/10 01:05:04  bouzekc
 * Another attempt at removing CTRL-M characters.
 * 
 * Revision 1.61  2003/10/10 01:03:14  bouzekc
 * Removed Windows CTRL-M characters.
 *
 * Revision 1.60  2003/10/10 00:41:52  bouzekc
 * Changed references from ScriptProcessor to IScriptProcessor.
 *
 * Revision 1.59  2003/07/18 22:00:48  rmikk
 * Added code so that the Send in scripts works in the
 * immediate pane and also when a script with no
 * parameters is run
 *
 * Revision 1.58  2003/07/01 21:42:48  rmikk
 * Changed the Dialog box to be non-modal. It still reports
 *    error conditions.
 *
 * Revision 1.57  2003/06/02 22:29:46  rmikk
 * -Reduced the adding of IObservers to a Script.  This
 *  is done in the JParametersDialog, if the IObserver
 * argument is not null
 *
 * Revision 1.56  2003/06/02 14:28:05  rmikk
 * -Added setDefaultParameter after the Run button is pressed
 *   in case a new script was loaded in
 *
 * Revision 1.55  2003/05/28 18:40:39  pfpeterson
 * Changed System.getProperty to SharedData.getProperty
 *
 * Revision 1.54  2003/05/16 15:21:27  pfpeterson
 * Removed a redundant call to setDefaultParameters() immediately after
 * an operator is instantiated (it is done in the constructor).
 *
 * Revision 1.53  2003/04/21 19:21:34  pfpeterson
 * Sets the filename on the script editor border when saving a file.
 *
 * Revision 1.52  2003/03/21 19:27:25  rmikk
 * Reset Error and other conditions before running a script
 *
 * Revision 1.51  2003/03/06 22:53:48  pfpeterson
 * No longer gives or receives StatusPane with anyone.
 *
 * Revision 1.50  2003/03/06 15:51:36  pfpeterson
 * Changed to work with SharedData's private StatusPane.
 *
 * Revision 1.49  2003/02/24 13:26:44  rmikk
 * Eliminated null pointer exception when jython is not available
 *
 * Revision 1.48  2003/02/21 19:35:44  pfpeterson
 * Changed calls to fixSeparator appropriate (not deprecated) method.
 *
 * Revision 1.47  2003/02/11 16:56:42  pfpeterson
 * No longer allows 'jython' option if Jython cannot be used.
 *
 * Revision 1.46  2003/01/07 16:06:43  rmikk
 * Fixed error so choice of Jython interpreter is not allowed if the Jython system is absent
 *
 * Revision 1.45  2003/01/02 20:42:13  rmikk
 * Now supports the python interpreter in addition to ISAW's Script interpreter
 *    1) Adds a Combo box to choose interpreter
 *    2) .py extensions use the Jython interpreter while .iss use the old interpreter
 *    3) Keeps track of all IObservers and all data sets that are added externally
 *
 * Revision 1.44  2002/12/08 22:09:05  dennis
 * Now uses new Jhelp class. (Ruth)
 *
 * Revision 1.43  2002/12/02 15:41:20  rmikk
 * The Command Pane's help button now uses the JHelp system if it is present
 *   otherwise the other system is used
 *
 * Revision 1.42  2002/11/27 23:12:10  pfpeterson
 * standardized header
 *
 * Revision 1.41  2002/11/01 20:29:33  pfpeterson
 * Moved debuging statement to only print when debug flag is on.
 *
 * Revision 1.40  2002/10/23 19:59:08  pfpeterson
 * When editing a file, the Save button now defaults to the file
 * being edited.
 *
 * Revision 1.39  2002/08/19 17:06:57  pfpeterson
 * Reformated file to make it easier to read.
 *
 * Revision 1.38  2002/06/28 13:26:25  rmikk
 * -Eliminated  unused code and methods
 * -Made the last opened filename appear on the title of the
 *  CommandPane's Editor window
 * - Command Pane( standalone) now uses the SharedData
 *   to load the system properties
 * - The status pane is now IS a JPanel that includes
 *    a) The scroll bars
 *    b) The Save and Clear buttons
 *   c) The editable unwrappable text area( this was the previous StatusPane)
 *
 * Revision 1.37  2002/06/03 13:54:53  rmikk
 * Eliminated the clearing of the status pane when the Run
 *   or Clear button is pressed
 * Adjusted a few indents
 *
 * Revision 1.36  2002/02/22 20:33:36  pfpeterson
 * Operator Reorganization.
 *
 * Revision 1.35  2002/01/10 15:38:33  rmikk
 * Now uses the Global StatusPane
 *     ( DataSetTools.util.SharedData.status_pane)
 *
 * Revision 1.34  2002/01/09 19:28:54  rmikk
 * -Extracted the StatusPane from CommandPane
 * -The status pane is now constructed and added in the
 *    main program so CommandPane is still standalone with
 *    a status pane
 *
 * Revision 1.33  2001/12/21 17:50:20  dennis
 * 1: Almost completely separated out the status pane.
 * 2: Included a Save and Clear option on this status pane
 *    for demonstration only.
 * 3: The open and save buttons now use the new actionlisteners:
 *    OpenFileToDocListener and SaveDocToFileListener
 *
 */
package Command;

import DataSetTools.components.ParametersGUI.*;

import DataSetTools.dataset.*;

import DataSetTools.operator.Generic.*;

import DataSetTools.util.*;

import IsawGUI.*;

import gov.anl.ipns.Util.Messaging.*;
import gov.anl.ipns.Util.Sys.*;
import gov.anl.ipns.ViewTools.Panels.GraphTagFrame;
import gov.anl.ipns.ViewTools.UI.*;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.io.*;

import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;


/**
 * Pane to enter, execute and handle commands A command can be executed
 * immediately  or  A sequence of commands can all be executed. The Immediate
 * commands are entered in the immediate window The sequence of commands are
 * entered in the Editor window. The commands can act on Isaw Data Sets and
 * will be extended to include most of the commands available in the GUI.
 */
public class CommandPane extends JPanel implements PropertyChangeListener,
  IObservable, IObserver {
  //~ Instance fields **********************************************************

  JButton Run;
  JButton Open;
  JButton Save;
  JButton Help;
  JButton Clear;
  JComboBox Language;
  JTextArea Commands;
  JTextArea Immediate;
  JScrollPane CommandSP      = null;  //Needed to change its border's title
  String FilePath            = null;  // for macro storage and retrieval
  File SelectedFile          = null;
  Document logDoc            = null;
  public IScriptProcessor SP;
  boolean Debug              = false;
  PropertyChangeSupport PC;
  IObserverList IObslist;
  Vector DSList;
  OpenFileToDocListener Opn = null;
  SaveDocToFileListener Sav  = null;

  //~ Constructors *************************************************************

  /**
   * Creates the JPanel for editing and executing scripts
   */
  public CommandPane(  ) {
    super();
    PC         = new PropertyChangeSupport( this );
    IObslist   = new IObserverList(  );
    initt(  );
    SP = new ScriptOperator( Commands.getDocument(  ) );
  }

  //~ Methods ******************************************************************

  /**
   * This routine can be used by Isaw to run a macro with parameters.  It
   * creates a GUI that lets users enter values for the parameters in the
   * macro
   * 
   * <P></p>
   *
   * @param fname The name of the file that stores the Macro
   * @param X An Obsever who will receive the data sets that are "Sent" with
   *        the SEND command and that are the result of the script fname
   * @param ds_src A list of data sets that can be selected as values for Data
   *        Set Parameters.
   * @param   DocLog the Document that stores log information
   */
  public void getExecScript( 
    String fname, IObserver X, IDataSetListHandler ds_src, Document DocLog ) {
    ScriptOperator cp = new ScriptOperator( fname );
    cp.addIObserver( X );
    cp.addPropertyChangeListener( this );
    cp.setLogDoc( DocLog );

    if( cp.getErrorCharPos(  ) >= 0 ) {
      return;
    }
    new IsawGUI.Util(  ).appendDoc( logDoc, "#$ Script File Execute " + fname );

    new JParametersDialog( cp, ds_src, logDoc, X, false );
  }

  /**
   * Set the Document that logs operations
   *
   * @param doc the Document that will receive log information NOTE: In the
   *        future the log document may be executable to redo a session
   */
  public void setLogDoc( Document doc ) {
    logDoc = doc;
    SP.setLogDoc( doc );
  }

  /**
   * Allows for DataSets from external sources to be used by the
   * ScriptOperator
   *
   * @param dss The data set that is to be added to this CommandPane unit
   */
  public void addDataSet( DataSet dss ) {
    SP.addDataSet( dss );
    DSList.addElement( dss );
    dss.addIObserver( this );
  }

  /**
   * adds and IObserver.
   *
   * @param iobs The IObserver that is to be added NOTE: This unit only
   *        notifies observers of a new DataSet
   */
  public void addIObserver( IObserver iobs ) {
    //SP.addIObserver( iobs );
    IObslist.addIObserver( iobs );
  }

  /**
   * Adds property change Listener.  So far the only events sent have the
   * Display and Clear property for the Status Pane
   */
  public void addPropertyChangeListener( PropertyChangeListener listener ) {
    if( PC != null ) {
      PC.addPropertyChangeListener( listener );
    }

    if( SP != null ) {
      SP.addPropertyChangeListener( listener );
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param logDoc DOCUMENT ME!
   * @param appendDoc DOCUMENT ME!
   * @param Message DOCUMENT ME!
   */
  public void appendlog( Document logDoc, Document appendDoc, String Message ) {
    if( appendDoc == null ) {
      return;
    }

    if( logDoc == null ) {
      return;
    }

    try {
      String Txt = appendDoc.getText( 
          appendDoc.getStartPosition(  ).getOffset(  ), appendDoc.getLength(  ) );

      if( Txt != null ) {
        if( Txt.length(  ) > 0 ) {
          if( Txt.charAt( Txt.length(  ) - 1 ) != '\n' ) {
            Txt = Txt + "\n";
          }
        }
      }
      Txt = "#$ Start" + Message + "\n" + Txt + "#$ End" + Message + "\n";
      logDoc.insertString( logDoc.getLength(  ), Txt, null );
    } catch( Exception s ) {
      System.out.println( "Error in appendlog=" + s );
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param iobs DOCUMENT ME!
   */
  public void deleteIObserver( IObserver iobs ) {
    //SP.deleteIObserver( iobs );
    IObslist.deleteIObserver( iobs );
  }

  /**
   * DOCUMENT ME!
   */
  public void deleteIObservers(  ) {
    //SP.deleteIObservers();
    IObslist.deleteIObservers(  );
  }

  /**
   * Attempts to fix differences in CR-LF handling of systems
   *
   * @param Doc the Plain document with the errant characters
   */
  public static void fixUP( Document Doc ) {
    String S;

    try {
      S = Doc.getText( 0, Doc.getLength(  ) );
    } catch( BadLocationException e ) {
      return;
    }

    char c;

    if( S.length(  ) <= 0 ) {
      return;
    }

    if( S.length(  ) <= 1 ) {
      c = S.charAt( 0 );

      if( ( c != 10 ) || ( c != 13 ) ) {
        return;
      }

      try {
        Doc.remove( 0, 1 );
        Doc.insertString( 0, "\n", null );
      } catch( BadLocationException e ) {
        return;
      }
    }

    for( int i = S.length(  ) - 1; i >= 0; i-- ) {
      c = S.charAt( i );

      if( ( int )c < 32 ) {
        if( c != '\n' ) {
          if( ( c == 10 ) || ( c == 13 ) ) {
            boolean remove = false;

            if( ( i + 1 ) < S.length(  ) ) {
              if( S.charAt( i + 1 ) == '\n' ) {
                remove = true;
              }
            }

            if( i > 0 ) {
              if( S.charAt( i - 1 ) == '\n' ) {
                remove = true;
              }
            }

            try {
              Doc.remove( i, 1 );

              if( !remove ) {
                Doc.insertString( i, "\n", null );
              }
            } catch( BadLocationException ee ) {}
          }
        }
      }
    }
  }

  //*****************SECTION:MAIN********************

  /**
   * Test program for this unit- no args are used
   */
  public static void main( String[] args ) {
    new SharedData(  );
    FinishJFrame F;
    CommandPane P;
    F   = new FinishJFrame( "Command Pane" );
    P   = new CommandPane(  );

    Dimension D = P.getToolkit(  )
                   .getScreenSize(  );
    F.setSize( ( int )( .6 * D.width ), ( int )( .7 * D.height ) );
    WindowShower.show( F );

    /* JScrollPane X = new JScrollPane( SharedData.status_pane);
       X.setBorder(new TitledBorder( "Status" ));
       JPanelwithToolBar YY = new JPanelwithToolBar("Save", "Clear",
       new SaveDocToFileListener( SharedData.status_pane.getDocument(),
       null), new ClearDocListener( SharedData.status_pane.getDocument()),
       X,BorderLayout.EAST);
    
       SharedData.status_pane.setEditable( true);
     */
    SplitPaneWithState Center = new SplitPaneWithState( 
        JSplitPane.VERTICAL_SPLIT, P, SharedData.getStatusPane(  ), .80f );
    F.getContentPane(  )
     .add( Center );
    P.addPropertyChangeListener( SharedData.getStatusPane(  ) );
    F.validate(  );
  }

  /**
   * Receives a PropertyChange Event.
   *
   * @param evt the PropertyChangeEvent. Note: The only event that is serviced
   *        is the one with the property name "Display".  The new value will
   *        be displayed in the Status line
   */
  public void propertyChange( PropertyChangeEvent evt ) {
    String filename = null;

    if( evt.getSource(  ) instanceof OpenFileToDocListener ) {
      OpenFileToDocListener opn = ( OpenFileToDocListener )( evt.getSource(  ) );
      filename = opn.getFileName(  );

      if( Debug ) {
        System.out.println( "CP:" + filename );
      }
      CommandSP.setBorder( new TitledBorder( "Prgm Editor:" + filename ) );
      PC.firePropertyChange( "filename", null, filename );

      
      IScriptProcessor sp = null;
      try{
         
         sp = DataSetTools.operator.IntrinsicJavaScript.
                                           ScriptHandlerFromFile( filename ); 
         Language.setSelectedItem(((DataSetTools.operator.IntrinsicJavaScript)sp).ScriptLang);
      }catch( Throwable ss){
         
         sp = null;
      }
      
      if( sp == null)
         sp = ScriptInterpretFetch.getScriptProcessor( 
                                        filename, Commands.getDocument(  ) );

      if( sp == null ) {
        sp = new ScriptOperator( Commands.getDocument(  ) );
      }
      sp.addPropertyChangeListener( this );
      SP = sp;

      //SP.setIObserverList( IObslist );
      SP.setPropertyChangeList( PC );
      addDataSets( SP );

      if( Language != null ) {
        if( SP instanceof DataSetTools.operator.PyScriptOperator ) {
          Language.setSelectedIndex( 1 );
        } else if( SP instanceof ScriptOperator){
          Language.setSelectedIndex( 0 );
        }
      }
    } else if( evt.getSource(  ) instanceof SaveDocToFileListener ) {
      filename = ( ( SaveDocToFileListener )evt.getSource(  ) ).getFileName(  );
    } else {
      return;
    }

    if( filename != null ) {
      CommandSP.setBorder( new TitledBorder( "Prgm Editor:" + filename ) );
    }
  }

  /**
   * Adds property change Listener.  So far the only events sent have the
   * Display and Clear property for the Status Pane
   */
  public void removePropertyChangeListener( PropertyChangeListener listener ) {
    PC.removePropertyChangeListener( listener );
    //SP.removePropertyChangeListener( listener);
  }

  /**
   * DOCUMENT ME!
   *
   * @param observed_obj DOCUMENT ME!
   * @param reason DOCUMENT ME!
   */
  public void update( Object observed_obj, Object reason ) {
    if( observed_obj instanceof DataSet ) {
      if( reason instanceof String ) {
        if( IObserver.DESTROY.equals( reason ) ) {
          DataSet ds = ( DataSet )observed_obj;
          ds.deleteIObserver( this );

          while( DSList.removeElement( ds ) ) {}

          return;
        }
      }
    }
    IObslist.notifyIObservers( observed_obj, reason );
  }

  /**
   * DOCUMENT ME!
   *
   * @param TA DOCUMENT ME!
   * @param line DOCUMENT ME!
   * @param charpos DOCUMENT ME!
   */
  private void setErrorCursor( JTextArea TA, int line, int charpos ) {
    int p;
    Element Eline = null;
    Document doc  = TA.getDocument(  );
    Element E     = doc.getDefaultRootElement(  );

    if( ( line >= 0 ) && ( line < E.getElementCount(  ) ) ) {
      Eline   = E.getElement( line );
      p       = Eline.getStartOffset(  ) + charpos;

      if( p > Eline.getEndOffset(  ) ) {
        p = Eline.getEndOffset(  ) - 1;
      }
      TA.requestFocus(  );
      TA.setCaretPosition( p );
    }
  }

  /**
   * Adds DataSets from the Tree.
   *
   * @param X DOCUMENT ME!
   */
  private void addDataSets( IScriptProcessor X ) {
    for( int i = 0; i < DSList.size(  ); i++ ) {
      X.addDataSet( ( DataSet )( DSList.elementAt( i ) ) );
    }
  }

  /**
   * Common entry point.
   */
  private void initt(  ) {
    JPanel JP;
    String ISAWscript        = "ISAW Script";
    Run                      = new JButton( "Run Script" );
    Open                     = new JButton( "Open Script" );
    Save                     = new JButton( "Save Script" );
    Help                     = new JButton( "Help" );
    Clear                    = new JButton( "Reset" );

    String[] othLang ;
    try{
       
       othLang = DataSetTools.operator.IntrinsicJavaScript.getScriptLanguages();
       
    }catch(Throwable s){
       
       othLang = new String[0];
    } 
    String[] LanguageChoices = new String[2+othLang.length];
    LanguageChoices[0]       = ISAWscript;

    {
      IScriptProcessor sp = ScriptInterpretFetch.getScriptProcessor( 
          "mine.py", null );

      if( sp == null ) {
        LanguageChoices      = new String[1+othLang.length];
        LanguageChoices[0]   = ISAWscript;
      } else {
        LanguageChoices[1] = "Jython Script";
      }
      for( int i=0; i< othLang.length;i++)
         LanguageChoices[ LanguageChoices.length -1-othLang.length+1+i] = othLang[i];
      sp = null;
    }

    if( LanguageChoices.length > 1 ) {
      Language = new JComboBox( LanguageChoices );
      Language.setSelectedIndex( 0 );
    }

    Jhelp jh = null;

    try {
      jh = new Jhelp(  );
    } catch( Throwable sss ) {
      jh = null;
    }
    Run.addActionListener( new MyMouseListener( this, jh ) );
    Help.addActionListener( new MyMouseListener( this, jh ) );
    Clear.addActionListener( new MyMouseListener( this, jh ) );

    if( LanguageChoices.length > 1 ) {
      Language.addActionListener( new MyMouseListener( this, jh ) );
    }
    setLayout( new BorderLayout(  ) );
    JP = new JPanel(  );
    JP.setLayout( new GridLayout( 1, 6 ) );
    JP.add( Run );
    JP.add( Open );
    JP.add( Save );
    JP.add( Clear );

    if( LanguageChoices.length > 1 ) {
      JP.add( Language );
    } else {
      JP.add( new JLabel( ISAWscript, JLabel.CENTER ) );
    }
    JP.add( Help );
    add( JP, BorderLayout.NORTH );
    Commands = new JTextArea( 7, 50 );
    Commands.setLineWrap( true );
    Commands.setFont( FontUtil.MONO_FONT );
    Immediate = new JTextArea( 5, 50 );
    Immediate.setFont( FontUtil.MONO_FONT );
    Immediate.addKeyListener( new MyKeyListener( this ) );
    Commands.setCaret( new MyCursor(  ) );
    Immediate.setCaret( new MyCursor(  ) );
    JLabel LineNum = new JLabel("line number 0");
    CommandSP = new CommandHolder( Commands , LineNum);
    CommandSP.setBorder( new TitledBorder( "Prgm Editor" ) );

    JPanel CommandWstat= new JPanel();
    CommandWstat.setLayout(  new BorderLayout() );
    CommandWstat.add( CommandSP, BorderLayout.CENTER );
    JPanel status = new JPanel();
    BoxLayout bl = new BoxLayout(status, BoxLayout.X_AXIS );
    status.setLayout( bl );
    status.add(  Box.createHorizontalGlue( ) );
    status.add(LineNum);
    CommandWstat.add(  status, BorderLayout.SOUTH )
    ;
    JScrollPane Y = new JScrollPane( Immediate );
    Y.setBorder( new TitledBorder( "Immediate" ) );

    SplitPaneWithState JPS = new SplitPaneWithState( 
        JSplitPane.VERTICAL_SPLIT, CommandWstat, Y, .75f );
    add( JPS, BorderLayout.CENTER );

    try {
      FilePath   = SharedData.getProperty( "Script_Path" );
      FilePath   = StringUtil.setFileSeparator( FilePath );

      if( Debug ) {
        System.out.println( "FilePath is " + FilePath );
      }
    } catch( Exception s ) {
      FilePath = null;

      if( Debug ) {
        System.out.println( " System properties could not be set" );
      }
    }

    Opn = new OpenFileToDocListener( 
        Commands.getDocument(  ), FilePath );
    Sav = new SaveDocToFileListener( 
        Commands.getDocument(  ), FilePath, Opn, "filename" );
    Opn.addPropertyChangeListener( Sav );
    Opn.addPropertyChangeListener( this );
    this.addPropertyChangeListener( Sav );
    Opn.addPropertyChangeListener( this );
    Sav.addPropertyChangeListener( this );
    Open.addActionListener( Opn );
    Save.addActionListener( Sav );
    DSList = new Vector(  );
  }
  
  

  //~ Inner Classes ************************************************************

  /**
   * DOCUMENT ME!
   *
   * @author $author$
   * @version $Revision$
   */
  private class MyKeyListener extends KeyAdapter implements KeyListener {
    //~ Instance fields ********************************************************

    CommandPane CP;
    int line = 0;

    //~ Constructors ***********************************************************

    /**
     * Creates a new MyKeyListener object.
     *
     * @param CP DOCUMENT ME!
     */
    public MyKeyListener( CommandPane CP ) {
      this.CP = CP;
    }

    //~ Methods ****************************************************************

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void keyTyped( KeyEvent e ) {
      if( 'x' == 'y' ) {  //e.getKeyChar())   used for testing macros){
        line = ScriptOperator.getNextMacroLine( 
            Commands.getDocument(  ), line );
        System.out.println( "line =" + line );
      }

      if( e.getKeyChar(  ) == KeyEvent.VK_ENTER ) {
        if( e.getSource(  )
               .equals( CP.Immediate ) ) {
          fixUP( CP.Immediate.getDocument(  ) );

          int i        = CP.Immediate.getCaretPosition(  );
          Document doc = CP.Immediate.getDocument(  );
          Element E    = doc.getDefaultRootElement(  );
          int line     = E.getElementIndex( i ) - 1;

          try {
            if( doc.getText( i, 1 )
                     .charAt( 0 ) >= ' ' ) {
              CP.Immediate.getDocument(  )
                          .remove( i - 1, 1 );
            }
          } catch( javax.swing.text.BadLocationException s ) {
            // let it drop on the floor
          }
          CP.SP.resetError(  );
          new IsawGUI.Util(  ).appendDoc( CP.logDoc, "#$ Start Immediate Run" );
          new IsawGUI.Util(  ).appendDoc( 
            CP.logDoc,
            ScriptOperator.getLine( CP.Immediate.getDocument(  ), line ) );
          CP.SP.addIObserver( CP );
          CP.SP.execute1( Immediate.getDocument(  ), line );
          CP.SP.deleteIObserver( CP );
          new IsawGUI.Util(  ).appendDoc( CP.logDoc, "#$ End Immediate Run" );

          if( CP.SP.getErrorCharPos(  ) >= 0 ) {
            CP.PC.firePropertyChange( 
              "Display", null,
              "Status: Error " + CP.SP.getErrorMessage(  ) + " on line " +
              line + " character" + CP.SP.getErrorCharPos(  ) );

            int p;
            Element Eline = null;

            if( ( line >= 0 ) && ( line < E.getElementCount(  ) ) ) {
              Eline   = E.getElement( line );
              p       = Eline.getStartOffset(  ) + CP.SP.getErrorCharPos(  );

              if( p > Eline.getEndOffset(  ) ) {
                p = Eline.getEndOffset(  ) - 1;
              }
              CP.Immediate.setCaretPosition( p );
            }
          }

          //if perror>=0
          line = E.getElementCount(  ) - 1;

          if( line >= 0 ) {
            if( 
              ( E.getElement( line )
                   .getEndOffset(  ) - E.getElement( line )
                                          .getStartOffset(  ) ) < 2 ) {
              return;
            }
          }

          try {
            doc.insertString( doc.getLength(  ), "\n ", null );
          } catch( BadLocationException s ) {
            System.out.println( "XXCVB" );
          }
        }

        //if immediate window
      }
    }
  }

  //End MyKeyListener

  /**
   * DOCUMENT ME!
   *
   * @author $author$
   * @version $Revision$
   */
  private class MyMouseListener extends MouseAdapter implements ActionListener,
    Serializable {
    //~ Instance fields ********************************************************

    CommandPane CP;
    Jhelp jh;

    //~ Constructors ***********************************************************

    /**
     * Creates a new MyMouseListener object.
     *
     * @param cp DOCUMENT ME!
     * @param jh DOCUMENT ME!
     */
    public MyMouseListener( CommandPane cp, Jhelp jh ) {
      CP        = cp;
      this.jh   = jh;
    }

    //~ Methods ****************************************************************

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed( ActionEvent e ) {
      //Document doc;

      if( e.getActionCommand(  ) == JParametersDialog.OPERATION_THROUGH ) {
        ReportExecutionStatus(  );

        return;
      } else if( e.getSource(  )
                    .equals( CP.Run ) ) {
        fixUP( CP.Commands.getDocument(  ) );
        CP.SP.setDocument( CP.Commands.getDocument(  ) );
        CP.SP.reset(  );
        CP.SP.resetError(  );
        CP.SP.setDefaultParameters(  );  //may change if script changes

        if( CP.SP.getErrorCharPos(  ) >= 0 ) {
          CP.PC.firePropertyChange( 
            "Display", null,
            "setDefault Error " + CP.SP.getErrorMessage(  ) + " at position " +
            CP.SP.getErrorCharPos(  ) + " on line " + CP.SP.getErrorLine(  ) );
          CP.SP.setDocument( null );

          return;
        }

        //CP.PC.firePropertyChange("Clear", null,null);
        appendlog( logDoc, Commands.getDocument(  ), "CommandPane Run" );

        if( CP.SP.getNum_parameters(  ) > 0 ) {
          if( SelectedFile != null ) {
            CP.SP.setTitle( SelectedFile.toString(  ) );
          } else {
            CP.SP.setTitle( "CommandPane" );
          }

          JParametersDialog pDialog = new JParametersDialog( 
              ( GenericOperator )( CP.SP ), SP, new PlainDocument(  ), CP );
          pDialog.addActionListener( this );

          return;
        } else {
          CP.SP.addIObserver( CP );
          Object Result = CP.SP.getResult(  );
          ScriptUtil.display( Result);
          CP.SP.deleteIObservers(  );
        }

        if( CP.SP.getErrorCharPos(  ) >= 0 ) {
          //new Util().appendDoc( StatusLine.getDocument(), 
         /* int errLine = CP.SP.getErrorLine(  );
          int errPos =CP.SP.getErrorCharPos(  ) ;
          CP.PC.firePropertyChange( 
            "Display", null,
            "Error " + CP.SP.getErrorMessage(  ) + " at position " +
            CP.SP.getErrorCharPos(  ) + " on line " + CP.SP.getErrorLine(  ) );
          CP.SP.setDocument( null );
          CP.setErrorCursor( Commands, errLine, errPos );
          */
           ReportExecutionStatus();
          return;
        }
      } else if( e.getSource(  )
                    .equals( CP.Clear ) ) {
        if( CP.SP == null ) {
          return;
        }
        CP.SP.reset(  );
        //CP.PC.firePropertyChange("Clear", null,null);
      }
      /*else if( e.getSource().equals( CP.Save )
         || e.getSource().equals( CP.Open )){
         final JFileChooser fc = new JFileChooser(FilePath) ;
         int state  ;
         if( SelectedFile != null )
         fc.setSelectedFile( SelectedFile );
         if( e.getSource().equals( Save ))
         state = fc.showSaveDialog( null ) ;
         else
         state =  fc.showOpenDialog( null  ) ;
      
         if( state != JFileChooser.APPROVE_OPTION )return ;
         FilePath= fc.getCurrentDirectory().toString();
         SelectedFile = fc.getSelectedFile() ;
         String filename  = SelectedFile.toString() ;
         String fname = SelectedFile.getName() ;
         System.out.println( "The filename is "  + filename ) ;
      
         if( e.getSource().equals( Save ) ){
         doc = Commands.getDocument() ;
         (new Util()).saveDoc( doc , filename );
         }
         else if( e.getSource().equals( Open ) ){
         doc = (new Util()).openDoc( filename );
         if( doc != null){
         Commands.setDocument( doc );
         Commands.setCaretPosition(0);
         Commands.setBorder(
         new TitledBorder( "Last File:"+filename ));
         }else
         System.out.println("Document is null");
         }
         }
       */
      else if( e.getSource(  ) 
                  .equals( Language ) ) {
        int indx = Language.getSelectedIndex(  );
       
        if( ( indx == 0 ) && ( SP instanceof ScriptOperator ) ) {
          return;
        }
        
        if( ( indx ==1 ) && ( SP instanceof DataSetTools.operator.PyScriptOperator ) ) {
          return;
        }
        
        String Fname = "*.iss";

        if( indx != 0 ) {
          Fname = "*.py";
        }
        
        IScriptProcessor sp = null;
        
        if( indx >0)
           try{
              
              sp =new DataSetTools.operator.IntrinsicJavaScript( 
                                     Language.getSelectedItem().toString().trim());
              
           }catch( Throwable ss){
              
              sp = null;
           }
           
        if( sp == null)
           sp = ScriptInterpretFetch.getScriptProcessor( 
                  Fname, Commands.getDocument(  ) );
       
        if( sp == null ) {
          //if the IScriptProcessor comes back null, there was some kind of problem with the script.
          //check to see what we are dealing with.
          if( Fname.equals( "*.py" ) ) {
            indx = 1;
          } else {
            indx   = 0;
          }
          
          //swap it back to a regular ScriptOperator-we have an error in the script anyway
          sp     = new ScriptOperator( Commands.getDocument(  ) );
          SharedData.addmsg( "There was an error processing your script.  " +
           "Either the Jython classes were not found, or there " );
          SharedData.addmsg( "is a syntax error in your script.  " +
           "You should see some additional messages either " );
          SharedData.addmsg( "here or on the console that should help." );
        }
        SP = sp;
        
        //SP.setIObserverList( IObslist );
        SP.setPropertyChangeList( PC );
        addDataSets( SP );
        Language.setSelectedIndex( indx );
      } else if( e.getSource(  )
                    .equals( Help ) ) {
        Dimension D = getToolkit(  )
                        .getScreenSize(  );

        if( jh != null ) {
          //HelpSet hs = new IsawHelp.HelpSystem.IsawOpHelpSet( false);
          //JHelp jh = new JHelp( hs);
          JFrame jf = new JFrame( "ISAW HELP" );
          jf.setSize( ( int )( .6 * D.width ), ( int )( .6 * D.height ) );
          jf.getContentPane(  )
            .add( jh.getHelpComponent(  ) );
          WindowShower.show(jf);
          jf.validate(  );

          return;
        }

        //catch( Throwable undef)
        //  {
        //  }
        //BrowserControl H = new BrowserControl() ; 
        HTMLPage H;
        String S;
        S = SharedData.getProperty( "Help_Directory" );

        if( S != null ) {
          S   = StringUtil.setFileSeparator( S );
          S   = S.trim(  );

          if( S.length(  ) < 1 ) {
            S = null;
          } else if( "\\/".indexOf( S.charAt( S.length(  ) - 1 ) ) < 0 ) {
            S = S + java.io.File.separator;
          }

          if( new File( S + "Command/CommandPane.html" ).exists(  ) ) {}
          else {
            S = null;
          }
        }

        if( S == null ) {
          S = SharedData.getProperty( "user.dir" )
                        .trim(  );

          if( S != null ) {
            if( S.length(  ) > 0 ) {
              if( "\\/".indexOf( S.charAt( S.length(  ) - 1 ) ) < 0 ) {
                S = S + java.io.File.separator;
              }
            }
          }
          S = StringUtil.setFileSeparator( S );

          if( !new File( S + "IsawHelp/Command/CommandPane.html" ).exists(  ) ) {
            S = null;
          } else {
            S = S + "IsawHelp" + java.io.File.separator;
          }
        }

        if( S != null ) {
          S = S + "Command/CommandPane.html";
        } else {
          String CP = SharedData.getProperty( "java.class.path" )
                                .replace( '\\', '/' );
          int s;
          int t;

          //System.out.println("E");
          for( s = 0; ( s < CP.length(  ) ) && ( S == null ); s++ ) {
            t = CP.indexOf( java.io.File.pathSeparator, s + 1 );

            if( t < 0 ) {
              t = CP.length(  );
            }
            S = CP.substring( s, t )
                  .trim(  );

            if( S.length(  ) > 0 ) {
              if( S.charAt( S.length(  ) - 1 ) != '/' ) {
                S = S + "/";
              }
            }

            if( new File( S + "IsawHelp/Command/CommandPane.html" ).exists(  ) ) {
              S = S + "IsawHelp/Command/CommandPane.html";
            } else {
              S = null;
            }
          }
        }

        if( S == null ) {
          S = "http://www.pns.anl.gov/isaw/IsawHelp/CommandPane.html";
        } else {
          S = "file:///" + S;
        }
        S = S.replace( '\\', '/' );

        try {
          H = new HTMLPage( S );
          H.setSize( ( int )( .6 * D.width ), ( int )( .6 * D.height ) );
          WindowShower.show(H);
        } catch( Exception s ) {
          // let it drop on the floor
        }
      }
    }

    // end actionperformed 

    /**
     * DOCUMENT ME!
     */
    private void ReportExecutionStatus(  ) {
       
      if( CP.SP.getErrorCharPos(  ) >= 0 ) {
        int errLine = SP.getErrorLine(  );
        int errPos = SP.getErrorCharPos( );
        if( errPos <=0)
           errPos = 1;
        
        CP.PC.firePropertyChange( "Display", null,          
          "Error " + CP.SP.getErrorMessage(  ) + " at position " +
          CP.SP.getErrorCharPos(  ) + " on line " + CP.SP.getErrorLine(  ) );
        
        CP.SP.setDocument( null );
        
        CP.setErrorCursor( Commands, errLine, errPos );
      }
    }
  }

  //End mouseAdapter 
  
  class CommandHolder   extends JScrollPane implements MouseListener, MouseMotionListener
  {
     JTextArea comp;
     FinishJFrame Pop = null;
     JLabel LineNumShow;
     boolean first = true;
     public CommandHolder( JTextArea comp, JLabel LineNumShow)
     {
        super( comp);
        this.comp = comp;
        this.LineNumShow = LineNumShow;
        if( LineNumShow != null )
           first = false;
        comp.addMouseListener( this );
     }
     
    private  void removePop( )
    {
       if( Pop != null)
       {
          Pop.removeAll( );
          Pop.dispose( );
          Pop= null;
       }
    }
   @Override
   public void mouseClicked(MouseEvent evt)
   {
      if( first )
      {
         JOptionPane.showMessageDialog(null,
               "Double click a line to see the line number");
         first = false;
         return;
      }
      if( evt.getClickCount( ) < 2 && LineNumShow == null)
         return;
      
      Point P = evt.getPoint( );
      int DocPos = comp.viewToModel( P );
     
      int p;
      Element Eline = null;
      Document doc  = comp.getDocument(  );
      Element E     = doc.getDefaultRootElement(  );
      boolean found = false;
      int line =0;
      for( line=0; line < E.getElementCount() && !found; )
      {
         Eline   = E.getElement( line );
        
        if( DocPos < Eline.getEndOffset(  ))
           found = true;       
        else
           line++;
         
      }

     removePop();
         
   
      
      if( LineNumShow != null)
      {
         LineNumShow.setText( "line number is "+(line+1) );
         return;
      }
      JTextField tf = new JTextField("line number is "+(line+1));
      Pop = new FinishJFrame();
      Pop.getContentPane().setLayout( new GridLayout(1,1) );
      P = GraphTagFrame.getPositionAbs( comp );
      Dimension D = comp.getSize( );
      System.out.println("P,D="+P+","+D);
      Pop.setBounds(P.x+D.width/2 ,P.y+D.height/2,150,100 );
      Pop.getContentPane( ).add(tf);
      WindowShower.show( Pop );
      //comp.requestFocus( );
      
    }

   @Override
   public void mouseEntered(MouseEvent arg0)
   {
      removePop();
      }

   @Override
   public void mouseExited(MouseEvent arg0)
   {
      removePop();   }

   @Override
   public void mousePressed(MouseEvent arg0)
   {

      removePop();   }

   @Override
   public void mouseReleased(MouseEvent arg0)
   {
      removePop();   }


   @Override
   public void mouseDragged(MouseEvent arg0)
   {
      removePop();      
   }


   @Override
   public void mouseMoved(MouseEvent arg0)
   {

      removePop();
     
      
   }
     
     
  }
}
