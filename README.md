# Bee

The Apisense Android app project is a scientific tool used to crowd sensing on mobile devices.

# How to use it ?

1. To participate to any experiment you do not need any account even if it's appreciate.
2. To do so, first you need to go to the Bee Store where you can find all current running experiments.
    * You can install them using long click on it.
    * Short click will bring you to detailed information about it. From there, you can still click the subscribe button.
    * You can uninstall them doing the same thing.
3. Then you need to go back to the main interface
    * Again, a long click will start any experiment. Nothing will be collected until you do that !
    * You can also use a short click et then press the start button.
    * Again, if you want to stop harvesting data, you just have to do the reversed process.
4. In the detailed view, you will find valuable information about the experiment selected and data visualization.

# How to add a new game achievement in Bee ?
1. First, you have to add a new achievement in the Google Play Games in the Play Developer Console.
    * You have to choose the name and the description
    * You have to choose the reward (XP) when the achievement is completed. Be aware that the number that you enter in the field is multiplied by 1000 in the application.
2. Then, you need to integrate this new achievement in the application.
    1. Get the achievement ID in the Play Developer console. The achievement must be like this : CgkIl-DToIgLEAIQAw.
    2. When you have the ID, open the GameAchievement class and add a string field with the achievement name and the ID value.
```java
    public static final String SHARE_ACE_KEY = "CgkIl-DToIgLEAIQAw";
```
    3. Then, you need to add the custom game achievement class. This class must implement the GameAchievement class and contains the specific code to process your achievement. For this purpose, you need to implement the process method. This method must returns if the achievement is completed or not. In the example below, we check the current bee experiment amount. In addition of that, you need to put the score of the achievement. This score is a numeric value added on the mission leaderboard when the achievement is finished.
```java
    @Override
    public boolean process() {
        Log.getInstance().i("BeeFirstMission", "size=" + BeeGameManager.getInstance().getCurrentExperiments().size());
        return BeeGameManager.getInstance().getCurrentExperiments().size() >= 1;
    }
```
    4. Now, you have to add your achievement in the achievement factory which provides achievement object by the Play Games achievement object.
```java
            case GameAchievement.FIRST_MISSION_KEY:
                return new FirstMissionAchievement(achievement);
```
    5. We are done with the achievement object itself. Now, you have to choose the real type of your achievement. For example, in the current library, there are three kinds of achievements : 
        * Share achievements : this type of achievement is involved when the user share something in the app.
        * Sign-in achievements : this type of achievement is involved when the user signs-in with a custom remote platform.
        * Subscribe achievements : this type of achievement is involved when the user subscribes to a new bee mission.
        * Your type : you can create a new custom type of achievement if you need it.
    
    6. We are almost done. Now you have to fire a new event in the UI when you want to attach some user action to a game event. All you have to do is to create a new game event in the activity, like the example below. The game manager will check each game achievement and call the process method of the achievement if the event matches with the achievement.
```java
        BeeGameManager.getInstance().fireGameEventPerformed(new MissionSubscribeEvent(StoreExperimentDetailsActivity.this));
```

Note : if you have add a new type of event, you need to modify the game manager. You need to add your event in the search method which get the achievement list by the event class type.
```java
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

# Contact

For more information, you can contact us using : contact (at) apisense.com
