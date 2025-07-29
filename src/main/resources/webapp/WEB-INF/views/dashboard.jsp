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
<%@ include file="00-header.jsp" %>
<style>
	.tileContainer {
		display: contents;
	} 

	.tileRow1 {
		display: flex;
		flex-direction: column;
		justify-content: space-between; /* Ensures consistent height */
		align-items: center;
		text-align: center; /* Optional: Center align content */
		padding: 1rem;
		border: 1px solid #ccc; /* Optional: Add borders */
		background-color: #f9f9f9; /* Optional: Add a background color */
		box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1); /* Optional: Add shadow */
	}

</style>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<link rel="stylesheet" href="https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css">
<div class="content" id="mainContent">
<div class="tileWrapper">
	<div class="tileContainer">
		<a class="tileRow1" href="${ctxPath}/manager/chargepoints">
			Number of<br>Charge Points
			<span class="base formatNumber">${stats.numChargeBoxes}</span>
		</a>
		<a class="tileRow1" href="${ctxPath}/manager/dashboard/ocppJsonStatus">
			Connected<br>Charge Points
			<span class="baseTable">
				<span class="baseRow">
					<span class="baseCell">OCPP 1.6 :</span>
					<span class="baseCell formatNumber">${stats.numOcpp16JChargeBoxes}</span>
				</span>
			</span>
		</a>
		<a class="tileRow1 heartbeat-tile" href="${ctxPath}/manager/chargepoints">
			Heartbeats
			<span class="baseTable">
				<span class="baseRow">
					<span class="baseCell">Today :</span>
					<span class="baseCell formatNumber">${stats.heartbeatToday}</span>
				</span>
				<span class="baseRow">
					<span class="baseCell">Yesterday :</span>
					<span class="baseCell formatNumber">${stats.heartbeatYesterday}</span>
				</span>
				<span class="baseRow">
					<span class="baseCell">Earlier :</span>
					<span class="baseCell formatNumber">${stats.heartbeatEarlier}</span>
				</span>
			</span>
		</a>
		<a class="tileRow1" href="${ctxPath}/manager/reservations">
			Active  <br>Reservations
			<span class="base formatNumber">${stats.numReservations}</span>
		</a>
		<a class="tileRow1" href="${ctxPath}/manager/transactions">
			Active  <br>Transactions
			<span class="base formatNumber">${stats.numTransactions}</span>
		</a>
	</div>
	<br>
</div>
<div>
		<table class="res action" id="partialContent">
			<thead>
			<tr>
				<th data-sort="string" onclick="sortTable(0)">ChargeBox ID</th>
				<th data-sort="string">Description</th>
				<th data-sort="string">Connector</th>
				<th data-sort="date">Last Heartbeat</th>
				<th data-sort="string">Status</th>
				<th>Message</th>
			</tr>
			</thead>
			<tbody>
				<c:forEach items="${connectorStatusList}" var="cs">
					<tr>
						<td>
							<a href="${ctxPath}/manager/chargepoints/details/${cs.chargeBoxPk}">${cs.chargeBoxId}</a>
							<c:if test="${cs.jsonAndDisconnected}">
								<a class="tooltip" href="#"><img src="${ctxPath}/static/images/offline-icon.svg" style="height: 1em">
									<span>This JSON charge point is currently disconnected. The status information of its
									connectors might be not up-to-date.</span>
								</a>
							</c:if>
						</td>
						<td>${cs.description}</td>
						<td>${cs.connectorId}</td>
						<td data-sort-value="${cs.statusTimestamp.millis}">${cs.timeStamp}</td>
						<td>
							<c:choose>
								<c:when test="${cs.jsonAndDisconnected}">
								</c:when>
								<c:otherwise>
									<encode:forHtml value="${cs.status}" />
								</c:otherwise>
							</c:choose>
						</td>
						<td><button type="button" class="greenView" onclick="viewLogResults('${cs.chargeBoxId}')">View</button></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
 <!-- jQuery -->
 <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
 <!-- DataTables JS -->
 <script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
<script>
	 function viewLogResults(chargeBoxId) {
        // Construct the URL to fetch logs based on the context path and chargeBoxId
		// alert(chargeBoxId);
        const url = `/log/charger/`+chargeBoxId;
        
        // Open the URL in a new window or tab
        window.open(url, '_blank');
    }
	
	$(document).ready(function(){
		// Function to refresh partial content
		function refreshContent() {
			// Use setInterval to refresh content every 1 second (1000 milliseconds)
			setInterval(function() {
				// Load new content using AJAX
				$.ajax({
					url: window.location.href, // Fetch the same URL
					type: 'GET',
					success: function(data) {
						// Extract and replace only the relevant part of the content
						let newContent = $(data).find('#mainContent').html();
						$('#mainContent').html(newContent);
					},
					error: function(xhr, status, error) {
						console.error('Failed to reload content:', error);
					}
				});
			}, 1000); // Refresh every 1000 milliseconds (1 second)
		}
		// Call the function on page load
		refreshContent();
		
	});
</script>

<div></div>
	
<%@ include file="00-footer.jsp" %>