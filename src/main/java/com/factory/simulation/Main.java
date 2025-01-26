package com.factory.simulation;

import static java.io.File.separatorChar;

public class Main {

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            throw new RuntimeException("Input file path is not specified");
        }
        String inputFilePath = args[0];

        Factory factory = FileService.getFactory(inputFilePath);
        factory.startSimulation();
        FileService.writeReport(factory.getReport(),
                inputFilePath.substring(0, inputFilePath.lastIndexOf(separatorChar) + 1));
    }
}
