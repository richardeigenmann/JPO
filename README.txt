Welcome to the source directory for JPO, the Java Picture Organizer

The source is distributed under the GPL licence. See src/jpo/gpl.txt

The project homepage is:  http://j-po.sourceforge.net

The source directory follows the recommendations laid out by Jacob Dreyer
at http://geosoft.no  Please see his great homepage for details about how
all the makefiles work.



1. How to compile JPO
---------------------

First download the source files and unzip them to a suitable directory.

Download and install the Java Software Development Kit from Sun or IBM 
(or any other >= 1.4.0 JVM)

Make sure you installed Java Web Start. On Windows this is pretty much 
automatic on Linux you must unzip a file.

Generate a key with which you can sign the jar files

	keytool -genkey -keystore myKeystore -alias myself
	
See the Java Web Start developers guide for details: 
http://java.sun.com/products/javawebstart/docs/developersguide.html



Then set up your environment. I have a script that I source:

	source jpoenv
	
It does the following:

	#!/bin/sh  
	JAVA_HOME=/opt/IBMJava2-14
	JAVA_DEV_ROOT=/path_to_my_sources/Jpo 
	IS_UNIX=true 
	JAVA_KEY_STORE=/path_to_my_keystore/javaKeyStore
	JAVA_KEY_STORE_KEY=my_secret_key

	export JAVA_HOME 
	export JAVA_DEV_ROOT 
	export IS_UNIX
	export JAVA_KEY_STORE
	export JAVA_KEY_STORE_KEY

	cd /path_to_my_sources/Jpo/src/jpo


I have changed to the apache-ant build tools on 4.5.2004. Csaba Nagy kindly helped put together
the build.xml for me. So to get you going:

1. Install apache-ant on your machine
2. cd to the main directory i.e. /Download/Jpo  
3. run any of the following commands:
	ant compile     - compiles everything requiring a compilation. The classes are put in 
			  build/classes   by their package (which is jpo)
	ant run		- runs the application from build/classes
	ant clean	- deletes the compiled classes
	ant rebuild	- runc clean and compile
	ant document	- creates the javadoc in the directory
			  build/docs/index.html
	ant go          - compiles and runs the application
	
	
I always work in the src/jpo subdirectory of the main Jpo directory on my Linux machine.

From there use the following commands:

	make 		- compiles everything requiring a compilation
	make run	- runs JPO directly from the classes
	make jar	- creates and signs the jar file
	make runjar	- runs JPO from the jar files
	make clean	- deletes all precompiled stuff
	make javadoc	- creates the javadoc documentation

	
You can concatenate some commands which can make sense. I frequently use:

	make && make run
	
This compiles the modified sources and only if there were no compile errors starts the application.
	
	


Have fun

Richard Eigenmann
14.1.2003


