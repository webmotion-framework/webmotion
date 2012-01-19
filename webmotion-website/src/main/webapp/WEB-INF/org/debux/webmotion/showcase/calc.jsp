<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!--
  #%L
  Webmotion website
  
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
<!DOCTYPE html>
<html>
    <head>
        <title>Calc</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.js"></script>
    </head>
    <body>
        <form>
            Value : <input id="value" name="value"/>
            Other : <input id="other" name="other"/>
            <input type="button" value="add" onclick="add()"/>
        </form>
        
        <div id="result"></div>

        <script>
            function add() {
                $.ajax({
                    type : "POST",
                    dataType : "html",
                    url: "<c:url value="/showcase/action/add"/>",
                    data : {
                        value : $('#value').val(),
                        other : $('#other').val()
                    },
                    success: function(html){
                        $('#result').html(html);
                    }
                });
            }
        </script>

    </body>
</html>
