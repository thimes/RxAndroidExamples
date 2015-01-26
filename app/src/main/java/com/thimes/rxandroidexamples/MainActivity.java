package com.thimes.rxandroidexamples;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.only_text_view) TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        resetTextView();
    }

    private void resetTextView() {
        textView.setText("Perhaps choose something from the menu?");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch(id) {
            case R.id.action_clear:
                resetTextView();
                return true;
            case R.id.action_simple_observable:
                createSimpleObservable();
                return true;
            case R.id.action_observable_actions:
                final StringBuilder sb = new StringBuilder();
                createSimpleObservable().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        sb.append("received ").append(integer).append("\n");
                        textView.setText(sb.toString());
                    }
                });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Observable<Integer> createSimpleObservable() {
        Observable<Integer> simple =
                Observable
                        .range(0, 10)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        return simple;
    }
}
