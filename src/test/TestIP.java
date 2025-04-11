package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestIP {

	private static ArrayList<String> rowList;
	
	public static final String DEFAULTGATEWAY = "默认网关";
	public static final int ACCURACY = 10;

	public static String getGateway(String MAC) {
		for (int i = 0; i < rowList.size(); i++) {
			if (rowList.get(i).contains(MAC.toUpperCase())) {
				// 存在一行中有该MAC地址
				for (int j = i; j < i + ACCURACY; i++) {
					// 搜索该行往下10行以内是否存在 默认网关 字段
					if (rowList.get(j).contains(DEFAULTGATEWAY)) {
						// 若在10行以内存在
						String[] split = rowList.get(j).trim().split(" ");
						for (int k = 0; k < split.length; k++) {
							
						}
					}
				}

			}
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		Process pro = Runtime.getRuntime().exec("cmd /c ipconfig/all");
		BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream(), "GBK"));
		rowList = new ArrayList<String>();
		String temp;
		while ((temp = br.readLine()) != null) {
			rowList.add(temp);
		}

		// System.out.println(rowList.size());
		for (int index = 0; index < rowList.size(); index++) {
			System.out.println(rowList.get(index));
		}
		for (String string : rowList) {
			if (string.indexOf("Subnet Mask") != -1) {
				Matcher mc = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").matcher(string);
				if (mc.find()) {
					System.out.println("子掩码：" + mc.group());
				} else {
					System.out.println("子掩码为空");
				}
			}
			;
			if (string.indexOf("Default Gateway") != -1) {
				Matcher mc = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").matcher(string);
				if (mc.find()) {
					System.out.println("默认网关：" + mc.group());
				} else {
					System.out.println("默认网关为空");
				}
				return;
			}

		}
	}
}
