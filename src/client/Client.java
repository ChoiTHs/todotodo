package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

import dbConn.util.ConnectionHelper;

public class Client {

	Connection conn;
	Statement stmt;
	PreparedStatement pstmtInsert, pstmtDlete;
	PreparedStatement pstmtSelect, pstmtSelectScroll;
	PreparedStatement pstmtSearch, pstmtSearchScroll;
	
	private String sqlInsert = "insert into customers values(?,?,?,?)";
	private String sqlDelete = "delete from customers where code = ?";
	private String sqlSelect = "select * from customers";
	private String sqlSearch = "select * from customers where name = ?";
	
	public void dbConnect() {
		try {
			conn = ConnectionHelper.getConnection("oracle");
			
			pstmtInsert = conn.prepareStatement(sqlInsert);
			pstmtDlete = conn.prepareStatement(sqlDelete);
			pstmtSelect = conn.prepareStatement(sqlSelect);
			pstmtSearch = conn.prepareStatement(sqlSearch);
			
			pstmtSelectScroll = conn.prepareStatement(sqlSelect,ResultSet.TYPE_SCROLL_SENSITIVE,// 커서 이동을 자유롭게
													ResultSet.CONCUR_UPDATABLE);// 업데이트 내용을 반영한다.
			// resultset object 의 변경이 가능 <==> CONCUR_READ_ONLY
			
			pstmtSearchScroll = conn.prepareStatement(sqlSearch, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			System.out.println("connection success!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//db 연결 끝///
	
	public static void main(String[] args) {
		if( args.length != 1 ) {
			System.out.println("USAGE : java TcpMultiChatClient nickName ? ");
			System.exit(0);
		}
		try {
			String ip = "127.0.0.1"; // "127.0.0.1" or "192.168.0.61"
			Socket s = new Socket(ip, 7799);
			System.out.println("서버에 연결 되었습니다.");
			
			Thread sender = new Thread(new ClientSender(s, args[0]));
			Thread receiver = new Thread(new ClientReceiver(s));
			
			sender.start();			receiver.start();  // run() 메소드 유도
		} catch (Exception e) {	e.printStackTrace(); }
	}//end main
	
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
