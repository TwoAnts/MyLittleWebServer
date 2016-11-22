package hzy;

public class Constant {
	public static final String[] REQUEST_METHOD = {
		"GET",
		"HEAD",
		"POST",
		"PUT",
		"DELETE",
		"OPTIONS",
		"TRACE",
		"CONNECT"
	};
	
	public static final String[] REQUEST_FIELD = {
		"Connection",
		"User-Agent",
		"Host",
		"If-Modified-Since",
		"Referer",
		"Accept-language",
		"Accept",
		"Cache-Control",
		"Cookie"
	};
	
	public static final String[] RESPONSE_FIELD = {
		"Connection",
		"Date",
		"Server",
		"Last-Modified",
		"Content-Length",
		"Content-Type",
		"Location",
		"Set-Cookie"
	};
	
	public static final String[] RESPONSE_STATUS = {
		"200 OK", 
		"301 Moved Permanently",
		"400 Bad Request",
		"404 Not Found",
		"505 Http Version Not Supported"
	};
	
	
	public static String getStatus(int statusCode){
		String code = statusCode + "";
		int i;
		for(i = 0;i < RESPONSE_STATUS.length; i++){
			if(RESPONSE_STATUS[i].startsWith(code)){
				return RESPONSE_STATUS[i].substring(4);
			}
		}
		return null;
	}
	
	public static boolean isRqMethodSupported(String method){
		for(String m : REQUEST_METHOD){
			if(m.equalsIgnoreCase(method)){
				return true;
			}
		}
		return false;
	}
}
