package com.getrent.timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.getrent.timer.database.DBConnection;
import com.getrent.timer.model.TimerList;

import java.util.ArrayList;

public class TimerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<TimerList> timerLists;
    private DBConnection sqliteDB;
    public TimerListAdapter(Context context, ArrayList<TimerList> lists){
        this.mContext = context;
        this.timerLists = lists;
        sqliteDB = new DBConnection(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final TimerList list = timerLists.get(position);
        final String sNo = list.getId();
        String sTime = list.getStartTime();
        String stopTime = list.getStopTime();
        String[] time = sTime.split("/");
        ViewHolder vHolder = (ViewHolder)holder;
        vHolder.tvNo.setText(sNo);
        vHolder.tvStartTime.setText(time[0]+"\n"+time[1]);
        vHolder.tvStopTime.setText(stopTime);

        vHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              boolean tt =  sqliteDB.deleteData(Integer.parseInt(sNo));
                Toast.makeText(mContext, ""+tt, Toast.LENGTH_SHORT).show();
                timerLists.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return timerLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvNo,tvStartTime,tvStopTime;
        private ImageView ivDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNo = itemView.findViewById(R.id.tvNo);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvStopTime = itemView.findViewById(R.id.tvStopTime);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
