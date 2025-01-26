package com.factory.simulation;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.factory.simulation.InputFileConstants.*;

public class FileService {

    public static Factory getFactory(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = WorkbookFactory.create(fis)) {
            int workersCount = getNumericCellValue(workbook, SCENARIO_SHEET_NAME, Scenario.WORKERS_COUNT_ROW_NUMBER,
                    Scenario.WORKERS_COUNT_CELL_NUMBER, "WorkersCount");
            int partsCount = getNumericCellValue(workbook, SCENARIO_SHEET_NAME, Scenario.PARTS_COUNT_ROW_NUMBER,
                    Scenario.PARTS_COUNT_CELL_NUMBER, "PartsCount");

            return new Factory(workersCount, partsCount, readProductionCenters(workbook));
        } catch (IOException | EncryptedDocumentException e) {
            throw new RuntimeException("Error while trying to read data from file", e);
        }
    }

    public static void writeReport(Report report, String directoryPath) {
        List<Report.Record> records = report.getRecords();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + "output.csv"))) {
            writer.write("Time, ProductionCenter, WorkersCount, BufferCount");
            writer.newLine();

            for (Report.Record record : records) {
                writer.write(record.time() + DELIMITER + record.productionCenter() + DELIMITER + record.workersCount() + DELIMITER + record.BufferCount());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getNumericCellValue(Workbook workbook, String sheetName, int rowNum, int cellNum, String valueName) {
        Sheet scenarioSheet = getSheet(workbook, sheetName);
        Row row = scenarioSheet.getRow(rowNum);
        if (row == null || row.getCell(cellNum) == null) {
            throw new RuntimeException(valueName + " is not defined");
        }
        return (int) row.getCell(cellNum).getNumericCellValue();
    }

    private static List<ProductionCenter> readProductionCenters(Workbook workbook) {
        List<ProductionCenter> productionCenters = new ArrayList<>();
        Sheet productionCenterSheet = getSheet(workbook, PRODUCTION_CENTER_SHEET_NAME);
        int lastRowNum = productionCenterSheet.getLastRowNum();
        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = productionCenterSheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            int id = row.getRowNum();
            String name = row.getCell(InputFileConstants.ProductionCenter.NAME_CELL_NUMBER).getStringCellValue();
            double performance = row.getCell(InputFileConstants.ProductionCenter.PERFORMANCE_CELL_NUMBER).getNumericCellValue();
            int maxWorkersCount = (int) row.getCell(InputFileConstants.ProductionCenter.MAX_WORKERS_COUNT_CELL_NUMBER).getNumericCellValue();

            ProductionCenter center = new ProductionCenter(id, name, performance, maxWorkersCount);
            productionCenters.add(center);

        }
        setProductionCentersConnections(workbook, productionCenters);
        return productionCenters;
    }

    private static void setProductionCentersConnections(Workbook workbook, List<ProductionCenter> productionCenters) {
        Map<String, ProductionCenter> nameToProductionCenter = productionCenters.stream()
                .collect(Collectors.toMap(ProductionCenter::getName, Function.identity()));

         Sheet connectionSheet = getSheet(workbook, CONNECTION_SHEET_NAME);

        int lastRowNum = connectionSheet.getLastRowNum();
        for (int rowIndex = 1; rowIndex <= lastRowNum; rowIndex++) {
            Row row = connectionSheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String sourceCenterName = row.getCell(Connection.SOURCE_CENTER_CELL_NUMBER).getStringCellValue();
            String destinationCenterName = row.getCell(Connection.DESTINATION_CENTER_CELL_NUMBER).getStringCellValue();

            Optional<ProductionCenter> sourceCenter = Optional.ofNullable(nameToProductionCenter.get(sourceCenterName));
            Optional<ProductionCenter> destinationCenter = Optional.ofNullable(nameToProductionCenter.get(destinationCenterName));

            sourceCenter.ifPresent(sc -> destinationCenter.ifPresent(sc::addNext));
        }
    }

    private static Sheet getSheet(Workbook workbook, String name) {
        Sheet sheet = workbook.getSheet(name);
        if (sheet == null) {
            throw new RuntimeException(String.format("Sheet \"%s\" not found in file", name));
        }
        return sheet;
    }
}
