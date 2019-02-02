package ru.lionzxy.androidperfomance;

import java.io.File;
import java.util.Locale;

import dk.ilios.spanner.Benchmark;
import dk.ilios.spanner.BenchmarkConfiguration;
import dk.ilios.spanner.SpannerConfig;


public class BenchmarkActivity {
    private File filesDir = App.getContext().getFilesDir();
    private File resultsDir = new File(filesDir, "results");

    @BenchmarkConfiguration
    public SpannerConfig configuration = new SpannerConfig.Builder()
            .saveResults(resultsDir, BenchmarkActivity.class.getCanonicalName() + ".json")
            .medianFailureLimit(1.0f) // Accept 100% difference, normally should be 10-15%
            .uploadResults()
            .build();

    public String str1 = "Test";
    public String str2 = "Test2";


    @Benchmark
    public String testFormat() {
        return String.format(Locale.US, "%s (%s)", str1, str2);
    }

    @Benchmark
    public String testSimple() {
        return str1 + "(" + str2 + ")";
    }
}
