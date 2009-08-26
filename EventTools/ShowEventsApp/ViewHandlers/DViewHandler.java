/* 
 * File: DViewHandler.java
 *
 * Copyright (C) 2009, Paul Fischer
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
 * This work was supported by the National Science Foundation under grant
 * number DMR-0800276 and by the Spallation Neutron Source Division
 * of Oak Ridge National Laboratory, Oak Ridge TN, USA.
 *
 *  Last Modified:
 * 
 *  $Author: fischerp $
 *  $Date: 2009-08-25 11:02:13 -0500 (Tue, 25 Aug 2009) $            
 *  $Revision: 19931 $
 */

package EventTools.ShowEventsApp.ViewHandlers;

import java.awt.GridLayout;

import javax.swing.*;

import EventTools.ShowEventsApp.Command.Commands;
import gov.anl.ipns.ViewTools.Components.OneD.FunctionViewComponent;
import MessageTools.*;

/**
 * Builds and Displays a graph of d-spacing. Updates
 * automatically when data is loaded and displayed on the screen.
 */
public class DViewHandler implements IReceiveMessage
{
   private MessageCenter messageCenter;
   private JFrame        dDisplayFrame;
   private JPanel        graphPanel;
   private String        Title = "d-spacing";
   private String        x_units = "" + '\u00c5';
   private String        y_units = "weighted";
   private String        x_label = "d-spacing";
   private String        y_label = "Intensity";
   
   /**
    * Sets the message center for the DViewHandler but does
    * not display or create anything else.  The class relies mainly
    * on the message center.  It will display the jframe when it
    * receives SHOW_D_GRAPH message.
    * 
    * @param messageCenter
    */
   public DViewHandler(MessageCenter messageCenter)
   {
      this.messageCenter = messageCenter;
      this.messageCenter.addReceiver(this, Commands.SHOW_D_GRAPH);
      this.messageCenter.addReceiver(this, Commands.HIDE_D_GRAPH);
      this.messageCenter.addReceiver(this, Commands.SET_D_VALUES);
      this.messageCenter.addReceiver(this, Commands.ADD_EVENTS_TO_VIEW);
   }
   
   /**
    * Creates a new JFrame to display the graph every time
    * it is called.  Will display a graph if its been built
    * or will display a placeholder saying no data loaded.
    */
   private void displayDFrame()
   {
      dDisplayFrame = new JFrame("d-spacing View");
      dDisplayFrame.setLayout(new GridLayout(1,1));
      dDisplayFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      dDisplayFrame.setBounds(0, 0, 500, 300);
      dDisplayFrame.setVisible(true);
      
      if (graphPanel != null)
         dDisplayFrame.add(graphPanel);
      else
         dDisplayFrame.add(placeholderPanel());
      
      dDisplayFrame.repaint();
   }
   
   /**
    * Placeholder to put in the frame if no data is loaded.
    * 
    * @return Panel
    */
   private JPanel placeholderPanel()
   {
      JPanel placeholderpanel = new JPanel();
      placeholderpanel.setLayout(new GridLayout(1,1));
      
      JLabel label = new JLabel("No Data Loaded!");
      label.setHorizontalAlignment(JLabel.CENTER);
      
      placeholderpanel.add(label);
      
      return placeholderpanel;
   }
   
   /**
    * Takes the data and creates an instance of
    * FunctionViewComponent and adds it to the graphPanel
    * and then to the frame if the frame has been created.
    * This allows for the graph to be updated while the frame 
    * is displayed.
    * 
    * @param xyValues X,Y values of the data for the graph.
    */
   private void setPanelInformation(float[][] xyValues)
   {
      float[] x_values = xyValues[0];
      float[] y_values = xyValues[1];
      float[] errors = null;

      if(dDisplayFrame != null)
         dDisplayFrame.getContentPane().removeAll();

      graphPanel = FunctionViewComponent.ShowGraphWithAxes(x_values, y_values, errors, 
               Title, x_units, y_units, x_label, y_label);
      
      if (dDisplayFrame != null)
         dDisplayFrame.add(graphPanel);
   }
   
   /**
    * Send a message to the messagecenter
    * 
    * @param command
    * @param value
    */
   private void sendMessage(String command, Object value)
   {
      Message message = new Message(command, value, true);
      
      messageCenter.receive(message);
   }

   /**
    * Receive messages to display the frame, hide the frame,
    * get the xy values, and set the values/create the graph.
    */
   public boolean receive(Message message)
   {
      if (message.getName().equals(Commands.SHOW_D_GRAPH))
      {
         displayDFrame();
         
         return true;
      }
      
      if (message.getName().equals(Commands.ADD_EVENTS_TO_VIEW))
      {
         sendMessage(Commands.GET_D_VALUES, null);
      }
      
      if (message.getName().equals(Commands.HIDE_D_GRAPH))
      {
         dDisplayFrame.dispose();
         
         return true;
      }
      
      if (message.getName().equals(Commands.SET_D_VALUES))
      {
         setPanelInformation(((float[][])message.getValue()));
         
         if (dDisplayFrame != null)
            dDisplayFrame.validate();
         
         return true;
      }
      
      return false;
   }
}