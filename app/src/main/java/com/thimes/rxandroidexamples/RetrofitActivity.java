package com.thimes.rxandroidexamples;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.thimes.rxandroidexamples.retrofit.Contributor;
import com.thimes.rxandroidexamples.retrofit.GitHubService;
import com.thimes.rxandroidexamples.retrofit.User;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by thimes on 1/27/15.
 */
public class RetrofitActivity extends ActionBarActivity {

    private static final String TAG = "com.thimes.rxandroidexamples.SubjectActivity";

    @InjectView(R.id.only_text_view) TextView textView;

    private GitHubService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        resetTextView();

        service = new RestAdapter.Builder()
                .setEndpoint("https://api.github.com") // The base API endpoint.
                .build().create(GitHubService.class);

    }

    private void resetTextView() {
        textView.setText("Let the operations begin!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_retrofit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_get_github_info:
                getGithubInfo();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getGithubInfo() {

        service.getContributorsObservable("square", "retrofit")
                .flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {
                    @Override
                    public Observable<Contributor> call(List<Contributor> contributors) {
                        return Observable.from(contributors);
                    }
                })
                .flatMap(new Func1<Contributor, Observable<User>>() {
                    @Override
                    public Observable<User> call(Contributor contributor) {
                        return service.getContributorsObservable(contributor.login);
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        textView.append("\nCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        textView.append("error: " + throwable.getMessage());
                    }

                    @Override
                    public void onNext(User user) {
                        textView.append("\n" + (TextUtils.isEmpty(user.name) ? user.login : user.name));
                    }
                });
    }

}
