package com.apisense.bee.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.R;
import com.apisense.bee.ui.activity.HomeActivity;
import io.apisense.sdk.APISENSE;

public class AboutFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_about, container, false);

        homeActivity.getSupportActionBar().setTitle(R.string.title_activity_about);
        homeActivity.selectDrawerItem(HomeActivity.DRAWER_ABOUT_IDENTIFIER);

        TextView beeVersion = (TextView) root.findViewById(R.id.about_bee_version);
        TextView apisenseVersion = (TextView) root.findViewById(R.id.about_apisense_version);
        TextView copyright = (TextView) root.findViewById(R.id.about_copyright);
        Linkify.addLinks(copyright, Linkify.ALL);

        Resources res = getResources();
        if (getAppInfo() != null)
            beeVersion.setText(String.format(res.getString(R.string.bee_version), getAppInfo().versionName));
        apisenseVersion.setText(String.format(res.getString(R.string.apisense_version), APISENSE.VERSION_NAME));

        copyright.setText(Html.fromHtml(res.getString(R.string.about_copyright)));
        copyright.setMovementMethod(LinkMovementMethod.getInstance());
        return root;
    }

    /**
     * Helper to get the app version info
     *
     * @return a PackageInfo object
     */
    private PackageInfo getAppInfo() {
        PackageManager manager = getActivity().getPackageManager();
        try {
            return manager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
