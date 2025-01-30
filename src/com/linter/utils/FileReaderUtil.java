package com.linter.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileReaderUtil {
    public static List<String> readFileLines(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
