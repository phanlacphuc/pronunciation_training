package ritsumeikan.pronunciationtraining;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by phanlacphuc on 12/28/15.
 */
public class Api {

    public interface OnCustomSuccessListener{
        public void onSuccess();
        public void onFail(String errorMessage);
    }

    public interface OnJoinClassSuccessListener{
        public void onSuccess(String classId, ArrayList<String> wordList);
        public void onFail(String errorMessage);
    }

    public interface AddUserEventListener{
        public void handleEvent(String userId, String username, String avatar);
    }

    public interface NewMessageEventListener{
        public void handleEvent(String userId, String message, String username, String avatar);
    }

    public interface StartGameEventListener{
        public void handleEvent(ArrayList<String> wordList);
    }

    public interface DisconnectEventListener{
        public void handleEvent();
    }


    static String BASE_URL = "http://104.197.54.175:3000/";
    static String TAG = "Api";
    static Api ourInstance = new Api();

    private ArrayList<MessagingActivity.SpeechMessage> mRightPronunciationMessageArray;
    private HashMap mUserInfoHashMap;

    private AsyncHttpClient mClient = new AsyncHttpClient();
    private AddUserEventListener mAddUserEventListener;
    private NewMessageEventListener mNewMessageEventListener;
    private StartGameEventListener mStartGameEventListener;
    private DisconnectEventListener mDisconnectEventListener;

    private Emitter.Listener onAddUser = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject receivedJSONObject = (JSONObject) args[0];
            Log.d(TAG, "onAddUser: receivedJSONObject = " + receivedJSONObject);
            String userId;
            String username;
            String avatar;
            try {
                userId = receivedJSONObject.getString("userId");
                username = receivedJSONObject.getString("username");
                avatar = receivedJSONObject.getString("avatar");
                if (mAddUserEventListener != null) {
                    mAddUserEventListener.handleEvent(userId, username, avatar);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject receivedJSONObject = (JSONObject) args[0];
            Log.d(TAG, "onNewMessage: receivedJSONObject = " + receivedJSONObject);
            String userId;
            String message;
            String username;
            String avatar;
            try {
                userId = receivedJSONObject.getString("userid");
                message = receivedJSONObject.getString("message");
                username = receivedJSONObject.getString("username");
                avatar = receivedJSONObject.getString("avatar");
                if (mNewMessageEventListener != null) {
                    mNewMessageEventListener.handleEvent(userId, message, username, avatar);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

        }
    };

    private Emitter.Listener onStartGame = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject receivedJSONObject = (JSONObject) args[0];
            Log.d(TAG, "onStartGame: receivedJSONObject = " + receivedJSONObject);
            ArrayList<String> wordList = new ArrayList<String>();
            try {
                JSONArray wordListJsonArray = receivedJSONObject.getJSONArray("wordList");
                // TODO: save word list to array

                if (mStartGameEventListener != null) {
                    mStartGameEventListener.handleEvent(wordList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d(TAG, "onDisconnect");
            // TODO: Disconnect


            if (mDisconnectEventListener != null) {
                mDisconnectEventListener.handleEvent();
            }


        }
    };

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static Api getInstance() {
        return ourInstance;
    }

    private Api() {
    }

    public void login(Context context, String facebookId, String name, String avatar, final OnCustomSuccessListener onCustomSuccessListener) {
        Log.d(TAG, "login api+ facebookId fbname avatar: " +facebookId + " "+ name + " " +avatar);
        RequestParams params = new RequestParams();
        params.add("facebookId", facebookId);
        params.add("name", name);
        params.add("avatar", avatar);
        mClient.post(BASE_URL + "login", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "response = " + response);
                try {
                    String errorMessage = response.getString("error");
                    if (errorMessage == null || errorMessage.length()==0) {
                        onCustomSuccessListener.onSuccess();
                    } else {
                        onCustomSuccessListener.onFail(errorMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void createClass(JSONArray wordArray, String facebookId, final OnJoinClassSuccessListener onJoinClassSuccessListener) {
        RequestParams params = new RequestParams();
        params.add("wordArray", wordArray.toString());
        params.add("facebookId", facebookId);
        mClient.post(BASE_URL + "createClass", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "response = " + response);
                try {
                    String errorMessage = response.getString("error");
                    if (errorMessage == null || errorMessage.length()==0) {
                        JSONObject data = response.getJSONObject("data");
                        String classId = data.getString("classId");

                        ArrayList<String> wordList = new ArrayList<String>();
                        JSONArray wordArray = data.getJSONArray("wordList");
                        if (wordArray != null) {
                            for (int i=0;i<wordArray.length();i++){
                                wordList.add(wordArray.get(i).toString());
                            }
                        }
                        onJoinClassSuccessListener.onSuccess(classId, wordList);
                    } else {
                        onJoinClassSuccessListener.onFail(errorMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void inviteFriend(String classId, String email, final OnCustomSuccessListener onCustomSuccessListener) {
        RequestParams params = new RequestParams();
        params.add("classId", classId);
        params.add("email", email);
        mClient.post(BASE_URL + "inviteFriend", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "response = " + response);
                try {
                    String errorMessage = response.getString("error");
                    if (errorMessage == null || errorMessage.length()==0) {
                        onCustomSuccessListener.onSuccess();
                    } else {
                        onCustomSuccessListener.onFail(errorMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void joinClass(String facebookId, String inviteCode, final OnJoinClassSuccessListener onJoinClassSuccessListener) {
        RequestParams params = new RequestParams();
        params.add("facebookId", facebookId);
        params.add("inviteCode", inviteCode);
        mClient.post(BASE_URL + "joinClass", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d(TAG, "response = " + response);
                try {
                    String errorMessage = response.getString("error");
                    if (errorMessage == null || errorMessage.length()==0) {
                        JSONObject data = response.getJSONObject("data");
                        String classId = data.getString("classId");

                        ArrayList<String> wordList = new ArrayList<String>();
                        JSONArray wordArray = data.getJSONArray("wordList");
                        if (wordArray != null) {
                            for (int i = 0; i < wordArray.length(); i++) {
                                wordList.add(wordArray.get(i).toString());
                            }
                        }
                        onJoinClassSuccessListener.onSuccess(classId, wordList);
                    } else {
                        onJoinClassSuccessListener.onFail(errorMessage);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void connectSocket() {
        mSocket.on("add user", onAddUser);
        mSocket.on("new message", onNewMessage);
        mSocket.on("start game", onStartGame);
        mSocket.on("disconnect", onDisconnect);
        mSocket.connect();
    }
    public void disconnectSocket() {
        mSocket.disconnect();
        mSocket.off("add user");
        mSocket.off("new message");
        mSocket.off("start game");
        mSocket.off("disconnect");
        mAddUserEventListener = null;
        mNewMessageEventListener = null;
        mStartGameEventListener = null;
        mDisconnectEventListener = null;
    }

    public void attemptAddUser(String userId) {
        Log.d(TAG, "attemptAddUser:" + userId);
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        mSocket.emit("add user", userId);
    }

    public void attemptSendMessage(String message) {
        Log.d(TAG, "attemptSendMessage:" + message);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mSocket.emit("new message", message);
    }

    public void attemptStartGame(String classId) {
        Log.d(TAG, "attemptStartGame:" + classId);
        mSocket.emit("start game", classId);
    }

    public void attemptDisconnectGame() {
        Log.d(TAG, "attemptDisconnect:");
        mSocket.emit("disconnect");
    }

    public void setAddUserEventListener(AddUserEventListener addUserEventListener) {
        mAddUserEventListener = addUserEventListener;
    }

    public void setNewMessageEventListener(NewMessageEventListener newMessageEventListener) {
        mNewMessageEventListener = newMessageEventListener;
    }

    public void setStartGameEventListener(StartGameEventListener startGameEventListener) {
        mStartGameEventListener = startGameEventListener;
    }

    public void setDisconnectEventListener(DisconnectEventListener disconnectEventListener) {
        mDisconnectEventListener = disconnectEventListener;
    }

    public void setRightPronunciationMessageArray(ArrayList<MessagingActivity.SpeechMessage> rightPronunciationMessageArray) {
        mRightPronunciationMessageArray = rightPronunciationMessageArray;
    }

    ArrayList<MessagingActivity.SpeechMessage> getRightPronunciationMessageArray() {
        return mRightPronunciationMessageArray;
    }

    public void setUserInfoHashMap(HashMap hashMap){
        mUserInfoHashMap = hashMap;
    }

    HashMap getUserInfoHashMap() {
        return mUserInfoHashMap;
    }
}
