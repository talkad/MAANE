import React, { useState } from "react";
import './Survey.css'
import {Collapse, Fade, Paper, Typography, Zoom} from "@mui/material";
import TextField from "@mui/material/TextField";
import SurveyQuestion from "./SurveyQuestion";
import Button from "@mui/material/Button";
import { TransitionGroup } from 'react-transition-group';

export default function Survey(){
    const [showError, setShowError] = useState(false)
    const [errorMessage, setErrorMessage] = useState('');
    const [questionID, setQuestionID] = useState(1);
    const [questions, setQuestions] = useState([<SurveyQuestion id={0}/>]);

    const header_string = 'בניית סקר'
    const survey_title_label_string = 'כותרת הסקר'
    const survey_description_label_string = 'תיאור הסקר'
    const add_question_string = 'הוסף/י שאלה'

    const add_question = () => {
        questions.push(<SurveyQuestion id={questionID}/>)
        setQuestions(questions);
        setQuestionID(questionID+1);
    }

    return (
        <div className="Survey">
            <h1>{header_string}</h1>
            <Paper className="Survey-paper" elevation={3}>
                {/*TODO: make the margin work */}
                <TextField
                    color="secondary"
                    className="Survey-text-field"
                    error={showError}
                    margin="normal"
                    variant="standard"
                    required
                    id="title"
                    label={survey_title_label_string}
                    name="title"
                    autoFocus
                />

                <TextField
                    color="secondary"
                    className="Survey-text-field"
                    error={showError}
                    margin="normal"
                    variant="standard"
                    required
                    id="description"
                    label={survey_description_label_string}
                    name="description"
                />
            </Paper>

            {/*TODO: animation transition when adding a question*/}
            {questions.map(x => x)}

            <Button onClick={add_question} color="secondary" variant="contained">{add_question_string}</Button>
        </div>
    )
}