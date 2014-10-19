package com.apisense.bee;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

public class BeeRobolectricTestRunner extends RobolectricTestRunner {

    /**
     * Creates a runner to run {@code testClass}. Looks in your working directory for your AndroidManifest.xml file
     * and res directory by default. Use the {@link org.robolectric.annotation.Config} annotation to configure.
     *
     * @param testClass the test class to be run
     * @throws org.junit.runners.model.InitializationError if junit says so
     */
    public BeeRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {

        String manifestPath = "../bee/src/main/AndroidManifest.xml";
        String resPath = "../bee/src/main/res";
        return new AndroidManifest(Fs.fileFromPath(manifestPath), Fs.fileFromPath(resPath)) {
            @Override
            public int getTargetSdkVersion() {
                // Robolectric does not support API 19...
                return 18;
            }
        };
    }
}
