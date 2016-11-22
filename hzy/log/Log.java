package hzy.log;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	private static LogListener mlistener = null;
	
	private static String getCurrentTime(){
		DateFormat dateFormat = DateFormat.getTimeInstance();
		return dateFormat.format(new Date());
		
	}
	
	public static void log(Socket socket, String msg){
		String m = msg;
		if(null == socket){
			m = "[" + getCurrentTime() +"]"+ msg;
			System.out.println(m);
			if(null != mlistener){
				mlistener.log(m);
			}
			return;
		}
		m = "[" + getCurrentTime() + "]" +
				"[" + socket.getInetAddress() + ":" +socket.getPort() + "]" + " " + msg;
		System.out.println(m);
		if(null != mlistener){
			mlistener.log(m);
		}
	}
	
	public static void err(Socket socket, String msg){
		String m = msg;
		if(null == socket){
			m = "[" + getCurrentTime() + "]" + msg;
			System.err.println(m);
			if(null != mlistener){
				mlistener.err(m);
			}
			return;
		}
		m = "[" + getCurrentTime() + "]"
				+ "[" + socket.getInetAddress() + ":" +socket.getPort() + "]" + " " + msg;
		System.err.println(m);
		if(null != mlistener){
			mlistener.err(m);
		}
	}
	
	public static void setLogListener(LogListener listener){
		Log.mlistener = listener;
	}
	
	
	public static interface LogListener{
		public void log(String msg);
		public void err(String msg);
	}

}
