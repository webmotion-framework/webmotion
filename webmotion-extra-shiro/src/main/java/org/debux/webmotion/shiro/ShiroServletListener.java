/*
 * #%L
 * WebMotion extra sitemesh
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
package org.debux.webmotion.shiro;

import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.Callable;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.ShiroHttpServletResponse;
import org.apache.shiro.web.subject.WebSubject;

/**
 * Copy part of AbstractShiroFilter.java, which bind the subject and wrap the 
 * request and response for taglib.
 * 
 * @author julien
 */
public class ShiroServletListener implements ServletContextListener {

    /** Current filter */
    protected Filter filter;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        if (filter == null) {
            // Create the filter
            filter = new Filter() {
                
                @Override
                public void init(FilterConfig filterConfig) throws ServletException {
                    // Do nothing
                }

                @Override
                public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain chain) throws IOException, ServletException {
                    Throwable t = null;

                    try {
                        final ServletRequest request = prepareServletRequest(servletRequest, servletResponse, chain);
                        final ServletResponse response = prepareServletResponse(request, servletResponse, chain);

                        final Subject subject = createSubject(request, response);

                        //noinspection unchecked
                        subject.execute(new Callable() {

                            @Override
                            public Object call() throws Exception {
                                executeChain(request, response, chain);
                                return null;
                            }
                        });
                    } catch (ExecutionException ex) {
                        t = ex.getCause();
                    } catch (Throwable throwable) {
                        t = throwable;
                    }

                    if (t != null) {
                        if (t instanceof ServletException) {
                            throw (ServletException) t;
                        }
                        if (t instanceof IOException) {
                            throw (IOException) t;
                        }
                        //otherwise it's not one of the two exceptions expected by the filter method signature - wrap it in one:
                        String msg = "Filtered request failed.";
                        throw new ServletException(msg, t);
                    }
                }
                
                protected void executeChain(ServletRequest request, ServletResponse response, FilterChain origChain)
                        throws IOException, ServletException {
                    origChain.doFilter(request, response);
                }

                protected ServletResponse prepareServletResponse(ServletRequest request, ServletResponse response, FilterChain chain) {
                    ServletResponse toUse = response;
                    if (!isHttpSessions() && (request instanceof ShiroHttpServletRequest)
                            && (response instanceof HttpServletResponse)) {
                        //the ShiroHttpServletResponse exists to support URL rewriting for session ids.  This is only needed if
                        //using Shiro sessions (i.e. not simple HttpSession based sessions):
                        toUse = wrapServletResponse((HttpServletResponse) response, (ShiroHttpServletRequest) request);
                    }
                    return toUse;
                }

                protected ServletRequest prepareServletRequest(ServletRequest request, ServletResponse response, FilterChain chain) {
                    ServletRequest toUse = request;
                    if (request instanceof HttpServletRequest) {
                        HttpServletRequest http = (HttpServletRequest) request;
                        toUse = wrapServletRequest(http);
                    }
                    return toUse;
                }
                
                protected ServletResponse wrapServletResponse(HttpServletResponse orig, ShiroHttpServletRequest request) {
                    return new ShiroHttpServletResponse(orig, request.getServletContext(), request);
                }
                
                protected ServletRequest wrapServletRequest(HttpServletRequest orig) {
                    return new ShiroHttpServletRequest(orig, orig.getServletContext(), isHttpSessions());
                }
                
                protected boolean isHttpSessions() {
                    return getSecurityManager().isHttpSessionMode();
                }
                
                protected WebSubject createSubject(ServletRequest request, ServletResponse response) {
                    return new WebSubject.Builder(getSecurityManager(), request, response).buildWebSubject();
                }

                public WebSecurityManager getSecurityManager() {
                    return (WebSecurityManager) SecurityUtils.getSecurityManager();
                }
                
                @Override
                public void destroy() {
                    // Do nothing
                }
            };
            
            // Add filter into webapp
            ServletContext servletContext = event.getServletContext();
            FilterRegistration registration = servletContext.addFilter("shiro", filter);
            registration.addMappingForUrlPatterns(
                    EnumSet.of(DispatcherType.FORWARD,
                    DispatcherType.INCLUDE,
                    DispatcherType.REQUEST,
                    DispatcherType.ERROR),
                    true, "/*");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Do nothing
    }
    
}
