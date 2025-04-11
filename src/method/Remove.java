package method;

import java.util.ArrayList;

public class Remove {
	@SuppressWarnings("unchecked")
	public static <T> T[] removeDuplicate(T[] array) {
		ArrayList<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length; i++) {
			if (!list.contains(array[i])) {
				list.add(array[i]);
			}
		} 
		return (T[]) list.toArray((T[]) new Object[0]);
	} 

	// 波纹删除所有null元素
	@SuppressWarnings("unchecked")
	public static <T> T[] removeNull(T[] array) {
		ArrayList<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				list.add(array[i]);
			}
		}
		return (T[]) list.toArray((T[]) new Object[0]);
	}

	// 删除掉所有null的元素以及重复元素
	@SuppressWarnings("unchecked")
	public static <T> T[] removeUseless(T[] array) {
		ArrayList<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null && !list.contains(array[i])) {
				list.add(array[i]);
			}
		}
		return list.toArray((T[]) new String[list.size()]);// Object永远都转换错误，只能如此
	}
}
