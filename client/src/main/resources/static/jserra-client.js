function connect() {
    var socket = new SockJS('/request');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/registrations', function (registration) {
            console.log(registration);
            receiveRegistration(JSON.parse(registration.body));
        });
        stompClient.subscribe('/topic/receipts', function (receipt) {
            console.log(receipt);
            receiveMessage(JSON.parse(receipt.body));
        });
    });
    speakText("hello.");
    fadeInRegistration();
}

function receiveRegistration(registration) {
    console.log("receiveRegistration()");
    if ((currUsername != "") && (registration.username != currUsername)) {
        speakText(registration.username + " has logged in.");
        var htmlMessage = "<option value=\"" + registration.username + "\"> " + registration.username;
        $("#recipients").append(htmlMessage);
    }
}

function receiveMessage(receipt) {
    if (receipt.recipient == currUsername) {
        document.getElementById('audio_cashregister').play();
        currBalance = currBalance + receipt.amount;
        updateDisplayedBalance();
        speakText("You have received $" + receipt.amount + " from " + receipt.sender + ", with the message '" + receipt.message + "'.");
    } else {
        // speakText(receipt.sender + " has sent $" + receipt.amount + " to " + receipt.recipient + ", with the message '" + receipt.message + "'.");
    }
}

function fadeInRegistration() {
    $("#register").fadeIn("slow");
}

function submitRegistration() {
    $("#register").fadeOut("slow");

    $.getJSON("jserra/register", function (data) {
        currUsername = data.username;
        speakText("Welcome, " + currUsername + "!");
        $("#teamname").html(data.username);
        currBalance = data.balance;
        $("#balance").html('$' + currBalance.toFixed(2));

        $("#main").fadeIn("slow");
        // document.getElementById('audio_chime').play();

        for (var i = 0; i < data.registeredUsers.length; i++) {
            var registeredUser = data.registeredUsers[i]
            var htmlMessage = "<option value=\"" + registeredUser.username + "\"> " + registeredUser.username;
            $("#recipients").append(htmlMessage);
        }
    });
}

function sendMoney() {
    var userAmountAsString = $("#sendamount").val();
    if (userAmountAsString == "") {
        alert("You have to enter an amount.");
        return;
    }
    var userAmountAsNumber = Number(userAmountAsString);
    if (isNaN(userAmountAsNumber)) {
        alert("WTF!? That's not a number!")
        return;
    }
    if (userAmountAsNumber > currBalance) {
        alert("You don't have that much money.");
        return;
    }
    var userMessage = $("#message").val();

    var userRecipient = $("#recipients").val();
    if (userRecipient == null) {
        alert("You must pick a recipient.");
        return;
    }
    $.post("jserra/sendmoney", {
        recipient: userRecipient,
        amount: userAmountAsNumber,
        message: userMessage
    }, function (data) {
        $("#sendamount").val(Number(data.amount).toFixed(2));
        $("#message").val(data.message);
        $("#recipients").val(data.recipient);

        currBalance -= data.amount;
        updateDisplayedBalance();
    });
}

function updateDisplayedBalance() {
    $("#balance").html('$' + currBalance.toFixed(2));
}

function speakText(textToSpeak) {
    if ('speechSynthesis' in window) {
        var msg = new SpeechSynthesisUtterance(textToSpeak);
        // msg.voice = speechSynthesis.getVoices().filter(function(voice) { return voice.name == 'Pipe Organ'; })[0];
        speechSynthesis.speak(msg);
    }
}

window.speechSynthesis.onvoiceschanged = function () {
    var speechSynthesisVoices = speechSynthesis.getVoices();
    /*
     var accents = _(speechSynthesisVoices).pluck('lang');
     var voices = _(speechSynthesisVoices).pluck('voiceURI');
     var names = _(speechSynthesisVoices).pluck('name');
     console.log('names', names);
     console.log('accents', _.uniq(accents));
     console.log('voices', voices);
     */
};

var currUsername = "";

var currBalance = 0;

window.onload = connect;
