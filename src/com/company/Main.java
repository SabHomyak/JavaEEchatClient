package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Чтобы зарегистрироваться нажмите 1, чтобы войти в профиль 2");
            String login;
            String interlocutor = "";
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
            List<User> users = getListUsers(login);
            List<ChatRoom> chatRooms = getListChatRoom();
            if (users.size() != 1) {
                System.out.println("Для выбора собеседника нажмите 1:");
                for (User user : users) {
                    if (!user.getName().equals(login)) {
                        System.out.println(user.getName() + " status:" + user.getStatus());
                    }
                }
                System.out.println("Для выбора комнаты нажмите 2:");
                for (ChatRoom room : chatRooms) {
                    System.out.println(room.getName());
                }
                System.out.println("Для создание чат-комнаты нажмите 3");
                Scanner scan = new Scanner(System.in);
                interlocutor = scan.nextLine();
                if (interlocutor.equals("3")) {
                    System.out.println("Введите название комнаты:");
                    String name = scan.nextLine();
                    System.out.println("Введите пароль комнаты:");
                    String pass = scan.nextLine();
                    ChatRoom chatRoom = new ChatRoom(name, pass);
                    interlocutor = chatRoom.getName();
                    System.out.println("Добавьте пользователей в комнату:");
                    while (true) {
                        scan = new Scanner(System.in);
                        String user = scan.nextLine();
                        if (user.equals("stop")) {
                            break;
                        }
                        if (!checkNameUser(users, user, login)) {
                            System.out.println("Неверно выбран собеседник!");
                            throw new IOException();
                        }
                        chatRoom.addUserToChat(user);
                        System.out.println("Чтобы завершить добавление напишите команду 'stop'");
                    }
                    System.out.println(Arrays.toString(chatRoom.getUsers().toArray()));
                    UserAccount.createChat(chatRoom);
                } else if (!checkNameUser(users, interlocutor, login)) {
                    System.out.println("Неверно выбран собеседник!");
                    throw new IOException();
                }
                System.out.println("Ваш собеседник " + interlocutor);
            } else {
                System.out.println("Вы пока один в нашей программе:(");
            }
            Thread th = new Thread(new GetThread(login));
            th.setDaemon(true);
            th.start();
            System.out.println("Enter your message: ");
            while (true) {
                String text = scanner.nextLine();
                if (text.isEmpty()) break;

                Message m = new Message(login, text);
                m.setTo(interlocutor);
                int res = m.send(Utils.getURL() + "/add");

                if (res != 200) { // 200 OK
                    System.out.println("HTTP error occured: " + res);
                    return;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
    public static List<ChatRoom> getListChatRoom(){
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

    public static boolean checkNameUser(List<User> users, String name, String myLogin) {
        for (User user : users) {
            if ((!user.getName().equals(myLogin) && (user.getName().equals(name)))) {
                return true;
            }
        }
        return false;
    }
}
