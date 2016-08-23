package fi.foyt.fni.rest;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateTimeParameter implements Serializable {
  
  private static final long serialVersionUID = 1L;

  public DateTimeParameter(String dateString) {
    TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_DATE_TIME.parse(dateString);
    if (temporalAccessor != null) {
      dateTime = OffsetDateTime.from(temporalAccessor);
    } else {
      dateTime = null;
    }
  }

  public OffsetDateTime getDateTime() {
    return dateTime;
  }

  private final OffsetDateTime dateTime;
}