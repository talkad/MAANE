import React, { useState } from "react";
import './SurveyBuilder.css'
import {Collapse, Fade, Paper, Typography, Zoom} from "@mui/material";
import TextField from "@mui/material/TextField";
import SurveyQuestion from "./SurveyBuilderQuestion";
import Button from "@mui/material/Button";
import { TransitionGroup } from 'react-transition-group';
import SurveyQuestionBuilder from "./SurveyBuilderQuestion";

// TODO: change to react-space

export default function SurveyBuilder(){
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


        // TODO: see about this thingy
        // const added_question =
        // {
        //     id: questionID,
        //     element: <SurveyQuestion id={questionID} modify={modify_question} delete={delete_question} delete_answer={delete_question_answer}/>,
        //     question: '',
        //     type: 'multiple',
        //     answers: [],
        // }
        //
        // setQuestions((questions) => [...questions, added_question]);

        questions.push(
            {
                id: questionID,
                element: <SurveyQuestionBuilder id={questionID} modify={modify_question} delete={delete_question} delete_answer={delete_question_answer}/>,
                question: '',
                type: 'multiple',
                answers: [],
            }
        )

        setQuestions(questions)

        // setting id
        setQuestionID(questionID+1);
    }

    const modify_question = (id, attribute, value, answer_id = -1) => {

        const index = questions.findIndex(element => element['id'] === id);

        if(attribute !== 'answers'){
            questions[index][attribute] = value;

            if(attribute === 'type'){ // if the type has changed then resetting the answers TODO: maybe make it save it?
                questions[index]['answers'] = []
            }
        }
        else{
            const answer = questions[index]['answers'].find(element => element['id'] === answer_id);
            if(answer === undefined) {
                questions[index]['answers'].push({id: answer_id, value: value});
            }
            else{
                questions[index]['answers'][answer_id] = value;
            }
        }
    }

    const delete_question_answer = (id, answer_id) => {
        const index = questions.findIndex(element => element['id'] === id);

        const answer_index = questions[index]['answers'].findIndex(element => element['id'] === answer_id)

        questions[index]['answers'].splice(answer_index, 1)
    }

    // TODO: NOT FUCKING WORKING CORRECTLY. NOT HERE AND NOT IN THE MULTIPLE ANSWERS OF A QUESTION
    const delete_question = (id) => {
        const index = questions.findIndex(element => element['id'] === id);
        questions.splice(index, 1)
        setQuestions(questions => [...questions])
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