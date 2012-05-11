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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="bundle.wikimotion"/>

<link rel="stylesheet" href="<c:url value="/static/js/codemirror-2.11/lib/codemirror.css"/>">
<script src="<c:url value="/static/js/codemirror-2.11/lib/codemirror.js"/>"></script>

<script src="<c:url value="/static/js/codemirror-2.11/mode/xml/xml.js"/>"></script>
<script src="<c:url value="/static/js/codemirror-2.11/mode/javascript/javascript.js"/>"></script>
<script src="<c:url value="/static/js/codemirror-2.11/mode/css/css.js"/>"></script>
<script src="<c:url value="/static/js/codemirror-2.11/mode/htmlmixed/htmlmixed.js"/>"></script>
<script src="<c:url value="/static/js/codemirror-2.11/mode/stex/stex.js"/>"></script>
<script src="<c:url value="/static/js/codemirror-2.11/mode/rst/rst.js"/>"></script>

<link rel="stylesheet" href="<c:url value="/static/js/codemirror-2.11/mode/rst/rst.css"/>">
<link rel="stylesheet" href="<c:url value="/static/js/codemirror-2.11/theme/default.css"/>">

<div id="wiki">
    <form id="wiki_edit" action="<c:url value="${url}"/>" method="POST">
        <input type="hidden" name="sub" value="save" />
        
        <textarea id="content" name="content"><jsp:include page="${url}?sub=source" /></textarea>

        <div id="wiki_action" class="form-actions">
            <a class="btn btn-primary" href="#" onclick="$('#wiki_edit').submit();"><fmt:message key="wiki.save"/></a>
            <a class="btn btn-primary" href="#" onclick="preview();"><fmt:message key="wiki.preview"/></a>
            <a class="btn btn-primary" href="<c:url value="${requestScope.url}"/>"><fmt:message key="wiki.cancel"/></a>
        </div>
    </form>

    <div id="preview"></div>
</div>

<c:url value="${url}" var="previewUrl" />
            
<script type="text/javascript">
    var modes = {
        html : "htmlmixed",
        rst : "rst",
        tex : "stex"
    };

    $("#preview").hide();
    createEditor(modes["${type}"]);

    function createEditor(type) {
        editor = CodeMirror.fromTextArea(
                        $("#content")[0], {
                            lineNumbers: true,
                            mode : type
                        }
                     );
    }

    function preview() {
        var type = "${type}";
        $("#preview").show();

        $.ajax({
                url : '${previewUrl}',
                type: 'POST',
                data: {
                    sub : 'preview',
                    type: type,
                    content: editor.getValue()
                },
                dataType: "html",
                
                success: function(value) {
                    value = value || "no response text";
                    $("#preview").html(value);
                },
                fail: function() {
                    $("#preview").html("Something went wrong...");
                }
            });
    }
</script>
