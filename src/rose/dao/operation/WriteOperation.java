package rose.dao.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.springframework.util.NumberUtils;

import rose.dao.annotation.SQLParam;
import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;

import rose.dao.DaoOperation;
import rose.dao.Identity;

public class WriteOperation implements DaoOperation {

	private final String sql;

	private final SQLParam[] sqlParamAnnotations;

	private final Class<?> returnType;

	private final DaoDescription desc;

	private final Dao dao;

	public WriteOperation(Dao dao, String sql, DaoDescription desc) {
		this.dao = dao;
		this.sql = sql;
		this.desc = desc;
		this.returnType = desc.getReturnType();
		this.sqlParamAnnotations = desc.getParameterAnnotations(SQLParam.class);
	}

	public Object execute(Map<String, Object> parameters) {
		if (parameters.get(":1") instanceof List<?>) {
			Class<?> returnType = desc.getReturnType();
			if (returnType != void.class && returnType != int[].class
					&& returnType != Integer[].class && returnType != int.class
					&& returnType != Integer.class) {
				throw new IllegalArgumentException(
						"error return type for batch update.");
			}
			// 批量执行查询
			return executeBatch(dao, parameters);
		} else {
			// 单个执行查询
			return executeSignle(dao, parameters, returnType);
		}
	}

	public DaoDescription getDesc() {
		return desc;
	}

	// ///////////////////////////////////help//////////////////////////////////////
	private Object executeBatch(Dao dao, Map<String, Object> parameters) {

		List<?> list = (List<?>) parameters.get(":1");

		int[] updatedArray;

		List<Map<String, Object>> parametersList = new ArrayList<Map<String, Object>>(list.size());
		
		for (Object arg : list) {

			HashMap<String, Object> clone = new HashMap<String, Object>(
					parameters);

			// 更新执行参数
			clone.put(":1", arg);
			if (this.sqlParamAnnotations[0] != null) {
				clone.put(this.sqlParamAnnotations[0].value(), arg);
			}
			parametersList.add(clone);
		}
		updatedArray = dao.batchUpdate(sql, desc, parametersList);

		Class<?> batchReturnClazz = desc.getReturnType();
		if (batchReturnClazz == int[].class) {
			return updatedArray;
		}
		if (batchReturnClazz == Integer[].class) {
			Integer[] ret = new Integer[updatedArray.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = updatedArray[i];
			}
			return updatedArray;
		}
		if (batchReturnClazz == void.class) {
			return null;
		}
		if (batchReturnClazz == int.class || batchReturnClazz == Integer.class) {
			int updated = 0;
			for (int i = 0; i < updatedArray.length; i++) {
				updated += updatedArray[i];
			}
			return updated;
		}

		return null;
	}

	private Object executeSignle(Dao dao, Map<String, Object> parameters,
			Class<?> returnType) {

		if (returnType == Identity.class) {

			// 执行 INSERT 查询
			Number number = dao.insertReturnId(sql, desc, parameters);

			// 将结果转成方法的返回类型
			return new Identity(number);

		} else {

			// 执行 UPDATE / DELETE 查询
			int updated = dao.update(sql, desc, parameters);

			// 转换基本类型
			if (returnType.isPrimitive()) {
				returnType = ClassUtils.primitiveToWrapper(returnType);
			}

			// 将结果转成方法的返回类型
			if (returnType == Boolean.class) {
				return Boolean.valueOf(updated > 0);
			} else if (returnType == Long.class) {
				return Long.valueOf(updated);
			} else if (returnType == Integer.class) {
				return Integer.valueOf(updated);
			} else if (Number.class.isAssignableFrom(returnType)) {
				return NumberUtils.convertNumberToTargetClass( // NL
						Integer.valueOf(updated), returnType);
			}
		}

		return null; // 没有返回值
	}

}
