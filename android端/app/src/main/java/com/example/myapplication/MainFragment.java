package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private List<ItemFactory> itemFactories;
    private ImageRecyclerAdapter adapter;
    private RecyclerView recyclerView;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.top_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add:
                Log.i("msg", "add");

                new MaterialDialog.Builder(getContext())
                        .items(R.array.main_items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(which == 0){
                                    Navigation.findNavController(getActivity(),R.id.host_fragment).navigate(R.id.action_mainFragment_to_uploadFragment);
                                }else {
                                    Navigation.findNavController(getActivity(),R.id.host_fragment).navigate(R.id.action_mainFragment_to_createGroupFragment);

                                }
                            }
                        })
                        .show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = view.findViewById(R.id.rv_main);

        setHasOptionsMenu(true);

        itemFactories = new ArrayList<>();
        itemFactories.add(new ItemFactory(ItemFactory.TYPE.HEAD,"我的相册",48));

        adapter = new ImageRecyclerAdapter(itemFactories, getActivity());

        adapter.setOnItemClickListener(new ImageRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {

                if(itemFactories.get(position).getType().ordinal() == 0 || itemFactories.get(position).getType().ordinal() == 2){

                }else {
                    Bundle bandle = new Bundle();
                    bandle.putString("ObjectId",itemFactories.get(position).getObjectId());
                    Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_detailFragment,bandle);
                }

            }
        });

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        return view;

    }

    private void getPhoto(){
        new Thread(){
            @Override
            public void run(){
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


                    itemFactories.clear();
                    itemFactories.add(new ItemFactory(ItemFactory.TYPE.HEAD,"我的相册",48));

                    for (Image i : list){
                        itemFactories.add(new ItemFactory(ItemFactory.TYPE.IMAGE,i._id,0,2));
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("msg111",itemFactories.size()+"");
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity().getApplicationContext(), "获取数据成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception ex) {
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

    @Override
    public void onResume() {
        super.onResume();
        getPhoto();
        Toast.makeText(getActivity().getApplicationContext(), "我回来啦", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);Toast.makeText(getActivity().getApplicationContext(), "获取数据成功", Toast.LENGTH_SHORT).show();

        if (!hidden) {
            getPhoto();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
