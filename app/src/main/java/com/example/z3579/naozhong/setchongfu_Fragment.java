package com.example.z3579.naozhong;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link setchongfu_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link setchongfu_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class setchongfu_Fragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String[] mParam1;
    private String mParam2;
    private TextView weekday7,weekday1,weekday2,weekday3,weekday4,weekday5,weekday6;
    //将textview放入数组便于循环调用
    private TextView[] weekdays = new TextView[7];

    private Drawable dwRight;
    private  boolean[] check={false,false,false,false,false,false,false};

    private View view;
    private OnFragmentInteractionListener mListener;

    public setchongfu_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment setchongfu_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static setchongfu_Fragment newInstance(String[] a ) {
        setchongfu_Fragment fragment = new setchongfu_Fragment();
//        if(a!=null){
//            for (String s:a){
//                Log.d("测试","---"+s);
//            }
//            Bundle args = new Bundle();
//            args.putStringArray(ARG_PARAM1, a);//传入之前的重复星期数据，
//        }
//      args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//
//            mParam1 = getArguments().getStringArray(ARG_PARAM1);
//            for (String s:mParam1){
//                Log.d("测试","mParam1"+s);
//            }
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view=inflater.inflate(R.layout.clockset_chongfu, container, false);
        init();
        initShowCheck();
        return view;
    }
    //改变选中状态
    private void changeCheck(int index){
        if(check[index]){
            check[index]=false;
        }else {
            check[index]=true;
        }
    }
    //初始化显示右侧选中状态
    private void initShowCheck(){
        for(int i =0;i<check.length;i++){
            if(check[i]){
                //显示
                showCheck(i);
            }
        }
    }
    //显示或隐藏右侧选中图标
    private void showCheck(int index){

        if(check[index]){
            weekdays[index].setCompoundDrawables(null, null, dwRight, null);
        }else {
            weekdays[index].setCompoundDrawables(null, null, null, null);
        }

    }
    private void init() {
        if(MainActivity.clock!=null&&MainActivity.clock.getRepeat()!=null){
            for (String s: MainActivity.clock.getRepeat()){//初始化星期选中状态
                changeCheck(Integer.parseInt(s)-1);
            }
        }
        dwRight = getResources().getDrawable(R.drawable.ic_check_black_24dp);
        dwRight.setBounds(0, 0, dwRight.getMinimumWidth(), dwRight.getMinimumHeight());
        weekday7=view.findViewById(R.id.weekday7);
        weekday1=view.findViewById(R.id.weekday1);
        weekday2=view.findViewById(R.id.weekday2);
        weekday3=view.findViewById(R.id.weekday3);
        weekday4=view.findViewById(R.id.weekday4);
        weekday5=view.findViewById(R.id.weekday5);
        weekday6=view.findViewById(R.id.weekday6);
        weekdays[0]=weekday7;
        weekdays[1]=weekday1;
        weekdays[2]=weekday2;
        weekdays[3]=weekday3;
        weekdays[4]=weekday4;
        weekdays[5]=weekday5;
        weekdays[6]=weekday6;
        weekday7.setOnClickListener(this);
        weekday1.setOnClickListener(this);
        weekday2.setOnClickListener(this);
        weekday3.setOnClickListener(this);
        weekday4.setOnClickListener(this);
        weekday5.setOnClickListener(this);
        weekday6.setOnClickListener(this);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.weekday7:
                changeCheck(0);
                showCheck(0);
                break;
            case R.id.weekday1:
                changeCheck(1);
                showCheck(1);
                break;
            case R.id.weekday2:
                changeCheck(2);
                showCheck(2);
                break;
            case R.id.weekday3:
                changeCheck(3);
                showCheck(3);
                break;
            case R.id.weekday4:
                changeCheck(4);
                showCheck(4);
                break;
            case R.id.weekday5:
                changeCheck(5);
                showCheck(5);
                break;
            case R.id.weekday6:
                changeCheck(6);
                showCheck(6);
                break;

        }
    }

    public void checkData(){
        if(mListener!=null){
            mListener.onGetData(check);
        }
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
        void onFragmentInteraction(Uri uri);
        //调用返回选中数据接口
        void onGetData(boolean[] check);
    }
}
