package date_utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    // 날짜를 'yy/MM/dd' 형식으로 포맷팅
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        return sdf.format(date);
    }

    // 오늘 날짜와 주어진 날짜 간의 차이를 D-형식으로 반환
    public static String getDaysRemaining(Date date) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = date.toLocalDate();
        long daysRemaining = ChronoUnit.DAYS.between(today, targetDate);
        return "D-" + daysRemaining;
    }

    public static boolean isValidDate(String inputDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(inputDate);
        } catch (ParseException e) {
            return false;
        }

        // 날짜 형식이 유효한 경우 오늘 날짜와 비교
        try {
            String todayFm = new SimpleDateFormat("MM-dd").format(new Date(System.currentTimeMillis()));

            Date date = new Date(sdf.parse(inputDate).getTime());
            Date today = new Date(sdf.parse(todayFm).getTime());

            // 날짜 비교
            return !date.before(today);
        } catch (ParseException e) {
            return false;
        }
    }
}