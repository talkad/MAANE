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
    const [questionID, setQuestionID] = useState(0);
    const [questions, setQuestions] = useState([]);

    const header_string = 'בניית סקר'
    const survey_title_label_string = 'כותרת הסקר'
    const survey_description_label_string = 'תיאור הסקר'
    const add_question_string = 'הוסף/י שאלה'
    const submit_survey_string = 'סיום'

    const add_question = () => {
        // setting new question element
        questions.push(
            {
                id: questionID,
                element: <SurveyQuestion id={questionID} modify={modify_question} delete={delete_question}/>,
                question: '',
                type: 'multiple',
                answers: [],
            });
        setQuestions(questions);

        // setting id
        setQuestionID(questionID+1);
    }

    const modify_question = (id, attribute, value, answer_id = -1) => {
        const item = questions.find(element => element['id'] === id);
        const index = questions.indexOf(item);

        if(attribute !== 'answers'){
            questions[index][attribute] = value;
        }
        else{
            const answer = questions[index]['answers'].find(element => element['id'] === id);
            if(answer === undefined) {
                questions[index]['answers'].push({id: answer_id, value: value});
            }
            else{
                questions[index]['answers'] = value;
            }
        }
    }

    const delete_question = (id) => {
        console.log(id)
        setQuestions(questions.filter(element => element['id'] !== id))
    }

    const submit_survey = () => {
        // TODO: send
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
            {questions.map(x => x['element'])}

            <Button onClick={add_question} color="secondary" variant="contained">{add_question_string}</Button>
            <br/>
            <Button onClick={submit_survey} color="secondary" variant="contained">{submit_survey_string}</Button>
        </div>
    )
}