package com.example.myapplication;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void Base64Test() throws Exception{
        InputStream inputStream=new FileInputStream("/Users/apple/Desktop/dujuan.jpg");
        OutputStream outputStream=new FileOutputStream("/Users/apple/Desktop/dujuan2.jpg");

        //文件读入缓存并编码
        byte[] buf=new byte[inputStream.available()];
        inputStream.read(buf);
        //编码
        String s=new String(Base64.getEncoder().encode(buf));

        System.out.println(s);

        //解码，并写入文件
        byte[] buf1= Base64.getDecoder().decode(s+"123");
        outputStream.write(buf1);

        outputStream.close();
        inputStream.close();
    }
    @Test
    public void addUser() throws Exception{//完成

//        public static final String TAG = "MainActivity";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client=new OkHttpClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("username","胡一统");
        Gson gson = new Gson();
        String data = gson.toJson(map);

        RequestBody formBody;
        formBody = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .post(formBody)
                .url("http://39.106.50.33:3000/baidu/adduser")
                .build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }
    @Test
    public void group(){//完成

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("http://39.106.50.33:3000/baidu/group")
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Gson gson = new Gson();
            List<Group> list = gson.fromJson(json,  new TypeToken<List<Group>>(){}.getType());

            for (Group g : list){
                if (g.image != null){
                    System.out.println(g.image);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void photo(){//完成

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("http://39.106.50.33:3000/baidu/photo")
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Gson gson = new Gson();
            List<Image> list = gson.fromJson(json,  new TypeToken<List<Image>>(){}.getType());

            for (Image i : list){
                System.out.println(i._id);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Test
    public void image(){//完成

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host("39.106.50.33")
                .port(3000)
                .addPathSegment("baidu")
                .addPathSegment("image")
                .addQueryParameter("userId", "5dcfc8bec4adca7650653afa")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();


        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Gson gson = new Gson();
            List<Image> list = gson.fromJson(json,  new TypeToken<List<Image>>(){}.getType());

            for (Image i : list){
                System.out.println(i._id);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @Test
    public void search() throws IOException {//完成
        String path="/Users/apple/Desktop/43.jpg";
        OkHttpClient client=new OkHttpClient();

        RequestBody formBody=  RequestBody
                .create(new File(path),MediaType.parse("image/jpeg"));

        RequestBody requestBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //filename:avatar,originname:abc.jpg
                .addFormDataPart("file", "abc.jpg",formBody)
                .build();

        Request request = new Request.Builder()
                .url("http://39.106.50.33:3000/baidu/search")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    @Test
    public void addImage() throws IOException {
        String path="/Users/apple/Desktop/liumei1.jpeg";
        OkHttpClient client=new OkHttpClient();

        RequestBody formBody=  RequestBody
                .create(new File(path),MediaType.parse("image/jpeg"));
        ;

        RequestBody requestBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //filename:avatar,originname:abc.jpg
                .addFormDataPart("file", "abc.jpg",formBody)
                .addFormDataPart("userId", "5dcfc8bec4adca7650653afa")
                .build();

        Request request = new Request.Builder()
                .url("http://39.106.50.33:3000/baidu/addImage")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }
    @Test
    public void faceToken(){//完成
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host("39.106.50.33")
                .port(3000)
                .addPathSegment("baidu")
                .addPathSegment("faceToken")
                .addQueryParameter("imageId", "5dcfd535a2d69c77243cc893")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();


        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            Gson gson = new Gson();
            List<FaceToken> list = gson.fromJson(json,  new TypeToken<List<FaceToken>>(){}.getType());

            for (FaceToken f : list){
                if (f.destination != null){
                    System.out.println(f.destination);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}