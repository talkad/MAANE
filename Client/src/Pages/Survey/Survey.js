import React, {useEffect, useState} from "react";
import '../SurveyBuilder/SurveyBuilder.css';
import {Alert, AlertTitle, Box, Grid, Pagination, Paper, TextField} from "@mui/material";
import Button from "@mui/material/Button";
import SurveyQuestion from "./SurveyQuestion";
import * as Space from 'react-spaces';
import NotificationSnackbar from "../../CommonComponents/NotificationSnackbar";
import Connection from "../../Communication/Connection";

const questionsPerPage = 5

export default function Survey(){

    const [surveyTitle, setSurveyTitle] = useState('Hello there');
    const [surveyDescription, setSurveyDescription] = useState('General Kenobi');
    // initializing with dummy data for offline testing
    const [questions, setQuestions] = useState([
        {
            id: 0,
            question: 'a',
            type: 'OPEN_ANSWER',
            answer: '',
        },
        {
            id: 1,
            question: 'b',
            type: 'NUMERIC_ANSWER',
            answer: '',
        },
        {
            id: 2,
            choices: ['אנקין', 'פדמה',],
            question: 'c',
            type: 'MULTIPLE_CHOICE',
            answer: '',
        },
        {
            id: 3,
            question: 'e',
            type: 'NUMERIC_ANSWER',
            answer: '',
        },
        {
            id: 4,
            question: 't',
            type: 'NUMERIC_ANSWER',
            answer: '',
        },
        {
            id: 5,
            question: 'u',
            type: 'NUMERIC_ANSWER',
            answer: '',
        },
        {
            id: 6,
            question: 'q',
            type: 'NUMERIC_ANSWER',
            answer: '',
        },
        {
            id: 7,
            question: 'z',
            type: 'NUMERIC_ANSWER',
            answer: '',
        },
    ]);
    const [page, setPage] = React.useState(1);

    // error states
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [errorSeverity, setErrorSeverity] = useState('error');
    const [openSnackbar, setOpenSnackbar] = useState(false);

    const [surveyComplete, setSurveyComplete] = useState(false);

    // STRINGS
    const submit_survey_string = 'סיום מענה';

    const survey_success_title_string = "תשובות הסקר נשמרו בהצלחה!"
    const survey_success_message_string = "ניתן לסגור את החלון";

    useEffect(() => {
        // TODO: send request to the server

        function onCloseMessage(e) {

            if(!surveyComplete){
                var dialogText = 'temp';
                e.preventDefault();
                e.returnValue = dialogText;
            }
        }


        window.addEventListener('beforeunload', onCloseMessage); // todo: make this work

        return () => {
            window.removeEventListener('beforeunload', onCloseMessage);
        }
    }, []);



    /**
     * handler for changing the answer of a question
     * @param id the id of the question to which update the answer
     * @param value the answer to the question
     */
    const handleAnswerChange = (id, value) => {
        setQuestions(questions.map(function (element) {
            if (element.id !== id) {
                return element;
            }

            const temp_element = {...element};
            temp_element["answer"] = value;

            return temp_element;
        }));
    };

    /**
     * handler for changing a page in the survey
     * @param event required but not used
     * @param value the new page to go to
     */
    const handlePageChange = (event, value) => {
        setPage(value);
    };

    /**
     * a call back function which handles the response from the server regarding the request to submit a fill survey
     * @param data the response from the server
     */
    const submitCallback = (data) =>{
        if (data.failure){
            setOpenSnackbar(true);
            setErrorMessage("הפעולה נכשלה. אנא נסה/י שנית");
            setSurveyComplete(false);
        }
        else{
            setSurveyComplete(true);
        }
    }

    /**
     * sends the answers to the survey to the server
     */
    const handleSubmit = () => {
        // checking for an empty required field
        if(questions.reduce((prev, curr) => prev ||
                curr.answer.trim() === '', false)){
            setShowError(true);
            setOpenSnackbar(true);
            setErrorMessage("נא למלא את כל השדות");
            setErrorSeverity("error");
        }
        else{
            setShowError(false);

            // todo: send the survey
        }
    }

    // todo: change the page to output a success message if the submission succeeds
    return (
        <Space.Fill scrollable>
            {!surveyComplete && <div style={{margin: '5vh'}} className="Survey">
                {/*alert*/}
                <Box sx={{width: "70%", marginBottom: "1%"}}>
                    {showError && <Alert severity={errorSeverity}> {errorMessage} </Alert>}
                </Box>

                <Paper className="Survey-paper" elevation={3}>
                    {/*TODO: have this big and in bold*/}
                    {/*title of the survey*/}
                    <TextField
                        color="secondary"
                        margin="normal"
                        className="Survey-text-field"
                        defaultValue={surveyTitle}
                        InputProps={{
                            readOnly: true,
                        }}
                        variant="standard"
                    />
                    {/*TODO: have this a little smaller*/}
                    {/*description of the survey*/}
                    <TextField
                        color="secondary"
                        margin="normal"
                        className="Survey-text-field"
                        defaultValue={surveyDescription}
                        InputProps={{
                            readOnly: true,
                        }}
                        variant="standard"
                    />
                </Paper>

                {/*the question to the survey*/}
                {questions.slice((page-1) * questionsPerPage, Math.min(page * questionsPerPage, questions.length)).map(question =>
                    <SurveyQuestion id={question.id}
                                    questionString={question.question}
                                    choices={question.type === "MULTIPLE_CHOICE" ? question.choices : []}
                                    type={question.type}
                                    answer={question.answer}
                                    showError={showError}
                                    answerChange={handleAnswerChange} />)}

                {/*paging component*/}
                <Pagination count={Math.ceil(questions.length/questionsPerPage)} page={page} onChange={handlePageChange} />
                <br/>
                {/*submitting the survey*/}
                {page === Math.ceil(questions.length/questionsPerPage) && <Button color="secondary" variant="contained" onClick={handleSubmit}>{submit_survey_string}</Button>}

                {/*pop up notification*/}
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
                </Grid>}

        </Space.Fill>
    )
}