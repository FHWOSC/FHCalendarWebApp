<%@ page import="de.yonux.fhcalendarwebapp.task.Tasks" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %></h1>
<br/>
<form action="${pageContext.request.contextPath}/view.jsp" method="get">
    <label for="url">Link zum Kalender:</label><input type="url" id="url" name="url">
    <input type="submit" value="Konvertieren!">
</form>
<br>
<h1>Standard</h1>
<%= Tasks.runCourseFetcher() %>
<br><br>
<h1>Benutzerdefiniert</h1>
<%= Tasks.runCustomizerFetcher() %>
</body>
</html>