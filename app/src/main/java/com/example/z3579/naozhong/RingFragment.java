package com.example.z3579.naozhong;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.z3579.naozhong.entity.Ring;
import com.example.z3579.naozhong.until.MySimpleItemAnimator;
import com.example.z3579.naozhong.until.PlayRing;
import com.example.z3579.naozhong.until.RingRecyclerViewAdapter;
import com.example.z3579.naozhong.until.SimpleDividerDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RingFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private OnFragmentInteractionListener mListener;
    private RecyclerView ringlist;

    private  PlayRing playRing;

    public RingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment RingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RingFragment newInstance( ) {
        RingFragment fragment = new RingFragment();
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
        View view = inflater.inflate(R.layout.fragment_ring, container, false);
        ringlist = view.findViewById(R.id.ring_list);
        playRing = PlayRing.getPlayRing(getActivity());//音乐播放帮助类

        List<Ring> list =playRing.get_List_ALARM();//得到闹铃列表
        RingRecyclerViewAdapter ringAdapter= new RingRecyclerViewAdapter(list,getActivity(),MainActivity.ring.getIndex(),playRing);
        ringlist.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        //自定义分割线,
        SimpleDividerDecoration simpleDividerDecoration = new SimpleDividerDecoration(getActivity());
        simpleDividerDecoration.setColor(ContextCompat.getColor(getActivity(), R.color.clock_set_text));
        simpleDividerDecoration.setLeft(getActivity().getResources().getDimensionPixelSize(R.dimen.paddingLeft));
        simpleDividerDecoration.setRight(getActivity().getResources().getDimensionPixelSize(R.dimen.paddingLeft));
        simpleDividerDecoration.setDividerHeight(5);
        ringlist.addItemDecoration(simpleDividerDecoration);

        //ringlist.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));默认的设置分割线方法
        //ringlist.setItemAnimator(new DefaultItemAnimator());//使用默认动画有闪烁
        ringlist.setItemAnimator(new MySimpleItemAnimator());//使用自定义解决闪烁问题，原理删除了明暗变化设置。
        ringlist.setAdapter(ringAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Ring ring) {
        if (mListener != null) {
            mListener.onFragmentInteraction(ring);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        playRing.destoryMusic();
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
        void onFragmentInteraction(Ring ring);
    }
}
