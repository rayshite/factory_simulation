package com.factory.simulation;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductionCenterTest {

    @Test
    @SuppressWarnings("unchecked")
    void testDoWorkDecrementsRemainingTime() throws NoSuchFieldException, IllegalAccessException {
        ProductionCenter pc = new ProductionCenter(1, "PC1", 1.0, 3);
        pc.setBuffer(1);
        pc.setWorkersCount(1);
        pc.getFromBufferToWork();

        assertEquals(1, pc.getPartsInProcessCount());

        Field inProcess = pc.getClass().getDeclaredField("inProcess");
        inProcess.setAccessible(true);
        int remainingTimeBefore = ((List<Integer>) inProcess.get(pc)).getFirst();

        pc.doWork();

        int remainingTimeAfter = ((List<Integer>) inProcess.get(pc)).getFirst();

        assertEquals(1, pc.getPartsInProcessCount());
        assertEquals(remainingTimeBefore - 1, remainingTimeAfter);
    }

    @Test
    void testTransferReadyParts() {
        ProductionCenter pc1 = new ProductionCenter(1, "PC1", 1.0, 2);
        ProductionCenter pc2 = new ProductionCenter(2, "PC2", 2.0, 2);
        pc1.addNext(pc2);
        pc1.setBuffer(1);
        pc1.setWorkersCount(1);

        pc1.getFromBufferToWork();
        assertEquals(0, pc1.getBuffer());
        assertEquals(1, pc1.getPartsInProcessCount());

        for (int i = 0; i < 60; i++) {
            pc1.doWork();
        }
        pc1.transferReadyParts();

        assertEquals(0, pc1.getPartsInProcessCount());
        assertEquals(1, pc2.getBuffer());
    }

    @Test
    void testPriorityCalculation() {
        ProductionCenter pc = new ProductionCenter(1, "PC1", 1.0, 3);

        assertEquals(Float.MIN_VALUE, pc.getPriority());

        pc.setBuffer(5);
        assertEquals(5.0f, pc.getPriority());

        pc.setWorkersCount(3);
        pc.getFromBufferToWork();
        assertEquals(2, pc.getBuffer()); // из 5 две перешли в процесс
        assertEquals(3, pc.getPartsInProcessCount());

        float expected = 2 / 180f;
        assertEquals(expected, pc.getPriority(), 0.0001);
    }
}