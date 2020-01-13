package com.example.expancetraker;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    public Spinner spinner;
    EditText expenseAmountId, expenseDateId, expenseTimeId;
    Button addDocId, addExpId;
    ImageView diplayDocId;

    String itemPos, expAmount,expDate,expTime;

    private static final int SELECT_PHOTO = 1;
    private static final int CAPTURE_PHOTO = 2;

    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();


    ExpenseTypeSpinner expenseTypeSpinner;
    DatabaseHandler databaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        expenseTypeSpinner = new ExpenseTypeSpinner(this, spinner);
        expenseTypeSpinner.ExpenseType();

        InsertExpense();

    }

    public void init() {
        spinner = findViewById(R.id.spinner);
        expenseAmountId = findViewById(R.id.expenseAmountId);
        expenseDateId = findViewById(R.id.expanseDateId);
        expenseTimeId = findViewById(R.id.expenseTimeId);
        addDocId = findViewById(R.id.addDocId);
        addExpId = findViewById(R.id.addExpId);
        diplayDocId = findViewById(R.id.displayDocId);
    }

    public void AddDocument(View view) {
        if (view.getId() == R.id.addDocId) {
            new MaterialDialog.Builder(this)
                    .title(R.string.uploadImages)
                    .items(R.array.uploadImages)
                    .itemsIds(R.array.itemIds)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                            switch (position) {
                                case 0:
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                                    break;
                                case 1:
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, CAPTURE_PHOTO);
                                    break;
                                case 2:
                                    diplayDocId.setImageResource(R.drawable.ic_assignment_black_24dp);
                                    break;
                            }

                        }
                    }).show();
        }


    }



    public void setProgressBar(){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressBarStatus < 100){
                    progressBarStatus += 30;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progressBarbHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.dismiss();

            }
        }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PHOTO){
            if(resultCode == RESULT_OK) {
                try {
                    assert data != null;
                    final Uri imageUri = data.getData();
                    assert imageUri != null;
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    setProgressBar();
                    diplayDocId.setImageBitmap(selectedImage);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }else if(requestCode == CAPTURE_PHOTO){
            if(resultCode == RESULT_OK) {

                assert data != null;
                Bundle extras = data.getExtras();
                assert extras != null;
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                setProgressBar();
                diplayDocId.setMaxWidth(200);
                diplayDocId.setImageBitmap(imageBitmap);

            }
        }
    }

    public void InsertExpense(){
        addExpId.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                expenseTypeSpinner = new ExpenseTypeSpinner(MainActivity.this, spinner);
                itemPos = spinner.getSelectedItem().toString();
                expAmount = expenseAmountId.getText().toString();
                expDate = expenseDateId.getText().toString();
                expTime = expenseTimeId.getText().toString();

                diplayDocId.setDrawingCacheEnabled(true);
                diplayDocId.buildDrawingCache();
                Bitmap bitmap = diplayDocId.getDrawingCache();
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,arrayOutputStream);
                byte[] data = arrayOutputStream.toByteArray();
                databaseHandler = new DatabaseHandler(MainActivity.this);
                long id = databaseHandler.insertToDB(itemPos,expAmount,expDate,expTime,data);
                Toast.makeText(MainActivity.this,"Inserted Id: " + id,Toast.LENGTH_LONG).show();


            }
        });

    }
}
