import React, { useState } from "react";
import './Survey.css'
import {Paper, Typography} from "@mui/material";
import TextField from "@mui/material/TextField";
import SurveyQuestion from "./SurveyQuestion";
import Button from "@mui/material/Button";

export default function Survey(){
    const [showError, setShowError] = useState(false)
    const [errorMessage, setErrorMessage] = useState('');

    const header_string = 'בניית סקר'
    const survey_title_label_string = 'כותרת הסקר'
    const survey_description_label_string = 'תיאור הסקר'
    const add_question_string = 'הוסף/י שאלה'

    const add_question = () => {
        // TODO: implement
    }

    return (
        <div className="Survey">
            <h1>{header_string}</h1>
            <Paper className="Survey-paper" elevation={3}>
                {/*TODO: make the margin work */}
                <TextField
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

            <SurveyQuestion></SurveyQuestion>

            <Button onClick={add_question} color="secondary" variant="outlined">{add_question_string}</Button>
        </div>
    )
}