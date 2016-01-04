package ritsumeikan.pronunciationtraining;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.Profile;

import java.util.ArrayList;

public class MessagingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Api.getInstance().setAddUserEventListener(new Api.AddUserEventListener() {
            @Override
            public void handleEvent(String username) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

        Api.getInstance().setNewMessageEventListener(new Api.NewMessageEventListener() {
            @Override
            public void handleEvent(String username, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

        Api.getInstance().setStartGameEventListener(new Api.StartGameEventListener() {
            @Override
            public void handleEvent(ArrayList<String> wordList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });

        Api.getInstance().setDisconnectEventListener(new Api.DisconnectEventListener() {
            @Override
            public void handleEvent() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });


        Api.getInstance().connectSocket();

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Api.getInstance().attemptAddUser(profile.getId());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Api.getInstance().disconnectSocket();
    }
}
