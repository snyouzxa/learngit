package cn.believeus.mydfs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
	public static void main(String[] args) {
		try {
			final Scanner scanner = new Scanner(System.in);
			System.out.print("请输入上传文件地址:");
			String filename = "d:/usr/local/jdk-6u45-windows-x64.exe";
			final File file = new File(filename);
			final long filesize = file.length();
			final int block = 1024 * 1024 * 10;// 10M 100M
			double times = Math.ceil(filesize / (float) block);
			final String fileId = UUID.randomUUID().toString();
			ExecutorService pool = Executors.newFixedThreadPool(3);
			// 100M 10M
			for (int i = 0; i < times; i++) {
				try {
					Socket socket = new Socket("192.168.3.20", 9999);
					OutputStream o = socket.getOutputStream();
					InputStream in = socket.getInputStream();
					DataInputStream datain = new DataInputStream(in);
					// 加密流
					DataOutputStream dataout = new DataOutputStream(o);
					// 截取文件后缀
					String stuffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);// /
					// 0.把文件名传输过去
					dataout.writeUTF(fileId + "-" + i);
					// 1.把文件后缀写到服务端 // pp.txt
					dataout.writeUTF(stuffix);
					// 2.把每次写的大小写到服务端
					dataout.writeInt(block);
					// 3.把文件大小输送到服务端

					dataout.writeLong(filesize);

					RandomAccessFile raFile = new RandomAccessFile(file, "r");
					raFile.seek(i * block);
					byte[] buf = new byte[block];//1024x1024x10
					int len = raFile.read(buf);
					// 我们每次发送到服务端的数据是block大小
					dataout.write(buf, 0, len);
					dataout.flush();//工程代码
					//必须等待服务器接收完毕，所有的流才关闭
					datain.readUTF();
					dataout.close();
					socket.close();
					raFile.close();
					scanner.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
