package com.apisense.bee.ui.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.apisense.bee.BeeApplication;
import com.apisense.bee.R;
import com.apisense.bee.games.SimpleGameAchievement;
import com.apisense.bee.ui.activity.HomeActivity;
import com.apisense.bee.ui.activity.LoginActivity;
import com.apisense.sdk.APISENSE;
import com.apisense.sdk.core.APSCallback;
import com.apisense.sdk.exception.UserNotConnectedException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AccountFragment extends BaseFragment {

    @BindView(R.id.account_logout) Button mLogout;
    @BindView(R.id.account_share) Button mShare;

    private APISENSE.Sdk apisenseSdk;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_account, container, false);
        apisenseSdk = ((BeeApplication) getActivity().getApplication()).getSdk();
        unbinder = ButterKnife.bind(this, root);

        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_account);
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_ACCOUNT_IDENTIFIER);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.account_share)
    void doApplicationShare() {
        new SimpleGameAchievement(getString(R.string.achievement_recruiting_bee)).unlock(this);
        Resources resources = getResources();
        Intent sendIntent = new Intent(Intent.ACTION_SEND)
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_bee_text));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.action_share)));
    }

    @OnClick(R.id.account_logout)
    void doDisconnect() {
        apisenseSdk.getSessionManager().logout(new SignedOutCallback());
    }

    public class SignedOutCallback implements APSCallback<Void> {
        @Override
        public void onDone(Void aVoid) {
            Toast.makeText(getActivity(), R.string.status_changed_to_anonymous, Toast.LENGTH_SHORT).show();
            openSlideShow();
        }

        @Override
        public void onError(Exception e) {
            if (e instanceof UserNotConnectedException) {
                openSlideShow();
            }
        }

        private void openSlideShow() {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

}
