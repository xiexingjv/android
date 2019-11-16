package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DetailFragment extends Fragment {

    public DetailFragment() {
        // Required empty public constructor
    }


    private List<ItemFactory> itemFactories;
    private ImageRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private String ObjectId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("msg",getArguments().getString("ObjectId"));
        ObjectId = getArguments().getString("ObjectId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = view.findViewById(R.id.rv_main);

        setHasOptionsMenu(true);

        itemFactories = new ArrayList<>();

        getFaceToken();

        adapter = new ImageRecyclerAdapter(itemFactories, getActivity());

        adapter.setOnItemClickListener(new ImageRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {

//                if(itemFactories.get(position).getURL() == "add" || itemFactories.get(position).getURL() == "head"){
//
//                }else {
//                    Bundle bandle = new Bundle();
//                    bandle.putString("ObjectId","123");
//                    Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_detailFragment,bandle);
//                }

            }
        });

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        return view;
    }


    public void getFaceToken(){
        new Thread(){
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host("39.106.50.33")
                        .port(3000)
                        .addPathSegment("baidu")
                        .addPathSegment("faceToken")
                        .addQueryParameter("imageId", ObjectId)
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

                    Log.i("msg",list.size()+"");

                    itemFactories.clear();

                    itemFactories.add(new ItemFactory(ItemFactory.TYPE.IMAGE,ObjectId,0,1));
                    if (list.size() == 0){
                        itemFactories.add(new ItemFactory(ItemFactory.TYPE.DETAIL,"未检测到",40));
                    }else {
                        itemFactories.add(new ItemFactory(ItemFactory.TYPE.DETAIL,"检测到：",40));
                    }

                    for (FaceToken f : list){
                        itemFactories.add(new ItemFactory(ItemFactory.TYPE.DETAIL,f.face_token,20));
                        if (f.destination != null){
                            itemFactories.add(new ItemFactory(ItemFactory.TYPE.DETAIL,f.destination,20));
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity().getApplicationContext(), "获取数据成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception ex) {
                    Log.i("msg",ex.toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }
}
