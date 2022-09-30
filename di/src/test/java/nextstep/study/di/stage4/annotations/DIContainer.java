package nextstep.study.di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContext {

    private final Map<Class<?>, Object> peanuts;
    private static Reflections reflections;

    public DIContext(final Set<Class<?>> classes) {
        peanuts = new HashMap();
        try {
            initInternal(classes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initInternal(final Set<Class<?>> classes) throws Exception {
        final Set<Class<?>> peanutTypes = classes;
        for (final Class<?> peanutType : peanutTypes) {
            if(isNotCreateCase(peanutType)) {
                continue;
            }
            final PeanutInnerDto peanutInnerDto = dfs(peanutType);
            final Class<?> type = peanutInnerDto.type;
            final Object newInstance = peanutInnerDto.newInstance;
            peanuts.putIfAbsent(type, newInstance);
        }
    }

    private boolean isNotCreateCase(final Class<?> type) {
        /**
         * 어노테이션이거나
         * 인터페이스가 존재한다면(#1)
         * 처리하지 않는다
         *
         * #1의 경우 인터페이스에 따른 생성방식을 따로 처리하고 있는 로직이 있기 때문에 이곳에서 처리하지 않는다
         */
        return type.isAnnotation() && type.getInterfaces().length > 0;
    }

    private PeanutInnerDto dfs(final Class<?> type) throws Exception {
        /** 이미 있는 경우 */
        if (isAlreadyExistPeanut(type)) {
            return new PeanutInnerDto(peanuts.get(type));
        }

        /** 인터페이스인 경우 */
        if (type.isInterface()) {
            return createConcretePeanut(type);
        }

        /** 기본 생성자가 있는 경우 -> 근데 이 경우에 대해서도 인터페이스가 존재하는지가 엇갈린다 ! */
        final Constructor<?> defaultConstructor = getDefaultConstructor(type);
        if (hasDefaultConstructor(type)) {
            final Object newInstance = defaultConstructor.newInstance();
            return createInstanceByDefaultConstructor(newInstance);
        }

        /** 필드 주입의 경우 */
        if (isFieldInjection(type)) {
            final Constructor<?> hiddenDefaultConstructor = getHiddenDefaultConstructor(type);
            final Object newInstance = hiddenDefaultConstructor.newInstance();
            final Field[] fields = type.getDeclaredFields();
            for (final Field field : fields) {
                if (field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    field.set(newInstance, dfs(field.getType()).newInstance);
                }
            }
            return new PeanutInnerDto(newInstance);
        }

        /** 여러 args인 경우 */
        final Constructor<?>[] constructors = type.getDeclaredConstructors();
        validateConstructorIsUnique(constructors);
        final Constructor<?> constructor = constructors[0];
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] parameterInstances = findAndCacheParameterInstances(parameterTypes);
        final Object newInstance = constructor.newInstance(parameterInstances);
        return new PeanutInnerDto(newInstance);
    }

    private Constructor<?> getHiddenDefaultConstructor(final Class<?> type) throws NoSuchMethodException {
        final Constructor<?> constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor;
    }

    private PeanutInnerDto createInstanceByDefaultConstructor(final Object newInstance) {
        final Class<?>[] interfaces = newInstance.getClass().getInterfaces();
        if (interfaces.length > 0) {
            return new PeanutInnerDto(interfaces[0], newInstance);
        }
        return new PeanutInnerDto(newInstance);
    }

    /**
     * 1. public으로 된 기본 생성자가 없다
     * 2. Inject로 된 필드가 하나라도 있다
     */
    private boolean isFieldInjection(final Class<?> peanut) {
        return !existDefaultConstructor(peanut) && existInjectAnnotationAtField(peanut);
    }

    public static DIContext createContextForPackage(final String rootPackageName) {
        reflections = new Reflections(rootPackageName);
        final Set<Class<?>> peanuts = findPeanutAnnotatedTypes(reflections);
        return new DIContext(peanuts);
    }

    private static Set<Class<?>> findPeanutAnnotatedTypes(final Reflections reflections) {
        final Set<Class<?>> peanuts = reflections.getTypesAnnotatedWith(ImPeanut.class);
        peanuts.addAll(reflections.getTypesAnnotatedWith(Controller.class));
        peanuts.addAll(reflections.getTypesAnnotatedWith(Service.class));
        peanuts.addAll(reflections.getTypesAnnotatedWith(Repository.class));
        return peanuts;
    }

    private PeanutInnerDto createConcretePeanut(final Class<?> interfaceType)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        final Class<?> concreteClassType = reflections.getSubTypesOf(interfaceType).toArray(new Class<?>[]{})[0];
        final Object newInstance = concreteClassType.getConstructor().newInstance();
        return new PeanutInnerDto(interfaceType, newInstance);
    }

    private boolean isAlreadyExistPeanut(final Class<?> type) {
        return this.peanuts.get(type) != null;
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
            peanuts.putIfAbsent(parameterType, parameterInstance);
            parameterInstances[i] = parameterInstance;
        }
        return parameterInstances;
    }

    private boolean hasDefaultConstructor(final Class<?> type) {
        return existDefaultConstructor(type) && !existInjectAnnotationAtField(type);
    }

    private boolean existDefaultConstructor(final Class<?> type) {
        try {
            type.getConstructor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean existInjectAnnotationAtField(final Class<?> type) {
        return Arrays.stream(type.getDeclaredFields())
                .anyMatch(field -> field.isAnnotationPresent(Inject.class));
    }

    @SuppressWarnings("unchecked")
    public <T> T getPeanut(final Class<T> aClass) {
        final T peanut = (T) peanuts.get(aClass);
        if (peanut == null) {
            throw new IllegalArgumentException("bean이 존재하지 않습니다.");
        }
        return peanut;
    }

    /**
     * 내부에서 사용하기 위한 Peanut DTO
     * 인터페이스와 구체클래스의 관계 처럼
     * 실제로 생성된 인스턴스와 인터페이스의 타입이 다르기 때문에 생성한 DTO
     */
    private static class PeanutInnerDto {
        public Class<?> type;
        public Object newInstance;

        /**
         * 대개의 경우 아래 생성자로 해결이 됩니다
         */
        public PeanutInnerDto(final Object newInstance) {
            this.type = newInstance.getClass();
            this.newInstance = newInstance;
        }

        public PeanutInnerDto(final Class<?> type, final Object newInstance) {
            this.type = type;
            this.newInstance = newInstance;
        }
    }
}
