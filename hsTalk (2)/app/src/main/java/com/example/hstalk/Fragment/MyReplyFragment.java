package com.example.hstalk.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hstalk.BoardActivity;
import com.example.hstalk.MainActivity;
import com.example.hstalk.R;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetBoardList;
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetCommentsUserName;
import com.example.hstalk.Retrofit.RetroCallback;
import com.example.hstalk.Retrofit.RetroClient;

import java.util.ArrayList;
import java.util.List;

public class MyReplyFragment extends Fragment {
    private static String IP_ADDRESS = "52.231.69.121";
    private static String TAG ="myreplytest";
    private static int myreply;

    String userName = null;
    String commentUser = null;
    ArrayList<ListItem> title = new ArrayList<ListItem>();
    ListView lv;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof MainActivity){
            userName = ((MainActivity)getActivity()).getData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myreply,container,false);
        TextView tv1 =(TextView)view.findViewById(R.id.myreplyTextView1);

        lv = (ListView)view.findViewById(R.id.myreplyListView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
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

        getCommentsUserName();

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lv.setAdapter(null);
        title.clear();
        getCommentsUserName();
    }

    protected void getCommentsUserName(){
        RetroClient retroClient = RetroClient.getInstance(getActivity()).createBaseApi();
        retroClient.getCommentsUserName(userName, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
                Toast.makeText(getActivity(),t.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                List<ResponseGetCommentsUserName> data = (List<ResponseGetCommentsUserName>) receivedData;
                Log.d("jisu",String.valueOf(data.size()));
                for(int i=0; i<data.size(); i++){
                    title.add(new ListItem(data.get(i).postId, data.get(i).title, data.get(i).description, data.get(i).createTime, data.get(i).startTime,
                            data.get(i).endTime, data.get(i).writer, data.get(i).freeState));
                }

                //게시글 눌러서 내용확인
                MyReplyAdapter adapter = new MyReplyAdapter(
                        getActivity(),         // 현재화면의 제어권자
                        R.layout.item_board,  // 리스트뷰의 한행의 레이아웃
                        title);               // 데이터
                lv.setAdapter(adapter);
            }

            @Override
            public void onFailure(int code) {
                Toast.makeText(getActivity(),"fail",Toast.LENGTH_SHORT).show();
            }
        });
    }
}

class MyReplyAdapter extends BaseAdapter { // 리스트 뷰의 아답타
    Context context;
    int layout;
    ArrayList<ListItem> title;
    LayoutInflater inf;
    public MyReplyAdapter(Context context, int layout, ArrayList<ListItem> title) {
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
class MyReplyListItem {
    int postId;
    String title = "";
    String body = "";
    String createTime = "";
    String startTime = "";
    String endTime = "";
    String writer = "";
    int freeState;

    public MyReplyListItem(int postId, String title, String body, String createTime, String startTime, String endTime, String writer, int freeState) {
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
    public MyReplyListItem() {}

}