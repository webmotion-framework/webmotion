package org.debux.webmotion.server.render;

/*
 * #%L
 * WebMotion server
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
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.debux.webmotion.server.WebMotionException;
import org.debux.webmotion.server.call.Call;
import org.debux.webmotion.server.call.HttpContext;
import org.debux.webmotion.server.mapping.Mapping;

/**
 * Render use to return feed to RSS or ATOM format with ROME library.
 * 
 * @author julien
 */
public class RenderFeed extends Render {
    
    public static String RSS_1 = "rss_1.0";
    public static String RSS_2 = "rss_2.0";
    public static String ATOM_1 = "atom_1.0";
    
    protected SyndFeed feed;
    protected String type;

    public RenderFeed(SyndFeed feed, String type) {
        this.feed = feed;
        this.type = type;
    }

    @Override
    public void create(Mapping mapping, Call call) throws IOException, ServletException {
        HttpContext context = call.getContext();
        HttpServletResponse response = context.getResponse();

        if (type.startsWith("rss")) {
            response.setContentType("application/rss+xml");
        } else {
            response.setContentType("application/atom+xml");
        }

        feed.setFeedType(type);
        
        SyndFeedOutput output = new SyndFeedOutput();
        PrintWriter out = context.getOut();
        try {
            output.output(feed, out);
        } catch (FeedException ex) {
            throw new WebMotionException("Error render feed", ex, call.getRule());
        }
    }
    
}
