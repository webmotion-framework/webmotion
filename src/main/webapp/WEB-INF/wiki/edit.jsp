<!--
  #%L
  Webmotion in test
  
  $Id$
  $HeadURL$
  %%
  Copyright (C) 2011 Debux
  %%
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as 
  published by the Free Software Foundation, either version 3 of the 
  License, or (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Lesser Public License for more details.
  
  You should have received a copy of the GNU General Lesser Public 
  License along with this program.  If not, see
  <http://www.gnu.org/licenses/lgpl-3.0.html>.
  #L%
  -->
<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

    <head>
        <meta charset="utf-8">
        <title>WikiMotion</title>
        <link rel="stylesheet" href="/wikimotion/css/simple.css" type="text/css"  media="screen">
        <script type="text/javascript" src="/wikimotion/js/codemirror/codemirror.js"></script>
        <link rel="stylesheet" href="/wikimotion/js/codemirror/codemirror.css">
        <link rel="stylesheet" href="/wikimotion/js/codemirror/default.css">
    </head>

    <body>

        <header>
            <div class="logo">WikiMotion</div>
            <nav>
                <jsp:include page="/deploy/include/menu" />
            </nav>
        </header>

        <div class="content">
            <div id="selector">
                <select id="mode">
                    <option value="htmlmixed">HTML</option>
                    <option value="rst">Rst</option>
                    <option value="stex">LaTex</option>
                </select>
                <span onclick="createEditor();">Create page</span>
            </div>
            
            <textarea id="code" style="display: none">
<jsp:include page="${requestScope.url}" />
            </textarea>
            <script type="text/javascript">
                
                var selector = document.getElementById("selector");
                var mode = document.getElementById("mode");
                var code = document.getElementById("code");
                
                function createEditor() {
                    var editor = CodeMirror.fromTextArea(
                                    code, {
                                        lineNumbers: true,
                                        mode : mode.value
                                    }
                                 );
                                    
                    selector.style.display = "none";
                }
            </script>
            
        </div>

        <footer>
            <nav>
                <jsp:include page="/deploy/include/menu" />
            </nav>
            <div>Powerd by WikiMotion and WebMotion</div>
        </footer>

    </body>

</html>
