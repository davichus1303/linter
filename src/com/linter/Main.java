package com.linter;
import java.nio.file.Path;

import com.linter.constants.GitCommands;
import com.linter.utils.GitExecutor;
import com.linter.utils.RouteManager;

public class Main {
	
	public static void main(String[] args) {
		Path route = null;
		String modifiedAndAddedFiilesCommand = GitCommands.MODIFIED_ADDED_TS_JS;
		route = RouteManager.obtenerRuta();
		
		String[] modifiedFiles = GitExecutor.executeGitCommand(modifiedAndAddedFiilesCommand, route.toString());
	}

}
