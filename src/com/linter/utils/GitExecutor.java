package com.linter.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitExecutor {

    // Method to execute a Git command
	public static String[] executeGitCommand(String command, String repositoryPath) {
	    try {
	        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
	        processBuilder.directory(new File(repositoryPath));
	        processBuilder.redirectErrorStream(true);
	        
	        Process process = processBuilder.start();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

	        List<String> outputLines = new ArrayList<>();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            outputLines.add(line);
	        }
	        process.waitFor();
	        
	        return outputLines.toArray(new String[0]);
	    } catch (IOException | InterruptedException e) {
	        e.printStackTrace();
	        return new String[0];
	    }
	}

}
