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
<html>
    <body>
        <h2>File Upload</h2>

        <!-- Frame use not block process -->
        <iframe name="uploadFrame" height="0" width="0" frameborder="0" scrolling="yes"></iframe>

        <form method="POST" action="/webmotion-test/deploy/fileupload/upload" target="uploadFrame" enctype="multipart/form-data" onsubmit="progress()">
            File : <input name="file" type="file"/>
            <input type="submit" value="upload"/>
        </form>
        
        Progress : <span id="progress"></span>
        <script>
            function progress() {
                var xhr = new XMLHttpRequest();
                xhr.onreadystatechange = function() { 
                    if(xhr.readyState == 4 && xhr.status == 200) {
                        var response = xhr.responseText;
                        if(response) {
                            var json = eval('(' + response + ')'); 
                            
                            var node = document.getElementById("progress");
                            node.innerHTML =  json.bytesRead + " / " + json.contentLength;
                        }
                        
                        if(!json.bytesRead || !json.contentLength || json.bytesRead != json.contentLength) {
                            setTimeout(progress, 100);
                        }
                    } 
                }; 

                xhr.open("GET", "/webmotion-test/deploy/fileupload/progress", true);                
                xhr.send(); 
            }
        </script>
    </body>
</html>
