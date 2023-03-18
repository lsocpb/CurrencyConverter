package com.example.gui;

import java.io.*;
import java.math.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainGUI extends Application {
    private TextField amountField;
    private TextField currencyField;
    private Label resultLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Przelicznik walut");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label amountLabel = new Label("Kwota :");
        GridPane.setConstraints(amountLabel, 0, 0);
        amountField = new TextField();
        GridPane.setConstraints(amountField, 1, 0);

        Label currencyLabel = new Label("Waluta docelowa:");
        GridPane.setConstraints(currencyLabel, 0, 1);
        currencyField = new TextField();
        GridPane.setConstraints(currencyField, 1, 1);

        Button convertButton = new Button("Przelicz");
        GridPane.setConstraints(convertButton, 1, 2);
        convertButton.setOnAction(event -> {
            BigDecimal amount = new BigDecimal(amountField.getText());
            String currencyCode = currencyField.getText().toUpperCase();
            BigDecimal exchangeRate = getExchangeRate(currencyCode);
            BigDecimal result = amount.multiply(exchangeRate);
            resultLabel.setText(amount + currencyCode + " = " + result + " PLN");
        });

        resultLabel = new Label();
        GridPane.setConstraints(resultLabel, 1, 3);

        gridPane.getChildren().addAll(amountLabel, amountField, currencyLabel, currencyField, convertButton, resultLabel);

        Scene scene = new Scene(gridPane, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static BigDecimal getExchangeRate(String currencyCode) {
        try {
            URL url = new URL("https://api.nbp.pl/api/exchangerates/rates/a/" + currencyCode + "/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject json = new JSONObject(response.toString());
            BigDecimal exchangeRate = json.getJSONArray("rates").getJSONObject(0).getBigDecimal("mid");

            return exchangeRate;
        } catch (Exception e) {
            System.out.println("Nie udało się pobrać kursu wymiany dla waluty " + currencyCode);
            return BigDecimal.ZERO;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
