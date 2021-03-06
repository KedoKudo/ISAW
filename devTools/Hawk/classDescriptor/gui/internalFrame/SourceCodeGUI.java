/*
 * File:  SourceCodeGUI.java
 *
 * Copyright (C) 2004 Dominic Kramer
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
 *           Dominic Kramer <kramerd@uwstout.edu>
 *           Department of Mathematics, Statistics and Computer Science
 *           University of Wisconsin-Stout
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA and by
 * the National Science Foundation under grant number DMR-0218882.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:
 *
 * $Log$
 * Revision 1.4  2004/05/26 19:58:57  kramer
 * Made the gui use a SourceCodeJPanel to show the actual information.
 *
 * Revision 1.3  2004/03/12 19:46:16  bouzekc
 * Changes since 03/10.
 *
 * Revision 1.1  2004/02/07 05:09:16  bouzekc
 * Added to CVS.  Changed package name.  Uses RobustFileFilter
 * rather than ExampleFileFilter.  Added copyright header for
 * Dominic.
 *
 */
package devTools.Hawk.classDescriptor.gui.internalFrame;

import java.awt.Component;

import javax.swing.JMenuBar;

import devTools.Hawk.classDescriptor.gui.frame.HawkDesktop;
import devTools.Hawk.classDescriptor.gui.panel.SourceCodeJPanel;
import devTools.Hawk.classDescriptor.modeledObjects.Interface;

/**
 * This is a special type of JInternalFrame that displays the source code for a class 
 * or interface and has support for coloring keywords, javadoc statements, and comments.
 * @author Dominic Kramer
 */
public class SourceCodeGUI extends DesktopInternalFrame
{
	/**
	 * The SourceCodeJPanel that is located on the frame.
	 */
	protected SourceCodeJPanel panel;
	
	/**
	 * Create a new SourceCodeGUI.
	 * @param INT The Interface object whose source code is to be displayed.
	 * @param title The window's title.
	 * @param desk The HawkDesktop that this window is in.
	 */
	public SourceCodeGUI(Interface INT, String title, HawkDesktop desk)
	{
		super(desk,desk.getSelectedDesktop(),INT,true,true,true,false);
		
		//now to set some of the characteristics of the window
			setTitle(title);
			setLocation(0,0);
			setSize(175,400);
			setClosable(true);
			setIconifiable(true);
			setMaximizable(true);
			setResizable(true);
		
		panel = new SourceCodeJPanel(INT,this);
		JMenuBar menuBar = panel.createJMenuBar();
		menuBar.add(viewMenu);
		menuBar.add(windowMenu);
		setJMenuBar(menuBar);
		getContentPane().add(panel);
		resizeAndRelocate();
	}
	
	/**
	 * Gets a copy of this window.
	 * @return A copy of this window.
	 */
	public AttachableDetachableFrame getCopy()
	{
		return new SourceCodeGUI(selectedInterface,getTitle(),desktop);
	}
	
	/**
	 * The Components in the array returned from this method are the Components that should have the 
	 * mouse use the waiting animation when an operation is in progress.
	 */
	public Component[] determineWaitingComponents()
	{
		return panel.determineWaitingComponents();
	}
}
