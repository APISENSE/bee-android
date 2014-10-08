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

# Build process

* Build with Gradle ```$ ./gradlew clean build```