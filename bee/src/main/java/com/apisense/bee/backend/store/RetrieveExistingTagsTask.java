package com.apisense.bee.backend.store;

import com.apisense.bee.BeeApplication;
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
        List<String> tags = new ArrayList<String>();
        // TODO: Débouchonner Tags retrieval
        this.errcode = BeeApplication.ASYNC_SUCCESS;
        tags.add("No tag");
        tags.add("tata");
        tags.add("titi");
        tags.add("toto");
        tags.add("tutu");
        tags.add("My Little Tag");

        return tags;
    }
}
