package com.apisense.bee.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.games.BeeGameActivity;
import com.apisense.bee.ui.fragment.AboutFragment;
import com.apisense.bee.ui.fragment.AccountFragment;
import com.apisense.bee.ui.fragment.HomeFragment;
import com.apisense.bee.ui.fragment.PrivacyFragment;
import com.apisense.bee.ui.fragment.StoreFragment;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.apisense.sdk.APISENSE;

public class HomeActivity extends BeeGameActivity implements HomeFragment.OnStoreClickedListener {
    private static String TAG = "HomeActivity";

    private AccountHeader headerResult;
    public Drawer drawer;
    private APISENSE.Sdk apisenseSdk;
    private boolean drawerInitializedWithUser;

    // Drawer item identifiers
    public static final int DRAWER_HOME_IDENTIFIER = 1;
    public static final int DRAWER_STORE_IDENTIFIER = 2;
    private static final int DRAWER_PLAY_IDENTIFIER = 3;
    private static final int DRAWER_PLAY_REWARD_IDENTIFIER = 4;
    public static final int DRAWER_PRIVACY_IDENTIFIER = 5;
    public static final int DRAWER_ACCOUNT_IDENTIFIER = 6;
    public static final int DRAWER_ABOUT_IDENTIFIER = 7;

    // Drawer items
    private final PrimaryDrawerItem home = generateDrawerItem(R.string.title_activity_home, R.drawable.ic_home, DRAWER_HOME_IDENTIFIER);
    private final PrimaryDrawerItem store = generateDrawerItem(R.string.title_activity_store, R.drawable.ic_store_blck, DRAWER_STORE_IDENTIFIER);
    private final PrimaryDrawerItem play = generateDrawerItem(R.string.title_activity_gpg, R.drawable.ic_gpg, DRAWER_PLAY_IDENTIFIER);
    private final PrimaryDrawerItem playReward = generateDrawerItem(R.string.title_activity_reward, R.drawable.ic_gpg, DRAWER_PLAY_REWARD_IDENTIFIER);
    private final PrimaryDrawerItem settings = generateDrawerItem(R.string.title_activity_settings, R.drawable.ic_action_settings, DRAWER_PRIVACY_IDENTIFIER);
    private final PrimaryDrawerItem profile = generateDrawerItem(R.string.title_activity_account, R.drawable.ic_action_person, DRAWER_ACCOUNT_IDENTIFIER);
    private final PrimaryDrawerItem about = generateDrawerItem(R.string.title_activity_about, R.drawable.ic_action_about, DRAWER_ABOUT_IDENTIFIER);

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_home);

        apisenseSdk = ((BeeApplication) getApplication()).getSdk();

        drawerInitializedWithUser = false;
        headerResult = generateAccountHeader();
        drawer = generateNavigationDrawer(savedInstanceState, headerResult);
        hideHeaderDrawerInformation();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

        if (findViewById(R.id.exp_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            if (!apisenseSdk.getSessionManager().isConnected()) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.exp_container, new HomeFragment())
                        .commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSignInSucceeded() {
        super.onSignInSucceeded();

        if (!drawerInitializedWithUser) {
            drawerInitializedWithUser = true;
            drawer.removeItem(DRAWER_PLAY_IDENTIFIER);
            drawer.addItemAtPosition(playReward, DRAWER_PLAY_REWARD_IDENTIFIER);
            refreshGPGData();
        }
    }

    @Override
    public void onSignInFailed() {
        super.onSignInFailed();
        Log.w(TAG, "Error on GPG signin: " + String.valueOf(getSignInError()));
    }

    public void selectDrawerItem(int item) {
        drawer.setSelectionAtPosition(item, false);
    }

    // Private methods

    /**
     * Refresh Google Play Games user information
     * in the Drawer
     */
    private void refreshGPGData() {
        refreshPlayGamesData(new Pending<Player>() {
            @Override
            public void onFetched(Player player) {
                setHeaderDrawerInformation(player);
            }
        });
    }

    /**
     * Hide avatar bubble and text switcher in the drawer
     */
    private void hideHeaderDrawerInformation() {
        (headerResult.getView().findViewById(R.id.material_drawer_account_header_text_switcher)).setVisibility(View.GONE);
    }

    /**
     * Set Google Play Games user information
     *
     * @param player Player from Google
     */
    private void setHeaderDrawerInformation(final Player player) {
        ImageManager.create(HomeActivity.this).loadImage(new ImageManager.OnImageLoadedListener() {
            @Override
            public void onImageLoaded(Uri uri, Drawable drawable, boolean b) {
                setHeaderContent(drawable, player);
            }
        }, player.getIconImageUri());
    }

    /**
     * Draw header content using Google Play Games data.
     *
     * @param drawable The user icon to draw.
     * @param player   The player info.
     */
    private void setHeaderContent(Drawable drawable, Player player) {
        headerResult.addProfiles(
                new ProfileDrawerItem()
                        .withName(getResources().getString(R.string.level)
                                + " " + player.getLevelInfo().getCurrentLevel().getLevelNumber()
                        )
                        .withEmail(player.getDisplayName())
                        .withIcon(drawable)

        );
        hideHeaderDrawerInformation();
    }

    /**
     * Create navigation drawer on every fragment handled by HomeActivity
     */
    private Drawer generateNavigationDrawer(Bundle savedInstanceState, AccountHeader headerResult) {
        return new DrawerBuilder()
                .withActivity(this)
                .withSavedInstance(savedInstanceState)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        home, store,
                        new DividerDrawerItem(),
                        play, settings, profile,
                        new DividerDrawerItem(),
                        about
                )
                .withTranslucentStatusBar(false)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int identifier = (int) drawerItem.getIdentifier();
                        switch (identifier) { // Based on addDrawerItems order starting from 1..n
                            case DRAWER_HOME_IDENTIFIER:
                                startAndAddFragmentToBackStack(new HomeFragment(), false);
                                break;
                            case DRAWER_STORE_IDENTIFIER:
                                switchToStore();
                                break;
                            case DRAWER_PLAY_IDENTIFIER:
                                beginUserInitiatedSignIn();
                                break;
                            case DRAWER_PLAY_REWARD_IDENTIFIER:
                                startActivityForResult(
                                        Games.Achievements.getAchievementsIntent(getApiClient()), 0
                                );
                                break;
                            case DRAWER_PRIVACY_IDENTIFIER:
                                startAndAddFragmentToBackStack(new PrivacyFragment(), true);
                                break;
                            case DRAWER_ACCOUNT_IDENTIFIER:
                                startAndAddFragmentToBackStack(new AccountFragment(), true);
                                break;
                            case DRAWER_ABOUT_IDENTIFIER:
                                startAndAddFragmentToBackStack(new AboutFragment(), true);
                                break;
                            default: // Separator cases, nothing to do.
                                break;
                        }
                        drawer.closeDrawer();
                        return true;
                    }
                })
                .build();
    }

    /**
     * Generate Header in the drawer
     *
     * @return
     */
    @NonNull
    private AccountHeader generateAccountHeader() {
        return headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_background)
                .build();
    }

    /**
     * Generate PrimaryDrawerItem for the Drawer
     *
     * @param name       Path to resource name
     * @param icon       Path to resource icon
     * @param identifier Static item identifier
     * @return Drawer item
     */
    private PrimaryDrawerItem generateDrawerItem(int name, int icon, int identifier) {
        return new PrimaryDrawerItem().withName(name)
                .withIcon(icon).withIdentifier(identifier);
    }

    /**
     * Start a new fragment and add it to the back stack
     *
     * @param instance       Fragment instance to start
     * @param addToBackStack Replace fragment if true, add otherwise
     */
    private void startAndAddFragmentToBackStack(Fragment instance, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            getSupportFragmentManager().popBackStackImmediate();
            transaction.replace(R.id.exp_container, instance);
            transaction.addToBackStack(null);
        } else {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            transaction.replace(R.id.exp_container, instance);
        }
        transaction.commit();
    }

    @Override
    public void switchToStore() {
        startAndAddFragmentToBackStack(new StoreFragment(), true);
    }
}
