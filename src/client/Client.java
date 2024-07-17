package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener {
	JPanel panWest, panSouth; // 왼쪽텍스트필드, 아래쪽 버튼
	JPanel p1, p2;
	JTextField txtName, txtPwd;
	JButton btnSingIn, btnRegst;

	JTable table; // 검색과 전체 보기를 위한 테이블 객체 생성
	// 상태변화를 위한 변수 선언
	private static final int NONE = 0;
	private static final int LOGIN = 1, SingIn = 1;
	private static final int Regst = 2;
	int cmd = NONE;
	
	public static void main(String[] args) {
		Client();
	}
	
	private static void Client() {
		DataOutputStream dos;
		DataInputStream dis;
		try {
			String ip = "127.0.0.1"; // "127.0.0.1" or "192.168.0.61"
			Socket s = new Socket(ip, 7799);
			System.out.println("서버에 연결 되었습니다.");
			
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			
			Thread sender = new Thread(new ClientSender(s, "client"));
			Thread receiver = new Thread(new ClientReceiver(s));
			
			sender.start();			receiver.start();  // run() 메소드 유도
			
			System.out.println("송/수신 활성화");
			System.out.println("로그인 : 1, 회원가입 : 2");
			
			String type = new Scanner(System.in).nextLine();
			switch (Integer.parseInt(type)) {
			case 1: {
				System.out.println("로그인!");
				dos.writeUTF(type);
				System.out.println("ID : ");
				String nickname = new Scanner(System.in).nextLine();
				System.out.println("PWD : ");
				String pwd = new Scanner(System.in).nextLine();
				
				break;
			}
			case 2 :{
				System.out.println("회원가입!");
				dos.writeUTF(type);
				
				System.out.println("ID : ");
				String nickname = new Scanner(System.in).nextLine();
				System.out.println("PWD : ");
				String pwd = new Scanner(System.in).nextLine();
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + type);
			}
			
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
		} 
	}
	
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
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btnSingIn) {
			if (cmd != SingIn) {
				setText(SingIn); // user method
				return;
			} // if in
			setTitle(e.getActionCommand());
//			add(); // 추가

		} else if( obj == btnRegst ){
				if( cmd != Regst ){
					setText(Regst);  //user method
					return;
				} //if in
				setTitle(e.getActionCommand());
				init();
		}
	}

	private void init() {
		txtName.setText("");
		txtName.setEditable(false);
		txtPwd.setText("");
		txtPwd.setEditable(false);
	}

	private void setText(int command) {
		switch (command) {
		case SingIn:
			txtName.setEditable(true);
			txtPwd.setEditable(true);
			break;
		case Regst:
			txtName.setEditable(true);
			txtName.setEditable(true);
		}// switch end

		setButton(command); // user method
	}
	private void setButton(int command) {
		// TODO 자동 생성된 메소드 스텁
		
	}
}
