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
<%@ page contentType="text/html; charset=UTF-8" errorPage="/deploy/error" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

    <head>
        <meta charset="utf-8">
        <title>WikiMotion</title>
        <link rel="icon" type="image/png" href="/wikimotion/img/collaboration.png" />
                
        <script type="text/javascript" src="/wikimotion/js/prototype.js"></script>
        <script type="text/javascript" src="/wikimotion/js/generated_toc.js"></script>

        <link rel="stylesheet" href="/wikimotion/css/classic.css" type="text/css"  media="screen">
        <link href='http://fonts.googleapis.com/css?family=Droid+Sans:regular,bold&v1' rel='stylesheet' type='text/css'>
    </head>

    <body>

        <div id="header">
            <div class="logo">WikiMotion</div>
            <div class="nav">
                <jsp:include page="/deploy/include/menu_header" />
            </div>
        </div>

        <div id="main">
            <div id="main_toc">
                <h2>Table Of Contents</h2>
                <div id="generated-toc" class="generate_from_h1 generate_for_main_content"></div>
                <div class="action">
                    <a href="/wikimotion/deploy/edit/${requestScope.url}"><img src="/wikimotion/img/consulting.png" alt="Modifier cette page" title="Modifier cette page" /></a>
                    <a href="/wikimotion/deploy/upload"><img src="/wikimotion/img/folder.png" alt="Transférer un média" title="Transférer un média" /></a>
                </div>
            </div>
            
            <div id="main_content">
                <jsp:include page="/deploy/include/${requestScope.url}" />
            </div>
        </div>
        
        <div id="footer">
            <div class="nav">
                <jsp:include page="/deploy/include/menu_footer" />
            </div>
            <div>Powered by WikiMotion and WebMotion</div>
        </div>

    </body>

</html>
