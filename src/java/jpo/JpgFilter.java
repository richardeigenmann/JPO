package jpo;

import java.io.*;


/*
JpgFilter.java:  filter to choose only jpg images

Copyright (C) 2002  Richard Eigenmann.
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
 *   ending in .jpg or .jpeg. 
 **/
public class JpgFilter extends javax.swing.filechooser.FileFilter {

	/**
	 *  accepts directories and files ending in .jpg, .jpeg, .tif, .tiff or .gif
	 **/   
	public boolean accept( File f ) {
		String lowercaseFilename = f.getAbsolutePath().toLowerCase();
		return f.isDirectory() 
			|| lowercaseFilename.endsWith( ".jpg" ) 
			|| lowercaseFilename.endsWith( ".jpeg" ) ; 
	}
	
	   
	/**
	 *   returns the description "JPEG Files"
	 */
	public String getDescription() {
        	return "JPEG Files";
	}

}
