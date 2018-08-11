package com.example.z3579.naozhong;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.z3579.naozhong.dummy.DummyContent;
import com.example.z3579.naozhong.dummy.DummyContent.DummyItem;
import com.example.z3579.naozhong.entity.Clock;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class Clock_listFragment extends Fragment {
    //闹钟列表RecyclerView
    private RecyclerView clock_view;//闹钟显示列表
    //闹钟数据集合
    private static List<Clock> list;
    //闹钟列表适配器
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    private Context context;
    //
    private MyApplication application;
    // TODO: Customize parameter argument names
    // TODO: Customize parameters
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public Clock_listFragment() {

    }
    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Clock_listFragment newInstance(List<Clock> list) {
      Clock_listFragment fragment = new Clock_listFragment();
//      Bundle args = new Bundle();
//
//      fragment.setArguments(args);
        Clock_listFragment.list=list;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            falg12or24 = getArguments().getBoolean("falg12or24");
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        init();
        // Set the adapter，修改了一下代码模板，因为不涉及一行多列的变化，去掉mColumnCount判断
        if (view instanceof RecyclerView) {
            context = view.getContext();
            clock_view = (RecyclerView) view;
            //显示列表
            showClockList();
        }
        return view;
    }

    /**
     *  初始化
     */
    private void init(){
        application = (MyApplication) getActivity().getApplication();

    }
    /**
     * 显示闹钟列表
     */
    private void showClockList(){
        if(list!=null){
            clock_view.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
            clock_view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            clock_view.setItemAnimator(new DefaultItemAnimator());
            myRecyclerViewAdapter= new MyRecyclerViewAdapter(list,application.isFalg12or24());
            clock_view.setAdapter(myRecyclerViewAdapter);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Integer item);
    }
}
