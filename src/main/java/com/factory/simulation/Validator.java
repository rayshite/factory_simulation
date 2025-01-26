package com.factory.simulation;

import java.util.*;

public class Validator {

    public static void validateAndMarkInitialPc(List<ProductionCenter> productionCenters) {
        assertProductionCentersAreSpecified(productionCenters);

        ProductionCenter initial = assertSingleInitialProductionCenterAndGet(productionCenters);
        initial.setAsInitial();

        assertSingleTerminatingProductionCenter(productionCenters);
        assertNoCyclesInProductionCentersGraph(productionCenters);
        assertProductionCentersGraphIsConnected(productionCenters, initial);
    }

    private static void assertProductionCentersAreSpecified(List<ProductionCenter> productionCenters) {
        if (productionCenters == null || productionCenters.isEmpty()) {
            throw new RuntimeException("No production centers specified");
        }
    }

    private static ProductionCenter assertSingleInitialProductionCenterAndGet(List<ProductionCenter> productionCenters) {
        Set<ProductionCenter> referencedCenters = new HashSet<>();
        for (ProductionCenter pc : productionCenters) {
            referencedCenters.addAll(pc.getNext());
        }

        List<ProductionCenter> startCenters = productionCenters.stream()
                .filter(pc -> !referencedCenters.contains(pc))
                .toList();

        if (startCenters.size() != 1) {
            throw new RuntimeException("Should be exactly one initial production center");
        }
        return startCenters.getFirst();
    }

    private static void assertSingleTerminatingProductionCenter(List<ProductionCenter> productionCenters) {
        List<ProductionCenter> endCenters = productionCenters.stream()
                .filter(pc -> pc.getNext().isEmpty())
                .toList();

        if (endCenters.size() != 1) {
            throw new RuntimeException("Should be exactly one terminating production center");
        }
    }

    private static void assertNoCyclesInProductionCentersGraph(List<ProductionCenter> productionCenters) {
        Map<ProductionCenter, State> states = new HashMap<>();
        for (ProductionCenter pc : productionCenters) {
            states.put(pc, State.UNVISITED);
        }

        for (ProductionCenter pc : productionCenters) {
            if (states.get(pc) == State.UNVISITED) {
                if (dfsHasCycle(pc, states)) {
                    throw new RuntimeException("The graph of processing centers contains cycles");
                }
            }
        }
    }

    public static void assertProductionCentersGraphIsConnected(List<ProductionCenter> productionCenters, ProductionCenter initial) {
        Set<ProductionCenter> visited = new HashSet<>();
        traverse(initial, visited);
        if (visited.size() != productionCenters.size()) {
            throw new RuntimeException("The graph of processing centers is not connected");
        }
    }

    private static void traverse(ProductionCenter current, Set<ProductionCenter> visited) {
        boolean wasAdded = visited.add(current);
        if (!wasAdded) {
            return;
        }
        for (ProductionCenter next : current.getNext()) {
            traverse(next, visited);
        }
    }

    private static boolean dfsHasCycle(ProductionCenter current, Map<ProductionCenter, State> states) {
        states.put(current, State.VISITING);
        for (ProductionCenter nxt : current.getNext()) {
            State nxtState = states.get(nxt);

            if (nxtState == State.VISITING) {
                return true;
            }
            if (nxtState == State.UNVISITED) {
                if (dfsHasCycle(nxt, states)) {
                    return true;
                }
            }
        }
        states.put(current, State.VISITED);
        return false;
    }

    private enum State {
        UNVISITED, VISITING, VISITED
    }
}
