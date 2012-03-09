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
 * Demonstrate WebMotion, each method show a possibility.
 * 
 * @author julien
 */
public class Showcase extends WebMotionController {

    protected static final int INDEX_EXTENSIONS = 6;
    protected static final int INDEX_FILTERS = 11;
    protected static final int INDEX_ERRORS = 17;
    protected static final int INDEX_ACTIONS = 25;
    
    protected static final String SECTION_ACTIONS = "actions";
    protected static final String SECTION_FILTERS = "filters";
    protected static final String SECTION_ERRORS = "errors";
    protected static final String SECTION_EXTENSIONS = "extensions";
            
    private static final Logger log = LoggerFactory.getLogger(Showcase.class);
    
    protected List<String> mappingShowcase;
    
    public Showcase() throws IOException {
        InputStream stream = Showcase.class.getClassLoader().getResourceAsStream("/test");
        mappingShowcase = IOUtils.readLines(stream);
    }

    protected String getFile(String name) throws IOException {
        InputStream stream = Showcase.class.getClassLoader().getResourceAsStream(name);
        String content = IOUtils.toString(stream);
        content = content.replace("/test", "");
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
        return new FileContent("/src/main/java/org/debux/webmotion/test/" + name, content);
    }

    protected String getJava(String name) throws IOException {
        String content = getFile("/src/" + name);
        return content;
    }

    protected FileContent getPageContent(String name) throws IOException {
        String content = getFile("/pages/" + name);
        content = content.replaceAll("", "");
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
            section += "package.base=org.debux.webmotion.test\n";
        }
        return new FileContent("/src/main/resources/mapping", section + "\n");
    }
    
    protected String getMapping(String section, int index, int length) {
        return getFirstMapping(section, index, length) + "\n";
    }

    protected String getFirstMapping(String section, int index, int length) {
        String content = "[" + section + "]\n";
        content += getContentMapping(index, length);
        return content;
    }

    protected String getContentMapping(int index, int length) {
        String content = "";
        List<String> lines = mappingShowcase.subList(index - 1, index + length - 1);
        for (String line : lines) {
            content += line + "\n";
        }
        return content;
    }

    public Render hello() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/hello"
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
                        "/helloModel"
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
                    "/helloParameters?who=you",
                    "/helloParameters?who=me"
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
                    "/view"
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
                    "/url"
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
                    "/act"
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
                    "/dynamic/get",
                    "/dynamic/set"
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
                    "/helloDefaultParameters",
                    "/helloDefaultParameters?who=other"
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
                    "/path/log?value=42"
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
                    "/other/repeat?number=6",
                    "/other/repeat?number=3"
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
                    "/parse"
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
                    "/service"
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
                    "/npe"
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
                    "/content"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 14, 1)),
                    getJavaContent("Content.java")
                )
        );
    }
    
    public Render stream() throws IOException {
        FileContent img = new FileContent("/src/main/resources/Outlook.png", null);
        
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/stream"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 15, 1)),
                    getJavaContent("Stream.java"),
                    img
                )
        );
    }
    
    public Render xml() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/xml"
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
                    "/json"
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
                    "/script"
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
                    "/first"
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
                    "/index"
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
                    "/save"
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
                    "/nocontent"
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
                    "/forbidden"
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
                    "/load"
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
                    "/message"
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
                    "/calc"
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
    
    public Render staticResources() throws IOException {
        FileContent content = new FileContent("/src/main/webapp/README", "Dummy readme");
        FileContent img = new FileContent("/src/main/webapp/wm_cloud.png", null);
        
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/readme",
                    "/img"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, false, false)
                        .addContent(getFirstMapping(SECTION_ACTIONS, INDEX_ACTIONS + 32, 1))
                        .addContent(getContentMapping(INDEX_ACTIONS + 69, 1)),
                    content,
                    img
                )
        );
    }
    
    public Render form() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/form"
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
                    "/pattern/aaaa?value=aaaa",
                    "/pattern/baaa?value=baaaa"
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
                    "/page"
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
                    "/upload"
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
                    "/file"
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
                    "/create?book.isbn=007&book.title=James%20Bond",
                    "/create?book.isbn=007&book.title=James",
                    "/comment?book.isbn=007&book.comment=cool",
                    "/comment?book.isbn=007",
                    "/search?query=007",
                    "/search",
                    "/info?book.isbn=007"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, true)
                        .addContent(getMapping(SECTION_ERRORS, INDEX_ERRORS + 1, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 46, 4)),
                    getJavaContent("Shop.java"),
                    getJavaContent("ErrorConstraint.java"),
                    getJavaContent("Book.java"),
                    getPageContent("error.jsp")
                )
        );
    }
    
    public Render blog() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/blog/index"
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
                    "/wikipedia/tutu",
                    "/wikipedia/tata"
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
                    "/text?file=readme",
                    "/text?file=changelog"
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
                    "/notfound"
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
                    "/media"
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
                    "/login?user.name=john&user.passwd=azerty",
                    "/login?name=john&passwd=azerty"
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
                    "/helloView?name=John",
                    "/helloView?name=Jack"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 57, 1)),
                    getPageContent("helloParameters.jsp")
                ) 
        );
    }
    
    public Render spring() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/spring/"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, false, false, false)
                        .addContent(getMapping(SECTION_EXTENSIONS, INDEX_EXTENSIONS + 2, 1)),
                    new FileContent("/src/main/resources/spring", getFile("spring")),
                    getJavaContent("Spring.java"),
                    getJavaContent("Bean.java"),
                    getPageContent("spring.jsp")
                ) 
        );
    }
    
    public Render select() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/select?param=value",
                    "/select"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 58, 2)),
                    getPageContent("selectValue.jsp"),
                    getPageContent("select.jsp")
                ) 
        );
    }
    
    public Render async() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/async"
                ),
                "files", Arrays.asList(
                    getConfig(true, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 60, 1)),
                    getJavaContent("Async.java"),
                    getPageContent("hello.jsp")
                )
        );
    }
    
    public Render template() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/template"
                ),
                "files", Arrays.asList(
                    getConfig(false, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 61, 1)),
                    getJavaContent("Template.java"),
                    new FileContent("/src/main/resources/template.stg", getFile("template.stg"))
                )
        );
    }
    
    public Render listener() throws IOException {
        String section = "[config]\n";
        section += "package.base=org.debux.webmotion.showcase\n";
        section += "server.listener.class=org.debux.webmotion.showcase.Listener\n";
        
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/context"
                ),
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", section + "\n")
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 62, 1)),
                    getJavaContent("Listener.java"),
                    getJavaContent("Context.java")
                )
        );
    }
    
    public Render stats() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/stats"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, false, false, false)
                        .addContent(getMapping(SECTION_EXTENSIONS, INDEX_EXTENSIONS + 3, 1)),
                    new FileContent("/src/main/resources/stats.wm", getFile("META-INF/stats.wm")),
                    getJavaContent("Stats.java")
                )
        );
    }
    
    public Render application() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/application"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 63, 2)),
                    getJavaContent("Application.java"),
                    getPageContent("application.jsp")
                )
        );
    }
    
    public Render internal() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/internal"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 65, 2)),
                    getJavaContent("Internal.java")
                )
        );
    }
    
    public Render decorator() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/contact/view",
                    "/company/view"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, false, true, false)
                        .addContent(getMapping(SECTION_FILTERS, INDEX_FILTERS + 3, 2))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 67, 2)),
                    getJavaContent("Decorator.java"),
                    getPageContent("contact.jsp"),
                    getPageContent("company.jsp")
                )
        );
    }
    
}
