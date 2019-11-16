package com.example.myapplication;

import android.os.Bundle;

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

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GroupDetailFragment extends Fragment {

    public GroupDetailFragment() {
        // Required empty public constructor
    }


    private List<ItemFactory> itemFactories;
    private ImageRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private String ObjectId;
    private String username;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectId = getArguments().getString("UserObjectId");
        username = getArguments().getString("username");
        Log.i("msg222",ObjectId);
        Log.i("msg222",username);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_group_detail, container, false);
        recyclerView = view.findViewById(R.id.rv_main);

        setHasOptionsMenu(true);

        itemFactories = new ArrayList<>();


        adapter = new ImageRecyclerAdapter(itemFactories, getActivity());

        adapter.setOnItemClickListener(new ImageRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {

            }
        });

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.top_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add:

                new MaterialDialog.Builder(getContext())
                        .items(R.array.group_detail_items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(which == 0){
                                    Bundle bundle = new Bundle();
                                    bundle.putString("UserObjectId",ObjectId);
                                    Navigation.findNavController(getActivity(),R.id.host_fragment).navigate(R.id.action_groupDetailFragment_to_groupUploadFragment,bundle);
                                }
                            }
                        })
                        .show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void getImage(){
        new Thread(){
            @Override
            public void run(){
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host("39.106.50.33")
                        .port(3000)
                        .addPathSegment("baidu")
                        .addPathSegment("image")
                        .addQueryParameter("userId", ObjectId)
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

                    itemFactories.clear();
                    itemFactories.add(new ItemFactory(ItemFactory.TYPE.HEAD,username,40));
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
        getImage();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getImage();
        }
    }
}
