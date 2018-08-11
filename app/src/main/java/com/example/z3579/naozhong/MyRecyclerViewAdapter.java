package com.example.z3579.naozhong;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.z3579.naozhong.entity.Clock;
import com.example.z3579.naozhong.until.ClockSet;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private List<Clock>  list;
    private boolean flag;//判断是否显示上下午,true 显示，false不显示
    private Context context;
    public MyRecyclerViewAdapter(List<Clock> list,boolean flag){

    this.list=list;
    this.flag=flag;
    }

    @Override

    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_clock, parent, false);
        context=parent.getContext();
        MyRecyclerViewAdapter.ViewHolder viewHolder = new MyRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    /**
     * 在该方法中进行列表组件操作。
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter.ViewHolder holder, int position) {
        final Clock clock = list.get(position);
        if(this.flag){
            //显示上下午控件
            holder.list_shangxia.setVisibility(View.VISIBLE);
            holder.list_wu.setVisibility(View.VISIBLE);
            holder.list_shangxia.setText(clock.getap_am());
            holder.list_time.setText(list.get(position).get12time());
        }else {
            //隐藏
            holder.list_shangxia.setVisibility(View.INVISIBLE);
            holder.list_wu.setVisibility(View.INVISIBLE);
            holder.list_time.setText(list.get(position).getClock_time_str_Delete0());
        }

        holder.list_note.setText(list.get(position).getClock_noteandrepeat());
        if(list.get(position).isIson()){//初始化switch选择状态
            holder.awitch.setChecked(true);
        }
        holder.awitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //设置闹钟定时
                    ClockSet.getInstance(context).sendClockBroadCast(clock);
                }else {
                    //移除闹钟定时
                    ClockSet.getInstance(context).moveClock(clock);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView list_time;
        private TextView list_note;
        private TextView list_shangxia;
        private TextView list_wu;
        private Switch awitch;

        public ViewHolder(View itemView) {
            super(itemView);
            list_time = itemView.findViewById(R.id.list_clocktime);
            list_note=itemView.findViewById(R.id.list_note);
            list_shangxia=itemView.findViewById(R.id.list_shangxia);
            list_wu =itemView.findViewById(R.id.list_wu);
            awitch =itemView.findViewById(R.id.list_switch);

        }
    }
}
