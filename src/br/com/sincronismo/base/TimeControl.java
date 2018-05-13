package br.com.sincronismo.base;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeControl {

	public static String getFormat(Date date){
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return fmt.format(date);
	}

	public static Date getNow() {
		RandomTime randomTime = new RandomTime();
		LocalDateTime ldt = LocalDateTime.of(randomTime.year(),
				randomTime.month(), randomTime.day(), randomTime.hour(),
				randomTime.minute(), randomTime.second());
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}
}