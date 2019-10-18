package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

    public static String signIn() throws IOException {
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

    public static String selectChatRoom(List<ChatRoom> list) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите название комнаты:");
        String chatRoom = scanner.nextLine();
        System.out.println("Введите парль комнаты:");
        String pass = scanner.nextLine();
        ChatRoom chatRoomTwo = new ChatRoom(chatRoom, pass);
        for (ChatRoom chat : list) {
            if (chat.equals(chatRoomTwo)) {
                return chatRoom;
            }
        }
        System.out.println("Неверно выбрана комната или введён неправильный пароль!");
        throw new IOException();
    }

    public static void showUsers(List<User> users, String login) {
        for (User user : users) {
            if (!user.getName().equals(login)) {
                System.out.println(user.getName() + " status:" + user.getStatus());
            }
        }
    }

    public static void showChatRooms(List<ChatRoom> chatRooms) {
        for (ChatRoom room : chatRooms) {
            System.out.println(room.getName());
        }
    }

    public static boolean checkNameUser(List<User> users, String name, String myLogin) {
        for (User user : users) {
            if ((!user.getName().equals(myLogin) && (user.getName().equals(name)))) {
                return true;
            }
        }
        return false;
    }

    public static List<ChatRoom> getListChatRoom() {
        Gson gson = new Gson();
        try {
            URL url = new URL(Utils.getURL() + "/getChat");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try (InputStream is = connection.getInputStream()) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] bytes = new byte[10240];
                int len;
                while ((len = is.read(bytes)) > 0) {
                    bos.write(bytes, 0, len);
                }
                return gson.fromJson(bos.toString(), new TypeToken<List<ChatRoom>>() {
                }.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<User> getListUsers(String login) {
        Gson gson = new Gson();
        try {
            URL url = new URL(Utils.getURL() + "/getUsers?user=" + login);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try (InputStream is = connection.getInputStream()) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] bytes = new byte[10240];
                int len;
                while ((len = is.read(bytes)) > 0) {
                    bos.write(bytes, 0, len);
                }
                return gson.fromJson(bos.toString(), new TypeToken<List<User>>() {
                }.getType());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String selectRegistOrSignIn() throws IOException {
        String login;
        while (true) {
            Scanner scn = new Scanner(System.in);
            login = scn.nextLine();
            if (login.equals("1")) {
                login = UserAccount.authorization();
                break;
            }
            if (login.equals("2")) {
                login = UserAccount.signIn();
                break;
            }
        }
        return login;
    }
}
