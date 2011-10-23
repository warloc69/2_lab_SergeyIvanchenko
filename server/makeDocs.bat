echo off
cd src
javadoc.exe -d docs lab lab.exception lab.client.conntroller lab.client.view lab.client lab.server lab.server.model lab.server.model.bridge
cd ..
xcopy /D/E/Y src\docs docs\
rmdir /q/s src\docs
pause