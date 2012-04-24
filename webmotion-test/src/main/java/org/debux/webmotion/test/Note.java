/*
 * #%L
 * WebMotion test
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
package org.debux.webmotion.test;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.debux.webmotion.jpa.IdentifiableEntity;

/**
 * Note entity
 * 
 * @author julien
 */
@Entity
@NamedQueries({
    @NamedQuery(
        name = "findAll",
        query = "SELECT n FROM Note n"),
    @NamedQuery(
        name = "incLike",
        query = "UPDATE Note n SET n.likes = n.likes + 1 WHERE id = :id")
})
public class Note extends IdentifiableEntity {
    
    @Basic
    protected String content;
    
    @Basic
    protected int likes;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
    
}
