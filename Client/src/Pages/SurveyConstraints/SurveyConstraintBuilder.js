// // TODO: decide if we keep this or changing how we present this
//
// import {
//     GraphView, // required
//     Edge, // optional
//     IEdge, // optional
//     Node, // optional
//     INode, // optional
//     LayoutEngineType, // required to change the layoutEngineType, otherwise optional
//     BwdlTransformer, // optional, Example JSON transformer
//     GraphUtils // optional, useful utility functions
// } from 'react-digraph';
// import * as Space from 'react-spaces';
// import {useRef, useState} from "react";
//
// const NODE_KEY = "id"
//
// const GraphConfig =  {
//     NodeTypes: {
//         empty: { // required to show empty nodes
//             typeText: "None",
//             shapeId: "#empty", // relates to the type property of a node
//             shape: (
//                 <symbol viewBox="0 0 100 100" id="empty" key="0">
//                     <circle cx="50" cy="50" r="45"></circle>
//                 </symbol>
//             )
//         },
//         custom: { // required to show empty nodes
//             typeText: "Custom",
//             shapeId: "#custom", // relates to the type property of a node
//             shape: (
//                 <symbol viewBox="0 0 50 25" id="custom" key="0">
//                     <ellipse cx="50" cy="25" rx="50" ry="25"></ellipse>
//                 </symbol>
//             )
//         }
//     },
//     NodeSubtypes: {},
//     EdgeTypes: {
//         emptyEdge: {  // required to show empty edges
//             shapeId: "#emptyEdge",
//             shape: (
//                 <symbol viewBox="0 0 50 50" id="emptyEdge" key="0">
//                     <circle cx="25" cy="25" r="8" fill="currentColor"> </circle>
//                 </symbol>
//             )
//         }
//     }
// }
//
// export default function SurveyConstraintBuilder(){
//     const [graph, setGraph] = useState({
//         "nodes": [
//             {
//                 "id": 1,
//                 "title": "Node A",
//                 "x": 258.3976135253906,
//                 "y": 331.9783248901367,
//                 "type": "empty"
//             },
//             {
//                 "id": 2,
//                 "title": "Node B",
//                 "x": 593.9393920898438,
//                 "y": 260.6060791015625,
//                 "type": "empty"
//             },
//             {
//                 "id": 3,
//                 "title": "Node C",
//                 "x": 237.5757598876953,
//                 "y": 61.81818389892578,
//                 "type": "custom"
//             },
//             {
//                 "id": 4,
//                 "title": "Node C",
//                 "x": 600.5757598876953,
//                 "y": 600.81818389892578,
//                 "type": "custom"
//             }
//         ],
//         "edges": [
//             {
//                 "source": 1,
//                 "target": 2,
//                 "type": "emptyEdge"
//             },
//             {
//                 "source": 2,
//                 "target": 4,
//                 "type": "emptyEdge"
//             }
//         ]
//     })
//     const [selected, setSelected] = useState({})
//     const graphViewRef = useRef();
//
//     const onSelect = (data) => {
//         console.log(data)
//     }
//
//     const onCreateNode = (x, y) => {
//         console.log(x)
//         console.log(y)
//         // graph['nodes'].push({
//         //     "id": 4,
//         //     "title": "Node C",
//         //     "x": 600.5757598876953,
//         //     "y": 600.81818389892578,
//         //     "type": "custom"
//         // })
//         //setGraph()
//     }
//
//     return(
//         <Space.Fill>
//             <h1>hi</h1>
//             {/*TODO: delete this and make a different way of representing it*/}
//             <GraphView  ref={graphViewRef}
//                         nodeKey={NODE_KEY}
//                         nodes={graph.nodes}
//                         edges={graph.edges}
//                         selected={selected}
//                         nodeTypes={GraphConfig.NodeTypes}
//                         nodeSubtypes={GraphConfig.NodeSubtypes}
//                         edgeTypes={GraphConfig.EdgeTypes}
//                         allowMultiselect={true} // true by default, set to false to disable multi select.
//                         onSelect={onSelect}
//                         onCreateNode={onCreateNode}
//                         //onUpdateNode={this.onUpdateNode}
//                         //onDeleteNode={this.onDeleteNode}
//                         //onCreateEdge={this.onCreateEdge}
//                         //onSwapEdge={this.onSwapEdge}
//                         //onDeleteEdge={this.onDeleteEdge}
//             />
//         </Space.Fill>
//     )
// }