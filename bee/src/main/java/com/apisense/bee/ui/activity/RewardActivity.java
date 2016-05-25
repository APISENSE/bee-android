package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.widget.ApisenseTextView;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievement;

import java.util.List;

public class RewardActivity extends BeeGameActivity implements View.OnClickListener {
    private static final int MISSION_ACHIEVEMENTS_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_reward);

        Toolbar toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_action_back);
            setSupportActionBar(toolbar);
        }

        LinearLayout layoutAchievements = (LinearLayout) findViewById(R.id.reward_game_badge_panel);
        if (layoutAchievements != null) {
            layoutAchievements.setOnClickListener(this);
        }
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();
        final ApisenseTextView apvAchievements = (ApisenseTextView) findViewById(R.id.reward_game_achievements);
        refreshAchievements(new Pending<List<Achievement>>() {
            @Override
            public void onFetched(List<Achievement> achievements) {
                if (apvAchievements != null) {
                    apvAchievements.setText(String.valueOf(countUnlocked(achievements)));
                }
            }
        });
    }

    public void doGoToHome(View homeButton) {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reward_game_badge_panel:
                startActivityForResult(
                        Games.Achievements.getAchievementsIntent(getApiClient()),
                        MISSION_ACHIEVEMENTS_REQUEST_CODE
                );
                break;
            default:
                break;
        }

    }
}
