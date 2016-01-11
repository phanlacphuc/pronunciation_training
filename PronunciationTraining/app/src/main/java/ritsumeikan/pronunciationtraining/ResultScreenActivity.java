package ritsumeikan.pronunciationtraining;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultScreenActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);

        ArrayList<MessagingActivity.SpeechMessage> rightPronunciationMessageArray =
                Api.getInstance().getRightPronunciationMessageArray();
        HashMap userInfoHashMap = Api.getInstance().getUserInfoHashMap();
        ArrayList<String> wordList = getIntent().getStringArrayListExtra("wordList");
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_layout);

        TableRow row;
        row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_result_layout, null);
        ((TextView)row.findViewById(R.id.first_column_text_view)).setText("WORD");
        ((TextView)row.findViewById(R.id.second_column_text_view)).setText("USER");
        ((TextView)row.findViewById(R.id.third_column_text_view)).setText("TIME");
        tableLayout.addView(row);

        for (String word:wordList) {
            int countRightUser = 0;
            for (MessagingActivity.SpeechMessage speechMessage:rightPronunciationMessageArray) {
                if (speechMessage.message.equalsIgnoreCase(word)) {
                    countRightUser++;
                    row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_result_layout, null);
                    if (countRightUser == 1) {
                        ((TextView)row.findViewById(R.id.first_column_text_view)).setText(word);
                    } else {
                        ((TextView)row.findViewById(R.id.first_column_text_view)).setText("");
                    }
                    MessagingActivity.UserInfo userInfo = (MessagingActivity.UserInfo) userInfoHashMap.get(speechMessage.user_id);
                    ((TextView)row.findViewById(R.id.second_column_text_view)).setText(userInfo.username);
                    ((TextView)row.findViewById(R.id.third_column_text_view)).setText(speechMessage.time_in_secs + " secs");
                    tableLayout.addView(row);
                }
            }

            if (countRightUser == 0) {
                row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_result_layout, null);
                ((TextView)row.findViewById(R.id.first_column_text_view)).setText(word);
                ((TextView)row.findViewById(R.id.second_column_text_view)).setText("");
                ((TextView)row.findViewById(R.id.third_column_text_view)).setText("");
                tableLayout.addView(row);
            }
        }

    }

}
