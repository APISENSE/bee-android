# Bee

The Apisense Android app project.

# Quick start

1. Get the project with ```git clone ssh://git@code.apisense.com:2222/proto/apisenseandroid.git```.
2. Specify your android SDK: 
    * By adding a ```local.properties``` file to ApisenseAndroid project with the line ```sdk.dir=/Your/path/to/android-sdk```
    * Or by setting an environment variable named ```ANDROID_HOME``` with ```/Your/path/to/android-sdk``` as a value
3. Add a ```gradle.properties``` file in the project root containing the following lines:
    * artifactory_user = *username*
    * artifactory_password = *artifactory_hashed_password*
    * artifactory_contextUrl = http://repo.apisense.com

# External Libraries
1. HockeyKit
    * **Usage:** Broadcast beta application updates
    * **Version:** Development Branch - Currently, the last tag (2.0.7) does not support Graddle.
    * **Location:** lib/HockeyKit
    * **Content:** https://github.com/TheRealKerni/HockeyKit/tree/develop/client/Android

# Build process

* Build with Gradle ```$ ./gradlew clean build```

# Create and run tests

1. Creation
    1. Unit tests (Uses [Robolectric](http://robolectric.org/) to make tests without needing any android device):
        * Create a new class in the ```bee/src/test/java/$YourPackage``` folder
        * Annotate this class with ```@RunWith(BeeRobolectricTestRunner.class)```
        * Use Roboletric framework
        * _NB: to integrate these unit tests into Intellij, please install the plugin ```Android Studio Unit Test```._
    2. Connected tests (Uses [Robotium](https://code.google.com/p/robotium/) to make tests which needs to be connected to an android device):
        * Place cursor (in the Editor window) at the class name inside one of the files that you want to test (e.g. MainActivity”) and press Alt+Enter.
        * Select “Create Test”. Select the proper superclass for Robotium: android.test.ActivityInstrumentationTestCase2. IntelliJ will create test file and package needed.

2. Run (from the project root)
    1. Lint tests
        * Using command line ```$ ./gradlew :bee:lint```
        * You can find report inside ```./bee/build/outputs/lint-results.html```
    1. Unit tests 
        * Using command line ```$ ./gradlew test```
        * You can find report inside ```./bee/build/test-report/debug/index.html```
    2. Connected tests
        * Using IntelliJ, run all tests
        * Using command lines ```$ ./gradlew installDebugTest``` and ```$ ./gradlew connectedAndroidTest``` -- The first one may not be necessary
        * You can find report inside ```./bee/build/outputs/reports/androidTests/connected/index.html```
                                 
