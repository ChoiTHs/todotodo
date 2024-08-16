package view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarPanel extends JPanel {
    private Calendar calendar;
    private JLabel monthLabel;
    private JPanel calendarPanel;
    private DateSelectListener listener;

    public CalendarPanel(DateSelectListener listener) {
        this.listener = listener;
        calendar = Calendar.getInstance();
        setLayout(new BorderLayout());
        monthLabel = new JLabel("", JLabel.CENTER);
        add(monthLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton prevMonthButton = new JButton("Previous");
        JButton nextMonthButton = new JButton("Next");
        buttonPanel.add(prevMonthButton);
        buttonPanel.add(nextMonthButton);
        add(buttonPanel, BorderLayout.SOUTH);

        calendarPanel = new JPanel(new GridLayout(7, 7));
        add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();

        prevMonthButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextMonthButton.addActionListener(e -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
    }

    private void updateCalendar() {
        calendarPanel.removeAll();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        monthLabel.setText(sdf.format(calendar.getTime()));

        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (String day : days) {
            calendarPanel.add(new JLabel(day, JLabel.CENTER));
        }

        int firstDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < firstDay; i++) {
            calendarPanel.add(new JLabel());
        }

        for (int i = 1; i <= daysInMonth; i++) {
            JButton dayButton = new JButton(String.valueOf(i));
            dayButton.addActionListener(e -> {
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(e.getActionCommand()));

                SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dayFormat.format(calendar.getTime());
                listener.dateSelected(formattedDate);
            });
            calendarPanel.add(dayButton);
        }
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    public interface DateSelectListener {
        void dateSelected(String date);
    }

    public static void createCalendarDialog(JFrame frame, DateSelectListener listener) {
        JDialog calendarDialog = new JDialog(frame, "날짜 선택", true);
        calendarDialog.setSize(400, 300);
        calendarDialog.setLayout(new BorderLayout());

        CalendarPanel calendarPanel = new CalendarPanel(date -> {
            listener.dateSelected(date);
            calendarDialog.dispose();
        });

        calendarDialog.add(calendarPanel, BorderLayout.CENTER);
        calendarDialog.setVisible(true);
    }
}