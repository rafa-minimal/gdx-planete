Żeby odpalić projekt (nawet desktop) trzeba mieć zainstalowany Android SDK.
Inaczej nie uda się zaimportować projektu.
Android SDK instaluje się razem z Android Studio, można też osobno.

Po zainstalowaniu trzeba utworzyć plik `local.properties` w katalogu głównym projektu (albo ustawić zmienną środowiskową ANDROID_HOME)
ze ścieżką do Android SDK

```
#local.properties
sdk.dir = c:\dev\android
```

Musimy zaciągnąć odpowiednie wersje build tools i platform,
takie jakie mamy zadeklarowane w build.grale dla projektu android
(albo lepiej - podbić wersje w build.gradle):
```
buildToolsVersion "27.0.1"          //   /ANDROID_HOME/build-tools/27.0.1
compileSdkVersion 27                //   /ANDROID_HOME/platforms/android-27
```

Komunikaty błędów:

* `Failed to find target with hash string 'android-20' in: c:\dev\android`

  Nie mamy odpowiedniej platformy: `/ANDROID_HOME/platforms/android-27`