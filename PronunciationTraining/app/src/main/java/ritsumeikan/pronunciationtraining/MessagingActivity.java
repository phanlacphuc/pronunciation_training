package ritsumeikan.pronunciationtraining;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Profile;

import java.util.ArrayList;
import java.util.Locale;

public class MessagingActivity extends ActionBarActivity {

    static String TAG = "MessagingActivity";
    static final int REQ_CODE_SPEECH_INPUT = 100;

    private Dialog mDialogInviteFriendByEmail;

    class SpeechMessage {
        public String username;
        public String message;
        public long time_in_secs;
    }

    private String mClassId;
    private ArrayList<String> mUsernameArrayList = new ArrayList<String>();
    private ArrayList<SpeechMessage> mSpeechMessageArrayList = new ArrayList<SpeechMessage>();
    private ArrayList<String> mWordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mClassId = getIntent().getStringExtra("classId");
        mWordList = getIntent().getStringArrayListExtra("wordList");

        findViewById(R.id.invite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInviteFriendByEmail();
            }
        });
        
        Api.getInstance().setAddUserEventListener(new Api.AddUserEventListener() {
            @Override
            public void handleEvent(final String username) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mUsernameArrayList.add(username);
                    }
                });
            }
        });

        Api.getInstance().setNewMessageEventListener(new Api.NewMessageEventListener() {
            @Override
            public void handleEvent(final String username, final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SpeechMessage speechMessage = new SpeechMessage();
                        speechMessage.username = username;
                        speechMessage.message = message;
                        speechMessage.time_in_secs = 0; // TODO : set to current time
                        mSpeechMessageArrayList.add(speechMessage);

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
                        // TODO: remove username
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
            Api.getInstance().inviteFriend(mClassId, email, new Api.OnCustomSuccessListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "invited friend");
                }
            });
        }
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something&#8230;");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showToastMessage("Sorry! Your device doesn\'t support peech input");
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // TODO: add message to scroll view
                    //txtSpeechInput.setText(result.get(0));

                } else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                    showToastMessage("Audio Error");
                } else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                    showToastMessage("Client Error");
                } else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                    showToastMessage("Network Error");
                } else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                    showToastMessage("No Match");
                } else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                    showToastMessage("Server Error");
                }

                break;
            }

        }
    }

    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
