package com.frauddetection.controller;

import com.frauddetection.rules.GestionRule;
import com.frauddetection.model.Rule;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Alert;
import javafx.util.Callback;

public class RuleManagementView {
    private GestionRule gestionRule; // Gestionnaire des règles
    // Les composants de l'interface
    private TextField ruleIdField;
    private ComboBox<String> ruleStateField;
    private TextField groupingKeyNamesField;
    private ComboBox<String> aggregatorFunctionTypeField;
    private ComboBox<String> limitOperatorTypeField;
    private TextField limitField;
    private TextField windowMinutesField;

    // Tableau pour afficher les règles
    private TableView<Rule> ruleTableView;
    private TableColumn<Rule, String> ruleIdColumn;
    private TableColumn<Rule, String> ruleStateColumn;
    private TableColumn<Rule, String> groupingKeyNamesColumn;
    private TableColumn<Rule, String> aggregatorFunctionTypeColumn;
    private TableColumn<Rule, String> limitOperatorTypeColumn;
    private TableColumn<Rule, String> limitColumn;
    private TableColumn<Rule, String> windowMinutesColumn;

    private <T> Callback<TableColumn<Rule, T>, TableCell<Rule, T>> centeredCellFactory() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setStyle("-fx-alignment: CENTER;"); // Centrer les valeurs
            }
        };
    }



    public RuleManagementView(GestionRule gestionRule) {
        this.gestionRule = gestionRule;

        // Initialiser les composants graphiques
        ruleIdField = new TextField();
        ruleStateField = new ComboBox<>();
        ruleStateField.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
        ruleStateField.setValue("ACTIVE");
        ruleStateField.setPrefWidth(200);

        groupingKeyNamesField = new TextField();  // Maintenant un champ texte pour entrer des clés séparées par des virgules

        aggregatorFunctionTypeField = new ComboBox<>();
        aggregatorFunctionTypeField.setItems(FXCollections.observableArrayList("SUM", "AVERAGE", "COUNT"));
        aggregatorFunctionTypeField.setValue("SUM");
        aggregatorFunctionTypeField.setPrefWidth(200);

        limitOperatorTypeField = new ComboBox<>();
        limitOperatorTypeField.setItems(FXCollections.observableArrayList("GREATER", "LESS", "GREATER_EQUAL", "LESS_EQUAL"));
        limitOperatorTypeField.setValue("GREATER");
        limitOperatorTypeField.setPrefWidth(200);

        limitField = new TextField();


        windowMinutesField = new TextField();


        limitField.setMaxWidth(200);
        windowMinutesField.setMaxWidth(200);


        Button addButton = new Button("Add Rule");
        Button updateButton = new Button("Update Rule");
        Button removeButton = new Button("Remove Rule");

        // Ajout des couleurs et des événements sur les boutons
        addButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-pref-width: 120px; " +
                        "-fx-pref-height: 35px; " +
                        "-fx-background-radius: 10;"
        );

        updateButton.setStyle(
                "-fx-background-color: #FF9800; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-pref-width: 120px; " +
                        "-fx-pref-height: 35px; " +
                        "-fx-background-radius: 10;"
        );

        removeButton.setStyle(
                "-fx-background-color: #F44336; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-pref-width: 120px; " +
                        "-fx-pref-height: 35px; " +
                        "-fx-background-radius: 10;"
        );


        addButton.setOnAction(e -> addRule());
        updateButton.setOnAction(e -> updateRule());
        removeButton.setOnAction(e -> removeRule());

        // Layout de la fenêtre de gestion des règles
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setVgap(15);  // Augmenter l'espacement vertical
        grid.setHgap(15);  // Augmenter l'espacement horizontal

        grid.getColumnConstraints().addAll(
                new ColumnConstraints(200), // Largeur des colonnes fixes pour uniformité
                new ColumnConstraints(200),
                new ColumnConstraints(200),
                new ColumnConstraints(200)
        );

        // Ajouter des labels personnalisés avec des couleurs et polices
        Label ruleIdLabel = new Label("Rule ID:");
        ruleIdLabel.setTextFill(Color.DARKBLUE);
        ruleIdLabel.setFont(Font.font("Arial", 14));
        grid.add(ruleIdLabel, 0, 2);

        grid.add(ruleIdField, 1, 2);

        Label ruleStateLabel = new Label("Rule State:");
        ruleStateLabel.setTextFill(Color.DARKBLUE);
        ruleStateLabel.setFont(Font.font("Arial", 14));
        grid.add(ruleStateLabel, 0, 3);
        grid.add(ruleStateField, 1, 3);

        Label groupingKeyNamesLabel = new Label("Grouping Key Names:");
        groupingKeyNamesLabel.setTextFill(Color.DARKBLUE);
        groupingKeyNamesLabel.setFont(Font.font("Arial", 14));
        grid.add(groupingKeyNamesLabel, 0, 4);
        grid.add(groupingKeyNamesField, 1, 4);

        Label aggregatorFunctionTypeLabel = new Label("Aggregator Function Type:");
        aggregatorFunctionTypeLabel.setTextFill(Color.DARKBLUE);
        aggregatorFunctionTypeLabel.setFont(Font.font("Arial", 14));
        grid.add(aggregatorFunctionTypeLabel, 0, 5);
        grid.add(aggregatorFunctionTypeField, 1, 5);

        Label limitOperatorTypeLabel = new Label("Limit Operator Type:");
        limitOperatorTypeLabel.setTextFill(Color.DARKBLUE);
        limitOperatorTypeLabel.setFont(Font.font("Arial", 14));
        grid.add(limitOperatorTypeLabel, 3, 2);
        GridPane.setMargin(limitOperatorTypeLabel, new Insets(0,0,0,50));
        grid.add(limitOperatorTypeField, 4, 2);
        GridPane.setMargin(limitOperatorTypeField, new Insets(0, 0, 0, 10));

        Label limitLabel = new Label("Limit:");
        limitLabel.setTextFill(Color.DARKBLUE);
        limitLabel.setFont(Font.font("Arial", 14));
        grid.add(limitLabel, 3, 3);
        GridPane.setMargin(limitLabel, new Insets(0,0,0,50));
        grid.add(limitField, 4, 3);
        GridPane.setMargin(limitField, new Insets(0, 0, 0, 10));

        Label windowMinutesLabel = new Label("Window Minutes:");
        windowMinutesLabel.setTextFill(Color.DARKBLUE);
        windowMinutesLabel.setFont(Font.font("Arial", 14));
        grid.add(windowMinutesLabel, 3, 4);
        GridPane.setMargin(windowMinutesLabel, new Insets(0,0,0,50));
        grid.add(windowMinutesField, 4, 4);
        GridPane.setMargin(windowMinutesField, new Insets(0, 0, 0, 10));


        // Ajouter une section avec les boutons en ligne (HBox)
        HBox buttonBox = new HBox(10, addButton, updateButton, removeButton);
        buttonBox.setPadding(new Insets(10,0,0,0));
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(buttonBox, Priority.ALWAYS);
        grid.add(buttonBox, 1, 8, 3, 1);

        // Titre "Rules List" avec style
        Label rulesLabel = new Label("Rules List:");
        rulesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;"); // Texte en vert et gras

        // Ajout au GridPane
        grid.add(rulesLabel, 0, 0);

        // Initialiser le tableau
        ruleTableView = new TableView<>();
        ruleTableView.setEditable(false);

        ruleTableView.skinProperty().addListener((observable, oldSkin, newSkin) -> {
            if (newSkin != null) {
                // Obtenir le noeud de l'en-tête après que le skin a été chargé
                Node header = ruleTableView.lookup(".column-header-background");
                if (header != null) {
                    header.setStyle("-fx-pref-height: 40px;"); // Augmente la hauteur de l'entête (ajustez la valeur)
                }
            }
        });

        // Créer les colonnes du tableau
        ruleIdColumn = new TableColumn<>("Rule ID");
        ruleIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getRuleId())));
        ruleIdColumn.setPrefWidth(140);

        ruleStateColumn = new TableColumn<>("Rule State");
        ruleStateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRuleState()));
        ruleStateColumn.setPrefWidth(170);
        // Définir le CellFactory pour personnaliser l'apparence de la cellule
        ruleStateColumn.setCellFactory(col -> new TableCell<Rule, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // Vérifier si la cellule est vide
                if (empty || item == null) {
                    setText(null);
                    setStyle("");  // Réinitialiser le style si la cellule est vide
                } else {
                    setText(item);  // Afficher le texte du statut

                    // Appliquer un style en fonction du statut
                    if ("ACTIVE".equalsIgnoreCase(item)) {
                        setStyle("-fx-background-color: #a8f5af; -fx-text-fill: white;");  // Vert pour 'active'
                    } else if ("INACTIVE".equalsIgnoreCase(item)) {
                        setStyle("-fx-background-color: red; -fx-text-fill: white;");  // Rouge pour 'inactive'
                    } else {
                        setStyle("");  // Réinitialiser pour d'autres statuts
                    }
                }
            }
        });
        groupingKeyNamesColumn = new TableColumn<>("Grouping Key Names");
        groupingKeyNamesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGroupingKeyNames()));
        groupingKeyNamesColumn.setPrefWidth(180);
        aggregatorFunctionTypeColumn = new TableColumn<>("Aggregator Function Type");
        aggregatorFunctionTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAggregatorFunctionType()));

        limitOperatorTypeColumn = new TableColumn<>("Limit Operator Type");
        limitOperatorTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLimitOperatorType()));
        limitOperatorTypeColumn.setPrefWidth(180);

        limitColumn = new TableColumn<>("Limit");
        limitColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLimit())));
        limitColumn.setPrefWidth(180);

        windowMinutesColumn = new TableColumn<>("Window Minutes");
        windowMinutesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWindowMinutes()));
        windowMinutesColumn.setPrefWidth(160);

        // Ajouter les colonnes à la table
        ruleTableView.getColumns().addAll(ruleIdColumn, ruleStateColumn, groupingKeyNamesColumn, aggregatorFunctionTypeColumn,
                limitOperatorTypeColumn, limitColumn, windowMinutesColumn);

        ruleTableView.setRowFactory(tv -> {
            TableRow<Rule> row = new TableRow<>();
           // row.setStyle("-fx-background-color: #ADD8E6;"); // Light color for all

            // Lorsqu'une ligne est mise à jour (en fonction de l'élément de la ligne)
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    // Vérifiez le statut de la règle et appliquez un style
                    if ("active".equalsIgnoreCase(newItem.getRuleState())) {
                        row.setStyle("-fx-background-color: #a8f5af; -fx-text-fill: white;");
                    } else if ("inactive".equalsIgnoreCase(newItem.getRuleState())) {
                        row.setStyle("-fx-background-color: #f96c87; -fx-text-fill: white;");
                    } else {
                        row.setStyle("-fx-background-color: ;"); // Réinitialiser le style si aucun statut particulier
                    }
                }
            });
            row.setPrefHeight(35);
            return row;
        });

        // Colorer l'en-tête
        ruleTableView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    ruleTableView.lookupAll(".column-header").forEach(header -> {
                        header.setStyle("-fx-background-color: #39b7fa; -fx-text-fill: white;");
                    });
                });
            }
        });

        // Appliquer le CellFactory pour centrer les valeurs
        ruleIdColumn.setCellFactory(centeredCellFactory());
        ruleStateColumn.setCellFactory(centeredCellFactory());
        groupingKeyNamesColumn.setCellFactory(centeredCellFactory());
        aggregatorFunctionTypeColumn.setCellFactory(centeredCellFactory());
        limitOperatorTypeColumn.setCellFactory(centeredCellFactory());
        limitColumn.setCellFactory(centeredCellFactory());
        windowMinutesColumn.setCellFactory(centeredCellFactory());


// ListView ajustée avec taille et style
        ruleTableView.setPrefHeight(300); // Hauteur de la liste
        ruleTableView.setPrefWidth(1200);  // Largeur de la liste
        //ruleTableView.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-padding: 5;"); // Bordure verte avec du padding

        grid.add(ruleTableView, 0, 1, 5, 1);
        refreshRuleTableView();
        
        // Gestion de la sélection dans la ListView
        ruleTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Rule selectedRule = newValue;
                // Remplir les champs avec les détails de la règle sélectionnée
                ruleIdField.setText(String.valueOf(selectedRule.getRuleId()));
                ruleStateField.setValue(selectedRule.getRuleState()); // Assurez-vous que ruleStateField est un objet approprié pour ce champ
                groupingKeyNamesField.setText(selectedRule.getGroupingKeyNames());
                aggregatorFunctionTypeField.setValue(selectedRule.getAggregatorFunctionType());
                limitOperatorTypeField.setValue(selectedRule.getLimitOperatorType());
                limitField.setText(String.valueOf(selectedRule.getLimit()));
                windowMinutesField.setText(selectedRule.getWindowMinutes());
                // Afficher les détails de la règle sélectionnée si nécessaire
                System.out.println("Règle sélectionnée: " + selectedRule.getRuleId());
            }
        });
    }

    // Méthode pour afficher une alerte
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode pour ajouter une nouvelle règle
    private void addRule() {
        if (ruleIdField.getText().isEmpty() || limitField.getText().isEmpty() || windowMinutesField.getText().isEmpty() || groupingKeyNamesField.getText().isEmpty()) {
            showAlert("Input Error", "Please fill all required fields.", Alert.AlertType.ERROR);
            return;
        }

        int ruleId = Integer.parseInt(ruleIdField.getText());
        if (gestionRule.getAllRules().stream().anyMatch(rule -> rule.getRuleId() == ruleId)) {
            showAlert("Duplicate Rule ID", "A rule with this ID already exists.", Alert.AlertType.ERROR);
            return;
        }

        Rule rule = new Rule(
                ruleId,
                ruleStateField.getValue(),
                groupingKeyNamesField.getText(),  // Récupération de la chaîne de caractères entrée
                aggregatorFunctionTypeField.getValue(),
                limitOperatorTypeField.getValue(),
                Double.parseDouble(limitField.getText()),
                windowMinutesField.getText()  // La durée de la fenêtre est toujours une chaîne
        );

        gestionRule.addRule(rule);
        refreshRuleTableView();
        showAlert("Success", "Rule added successfully.", Alert.AlertType.INFORMATION);
    }

    // Méthode pour mettre à jour une règle
    private void updateRule() {
        if (ruleIdField.getText().isEmpty()) {
            showAlert("Input Error", "Please enter the Rule ID to update.", Alert.AlertType.ERROR);
            return;
        }

        int ruleId = Integer.parseInt(ruleIdField.getText());
        Rule rule = new Rule(
                ruleId,
                ruleStateField.getValue(),
                groupingKeyNamesField.getText(),  // Récupération de la chaîne de caractères entrée
                aggregatorFunctionTypeField.getValue(),
                limitOperatorTypeField.getValue(),
                Double.parseDouble(limitField.getText()),
                windowMinutesField.getText()  // La durée de la fenêtre est toujours une chaîne
        );

        if (!gestionRule.updateRule(rule)) {
            showAlert("Update Error", "No rule found with this ID.", Alert.AlertType.ERROR);
        } else {
            refreshRuleTableView();
            showAlert("Success", "Rule updated successfully.", Alert.AlertType.INFORMATION);
        }
    }

    // Méthode pour supprimer une règle
    private void removeRule() {
        int ruleId;
        try {
            ruleId = Integer.parseInt(ruleIdField.getText());
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid Rule ID.", Alert.AlertType.ERROR);
            return;
        }

        if (!gestionRule.removeRule(ruleId)) {
            showAlert("Remove Error", "No rule found with this ID.", Alert.AlertType.ERROR);
        } else {
            refreshRuleTableView();
            showAlert("Success", "Rule removed successfully.", Alert.AlertType.INFORMATION);
        }
    }

    private void refreshRuleTableView() {
        ObservableList<Rule> rules = FXCollections.observableArrayList(gestionRule.getAllRules());
        ruleTableView.setItems(rules);
    }

    // Retourner le layout
    public GridPane getView() {
        return (GridPane) ruleTableView.getParent();
    }
}