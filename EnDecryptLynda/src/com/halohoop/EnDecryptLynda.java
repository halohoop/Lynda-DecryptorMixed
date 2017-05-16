package com.halohoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class EnDecryptLynda extends Application {

	private TextArea videoLogArea;
	private TextArea capLogArea;
	private Button direChooseBtn;
	private Button decryptBtn;
	private TextField direChooseTf;
	private String videoLogHint = "Videos decrypting Log will be printed here!";
	private String capLogHint = "Captions decrypting Log will be printed here!";

	private static String CAPTION_DECRY_FILE_PATH = "D:\\workspaces\\eclipse\\201705_new\\EnDecryptLynda\\LyndaDecryptor\\LyndaCaptionToSrtConvertor";
	private static String VIDEO_DECRY_FILE_PATH = "D:\\workspaces\\eclipse\\201705_new\\EnDecryptLynda\\LyndaDecryptor\\LyndaDecryptor";
	
	@Override
	public void start(Stage primaryStage) {

		DirectoryChooser direChooser = new DirectoryChooser();

		StackPane root = new StackPane();
		root.setPadding(new Insets(10));
		root.setStyle("-fx-background-color: #336699;");
		HBox hBoxUp = new HBox(10);
		HBox hBoxDown = new HBox(10);
		VBox mainVBox = new VBox(10);

		root.getChildren().add(mainVBox);

		direChooseBtn = new Button("Choose");
		direChooseBtn.setPrefSize(150, 50);
		direChooseTf = new TextField("Please choose a directory contains encrypted videos or encrypted captions!");
		direChooseTf.setPrefSize(1040, 50);
		direChooseTf.setEditable(false);
		hBoxUp.getChildren().addAll(direChooseBtn, direChooseTf);

		VBox logVBox = new VBox(10);
		videoLogArea = new TextArea();
		videoLogArea.setPrefSize(590, 215);
		videoLogArea.setText(videoLogHint);
		videoLogArea.setEditable(false);
		capLogArea = new TextArea();
		capLogArea.setPrefSize(590, 215);
		capLogArea.setText(capLogHint);
		capLogArea.setEditable(false);
		logVBox.getChildren().addAll(videoLogArea, capLogArea);
		logVBox.setPrefSize(590, 440);

		decryptBtn = new Button("Decrypt");
		decryptBtn.setPrefSize(590, 440);
		hBoxDown.getChildren().addAll(logVBox, decryptBtn);
		mainVBox.getChildren().addAll(hBoxUp, hBoxDown);

		// set onclick
		direChooseBtn.setOnMouseClicked((Event event) -> {
			File file = direChooser.showDialog(primaryStage);
			if (file != null) {
				String absolutePath = file.getAbsolutePath();
				direChooseTf.setText(absolutePath);
			}
		});
		decryptBtn.setOnMouseClicked((Event event) -> {
			// 开始解密
			String text = direChooseTf.getText();
			startDecrypt(text);
		});

		Scene scene = new Scene(root, 1200, 500, Color.GRAY);

		primaryStage.setTitle("Lynda Downloaded Decrypter");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void startDecrypt(String filePath) {
		final File dire = new File(filePath);

		if (dire.exists() && dire.isDirectory()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 开始调用exe文件
					runVideoDecryptExe(VIDEO_DECRY_FILE_PATH, dire.getAbsolutePath(), videoLogArea);						
				}
			}).start();
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 开始调用exe文件
					runCaptionDecryptExe(CAPTION_DECRY_FILE_PATH, dire.getAbsolutePath(), capLogArea);							
				}
			}).start();
		} else {
			direChooseTf.setText("Please rechoose the directory!");
		}
	}

	protected void runCaptionDecryptExe(String decryExePath, String absolutePath, TextArea logArea) {
		final String outDire = absolutePath + File.separator + "decrypted";
		File outDir = new File(outDire);
		outDir.mkdirs();
		BufferedReader br = null;
		String headFlag = "Done ";
		String tailFlag = ".srt";
		try {
			logArea.setText("");
			String s;
			Process process = Runtime.getRuntime().exec(decryExePath + " /D " 
			+ absolutePath);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((s = br.readLine()) != null) {
				if(s.startsWith(headFlag) && s.endsWith(tailFlag)){
					String substring = s.substring(headFlag.length());
					File file2 = new File(substring);
					File newFile = new File(outDir.getAbsolutePath()+file2.separator+file2.getName());
					file2.renameTo(newFile);
				}
				logArea.appendText(s + "\n");
				logArea.setScrollTop(Double.MAX_VALUE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void runVideoDecryptExe(String decryExePath, String absolutePath, TextArea logArea) {
		String outDire = absolutePath + File.separator + "decrypted";
		BufferedReader br = null;
		try {
			logArea.setText("");
			String s;
			String cmd = decryExePath + " /D " 
					+ absolutePath + " /DB " + absolutePath +" /OUT "+outDire;
//			System.out.println(cmd);
			Process process = Runtime.getRuntime().exec(cmd);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((s = br.readLine()) != null) {
				logArea.appendText(s + "\n");
				logArea.setScrollTop(Double.MAX_VALUE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		super.init();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
//		System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径 
		String currProjectDire = System.getProperty("user.dir")+File.separator+"LyndaDecryptor"+File.separator;
		CAPTION_DECRY_FILE_PATH = currProjectDire+"LyndaCaptionToSrtConvertor";
		VIDEO_DECRY_FILE_PATH = currProjectDire+"LyndaDecryptor";
		launch(args);
	}
}
