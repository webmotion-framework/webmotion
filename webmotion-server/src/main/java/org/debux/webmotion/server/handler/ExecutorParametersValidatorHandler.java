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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Payload;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.mapping.Mapping;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.call.Executor;
import org.debux.webmotion.server.call.InitContext;
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
 * <p>
 * Bean exemple is :<p>
 *<pre>
 * Render getUser(@Valid User user) {
 *       // ...
 * }
 * </pre>
 * <p>
 * Bean groups exemple is :<p>
 *<pre>
 * Render getUser(@ValidGroup(Group1.class, Group2.class) User user) {
 *       // ...
 * }
 * </pre>
 * 
 * @author julien
 */
public class ExecutorParametersValidatorHandler implements WebMotionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutorParametersValidatorHandler.class);

    protected MethodValidator methodValidator = null;

    public ExecutorParametersValidatorHandler() {
        try {
            methodValidator = Validation.byProvider(HibernateValidator.class)
                .configure()
                .buildValidatorFactory()
                .getValidator()
                .unwrap(MethodValidator.class);
            
        } catch (ValidationException ve) {
            // Glassfish not supports MethodValidator, I don't known why.
            log.info("MethodValidator not supported", ve);
            return;
        }
    }
    
    @Override
    public void init(InitContext context) {
        // do nothing
    }

    @Override
    public void handle(Mapping mapping, Call call) {
        Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();
        
        List<Executor> executors = call.getExecutors();
        for (Executor executor : executors) {
            
            Method method = executor.getMethod();
            WebMotionController instance = executor.getInstance();

            int parameterIndex = 0;
            Map<String, Object> parameters = executor.getParameters();
            Collection<Object> values = parameters.values();
            for (Object parameterValue : values) {
                
                // Test validation on method
                violations.addAll(methodValidator.validateParameter(instance, method, parameterValue, parameterIndex));
                
                parameterIndex++;
            }
        }
        
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
    
    /**
     * Mark a bean to be valid on specific groups
     */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = ValidGroupValidator.class)
    @Documented
    public @interface ValidGroup {
        Class<?>[] value() default {};
        
        String message() default "{org.debux.webmotion.server.handler.ValidGroup.message}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    /**
     * Implements contrains to valid bean on specific groups
     */
    public static class ValidGroupValidator implements ConstraintValidator<ValidGroup, Object> {

        protected Class<?>[] groups;

        @Override
        public void initialize(ValidGroup annotation) {
            groups = annotation.value();
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            Validator beanValidator = Validation.buildDefaultValidatorFactory().getValidator();
            context.disableDefaultConstraintViolation();
            
            Set<ConstraintViolation<Object>> violations = beanValidator.validate(value, groups);
            for (ConstraintViolation<Object> violation : violations) {
                
                ConstraintViolationBuilder constraintViolation = context.buildConstraintViolationWithTemplate(violation.getMessageTemplate());
                
                Path propertyPath = violation.getPropertyPath();
                for (Iterator<Path.Node> iterator = propertyPath.iterator(); iterator.hasNext();) {
                    Path.Node node = iterator.next();
                    constraintViolation.addNode(node.getName());
                }
                
                constraintViolation.addConstraintViolation();
            }
            
            return violations.isEmpty();
        }
    }

}
