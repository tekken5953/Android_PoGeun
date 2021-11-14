package com.example.pogeun;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Music_Fragment extends Fragment {

    ViewGroup viewGroup;
    RecyclerView mRecyclerView = null;
    ArrayList<RecyclerItem> mList = new ArrayList<RecyclerItem>();
    final Field[] fields = R.raw.class.getFields();
    int imgID = 0;
    String music_name, music_singer, genre;
    Bitmap bitmap;
    Drawable drawable, drawable_category1, drawable_category2;
    float height = 100;
    float width = 100;
    RecyclerImageTextAdapter mAdapter = new RecyclerImageTextAdapter(mList);
    String back_main = "music";
    ImageView back;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView text = getActivity().findViewById(R.id.back_main);
        text.setText(back_main);
        drawable_category1 = getResources().getDrawable(R.drawable.pop);
        drawable_category2 = getResources().getDrawable(R.drawable.classic);
        back = getActivity().findViewById(R.id.recycle_back);

        mRecyclerView = getActivity().findViewById(R.id.recyclerview);
        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        mAdapter = new RecyclerImageTextAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        addItem(drawable_category1, "Pop", "팝 음악");
        addItem(drawable_category2, "Classic", "클래식 음악");
        mAdapter.setOnItemClickListener(new RecyclerImageTextAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    case 0:
                        mList.clear();
                        mAdapter = new RecyclerImageTextAdapter(mList);
                        mRecyclerView.setAdapter(mAdapter);
                        restictitem("pop_");
                        back.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mList.clear();
                        mAdapter = new RecyclerImageTextAdapter(mList);
                        mRecyclerView.setAdapter(mAdapter);
                        restictitem("classic");
                        back.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
                back.setVisibility(View.GONE);
            }
        });

        mAdapter.notifyDataSetChanged();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.setOnItemClickListener(new RecyclerImageTextAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), PlayerActivity.class);
                intent.putExtra("index", position);
                intent.putExtra("genre",genre);
                startActivity(intent);
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swiperefresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "refresh complete", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mList.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.music_fragment, container, false);
        return viewGroup;
    }

    public void restictitem(String genre) {
        // 아이템 추가.
        for (Field field : fields) {
            try {
                imgID = getResources().getIdentifier(field.getName(), "drawable", getActivity().getPackageName());
                bitmap = ((BitmapDrawable) mRecyclerView.getResources().getDrawable(imgID)).getBitmap();
                Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
                drawable = new BitmapDrawable(getResources(),resizedBmp);
            } catch (Exception e) {
                e.printStackTrace();
                bitmap = ((BitmapDrawable) mRecyclerView.getResources().getDrawable(R.drawable.no_img)).getBitmap();
                drawable = new BitmapDrawable(getResources(),bitmap);
            }

            try {
                String c = field.getName();
                boolean a = c.startsWith(genre);
                if (a) {
                    int i = c.indexOf("_");
                    String s = c.substring(0, i + 1);
                    genre = c.substring(0, i);
                    music_name = c.replaceFirst(s, "");
                    music_name = music_name.substring(0, music_name.indexOf("_")).replace("9", " ");
                    music_name = music_name.substring(0, 1).toUpperCase() + music_name.substring(1);
                    music_singer = c.substring(c.lastIndexOf("_") + 1).replace("9", " ");
                    music_singer = music_singer.substring(0, 1).toUpperCase() + music_singer.substring(1);
                    addItem(drawable, music_name, music_singer);
                    drawable = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refresh() {
        assert getFragmentManager() != null;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void addItem(Drawable icon, String title, String desc) {
        RecyclerItem item = new RecyclerItem();
        item.setIconDrawable(icon);
        item.setTitleStr(title);
        item.setSingerStr(desc);
        mList.add(item);
    }
}
