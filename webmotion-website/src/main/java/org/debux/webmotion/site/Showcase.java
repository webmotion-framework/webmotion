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
package org.debux.webmotion.site;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

    protected static final int INDEX_PROPERTIES = 7;
    protected static final int INDEX_EXTENSIONS = 12;
    protected static final int INDEX_FILTERS = 22;
    protected static final int INDEX_ERRORS = 29;
    protected static final int INDEX_ACTIONS = 37;
    
    protected static final String SECTION_PROPERTIES = "properties";
    protected static final String SECTION_ACTIONS = "actions";
    protected static final String SECTION_FILTERS = "filters";
    protected static final String SECTION_ERRORS = "errors";
    protected static final String SECTION_EXTENSIONS = "extensions";
            
    private static final Logger log = LoggerFactory.getLogger(Showcase.class);
    
    protected List<String> mappingShowcase;
    
    public Showcase() throws IOException {
        String content = getFile("/test.wm");
        mappingShowcase = Arrays.asList(content.split("\n"));
    }

    protected String getFile(String name) throws IOException {
        InputStream stream = Showcase.class.getClassLoader().getResourceAsStream(name);
        String content = IOUtils.toString(stream);
        content = content.replace("/test", "");
        if (content.contains("#L" + "%\n */\n")) {
            content = StringUtils.substringAfter(content, "#L" + "%\n */\n");
        }
        if (content.contains("#L" + "%\n  -->\n")) {
            content = StringUtils.substringAfter(content, "#L" + "%\n  -->\n");
        }
        if (content.contains("# #L" + "%\n###\n")) {
            content = StringUtils.substringAfter(content, "# #L%\n###\n");
        }
        if (content.contains("<%--\n  #%" + "L")) {
            content = StringUtils.substringAfter(content, "  #L" + "%\n  --%>\n");
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
                "language", "fr",
                "path_demo", Arrays.asList(
                    "/test/hello"
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
                        "/test/helloModel"
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
                    "/test/helloParameters?who=you",
                    "/test/helloParameters?who=me"
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
                    "/test/view"
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
                    "/test/url"
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
                    "/test/act"
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
                    "/test/dynamic/get",
                    "/test/dynamic/set"
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
                    "/test/helloDefaultParameters",
                    "/test/helloDefaultParameters?who=other"
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
                    "/test/path/log?value=42"
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
                    "/test/other/repeat?number=6",
                    "/test/other/repeat?number=3"
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
                    "/test/parse"
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
                    "/test/service"
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
                    "/test/npe"
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
                    "/test/content"
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
                    "/test/stream"
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
                    "/test/xml"
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
                    "/test/json"
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
                    "/test/script"
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
                    "/test/first"
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
                    "/test/index"
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
                    "/test/save"
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
                    "/test/nocontent"
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
                    "/test/forbidden"
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
                    "/test/load"
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
                    "/test/message"
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
                    "/test/calc"
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
                    "/test/readme",
                    "/test/img"
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
                    "/test/form"
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
                    "/test/pattern/aaaa?value=aaaa",
                    "/test/pattern/baaa?value=baaaa"
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
                    "/test/page"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 38, 3)),
                    getJavaContent("Layout.java"),
                    getPageContent("page.jsp"),
                    getPageContent("author.jsp"),
                    getPageContent("footer.html")
                )
        );
    }
    
    public Render upload() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/upload"
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
                    "/test/file"
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
                    "/test/create?book.isbn=007&book.title=James%20Bond",
                    "/test/create?book.isbn=007&book.title=James",
                    "/test/comment?book.isbn=007&book.comment=cool",
                    "/test/comment?book.isbn=007",
                    "/test/search?query=007",
                    "/test/search",
                    "/test/info?book.isbn=007"
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
                    "/test/blog/index"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, false, false, false)
                        .addContent(getMapping(SECTION_EXTENSIONS, INDEX_EXTENSIONS + 1, 1)),
                    new FileContent("/src/main/resources/blog.wm", getFile("blog.wm")),
                    getPageContent("blog.jsp")
                )
        );
    }
    
    public Render wikipedia() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/wikipedia/tutu",
                    "/test/wikipedia/tata"
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
                    "/test/text?file=readme",
                    "/test/text?file=changelog"
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
                    "/test/notfound"
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
                    "/test/media"
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
                    "/test/login?name=john&passwd=azerty",
                    "/test/login?user.name=john&user.passwd=azerty",
                    "/test/login?username=john&userpasswd=azerty",
                    "/test/auth?name=john&passwd=azerty"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getFirstMapping(SECTION_ACTIONS, INDEX_ACTIONS + 55, 2))
                        .addContent(getContentMapping(INDEX_ACTIONS + 91, 2)),
                    getJavaContent("Auth.java"),
                    getJavaContent("User.java")
                )
        );
    }
    
    public Render helloView() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/helloView?name=John",
                    "/test/helloView?name=Jack"
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
                    "/test/spring"
                ),
                
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", getFile("spring.wm")),
                    getJavaContent("Spring.java"),
                    getJavaContent("Bean.java"),
                    getPageContent("spring.jsp")
                ) 
        );
    }
    
    public Render select() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/select?param=value",
                    "/test/select"
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
                    "/test/async"
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
                    "/test/template"
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
                    "/test/context"
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
                    "/test/stats"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, false, false, false)
                        .addContent(getMapping(SECTION_EXTENSIONS, INDEX_EXTENSIONS + 4, 1)),
                    new FileContent("/src/main/resources/stats.wm", getFile("META-INF/stats.wm")),
                    getJavaContent("Stats.java")
                )
        );
    }
    
    public Render application() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/application"
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
                    "/test/internal"
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
                    "/test/contact/view",
                    "/test/company/view"
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
    
    public Render cookie() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/cookie/create?secured=false",
                    "/test/cookie/create?secured=true"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 76, 2)),
                    getJavaContent("CookieService.java")
                )
        );
    }
    
    public Render cookieObject() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/cookie/object"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 78, 4)),
                    getJavaContent("CookieObject.java"),
                    getJavaContent("UserCookie.java"),
                    getPageContent("cookieObject.jsp")
                )
        );
    }
    
    public Render sitemesh() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/sitemesh/content"
                ),
                
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", getFile("sitemesh.wm")),
                    getPageContent("content.html"),
                    getPageContent("decorator.html")
                )
        );
    }

    public Render global() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/global/hello"
                ),
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", getFile("/global.wm")),
                    getJavaContent("GlobalListener.java"),
                    getJavaContent("HelloParameters.java"),
                    getPageContent("helloParameters.jsp")
                )
        );
    }
        
    public Render injector() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/config"
                ),
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", getFile("/injector.wm")),
                    getJavaContent("InjectorListener.java"),
                    getJavaContent("Config.java"),
                    getJavaContent("ConfigAction.java")
                )
        );
    }
        
    public Render converter() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/jsonelement?element=%7Btest=%22value%22%7D"
                ),
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", getFile("/converter.wm")),
                    getJavaContent("ConverterListener.java"),
                    getJavaContent("JsonElementAction.java")
                )
        );
    }

    public Render jpa() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/note"
                ),
                
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", getFile("note.wm")),
                    new FileContent("/META-INF/persistence.xml", getFile("showcase-persistence.xml")),
                    getJavaContent("Note.java"),
                    getPageContent("note.jsp")
                )
        );
    }

    public Render clientSession() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/session/store?value=info"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 82, 2)),
                    getJavaContent("Session.java")
                )
        );
    }

    public Render properties() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/echo"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_PROPERTIES, INDEX_PROPERTIES + 1, 2))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 84, 1)),
                    getJavaContent("Echo.java")
                )
        );
    }
    
    public Render forward() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/forward"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getFirstMapping(SECTION_ACTIONS, INDEX_ACTIONS + 86, 1))
                        .addContent(getContentMapping(INDEX_ACTIONS + 1, 1)),
                    getJavaContent("Hello.java"),
                    getPageContent("hello.jsp")
                )
        );
    }

    public Render dynamicForward() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/forward/dynamic/get",
                    "/test/forward/dynamic/set"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 88, 3)),
                    getJavaContent("Dynamic.java")
                )
        );
    }

    public Render shiro() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/menu"
                ),
                
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", getFile("shiro.wm")),
                    new FileContent("src/main/resources/shiro.properties", getFile("shiro.properties")),
                    getPageContent("menu.jsp"),
                    getPageContent("admin_index.jsp"),
                    getPageContent("guest_index.jsp"),
                    getPageContent("login.jsp"),
                    getPageContent("forbidden.jsp")
                )
        );
    }

    public Render wsText() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/echoChat/index"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 93, 2)),
                    getJavaContent("EchoChat.java"),
                    getPageContent("echoChat.jsp")
                )
        );
    }

    public Render wsJSON() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/ping/index"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false)
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 95, 2)),
                    getJavaContent("Ping.java"),
                    getPageContent("ping.jsp")
                )
        );
    }
    
    public Render chain() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/chain/log?value=42"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, true, false) 
                        .addContent(getMapping(SECTION_FILTERS, INDEX_FILTERS + 5, 1))
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 97, 1)),
                    getJavaContent("Chain.java"),
                    getJavaContent("Log.java")
                )
        );
    }
    
    public Render api() throws IOException {
        String section = "[config]\n";
        section += "package.base=org.debux.webmotion.showcase\n";
        section += "default.render=org.debux.webmotion.server.render.DefaultRender\n";
        
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/api"
                ),
                "files", Arrays.asList(
                    new FileContent("/src/main/resources/mapping", section + "\n")
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 98, 1)),
                    getJavaContent("Api.java")
                )
        );
    }
    
    public Render hellos() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/hellos?names=me&names=you",
                    "/test/hellos?names[0]=we&names[1]=other"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 99, 1)),
                    getJavaContent("Hellos.java"),
                    getPageContent("helloParameters.jsp")
                )
        );
    }
    
    public Render helloNames() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/helloNames?names.values[0]=me&names.values[1]=you"
                ),
                
                "files", Arrays.asList(
                    getConfig(true, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 100, 1)),
                    getJavaContent("HelloNames.java"),
                    getPageContent("helloParameters.jsp")
                )
        );
    }
    
    public Render feed() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/news/rss",
                    "/test/news/atom"
                ),
                
                "files", Arrays.asList(
                    getConfig(false, true, false, false) 
                        .addContent(getMapping(SECTION_ACTIONS, INDEX_ACTIONS + 101, 2)),
                    getJavaContent("News.java")
                )
        );
    }
    
    public Render conventionController() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/hello/convention/says"
                ),
                
                "files", Arrays.asList(
                    getJavaContent("HelloConvention.java")
                )
        );
    }
    
    public Render conventionAllFilter() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/test/hello/convention/says"
                ),
                
                "files", Arrays.asList(
                    getJavaContent("HelloConvention.java"),
                    getJavaContent("Security.java")
                )
        );
    }
    
    public Render conventionPackageFilter() throws IOException {
        return renderView("showcase.jsp",  
                "path_demo", Arrays.asList(
                    "/sub/hello/convention/says"
                ),
                
                "files", Arrays.asList(
                    getJavaContent("sub/HelloConvention.java"),
                    getJavaContent("sub/Security.java"),
                    getJavaContent("Security.java")
                )
        );
    }
    
}
