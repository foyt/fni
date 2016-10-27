package fi.foyt.fni.utils.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtils {
  
  private DateTimeUtils() {
  }

  public static String formatIsoDate(LocalDate localDate) {
    return DateTimeFormatter.ISO_DATE.format(localDate);
  }

  public static String formatIsoLocalDate(OffsetDateTime offsetDateTime) {
    return formatIsoDate(offsetDateTime.toLocalDate());
  }

  public static String formatIsoLocalDate(ZonedDateTime zonedDateTime) {
    return formatIsoDate(zonedDateTime.toLocalDate());
  }

  public static String formatIsoLocalDate(Date date) {
    return formatIsoLocalDate(date, ZoneId.systemDefault());
  }
  
  public static String formatIsoLocalDate(Date date, ZoneId zoneId) {
    Instant instant = Instant.ofEpochMilli(date.getTime());
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
    return formatIsoLocalDate(zonedDateTime);
  }

  public static String formatIsoOffsetDateTime(OffsetDateTime offsetDateTime) {
    return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime);
  }

  public static String formatIsoOffsetDateTime(ZonedDateTime zonedDateTime) {
    return formatIsoOffsetDateTime(zonedDateTime.toOffsetDateTime());
  }

  public static String formatIsoOffsetDateTime(Date time) {
    return formatIsoOffsetDateTime(time, ZoneId.systemDefault());
  }

  public static String formatIsoOffsetDateTime(Date time, ZoneId zoneId) {
    Instant instant = Instant.ofEpochMilli(time.getTime());
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
    return formatIsoOffsetDateTime(zonedDateTime);
  }

  public static OffsetDateTime parseIsoOffsetDateTime(String text) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    return OffsetDateTime.parse(text, formatter);
  }

  public static ZonedDateTime parseIsoZonedDateTime(String text) {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    return ZonedDateTime.parse(text, formatter);
  }

  public static LocalDate parseIsoLocalDate(String text) {
    return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
  }
  
  public static Date toDate(ZonedDateTime zonedDateTime) {
    return Date.from(zonedDateTime.toInstant());
  }
  
  public static Date toDate(OffsetDateTime offsetDateTime) {
    return Date.from(offsetDateTime.toInstant());
  }
  
  public static Date toDate(LocalDate localDate, ZoneId zoneId) {
    return Date.from(localDate.atStartOfDay(zoneId).toInstant());
  }
  
  public static Date toDate(LocalDate localDate) {
    return toDate(localDate, ZoneId.systemDefault());
  }

  public static OffsetDateTime toOffsetDateTime(Date date) {
    return toOffsetDateTime(date, ZoneId.systemDefault());
  }
  
  public static OffsetDateTime toOffsetDateTime(Date date, ZoneId zoneId) {
    return OffsetDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), zoneId);
  }
}
