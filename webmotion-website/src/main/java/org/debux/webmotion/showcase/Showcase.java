/*
 * #%L
 * Webmotion website
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
package org.debux.webmotion.showcase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.debux.webmotion.server.WebMotionController;
import org.debux.webmotion.server.render.Render;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author julien
 */
public class Showcase extends WebMotionController {

    protected static final int INDEX_EXTENSIONS = 5;
    protected static final int INDEX_FILTERS = 8;
    protected static final int INDEX_ERRORS = 12;
    protected static final int INDEX_ACTIONS = 20;
    
    protected static final String SECTION_ACTIONS = "actions";
    protected static final String SECTION_FILTERS = "filters";
    protected static final String SECTION_ERRORS = "errors";
    protected static final String SECTION_EXTENSIONS = "extensions";
            
    private static final Logger log = LoggerFactory.getLogger(Showcase.class);
    
    protected List<String> mappingShowcase;
    
    public Showcase() throws IOException {
        InputStream stream = Showcase.class.getClassLoader().getResourceAsStream("/showcase-action");
        mappingShowcase = IOUtils.readLines(stream);
    }

    protected String getFile(String name) throws IOException {
        InputStream stream = Showcase.class.getClassLoader().getResourceAsStream(name);
        String content = IOUtils.toString(stream);
        if(content.contains("#L%\n */\n")) {
            content = StringUtils.substringAfter(content, "#L%\n */\n");
        }
        if(content.contains("#L%\n  -->\n")) {
            content = StringUtils.substringAfter(content, "#L%\n  -->\n");
        }
        return content;
    }
    
    protected FileContent getJavaContent(String name) throws IOException {
        String content = getJava(name);
        return new FileContent("/src/main/java/org/debux/webmotion/showcase/" + name, content);
    }

    protected String getJava(String name) throws IOException {
        String content = getFile("/src/" + name);
        return content;
    }

    protected FileContent getPageContent(String name) throws IOException {
        String content = getFile("/pages/" + name);
        content = content.replaceAll("/webmotion-website/showcase/action", "/contextPath");
        content = content.replaceAll("/showcase/action", "");
        return new FileContent("/src/main/webapp/WEB-INF/pages/" + name, content);
    }

    protected FileContent getConfig(boolean views, boolean actions, boolean filters, boolean errors) {
        if(!views && !actions && !filters && !errors) {
            return new FileContent("/src/main/resources/mapping", "");
        }
        
        String section = "[config]\n";
        if(views) {
            section += "package.views=WEB-INF/pages/\n";
        }
        if(actions || filters || errors) {
            section += "package.base=org.debux.webmotion.showcase\n";
        }
        return new FileContent("/src/main/resources/mapping", section + "\n");
    }
    
    protected String getMapping(String section, int index, int length) {
        String content = "[" + section + "]\n";
        List<String> lines = mappingShowcase.subList(index - 1, index + length - 1);
        for (String line : lines) {
            content += line + "\n";
        }
        return content + "\n";
    }
    
    public Render hello() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/hello"
                ),
                "files", Arrays.asList(
                    new FileContent("/pom.xml", getFile("showcase-pom.xml")),
                    new FileContent("/src/main/webapp/WEB-INF/web.xml", getFile("showcase-web.xml")),
                    getConfig(true, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 1, 1)),
                    getJavaContent("Hello.java"),
                    getPageContent("hello.jsp")
                )
        );
    }
    
    public Render helloModel() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                        "/showcase/action/helloModel"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 2, 1)),
                    getJavaContent("HelloModel.java"),
                    getPageContent("helloModel.jsp")
                )
        );
    }
    
    public Render helloParameters() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/helloParameters?who=you",
                    "/showcase/action/helloParameters?who=me"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 3, 1)),
                    getJavaContent("HelloParameters.java"),
                    getPageContent("helloParameters.jsp")
                )
        );
    }
    
    public Render view() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/view"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 4, 1)),
                    getPageContent("hello.jsp")
                )
        );
    }
    
    public Render url() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/url"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, false, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 5, 1))
                )
        );
    }
    
    public Render act() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/act"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 6, 1)),
                    getJavaContent("Action.java")
                )
        );
    }
    
    public Render dynamic() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/dynamic/get",
                    "/showcase/action/dynamic/set"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 7, 1)),
                    getJavaContent("Dynamic.java")
                )
        );
    }
    
    public Render helloDefaultParameters() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/helloDefaultParameters"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 8, 1)),
                    getJavaContent("HelloParameters.java"),
                    getPageContent("helloParameters.jsp")
                )
        );
    }
    
    public Render filter() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/path/log?value=42"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, true, false) 
                        .addContent(getMapping(SECTION_FILTERS, INDEX_FILTERS + 1, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 9, 1)),
                    getJavaContent("Filter.java"),
                    getJavaContent("Log.java")
                )
        );
    }
    
    public Render condition() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/other/repeat?number=6",
                    "/showcase/action/other/repeat?number=3"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, true, false)
                        .addContent(getMapping(SECTION_FILTERS, INDEX_FILTERS + 2, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 10, 1)),
                    getJavaContent("Condition.java"),
                    getJavaContent("Repeat.java")
                )
        );
    }
    
    public Render code() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/parse"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, true)
                        .addContent(getMapping(SECTION_ERRORS, INDEX_ERRORS + 5, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 11, 1)),
                    getJavaContent("Parser.java"),
                    getJavaContent("ErrorCode.java"),
                    getPageContent("error.jsp")
                )
        );
    }
    
    public Render exception() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/service"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, true)
                        .addContent(getMapping(SECTION_ERRORS, INDEX_ERRORS + 3, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 12, 1)),
                    getJavaContent("Service.java"),
                    getJavaContent("ServiceException.java"),
                    getJavaContent("ErrorService.java"),
                    getPageContent("error.jsp")
                )
        );
    }
    
    public Render npe() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/npe"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, false, true)
                        .addContent(getMapping(SECTION_ERRORS, INDEX_ERRORS + 2, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 13, 1)),
                    getJavaContent("Generate.java"),
                    getPageContent("serverError.jsp")
                )
        );
    }
    
    public Render content() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/content"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 14, 1)),
                    getJavaContent("Content.java")
                )
        );
    }
    
    public Render stream() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/stream"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 15, 1)),
                    getJavaContent("Stream.java")
                )
        );
    }
    
    public Render xml() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/xml"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 16, 1)),
                    getJavaContent("Xml.java"),
                    getJavaContent("User.java")
                )
        );
    }
    
    public Render json() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/json"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 17, 1)),
                    getJavaContent("Json.java"),
                    getJavaContent("User.java")
                )
        );
    }
    
    public Render jsonp() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/script"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 18, 2)),
                    getJavaContent("Jsonp.java"),
                    getJavaContent("User.java"),
                    getPageContent("script.jsp")
                )
        );
    }
    
    public Render first() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/first"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 20, 1)),
                    getJavaContent("Call.java")
                )
        );
    }
    
    public Render index() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/index"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 21, 1)),
                    getJavaContent("Page.java"),
                    getPageContent("index.jsp")
                )
        );
    }
    
    public Render save() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/save"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 22, 2)),
                    getJavaContent("Reader.java")
                )
        );
    }
    
    public Render nocontent() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/nocontent"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 24, 1)),
                    getJavaContent("Status.java")
                )
        );
    }
    
    public Render forbidden() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/forbidden"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ERRORS, INDEX_ERRORS + 4, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 25, 1)),
                    getJavaContent("Error.java"),
                    getJavaContent("ErrorCode.java"),
                    getPageContent("error.jsp")
                )
        );
    }
    
    public Render load() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/load"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 26, 2)),
                    getJavaContent("Reload.java"),
                    getPageContent("load.jsp")
                )
        );
    }
    
    public Render message() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/message"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 28, 2)),
                    getJavaContent("Message.java"),
                    getPageContent("receive.jsp")
                )
        );
    }
    
    public Render calc() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/calc"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 30, 2)),
                    getJavaContent("Calc.java"),
                    getPageContent("calc.jsp"),
                    getPageContent("result.jsp")
                )
        );
    }
    
    public Render readme() throws IOException {
        FileContent content = new FileContent("/src/main/webapp/README", "Dummy readme");
        
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/readme"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 32, 1)),
                    content
                )
        );
    }
    
    public Render form() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/form"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 33, 3)),
                    getJavaContent("Person.java"),
                    getPageContent("form.jsp")
                )
        );
    }
    
    public Render pattern() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/pattern/aaaa?value=aaaa",
                    "/showcase/action/pattern/baaa?value=baaaa"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 36, 2)),
                    getJavaContent("Pattern.java")
                )
        );
    }
    
    public Render page() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/page"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 38, 3)),
                    getJavaContent("Layout.java"),
                    getPageContent("page.jsp"),
                    getPageContent("footer.html")
                )
        );
    }
    
    public Render upload() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/upload"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 41, 3)),
                    getJavaContent("Progress.java"),
                    getPageContent("upload.jsp")
                )
        );
    }
    
    public Render file() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/file"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 44, 2)),
                    getJavaContent("Download.java"),
                    getPageContent("file.jsp")
                )
        );
    }
    
    public Render shop() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/create?book.isbn=007&book.title=James%20Bond",
                    "/showcase/action/create?book.isbn=007&book.title=James",
                    "/showcase/action/comment?book.isbn=007&book.comment=cool",
                    "/showcase/action/comment?book.isbn=007",
                    "/showcase/action/search?query=007",
                    "/showcase/action/search",
                    "/showcase/action/info?book.isbn=007"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, true)
                        .addContent(getMapping(SECTION_ERRORS, INDEX_ERRORS + 1, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 46, 4)),
                    getJavaContent("Shop.java"),
                    getJavaContent("ErrorConstraint.java"),
                    getJavaContent("Book.java")
                )
        );
    }
    
    public Render blog() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/blog/index"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, false, false, false)
                        .addContent(getMapping(SECTION_EXTENSIONS, INDEX_EXTENSIONS + 1, 1)),
                    new FileContent("/src/main/resources/blog", getFile("blog")),
                    getPageContent("blog.jsp")
                )
        );
    }
    
    public Render wikipedia() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/wikipedia/tutu",
                    "/showcase/action/wikipedia/tata"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, false, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 50, 1))
                )
        );
    }
    
    public Render text() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/text?file=readme",
                    "/showcase/action/text?file=changelog"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 51, 1)),
                    getPageContent("readme.txt"),
                    getPageContent("changelog.txt")
                )
        );
    }
    
    public Render notfound() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/notfound"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, true)
                        .addContent(getMapping(SECTION_ERRORS, INDEX_ERRORS + 6, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 52, 1)),
                    getJavaContent("NotFound.java"),
                    getJavaContent("ErrorCode.java"),
                    getPageContent("error.jsp")
                ) 
        );
    }
    
    public Render media() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/media"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 53, 2)),
                    getJavaContent("Media.java"),
                    getPageContent("media.jsp")
                ) 
        );
    }
    
    public Render login() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/login?user.name=john&user.passwd=azerty",
                    "/showcase/action/login?name=john&passwd=azerty"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 55, 2)),
                    getJavaContent("Auth.java"),
                    getJavaContent("User.java")
                ) 
        );
    }
    
    public Render helloView() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/showcase/action/helloView?name=John",
                    "/showcase/action/helloView?name=Jack"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 57, 1)),
                    getPageContent("helloParameters.jsp")
                ) 
        );
    }
    
}
