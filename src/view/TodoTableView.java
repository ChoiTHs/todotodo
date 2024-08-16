package view;

import java.awt.BorderLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TodoTableView extends JFrame {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd");

//  	JTable jTable = new JTable(model);
  	
    public TodoTableView() {
        frame = new JFrame("Todo List");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        String[] columnNames = {"제목", "중요도", "상태", "할 일 날짜"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public void updateTable(String response) {
        tableModel.setRowCount(0);

        String[] lines = response.split("\n");
        for (String line : lines) {
            String[] parts = line.split(",", -1);
            if (parts.length == 4) {
                String title = parts[0].trim();
                String importance = parts[1].trim();
                String status = parts[2].trim();
                String todoCreatedAt = parts[3].trim();

                String formattedDate = convertDate(todoCreatedAt);

                tableModel.addRow(new Object[]{title, importance, status, formattedDate});
            }
        }
    }

    private String convertDate(String dateStr) {
        try {
            Date date = inputDateFormat.parse(dateStr);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    public void show() {
        frame.setVisible(true);
    }
    
   
    
  	public  void selectupdateTable(String response) {
  		String deleteSelect[]= {"title","name","status","importance","todocreatedat"};
  	  	tableModel=new DefaultTableModel(deleteSelect,0);
  	  	table = new JTable(tableModel);
        // 테이블 초기화
  	  tableModel.setRowCount(0);

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
                    String importance = columns[3];
                    String todoCreatedAt = columns[4];

                    // 테이블에 행 추가
                    tableModel.addRow(new Object[]{title, name, status, importance,todoCreatedAt});
                }
            }
        }

        // 테이블을 다이얼로그로 보여줍니다.
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(frame, scrollPane, "조회 결과", JOptionPane.INFORMATION_MESSAGE);
    }
  	
  	public  void selectWeeklyupdateTable(String response) {
  		String select[]= {"content","status","createdat"};
  		tableModel=new DefaultTableModel(select,0);
  		table = new JTable(tableModel);
  		// 테이블 초기화
  		tableModel.setRowCount(0);
  		
  		// 응답을 줄 단위로 나누기
  		String[] lines = response.split("\n");
  		
  		// 데이터가 비어 있지 않은 경우에만 처리
  		if (lines.length > 0) {
  			for (String line : lines) {
  				// 각 줄을 쉼표로 나누기
  				String[] columns = line.split(",");
  				
  				// 데이터가 유효한 경우에만 추가
  				if (columns.length == 5) {
  					String content = columns[0];
  					String status = columns[1];
  					String createdate = columns[2];
  					
  					// 테이블에 행 추가
  					tableModel.addRow(new Object[]{content,status,createdate});
  				}
  			}
  		}
  		
  		// 테이블을 다이얼로그로 보여줍니다.
  		JScrollPane scrollPane = new JScrollPane(table);
  		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
  		JOptionPane.showMessageDialog(frame, scrollPane, "조회 결과", JOptionPane.INFORMATION_MESSAGE);
  	}
  	
}
    




