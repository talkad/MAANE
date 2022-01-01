import React, { useState } from "react";
import './SurveryBuilderQuestion.css'
import {FormControlLabel, Grid, MenuItem, Paper, Radio, RadioGroup, Select, Tooltip} from "@mui/material";
import FormControl from '@mui/material/FormControl';
import TextField from "@mui/material/TextField";
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Delete';
import Button from "@mui/material/Button";
import RemoveCircleOutlineIcon from '@mui/icons-material/RemoveCircleOutline';

// TODO: change to react-space

export default function SurveyQuestionBuilder(props) {
    const [selection, setSelection] = useState('MULTIPLE_CHOICE');
    const [multipleAnswersID, setMultipleAnswersID] = useState(0);
    const [multipleAnswers, setMultipleAnswers] = useState([])

    const question_label_string = 'שאלה';
    const answer_label_string = 'תשובה';
    const multiple_item_string = 'בחירה מרובה';
    const multiple_add_string = 'הוסף/י תשובה'
    const open_item_string = 'פתוחה';
    const open_number_item_string = 'מספרית';
    const question_title_tooltip_string = 'מחיקת שאלה';
    const answer_title_tooltip_string = 'מחיקת תשובה';

    const handleQuestionChange = (event) => {
        props.modify(props.id, 'question', event.target.value);
    }

    const handleChange = (event) => {
        setSelection(event.target.value);
        props.modify(props.id, 'type', selection);
    }

    const delete_question = () => {
        props.delete(props.id)
    }

    const handleAnswerChange = (event) => {
        props.modify(props.id, 'answers', event.target.value, event.target.id);
    }

    const add_answer = () => {
        const to_add = [multipleAnswersID.toString(),
            <TextField
            id={multipleAnswersID.toString()}
            color="secondary"
            className="SurveyQuestion-text-field"
            margin="normal"
            variant="standard"
            required
            label={answer_label_string}
            onChange={handleAnswerChange}
        />]
        setMultipleAnswers(multipleAnswers => [...multipleAnswers, to_add]);
        props.modify(props.id, 'answers', '', multipleAnswersID.toString());

        setMultipleAnswersID(multipleAnswersID+1);


    }

    const delete_answer = (answer_id) => {
        const index = multipleAnswers.findIndex(element => element[0] === answer_id);

        multipleAnswers.splice(index, 1) // removing the answer
        setMultipleAnswers([...multipleAnswers]); // updating the renderer
        props.delete_answer(props.id, answer_id);
    }

    return (
        <div className="SurveyQuestion">
            <Paper className="Survey-paper" elevation={3}>
                <Grid container spacing={2}>
                    <Grid item xs={9}>
                        <TextField
                            color="secondary"
                            className="SurveyQuestion-text-field"
                            margin="normal"
                            variant="filled"
                            required
                            id={props.id}
                            label={question_label_string}
                            name="question"
                            autoFocus
                            onChange={handleQuestionChange}
                        />
                    </Grid>
                    <Grid item xs={2}>
                        <FormControl  sx={{ m: 1, minWidth: 80 }}>
                            <Select
                                value={selection}
                                onChange={handleChange}
                            >
                                {/*todo: add an icon for each option*/}
                                {/*TODO: change this to enums from strings*/}
                                <MenuItem value={'MULTIPLE_CHOICE'}>{multiple_item_string}</MenuItem>
                                <MenuItem value={'OPEN_ANSWER'}>{open_item_string}</MenuItem>
                                <MenuItem value={'NUMERIC_ANSWER'}>{open_number_item_string}</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={1}>
                        <Tooltip title={question_title_tooltip_string}>
                            <IconButton onClick={delete_question} aria-label="delete">
                                <DeleteIcon />
                            </IconButton>
                        </Tooltip>
                    </Grid>
                    {selection === 'MULTIPLE_CHOICE' &&
                        <Grid item xs={12}>
                                <RadioGroup column>
                                        {multipleAnswers.map(x =>
                                            <Grid container spacing={2}>
                                                <Grid item xs={2}>
                                                    <FormControlLabel disabled value={'idk'} control={<Radio />} label={x[1]}/>
                                                </Grid>
                                                <Grid item xs={1}>
                                                    <Tooltip title={answer_title_tooltip_string}>
                                                        <IconButton onClick={() => delete_answer(x[0])} aria-label="delete">
                                                            <RemoveCircleOutlineIcon color="warning" />
                                                        </IconButton>
                                                    </Tooltip>
                                                </Grid>
                                            </Grid>
                                        )}
                                    <FormControlLabel disabled value={'idk'} control={<Radio />} label={<Button onClick={() => add_answer()} variant="text" color="secondary">{multiple_add_string}</Button>}/>
                                </RadioGroup>
                            <br/>
                        </Grid>
                    }
                    {(selection === 'OPEN_ANSWER' || selection === 'NUMERIC_ANSWER')  &&
                    <Grid sx={{alignItems: 'center'}} item xs={12}>
                        <TextField
                            color="secondary"
                            className="SurveyQuestion-text-field"
                            margin="normal"
                            variant="standard"
                            disabled
                            label={answer_label_string}
                        />
                    </Grid>}
                </Grid>
            </Paper>
        </div>
    )

}