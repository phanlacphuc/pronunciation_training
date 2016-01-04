package ritsumeikan.pronunciationtraining;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.Profile;

import java.util.ArrayList;

public class MessagingActivity extends ActionBarActivity {

    static String TAG = "MessagingActivity";

    private Dialog mDialogInviteFriendByEmail;

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

    private void showDialogInviteFriendByEmail() {
        if (mDialogInviteFriendByEmail == null) {
            mDialogInviteFriendByEmail = new Dialog(this);
            mDialogInviteFriendByEmail.setContentView(R.layout.dialog_invite_friend_by_email_layout);
            mDialogInviteFriendByEmail.setTitle("Send Invitation Code To Email");
            final EditText emailEditText  = (EditText) mDialogInviteFriendByEmail.findViewById(R.id.email_edit_text);

            Button okButton = (Button) mDialogInviteFriendByEmail.findViewById(R.id.ok_button);
            // if button is clicked, close the custom dialog
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogInviteFriendByEmail.dismiss();
                    invite(emailEditText.getText().toString());
                }
            });

            Button cancelButton = (Button) mDialogInviteFriendByEmail.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogInviteFriendByEmail.dismiss();
                }
            });
        }
        mDialogInviteFriendByEmail.show();
    }

    private void invite(String email) {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            String classId = getIntent().getStringExtra("class_id");
            Api.getInstance().inviteFriend(classId, email, new Api.OnCustomSuccessListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "invited friend");
                }
            });
        }
    }
}
