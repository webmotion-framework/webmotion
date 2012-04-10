/*
 * #%L
 * WebMotion full stack
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
package org.debux.webmotion.jpa;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author julien
 */
@Entity
@NamedQueries({
    @NamedQuery(
        name = "findAll",
        query = "SELECT c FROM Comment c"
    ),
    @NamedQuery(
        name = "findByUsername",
        query = "SELECT c FROM Comment c where c.username=:username"
    ),
    @NamedQuery(
        name = "findByUsernames",
        query = "SELECT c FROM Comment c where c.username IN (:usernames)"
    )
})
public class Comment extends IdentifiableEntity {

    @Basic
    protected String username;
    
    @Basic
    protected String comment;

    @Basic
    protected int note;

    @ElementCollection
    protected List<String> tags;

    @OneToMany(mappedBy = "parent")
    @Transient
    protected List<Comment> threads;
    
    @ManyToOne
    protected Comment parent;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public List<Comment> getThreads() {
        return threads;
    }

    public void setThreads(List<Comment> threads) {
        this.threads = threads;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
}
