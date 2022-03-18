import React from "react";
import Tree from "react-d3-tree";
import "./styles.css";

const debugData = [
    {
        name: "",
        attributes: {
            title: "Card title",
            subtitle: "Card subtitle",
            text: "Some text to build on the card."
        },
        children: [
            {
                name: "",
                attributes: {
                    title: "Card title",
                    subtitle: "Card subtitle",
                    text: "Some text to build on the card."
                }
            },
            {
                name: "",
                attributes: {
                    title: "Card title",
                    subtitle: "Card subtitle",
                    text: "Some text to build on the card."
                }
            }
        ]
    }
];

const containerStyles = {
    width: "100%",
    height: "100vh"
};

const Card = ({ nodeData }) => (
    <div>
        <div className="card">
            <div className="card-body">
                <h5 style={{ margin: "5px" }} className="card-title">
                    {nodeData.attributes.title}
                </h5>
                <h6 style={{ margin: "5px" }} className="card-subtitle mb-2 text-muted">
                    {nodeData.attributes.subtitle}
                </h6>
                <p style={{ margin: "5px" }} className="card-text">
                    {nodeData.attributes.text}
                </p>
            </div>
        </div>
    </div>
);

export default class CenteredTree extends React.PureComponent {
    state = {};

    componentDidMount() {
        const dimensions = this.treeContainer.getBoundingClientRect();
        this.setState({
            translate: {
                x: dimensions.width / 2,
                y: dimensions.height / 2
            }
        });
    }

    render() {
        return (
            <div style={containerStyles} ref={tc => (this.treeContainer = tc)}>
                <Tree
                    data={debugData}
                    // translate={this.state.translate}
                    zoomable={true}
                    scaleExtent={{ min: 1, max: 3 }}
                    pathFunc="elbow"
                    allowForeignObjects
                    nodeSvgShape={{ shape: "none" }}
                    translate={{ x: 200, y: 200 }}
                    nodeSize={{x: 300, y: 200}}
                    nodeLabelComponent={{
                        render: <Card />,
                        foreignObjectWrapper: {
                            style: {
                                border: "1px solid black",
                                width: "150px",
                                height: "100px",
                                x: 10,
                                y: -50
                            }
                        }
                    }}
                />
            </div>
        );
    }
}


// import React, { useState, useEffect } from "react";
// import './custom-tree.css';
// import { Graph } from "react-d3-graph";
// import Tree from 'react-d3-tree';
// //
// // // graph payload (with minimalist structure)
// // const data = {
// //     nodes: [{ id: "Harry" }, { id: "Sally" }, { id: "Alice" }],
// //     links: [
// //         { source: "Harry", target: "Sally" },
// //         { source: "Harry", target: "Alice" },
// //     ],
// // };
// //
// // // the graph configuration, just override the ones you need
// // const myConfig = {
// //     nodeHighlightBehavior: true,
// //     node: {
// //         color: "lightgreen",
// //         size: 120,
// //         highlightStrokeColor: "blue",
// //     },
// //     link: {
// //         highlightColor: "lightblue",
// //     },
// // };
// //
// // const onClickNode = function(nodeId) {
// //     window.alert(`Clicked node ${nodeId}`);
// // };
// //
// // const onClickLink = function(source, target) {
// //     window.alert(`Clicked link between ${source} and ${target}`);
// // };
// //
// //
// //
// // export default function OrgChartTree() {
// //
// //
// //     return (
// //         // `<Tree />` will fill width/height of its container; in this case `#treeWrapper`.
// //         <div id="treeWrapper" style={{ width: '80em', height: '40em' }}>
// //             <Graph
// //                 id="graph-id" // id is mandatory
// //                 data={data}
// //                 config={myConfig}
// //                 onClickNode={onClickNode}
// //                 onClickLink={onClickLink}
// //             />;
// //         </div>
// //     );
// // }
//
//
//
//
//
// // This is a simplified example of an org chart with a depth of 2.
// // Note how deeper levels are defined recursively via the `children` property.
// const data = {
//     name: 'CEO',
//     children: [
//         {
//             name: 'Manager',
//             attributes: {
//                 department: 'Production',
//             },
//             children: [
//                 {
//                     name: 'Foreman',
//                     attributes: {
//                         department: 'Fabrication',
//                     },
//                     children: [
//                         {
//                             name: 'Worker',
//                         },
//                     ],
//                 },
//                 {
//                     name: 'Foreman',
//                     attributes: {
//                         department: 'Assembly',
//                     },
//                     children: [
//                         {
//                             name: 'Worker',
//                         },
//                     ],
//                 },
//             ],
//         },
//     ],
// };
//
//
// export default function OrgChartTree() {
//     const getDynamicPathClass = ({ source, target }, orientation) => {
//         if (!target.children) {
//             // Target node has no children -> this link leads to a leaf node.
//             return 'link__to-leaf';
//         }
//
//         // Style it as a link connecting two branch nodes by default.
//         return 'link__to-branch';
//     };
//
//     return (
//         // `<Tree />` will fill width/height of its container; in this case `#treeWrapper`.
//         <div id="treeWrapper" style={{ width: '80em', height: '40em' }}>
//             <Tree data={data}
//                   translate={{x: 800, y: 50}}
//                   orientation={"vertical"}
//                   pathClassFunc={getDynamicPathClass}
//
//                   rootNodeClassName="node__root"
//                   branchNodeClassName="node__branch"
//                   leafNodeClassName="node__leaf"
//             />
//         </div>
//     );
// }
