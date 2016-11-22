package hzy.func;

import java.io.File;

import hzy.Config;

public class UriFunc {

	
		public static String getSrcPath(String uri){
			String srcpath = uri;
			if(uri.startsWith("/")){
				srcpath =  uri.substring(1);
			}
			srcpath = Config.getSrcDirPath() + File.separator + srcpath;
			return srcpath;
		}
		
		public static String getHttpUri(String host, int port , String path){
			if(port == 80) return "http://" + host + "/" + path;
			return "http://" + host + ":" + port + "/" + path;
		}
}
