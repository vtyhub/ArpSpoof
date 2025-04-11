package method;

public class ManageStr {
	public static String copy(String str, int count) {
		String resultstr = "";
		for (int i = 0; i < count; i++) {
			resultstr += str;
		}
		return resultstr;
	}

	public static void main(String[] args) {
		String s = "s";
		System.out.println(copy("", 5));
	}
}
