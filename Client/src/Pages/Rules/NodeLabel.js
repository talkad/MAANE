import React from "react";

import Card from "@material-ui/core/Card";
import CardActionArea from "@material-ui/core/CardActionArea";
import CardActions from "@material-ui/core/CardActions";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import Avatar from "@material-ui/core/Avatar";
import CardHeader from "@material-ui/core/CardHeader";
import Person from "@material-ui/icons/Person";


export default class NodeLabel extends React.PureComponent {
    render() {
        const { className, nodeData } = this.props;
        //const classes=useStyles();
        return (
            <div className={className}>
                <Card className="cardview">
                    <CardHeader
                        onClick={e => e.stopPropagation()}
                        avatar={
                            <Avatar aria-label="Recipe" className="avatar">
                                <Person />
                            </Avatar>
                        }
                        title="Shrimp and Chorizo Paella"
                    />

                    <CardActionArea onClick={e => e.stopPropagation()}>
                        <Typography variant="body2" color="textSecondary" component="p">
                            {nodeData.name}
                        </Typography>
                    </CardActionArea>
                    <CardActions>
                        <Button
                            size="small"
                            color="primary"
                            onClick={e =>
                                console.log((nodeData._collapsed = !nodeData._collapsed))
                            }
                        >
                            {nodeData._collapsed ? "Expand" : "Collapse"}
                        </Button>
                        <Button className="flex" />
                    </CardActions>
                </Card>
            </div>
        );
    }
}