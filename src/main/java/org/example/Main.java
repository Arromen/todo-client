package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    private static final String BASE_URL = "http://localhost:8080/tasks";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
            System.out.println("\n=== To-Do List ===");
            System.out.println("1. Показать задачи");
            System.out.println("2. Добавить задачу");
            System.out.println("3. Обновить задачу");
            System.out.println("4. Удалить задачу");
            System.out.println("5. Выход");
            System.out.print("Выбор: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> listTasks();
                case 2 -> addTask();
                case 3 -> updateTask();
                case 4 -> deleteTask();
                case 5 -> { System.out.println("Пока!"); return; }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private static void listTasks() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET().build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("\nЗадачи:\n" + res.body());
    }

    private static void addTask() throws IOException, InterruptedException {
        System.out.print("Название: ");
        String title = scanner.nextLine();
        ObjectNode json = mapper.createObjectNode();
        json.put("title", title);
        json.put("completed", false);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("✅ Задача добавлена.");
    }

    private static void updateTask() throws IOException, InterruptedException {
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        System.out.print("Новое название: ");
        String title = scanner.nextLine();
        System.out.print("Выполнена? (true/false): ");
        boolean done = Boolean.parseBoolean(scanner.nextLine());

        ObjectNode json = mapper.createObjectNode();
        json.put("title", title);
        json.put("completed", done);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Статус: " + res.statusCode());
    }

    private static void deleteTask() throws IOException, InterruptedException {
        System.out.print("ID для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE().build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("✅ Удалено.");
    }
}