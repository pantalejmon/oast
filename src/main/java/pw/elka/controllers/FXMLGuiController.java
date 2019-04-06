/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pw.elka.controllers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pw.elka.App;

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

    @FXML
    private TextArea console;
    @FXML
    private ProgressBar progress;
    @FXML
    private TextField path;

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
        System.setErr(print);
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
                new FileChooser.ExtensionFilter("Output", "*.oast"));
        chooser.setInitialFileName("simulation");
        file = chooser.showSaveDialog(new Stage());
        if (file != null) {
            this.path.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void startSim(ActionEvent event) {
        //Tutaj bedzie kolejna logika wołana
        System.out.print("Starting simulation...\n");
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
}
