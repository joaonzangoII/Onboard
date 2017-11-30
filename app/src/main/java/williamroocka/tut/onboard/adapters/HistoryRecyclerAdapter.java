package williamroocka.tut.onboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import williamroocka.tut.onboard.R;
import williamroocka.tut.onboard.models.Time;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter {
    private List<Time> timeList = new ArrayList<>();
    private LayoutInflater inflater;

    public HistoryRecyclerAdapter(final Context context,
                                  final List<Time> timeList) {
        this.timeList = timeList;
        inflater = LayoutInflater.from(context);
    }


    public void setItems(final List<Time> timeList) {
        this.timeList = new ArrayList<>();
        this.timeList = timeList;

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        final View view = inflater.inflate(
                R.layout.history_list_item,
                parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 int position) {

        final Time time = getItem(position);
        final MyViewHolder h = (MyViewHolder) holder;
        if (time != null) {
            h.date.setText("Date: " + time.getDate());
            h.timeIn.setText("Time In: " + time.getTime_in());
            h.timeOut.setText("Time Out: " + time.getTime_out());
            h.duration.setText("Duration: " + time.getDuration());
        }
    }

    @Override
    public int getItemCount() {
        return timeList.size();
    }

    public Time getItem(final int position) {
        return timeList.get(position);
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView timeIn;
        public TextView timeOut;
        public TextView duration;

        public MyViewHolder(final View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            timeIn = (TextView) itemView.findViewById(R.id.time_in);
            timeOut = (TextView) itemView.findViewById(R.id.time_out);
            duration = (TextView) itemView.findViewById(R.id.duration);
        }
    }
}
