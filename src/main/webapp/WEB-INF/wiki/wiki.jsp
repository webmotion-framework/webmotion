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
        
        <script type="text/javascript" src="/wikimotion/js/generated_toc.js"></script>

        <link rel="stylesheet" href="/wikimotion/css/classic.css" type="text/css"  media="screen">
        <link href='http://fonts.googleapis.com/css?family=Droid+Sans:regular,bold&v1' rel='stylesheet' type='text/css'>
    </head>

    <body>

        <header>
            <div class="logo">WikiMotion</div>
            <nav>
                <jsp:include page="/deploy/include/menu" />
            </nav>
        </header>

        <div id="main">
            <div id="main_toc">
                <h2>Table Of Contents</h2>
                <div id="generated-toc" class="generate_from_h1 generate_for_main_content"></div>
                <div class="action">
                    <button value="edit" onclick="window.location='/wikimotion/deploy/edit/${requestScope.url}'">Edit page</button>
                </div>
            </div>
            <div id="main_content">
                <jsp:include page="/deploy/include/${requestScope.url}" />
            </div>
        </div>
        
        <footer>
            <nav>
                <jsp:include page="/deploy/include/menu" />
            </nav>
            <div>Powerd by WikiMotion and WebMotion</div>
        </footer>

    </body>

</html>
