package com.apisense.bee.backend.store;

import com.apisense.bee.backend.AsyncTaskWithCallback;
import com.apisense.bee.backend.AsyncTasksCallbacks;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to fetch every existing Tag settled on experiments
 *
 */
public class RetrieveExistingTagsTask extends AsyncTaskWithCallback<Void, Void, List<String>>{

    public RetrieveExistingTagsTask(AsyncTasksCallbacks listener) {
        super(listener);
    }

    @Override
    protected List<String> doInBackground(Void... params) {
        List<String> tags = new ArrayList<>();
        // TODO: Débouchonner Tags retrieval
        this.errcode = 0;
        tags.add("All");
        tags.add("Network");
        tags.add("Social");
        tags.add("Quality");
        tags.add("Environment");
        tags.add("Special");

        return tags;
    }
}
