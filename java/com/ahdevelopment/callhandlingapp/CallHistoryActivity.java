import java.text.DateFormat;

import javax.swing.text.View;
import javax.swing.text.html.ListView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

public class CallHistoryActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_CALL_LOG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        // Check if the READ_CALL_LOG permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {

            // If not, request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 
                    REQUEST_CODE_READ_CALL_LOG);
        } else {
            // If permission is already granted, load the call logs
            loadCallHistory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the call logs
                loadCallHistory();
            }
        }
    }

    private void loadCallHistory() {
         Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
            null, null, null, CallLog.Calls.DATE + " DESC");

        if (cursor != null) {
            String[] fromColumns = {
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.DATE
            };

            int[] toViews = {R.id.callNumber, R.id.callType, R.id.callDuration, R.id.callDate};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.call_log_item, cursor, fromColumns, toViews, 0);

            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (view.getId() == R.id.callType) {
                        // Handle call type (incoming, outgoing, missed)
                        int type = cursor.getInt(columnIndex);
                        String callType = "";
                        switch (type) {
                            case CallLog.Calls.INCOMING_TYPE:
                                callType = "Incoming";
                                break;
                            case CallLog.Calls.OUTGOING_TYPE:
                                callType = "Outgoing";
                                break;
                            case CallLog.Calls.MISSED_TYPE:
                                callType = "Missed";
                                break;
                        }
                        ((TextView) view).setText(callType);
                        return true;
                    } else if (view.getId() == R.id.callDate) {
                        // Format the call date
                        long dateMillis = cursor.getLong(columnIndex);
                        String dateString = DateFormat.format("dd/MM/yyyy hh:mm:ss", new java.util.Date(dateMillis)).toString();
                        ((TextView) view).setText(dateString);
                        return true;
                    }
                    return false;
                }
            });

            ListView listView = findViewById(R.id.callHistoryList);
            listView.setAdapter(adapter);
        }
    }
}
