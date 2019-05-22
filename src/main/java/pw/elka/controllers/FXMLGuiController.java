/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.elka.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pw.elka.App;
import pw.elka.simulator.Simulator;

/**
 * FXML Controller class
 *
 * @author janek
 */
public class FXMLGuiController implements Initializable {

    private App owner;
    private PrintStream print;
    private OutputStream out;
    private File file;
    private Simulator simulator;

    @FXML
    private TextArea console;
    @FXML
    private ProgressBar progress;
    @FXML
    private TextField path;
    @FXML
    private TextField repeats;
    @FXML
    private TextField events;
    @FXML
    private TextField lambda;
    @FXML
    private CheckBox crashes;
    @FXML
    private CheckBox uniform;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {

                console.appendText(String.valueOf((char) b));

                //console.setCaretPosition(textArea.getDocument().getLength());
            }
        };
        print = new PrintStream(out);
        System.setOut(print);
        //System.setErr(print);

        repeats.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    repeats.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        events.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                    String newValue) {
                if (!newValue.matches("\\d*")) {
                    events.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        

        file = new File(System.getProperty("user.dir") + "/simulation" + new Date().getTime() + ".csv");
        this.path.setText(file.getAbsolutePath());
    }

    @FXML
    private void chooseFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.setInitialFileName("user.home");

        chooser.setInitialDirectory(
                new File(System.getProperty("user.dir"))
        );
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Output", "*.csv"));
        chooser.setInitialFileName("simulation");
        file = chooser.showSaveDialog(new Stage());
        if (file != null) {
            this.path.setText(file.getAbsolutePath());
            System.out.println("Output file: " + file.getAbsolutePath());
        }
    }

    @FXML
    private void startSim(ActionEvent event) throws CloneNotSupportedException {
        //Tutaj bedzie kolejna logika wołana
        if (file == null) {
            System.out.print("Please select output file first\n");
            return;
        }
        System.out.print("Starting simulation...\n");
        // TODO jak skończysz to zapisz do pliku stat, tam jest metoda co zwraca stringa
        FXMLGuiController ref = this;
        this.progress.setProgress(0);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                simulator = new Simulator(Double.parseDouble(lambda.getText()), 0.125, false, ref);
                simulator.estimate(Integer.parseInt(repeats.getText()), Integer.parseInt(events.getText()), crashes.isSelected(), uniform.isSelected());
                FileWriter fr = null;
                simulator.compute();
                String text = simulator.getCsv();
                Platform.runLater(()->{
                    System.out.print("Writing to file...\n");
                });
                try {
                    fr = new FileWriter(file);
                    fr.write(text);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //close resources
                    try {
                        fr.close();
                        //System.out.print("Write success\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                file = new File(System.getProperty("user.dir") + "/simulation" + new Date().getTime() + ".csv");
                ref.path.setText(file.getAbsolutePath());
                return null;
            }
        };
        new Thread(task).start();

    }

    public App getOwner() {
        return owner;
    }

    public void setOwner(App owner) {
        this.owner = owner;
    }

    @FXML
    private void toggleConsole(ActionEvent event) {
        if (console.isVisible()) {
            console.setVisible(false);
        } else {
            console.setVisible(true);
        }
    }

    public void setProgress(double progress) {
        this.progress.setProgress(progress);
    }

    @FXML
    private void clear(ActionEvent event) {
        this.console.clear();
    }
}
