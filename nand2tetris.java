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
    gui.getTextField1().textProperty().addListener((obs, oldText, newText) -> resetOuputFields());
    gui.getTextField2().textProperty().addListener((obs, oldText, newText) -> resetOuputFields());
    gui.getTextField3().textProperty().addListener((obs, oldText, newText) -> resetOuputFields());
  }

  private void resetOuputFields() {
    gui.getTextField4().setText("");
    gui.getErrorLabel().setText("");
  }

  private void load1ButtonHandle() {
    gui.getTextField1().setText(getJackFile());
  }

  private void load2ButtonHandle() {
    gui.getTextField2().setText(getVMDirectory());
  }

  private void load3ButtonHandle() {
    gui.getTextField3().setText(getAsmFile());
  }

  private String getJackFile() {
    return "";
  }

  private String getVMDirectory() {
    Stage stage = new Stage();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setInitialDirectory(retrieveSavedDirectory());
    File directory = directoryChooser.showDialog(stage);
    if (!containsVMFile(directory)) {
      gui.getErrorLabel().setText("ERROR: Chosen directory does not contain any .vm file.");
      return "";
    }
    return directory.getAbsolutePath();
  }

  private boolean containsVMFile(File directory) {
    String [] files = directory.list();
    if (files != null) {
      for(String file : files) {
        if (file.contains(".vm")) return true;
      }
    }
    return false;
  }

  private String getAsmFile() {
    Stage stage = new Stage();
    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(retrieveSavedDirectory());
    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Assembly files (*.asm)", "*.asm");
    fileChooser.getExtensionFilters().add(extFilter);
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
    File VMDirectory = new File(gui.getTextField2().getText());
    CodeWriter translator = new CodeWriter(VMDirectory);
    if (!VMDirectory.isDirectory()) {
      gui.getTextField3().setText(VMDirectory.getAbsolutePath().replaceAll(".vm", ".asm"));
    } else {
      String filePath = VMDirectory.getAbsolutePath() + File.separator + VMDirectory.getName() + ".asm";
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
    String path = "";
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
