# Quick start

1. Clone this project ```git clone git@github.com:APISENSE/bee-android.git```.
2. Build the application using ```./gradlew installDebug```
3. _[Optional]_ Add a ```gradle.properties``` file in the project root containing the following lines, if needed:
    * To use the APISENSE SDK (see [how to register an application](http://docs.apisense.io/en/1.11.0/guide/dashboard/#manage-own-applications)):
        * debug_sdk_key  = *application_key*
        * release_sdk_key = *application_key*
    * To upload error on a rollbar server
        * rollbar_key = *rollbar_key*
    * To build an application release:
        * release_keystore_password = *keystore password*
        * release_key_password = *key password*


# How to add a new game achievement in Bee ?

1. First, you have to add a new achievement in the Google Play Games in the Play Developer Console.
    * You have to choose the name and the description
    * You have to choose the reward (XP) when the achievement is completed. Be aware that the number that you enter in the field is multiplied by 1000 in the application.
2. Then, you need to integrate this new achievement in the application.
    1. Get the achievement ID in the Play Developer console. The achievement must be like this : CgkIl-DToIgLEAIQAw.
    2. Add this ID as a resource in _strings.xml_.

        ```
        <string translatable="false" name="achievement_secretive_bee">CgkIl-DToIgLEAIQAw</string>
        ```

    3. Unlock the achievement or increment its progress, e.g.:

        ```
        // In both examples,
        // this is an Activity extending BeeGameActivity, or a Fragment embedded in a BeeGameActivity.
        new SimpleGameAchievement(getString(R.string.achievement_secretive_bee)).unlock(this);
        new IncrementalGameAchievement(getString(R.string.achievement_bronze_wings)).increment(this);
        ```

# TroubleShooting

## Installing google play services in Genymotion
1. Install Google Play Services on your virtual device. To do so, refer to [this tutorial](http://stackoverflow.com/a/20137324/3472838).
2. Sign in with your personal or team Google account and update all services (G+, Gmail etc..)

## __ADB not responding:__
  
- *Symptom:*  The connected tests end with a timeout because ```ADB server didn't ACK```
- *Reason:* Happens when using [Genymotion](http://www.genymotion.com/) as android emulator.
Genymotion uses a specific version of ```adb``` embedded in their installation files and gradle uses your Android-SDK ```adb```, which might be a newer version.
- *Correction:* You might force the use of Genymotion's ```adb``` version (A symlink being the simplest correction)
    1. `cd $ANDROID_HOME/platform-tools/`
    2. `mv adb adb-newest`
    3. `ln -s $GENYMOTION_DIR/tool/adb adb`
