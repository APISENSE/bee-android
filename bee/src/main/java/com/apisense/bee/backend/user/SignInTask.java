//package com.apisense.bee.backend.user;
//
//import com.apisense.core.api.Log;
//import com.apisense.bee.BeeApplication;
//import com.apisense.bee.backend.AsyncTaskWithCallback;
//import com.apisense.bee.backend.AsyncTasksCallbacks;
//import fr.inria.apislog.APISLog;
//import fr.inria.bsense.service.BSenseServerService;
//import fr.inria.bsense.service.BeeSenseServiceManager;
//
///**
// * Represents an asynchronous login task used to authenticate the user.
// *
// */
//public class SignInTask extends AsyncTaskWithCallback<String, Void, String> {
//    private final String TAG = this.getClass().getSimpleName();
//    private final BSenseServerService servService;
//
//
//    public SignInTask(BeeSenseServiceManager apiServices, AsyncTasksCallbacks listener) {
//        super(listener);
//        servService = apiServices.getBSenseServerService();
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//        // params[0] == login
//        // params[1] == password
//        // params[2] == URL hive (optionnal)
//        String details = "";
//        if (params.length < 2) {
//            Log.e(TAG, "Not enough parameters");
//            this.errcode = BeeApplication.ASYNC_ERROR;
//        }else {
//            if (params[0].isEmpty() || params[1].isEmpty()) {
//                Log.e(this.TAG, "login or password is empty");
//                this.errcode = BeeApplication.ASYNC_ERROR;
//            } else {
//                String pseudo = params[0];
//                String password = params[1];
//                String apisenseUrl = (params.length >= 3 && !params[2].isEmpty()) ?
//                        params[2] : BeeApplication.BEE_DEFAULT_URL;
//                try {
//                    servService.setCentralHost(apisenseUrl);
//                    servService.connect(pseudo, password);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    details = e.getMessage();
//                    APISLog.send(e, APISLog.ERROR);
//                }
//
//                if (!servService.isConnected())
//                    this.errcode = BeeApplication.ASYNC_ERROR;
//                else {
//                    try {
//                        servService.updateUserAccount();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        APISLog.send(e, APISLog.ERROR);
//                    }
//                    this.errcode = BeeApplication.ASYNC_SUCCESS;
//                }
//            }
//        }
//        return details;
//    }
//}
