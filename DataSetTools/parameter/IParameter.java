/*
 * File:  IParameter.java 
 *
 * Copyright (C) 2002, Peter F. Peterson
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
 *           Intense Pulse Neutron Source Division
 *           Argonne National Laboratory
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
 *  Revision 1.1  2002/05/28 22:24:12  pfpeterson
 *  added to cvs
 *
 *
 */

package DataSetTools.parameter;

/**
 * This is an interface to be implemented by all parameters. Things
 * that need to use IParameters do not need a GUI constructed.
 */
public interface IParameter{
    /**
     * Returns the name of the parameter. This is normally used as the
     * title of the parameter.
     */
    String getName();

    /**
     * Set the name of the parameter.
     */
    void   setName(String name);

    /**
     * Returns the value of the parameter. While this is a generic
     * object specific parameters will return appropriate
     * objects. There can also be a 'fast access' method which returns
     * a specific object (such as Float or DataSet) without casting.
     */
    Object getValue();

    /**
     * Sets the value of the parameter.
     */
    void   setValue(Object value);

    /**
     * Returns whether or not the parameter is valid. Currently used
     * only by wizards.
     */
    boolean getValid();

    /**
     * Set the valid state of the parameter.
     */
    void   setValid(boolean valid);

    /**
     * Returns the string used in scripts to denote the particular
     * parameter.
     */
    String getType();
}
