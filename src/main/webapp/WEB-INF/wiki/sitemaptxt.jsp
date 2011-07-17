<%@ page contentType="text/plain; charset=UTF-8" %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><c:forEach var="page" items="${requestScope.map}">${page}
</c:forEach>
