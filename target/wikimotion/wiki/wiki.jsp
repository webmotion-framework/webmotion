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
<html lang="fr">

    <head>
        <meta charset="utf-8">
        <title>WikiMotion</title>
        <link rel="stylesheet" href="/webmotion-test/css/simple.css" type="text/css"  media="screen">
    </head>

    <body>

        <header>
            <div class="logo">WikiMotion</div>
            <nav>
                <jsp:include page="/deploy/display/menu" />
            </nav>
        </header>

        <%
            String url = "/deploy/display/" + (String) request.getAttribute("url");
        %>
        <div class="content">
            <jsp:include page="<%=url%>" />
        </div>

        <footer>
            <nav>
                <jsp:include page="/deploy/display/menu" />
            </nav>
            <div>Powerd by WikiMotion and WebMotion</div>
        </footer>

    </body>

</html>
