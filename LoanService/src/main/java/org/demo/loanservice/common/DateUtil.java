package org.demo.loanservice.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
//    public static final String YYYY_MM_DD_HYPHEN="yyyy-MM-dd";
//    public static final String FULL_DATE="HH:mm dd/MM/yyyy";
//    public static final String DD_MM_YYYY_SLASH="dd/MM/yyyy";
    public static final String DD_MM_YYY_HH_MM_SLASH="dd/MM/yyyy HH:mm";
    public static final String YYYY_MM_DD_HH_MM_SS="yyyy/MM/dd HH:mm:ss";
    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

    public static String format(String format, Date date){
        try{
            SimpleDateFormat sdf=new SimpleDateFormat(format);
            return sdf.format(date);
        }catch (Exception ex){
            log.error(ex.getMessage());
            return "";
        }
    }
    public static String format(String format, LocalDateTime date){
        try{
            SimpleDateFormat sdf=new SimpleDateFormat(format);
            return sdf.format( Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
        }catch (Exception ex){
            log.error(ex.getMessage());
            return "";
        }
    }
    public static Timestamp getCurrentTimeUTC7(){
        ZonedDateTime vietnamTime = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        return Timestamp.from(vietnamTime.toInstant());
    }
    public static Date getDateOfAfterNMonth(int n){
        LocalDate currentDate = LocalDate.now();
        LocalDate dateAfter3Months = currentDate.plusMonths(n);
        return Date.from(dateAfter3Months.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    public static Timestamp convertStringToTimeStamp(String timeString){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timeString, formatter);
        return  Timestamp.from(zonedDateTime.toInstant());
    }
}
