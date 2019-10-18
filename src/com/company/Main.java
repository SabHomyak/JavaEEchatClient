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
        try {
            System.out.println("Чтобы зарегистрироваться нажмите 1, чтобы войти в профиль 2");
            String login = UserAccount.selectRegistOrSignIn();
            String interlocutor = "";
            List<User> users = UserAccount.getListUsers(login);
            List<ChatRoom> chatRooms = UserAccount.getListChatRoom();
            if (users.size() != 1) {
                printChoice();
                Scanner scan = new Scanner(System.in);
                String select = scan.nextLine();
                interlocutor = select(select, chatRooms, users, login);
                System.out.println("Ваш собеседник " + interlocutor);
            } else {
                System.out.println("Вы пока один в нашей программе:(");
            }
            GetThread getThread = new GetThread(login);
            if (interlocutor.contains("[chat-room]")) {
                getThread.setChatRoom(interlocutor);
            }
            Thread th = new Thread(getThread);
            th.setDaemon(true);
            th.start();
            System.out.println("Enter your message: ");
            start(login, interlocutor);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String select(String interlocutor, List<ChatRoom> chatRooms, List<User> users, String login) throws IOException {
        Scanner scan = new Scanner(System.in);
        if (interlocutor.equals("2")) {
            System.out.println("Выберите чат-комнату:");
            UserAccount.showChatRooms(chatRooms);
            interlocutor = UserAccount.selectChatRoom(chatRooms) + "[chat-room]";
        } else if (interlocutor.equals("3")) {
            System.out.println("Введите название комнаты:");
            String name = scan.nextLine();
            System.out.println("Введите пароль комнаты:");
            String pass = scan.nextLine();
            ChatRoom chatRoom = new ChatRoom(name, pass);
            UserAccount.createChat(chatRoom);
            interlocutor = chatRoom.getName() + "[chat-room]";
        } else if (interlocutor.equals("1")) {
            Scanner scanSelectUser = new Scanner(System.in);
            System.out.println("Введите имя вашего собеседника:");
            UserAccount.showUsers(users, login);
            String user = scanSelectUser.nextLine();
            if (!UserAccount.checkNameUser(users, user, login)) {
                System.out.println("Неверно выбран собеседник!");
                throw new IOException();
            }
            interlocutor = user;
        } else {
            System.out.println("Неверно выбрано значение!");
            throw new IOException();
        }
        return interlocutor;
    }

    public static void printChoice() {
        System.out.println("Для выбора собеседника нажмите 1:");
        System.out.println("Для выбора чат-комнаты нажмите 2:");
        System.out.println("Для создание чат-комнаты нажмите 3");
    }

    public static void start(String login, String interlocutor) throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
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
        }
    }
}
