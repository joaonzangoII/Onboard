package williamroocka.tut.onboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import williamroocka.tut.onboard.adapters.HistoryRecyclerAdapter;
import williamroocka.tut.onboard.base.BaseActivity;
import williamroocka.tut.onboard.managers.Session;
import williamroocka.tut.onboard.models.Time;
import williamroocka.tut.onboard.requests.AuthRequest;
import williamroocka.tut.onboard.utils.Constant;

public class HistoryActivity extends BaseActivity {
    private Session session;
    private HistoryRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ProgressBar progres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");
        session = new Session(this);

        final List<Time> timeEntries = new ArrayList<>();//session.getTimeEntries();
        AuthRequest.getEntries(session,
                HistoryActivity.this,
                requestHandler);

        adapter = new HistoryRecyclerAdapter(HistoryActivity.this, timeEntries);
        layoutManager = new LinearLayoutManager(HistoryActivity.this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        progres = (ProgressBar) findViewById(R.id.progress);
        recyclerView.setLayoutManager(layoutManager);
        progres.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);
    }

    final Handler requestHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            final Bundle data = message.getData();
            final boolean success = data.getBoolean(Constant.KEY_SUCCESS);
            if (success) {
                final List<Time> timeEntries = session.getTimeEntries();
                adapter.setItems(timeEntries);

                progres.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            return false;
        }
    });
}
