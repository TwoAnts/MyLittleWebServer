package hzy;

import hzy.log.Log;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

public class MultiThreadServer{
	public final static int SEVER_ID = 0;
    private int port=Config.getPort();
    private String hostIP = Config.getHostIP();
    private ServerSocket serverSocket;
//    private ExecutorService executorService;
    private final int PROC_SIZE=2;
    
    private int maxThreadNum = Runtime.getRuntime().availableProcessors()*PROC_SIZE;
    private Thread[] mThreads = new Thread[maxThreadNum];
    private boolean running = false;
    
    public MultiThreadServer(){
    	
    }
    
    public void load(){
    	port = Config.getPort();
    	hostIP = Config.getHostIP();
    }
    
    public boolean isRunning(){
    	return running;
    }
    
    public void startService() throws IOException{
    	if(running) {
    		Log.log(null, "服务正在运行");
    		return;
    	}
    	running =  true;
    	load();//每次启动服务时，重新加载配置信息
    	serverSocket=new ServerSocket(port, maxThreadNum, InetAddress.getByName(hostIP));
    	serverSocket.setPerformancePreferences(2, 1, 0);
    	Log.log(null, "服务器启动 " + " ip:"+ hostIP + " port:" + port);
    	for(Thread thread : mThreads){
    		thread = new SocketServiceThread(serverSocket);
    		thread.start();//每个线程均阻塞在serverSocket.accept(),等待socket连接
    	}
    	
    }
   
    public void stopService(){
    	running = false;
    	//关闭serverSocket，
    	//这时阻塞在accept的线程会弹出Exception
    	//从而跳出循环，结束线程
    	try {
    		if(null != serverSocket){
    			serverSocket.close();
    			serverSocket = null;//去除serverSocket的引用
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	//清空所有thread
    	for(Thread thread : mThreads){
    		if(null != thread){
    			thread.interrupt();
    			thread = null;
    		}
    	}
    	
    	Log.log(null, "服务器停止");
    	
    }

}



