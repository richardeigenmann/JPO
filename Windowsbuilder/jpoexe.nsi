; Java Launcher
;--------------
 
Name "Jpo Launcher"
Caption "Jpo Launcher"
;Icon "Java Launcher.ico"
OutFile "Jpo.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
!define CLASSPATH "commons-compress-1.8.jar;commons-io-2.4.jar;commons-jcs-core-2.0-beta-1.jar;commons-jcs-jcache-2.0-beta-1.jar;commons-jcs-jcache-tck-2.0-beta-1.jar;commons-lang3-3.3.2.jar;commons-logging-1.1.3.jar;commons-net-3.3.jar;concurrent.jar;gdata-core-1.0.jar;gdata-maps-2.0.jar;gdata-media-1.0.jar;gdata-photos-2.0.jar;guava-16.0.jar;javax.mail-1.5.1.jar;jsch-0.1.51.jar;jwizz-0.1.4.jar;jxmapviewer2-2.0.jar;metadata-extractor-2.8.1.jar;miglayout-4.0.jar;mydoggy-api-1.5.0.jar;mydoggy-plaf-1.5.0.jar;mydoggy-res-1.5.0.jar;TableLayout-20050920.jar;TagCloud.jar;xmpcore-5.1.2.jar;jpo-0.12.jar"
!define CLASS "Main"
; Careful here: I tried Xmx4000M and it refused to start.
!define OPTIONS "-Xms80M -Xmx1000M"
 
Section ""
  Call GetJRE
  Pop $R0
 
  ; change for your purpose (-jar etc.)
  StrCpy $0 '"$R0" ${OPTIONS} -classpath "${CLASSPATH}" ${CLASS}'
 
  SetOutPath $EXEDIR
  
  ;MessageBox MB_ICONINFORMATION "Command I am going to run in dir $EXEDIR --> $0"
  ExecWait $0
SectionEnd


; See http://nsis.sourceforge.net/Java_Launcher

Function GetJRE
;
;  Find JRE (Java.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume java.exe in current dir or PATH
 

  Push $R0
  Push $R1

  ; use javaw.exe to avoid dosbox.
  ; use java.exe to keep stdout/stderr
  !define JAVAEXE "javaw.exe"
  
  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\${JAVAEXE}"
  IfFileExists $R0 JreFound  ;; 1) found it locally
  StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
  IfErrors 0 JreFound  ;; 2) found it in JAVA_HOME
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
 
  IfErrors 0 JreFound  ;;  3) found it in the registry
  StrCpy $R0 "${JAVAEXE}"  ;; 4) wishing you good luck
        
 JreFound:
  ;MessageBox MB_ICONINFORMATION "Looking for JRE... Found it in $R0"
  Pop $R1
  Exch $R0
FunctionEnd
