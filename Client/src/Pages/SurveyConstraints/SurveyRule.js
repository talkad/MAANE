import {Button, InputLabel, MenuItem, Paper, Select} from "@mui/material";
import './SurveyRule.css'
import {useEffect, useState} from "react";
import FormControl from "@mui/material/FormControl";

// TODO: make this page look better

export default function SurveyRule(props){

    const [trace] = useState([...props.trace, props.id])

    const goal_select_label_string = 'יעד';
    const question_select_label_string = 'שאלה או תנאי';

    const and_string = "וגם";
    const or_string = "או";

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

    return (
        <div>
            <Paper elevation={3} className={"Rule-paper"}
                   sx={{backgroundColor: props.colors[Math.min(props.colors.length-1, props.depth)]}}>
                <h1>hello there {props.id}</h1>

                {/*goal selection for the main cells*/}
                {props.depth === 0 &&
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

                {/*question or conditional selection for the sub cells*/}
                {props.depth > 0 &&
                    <FormControl fullWidth sx={{backgroundColor: 'white'}}>
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
                    </FormControl>}

                {/*constraint to fill for open numeric questions*/}
                {selectedQuestionType() === "NUMERIC_ANSWER" && <h1>open numeric</h1>}

                {selectedQuestionType() === "MULTIPLE_CHOICE" && <h1>multiple choice</h1>}

                {/*child cells*/}
                {props.children.map(child => <SurveyRule id={child.id} depth={props.depth + 1} colors={props.colors}
                                                         questions={props.questions}
                                                         children={child.children} trace={trace}
                                                         questionSelection={child.questionSelection}
                                                         addCondition={props.addCondition}
                                                         questionSelectionChange={props.questionSelectionChange}/>)}

                <Button onClick={() => addCondition()} variant={"contained"}>{add_condition_button_string}</Button>
            </Paper>
        </div>
    )
}