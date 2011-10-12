rmdir /q/s classServer
mkdir classServer
javac.exe -Xlint -d classServer -sourcepath  src -classpath log\log4j-1.2.16.jar;sql\sqlite4java.jar  src\TaskManagerServer.java
pause