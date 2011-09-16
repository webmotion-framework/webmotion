/*
 * #%L
 * Webmotion in action
 * *
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Debux
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.debux.webmotion.server.handler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import org.debux.webmotion.server.WebMotionContextable;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.Executor;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.method.MethodValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test if the parameters are valid. It is use JSR 303 Bean validation. The 
 * anotation is possible on parameters method and in bean parameters method.<p>
 * Basic exemple is :<p>
 *<pre>
 * Render getUser(@NotNull @Size(min = 5) String name) {
 *       // ...
 * }
 * </pre>
 * 
 * @author julien
 */
public class ExecutorParametersValidatorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorParametersValidatorHandler.class);

    @Override
    public void handle(Mapping mapping, Call call) {
        Validator beanValidator = Validation.buildDefaultValidatorFactory().getValidator();
        MethodValidator methodValidator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .buildValidatorFactory()
            .getValidator()
            .unwrap(MethodValidator.class);
       
        Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();
        
        List<Executor> executors = call.getExecutors();
        for (Executor executor : executors) {
            
            Method method = executor.getMethod();
            WebMotionContextable instance = executor.getInstance();

            int parameterIndex = 0;
            Map<String, Object> parameters = executor.getParameters();
            Collection<Object> values = parameters.values();
            for (Object parameterValue : values) {
                
                // Test validation on method
                violations.addAll(methodValidator.validateParameter(instance, method, parameterValue, parameterIndex++));
                
                // Test validation on bean
                if(parameterValue != null) {
                    violations.addAll(beanValidator.validate(parameterValue));
                }
            }
        }
        
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
