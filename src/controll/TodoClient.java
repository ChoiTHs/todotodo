package controll;

import static date_utils.DateUtils.isValidDate;
import static view.CalendarPanel.createCalendarDialog;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import view.CalendarPanel;
import view.TodoTableView;

public class TodoClient extends JFrame {
    private static Socket socket = null;
    private static DataOutputStream out = null;
    private static DataInputStream in = null;
    private static String loginUser = null;
    private static JTextArea resultsArea;

    // 카테고리 이름과 인덱스 매핑
    private static final String[] CATEGORIES = {"약속", "업무", "공부", "운동", "기타"};


    static String deleteSelect[] = {"content", "name", "status", "importance", "todocreatedat"};
    static DefaultTableModel model = new DefaultTableModel(deleteSelect, 0);
    static JTable jTable = new JTable(model);

    static String weeklySelect[] = {"title", "status", "createdat"};
    static DefaultTableModel weeklymodel = new DefaultTableModel(weeklySelect, 0);
    static JTable weeklyjTable = new JTable(weeklymodel);

    public static void main(String[] args) {
        try {
            socket = new Socket("192.168.0.160", 7777);
            System.out.println("서버에 연결되었습니다.");
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            JFrame frame = new JFrame("Todo Client");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // Login Panel
            JPanel loginPanel = new JPanel(new GridLayout(3, 2));
            JLabel loginNameLabel = new JLabel("닉네임:", JLabel.CENTER);
            JTextField loginNameField = new JTextField();
            JLabel loginPasswordLabel = new JLabel("비밀번호:", JLabel.CENTER);
            JPasswordField loginPasswordField = new JPasswordField();
            JButton loginButton = new JButton("로그인");
            JButton registerButton = new JButton("회원가입");
            JButton backButton = new JButton("뒤로가기");

            loginPanel.add(loginNameLabel);
            loginPanel.add(loginNameField);
            loginPanel.add(loginPasswordLabel);
            loginPanel.add(loginPasswordField);
            loginPanel.add(loginButton);
            loginPanel.add(registerButton);

            // Register Panel
            JPanel registerPanel = new JPanel(new GridLayout(3, 2));
            JLabel registerNameLabel = new JLabel("닉네임:");
            JTextField registerNameField = new JTextField();
            JLabel registerPasswordLabel = new JLabel("비밀번호:");
            JPasswordField registerPasswordField = new JPasswordField();
            JButton submitRegisterButton = new JButton("회원가입");

            registerPanel.add(registerNameLabel);
            registerPanel.add(registerNameField);
            registerPanel.add(registerPasswordLabel);
            registerPanel.add(registerPasswordField);
            registerPanel.add(submitRegisterButton);
            registerPanel.add(backButton);

            // Todo Panel
            JPanel todoPanel = new JPanel(new BorderLayout());

            // Menu Bar
            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("Menu");
            JMenuItem todoMenuItem = new JMenuItem("ToDo");
            JMenuItem weeklyGoalsMenuItem = new JMenuItem("Weekly Goals");
            menu.add(todoMenuItem);
            menu.add(weeklyGoalsMenuItem);
            menuBar.add(menu);
            frame.setJMenuBar(menuBar);

            // Buttons Panel
            JPanel buttonsPanel = new JPanel(new FlowLayout());
            JButton createButton = new JButton("할일 생성");
            JButton updateTitleButton = new JButton("할일 제목 수정");
            JButton updateTodoStatusButton = new JButton("할일 상태값 변경");
            JButton viewButton = new JButton("할일 조회");
            JButton deleteButton = new JButton("할일 삭제");
            JButton weeklyButton = new JButton("주간목표");
            JButton logoutButton = new JButton("로그아웃");

            buttonsPanel.add(createButton);
            buttonsPanel.add(updateTitleButton);
            buttonsPanel.add(updateTodoStatusButton);
            buttonsPanel.add(viewButton);
            buttonsPanel.add(deleteButton);
            buttonsPanel.add(weeklyButton);
            buttonsPanel.add(logoutButton);

            // Main content area for ToDo
            JTextArea resultsArea = new JTextArea();
            resultsArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(resultsArea);

            // Set layout for todoPanel
            todoPanel.add(scrollPane, BorderLayout.CENTER);
            todoPanel.add(buttonsPanel, BorderLayout.SOUTH);

            JPanel weeklyPanel = new JPanel(new BorderLayout());

            JPanel weeklybuttonsPanel = new JPanel(new FlowLayout());
            JButton weeklycreateButton = new JButton("주간목표 생성");
            JButton updateStatusButton = new JButton("주간목표 달성여부수정");
            JButton selectWeeklyButton = new JButton("주간목표 조회");
            JButton weeklylogoutButton = new JButton("로그아웃");
            JButton weeklybackbtn = new JButton("뒤로가기");

            weeklybuttonsPanel.add(weeklycreateButton);
            weeklybuttonsPanel.add(updateStatusButton);
            weeklybuttonsPanel.add(selectWeeklyButton);
            weeklybuttonsPanel.add(weeklylogoutButton);
            weeklybuttonsPanel.add(weeklybackbtn);

            JTextArea weeklyresultsArea = new JTextArea();
            weeklyresultsArea.setEditable(false);

            JScrollPane weeklyscrollPane = new JScrollPane(weeklyresultsArea);
            weeklyPanel.add(weeklyscrollPane, BorderLayout.CENTER);
            weeklyPanel.add(weeklybuttonsPanel, BorderLayout.SOUTH);

            // Add panels to mainPanel
            mainPanel.add(loginPanel, "login");
            mainPanel.add(registerPanel, "register");
            mainPanel.add(todoPanel, "todo");
            mainPanel.add(weeklyPanel, "weekly");

            // Show login panel initially
            cardLayout.show(mainPanel, "login");

            frame.add(mainPanel);
            frame.setVisible(true);

            // Action Listeners
            loginButton.addActionListener(e -> {
                try {
                    String l_name = loginNameField.getText();
                    String l_pwd = new String(loginPasswordField.getPassword());

                    out.writeInt(2); // 로그인 요청 코드
                    out.writeUTF(l_name);
                    out.writeUTF(l_pwd);
                    out.flush();

                    String response = in.readUTF();
                    System.out.println(response);

                    if (response.startsWith("로그인 성공")) {
                        loginUser = l_name; // 로그인 성공 시 사용자 정보를 저장
                        JOptionPane.showMessageDialog(frame, response, "알림", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(mainPanel, "todo");
                        loginNameField.setText("");
                        loginPasswordField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(frame, response, "알림", JOptionPane.ERROR_MESSAGE);
                        loginNameField.setText("");
                        loginPasswordField.setText("");
                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            logoutButton.addActionListener(e -> {
                try {
                    // 로그아웃 요청
                    System.out.println("로그아웃 요청 보내는 중...");
                    out.writeInt(13); // 로그아웃 요청 코드
                    out.writeUTF(loginUser != null ? loginUser : ""); // 로그인 사용자 정보를 전송
                    out.flush();

                    // 서버 응답 대기
                    String response = in.readUTF();
                    System.out.println("서버 응답: " + response);

                    // 응답 처리
                    if (response.startsWith("로그아웃 성공")) {
                        // 로그인 성공 시 사용자 정보를 초기화
                        loginUser = null;

                        // 로그인 화면으로 전환
                        cardLayout.show(mainPanel, "login");

                        // 입력 필드 초기화
                        loginNameField.setText("");
                        loginPasswordField.setText("");
                    } else {
                        // 서버 응답에 따른 처리 (로그아웃 실패 등의 경우)
                        JOptionPane.showMessageDialog(frame, response, "알림", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            logoutButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));

            weeklylogoutButton.addActionListener(e -> {
                try {
                    // 로그아웃 요청
                    System.out.println("로그아웃 요청 보내는 중...");
                    out.writeInt(13); // 로그아웃 요청 코드
                    out.writeUTF(loginUser != null ? loginUser : ""); // 로그인 사용자 정보를 전송
                    out.flush();

                    // 서버 응답 대기
                    String response = in.readUTF();
                    System.out.println("서버 응답: " + response);

                    // 응답 처리
                    if (response.startsWith("로그아웃 성공")) {
                        // 로그인 성공 시 사용자 정보를 초기화
                        loginUser = null;

                        // 로그인 화면으로 전환
                        cardLayout.show(mainPanel, "login");

                        // 입력 필드 초기화
                        loginNameField.setText("");
                        loginPasswordField.setText("");
                    } else {
                        // 서버 응답에 따른 처리 (로그아웃 실패 등의 경우)
                        JOptionPane.showMessageDialog(frame, response, "알림", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            weeklylogoutButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));

            registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));

            submitRegisterButton.addActionListener(e -> {
                try {
                    String r_name = registerNameField.getText();
                    String r_pwd = new String(registerPasswordField.getPassword());

                    if (r_name.isEmpty() || r_pwd.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "모든 필드를 입력해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    out.writeInt(1); // 회원가입 요청 코드
                    out.writeUTF(r_name);
                    out.writeUTF(r_pwd);
                    out.flush();

                    String response = in.readUTF();
                    System.out.println(response);

                    if (response.startsWith("환영합니다")) {
                        JOptionPane.showMessageDialog(frame, "회원가입 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(mainPanel, "login");
                    } else {
                        JOptionPane.showMessageDialog(frame, response, "알림", JOptionPane.ERROR_MESSAGE);
                        registerNameField.setText("");
                        registerPasswordField.setText("");
                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            backButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
            // 할일 생성 버튼 이벤트 리스너
            createButton.addActionListener(e -> {
                System.out.println("로그인 사용자: " + loginUser); // 로그인 사용자 확인

                if (loginUser == null) {
                    JOptionPane.showMessageDialog(frame, "로그인 해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean validInput = false;  // 입력 유효성 검증 변수
                while (!validInput) {
                    try {
                        JPanel createPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                        JLabel titleLabel = new JLabel("제목:");
                        JTextField titleField = new JTextField();
                        JLabel categoryLabel = new JLabel("카테고리:");
                        JComboBox<String> categoryComboBox = new JComboBox<>(CATEGORIES);
                        JLabel dateLabel = new JLabel("날짜:");
                        JTextField dateField = new JTextField();
                        dateField.setEditable(false);  // 직접 편집하지 않도록 설정
                        JButton dateButton = new JButton("날짜 선택");
                        JCheckBox imporCheckBoxLable = new JCheckBox("중요", false);

                        createPanel.add(titleLabel);
                        createPanel.add(titleField);
                        createPanel.add(categoryLabel);
                        createPanel.add(categoryComboBox);
                        createPanel.add(dateLabel);
                        createPanel.add(dateField);
                        createPanel.add(dateButton);
                        createPanel.add(imporCheckBoxLable);

                        dateButton.addActionListener(event -> createCalendarDialog(frame, selectedDate -> {
                            // MM-dd 형식으로 변환하여 텍스트 필드에 설정
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(selectedDate));
                                dateField.setText(sdf.format(calendar.getTime()));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }));

                        int result = JOptionPane.showConfirmDialog(frame, createPanel, "할일 생성",
                                JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            String title = titleField.getText();
                            String category = (String) categoryComboBox.getSelectedItem();
                            String date = dateField.getText();
                            String importance = String.valueOf(imporCheckBoxLable.isSelected() ? 1 : 0);

                            if (title.isEmpty() || date.isEmpty()) {
                                JOptionPane.showMessageDialog(frame, "모든 필드를 입력해 주세요.", "알림",
                                        JOptionPane.WARNING_MESSAGE);
                                continue;
                            }

                            // 날짜 형식 검증 (MM-dd), 오늘 날짜보다 이전인지.
                            boolean validDate = isValidDate(date);
                            if (!validDate) {
                                JOptionPane.showMessageDialog(frame, "날짜 형식이 잘못되었습니다. MM-dd 형식으로 유효한 날짜를 입력해주세요.",
                                        "알림",
                                        JOptionPane.WARNING_MESSAGE);
                                continue;
                            }

                            // 카테고리 문자열을 인덱스로 변환
                            int categoryIdx = -1;
                            for (int i = 0; i < CATEGORIES.length; i++) {
                                if (CATEGORIES[i].equals(category)) {
                                    categoryIdx = i + 1;
                                    break;
                                }
                            }

                            if (categoryIdx == -1) {
                                JOptionPane.showMessageDialog(frame, "잘못된 카테고리 선택", "알림",
                                        JOptionPane.ERROR_MESSAGE);
                                continue;
                            }
                            try {
                                // 데이터 전송
                                out.writeInt(5); // 할일 생성 요청 코드
                                out.writeUTF(loginUser);
                                out.writeUTF(title);
                                out.writeInt(categoryIdx); // 인덱스를 서버로 전송
                                out.writeUTF(date);
                                out.writeUTF(importance);
                                out.flush();

                                // 로그 추가
                                System.out.println(
                                        "전송된 데이터: " + loginUser + ", " + title + ", " + categoryIdx + ", " + date
                                                + ", "
                                                + importance);

                                String response = in.readUTF();
                                if (response.startsWith("오늘의 할 일 생성 성공")) {
                                    // If task creation is successful, show the new task in the results area
                                    resultsArea.append(
                                            "제목: " + title + ", 카테고리: " + category + ", 날짜: " + date + "\n");
                                    JOptionPane.showMessageDialog(frame, response, "알림",
                                            JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    // If task creation failed, show the error message
                                    JOptionPane.showMessageDialog(frame, response, "알림", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (EOFException ex) {
                                JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                                        JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            break;  // 취소 버튼 클릭시 루프 종료
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "입력 처리 중 오류가 발생했습니다.", "오류",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });

            // 할 일 제목 수정
            updateTitleButton.addActionListener(e -> {
                if (loginUser == null) {
                    JOptionPane.showMessageDialog(frame, "로그인 해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    // 서버에 할 일 리스트 요청
                    out.writeInt(17);   // 리스트 요청 코드
                    out.writeUTF(loginUser);
                    out.flush();

                    // 서버에서부터 할일 리스트 데이터 수신
                    String response = in.readUTF();

                    model.setRowCount(0);

                    String[] lines = response.split("\n");

                    if (lines.length > 0) {
                        for (String line : lines) {
                            // 각 줄을 쉼표로 나누기
                            String[] columns = line.split(",");

                            // 데이터가 유효한 경우에만 추가
                            if (columns.length == 5) {
                                String title = columns[0];
                                String name = columns[1];
                                String status = columns[2];
                                String todoCreatedAt = columns[3];
                                String importance = columns[4];

                                // 테이블에 행 추가
                                model.addRow(new Object[]{title, name, status, todoCreatedAt, importance});
                            }
                        }
                    }
                    JPanel dialogPanel = new JPanel(new BorderLayout());
                    JScrollPane tableScrollPane = new JScrollPane(jTable);
                    dialogPanel.add(tableScrollPane, BorderLayout.CENTER);

                    int option = JOptionPane.showConfirmDialog(frame, dialogPanel, "할 일 목록",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if (option == JOptionPane.OK_OPTION) {
                        String updateTitle = JOptionPane.showInputDialog(frame, "수정할 제목을 입력하세요.");
                        String newTitle = JOptionPane.showInputDialog(frame, "새로운 제목을 입력하세요.");

                        if (updateTitle != null && !updateTitle.trim().isEmpty() && newTitle != null && !newTitle.trim()
                                .isEmpty()) {
                            boolean found = false;

                            for (int i = 0; i < model.getRowCount(); i++) {
                                String title = (String) model.getValueAt(i, 0);
                                if (title.equalsIgnoreCase(updateTitle)) {
                                    try {
                                        // 서버에 제목 수정 요청
                                        out.writeInt(8);
                                        out.writeUTF(loginUser);
                                        out.writeUTF(title);
                                        out.writeUTF(newTitle);
                                        out.flush();

                                        // 서버에서 응답 받음
                                        String responseUpdate = in.readUTF();
                                        System.out.println("서버 응답 " + responseUpdate);

                                        if (!responseUpdate.startsWith("할일 제목이 업데이트 되지 않았습니다.")) {
                                            JOptionPane.showMessageDialog(frame, "수정 성공: " + newTitle, "성공",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            found = true;

                                            out.writeInt(17);
                                            out.writeUTF(loginUser);
                                            out.flush();

                                            String allTodoList = in.readUTF();

                                            // 테이블 모델 초기화
                                            model.setRowCount(0);

                                            String[] lines_i = allTodoList.split("\n");
                                            if (lines_i.length > 0) {
                                                for (String line : lines_i) {
                                                    String[] columns = line.split(",");

                                                    if (columns.length == 5) {
                                                        String todo = columns[0];
                                                        String name = columns[1];
                                                        String status = columns[2];
                                                        String todoCreatedAt = columns[3];
                                                        String importance = columns[4];

                                                        // 데이터 유효성 검사
                                                        if (todo != null && status != null) {
                                                            model.addRow(
                                                                    new Object[]{todo, name, status, todoCreatedAt,
                                                                            importance});
                                                        } else {
                                                            System.err.println("Invalid data: " + line);
                                                        }
                                                    } else {
                                                        System.err.println(
                                                                "Unexpected number of columns: " + columns.length);
                                                    }
                                                }
                                            }
                                            JPanel dialogPanel_i = new JPanel(new BorderLayout());
                                            JScrollPane tableScrollPane_i = new JScrollPane(jTable);
                                            dialogPanel_i.add(tableScrollPane_i, BorderLayout.CENTER);

                                            JOptionPane.showMessageDialog(frame, dialogPanel_i, "할 일 목록",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                            break;
                                        }
                                    } catch (EOFException ex) {
                                        JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                                                JOptionPane.ERROR_MESSAGE);
                                        ex.printStackTrace();


                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                            if (!found) {
                                JOptionPane.showMessageDialog(frame, "제목을 찾을 수 없습니다. " + newTitle, "오류",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            // 할 일 상태값 변경
            updateTodoStatusButton.addActionListener(e -> {
                if (loginUser == null) {
                    JOptionPane.showMessageDialog(frame, "로그인 해주세요.", "알림",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 서버에서 유저의 할 일 리스트 조회
                try {
                    out.writeInt(17);
                    out.writeUTF(loginUser);
                    out.flush();

                    // 서버로부터 데이터 읽기
                    String response = in.readUTF();

                    // 테이블 초기화
                    model.setRowCount(0);

                    // 응답을 줄 단위로 나누기
                    String[] lines = response.split("\n");

                    //데이터가 비어 있지 않은 경우에만 처리
                    if (lines.length > 0) {
                        for (String line : lines) {
                            String[] columns = line.split(",");

                            // 데이터가 유효한 경우에만 추가
                            if (columns.length == 5) {
                                String title = columns[0];
                                String name = columns[1];
                                String status = columns[2];
                                String todoCreatedAt = columns[3];
                                String importance = columns[4];

                                if (title != null && status != null
                                        && todoCreatedAt != null) {
                                    // 테이블에 행 추가
                                    model.addRow(new Object[]{title, name, status, todoCreatedAt, importance});
                                } else {
                                    System.err.println("Invalid data: " + line);
                                }
                            } else {
                                System.err.println(
                                        "Unexpected number of columns: "
                                                + columns.length);
                            }
                        }
                    }
                    JPanel dialogPanel = new JPanel(new BorderLayout());
                    JScrollPane tableScrollPane = new JScrollPane(jTable);
                    dialogPanel.add(tableScrollPane, BorderLayout.CENTER);

                    // 다이얼로그에서 OK 버튼 클릭 시 이벤트 처리
                    int option = JOptionPane.showConfirmDialog(frame, dialogPanel, "주간 목록",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if (option == JOptionPane.OK_OPTION) {
                        String updateStatusTitle = JOptionPane.showInputDialog(frame, "완료한 할 일의 제목을 입력해주세요.");
                        if (updateStatusTitle != null && !updateStatusTitle.trim().isEmpty()) {
                            boolean found = false;
                            for (int i = 0; i < model.getRowCount(); i++) {
                                String title = (String) model.getValueAt(i, 0);
                                if (title.equalsIgnoreCase(updateStatusTitle)) {
                                    try {
                                        // 서버에 수정 요청
                                        out.writeInt(7);
                                        out.writeUTF(updateStatusTitle);
                                        out.writeUTF(loginUser);
                                        out.flush();

                                        String responseUpdateTodoStatus = in.readUTF();

                                        if (!responseUpdateTodoStatus.startsWith("할 일이 업데이트 되지 않았습니다.")) {
                                            JOptionPane.showMessageDialog(frame, "수정 성공: " + title,
                                                    "성공", JOptionPane.INFORMATION_MESSAGE);
                                            found = true;
                                            out.writeInt(17);
                                            out.writeUTF(loginUser);
                                            out.flush();

                                            String allTodoResponse = in.readUTF();

                                            model.setRowCount(0);

                                            String[] lines_1 = allTodoResponse.split("\n");
                                            if (lines_1.length > 0) {
                                                for (String line : lines_1) {
                                                    String[] columns = line.split(",");

                                                    if (columns.length == 5) {
                                                        String todo = columns[0];
                                                        String name = columns[1];
                                                        String status = columns[2];
                                                        String todoCreatedAt = columns[3];
                                                        String importance = columns[4];

                                                        model.addRow(new Object[]{todo, name, status, todoCreatedAt,
                                                                importance});
                                                    } else {
                                                        System.err.println("Invalid data: " + line);
                                                    }
                                                }

                                            }
                                            JPanel dialogPanel_1 = new JPanel(new BorderLayout());
                                            JScrollPane tableScrollPane_1 = new JScrollPane(jTable);
                                            dialogPanel_1.add(tableScrollPane_1, BorderLayout.CENTER);

                                            // 다이얼로그로 테이블 표시
                                            JOptionPane.showMessageDialog(frame, dialogPanel_1,
                                                    "할 일 목록", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    } catch (EOFException ex) {
                                        JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.",
                                                "연결 오류", JOptionPane.ERROR_MESSAGE);
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                            if (!found) {
                                JOptionPane.showMessageDialog(frame, "제목을 찾을 수 없습니다. " + updateStatusTitle, "오류",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }

                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            viewButton.addActionListener(e ->

            {
                if (loginUser == null) {
                    JOptionPane.showMessageDialog(frame, "로그인 해주세요.", "알림",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String[] options = {"카테고리별 조회", "날짜별 조회", "할일 완료 여부 조회"};
                int choice = JOptionPane.showOptionDialog(frame, "조회 방식을 선택하세요:", "조회 옵션 선택",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options,
                        options[0]);

                try {
                    switch (choice) {
                        case 0: // 카테고리별 조회
                            String category = (String) JOptionPane.showInputDialog(frame,
                                    "카테고리를 선택하세요:",
                                    "카테고리별 조회", JOptionPane.QUESTION_MESSAGE, null, CATEGORIES,
                                    CATEGORIES[0]);
                            if (category != null) {
                                out.writeInt(4); // 카테고리별 조회 요청 코드
                                out.writeUTF(loginUser);
                                out.writeUTF(category);
                                out.flush();
                                String response = in.readUTF();

                                TodoTableView todoTableView = new TodoTableView();
                                todoTableView.updateTable(response);
                                todoTableView.show();
                            }
                            break;
                        case 1: // 날짜별 조회
                            JDialog calendarDialog = new JDialog(frame, "날짜 선택", true);
                            calendarDialog.setSize(400, 300);
                            calendarDialog.setLayout(new BorderLayout());

                            CalendarPanel calendarPanel = new CalendarPanel(date -> {
                                try {
                                    out.writeInt(9); // 날짜별 조회 요청 코드
                                    out.writeUTF(loginUser);
                                    out.writeUTF(date);
                                    out.flush();
                                    String response = in.readUTF();
                                    SwingUtilities.invokeLater(() -> {
                                        TodoTableView todoTableView = new TodoTableView();
                                        todoTableView.updateTable(response);
                                        todoTableView.show();
                                    });

                                } catch (IOException ex) {
                                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.",
                                            "연결 오류",
                                            JOptionPane.ERROR_MESSAGE);
                                    ex.printStackTrace();
                                }
                                calendarDialog.dispose();
                            });

                            calendarDialog.add(calendarPanel, BorderLayout.CENTER);
                            calendarDialog.setVisible(true);
                            break;

                        case 2: // 유저별 할일 조회
                            String status = JOptionPane.showInputDialog(frame,
                                    "조회할 완료여부를 입력하세요 (1: 완료, 0: 미완료):");
                            if (status != null && !status.trim().isEmpty()) {
                                try {

                                    out.writeInt(12); // 완료여부별 조회 코드
                                    out.writeUTF(loginUser);
                                    out.writeUTF(status);
                                    out.flush();
                                    String response = in.readUTF();
                                    TodoTableView todoTableView = new TodoTableView();
                                    todoTableView.selectupdateTable(response);
                                } catch (EOFException ex) {
                                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.",
                                            "연결 오류", JOptionPane.ERROR_MESSAGE);
                                    ex.printStackTrace();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }

                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            // 삭제 버튼 클릭 이벤트
            deleteButton.addActionListener(e ->

            {
                if (loginUser == null) {
                    JOptionPane.showMessageDialog(frame, "로그인 해주세요.", "알림",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    out.writeInt(14); // 할 일 조회 요청 코드
                    out.writeUTF(loginUser);
                    out.flush();

                    // 서버로부터 데이터 읽기
                    String response = in.readUTF();

                    // 테이블 초기화
                    model.setRowCount(0);

                    // 응답을 줄 단위로 나누기
                    String[] lines = response.split("\n");

                    // 데이터가 비어 있지 않은 경우에만 처리
                    if (lines.length > 0) {
                        for (String line : lines) {
                            // 각 줄을 쉼표로 나누기
                            String[] columns = line.split(",");

                            // 데이터가 유효한 경우에만 추가
                            if (columns.length == 5) {
                                String title = columns[0];
                                String name = columns[1];
                                String status = columns[2];
                                String todoCreatedAt = columns[3];
                                String importance = columns[4];

                                // 테이블에 행 추가
                                model.addRow(new Object[]{title, name, status, todoCreatedAt,
                                        importance});
                            }
                        }
                    }

                    // 테이블을 다이얼로그로 보여주는 패널 생성
                    JPanel dialogPanel = new JPanel(new BorderLayout());
                    JScrollPane tableScrollPane = new JScrollPane(jTable);
                    dialogPanel.add(tableScrollPane, BorderLayout.CENTER);

                    // 다이얼로그에서 OK 버튼 클릭 시 이벤트 처리
                    int option = JOptionPane.showConfirmDialog(frame, dialogPanel, "할 일 목록",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if (option == JOptionPane.OK_OPTION) {
                        // OK 버튼 클릭 시 삭제할 제목 입력받기
                        String searchTitle = JOptionPane.showInputDialog(frame, "삭제할 제목을 입력하세요:");

                        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                            boolean found = false;

                            for (int i = 0; i < model.getRowCount(); i++) {
                                String title = (String) model.getValueAt(i, 0);
                                if (title.equalsIgnoreCase(searchTitle)) {
                                    try {
                                        // 서버에 삭제 요청
                                        out.writeInt(6); // 삭제 요청 코드
                                        out.writeUTF(loginUser);
                                        out.writeUTF(title);
                                        out.flush();

                                        // 서버 응답을 받음
                                        String responseDelete = in.readUTF();

                                        if (responseDelete.startsWith("게시글이 삭제 되지 않았습니다.")) {
                                            JOptionPane.showMessageDialog(frame, "삭제 실패: " + title,
                                                    "오류", JOptionPane.ERROR_MESSAGE);
                                        } else {
                                            model.removeRow(i);
                                            resultsArea.append(responseDelete + "\n");
                                            JOptionPane.showMessageDialog(frame, "삭제 성공: " + title,
                                                    "성공", JOptionPane.INFORMATION_MESSAGE);
                                            found = true;

                                            break;
                                        }
                                    } catch (EOFException ex) {
                                        JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.",
                                                "연결 오류", JOptionPane.ERROR_MESSAGE);
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }

                            if (!found) {
                                JOptionPane.showMessageDialog(frame,
                                        "제목을 찾을 수 없습니다: " + searchTitle, "오류",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

// ---------------------------------------------------주간목표 생성
            weeklycreateButton.addActionListener(e ->

            {
                if (loginUser == null) {
                    JOptionPane.showMessageDialog(frame, "로그인 해주세요.", "알림",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    JPanel createPanel = new JPanel(new GridLayout(2, 2));
                    JLabel contentlabel = new JLabel("제목:");
                    JTextField contentField = new JTextField();

                    createPanel.add(contentlabel);
                    createPanel.add(contentField);

                    int result = JOptionPane.showConfirmDialog(frame, createPanel, "주간 목표 생성",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        String content = contentField.getText();

                        if (content.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "모든 필드를 입력해 주세요.", "알림",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        // 서버에 주간 목표 생성 요청
                        out.writeInt(10); // 주간 목표 생성 코드
                        out.writeUTF(loginUser);
                        out.writeUTF(content);
                        out.flush();

                        String response = in.readUTF();
                        if (!response.startsWith("주간목표 생성 실패")) {
                            JOptionPane.showMessageDialog(frame, "생성 성공: " + content, "성공",
                                    JOptionPane.INFORMATION_MESSAGE);

                            // 서버에 전체 조회 요청
                            out.writeInt(15); // 전체 조회 코드
                            out.writeUTF(loginUser);
                            out.flush();

                            // 서버로부터 전체 주간 목표 조회 결과 받기
                            String allGoalsResponse = in.readUTF();

                            // 테이블 모델 초기화
                            weeklymodel.setRowCount(0);

                            // 응답을 줄 단위로 나누기
                            String[] lines = allGoalsResponse.split("\n");
                            if (lines.length > 0) {
                                for (String line : lines) {
                                    String[] columns = line.split(",");

                                    if (columns.length == 3) {
                                        String weeklyContent = columns[0];
                                        String status = columns[1];
                                        String createdDate = columns[2];

                                        // 데이터 유효성 검사
                                        if (weeklyContent != null && status != null
                                                && createdDate != null) {
                                            weeklymodel.addRow(new Object[]{weeklyContent, status,
                                                    createdDate});
                                        } else {
                                            System.err.println("Invalid data: " + line);
                                        }
                                    } else {
                                        System.err.println(
                                                "Unexpected number of columns: " + columns.length);
                                    }
                                }
                            }

                            // 테이블을 다이얼로그로 보여주는 패널 생성
                            JPanel dialogPanel = new JPanel(new BorderLayout());
                            JScrollPane tableScrollPane = new JScrollPane(weeklyjTable);
                            dialogPanel.add(tableScrollPane, BorderLayout.CENTER);

                            // 다이얼로그로 테이블 표시
                            JOptionPane.showMessageDialog(frame, dialogPanel, "주간 목표 목록",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

// ---------------------------------------------------주간목표 수정
            updateStatusButton.addActionListener(e ->

            {
                if (loginUser == null) {
                    JOptionPane.showMessageDialog(frame, "로그인 해주세요.", "알림",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 서버에서 주간 목표 리스트 요청
                try {
                    out.writeInt(19);
                    out.writeUTF(loginUser);
                    out.flush();

                    // 서버로부터 데이터 읽기
                    String response = in.readUTF();

                    // 테이블 초기화
                    model.setRowCount(0);

                    // 응답을 줄 단위로 나누기
                    String[] lines = response.split("\n");

                    // 데이터가 비어 있지 않은 경우에만 처리
                    if (lines.length > 0) {
                        for (String line : lines) {
                            // 각 줄을 쉼표로 나누기
                            String[] columns = line.split(",");

                            // 데이터가 유효한 경우에만 추가
                            if (columns.length == 3) {
                                String content = columns[0];
                                String status = columns[1];
                                String createdAt = columns[2];

                                // 테이블에 행 추가
                                model.addRow(new Object[]{content, status, createdAt});
                            }
                        }
                    }

                    // 테이블을 다이얼로그로 보여주는 패널 생성
                    JPanel dialogPanel = new JPanel(new BorderLayout());
                    JScrollPane tableScrollPane = new JScrollPane(jTable);
                    dialogPanel.add(tableScrollPane, BorderLayout.CENTER);

                    // 다이얼로그에서 OK 버튼 클릭 시 이벤트 처리
                    int option = JOptionPane.showConfirmDialog(frame, dialogPanel, "주간 목록",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

                    if (option == JOptionPane.OK_OPTION) {
                        String weeklyTitle = JOptionPane.showInputDialog(frame,
                                "완료한 주간 목표 제목을 입력하세요:");
                        if (weeklyTitle != null && !weeklyTitle.trim().isEmpty()) {
                            boolean found = false;

                            for (int i = 0; i < model.getRowCount(); i++) {
                                String title = (String) model.getValueAt(i, 0);
                                if (title.equalsIgnoreCase(weeklyTitle)) {
                                    try {
                                        // 서버에 수정 요청
                                        out.writeInt(18);
                                        out.writeUTF(title);
                                        out.writeUTF(loginUser);
                                        out.flush();

                                        // 서버 응답 받음
                                        String responseUpdateStatus = in.readUTF();

                                        if (!responseUpdateStatus.startsWith("주간 목표가 업데이트 되지 않았습니다.")) {
                                            JOptionPane.showMessageDialog(frame, "수정 성공: " + title,
                                                    "성공", JOptionPane.INFORMATION_MESSAGE);
                                            found = true;

                                            // 서버에 전제 조회 요청
                                            out.writeInt(19); // 전체 조회 코드
                                            out.writeUTF(loginUser);
                                            out.flush();

                                            // 서버로부터 전체 주간 목표 조회 결과 받기
                                            String allGoalsResponse = in.readUTF();

                                            // 테이블 모델 초기화
                                            model.setRowCount(0);

                                            String[] lines_1 = allGoalsResponse.split("\n");
                                            if (lines_1.length > 0) {
                                                for (String line : lines_1) {
                                                    String[] columns = line.split(",");

                                                    if (columns.length == 3) {
                                                        String weeklyContent = columns[0];
                                                        String status = columns[1];
                                                        String createdDate = columns[2];

                                                        // 데이터 유효성 검사
                                                        if (weeklyContent != null && status != null
                                                                && createdDate != null) {
                                                            model.addRow(
                                                                    new Object[]{weeklyContent, status,
                                                                            createdDate});
                                                        } else {
                                                            System.err.println("Invalid data: " + line);
                                                        }
                                                    } else {
                                                        System.err.println(
                                                                "Unexpected number of columns: "
                                                                        + columns.length);
                                                    }
                                                }
                                            }

//                                            // 테이블을 다이얼로그로 보여주는 패널 생성
                                            JPanel dialogPanel_1 = new JPanel(new BorderLayout());
                                            JScrollPane tableScrollPane_1 = new JScrollPane(jTable);
                                            dialogPanel_1.add(tableScrollPane_1, BorderLayout.CENTER);

                                            // 다이얼로그로 테이블 표시
                                            JOptionPane.showMessageDialog(frame, dialogPanel_1,
                                                    "주간 목표 목록", JOptionPane.INFORMATION_MESSAGE);
                                        }

                                    } catch (EOFException ex) {
                                        JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.",
                                                "연결 오류", JOptionPane.ERROR_MESSAGE);
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                            if (!found) {
                                JOptionPane.showMessageDialog(frame, "제목을 찾을 수 없습니다. " + weeklyTitle, "오류",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }

                    }
                } catch (EOFException ex) {
                    JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
//----------------------------------------------------주간목표 조회
            selectWeeklyButton.addActionListener(e ->

            {
                if (loginUser == null) {
                    JOptionPane.showMessageDialog(frame, "로그인 해주세요.", "알림",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String dateWeekly = JOptionPane.showInputDialog(frame, "주간 조회할 날짜 입력하세요:");
                if (dateWeekly != null && !dateWeekly.trim().isEmpty()) {
                    try {
                        out.writeInt(11); // 주간 전체 조회 요청 코드
                        out.writeUTF(loginUser); // 사용자 이름
                        out.writeUTF(dateWeekly); // 조회할 날짜
                        out.flush();

                        // 서버로부터 데이터 읽기
                        String response = in.readUTF(); // 주간 목표 데이터
                        String achievementRate = in.readUTF(); // 달성률

                        // 테이블 모델을 초기화
                        weeklymodel.setRowCount(0);

                        // 응답을 줄 단위로 나누기
                        String[] lines = response.split("\n");
                        if (lines.length > 0) {
                            for (String line : lines) {
                                String[] columns = line.split(",");

                                if (columns.length == 3) {
                                    String content = columns[0];
                                    String status = columns[1];
                                    String createdDate = columns[2];

                                    // 데이터 유효성 검사
                                    if (content != null && status != null && createdDate != null) {
                                        weeklymodel.addRow(
                                                new Object[]{content, status, createdDate});
                                    } else {
                                        System.err.println("Invalid data: " + line);
                                    }
                                } else {
                                    System.err.println(
                                            "Unexpected number of columns: " + columns.length);
                                    System.err.println("Data: " + line);
                                }
                            }
                        }

                        // 테이블을 다이얼로그로 보여주는 패널 생성
                        JPanel dialogPanel = new JPanel(new BorderLayout());
                        JScrollPane tableScrollPane = new JScrollPane(weeklyjTable);
                        dialogPanel.add(tableScrollPane, BorderLayout.CENTER);

                        JOptionPane.showMessageDialog(frame, dialogPanel, "주간 목표 목록",
                                JOptionPane.INFORMATION_MESSAGE);

                        // 달성률 알림창
                        JOptionPane.showMessageDialog(frame, achievementRate, "달성률 정보",
                                JOptionPane.INFORMATION_MESSAGE);

                    } catch (EOFException ex) {
                        JOptionPane.showMessageDialog(frame, "서버와의 연결이 끊어졌습니다.", "연결 오류",
                                JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            weeklyButton.addActionListener(e -> cardLayout.show(mainPanel, "weekly"));
            weeklybackbtn.addActionListener(e -> cardLayout.show(mainPanel, "todo"));

            // 종료 후 소켓 닫기
            Runtime.getRuntime().

                    addShutdownHook(new Thread(() ->

                    {
                        try {
                            if (in != null) {
                                in.close();
                            }
                            if (out != null) {
                                out.close();
                            }
                            if (socket != null) {
                                socket.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }));

        } catch (
                IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "서버 연결에 실패했습니다. 서버가 실행 중인지 확인하세요.", "연결 실패",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}