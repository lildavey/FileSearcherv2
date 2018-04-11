package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Search extends Thread {

    @FXML
    private TextField directorySearch, search;
    @FXML private ListView fileList;
    @FXML private ComboBox<String> searchType;
    @FXML private Label filesSearched, resultsFound;

    public Search() {
    }
    public void run()
    {
        fileList.getItems().clear();
        List<File> filesInFolder = null;
        try {
            filesInFolder = Files.walk(Paths.get(directorySearch.getText()))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        ObservableList<File> list = FXCollections.observableArrayList(filesInFolder);


        for (File temp:list)
        {
            if(!textSearch.isSelected()) {

                if (searchType.getValue().equals("Contains") && (temp.getName().contains(search.getText()))) fileList.getItems().add(temp);

                if (searchType.getValue().equals("Match") && (temp.getName().equals(search.getText()))) fileList.getItems().add(temp);

                if (searchType.getValue().equals("Best Match")) fileList.setItems(fuzzySearch(list));

                if (searchType.getValue().equals("Regular Expression")) fileList.setItems(regexSearch(list));
            }
            else
            {

                if (searchType.getValue().equals("Contains")) fileList.setItems(fileContainsSearch(list));

                if (searchType.getValue().equals("Match")) fileList.setItems(fileMatchSearch(list));

                //if (searchType.getValue().equals("Best Match")) fileList.setItems(fuzzySearch(list));

                if (searchType.getValue().equals("Regular Expression")) fileList.setItems(fileRegexSearch(list));

            }

        }
        resultsFound.setText("Results Found: "+fileList.getItems().size());
        filesSearched.setText("Files Searched: "+list.size());


        Platform.runLater(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
