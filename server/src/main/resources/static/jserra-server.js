
function connect() {
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
            receiveMessage(JSON.parse(receipt.body));
        });
    });
    speakText("the server is online.");
    fadeInMain();
}

function receiveRegistration(registration) {
    document.getElementById('audio_chime').play();
    console.log(registration);
    var htmlMessage = "<tr class='recipient'>";
    htmlMessage += "<td class='username'>" + registration.username + "</td>";
    htmlMessage += "<td class='currbalance'>" + registration.balance.toFixed(2) + "</td>";
    htmlMessage += "</tr>"
    $("#recipienttable").append(htmlMessage);
}

function receiveMessage(receipt) {
    console.log(receipt);
    var messageBlock = receipt.message;
    
    if(!(messageBlock== null || messageBlock == "")){
        messageBlock = ", with the message '" + receipt.message + "'";
    }

    speakText(receipt.sender + " has sent $" + receipt.amount + " to " + receipt.recipient + messageBlock + ".");

    $("#recipienttable").find("tr").each(function() {
        var username = $(this).find(".username").html();
        var balance = Number($(this).find(".currbalance").html());
        var sendAmount = Number(receipt.amount);
        console.log("username: " + username);
        // decrement the sender's balance
        if (username == receipt.sender) {
            console.log("match!");
            var newbalance = balance - sendAmount;
            console.log("new balance = " + newbalance)
            $(this).find(".currbalance").html(newbalance);

        }
        // increment the sender's balance
        else if (username == receipt.recipient) {
            console.log("match!");
            var newbalance = balance + sendAmount;
            console.log("new balance = " + newbalance)
            $(this).find(".currbalance").html(newbalance);
        }
    });

    var htmlMessage = "<div class='message'>";
    htmlMessage += "<table>"
    for (prop in receipt) {
        console.log(prop);
        console.log(prop + " : " + receipt[prop]);
        htmlMessage += "<tr>"
        htmlMessage += "<td class='messageproperty'>" + prop + "</td>";
        htmlMessage += "<td class='messagevalue'>" + receipt[prop] + "</td>";
        htmlMessage += "</tr>";
    }
    htmlMessage += "</div>";
    $("#messages").append(htmlMessage);
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

window.onload = connect;

