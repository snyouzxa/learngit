package cn.believeus.mydfs;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//5GЭ��
public class Server {
	public static void main(String[] args) {
		try {
			// 127.0.0.1:9999/192.168.3.20:9999
			ServerSocket serverSocket = new ServerSocket(9999);
			ExecutorService pool = Executors.newFixedThreadPool(5);
			ResourceBundle bundle = ResourceBundle.getBundle("project");
			final String basepath = bundle.getString("basepath");
			new File(basepath).mkdirs();
			while (true) {
				System.out.println("�ȴ��ͻ����ļ��ϴ�����");
				final Socket socket = serverSocket.accept();// ���Žӿ�
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				final DataOutputStream dataout=new DataOutputStream(out);
				final DataInputStream datain = new DataInputStream(in);
				pool.execute(new Runnable() {

					@Override
					public void run() {
						try {
							// 0.�����ļ���
							String fileId = datain.readUTF();
							// 1.�����ļ���׺
							String stuffix = datain.readUTF();
							// 2.����ÿ�ζ����С
							int block = datain.readInt();
							// 3.�����ͻ����ļ���С
							long filesize = datain.readLong();

							
							// ƴ���ļ�����
							String fileName = basepath + fileId + "." + stuffix;
							FileOutputStream out = new FileOutputStream(fileName);
							byte[] buf = new byte[block];
							int len = datain.read(buf);
							out.write(buf, 0, len);
							out.flush();
							/*int readsize=1024*1024;
							double times = Math.ceil(block/ (float)readsize);
							
							byte[] buf = new byte[block];
							//�ͻ��˲�����-1 
							for (int i = 0; i < times; i++) {
								int len = datain.read(buf);
								out.write(buf, 0, len);
								out.flush();
							}*/
							out.close();
							dataout.writeUTF("over");
							datain.close();
							socket.close();
							System.out.println("file save path:" + fileName);

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
