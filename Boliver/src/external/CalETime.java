package external;

import java.util.Calendar;

public class CalETime {
	public static String calculateETime(String timeTaken) {
		Calendar calendar = Calendar.getInstance(); 
		calendar.add(Calendar.MINUTE,Integer.parseInt(timeTaken)); 
		return calendar.getTime().toString();
		
	}
	
	/*
	public static void main(String[] args) {
		System.out.println(CalETime.calculateETime("5"));
	}
	*/
}
