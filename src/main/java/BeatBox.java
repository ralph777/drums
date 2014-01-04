/**
 * Created by rkoch on 19.12.13.
 */
import java.util.HashMap;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static javax.swing.BoxLayout.Y_AXIS;

public class BeatBox {

    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;

    Box buttonBox;
    JPanel background;

    int instrumentCount = 4; //TODO
    int taktLength = 16; //TODO
    //int rowOffset = 8; //TODO
    //int colOffset = 4; //TODO
    //int instrumentNamesCol = 2; //TODO // 2 oder 38
    //int taktRow = 6; //TODO

    String[][] playSequence;

    ArrayList<String> instrumentNames;
    ArrayList<Integer> instrumentNumbers;
    ArrayList<String> takt = new ArrayList<String>();
    /**
     * 35 Bass
     * 42 HiHat closed
     * 46 HiHat open
     * 38 Snare
     * 49 Crash
     * 39 Hand Clap
     * 50 High Tom
     * 60 Hi Bongo
     * 70 Maracas
     * 47 Whistle
     * 72 Low Conga
     * 64 Cowbell
     * 56 Vibraslap
     * 58 Low-mid Tom
     * 67 High Agogo
     * 63 Open High Conga
     */

    public static void main(final String[] args) {

        BeatBox beatBox = new BeatBox();

        beatBox.setDefaultSettings();
        beatBox.builderGUI();

        //System.out.println(PlaySequence[0][0]);
    }

    private void setDefaultSettings(){
        InstrumentHandler instrumentHandler = new InstrumentHandler();

        instrumentNames = instrumentHandler.getInstrumentNames(true);
        instrumentNumbers = instrumentHandler.getInstrumentNumbers(instrumentNames);
        System.out.println(instrumentNumbers.get(1));
        takt = instrumentHandler.getTakt(true);

        }

    private void builderGUI() {
        theFrame = new JFrame("Ralphs Beatbox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("Datei");
        JMenuItem menuItemLoad = new JMenuItem((new MyActionLoad()));
        JMenuItem menuItemGDocLoad = new JMenuItem((new MyActionGDocLoad()));
        JMenuItem menuItemAddInstrument = new JMenuItem((new MyActionAddInstrument()));
        JMenuItem menuItemSettings = new JMenuItem((new MyActionSettings()));
        menuFile.add(menuItemLoad);
        menuFile.add(menuItemGDocLoad);
        menuFile.add(menuItemAddInstrument);
        menuFile.add(menuItemSettings);
        menuBar.add(menuFile);

        checkboxList = new ArrayList<JCheckBox>();
        buttonBox = new Box(Y_AXIS);

        //Buttons
        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton LoadSpreadsheet = new JButton("Load Spreadsheet");
        LoadSpreadsheet.addActionListener(new MyLoadSpreadsheetListener());
        buttonBox.add(LoadSpreadsheet);

        //InstrumentNameBox
        GuiHandler guiHandler = new GuiHandler();
        Box instrumentNameBox = guiHandler.getGuiInstrumentNameBox(instrumentNames);

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, instrumentNameBox);

        theFrame.setJMenuBar(menuBar);
        theFrame.getContentPane().add(background);


        GridLayout grid = new GridLayout(instrumentCount+1,taktLength);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        //Takt
        for (int i = 0; i < takt.size(); i++) {
            mainPanel.add(new Label(takt.get(i)));
        }

        //PlaySequence
        playSequence = InstrumentHandler.getPlaySequence(instrumentNumbers, taktLength, true);

        ArrayList<JCheckBox> checkboxes = guiHandler.getCheckBoxes(playSequence);
        for (JCheckBox c : checkboxes){
            checkboxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);
    }

    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        int[] trackList = null;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < instrumentNumbers.size(); i++) {
            trackList = new int[taktLength];

            int key = instrumentNumbers.get(i);

            for(int j = 0; j < taktLength; j++) {
                JCheckBox jc = checkboxList.get(j + (taktLength*i));
                if ( jc.isSelected()) {
                    trackList[j] = key;
                }else {
                    trackList[j] = 0;
                }
            }

            makeTracks(trackList);
            track.add(makeEvent(176,1,127,0,16));
        }

        track.add(makeEvent(192,9,1,0,15));
        try {

            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Buttons
    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempoFactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * .97));
        }
    }

    public class MyLoadSpreadsheetListener implements ActionListener {
        public void actionPerformed(ActionEvent a) {

             //TODO LadeInformation anzeigen.

            InstrumentHandler instrumentHandler = new InstrumentHandler();
            instrumentNames = instrumentHandler.getInstrumentNames(false);
            instrumentNumbers = instrumentHandler.getInstrumentNumbers(instrumentNames);
            takt = instrumentHandler.getTakt(false);

            GuiHandler guiHandler = new GuiHandler();
            Box newInstrumentNameBox = guiHandler.getGuiInstrumentNameBox(instrumentNames);

            background.removeAll();
            mainPanel.removeAll();
            checkboxList.clear();
            background.add(BorderLayout.EAST, buttonBox);
            background.add(BorderLayout.WEST, newInstrumentNameBox);
            background.add(BorderLayout.CENTER, mainPanel);

            //Takt
            for (int i = 0; i < takt.size(); i++) {
                mainPanel.add(new Label(takt.get(i)));
            }

            //PlaySequence
            playSequence = InstrumentHandler.getPlaySequence(instrumentNumbers, taktLength, false);

            ArrayList<JCheckBox> checkboxes = guiHandler.getCheckBoxes(playSequence);
            for (JCheckBox c : checkboxes){
                checkboxList.add(c);
                mainPanel.add(c);
            }
            background.updateUI();
            theFrame.pack();
        }
    }

    //MenuItems
    public class MyActionGDocLoad extends AbstractAction {
        public MyActionGDocLoad() {
            super("GDoc Laden...");
        }

        public void actionPerformed(ActionEvent e) {
            //TODO LadeInformation anzeigen.

            InstrumentHandler instrumentHandler = new InstrumentHandler();
            instrumentNames = instrumentHandler.getInstrumentNames(false);
            instrumentNumbers = instrumentHandler.getInstrumentNumbers(instrumentNames);
            takt = instrumentHandler.getTakt(false);

            GuiHandler guiHandler = new GuiHandler();
            Box newInstrumentNameBox = guiHandler.getGuiInstrumentNameBox(instrumentNames);

            background.removeAll();
            mainPanel.removeAll();
            checkboxList.clear();
            background.add(BorderLayout.EAST, buttonBox);
            background.add(BorderLayout.WEST, newInstrumentNameBox);
            background.add(BorderLayout.CENTER, mainPanel);

            //Takt
            for (int i = 0; i < takt.size(); i++) {
                mainPanel.add(new Label(takt.get(i)));
            }

            //PlaySequence
            playSequence = InstrumentHandler.getPlaySequence(instrumentNumbers, taktLength, false);

            ArrayList<JCheckBox> checkboxes = guiHandler.getCheckBoxes(playSequence);
            for (JCheckBox c : checkboxes){
                checkboxList.add(c);
                mainPanel.add(c);
            }
            background.updateUI();
            theFrame.pack();
        }
    }

    public class MyActionLoad extends AbstractAction {
        public MyActionLoad() {
            super("Laden...");
        }

        public void actionPerformed(ActionEvent e) {
            // Button pressed logic goes here
        }
    }

    public class MyActionAddInstrument extends AbstractAction {
        public MyActionAddInstrument() {
            super("Instrument hinzufügen");
        }

        public void actionPerformed(ActionEvent e) {
            // Button pressed logic goes here
        }
    }

    public class MyActionSettings extends AbstractAction {
        public MyActionSettings() {
            super("Einstellungen");
        }

        public void actionPerformed(ActionEvent e) {
            String name = JOptionPane.showInputDialog(theFrame,"What is your name?",null);
            //JDialog settingsPopup = new JDialog();
            //theFrame.add(settingsPopup);
        }
    }



    //Music
    public void makeTracks(int[] list) {

        for(int i = 0; i < taktLength; i++) {
            int key = list[i];

            if(key != 0) {
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(128,9,key,100,i+1));
            }
        }
    }

    public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one, two);
            event = new MidiEvent(a, tick);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return event;
    }

}