echo off
cd src
javadoc.exe -d docs lab lab.exception lab.view
cd ..
xcopy /D/E/Y src\docs docs\
rmdir /q/s src\docs
pause