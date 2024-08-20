package controll;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;



public class TodoClient  {
    public static void main(String[] args) {
        System.out.println("클라이언트 실행");

        Socket socket = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        Scanner sc = new Scanner(System.in);
        String loginUser = null;
        
     
        
        try {
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("서버에 연결되었습니다.");
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            
            while (true) {
            	System.out.print("메뉴 골라주세요. 1 : 회원가입 2 : 로그인 3: 테이블 조회 4: 카테고리별 테이블 조회 5: 할 일 생성");
                System.out.println("6: 할 일 삭제 7: 할일 달성여부수정 8: 투투 할일 변경 9: 할일 조회 10: 주간목표 생성 11: 주간목표 달성률 조회 12 : 종료");
            	int n = sc.nextInt();
                out.writeInt(n);
                String response = null;

                switch (n) {
                    case 1:
   					 while (true) {
                            System.out.println("사용하실 닉네임 입력 :");
                            String n_name = sc.next();
   					  
                            System.out.println("사용하실 비밀번호 입력 : ");
                            String pwd = sc.next();
                            out.writeUTF(n_name);
                            out.writeUTF(pwd);

                            out.flush();
                            response = in.readUTF();
                            System.out.println(response);

                            if (response.startsWith("환영합니다")) {
                                break;
                            }

                        }
                        break;

                    case 2:
                    	while (true) {
                            System.out.println("닉네임 입력 :");
                            String l_name = sc.next();

                            System.out.println("비밀번호 입력 :");
                            String l_pwd = sc.next();

                            out.writeUTF(l_name);
                            out.writeUTF(l_pwd);
                            out.flush();

                            response = in.readUTF();
                            System.out.println(response);

                            if (response.equals("로그인 성공")) {
                            	loginUser = l_name;
                                break;
                            }
                            break;
                        }
                        break;

                    case 3:
                        System.out.println("테이블 조회 요청");

                        response = in.readUTF();
                        System.out.println(response);

                        break;

                    case 4:
                    	 if (loginUser != null) {
                             System.out.println(loginUser + "님 카테고리 조회");
                             out.writeUTF(loginUser);  // 서버로 닉네임 전송
                             out.flush();  // 데이터를 서버로 보내는 즉시 flush
                             System.out.println("원하는 카테고리 입력해주세요");
                             String category = sc.next();
                             out.writeUTF(category);
                             out.flush();  // 데이터를 서버로 보내는 즉시 flush
                             response = in.readUTF();
                             System.out.println(response);
                         } else {
                             System.out.println("로그인 해주세요");
                         }
                         break;
                    case 5:
                        if (loginUser != null) {
                            System.out.println("할 일 생성");

                            System.out.println("할 일 날짜를 입력해주세요! ex) 'MM-DD'");
                            String date = sc.next();

                            System.out.println("카테고리를 선택해주세요! 1. 약속 2. 업무 3. 공부 4. 운동 5. 기타");
                            int categoryIdx = sc.nextInt();

                            System.out.println("할 일을 입력해주세요!");
                            sc.nextLine();
                            String todo = sc.nextLine();

                            out.writeUTF(loginUser);
                            out.writeUTF(date);
                            out.writeInt(categoryIdx);
                            out.writeUTF(todo);

                            response = in.readUTF();
                            System.out.println(response);

                        } else {
                            System.out.println("로그인 해주세요");
                        }
                        break;
                    case 6:
                        System.out.println("삭제할 테이블 입력. ");
                        int listIdx = sc.nextInt();
                        out.writeUTF(loginUser);
                        out.writeInt(listIdx);

                        response = in.readUTF();
                        System.out.println(response);

                        break;
                    case 7:
					    System.out.println(loginUser);
					    System.out.println("달성 여부 입력");
					    int status = sc.nextInt();sc.nextLine();
					    System.out.println("변경할 목표의 번호 입력");
					    String todoTitle = sc.nextLine();

    					out.writeUTF(loginUser);
    					out.writeInt(status);
    					out.writeUTF(todoTitle);
    
    					response = in.readUTF();
    					System.out.println(response);
    
    					break;
    				case 8:
    					System.out.println(loginUser);
    					System.out.println("변경할 목표 입력"); 
    					todoTitle = sc.next();
    					sc.nextLine();
    
    					System.out.println("변경될 목표");
    					String title = sc.next();
    					sc.nextLine();
    					
    					out.writeUTF(loginUser);
    					out.writeUTF(todoTitle);
    					out.writeUTF(title);
    
    					response = in.readUTF();
    					System.out.println(response);
    
    					break;
                    case 9 :
                    	if (loginUser != null) {
                            System.out.println(loginUser + "님 할일 조회");
                            out.writeUTF(loginUser);  // 서버로 닉네임 전송
                            out.flush();  // 데이터를 서버로 보내는 즉시 flush
                            System.out.println("원하는 날짜 입력해주세요");
                            String todoDate = sc.next();
                            out.writeUTF(todoDate);
                            out.flush();  // 데이터를 서버로 보내는 즉시 flush
                            response = in.readUTF();
                            System.out.println(response);
                        } else {
                            System.out.println("로그인 해주세요");
                        }
                        break;
                    case 10:
                        if (loginUser != null) {
                            System.out.println("주간목표 생성");

                            System.out.println("주간 목표를 작성해주세요.");
                            sc.nextLine();
                            String todoContent = sc.nextLine();

                            out.writeUTF(loginUser);
                            out.writeUTF(todoContent);

                            response = in.readUTF();
                            System.out.println(response);
                        } else {
                            System.out.println("로그인 해주세요.");
                        }
                        break;
                    // 사용자별 조회 - 달성률( title, 달성률, 상태값)
                    case 11:
                    	if (loginUser != null) {
                            System.out.println(loginUser + "님 달성률 조회");
                            out.writeUTF(loginUser);
                            out.flush();
                            System.out.println("검색할 주간목표 날짜를 입력해주세요 (MM-DD)");
                            String week_date = sc.next();
                            out.writeUTF(week_date);
                            out.flush();
                            response = in.readUTF();
                            System.out.println(response);
                        } else {
                            System.out.println("로그인 해주세요");
                        }
                        break;
                    case 12:
                        if (loginUser != null) {

                            System.out.println("완료한 할 일을 조회하려면 1, 완료하지 않은 할 일을 조회하려면 0을 입력해주세요.");
                            int statusInt = sc.nextInt();
                            // 완료
                            if (statusInt == 1) {
                                out.writeInt(1);
                                out.writeUTF(loginUser);
                                out.flush();
                                response = in.readUTF();
                                System.out.println(response);

                                // 미완료
                            } else if (statusInt == 0) {
                                out.writeInt(0);
                                out.writeUTF(loginUser);
                                out.flush();
                                response = in.readUTF();
                                System.out.println(response);
                            } else {
                                System.out.println("0,1 중에 입력해주세요.");
                            }
                        } else {
                            System.out.println("로그인 해주세요");
                        }
                        break;
                    case 13:
                    	System.out.println("종료합니다.");
                    	out.writeUTF(loginUser);
                    	response = in.readUTF();
                    	System.out.println(response);
                    	System.exit(0);
                    	break;
                    default:
                    	System.out.println("잘못된 번호 입니다. 다시 입력해주세요");
                    	break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (sc != null) sc.close();
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("프로그램이 종료되었습니다.");
    }
}
