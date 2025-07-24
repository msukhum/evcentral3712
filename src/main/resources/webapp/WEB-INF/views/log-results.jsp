<%@ include file="00-header.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Filtered Log Messages</title>
</head>
<body>
    <h2>Filtered Log Messages</h2>
    <table border="1">
        <tr>
            <th>Log Message</th>
        </tr>
        <!-- Loop through each filtered message and display it in a table row -->
        <c:forEach var="message" items="${filteredMessages}">
            <tr>
                <td>${message}</td>
            </tr>
        </c:forEach>
    </table>
    <button onclick="window.close()">Close Window</button>
</body>
</html>
<%@ include file="00-footer.jsp" %>