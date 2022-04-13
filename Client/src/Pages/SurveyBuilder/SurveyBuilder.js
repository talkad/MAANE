import React, { useState } from "react";
import './SurveyBuilder.css'
import {Alert, AlertTitle, Box, Grid, Paper} from "@mui/material";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import SurveyQuestionBuilder from "./SurveyBuilderQuestion";
import Connection from "../../Communication/Connection";
import * as Space from 'react-spaces';
import NotificationSnackbar from "../../CommonComponents/NotificationSnackbar";
import {useNavigate} from "react-router-dom";

export default function SurveyBuilder(){
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [questions, setQuestions] = useState([]);
    const [questionID, setQuestionID] = useState(0);

    // error states
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [errorSeverity, setErrorSeverity] = useState('error');
    const [openSnackbar, setOpenSnackbar] = useState(false);

    const [surveyComplete, setSurveyComplete] = useState(false);



    // STRINGS
    const header_string = 'בניית סקר'
    const survey_title_label_string = 'כותרת הסקר'
    const survey_description_label_string = 'תיאור הסקר'
    const add_question_string = 'הוספ/י שאלה'
    const submit_survey_string = 'סיום'

    const survey_success_title_string = "הסקר נוצר בהצלחה"
    const survey_success_message_string = "לחזרה לתפריט הסקרים נא ללחוץ על הכפתור למטה";
    const survey_success_button_string = "חזרה לתפריט הסקרים";

    let navigate = useNavigate();

    /**
     * adds a new question to the survey
     */
    const add_question = () => {
        // adding a new question
        setQuestions( [...questions, {id: questionID, question: '', type: 'MULTIPLE_CHOICE', answers: []}]);

        // setting id
        setQuestionID(questionID+1);
    }

    /**
     * update a field of a given question
     * @param id the id of the question's cell
     * @param attribute the attribute of the question to update
     * @param value the value to update to
     * @param answer_id for the case of updating an answer in multiple-choice, the id of the answer
     */
    const modify_question = (id, attribute, value, answer_id = -1) => {

        setQuestions(questions.map(function (element) {
            if (element.id !== id) {
                return element;
            }

            const temp_element = {...element};

            if(attribute !== 'answers'){
                temp_element[attribute] = value;

                if(attribute === 'type'){ // if the type has changed then resetting the answers
                    temp_element['answers'] = [];
                }
            }
            else{
                const answer_index = temp_element['answers'].findIndex(answer => answer.id === answer_id);
                if (answer_index === -1) {
                    temp_element['answers'].push({id: answer_id, value: value});
                }
                else{
                    temp_element['answers'][answer_index].value = value;
                }
            }

            return temp_element;
        }));
    }

    /**
     * delete an answer in multiple-choice questions
     * @param id the id of the question
     * @param answer_id the id of the answer to delete
     */
    const delete_question_answer = (id, answer_id) => {
        setQuestions(questions.map(item => item.id === id ? {id: item.id, question: item.question, type: item.type,
            answers: item.answers.filter(answer => answer.id !== answer_id)} : item));
    }

    /**
     * deletes a question from the survey
     * @param id the id of the question to delete
     */
    const delete_question = (id) => {
        setQuestions(questions.filter(item => item.id !== id));
    }

    const handleTitleChange = (event) => {
        setTitle(event.target.value);
    }

    const handleDescriptionChange = (event) => {
        setDescription(event.target.value);
    }

    const submitSurveyCallback = (data) => {
        if(data.failure){
            setOpenSnackbar(true);
            setErrorMessage("הפעולה נכשלה. אנא נסה/י שנית");
            setSurveyComplete(false);
        }
        else{
            setSurveyComplete(true);
        }
    }

    /**
     * sends the structure of the built survey to the sever
     */
    const submit_survey = () => {

        // checking for an empty required field
        if(title.trim() === '' || description.trim() === '' ||
            questions.reduce((prev, curr) => prev &&
                curr.question.trim() === '' &&
                curr.answers.reduce((prev, curr) => prev && curr.value.trim() === '', true), true)){
            setShowError(true);
            setOpenSnackbar(true);
            setErrorMessage("נא למלא את כל השדות");
            setErrorSeverity("error");
        }
        else{
            setShowError(false);

            new Connection().createSurvey(title, description, questions.map((x) => x["question"]),
                questions.map((x) => x["answers"].map((y) => y.value)), questions.map((x) => x["type"]), submitSurveyCallback);
        }


    }

    return (
        <Space.Fill scrollable >
            {!surveyComplete && <div className="Survey">
                <h1>{header_string}</h1>
                <Box sx={{width: "70%", marginBottom: "1%"}}>
                    {showError && <Alert severity={errorSeverity}> {errorMessage} </Alert>}
                </Box>

                <Paper className="Survey-paper" elevation={3}>
                    {/*TODO: make the margin work */}
                    {/*the title of the survey*/}
                    <TextField
                        value={title}
                        onChange={handleTitleChange}
                        color="secondary"
                        className="Survey-text-field"
                        error={showError && title.trim() === ''}
                        margin="normal"
                        variant="standard"
                        required
                        label={survey_title_label_string}
                        autoFocus
                    />
                    {/*the description of the survey*/}
                    <TextField
                        value={description}
                        onChange={handleDescriptionChange}
                        color="secondary"
                        className="Survey-text-field"
                        error={showError && description.trim() === ''}
                        margin="normal"
                        variant="standard"
                        required
                        label={survey_description_label_string}
                    />
                </Paper>

                {/*the questions*/}
                {questions.map((x) => <SurveyQuestionBuilder id={x.id}
                                                           question={x.question}
                                                           type={x.type}
                                                           answers={x.answers}
                                                           modify={modify_question}
                                                           delete={delete_question}
                                                           delete_answer={delete_question_answer}
                                                            showError={showError}/>)}

                {/*add question button*/}
                <Button onClick={add_question} color="secondary" variant="contained">{add_question_string}</Button>
                <br/>
                {/*submit question button*/}
                <Button onClick={submit_survey} color="secondary" variant="contained">{submit_survey_string}</Button>
                <NotificationSnackbar
                    open={openSnackbar}
                    setOpen={setOpenSnackbar}
                    severity={errorSeverity}
                    message={errorMessage}/>
            </div>}
            {surveyComplete &&
                <Grid container
                     direction="column"
                     alignItems="center"
                     justifyContent="center"
                spacing={1}>
                    <Grid item xs={12}>
                        <Alert severity="success">
                            <AlertTitle>{survey_success_title_string}</AlertTitle>
                            {survey_success_message_string}
                        </Alert>
                    </Grid>
                    <Grid item xs={12}>
                        <Button onClick={() => navigate('../menu', true)} variant="outlined">{survey_success_button_string}</Button>
                    </Grid>
                </Grid>}
        </Space.Fill>
    )
}