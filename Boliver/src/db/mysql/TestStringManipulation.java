package db.mysql;

public class TestStringManipulation {
	public static String solve(String curLocation) {
		char[] array = curLocation.toCharArray();
		
		for(int i = 0; i < array.length; i++) {
			if(array[i] == ' ') {
				array[i] = '+';
			}
		}
		
		return new String(array, 0, array.length);
	}
	public static void main(String[] args) {
		System.out.println(TestStringManipulation.solve("3369 Mission St, San Francisco, CA 94110"));
	}
}
