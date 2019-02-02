package ru.lionzxy;

import org.openjdk.jmh.annotations.*;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Random;

@BenchmarkMode(Mode.All) // тестируем во всех режимах
@Warmup(iterations = 10) // число итераций для прогрева нашей функции
@Measurement(iterations = 100, batchSize = 10)
public class StringRandomAppendTest {
    @State(Scope.Thread)
    public static class StringState {
        static final int RANDOM_STRING_COUNT = 1025;
        String[] randomStrings = new String[RANDOM_STRING_COUNT];
        volatile int iterrator = 0;


        String getStr() {
            int item = iterrator;
            iterrator = (iterrator + 1) % RANDOM_STRING_COUNT;
            return randomStrings[item];
        }

        @Setup
        public void setup() {
            Random random = new Random();
            for(int i = 0; i < RANDOM_STRING_COUNT; i++) {
                byte[] array = new byte[random.nextInt(10)];
                random.nextBytes(array);
                randomStrings[i] = new String(array, Charset.forName("UTF-8"));
            }
        }
    }

    @Benchmark
    public String testFormat(StringState state) {
        return String.format(Locale.US, "%s (%s)", state.getStr(), state.getStr());
    }

    @Benchmark
    public String testSimple(StringState state) {
        return state.getStr() + "(" + state.getStr() + ")";
    }

}
