/*
 * File: JDataTreeRingmaster.java
 *
 * Copyright (C) 2001, Kevin Neff
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
 * Contact : Alok Chatterjee <achatterjee@anl.gov>
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
 * $Log$
 * Revision 1.18  2005/02/07 23:02:05  dennis
 * Replaced highly inefficient sequence that cloned a DataSet
 * then removed all data entries with single call to get an
 * empty clone of the DataSet.  This occurred in a method
 * that is not currently used.
 *
 * Revision 1.17  2004/07/15 14:38:15  kramer
 *
 * Now when the user highlights a group of DataMutableTreeNodes, right clicks,
 * and selects clear, only the highlighted nodes are cleared, not the entire
 * tree.
 *
 * Revision 1.16  2004/03/15 03:31:25  dennis
 * Moved view components, math and utils to new source tree
 * gov.anl.ipns.*
 *
 * Revision 1.15  2003/12/16 00:00:49  bouzekc
 * Removed unused imports.
 *
 * Revision 1.14  2003/03/28 19:24:47  pfpeterson
 * Removed a debug statement.
 *
 * Revision 1.13  2002/11/27 23:27:07  pfpeterson
 * standardized header
 *
 * Revision 1.12  2002/11/25 20:29:51  pfpeterson
 * Moved the package definition to be below the GPL.
 *
 */

package IsawGUI;

import gov.anl.ipns.Util.Messaging.IObserver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.text.Document;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import DataSetTools.components.ui.OperatorMenu;
import DataSetTools.components.ui.ViewMenu;
import DataSetTools.dataset.Data;
import DataSetTools.dataset.DataSet;
import DataSetTools.operator.Operator;
import DataSetTools.viewer.ViewManager;


/**
 * encapsulates all event handling for a JDataTree.  generates
 * appropriate right-click menus for selections and handles the
 * events generated by the menus.  this includes selecting and
 * clearing selection of Data and DataSet objects, deleting tree
 * nodes, applying operators to DataSet objects, creating views
 * of DataSet objects, and all of the other actions that are
 * associated with the generated right-click menu.
 */
public class JDataTreeRingmaster
{

  private final String MENU_SELECT    = "Select";
  private final String MENU_CLEAR     = "Clear";
  private final String MENU_CLEAR_ALL = "Clear All";
  private final String MENU_DELETE    = "Delete";
  private final String MENU_VIEW      = "View";

  private final String MENU_SEND              = "Send To";
  private final String SEND_TO_NEW_EXPERIMENT = "New Experiment";
  private final String SEND_TO_OLD_EXPERIMENT = "Experiment: ";

                    //the container of all of the data that this
                    //object uses.
  JDataTree tree;           

                    //when operations are invoked that generate a 
                    //new DataSet object, this is the object that
                    //will be updated via the IObserver.update(...)
                    //mechanism by passing the DataSet object as 
                    //the 'reason'.
  IObserver new_ds_observer;

  Document sessionLog = null;


  /**
   *
   */
  public JDataTreeRingmaster( JDataTree tree, 
                              IObserver new_ds_observer )
  {
    this.tree = tree;
    this.new_ds_observer = new_ds_observer;
  }


  /**
   *
   */ 
  public JDataTreeRingmaster( JDataTree tree, 
                              IObserver new_ds_observer,
                              Document log )
  {
    this.tree = tree;
    this.new_ds_observer = new_ds_observer;
    sessionLog = log;
  }


  /**
   * decides what kind of menu should be generated.  there are five (5)
   * types of menus that can be generated:
   *
   *   0) single Data object selection
   *   1) multiple Data object selection
   *   2) single DataSet object selection
   *   3) multiple DataSet object selection
   *   4) runfile selection
   *
   * all selected nodes of unknown types are ignored.
   */
  public void generatePopupMenu( TreePath[] tps, MouseEvent e )
  {
    MutableTreeNode node = null;
    if(  tps.length > 0  )
      node = (MutableTreeNode)(  tps[0].getLastPathComponent()  );


                                              //generates a popup menu
                                              //for Experiment object
                                              //selections
    if( node instanceof Experiment )
    {
      Experiment exp = (Experiment)node;
      if( exp.toString() != JDataTree.MODIFIED_NODE_TITLE )
        ExperimentPopupMenu( tps, e );
      
      return;
    }

                                              //generates a popup menu
                                              //for single Data object
                                              //selections
    else if(   node instanceof DataMutableTreeNode  &&  tps.length == 1   )
      SingleDataBlockPopupMenu( tps, e );

                                              //generates a popup menu
                                              //for single DataSet object
                                              //selections
    else if(  node instanceof DataSetMutableTreeNode  &&  tps.length == 1  )
      SingleDataSetPopupMenu( tps, e );


                                               //generates a popup menu
                                               //for multiple Data object
                                               //selections
    else if(  node instanceof DataMutableTreeNode  )
      MultipleDataBlockPopupMenu( tps, e );


    else if(  node instanceof DataSetMutableTreeNode  )
      MultipleDataSetPopupMenu( tps, e );

    else 
    {
//      System.out.println( "type not appropriate for actionMenu" );
    }
  }


  /**
   * creates a popup menu that is appropriate for a single
   * Data object when the user right-clicks on it.
   */
  public void SingleDataBlockPopupMenu( TreePath[] tps, MouseEvent e )
  {

    class SingleDataBlockMenuItemListener implements ActionListener
    {
      TreePath[] tps;

      public SingleDataBlockMenuItemListener( TreePath[] tps_ )
      {
        tps = tps_;
      }

      /*
       * trap mouse events for the right-click menus.
       */
      public void actionPerformed( ActionEvent item_e )
      {
        if(  item_e.getActionCommand() == MENU_SELECT  )
          tree.selectNodesWithPaths( tps );

        else if(  item_e.getActionCommand() == MENU_CLEAR  )
        {
          DataMutableTreeNode node = (DataMutableTreeNode)(  tps[0].getLastPathComponent()  );
          Data d = node.getUserObject();
          d.setSelected( false );

                                    //find the DataSet that these Data objects
                                    //belong to and have it notify its IObservers
          DataSet ds = tree.getDataSet( node );
          ds.notifyIObservers( IObserver.SELECTION_CHANGED );
        }

        else if(  item_e.getActionCommand() == MENU_CLEAR_ALL  )
          tree.clearSelections();

        else if(  item_e.getActionCommand() == MENU_DELETE  )
        {
          DataMutableTreeNode node = (DataMutableTreeNode)(  tps[0].getLastPathComponent()  );
          tree.deleteNode( node, true );
        }
      }
    }

    SingleDataBlockMenuItemListener item_listener = null;
    item_listener = new SingleDataBlockMenuItemListener( tps );

    JSeparator separator = new JSeparator();
    JMenuItem select_item = new JMenuItem( MENU_SELECT );
              select_item.addActionListener( item_listener );
    JMenuItem clear_item = new JMenuItem( MENU_CLEAR );
              clear_item.addActionListener( item_listener );
    JMenuItem clear_all_item = new JMenuItem( MENU_CLEAR_ALL );
              clear_all_item.addActionListener( item_listener );
    JMenuItem delete_item = new JMenuItem( MENU_DELETE );
              delete_item.addActionListener( item_listener );
    JPopupMenu popup_menu = new JPopupMenu( "SingleDataBlockPopupMenu" );
               popup_menu.add( select_item );
               popup_menu.add( separator );
               popup_menu.add( clear_item );
               popup_menu.add( clear_all_item );
               popup_menu.add( delete_item );
               popup_menu.show(  e.getComponent(), e.getX(), e.getY()  );
  }


  /**
   * creates a popup menu that is appropriate for multiple
   * Data objects when the user right-clicks on the highlighted
   * items.
   */
  public void MultipleDataBlockPopupMenu( TreePath[] tps, MouseEvent e )
  {
    class MultipleDataBlockMenuItemListener
      implements ActionListener
    {
      TreePath[] tps;

      public MultipleDataBlockMenuItemListener( TreePath[] tps )
      {
        this.tps = tps;
      }

      public void actionPerformed( ActionEvent item_e )
      {
        if(  item_e.getActionCommand() == MENU_SELECT  )
          tree.selectNodesWithPaths( tps );

        else if(  item_e.getActionCommand() == MENU_CLEAR  )
        {
           DataMutableTreeNode node = null;
           Data d = null;
           DataSet ds = null;
           LinkedList list = new LinkedList();
           for (int i=0; i<tps.length; i++)
           {
              node = (DataMutableTreeNode)(  tps[i].getLastPathComponent()  );
              d = node.getUserObject();
              d.setSelected( false );

                                     //find the DataSet that these Data objects
                                     //belong to and have it notify its IObservers
              ds = tree.getDataSet( node );
              if (!list.contains(ds))
                 list.add(ds);
           }
           
           for (int i=0; i<list.size(); i++)
              ((DataSet)list.get(i)).notifyIObservers( IObserver.SELECTION_CHANGED );
         }
        else if(  item_e.getActionCommand() == MENU_CLEAR_ALL  )
          tree.clearSelections();

        else if(  item_e.getActionCommand() == MENU_DELETE  )
          tree.deleteNodesWithPaths( tps );
      }
    } 
                                       //create a view sub-menu
    JMenu view_popup_menu = new JMenu( MENU_VIEW );
    ViewMenu view_menu_maker = new ViewMenu();
    view_menu_maker.build( view_popup_menu, null, new IsawViewMenuListener() );
    view_popup_menu.setPopupMenuVisible( true );

    MultipleDataBlockMenuItemListener item_listener = null;
    item_listener = new MultipleDataBlockMenuItemListener( tps );

    JSeparator separator = new JSeparator();
    JMenuItem select_item = new JMenuItem( MENU_SELECT );
              select_item.addActionListener( item_listener );
    JMenuItem clear_item = new JMenuItem( MENU_CLEAR );
              clear_item.addActionListener( item_listener );
    JMenuItem clear_all_item = new JMenuItem( MENU_CLEAR_ALL );
              clear_all_item.addActionListener( item_listener );
    JMenuItem delete_item = new JMenuItem( MENU_DELETE );
              delete_item.addActionListener( item_listener );
    JPopupMenu popup_menu = new JPopupMenu( "MultipleDataBlockPopupMenu" );
               popup_menu.add( select_item );
               popup_menu.add( separator );
               popup_menu.add( clear_item );
               popup_menu.add( clear_all_item );
               popup_menu.add( delete_item );
               popup_menu.add( view_popup_menu );
               popup_menu.show(  e.getComponent(), e.getX(), e.getY()  );
  }


  /**
   * creates a popup menu that is appropriate for a single
   * DataSet object when the user right-clicks on highlighted
   * items.
   */
  public void SingleDataSetPopupMenu( TreePath[] tps, MouseEvent e )
  {
    DataSetMutableTreeNode node = (DataSetMutableTreeNode)(  tps[0].getLastPathComponent()  );
    DataSet ds = node.getUserObject();

                                        //create a sub-menu to show
                                        //operator that can be applied
                                        //to this particular DataSet object
    int num_ops = ds.getNum_operators();
    Operator ds_ops[] = new Operator[num_ops];
    for ( int i = 0; i < num_ops; i++ )
      ds_ops[i] = ds.getOperator(i);

    DataSet[] dss = new DataSet[1];
    dss[0] = ds;
    JMenu ops_popup_menu = new JMenu( "Operations" );
    OperatorMenu om = new OperatorMenu();
    JOperationsMenuHandler popup_listener = new JOperationsMenuHandler( dss, 
                                                                        false, 
                                                                        tree, 
                                                                        new_ds_observer,
                                                                        sessionLog );
    om.build( ops_popup_menu, ds_ops, popup_listener );
    ops_popup_menu.setPopupMenuVisible( true );

                                        //create a sub-menu to show
                                        //viewer options
    JMenu view_popup_menu = new JMenu( MENU_VIEW );
    ViewMenu view_menu_maker = new ViewMenu();
    view_menu_maker.build( view_popup_menu, null, new IsawViewMenuListener() );
    view_popup_menu.setPopupMenuVisible( true );

    /*
     * listens to the right-click menu 
     */
    class singleDataSetMenuItemListener implements ActionListener
    {
      TreePath[] tps;
 
      public singleDataSetMenuItemListener( TreePath[] tps_ )
      {  
        tps = tps_;  
      }

      public void actionPerformed( ActionEvent item_e )
      {
        if(  item_e.getActionCommand() == MENU_SELECT  )
          tree.selectNodesWithPaths( tps );

        else if(  item_e.getActionCommand() == MENU_DELETE  )
          tree.deleteNodesWithPaths( tps );

      }
    }


    JMenu send_popup_menu = new JMenu( MENU_SEND );
    send_popup_menu.setPopupMenuVisible( true );
    DataSetSendMenuItemListener send_listener = new DataSetSendMenuItemListener( tps );

    JMenuItem new_experiment = new JMenuItem( SEND_TO_NEW_EXPERIMENT );
    new_experiment.addActionListener( send_listener );

                                      //get all of the Experiment objects
                                      //currently in the tree and make them
                                      //options to add the currently
                                      //selected DataSet object to.
    Experiment[] exps = tree.getExperiments();
    JMenuItem[]  menu_items = new JMenuItem[ exps.length ];
    for( int i=0;  i< exps.length;  i++ )
    {
      String menu_title = new String(  SEND_TO_OLD_EXPERIMENT + exps[i].toString()  );
      menu_items[i] = new JMenuItem( menu_title );
      menu_items[i].addActionListener( send_listener );
    }

    send_popup_menu.add( new_experiment );
    for( int i=0;  i<menu_items.length;  i++ )
      send_popup_menu.add( menu_items[i] );

    singleDataSetMenuItemListener item_listener = new singleDataSetMenuItemListener( tps );
    JSeparator separator = new JSeparator();
    JMenuItem select_item = new JMenuItem( MENU_SELECT );
              select_item.addActionListener( item_listener );
    JMenuItem delete_item = new JMenuItem( MENU_DELETE );
              delete_item.addActionListener( item_listener );

    JPopupMenu popup_menu = new JPopupMenu( "SingleDataSetPopupMenu" );
    popup_menu.add( select_item );
    popup_menu.add( separator );
    popup_menu.add( delete_item );
    popup_menu.add( ops_popup_menu );
    popup_menu.add( view_popup_menu );
    popup_menu.add( send_popup_menu );
    popup_menu.show(  e.getComponent(), e.getX(), e.getY()  );
  }


  /**
   * creates a popup menu that is appropriate for multiple
   * DataSet object selection (when the user right-clicks
   * on a number of highlighted DataSet nodes)
   */
  public void MultipleDataSetPopupMenu( TreePath[] tps, MouseEvent e )
  {
    DataSetMutableTreeNode node = (DataSetMutableTreeNode)(  tps[0].getLastPathComponent()  );
    DataSet ds = node.getUserObject();

    int num_ops = ds.getNum_operators();                //create a sub-menu
    Operator ds_ops[] = new Operator[num_ops];          //for the current
    for( int i = 0; i < num_ops; i++ )                  //DataSet objects
      ds_ops[i] = ds.getOperator(i);

                               //get the DataSet objects to which we will 
                               //apply the operator.  we'll assume that all
                               //of the TreePath objects have been filtered
                               //and that they are all DataSetMutableTreeNodes.
                               //karma--
    //TODO: use select w/ paths and getselected to do this (in JDataTree)
    DataSet[] dss = new DataSet[ tps.length ];
    for( int i=0;  i<tps.length;  i++ )
      dss[i] = (DataSet)(  ( (DataSetMutableTreeNode)tps[i].getLastPathComponent() ).getUserObject()  );

                               //create the actual menu
    JMenu ops_popup_menu = new JMenu( "Operations" );
    OperatorMenu om = new OperatorMenu();
    JOperationsMenuHandler popup_listener = new JOperationsMenuHandler( dss,
                                                                        true,
                                                                        tree, 
                                                                        new_ds_observer,
                                                                        sessionLog );
    om.build( ops_popup_menu, ds_ops, popup_listener );
    ops_popup_menu.setPopupMenuVisible( true );


                                   //create a view sub-menu
    JMenu view_popup_menu = new JMenu( MENU_VIEW );
    ViewMenu view_menu_maker = new ViewMenu();
    view_menu_maker.build( view_popup_menu, dss, new IsawViewMenuListener() );
    view_popup_menu.setPopupMenuVisible( true );

                                  //create a send sub-menu
    JMenu send_popup_menu = new JMenu( MENU_SEND );
    send_popup_menu.setPopupMenuVisible( true );
    DataSetSendMenuItemListener send_listener = new DataSetSendMenuItemListener( tps );

    JMenuItem new_experiment = new JMenuItem( SEND_TO_NEW_EXPERIMENT );
    new_experiment.addActionListener( send_listener );

                                      //get all of the Experiment objects
                                      //currently in the tree and make them
                                      //options to add the currently
                                      //selected DataSet object to.
    Experiment[] exps = tree.getExperiments();
    JMenuItem[]  menu_items = new JMenuItem[ exps.length ];
    for( int i=0;  i< exps.length;  i++ )
    {
      String menu_title = new String(  SEND_TO_OLD_EXPERIMENT + exps[i].toString()  );
      menu_items[i] = new JMenuItem( menu_title );
      menu_items[i].addActionListener( send_listener );
    }

    send_popup_menu.add( new_experiment );
    for( int i=0;  i<menu_items.length;  i++ )
      send_popup_menu.add( menu_items[i] );

    class MultipleDataSetMenuItemListener implements ActionListener
    {
      TreePath[] tps;
 
      public MultipleDataSetMenuItemListener( TreePath[] tps_ )
      {
        tps = tps_;
      }


      public void actionPerformed( ActionEvent item_e )
      {
        if(  item_e.getActionCommand() == MENU_SELECT  )
          tree.selectNodesWithPaths( tps );

        if(  item_e.getActionCommand() == MENU_DELETE  )
          tree.deleteNodesWithPaths( tps );
      }
    }


    MultipleDataSetMenuItemListener item_listener = new MultipleDataSetMenuItemListener( tps );
    JSeparator separator = new JSeparator();
    JMenuItem select_item = new JMenuItem( MENU_SELECT );
              select_item.addActionListener( item_listener );
    JMenuItem delete_item = new JMenuItem( MENU_DELETE );
              delete_item.addActionListener( item_listener );
    JPopupMenu popup_menu = new JPopupMenu( "MultipleDataSetPopupMenu" );
               popup_menu.add( select_item );
               popup_menu.add( separator );
               popup_menu.add( delete_item );
               popup_menu.add( ops_popup_menu );
               popup_menu.add( view_popup_menu );
               popup_menu.add( send_popup_menu );
               popup_menu.show(  e.getComponent(), e.getX(), e.getY()  );
  }


  /**
   * creates a popup menu that is appropriate for a single
   * Runfile selection (when the user right-clicks on highlighted
   * runfile)
   */
  public void ExperimentPopupMenu( TreePath[] tps, MouseEvent e )
  {
    class ExperimentMenuItemListener implements ActionListener
    {
      TreePath[] tps;

      public ExperimentMenuItemListener( TreePath[] tps )
      {
        this.tps = tps;
      }


      public void actionPerformed( ActionEvent item_e )
      {
        if(  item_e.getActionCommand() == MENU_SELECT  )
          tree.selectNodesWithPaths( tps );

        if(  item_e.getActionCommand() == MENU_DELETE  )
          tree.deleteNodesWithPaths( tps );
      }
    }


    ExperimentMenuItemListener item_listener = new ExperimentMenuItemListener( tps );
    JSeparator separator = new JSeparator();
    JMenuItem select_item = new JMenuItem( MENU_SELECT );
              //select_item.setMnemonic( KeyEvent.VK_S );
              select_item.addActionListener( item_listener );
    JMenuItem delete_item = new JMenuItem( MENU_DELETE );
              //delete_item.setMnemonic( KeyEvent.VK_X );
              delete_item.addActionListener( item_listener );
    JPopupMenu popup_menu = new JPopupMenu( "SingleDataSetPopupMenu" );
               popup_menu.add( select_item );
               popup_menu.add( separator );
               popup_menu.add( delete_item );
               popup_menu.show(  e.getComponent(), e.getX(), e.getY()  );
  }



  /**
   * if 'tps' leads to a Data object, this method sets POINTED_AT
   * in that Data object's containing DataSet.
   */
  public void pointAtNode( TreePath tp )
  {
    MutableTreeNode node = (MutableTreeNode)tp.getLastPathComponent();

    if(  node instanceof DataMutableTreeNode  )
    {
      DataMutableTreeNode d_node = (DataMutableTreeNode)node;

      if( tree == null )
        System.out.println( "null tree" );

      DataSet ds = tree.getDataSet( d_node );
      ds.setPointedAtIndex(  ds.getIndex_of_data( d_node.getUserObject() )  );
      ds.notifyIObservers( IObserver.POINTED_AT_CHANGED );
    }
  }


  /**
   * listens to the view menu in ISAW
   */
  class IsawViewMenuListener implements ActionListener
  {
    public void actionPerformed( ActionEvent e )
    {
      if(  e.getActionCommand().equals( ViewManager.IMAGE )  )
      {
        TreePath[] tps = null;
        tps = tree.getSelectedNodePaths();
   
        MutableTreeNode node;
        for( int i=0;  i<tps.length;  i++ )
        {
          node = (MutableTreeNode)tps[i].getLastPathComponent();
          if( node instanceof DataSetMutableTreeNode )
          {
            DataSetMutableTreeNode ds_node = (DataSetMutableTreeNode)node;
            DataSet ds = ds_node.getUserObject();
            new ViewManager( ds, ViewManager.IMAGE );
          }
        }  
      }
      if(  e.getActionCommand().equals( ViewManager.SCROLLED_GRAPHS )  )
      {
        TreePath[] tps = null;
        tps = tree.getSelectedNodePaths();
   
        MutableTreeNode node;
        for( int i=0;  i<tps.length;  i++ )
        {
          node = (MutableTreeNode)tps[i].getLastPathComponent();
          if( node instanceof DataSetMutableTreeNode )
          {
            DataSetMutableTreeNode ds_node = (DataSetMutableTreeNode)node;
            DataSet ds = ds_node.getUserObject();
            new ViewManager( ds, ViewManager.SCROLLED_GRAPHS );
          }
        }  
      }
      if(  e.getActionCommand().equals( ViewManager.SELECTED_GRAPHS )   )
      {
        TreePath[] tps = null;
        tps = tree.getSelectedNodePaths();
   
        MutableTreeNode node;
        for( int i=0;  i<tps.length;  i++ )
        {
          node = (MutableTreeNode)tps[i].getLastPathComponent();
          if( node instanceof DataSetMutableTreeNode )
          {
            DataSetMutableTreeNode ds_node = (DataSetMutableTreeNode)node;
            DataSet ds = ds_node.getUserObject();
            new ViewManager( ds, ViewManager.SELECTED_GRAPHS );
          }
        }  
      }
      if(  e.getActionCommand().equals( ViewManager.THREE_D )  )
      {
        TreePath[] tps = null;
        tps = tree.getSelectedNodePaths();
   
        MutableTreeNode node;
        for( int i=0;  i<tps.length;  i++ )
        {
          node = (MutableTreeNode)tps[i].getLastPathComponent();
          if( node instanceof DataSetMutableTreeNode )
          {
            DataSetMutableTreeNode ds_node = (DataSetMutableTreeNode)node;
            DataSet ds = ds_node.getUserObject();
            new ViewManager( ds, ViewManager.THREE_D );
          }
        }  
      }
    }
  }


  /*
   * listens to "Send To" menu items.
   */ 
  class DataSetSendMenuItemListener implements ActionListener
  {
    TreePath[] tps;

    public DataSetSendMenuItemListener( TreePath[] tps )
    {
      this.tps = tps;
    }

    public void actionPerformed( ActionEvent item_e )
    {
      if(  item_e.getActionCommand() == SEND_TO_NEW_EXPERIMENT  )
      {
        String name = JOptionPane.showInputDialog( "Enter Experiment Name" );
        if( name == null )
          return;
          
        name = new String(  name.trim()  );
        if(  name.length() < 1  )
          return;

        DataSet[] listener_dss = new DataSet[ tps.length ];
        DataSetMutableTreeNode listener_node;
        for( int i=0;  i<tps.length;  i++ )
        {
          listener_node = (DataSetMutableTreeNode)tps[i].getLastPathComponent();
          listener_dss[i] = (DataSet)listener_node.getUserObject();
        }

        if(  !tree.addExperiment( listener_dss, name )  )
        {
          String msg = new String( "Experiment names must be unique" );
          System.out.println( msg );
        }
      }

                                     //if we're sending to an existing Experiment
                                     //object we have to find it first, then add
                                     //each DataSet that 'tps' points to.
      Experiment[] exps = tree.getExperiments();
      for( int i=0;  i<exps.length;  i++ )
      {
        String menu_title = new String(  SEND_TO_OLD_EXPERIMENT + exps[i].toString()  );
        if(  item_e.getActionCommand().equals( menu_title )  )
        {
//          System.out.println( "found: \"" + menu_title + "\"" );

          for(  int j=0;  j<tps.length;  j++ )
          {
            DataSetMutableTreeNode ds_node = (DataSetMutableTreeNode)tps[j].getLastPathComponent();
            DataSet listener_ds = (DataSet)ds_node.getUserObject();
            tree.addToExperiment( listener_ds, exps[i] );
          }
        }
      }
    }
  }


  protected DataSet[] gather_selected_Data( TreePath[] tps )
  {
                              //make sure we're only dealing 
                              //w/ DataSet objects.
    Vector ds_paths = new Vector();
    for( int i=0;  i<tps.length;  i++ )
      if(  tps[i].getLastPathComponent() instanceof DataSetMutableTreeNode  )
        ds_paths.addElement( tps[i] );

                              //if there are any Data objects
                              //selected, form them into DataSet
                              //objects, add them to the tree,
                              //and pass their TreePath's along.
    Vector new_dss_data = new Vector();
    Vector old_dss      = new Vector();  //the DataSet to clone
    for( int i=0;  i<tps.length;  i++ )
    {
                              //keep track of Data objects that
                              //will be included in a particular
                              //(new) DataSet object.
      Vector data = new Vector();
      if(  tps[i].getLastPathComponent() instanceof DataMutableTreeNode  )
      {
        int second_last = tps[i].getPathCount() - 2;
        DataSetMutableTreeNode ds_node = (DataSetMutableTreeNode)tps[i].getPathComponent( second_last );
        DataSet ds = ds_node.getUserObject();

        DataMutableTreeNode d_node = (DataMutableTreeNode)tps[i].getLastPathComponent();
        Data d = d_node.getUserObject();
 
                              //we've found a Data object, and
                              //since there are not any elements
                              //in 'new_dss_data', we can't possibly
                              //be creating a redundant DataSet object
        if( new_dss_data.size() == 0  )
        {
          data.addElement( d );
          new_dss_data.addElement( data );

                              //we'll need this later on to get the
                              //and attributes for the DataSet we
                              //are going to create
          old_dss.addElement( ds );
        }
                              //if 'new_dss_data' isn't empty,
                              //we have to make sure this Data
                              //object gets put into the correct
                              //(newly formed) DataSet object.
        else
        {
          for( int j=0;  j<old_dss.size();  j++ )

                              //cmp mem addr
            if( old_dss.get(j) == ds )
            {
              Vector data_vector = (Vector)new_dss_data.get(j);
              data_vector.addElement( d );
            }
        }
      }
    }

    DataSet[] new_dss = new DataSet[ old_dss.size() ];
    for( int i=0;  i<new_dss.length;  i++ )
    {
                              //make an empty clone of the DataSet object
                              //that these Data objects came from
                              //to make sure we get the right units
      DataSet ds = (DataSet)old_dss.get(i);
      new_dss[i] = (DataSet)ds.empty_clone();
      new_dss[i].clearSelections();

                              //add Data objects to the appropriate
                              //DataSet object
      Vector data_for_this_ds = (Vector)new_dss_data.get(i);
      for( int di=0;  di<data_for_this_ds.size();  di++ )
        new_dss[i].addData_entry(  (Data)data_for_this_ds.get( di )  );
    }

//    for(  int i=0;  i<new_dss.length;  i++ )
//      tree.addToModifiedExperiment( new_dss[i] );

    return new_dss;
  }

}
