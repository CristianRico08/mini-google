package co.edu.uptc.controller;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import co.edu.uptc.model.SearchResult;
import co.edu.uptc.model.WordStat;
import co.edu.uptc.service.SearchEngineService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class MainController {

    @FXML private TextField txtSearch;
    @FXML private ListView<String> lstAutocomplete;
    @FXML private TableView<SearchResult> tblResults;
    @FXML private TableColumn<SearchResult, String> colTitle;
    @FXML private TableColumn<SearchResult, String> colPath;
    @FXML private TableColumn<SearchResult, Double> colScore;
    @FXML private ComboBox<String> cbCategory;
    @FXML private DatePicker dpMinDate;
    @FXML private ListView<String> lstWordCloud;

    @FXML private TextField txtAuthor;
    @FXML private TextField txtCategoryInput;

    private SearchEngineService searchService;

    @FXML
    public void initialize() {
        searchService = new SearchEngineService("data/index.json", "data/metadata.xml");

        if (colTitle != null) colTitle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDocument().getTitle()));
        if (colPath != null) colPath.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDocument().getPath()));
        if (colScore != null) colScore.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getScore()).asObject());

        if (cbCategory != null) {
            cbCategory.setItems(FXCollections.observableArrayList("Todas", "Noticias", "Artículos", "Apuntes"));
            cbCategory.getSelectionModel().selectFirst();
        }

        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, oldText, newText) -> {
                if (newText == null || newText.isBlank()) {
                    if (lstAutocomplete != null) lstAutocomplete.getItems().clear();
                } else {
                    List<String> suggestions = searchService.getAutocompleteSuggestions(newText);
                    if (lstAutocomplete != null) lstAutocomplete.setItems(FXCollections.observableArrayList(suggestions));
                }
            });
        }

        if (lstAutocomplete != null) {
            lstAutocomplete.setOnMouseClicked(event -> {
                String selected = lstAutocomplete.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    txtSearch.setText(selected);
                    lstAutocomplete.getItems().clear();
                    onSearch();
                }
            });
        }

        updateWordCloud();
    }

    @FXML
    public void onSearch() {
        String query = txtSearch.getText();
        String category = cbCategory != null ? cbCategory.getValue() : null;
        LocalDate date = dpMinDate != null ? dpMinDate.getValue() : null;

        List<SearchResult> results = searchService.search(query, category, date);
        if (tblResults != null) {
            tblResults.setItems(FXCollections.observableArrayList(results));
        }
    }

    @FXML
    public void onUploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("Documentos Soportados (*.txt, *.pdf, *.docx)", "*.txt", "*.pdf", "*.docx")
            );
         File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String author = txtAuthor != null && !txtAuthor.getText().isBlank() ? txtAuthor.getText() : "Anónimo";
                String category = txtCategoryInput != null && !txtCategoryInput.getText().isBlank() ? txtCategoryInput.getText() : "General";

                searchService.indexFile(selectedFile, author, category);
                updateWordCloud();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Archivo indexado correctamente.");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al indexar el archivo: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void updateWordCloud() {
        if (lstWordCloud != null) {
            List<WordStat> topWords = searchService.getTopWordFrequencies(10);
            List<String> formatted = topWords.stream()
                    .map(w -> w.word() + " (" + w.frequency() + ")")
                    .toList();
            lstWordCloud.setItems(FXCollections.observableArrayList(formatted));
        }
    }
}