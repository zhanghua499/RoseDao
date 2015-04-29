package rose.dao.operation;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;
import rose.dao.DaoOperation;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

public class ReadOperation implements DaoOperation {

    private final String sql;

    private final RowMapper rowMapper;

    private final Class<?> returnType;

    private final DaoDescription desc;

    private final Dao dao;
	
    public ReadOperation(Dao dao,String sql,DaoDescription desc,RowMapper rowMapper){
    	this.dao = dao;
    	this.sql = sql;
    	this.desc = desc;
    	this.rowMapper = rowMapper;
    	this.returnType = desc.getReturnType();
    }
    
	@SuppressWarnings("unchecked")
	public Object execute(Map<String, Object> parameters) {
		List<?> listResult = dao.select(sql, desc, parameters, rowMapper);
		final int sizeResult = listResult.size();
	       // 将 Result 转成方法的返回类型
        if (returnType.isAssignableFrom(List.class)) {

            // 返回  List 集合
            return listResult;

        } else if (returnType.isArray() && byte[].class != returnType) {

            Object array = Array.newInstance(returnType.getComponentType(), sizeResult);

            listResult.toArray((Object[]) array);

            return array;

        } else if (Map.class.isAssignableFrom(returnType)) {
            // 将返回的  KeyValuePair 转换成  Map 对象
            // 因为entry.key可能为null，所以使用HashMap
            Map<Object, Object> map = (Map<Object, Object>) listResult.get(0);
//            if (returnType.isAssignableFrom(HashMap.class)) {
//
//                map = new HashMap<Object, Object>(listResult.size() * 2);
//
//            } else if (returnType.isAssignableFrom(Hashtable.class)) {
//
//                map = new Hashtable<Object, Object>(listResult.size() * 2);
//
//            } else {
//
//                throw new Error(returnType.toString());
//            }
//            for (Object obj : listResult) {
//                if (obj == null) {
//                    continue;
//                }
//
//                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) obj;
//
//                if (map.getClass() == Hashtable.class && entry.getKey() == null) {
//                    continue;
//                }
//
//                map.put(entry.getKey(), entry.getValue());
//            }

            return map;

        } else if (returnType.isAssignableFrom(HashSet.class)) {

            // 返回  Set 集合
            return new HashSet<Object>(listResult);

        } else {

            if (sizeResult == 1) {
                // 返回单个  Bean、Boolean等类型对象
                return listResult.get(0);

            } else if (sizeResult == 0) {

                // 基础类型的抛异常，其他的返回null
                if (returnType.isPrimitive()) {
                    String msg = "Incorrect result size: expected 1, actual " + sizeResult + ": "
                            + desc.toString();
                    throw new EmptyResultDataAccessException(msg, 1);
                } else {
                    return null;
                }

            } else {
                // IncorrectResultSizeDataAccessException
                String msg = "Incorrect result size: expected 0 or 1, actual " + sizeResult + ": "
                        + desc.toString();
                throw new IncorrectResultSizeDataAccessException(msg, 1, sizeResult);
            }
        }
	}

	public DaoDescription getDesc() {
		return desc;
	}

}
