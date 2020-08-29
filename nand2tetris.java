// From nand2tetris_java folder
// export PATH_TO_FX=secondaprovainitinere/src/javafx/lib
// javac --module-path $PATH_TO_FX --add-modules javafx.controls nand2tetris.java
// java --module-path $PATH_TO_FX --add-modules javafx.controls nand2tetris

import gui.Gui;
import assembler.*;
import vmtranslator.*;

import javafx.application.*;
import javafx.stage.*;

import java.io.*;

public class nand2tetris extends Application {

  private Gui gui;

  private void setActions() {
    gui.getLoad1Button().setOnAction( e -> load1ButtonHandle() );
    gui.getLoad2Button().setOnAction( e -> load2ButtonHandle() );
    gui.getLoad3Button().setOnAction( e -> load3ButtonHandle() );
    gui.getAssembleButton().setOnAction( e -> assembleButtonHandle() );
    gui.getTranslateButton().setOnAction( e -> translateButtonHandle() );
  }

  private void load1ButtonHandle() {
    gui.getTextField1().setText(getFile());
  }

  private void load2ButtonHandle() {
    gui.getTextField2().setText(getDirectory());
  }

  private void load3ButtonHandle() {
    gui.getTextField3().setText(getFile());
  }

  private String getDirectory() {
    Stage stage = new Stage();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setInitialDirectory(retrieveSavedDirectory());
    File file = directoryChooser.showDialog(stage);
    return file.getAbsolutePath();
  }

  private String getFile() {
    Stage stage = new Stage();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(retrieveSavedDirectory());
    File file = fileChooser.showOpenDialog(stage);
    return file.getAbsolutePath();
  }

  private void assembleButtonHandle() {
    String assemblyFile = gui.getTextField3().getText();
    HackAssembler assembler = new HackAssembler(assemblyFile);
    gui.getTextField4().setText(assemblyFile.replaceAll(".asm", ".hack"));
    saveLastDirectory();
  }

  private void translateButtonHandle() {
    File VMFile = new File(gui.getTextField2().getText());
    CodeWriter translator = new CodeWriter(VMFile, VMFile.isDirectory());
    if (!VMFile.isDirectory()) {
      gui.getTextField3().setText(VMFile.getAbsolutePath().replaceAll(".vm", ".asm"));
    } else {
      String filePath = VMFile.getAbsolutePath() + File.separator + VMFile.getName() + ".asm";
      gui.getTextField3().setText(filePath);
    }

    assembleButtonHandle();
  }

  private void saveLastDirectory() {
    try {
      String basePath = new File("").getAbsolutePath();
      PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(new File(basePath + File.separator + "lastPath.txt"))));
      w.println(new File(gui.getTextField4().getText()).getParent());
      w.close();
    } catch (Exception e)  {
      e.printStackTrace();
    }
  }

  private File retrieveSavedDirectory() {
    String path = new String();
    try {
      String basePath = new File("").getAbsolutePath();
      BufferedReader r = new BufferedReader(new FileReader(new File(basePath + File.separator + "lastPath.txt")));
      path = r.readLine();
      r.close();
    } catch (Exception e)  {
      e.printStackTrace();
    }
    return new File(path);
  }

  @Override
  public void init() {
    System.out.println("Nand2Tetris program initializing...");
  }

  @Override
  public void start(Stage window) {
    gui = new Gui();
    setActions();
  }

  @Override
  public void stop() {
    System.out.println("Exiting.");

  }

  public static void main(String [] args) {
    launch();
  }

}
