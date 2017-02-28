package hzy;

import hzy.Request.NoContentException;
import hzy.log.Log;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;


public class SocketServiceThread extends Thread{
    private Socket socket;
    private ServerSocket serverSocket;
    private PrintWriter pw;
    
    private boolean isKeepAlive = false;
    
    public SocketServiceThread(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    
    /** 
     * 判断是否断开连接，断开返回true,没有返回false 
     * @param socket 
     * @return 
     */  
     public Boolean isSocketClose(Socket socket){  
        try{  
            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信  
            return false;  
        }catch(Exception se){  
            return true;  
        }  
     }  
     
     public void close(){
        try {
             if(socket != null){
                Log.log(socket, "[" + Thread.currentThread().getName() + "] " + "socket closed!\n");
                socket.close();
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
    

    @Override
    public void run() {
        
        while (!serverSocket.isClosed()) {
            //等待连接
            try {
                socket = serverSocket.accept();
            } catch (IOException e1) {
                
            }
            
            if(serverSocket.isClosed()){
                break;
            }
            
            Log.log(socket, "[" + Thread.currentThread().getName() + "] " + 
                    "New connection accepted ");
            
            Request request = new Request(socket);
            Response response = new Response(socket);
            
            boolean isNoContent = false;
            //进行处理
            try {
                request.parse();
                response.setRequest(request);
                response.sendResponse();

            } catch (SocketException e){
                if(e.getLocalizedMessage() == "Connection reset"){
                    isNoContent = true;
                    Log.log(socket, "connection reset.");
                }else{
                    e.printStackTrace();
                }
            }catch (NoContentException e) {
                // TODO Auto-generated catch block
                isNoContent = true;
                //Browser like to establish many connections at one time.
                //When not needed, empty connections will be closed.
            }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if(!serverSocket.isClosed() && !isNoContent){
                    Log.log(socket, request.toString());
                    Log.log(socket, response.toString());
                }
                close();
            }
        }
        
        
    }

}
