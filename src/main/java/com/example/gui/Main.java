package com.example.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Podaj kwotę: ");
        BigDecimal amount = scanner.nextBigDecimal();

        System.out.print("Podaj walutę: ");
        String currencyCode = scanner.next().toUpperCase();

        BigDecimal exchangeRate = getExchangeRate(currencyCode);

        BigDecimal result = amount.multiply(exchangeRate);

        System.out.println(amount + currencyCode + " = " + result + " PLN");

        scanner.close();
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
}
