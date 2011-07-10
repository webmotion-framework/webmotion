Exemple
=======

Basique
-------

Petit exemple pour commencer et voir la simplicité.

web.xml ::

 <web-app version="3.0"
    xmlns="http://java.sun.com/xml/ns/javaee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
    http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" />

mapping ::

 [config]
 package.views=org.debux.webmotion.test.views
 package.actions=org.debux.webmotion.test.actions

 [actions]
 # Appel la méthode index de la classe Calc sur l'url /webmotion-test/deploy/index
 GET           /index                               Calc.index
 
 # Appel la méthode add de la classe Calc sur l'url /webmotion-test/deploy/add
 POST          /add                                 Calc.add

Calc.java ::

 package org.debux.webmotion.test.actions;
 
 public class Calc extends WebMotionAction {
    
    public Render index() {
        return renderView("index.jsp");
    }
    
    public Render add(int value, int other) {
        int result = value + other;
        return renderView("result.jsp", "result", result);
    }
    
 }

index.jsp ::

 <html>
    <body>
        <h2>Calc</h2>

        <form method="POST" action="/webmotion-test/deploy/add">
            Value : <input name="value"/>
            Other : <input name="other"/>
            <input type="submit" value="add"/>
        </form>
    </body>
 </html>

result.jsp ::

 <html>
    <body>
        <h2>Result = ${result}</h2>
    </body>
 </html>

AJAX
----

Exemple d'appel AJAX avec un rendu par template.

mapping ::

 [config]
 package.views=org.debux.webmotion.test.views
 package.actions=org.debux.webmotion.test.actions
 
 [actions]
 GET           /index                                  Test.index
 GET           /template                               Test.template

Test.java ::

 package org.debux.webmotion.test.actions;

 public class Test extends WebMotionAction {
    
    public Render index() {
        return renderView("index.jsp");
    }
    
    public Render template() {
        return renderTemplate("template.jsp", 
                "key1", "value1",
                "key2", "value2"
            );
    }
    
 }

index.jsp ::

 <a href="#" onclick="test();">Template</a><br/>

 <div id="template">
    Empty
 </div>

 <script>
    function test() {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() { 
            if(xhr.readyState == 4 && xhr.status == 200) {
                var node = document.getElementById("template");
                node.innerHTML =  xhr.responseText;
            } 
        }; 
        
        xhr.open("GET", "/webmotion-test/deploy/template", true);                
        xhr.send(); 
    }
 </script>

template.jsp ::

 <p>Value = ${key1}</p>
 <p>Value = ${key2}</p>
 </code>

Gestion de fichier
-----------------

Exemple sur l'envoi d'un fichier sur le serveur avec suivi de la progression en AJAX.

mapping ::

 [config]
 package.views=org.debux.webmotion.test.views
 package.actions=org.debux.webmotion.test.actions
 
 [actions]
 *           /{class}/{method}                               {class}.{method}

Fileupload.java ::

 public class Fileupload extends WebMotionAction {
    
    private static final Logger log = LoggerFactory.getLogger(Fileupload.class);
    
    public Render index() {
        return renderView("index.jsp");
    }
    
    public Render progress() {
        HttpContext context = getContext();
        HttpSession session = context.getSession();
        FileProgressListener listener = 
            (FileProgressListener) session.getAttribute(FileProgressListener.SESSION_ATTRIBUTE_NAME);
        log.info("listener = " + listener);
        return renderJSON(listener);
    }
    
    public Render upload(File file) {
        // Display in frame, normaly do a redirect by JS next page
        return renderView("finish.jsp");
    }
    
 }

index.html ::

 <html>
    <body>
        <h2>File Upload</h2>

        <!-- Frame use not block process -->
        <iframe name="uploadFrame" height="0" width="0" frameborder="0" scrolling="yes"></iframe>

        <form method="POST" action="/webmotion-test/deploy/fileupload/upload" 
              target="uploadFrame" enctype="multipart/form-data" onsubmit="progress()">
            File : <input name="file" type="file"/>
            <input type="submit" value="upload"/>
        </form>
        
        Progress : <span id="progress"></span>
        <script>
            function progress() {
                var xhr = new XMLHttpRequest();
                xhr.onreadystatechange = function() { 
                    if(xhr.readyState == 4 && xhr.status == 200) {
                        var response = xhr.responseText;
                        if(response) {
                            var json = eval('(' + response + ')'); 
                            
                            var node = document.getElementById("progress");
                            node.innerHTML =  json.bytesRead + " / " + json.contentLength;
                        }
                        
                        if(!json.bytesRead || !json.contentLength || json.bytesRead != json.contentLength) {
                            setTimeout(progress, 100);
                        }
                    } 
                }; 

                xhr.open("GET", "/webmotion-test/deploy/fileupload/progress", true);                
                xhr.send(); 
            }
        </script>
    </body>
 </html>
