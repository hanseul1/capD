package com.example.hstalk.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.CreateBoardActivity;
import com.example.hstalk.R;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetBoardList;
import com.example.hstalk.Retrofit.RetroCallback;
import com.example.hstalk.Retrofit.RetroClient;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class BoardFragment extends Fragment {
    private static String IP_ADDRESS = "52.231.69.121";
    private static String TAG ="boardtest";
    ArrayList<ListItem> title = new ArrayList<ListItem>();
    ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board,container,false);
        TextView tv1 =(TextView)view.findViewById(R.id.textView1);
        ImageButton create = (ImageButton)view.findViewById(R.id.imageButton1);
        ImageButton temp = (ImageButton)view.findViewById(R.id.imageButton2);

        lv = (ListView)view.findViewById(R.id.listView1);

        getBoardList();



       create.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                               Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        CreateBoardActivity.class); // 다음넘어갈 화면
                        startActivity(intent);

           }
       });



//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // 상세정보 화면으로 이동하기(인텐트 날리기)
//                // 1. 다음화면을 만든다
//                // 2. AndroidManifest.xml 에 화면을 등록한다
//                // 3. Intent 객체를 생성하여 날린다
//                Intent intent = new Intent(
//                        getActivity(), // 현재화면의 제어권자
//                        BoardActivity.class); // 다음넘어갈 화면
//
//                // intent 객체에 데이터를 실어서 보내기
//                // 리스트뷰 클릭시 인텐트 (Intent) 생성하고 position 값을 이용하여 인텐트로 넘길값들을 넘긴다
//                intent.putExtra("title", title.get(position).title);
//
//                startActivity(intent);
//            }
//        });


        return view;

    }

    protected void getBoardList(){
        RetroClient retroClient = RetroClient.getInstance(getActivity()).createBaseApi();
        retroClient.getBoardList(new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(getActivity(),t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                List<ResponseGetBoardList> data = (List<ResponseGetBoardList>) receivedData;
                for(int i=0; i<data.size(); i++){
                    title.add(new ListItem(data.get(i).title,data.get(i).description,data.get(i).created_at,data.get(i).writeId));
                }

                //게시글 눌러서 내용확인
                MyAdapter adapter = new MyAdapter(
                        getActivity(), // 현재화면의 제어권자
                        R.layout.item_board,  // 리스트뷰의 한행의 레이아웃
                        title);         // 데이터
                lv.setAdapter(adapter);
            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(getActivity(),"fail",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

class MyAdapter extends BaseAdapter { // 리스트 뷰의 아답타
    Context context;
    int layout;
    ArrayList<ListItem> title;
    LayoutInflater inf;
    public MyAdapter(Context context, int layout, ArrayList<ListItem> title) {
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

        TextView tvTitle = (TextView)convertView.findViewById(R.id.board_title);
        TextView tvBody = (TextView)convertView.findViewById(R.id.board_body);
        TextView tvDate = (TextView)convertView.findViewById(R.id.board_date);
        TextView tvWriter = (TextView)convertView.findViewById(R.id.board_writer);
        ListItem m = title.get(position);
        tvTitle.setText(m.title);
        tvBody.setText(m.body);
        tvDate.setText(m.date);
        tvWriter.setText(m.writer);
        return convertView;
    }
}
class ListItem { //
    String title = ""; // title
    String body = "";
    String date = "";
    String writer = "";
    public ListItem(String title, String body, String date, String writer) {
        super();
        this.title = title;
        this.body = body;
        this.date = date;
        this.writer = writer;
    }
    public ListItem() {}
}

