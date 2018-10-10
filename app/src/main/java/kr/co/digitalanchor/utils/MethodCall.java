package kr.co.digitalanchor.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * 객체의 메소드를 호출할 때 값의 전달, 실행을 용이하게 하고 오류 처리를 간소화 하기 위한 클래스.
 */
@SuppressWarnings("unused")
public class MethodCall {

    /**
     * 객체의 메소드를 호출한다.
     *
     * @param target 메소드를 호출할 객체.
     * @param method 메소드 명.
     * @param types  메소드 호출할 때 전달할 파라미터 타입들.
     * @param params 메소드 호출할 때 전달할 파라미터 값들.
     * @return 호출 성공 여부. true 일 경우 성공.
     */
    public static boolean call(Object target, String method, Class<?>[] types, Object[] params) {
        try {
            Method m = target.getClass().getDeclaredMethod(method, types);
            m.setAccessible(true);
            m.invoke(target, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 객체의 메소드를 호출한다.
     *
     * @param target    메소드를 호출할 객체.
     * @param annoClass 메소드에 설정된 {@link Annotation}의 클래스.
     * @param params    메소드 호출할 때 전달할 파라미터 값들.
     * @return 호출 성공 여부. true 일 경우 성공.
     */
    public static boolean call(Object target, Class<? extends Annotation> annoClass,
                               Object[] params) {
        try {
            for (Method method : target.getClass().getDeclaredMethods()) {
                if (method.getAnnotation(annoClass) != null) {
                    method.setAccessible(true);
                    method.invoke(target, params);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
