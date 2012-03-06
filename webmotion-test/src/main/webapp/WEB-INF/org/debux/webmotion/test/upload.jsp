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
        <title>Upload</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.js"></script>
    </head>
    <body>
        
        <!-- Frame use not block process -->
        <iframe id="uploadFrame" name="uploadFrame" height="0" width="0" frameborder="0" scrolling="yes"></iframe>
       
        <form method="POST" action="<c:url value="/test/finish"/>"
               target="uploadFrame" enctype="multipart/form-data" onsubmit="progress()">
            
            File : <input name="file" type="file"/>
            <input type="submit" value="upload"/>
        </form>
        
        Progress : <span id="progress"></span>
        
        <script>
           function progress() {
               $.ajax({
                    type : "GET",
                    url: "<c:url value="/test/progress"/>",
                    dataType: 'json',
                    success: function(progression){
                        var bytesRead = progression.bytesRead;
                        var contentLength = progression.contentLength;
                        
                        $('#progress').text(bytesRead + " / " + contentLength);
                        
                        if(!bytesRead || !contentLength || bytesRead != contentLength) {
                            setTimeout(progress, 250);
                        }jsonp
                    }
                });
           }
           
           $('#uploadFrame').load(
               function() {
                   $('#progress').text("finish");
               }
           );
        </script>
      
    </body>
</html>
