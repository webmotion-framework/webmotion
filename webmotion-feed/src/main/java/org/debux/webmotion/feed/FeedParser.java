/*
 * #%L
 * WebMotion Feed
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
package org.debux.webmotion.feed;

import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.commons.digester3.Digester;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author julien
 */
public class FeedParser extends WebMotionController {

    private static final Logger log = LoggerFactory.getLogger(FeedParser.class);
    
    public Render atom(String url) throws MalformedURLException, IOException, SAXException {
        Digester digester = new Digester();
        
        digester.addObjectCreate("feed", FeedValues.class);
        digester.addObjectCreate("feed/entry", FeedValue.class);
        digester.addSetNext("feed/entry", "addValues");
        digester.addBeanPropertySetter("feed/entry/title", "title");
        digester.addBeanPropertySetter("feed/entry/content", "content");
        digester.addBeanPropertySetter("feed/entry/updated", "date");
        digester.addBeanPropertySetter("feed/entry/link", "link");
        
        FeedValues parse = digester.parse(url);
        
        return renderStringTemplate("feed.stg", "text/html",
                "feeds", parse);
    }
    
    public Render rss(String url) throws MalformedURLException, IOException, SAXException {
        Digester digester = new Digester();
        
        digester.addObjectCreate("rss/channel", FeedValues.class);
        digester.addObjectCreate("rss/channel/item", FeedValue.class);
        digester.addSetNext("rss/channel/item", "addValues");
        digester.addBeanPropertySetter("rss/channel/item/title", "title");
        digester.addBeanPropertySetter("rss/channel/item/link", "link");
        digester.addBeanPropertySetter("rss/channel/item/description", "content");
        digester.addBeanPropertySetter("rss/channel/item/pubDate", "date");
        
        FeedValues parse = digester.parse(url);
        
        return renderStringTemplate("feed.stg", "text/html",
                "feeds", parse);
    }
    
}
