package com.linter.utils;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class RulesFunctions {

    // Expresión regular para detectar funciones públicas y privadas con comentarios JSDoc
    private static final Pattern FUNCTION_PATTERN = Pattern.compile(
            "/\\*\\*([\\s\\S]*?)\\*/\\s*(public|private)\\s+\\w+\\s+\\w+\\s*\\(.*\\)"
    );
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("@description");
    private static final Pattern PARAM_PATTERN = Pattern.compile("@param\\s+\\{(\\w+)\\}\\s+(\\w+)\\s+-\\s+(.+)");
    private static final Pattern RETURN_PATTERN = Pattern.compile("@returns\\s+\\{(\\w+)\\}\\s+(.+)");

    /**
     * Validates function comments in TypeScript/JavaScript code.
     * @param fileLines List of file lines.
     * @return List of lines that need review.
     */
    public static List<String> validateFunctionComments(List<String> fileLines) {
        String fileContent = String.join("\n", fileLines);
        Matcher functionMatcher = FUNCTION_PATTERN.matcher(fileContent);
        List<String> issues = new ArrayList<>();

        while (functionMatcher.find()) {
            String commentBlock = functionMatcher.group(1); // Extract comment block
            List<String> errors = checkCommentErrors(commentBlock);

            if (!errors.isEmpty()) {
                int lineNumber = findLineNumber(fileLines, functionMatcher.start());
                issues.add("Line " + lineNumber + ":\n" + String.join("\n", errors));
            }
        }

        return issues;
    }

    /**
     * Finds the line number in the file based on the position in the content.
     * @param fileLines List of file lines.
     * @param charIndex The character index where the function is found.
     * @return Line number.
     */
    private static int findLineNumber(List<String> fileLines, int charIndex) {
        int lineNumber = 1;
        int charCount = 0;

        for (String line : fileLines) {
            charCount += line.length() + 1; // Including newline character
            if (charCount > charIndex) {
                break;
            }
            lineNumber++;
        }

        return lineNumber;
    }

    /**
     * Checks the extracted comment block for missing tags.
     * @param commentBlock The comment block to check.
     * @return List of errors found.
     */
    private static List<String> checkCommentErrors(String commentBlock) {
        List<String> errors = new ArrayList<>();

        // Check for @description
        if (!DESCRIPTION_PATTERN.matcher(commentBlock).find()) {
            errors.add("❌ Missing @description tag.");
        }

        // Check @param format
        Matcher paramMatcher = PARAM_PATTERN.matcher(commentBlock);
        while (paramMatcher.find()) {
            if (paramMatcher.group(1).isEmpty() || paramMatcher.group(3).isEmpty()) {
                errors.add("❌ @param {"+paramMatcher.group(1)+"} "+paramMatcher.group(2)+" must include a description.");
            }
        }

        // Check @returns format
        Matcher returnMatcher = RETURN_PATTERN.matcher(commentBlock);
        if (returnMatcher.find() && (returnMatcher.group(1).isEmpty() || returnMatcher.group(2).isEmpty())) {
            errors.add("❌ @returns must include {type} and a description.");
        }

        return errors;
    }
    
    private void displayResults(String methodName, List<String> warnings, List<String> errors) {
        System.out.println(methodName);
        if (!warnings.isEmpty()) {
            System.out.println("Warnings");
            for (String warning : warnings) {
                System.out.println("  " + warning);
            }
        }
        if (!errors.isEmpty()) {
            System.out.println("Errors");
            for (String error : errors) {
                System.out.println("  " + error);
            }
        }
    }


    private static boolean isCamelCase(String methodName) {
        return methodName.matches("^[a-z]+([A-Z][a-z]*)*$");
    }
    

    private static String extractMethodName(String line) {
        // Extrae el nombre del método de la línea, asumiendo formato 'function methodName'
        String[] parts = line.split("\\(");
        return parts[0].replaceAll(".*\\s", "").trim();
    }

    private static List<String> extractParameters(String line) {
        // Extrae los parámetros de una función, asumiendo formato 'function(param1, param2)'
        List<String> params = new ArrayList<>();
        if (line.contains("(") && line.contains(")")) {
            String paramString = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
            String[] paramArray = paramString.split(",");
            for (String param : paramArray) {
                params.add(param.trim());
            }
        }
        return params;
    }

    private static String convertArrowToFunction(String line) {
        return line.replaceAll("\\.\\w+\\s*\\(.*?\\)\\s*=>", "function");
    }
    
    private static List<String> programingMethodsWarnings(List<String> fileLines, List<String> warnings){
    	for (String line : fileLines) {
            if (line.contains("function")) {
                List<String> params = extractParameters(line);
                for (String param : params) {
                    if (param.contains(":")) {
                    	warnings.add("⚠️ Parameter " + param + " should be typed.");
                    }
                }
            }

            if (line.contains(".find(") || line.contains(".map(")) {
                String modifiedLine = convertArrowToFunction(line);
                warnings.add("⚠️ Function declaration should be traditional: " + modifiedLine);
            }

            if (line.contains("let ")) {
                warnings.add("⚠️ 'let' is used. Please review: " + line.trim());
            }
        }
    	return warnings;
    }
    
    private static List<String> programingMethodsErrors(List<String> fileLines, List<String> errors){
    	for (String line : fileLines) {
            if (line.contains("public") || line.contains("private")) {
                String methodName = extractMethodName(line);
                if (!isCamelCase(methodName)) {
                    errors.add("❌ Method " + methodName + " is not in camelCase.");
                }
            }

            if (line.matches(".*\\b(if|return|while|for)\\b.*")) {
                errors.add("❌ Missing blank line before " + line.trim());
            }

            if (line.contains("var ")) {
                errors.add("❌ 'var' should not be used. Consider using 'let' or 'const'.");
            }
    	}
    	return errors;
    	
    	
    }

    /**
     * Reads the contents of a file into a List of strings (lines).
     * @param filePath The path of the file.
     * @return List of file lines.
     * @throws IOException If the file cannot be read.
     */
    public static List<String> readFileLines(String filePath) throws IOException {
        List<String> fileLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileLines.add(line);
            }
        }
        return fileLines;
    }
    
    /**
     * Applies function comment validation inside applyFixes.
     * @param fileLines List of file lines.
     * @return The same list of lines with potential fixes applied.
     */
    public static List<String> applyFixes(List<String> fileLines) {
        List<String> issues = validateFunctionComments(fileLines);
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        warnings = programingMethodsWarnings(fileLines, warnings);
        errors = programingMethodsErrors(fileLines, errors);
        if (!issues.isEmpty()) {
            System.out.println("⚠️ Issues found in comments:");
            issues.forEach(System.out::println);
        }
        
        if (!warnings.isEmpty()) {
        	warnings.forEach(System.out:: print);
        }

        if (!errors.isEmpty()) {
        	errors.forEach(System.out:: print);
        }
        return fileLines;
    }
    
}
