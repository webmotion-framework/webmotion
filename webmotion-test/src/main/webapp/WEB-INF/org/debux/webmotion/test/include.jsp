<!--
  #%L
  Webmotion website
  
  $Id$
  $HeadURL$
  %%
  Copyright (C) 2011 - 2015 Debux
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
<html>
    <head>
        <title>Lorem ipsum</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <div class="header">
            <jsp:include page="/include/header">
                <jsp:param name="title" value="Title lorem ipsum" />
            </jsp:include>
        </div>
        <div class="author">
            <jsp:include page="./author.jsp"/>
        </div>
        <div class="content">
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus.</p>
        </div>
        <div class="footer">
            <jsp:include page="/include/footer"/>
        </div>
    </body>
</html>
