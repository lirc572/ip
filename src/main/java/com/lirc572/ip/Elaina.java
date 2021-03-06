package com.lirc572.ip;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The entry point of the program.
 */
public class Elaina extends Application {

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private AnchorPane mainLayout;
    private Button sendButton;
    private String userImageSrc;
    private String elainaImageSrc;
    private String elainaImage2Src;
    private String elainaIconSrc;

    /**
     * The task list.
     */
    private final TaskList tasks;

    /**
     * Constructs an Elaina object.
     */
    public Elaina() {
        this.tasks = new TaskList();
        Storage.readFromFile(this.tasks);
    }

    private void initializeImageSources() {
        this.userImageSrc = "/images/dialogPic/User/majo_saya2.jpg";
        this.elainaImageSrc = "/images/dialogPic/Elaina/majo_elaina1.jpg";
        this.elainaImage2Src = "/images/dialogPic/Elaina/majo_elaina0.jpg";
        this.elainaIconSrc = "/images/dialogPic/Elaina/majo_elaina2.jpg";
    }

    private void initializeComponents() {
        this.scrollPane = new ScrollPane();
        this.dialogContainer = new VBox();
        this.scrollPane.setContent(dialogContainer);

        this.userInput = new TextField();
        this.sendButton = new Button("Send");

        this.mainLayout = new AnchorPane();
        this.mainLayout.getChildren().addAll(this.scrollPane, this.userInput, this.sendButton);
    }

    private void initializeComponentProperties() {
        this.mainLayout.setPrefSize(400.0, 600.0);

        this.scrollPane.setPrefSize(385.0, 535.0);
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.scrollPane.setVvalue(1.0);
        this.scrollPane.setFitToWidth(true);
        // this.scrollPane.setStyle("-fx-background: #90EE90;");
        this.scrollPane.lookup(".viewport").setStyle(
            "-fx-background-image: url('/images/background/bg2.jpg');"
            + "-fx-background-size: 800 600;"
            + "-fx-background-position: center;"
        );

        this.dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        this.dialogContainer.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));
        this.dialogContainer.setSpacing(10.0);

        this.userInput.setPrefWidth(325.0);

        this.sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(this.scrollPane, 1.0);
        AnchorPane.setBottomAnchor(this.sendButton, 1.0);
        AnchorPane.setRightAnchor(this.sendButton, 1.0);
        AnchorPane.setLeftAnchor(this.userInput, 1.0);
        AnchorPane.setBottomAnchor(this.userInput, 1.0);
    }

    private void initializeEventListeners() {
        this.sendButton.setOnMouseClicked((event) -> {
            this.sendCommand();
        });

        this.userInput.setOnKeyReleased((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                this.sendCommand();
            }
        });

        this.dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Step 1. Setting up required components
        this.initializeImageSources();
        this.initializeComponents();
        Scene scene = new Scene(this.mainLayout); // Setting the scene to be our Label
        primaryStage.setScene(scene); // Setting the stage to show our screen
        primaryStage.show(); // Render the stage.

        // Step 2. Formatting the window to look as expected
        primaryStage.setTitle("Elaina");
        primaryStage.setResizable(false);
        primaryStage.setMinHeight(600.0);
        primaryStage.setMinWidth(400.0);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream(this.elainaIconSrc)));

        this.initializeComponentProperties();

        // Step 3. Add functionality to handle user input.
        this.initializeEventListeners();

        // Step 4. Print welcome message.
        for (String line : this.getWelcomeText(false).split("\n")) {
            this.dialogContainer.getChildren().add(DialogBox.getElainaDialogBox(line, this.elainaImageSrc));
        }
    }

    private void sendCommand() {
        String userText = userInput.getText();
        if (userText.trim().equals("")) {
            return;
        }
        String elainaText = this.getResponse(userInput.getText());
        if (elainaText.equals("clear\n")) {
            this.dialogContainer.getChildren().clear();
        } else {
            this.dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialogBox(
                            userText,
                            this.userImageSrc
                    ),
                    DialogBox.getElainaDialogBox(
                            elainaText,
                            elainaText.startsWith("Error: ")
                                    ? this.elainaImage2Src
                                    : this.elainaImageSrc
                    )
            );
        }
        this.userInput.clear();
        if (elainaText.equals("Bye. Hope to see you again soon!\n")) {
            Platform.exit();
            System.exit(0);
        }
    }

    private String getResponse(String input) {
        String response = null;
        try {
            response = Parser.processCommand(input, this.tasks);
        } catch (Exception e) {
            String errorText = "";
            errorText += Ui.printError(e);
            response = errorText;
        }
        return response;
    }

    private String getWelcomeText(boolean withLogo) {
        String welcomeText = "";
        if (withLogo) {
            welcomeText += Ui.printLogo();
            welcomeText += Ui.printEmptyLine();
        }
        welcomeText += Ui
                .printLine("Who is the ultimate Personal Assistant Chatbot that helps keep track of various things?");
        welcomeText += Ui.printLine("\u305D\u3046\u3001\u79C1\u3067\u3059\uFF01"); // "Sou, watashi desu!"
        welcomeText += Ui.printLine("My official website: https://elaina.lirc572.com");
        return welcomeText;
    }

    /**
     * Constructs an Elaina object and run it.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Start in GUI mode
        launch();
    }
}
