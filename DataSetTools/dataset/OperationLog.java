/*

 * @(#)OperationLog.java     1.0  98/08/03  Dennis Mikkelson

 *

 */



package  DataSetTools.dataset;



import java.util.Vector;

import java.io.*;



public class OperationLog implements Serializable

{

  private Vector log;



  public OperationLog( )

  {

    this.log = new Vector();

  }



  public void addEntry( String str )

  {

    log.addElement( str ); 

  }



  public int numEntries( )

  {

    return log.size();

  }



  public String getEntryAt( int i )

  {

    return (String)(this.log.elementAt(i));

  }





  public String toString()           // for now just return first character

  {                                  // of each string in the log

    String  temp = new String( );



    for ( int i = 0; i < this.log.size(); i++ )

      temp = temp + ((String)this.log.elementAt( i )).charAt(0) + "\n";



    return temp;

  }



  public Object clone( )

  {

    OperationLog  new_log = new OperationLog();



    for ( int i = 0; i < this.numEntries(); i++ )

    {

      new_log.addEntry( this.getEntryAt(i) );

    }



    return new_log;

  }



  public static void main( String argv[] )

  {

    OperationLog test_log = new OperationLog();



    for ( int i = 0; i < 5; i++ )

      test_log.addEntry( i + " new entry, " );



    System.out.println( "test_log" );

    System.out.println( test_log );



    OperationLog new_log = (OperationLog) test_log.clone();

    new_log.addEntry( "Entry added to new log" );

    System.out.println( "new_log" );

    System.out.println( new_log );

  }

}

