import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by rkoch on 29.12.13.
 */
public class GuiHandler {

    public Box getGuiInstrumentNameBox(ArrayList<String> instrumentNames) {
        Box instrumentNameBox = new Box(BoxLayout.Y_AXIS);
        instrumentNameBox.removeAll();
        for (int i = 0; i < instrumentNames.size(); i++) {
            instrumentNameBox.add(new Label(instrumentNames.get(i)));
        }
        return instrumentNameBox;
    }

    public ArrayList<JCheckBox> getCheckBoxes(String[][] playSequence) {
        ArrayList<JCheckBox> checkBoxes =  new ArrayList<JCheckBox>();
        for (int k = 0; k < playSequence.length; k++)
            for (int l = 0; l < playSequence[k].length; l++) {
                JCheckBox c = new JCheckBox();
                if (playSequence[k][l] == null) {
                    c.setSelected(false);
                } else {
                    c.setSelected(true);
                }
                checkBoxes.add(c);
            }
        return checkBoxes;
    }

}