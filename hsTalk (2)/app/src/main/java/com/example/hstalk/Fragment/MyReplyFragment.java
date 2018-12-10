package com.example.hstalk.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.hstalk.Retrofit.ResponseBody.ResponseGetComments;
import com.example.hstalk.Retrofit.RetroCallback;
import com.example.hstalk.Retrofit.RetroClient;
import com.example.hstalk.util.Constants;
import com.google.firebase.auth.FirebaseAuth;

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
//        ImageButton refresh = (ImageButton)view.findViewById(R.id.myreplyButton_refresh);

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

        getBoardList();

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

//                SharedPreferences sharedPreferences;
//                sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFS , Context.MODE_PRIVATE);
//                commentUser = sharedPreferences.getString("writeId","");
//                Toast.makeText(getActivity(), "유저 : " + userName, Toast.LENGTH_SHORT).show();

                //client 객체 새로 선언해서 getcomments로 만들어서 onsuccess랑 그런거 만들어서 비교비교비교비교비교해!
                List<ResponseGetBoardList> data = (List<ResponseGetBoardList>) receivedData;
                RetroClient rClient = RetroClient.getInstance(getActivity()).createBaseApi();

                for(int i=0; i<data.size(); i++){
                    rClient.getComments(data.get(i).postId, new RetroCallback() {

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(getActivity(),t.toString(),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(int code, Object receivedData) {
                            List<ResponseGetComments> cData = (List<ResponseGetComments>) receivedData;
                            for(int j = 0; j < cData.size(); j++){
                                if(cData.get(j).writeId.equals(userName)){
                                    Toast.makeText(getActivity(), "onSuccess, myreply = 1", Toast.LENGTH_SHORT).show();

                                    myreply = 1;
                                    //댓글을 두 개 달았으면?
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(int code) {
                            Toast.makeText(getActivity(),"fail",Toast.LENGTH_SHORT).show();
                        }

                    });

                    Toast.makeText(getActivity(), "myreply : " + myreply, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getActivity(), "chk.check: " + chk.check, Toast.LENGTH_SHORT).show();

//                    int temp = chk.check;
                    if(myreply == 1) {
                        Toast.makeText(getActivity(), "뭔데 ㅅㅂ" , Toast.LENGTH_SHORT).show();
                        title.add(new ListItem(data.get(i).postId, data.get(i).title, data.get(i).description, data.get(i).created_at, data.get(i).started_at,
                                data.get(i).ended_at, data.get(i).writeId, data.get(i).freeState));
                        myreply = 0;
                    }
                }

                //게시글 눌러서 내용확인
                MyReplyAdapter adapter = new MyReplyAdapter(
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
    String title = ""; // title
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