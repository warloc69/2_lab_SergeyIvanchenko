rmdir /q/s classClient
mkdir classClient
javac.exe -Xlint -d classClient -sourcepath  src -classpath log\log4j-1.2.16.jar  src\TaskManagerClient.java
pause