package com.thimes.rxandroidexamples;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class OperatorsActivity extends ActionBarActivity {

    private static final String TAG = "com.thimes.rxandroidexamples.MainActivity";

    @InjectView(R.id.only_text_view) TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        resetTextView();
    }

    private void resetTextView() {
        textView.setText("Let the operations begin!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_operators, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_clear:
                resetTextView();
                return true;
            case R.id.action_try_operator:
//                subscriberThread();
//                observerThread();
//                tryMap();
//                tryFlatMap();
//                tryTake();
//                tryBuffer();
                trySample();
                // try your favorite operator...!
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void tryMap() {
        Observable.range(0, 10).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return fib(integer);
            }
        }).subscribe(getLoggingSubscriber());
    }

    private void subscriberThread() {
        createSimpleObservable().subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                textView.append("\n" + fibSlow(integer));
            }
        });
    }

    private void observerThread() {
        createObservableThatDoesWork().subscribe(new Subscriber<Pair<Integer, Long>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Pair<Integer, Long> integerLongPair) {
                textView.append("\n" + integerLongPair.first + ", " + integerLongPair.second);
            }
        });
    }

    private Observable<Pair<Integer, Long>> createObservableThatDoesWork() {
        Observable<Pair<Integer, Long>> fibObservable = Observable.create(new Observable.OnSubscribe<Pair<Integer, Long>>() {
            @Override
            public void call(Subscriber<? super Pair<Integer, Long>> subscriber) {
                for (int i = 0; i < 14; i++) {
                    int fib = 0;
                    long start = System.nanoTime();
                    fib = fibSlow(i);
                    long end = System.nanoTime();
                    subscriber.onNext(new Pair<Integer, Long>(fib, end - start));
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return fibObservable;
    }

    private int fibSlow(Integer integer) {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        switch (integer) {
            case 1:
            case 0:
                return 1;
            default:
                return fibSlow(integer - 2) + fibSlow(integer - 1);
        }
    }

    private int fib(Integer integer) {
        switch (integer) {
            case 1:
            case 0:
                return 1;
            default:
                return fib(integer - 2) + fib(integer - 1);
        }
    }

    private void tryFlatMap() {
        Observable.just(Arrays.asList("First", "Second", "third", "etc")).flatMap(new Func1<List<String>, Observable<? extends String>>() {
            @Override
            public Observable<? extends String> call(List<String> strings) {
                return Observable.from(strings);
            }
//        }).flatMap(new Func1<String, Observable<String>>() {
//            @Override
//            public Observable<String> call(String s) {
//                return Observable.from(s.split(""));
//            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getLoggingSubscriber());
    }

    private void tryTake() {
        createSimpleObservable().take(2).subscribe(getLoggingSubscriber());
    }

    private void tryBuffer() {
        createSimpleObservable().buffer(5).subscribe(getLoggingSubscriber());
    }

    private void trySample() {
        Observable
                .range(0, 100000)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .sample(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getLoggingSubscriber());
    }

    private Observable<Integer> createSimpleObservable() {
        Observable<Integer> simple =
                Observable
                        .range(0, 10)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        return simple;
    }

    private Subscriber<Object> getLoggingSubscriber() {
        return new Subscriber<Object>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object s) {
                textView.append("\n" + s.toString());
            }
        };
    }


}
