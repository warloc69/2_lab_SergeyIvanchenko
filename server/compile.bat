rmdir /q/s class
mkdir class
javac.exe -Xlint -d class -sourcepath  src -classpath log\log4j-1.2.16.jar;sql\sqlite4java.jar  src\TaskManager.java
pause