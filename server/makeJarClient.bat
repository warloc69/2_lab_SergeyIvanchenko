
cd classClient
jar.exe -cfm TaskManagerClient.jar ..\manifestClient.mf TaskManagerClient.class lab
cd ..
copy /Y classClient\TaskManagerClient.jar TaskManagerClient.jar
del classClient\TaskManagerClient.jar
pause