package com.example.webbuilder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;

public class HelloApplication extends Application {

    private File selectedImage;
    private Color selectedColor = Color.WHITE;
    private String headerText = "";
    private String navbarText = "";
    private String pageText = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        Button headerButton = new Button("Select Header");
        headerButton.setOnAction(event -> {
            TextArea headerTextArea = new TextArea();
            headerTextArea.setPrefRowCount(4);
            headerTextArea.setWrapText(true);
            headerTextArea.setText(headerText);
            headerTextArea.textProperty().addListener((observable, oldValue, newValue) -> headerText = newValue);

            // Create a font selector dropdown
            ObservableList<String> fontList = FXCollections.observableArrayList(Font.getFamilies());
            ComboBox<String> fontSelector = new ComboBox<>(fontList);
            fontSelector.setValue("System");
            fontSelector.setOnAction(fontEvent -> {
                String selectedFont = fontSelector.getValue();
                headerTextArea.setFont(Font.font(selectedFont, FontWeight.NORMAL, FontPosture.REGULAR, headerTextArea.getFont().getSize()));
            });

            // Create a color picker
            ColorPicker colorPicker = new ColorPicker(Color.BLACK);
            colorPicker.setOnAction(colorEvent -> {
                Color selectedColor = colorPicker.getValue();
                headerTextArea.setStyle("-fx-text-fill: " + toRGBCode(selectedColor) + ";");
            });

            // Create a layout for the popup window that includes the headerTextArea, fontSelector, and colorPicker
            VBox layout = new VBox(10);
            layout.getChildren().addAll(new Label("Header Text"), headerTextArea,new Label("Font"), fontSelector, new Label("Color"), colorPicker);
            layout.setAlignment(Pos.CENTER);

            showPopupWindow("Header Text", layout);
        });


        // Navbar button
        Button navbarButton = new Button("Show Navbar");
        navbarButton.setOnAction(event -> {
            TextArea navbarTextArea = new TextArea();
            navbarTextArea.setPrefRowCount(4);
            navbarTextArea.setWrapText(true);
            navbarTextArea.setText(navbarText);
            navbarTextArea.textProperty().addListener((observable, oldValue, newValue) -> navbarText = newValue);
            showPopupWindow("Navbar Text", navbarTextArea);
        });

        // Image button
        Button imageButton = new Button("Select Image");
        imageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
            );
            selectedImage = fileChooser.showOpenDialog(primaryStage);
            if (selectedImage != null) {
                ImageView imageView = new ImageView(new Image(selectedImage.toURI().toString()));
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                showPopupWindow("Selected Image", imageView);
            }
        });

        // Color button
        ColorPicker colorPicker = new ColorPicker(selectedColor);
        colorPicker.setOnAction(event -> selectedColor = colorPicker.getValue());

        // Text button
        Button textButton = new Button("Add Text");
        textButton.setOnAction(event -> {
            TextField textField = new TextField();
            textField.textProperty().addListener((observable, oldValue, newValue) -> pageText = newValue);
            showPopupWindow("Enter Text", textField);
        });

        // Preview button
        Button previewButton = new Button("Preview");
        previewButton.setOnAction(event -> {
            // create a preview window with selected options
            VBox previewBox = new VBox();
            previewBox.setAlignment(Pos.CENTER);
            previewBox.setPadding(new Insets(20));
            previewBox.setSpacing(20);

            if (!headerText.isEmpty()) {
                Label headerLabel = new Label(headerText);
                headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                previewBox.getChildren().add(headerLabel);
            }

            if (!navbarText.isEmpty()) {
                Label navbarLabel = new Label(navbarText);
                navbarLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                previewBox.getChildren().add(navbarLabel);
            }
            if (selectedImage != null) {
                ImageView imageView = new ImageView(new Image(selectedImage.toURI().toString()));
                imageView.setFitWidth(400);
                imageView.setPreserveRatio(true);
                previewBox.getChildren().add(imageView);
            }

            if (!pageText.isEmpty()) {
                Label textLabel = new Label(pageText);
                textLabel.setStyle("-fx-font-size: 16px;");
                previewBox.getChildren().add(textLabel);
            }

            previewBox.setStyle("-fx-background-color: " + toHexString(selectedColor) + ";");
            Scene previewScene = new Scene(previewBox, 600, 400);
            Stage previewStage = new Stage();
            previewStage.setTitle("Preview");
            previewStage.setScene(previewScene);
            previewStage.show();
        });

        // Export button
        Button exportButton = new Button("Export");
        exportButton.setOnAction(event -> {
            // create HTML file with selected options
            StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html><head><title>My Web Page</title></head><body>");

            if (!headerText.isEmpty()) {
                htmlBuilder.append("<h1>").append(headerText).append("</h1>");
            }

            if (!navbarText.isEmpty()) {
                htmlBuilder.append("<nav>").append(navbarText).append("</nav>");
            }

            if (selectedImage != null) {
                htmlBuilder.append("<img src=\"").append(selectedImage.toURI().toString()).append("\"/>");
            }

            if (!pageText.isEmpty()) {
                htmlBuilder.append("<p>").append(pageText).append("</p>");
            }

            htmlBuilder.append("</body></html>");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save HTML File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("HTML Files", "*.html", "*.htm")
            );
            File outputFile = fileChooser.showSaveDialog(primaryStage);
            if (outputFile != null) {
                try {
                    Files.write(outputFile.toPath(), htmlBuilder.toString().getBytes("UTF-8"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Layout
        HBox topBox = new HBox(headerButton, navbarButton, imageButton, colorPicker, textButton, previewButton, exportButton);
        topBox.setPadding(new Insets(10));
        topBox.setSpacing(10);
        topBox.setAlignment(Pos.CENTER);

        root.setTop(topBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Web Page Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showPopupWindow(String title, javafx.scene.Node content) {
        VBox popupBox = new VBox(content);
        popupBox.setAlignment(Pos.CENTER);
        Scene popupScene = new Scene(popupBox, 400, 300);
        Stage popupStage = new Stage();
        popupStage.setTitle(title);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }
    // Helper method to convert a Color object to a hex code string
    private String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
