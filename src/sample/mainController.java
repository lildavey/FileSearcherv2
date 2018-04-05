package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/** TODO: 4/4/2018
 * root directory
 * search term
 * output list of files that match/contain search term
 * save output to file
 * status bar
 * search text files
 */
public class mainController {
    @FXML private GridPane root;
    @FXML private TextField directorySearch, search;
    @FXML private MenuButton searchType;
    @FXML private Button directoryChooser;
    @FXML private Stage stage;

    /**
     *
     * @param event
     * @throws IOException
     * allows user to pick a folder
     * outputs folder directory path onto directorySearch
     */
    @FXML
    public void DirectorySearchAction(ActionEvent event) throws IOException
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if(selectedDirectory == null){
            directorySearch.setText("No Directory selected");
        }else{
            directorySearch.setText(selectedDirectory.getAbsolutePath());
        }


    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }



}
