package com.alpho.hospitalizedpatientsfollowup;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/* TodoDetailActivity allows to enter a new todo item
* or to change an existing
*/
public class activity_detail extends Activity {
   private Spinner mCategory;
   private EditText mTitleText;
   private EditText mBodyText;
   private Uri todoUri;
   @Override
   protected void onCreate(Bundle bundle) {
       super.onCreate(bundle);
       setContentView(R.layout.activity_patient_edit);
       mCategory = (Spinner) findViewById(R.id.category);
       mTitleText = (EditText) findViewById(R.id.todo_edit_summary);
       mBodyText = (EditText) findViewById(R.id.todo_edit_description);
       Button confirmButton = (Button) findViewById(R.id.todo_edit_button);
       Bundle extras = getIntent().getExtras();
// Check from the saved Instance
       todoUri = (bundle == null) ? null : (Uri) bundle
               .getParcelable(PatientContentProvider.CONTENT_ITEM_TYPE);
// Or passed from the other activity
       if (extras != null) {
           todoUri = extras
                   .getParcelable(PatientContentProvider.CONTENT_ITEM_TYPE);
           fillData(todoUri);
       }
       confirmButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View view) {
               if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                   makeToast();
               } else {
                   setResult(RESULT_OK);
                   finish();
               }
           }
       });
   }
   private void fillData(Uri uri) {
       String[] projection = { PatientTable.COLUMN_SUMMARY,
               PatientTable.COLUMN_DESCRIPTION, PatientTable.COLUMN_CATEGORY };
       Cursor cursor = getContentResolver().query(uri, projection, null, null,
               null);
       if (cursor != null) {
           cursor.moveToFirst();
           String category = cursor.getString(cursor
                   .getColumnIndexOrThrow(PatientTable.COLUMN_CATEGORY));
           for (int i = 0; i < mCategory.getCount(); i++) {
               String s = (String) mCategory.getItemAtPosition(i);
               if (s.equalsIgnoreCase(category)) {
                   mCategory.setSelection(i);
               }
           }
           mTitleText.setText(cursor.getString(cursor
                   .getColumnIndexOrThrow(PatientTable.COLUMN_SUMMARY)));
           mBodyText.setText(cursor.getString(cursor
                   .getColumnIndexOrThrow(PatientTable.COLUMN_DESCRIPTION)));
// Always close the cursor
           cursor.close();
       }
   }
   protected void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       saveState();
       outState.putParcelable(PatientContentProvider.CONTENT_ITEM_TYPE, todoUri);
   }
   @Override
   protected void onPause() {
       super.onPause();
       saveState();
   }
   private void saveState() {
       String category = (String) mCategory.getSelectedItem();
       String treatment = mTitleText.getText().toString();
       String prescription = mBodyText.getText().toString();
// Only save if either Treatment or prescription
// is available
       if (prescription.length() == 0 && treatment.length() == 0) {
           return;
       }
       ContentValues values = new ContentValues();
       values.put(PatientTable.COLUMN_CATEGORY, category);
       values.put(PatientTable.COLUMN_SUMMARY, treatment);
       values.put(PatientTable.COLUMN_DESCRIPTION, prescription);
       if (todoUri == null) {
// New todo
           todoUri = getContentResolver().insert(PatientContentProvider.CONTENT_URI, values);
       } else {
// Update todo
           getContentResolver().update(todoUri, values, null, null);
       }
   }
   private void makeToast() {
       Toast.makeText(activity_detail.this, "Please enter patient data",
               Toast.LENGTH_LONG).show();
   }
}
