package org.debux.webmotion.sitemesh;

import java.lang.reflect.Method;
import java.util.List;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.WebMotionUtils;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.handler.FilterMethodFinderHandler;
import org.debux.webmotion.server.mapping.Action;
import org.debux.webmotion.server.mapping.FilterRule;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Force SiteMesh filter as class when is declared in other mapping package.
 * 
 * @author julien
 */
public class SiteMeshFilterMethodFinderHandler extends FilterMethodFinderHandler {

    @Override
    protected void handle(Mapping mapping, Call call, FilterRule filterRule) throws WebMotionException {
        Action action = filterRule.getAction();
        String className = action.getClassName();
        if (className.equals("SiteMesh")) {
            
            String methodName = action.getMethodName();
            Method method = WebMotionUtils.getMethod(SiteMesh.class, methodName);
            if (method == null) {
                String fullQualifiedName = action.getFullName();
                throw new WebMotionException("Method not found with name " + methodName + " on class " + fullQualifiedName, filterRule);
            }
            
            
            Executor executor = new Executor();
            executor.setClazz(SiteMesh.class);
            executor.setMethod(method);
            
            List<Executor> filters = call.getFilters();
            filters.add(executor);
            
        } else {
            super.handle(mapping, call, filterRule);
        }
    }
    
}
