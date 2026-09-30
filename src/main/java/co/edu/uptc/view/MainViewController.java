package co.edu.uptc.view;

import co.edu.uptc.controller.IndexingReport;
import co.edu.uptc.controller.MainController;
import co.edu.uptc.i18n.I18n;
import co.edu.uptc.model.Document;
import co.edu.uptc.model.DocumentMetadata;
import co.edu.uptc.model.SearchResult;
import co.edu.uptc.model.WordStat;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador exclusivamente visual.
 *
 * No contiene lógica de negocio.
 * Coordina la interfaz con MainController.
 */
public class MainViewController {

    @FXML
    private Label lblBrand;

    @FXML
    private Label lblSubtitle;

    @FXML
    private Button btnLanguage;

    @FXML
    private Button btnSelectFile;

    @FXML
    private Label lblSelectedFile;

    @FXML
    private TextField txtAuthor;

    @FXML
    private TextField txtCategoryInput;

    @FXML
    private Button btnUploadIndex;

    @FXML
    private TextField txtSearch;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnClearSearch;

    @FXML
    private ComboBox<String> cbCategory;

    @FXML
    private DatePicker dpMinDate;

    @FXML
    private Button btnClearFilters;

    @FXML
    private Tab tabResults;

    @FXML
    private Tab tabDocuments;

    @FXML
    private Label lblResultsTitle;

    @FXML
    private Label lblDocumentsTitle;

    @FXML
    private Label lblResultCount;

    @FXML
    private Label lblDocumentCount;

    @FXML
    private TableView<SearchResult> tblResults;

    @FXML
    private TableColumn<SearchResult, String> colTitle;

    @FXML
    private TableColumn<SearchResult, String> colAuthor;

    @FXML
    private TableColumn<SearchResult, String> colCategory;

    @FXML
    private TableColumn<SearchResult, String> colDate;

    @FXML
    private TableColumn<SearchResult, Double> colScore;

    @FXML
    private TableView<Document> tblDocuments;

    @FXML
    private TableColumn<Document, String> colDocName;

    @FXML
    private TableColumn<Document, String> colDocAuthor;

    @FXML
    private TableColumn<Document, String> colDocCategory;

    @FXML
    private TableColumn<Document, String> colDocDate;

    @FXML
    private TableColumn<Document, Void> colDocAction;

    @FXML
    private Label lblStatsTitle;

    @FXML
    private Label lblDocumentsMetric;

    @FXML
    private Label lblWordsMetric;

    @FXML
    private Label lblCloudTitle;

    @FXML
    private Label lblCloudHelper;

    @FXML
    private FlowPane flowWordCloud;

    @FXML
    private Label lblStatus;

    private final MainController mainController =
            new MainController(
                    "data/index.json",
                    "data/metadata.xml"
            );

    private final Popup autocompletePopup =
            new Popup();

    private final ListView<String> autocompleteList =
            new ListView<>();

    private final Map<String, String> categoryDisplayToValue =
            new HashMap<>();

    private List<File> selectedFiles = List.of();

    @FXML
    public void initialize() {

        configureResultTable();
        configureDocumentTable();
        configureAutocomplete();
        configureSearch();

        refreshAllViewData();
        updateTexts();

        Platform.runLater(this::updateStageTitle);
    }

    private void configureResultTable() {

        colTitle.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue()
                                        .getDocument()
                                        .getTitle()
                        )
        );

        colAuthor.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                authorOf(
                                        data.getValue()
                                                .getDocument()
                                )
                        )
        );

        colCategory.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                categoryOf(
                                        data.getValue()
                                                .getDocument()
                                )
                        )
        );

        colDate.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                dateOf(
                                        data.getValue()
                                                .getDocument()
                                )
                        )
        );

        colScore.setCellValueFactory(
                data ->
                        new SimpleDoubleProperty(
                                data.getValue()
                                        .getScore()
                        ).asObject()
        );

        colScore.setCellFactory(
                column ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    Double value,
                                    boolean empty) {

                                super.updateItem(
                                        value,
                                        empty
                                );

                                if (empty || value == null) {
                                    setText(null);
                                } else {
                                    setText(
                                            String.format(
                                                    "%.4f",
                                                    value
                                            )
                                    );
                                }
                            }
                        }
        );
    }

    private void configureDocumentTable() {

        colDocName.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                data.getValue()
                                        .getTitle()
                        )
        );

        colDocAuthor.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                authorOf(
                                        data.getValue()
                                )
                        )
        );

        colDocCategory.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                categoryOf(
                                        data.getValue()
                                )
                        )
        );

        colDocDate.setCellValueFactory(
                data ->
                        new SimpleStringProperty(
                                dateOf(
                                        data.getValue()
                                )
                        )
        );

        colDocAction.setCellFactory(
                column ->
                        new TableCell<>() {

                            private final Button deleteButton =
                                    new Button();

                            {
                                deleteButton
                                        .getStyleClass()
                                        .add("delete-button");

                                deleteButton.setOnAction(
                                        event -> {

                                            Document document =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );

                                            confirmDelete(document);
                                        }
                                );
                            }

                            @Override
                            protected void updateItem(
                                    Void item,
                                    boolean empty) {

                                super.updateItem(
                                        item,
                                        empty
                                );

                                if (empty) {
                                    setGraphic(null);
                                } else {

                                    deleteButton.setText(
                                            I18n.get(
                                                    "button.delete"
                                            )
                                    );

                                    setGraphic(
                                            deleteButton
                                    );
                                }
                            }
                        }
        );
    }

    private void configureAutocomplete() {

        autocompleteList.setPrefHeight(160);

        autocompleteList
                .getStyleClass()
                .add("autocomplete-popup-list");

        autocompletePopup.setAutoHide(true);
        autocompletePopup.setHideOnEscape(true);

        autocompletePopup
                .getContent()
                .add(autocompleteList);

        autocompleteList.setOnMouseClicked(
                event -> {

                    String selected =
                            autocompleteList
                                    .getSelectionModel()
                                    .getSelectedItem();

                    if (selected != null) {

                        txtSearch.setText(selected);

                        autocompletePopup.hide();

                        onSearch();
                    }
                }
        );
    }

    private void configureSearch() {

        txtSearch.textProperty()
                .addListener(
                        (obs, oldValue, newValue) -> {

                            if (newValue == null
                                    || newValue.isBlank()) {

                                autocompletePopup.hide();
                                return;
                            }

                            List<String> suggestions =
                                    mainController
                                            .getAutocompleteSuggestions(
                                                    newValue
                                            );

                            if (suggestions.isEmpty()) {

                                autocompletePopup.hide();
                                return;
                            }

                            autocompleteList.setItems(
                                    FXCollections
                                            .observableArrayList(
                                                    suggestions
                                            )
                            );

                            autocompleteList.setPrefWidth(
                                    txtSearch.getWidth()
                            );

                            Bounds bounds =
                                    txtSearch.localToScreen(
                                            txtSearch
                                                    .getBoundsInLocal()
                                    );

                            if (bounds != null) {

                                autocompletePopup.show(
                                        txtSearch,
                                        bounds.getMinX(),
                                        bounds.getMaxY() + 3
                                );
                            }
                        }
                );
    }

    @FXML
    public void onSelectFile() {

        FileChooser chooser =
                new FileChooser();

        chooser.setTitle(
                I18n.get("chooser.title")
        );

        chooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                I18n.get("chooser.filter"),
                                "*.txt",
                                "*.pdf",
                                "*.docx"
                        )
                );

        Stage stage =
                getStage(btnSelectFile);

        List<File> files =
                chooser.showOpenMultipleDialog(stage);

        if (files == null || files.isEmpty()) {

            selectedFiles = List.of();

            lblSelectedFile.setText(
                    I18n.get("upload.noFile")
            );

            return;
        }

        selectedFiles = List.copyOf(files);

        if (selectedFiles.size() == 1) {

            lblSelectedFile.setText(
                    selectedFiles
                            .get(0)
                            .getName()
            );

        } else {

            lblSelectedFile.setText(
                    I18n.get(
                            "upload.filesSelected",
                            selectedFiles.size()
                    )
            );
        }

        setStatus(
                I18n.get(
                        "status.filesSelected",
                        selectedFiles.size()
                )
        );
    }

    @FXML
    public void onUploadIndex() {

        if (selectedFiles.isEmpty()) {

            showMessage(
                    Alert.AlertType.WARNING,
                    I18n.get("alert.warningTitle"),
                    I18n.get("alert.selectFile")
            );

            return;
        }

        String author =
                txtAuthor.getText().isBlank()
                        ? I18n.get("author.anonymous")
                        : txtAuthor.getText().trim();

        String category =
                txtCategoryInput
                        .getText()
                        .isBlank()
                        ? I18n.get("category.general")
                        : txtCategoryInput
                                .getText()
                                .trim();

        IndexingReport report =
                mainController.indexFiles(
                        selectedFiles,
                        author,
                        category
                );

        selectedFiles = List.of();

        lblSelectedFile.setText(
                I18n.get("upload.noFile")
        );

        txtAuthor.clear();
        txtCategoryInput.clear();

        refreshAllViewData();

        setStatus(
                I18n.get(
                        "status.indexedSummary",
                        report.indexed(),
                        report.selected()
                )
        );

        showIndexingAlert(report);
    }

    @FXML
    public void onSearch() {

        String query =
                txtSearch.getText();

        if (query == null || query.isBlank()) {

            tblResults
                    .getItems()
                    .clear();

            updateResultCount();

            setStatus(
                    I18n.get("status.emptySearch")
            );

            autocompletePopup.hide();

            return;
        }

        String category =
                categoryDisplayToValue.getOrDefault(
                        cbCategory.getValue(),
                        cbCategory.getValue()
                );

        LocalDate minDate =
                dpMinDate.getValue();

        List<SearchResult> results =
                mainController.search(
                        query,
                        category,
                        minDate
                );

        tblResults.setItems(
                FXCollections.observableArrayList(
                        results
                )
        );

        updateResultCount();

        if (results.isEmpty()) {

            setStatus(
                    I18n.get("status.noResults")
            );

        } else {

            setStatus(
                    I18n.get(
                            "status.resultsFound",
                            results.size()
                    )
            );
        }

        autocompletePopup.hide();
    }

    @FXML
    public void onClearSearch() {

        txtSearch.clear();

        tblResults
                .getItems()
                .clear();

        updateResultCount();

        autocompletePopup.hide();

        setStatus(
                I18n.get("status.ready")
        );
    }

    @FXML
    public void onClearFilters() {

        cbCategory
                .getSelectionModel()
                .selectFirst();

        dpMinDate.setValue(null);

        if (!txtSearch.getText().isBlank()) {

            onSearch();

        } else {

            setStatus(
                    I18n.get("status.filtersCleared")
            );
        }
    }

    @FXML
    public void onToggleLanguage() {

        I18n.toggleLanguage();

        refreshAllViewData();
        updateTexts();
        updateStageTitle();
    }

    private void refreshAllViewData() {

        configureCategories();

        tblDocuments.setItems(
                FXCollections.observableArrayList(
                        mainController
                                .getIndexedDocuments()
                )
        );

        updateDocumentCount();
        updateStatistics();
        updateWordCloud();
    }

    private void configureCategories() {

        String current =
                categoryDisplayToValue.get(
                        cbCategory.getValue()
                );

        categoryDisplayToValue.clear();

        String all =
                I18n.get("filter.all");

        categoryDisplayToValue.put(
                all,
                "Todas"
        );

        List<String> values =
                new ArrayList<>();

        values.add(all);

        for (String category :
                mainController.getCategories()) {

            categoryDisplayToValue.put(
                    category,
                    category
            );

            values.add(category);
        }

        cbCategory.setItems(
                FXCollections.observableArrayList(
                        values
                )
        );

        String selected = all;

        if (current != null) {

            selected =
                    categoryDisplayToValue
                            .entrySet()
                            .stream()
                            .filter(
                                    entry ->
                                            entry.getValue()
                                                    .equalsIgnoreCase(
                                                            current
                                                    )
                            )
                            .map(
                                    Map.Entry::getKey
                            )
                            .findFirst()
                            .orElse(all);
        }

        cbCategory
                .getSelectionModel()
                .select(selected);
    }

    private void updateTexts() {

        lblBrand.setText(
                I18n.get("app.title")
        );

        lblSubtitle.setText(
                I18n.get("app.subtitle")
        );

        btnLanguage.setText(
                I18n.get("button.language")
        );

        btnSelectFile.setText(
                I18n.get("button.selectFile")
        );

        txtAuthor.setPromptText(
                I18n.get("input.author")
        );

        txtCategoryInput.setPromptText(
                I18n.get("input.category")
        );

        btnUploadIndex.setText(
                I18n.get("button.uploadIndex")
        );

        txtSearch.setPromptText(
                I18n.get("search.placeholder")
        );

        btnSearch.setText(
                I18n.get("button.search")
        );

        btnClearSearch.setText(
                I18n.get("button.clearSearch")
        );

        cbCategory.setPromptText(
                I18n.get("filter.category")
        );

        dpMinDate.setPromptText(
                I18n.get("filter.date")
        );

        btnClearFilters.setText(
                I18n.get("button.clearFilters")
        );

        tabResults.setText(
                I18n.get("tab.results")
        );

        tabDocuments.setText(
                I18n.get("tab.documents")
        );

        lblResultsTitle.setText(
                I18n.get("results.title")
        );

        lblDocumentsTitle.setText(
                I18n.get("documents.title")
        );

        colTitle.setText(
                I18n.get("results.titleColumn")
        );

        colAuthor.setText(
                I18n.get("results.authorColumn")
        );

        colCategory.setText(
                I18n.get("results.categoryColumn")
        );

        colDate.setText(
                I18n.get("results.dateColumn")
        );

        colScore.setText(
                I18n.get("results.scoreColumn")
        );

        colDocName.setText(
                I18n.get("documents.nameColumn")
        );

        colDocAuthor.setText(
                I18n.get("documents.authorColumn")
        );

        colDocCategory.setText(
                I18n.get("documents.categoryColumn")
        );

        colDocDate.setText(
                I18n.get("documents.dateColumn")
        );

        colDocAction.setText(
                I18n.get("documents.actionColumn")
        );

        lblStatsTitle.setText(
                I18n.get("stats.title")
        );

        lblCloudTitle.setText(
                I18n.get("stats.cloudTitle")
        );

        lblCloudHelper.setText(
                I18n.get("stats.helper")
        );

        tblResults.setPlaceholder(
                new Label(
                        I18n.get("results.placeholder")
                )
        );

        tblDocuments.setPlaceholder(
                new Label(
                        I18n.get("documents.placeholder")
                )
        );

        updateResultCount();
        updateDocumentCount();
        updateStatistics();
        updateWordCloud();
    }

    private void updateWordCloud() {

        flowWordCloud
                .getChildren()
                .clear();

        List<WordStat> words =
                mainController.getTopWordFrequencies(18);

        if (words.isEmpty()) {

            flowWordCloud
                    .getChildren()
                    .add(
                            new Label(
                                    I18n.get("stats.empty")
                            )
                    );

            return;
        }

        int max =
                words.stream()
                        .mapToInt(WordStat::frequency)
                        .max()
                        .orElse(1);

        int min =
                words.stream()
                        .mapToInt(WordStat::frequency)
                        .min()
                        .orElse(1);

        for (WordStat stat : words) {

            Label chip =
                    new Label(stat.word());

            chip.getStyleClass()
                    .add("word-chip");

            double fontSize;

            if (min == max) {

                fontSize = 22;

            } else {

                fontSize =
                        13
                                + (
                                (stat.frequency() - min)
                                        * 18.0
                                        / (max - min)
                        );
            }

            chip.setStyle(
                    "-fx-font-size: "
                            + String.format(
                                    "%.1fpx",
                                    fontSize
                            )
                            + ";"
            );

            chip.setTooltip(
                    new javafx.scene.control.Tooltip(
                            I18n.get(
                                    "stats.tooltip",
                                    stat.frequency()
                            )
                    )
            );

            flowWordCloud
                    .getChildren()
                    .add(chip);
        }
    }

    private void updateStatistics() {

        lblDocumentsMetric.setText(
                String.valueOf(
                        mainController.getDocumentCount()
                )
        );

        lblWordsMetric.setText(
                String.valueOf(
                        mainController.getTotalIndexedWords()
                )
        );
    }

    private void updateResultCount() {

        int count =
                tblResults.getItems().size();

        lblResultCount.setText(
                I18n.get(
                        "status.resultsFound",
                        count
                )
        );
    }

    private void updateDocumentCount() {

        int count =
                tblDocuments.getItems().size();

        lblDocumentCount.setText(
                I18n.get(
                        "stats.documents",
                        count
                )
        );
    }

    private void confirmDelete(
            Document document) {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                I18n.get("alert.deleteTitle")
        );

        alert.setHeaderText(
                I18n.get("alert.deleteHeader")
        );

        alert.setContentText(
                I18n.get(
                        "alert.deleteMessage",
                        document.getTitle()
                )
        );

        alert.showAndWait()
                .ifPresent(
                        response -> {

                            if (response == ButtonType.OK) {

                                boolean removed =
                                        mainController
                                                .removeDocument(
                                                        document.getId()
                                                );

                                if (removed) {

                                    refreshAllViewData();

                                    if (!txtSearch
                                            .getText()
                                            .isBlank()) {

                                        onSearch();
                                    }

                                    setStatus(
                                            I18n.get(
                                                    "status.deleted"
                                            )
                                    );
                                }
                            }
                        }
                );
    }

    private void showIndexingAlert(
            IndexingReport report) {

        Alert.AlertType type =
                report.hasErrors()
                        ? Alert.AlertType.WARNING
                        : Alert.AlertType.INFORMATION;

        String title =
                report.hasErrors()
                        ? I18n.get("alert.warningTitle")
                        : I18n.get("alert.successTitle");

        StringBuilder message =
                new StringBuilder(
                        I18n.get(
                                "alert.indexed",
                                report.indexed()
                        )
                );

        if (report.hasErrors()) {

            message.append("\n\n")
                    .append(
                            I18n.get("alert.errors")
                    );

            report.errors()
                    .forEach(
                            error ->
                                    message.append(
                                            "\n• "
                                    ).append(error)
                    );
        }

        showMessage(
                type,
                title,
                message.toString()
        );
    }

    private void showMessage(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private String authorOf(
            Document document) {

        DocumentMetadata metadata =
                document.getMetadata();

        if (metadata == null
                || metadata.author() == null
                || metadata.author().isBlank()) {

            return I18n.get("author.anonymous");
        }

        return metadata.author();
    }

    private String categoryOf(
            Document document) {

        DocumentMetadata metadata =
                document.getMetadata();

        if (metadata == null
                || metadata.category() == null
                || metadata.category().isBlank()) {

            return I18n.get("category.general");
        }

        return metadata.category();
    }

    private String dateOf(
            Document document) {

        DocumentMetadata metadata =
                document.getMetadata();

        if (metadata == null
                || metadata.creationDate() == null) {

            return "-";
        }

        return metadata.creationDate().toString();
    }

    private void setStatus(
            String message) {

        lblStatus.setText(message);
    }

    private Stage getStage(
            Node node) {

        if (node == null
                || node.getScene() == null) {

            return null;
        }

        return node
                .getScene()
                .getWindow()
                instanceof Stage stage
                ? stage
                : null;
    }

    private void updateStageTitle() {

        Stage stage =
                getStage(btnLanguage);

        if (stage != null) {

            stage.setTitle(
                    I18n.get("window.title")
            );
        }
    }
}