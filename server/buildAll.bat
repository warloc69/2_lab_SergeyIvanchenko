rmdir /q/s build
mkdir build\client
xcopy /D/E/Y classClient build\client\classClient\
xcopy /D/E/Y log build\client\log\
xcopy /D/E/Y option build\client\option\
xcopy /D/E/Y img build\client\img\
copy /Y manifestClient.mf build\client\
copy /Y makeJarClient.bat build\client\
del build\client\log\log4jServer.properties
mkdir build\server
xcopy /D/E/Y classServer build\server\classServer\
xcopy /D/E/Y log build\server\log\
xcopy /D/E/Y sql build\server\sql\
copy /Y manifestServer.mf build\server\
copy /Y makeJarServer.bat build\server\
del build\server\log\log4jClient.properties
pause

