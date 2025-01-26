package com.factory.simulation;

import java.util.List;

public class Factory {

    private static final int SECONDS_PER_MINUTE = 60;

    private final int workersCount;
    private final List<ProductionCenter> productionCenters;
    private final Report report = new Report();

    public Factory(int workersCount, int partsCount, List<ProductionCenter> productionCenters) {
        Validator.validateAndMarkInitialPc(productionCenters);
        this.workersCount = workersCount;
        this.productionCenters = productionCenters;

        ProductionCenter initialPc = productionCenters.stream()
                .filter(ProductionCenter::isInitial)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Initial production center is not defined"));
        initialPc.setBuffer(partsCount);
    }

    public void startSimulation() {
        allocateWorkers();

        for (int time = 0; !isWorkCompleted(productionCenters); time++) {
            for (ProductionCenter pc : productionCenters) {
                pc.getFromBufferToWork();
                fixState(time, pc);
                pc.doWork();
                pc.transferReadyParts();
            }

            reallocateWorkers();
        }
    }

    public Report getReport() {
        return report;
    }

    private void fixState(int time, ProductionCenter pc) {
        if (time % 60 == 0) {
            int timeInMinutes = time / SECONDS_PER_MINUTE;
            report.addRecord(timeInMinutes, pc);
        }
    }

    private void allocateWorkers() {
        int availableWorkers = workersCount;
        for (ProductionCenter pc : productionCenters) {
            if (availableWorkers > 0) {
                pc.setWorkersCount(Math.min(pc.getMaxWorkersCount(), availableWorkers));
                availableWorkers -= pc.getMaxWorkersCount();
            }
        }
    }

    private void reallocateWorkers() {
        int partsInProcessTotalCount = productionCenters.stream()
                .map(ProductionCenter::getPartsInProcessCount)
                .mapToInt(i -> i)
                .sum();
        int freeWorkers = workersCount - partsInProcessTotalCount;

        List<ProductionCenter> sorted = productionCenters.stream()
                .sorted((pc1, pc2) -> Float.compare(pc2.getPriority(), pc1.getPriority()))
                .toList();

        for (ProductionCenter pc : sorted) {
            pc.setWorkersCount(pc.getPartsInProcessCount());
            while (pc.getWorkersCount() < pc.getMaxWorkersCount() && freeWorkers > 0) {
                pc.addOneWorker();
                freeWorkers--;
            }
        }
    }

    private boolean isWorkCompleted(List<ProductionCenter> productionCenters) {
        return productionCenters.stream()
                .allMatch(ProductionCenter::isWorkCompleted);
    }
}
