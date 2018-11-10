package com.example.hstalk.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.R;
import com.example.hstalk.model.BoardActivity;

import java.util.ArrayList;

public class BoardFragment extends Fragment {
    private static String IP_ADDRESS = "52.231.69.121";
    private static String TAG ="boardtest";
    ArrayList<Title> title = new ArrayList<Title>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board,container,false);
        title.add(new Title("내가 쓴 글",R.drawable.my));
        title.add(new Title("댓글 단 글",R.drawable.comment));
        title.add(new Title("자유게시판",R.drawable.comment));
        title.add(new Title("통역 해주세요",R.drawable.comment));
        title.add(new Title("통역 해드려요",R.drawable.comment));
        MyAdapter adapter = new MyAdapter(
                getActivity(), // 현재화면의 제어권자
                R.layout.item_board,  // 리스트뷰의 한행의 레이아웃
                title);         // 데이터

        ListView lv = (ListView)view.findViewById(R.id.listView1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 상세정보 화면으로 이동하기(인텐트 날리기)
                // 1. 다음화면을 만든다
                // 2. AndroidManifest.xml 에 화면을 등록한다
                // 3. Intent 객체를 생성하여 날린다
                Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        BoardActivity.class); // 다음넘어갈 화면

                // intent 객체에 데이터를 실어서 보내기
                // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다
                intent.putExtra("title", title.get(position).title);

                startActivity(intent);
            }
        });



        return view;

    }
}

class MyAdapter extends BaseAdapter { // 리스트 뷰의 아답타
    Context context;
    int layout;
    ArrayList<Title> title;
    LayoutInflater inf;
    public MyAdapter(Context context, int layout, ArrayList<Title> title) {
        this.context = context;
        this.layout = layout;
        this.title = title;
        inf = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return title.size();
    }
    @Override
    public Object getItem(int position) {
        return title.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = inf.inflate(layout, null);
        }
        ImageView iv = (ImageView)convertView.findViewById(R.id.imageView1);
        TextView tvName = (TextView)convertView.findViewById(R.id.textView1);

        Title m = title.get(position);
        iv.setImageResource(m.img);
        tvName.setText(m.title);

        return convertView;
    }
}

class Title { //
    String title = ""; // title
    int img; // 이미지
    public Title(String title, int img) {
        super();
        this.title = title;
        this.img = img;
    }
    public Title() {}
}