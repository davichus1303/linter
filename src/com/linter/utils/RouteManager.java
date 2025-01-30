package com.linter.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class RouteManager {

  /**
   * Obtains the path to a project directory.
   * 
   * This method prompts the user to input the name of a project folder within a default directory.
   * If the user provides an empty input, it then prompts the user to input the full path to the project directory.
   * 
   * @return A Path object representing the project directory.
   */
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
