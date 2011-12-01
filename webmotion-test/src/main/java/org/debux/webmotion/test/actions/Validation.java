/*
 * #%L
 * Webmotion in test
 * 
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
package org.debux.webmotion.test.actions;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.handler.ExecutorParametersValidatorHandler.ValidGroup;

/**
 *
 * @author jruchaud
 */
public class Validation extends WebMotionController {

    public static interface AGroup {
    }

    public static class TheBean {
        @NotNull
        protected String value;
        
        @NotNull(groups = AGroup.class)
        protected String group;
        
        protected String nothing;

        public void setGroup(String group) {
            this.group = group;
        }

        public void setNothing(String nothing) {
            this.nothing = nothing;
        }

        public void setValue(String value) {
            this.value = value;
        }
    } 
    
    public Render testValidValue(@NotNull @Size(min = 5) String value) {
        return renderContent("Good", "text/html");
    }
    
    public Render testNotValidBean(TheBean bean) {
        return renderContent("Good", "text/html");
    }
    
    public Render testValidBean(@Valid TheBean bean) {
        return renderContent("Good", "text/html");
    }
    
    public Render testGroupValidBean(@ValidGroup(AGroup.class) TheBean bean) {
        return renderContent("Good", "text/html");
    }
    
}
