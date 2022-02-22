package ReviewInAppPlugin;

import android.util.Log;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * This class echoes a string called from JavaScript.
 */
public class ReviewInAppPluginClass extends CordovaPlugin {

    String LOG="ReviewInAppPluginClass";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("requestReview")) {

            Boolean isFake = args.getBoolean(0);

            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    requestReview(isFake, callbackContext);
                }
            });


            return true;
        }
        return false;
    }

    private void requestReview(Boolean isFake, CallbackContext callbackContext) {

        ReviewManager manager;
        if(isFake){
            Log.d(LOG,"isFake");
            manager = new FakeReviewManager(cordova.getContext());
        }else {
            Log.d(LOG,"notIsFake");
            manager = ReviewManagerFactory.create(cordova.getContext());
        }

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
           if(task.isComplete()){
               ReviewInfo reviewInfo = task.getResult();
               Task<Void> flow=manager.launchReviewFlow(this.cordova.getActivity(),reviewInfo);
               flow.addOnCompleteListener(taskflowsuccess->{
                   if(taskflowsuccess.isComplete()){
                       Log.d(LOG,"Completado");
                       if(taskflowsuccess.isSuccessful()){
                           Log.d(LOG,"Proceso exitoso");
                           callbackContext.success();
                       }else{
                           callbackContext.success();
                       }
                   }
               }).addOnFailureListener(taskflowfail->{
                   callbackContext.error(taskflowfail.getMessage());
               });
               Log.d(LOG,reviewInfo.toString());
           }else{
               String reviewErrorCode =  task.getException().toString();
               Log.d(LOG,reviewErrorCode);
           }
        }).addOnFailureListener(taskfail->{
            callbackContext.error(taskfail.getMessage());
        });
    }
}