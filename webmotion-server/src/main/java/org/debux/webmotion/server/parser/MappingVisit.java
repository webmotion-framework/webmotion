/*
 * #%L
 * WebMotion server
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Debux
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
package org.debux.webmotion.server.parser;

import java.util.List;
import org.debux.webmotion.server.mapping.*;

/**
 *
 * @author julien
 */
public class MappingVisit {
    
    public static class Visitor {
        public void accept(Mapping mapping) {
        }
        
        public void accept(Mapping mapping, Rule rule) {
        }
        
        public void accept(Mapping mapping, ActionRule actionRule) {
        }
        
        public void accept(Mapping mapping, ErrorRule errorRule) {
        }
        
        public void accept(Mapping mapping, FilterRule filterRule) {
        }
        
        public void accept(Mapping mapping, Mapping extension) {
        }
    }
    
    public void visit(Mapping mapping, Visitor visitor) {
        visitor.accept(mapping);
        
        List<FilterRule> filterRules = mapping.getFilterRules();
        for (FilterRule filterRule : filterRules) {
            visitor.accept(mapping, (Rule) filterRule);
            visitor.accept(mapping, filterRule);
        }

        List<ActionRule> actionRules = mapping.getActionRules();
        for (ActionRule actionRule : actionRules) {
            visitor.accept(mapping, (Rule) actionRule);
            visitor.accept(mapping, actionRule);
        }

        List<ErrorRule> errorRules = mapping.getErrorRules();
        for (ErrorRule errorRule : errorRules) {
            visitor.accept(mapping, (Rule) errorRule);
            visitor.accept(mapping, errorRule);
        }

        List<Mapping> extensionsRules = mapping.getExtensionsRules();
        for (Mapping extension : extensionsRules) {
            visitor.accept(mapping, extension);
            visit(extension, visitor);
        }
    }
    
}
