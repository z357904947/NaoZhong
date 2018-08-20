package com.example.z3579.naozhong;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cncoderx.wheelview.OnWheelChangedListener;
import com.example.z3579.naozhong.entity.Clock;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam2;
    private static AddFragment fragment;
    private OnFragmentInteractionListener mListener;
    //loopView控件
    private LoopView loopView_hour;//显示小时
    private LoopView loopView_minute;//显示分钟
 //   private LoopView loopView_ap_am;//当12小时制式时，显示上下午
    private MyApplication application;
    //选择ITEM容器,小时 12小时制式
 // private ArrayList<String>  list_12hour= null;
    //选择ITEM容器,小时 24小时制式,只实现一种24小时的
    private ArrayList<String>  list_24hour= null;
    //分钟
    private ArrayList<String> list_minute=null;
    //上下午
//    private ArrayList<String> list_ap_am=null;
    //存储选中的时间
    private String hour_str;
    private String  mintue_str;
//  private Integer ap_am=-1;

    private View view;
    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(Clock clock ) {

        fragment = new AddFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.addclock_fragment, container, false);
        init();
        initView();
        addOrUpdate();
        initShowSet();
        return view;
    }

    private void initShowSet() {
        //星期重复功能及显示
        TextView textView_chongfu = view.findViewById(R.id.textView_chongfu);
        textView_chongfu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开星期重复设置页面，回调activity执行打开，因为还需要对activityUI进行绘制，放入activity操作方便
                if (mListener!=null){
                    mListener.onFragmentInteraction();
                }

            }
        });
        TextView chongfu_lable = view.findViewById(R.id.set_lable1);
        if(MainActivity.clock.repeattoString().length()!=0){
            chongfu_lable.setText(MainActivity.clock.repeattoString());
        }else {
            chongfu_lable.setText("永不");
        }
        //闹钟标签功能及显示
        TextView clock_note=view.findViewById(R.id.textView_label);
        clock_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onLableUpdate();
                }
            }
        });
        TextView note_lable = view.findViewById(R.id.set_lable2);
        //闹钟铃声选择功能
        note_lable.setText(MainActivity.clock.getClock_note());
        TextView click_ring = view.findViewById(R.id.textView_ring);//铃声选择
        click_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.openringlist();
                }
            }
        });
        TextView ring_lable = view.findViewById(R.id.set_lable3);
        ring_lable.setText(MainActivity.ring.getTitle());
        //是否稍后提醒
        Switch switch_tixing =view.findViewById(R.id.switch_1);
        switch_tixing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    MainActivity.clock.setIsalert(true);
                }else {
                    MainActivity.clock.setIsalert(false);
                }
            }
        });
    }

    private void initView(){
        loopView_hour = view.findViewById(R.id.wheelview);
        loopView_hour.setItems(list_24hour);
        loopView_minute =  view.findViewById(R.id.wheelview_minute);
        loopView_minute.setItems(list_minute);
    }

    private void addOrUpdate() {
        int hour;
        int minute;
        if(MainActivity.clock==null){
            Calendar calendar  = Calendar.getInstance();
            hour =calendar.get(Calendar.HOUR_OF_DAY);
            minute=calendar.get(Calendar.MINUTE);
            MainActivity.clock=new Clock();
        }else {
            Log.d("测试","有clock对象");
            hour=MainActivity.clock.getHour();
            minute=MainActivity.clock.getMinute();
        }
        checkedTime(hour,minute);//
    }
    //选定时间
    private void checkedTime(int hour,int minute){
        loopView_hour.setInitPosition(hour);//设置初始化选择当前小时
        loopView_minute.setInitPosition(minute);//设置初始化选择当前分钟
        hour_str =list_24hour.get(hour);//存储当前时间-小时字符
        mintue_str =list_minute.get(minute);//保存当前分钟字符
        MainActivity.clock.setClock_time_str(hour_str+":"+mintue_str);
        loopView_minute.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mintue_str=list_minute.get(index);
                MainActivity.clock.setClock_time_str(hour_str+":"+mintue_str);
                Log.d("测试","MainActivity"+MainActivity.clock.getClock_time_str());
                Log.d("测试", "选择分钟为： " + mintue_str);
            }
        });
        loopView_hour.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                //得到当前选中时间
                hour_str=list_24hour.get(index);
                MainActivity.clock.setClock_time_str(hour_str+":"+mintue_str);
                Log.d("测试","MainActivity"+MainActivity.clock.getClock_time_str());
                Log.d("测试", "选择小时为： " + hour_str);
            }
        });
    }
    //初始化数据
    private void  init(){
        application = (MyApplication) getActivity().getApplication();
        //初始化选择器数据集合
        initList_hour();
        initList_minute();
    }
    //初始化时间选择器list
    private void initList_hour() {

            if (list_24hour == null) {
                list_24hour = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    if (i < 10) {
                        list_24hour.add("0" + i);
                    } else {
                        list_24hour.add(String.valueOf(i));
                    }
                }

            }
    }
    /**
     * 初始化分钟
     */
    private void initList_minute() {
        if(list_minute==null){
            list_minute = new ArrayList<>();
            for (int i =0;i<60;i++){
                if(i<10){
                    list_minute.add("0"+i);
                }else {
                    list_minute.add(String.valueOf(i));
                }
            }
        }


    }
    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
        void onFragmentDestroy();
        void onLableUpdate();
        void openringlist();
    }
    @Override
    public void onDestroy() {
        //在onDestroy，回调Activity,显示添加按钮
        mListener.onFragmentDestroy();
        super.onDestroy();
    }
}
