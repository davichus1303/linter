package com.linter.constants;

public class GitCommands {
    public static final String MODIFIED_ADDED_TS_JS = "public static final String MODIFIED_ADDED_TS_JS = \n"
    		+ "    \"(git diff --name-only --diff-filter=AM -- 'src/**/*.ts' 'src/**/*.js' && \" + \n"
    		+ "    \"git diff --cached --name-only --diff-filter=AM -- 'src/**/*.ts' 'src/**/*.js') "
    		+ "| grep -v 'src/environments/env.js'\";\n";
}
