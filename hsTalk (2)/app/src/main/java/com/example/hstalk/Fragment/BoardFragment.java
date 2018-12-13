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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.BoardActivity;
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

        lv = (ListView)view.findViewById(R.id.listView1);

        getBoardList();

       create.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                               Intent intent = new Intent(
                        getActivity(), // 현재화면의 제어권자
                        CreateBoardActivity.class); // 다음넘어갈 화면
                        startActivityForResult(intent,3000);
           }
       });


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
                intent.putExtra("postId", title.get(position).postId);
                intent.putExtra("title", title.get(position).title);
                intent.putExtra("writer", title.get(position).writer);
                intent.putExtra("startTime", title.get(position).startTime);
                intent.putExtra("endTime", title.get(position).endTime);
                intent.putExtra("createTime", title.get(position).createTime);
                intent.putExtra("description", title.get(position).body);
                intent.putExtra("freeState", title.get(position).freeState);

                startActivity(intent);
            }
        });


        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lv.setAdapter(null);
        title.clear();
        getBoardList();

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
                for(int i=data.size()-1; i>=0; i--){
                    title.add(new ListItem(data.get(i).postId,data.get(i).title,data.get(i).description,data.get(i).created_at,data.get(i).started_at,
                            data.get(i).ended_at,data.get(i).writeId,data.get(i).freeState));
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
                Toast.makeText(getActivity(),"BoardList fail",Toast.LENGTH_SHORT).show();
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

        TextView tvTitle = (TextView)convertView.findViewById(R.id.boarditem_title);
        TextView tvBody = (TextView)convertView.findViewById(R.id.boarditem_body);
        TextView tvDate = (TextView)convertView.findViewById(R.id.boarditem_date);
        TextView tvWriter = (TextView)convertView.findViewById(R.id.boarditem_writer);
        ListItem m = title.get(position);
        tvTitle.setText(m.title);
        tvBody.setText(m.body);
        tvDate.setText(m.createTime);
        tvWriter.setText(m.writer);
        return convertView;
    }
}
class ListItem { //
    int postId;
    String title = ""; // title
    String body = "";
    String createTime = "";
    String startTime = "";
    String endTime = "";
    String writer = "";
    int freeState;

    public ListItem(int postId, String title, String body, String createTime, String startTime, String endTime, String writer, int freeState) {
        super();
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.writer = writer;
        this.freeState = freeState;
    }
    public ListItem() {}
}

