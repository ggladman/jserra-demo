var nodeCount = 0;
var nodeIds = [];
var nodeBalances = [];

var cy = null;

var timerMap = {};

function initialize() {
    var socket = new SockJS('/request');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/registrations', function(registration){
            console.log(registration);
            receiveRegistration(JSON.parse(registration.body));
        });
        stompClient.subscribe('/topic/receipts', function(receipt){
            console.log(receipt);
            receiveTransfer(JSON.parse(receipt.body));
        });
    });
    speakText("the server is online.");
    fadeInMain();
    setupGraph();
}

function receiveRegistration(registration) {
    document.getElementById('audio_chime').play();
    console.log("***** REGISTRATION: " + registration);

    // add user to node
    addUser(registration.username, registration.balance);
}

function receiveTransfer(transfer) {
    console.log(transfer);
    var messageBlock = transfer.message;
    
    if(!(messageBlock== null || messageBlock == "")){
        messageBlock = ", with the message '" + transfer.message + "'";
    }
    speakText(transfer.sender + " has sent $" + transfer.amount + " to " + transfer.recipient + messageBlock + ".");

    $("#recipienttable").find("tr").each(function() {
        var username = $(this).find(".username").html();
        var balance = Number($(this).find(".currbalance").html());
        console.log(transfer.amount);
        var sendAmount = Number(transfer.amount);
        console.log("username: " + username);
        // decrement the sender's balance
        if (username == transfer.sender) {
            console.log("match!");
            var newbalance = balance - sendAmount;
            console.log("new balance = " + newbalance)
            $(this).find(".currbalance").html(newbalance.toFixed(2));

            updateNodeBalance(username, newbalance);
        }
        // increment the sender's balance
        else if (username == transfer.recipient) {
            console.log("match!");
            var newbalance = balance + sendAmount;
            console.log("new balance = " + newbalance)
            $(this).find(".currbalance").html(newbalance);

            updateNodeBalance(username, newbalance);
        }

        cy.load( cy.elements('*').jsons() );
    });

    var htmlMessage = "<div class='message'>";
    htmlMessage += "<table>"
    for (prop in transfer) {
        console.log(prop);
        console.log(prop + " : " + transfer[prop]);
        htmlMessage += "<tr>"
        htmlMessage += "<td class='messageproperty'>" + prop + "</td>";
        htmlMessage += "<td class='messagevalue'>" + transfer[prop] + "</td>";
        htmlMessage += "</tr>";
    }
    htmlMessage += "</div>";
    $("#message_list").prepend(htmlMessage);

    activateLink(transfer.sender, transfer.recipient, transfer.amount);
}

function fadeInMain() {
    $("#main").fadeIn("slow");
}

function speakText(textToSpeak) {
    if ('speechSynthesis' in window) {
        var msg = new SpeechSynthesisUtterance(textToSpeak);
        // msg.voice = speechSynthesis.getVoices().filter(function(voice) { return voice.name == 'Pipe Organ'; })[0];
        speechSynthesis.speak(msg);
    }
}

function addUser(id, balance) {
    nodeIds.push(id);
    nodeBalances.push(balance);
    var label = formatLabel(id, balance);
    nodeCount++;

    var htmlMessage = "<tr class='recipient'>";
    htmlMessage += "<td class='username'>" + id + "</td>";
    htmlMessage += "<td class='currbalance'>" + balance.toFixed(2) + "</td>";
    htmlMessage += "</tr>"
    $("#recipienttable").append(htmlMessage);

    cy.add([
        { group: "nodes", data: { id: id, name: label }, position: { x: (cy.width() / 2), y: (cy.height() / 2) }, classes: 'animedge' },
    ]);

    if (nodeCount > 1) {
        for (linkNode = 0; linkNode < (nodeCount - 1); linkNode++) {

            var edgeId = 'edge_' + (nodeCount-1) + '_' + linkNode;
            cy.add([
                { group: "edges", data: { id: edgeId, name: edgeId, source: id, target: nodeIds[linkNode] } }
            ]);
        }
    }

    cy.load( cy.elements('*').jsons() );

    // these seem to be unnecessary... -gg
    //
    //    cy.fit();
}

function updateNodeBalance(id, balance) {
    if (id === null) {
        return;
    }
    for (i = 0 ; i < nodeCount; i++) {
        if (nodeIds[i] == id) {
            var elem = cy.getElementById(id);
            elem.data('name', formatLabel(id, balance));
        }
    }
}

function formatLabel(id, balance) {
    return id + ": $" + balance.toFixed(2);
}

function activateLink(idFrom, idTo, amount) {
    var indexFrom = null;
    var indexTo = null;

    for (i = 0; i < nodeCount; i++) {
        var nodeId = nodeIds[i];
        if (nodeId == idFrom) {
            indexFrom = i;
        }
        if (nodeId == idTo) {
            indexTo = i;
        }
    }
    if ((indexFrom != null) && (indexTo != null)) {
        var idHigh = Math.max(indexFrom, indexTo);
        var idLow  = Math.min(indexFrom, indexTo);
        var edgeId = "edge_" + idHigh + "_" + idLow;

        if (timerMap[edgeId] != null) {
            clearTimeout(timerMap[edgeId]);
            timerMap[edgeId] = null;
        }
        var link = cy.getElementById(edgeId);
        if (link != null) {
            link.data('name', "$" + Number(amount).toFixed(2));
            link.addClass('highlighted');

            var timeout = setTimeout(function() {
                console.log("link highlight timeout: " + edgeId);
                timerMap[edgeId] = null;
                var linkToDeselect = cy.getElementById(edgeId);
                console.log(linkToDeselect.classes);
                link.data('name', "");
                linkToDeselect.removeClass('highlighted');
            }, 5000);
            timerMap[edgeId] = timeout;

        }
    }
}

window.onload = initialize;

