package com.apisense.bee.ui.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apisense.bee.R;

public class AboutSettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings_about, container, false);

        TextView beeVersion = (TextView) root.findViewById(R.id.about_bee_version);
        TextView apisenseVersion = (TextView) root.findViewById(R.id.about_apisense_version);
        TextView copyright = (TextView) root.findViewById(R.id.about_copyright);

        Resources res = getResources();
        if (getAppInfo() != null)
            beeVersion.setText(String.format(res.getString(R.string.bee_version), getAppInfo().versionName));
        // TODO: APISENSE sdk must propose a versionName value
        apisenseVersion.setText(String.format(res.getString(R.string.apisense_version), "1.2.0-SNAPSHOT"));

        copyright.setMovementMethod(LinkMovementMethod.getInstance());
        copyright.setText(Html.fromHtml(res.getString(R.string.about_copyright)));
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
