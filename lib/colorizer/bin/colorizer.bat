@echo off
IF NOT EXIST %JAVA_HOME% GOTO trywithoutjavahome
SET JAVA_EXE=%JAVA_HOME%\jre\bin\java.exe
GOTO run

:trywithoutjavahome
SET JAVA_EXE=javaw.exe

:run
set HOME=%0\..\..
set LIBDIR=%HOME%\lib
set THEMESDIR=%HOME%\themes
%JAVA_EXE% -classpath %LIBDIR%\colorizer.jar;%HOME%\resources;%LIBDIR%\squareness.jar;%LIBDIR%\forms_rt.jar;%LIBDIR%\swingx.jar -Dthemes.dir=%THEMESDIR% net.beeger.squareness.colorizer.Startup
