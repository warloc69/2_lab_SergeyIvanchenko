
cd classServer
jar.exe -cfm TaskManagerServer.jar ..\manifestServer.mf TaskManagerServer.class lab
cd ..
copy /Y classServer\TaskManagerServer.jar TaskManagerServer.jar
del classServer\TaskManagerServer.jar
pause