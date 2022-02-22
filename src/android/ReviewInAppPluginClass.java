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
        else if (action.equals("requestReviewInApp")){

            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    requestReviewInApp(callbackContext);
                }
            });
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

    private void requestReviewInApp(CallbackContext callbackContext) {

        Log.d(LOG,"Inicia requestReviewInApp");
        //ReviewManager manager = ReviewManagerFactory.create(cordova.getContext());
        ReviewManager manager = new FakeReviewManager(cordova.getContext());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                Log.d(LOG,"Then");
                ReviewInfo reviewInfo = task.getResult();
                callbackContext.success();
            } else {
                // There was some problem, log or handle the error code.
                //String reviewErrorCode =  task.getException().toString();
                //@ReviewErrorCode int reviewErrorCode = ((TaskException) task.getException()).getErrorCode();
                Log.d(LOG,"Else");
                callbackContext.success();
                //Log.d(LOG, reviewErrorCode);
            }
        });

        //Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
        //flow.addOnCompleteListener(task -> {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        //});

    }
}