package ritsumeikan.pronunciationtraining;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

/**
 * Created by phanlacphuc on 12/28/15.
 */
public class Api {
    private static String TAG = "Api";
    private static Api ourInstance = new Api();
    private static String BASE_URL = "http://130.211.164.255:3000/";

    private AsyncHttpClient mClient = new AsyncHttpClient();

    public static Api getInstance() {
        return ourInstance;
    }

    private Api() {
    }

    public interface OnCustomSuccessListener{
        public void onSuccess();   //method, which can have parameters
    }

    public void login(Context context, String facebookId, String name, String avatar, final OnCustomSuccessListener onCustomSuccessListener) {
        RequestParams params = new RequestParams();
        params.add("facebookId", facebookId);
        params.add("name", name);
        params.add("avatar", avatar);
        mClient.post(BASE_URL + "login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "response = " + response);
                onCustomSuccessListener.onSuccess();
            }
        });
    }
    public void createClass(JSONArray wordArray, String facebookId, final OnCustomSuccessListener onCustomSuccessListener) {
        RequestParams params = new RequestParams();
        params.add("wordArray", wordArray.toString());
        params.add("facebookId", facebookId);
        mClient.post(BASE_URL + "createClass", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "response = " + response);
                onCustomSuccessListener.onSuccess();
            }
        });
    }
    public void inviteFriend(String classId, String email, final OnCustomSuccessListener onCustomSuccessListener) {
        RequestParams params = new RequestParams();
        params.add("classId", classId);
        params.add("email", email);
        mClient.post(BASE_URL + "inviteFriend", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "response = " + response);
                onCustomSuccessListener.onSuccess();
            }
        });
    }
    public void joinClass(String facebookId, String inviteCode, final OnCustomSuccessListener onCustomSuccessListener) {
        RequestParams params = new RequestParams();
        params.add("facebookId", facebookId);
        params.add("inviteCode", inviteCode);
        mClient.post(BASE_URL + "joinClass", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "response = " + response);
                onCustomSuccessListener.onSuccess();
            }
        });
    }
}
