/*
 * #%L
 * Webmotion website
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2015 Debux
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
package org.debux.webmotion.test;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.debux.webmotion.server.handler.ExecutorParametersValidatorHandler.ValidGroup;
import org.debux.webmotion.test.Book.BookCommentValidation;

/**
 * Validation example
 * 
 * @author julien
 */
public class Shop extends WebMotionController {
    
    public Render create(@Valid Book book) {
        return renderContent("<h1>Book created</h1>", "text/html");
    }
    
    public Render comment(@ValidGroup(BookCommentValidation.class) Book book) {
        return renderContent("<h1>Comment saved</h1>", "text/html");
    }
    
    public Render search(@NotNull String query) {
        return renderContent("<h1>Not found</h1>", "text/html");
    }
    
    public Render info(Book book) {
        return renderContent("<h1>Not found</h1>", "text/html");
    }
    
}
