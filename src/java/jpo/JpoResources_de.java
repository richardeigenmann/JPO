package jpo;

import java.util.*;

/*
JpoResources.java:  class that holds the generic labels for the JPO application

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
 *  class that holds the generic labels for the JPO application.
 *  Use the following command to access your Strings:
 *  Settings.jpoResources.getString("key")
 */
public class JpoResources_de extends ListResourceBundle {
	public Object[][] getContents() {
		return contents;

	}
	static final Object[][] contents = {
		// Jpo
		{"ApplicationTitle", "JPO - Java Picture Organizer"},  
		
	
		// Generic texts
         	{"genericTargetDirText", "Ziel Verzeichnis:"},
         	{"genericCancelText", "Abbruch"},
		{"genericSaveButtonLabel", "Speichern"},
         	{"genericOKText", "OK"},             
		{"genericSelectText", "Auswählen"},
		{"threeDotText", "..."},
		{"genericExportButtonText", "Export"},
		{"genericSecurityException", "Sicherheits-Ausnahme"},
		{"genericError", "Error"},
		{"internalError", "oder"},
		{"genericWarning", "Warnung"},
		{"genericExit", "Exit"},
		{"outOfMemoryError", "Ein \"Out of Memory Error\" wurde ausgeählt"},
		{"areYouSure", "Sind Sie sicher?"},
		
		
		
		// Help About Dialog
		{"HelpAboutText", "JPO Version 0.8.4 ist ein Java/Swing Programm\n" 
			+ "geschrieben von Richard Eigenmann, Zrich, Schweiz\n" 
			+ "Copyright 2000 - 2004\n"
			+ "richard_eigenmann@compuserve.com\n"
			+ "http://j-po.sourceforge.net\n"
			+ "\nDie Exif Extraktion wurde von Drew Noakes entwickelt\n"
			+  "Der Tablen Sortierer stammt von Philip Milne\n\n"},
		{"HelpAboutUser", "Benutzer: " },
		{"HelpAboutOs", "Betriebssystem: " },
		{"HelpAboutJvm", "JVM: " },
		{"HelpAboutJvmMemory", "JVM Max Speicher: " },
		{"HelpAboutJvmFreeMemory", "JVM Freier Speicher: " },


		// QueryJFrame
		{"searchDialogTitle", "Bilder Suchen"},
		{"searchDialogLabel", "Suche nach:"},
		{"searchDialogSaveResultsLabel", "Resultate Speichern"},
		{"advancedFindJButtonOpen", "Erweiterte Kriterien"},
		{"advancedFindJButtonClose", "Einfache Suche"},
		{"noSearchResults", "Es wurden keine Bilder zu den Kriterien gefunden."},
		{"lowerDateJLabel", "Zwischen:"},
		{"dateRangeError", "Entschuldigung, der Datatumsbereich macht keinen Sinn."},
		


		
		// PictureViewer Texts
		{"PictureViewerTitle", "JPO Bilder Betrachter"},
		{"PictureViewerKeycodes", "Die folgenden Tasten kähnen benutzt werden:\n" 
			+ "N: Nähhstes Bild\n"
			+ "P: Vorhergehendes Bild\n"
			+ "I: Informationen ein | aus\n"
			+ "<space>,<home>: Auf Vollbild zoomen\n"
			+ "<links>,<rechts>,<rauf>,<runter>: bild in Pfeilrichtung verschieben\n"
			+ "<PgUp>: Reinzoomen\n"
			+ "<PgDown>: Rauszoomen\n"
			+ "F: Fenstergrähse-Menu\n"
			+ "M: Popup Menu"},
		{"PictureViewerKeycodesTitle", "Tastatur-Abkrzungen"},
		{"NavigationPanel", "Navigations Werkzeuge"},
		{"fullScreenJButton.ToolTipText", "Vollbild"},
		{"popupMenuJButton.ToolTipText", "Popup Menu"},
		{"nextJButton.ToolTipText", "Nähhstes Bild"},
		{"previousJButton.ToolTipText", "Vorheriges Bild"},
		{"infoJButton.ToolTipText", "Informationen"},
		{"resetJButton.ToolTipText", "Reset"},
		{"clockJButton.ToolTipText", "Automatscher Bildwechsel"},
		{"closeJButton.ToolTipText", "Fenster schliessen"},

		// SettingsDialog Texts
		{"settingsDialogTitle", "Einstellungen"},
		
		{"browserWindowSettingsJPanel", "Allgemein"},
		{"languageJLabel", "Sprache:"},
		{"autoLoadJLabelLabel", "Automatisch laden:"},
		{"logfileJCheckBoxLabel", "Logdatei schreiben"},
		{"logfileJLabelLabel", "Pfad der Logdatei und Filenamen:"},
		{"saveSizeJCheckBoxLabel", "Fensterposition und Grähse bei Applikationsende speichern"},
		{"MainCoordinates", "Koordinaten des Hauptfensters (x/y):"},
		{"MainSize", "Grähse des Hauptfensters (b/h):"},
		
		{"pictureViewerJPanel", "Bilder Betrachter"},
		{"maximumPictureSizeLabel", "Maximale Bild Vergrähserung:"},
		{"maxCacheLabel", "Maximal zwischengespeichterte Bilder:"},
		{"leaveSpaceLabel", "Abstand nach unten:"},
		{"dontEnlargeJCheckBoxLabel", "Kleine Bilder nicht vergrähsern"},
		{"pictureCoordinates", "Default Koordinaten des Bilder Betrachter Fensters (x/y):"},
		{"pictureSize", "Default Grösse des Bilder Betrachter Fensters (b/h):"},
		{"pictureViewerFastScale", "Geschwindigkeit statt Qualitäh optimieren beim vergrähsen"},
		
		{"thumbnailSettingsJPanel", "Verkleinerungen"},
		{"thumbnailDirLabel", "Verzeichnis für die Verkleinerungen:"},
		{"keepThumbnailsJCheckBoxLabel", "Verkleinerungen auf Disk schreiben"},
		{"maxThumbnailsLabelText", "Maximale Anzahl Verkleinerungen pro Seite:"},
		{"thumbnailSizeLabel", "Grösse der Verkleinerungen:"},
		{"thumbnailFastScale", "Geschwindigkeit statt QualitÃ¤tähbeim verkleinern"},
		{"zapThumbnails", "Alle Verkleinerungen löschen"},
		{"thumbnailsDeleted", " Verkleinerungen gelöscht"},
		
		{"autoLoadChooserTitle", "Datei die automatich geladen wird"},
		{"logfileChooserTitle", "Logdatei wählen"},
		{"thumbDirChooserTitle", "Verzeichnis für Verkleinerungen wahlen"},
		
		{"settingsError", "Fehler der Einstellungen"},
		{"generalLogFileError", "Es besteht ein mit der Logdatei. Logmeldungen werden nicht geschrieben."},
		{"thumbnailDirError", "Etwas ist ernsthaft falsch mit dem Verkleinerungs-Verzeichnis"},

		{"userFunctionJPanel", "Benuterfunktionen"},
		{"userFunction1JLabel", "Benuterfunktion 1"},
		{"userFunction2JLabel", "Benuterfunktion 2"},
		{"userFunction3JLabel", "Benuterfunktion 3"},
		{"userFunctionNameJLabel", "Name:"},
		{"userFunctionCmdJLabel", "Kommando:"},
		{"userFunctionHelpJTextArea", "%f wird durch den Filenamen substituiert\n%u wird durch die URL des Bildes substituiert"},


		// Settings
		{"thumbNoExistError", "Das Verzeichnis fr die Verkleinerungen existiert nicht.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nZwischenspeicherung von Verkleinerungen wurde deaktiviert."},
		{"thumbNoWriteError", "Das Verzeichnis der Verkleinerungen erlaubt keine Schreibzugriffe.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nZwischenspeicherung von Verkleinerungen wurde deaktiviert."},
		{"thumbNoDirError", "Das Verzeichnis fr die Verkleinerungen ist kein Verzeichnis.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nZwischenspeicherung von Verkleinerungen wurde deaktiviert.."},
		{"logFileCanWriteError", "Die Logdatei erlaubt keine Schreibzugriffe.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nLogging wurde augeschaltet"},
		{"logFileIsFileError", "Die Logdatei der Einstellungen ist kein Datei.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nLogging wurde augeschaltet"},
		{"generalLogFileError", "Es besteht ein Problem mit der Logdatei. Logging wurde deaktiviert."},
		{"cantWriteIniFile", "Fehler beim Schreiben der ini Datei:\n"},
		{"cantReadIniFile", "Konnte JPO.ini nicht lesen. Grundeinstellungen werden verwendet.\n"},
		



		// HtmlDistillerJFrame
		{"HtmlDistillerJFrameHeading", "Export nach HTML"},
		{"HtmlDistillerThreadTitle", "Extraktion nach HTML"}, 
		{"HtmlDistillerChooserTitle", "Zielverzeichnis fr HTML"},
		{"exportHighresJCheckBox", "Originalbilder exportieren"},
		{"linkToHighresJCheckBox", "Link auf Originalbilder im aktuellen Verzeichnis erstellen"},
		{"generateDHTMLJCheckBox", "DHTML mouseover Effekte generieren"},
		{"picsPerRowText", "Spalten"},
		{"thubnailSizeJLabel", "Grösse der Verkleinerungen"},
		{"htmlDistCrtDirError", "Konnte das Export Verzeichnis nicht generieren!"},
		{"htmlDistIsDirError", "Das ist kein Verzeichnis!"},
		{"htmlDistCanWriteError", "Dies ist kein Beschreibbares Verzeichnis!"},
		{"htmlDistIsNotEmptyWarning", "Das Zielverzeichnis ist nicht leer.\nBestätigen sie, dass JPO fortfahren soll und allenfalls Dateien berschreibt."},
		{"midresSizeJLabel", "Grösse mittlere Auflösung"},
		{"jpgQualitySlider", "Jpg Qualität"},
		{"jpgQualityBad", "Gering"},
		{"jpgQualityGood", "Gut"},
		{"jpgQualityBest", "Beste"},
		
		// HtmlDistillerThread
		{"LinkToJpo", "Mit <A HREF=\"http://j-po.sourceforge.net\">JPO</A> erstellt"},
		{"htmlDistillerInterrupt", "saubere Unterbrechung"},
		{"CssCopyError", "Konnte das Stylesheet jpo.css nicht kopieren\n"},
			

		// ReconcileJFrame
		{"ReconcileJFrameTitle", "Verzeichnisabgleich gegenber der Sammlung"},
		{"ReconcileBlaBlaLabel", "<HTML>Diese Funktion berprft ob die Dateien im angegebenen Verzeichnis in der Sammlung vorhanden sind.</htm>"},
		{"directoryJLabelLabel", "Verzeichnis fr den Abgleich:"},
		{"directoryCheckerChooserTitle", "Verzeichnis fr den Abgleich"},
		{"ReconcileFound", " in Sammlung gefunden als "},
		{"ReconcileNotFound", "Nicht in der Sammlung: "},
		{"ReconcileDone", "Fertig.\n"},
		{"ReconcileInterrupted", "Unterbrochen.\n"},
		{"ReconcileListPositives", "List Positive matches"},
		{"ReconcileOkButtonLabel", "Abgleich"},
		{"ReconcileSubdirectories", "Unterverzeichnisse abgleichen"},
		{"ReconcileCantReadError", "Lesefehler: "},
		{"ReconcileNullFileError", "Ungltiges Verzeichnis"},
		{"ReconcileStart", "Abgleichsverzeichnis: "},
		{"ReconcileNoFiles", "Keine Dateien gefunden.\n"},

		
		// CollectionDistillerJFrame
		{"CollectionDistillerJFrameFrameHeading", "Export in eine neue Sammlung"},
		{"collectionExportPicturesText", "Bilder Exportieren"},
		{"xmlFileNameLabel", "Name fr die XML Datei:"},
		{"collectionExportChooserTitle", "Zielverzeichnis fr die Sammlung"},


		// ConsolidateGroupJFrame
		{"ConsolidateDirChooserTitle", "Konsolidierungsverzeichnis auswählen"},
		{"RecurseSubgroupsLabel", "Untergruppen einbeziehen"},
		{"ConsolidateGroupBlaBlaLabel", "<HTML>Diese Funktion wird all Bilder der ausgewählten Gruppe in das Zielverzeichnis verschieben. Es korrigiert Verweise<br>auf diese Bilder in dieser Sammlung. Die Dateien werden physisch verschoben.<br><p> <font color=red>Sind Sie sicher, dass sie dies wollen?<br></font></htm>"},
		{"ConsolidateGroupJFrameHeading", "Konsolidierung / Bilder Verschieben"},
		{"ConsolidateButton", "Konsolidieren"},
		{"ConsolidateFailure", "Konsolidierung generierte einen Fehler und wurde abgebrochen"},
		{"ConsolitdateProgBarTitle", "Konsolidierung läuft"},
		{"ConsolitdateProgBarDone", " Bilder konsolidiert"},
		{"lowresJCheckBox", "Auch Verkleinerungen konsolidieren"},
		

		
		// JarDistillerJFrame
		{"groupExportJarTitleText", "Export in ein Jar"},
		{"JarDistillerLabel", "Jar (Java Archive) zu erstellen:"},
		{"SelectJarFileTitle", "Zielverzeichnis fr Bilder"},
		
		// PictureInfoEditor
		{"PictureInfoEditorHeading", "Eigenschaften"},
		{"highresChooserTitle", "Originalbild auswählen"},
		{"pictureDescriptionLabel", "Bildbeschreibung:"},
		{"creationTimeLabel", "Erstellungsdatum und Zeit:"},
		{"highresLocationLabel", "Original-Pfad:"},
		{"lowresLocationLabel", "Verkleinerungs-Pfad:"},
		{"filmReferenceLabel", "Film Referenz:"},
		{"rotationLabel", "Rotation beim Laden:"},
		{"commentLabel", "Kommentar:"},
		{"copyrightHolderLabel", "Copyright Vermerk:"},
		{"photographerLabel", "Photograph:"},
		{"resetLabel", "Reset"},
		{"checksumJButton", "erneuern"},
		{"checksumJLabel", "Adler32 Checksum: "},
		{"parsedAs", "Entschlsselt als: "},
		{"failedToParse", "Kann nicht als Datum entschlsselt werden"},
		{"categoriesJLabel-2", "Kategorien:"},
		{"setupCategories", ">> Kategorien erstellen <<"},
		{"noCategories", ">> Keine <<"},

		
		//GroupInfoEditor
		{"GroupInfoEditorHeading", "Gruppenbeschreibung ändern"},
		{"groupDescriptionLabel", "Grouppenbeschreibung:"},
		
		// GroupPopupMenu
		{"groupSlideshowJMenuItemLabel", "Bilder anzeigen"},
		{"groupFindJMenuItemLabel", "Suchen"},
		{"groupEditJMenuItemLabel", "Umbenennen"},
		{"groupTableJMenuItemLabel", "Als Tablle bearbeiten"},
		{"addGroupJMenuLabel", "Hinzufgen"},
		{"addNewGroupJMenuItemLabel", "Neue Gruppe"},
		{"addPicturesJMenuItemLabel", "Bilder"},
		{"addCollectionJMenuItemLabel", "Sammlung"},
		{"groupExportNewCollectionMenuText", "Export als Sammlung"},
		{"addFlatFileJMenuItemLabel", "Einfache Datei"},
		{"moveNodeJMenuLabel", "Verschieben"},
		{"moveGroupToTopJMenuItem", "zuoberst"},
		{"moveGroupUpJMenuItem", "rauf"},
		{"moveGroupDownJMenuItem", "runter"},
		{"moveGroupToBottomJMenuItem", "zuunterst"},
		{"indentJMenuItem", "einrcken"},
		{"outdentJMenuItem", "ausrcken"},
		{"groupRemoveLabel", "Gruppe entfernen"},
		{"consolidateMoveLabel", "Konsolidieren/Verschieben"},
		{"sortJMenu", "Sortieren nach"},
		{"sortByDescriptionJMenuItem", "Beschreibung"},
		{"sortByFilmReferenceJMenuItem", "Film Referenz"},
		{"sortByCreationTimeJMenuItem", "Erstellungszeit"},
		{"sortByCommentJMenuItem", "Kommentar"},
		{"sortByPhotographerJMenuItem", "Photograph"},
		{"sortByCopyrightHolderTimeJMenuItem", "Copyright"},
		{"groupExportHtmlMenuText", "Export nach HTML"},
		{"groupExportFlatFileMenuText", "Export in Einfache Datei"},
		{"groupExportJarMenuText", "Export in Jar Archive"},
		
		
		// PicturePopupMenu
		{"pictureShowJMenuItemLabel", "Bild anzeigen"},
		{"pictureEditJMenuItemLabel", "Eigenschaften"},
		{"copyImageJMenuLabel", "Bild Kopieren"},
		{"copyToNewLocationJMenuItem", "Zielverzeichnis auswählen"},
		{"FileOperations", "Dateio Operationen"},
		{"fileRenameJMenuItem", "Umbenennen"},
		{"FileRenameLabel1", "Bennene \n"},
		{"FileRenameLabel2", "\nun in: "},
		{"fileDeleteJMenuItem", "Löschen"},
		{"pictureRefreshJMenuItem", "Verkleinerung erneuern"},
		{"rotation", "Rotation"},
		{"rotate90", "Nach Rechts 90"},
		{"rotate180", "Um 180"},
		{"rotate270", "Nach Links 270"},
		{"rotate0", "Keine Rotation"},
		{"userFunctionsJMenu", "Benutzerfunktionen"},
		{"pictureNodeRemove", "Bild Entfernen"},
		{"movePictureToTopJMenuItem", "zuoberst"},
		{"movePictureUpJMenuItem", "rauf"},
		{"movePictureDownJMenuItem", "runter"},
		{"movePictureToBottomJMenuItem", "zuunterst"},
		{"recentDropNodePrefix", "Zu Gruppe: "},


		// ThumbnailJScrollPane
		{"ThumbnailSearchResults", "Suchresultate fr: "},
		{"ThumbnailSearchResults2", " in "},

		//ChangeWindowPopupMenu
		{"fullScreenLabel", "Vollbild"},
		{"leftWindowLabel", "Links"},
		{"rightWindowLabel", "Rechts"},
		{"topLeftWindowLabel", "Oben Links"},
		{"topRightWindowLabel", "Oben Rechts"},
		{"bottomLeftWindowLabel", "Unten Links"},
		{"bottomRightWindowLabel", "Unten Rechts"},
		{"defaultWindowLabel", "Default"},
		{"windowDecorationsLabel", "Fensterrahmen"},
		{"windowNoDecorationsLabel", "Kein Rahmen"},


		// CleverJTree
		{"DefaultRootNodeText", "Neue Sammlung"},
		{"CopyImageDialogButton", "Kopieren"},
		{"CopyImageDialogTitle", "Zieldatei angeben fr: "},
		{"CopyImageNullError", "validateAndCopyPicture mit null arguments aufgerufen! Kopieren abgebrochen."},
		{"CopyImageDirError", "Zielverzeichnis kann nicht erstellt werden. Kopieren abgebrochen.\n"},
		{"fileOpenButtonText", "öffnen"},
		{"fileOpenHeading", "Sammlung öffnen"},
		{"fileSaveAsTitle", "Sammlung speichern als"},
		{"collectionSaveTitle", "Sammlung gespeichert"},
		{"collectionSaveBody", "Sammlung gespeichert als:\n"},
		{"addSinglePictureTitle", "Bild auswählen"},
		{"addSinglePictureButtonLabel", "Auswählen"},
		{"addFlatFileTitle", "Einfache Datei auswählen"},
		{"saveFlatFileTitle", "Bilderliste als Einfache Datei speichern"},
		{"saveFlatFileButtonLabel", "Speichern"},
		{"moveNodeError", "Ziel ist Abkömmling von Quelle. Verschieben abgebrochen."},
		{"unsavedChanges", "Es sind ungespeicherte änderungen vorhanden."},
		{"confirmSaveAs", "Zieldatei existiert!\nFortfahren und berschreiben?"},
		{"discardChanges", "Verwerfen"},
		{"noPicsForSlideshow", "Diese Gruppe hat keine Bilder."},
		{"fileRenameTitle", "Datei Umbenennen"},
		{"fileDeleteTitle", "Datei Löschen"},
		{"fileDeleteError", "Die Datei konnte nicht gelöscht werden:\n"},
		{"deleteRootNodeError", "Die Wurzelgruppe kann nicht entfernt werden."},
				
		// ApplicationJMenuBar
		{"FileMenuText", "Datei"},
		{"FileNewJMenuItem", "Neue Sammlung"},
		{"FileLoadMenuItemText", "Sammlung öffnen"},
		{"FileOpenRecentItemText", "Krzlich verwendet öffnen"},
		{"FileAddMenuItemText", "Bilder hinzufgen"},
		{"FileCameraJMenuItem", "Von Kamera hinzufgen"},
		{"FileSaveMenuItemText", "Sammlung Speichern"},
		{"FileSaveAsMenuItemText", "Speichern unter"},
		{"FileExitMenuItemText", "Abbruch"},
		{"EditJMenuText", "Editieren"},
		{"EditFindJMenuItemText", "Suchen"},
		{"EditCheckDirectoriesJMenuItemText", "Abgleichen"},
		{"EditCollectionPropertiesJMenuItem", "Eigenschaften Sammlung"},
		{"EditCheckIntegrityJMenuItem", "Integrität überprüfen"},
		{"EditCamerasJMenuItem", "Kameras"},
		{"EditCategoriesJMenuItem", "Kategorien"},
		{"EditSettingsMenuItemText", "Einstellungen"},
		{"HelpJMenuText", "Hilfe"},
		{"HelpAboutMenuItemText", "Über"},
		{"HelpLicenseMenuItemText", "Lizenz"},
		
		// PictureViewer
		{"autoAdvanceDialogTitle", "Automatichen Bildwechsel starten"},
		{"randomAdvanceJRadioButtonLabel", "Zufallsauswahl"},
		{"sequentialAdvanceJRadioButtonLabel", "Sequentiell"},
		{"restrictToGroupJRadioButtonLabel", "Auf aktuelle Gruppe beschränken"},
		{"useAllPicturesJRadioButtonLabel", "Alle Bilder"},
		{"timerSecondsJLabelLabel", "Wartezeit (Sekunden)"},

		// ExifViewerJFrame		
		{"ExifTitle", "EXIF Headers\n"},
		{"noExifTags", "Keine EXIF tags gefunden"},
		
		// PictureAdder
		{"PictureAdderDialogTitle", "Bilder und Verzeichnisse hinzufgen"},
		{"PictureAdderProgressDialogTitle", "Fge Bilder hinzu"},
		{"notADir", "Kein Verzeichnis:\n"},
		{"notGroupInfo", "Knoten ist kein Gruppen-Knoten."},
		{"fileChooserAddButtonLabel", "Hinzufgen"},
		{"recurseSubdirectoriesTitle", "Unterverzeichnisse Einbeziehen"},
		{"recurseSubdirectoriesMessage", "Es sind Unterverzeichnisse in Ihrer Auswahl vorhanden.\nSollen diese auch einbezogen werden?"},
		{"recurseSubdirectoriesOk", "Hinzufgen"},
		{"recurseSubdirectoriesNo", "Nein"},
		{"picturesAdded", " Bilder hinzugefgt"},

		// AddFromCamera
		{"AddFromCamera", "Bilder von der Kamera hinzufgen"},
		{"cameraNameJLabel", "Name der Kamera:"},
		{"cameraDirJLabel", "Wurzelverzeichnis der Kamera im Verzeichnisbaum des Rechners:"},
		{"cameraConnectJLabel", "Kommando um die Kamera anzuschliessen:"},
		{"cameraDisconnectJLabel", "Kommando um die Kamera vom Dateisystem zu trennen:"},
		{"allPicturesJRadioButton", "Alle Bilder der Kamera zur Sammlung hinzufgen"},
		{"newPicturesJRadioButton", "Nur neue Bilder der Kamera hinzufgen"},
		{"missingPicturesJRadioButton", "Bilder die in der Sammlung fehlen von der Kamera hinzufgen"},
		{"targetDirJLabel", "Zielverzeichnis fr die Bilder:"},
		{"AddFromCameraOkJButton", "Start"},
		{"editCameraJButton", "Kameraeinstellungen"},
		
		// CameraEditor
		{"CameraEditor", "Kameraeinstellungen bearbeiten"},
		{"cameraNewNameJLabel", "Neuer Name"},
		{"runConnectJButton", "Starten"},
		{"saveJButton", "Speichern"},
		{"memorisedPicsJLabel", "Anzahl vermerkter Bilder beim letzten Import:"},
		{"refreshJButton", "Erneuern"},
		{"zeroJButton", "Abbruch"},
		{"addJButton", "Hinzufgen"},
		{"deleteJButton", "Löschen"},
		{"closeJButton", "Schliessen"},
		{"filenameJCheckBox", "nur Dateinamen berprfen (schneller)"},
		

		// Camera
		{"countingChecksum", "Prfsummen werden berechnet"},
		{"countingChecksumComplete", "Prfsummen berechnet"},
		{"newCamera", "Neue Kamera"},
		
		
		

		// XmlDistiller
		{"DtdCopyError", "Konnte collection.dtd nicht kopieren\n"},

		// CollectionProperties
		{"CollectionPropertiesJFrameTitle", "Eigenschaften der Sammlung"},
		{"CollectionNodeCountLabel", "Anzahl Knoten: "},
		{"CollectionGroupCountLabel", "Anzahl Gruppen: "},
		{"CollectionPictureCountLabel", "Anzahl Bilder: "},
		{"CollectionSizeJLabel", "Belegter Speicherplatz: "},
		{"editProtectJCheckBoxLabel", "Sammlung vor Äderungen schützen"},
		
		// Tools
		{"copyPictureError1", "Konnte \n"},
		{"copyPictureError2", "\nnicht nach: "},
		{"copyPictureError3", "\nkopieren weil: "},

		// PictureAdder
		{"recurseJCheckBox", "Unterverzeichnisse einbeziehen"},
		{"newOnlyJCheckBox", "Ausschliesslich neue Bilder hinzufgen"},
		{"showThumbnailJCheckBox", "Verkleinerung anzeigen"},

		// IntegrityChecker
		{"IntegrityCheckerTitle", "Überprüfen der Integrität¤der Sammlung"},
		{"integrityCheckerLabel", "Integrität überprüfen:"},
		{"check1", "Überprüfe Datums-codierung"},
		{"check1done", "Datümmer die nicht decodiert werden können: "},
		{"check2", "Prüfsummen überprüfen"},
		{"check2progress", "Überprüfe Prüfsummen (läuft): "},
		{"check2done", "Korrigierte Prüfsummen: "},
		{"check3", "Check 3"},
		
		// SortableDefaultMutableTreeNode
		{"GDPMdropBefore", "vor Ziel einfgen"},
		{"GDPMdropAfter", "nach Ziel einfgen"},
		{"GDPMdropIntoFirst", "an erste Stelle einfgen"},
		{"GDPMdropIntoLast", "an letzter Stelle einfgen"},
		{"GDPMdropCancel", "verschieben abbrechen"},
		{"copyAddPicturesNoPicturesError", "Keine Bilder gefunden. Operation abgebrochen."},
		{"FileDeleteTitle", "Löschen"},
		{"FileDeleteLabel", "Datei Löschen\n"},
		{"newGroup", "Neue Gruppe"},


		// CategoryEditorJFrame
		{"CategoryEditorJFrameTitle", "Kategorien Bearbeiten"},
		{"categoryJLabel", "Kategorie"},
		{"categoriesJLabel", "Kategorien"},
		{"addCateogryJButton", "Kategorie Hinzufgen"},
		{"deleteCateogryJButton", "Kategorie Löschen"},
		{"renameCateogryJButton", "Kategorie Umbenennen"},
		{"doneJButton", "Done"},
		{"countCategoryUsageWarning1", "Diese Kategorie wird von "},
		{"countCategoryUsageWarning2", " Knoten verwendet.\nBestätigen Sie, dass Sie sie entfernen wollen."},
		
		{"Template", "Template"},
		{"Template", "Template"}
		
		
		};
	}

