package com.company;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class UserAccount {
    private static Gson gson = new Gson();

    public static String authorization() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя:");
        String name = scanner.nextLine();
        System.out.println("Введите пароль:");
        String pass = scanner.nextLine();
        User user = new User(name, pass);
        try {
            URL url = new URL(Utils.getURL() + "/authorizationAccount");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream();) {
                os.write(user.toJson().getBytes());
            }
            try (InputStream is = connection.getInputStream()) {
                ByteArrayOutputStream baous = new ByteArrayOutputStream();
                byte[] bytes = new byte[10240];
                int len = 0;
                while ((len = is.read(bytes)) > 0) {
                    baous.write(bytes, 0, len);
                }
                String ans = new String(baous.toByteArray());
                if (ans.equals("ok")) {
                    System.out.println("Вы успешно зарегистрироались!!");
                    return user.getName();
                } else {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            System.out.println("Такой пользователь уже существует!");
            throw e;
        }
    }

    public static String signIn() throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя:");
        String name = scanner.nextLine();
        System.out.println("Введите пароль:");
        String pass = scanner.nextLine();
        User user = new User(name, pass);
        try {
            URL url = new URL(Utils.getURL() + "/signInAccount");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream();) {
                os.write(user.toJson().getBytes());
            }
            try (InputStream is = connection.getInputStream()) {
                ByteArrayOutputStream baous = new ByteArrayOutputStream();
                byte[] bytes = new byte[10240];
                int len = 0;
                while ((len = is.read(bytes)) > 0) {
                    baous.write(bytes, 0, len);
                }
                String ans = new String(baous.toByteArray());
                if (ans.equals("ok")) {
                    System.out.println("Вы успешно вошли в профиль!");
                    return user.getName();
                } else {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            System.out.println("Неверный логин или пароль!");
            throw e;
        }
    }
    public static void createChat(ChatRoom chatRoom) throws IOException {
        try {
            URL url = new URL(Utils.getURL() + "/addChat");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(gson.toJson(chatRoom).getBytes());
            }
            try (InputStream is = connection.getInputStream()) {
                ByteArrayOutputStream baous = new ByteArrayOutputStream();
                byte[] bytes = new byte[10240];
                int len = 0;
                while ((len = is.read(bytes)) > 0) {
                    baous.write(bytes, 0, len);
                }
                String ans = new String(baous.toByteArray());
                if (ans.equals("ok")) {
                    System.out.println("Вы создали комнату!");
                } else {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            System.out.println("Такая комната уже есть!");
            throw e;
        }
    }
    public static String selectChatRoom(List<ChatRoom> list, String chatRoom) throws IOException {
        for(ChatRoom chat: list){
            if(chat.getName().equals(chatRoom)){
                return chatRoom;
            }
        }
        throw new IOException();
    }
}
