//set dest=C:\Users\Adam\AndroidStudioProjects\RingMyPhoneAndroid\RingMyPhone\src\main\res\drawable-xxhdpi
set dest=test

for %%f in (*.png) do (
	::echo %%~nf

	::xcopy /i %%f %dest%\drawable-xxhdpi\flag_%f:name~0,2%.png

)