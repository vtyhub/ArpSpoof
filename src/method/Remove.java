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

	// ����ɾ������nullԪ��
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

	// ɾ��������null��Ԫ���Լ��ظ�Ԫ��
	@SuppressWarnings("unchecked")
	public static <T> T[] removeUseless(T[] array) {
		ArrayList<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null && !list.contains(array[i])) {
				list.add(array[i]);
			}
		}
		return list.toArray((T[]) new String[list.size()]);// Object��Զ��ת������ֻ�����
	}
}
