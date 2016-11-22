package hzy.func;

import hzy.Config;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;

public class FileFunc {
	public static String getMimeType(String fileUrl)throws java.io.IOException{  
		FileNameMap fileNameMap = URLConnection.getFileNameMap();  
		String type = fileNameMap.getContentTypeFor(fileUrl);  
		
		return type;  
	}  
	
	
}
