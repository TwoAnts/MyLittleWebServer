package hzy.ui;

import hzy.Config;
import hzy.MultiThreadServer;
import hzy.func.FileFunc;
import hzy.log.Log;
import hzy.log.Log.LogListener;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame{
	private JButton btn_start;
	private JButton btn_stop;
	private JButton btn_open_config;
	private JButton btn_reload_config;
	private JTextArea ta_log;

	private JButton btn_sel_mainpage;
	private JButton btn_sel_src;
	private JButton btn_set_host_port;
	private JLabel label_mainpage;
	private JLabel label_src;
	private JLabel label_host_port;
	
	private JPanel pan_top;
	private JPanel pan_middle;
	private JPanel pan_bottom;
	
	private MultiThreadServer mServer;
	
	public MainFrame(){
		setTitle("hzy MultiThreadServer");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		initView();
		
		addListener();
		//加载配置信息到界面
		load();
		mServer = new MultiThreadServer();
		
		
	}
	
	private void addListener(){
		btn_set_host_port.addActionListener(btnSetHostPortListener);
		btn_sel_mainpage.addActionListener(btnMainPageListener);
		btn_sel_src.addActionListener(btnSrcListener);
		
		btn_start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(null != mServer){
					try {
						mServer.startService();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		btn_stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(null != mServer){
					mServer.stopService();
				}
			}
		});
		
		btn_open_config.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Runtime.getRuntime().exec("notepad " + Config.getUserDir() + File.separator + Config.configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		btn_reload_config.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				load();
			}
		});
		
		Log.setLogListener(new LogListener() {
			
			@Override
			public void log(String msg) {
				ta_log.append(msg + "\n");
			}
			
			@Override
			public void err(String msg) {
				ta_log.append("[ERR] " + msg + "\n");
			}
		});
	}
	
	private void load(){
		label_mainpage.setText("main page = " + Config.getMainPage());
		label_src.setText("src path = " + Config.getSrcDirPath());
		label_host_port.setText("ip:port = " + Config.getHostIP() + ":" + Config.getPort());
	}
	
	private ActionListener btnSetHostPortListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String input = JOptionPane.showInputDialog(
					MainFrame.this, "new ip:port = x.x.x.x:x", "set ip:port", JOptionPane.PLAIN_MESSAGE);
			if(null == input){
				return ;
			}
			input = input.trim();
			if(input.matches("[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}:[0-9]{1,5}")){
				label_host_port.setText("ip:port = " + input);
				String[] strs = input.split(":");
				Config.setHostIP(strs[0]);
				Config.setPort(Integer.valueOf(strs[1]));
			}else{
				JOptionPane.showMessageDialog(
						MainFrame.this, "ip:port is invalid!", "info", JOptionPane.PLAIN_MESSAGE);
			}
		}
	};
	
	
	private ActionListener btnMainPageListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JFileChooser fileChooser = new JFileChooser((null == Config.getSrcDirPath())?Config.getUserDir():Config.getSrcDirPath());
			fileChooser.setDialogTitle("select main page");
			fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Files", "html"));
			int result = fileChooser.showOpenDialog(MainFrame.this);
			if(result == JFileChooser.APPROVE_OPTION){
				File file = fileChooser.getSelectedFile();
				if(null == Config.getSrcDirPath()){
					label_mainpage.setText("main page = " + file.getName());
					label_src.setText("src path = " + file.getParent());
					Config.setMainPage(file.getName());
					Config.setSrcDirPath(file.getParent());
				}else {
					if(file.getPath().startsWith(Config.getSrcDirPath())){
						String path = file.getPath().substring(Config.getSrcDirPath().length());
						if(path.startsWith(File.separator)){
							path = path.substring(1);
						}
						label_mainpage.setText(
								"main page = " + path);
						Config.setMainPage(path);
					}else {
						label_mainpage.setText("main page = " + file.getName());
						label_src.setText("src path = " + file.getParent());
						Config.setMainPage(file.getName());
						Config.setSrcDirPath(file.getParent());
					}
				}
				
			}
		}
	};
	
	private ActionListener btnSrcListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("select source dir");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showOpenDialog(MainFrame.this);
			if(result == JFileChooser.APPROVE_OPTION){
				String path = fileChooser.getSelectedFile().getPath();
				label_src.setText("src path = " + path);
				String[] files = fileChooser.getSelectedFile().list();
				boolean have = false;
				for(String file : files){
					if(file.equals(Config.getMainPage())){
						have = true;
						break;
					}
				}
				if(!have){
					label_mainpage.setText("src path = ");
					Config.setMainPage(null);
				}
				Config.setSrcDirPath(path);
			}
			
		}
	};
	
	
	
	
	
	private void initView(){
		createTopPanel();
		createMiddlePanel();
		createBottomPanel();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c1 = new GridBagConstraints(); 
		c1.gridx = 0; 
		c1.gridy = 0;
		c1.weightx = 1; 
		c1.weighty = 1; 
		c1.fill = GridBagConstraints.BOTH; 
		// 加入 topPanel 
		add(pan_top, c1); 
		
		GridBagConstraints c2 = new GridBagConstraints(); 
		c2.gridx = 0; 
		c2.gridy = 1; 
		c2.weightx = 1; 
		c2.weighty = 30; 
		c2.fill = GridBagConstraints.BOTH; 
		// 加入 middlePanel 
		add(pan_middle, c2); 
		
		GridBagConstraints c3 = new GridBagConstraints(); 
		c3.gridx = 0; 
		c3.gridy = 2; 
		c3.weightx = 1; 
		c3.weighty = 1; 
		c3.fill = GridBagConstraints.HORIZONTAL; 
		
		add(pan_bottom, c3);
		
		
		
	}
	
	private void createTopPanel(){
		pan_top = new JPanel();
		btn_sel_mainpage = new JButton("选择首页");
		btn_sel_src = new JButton("选择主目录");
		btn_set_host_port = new JButton("设置IP和端口");
		label_mainpage = new JLabel();
		label_mainpage.setText("main page =");
		
		label_src = new JLabel();
		label_src.setText("src path = ");
		
		label_host_port = new JLabel();
		label_host_port.setText("ip:port = ");
		
		Border titled = BorderFactory.createTitledBorder("配置信息");
		pan_top.setBorder(titled);
		pan_top.setLayout(new GridBagLayout());
		
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 0;
		c1.gridy = 0;
		c1.weightx = 9;
		c1.anchor = GridBagConstraints.WEST;
		pan_top.add(label_host_port, c1);
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 0;
		c2.weightx = 1;
		c2.fill = GridBagConstraints.BOTH;
		pan_top.add(btn_set_host_port, c2);
		
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx = 0;
		c3.gridy = 1;
		c3.weightx = 9;
		c3.anchor = GridBagConstraints.WEST;
		pan_top.add(label_mainpage, c3);
		
		
		GridBagConstraints c4 = new GridBagConstraints();
		c4.gridx = 1;
		c4.gridy = 1;
		c4.weightx = 1;
		c4.fill = GridBagConstraints.BOTH;
		pan_top.add(btn_sel_mainpage, c4);
		
		GridBagConstraints c5 = new GridBagConstraints();
		c5.gridx = 0;
		c5.gridy = 2;
		c5.weightx = 9;
		c5.anchor = GridBagConstraints.WEST;
		pan_top.add(label_src, c5);
		
		GridBagConstraints c6 = new GridBagConstraints();
		c6.gridx = 1;
		c6.gridy = 2;
		c6.weightx = 1;
		c6.fill = GridBagConstraints.BOTH;
		pan_top.add(btn_sel_src, c6);
		
		
	}
	
	private void createMiddlePanel(){
		pan_middle = new JPanel();
		ta_log = new JTextArea();
		ta_log.setEditable(false);
		ta_log.setLineWrap(false);
//		ta_log.append("这是示例文字\n这是示例文字");
		JScrollPane jsp = new JScrollPane(ta_log);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		Border titled = BorderFactory.createTitledBorder("Log");
		jsp.setBorder(titled);
		pan_middle.setLayout(new BorderLayout());
		pan_middle.add(jsp, BorderLayout.CENTER);
	}
	
	private void createBottomPanel(){
		pan_bottom = new JPanel();
		pan_bottom.setLayout(new BoxLayout(pan_bottom, BoxLayout.X_AXIS));
		btn_start = new JButton("启动服务");
		btn_stop = new JButton("停止服务");
		btn_open_config = new JButton("打开配置文件");
		btn_reload_config = new JButton("更新配置");
		
		
//		pan_bottom.add(Box.createHorizontalStrut(50));
		pan_bottom.add(Box.createHorizontalGlue());
		pan_bottom.add(btn_start);
		pan_bottom.add(Box.createHorizontalStrut(50));
		pan_bottom.add(btn_stop);
		pan_bottom.add(Box.createHorizontalStrut(50));
		pan_bottom.add(btn_open_config);
		pan_bottom.add(Box.createHorizontalStrut(50));
		pan_bottom.add(btn_reload_config);
		pan_bottom.add(Box.createHorizontalGlue());
		
	}
	
	
	public static final int DEFAULT_WIDTH = 750;
	public static final int DEFAULT_HEIGHT = 600;
}
