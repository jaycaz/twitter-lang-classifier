package org.main;/**
 * Created by jacaz_000 on 12/9/2015.
 */

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.classifier.NGramClassifier;


public class ClassifierGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final NGramClassifier classifier = new NGramClassifier();
        classifier.loadFile("NGram");

        primaryStage.setTitle("NGram Classifier Demo");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Create GUI components
        Label instructions = new Label("Start typing and we will guess the language!");
        instructions.setWrapText(true);
        instructions.setFont(new Font(16));
        final Label lang = new Label("LANGUAGE");
        final TextField textField = new TextField();
        textField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                System.out.println("Change");
                String ans = classifier.classify(textField.getText());
                System.out.println("Language answer: " + ans);
                lang.setText(ans);
            }
        });

        grid.add(instructions, 0, 1);
        grid.add(textField, 0, 2);
        grid.add(lang, 0, 3);

        primaryStage.setScene(new Scene(grid, 300, 250));
        primaryStage.show();
    }
}
