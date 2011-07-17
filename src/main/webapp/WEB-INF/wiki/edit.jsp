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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>

    <head>
        <meta charset="utf-8">
        <title>Edit page</title>
        <link rel="icon" type="image/png" href="<c:url value="/img/collaboration.png"/>" />
        
        <script type="text/javascript" src="<c:url value="/js/prototype.js"/>"></script>
        
        <link rel="stylesheet" href="<c:url value="/js/codemirror-2.11/lib/codemirror.css"/>">
        <script src="<c:url value="/js/codemirror-2.11/lib/codemirror.js"/>"></script>
    
        <script src="<c:url value="/js/codemirror-2.11/mode/xml/xml.js"/>"></script>
        <script src="<c:url value="/js/codemirror-2.11/mode/javascript/javascript.js"/>"></script>
        <script src="<c:url value="/js/codemirror-2.11/mode/css/css.js"/>"></script>
        <script src="<c:url value="/js/codemirror-2.11/mode/htmlmixed/htmlmixed.js"/>"></script>
        <script src="<c:url value="/js/codemirror-2.11/mode/stex/stex.js"/>"></script>
        <script src="<c:url value="/js/codemirror-2.11/mode/rst/rst.js"/>"></script>
        <link rel="stylesheet" href="<c:url value="/js/codemirror-2.11/mode/rst/rst.css"/>">

        <link rel="stylesheet" href="<c:url value="/js/codemirror-2.11/theme/default.css"/>">

        <link rel="stylesheet" href="<c:url value="/css/classic.css"/>" type="text/css"  media="screen">
    </head>

    <body>

        <div id="header">
            <fmt:setBundle basename="config"/>
            <div class="logo"><fmt:message key="site.name"/></div>
            <div class="nav">
                <jsp:include page="/deploy/include/menu_header" />
            </div>
        </div>

        <div id="main">
            <div id="main_content">
                <h1>Edit page</h1>
                <form action="<c:url value="/deploy/save"/>" method="POST">
                    <div id="selector">
                        <select id="select" name="type">
                            <option value="html">HTML</option>
                            <option value="rst">Rst</option>
                            <option value="tex">LaTex</option>
                        </select>
                        <button type="button" value="create" onclick="createEditor(modes[$('select').value]);">Create page</button>
                    </div>

                    <div id="edit" style="display: none;">
                        <textarea id="content" name="content" style="display: none"><jsp:include page="/deploy/content/${requestScope.url}" /></textarea>

                        <div>
                            <label for="nameSpace">Name space : </label>
                            <input name="nameSpace" value="${requestScope.nameSpace}"/>
                        </div>
                        <div>
                            <label for="pageName">Page name : </label>
                            <input name="pageName" value="${requestScope.pageName}"/>
                        </div>

                        <button type="submit" value="save">Save</button>
                        <button type="button" value="preview" onclick="preview();">Preview</button>
                        <button type="button" value="cancel" onclick="history.back();">Cancel</button>
                    </div>
                </form>

                <div id="preview"></div>
            </div>
        </div>

        <c:url value="/deploy/preview" var="previewUrl" />
        <script type="text/javascript">
            var modes = {
                html : "htmlmixed",
                rst : "rst",
                tex : "stex"
            };

            $("preview").style.display = "none";

            if($("content").value != "") {
                createEditor(modes["${requestScope.type}"]);
            }

            function createEditor(type) {
                $("edit").style.display = "block";
                editor = CodeMirror.fromTextArea(
                                $("content"), {
                                    lineNumbers: true,
                                    mode : type
                                }
                             );

                $("selector").style.display = "none";
            }

            function preview() {
                var type = "${requestScope.type}";
                if(type == "") {
                    type = $('select').value;
                }

                $("preview").style.display = "block";

                new Ajax.Request('${previewUrl}',
                    {
                        method:'post',
                        parameters: {type: type, content: editor.getValue()},
                        onSuccess: function(transport) {
                            var response = transport.responseText || "no response text";
                            $("preview").innerHTML = response;
                        },
                        onFailure: function() {
                            $("preview").innerHTML = "Something went wrong...";
                        }
                    });
            }
        </script>

        <div id="footer">
            <div class="nav">
                <jsp:include page="/deploy/include/menu_footer" />
            </div>
            <div>Powerd by WikiMotion and WebMotion</div>
        </div>

    </body>

</html>
