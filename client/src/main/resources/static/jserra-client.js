
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
        })
    });

    speakText("hello!");
    fadeInRegistration();
    $.getJSON("jserra/config", function (data) {
        if (data.enableMessaging == true)  {
            $(".message_ui").show();
        }
    });
}

function receiveRegistration(registration) {
    console.log("receiveRegistration()");
    for (var i = 0; i < registeredUsers.length; i++) {
        if (registeredUsers[i] == registration.username) {
            return;
        }
    }
    if ((currUsername != "") && (registration.username != currUsername)) {
        speakText(registration.username + " has logged in.");
        var htmlMessage = "<option value=\"" + registration.username + "\"> " + registration.username;
        $("#recipients").append(htmlMessage);
        registeredUsers.push(registration.username);
    }
}

function receiveMessage(receipt) {
    if (receipt.recipient == currUsername) {
        document.getElementById('audio_cashregister').play();
        currBalance = currBalance + Number(receipt.amount);
        updateDisplayedBalance();

        var messageBlock = receipt.message;

        if(!(messageBlock== null || messageBlock == "")){
            messageBlock = ", with the message '" + receipt.message + "'";
        }
        speakText("You have received $" + receipt.amount + " from " + receipt.sender + messageBlock + ".");
    } else {
        // speakText(receipt.sender + " has sent $" + receipt.amount + " to " + receipt.recipient + ", with the message '" + receipt.message + "'.");
    }
}

function fadeInRegistration() {
    $("#register").fadeIn("slow");
}

function submitRegistration() {

    $.getJSON("jserra/register", function (data) {
        currUsername = data.username;

        if (currUsername == null || currUsername.trim() == "") {
            speakText("Please configure your team name.");
            alert("Please configure your team name.");
            return;
        } else {
            $("#register").fadeOut("slow");

            speakText("Welcome, " + currUsername + "!");

            $("#teamname").html(currUsername);
            registeredUsers.push(currUsername);
            currBalance = Number(data.balance);
            $("#balance").html('$' + currBalance.toFixed(0));

            $("#main").fadeIn("slow");
            // document.getElementById('audio_chime').play();

            for (var i = 0; i < data.registeredUsers.length; i++) {
                var registeredUser = data.registeredUsers[i];

                if (registeredUser.username != "" && registeredUser.username != currUsername) {
                    var htmlMessage = "<option value=\"" + registeredUser.username + "\"> " + registeredUser.username;
                    $("#recipients").append(htmlMessage);
                    registeredUsers.push(registeredUser.username);
                }
            }
        }
    });
}

function sendMoney() {
    if (sendButtonEnabled == false) {
        return;
    }
    sendButtonEnabled = false;
    $("#submit").css({opacity: 0.5});

    setTimeout(function () {
        sendButtonEnabled = true;
        $("#submit").css({opacity: 1.0});
    },3000);

    var userAmountAsString = $("#sendamount").val();
    if (userAmountAsString == "") {
        alert("You have to enter an amount.");
        return;
    }
    var userAmountAsNumber = Number(userAmountAsString);
    if (isNaN(userAmountAsNumber)) {
        alert("Oops! That's not a number!")
        return;
    }
    userAmountAsNumber = Number(userAmountAsNumber).toFixed(0);

    if (userAmountAsNumber <= 0) {
        alert("Crime doesn't pay!!");
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
        //$("#sendamount").val(Number(data.amount).toFixed(0));
        $("#sendamount").val(Number(data.amount));
        // $("#message").val(data.message);
        $("#recipients").val(data.recipient);

        currBalance -= Number(data.amount);
        updateDisplayedBalance();
    });
}

function updateDisplayedBalance() {
    $("#balance").html('$' + currBalance.toFixed(0));
}

function speakText(textToSpeak) {
    if ('speechSynthesis' in window) {
        var msg = new SpeechSynthesisUtterance(textToSpeak);
        msg.voice = speechSynthesis.getVoices().filter(function(voice) { return voice.name == 'Karen'; })[0];
        speechSynthesis.speak(msg);
    }
}

var currUsername = "";

var currBalance = 0;

var registeredUsers = new Array();

var sendButtonEnabled = true;

window.onload = connect;
