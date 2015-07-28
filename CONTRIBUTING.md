# Quick start

1. Get the project with ```git clone git@apisforge.inria.fr:apps/bee-android.git```.
2. Specify your android SDK: 
    * By adding a ```local.properties``` file to ApisenseAndroid project with the line ```sdk.dir=/Your/path/to/android-sdk```
    * Or by setting an environment variable named ```ANDROID_HOME``` with ```/Your/path/to/android-sdk``` as a value
3. Add a ```gradle.properties``` file in the project root containing the following lines:
    * artifactory_user = *username*
    * artifactory_password = *artifactory_hashed_password*
    * artifactory_contextUrl = http://repo.apisense.com
    * release_keystore_password = *keystore password*
    * release_key_password = *key password*


# Installing google play services in Genymotion
1. Install Google Play Services on your virtual device. To do so, refer to [this tutorial](http://stackoverflow.com/a/20137324/3472838).
2. Sign in with your personal or team Google account and update all services (G+, Gmail etc..)

# Build process

* Build with Gradle ```$ ./gradlew clean assembleDebug```

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
    2. Unit tests
        * Using command line ```$ ./gradlew test```
        * You can find report inside ```./bee/build/test-report/debug/index.html```
    3. Connected tests
        * Using IntelliJ, run all tests
        * Using command lines ```$ ./gradlew installDebugTest``` and ```$ ./gradlew connectedAndroidTest``` -- The first one may not be necessary
        * You can find report inside ```./bee/build/outputs/reports/androidTests/connected/index.html```
    4. Coverage (for unit and connected tests)
        * Using command line ```$ ./gradlew testsCoverage```
        * You can find report inside ```./bee/build/outputs/reports/coverage/html/index.html```


# How to add a new game achievement in Bee ?

1. First, you have to add a new achievement in the Google Play Games in the Play Developer Console.
    * You have to choose the name and the description
    * You have to choose the reward (XP) when the achievement is completed. Be aware that the number that you enter in the field is multiplied by 1000 in the application.
2. Then, you need to integrate this new achievement in the application.
    1. Get the achievement ID in the Play Developer console. The achievement must be like this : CgkIl-DToIgLEAIQAw.
    2. When you have the ID, open the GameAchievement class and add a string field with the achievement name and the ID value.

        ```
        public static final String SHARE_ACE_KEY = "CgkIl-DToIgLEAIQAw";
        ```

    3. Then, you need to add the custom game achievement class.
    This class must implement the GameAchievement class and contains the specific code to process your achievement.
    For this purpose, you need to implement the process method. This method must returns if the achievement is completed or not.
    In the example below, we check the current bee experiment amount. In addition of that, you need to put the score of the achievement.
    This score is a numeric value added on the mission leaderboard when the achievement is finished.

        ```
        @Override
        public boolean process() {
            Log.getInstance().i("BeeFirstMission", "size=" + BeeGameManager.getInstance().getCurrentExperiments().size());
            return BeeGameManager.getInstance().getCurrentExperiments().size() >= 1;
        }
        ```

    4. Now, you have to add your achievement in the achievement factory which provides achievement object by the Play Games achievement object.

        ```
        case GameAchievement.FIRST_MISSION_KEY:
            return new FirstMissionAchievement(achievement);
        ```

    5. We are done with the achievement object itself. Now, you have to choose the real type of your achievement.
       For example, in the current library, there are three kinds of achievements :
        * Share achievements : this type of achievement is involved when the user share something in the app.
        * Sign-in achievements : this type of achievement is involved when the user signs-in with a custom remote platform.
        * Subscribe achievements : this type of achievement is involved when the user subscribes to a new bee mission.
        * Your type : you can create a new custom type of achievement if you need it.
    6. We are almost done. Now you have to fire a new event in the UI when you want to attach some user action to a game event.
    All you have to do is to create a new game event in the activity, like the example below.
    The game manager will check each game achievement and call the process method of the achievement if the event matches with the achievement.

        ```
        BeeGameManager.getInstance().fireGameEventPerformed(new MissionSubscribeEvent(StoreExperimentDetailsActivity.this));
        ```

Note : if you have add a new type of event, you need to modify the game manager. You need to add your event in the search method which get the achievement list by the event class type.

```
private List<GameAchievement> getGameAchievements(GameEvent event) {
    List<GameAchievement> achievements = new ArrayList<>();
    for (GameAchievement ga : currentAchievements.values()) {
        if ((ga instanceof MissionSuscribeAchievement && event instanceof MissionSubscribeEvent) ||
                (ga instanceof ShareAceAchievement && event instanceof ShareEvent)) {
            achievements.add(ga);
        }
    }
    return achievements;
}
```

# TroubleShooting

- __ADB not responding:__
    - *Symptom:*  The connected tests end with a timeout because ```ADB server didn't ACK```
    - *Reason:* Happens when using [Genymotion](http://www.genymotion.com/) as android emulator.
    Genymotion uses a specific version of ```adb``` embedded in their installation files and gradle uses your Android-SDK ```adb```, which might be a newer version.
    - *Correction:* You might force the use of Genymotion's ```adb``` version (A symlink being the simplest correction)
        1. `cd $ANDROID_HOME/platform-tools/`
        2. `mv adb adb-newest`
        3. `ln -s $GENYMOTION_DIR/tool/adb adb`
