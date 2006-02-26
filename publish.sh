ant signjar
rsync -vz -e "ssh -l richieigenmann"  --stats --progress web/jpo-0.8.5.jnlp richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress jars/jpo-0.8.5.jar richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress libs/metadata-extractor-2.3.0.jar richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress web/index.html richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress web/jnlp.jnlp richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress libs/jnlp.jar richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress web/jpo-devel.jnlp richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress web/activation.jnlp richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress libs/activation.jar richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress web/mail.jnlp richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress libs/mail.jar richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
rsync -vz -e "ssh -l richieigenmann"  --stats --progress web/Jpo.bat richieigenmann@shell.sourceforge.net:/home/groups/j/j-/j-po/htdocs
