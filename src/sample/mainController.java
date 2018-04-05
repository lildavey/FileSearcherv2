package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** TODO: 4/4/2018
 * root directory
 * search term
 * output list of files that match/contain search term
 * save output to file
 * status bar
 * search text files
 */
public class mainController implements Initializable{
    @FXML private GridPane root;
    @FXML private TextField directorySearch, search;
    @FXML private ListView fileList;
    @FXML private ChoiceBox searchType;
    @FXML private Button directoryChooser, searchBtn;
    @FXML private Stage stage;



    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        searchType.getItems().addAll("Match","Contains");

    }

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
        directoryChooser.setInitialDirectory(new File("fileSearchTest"));
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

    public void searchBtnAction() throws Exception
    {
        List<File> filesInFolder = Files.walk(Paths.get(directorySearch.getText()))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());

        ObservableList<File> list = FXCollections.observableArrayList(filesInFolder);
        for (File temp:list)
        {
            if(searchType.getValue().equals("Contains"))
            if((temp.getName().contains(search.getText())))fileList.getItems().add(temp);

        }
        //fileList.setItems(list);
    }

}
