import React from "react";
import Tree from "react-d3-tree";
import NodeLabel from "./NodeLabel";

const containerStyles = {
    width: "100%",
    height: "100vh"
};

const myTreeData = [
    {
        name: "Top Level",
        attributes: {
            keyA: "val A",
            keyB: "val B",
            keyC: "val C"
        },
        children: [
            {
                name: "Level 2: A",
                attributes: {
                    keyA: "val A",
                    keyB: "val B",
                    keyC: "val C"
                }
            },
            {
                name: "Level 2: B"
            }
        ]
    }
];

export default class CenteredTree extends React.PureComponent {
    state = {};

    componentDidMount() {
        const dimensions = this.treeContainer.getBoundingClientRect();
        this.setState({
            translate: {
                x: dimensions.width / 2,
                y: dimensions.height / 2
            },
            nodeSize: {
                x: 400,
                y: 200
            }
        });
    }

    render() {
        return (
            <div style={containerStyles} ref={tc => (this.treeContainer = tc)}>
                <Tree
                    data={myTreeData}
                    translate={this.state.translate}
                    nodeSize={this.state.nodeSize}
                    orientation={"vertical"}
                    allowForeignObjects
                    nodeLabelComponent={{
                        render: <NodeLabel className="myLabelComponentInSvg" />,
                        foreignObjectWrapper: {
                            y: -50,
                            x: -125,
                            width: 250,
                            height: 150
                        }
                    }}
                />
            </div>
        );
    }
}