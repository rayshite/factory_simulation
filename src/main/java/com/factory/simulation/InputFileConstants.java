package com.factory.simulation;

public abstract class InputFileConstants {

    public static final String SCENARIO_SHEET_NAME = "Scenario";
    public static final String PRODUCTION_CENTER_SHEET_NAME = "ProductionCenter";
    public static final String CONNECTION_SHEET_NAME = "Connection";

    public static class Scenario {
        public static final int WORKERS_COUNT_ROW_NUMBER = 1;
        public static final int WORKERS_COUNT_CELL_NUMBER = 0;
        public static final int PARTS_COUNT_ROW_NUMBER = 1;
        public static final int PARTS_COUNT_CELL_NUMBER = 1;
    }

    public static class ProductionCenter {
        public static final int NAME_CELL_NUMBER = 1;
        public static final int PERFORMANCE_CELL_NUMBER = 2;
        public static final int MAX_WORKERS_COUNT_CELL_NUMBER = 3;
    }

    public static class Connection {
        public static final int SOURCE_CENTER_CELL_NUMBER = 0;
        public static final int DESTINATION_CENTER_CELL_NUMBER = 1;
    }

    public static final String DELIMITER = ", ";
}
