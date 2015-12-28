package ritsumeikan.pronunciationtraining;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

/**
 * Created by phanlacphuc on 12/28/15.
 */
public class Api {
    private static Api ourInstance = new Api();
    private static String BASE_URL = "http://130.211.164.255:3000/";

    private AsyncHttpClient mClient = new AsyncHttpClient();

    public static Api getInstance() {
        return ourInstance;
    }

    private Api() {
    }

    public void login(Context context, String facebookId, String name, String avatar) {
        RequestParams params = new RequestParams();
        params.add("facebookId", facebookId);
        params.add("name", name);
        params.add("avatar", avatar);
        mClient.post(BASE_URL + "login", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
    public void createClass(Context context, JSONArray wordArray, String facebookId) {
        RequestParams params = new RequestParams();
        params.add("wordArray", wordArray.toString());
        params.add("facebookId", facebookId);
        mClient.post(BASE_URL + "createClass", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
    public void inviteFriend(Context context, String classId, String email) {
        RequestParams params = new RequestParams();
        params.add("classId", classId);
        params.add("email", email);
        mClient.post(BASE_URL + "inviteFriend", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
    public void joinClass(Context context, String facebookId, String inviteCode) {
        RequestParams params = new RequestParams();
        params.add("facebookId", facebookId);
        params.add("inviteCode", inviteCode);
        mClient.post(BASE_URL + "joinClass", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}
