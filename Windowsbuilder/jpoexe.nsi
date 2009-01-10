; Java Launcher
;--------------
 
Name "Jpo Launcher"
Caption "Jpo Launcher"
;Icon "Java Launcher.ico"
OutFile "Jpo.exe"
 
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
 
!define CLASSPATH "jnlp.jar;metadata-extractor-2.3.0.jar;activation.jar;mail.jar;poi-3.1-FINAL-20080629.jar;poi-contrib-3.1-FINAL-20080629.jar;poi-scratchpad-3.1-FINAL-20080629.jar;jwizz-0.1.4.jar;jpo-0.9.jar"
!define CLASS "jpo.Jpo"
!define OPTIONS "-Xms80M -Xmx500M"
 
Section ""
  Call GetJRE
  Pop $R0
 
  ; change for your purpose (-jar etc.)
  StrCpy $0 '"$R0" ${OPTIONS} -classpath "${CLASSPATH}" ${CLASS}'
 
  SetOutPath $EXEDIR
  ExecWait $0
;  Exec $0
SectionEnd
 
Function GetJRE
;
;  Find JRE (Java.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume java.exe in current dir or PATH
 
  Push $R0
  Push $R1
 
  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\java.exe"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\java.exe"
  IfErrors 0 JreFound
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\java.exe"
 
  IfErrors 0 JreFound
 ; StrCpy $R0 "java.exe"
  StrCpy $R0 "javaw.exe"
        
 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd
