
cd class
jar.exe -cfm TaskManager.jar ..\manifest.mf TaskManager.class lab
cd ..
copy /Y class\TaskManager.jar TaskManager.jar
del class\TaskManager.jar
pause