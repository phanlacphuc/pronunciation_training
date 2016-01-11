package ritsumeikan.pronunciationtraining;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MessagingActivity extends ActionBarActivity {

    static String TAG = "MessagingActivity";
    static final int REQ_CODE_SPEECH_INPUT = 100;

    private Dialog mDialogInviteFriendByEmail;

    class UserInfo {
        public String user_id;
        public String username;
        public String avatar;
    }
    class SpeechMessage {
        public String user_id;
        public String message;
        public long time_in_secs;
    }

    private TextView mCurrentWordTextView;
    private TextView mCurrentTimeLeftTextView;

    private CustomListAdapter mAdapter;
    private ListView mMessageListView;

    private String mClassId;
    private HashMap mUserInfoHashMap = new HashMap();
    private ArrayList<SpeechMessage> mSpeechMessageArrayList = new ArrayList<SpeechMessage>();
    private ArrayList<SpeechMessage> mRightPronunciationArrayList = new ArrayList<SpeechMessage>();
    private ArrayList<String> mWordList;
    private int mCurrentWordIndex;

    private int mTimeInSecs = 0;

    CountDownTimer mCountDownTimer = new CountDownTimer(16000, 1000) {

        public void onTick(long millisUntilFinished) {
            mCurrentTimeLeftTextView.setText("remaining: " + millisUntilFinished / 1000 + " secs");
            //here you can have your logic to set text to edittext
            mTimeInSecs++;
        }

        public void onFinish() {
            finishOneWordSession();
        }
    };

    class CustomListAdapter extends ArrayAdapter<SpeechMessage> {

        private Context mContext;
        private int mLayoutResourceId;
        private ArrayList<SpeechMessage> mSpeechMessageArrayList;

        public CustomListAdapter(Context context, int layoutResourceId, ArrayList<SpeechMessage> speechMessageArrayList){
            super(context, layoutResourceId, speechMessageArrayList);
            this.mContext = context;
            this.mLayoutResourceId = layoutResourceId;
            this.mSpeechMessageArrayList = speechMessageArrayList;
        }

        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            View rowView = inflater.inflate(mLayoutResourceId, parent, false);

            SpeechMessage speechMessage = mSpeechMessageArrayList.get(position);
            UserInfo userInfo = (UserInfo) mUserInfoHashMap.get(speechMessage.user_id);

            ImageView profilePictureImageView = (ImageView) rowView.findViewById(R.id.profile_picture_image_view);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
            // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
            //  which implements ImageAware interface)
            imageLoader.displayImage(userInfo.avatar, profilePictureImageView);

            TextView usernameTextView = (TextView) rowView.findViewById(R.id.user_name_text_view);
            usernameTextView.setText(userInfo.username);
            TextView messageTextView = (TextView) rowView.findViewById(R.id.message_text_view);
            messageTextView.setText(speechMessage.message);
            TextView timestampTextView = (TextView) rowView.findViewById(R.id.time_stamp_text_view);
            timestampTextView.setText(speechMessage.time_in_secs + " secs");

            return rowView;

        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mCurrentWordTextView = (TextView) findViewById(R.id.current_word_text_view);
        mCurrentTimeLeftTextView = (TextView) findViewById(R.id.current_time_left_text_view);

        mClassId = getIntent().getStringExtra("classId");
        mWordList = getIntent().getStringArrayListExtra("wordList");

        mAdapter = new CustomListAdapter(this, R.layout.message_bubble_layout, mSpeechMessageArrayList);
        mMessageListView = (ListView)findViewById(R.id.message_líst_view);
        mMessageListView.setAdapter(mAdapter);

        findViewById(R.id.invite_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInviteFriendByEmail();
            }
        });

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        findViewById(R.id.micro_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        Api.getInstance().setAddUserEventListener(new Api.AddUserEventListener() {
            @Override
            public void handleEvent(final String userId, final String username, final String avatar) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo userInfo = new UserInfo();
                        userInfo.user_id = userId;
                        userInfo.username = username;
                        userInfo.avatar = avatar;
                        mUserInfoHashMap.put(userId, userInfo);

                        addNewMessageBubble(userId, "joined class!", false);
                    }
                });
            }
        });

        Api.getInstance().setNewMessageEventListener(new Api.NewMessageEventListener() {
            @Override
            public void handleEvent(final String userId, final String message, final String username, final String avatar) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean rightPronunciation = message.equalsIgnoreCase(mWordList.get(mCurrentWordIndex));
                        addNewMessageBubble(userId, message, rightPronunciation);
                        UserInfo userInfo = new UserInfo();
                        userInfo.user_id = userId;
                        userInfo.username = username;
                        userInfo.avatar = avatar;
                        mUserInfoHashMap.put(userInfo.user_id, userInfo);
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
                        mCurrentWordIndex = -1;
                        displayNextWord();
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

            UserInfo userInfo = new UserInfo();
            userInfo.user_id = profile.getId();
            userInfo.username = profile.getName();
            userInfo.avatar = profile.getProfilePictureUri(100, 100).toString();
            mUserInfoHashMap.put(userInfo.user_id, userInfo);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Api.getInstance().disconnectSocket();
    }

    private void startGame() {
        Api.getInstance().attemptStartGame(mClassId);
    }

    private void displayNextWord() {
        mTimeInSecs = 0;
        mCurrentWordIndex++;
        mCurrentWordTextView.setText("word" + (mCurrentWordIndex+1) + ": " + mWordList.get(mCurrentWordIndex));
        mCountDownTimer.start();
    }

    private void finishOneWordSession() {
        if (mCurrentWordIndex < (mWordList.size() - 1)) {
            displayNextWord();
        } else {
            finishGame();
        }
    }

    private void finishGame() {
        Api.getInstance().attemptDisconnectGame();
        Api.getInstance().setRightPronunciationMessageArray(mRightPronunciationArrayList);
        Api.getInstance().setUserInfoHashMap(mUserInfoHashMap);
        Intent intent = new Intent(this, ResultScreenActivity.class);
        intent.putStringArrayListExtra("wordList", mWordList);
        startActivity(intent);
    }

    private void addNewMessageBubble(String userId, String message, boolean rightPronunciation) {

        SpeechMessage speechMessage = new SpeechMessage();
        speechMessage.user_id = userId;
        speechMessage.message = message;
        speechMessage.time_in_secs = mTimeInSecs;
        mSpeechMessageArrayList.add(speechMessage);
        mAdapter.notifyDataSetChanged();

        if (rightPronunciation) {
            mRightPronunciationArrayList.add(speechMessage);
        }

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

                @Override
                public void onFail(String errorMessage) {
                    showToastMessage(errorMessage);
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something!");
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
                    String recognizedWord = result.get(0);
                    Boolean rightPronunciation = recognizedWord.equalsIgnoreCase(mWordList.get(mCurrentWordIndex));
                    if (!rightPronunciation){
                        showToastMessage("wrong word: " + recognizedWord);
                    } else {
                        showToastMessage("right pronounced!");
                    }

                    Api.getInstance().attemptSendMessage(recognizedWord);
                    addNewMessageBubble(Profile.getCurrentProfile().getId(), recognizedWord, rightPronunciation);

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
        if (message != null) Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
