import React, { useState } from "react";
import './SurveyBuilder.css'
import {Paper} from "@mui/material";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import SurveyQuestionBuilder from "./SurveyBuilderQuestion";
import Connection from "../../Communication/Connection";
import * as Space from 'react-spaces';

let question_id = 0;

export default function SurveyBuilder(){
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [questions, setQuestions] = useState([]);
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [questionID, setQuestionID] = useState(0);

    // STRINGS
    const header_string = 'בניית סקר'
    const survey_title_label_string = 'כותרת הסקר'
    const survey_description_label_string = 'תיאור הסקר'
    const add_question_string = 'הוסף/י שאלה'
    const submit_survey_string = 'סיום'

    /**
     * adds a new question to the survey
     */
    const add_question = () => {
        // setting new question element

        questions.push(
            {
                id: question_id,
                element: <SurveyQuestionBuilder id={question_id} modify={modify_question} delete={delete_question} delete_answer={delete_question_answer}/>,
                question: '',
                type: 'MULTIPLE_CHOICE',
                answers: {},
            }
        );

        setQuestions(questions);

        // setting id
        setQuestionID(questionID+1);
        question_id++;
    }

    /**
     * update a field of a given question
     * @param id the id of the question's cell
     * @param attribute the attribute of the question to update
     * @param value the value to update to
     * @param answer_id for the case of updating an answer in multiple-choice, the id of the answer
     */
    const modify_question = (id, attribute, value, answer_id = -1) => {

        const index = questions.findIndex(element => element['id'] === id);

        if(attribute !== 'answers'){
            questions[index][attribute] = value;

            if(attribute === 'type'){ // if the type has changed then resetting the answers
                questions[index]['answers'] = [];
            }
        }
        else{
            questions[index]['answers'][answer_id] = value;

            // const answer = questions[index]['answers'].find(element => element['id'] === answer_id);
            // console.log(questions[index]['answers']);
            // if(answer === undefined) {
            //     questions[index]['answers'].push({id: answer_id, value: value});
            // }
            // else{
            //     questions[index]['answers'][answer_id] = value;
            // }
        }

        setQuestions(questions);
    }

    /**
     * delete an answer in multiple-choice questions
     * @param id the id of the question
     * @param answer_id the id of the answer to delete
     */
    const delete_question_answer = (id, answer_id) => {
        const index = questions.findIndex(element => element['id'] === id);

        //const answer_index = questions[index]['answers'].findIndex(element => element['id'] === answer_id);
        delete questions[index]['answers'][answer_id];

        setQuestions(questions);
        //questions[index]['answers'].splice(answer_index, 1);
    }

    // TODO: NOT FUCKING WORKING CORRECTLY. NOT HERE AND NOT IN THE MULTIPLE ANSWERS OF A QUESTION
    /**
     * deletes a question from the survey
     * @param id the id of the question to delete
     */
    const delete_question = (id) => {
        const index = questions.findIndex(element => element['id'] === id);
        questions.splice(index, 1);
        setQuestions(questions => [...questions]);
    }

    const handleTitleChange = (event) => {
        setTitle(event.target.value);
    }

    const handleDescriptionChange = (event) => {
        setDescription(event.target.value);
    }

    const submitSurveyCallback = (data) => {
        // todo: implement
    }

    /**
     * sends the structure of the built survey to the sever
     */
    const submit_survey = () => {
        new Connection().createSurvey(title, description, questions.map(x => x["question"]),
            questions.map(x => Object.entries(x["answers"]).map(x => x[1])), questions.map(x => x["type"]), submitSurveyCallback);
    }

    return (
        <Space.Fill scrollable >
            <div className="Survey">
                <h1>{header_string}</h1>
                <Paper className="Survey-paper" elevation={3}>
                    {/*TODO: make the margin work */}
                    {/*the title of the survey*/}
                    <TextField
                        value={title}
                        onChange={handleTitleChange}
                        color="secondary"
                        className="Survey-text-field"
                        error={showError}
                        margin="normal"
                        variant="standard"
                        required
                        label={survey_title_label_string}
                        name="title"
                        autoFocus
                    />
                    {/*the description of the survey*/}
                    <TextField
                        value={description}
                        onChange={handleDescriptionChange}
                        color="secondary"
                        className="Survey-text-field"
                        error={showError}
                        margin="normal"
                        variant="standard"
                        required
                        label={survey_description_label_string}
                        name="description"
                    />
                </Paper>

                {/*the questions*/}
                {questions.map(x => x['element'])}

                {/*add question button*/}
                <Button onClick={add_question} color="secondary" variant="contained">{add_question_string}</Button>
                <br/>
                {/*submit question button*/}
                <Button onClick={submit_survey} color="secondary" variant="contained">{submit_survey_string}</Button>
            </div>
        </Space.Fill>
    )
}