package williamroocka.tut.onboard.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import williamroocka.tut.onboard.R;
import williamroocka.tut.onboard.models.Event;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter {
    final List<Event> events;
    final LayoutInflater inflater;

    public HistoryRecyclerAdapter(final Context context, final List<Event> events) {
        this.events = events;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        final View view = inflater.inflate(R.layout.history_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 int position) {

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
