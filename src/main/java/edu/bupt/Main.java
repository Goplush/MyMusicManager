package edu.bupt;

import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        serverThread thread = new serverThread(new Socket());
        thread.main();
    }
}
