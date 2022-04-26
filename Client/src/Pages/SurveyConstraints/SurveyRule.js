import {Button, Paper} from "@mui/material";
import './SurveyRule.css'
import {useEffect, useState} from "react";


export default function SurveyRule(props){

    const [trace] = useState([...props.trace, props.id])

    useEffect(() => {

        // TODO: send for the data

    }, []);

    const add_condition_button_string = "הוספת תנאי";

    const addCondition = () => {
        props.addCondition(props.id, props.trace);
    }

    return (
        <div>
            <Paper elevation={3} className={"Rule-paper"}>
                <h1>hello there {props.id}</h1>

                {props.children.map(child => <SurveyRule id={child.id} children={child.children} trace={trace}
                                                         addCondition={props.addCondition}/>)}

                <Button onClick={() => addCondition()} variant={"contained"}>{add_condition_button_string}</Button>
            </Paper>
        </div>
    )
}