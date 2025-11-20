@echo off
REM Script to export SSL certificates from websites and import into custom truststore

SET JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
SET KEYTOOL=%JAVA_HOME%\bin\keytool.exe
SET TRUSTSTORE=%~dp0gradle-truststore.jks
SET CACERTS=%JAVA_HOME%\lib\security\cacerts

REM Copy default cacerts as starting point
echo Creating custom truststore...
copy /Y "%CACERTS%" "%TRUSTSTORE%"

echo.
echo Truststore created at: %TRUSTSTORE%
echo Default password: changeit
echo.
echo To use this truststore, add to gradle.properties:
echo org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 -Djavax.net.ssl.trustStore=%TRUSTSTORE% -Djavax.net.ssl.trustStorePassword=changeit
echo.
pause

