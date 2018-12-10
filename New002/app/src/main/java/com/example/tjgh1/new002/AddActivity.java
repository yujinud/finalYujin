package com.example.tjgh1.new002;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.desmond.squarecamera.CameraActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private File filePathImageCamera;
    private LinearLayout emptyLayout;
    private ImageView memoryImage;
    ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoaderConfiguration config;
    DBHelper db;
    private EditText memoryTitle;
    private EditText username;
    private EditText description;
    private  String imageURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        MainActivity.startActivity = false;

        db = new DBHelper(this);
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        config = ImageLoaderConfiguration.createDefault(this);


        imageLoader.init(config);





        memoryTitle = (EditText)  findViewById(R.id.memoryTitle);
        username = (EditText)findViewById(R.id.username);
        description = (EditText)  findViewById(R.id.description);
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(false)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, memoryImage.getWidth(), memoryImage.getHeight(), false);
                    }
                })
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();


        emptyLayout = (LinearLayout)findViewById(R.id.emptyLayout);
        emptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPicker(getString(R.string.select_picture_title));
            }
        });

        memoryImage = (ImageView)findViewById(R.id.memoryImage);
        memoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPicker(getString(R.string.change_memory_image));
            }
        });


        Button complete = (Button)findViewById(R.id.completed);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkTitle() && checkName()) {
                    DialogSimple();
                }
            }
        });
    }

    private void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setMessage(getString(R.string.complete_check)).setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        save();
                    }
                }
                ).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alt_bld.create();
        alert.setTitle(getString(R.string.complete_notify));
        alert.show();
    }

    private  boolean checkTitle()
    {
        if(memoryTitle.getText().length() < 1)
        {
            Toast.makeText(this,
                    getString(R.string.require_title), Toast.LENGTH_LONG).show();
            return false;
        }

        return  true;
    }
    private  boolean checkName()
    {
        if(username.getText().length() < 1)
        {
            Toast.makeText(this,
                    getString(R.string.require_title), Toast.LENGTH_LONG).show();
            return false;
        }

        return  true;
    }

    private void save()
    {


        try {

            MemoryModel model = new MemoryModel();
            model.Title = memoryTitle.getText().toString();
            model.UserName = username.getText().toString();
            model.Comment = description.getText().toString();
            model.ImageUrl = imageURL;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            model.RegDate = simpleDateFormat.format(new Date());
            db.AddMemory(model);

            Toast.makeText(this,
                    getString(R.string.add_success), Toast.LENGTH_LONG).show();

            setResult(RESULT_OK);
            finish();
        }
        catch (Exception e)
        {
            setResult(RESULT_CANCELED);
            Toast.makeText(this,
                    getString(R.string.show_error), Toast.LENGTH_LONG).show();
            return;
        }
    }

    private  void showPicker(String title) {
        final CharSequence[] items = new CharSequence[]{"카메라", "갤러리"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            // 리스트 선택 시 이벤트
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    photoCameraIntent();
                }
                else
                {
                    photoGalleryIntent();
                }
            }
        });
        dialog.show();
    }




    private void photoCameraIntent(){
    }


    private void photoGalleryIntent(){

        Intent intent;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }else{
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_GALLERY_REQUEST){
            if (resultCode == RESULT_OK){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){

                    imageURL = selectedImageUri.toString();
                    imageLoader.displayImage(imageURL, memoryImage);
                    emptyLayout.setVisibility(View.GONE);
                }else{

                    imageURL = "";
                    emptyLayout.setVisibility(View.VISIBLE);
                }
            }
        }

        else if (requestCode == IMAGE_CAMERA_REQUEST){
            if (resultCode == RESULT_OK) {

                Uri photoUri = data.getData();

                if (photoUri != null) {
                    imageURL = photoUri.toString();
                    imageLoader.displayImage(imageURL.toString(), memoryImage);
                    emptyLayout.setVisibility(View.GONE);
                } else {
                    imageURL = "";
                    emptyLayout.setVisibility(View.VISIBLE);
                }


            }
        }
    }
    //홈으로 메뉴
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_home:
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
