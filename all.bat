call cls

call set HUDSON_PORT=80
call set HUDSON_WAR=C:\Users\jacob\workspace-helios\hudson-binary-war\hudson-1.386.war
call set HUDSON_HOME=C:\Users\jacob\.hudson
call set PLUGIN=configurationslicing

call mvn clean install -Dmaven.test.skip=true
rem -gs C:\softwaredistribution\apache-maven-2.2.1\conf\jenkins-settings.xml -Pjenkins-repos

call rmdir /S /Q "%HUDSON_HOME%\plugins\%PLUGIN%"
call copy target\%PLUGIN%.hpi "%HUDSON_HOME%\plugins"

call "C:\Program Files\Java\jdk1.6.0_21\bin\java.exe" -jar %HUDSON_WAR% --httpPort=%HUDSON_PORT%
