package db.mysql;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateId {
	private static int RANDOM = 0;
	
	public static String generate(String username) {
		Date date = new Date();
		SimpleDateFormat mf = new SimpleDateFormat("yyyyMMddhhmmss");
		String createTime = mf.format(date);
		String id = createTime + String.format("%05d", RANDOM++);
		if (RANDOM >= 100) {
			RANDOM = 0;
		}
		String userId = username + id;
		return userId;
	}
	
	public static String generateId() {
		Date date = new Date();
		SimpleDateFormat mf = new SimpleDateFormat("yyyyMMddhhmmss");
		String createTime = mf.format(date);
		String id = createTime + String.format("%05d", RANDOM++);
		if (RANDOM >= 100) {
			RANDOM = 0;
		}
		
		return id;
	}
	
	public static void main(String[] args) {
		System.out.println("neko: " + GenerateId.generate("neko"));
		System.out.println("inu: " + GenerateId.generate("inu"));
		System.out.println("usagi: " + GenerateId.generate("usagi"));
		System.out.println("sakana: " + GenerateId.generate("sakana"));
		System.out.println("hito: " + GenerateId.generate("hito"));
		for(int i = 0; i < 6; i ++) {
			System.out.println("id" + i + GenerateId.generateId());
		}
	}
}
