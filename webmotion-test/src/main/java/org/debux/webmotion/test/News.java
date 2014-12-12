/*
 * #%L
 * WebMotion test
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Debux
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

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;

/**
 * Return news as RSS or ATOM with ROME library.
 * 
 * @author julien
 */
public class News extends WebMotionController {
    
    public SyndFeed getFeed() {
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("WebMotion");
        feed.setLink("http://webmotion-framework.org");
        feed.setDescription("Articles of WebMotion");
        
     
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle("DZone");
        entry.setLink("http://css.dzone.com/articles/websocket-webmotion-and");
        entry.setPublishedDate(new Date());
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("WebSocket with WebMotion and AngularJS");
        entry.setDescription(description);
        entries.add(entry);
        
        entry = new SyndEntryImpl();
        entry.setTitle("Developpez.com");
        entry.setLink("http://julien-ruchaud.developpez.com/tutoriels/introduction-framework-web-webmotion/");
        entry.setPublishedDate(new Date());
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Tutoriel sur une introduction au framework Web WebMotion");
        entry.setDescription(description);
        entries.add(entry);
        
        feed.setEntries(entries);
        
        return feed;
    }
    
    public Render rss() {
        SyndFeed feed = getFeed();
        return renderRss(feed);
    }
    
    public Render atom() {
        SyndFeed feed = getFeed();
        return renderAtom(feed);
    }
    
}
