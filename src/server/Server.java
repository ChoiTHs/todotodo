package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import dbConn.util.ConnectionHelper;

public class Server {  //outer class
	
	Connection conn;
	Statement stmt;
	PreparedStatement pstmtInsert;
	PreparedStatement pstmtSelect, pstmtSelectScroll;
	PreparedStatement pstmtSearch, pstmtSearchScroll;
	PreparedStatement pstmtUsersSearch, pstmtUsersScroll;
	PreparedStatement pstmtUsersInsert;
	MyModel model;
	
	private String sqlUsersInsert = "INSERT INTO USERS(USERIDX, NICKNAME, PWD, STATUS) VALUES(USERS_SEQ.NEXTVAL, ?, ?, ?)";
	private String sqlUsersSelect = "SELECT * FROM USERS";
	private String sqlUsersSearch = "SELECT * FROM USERS WHERE (NICKNAME = ? AND PWD = ?)";
	
	public void dbConnect() {
		try {
			conn = ConnectionHelper.getConnection("oracle");
			
			pstmtInsert = conn.prepareStatement(sqlUsersInsert);
			pstmtSelect = conn.prepareStatement(sqlUsersSelect);
			pstmtSearch = conn.prepareStatement(sqlUsersSearch);
			
			pstmtSearchScroll = conn.prepareStatement(sqlUsersSearch, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//db 연결 끝///
	
	HashMap clients ;  // key, value
	
	public Server() { //생성자함수 - 멤버변수 초기화담당
		clients = new HashMap(); //HashMap 객체 생성
		Collections.synchronizedMap(clients);  //동기화
	}
	
	public void start() {  //user method
		ServerSocket ss = null;  // server socket 1
		Socket s = null; // client socket 1
		DataInputStream dis;
		
		try {
			ss = new ServerSocket(7799);
			System.out.println("서버 시작 되었습니다. 쳇 시작 합시다!!");
			while(true) {
				s = ss.accept(); //응답대기, 클라이언트소켓
				dbConnect();
				System.out.println("["+s.getInetAddress()+":"+s.getPort()+"] 에서 접속하셨습니다.");
				
				dis = new DataInputStream(s.getInputStream());
				String nickname = dis.readUTF();
				String pwd = dis.readUTF();
				
				pstmtSearchScroll.setString(1, nickname);
				pstmtSearchScroll.setString(2, pwd);
				ResultSet rsScroll = pstmtSearchScroll.executeQuery();
				pstmtSearch.setString(1, nickname);
				pstmtSearch.setString(2, pwd);
				ResultSet rs = pstmtSearch.executeQuery();
				
				if( model == null ) model = new MyModel();
				
				model.getRowCount(rsScroll); 
				model.setData(rs);
				
				pstmtSearch.setString(1, nickname);
				pstmtSearch.setString(2, pwd);
				
				if(model.rows < 1) {
					pstmtInsert.setString(1, nickname);
					pstmtInsert.setString(2, pwd);
					pstmtInsert.setInt(3, 1);
					
					pstmtInsert.executeQuery();
				}
				
				ServerReceiver thread = new ServerReceiver(s);  // user class
				thread.start();
			} //end while
		} catch (Exception e) { e.printStackTrace(); 	}		
	}//end start()
	
	
	class ServerReceiver extends Thread { //inner class
		Socket s ; //client socket 
		DataInputStream dis;  // readXXX() - XXX : 자료형
		DataOutputStream dos; // writeXXX()
		
		public ServerReceiver(Socket s) { //응답
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream()); //네트워크 통해서 읽기
				dos = new DataOutputStream(s.getOutputStream()); //네트워크 통해서 쓰기
				
			} catch (Exception e) { e.printStackTrace(); 	}	
		}//end constructor
		
		public void sendToAll(String msg) {//전체 메시지 보내기
			Iterator it = clients.keySet().iterator();  //key 값 출력
			
			while( it.hasNext() ) { //요소가 있다면,...
				try {
					DataOutputStream dos = (DataOutputStream)clients.get(it.next()); //해당 키 값으로 value 값 출력
					//System.out.println("dos.toString() : "+dos.toString());
					dos.writeUTF(msg);
				} catch (Exception e) { e.printStackTrace(); 	}	
			}
			
		}//end sendToAll()

		@Override
		public void run() { //스레드 실행부(구현부)
			String nickname = "";
			try {
				nickname = dis.readUTF();
				
				
				
				sendToAll("#"+nickname+" 님이 입장하셨습니다.");
				
				clients.put(nickname, dos);  // HashMap 넣기
				System.out.println("현재 서버 접속자 수는 : "+ clients.size() + " 입니다."); //접속자 수 확인
				 
				while( dis != null ) {
					sendToAll(dis.readUTF());
				}
			} catch (Exception e) {	e.printStackTrace();
			} finally {
				sendToAll("#"+nickname+" 님이 나가셨습니다.");
				clients.remove(nickname); //접속자 수 제거
				System.out.println("["+s.getInetAddress()+":"+s.getPort()+"] 에서 접속 종료 하셨습니다.");
				System.out.println("현재 서버 접속자 수는 : "+ clients.size() + " 입니다.");
			} //try end
		} //end run()
	}//end ServerReceiver class
	
	
	public static void main(String[] args) {
		new Server().start();
//		TcpMultiChatServer server = new TcpMultiChatServer();
//		server.start();
	}
} //outer class
