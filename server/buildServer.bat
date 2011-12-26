rmdir /q/s classServer
mkdir classServer
javac.exe -Xlint -d classServer -sourcepath  src -classpath log\log4j-1.2.16.jar;sql\sqlite4java.jar  src\TaskManagerServer.java
rmdir /q/s build\server\
mkdir build\server
xcopy /D/E/Y log build\server\log\
xcopy /D/E/Y sql build\server\sql\
del build\server\log\log4jClient.properties

cd classServer
jar.exe -cfm TaskManagerServer.jar ..\manifestServer.mf TaskManagerServer.class lab
cd ..
copy /Y classServer\TaskManagerServer.jar build\server\TaskManagerServer.jar
rmdir /q/s classServer
pause