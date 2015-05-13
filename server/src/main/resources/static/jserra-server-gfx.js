
function setupGraph() {
    cy = cytoscape({
        container: document.getElementById('recipient_graph'),

        style: cytoscape.stylesheet()
            .selector('node')
            .css({
                'content': 'data(name)',
                'background-color': 'green',
                'font-size': '24px'
            })
            .selector('edge')
            .css({
                'width': 4,
                'line-color': '#ada',
                'text-opacity' : 0,
                'font-size': '36px',
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

    // doLocalTesting();

}
