package nextstep.study.di.stage3.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import org.reflections.Reflections;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContext {

    private final Set<Object> beans;
    private final Reflections reflections;

    public DIContext(final String path, final Set<Class<?>> classes) {
        beans = new HashSet<>();
        reflections = new Reflections(path);
        try {
            initInternal(classes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initInternal(final Set<Class<?>> classes) throws Exception {
        final Set<Class<?>> peanutTypes = classes;
        for (final Class<?> peanutType : peanutTypes) {
            final Object peanutInstance = dfs(peanutType);
            beans.add(peanutInstance);
        }
    }

    private Object dfs(final Class<?> peanut) throws Exception {
        /** 이미 있는 경우 */
        if (isAlreadyExistPeanut(peanut)) {
            return getBean(peanut);
        }
        /** 인터페이스인 경우 */
        if (peanut.isInterface()) {
            return createConcreteBean(peanut);
        }
        /** 기본 생성자가 있는 경우 */
        final Constructor<?> defaultConstructor = getDefaultConstructor(peanut);
        if (hasDefaultConstructor(peanut)) {
            final Object newInstance = defaultConstructor.newInstance();
            beans.add(newInstance);
            return newInstance;
        }
        /** 여러 args인 경우 */
        final Constructor<?>[] constructors = peanut.getDeclaredConstructors();
        validateConstructorIsUnique(constructors);
        final Constructor<?> constructor = constructors[0];
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] parameterInstances = findAndCacheParameterInstances(parameterTypes);
        return constructor.newInstance(parameterInstances);
    }

    private Object createConcreteBean(final Class<?> peanut)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Class<?> subType = reflections.getSubTypesOf(peanut).toArray(new Class<?>[]{})[0];
        final Object newInstance = subType.getConstructor().newInstance();
        beans.add(newInstance);
        return newInstance;
    }

    private boolean isAlreadyExistPeanut(final Class<?> peanut) {
        return beans.stream()
                .anyMatch(bean -> bean.getClass() == peanut);
    }

    private Constructor<?> getDefaultConstructor(final Class<?> peanut) {
        try {
            return peanut.getConstructor(null);
        } catch (NoSuchMethodException e) {
            return null; // 메서드가 존재하지 않는 경우를 허용해야 함!
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateConstructorIsUnique(final Constructor<?>[] constructors) {
        if (constructors.length > 1) {
            throw new RuntimeException("Peanut은 하나의 생성자만을 가져야 합니다.");
        }
    }

    private Object[] findAndCacheParameterInstances(final Class<?>[] parameterTypes) throws Exception {
        final Object[] parameterInstances = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];
            final Object parameterInstance = dfs(parameterType);
            beans.add(parameterInstance);
            parameterInstances[i] = parameterInstance;
        }
        return parameterInstances;
    }

    private boolean hasDefaultConstructor(final Class<?> type) {
        try {
            type.getConstructor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return (T) beans.stream()
                .filter(bean -> bean.getClass() == aClass)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("bean이 존재하지 않습니다."));
    }
}
