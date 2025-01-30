package com.linter.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class RouteManager {

    public static Path obtenerRuta() {
		String defaultRoute = "/home/david/Tep/";
        Scanner scanner = new Scanner(System.in);

        System.out.print("Escribe el nombre del proyecto en la carpeta default, enter para asignar ruta: ");
        
        String projectFolder = scanner.nextLine();
        if (projectFolder.equals("")) {
        	System.out.print("Escribe la ruta del proyecto:");
        	projectFolder = scanner.nextLine();
        } else {
        	projectFolder = defaultRoute + projectFolder;
        }
        Path route = Paths.get(projectFolder);
        scanner.close();
        
        return route;
    }
}
