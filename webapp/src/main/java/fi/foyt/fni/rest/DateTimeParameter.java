package fi.foyt.fni.rest;

import java.io.Serializable;
import java.time.OffsetDateTime;

import fi.foyt.fni.utils.time.DateTimeUtils;

public class DateTimeParameter implements Serializable {
  
  private static final long serialVersionUID = 1L;

  public DateTimeParameter(String dateString) {
    dateTime = DateTimeUtils.parseIsoOffsetDateTime(dateString);
  }

  public OffsetDateTime getDateTime() {
    return dateTime;
  }

  private final OffsetDateTime dateTime;
}