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

public class XClient {
	public static void main(String[] args) {
		try {
			final Scanner scanner = new Scanner(System.in);
			System.out.print("�������ϴ��ļ���ַ:");
			String filename = "C:\\Users\\84221\\Desktop\\who\\zip.zip";
			final File file = new File(filename);
			final long filesize = file.length();
			int block = 1024 * 1024 * 10;
			int times = (int)Math.ceil(filesize / (float) block);
			//��������һ����ļ���С (1024*1024*10)x3+10
			int endsize=(int)(filesize-((times-1)*block));
			
			final String fileId = UUID.randomUUID().toString();
			ExecutorService pool = Executors.newFixedThreadPool(3);
			// 100M 10M
			for (int i = 0; i < times; i++) {
				try {
					Socket socket = new Socket("192.168.3.8", 9998);
					OutputStream o = socket.getOutputStream();
					InputStream in = socket.getInputStream();
					DataInputStream datain = new DataInputStream(in);
					// ������
					DataOutputStream dataout = new DataOutputStream(o);
					// ��ȡ�ļ���׺
					String stuffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);// /
					// 0.���ļ��������ȥ
					dataout.writeUTF(fileId + "-" + i);
					// 1.���ļ���׺д������� // pp.txt
					dataout.writeUTF(stuffix);
					// 2.��ÿ�ζ��Ĵ�Сд�������,���һ���ļ���С<=block
					if(i==(times-1)){
						dataout.writeInt(endsize);
					}else {
						dataout.writeInt(block);
					}
					RandomAccessFile raFile = new RandomAccessFile(file, "r");
					raFile.seek(i * block);
					byte[] buf = new byte[block];
					int len = raFile.read(buf);
					// ����ÿ�η��͵�����˵�������block��С
					//����:���û�д�����ɣ�Ҳ����˵��Ϊ���������
					//�����ݲ���һ���ԣ������ȥ�ģ����ߴ����ʱ���ǲ����δ���
					dataout.write(buf, 0, len);
					dataout.flush();
					scanner.close();
					//����ȴ�������������ϣ����е����Źر�
					datain.readUTF();
					dataout.close();
					socket.close();
					raFile.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
