var nodeCount = 0;
var nodeIds = [];
var nodeBalances = [];

var cy = null;

var timerMap = {};

function setupGraph() {
    cy = cytoscape({
        container: document.getElementById('recipient_graph'),

        style: cytoscape.stylesheet()
            .selector('node')
            .css({
                'content': 'data(name)',
                'color': 'white',
                'background-color': 'green',
                'font-size': '60px'
            })
            .selector('edge')
            .css({
                'width': 4,
                'line-color': '#8b8',
                'text-opacity' : 0,
                'font-size': '48px',
                'edge-text-rotation': 'autorotate',
                'content': 'data(name)',
                'transition-property': 'line-color, width, text-opacity',
                'transition-duration': '1.0s'
            })
            .selector('.highlighted')
            .css({
                'text-opacity': 1.0,
                'width': 16,
                'line-color': '#61bffc',
                'transition-property': 'line-color, width, text-opacity',
                'transition-duration': '1.0s'
            }),

        elements: {
            nodes: [],
            edges: []
        },

        layout: {
            name: 'circle',
            animate: true,
            animationDuration: 200,
            padding: 50
        },

        zoomingEnabled: true,
        userZoomingEnabled: false,
        panningEnabled: true,
        userPanningEnabled: false,
        autoungrabify: true
    });
    
//    doLocalTesting();
}

function addNode(id, balance) {
    nodeIds.push(id);
    nodeBalances.push(balance);
    var label = formatLabel(id, balance);
    nodeCount++;

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
}

function formatLabel(id, balance) {
//    return id + ": $" + balance.toFixed(2);
    return id;
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
