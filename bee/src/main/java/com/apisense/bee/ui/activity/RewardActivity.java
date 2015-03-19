package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.games.BeeGameManager;
import com.apisense.bee.widget.ApisenseTextView;

/**
 * Created by Warnant on 08-03-15.
 */
public class RewardActivity extends BeeGameActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_reward);

        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        setSupportActionBar(toolbar);


        ApisenseTextView apvPoints = (ApisenseTextView) findViewById(R.id.reward_game_points);
        apvPoints.setText("" + BeeGameManager.getInstance().getPlayerPoints());

        ApisenseTextView apvAchievements = (ApisenseTextView) findViewById(R.id.reward_game_achievements);
        apvAchievements.setText("" + BeeGameManager.getInstance().getAchievementUnlockCount());

        ApisenseTextView apvThanks = (ApisenseTextView) findViewById(R.id.reward_game_thanks);
        apvAchievements.setText("" + 0);
    }

    public void doGoToHome(View homeButton) {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

}
