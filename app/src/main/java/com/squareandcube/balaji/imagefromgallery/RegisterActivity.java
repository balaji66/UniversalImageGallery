package com.squareandcube.balaji.imagefromgallery;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    /*Global Variabe*/
    ImageView mImage;
    /*Constants*/
    private static final int PICTURE_SELECTED = 1;
    private static final int RESULT_CROP = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
        initListener();
    }

    /*Initiate Views*/
    public void initViews() {
        mImage = (ImageView) findViewById(R.id.image);

    }

    /*Inititate Listeners*/
    public void initListener() {
        mImage.setOnClickListener(this);
    }

    //Crop activity method
    private void performCrop(Uri picUri) {
        try {
            //Start Crop Activity
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 250);
            cropIntent.putExtra("outputY", 250);
            cropIntent.putExtra("data", true);
            cropIntent.putExtra("scaleUpIfNeeded", true);
            if (cropIntent.resolveActivity(getPackageManager()) != null) {
                cropIntent.putExtra("scale", true);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, RESULT_CROP);
            } else {
                Toast.makeText(getApplicationContext(), "data null", Toast.LENGTH_LONG).show();
            }
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case RESULT_CROP:
                try {
                    /*if(data.getExtras()!=null)
                    {*/

                    Bundle extras = data.getExtras();
                    Toast.makeText(getApplicationContext(), "image cropped successfully", Toast.LENGTH_LONG).show();
                    assert extras != null;
                    Bitmap selectedBitmap = (Bitmap) extras.getParcelable("data");
                    // Set The Bitmap Data To ImageView
                    mImage.setImageBitmap(selectedBitmap);
                    mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                    Intent returnFromGalleryIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
                    Toast.makeText(getApplicationContext(), "image cropping failed", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED, returnFromGalleryIntent);
                }

            case PICTURE_SELECTED:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    try {
                        //this code is for android version 21 and 24
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1)) {
                            try {
                                final Uri imageUri = data.getData();
                                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                mImage.setImageBitmap(selectedImage);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // this is for except Android version 21 and 24
                            Uri selectedImage = data.getData();
                            performCrop(selectedImage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent returnFromGalleryIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
                        setResult(RESULT_CANCELED, returnFromGalleryIntent);
                    }
                } else {
                    Intent returnFromGalleryIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
                    setResult(RESULT_CANCELED, returnFromGalleryIntent);

                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:

                // this is for SDK version 21 and 24
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N ||  Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ) {
                    Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    in.putExtra("crop", true);
                    in.putExtra("outputX", 100);
                    in.putExtra("outputY", 100);
                    in.putExtra("scale", true);
                    in.putExtra("return-data", true);
                    Toast.makeText(getApplicationContext(),"hai",Toast.LENGTH_LONG).show();
                    startActivityForResult(Intent.createChooser(in, "select image from gallery"), PICTURE_SELECTED);
                } else {

                    // this is for all Sdk versions
                    // except SDK version 21 and 24
                    Toast.makeText(getApplicationContext(),"hai hai",Toast.LENGTH_LONG).show();
                    Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(in, "select image from gallery"), PICTURE_SELECTED);
                    break;
                }
        }
    }

}
