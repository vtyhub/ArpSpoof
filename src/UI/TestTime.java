package UI;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class TestTime {
	public static void main(String[] args) {
		Instant now = Instant.now();
		System.out.println(now);
		
		LocalDate now3 = LocalDate.now();
		System.out.println(now3);
		
		LocalTime now4 = LocalTime.now();
		System.out.println(now4);
		
		LocalDateTime now2 = LocalDateTime.now();
		System.out.println(now2);
		
		ArrayList<Object> list = new ArrayList<>();
		System.out.println(list.size());
		
	}
}
