package ru.lionzxy.androidperfomance;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import dk.ilios.spanner.Spanner;
import dk.ilios.spanner.SpannerCallbackAdapter;
import dk.ilios.spanner.internal.InvalidBenchmarkException;
import dk.ilios.spanner.model.Trial;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getName();
    private LinearLayout rootLayout;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = ((LinearLayout) findViewById(R.id.container));
        handler = new Handler();
        findViewById(R.id.runtest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootLayout.removeAllViews();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            startBenchmark();
                        } catch (InvalidBenchmarkException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        });
    }

    private void startBenchmark() throws InvalidBenchmarkException {
        Spanner.runAllBenchmarks(BenchmarkActivity.class, new SpannerCallbackAdapter() {
            @Override
            public void trialStarted(Trial trial) {
                addStatus("Start: " + getDescription(trial));
            }

            @Override
            public void trialSuccess(Trial trial, Trial.Result result) {
                double baselineFailure = 15; //benchmarkConfiguration.getBaselineFailure()
                if (trial.hasBaseline()) {
                    double absChange = Math.abs(trial.getChangeFromBaseline(50));
                    if (absChange > baselineFailure) {
                        addStatus(String.format("Change from baseline was to big: %.2f%%. Limit is %.2f%%",
                                absChange, baselineFailure));
                    }
                } else {
                    String resultString = String.format(" [%.2f ns.]", trial.getMedian());
                    addStatus(getDescription(trial) + resultString);
                }
            }

            @Override
            public void trialFailure(Trial trial, Throwable error) {
                addStatus(error.getMessage());
            }

            @Override
            public void onComplete() {
                addStatus("Benchmarks completed");
            }

            @Override
            public void onError(Exception error) {
                addStatus(error.getMessage());
            }
        });
    }

    private String getDescription(Trial trial) {
        return trial.experiment().instrumentation().benchmarkMethod().getName();
    }

    private void addStatus(final String txt) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, txt);
                TextView tv = new TextView(MainActivity.this);
                tv.setText(txt);
                rootLayout.addView(tv);
            }
        });
    }

}