package rose.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import rose.dao.DaoOperationFactory;
import rose.dao.operation.DaoOperationFactoryImpl;

import rose.dao.DaoOperation;

import rose.dao.annotation.SQLParam;
import rose.dao.provider.Dao;
import rose.dao.provider.DaoDescription;

public class DaoInvocationHandler implements InvocationHandler{
	
    private static DaoOperationFactory daoOperationFactory = new DaoOperationFactoryImpl();

    private HashMap<Method, DaoOperation> daoOperations = new HashMap<Method, DaoOperation>();

    private final DaoDescription desc;

    private final Dao dao;

    public DaoInvocationHandler(DaoDescription desc, Dao dao) {
        this.desc = desc;
        this.dao = dao;
    }

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		desc.setMethod(method);
		desc.setArgs(args);
		desc.initDescription();
		DaoOperation operation = daoOperations.get(method);
        if (operation == null) {
            synchronized (daoOperations) {
                operation = daoOperations.get(method);
                if (operation == null) {
                    operation = daoOperationFactory.getDaoOperation(dao, desc);
                    daoOperations.put(method, operation);
                }
            }
        }
        //
        // 将参数放�? Map
        Map<String, Object> parameters;
        if (args == null || args.length == 0) {
            parameters = new HashMap<String, Object>(4);
        } else {
            parameters = new HashMap<String, Object>(args.length * 2 + 4);
            SQLParam[] sqlParams = operation.getDesc().getParameterAnnotations(SQLParam.class);
            for (int i = 0; i < args.length; i++) {
                parameters.put(":" + (i + 1), args[i]);
                SQLParam sqlParam = sqlParams[i];
                if (sqlParam != null) {
                    parameters.put(sqlParam.value(), args[i]);
                }
            }
        }
        //

        return operation.execute(parameters);
	}

}
