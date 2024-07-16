package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	public static void main(String[] args) {
		Client();
	}//end main
	
	private static void Client() {
		
		DataOutputStream dos;
		
		System.out.println("이름을 입력하세요");
		String nickname = new Scanner(System.in).nextLine();
		System.out.println("비밀번호를 입력해주세요");
		String pwd = new Scanner(System.in).nextLine();
		
		if( nickname.length() < 1 ) {
			System.out.println("USAGE : java TcpMultiChatClient nickName ? ");
			System.exit(0);
		}
		try {
			String ip = "127.0.0.1"; // "127.0.0.1" or "192.168.0.61"
			Socket s = new Socket(ip, 7799);
			System.out.println("서버에 연결 되었습니다.");
			
			
			dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(nickname);
			dos.writeUTF(pwd);
			
			Thread sender = new Thread(new ClientSender(s, nickname));
			Thread receiver = new Thread(new ClientReceiver(s));
			
			sender.start();			receiver.start();  // run() 메소드 유도
			System.out.println("송/수신 활성화");
		} catch (Exception e) {	e.printStackTrace(); }
	}

	static class ClientSender extends Thread { //inner class
		Socket s;
		DataOutputStream dos;
		String name;
		
		public ClientSender(Socket s, String name) {
			this.s = s;
			try {
				dos = new DataOutputStream(s.getOutputStream());
				this.name = name;
				
			}  catch (Exception e) { e.printStackTrace(); }
		}

		@Override
		public void run() {
			Scanner sc = new Scanner(System.in); //키보드로부터 입력 받기
			try {
				if( dos != null ) dos.writeUTF(name);
				while( dos != null ) dos.writeUTF("["+name+"] "+ sc.nextLine());
			}  catch (Exception e) { e.printStackTrace(); }
		} //end run()
	}// end ClientSender class
	
	static class ClientReceiver extends Thread { //inner class
		Socket s ;
		DataInputStream dis;
				public ClientReceiver(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
			} catch (Exception e) { e.printStackTrace(); }
		}

		@Override
		public void run() {
			while( dis != null ) {
				try {
					System.out.println(dis.readUTF());
				} catch (Exception e) { e.printStackTrace(); }
			}
		}//run() end
		
	}// end ClientReceiver class
}
