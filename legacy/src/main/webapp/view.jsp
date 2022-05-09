<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/download" method="get">
    <%
        Map<String, String[]> parameters = request.getParameterMap();
        for(String p : parameters.keySet()) {
            String v = request.getParameter(p);
            out.println("<input type=\"hidden\" name=\"" + p + "\" value=\"" + v + "\">");
        }
    %>

    <label for="nameformat">Format für Mitarbeiter:</label>
    <select id="nameformat" name="nameformat">
        <option value="none">nicht übernehmen</option>
        <option value="abbr">Kürzel</option>
        <option value="first_last">Vorname Nachname</option>
        <option value="first_last_abbr" selected>Vorname Nachname (Kürzel)</option>
        <option value="last_first">Nachname, Vorname</option>
        <option value="last_first_abbr">Nachname, Vorname (Kürzel)</option>
    </select>
    <br>
    <label for="roomformat">Format für Raumangaben:</label>
    <select id="roomformat" name="roomformat">
        <option value="none">nicht übernehmen</option>
        <option value="abbr">Kürzel</option>
        <option value="full_name">Langbezeichnung</option>
        <option value="full_name_and_abbr" selected>Langbezeichnung (Kürzel)</option>
    </select>
    <br>
    <input type="reset" value="Zurücksetzen">
    <input type="submit" value="Konvertieren">
</form>
</body>
</html>
