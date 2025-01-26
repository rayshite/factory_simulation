package com.factory.simulation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void testPositiveScenario() {
        ProductionCenter pc1 = new ProductionCenter(1, "PC1", 1.0, 2);
        ProductionCenter pc2 = new ProductionCenter(2, "PC2", 1.0, 2);
        ProductionCenter pc3 = new ProductionCenter(3, "PC3", 1.0, 2);

        pc1.addNext(pc2);
        pc2.addNext(pc3);

        List<ProductionCenter> allCenters = List.of(pc1, pc2, pc3);

        assertDoesNotThrow(() -> Validator.validateAndMarkInitialPc(allCenters));
        assertTrue(pc1.isInitial());
    }

    @Test
    void testExceptionIfNoInitialCenter() {
        ProductionCenter pc1 = new ProductionCenter(1, "PC1", 1.0, 2);
        ProductionCenter pc2 = new ProductionCenter(2, "PC2", 1.0, 2);

        pc1.addNext(pc2);
        pc2.addNext(pc1);

        List<ProductionCenter> allCenters = List.of(pc1, pc2);

        assertThrows(RuntimeException.class, () -> Validator.validateAndMarkInitialPc(allCenters));
    }

    @Test
    void testExceptionIfCycleIsDetected() {
        ProductionCenter pc1 = new ProductionCenter(1, "PC1", 1.0, 2);
        ProductionCenter pc2 = new ProductionCenter(2, "PC2", 1.0, 2);
        ProductionCenter pc3 = new ProductionCenter(3, "PC3", 1.0, 2);

        pc1.addNext(pc2);
        pc2.addNext(pc3);
        pc3.addNext(pc2);

        List<ProductionCenter> allCenters = List.of(pc1, pc2);

        assertThrows(RuntimeException.class, () -> Validator.validateAndMarkInitialPc(allCenters));
    }

    @Test
    void testExceptionIfNotConnectedGraph() {
        ProductionCenter pc1 = new ProductionCenter(1, "PC1", 1.0, 2);
        ProductionCenter pc2 = new ProductionCenter(2, "PC2", 1.0, 2);
        pc1.addNext(pc2);

        ProductionCenter pc3 = new ProductionCenter(3, "PC3", 1.0, 2);
        ProductionCenter pc4 = new ProductionCenter(4, "PC4", 1.0, 2);
        pc3.addNext(pc4);

        List<ProductionCenter> allCenters = List.of(pc1, pc2, pc3, pc4);

        assertThrows(RuntimeException.class, () -> Validator.validateAndMarkInitialPc(allCenters));
    }
}
