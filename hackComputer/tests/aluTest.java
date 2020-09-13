package hackComputer.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import hackComputer.alu.Alu;
import hackComputer.utility.Gen;

public class aluTest {

    private boolean [][] x;
    private boolean [][] y;
    private boolean [][] cccccc;
    private boolean [][] out;
    private boolean [] zr;
    private boolean [] ng;

    @Test
    public void test() {
        Alu alu = new Alu();
        populateInputs();

        for(int i=0; i<36;i++) {
            Assert.assertArrayEquals(out[i], alu.out(x[i], y[i], cccccc[i]));
            if (zr[i]) {
                Assert.assertTrue(alu.zr(x[i], y[i], cccccc[i]));
            } else {
                Assert.assertFalse(alu.zr(x[i], y[i], cccccc[i]));
            }

            if (ng[i]) {
                Assert.assertTrue(alu.ng(x[i], y[i], cccccc[i]));
            } else {
                Assert.assertFalse(alu.ng(x[i], y[i], cccccc[i]));
            }
        }

    }

    private void populateInputs() {
        x = new boolean[][]{Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.false16(),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000010001"),};

        y = new boolean[][]{Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.true16(),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("0000000000000011"),};

        cccccc = new boolean[][]{Gen.custom6("101010"),
                Gen.custom6("111111"),
                Gen.custom6("111010"),
                Gen.custom6("001100"),
                Gen.custom6("110000"),
                Gen.custom6("001101"),
                Gen.custom6("110001"),
                Gen.custom6("001111"),
                Gen.custom6("110011"),
                Gen.custom6("011111"),
                Gen.custom6("110111"),
                Gen.custom6("001110"),
                Gen.custom6("110010"),
                Gen.custom6("000010"),
                Gen.custom6("010011"),
                Gen.custom6("000111"),
                Gen.custom6("000000"),
                Gen.custom6("010101"),
                Gen.custom6("101010"),
                Gen.custom6("111111"),
                Gen.custom6("111010"),
                Gen.custom6("001100"),
                Gen.custom6("110000"),
                Gen.custom6("001101"),
                Gen.custom6("110001"),
                Gen.custom6("001111"),
                Gen.custom6("110011"),
                Gen.custom6("011111"),
                Gen.custom6("110111"),
                Gen.custom6("001110"),
                Gen.custom6("110010"),
                Gen.custom6("000010"),
                Gen.custom6("010011"),
                Gen.custom6("000111"),
                Gen.custom6("000000"),
                Gen.custom6("010101"), };

        out = new boolean[][]{ Gen.false16(),
                Gen.custom16("0000000000000001"),
                Gen.true16(),
                Gen.false16(),
                Gen.true16(),
                Gen.true16(),
                Gen.false16(),
                Gen.false16(),
                Gen.custom16("0000000000000001"),
                Gen.custom16("0000000000000001"),
                Gen.false16(),
                Gen.true16(),
                Gen.custom16("1111111111111110"),
                Gen.true16(),
                Gen.custom16("0000000000000001"),
                Gen.true16(),
                Gen.false16(),
                Gen.true16(),
                Gen.false16(),
                Gen.custom16("0000000000000001"),
                Gen.true16(),
                Gen.custom16("0000000000010001"),
                Gen.custom16("0000000000000011"),
                Gen.custom16("1111111111101110"),
                Gen.custom16("1111111111111100"),
                Gen.custom16("1111111111101111"),
                Gen.custom16("1111111111111101"),
                Gen.custom16("0000000000010010"),
                Gen.custom16("0000000000000100"),
                Gen.custom16("0000000000010000"),
                Gen.custom16("0000000000000010"),
                Gen.custom16("0000000000010100"),
                Gen.custom16("0000000000001110"),
                Gen.custom16("1111111111110010"),
                Gen.custom16("0000000000000001"),
                Gen.custom16("0000000000010011"),
        };

        zr = new boolean[]{true, false, false, true, false, false, true,
                true, false, false, true, false, false, false,
                false, false, true, false, true, false, false,
                false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false};

        ng = new boolean[]{false, false, true, false, true, true, false,
                false, false, false, false, true, true, true,
                false, true, false, true, false, false, true,
                false, false, true, true, true, true, false,
                false, false, false, false, false, true, false, false};

    }
}