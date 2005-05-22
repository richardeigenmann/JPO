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
		{"outOfMemoryError", "Ein \"Out of Memory Error\" wurde ausgewählt"},
		{"areYouSure", "Sind Sie sicher?"},
		
		
		
		// Help About Dialog
		{"HelpAboutText", "JPO Version 0.8.5 ist ein Java/Swing Programm\n" 
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
		{"dateRangeError", "Entschuldigung, der Datumsbereich macht keinen Sinn."},
		


		
		// PictureViewer Texts
		{"PictureViewerTitle", "JPO Bilder Betrachter"},
		{"PictureViewerKeycodes", "Die folgenden Tasten können benutzt werden:\n" 
			+ "N: Nächstes Bild\n"
			+ "P: Vorhergehendes Bild\n"
			+ "I: Informationen ein | aus\n"
			+ "<space>,<home>: Auf Vollbild zoomen\n"
			+ "<links>,<rechts>,<rauf>,<runter>: Bild in Pfeilrichtung verschieben\n"
			+ "<PgUp>: Reinzoomen\n"
			+ "<PgDown>: Rauszoomen\n"
			+ "1: auf 100% zoomen\n"
			+ "F: Fenstergrösse-Menu\n"
			+ "M: Popup Menu"},
		{"PictureViewerKeycodesTitle", "Tastatur-Abkürzungen"},
		{"NavigationPanel", "Navigations Werkzeuge"},
		{"fullScreenJButton.ToolTipText", "Vollbild"},
		{"popupMenuJButton.ToolTipText", "Popup Menu"},
		{"nextJButton.ToolTipText", "Nächstes Bild"},
		{"previousJButton.ToolTipText", "Vorheriges Bild"},
		{"infoJButton.ToolTipText", "Informationen"},
		{"resetJButton.ToolTipText", "Reset"},
		{"clockJButton.ToolTipText", "Automatischer Bildwechsel"},
		{"closeJButton.ToolTipText", "Fenster schliessen"},
		{"rotateLeftJButton.ToolTipText", "Nach Links rotieren"},
		{"rotateRightJButton.ToolTipText", "Nach Rechts rotieren"},

		// SettingsDialog Texts
		{"settingsDialogTitle", "Einstellungen"},
		
		{"browserWindowSettingsJPanel", "Allgemein"},
		{"languageJLabel", "Sprache:"},
		{"autoLoadJLabelLabel", "Automatisch laden:"},
		{"logfileJCheckBoxLabel", "Logdatei schreiben"},
		{"logfileJLabelLabel", "Pfad der Logdatei und Filenamen:"},
		{"saveSizeJCheckBoxLabel", "Fensterposition und Grösse bei Applikationsende speichern"},
		{"MainCoordinates", "Koordinaten des Hauptfensters (x/y):"},
		{"MainSize", "Grösse des Hauptfensters (b/h):"},
		
		{"pictureViewerJPanel", "Bilder Betrachter"},
		{"maximumPictureSizeLabel", "Maximale Bild Vergrösserung:"},
		{"maxCacheLabel", "Maximal zwischengespeicherte Bilder:"},
		{"leaveSpaceLabel", "Abstand nach unten:"},
		{"dontEnlargeJCheckBoxLabel", "Kleine Bilder nicht vergrössern"},
		{"pictureCoordinates", "Default Koordinaten des Bilder Betrachter Fensters (x/y):"},
		{"pictureSize", "Default Grösse des Bilder Betrachter Fensters (b/h):"},
		{"pictureViewerFastScale", "Geschwindigkeit statt Qualität optimieren beim vergrössern"},
		
		{"thumbnailSettingsJPanel", "Verkleinerungen"},
		{"thumbnailDirLabel", "Verzeichnis für die Verkleinerungen:"},
		{"keepThumbnailsJCheckBoxLabel", "Verkleinerungen auf Disk schreiben"},
		{"maxThumbnailsLabelText", "Maximale Anzahl Verkleinerungen pro Seite:"},
		{"thumbnailSizeLabel", "Grösse der Verkleinerungen:"},
		{"thumbnailFastScale", "Geschwindigkeit statt Qualität beim verkleinern"},
		{"zapThumbnails", "Alle Verkleinerungen löschen"},
		{"thumbnailsDeleted", " Verkleinerungen gelöscht"},
		
		{"autoLoadChooserTitle", "Datei die automatisch geladen wird"},
		{"logfileChooserTitle", "Logdatei wählen"},
		{"thumbDirChooserTitle", "Verzeichnis für Verkleinerungen wählen"},
		
		{"settingsError", "Fehler der Einstellungen"},
		{"generalLogFileError", "Es besteht ein mit der Logdatei. Logmeldungen werden nicht geschrieben."},
		{"thumbnailDirError", "Etwas ist ernsthaft falsch mit dem Verkleinerungs-Verzeichnis"},

		{"userFunctionJPanel", "Benutzerfunktionen"},
		{"userFunction1JLabel", "Benutzerfunktion 1"},
		{"userFunction2JLabel", "Benutzerfunktion 2"},
		{"userFunction3JLabel", "Benutzerfunktion 3"},
		{"userFunctionNameJLabel", "Name:"},
		{"userFunctionCmdJLabel", "Kommando:"},
		{"userFunctionHelpJTextArea", "%f wird durch den Filenamen substituiert\n%u wird durch die URL des Bildes substituiert"},

		{"emailJPanel", "Email Server"},
		{"emailJLabel", "Email Server Einstellungen"},
		{"predefinedEmailJLabel", "Vordefinierter Server:"},
		{"emailServerJLabel", "Email Server:"},
		{"emailPortJLabel", "Port:"},
		{"emailUserJLabel", "Benutzername:"},
		{"emailPasswordJLabel", "Password:"},
		

		// Settings
		{"thumbNoExistError", "Das Verzeichnis füt die Verkleinerungen existiert nicht.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nZwischenspeicherung von Verkleinerungen wurde deaktiviert."},
		{"thumbNoWriteError", "Das Verzeichnis der Verkleinerungen erlaubt keine Schreibzugriffe.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nZwischenspeicherung von Verkleinerungen wurde deaktiviert."},
		{"thumbNoDirError", "Das Verzeichnis für die Verkleinerungen ist kein Verzeichnis.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nZwischenspeicherung von Verkleinerungen wurde deaktiviert.."},
		{"logFileCanWriteError", "Die Logdatei erlaubt keine Schreibzugriffe.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nLogging wurde ausgeschaltet"},
		{"logFileIsFileError", "Die Logdatei der Einstellungen ist kein Datei.\nBitte geben sie es unter Bearbeiten | Einstellungen ein.\nLogging wurde augeschaltet"},
		{"generalLogFileError", "Es besteht ein Problem mit der Logdatei. Logging wurde deaktiviert."},
		{"cantWriteIniFile", "Fehler beim Schreiben der ini Datei:\n"},
		{"cantReadIniFile", "Konnte JPO.ini nicht lesen. Grundeinstellungen werden verwendet.\n"},
		



		// HtmlDistillerJFrame
		{"HtmlDistillerJFrameHeading", "Export nach HTML"},
		{"HtmlDistillerThreadTitle", "Extraktion nach HTML"}, 
		{"HtmlDistillerChooserTitle", "Zielverzeichnis für HTML"},
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
		{"ReconcileJFrameTitle", "Verzeichnisabgleich gegenüber der Sammlung"},
		{"ReconcileBlaBlaLabel", "<HTML>Diese Funktion überprüft ob die Dateien im angegebenen Verzeichnis in der Sammlung vorhanden sind.</htm>"},
		{"directoryJLabelLabel", "Verzeichnis für den Abgleich:"},
		{"directoryCheckerChooserTitle", "Verzeichnis für den Abgleich"},
		{"ReconcileFound", " in Sammlung gefunden als "},
		{"ReconcileNotFound", "Nicht in der Sammlung: "},
		{"ReconcileDone", "Fertig.\n"},
		{"ReconcileInterrupted", "Unterbrochen.\n"},
		{"ReconcileListPositives", "Gefundene auch anzeigen"},
		{"ReconcileOkButtonLabel", "Abgleich"},
		{"ReconcileSubdirectories", "Unterverzeichnisse abgleichen"},
		{"ReconcileCantReadError", "Lesefehler: "},
		{"ReconcileNullFileError", "Ungültiges Verzeichnis"},
		{"ReconcileStart", "Abgleichsverzeichnis: "},
		{"ReconcileNoFiles", "Keine Dateien gefunden.\n"},

		
		// CollectionDistillerJFrame
		{"CollectionDistillerJFrameFrameHeading", "Export in eine neue Sammlung"},
		{"collectionExportPicturesText", "Bilder Exportieren"},
		{"xmlFileNameLabel", "Name fr die XML Datei:"},
		{"collectionExportChooserTitle", "Zielverzeichnis für die Sammlung"},


		// ConsolidateGroupJFrame
		{"highresTargetDirJTextField", "Konsolidierungsverzeichnis für Highres auswählen"},
		{"lowresTargetDirJTextField", "Konsolidierungsverzeichnis für Lowres auswählen"},
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
		{"SelectJarFileTitle", "Zielverzeichnis für Bilder"},
		
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
		{"parsedAs", "Entschlüsselt als: "},
		{"failedToParse", "Kann nicht als Datum entschlüsselt werden"},
		{"categoriesJLabel-2", "Kategorien:"},
		{"setupCategories", ">> Kategorien erstellen <<"},
		{"noCategories", ">> Keine <<"},

		
		//GroupInfoEditor
		{"GroupInfoEditorHeading", "Gruppenbeschreibung ändern"},
		{"groupDescriptionLabel", "Gruppenbeschreibung:"},
		
		// GroupPopupMenu
		{"groupShowJMenuItem", "Gruppe zeigen"},
		{"groupSlideshowJMenuItem", "Bilder zeigen"},
		{"groupFindJMenuItemLabel", "Suchen"},
		{"groupEditJMenuItem", "Umbenennen"},
		{"groupRefreshJMenuItem", "Icon regenerieren"},
		{"groupTableJMenuItemLabel", "Als Tabelle bearbeiten"},
		{"addGroupJMenuLabel", "Hinzufügen"},
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
		{"indentJMenuItem", "einrücken"},
		{"outdentJMenuItem", "ausrücken"},
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
		{"FileOperations", "Datei Operationen"},
		{"fileRenameJMenuItem", "Umbenennen"},
		{"FileRenameLabel1", "Benenne \n"},
		{"FileRenameLabel2", "\nun in: "},
		{"fileDeleteJMenuItem", "Löschen"},
		{"pictureRefreshJMenuItem", "Verkleinerung erneuern"},
		{"pictureMailSelectJMenuItem", "Auswählen für eMail"},
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
		{"ThumbnailSearchResults", "Suchresultate für: "},
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
		{"CopyImageDialogTitle", "Zieldatei angeben für: "},
		{"CopyImageNullError", "validateAndCopyPicture mit null Argumenten aufgerufen! Kopieren abgebrochen."},
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
		{"unsavedChanges", "Es sind ungespeicherte Änderungen vorhanden."},
		{"confirmSaveAs", "Zieldatei existiert!\nFortfahren und überschreiben?"},
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
		{"FileOpenRecentItemText", "Kürzlich verwendet öffnen"},
		{"FileAddMenuItemText", "Bilder hinzufügen"},
		{"FileCameraJMenuItem", "Von Kamera hinzufügen"},
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
		{"actionJMenu", "Aktivitäten"},
		{"emailJMenuItem", "Email verschicken"},
		{"HelpJMenuText", "Hilfe"},
		{"HelpAboutMenuItemText", "Über"},
		{"HelpLicenseMenuItemText", "Lizenz"},
		
		// PictureViewer
		{"autoAdvanceDialogTitle", "Automatischen Bildwechsel starten"},
		{"randomAdvanceJRadioButtonLabel", "Zufallsauswahl"},
		{"sequentialAdvanceJRadioButtonLabel", "Sequentiell"},
		{"restrictToGroupJRadioButtonLabel", "Auf aktuelle Gruppe beschränken"},
		{"useAllPicturesJRadioButtonLabel", "Alle Bilder"},
		{"timerSecondsJLabelLabel", "Wartezeit (Sekunden)"},

		// ExifViewerJFrame		
		{"ExifTitle", "EXIF Headers\n"},
		{"noExifTags", "Keine EXIF tags gefunden"},
		
		// PictureAdder
		{"PictureAdderDialogTitle", "Bilder und Verzeichnisse hinzufügen"},
		{"PictureAdderProgressDialogTitle", "Füge Bilder hinzu"},
		{"notADir", "Kein Verzeichnis:\n"},
		{"notGroupInfo", "Knoten ist kein Gruppen-Knoten."},
		{"fileChooserAddButtonLabel", "Hinzufügen"},
		{"recurseSubdirectoriesTitle", "Unterverzeichnisse Einbeziehen"},
		{"recurseSubdirectoriesMessage", "Es sind Unterverzeichnisse in Ihrer Auswahl vorhanden.\nSollen diese auch einbezogen werden?"},
		{"recurseSubdirectoriesOk", "Hinzufügen"},
		{"recurseSubdirectoriesNo", "Nein"},
		{"picturesAdded", " Bilder hinzugefügt"},

		// AddFromCamera
		{"AddFromCamera", "Bilder von der Kamera hinzufügen"},
		{"cameraNameJLabel", "Name der Kamera:"},
		{"cameraDirJLabel", "Wurzelverzeichnis der Kamera im Verzeichnisbaum des Rechners:"},
		{"cameraConnectJLabel", "Kommando um die Kamera anzuschliessen:"},
		{"cameraDisconnectJLabel", "Kommando um die Kamera vom Dateisystem zu trennen:"},
		{"allPicturesJRadioButton", "Alle Bilder der Kamera zur Sammlung hinzufügen"},
		{"newPicturesJRadioButton", "Nur neue Bilder der Kamera hinzufügen"},
		{"missingPicturesJRadioButton", "Bilder die in der Sammlung fehlen von der Kamera hinzufügen"},
		{"targetDirJLabel", "Zielverzeichnis für die Bilder:"},
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
		{"addJButton", "Hinzufügen"},
		{"deleteJButton", "Löschen"},
		{"closeJButton", "Schliessen"},
		{"filenameJCheckBox", "nur Dateinamen überprüfen (schneller)"},
		{"refreshJButtonError", "Speichern Sie erst Ihre Aenderungen!"},


		// Camera
		{"countingChecksum", "Prüfsummen werden berechnet"},
		{"countingChecksumComplete", " Prüfsummen berechnet"},
		{"newCamera", "Neue Kamera"},
		
		
		

		// XmlDistiller
		{"DtdCopyError", "Konnte collection.dtd nicht kopieren\n"},

		// CollectionProperties
		{"CollectionPropertiesJFrameTitle", "Eigenschaften der Sammlung"},
		{"CollectionNodeCountLabel", "Anzahl Knoten: "},
		{"CollectionGroupCountLabel", "Anzahl Gruppen: "},
		{"CollectionPictureCountLabel", "Anzahl Bilder: "},
		{"CollectionSizeJLabel", "Belegter Speicherplatz: "},
		{"queCountJLabel", "Pendente Verkleinerungen: "},
		{"editProtectJCheckBoxLabel", "Sammlung vor Änderungen schützen"},
		
		// Tools
		{"copyPictureError1", "Konnte \n"},
		{"copyPictureError2", "\nnicht nach: "},
		{"copyPictureError3", "\nkopieren weil: "},

		// PictureAdder
		{"recurseJCheckBox", "Unterverzeichnisse einbeziehen"},
		{"retainDirectoriesJCheckBox", "Verzeichnisstruktur abbilden"},
		{"newOnlyJCheckBox", "Ausschliesslich neue Bilder hinzufügen"},
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
		{"GDPMdropBefore", "vor Ziel einfügen"},
		{"GDPMdropAfter", "nach Ziel einfügen"},
		{"GDPMdropIntoFirst", "an erste Stelle einfügen"},
		{"GDPMdropIntoLast", "an letzter Stelle einfügen"},
		{"GDPMdropCancel", "verschieben abbrechen"},
		{"copyAddPicturesNoPicturesError", "Keine Bilder gefunden. Operation abgebrochen."},
		{"FileDeleteTitle", "Löschen"},
		{"FileDeleteLabel", "Datei Löschen\n"},
		{"newGroup", "Neue Gruppe"},


		// CategoryEditorJFrame
		{"CategoryEditorJFrameTitle", "Kategorien Bearbeiten"},
		{"categoryJLabel", "Kategorie"},
		{"categoriesJLabel", "Kategorien"},
		{"addCateogryJButton", "Kategorie Hinzufügen"},
		{"deleteCateogryJButton", "Kategorie Löschen"},
		{"renameCateogryJButton", "Kategorie Umbenennen"},
		{"doneJButton", "Done"},
		{"countCategoryUsageWarning1", "Diese Kategorie wird von "},
		{"countCategoryUsageWarning2", " Knoten verwendet.\nBestätigen Sie, dass Sie sie entfernen wollen."},

		
		// EmailerJFrame
		{"EmailerJFrame", "Email versenden"},
		{"imagesCountJLabel", "Anzahl ausgewählte Bilder: "},
		{"emailJButton", "Senden"},
		{"noNodesSelected", "Es sind keine Bilder ausgewählt. Wählen Sie diese bitte erst mit dem PopupMenu auf den einzelnen Bildern."},
		{"fromJLabel", "Von:"},
		{"toJLabel", "An:"},
		{"messageJLabel", "Mitteilung:"},
		{"subjectJLabel", "Betreff:"},
		{"emailSendError", "Folgender Fehler trat auf:\n"},
		{"emailOK", "Das Email wurde erfolgreich versandt."},
		{"emailSizesJLabel", "Grösse:"},
		{"emailResizeJLabel", "Verkleinern auf:"},
		{"emailSize1", "Klein (350 x 300)"},
		{"emailSize2", "Mittel (700 x 550)"},
		{"emailSize3", "Mittel plus Original"},
		{"emailSize4", "Gross (1000 x 800)"},
		{"emailSize5", "Nur Originale"},
		{"emailOriginals", "Original Anhängen"},
		{"emailNoNodes", "Keine Bilder zum Versand ausgewählt. Mit dem Rechts-click Popupmenu auf den Bildern werden Bilder ausgewählt."},
		{"emailNoServer", "Keine Email Server eingestellt. Konfigurieren Sie den Server unter Editieren > Einstellungen > EmailServer."},
		
		
		{"Template", "Template"},
		{"Template", "Template"}
		
		
		};
	}

