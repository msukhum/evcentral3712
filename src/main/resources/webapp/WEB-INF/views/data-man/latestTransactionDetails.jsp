<%--

    SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
    Copyright (C) 2013-2024 SteVe Community Team
    All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>
<%@ include file="../00-header.jsp" %>
<script type="text/javascript">
    $(document).ready(function() {
        <%@ include file="../snippets/sortable.js" %>
    });
</script>
<div class="content"><div>
    <center>
        <table id="details" class="cpd">
            <thead><tr><th>ChargeBox Overview</th><th></th></tr></thead>
            <tr><td>ChargeBox ID</td><td><a href="${ctxPath}/manager/chargepoints/details/${details.transaction.chargeBoxPk}">${details.transaction.chargeBoxId}</a></td></tr>
        </table>
    </center>
    <br>
    <section><span>Intermediate Meter Values</span></section>
    <table class="res">
        <thead>
        <tr>
            <th>Charging Details</th>
            <th>Location</th>
            <th data-sort="date">Value Timestamp</th>
            <th data-sort="int">Value</th>
            <%-- <th>Reading Context</th> --%>
            <%-- <th>Format</th> --%>
            <th>Unit</th>
            <th>Phase</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${details.values}" var="v">
            <tr>
                <td><encode:forHtml value="${v.measurand}" /></td>
                <td><encode:forHtml value="${v.location}" /></td>
                <td data-sort-value="${v.valueTimestamp.millis}">${v.valueTimestamp}</td>
                <td><encode:forHtml value="${v.value}" /></td>
                <%-- <td><encode:forHtml value="${v.readingContext}" /></td> --%>
                <%-- <td><encode:forHtml value="${v.format}" /></td> --%>
                <td><encode:forHtml value="${v.unit}" /></td>
                <td><encode:forHtml value="${v.phase}" /></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div></div>
<%@ include file="../00-footer.jsp" %>
