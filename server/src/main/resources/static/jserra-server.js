var barChartData = {
    labels: [],
    datasets: [{
        label: 'Balance',
        backgroundColor: "rgba(255,255,255,0.7",
        borderColor: "rgba(255,255,255,0.7",
        borderWidth: 1,
        data: []
    }]
};

var userCount = 0;
var averageBalance = 0;

function initialize() {

    $(".pyro").hide();

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
    setupBarGraph();
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
        htmlMessage += transferMessage["sender"] + " has sent $" + transferMessage[amount] + " to " + transferMessage[recipient] + ".";
        htmlMessage += "</div>";
        $("#messages").append(htmlMessage);
    }
}

function receiveRegistration(registration) {
    document.getElementById('audio_chime').play();
    console.log("***** REGISTRATION: " + registration);

    // add user to list and graph
    addUser(registration.username, registration.balance);
    speakText(registration.username + " has joined.");
    averageBalance = registration.averageBalance;
    setupBarGraph();

    $.getJSON("jserra/isBalanced", function (data) {
        if ((data == true) && (userCount > 1)) {
            $(".pyro").show();
        } else {
            $(".pyro").hide();
        }
    });

}

function receiveTransfer(transfer) {
    console.log(transfer);
    var messageBlock = transfer.message;

    if(!(messageBlock== null || messageBlock == "")){
        messageBlock = ", with the message '" + transfer.message + "'";
    }

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
            $(this).find(".currbalance").html(newbalance.toFixed(0));

            updateGraphBalance(username, newbalance);
        }
        // increment the sender's balance
        else if (username == transfer.recipient) {
            console.log("match!");
            var newbalance = balance + sendAmount;
            console.log("new balance = " + newbalance)
            $(this).find(".currbalance").html(newbalance.toFixed(0));

            updateGraphBalance(username, newbalance);
        }
    });

    var htmlMessage = "<div class='message'>";
    htmlMessage += transfer["sender"] + " has sent $" + transfer["amount"] + " to " + transfer["recipient"] + ".";
    htmlMessage += "</div>";
    $("#message_list").prepend(htmlMessage);

    $.getJSON("jserra/isBalanced", function (data) {
        if (data == true) {
            $(".pyro").show();
            document.getElementById('epic-win').play();
        } else {
            $(".pyro").hide();
            speakText(transfer.sender + " has sent $" + transfer.amount + " to " + transfer.recipient + messageBlock + ".");
        }
    });
}

function fadeInMain() {
    $("#main").fadeIn("slow");
}

function speakText(textToSpeak) {
    if ('speechSynthesis' in window) {
        var msg = new SpeechSynthesisUtterance(textToSpeak);
        msg.voice = speechSynthesis.getVoices().filter(function(voice) { return voice.name == 'Karen'; })[0];
        speechSynthesis.cancel();
        speechSynthesis.speak(msg);
    }
}

function addUser(id, balance) {
    userCount++;
    var htmlMessage = "<tr class='recipient'>";
    htmlMessage += "<td class='username'>" + id + "</td>";
    htmlMessage += "<td class='currbalance'>" + balance.toFixed(0) + "</td>";
    htmlMessage += "</tr>"
    $("#recipienttable").append(htmlMessage);

    addGraphItem(id, balance);
}

function addGraphItem(id, balance) {
    barChartData.labels.push(id);
    barChartData.datasets[0].data.push(balance);

    window.myBar.update();
}

function updateGraphBalance(username, newbalance) {
    for (var i = 0; i < barChartData.labels.length; i++) {
        if (barChartData.labels[i] == username) {
            barChartData.datasets[0].data[i] = newbalance;
            window.myBar.update();
        }
    }
}


function setupBarGraph() {
    if (window.myBar != null) {
        window.myBar.destroy();
    }
    var ctx = document.getElementById('canvas').getContext('2d');
    window.myBar = new Chart(ctx, {
        type: 'bar',
        data: barChartData,
        options: {
            maintainAspectRatio: false,
            responsive: true,
            events: [],
            legend: {
                display: false,
                position: 'top',
            },
            title: {
                display: true,
            },
            scales: {
                xAxes: [{
                    display: true,
                    ticks: {
                        fontColor: "#fff",
                        fontSize: 20
                    }
                }],
                yAxes: [{
                    display: true,
                    gridLines: {
                        color: "rgba(255,255,255,0.3)"
                    },
                    ticks: {
                        fontColor: "#fff",
                        fontSize: 20,
                        suggestedMin: 0,
                        suggestedMax: 100,
                        beginAtZero: true
                    }
                }]
            },
            annotation: {
                events: ["click"],
                annotations: [
                    {
                        drawTime: "beforeDatasetsDraw",
                        id: "hline",
                        type: "line",
                        mode: "horizontal",
                        scaleID: "y-axis-0",
                        value: averageBalance,
                        borderColor: "rgba(0,255,0,0.75)",
                        borderWidth: 2,
                        label: {
                            // backgroundColor: "red",
                            content: "50",
                            enabled: false
                        },
                        onClick: function (e) {
                            // The annotation is is bound to the `this` variable
                            console.log("Annotation", e.type, this);
                        }
                    }
                ]
            }
        }
    });
}


window.onload = initialize;

