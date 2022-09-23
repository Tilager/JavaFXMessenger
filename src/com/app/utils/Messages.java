package com.app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Messages {
    public static boolean stopped = false;

    public static class SendMsgThrd extends Thread {
        Scanner sc = new Scanner(System.in);
        OutputStream out;

        public SendMsgThrd(OutputStream o) {
            out = o;
        }

        @Override
        public void run() {
            while (!Messages.stopped) {
                System.out.print("Введите сообщение: ");
                String msg = sc.nextLine();
                Messages.sendMsg(out, msg);
                if (msg.equals("exit")) {
                    System.out.println("Выход из потока Send");
                    Messages.stopped = true;
                    return;
                }
            }
        }
    }

    public static class ReadMsgThrd extends Thread {
        InputStream in;
        OutputStream out;

        public ReadMsgThrd(InputStream i, OutputStream o) {
            in = i;
            out = o;
        }

        @Override
        public void run() {
            while (!Messages.stopped) {
                String line = Messages.readMsg(in);
                if (line.equals("exit")) {
                    System.out.println("Выход из потока Read");
                    try {
                        out.write("exit".getBytes());
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Messages.stopped = true;
                    return;
                }
                System.out.printf("\nСобеседник: %s \n", line);
            }
        }
    }

    public static void sendMsg(OutputStream out, String msg) {
        try {
            out.write(msg.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String readMsg(InputStream in) {
        try {
            byte[] data = new byte[32 * 1024];
            int readBytes = in.read(data);
            return new String(data, 0, readBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
