package com.factory.simulation;

import java.util.ArrayList;
import java.util.List;

public class ProductionCenter {

    private static final int SECONDS_PER_MINUTE = 60;

    int id;
    private final String name;
    private final int performanceInSeconds;
    private final int maxWorkersCount;
    private int buffer = 0;
    private int workersCount = 0;
    private List<Integer> inProcess = new ArrayList<>();
    private int totalFinishedCount;
    private boolean isInitial = false;
    private final List<ProductionCenter> next = new ArrayList<>();

    public ProductionCenter(int id, String name, double performance, int maxWorkersCount) {
        this.id = id;
        this.name = name;
        this.performanceInSeconds = (int) performance * SECONDS_PER_MINUTE;
        this.maxWorkersCount = maxWorkersCount;
    }

    public String getName() {
        return name;
    }

    public int getMaxWorkersCount() {
        return maxWorkersCount;
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public int getWorkersCount() {
        return workersCount;
    }

    public void setWorkersCount(int workersCount) {
        this.workersCount = workersCount;
    }

    public void addOneWorker() {
        workersCount++;
    }

    public void setAsInitial() {
        isInitial = true;
    }

    public List<ProductionCenter> getNext() {
        return next;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public boolean isWorkCompleted() {
        return buffer == 0 && inProcess.isEmpty();
    }

    public void addNext(ProductionCenter next) {
        this.next.add(next);
    }

    public int getPartsInProcessCount() {
        return inProcess.size();
    }

    public void addOneToBuffer() {
        buffer++;
    }

    public void doWork() {
        inProcess = new ArrayList<>(inProcess.stream()
                .map(i -> i - 1)
                .toList());
    }

    public void transferReadyParts() {
        if (!next.isEmpty()) {
            for (int leftTime : inProcess) {
                if (leftTime == 0) {
                    int idx = totalFinishedCount % next.size();
                    next.get(idx).addOneToBuffer();
                    totalFinishedCount++;
                }
            }
        }

        inProcess = new ArrayList<>(inProcess.stream()
                .filter(i -> i > 0)
                .toList());
    }

    public float getPriority() {
        if (buffer == 0) {
            return Float.MIN_VALUE;
        }
        if (inProcess.isEmpty()) {
            return buffer;
        }
        return (float) buffer / (inProcess.size() * performanceInSeconds);
    }

    public void getFromBufferToWork() {
        while (buffer > 0 && workersCount > inProcess.size()) {
            buffer--;
            inProcess.add(performanceInSeconds);
        }
    }
}
