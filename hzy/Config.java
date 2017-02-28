package hzy;

import hzy.log.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Config {
    
    public static final String configFile = "server.config";
    
    public static final String Server = "hzy_mtServer/1.0";
    
    public static final String ProtocolVersion = "HTTP/1.1";
    
    
    private static String srcDirPath = getUserDir();
    private static String hostIP = "127.0.0.1";
    private static String mainPage = "hello.html";
    private static int port = 80;
    
    private static boolean firstLoaded = false;
    private static long lastModified = 0;
    
    private static boolean isNeedLoad(){
        File file = new File(getUserDir() + File.separator + configFile);
        if(!firstLoaded || file.lastModified() != lastModified){
            return true;
        }
        return false;
    }
    
    public static String getUserDir(){
        return System.getProperty("user.dir");
    }
    
    
    private static final String CF_SRC_DIR_PATH = "src_dir_path=";
    private static final String CF_MAIN_PAGE = "main_page=";
    private static final String CF_HOST_IP = "host_ip=";
    private static final String CF_PORT = "port=";
    
    
    
    public static void load(){
//      Log.log(null, "load config");
        firstLoaded = true;
        File file = new File(getUserDir() + File.separator + configFile);
        lastModified = file.lastModified();
        try {
            Scanner scanner = new Scanner(file, "UTF-8");
            String msg = null;
            while(scanner.hasNext()){
                msg = scanner.nextLine();
                if(msg.startsWith(CF_SRC_DIR_PATH)){
                    srcDirPath = msg.substring(CF_SRC_DIR_PATH.length()).trim();
                    if(srcDirPath.endsWith(File.separator)){
                        srcDirPath = srcDirPath.substring(0, srcDirPath.length() - 1);
                    }
                }else if(msg.startsWith(CF_HOST_IP)){
                    hostIP = msg.substring(CF_HOST_IP.length()).trim();
                }else if(msg.startsWith(CF_MAIN_PAGE)){
                    mainPage = msg.substring(CF_MAIN_PAGE.length()).trim();
                }else if(msg.startsWith(CF_PORT)){
                    port = new Integer(msg.substring(CF_PORT.length()).trim());
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Config.save();
        }
        
    }
    
    public static void save(){
//      Log.log(null, "save config");
        File file = new File(getUserDir() + File.separator + configFile);
        try {
            PrintWriter pw = new PrintWriter(file, "UTF-8");
            pw.println(CF_SRC_DIR_PATH + srcDirPath); 
            pw.println(CF_MAIN_PAGE + mainPage);
            pw.println(CF_HOST_IP + hostIP);
            pw.println(CF_PORT + port);
            pw.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        lastModified = file.lastModified();
    }

    public static String getSrcDirPath() {
        if(isNeedLoad()){
            Config.load();
        }
        return srcDirPath;
    }

    public static void setSrcDirPath(String srcDirPath) {
        Config.srcDirPath = srcDirPath;
        Config.save();
    }

    public static String getHostIP() {
        if(isNeedLoad()){
            Config.load();
        }
        return hostIP;
    }

    public static void setHostIP(String hostIP) {
        Config.hostIP = hostIP;
        Config.save();
    }

    public static String getMainPage() {
        if(isNeedLoad()){
            Config.load();
        }
        return mainPage;
    }

    public static void setMainPage(String mainPage) {
        Config.mainPage = mainPage;
        Config.save();
    }

    public static int getPort() {
        if(isNeedLoad()){
            Config.load();
        }
        return port;
    }

    public static void setPort(int port) {
        Config.port = port;
        Config.save();
    }
    
    
}
