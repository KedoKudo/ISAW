/*
 * File:  ActivatePrompt.java 
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
 *           Intense Pulsed Neutron Source
 *           Argonne National Laboratory
 *           Argonne, IL 60439-4845
 *           USA
 *
 * This work was supported by the Intense Pulsed Neutron Source Division
 * of Argonne National Laboratory, Argonne, IL 60439-4845, USA.
 *
 * For further information, see <http://www.pns.anl.gov/ISAW/>
 *
 * Modified:	
 *
 * $Log$
 * Revision 1.4  2002/10/29 16:00:23  dennis
 * Added getDocumentation method, and $Log:$ tag. (Mike Miller)
 *
 *
 */
package Operators.Calculator;

import DataSetTools.operator.Parameter;
import DataSetTools.operator.Generic.Calculator.*;
import DataSetTools.dataset.*;
import java.util.*;
import DataSetTools.materials.*;
import java.text.DecimalFormat;
/** 
 *  
 */
public class ActivatePrompt extends GenericCalculator
{
  private static final String TITLE = "Activate Prompt Activity";

 /* ------------------------ Default constructor ------------------------- */ 
 /**
  *  Creates operator with title "ActivatePrompt" and a default list
  *  of parameters.
  */  
  public ActivatePrompt()
  {
    super( TITLE );
  }

 /* ---------------------------- Constructor ----------------------------- */ 
 /** 
  *  Creates operator with title "ActivateStorage" and the specified
  *  list of parameters.  The getResult method must still be used to
  *  execute the operator.
  *
  *  @param  sample      Sample material composition
  *  @param  current     Facility beam current
  *  @param  inst_fac    Instrument factor
  */
  public ActivatePrompt( String sample ){//,
      this(); 
      parameters = new Vector();
      addParameter( new Parameter("Sample Composition", new String(sample) ) );
  }

 /* ---------------------------getDocumentation--------------------------- */
 /**
  *  Returns description/attributes of ActivatePrompt
  *   for a user activating the Help System
  */
  public String getDocumentation()
  {
    StringBuffer Res = new StringBuffer();
    Res.append("@overview This operator calculates and returns the ");
    Res.append("prompt activity (nCi/g) for a given sample\n");
    Res.append("@algorithm Given a sample of type Material ");
    Res.append("the prompt activity will be calculated\n");
    Res.append("@param String sample\n");
    Res.append("@return the String containing the numerical value ");
    Res.append("of the prompt activity followed by units (nCi/g)\n"); 
    Res.append("@error sample string is null, no set\n");
    Res.append("@error sample not valid\n");
    
    return Res.toString();
    
  }


 /* ---------------------------- getCommand ------------------------------- */ 
 /** 
  * Get the name of this operator to use in scripts
  * 
  * @return  "ActivatePrompt", the command used to invoke this 
  *           operator in Scripts
  */
  public String getCommand()
  {
    return "ActivatePrompt";
  }
  
 /* ------------------------ setDefaultParameters ------------------------- */ 
 /** 
  * Sets default values for the parameters. During testing the
  * parameters are set for SEPD at IPNS.
  */
  public void setDefaultParameters()
  {
    parameters = new Vector();
    addParameter(new Parameter("Sample Composition", new String("La,Mn,O_3")));
  }

 /* ----------------------------- getResult ------------------------------ */ 
 /** 
  *  Executes this operator using the values of the current parameters.
  *
  *  @return If successful, this operator returns the prompt activity
  *  of the activated sample.
  */
  public Object getResult(){
	String sample   = (String)(getParameter(0).getValue());
	String rs=null;

	if(sample==null){
	    return "no sample";
	}

	// get the material from the sample string
	Material material = new Material(sample);
	if(material.numAtoms()<=0){
	    return "invalid sample: "+sample;
	}

	// calculate the prompt activity
	float prompt=0.0f;
	for( int i=0 ; i<material.numAtoms() ; i++ ){
	    prompt=prompt+material.promptAct(i)*material.massFrac(i);
	}

	// format the result
	if(prompt>0.0f){
	    if(prompt==Float.POSITIVE_INFINITY){
		rs="radioactive sample";
	    }else{
		rs=new String((new DecimalFormat("#######0.")).format(prompt)
			      +" nCi/g");
	    }
	}else{
	    rs="no activation";
	}

	//rs=rs+" ["+material.toString()+"]";
	return rs;
  }

 /* ------------------------------- clone -------------------------------- */ 
 /** 
  *  Creates a clone of this operator.
  */
  public Object clone()
  { 
    ActivatePrompt op = new ActivatePrompt();
    op.CopyParametersFrom( this );
    return op;
  }

 /* ------------------------------- main --------------------------------- */ 
 /** 
  * Test program to verify that this will complile and run ok.  
  *
  */
  public static void main( String args[] )
  {
     System.out.println("Test of ActivatePrompt starting...");
     String material=null;

     // Test the operator by constructing and running it, specifying
     // values for all of the parameters.
     material="Ge";
     ActivatePrompt op = new ActivatePrompt( material );
     String output = (String)op.getResult();
     System.out.println("Using "+material+", the operator returned: ");
     System.out.println( output );

     // Test the operator by constructing and running it, specifying
     // values for all of the parameters.
     material="Si,Ge";
     op = new ActivatePrompt( material );
     output = (String)op.getResult();
     System.out.println("Using "+material+", the operator returned: ");
     System.out.println( output );

     // Test the operator by constructing and running it, specifying
     // values for all of the parameters.
     material="Y,Ba_2,Cu_3,O_7";
     op = new ActivatePrompt( material );
     output = (String)op.getResult();
     System.out.println("Using "+material+", the operator returned: ");
     System.out.println( output );

     // Will dump raw help information about the ActivatePrompt class to screen
     System.out.println( op.getDocumentation() );

     // Test the operator by constructing and running it, this time with the
     // default constructor.
     /* op = new ActivateStorage();
	obj = op.getResult();
	System.out.println("Using default parameters, the operator returned: ");
	System.out.println( (String)obj ); */

     System.out.println("Test of ActivateStorage done.");
  }
}
