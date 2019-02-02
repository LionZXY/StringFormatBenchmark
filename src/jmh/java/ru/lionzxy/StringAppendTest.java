package ru.lionzxy;

import org.openjdk.jmh.annotations.*;

import java.util.Locale;

@BenchmarkMode(Mode.All) // тестируем во всех режимах
@Warmup(iterations = 10) // число итераций для прогрева нашей функции
@Measurement(iterations = 100, batchSize = 10)
public class StringAppendTest {

    @State(Scope.Thread)
    public static class StringState {
        public String str1 = "Test";
        public String str2 = "Test2";
    }

    @Benchmark
    public String testFormat(StringState state) {
        return String.format(Locale.US, "%s (%s)", state.str1, state.str2);
    }

    @Benchmark
    public String testSimple(StringState state) {
        return state.str1 + "(" + state.str2 + ")";
    }
}
