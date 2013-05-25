/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.biemond.smartmeter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author edwin
 */
public class DateParam {
  private final Date date;

  public DateParam(String dateStr) throws WebApplicationException {
    if ( dateStr == null || "".equals(dateStr)) {
      this.date = null;
      return;
    }
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    try {
      this.date = dateFormat.parse(dateStr);
    } catch (ParseException e) {
      throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
        .entity("Couldn't parse date string: " + e.getMessage())
        .build());
    }
  }

  public Date getDate() {
    return date;
  }
}
