package com.example.myapplication;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class groupUploadFragment extends Fragment {
    private ImageView photo;
    private String uploadFileName;
    private byte[] fileBuf;
    private String ObjectId;

    public groupUploadFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectId = getArguments().getString("UserObjectId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        photo = view.findViewById(R.id.photo);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnSelect = view.findViewById(R.id.select);
        Button btnPhotoGraph = view.findViewById(R.id.photograph);
        Button btnUpload = view.findViewById(R.id.upload);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select(v);
            }
        });
        btnPhotoGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(v);
            }
        });
    }


    public void takePhoto(View view) {

        String[] permissions = new String[]{
                Manifest.permission.CAMERA
        };
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 2);
        } else {
            openCamera();
        }
    }


    public void handleTakePhoto() {

        //这就是拍照成功后的图片的路径，因为刚开始我们创建了这个图片的路径
        uri= Uri.fromFile(outputImage);

        uploadFileName = "camera.jpg";
        try {
            Glide.with(this).load(uri)
                    .fitCenter()
                    .into(photo);

            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            fileBuf=convertToBytes(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }




    }
    private Uri photoUri;   //相机拍照返回图片路径
    private Uri uri;
    private File outputImage;

    public void openCamera(){
        //创建file对象，用于存储拍照后的图片，这也是拍照成功后的照片路径
        outputImage = new File(getContext().getExternalCacheDir(),"output_image.jpg");
        try {
            //判断文件是否存在，存在删除，不存在创建
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //判断当前Android版本
        if(Build.VERSION.SDK_INT>=24){
            photoUri= FileProvider.getUriForFile(getContext(),getContext().getPackageName()+".FileProvider",outputImage);
        }else {
            photoUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
        startActivityForResult(intent,2);
    }

    //按钮点击事件
    public void select(View view) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //进行sdcard的读写请求
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            openGallery(); //打开相册，进行选择
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(getContext(), "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    //打开相册,进行照片的选择
    private void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
//        intent.putExtra(intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode != 0){
                    handleSelect(data);
                }
                break;
            case 2:
                handleTakePhoto();
                break;
        }
    }


    //选择后照片的读取工作
    private void handleSelect(Intent intent) {
        Cursor cursor = null;
        Uri uri = intent.getData();
        if (uri == null){
            ClipData clipData = intent.getClipData();
            int count = clipData.getItemCount();
            for (int i = 0;i < count; i++){
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri1 = item.getUri();
                Log.i("msg",uri1.toString());
            }
        }else {
            Log.i("msg1",uri.toString());
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                uploadFileName = cursor.getString(columnIndex);
                Log.i("msg1",uploadFileName);
            }
            try {
                Glide.with(this).load(uri)
                        .fitCenter()
                        .into(photo);

                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                fileBuf=convertToBytes(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cursor.close();
        }
    }


    //文件上传的处理
    public void upload(View view) {
        if(fileBuf != null){
            new Thread() {
                @Override
                public void run() {

                    Log.i("msg1","开始上传");
                    OkHttpClient client = new OkHttpClient();
                    //上传文件域的请求体部分
                    RequestBody formBody = RequestBody
                            .create(fileBuf, MediaType.parse("image/jpeg"));
                    //整个上传的请求体部分（普通表单+文件上传域）
                    RequestBody requestBody=new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            //filename:avatar,originname:abc.jpg
                            .addFormDataPart("file", "abc.jpg",formBody)
                            .addFormDataPart("userId", ObjectId)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://39.106.50.33:3000/baidu/addImage")
                            .post(requestBody)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        Log.i("数据", response.body().string() + "....");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MaterialDialog.Builder(getContext())
                                        .title("上传成功")
                                        .items(R.array.upload_items)
                                        .itemsCallback(new MaterialDialog.ListCallback() {
                                            @Override
                                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                photo.setImageDrawable(null);
                                                if(which == 0){
                                                    Navigation.findNavController(getActivity(),R.id.host_fragment).popBackStack();
                                                }
                                            }
                                        })
                                        .show();
                            }
                        });
                    } catch (Exception e) {
                        Log.i("error", ""+e);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                    }

                }
            }.start();
        }
    }


    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }
}
