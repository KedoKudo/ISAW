/*
 * File:  ScriptInterpretFetch.java 
 *             
 * Copyright (C) 2002, Ruth Mikkelson
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
 *           Menomonie, WI 54751, USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 *
 * Modified:
 *
 * $Log$
 * Revision 1.5  2004/06/17 18:28:58  rmikk
 * Uses a null Document in the constructor so no Python error occurs. The
 *   only error should be ClassNotFound when the Jython.jar is not present
 *
 * Revision 1.4  2003/10/10 01:20:04  bouzekc
 * Changed references from: ScriptProcessorOperator to IScriptProcessor,
 * ScriptProcessor to ScriptOperator, and pyScriptProcessor to
 * PyScriptOperator.
 *
 * Revision 1.3  2003/07/31 22:28:17  rmikk
 * Changed an Exception catch to a throwable catch
 *
 * Revision 1.2  2003/06/11 21:13:29  pfpeterson
 * More explicit about what is caught when trying to get the python
 * interpreter.
 *
 * Revision 1.1  2003/01/02 20:48:05  rmikk
 * Initial Checkin for the class with one method that returns the
 *    appropriate ScriptProcessor.  NOTE all reference to Jython
 *    can be removed from this class( and the method returns null).
 *    The ISAW system can then compile.
 *
 
*/
package Command;
import javax.swing.text.*;
import DataSetTools.operator.PyScriptOperator;

/** This class wraps the static method that fetches the appropriate ScriptProcessorOperator
*   relative to the extension of a filename.  If Jython is not installed, a null will be returned
*/
public class ScriptInterpretFetch
  {

   /**
   *   This class returns the appropriates script processor for the given
   *   filename extension.  The argument filename can be "*.py".
   *   @param  filename   The filename to be processed. It should have an extension
   *                      py or iss
   *   @return  the Jython interpreter if the extension is "py", ISAW's script 
   *                 interpreter if the extension is "iss", otherwise null
   */
   public static IScriptProcessor getScriptProcessor( String filename, Document doc)
     {
      if( filename == null)
        return null;
    
      if( filename.toUpperCase().endsWith( ".ISS" ))
         return new ScriptOperator( doc );
     
      if( !filename.toUpperCase().endsWith( ".PY" ))
         return null;
    
      try
        {
          return new PyScriptOperator( (Document)null ); 
        }
      catch( Throwable ss)
        {
          return null;
        }

     }


   }
