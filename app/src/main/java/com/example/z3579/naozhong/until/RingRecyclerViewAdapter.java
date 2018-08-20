package com.example.z3579.naozhong.until;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.z3579.naozhong.MyRecyclerViewAdapter;
import com.example.z3579.naozhong.R;
import com.example.z3579.naozhong.entity.Ring;

import java.io.IOException;
import java.util.List;

public class RingRecyclerViewAdapter extends RecyclerView.Adapter<RingRecyclerViewAdapter.ViewHolder> {
    private List<Ring> list;
    private RingtoneManager ringtoneManager;
    private Context context;
    private OnListener mListener;
    private int checkindex=0;// 选择的下标值
    private Drawable dwRight;
    private PlayRing playRing;
    private final String URL="content://media/internal/audio/media/";
    public RingRecyclerViewAdapter(List<Ring> list ,Context context,int checkindex,PlayRing playRing){
        this.list =list;
        this.context=context;
        this.mListener = (OnListener) context;
        this.playRing = playRing;
        this.checkindex=checkindex;
        dwRight = context.getResources().getDrawable(R.drawable.ic_check_black_24dp);
        dwRight.setBounds(0, 0, dwRight.getMinimumWidth(), dwRight.getMinimumHeight());
    }
    @Override
    public RingRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ring_layout,parent,false);
        RingRecyclerViewAdapter.ViewHolder  viewHolder = new RingRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RingRecyclerViewAdapter.ViewHolder holder, int position) {
        final int p =position;
        if(p==checkindex){
            holder.ring_name.setCompoundDrawables(null, null, dwRight, null);
        }else {
            holder.ring_name.setCompoundDrawables(null, null, null, null);
        }
        holder.ring_name.setText(list.get(position).getTitle());
        holder.ring_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(p!=checkindex){//点击不同的铃声时
                    int a = checkindex;
                    checkindex=p;//保存下标
                    notifyItemChanged(a);
                    notifyItemChanged(checkindex);
                    Uri uri=Uri.parse(list.get(p).getUrlStr());
                    playRing.destoryMusic();//停止上次播放
                    playRing.startAlarm(uri,context);//播放
                    mListener.getCheck( list.get(p));//返回选择铃声，后来觉得在这里回调不合理，应该放在页面关闭时，懒得改。

                }
            }
        });
    }
    public interface OnListener {
        // TODO: Update argument type and name
        void getCheck(Ring ring);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView ring_name;
        protected ViewHolder(View itemView) {
            super(itemView);
            ring_name =itemView.findViewById(R.id.ring_item);
        }
    }
}
