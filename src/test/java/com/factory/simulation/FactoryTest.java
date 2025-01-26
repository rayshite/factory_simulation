package com.factory.simulation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FactoryTest {

    @Test
    void testSimulationSimpleScenario() {
        ProductionCenter pc1 = new ProductionCenter(1, "PC1", 1.0, 3);
        ProductionCenter pc2 = new ProductionCenter(2, "PC2", 1.0, 3);
        ProductionCenter pc3 = new ProductionCenter(3, "PC3", 1.0, 3);

        pc1.addNext(pc2);
        pc2.addNext(pc3);

        List<ProductionCenter> centers = List.of(pc1, pc2, pc3);
        Factory factory = new Factory(5, 10, centers);

        factory.startSimulation();

        assertTrue(pc1.isWorkCompleted());
        assertTrue(pc2.isWorkCompleted());
        assertTrue(pc3.isWorkCompleted());

        Report report = factory.getReport();
        assertNotNull(report);
        assertFalse(report.getRecords().isEmpty());
    }

    @Test
    void testReallocationLogic() {
        ProductionCenter pc1 = new ProductionCenter(1, "PC1", 2.0, 2);
        ProductionCenter pc2 = new ProductionCenter(2, "PC2", 1.0, 3);
        pc1.addNext(pc2);

        Factory factory = new Factory(4, 6, List.of(pc1, pc2));
        factory.startSimulation();

        assertTrue(pc1.isWorkCompleted());
        assertTrue(pc2.isWorkCompleted());

        Report report = factory.getReport();
        assertFalse(report.getRecords().isEmpty());

        boolean pc1HadMoreThan1Worker = report.getRecords().stream()
                .anyMatch(r -> r.productionCenter().equals("PC1") && r.workersCount() > 1);

        boolean pc2HadLessThan3Workers = report.getRecords().stream()
                .anyMatch(r -> r.productionCenter().equals("PC2") && r.workersCount() < 3);

        assertTrue(pc1HadMoreThan1Worker);
        assertTrue(pc2HadLessThan3Workers);
    }
}
