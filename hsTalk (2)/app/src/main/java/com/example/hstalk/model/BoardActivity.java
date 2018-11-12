package com.example.hstalk.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hstalk.R;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {
   // ArrayList<Title> title = new ArrayList<Title>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        TextView Title = (TextView) findViewById(R.id.textView1);
        ImageButton Create = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton Search = (ImageButton) findViewById(R.id.imageButton2);
        ListView lv = (ListView) findViewById(R.id.listView1);
        Intent intent = getIntent(); // 보내온 Intent를 얻는다
        String type = intent.getStringExtra("title");
        Title.setText(type);

        if (type.equals("자유게시판") || type.equals("통역 해주세요") || type.equals("통역 해드려요")) {
            Create.setVisibility(View.VISIBLE);
            Search.setVisibility(View.VISIBLE);
            List itemlist = new ArrayList();
            itemlist.add(new FreeItem("Test1", "Test 1번 입니다", "2018/11/12/13:22", "pjs"));
            itemlist.add(new FreeItem("Test2", "Test 2번 입니다", "2018/11/12/14:31", "cmh"));
            itemlist.add(new FreeItem("Test3", "Test 3번 입니다", "2018/11/12/17:48", "jhs"));

            lv.setAdapter(new ListViewAdapter(itemlist, this));


//            title.add(new Title("Test1", "Test 1번 입니다", "2018/11/12/13:22", "pjs"));
//            title.add(new Title("Test2", "Test 2번 입니다", "2018/11/12/14:31", "cmh"));
//            title.add(new Title("Test3", "Test 3번 입니다", "2018/11/12/17:48", "jhs"));

//            MyAdapter adapter = new MyAdapter(
//                    getApplicationContext(), // 현재화면의 제어권자
//                    R.layout.item_freeboard,  // 리스트뷰의 한행의 레이아웃
//                    title);         // 데이터

//            ListView lv = (ListView)findViewById(R.id.listView1);
//            lv.setAdapter(adapter);


        }
    } // end of onCreate


    private class ListViewAdapter extends BaseAdapter {

        private List itemlist;
        private Context context;

        public ListViewAdapter(List itemlist, Context context) {
            this.itemlist = itemlist;
            this.context = context;
        }

        @Override
        public int getCount() {
            return itemlist.size();
        }

        @Override
        public Object getItem(int position) {
            return itemlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_freeboard, parent, false);
            }
            TextView freeTitle = (TextView) convertView.findViewById(R.id.freeboard_title);
            TextView freeMain = (TextView) convertView.findViewById(R.id.freeboard_body);
            TextView freeDate = (TextView) convertView.findViewById(R.id.freeboard_time);
            TextView freeWriter = (TextView) convertView.findViewById(R.id.freeboard_writer);

            FreeItem m = (FreeItem) getItem(position);

            freeTitle.setText(m.title);
            freeMain.setText(m.main);
            freeDate.setText(m.date);
            freeWriter.setText(m.writer);

            return convertView;
        }

    }


    class FreeItem{
        String title = ""; // title
        String main = "";
        String date = "";
        String writer = "";
        public FreeItem(String title, String main, String date, String writer) {
            super();
            this.title = title;
            this.main = main;
            this.date = date;
            this.writer = writer;
        }
        public FreeItem() {}

    }
}



//
//class MyAdapter extends BaseAdapter { // 리스트 뷰의 아답타
//    Context context;
//    int layout;
//    ArrayList<Title> al;
//    LayoutInflater inf;
//    public MyAdapter(Context context, int layout, ArrayList<Title> al) {
//        this.context = context;
//        this.layout = layout;
//        this.al = al;
//        inf = (LayoutInflater)context.getSystemService
//                (Context.LAYOUT_INFLATER_SERVICE);
//    }
//    @Override
//    public int getCount() {
//        return al.size();
//    }
//    @Override
//    public Object getItem(int position) {
//        return al.get(position);
//    }
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (convertView==null) {
//            convertView = inf.inflate(layout, null);
//        }
//        TextView freeTitle = (TextView)convertView.findViewById(R.id.freeboard_title);
//        TextView freeMain = (TextView)convertView.findViewById(R.id.freeboard_body);
//        TextView freeDate = (TextView)convertView.findViewById(R.id.freeboard_time);
//        TextView freeWriter = (TextView)convertView.findViewById(R.id.freeboard_writer);
//
//
//
//        Title m = al.get(position);
//        freeTitle.setText(m.title);
//        freeTitle.setText(m.main);
//        freeTitle.setText(m.date);
//        freeTitle.setText(m.writer);
//
//
//        return convertView;
//    }
//}
//
//
//
//
//
//
//class Title { //
//    String title = ""; // title
//    String main = "";
//    String date = "";
//    String writer = "";
//    public Title(String title, String main, String date, String writer) {
//        super();
//        this.title = title;
//        this.main = main;
//        this.date = date;
//        this.writer = writer;
//    }
//    public Title() {}
//}

