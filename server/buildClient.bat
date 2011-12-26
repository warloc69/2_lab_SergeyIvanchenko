rmdir /q/s classClient
mkdir classClient
javac.exe -Xlint -d classClient -sourcepath  src -classpath log\log4j-1.2.16.jar  src\TaskManagerClient.java
rmdir /q/s build\client\
mkdir build\client
xcopy /D/E/Y log build\client\log\
xcopy /D/E/Y option build\client\option\
xcopy /D/E/Y img build\client\img\
del build\client\log\log4jServer.properties
cd classClient
jar.exe -cfm TaskManagerClient.jar ..\manifestClient.mf TaskManagerClient.class lab
cd ..
copy /Y classClient\TaskManagerClient.jar build\client\TaskManagerClient.jar
del TaskManagerClient.jar
rmdir /q/s classClient
pause



