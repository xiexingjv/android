package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.InputType;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class GroupFragment extends Fragment {


    public GroupFragment() {
        // Required empty public constructor
    }


    private List<ItemFactory> itemFactories;
    private ImageRecyclerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_group, container, false);
        recyclerView = view.findViewById(R.id.rv_main);

        setHasOptionsMenu(true);

        itemFactories = new ArrayList<>();
        itemFactories.add(new ItemFactory(ItemFactory.TYPE.HEAD, "人脸库",48));


        adapter = new ImageRecyclerAdapter(itemFactories, getActivity());

        adapter.setOnItemClickListener(new ImageRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {


                if(itemFactories.get(position).getType().ordinal() == 0){

                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("UserObjectId",itemFactories.get(position).getUserId());
                    bundle.putString("username",itemFactories.get(position).getUsername());
                    Log.i("msg222",itemFactories.get(position).getUserId());
                    Log.i("msg222",itemFactories.get(position).getUsername());
                    Navigation.findNavController(view).navigate(R.id.action_createGroupFragment_to_groupDetailFragment,bundle);
                }

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

    private void getGroup() {
        new Thread() {
            @Override
            public void run() {
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

                    itemFactories.clear();
                    itemFactories.add(new ItemFactory(ItemFactory.TYPE.HEAD, "人脸库",48));

                    for (Group g : list){
                        if (g.image == null){
                            itemFactories.add(new ItemFactory(ItemFactory.TYPE.DETAIL,g.username,40,g._id, g.username));
                        }else {
                            itemFactories.add(new ItemFactory(ItemFactory.TYPE.IMAGE,g.image,0,2,g._id, g.username));
                        }

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


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add:
                Log.i("msg", "add");

                new MaterialDialog.Builder(getContext())
                        .title("添加人脸")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("请输入人脸标记，比如姓名", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog,final CharSequence input) {
                                new Thread() {
                                    @Override
                                    public void run() {

                                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                                        OkHttpClient client = new OkHttpClient();
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("username", input.toString());
                                        Gson gson = new Gson();
                                        String data = gson.toJson(map);

                                        RequestBody formBody;
                                        formBody = RequestBody.create(JSON, data);
                                        Request request = new Request.Builder()
                                                .post(formBody)
                                                .url("http://39.106.50.33:3000/baidu/adduser")
                                                .build();
                                        Response response = null;
                                        try {
                                            response = client.newCall(request).execute();
                                            System.out.println(response.body().string());

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    getGroup();
                                                }
                                            });
                                        } catch (IOException e) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getActivity().getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                }.start();
                            }
                        })
                        .positiveText("确定")
                        .show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        getGroup();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // TODO Auto-generated method stub
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getGroup();
        }
    }

}

