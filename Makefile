#*********************************************************************
#
#  (C) 2000 Geotecnical Software Services - GeoSoft
#
#*********************************************************************

PACKAGES = \
	jpo 


NODOC_PACKAGES = \ 

JARS = \
	jpo-0.8.4.jar \

JARS_3RDPARTY = jnlp.jar \
	metadata-extractor-2.2.jar \
	jnlp.jar \

MAIN_CLASS     = Jpo
MAIN_PACKAGE   = jpo
MAIN_JAR       = jpo-0.8.4.jar

RUN_PARAMETERS = -Xms100M -Xmx500M
#RUN_PARAMETERS =  -Xms200000000

#*********************************************************************
#
# Javadoc
#
#*********************************************************************

WINDOWTITLE = 'JPO application documentation'
DOCTITLE    = 'JPO application documentation'
HEADER      = 'JPO application documentation'
BOTTOM      = '<font size="-1">Copyright &copy; 2002 - 2004 by Richard Eigenmann <a href="http://ourworld.compuserve.com/richard_eigenmann">ourworld.compuserve.com/richard_eigenmann</a></font>' 
OVERVIEW    = src/docsrc/Overview.html
DOC_IMAGES  = src/docsrc

include $(JAVA_DEV_ROOT)/make/Makefile


# command to make src zipfiles:
# cd to parent of Jpo directory
# zip  -9 -r jpo-src-0.8.4 Jpo -x \*.zip  \*.class

# command to put the files on sourceforge
# scp ../../jars/jpo-0.8.4.jar richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
