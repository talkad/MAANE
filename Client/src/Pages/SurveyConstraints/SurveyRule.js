import {Box, Button, Grid, IconButton, InputLabel, MenuItem, Paper, Select, Tooltip} from "@mui/material";
import './SurveyRule.css'
import {useEffect, useState} from "react";
import FormControl from "@mui/material/FormControl";

import AddIcon from '@mui/icons-material/Add';
import RemoveCircleIcon from '@mui/icons-material/RemoveCircle';

// TODO: make this page look better

export default function SurveyRule(props){

    const [trace] = useState([...props.trace, props.id])

    const goal_select_label_string = 'יעד';
    const question_select_label_string = 'שאלה או תנאי';

    const and_string = "וגם (כל תתי התנאים צריכים להתקיים)";
    const or_string = "או (אחד מתתי התנאים צריכים להתקיים)";

    const add_condition_button_string = "הוספת תא";
    const remove_tooltip_string = "מחיקה";

    useEffect(() => {

        // TODO: send for the data

    }, []);



    /**
     * handles the addition of a new condition
     */
    const addCondition = () => {
        props.addCell(props.id, props.trace);
    }

    /**
     * onChange handler for the goal select
     * @param event the affected element
     */
    const handleGoalSelectionChange = (event) => {
        props.goalSelectionChange(props.id, event.target.value);
    }

    /**
     * onChange handler for the question select
     * @param event the affected element
     */
    const handleQuestionSelectionChange = (event) => {
        props.questionSelectionChange(props.id, props.trace, event.target.value);
    }

    /**
     * gets the type of the selected question
     * @returns {string|*}
     */
    const selectedQuestionType = () => {
        if(props.questionSelection === undefined){
            return '';
        }

        if(props.questionSelection === '' || props.questionSelection === 'AND' || props.questionSelection === 'OR'){
            return '';
        }

        const question = props.questions.find(element => element.id === props.questionSelection);
        return question.type;
    }

    const handleRemove = () => {
        props.removeCell(props.id, props.trace);
    }

    /**
     * a function which decided if to present the add button in the current cell
     */
    const isPresentAddButton = () => {
        return (props.depth === 0 && props.children.length === 0) ||
            (props.depth !== 0 && (props.questionSelection === 'AND' || props.questionSelection === 'OR' || props.questionSelection === ''))
    }
//Math.min(props.colors.length-1, props.depth)
    return (
        <div>
            <Box m={2}>
                <Paper elevation={3} className={"Rule-paper"}
                       sx={{backgroundColor: props.colors[((props.depth % props.colors.length))]}}>

                    {/*TODO: needed?*/}
                    <h1>hello there {props.id}</h1>

                    <Grid container spacing={1}>

                        {/*goal selection for the main cells*/}
                        {props.depth === 0 &&
                            <Grid  item xs={11}>
                                <FormControl fullWidth sx={{backgroundColor: 'white', marginLeft: '1%', marginBottom: '1%'}}>
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
                                </FormControl>
                            </Grid>}

                        {/*question or conditional selection for the sub cells*/}
                        {props.depth > 0 &&
                            <Grid item xs={11}>
                                <FormControl fullWidth sx={{backgroundColor: 'white', marginLeft: '1%', marginBottom: '1%'}}>
                                    <InputLabel id={`question-select-label-${props.id}`}>{question_select_label_string}</InputLabel>
                                    <Select
                                        labelId={`question-select-label-${props.id}`}
                                        id={`question-select-${props.id}`}
                                        value={props.questionSelection}
                                        label={question_select_label_string}
                                        onChange={handleQuestionSelectionChange}
                                    >
                                        <MenuItem value={'AND'}>{and_string}</MenuItem>
                                        <MenuItem value={'OR'}>{or_string}</MenuItem>

                                        {props.questions.map(question => <MenuItem value={question.id}>{question.question + " (" +
                                            (question.type === 'OPEN_ANSWER' ? 'תשובה פתוחה' : question.type === 'NUMERIC_ANSWER' ?
                                                'תשובה מספרית' : 'בחירה מרובה') + ")"}</MenuItem>)}
                                    </Select>
                                </FormControl>
                            </Grid>}

                        <Grid item xs={1} style={{textAlign: "center"}}>
                            {/*remove cell button*/}
                            <Tooltip title={remove_tooltip_string}>
                                <IconButton onClick={handleRemove}>
                                    <RemoveCircleIcon/>
                                </IconButton>
                            </Tooltip>
                        </Grid>


                    </Grid>

                    {/*constraint to fill for open numeric questions*/}
                    {selectedQuestionType() === "NUMERIC_ANSWER" && <h1>open numeric</h1>}

                    {selectedQuestionType() === "MULTIPLE_CHOICE" && <h1>multiple choice</h1>}

                    {/*child cells*/}
                    {props.children.map(child => <SurveyRule id={child.id} depth={props.depth + 1} colors={props.colors}
                                                             questions={props.questions}
                                                             children={child.children} trace={trace}
                                                             questionSelection={child.questionSelection}
                                                             addCell={props.addCell}
                                                             questionSelectionChange={props.questionSelectionChange}
                                                             removeCell={props.removeCell}/>)}

                    {/*todo: figure out about the color of the button*/}
                    {isPresentAddButton() &&
                        <Button onClick={() => addCondition()} sx={{margin: '1%'}} variant={"contained"}>{add_condition_button_string}</Button>}

                </Paper>
            </Box>

        </div>
    )
}