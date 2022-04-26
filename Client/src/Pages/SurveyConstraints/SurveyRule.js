import {Button, InputLabel, MenuItem, Paper, Select} from "@mui/material";
import './SurveyRule.css'
import {useEffect, useState} from "react";
import FormControl from "@mui/material/FormControl";


export default function SurveyRule(props){

    const [trace] = useState([...props.trace, props.id])

    const goal_select_label_string = 'יעד';

    useEffect(() => {

        // TODO: send for the data

    }, []);

    const add_condition_button_string = "הוספת תנאי";

    /**
     * handles the addition of a new condition
     */
    const addCondition = () => {
        props.addCondition(props.id, props.trace);
    }

    /**
     * onChange handler for the goal select
     * @param event the affected element
     */
    const handleGoalSelectionChange = (event) => {
        props.goalSelectionChange(props.id, event.target.value);
    }

    return (
        <div>
            <Paper elevation={3} className={"Rule-paper"}
                   sx={{backgroundColor: props.colors[Math.min(props.colors.length-1, props.depth)]}}>
                <h1>hello there {props.id}</h1>

                {/*goal selection for the main cells*/}
                {props.goals !== undefined &&
                    <FormControl fullWidth sx={{backgroundColor: 'white'}}>
                        <InputLabel id={`goal-select-label-${props.id}`}>{goal_select_label_string}</InputLabel>
                        <Select
                            labelId={`goal-select-label-${props.id}`}
                            id={`goal-select-${props.id}`}
                            value={props.goalSelection}
                            label={goal_select_label_string}
                            onChange={handleGoalSelectionChange}
                        >
                            {props.goals.map(goal => <MenuItem value={goal.value}>{goal.description}</MenuItem>)}
                        </Select>
                    </FormControl>}

                {/*child cells*/}
                {props.children.map(child => <SurveyRule id={child.id} depth={props.depth + 1} colors={props.colors} children={child.children} trace={trace}
                                                         addCondition={props.addCondition}/>)}

                <Button onClick={() => addCondition()} variant={"contained"}>{add_condition_button_string}</Button>
            </Paper>
        </div>
    )
}