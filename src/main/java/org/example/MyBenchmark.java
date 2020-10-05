package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 7, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class MyBenchmark {

    @Param({"100", "1000", "10000", "100000", "1000000"})
    public int size;

    public int[] arr;

    @Setup
    public void setup() {
        arr = new int[size];
        int bound = size / 10;

        Random random = new Random();
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(bound);
        }
    }


    @Benchmark
    public Object onlyArrayTest() {
        return findFirstUniqueOnlyArray(arr);
    }

    @Benchmark
    public Object withCollectionsTest() {
        return findFirstUniqueWithCollections(arr);
    }

    @Benchmark
    public Object withCollectionsAndStreamTest() {
        return findFirstUniqueWithCollectionsAndStreamAPI(arr);
    }


    public Object findFirstUniqueOnlyArray(int[] myArray) {
        for (int i = 0; i < myArray.length; i++) {
            boolean uniqueElement = true;
            for (int j = 0; j < myArray.length; j++) {
                if (j != i && myArray[i] == myArray[j]) {
                    uniqueElement = false;
                    break;
                }
            }
            if (uniqueElement) {
                return myArray[i];
            }
        }
        return false;
    }

    public Object findFirstUniqueWithCollections(int[] myArray) {
        Map<Integer, Boolean> linkedHashMap = new LinkedHashMap<>(myArray.length);
        for (int value : myArray) {
            boolean notUnique = linkedHashMap.containsKey(value);
            linkedHashMap.put(value, !notUnique);
        }
        for (Map.Entry<Integer, Boolean> entry : linkedHashMap.entrySet()) {
            if (entry.getValue()) {
                return entry.getKey();
            }
        }
        return false;
    }

    public Object findFirstUniqueWithCollectionsAndStreamAPI(int[] myArray) {
        Map<Integer, Boolean> linkedHashMap = new LinkedHashMap<>(myArray.length);
        for (int value : myArray) {
            boolean notUnique = linkedHashMap.containsKey(value);
            linkedHashMap.put(value, !notUnique);
        }
        Optional<Map.Entry<Integer, Boolean>> result = linkedHashMap.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {
            return false;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
