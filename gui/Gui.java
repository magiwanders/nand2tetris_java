package gui;

import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.* ;

public class Gui extends Stage {

  private VBox root;

    private HBox jackFile;
      private Label label1;
      private TextField textField1;
      private Button load1;

    private Button compileButton;

    private HBox vmcode;
      private Label label2;
      private TextField textField2;
      private Button load2;

    private Button translateButton;

    private HBox assembly;
      private Label label3;
      private TextField textField3;
      private Button load3;

    private Button assembleButton;

    private HBox binary;
      private Label label4;
      private TextField textField4;

    private Label errorLabel;

    public Gui() {
        super();

        root = new VBox();

        jackFile = new HBox();
          label1 = new Label("Jack File:     ");
          textField1 = new TextField();
          load1 = new Button("Load");

        compileButton = new Button("Compile");
        vmcode = new HBox();
          label2 = new Label("VMCode:        ");
          textField2 = new TextField();
          load2 = new Button("Load directory containing .vm files");


        translateButton = new Button("Translate");
        assembly = new HBox();
          label3 = new Label("Assembly File: ");
          textField3 = new TextField();
          load3 = new Button("Load .asm file");

        assembleButton = new Button("Assemble");
        binary = new HBox();
          label4 = new Label("Binary File:   ");
          textField4 = new TextField();

        errorLabel = new Label();

        assembleGUI();

        super.setTitle("nand2tetris");
        super.setScene(new Scene(root));
        super.setWidth(530);
        super.setHeight(230);
        super.show();
    }

    private void assembleGUI() {
      root.getChildren().add(jackFile);
        jackFile.getChildren().add(label1);
        jackFile.getChildren().add(textField1);
        jackFile.getChildren().add(load1);
      root.getChildren().add(compileButton);
      root.getChildren().add(vmcode);
        vmcode.getChildren().add(label2);
        vmcode.getChildren().add(textField2);
        vmcode.getChildren().add(load2);
      root.getChildren().add(translateButton);
      root.getChildren().add(assembly);
        assembly.getChildren().add(label3);
        assembly.getChildren().add(textField3);
        assembly.getChildren().add(load3);
      root.getChildren().add(assembleButton);
      root.getChildren().add(binary);
        binary.getChildren().add(label4);
        binary.getChildren().add(textField4);
      root.getChildren().add(errorLabel);
      errorLabel.setWrapText(true);

    }

    public Button getLoad1Button() {
      return load1;
    }

    public Button getLoad2Button() {
      return load2;
    }

    public Button getLoad3Button() {
      return load3;
    }

    public TextField getTextField1() {
      return textField1;
    }

    public TextField getTextField2() {
      return textField2;
    }

    public TextField getTextField3() {
      return textField3;
    }

    public TextField getTextField4() {
      return textField4;
    }

    public Button getAssembleButton() {
      return assembleButton;
    }

    public Button getTranslateButton() {
      return translateButton;
    }

}
