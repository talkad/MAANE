import React, {useEffect, useState} from "react";
import Connection from "../../Communication/Connection";
import * as Space from "react-spaces";
import {Alert, AlertTitle, Box, Grid, Pagination, Paper, TextField} from "@mui/material";
import SurveyQuestion from "../Survey/SurveyQuestion";
import Button from "@mui/material/Button";
import NotificationSnackbar from "../../CommonComponents/NotificationSnackbar";
import SurveyGeneralResultsQuestion from "./SurveyGeneralResultsQuestion";
import {navigate} from "react-big-calendar/lib/utils/constants";
import {useNavigate} from "react-router-dom";


const mock = [
    {
        id: 1,
        question: 'b',
        type: 'NUMERIC_ANSWER',
        statistics: 12,
    },
    {
        id: 2,
        choices: ['אנקין', 'פדמה',],
        question: 'c',
        type: 'MULTIPLE_CHOICE',
        statistics: [24, 12],
    },
];

const questionsPerPage = 5;

export default function SurveyGeneralResults(){

    const [surveyTitle, setSurveyTitle] = useState('');
    const [surveyDescription, setSurveyDescription] = useState('');
    // initializing with dummy data for offline testing
    const [questions, setQuestions] = useState(mock);
    const [page, setPage] = React.useState(1);

    // error states
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [errorSeverity, setErrorSeverity] = useState('error');
    const [openSnackbar, setOpenSnackbar] = useState(false);

    // STRINGS
    const finish_view_button_string = "סיום צפייה וחזרה לתפריט";

    let navigate = useNavigate();


    useEffect(() => {
        var url = new URL(window.location.href);
        var surveyID = url.searchParams.get("surveyID");
        new Connection().getSurvey(surveyID, arrangeSurvey);

        // TODO: ask for the stats

        // function onCloseMessage(e) {
        //
        //     if(!surveyComplete){
        //         var dialogText = 'temp';
        //         e.preventDefault();
        //         e.returnValue = dialogText;
        //     }
        // }
        //
        //
        // window.addEventListener('beforeunload', onCloseMessage); // todo: make this work
        //
        // return () => {
        //     window.removeEventListener('beforeunload', onCloseMessage);
        // }
    }, []);

    /**
     * arranges the data received from the server regarding the request to view and fill a survey
     * @param data the data recieved from the server
     */
    const arrangeSurvey = (data) => {
        if(!data.failure){
            function zip(arrays) {
                return arrays[0].map(function(_,i){
                    return arrays.map(function(array){return array[i]})
                });
            }

            const survey = data.result;

            setSurveyTitle(survey.title);
            setSurveyDescription(survey.description);

            const zippedQuestionsList = zip([survey.questions, survey.types, survey.answers]);

            let questionIndexer = 0;
            zippedQuestionsList.forEach(([question, type, answers]) => setQuestions(questions =>
                [...questions, {id: questionIndexer++, question: question, type: type, choices: answers, statics: '',}]));
        }
        else {
            // TODO: have a page for when showing the survey fails
        }
    }

    /**
     * handler for changing a page in the survey
     * @param event required but not used
     * @param value the new page to go to
     */
    const handlePageChange = (event, value) => {
        setPage(value);
    };

    return (
        <Space.Fill scrollable>
            <div style={{margin: '5vh'}} className="Survey">
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
                        value={surveyTitle}
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
                        value={surveyDescription}
                        InputProps={{
                            readOnly: true,
                        }}
                        variant="standard"
                    />
                </Paper>

                {/*the question to the survey*/}
                {questions.slice((page-1) * questionsPerPage, Math.min(page * questionsPerPage, questions.length)).map(question =>
                    <SurveyGeneralResultsQuestion id={question.id}
                                    questionString={question.question}
                                    choices={question.type === "MULTIPLE_CHOICE" ? question.choices : []}
                                    type={question.type}
                                    statistics={question.statistics} />)}

                {/*paging component*/}
                <Pagination count={Math.ceil(questions.length/questionsPerPage)} page={page} onChange={handlePageChange} />
                <br/>
                {/*submitting the survey*/}
                <Button color="secondary" variant="contained" onClick={() => navigate('../menu')}>{finish_view_button_string}</Button>

                {/*pop up notification*/}
                <NotificationSnackbar
                    open={openSnackbar}
                    setOpen={setOpenSnackbar}
                    severity={errorSeverity}
                    message={errorMessage}/>
            </div>
        </Space.Fill>
    )
}