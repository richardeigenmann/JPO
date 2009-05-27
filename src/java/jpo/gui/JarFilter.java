package jpo.gui;

import java.io.File;

/*
JarFilter.java:  filter that allows only selection of jar files

Copyright (C) 2002,2009  Richard Eigenmann.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed 
in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS 
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
*/


/** 
 *   This class overrides the abstract javax.swing.filechoose.FileFilter class
 *   (not the java.io.FileFilter) to provide a true or false indicator to
 *   using JFileChoosers whether the file is a directory or image 
 *   ending in .jar. 
 **/
public class JarFilter extends javax.swing.filechooser.FileFilter {

	
	/**
	 *  accepts directories and files ending in .jar
         *
         * @param file
         * @return 
         */
	public boolean accept(File file) {
		return file.isDirectory()
			|| file.getAbsolutePath().toUpperCase().endsWith( ".JAR" ) ;
	}

   
   
	/**
	 *   returns the description "JAR Files"
         *
         * @return
         */
	public String getDescription() {
        	return "JAR Files";
	}
}
