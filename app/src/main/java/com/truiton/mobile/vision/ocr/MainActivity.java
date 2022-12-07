package com.truiton.mobile.vision.ocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;


import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.truiton.mobile.vision.ocr.Room.ReceiptDao;
import com.truiton.mobile.vision.ocr.Room.ReceiptDatabase;
import com.truiton.mobile.vision.ocr.Room.ReceiptInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Text API";
    private static final int PHOTO_REQUEST = 10;
    private TextView scanResults;
    private Uri imageUri;
    private TextRecognizer detector;
    private ImageView imageView;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    ArrayList<String> detectedListValue= new ArrayList<>();

    private ReceiptDatabase receiptDatabase;
    private ReceiptDao receiptDao;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.camera_capture_button);
        imageView = findViewById(R.id.imageView);
//        scanResults = (TextView) findViewById(R.id.results);

        receiptDatabase = ReceiptDatabase.getInstance(this);
        receiptDao = receiptDatabase.getDao();

        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }
        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MainActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            try {
            launchMediaScanIntent();

                Bitmap bitmap = decodeBitmapUri(this, imageUri);
                imageView.setImageBitmap(bitmap);
                SparseArray<TextBlock> textBlocks = null;
                String receiptDate = null;
                if (detector.isOperational() && bitmap != null) {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    textBlocks = detector.detect(frame);
                    String blocks = "";
                    detectedListValue = new ArrayList<>();
                    ArrayList<String> typeValueList = new ArrayList<>();
                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock tBlock = textBlocks.valueAt(index);
//                        Log.e( "onActivityResult: ",tBlock.getBoundingBox()+"");
//                        Log.i("onActivityResult: ",tBlock.getValue()+"");

                        if (tBlock.getBoundingBox().left <= 50) {
                            if (tBlock.getValue().toLowerCase(Locale.ROOT).contains("machine")) {
                                receiptDate = String.valueOf(tBlock.getValue().split("TIME")[1]);
//                                String lastDate = String.valueOf(tBlock.getValue().split("CLEARED")[1]);
                                Log.e("onActivityResult: ", "Receipt Date" + receiptDate);
//                                Log.e("onActivityResult: ","Last cleared date"+lastDate );
                            }
                        } else if (tBlock.getBoundingBox().left >= 250 || tBlock.getBoundingBox().left < 500) {
//                            Log.e( "onActivityResult: ",tBlock.getValue()+"--250 - 500");
                            typeValueList.add(tBlock.getValue());
                        }
                        blocks = blocks + tBlock.getValue() + "\n" + "\n";
                        detectedListValue.add(tBlock.getValue());
//                        detectedListValue.add( "\n" + "\n");
                    }

                    Log.e("onActivityResult: ", "\n" + "Type list" + typeValueList);

                    if (textBlocks.size() == 0) {
                        Toast.makeText(this, "Scan Failed: Found nothing to scan", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(this, "Could not set up the detector!", Toast.LENGTH_SHORT).show();
                }
                ReceiptInfo receiptInfo = new ReceiptInfo(0, receiptDate,detectedListValue);
                receiptDao.insert(receiptInfo);
                Toast.makeText(MainActivity.this, "Inserted", Toast.LENGTH_SHORT).show();
                Intent toDetectedDetailsActivity = new Intent(this, TextActivity.class);
                toDetectedDetailsActivity.putExtra("Data", detectedListValue);
                startActivity(toDetectedDetailsActivity);

            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(LOG_TAG, e.toString());
            }
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
//            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }
}
