package hzy;

import hzy.func.DateFunc;
import hzy.func.FileFunc;
import hzy.func.UriFunc;
import hzy.log.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.activation.MimeType;


public class Response {
    
    private Socket socket;
    
    private Request request;
    
    private int statusCode;
    
    private String statusLine = null;
    private HashMap<String, String> headerFields = new HashMap<String, String>();
    
    
    public Response(Socket socket){
        this.socket = socket;
    }
    
    public void setRequest(Request request){
        this.request = request;
    }
    
    public Request getRequest(){
        return this.request;
    }
    
    public void sendResponse() throws IOException{
        
        
        if(!Constant.isRqMethodSupported(request.getMethod())){
            statusCode = 400;//bad request
            reErr(statusCode);
        }else if(!Config.ProtocolVersion.equals(request.getProtocolVersion())){
            statusCode = 505;//http version not support
            reErr(statusCode);
        }else if("GET".equals(request.getMethod())){
            reGet();
        }
        
    }
    
    private void reErr(int statusCode) throws IOException{
        OutputStream socketOut=socket.getOutputStream();
        PrintWriter pw = new PrintWriter(socketOut);
        
        String status = Constant.getStatus(statusCode);
        setStatusLine(Config.ProtocolVersion, statusCode, status);
        addHeader("Server", Config.Server);
        addHeader("Date", DateFunc.getDateStr(new Date()));
        sendStatusAndHeaders(pw);
        
    }
    
    private void reGet() throws IOException{
        
        Log.log(socket, "get " + request.getURI() + " : " + "response start");
        if(socket.isClosed()) {
            return;
        }
        
        OutputStream socketOut=socket.getOutputStream();
        PrintWriter pw = new PrintWriter(socketOut);
        
        
        String uri = request.getURI();
        if("/".equals(uri)){
            statusCode = 301;//重定向
            String status = Constant.getStatus(statusCode);
            setStatusLine(Config.ProtocolVersion, statusCode, status);
            addHeader("Server", Config.Server);
            addHeader("Date", DateFunc.getDateStr(new Date()));
            addHeader("Location", UriFunc.getHttpUri(Config.getHostIP(), Config.getPort(), Config.getMainPage()));
            sendStatusAndHeaders(pw);
            pw.printf("\r\n");
            pw.flush();
            return ;
        }
        
        
        File file = new File(UriFunc.getSrcPath(uri));
        if(!file.exists() || file.isDirectory()){
            statusCode = 404;//文件未找到
            String status = Constant.getStatus(statusCode);
            setStatusLine(Config.ProtocolVersion, statusCode, status);
            addHeader("Server", Config.Server);
            addHeader("Date", DateFunc.getDateStr(new Date()));
            sendStatusAndHeaders(pw);
            pw.printf("\r\n");
            pw.flush();
            return ;
        }
        
        
        statusCode = 200;
        String status = Constant.getStatus(statusCode);
        setStatusLine(Config.ProtocolVersion, statusCode, status);
        
        
        addHeader("Server", Config.Server);
        addHeader("Connection", "close");
        addHeader("Date", DateFunc.getDateStr(new Date()));
        addHeader("Content-Length", String.valueOf(file.length()));
        String mimeType = FileFunc.getMimeType(file.getName());
        String contentType = mimeType;
        if(null != mimeType && mimeType.startsWith("text")){
            contentType = mimeType + "; charset=UTF-8";
        }
        addHeader("Content-Type", contentType);
        addHeader("Last-Modified", DateFunc.getDateStr(new Date(file.lastModified())));
        sendStatusAndHeaders(pw);
        
        pw.printf("\r\n");
        pw.flush();
        
        Log.log(socket, "send data start");
        //传输数据,将文件整个输出
        sendBody(file, socketOut);
        Log.log(socket, "send data finished");
        
        
    }
    
    private void rePost(){
        
    }
    
    
    private void sendStatusAndHeaders(PrintWriter pw){
        sendStatusLine(pw);
        sendHeaders(pw);
        pw.flush();
    }
    
    
    private void setStatusLine(String version, int code, String status){
        statusLine = version + " " + code + " " + status;
    }
    
    private void sendStatusLine(PrintWriter pw){
        pw.printf("%s\r\n", statusLine);
    }
    
    private void clearHeaders(){
        headerFields.clear();
    }
    
    private void addHeader(String name, String value){
        headerFields.put(name, value);
    }
    
    private void sendHeaders(PrintWriter pw){
        Set<Entry<String, String>> set = headerFields.entrySet();
        for(Entry<String, String> e : set){
            pw.printf("%s: %s\r\n", e.getKey(), e.getValue());
        }
    }
    
    private void sendBody(File file, OutputStream socketOut) throws IOException{
        FileInputStream fin = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while((len = fin.read(buffer)) > 0){
            socketOut.write(buffer, 0, len);
        }
        fin.close();
        socketOut.flush();
    }
    
    
    private PrintWriter getWriter() throws IOException{
        OutputStream socketOut=socket.getOutputStream();
        return new PrintWriter(socketOut,true);
    }
    
    /** 
     * 判断是否断开连接，断开返回true,没有返回false 
     * @param socket 
     * @return 
     */  
     private Boolean isSocketClose(Socket socket){  
        try{  
            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信  
            return false;  
        }catch(Exception se){  
            return true;  
        }  
     }

    @Override
    public String toString() {
        return "Response [statusLine=" + statusLine + ", headerFields="
                + headerFields + "]";
    }
     
     
    

}
