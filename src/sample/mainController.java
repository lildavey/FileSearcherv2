package sample;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.*;


import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @FXML private ComboBox<String> searchType;
    @FXML private Label filesSearched, resultsFound, directory, currentFile;
    @FXML private CheckBox textSearch;

    @FXML private Button directoryChooser, searchBtn, save;
    @FXML private Stage stage;
    @FXML private ProgressBar progress;



    private Desktop desktop = Desktop.getDesktop();




    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Desktop desktop = Desktop.getDesktop();

        searchType.getItems().addAll("Match","Contains", "Best Match", "Regular Expression");
        searchType.setValue("Best Match");
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
        directoryChooser.setInitialDirectory(new File("src"));
        File selectedDirectory = directoryChooser.showDialog(stage);

        if(selectedDirectory == null){
            directorySearch.setText("");
        }else{
            directorySearch.setText(selectedDirectory.getAbsolutePath());
            stage.setTitle("File Search - " +selectedDirectory.getAbsolutePath());
        }


    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void searchBtnAction() throws Exception
    {
        Search search1 = new Search(directorySearch,search,fileList,searchType,filesSearched,resultsFound,textSearch, progress, currentFile);
        search1.run();
    }

    /**
     *
     * @param list
     * @return sorted list by Levenshtein distance
     *
     */
    public static ObservableList<File> fuzzySearch(ObservableList<File> list, String searchTerm) throws FileNotFoundException
    {
        List<File> returnList = new ArrayList<File>();
        Map<Integer, File> fuzzyList = new TreeMap<Integer, File>();
        for (File temp: list)
        {
            fuzzyList.put( distance(searchTerm,temp.getName()), temp);
        }
        if(returnList.size()<25)
        for (Map.Entry<Integer, File> temp:fuzzyList.entrySet())
        {
            returnList.add(temp.getValue());
        }
        return FXCollections.observableArrayList(returnList);


    }

    public static ObservableList<File> regexSearch(ObservableList<File> list, String searchTerm) throws FileNotFoundException
    {
        Pattern pattern = Pattern.compile(searchTerm);
        List<File> returnList = new ArrayList<File>();
        Matcher m;
        for (File temp: list)
        {
            m = pattern.matcher(temp.getName());
            if(m.find())returnList.add(temp);


        }
        return FXCollections.observableArrayList(returnList);
    }

    /**
     *
     * @param list
     * @return list containing all text files that contain the search term
     * @throws FileNotFoundException
     */
    public static ObservableList<File> fileContainsSearch(ObservableList<File> list, String searchTerm) throws FileNotFoundException
    {
        List<File> returnList = new ArrayList<File>();
        Scanner scan;

        for (File temp: list) {
            scan = new Scanner(temp);
            while (scan.hasNext()) {
                String line = scan.nextLine().toLowerCase().toString();
                if (line.contains(searchTerm)) {
                    returnList.add(temp);
                }
            }
        }
        return FXCollections.observableArrayList(returnList);

    }

    public static ObservableList<File> fileMatchSearch(ObservableList<File> list, String searchTerm) throws FileNotFoundException
    {
        List<File> returnList = new ArrayList<File>();
        Scanner scan;

        for (File temp: list) {
            String fileText = "";
            scan = new Scanner(temp);
            while (scan.hasNext()) {
                fileText = fileText + scan.nextLine().toLowerCase().toString();
            }
            if(fileText.equals(searchTerm))returnList.add(temp);
        }
        return FXCollections.observableArrayList(returnList);
    }

    public static ObservableList<File> fileRegexSearch(ObservableList<File> list, String searchTerm) throws FileNotFoundException
    {
        List<File> returnList = new ArrayList<File>();
        Scanner scan;

        Pattern pattern = Pattern.compile(searchTerm);
        Matcher m;

        for (File temp: list) {
            String fileText = "";
            scan = new Scanner(temp);
            m = pattern.matcher(temp.getName());
            while (scan.hasNext()) {
                fileText = fileText + scan.nextLine().toLowerCase();
            }
            if(m.find())returnList.add(temp);
        }
        return FXCollections.observableArrayList(returnList);
    }

    public static ObservableList<File> fileFuzzySearch(ObservableList<File> list, String searchTerm) throws FileNotFoundException
    {

        List<File> returnList = new ArrayList<File>();
        Map<Integer, File> fuzzyList = new TreeMap<Integer, File>();

        //Map<Integer, File> fuzzyFileList = new TreeMap<Integer, File>();

        Scanner scan;
        for (File temp: list)
        {
            scan = new Scanner(temp);
            String[] tempList;

            String fileText = "";
            int tempNum;

                //puts all text from file into list
            while (scan.hasNext()) {
                    fileText = fileText + scan.nextLine().toLowerCase();
            }
            tempList = fileText.split(" ");

            tempNum = distance(searchTerm,tempList[0]);
            //finds smallest distance
            for(String tempString: tempList)
            {
                if(distance(searchTerm,tempString)<tempNum)tempNum = distance(searchTerm,tempString);
            }
            //adds file corresponding to its lowest distance
            fuzzyList.put( tempNum, temp);
        }


        if(returnList.size()<25)
            for (Map.Entry<Integer, File> temp:fuzzyList.entrySet())
            {
                returnList.add(temp.getValue());
            }
        return FXCollections.observableArrayList(returnList);

    }

    /**
     *
     * @param a
     * @param b
     * @return The minimum number of single-character edits (insertions, deletions or substitutions) required to change b into a
     * used in fuzzSearch
     *
     */
    public static int distance(String a, String b)
    {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    public void openFile(MouseEvent click) {

        if (click.getClickCount() == 2) {

            File currentItemSelected = (File) fileList.getSelectionModel().getSelectedItem();
            try {
                if (currentItemSelected.exists())
                    desktop.open(currentItemSelected);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    public void saveFile() throws IOException
    {

        Dialog dialog = new TextInputDialog();

        dialog.setTitle("Save List");

        Button okButton = (Button) dialog.getDialogPane().lookupButton( ButtonType.OK );
        okButton.setText("Save");

        dialog.setHeaderText("Save file as:");


        Optional<String> result = dialog.showAndWait();


        String entered = "";


        if (result.isPresent()) {
            entered = result.get();

        }
        if(!entered.equals("")) {
            PrintWriter writer = new PrintWriter(entered + ".txt", "UTF-8");
            for (Object temp : fileList.getItems()) {
                writer.println(temp);
            }
            writer.close();
        }
    }
}

class Search extends Thread {

    @FXML private TextField directorySearch, search;
    @FXML private ListView fileList;
    @FXML private ComboBox<String> searchType;
    @FXML private Label filesSearched, resultsFound, currentFile;
    @FXML private CheckBox textSearch;
    @FXML private ProgressBar progress;
    private int numResultsFound, numFilesSearched = 0;
    private String currFileText="";


    /**
     *
     * @param directorySearch
     * @param search
     * @param fileList
     * @param searchType
     * @param filesSearched
     * @param resultsFound
     * @param textSearch
     */
    public Search(TextField directorySearch, TextField search, ListView fileList, ComboBox<String> searchType, Label filesSearched, Label resultsFound, CheckBox textSearch, ProgressBar progress, Label currentFile) {
        this.directorySearch = directorySearch;
        this.search = search;
        this.fileList = fileList;
        this.searchType =searchType;
        this.filesSearched = filesSearched;
        this.resultsFound = resultsFound;
        this.textSearch = textSearch;
        this.progress = progress;
        this.currentFile = currentFile;
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


        try {
            for (File temp : list) {
                if (!textSearch.isSelected()) {

                    if (searchType.getValue().equals("Contains") && (temp.getName().contains(search.getText())))
                        fileList.getItems().add(temp);

                    if (searchType.getValue().equals("Match") && (temp.getName().equals(search.getText())))
                        fileList.getItems().add(temp);

                    if (searchType.getValue().equals("Best Match"))
                        fileList.setItems(mainController.fuzzySearch(list, search.getText()));

                    if (searchType.getValue().equals("Regular Expression"))
                        fileList.setItems(mainController.regexSearch(list, search.getText()));
                } else {

                    if (searchType.getValue().equals("Contains"))
                        fileList.setItems(mainController.fileContainsSearch(list, search.getText()));

                    if (searchType.getValue().equals("Match"))
                        fileList.setItems(mainController.fileMatchSearch(list, search.getText()));

                    if (searchType.getValue().equals("Best Match"))
                        fileList.setItems(mainController.fileFuzzySearch(list, search.getText()));

                    if (searchType.getValue().equals("Regular Expression"))
                        fileList.setItems(mainController.fileRegexSearch(list, search.getText()));

                }

                currFileText = temp.getAbsolutePath();


                Platform.runLater(
                        () -> {

                            System.out.println("numFilesSearched = " + numFilesSearched);
                            System.out.println("list = " + list.size());
                            //System.out.println("progress = " + progress.getProgress());
                            resultsFound.setText("Results Found: " + numResultsFound++);
                            filesSearched.setText("Files Searched: "+numFilesSearched++);
                            progress.setProgress((numFilesSearched/list.size()));
                            currentFile.setText(currFileText);

                        });

            }


        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        }


    }
}