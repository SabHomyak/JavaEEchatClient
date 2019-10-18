package com.company;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ChatRoom {
    private String name;
    private String pass;
    private List<String> users = new LinkedList<>();

    public ChatRoom() {
    }

    public ChatRoom(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public void addUserToChat(String user) {
        if (users.contains(user)) {
            System.out.println("Пользователь уже добавлен в комнату!");
        } else {
            users.add(user);
            System.out.println(user + " добавлен в комнату!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(name, chatRoom.name) &&
                Objects.equals(pass, chatRoom.pass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pass);
    }
}
