@echo off
echo Resetting data from data_seed to data ...
copy /Y "data_seed\users.json" "data\users.json" >nul
copy /Y "data_seed\products.json" "data\products.json" >nul
copy /Y "data_seed\carts.json" "data\carts.json" >nul
echo Done.
pause
