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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>CookieObject</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <form method="GET" action="<c:url value="./object/read"/>"><input type="submit" value="Read"/></form>
        <form method="GET" action="<c:url value="./object/create"/>"><input type="text" name="value" value="test"/><input type="submit" value="Create"/></form>
        <form method="GET" action="<c:url value="./object/remove"/>"><input type="submit" value="Remove"/></form>
    </body>
</html>
