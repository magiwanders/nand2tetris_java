package gui;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;

public class HackComputerGui extends Stage {

    private final VBox root;
        private final HBox PCinstruction;
            private final Label PC;
            private Label instruction;
        private final HBox registers;
            private final Label a;
            private Label A;
            private final Label d;
            private Label D;
        private final HBox keyboard;
            private final Label kbd;
            private Label KBD;
        private final Button step;
        private final HBox timing;
            private TextField hertz;
            private Label hz;
            private final Button execute;
        private Image SCREEN;
        private ImageView SCREEN_VIEW;

    public HackComputerGui() {
        super();
        root = new VBox();
            PCinstruction = new HBox();
                PC = new Label("Next Instruction: ");
                instruction = new Label();
            registers = new HBox();
                a = new Label("A register: ");
                A = new Label();
                d = new Label("            D register: ");
                D = new Label();
            keyboard = new HBox();
                kbd = new Label("KBD: ");
                KBD = new Label();
            step = new Button("Step");
            timing = new HBox();
                hertz = new TextField();
                hz = new Label("Hz");
                execute = new Button("Execute");
            SCREEN = new Image("https://raw.githubusercontent.com/magiwanders/nand2tetris_java/master/gui/SCREEN.png");
            SCREEN_VIEW = new ImageView();
            SCREEN_VIEW.setImage(SCREEN);

        assembleGUI();

        super.setTitle("The Hack Computer");
        super.setScene(new Scene(root));
        super.setWidth(512);
        super.setHeight(600);
        super.show();
    }

    private void assembleGUI() {
        root.getChildren().add(PCinstruction);
            PCinstruction.getChildren().add(PC);
            PCinstruction.getChildren().add(instruction);
        root.getChildren().add(registers);
            registers.getChildren().add(a);
            registers.getChildren().add(A);
            registers.getChildren().add(d);
            registers.getChildren().add(D);
        root.getChildren().add(keyboard);
            keyboard.getChildren().add(kbd);
            keyboard.getChildren().add(KBD);
        root.getChildren().add(step);
        root.getChildren().add(timing);
            timing.getChildren().add(hertz);
            timing.getChildren().add(hz);
            timing.getChildren().add(execute);
        root.getChildren().add(SCREEN_VIEW);
    }

    public Label getInstruction() { return instruction;}
    public Label getA() { return A;}
    public Label getD() { return D;}
    public Label getKBD() { return KBD;}
    public Button getStep() {return step;}


}
