/*
 * File: BrowseButtonListener.java
 *
 * Copyright (C) 2002, Peter Peterson
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
 * Contact : Peter Peterson <pfpeterson@anl.gov>
 *           Intense Pulsed Neutron Source Division
 *           Argonne National Laboratory
 *           9700 S. Cass Avenue, Bldg 360
 *           Argonne, IL 60440
 *           USA
 *
 * For further information, see http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 *  $Log$
 *  Revision 1.1  2002/07/15 21:24:03  pfpeterson
 *  Added to CVS.
 *
 *
 */
package DataSetTools.components.ParametersGUI;

import javax.swing.*;
import DataSetTools.operator.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.filechooser.FileFilter;

    
public class BrowseButtonListener implements ActionListener{
    public static int SAVE_FILE  = 1;
    public static int LOAD_FILE  = 2;
    public static int LOAD_MULTI = 3;
    public static int DIR_ONLY   = 4;

    private static final boolean DEBUG=false;

    private JFileChooser   jfc;
    private FileFilter     file_filter;
    private JTextComponent textbox;
    private String         def_filename;
    private int            type;

    public BrowseButtonListener( JTextComponent entry, int type ){
        this(entry,type,null);
    }

    public BrowseButtonListener( JTextComponent entry, int type,
                                 FileFilter filter ){
        this.textbox=entry;
        this.type=type;
        this.file_filter=filter;
        this.jfc=null;
        this.def_filename=null;
        if(this.type==LOAD_MULTI){
            System.err.println("LOAD_MULTI not instantiated yet "
                               +"-- using LOAD_FILE instead");
            this.type=LOAD_FILE;
        }
    }

    public void actionPerformed( ActionEvent evt){ 
        if(DEBUG)System.out.println("type="+this.type);
        String filename=null;
        // configure the filechooser if we are on the first time
        if(jfc==null){
            jfc = new JFileChooser( def_filename);
            this.configureFileChooser();
            if( file_filter != null)
                jfc.addChoosableFileFilter( file_filter );
        }

        // set the text from the textbox
        filename=textbox.getText();
        if(filename!=null && filename.length()>0){
            if(this.type==SAVE_FILE){
                jfc.setSelectedFile(new File(filename));
            }else if(this.type==LOAD_FILE){
                jfc.setSelectedFile(new File(filename));
            }else if(this.type==LOAD_MULTI){
                // do something special
            }else if(this.type==DIR_ONLY){
                jfc.setSelectedFile(new File(filename));
            }
            filename=null;
        }

        // display the filechooser
        int returnVal=JFileChooser.CANCEL_OPTION;
        if( this.type==LOAD_FILE || this.type==LOAD_MULTI ){
            returnVal=jfc.showOpenDialog(null);
        }else if( this.type==DIR_ONLY ){
            returnVal=jfc.showDialog(null,"Select");
        }else if( this.type==SAVE_FILE ){
            returnVal=jfc.showSaveDialog(null);
        }

        // deal with the return value
        if( returnVal==( JFileChooser.APPROVE_OPTION) ){
            if(this.type==LOAD_MULTI){
                // do something special
            }else{
                File file= jfc.getSelectedFile();
                if(this.type==SAVE_FILE){
                    filename=file.getAbsolutePath();
                    textbox.setText( filename );
                }else if(this.type==LOAD_FILE || this.type==DIR_ONLY){
                    if(file.exists()){
                        filename=file.getAbsolutePath();
                        textbox.setText( filename );
                    }
                }
            }
        }
    }

    private void configureFileChooser(){
        // set the selection mode appropriately
        if( this.type==DIR_ONLY ){
            if(DEBUG)System.out.print("DIR");
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }else{ // all others are file only
            if(DEBUG)System.out.print("FILES");
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        if(DEBUG)System.out.print("_ONLY("+JFileChooser.DIRECTORIES_ONLY+","
                                  +JFileChooser.FILES_ONLY+":"
                                  +jfc.getFileSelectionMode()+") ");

        // set the type of dialog properly
        if( this.type==SAVE_FILE ){
            if(DEBUG)System.out.print("SAVE");
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
        }else{ // all other are loading or selecting directories
            if(DEBUG)System.out.print("OPEN");
            jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        }
        if(DEBUG)System.out.print("_DIALOG("+JFileChooser.SAVE_DIALOG+","
                                  +JFileChooser.OPEN_DIALOG+":"
                                  +jfc.getDialogType()+") ");

        // turn multi-selection on/off
        if(DEBUG)System.out.print("LOAD_MULTI="+(this.type==LOAD_MULTI)+" ");
        jfc.setMultiSelectionEnabled(this.type==LOAD_MULTI);
    }
}
