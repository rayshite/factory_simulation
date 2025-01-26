package com.factory.simulation;

import java.util.ArrayList;
import java.util.List;

public class Report {

    private final List<Record> records = new ArrayList<>();

    public void addRecord(int time, ProductionCenter pc) {
        records.add(new Record(time, pc.getName(), pc.getWorkersCount(), pc.getBuffer()));
    }

    public List<Record> getRecords() {
        return records;
    }

    public record Record (
        int time,
        String productionCenter,
        int workersCount,
        int BufferCount) {}
}
