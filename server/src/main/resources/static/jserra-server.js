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

    $.getJSON("jserra/messageHistory", function (data){
        loadMessageHistory( data )
    });

    $.getJSON("jserra/userList", function (data){
        loadUserList( data )
    });

    speakText("the server is online.");

    fadeInMain();
    setupGraph();
}

function loadUserList(userList){
    var userListLength = userList.length;
    for (var i = 0; i < userListLength; i++) {
        var userData = userList[i];
        addUser(userData.username, userData.balance);
    }
}

function loadMessageHistory(messageHistory){
    var messagesLength = messageHistory.length;
    for (var i = 1; i <= messagesLength; i++) {
        var transferMessage = messageHistory[messagesLength-i];
        //add to messages table
        var htmlMessage = "<div class='message'>";
        htmlMessage += "<table>"
        for (prop in transferMessage) {
            console.log(prop);
            console.log(prop + " : " + transferMessage[prop]);
            htmlMessage += "<tr>"
            htmlMessage += "<td class='messageproperty'>" + prop + "</td>";
            htmlMessage += "<td class='messagevalue'>" + transferMessage[prop] + "</td>";
            htmlMessage += "</tr>";
        }
        htmlMessage += "</div>";
        $("#messages").append(htmlMessage);
    }
}

function receiveRegistration(registration) {
    document.getElementById('audio_chime').play();
    console.log("***** REGISTRATION: " + registration);

    // add user to list and graph
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
            $(this).find(".currbalance").html(newbalance.toFixed(2));

            updateNodeBalance(username, newbalance);
        }

        cy.load( cy.elements('*').jsons() );
    });

    var htmlMessage = "<div class='message'>";
    htmlMessage += "<table>"
    for (prop in transfer) {
        var value = transfer[prop];
        if (prop == "amount") {
            value = "$" + Number(value).toFixed(2);
        }
        console.log(prop);
        console.log(prop + " : " + transfer[prop]);
        htmlMessage += "<tr>"
        htmlMessage += "<td class='messageproperty'>" + prop + "</td>";
        htmlMessage += "<td class='messagevalue'>" + value + "</td>";
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
    var htmlMessage = "<tr class='recipient'>";
    htmlMessage += "<td class='username'>" + id + "</td>";
    htmlMessage += "<td class='currbalance'>" + balance.toFixed(2) + "</td>";
    htmlMessage += "</tr>"
    $("#recipienttable").append(htmlMessage);

    addNode(id, balance);
}

window.onload = initialize;

