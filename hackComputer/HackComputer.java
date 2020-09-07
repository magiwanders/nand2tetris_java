package hackComputer;

import gui.HackComputerGui;
import hackComputer.logicGates.*;
import hackComputer.alu.*;
import hackComputer.Memory.*;
import hackComputer.CPU.*;
import hackComputer.utility.*;
import utilities.Log;

import java.io.*;

import static java.lang.Thread.sleep;

public class HackComputer {

    CPU cpu;
    RAM ram;
    ROM rom;

    HackComputerGui gui;
    String hackFile;
    BufferedReader r;

    public HackComputer(String hackFile, HackComputerGui hackComputerGui) {
        gui = hackComputerGui;
        this.hackFile = hackFile;
        cpu = new CPU();
        ram = new RAM();
        rom = new ROM();

        cpu.reset();
        updateGui();
        loadROM();
        setActions();
    }

    private void setActions() {
        gui.getStep().setOnAction(e -> handleStepButton());
    }

    private void handleStepButton() {
        boolean [] pcValue = cpu.getPCvalue();
        boolean [] currentInstruction = rom.read(pcValue);
        boolean [] inM = ram.getLastInM();
        ////System.out.println(Gen.boolToString(pcValue) + " " + Gen.boolToString(currentInstruction));
        cpu.update(currentInstruction, inM, false);
        //if(cpu.getWriteM())System.out.println("Writing " + Gen.boolToString(cpu.getOutM()) + " to "+ Gen.boolToString(cpu.getAddressM()));
        ram.write(cpu.getOutM(), cpu.getAddressM(), cpu.getWriteM());
        ram.update();
        updateGui();
        System.out.println("Ram[0] = " + Gen.boolToString(ram.read(Gen.toBoolean("0000000000000000"))));
        System.out.println("Ram[1] = " + Gen.boolToString(ram.read(Gen.toBoolean("0000000000000001"))));
        System.out.println("Ram[2] = " + Gen.boolToString(ram.read(Gen.toBoolean("0000000000000010"))));
       // System.out.println("Ram[3] = " + Gen.boolToString(ram.read(Gen.toBoolean("0000000000000011"))));
    }

    private void updateGui() {
        //Print.out(cpu.getPCvalue());
        //Print.out(cpu.getA());
        //Print.out(cpu.getD());
        gui.getInstruction().setText(Gen.boolToString(cpu.getPCvalue()));
        gui.getA().setText(Gen.boolToString(cpu.getA()));
        gui.getD().setText(Gen.boolToString(cpu.getD()));
    }

    private void loadROM() {
        boolean [] address = Gen.false16();
        Inc16 inc16 = new Inc16();
        try {
            r = new BufferedReader(new FileReader(new File(hackFile)));
            while(true) {
                String l = r.readLine();
                if (l == null) break;
                boolean [] line = Gen.toBoolean(l);
                rom.write(line, address, true);
                rom.update();
                //System.out.println("Setting ROM[" + Gen.boolToString(address) + "] to value " + Gen.boolToString(rom.read(address)));
                address = inc16.out(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean [] getA() {
        return cpu.getA();
    }

    public boolean [] getD() {
        return cpu.getD();
    }

    public boolean [] getPCvalue() {
        return cpu.getPCvalue();
    }

    private static void asleep(int time) {
        try {
            sleep(time);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
