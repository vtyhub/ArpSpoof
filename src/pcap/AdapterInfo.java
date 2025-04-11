package pcap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import method.ManageStr;

public class AdapterInfo {

	static {
		setList();
	}

	private static ArrayList<String> list;

	public static final String GATEWAY = "默认网关";
	public static final String DHCP = "DHCP";// 空格无法被contains方法解析？//DHCP启用状态在物理地址下面一两条，服务器地址在下面更多
	public static final String DNS = "DNS";
	public static final String MEDIASTATE = "媒体状态";

	public static final int UPMODE = 0;
	public static final int DOWNMODE = 1;

	public static final int MEDIASTATEACCURACY = 5;// 媒体状态在物理地址上方，太多容易搜索到上一条适配器的信息
	public static final int DEFAULTACCURACY = 10;
	public static final int DNSACCURACY = 15;// DNS在适配器信息最下方左右的位置，10条有时候搜索不到,且物理地址上方有时会有 连接特定的DNS后缀 这个字段，必须从下开始

	// Rule
	public static final String IPv4REGEXsimple = "^\\d{1,3}\\.\\d{1,3\\.\\d{1,3}\\.\\d{1,3}$";
	public static final String IPv4REGEXchinz = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
	public static final String IPv4REGEXjb = "^(?:(?:1[0-9][0-9]\\.)|(?:2[0-4][0-9]\\.)|(?:25[0-5]\\.)|(?:[1-9][0-9]\\.)|(?:[0-9]\\.)){3}(?:(?:1[0-9][0-9])|(?:2[0-4][0-9])|(?:25[0-5])|(?:[1-9][0-9])|(?:[0-9]))$";
	public static final String IPv6REGEX1 = "^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$";
	public static final String IPv6REGEX2 = "^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|::([\\da−fA−F]1,4:)0,4((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)2:([\\da−fA−F]1,4:)0,2((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|([\\da−fA−F]1,4:)4:((25[0−5]|2[0−4]\\d|[01]?\\d\\d?)\\.)3(25[0−5]|2[0−4]\\d|[01]?\\d\\d?)|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}|:((:[\\da−fA−F]1,4)1,6|:)|:((:[\\da−fA−F]1,4)1,6|:)|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|([\\da−fA−F]1,4:)2((:[\\da−fA−F]1,4)1,4|:)|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|([\\da−fA−F]1,4:)4((:[\\da−fA−F]1,4)1,2|:)|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?|([\\da−fA−F]1,4:)6:";
	public static final String MACREGEX = "^[\\da-fA-F]{2}-[\\da-fA-F]{2}-[\\da-fA-F]{2}-[\\da-fA-F]{2}-[\\da-fA-F]{2}-[\\da-fA-F]{2}$";
	public static final String GETMEDIASTATE = "媒体已断开";
	public static final String WINDEFAULTCHARSET = "GBK";

	// 自动配置
	public static final String DHCPSTATE1 = "DHCP";
	public static final String DHCPSTATE2 = "已启用";
	public static final int AUTOCONFIGSCCURACY = 3;// 大多数时候在物理地址下面一条
	public static final String AUTOCONFIGRULE = "是";
	public static final String IPv6AUTOCONFIG = "自动配置已启用";

	public static final String MAC = "74-e5-0b-3a-72-56";
	public static final String MAC2 = "00-50-56-C0-00-01";

	public static final String DEFAULTNEXT = " ";
	public static final int DEFAULTSTART = 0;
	public static final int DEFAULTEND = 5;

	public static String[] getInfo(List<String> list, String MAC, String key, String[] rule, int accuracy, int mode,
			String nextline, int nextlinestart, int nextlineend) {
		final String upperMAC = MAC.toUpperCase();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).contains(upperMAC)) {
				// 存在一行中有该MAC地址
				// System.out.println("MAC discover " + List.get(i));
				int j = 0;
				int border = 0;
				switch (mode) {
				case UPMODE:
					j = i - accuracy;// 从物理地址上面ACCURACY开始搜索直到物理地址为止
					border = i;
					break;
				case DOWNMODE:
					j = i;// 从物理地址往下开始搜索ACCURACY行
					border = i + accuracy;
					break;
				default:// 从物理地址上面ACCURACY开始搜索到下面ACCURACY行
					j = i - accuracy;
					border = i + accuracy;
				}
				for (; j < border; j++) {
					// 搜索该行上下10行以内是否存在 需要获取的 字段
					if (list.get(j).contains(key)) {
						// 若在行以内存在,将记录下该行，从该行开始向下搜索，若下一行的前nextlinelen字符为空串，则认为该行也是搜索字段的内容，
						// 再用同样方法搜索下一行，直到不满足条件为止，把第一行start和最后一行的索引end取出，把这些行中符合rule的内容存进数组
						// System.out.println(key + " discover " + List.get(j));
						int start = j, end = j;
						for (int z = j + 1; z < list.size(); z++) {
							String zs = list.get(z);
							if (zs != null) {
								int len = zs.length();
								if (ManageStr.copy(nextline, nextlineend - nextlinestart)
										.equals(zs.substring(nextlinestart, len > nextlineend ? nextlineend : len))) {
									end = z;
								} else {
									break;
								}
							}

						}
						// 获取了start和end的索引
						String[] result = new String[end - start + 1];
						for (int x = start; x <= end; x++) {
							String[] split = list.get(x).trim().split(" ");
							for (int k = 0; k < split.length; k++) {
								// 为了能够将v4v6同时筛选出来，使用规则数组，只要满足了规则数组内的任意一条规则，就可以添加进结果
								for (int k2 = 0; k2 < rule.length; k2++) {
									if (split[k].matches(rule[k2])) {
										result[x - start] = split[k];
									}
								}
							}
						}
						return result;
					}
				}
			}
		}
		return new String[0];// 防止空指针
	}

	public static String getMediaStatus(String MAC) {
		String[] info = getInfo(list, MAC, MEDIASTATE, new String[] { GETMEDIASTATE }, MEDIASTATEACCURACY, UPMODE,
				DEFAULTNEXT, DEFAULTSTART, DEFAULTEND);
		for (String string : info) {
			if (GETMEDIASTATE.equals(string)) {
				return "Disconnected";
			} else {
				return "Connected";
			}
		}
		return "Connected";
	}

	/**
	 * IPv4Info
	 * 
	 * @param MAC
	 * @param key
	 * @param accuracy
	 * @param mode
	 * @return
	 */
	public static String[] getIPv4Info(List<String> list, String MAC, String key, int accuracy, int mode,
			String nextline, int nextlinestart, int nextlineend) {
		return getInfo(list, MAC, key, new String[] { IPv4REGEXjb }, accuracy, mode, nextline, nextlinestart,
				nextlineend);
	}

	public static String[] getIPv4DNS(String MAC) {
		final String upperMAC = MAC.toUpperCase();
		String[] dns = new String[2];
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).contains(upperMAC)) {
				// 存在一行中有该MAC地址
				// System.out.println("MAC discover " + List.get(i));
				for (int j = i; j < i + DNSACCURACY; j++) {
					// 搜索该行以下10行以内是否存在DNS字段,因为该行往上有另一个DNS
					if (list.get(j).contains(DNS)) {
						// 若在10行以内存在,将该行分割，取出DNS
						// System.out.println("DNS discover " + List.get(j));
						for (int z = j; z <= j + 1; z++) {
							// 然后将下一行的IP放入第二的元素
							String[] split = list.get(z).trim().split(" ");
							for (int k = 0; k < split.length; k++) {
								// System.out.println(k + " " + split[k]);
								if (split[k].matches(IPv4REGEXjb)) {
									dns[z - j] = split[k];// 第一次是0，第二次是1
								}
							}
						}
						return dns;
					}
				}
			}
		}
		return dns;
	}

	public static String getIPv4Gateway(String MAC) {
		String[] iPv4Info = getIPv4Info(list, MAC, GATEWAY, DEFAULTACCURACY, 3, DEFAULTNEXT, DEFAULTSTART, DEFAULTEND);
		String result = "";

		for (int i = 0; i < iPv4Info.length; i++) {
			if (iPv4Info[i] != null && iPv4Info[i].matches(IPv4REGEXjb))
				result = iPv4Info[i];
		}
		return result;
	}

	public static String getDHCPv4Status(String MAC) {
		String[] info = getInfo(list, MAC, DHCPSTATE1, new String[] { AUTOCONFIGRULE }, AUTOCONFIGSCCURACY, DOWNMODE,
				DEFAULTNEXT, DEFAULTSTART, DEFAULTEND);
		for (String element : info) {
			if (AUTOCONFIGRULE.equals(element)) {
				return "Used";
			} else {
				return "Unused";
			}
		}
		return "Unused";

	}

	public static String getIPv6AutoAddressStatus(String MAC) {
		String[] info = getInfo(list, MAC, IPv6AUTOCONFIG, new String[] { AUTOCONFIGRULE }, AUTOCONFIGSCCURACY,
				DOWNMODE, DEFAULTNEXT, DEFAULTSTART, DEFAULTEND);
		for (String element : info) {
			if (AUTOCONFIGRULE.equals(element)) {
				return "Used";
			} else {
				return "Unused";
			}
		}
		return "Unused";
	}

	public static void setList() {
		list = new ArrayList<String>();
		try {
			Process pro = Runtime.getRuntime().exec("cmd /c ipconfig/all");
			BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream(), WINDEFAULTCHARSET));
			String temp;
			while ((temp = br.readLine()) != null) {
				list.add(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) {
		String iPv4Gateway = getIPv4Gateway(MAC);
		String[] info = getIPv4Info(list, MAC, GATEWAY, DEFAULTACCURACY, 3, " ", DOWNMODE, 5);
		String[] info2 = getInfo(list, MAC, GATEWAY, new String[] { IPv4REGEXjb }, DEFAULTACCURACY, 3, " ", 0, 5);
		System.out.println(iPv4Gateway);
		System.out.println(info[1]);
		System.out.println(info2[1]);
	}
}
