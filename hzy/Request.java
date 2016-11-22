package hzy;

import hzy.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;


public class Request {
	
	private Socket socket;
	
	private String method;
	
	private String URI;
	
	private String protocolVersion;
	
	private HashMap<String, String> headerFields = new HashMap<String, String>();
	
	private String entityBody;
	
	private String rq;
	
	public Request(Socket socket){
		this.socket = socket;
	}
	
	
	
	public String getMethod() {
		return method;
	}



	public String getURI() {
		return URI;
	}



	public String getProtocolVersion() {
		return protocolVersion;
	}



	public String getEntityBody() {
		return entityBody;
	}



	public String getHeaderField(String headerName){
		if(headerFields.containsKey(headerName)){
			return headerFields.get(headerName);
		}
		return null;
	}
	
	
	
	public void parse() throws IOException,  NoContentException{
		
		StringBuffer sb = new StringBuffer();
		BufferedReader br=getReader(socket);
		String msg=null;
		while((msg = br.readLine()) != null){
//			System.out.println(msg);
			sb.append(msg);
			//碰到两个换行符时，跳出循环
			if(msg.isEmpty()){
				break;
			}
			sb.append('\n');
        }
		rq = sb.toString();
//		System.out.println("rq:" + rq);
		String[] strs = rq.split("\n");
//		System.out.println("strs len:" + strs.length);
		
		String[] first = strs[0].split(" ", 3);
		
		if(strs[0].trim().length() == 0){
			//Some connection closed by remote browser. 
			//These connections have no content.
			throw new NoContentException();
		}
		
		if(first.length != 3){
//			System.err.println("request header invalid! header is " + strs[0]);
			Log.err(socket, "request header invalid! header is " + strs[0]);
        	return;
		}
		
		method = first[0];
		URI = first[1];
		protocolVersion = first[2];
		
		int i;
		for(i = 1; i < strs.length;i++){
			String[] kv = strs[i].split(":", 2);
        	if(kv.length != 2) continue;
        	headerFields.put(kv[0].trim(), kv[1].trim());
		}
		
		
		if(isSocketClose(socket)){
//			System.out.println("in request parse, socket closed!");
			Log.log(socket, "in request parse(), socket closed!");
		}
		
//		StringBuilder stringBuilder = new StringBuilder();
//		for(i++;i < strs.length;i++){
//			stringBuilder.append(msg);
//			stringBuilder.append('\n');
//		}
//		entityBody = stringBuilder.toString().trim();
		
	
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
	
	private BufferedReader getReader(Socket socket) throws IOException{
        InputStream socketIn=socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn));
    }

	@Override
	public String toString() {
		return "Request [method=" + method + ", URI=" + URI
				+ ", protocolVersion=" + protocolVersion + ", headerFields="
				+ headerFields + ", entityBody=" + entityBody + "]";
	}
	
	public static class NoContentException extends Exception{
		public NoContentException(){
			
		}
	}

}
