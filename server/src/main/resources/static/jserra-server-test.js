
function testReceiveRegistration(username) {
    var registrationObj = {};
    registrationObj.username = username;
    registrationObj.balance = 100;
    receiveRegistration(registrationObj);
}

function testReceiveTransfer(sender, recipient, amount, message) {
    var transferObj = {};
    transferObj.sender = sender;
    transferObj.recipient = recipient;
    transferObj.amount = amount;
    transferObj.message = message;

    receiveTransfer(transferObj);
}

function doLocalTesting() {
    addUser("Team A", 100);
    addUser("Team B", 100);
    addUser("Team C", 100);
    addUser("Team D", 100);
    addUser("Team E", 100);


    setTimeout(function () { testReceiveTransfer('Team A', 'Team B', 34.99, "3 seconds")}, 3000);
    setTimeout(function () { testReceiveTransfer('Team B', 'Team C', 15.25, "")}, 8000);

    setTimeout(function () { testReceiveRegistration('Team F')}, 7000);

    setTimeout(function () { testReceiveTransfer('Team E', 'Team D', 12.34, "6 seconds")}, 6000);
    setTimeout(function () { testReceiveTransfer('Team A', 'Team B', 9.95, "12 seconds")}, 12000);
    setTimeout(function () { testReceiveTransfer('Team C', 'Team F', 42, "12 seconds")}, 12000);
}
