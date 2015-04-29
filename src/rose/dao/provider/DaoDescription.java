package rose.dao.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

public class DaoDescription {

	private Class<?> clazz;
	private Method method;
	private Class<?>[] genericReturnTypes;
	private int parameterCount;
	//private Map<Class<? extends Annotation>, Annotation[]> parameterAnnotations = new HashMap<Class<? extends Annotation>, Annotation[]>();
	private HashMap<Method, Annotation[]> parameterAnnotations = new HashMap<Method, Annotation[]>();
	private Object[] args;
	
	
	public DaoDescription(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public DaoDescription(Method method) {
		this.method = method;
	}

	public DaoDescription(Class<?> clazz, Method method) {
		this.clazz = clazz;
		this.method = method;
		initDescription();

	}

	public void initDescription() {
		if (this.clazz != null && this.method != null) {
			genericReturnTypes = getActualClass(method.getGenericReturnType());

			Annotation[][] annotations = method.getParameterAnnotations();
			parameterCount = annotations.length;
			for (int index = 0; index < annotations.length; index++) {
				for (Annotation annotation : annotations[index]) {

					Class<? extends Annotation> annotationType = annotation
							.annotationType();
					Annotation[] annotationArray = parameterAnnotations
							.get(annotationType);
					if (annotationArray == null) {
						annotationArray = (Annotation[]) Array.newInstance( // NL
								annotationType, parameterCount);
						//parameterAnnotations.put(annotationType,annotationArray);
						parameterAnnotations.put(this.method,annotationArray);
					}

					annotationArray[index] = annotation;
				}
			}
		}
	}
	
	private static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];
	
    private  Class<?>[] getActualClass(Type genericType) {

        if (genericType instanceof ParameterizedType) {

            Type[] actualTypes = ((ParameterizedType) genericType).getActualTypeArguments();
            Class<?>[] actualClasses = new Class<?>[actualTypes.length];

            for (int i = 0; i < actualTypes.length; i++) {
                Type actualType = actualTypes[i];
                if (actualType instanceof Class<?>) {
                    actualClasses[i] = (Class<?>) actualType;
                } else if (actualType instanceof GenericArrayType) {
                    Type componentType = ((GenericArrayType) actualType).getGenericComponentType();
                    actualClasses[i] = Array.newInstance((Class<?>) componentType, 0).getClass();
                }
            }

            return actualClasses;
        }

        return EMPTY_CLASSES;
    }

	// ///////////////////////////////////get///set/////////////////////////////////////////////

	public String getName() {
		return method.getName();
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public Class<?> getReturnType() {
		return method.getReturnType();
	}

	public Class<?>[] getGenericReturnTypes() {
		return genericReturnTypes;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return method.getAnnotation(annotationClass);
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs(){
		return this.args;
	}
	
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
	public void setArgs(Object[] args){
		this.args= args;
	}

	@SuppressWarnings("unchecked")
	public <T extends Annotation> T[] getParameterAnnotations(
			Class<T> annotationClass) {
		//T[] annotations = (T[]) parameterAnnotations.get(annotationClass);
		T[] annotations = (T[]) parameterAnnotations.get(this.method);
		if (annotations == null) {
			annotations = (T[]) Array.newInstance(annotationClass,parameterCount);
			//parameterAnnotations.put(annotationClass, annotations);
			parameterAnnotations.put(this.method, annotations);
		}
		return annotations;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DaoDescription) {
			DaoDescription desc = (DaoDescription) obj;
			return clazz.equals(desc.clazz) && method.equals(desc.method);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return clazz.hashCode() ^ method.hashCode();
	}

}
