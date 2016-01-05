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

import org.json.JSONArray;

import java.util.ArrayList;

public class JoinClassActivity extends ActionBarActivity {

    static String TAG = "JoinClassActivity";

    private Dialog mDialogInvitationCode;
    private Dialog mDialogCreateNewClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);
        findViewById(R.id.create_class_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCreateNewClass();
            }
        });
        findViewById(R.id.join_class_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInvitationCodeInput();
            }
        });
    }

    private void showDialogCreateNewClass() {
        if (mDialogCreateNewClass == null) {
            mDialogCreateNewClass = new Dialog(this);
            mDialogCreateNewClass.setContentView(R.layout.dialog_new_class_setting_layout);
            mDialogCreateNewClass.setTitle("New Class");

            Button okButton = (Button) mDialogCreateNewClass.findViewById(R.id.ok_button);
            // if button is clicked, close the custom dialog
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogCreateNewClass.dismiss();
                    ArrayList<String> wordList = new ArrayList<String>();
                    wordList.add(((EditText)mDialogCreateNewClass.findViewById(R.id.first_word_edit_text)).getText().toString());
                    wordList.add(((EditText)mDialogCreateNewClass.findViewById(R.id.second_word_edit_text)).getText().toString());
                    wordList.add(((EditText)mDialogCreateNewClass.findViewById(R.id.third_word_edit_text)).getText().toString());
                    wordList.add(((EditText)mDialogCreateNewClass.findViewById(R.id.fourth_word_edit_text)).getText().toString());
                    wordList.add(((EditText)mDialogCreateNewClass.findViewById(R.id.fifth_word_edit_text)).getText().toString());
                    createClass(wordList);
                }
            });

            Button cancelButton = (Button) mDialogCreateNewClass.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogCreateNewClass.dismiss();
                }
            });
        }
        mDialogCreateNewClass.show();
    }

    private void showDialogInvitationCodeInput() {
        if (mDialogInvitationCode == null) {
            mDialogInvitationCode = new Dialog(this);
            mDialogInvitationCode.setContentView(R.layout.dialog_invitation_code_input_layout);
            mDialogInvitationCode.setTitle("Invitation Code");
            final EditText invitationCodeEditText  = (EditText) mDialogInvitationCode.findViewById(R.id.invitation_code_edit_text);

            Button okButton = (Button) mDialogInvitationCode.findViewById(R.id.ok_button);
            // if button is clicked, close the custom dialog
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogInvitationCode.dismiss();
                    joinClass(invitationCodeEditText.getText().toString());
                }
            });

            Button cancelButton = (Button) mDialogInvitationCode.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogInvitationCode.dismiss();
                }
            });
        }
        mDialogInvitationCode.show();
    }

    private void createClass(ArrayList<String> wordList) {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            JSONArray wordArray = new JSONArray(wordList);
            Api.getInstance().createClass(wordArray, profile.getId(), new Api.OnJoinClassSuccessListener() {
                @Override
                public void onSuccess(String classId, ArrayList<String> wordList) {
                    Log.d(TAG, "created class");
                    Intent intent = new Intent(JoinClassActivity.this, MessagingActivity.class);
                    intent.putExtra("class_id", classId);
                    intent.putStringArrayListExtra("wordList", wordList);
                    startActivity(intent);
                }
            });
        }
    }
    private void joinClass(String invitationCode) {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            Api.getInstance().joinClass(profile.getId(), invitationCode, new Api.OnJoinClassSuccessListener() {
                @Override
                public void onSuccess(String classId, ArrayList<String> wordList) {
                    Log.d(TAG, "joined class");
                    Intent intent = new Intent(JoinClassActivity.this, MessagingActivity.class);
                    intent.putExtra("class_id", classId);
                    intent.putStringArrayListExtra("wordList", wordList);
                    startActivity(intent);
                }
            });
        }
    }
}
