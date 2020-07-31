// From nand2tetris_java folder
// export PATH_TO_FX=secondaprovainitinere/src/javafx/lib
// javac --module-path $PATH_TO_FX --add-modules javafx.controls nand2tetris.java
// java --module-path $PATH_TO_FX --add-modules javafx.controls secondaprovainitinere.src.Client

import gui.Gui;
//import assembler;
// import VMTranslator;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.* ;

import java.io.*;

public class nand2tetris extends Application {

  private Gui gui;

  private void setActions() {
    //gui.getAssemblerButton().setOnAction( this::assembleButtonHandle() );
  }

  @Override
  public void init() throws Exception {
    System.out.println("Nand2Tetris program initializing...");
  }

  @Override
  public void start(Stage window) throws Exception {
    gui = new Gui();
    setActions();
  }

  @Override
  public void stop() throws Exception {
    System.out.println("Exiting.");

  }

  public static void main(String [] args) {
    launch();
  }

}
