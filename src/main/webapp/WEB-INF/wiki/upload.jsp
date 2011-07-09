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
        <link rel="icon" type="image/png" href="/wikimotion/img/collaboration.png" />
                
        <script type="text/javascript" src="/wikimotion/js/prototype.js"></script>
        
        <link rel="stylesheet" href="/wikimotion/css/classic.css" type="text/css"  media="screen">
        <link rel='stylesheet' href='http://fonts.googleapis.com/css?family=Droid+Sans:regular,bold&v1' type='text/css'>
    </head>

    <body>

        <div id="header">
            <div class="logo">WikiMotion</div>
            <div class="nav">
                <jsp:include page="/deploy/include/menu" />
            </div>
        </div>

        <div id="main">
            <div id="main_content">
                <h1>Edit page</h1>
                <form action="/wikimotion/deploy/upload" method="POST" enctype="multipart/form-data">
                    <div>
                        <label for="file">File : </label>
                        <input id="file" name="file" type="file" value="" onchange="$('mediaName').value=$('file').value"/>
                    </div>
                    <div>
                        <label for="nameSpace">Name space : </label>
                        <input name="nameSpace" value=""/>
                    </div>
                    <div>
                        <label for="mediaName">Media name : </label>
                        <input id="mediaName" name="mediaName" value=""/>
                    </div>
                    
                    <button type="submit" value="upload">Upload</button>
                    <button type="button" value="cancel" onclick="history.back();">Cancel</button>
                </form>
            </div>
        </div>

        <div id="footer">
            <div class="nav">
                <jsp:include page="/deploy/include/menu" />
            </div>
            <div>Powerd by WikiMotion and WebMotion</div>
        </div>

    </body>

</html>
