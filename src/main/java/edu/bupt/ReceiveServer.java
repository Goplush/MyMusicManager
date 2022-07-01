package edu.bupt;
//类receiveServer
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ReceiveServer
{
    final int RECEIVE_PORT = 9090;// 该服务器的端口号
    //以下是线程池初始化
    ExecutorService thread_pool = new ThreadPoolExecutor(10, 10,
            1, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(128),
            new ThreadPoolExecutor.DiscardPolicy());// 指定拒绝策略

    // receiveServer的构造器
    public ReceiveServer()
    {
        ServerSocket rServer = null;// ServerSocket的实例
        Socket request = null; // 用户请求的套接字
        Thread receiveThread = null;
        try
        {
            rServer = new ServerSocket(RECEIVE_PORT);
            // 初始化ServerSocket
            System.out.println("Welcome to the server!");
            System.out.println(new Date());
            System.out.println("The server is ready!");
            System.out.println("Port: " + RECEIVE_PORT);
            while (true)
            {
                // 等待用户请求
                request = rServer.accept();
                // 接收客户机连接请求， 生成serverThread的实例并交由线程池启动serverThread线程
                thread_pool.submit(new serverThread(request));
            }
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String args[])
    {
        new ReceiveServer();
    } // end of main
} // end of class