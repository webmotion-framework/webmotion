package org.debux.webmotion.spring;

import java.util.Arrays;
import java.util.List;
import org.debux.webmotion.server.WebMotionHandler;
import org.debux.webmotion.server.WebMotionMainHandler;
import org.debux.webmotion.server.handler.ExecutorParametersConvertorHandler;
import org.debux.webmotion.server.handler.ExecutorParametersInjectorHandler;
import org.debux.webmotion.server.handler.ExecutorParametersValidatorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add configuration to use Spring. It changes the ExecutorInstanceCreatorHandler 
 * by SpringInstanceCreatorHandler.
 * 
 * @author julien
 */
public class SpringMainHandler extends WebMotionMainHandler {

    private static final Logger log = LoggerFactory.getLogger(SpringMainHandler.class);

    @Override
    public List<Class<? extends WebMotionHandler>> getExecutorHandlers() {
        return Arrays.asList(
                    SpringInstanceCreatorHandler.class,
                    ExecutorParametersConvertorHandler.class,
                    ExecutorParametersInjectorHandler.class,
                    ExecutorParametersValidatorHandler.class
                );
    }
    
}
